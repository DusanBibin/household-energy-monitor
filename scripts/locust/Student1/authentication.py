from locust import HttpUser, task, between
import random

class AuthUser(HttpUser):
    wait_time = between(1, 3) 
    
    USER_POOL = [
        {"email": f"dusanbibin2+client{i}@gmail.com", "password": "sifra123"}
        for i in range(1, 2001)
    ]

    @task
    def authenticate(self):
        credentials = random.choice(self.USER_POOL)
        with self.client.post("/api/v1/auth/authenticate", json=credentials, catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Failed for {credentials['email']}")