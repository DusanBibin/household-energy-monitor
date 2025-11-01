import requests
import subprocess
import sys

# Endpoint URL for the Spring Boot application
ENDPOINT_URL = "http://192.168.1.103/api/v1/realestate/household/script"

# Python script to be executed for each ID
SCRIPT_TO_RUN = "household.py"

def fetch_ids(id_number):
    """Fetch the list of IDs from the Spring Boot endpoint."""
    try:
        response = requests.get(ENDPOINT_URL + "/" + str(id_number), verify=False)
        response.raise_for_status()  # Raise an HTTPError for bad responses (4xx or 5xx)
        return response.json()  # Assuming the endpoint returns a JSON array of integers
    except requests.RequestException as e:
        print(f"Error fetching IDs: {e}")
        sys.exit(1)

def run_scripts_for_ids(ids):
    """Run one instance of the script for each ID."""
    processes = []
    print("iksde")
    try:
        for id in ids:
            process = subprocess.Popen([sys.executable, SCRIPT_TO_RUN, str(id)])
            processes.append(process)

        # Wait for all processes to complete
        for process in processes:
            process.wait()
    except Exception as e:
        print(f"Error running scripts: {e}")
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <number>")
        sys.exit(1)
    
    try:
        id_number = int(sys.argv[1])
    except ValueError:
        print("Please provide a valid integer as the parameter.")
        sys.exit(1)


    ids = fetch_ids(id_number)
    print("Chosen household ids are:")
    print(ids)
    if not isinstance(ids, list) or not all(isinstance(id, int) for id in ids):
        print("Invalid response from the server. Expected a list of integers.")
        sys.exit(1)

    print(f"Fetched IDs: {ids}")

    # Run scripts for each ID
    run_scripts_for_ids(ids)
