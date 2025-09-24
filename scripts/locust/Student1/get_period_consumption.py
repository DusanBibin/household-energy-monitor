import random
import csv
from locust import HttpUser, task, between

# Load household-owner mappings
households_owners = []
with open("households_owners.csv", newline="") as csvfile:
    reader = csv.reader(csvfile)
    for row in reader:
        household_id, client_id = map(int, row)
        households_owners.append((household_id, client_id))

class RealestateUser(HttpUser):
    wait_time = between(1, 3)
    PASSWORD = "sifra123"
    PERIODS = ['3h', '6h', '12h', '24h', '7d', '1m', '3m', '1y']

    def on_start(self):
        # Pick a random row
        self.household_id, self.client_id = random.choice(households_owners)
        self.jwt_cookie = None  # per-user JWT

        # Construct email based on clientId from CSV
        email = f"dusanbibin2+client{self.client_id - 2}@gmail.com"
        payload = {"email": email, "password": self.PASSWORD}

        # Authenticate and get JWT
        with self.client.post("/api/v1/auth/authenticate", json=payload, catch_response=True) as response:
            if response.status_code == 200:
                self.jwt_cookie = self.client.cookies.get("jwt")
                if self.jwt_cookie:
                    response.success()
                else:
                    response.failure("No JWT cookie returned")
            else:
                response.failure(f"Authentication failed: {response.status_code}, {response.text}")

    @task
    def get_consumption(self):
        # Pick random period
        period = random.choice(self.PERIODS)

        headers = {"Authorization": f"Bearer {self.jwt_cookie}"} if self.jwt_cookie else {}

        with self.client.get(
            f"/api/v1/household/{self.household_id}/consumption",
            headers=headers,
            params={"period": period},
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
                print(f"[Consumption] householdId={self.household_id}, clientId={self.client_id}, period={period}")
            else:
                response.failure(f"Request failed: {response.status_code}, {response.text}")
                print(f"[Request] clientId: {self.client_id}, householdId: {self.household_id}, period={period}")
                print(f"[Request] Status code: {response.status_code}, Response: {response.text}")
