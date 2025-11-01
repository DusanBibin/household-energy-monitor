from locust import HttpUser, task, between
import random
from datetime import datetime, timedelta
import pytz

# --- CONFIGURATION ---
CLERK_IDS = [15926]
WORK_START = 8
WORK_END = 16
BREAK_START = 12
BREAK_END = 12.5  # 12:30
SLOT_DURATION = 0.5  # 30 minutes
TIMEZONE = pytz.timezone("Europe/Belgrade")
DATE_FORMAT = "%d/%m/%Y-%H:%M"

class AppointmentUser(HttpUser):
    wait_time = between(0.5, 2)
    PASSWORD = "sifra123"  # replace with correct password
    jwt_cookie = None

    # --- UTILITIES ---
    def get_next_slot(self, start_date):

        slot = start_date.replace(second=0, microsecond=0)

        while True:
            weekday = slot.weekday()  # 0=Monday, 6=Sunday

            # Skip weekends
            if weekday >= 5:
                # Move to next Monday
                slot += timedelta(days=(7 - weekday))
                slot = slot.replace(hour=WORK_START, minute=0)
                continue

            # Skip lunch break
            hour_min = slot.hour + slot.minute / 60
            if BREAK_START <= hour_min < BREAK_END:
                # Move to end of lunch break
                slot = slot.replace(hour=12, minute=30)
                continue

            # Check if within working hours
            if slot.hour < WORK_START:
                slot = slot.replace(hour=WORK_START, minute=0)
            elif slot.hour >= WORK_END or (slot.hour == WORK_END - 1 and slot.minute == 30):
                # Move to next day 08:00
                slot += timedelta(days=1)
                slot = slot.replace(hour=WORK_START, minute=0)
                continue

            # Ensure 30-minute boundary
            if slot.minute not in [0, 30]:
                # Round up to next :00 or :30
                if slot.minute < 30:
                    slot = slot.replace(minute=30)
                else:
                    slot += timedelta(hours=1)
                    slot = slot.replace(minute=0)
                continue

            # Valid slot found
            return slot

            # Move to next slot for next iteration if needed
            slot += timedelta(minutes=int(SLOT_DURATION * 60))



    # --- AUTHENTICATION ---
    def on_start(self):
        """Authenticate once per simulated user and prepare slot cycle"""
        user_number = random.randint(1, 2000)
        email = f"dusanbibin2+client{user_number}@gmail.com"
        payload = {"email": email, "password": self.PASSWORD}

        with self.client.post("/api/v1/auth/authenticate", json=payload, catch_response=True, verify=False) as response:
            if response.status_code == 200:
                self.jwt_cookie = self.client.cookies.get("jwt")
                if self.jwt_cookie:
                    response.success()
                else:
                    response.failure("No JWT cookie returned")
            else:
                response.failure(f"Authentication failed: {response.status_code}")

        # Start from 8am next day
        tomorrow = datetime.now(TIMEZONE) + timedelta(days=2)
        self.next_slot = tomorrow.replace(hour=WORK_START, minute=0, second=0, microsecond=0)
        self.clerk_index = 0

    # --- TASK ---
    @task
    def book_appointment(self):
        if not self.jwt_cookie:
            print("Skipping booking, user not authenticated")
            return

        clerk_id = CLERK_IDS[self.clerk_index % len(CLERK_IDS)]
        slot_str = self.next_slot.strftime(DATE_FORMAT)

        headers = {"Cookie": f"jwt={self.jwt_cookie}"}

        # Use the proper with-block for catch_response
        with self.client.post(
            f"/api/v1/clerk/{clerk_id}/appointment",
            params={"startDateTime": slot_str},
            headers=headers,
            catch_response=True,
            verify=False
        ) as response:

            if response.status_code == 200:
                print(f"SUCCESS: Booked {slot_str} with clerk {clerk_id}")
                response.success()
            else:
                print(f"FAILED ({response.status_code}): Slot {slot_str}, clerk {clerk_id}, moving to next slot")
                response.failure(f"{response.status_code}: {response.text}")
                # Move to next slot if failed
                self.next_slot = self.get_next_slot(self.next_slot + timedelta(minutes=int(SLOT_DURATION*60)))

        # Rotate clerk for the next request
        self.clerk_index += 1

