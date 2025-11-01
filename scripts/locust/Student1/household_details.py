from locust import HttpUser, task, between
import random


households = [
    [1,1],
    [2,2],
    [3,3],
    [4,4],
    [5,5],
    [6,6],
    [7,7],
    [8,8],
    [9,9],
    [77,10],
    [87,11],
    [88,12],
    [89,13],
    [90,14],
    [91,15],
    [92,16],
    [93,17],
    [94,18],
    [114,19],
    [115,20],
    [132,21],
    [152,22],
    [161,23],
    [162,24],
    [163,25],
    [164,26],
    [165,27],
    [166,28],
    [167,29],
    [168,30],
    [169,31],
    [201,32],
    [202,33],
    [203,34],
    [204,35],
    [205,36],
    [206,37],
    [207,38],
    [208,39],
    [209,40],
    [210,41],
    [228,42],
    [229,43],
    [236,44],
    [237,45],
    [238,46],
    [239,47],
    [240,48],
    [241,49],
    [242,50]
]

class RealestateUser(HttpUser):
    wait_time = between(1, 3)  # simulate think time
    jwt_cookie = None

    # Static password for all users
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
            with self.client.post("/api/v1/auth/authenticate", json=payload, catch_response=True, verify=False) as response:
                if response.status_code == 200:
                    self.jwt_cookie = self.client.cookies.get("jwt")
                    if self.jwt_cookie:
                        response.success()
                    else:
                        response.failure("No JWT cookie returned")
                else:
                    response.failure(f"Authentication failed: {response.status_code}")

    @task
    def household_details(self):
        """Call household details endpoint with random ID"""
        if not self.jwt_cookie:
            return  # skip if not authenticated

        # Pick random ID in 1..5000 (same for realestateId and householdId)
        _id = random.randint(1, 5000)
        
        household = random.choice(households)

        with self.client.get(
            f"/api/v1/realestate/{household[1]}/household/{household[0]}",
            catch_response=True, verify=False
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Household details failed: {response.status_code}")
