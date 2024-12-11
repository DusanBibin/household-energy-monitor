import pika
import time
import json

# RabbitMQ connection details
RABBITMQ_HOST = 'localhost'  # Host from Spring Boot config
RABBITMQ_PORT = 5672         # Port from Spring Boot config
RABBITMQ_USERNAME = 'nvt'    # Username from Spring Boot config
RABBITMQ_PASSWORD = '123'    # Password from Spring Boot config
RABBITMQ_QUEUE = 'neki_queue'  # The queue you want to send the heartbeat to

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

if __name__ == "__main__":
    send_heartbeat()
