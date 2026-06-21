ALTER TABLE rental_agreements
    ADD COLUMN IF NOT EXISTS final_mileage BIGINT,
    ADD COLUMN IF NOT EXISTS final_fuel_level INTEGER;
