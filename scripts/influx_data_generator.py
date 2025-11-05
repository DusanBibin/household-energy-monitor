from datetime import datetime, timedelta, timezone
import math
import random

# Base consumption values in kWh
BASE_NIGHT_CONSUMPTION = 0.1
BASE_DAY_CONSUMPTION = 0.3
PEAK_HOUR_MULTIPLIER = 1.3

# Seasonal multipliers
WINTER_MULTIPLIER = 1.3
SUMMER_MULTIPLIER = 1.4
SPRING_MULTIPLIER = 0.9
FALL_MULTIPLIER = 0.8

# Appliance usage probabilities
WEEKDAY_DAY_PROBABILITY = 0.7
WEEKEND_DAY_PROBABILITY = 0.9
WEEKDAY_NIGHT_PROBABILITY = 0.3
WEEKEND_NIGHT_PROBABILITY = 0.5

# Smaller random appliance spikes
MAX_RANDOM_USAGE = 0.5

def is_daytime(hour: int, month: int) -> bool:
    if month >= 11 or month <= 2:  # Winter
        sunrise, sunset = 7, 17
    elif 3 <= month <= 5:  # Spring
        sunrise, sunset = 6, 19
    elif 6 <= month <= 8:  # Summer
        sunrise, sunset = 5, 21
    else:  # Fall
        sunrise, sunset = 6, 18
    return sunrise <= hour < sunset

def get_seasonal_multiplier(month: int) -> float:
    if month >= 11 or month <= 2:  # Winter
        return WINTER_MULTIPLIER
    elif 3 <= month <= 5:  # Spring
        return SPRING_MULTIPLIER
    elif 6 <= month <= 8:  # Summer
        return SUMMER_MULTIPLIER
    else:  # Fall
        return FALL_MULTIPLIER

def generate_hourly_consumption(dt: datetime) -> float:
    hour = dt.hour
    is_weekend = dt.weekday() >= 5
    daytime = is_daytime(hour, dt.month)

    consumption = BASE_DAY_CONSUMPTION if daytime else BASE_NIGHT_CONSUMPTION

    # Peak hours (7–9 AM, 5–9 PM)
    if (7 <= hour <= 9) or (17 <= hour <= 21):
        consumption *= PEAK_HOUR_MULTIPLIER

    # Seasonal adjustment
    consumption *= get_seasonal_multiplier(dt.month)

    # Random usage
    usage_probability = (
        WEEKEND_DAY_PROBABILITY if is_weekend and daytime else
        WEEKEND_NIGHT_PROBABILITY if is_weekend else
        WEEKDAY_DAY_PROBABILITY if daytime else
        WEEKDAY_NIGHT_PROBABILITY
    )
    consumption += random.random() * MAX_RANDOM_USAGE * usage_probability

    return max(consumption, 0.05)  # Minimum consumption

def generate_historical_data_to_lp(household_ids, lp_file_path):
    start = datetime.now(timezone.utc) - timedelta(days=3*365)
    start = start.replace(minute=0, second=0, microsecond=0)
    end = datetime.now(timezone.utc)

    total_hours = int((end - start).total_seconds() // 3600)
    
    with open(lp_file_path, "w") as writer:
        for idx, household_id in enumerate(household_ids, start=1):
            current = start
            while current <= end:
                consumption = generate_hourly_consumption(current)
                ts_nano = int(current.timestamp() * 1_000_000_000)
                lp_line = f"E,hId={household_id} kWh={consumption:.3f} {ts_nano}"
                writer.write(lp_line + "\n")
                current += timedelta(hours=1)

            if household_id % 100 == 0:
                print(f"Progress: {idx}/{len(household_ids)} households")

    print(f"Line Protocol file generated: {lp_file_path}")
    print(f"Total rows: ~{len(household_ids) * total_hours:,}")

if __name__ == "__main__":
    household_ids = list(range(1, 1001))  
    generate_historical_data_to_lp(household_ids, "../nvtbackend/data/influxdb/electricity_compact.lp")
