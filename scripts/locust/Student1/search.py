from locust import HttpUser, task, between
import random
import csv

# Load CSV once at the module level
addresses = []
with open("addresses_test.csv", newline="", encoding="utf-8") as csvfile:
    reader = csv.reader(csvfile)
    for row in reader:
        addresses.append(row)

class RealestateUser(HttpUser):
    wait_time = between(1, 3)  # simulate user think time
    jwt_cookie = None
    PASSWORD = "sifra123"

    def on_start(self):
        """Authenticate once per simulated user"""
        user_number = random.randint(1, 2000)
        email = f"dusanbibin2+client{user_number}@gmail.com"

        payload = {"email": email, "password": self.PASSWORD}

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
    def search_addresses(self):
        """Call search endpoint with randomized partial address queries"""
        if not self.jwt_cookie or not addresses:
            return  # skip if not authenticated or no data

        # Step 1: pick a random row
        row = random.choice(addresses)

        # Step 2: pick a random number of elements from index 0
        num_elements = random.randint(1, 6)  # max 6 elements
        query_string = " ".join(row[:num_elements])  # concatenate sequential elements

        # Step 3: send GET request to /search
        with self.client.get(
            f"/api/v1/realestate/search?query={query_string}",
            catch_response=True, verify=False,
            headers={"Cookie": f"jwt={self.jwt_cookie}"}
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Search failed: {response.status_code}")
                print(response.text)
