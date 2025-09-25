import random
import datetime
from locust import HttpUser, task, between

class User(HttpUser):
    wait_time = between(1, 3)  # simulate think time
    jwt_cookie = None

    PASSWORD = "sifra123"

    def on_start(self):
        """Authenticate once per simulated user and generate week ranges"""
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
                        self.week_ranges = self.generate_future_week_ranges(10)
                    else:
                        response.failure("No JWT cookie returned")
                else:
                    response.failure(f"Authentication failed: {response.status_code}")

    def generate_future_week_ranges(self, count):
        week_ranges = []
        today = datetime.date.today()

        # Get this week's Monday
        this_monday = today - datetime.timedelta(days=today.weekday())

        for i in range(count):
            week_start = this_monday + datetime.timedelta(weeks=i)
            start_of_week = datetime.datetime.combine(week_start, datetime.time.min)
            end_of_week = start_of_week + datetime.timedelta(weeks=1)  # exactly 7 days apart, next Monday at 00:00:00

            week_ranges.append((
                start_of_week.isoformat() + "Z",
                end_of_week.isoformat() + "Z"
            ))

        return week_ranges


    @task
    def get_week_appointments(self):
        if not self.jwt_cookie:
            return  # skip if auth failed

        clerk_id = 15926  # choose random clerk (adjust as needed)
        startDateTime, endDateTime = random.choice(self.week_ranges)
        
        with self.client.get(
            f"/api/v1/clerk/{clerk_id}/appointment",
            params={
                "startDateTime": startDateTime,
                "endDateTime": endDateTime
            },
            cookies={"jwt": self.jwt_cookie},
            catch_response=True
        ) as response:
            if response.status_code != 200:
                
                response.failure(f"Failed with {response.status_code}")
            else:
                response.success()
                # print(response.text)
                
