import pika
import time
import json
import sys
import datetime
import threading
import random

household_id = 0
# RabbitMQ connection details
RABBITMQ_HOST = 'localhost'  # Host from Spring Boot config
RABBITMQ_PORT = 5672         # Port from Spring Boot config
RABBITMQ_USERNAME = 'nvt'    # Username from Spring Boot config
RABBITMQ_PASSWORD = '123'    # Password from Spring Boot config
RABBITMQ_QUEUE = 'neki_queue'  # The queue you want to send the heartbeat to
RABBITMQ_QUEUE_VALUES = 'values'

BASE_NIGHT_CONSUMPTION = 0.1
BASE_DAY_CONSUMPTION = 0.3
PEAK_HOUR_MULTIPLIER = 1.3
WINTER_MULTIPLIER = 1.3
SUMMER_MULTIPLIER = 1.4
SPRING_MULTIPLIER = 0.9
FALL_MULTIPLIER = 0.8
WEEKDAY_DAY_PROBABILITY = 0.7
WEEKEND_DAY_PROBABILITY = 0.9
WEEKDAY_NIGHT_PROBABILITY = 0.3
WEEKEND_NIGHT_PROBABILITY = 0.5
MAX_RANDOM_USAGE = 0.2  # you need to define this (example value)

def is_daytime(hour, month):
    if month in [11, 12, 1, 2]:
        sunrise, sunset = 7, 17
    elif month in [3, 4, 5]:
        sunrise, sunset = 6, 19
    elif month in [6, 7, 8]:
        sunrise, sunset = 5, 21
    else:
        sunrise, sunset = 6, 18
    return sunrise <= hour < sunset

def get_seasonal_multiplier(month):
    if month in [11, 12, 1, 2]:
        return WINTER_MULTIPLIER
    elif month in [3, 4, 5]:
        return SPRING_MULTIPLIER
    elif month in [6, 7, 8]:
        return SUMMER_MULTIPLIER
    else:
        return FALL_MULTIPLIER

def generate_hourly_consumption(date_time):
    hour = date_time.hour
    is_weekend = date_time.weekday() >= 5  # Saturday=5, Sunday=6
    is_day = is_daytime(hour, date_time.month)
    
    consumption = BASE_DAY_CONSUMPTION if is_day else BASE_NIGHT_CONSUMPTION

    if (7 <= hour <= 9) or (17 <= hour <= 21):
        consumption *= PEAK_HOUR_MULTIPLIER

    consumption *= get_seasonal_multiplier(date_time.month)

    usage_prob = (WEEKEND_DAY_PROBABILITY if is_day else WEEKEND_NIGHT_PROBABILITY) if is_weekend else (WEEKDAY_DAY_PROBABILITY if is_day else WEEKDAY_NIGHT_PROBABILITY)

    # random_usage = (MAX_RANDOM_USAGE * usage_prob) * (time.time() % 1) 
    random_usage = random.random() * MAX_RANDOM_USAGE * usage_prob

    consumption += random_usage
    return max(consumption, 0.05)  


def create_connection():
    """Create and return a RabbitMQ connection."""
    credentials = pika.PlainCredentials(RABBITMQ_USERNAME, RABBITMQ_PASSWORD)
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(
            host=RABBITMQ_HOST,
            port=RABBITMQ_PORT,
            credentials=credentials
        )
    )
    return connection

def send_heartbeat():
    print("Heartbeat was sent by household ", household_id)
    while True:
        try:
            # Establish connection to RabbitMQ
            connection = create_connection()
            channel = connection.channel()

            # Declare the queue as durable (it survives broker restarts)
            channel.queue_declare(queue=RABBITMQ_QUEUE, durable=True)

            while True:
                # Create the heartbeat message
                heartbeat_message = {
                    "status": "online",
                    "timestamp": int(time.time())  # Current Unix timestamp
                }

                # Convert the message to a JSON string
                message = json.dumps(heartbeat_message)

                # Send the heartbeat to the RabbitMQ queue with persistent delivery mode
                channel.basic_publish(
                    exchange='',
                    routing_key=RABBITMQ_QUEUE,
                    body=message,
                    properties=pika.BasicProperties(
                        delivery_mode=2  # Make message persistent
                    )
                )

                print(f"Sent heartbeat: {message}")

                # Wait for 30 seconds before sending the next heartbeat
                time.sleep(30)
            
        except pika.exceptions.AMQPConnectionError as e:
            print(f"Connection lost, retrying... Error: {e}")
            time.sleep(5)  # Wait for 5 seconds before retrying
        except pika.exceptions.StreamLostError as e:
            print(f"Stream lost, retrying... Error: {e}")
            time.sleep(5)  # Wait for 5 seconds before retrying
        except Exception as e:
            print(f"An unexpected error occurred: {e}")
            break


def send_consumption():

    simulated_time = datetime.datetime.now()

    while True:
        try:
            connection = create_connection()
            channel = connection.channel()
            channel.queue_declare(queue=RABBITMQ_QUEUE_VALUES, durable=True)

            while True:
                consumption_value = generate_hourly_consumption(simulated_time)
                consumption_message = {
                    "household_id": household_id,
                    "timestamp": simulated_time.strftime("%Y-%m-%dT%H:%M:%S"),
                    "consumption": round(consumption_value, 3)
                }
                channel.basic_publish(
                    exchange='',
                    routing_key=RABBITMQ_QUEUE_VALUES,
                    body=json.dumps(consumption_message),
                    properties=pika.BasicProperties(delivery_mode=2)
                )
                print(f"[CONS] Household {household_id} at {consumption_message['timestamp']} -> {consumption_message['consumption']} kWh")

                # Advance simulated time by 1 simulated hour (1 min real time)
                simulated_time += datetime.timedelta(hours=1)
                time.sleep(60)
        except Exception as e:
            print(f"[CONS] Connection error: {e}")
            time.sleep(5)



if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python household.py <household_id>")
        sys.exit(1)

    household_id = sys.argv[1]
    threading.Thread(target=send_consumption, daemon=True).start()
    send_heartbeat()
