from locust import HttpUser, task, between
import random

class RealestateUser(HttpUser):
    wait_time = between(1, 3)  # simulate think time
    jwt_cookie = None

    # Serbia bounding box (master)
    TOP_LAT = 45.872593351293425
    LEFT_LON = 18.447678263271374
    BOTTOM_LAT = 42.6195942068577
    RIGHT_LON = 23.380539591396374

    # Static password for all users
    PASSWORD = "sifra123"

    def on_start(self):
        """Authenticate once per simulated user"""
        # pick a random user number 1–2000
        user_number = random.randint(1, 2000)
        email = f"dusanbibin2+client{user_number}@gmail.com"

        payload = {
            "email": email,
            "password": self.PASSWORD
        }

        # Authenticate if not already authenticated
        if not self.jwt_cookie:
            with self.client.post("/api/v1/auth/authenticate", json=payload, catch_response=True, verify=False) as response:
                if response.status_code == 200:
                    # Locust manages cookies automatically
                    self.jwt_cookie = self.client.cookies.get("jwt")
                    if self.jwt_cookie:
                        response.success()
                    else:
                        response.failure("No JWT cookie returned")
                else:
                    response.failure(f"Authentication failed: {response.status_code}")

    def random_bbox(self):
        """Generate bounding box inside Serbia"""
        lon1 = random.uniform(self.LEFT_LON, self.RIGHT_LON - 0.1)
        lon2 = random.uniform(lon1 + 0.05, self.RIGHT_LON)

        lat1 = random.uniform(self.BOTTOM_LAT + 0.05, self.TOP_LAT)
        lat2 = random.uniform(self.BOTTOM_LAT, lat1 - 0.05)

        return lon1, lat1, lon2, lat2

    @task
    def aggregate(self):
        """Call aggregate endpoint using JWT cookie"""
        if not self.jwt_cookie:
            return  # skip if not authenticated

        lon1, lat1, lon2, lat2 = self.random_bbox()
        params = {
            "topLeftLon": lon1,
            "topLeftLat": lat1,
            "bottomRightLon": lon2,
            "bottomRightLat": lat2,
            "zoomLevel": random.randint(8, 18)
        }

        with self.client.get("/api/v1/realestate/aggregate", params=params, catch_response=True, verify=False) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Aggregate failed: {response.status_code}")
