import pika
import time
import json

# RabbitMQ connection details
RABBITMQ_HOST = 'localhost'  # Host from Spring Boot config
RABBITMQ_PORT = 5672         # Port from Spring Boot config
RABBITMQ_USERNAME = 'nvt'    # Username from Spring Boot config
RABBITMQ_PASSWORD = '123'    # Password from Spring Boot config
RABBITMQ_QUEUE = 'neki_queue'  # The queue you want to send the heartbeat to

def send_heartbeat():
    # Establish connection to RabbitMQ with credentials
    credentials = pika.PlainCredentials(RABBITMQ_USERNAME, RABBITMQ_PASSWORD)
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(
            host=RABBITMQ_HOST,
            port=RABBITMQ_PORT,
            credentials=credentials
        )
    )
    channel = connection.channel()

    # Declare the queue (make sure it exists before publishing)
    channel.queue_declare(queue=RABBITMQ_QUEUE, durable=True)

    while True:
        # Create the heartbeat message
        heartbeat_message = {
            "status": "online",
            "timestamp": int(time.time())  # Current Unix timestamp
        }

        # Convert the message to a JSON string
        message = json.dumps(heartbeat_message)

        # Send the heartbeat to the RabbitMQ queue
        channel.basic_publish(exchange='',
                              routing_key=RABBITMQ_QUEUE,
                              body=message)

        print(f"Sent heartbeat: {message}")
        
        # Wait for 30 seconds before sending the next heartbeat
        time.sleep(5)

if __name__ == "__main__":
    send_heartbeat()
