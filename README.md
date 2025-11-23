# Household energy monitor app

This guide explains how to configure and run the app. 
You can choose between different data-loading modes depending on your needs.

---

## Loading Full Address Data
By default, a smaller address dataset is used.  
To load the **full address dataset**:

1. Extract the contents of `addresses.7z`.
2. Copy the contents (REALESTATES.csv and HOUSEHOLDS.csv) of the extracted folder (addresses) to the folder: nvtbackend/data


3. Add the `--fullData` flag to the backend service command in `docker-compose.yml`.

---

## Loading Simulated Electricity Consumption Data
To include simulated electricity consumption data:

1. Extract the contents of `electricity_data.7z`.
2. Copy the contents (electricity_compact.lp) of the extracted folder (electricity_data) to the folder: nvtbackend/data/influxdb

##  Starting the Application
After choosing and configuring your preferred options, in the root folder run: `docker-compose up`


## Running the App Without Loading Initial Data
If you want to run the application **without** loading data into the database, simply remove the `--initMode` flag from the backend service command in `docker-compose.yml`.

---

## Email Sending
Email functionality is **currently disabled** due to an expired SendGrid API key.
