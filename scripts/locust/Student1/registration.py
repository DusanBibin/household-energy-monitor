import random
import datetime
from locust import HttpUser, task, between

class User(HttpUser):
    wait_time = between(1, 3)  # simulate think time
    jwt_cookie = None

    PASSWORD = "sifra123"

    def on_start(self):
        """Authenticate once per simulated user"""
        user_number = random.randint(1, 2000)
        email = f"dusanbibin2+client{user_number}@gmail.com"

        payload = {
            "email": email,
            "password": self.PASSWORD
        }

        if not self.jwt_cookie:
            with self.client.post("/api/v1/auth/authenticate", json=payload, catch_response=True) as response:
                if response.status_code == 200:
                    self.jwt_cookie = self.client.cookies.get("jwt")
                    if self.jwt_cookie:
                        response.success()
                    else:
                        response.failure("No JWT cookie returned")
                else:
                    response.failure(f"Authentication failed: {response.status_code}")

    @task
    def create_household_request(self):
        """Send a multipart/form-data POST with 1–3 copies of test.png"""
        if not self.jwt_cookie:
            return  # skip if not authenticated

        # Pick random household/realestate IDs for load testing
        realestate_id = random.randint(1000, 2000)
        household_id = random.randint(5000, 6000)

        # Decide how many files to upload (1–3 copies of test.png)
        num_files = random.randint(1, 3)

        files = []
        for i in range(num_files):
            files.append(
                ("files", ("test.png", open("test.png", "rb"), "image/png"))
            )

        with self.client.post(
            f"/api/v1/realestate/{realestate_id}/household/{household_id}/household-request",
            files=files,
            cookies={"jwt": self.jwt_cookie},
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
                print(f"Uploaded {num_files} file(s). Response: {response.text}")
            else:
                response.failure(f"Failed {response.status_code}: {response.text}")
