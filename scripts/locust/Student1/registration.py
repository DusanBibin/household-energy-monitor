import random
import json
from locust import HttpUser, task, between

class User(HttpUser):
    wait_time = between(1, 3)  # simulate think time

    @task
    def register_user(self):
        """Simulate user registration with profile image upload"""

        # Generate unique user data
        user_number = random.randint(1, 1000000)
        email = f"locust_user{user_number}@gmail.com"
        phone = f"06{random.randint(10000000, 99999999)}"

        form_data = {
            "name": "Test",
            "lastname": "User",
            "email": email,
            "phone": phone,
            "password": "StrongPass!1",
            "repeatPassword": "StrongPass!1"
        }

        files = {
            "formData": (None, json.dumps(form_data), "application/json"),
            "profileImage": ("test.png", open("test.png", "rb"), "image/png")
        }

        with self.client.post(
            "/api/v1/auth/register",
            files=files,
            catch_response=True,
            verify=False
        ) as response:
            if response.status_code == 200:
                response.success()
                print(f"Registered user {email} successfully")
            else:
                response.failure(f"Failed {response.status_code}: {response.text}")
