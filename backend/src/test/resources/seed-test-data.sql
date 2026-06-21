-- H2-compatible seed data for integration tests
INSERT INTO users (id, email, password_hash, role, registration_date, created_at, updated_at) VALUES
  ('00000000-0000-0000-0000-000000000001', 'admin@carrent.ru', '$2a$10$XptfskLsT9u/OL0bOuLBiOGRqYBTpgQFoGHOWbHLCjZ5kE9CgxfGi', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-0000-0000-000000000002', 'manager@carrent.ru', '$2a$10$XptfskLsT9u/OL0bOuLBiOGRqYBTpgQFoGHOWbHLCjZ5kE9CgxfGi', 'MANAGER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-0000-0000-000000000003', 'client@carrent.ru', '$2a$10$XptfskLsT9u/OL0bOuLBiOGRqYBTpgQFoGHOWbHLCjZ5kE9CgxfGi', 'CLIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO client_profiles (user_id, is_verified, created_at, updated_at) VALUES
  ('00000000-0000-0000-0000-000000000003', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cars (id, vin, license_plate, model_name, current_mileage, fuel_level_percentage, car_class, base_daily_rate, status, created_at, updated_at) VALUES
  ('00000000-0000-0000-0000-000000000010', 'WVWZZZ1JZXW000001', 'А123БВ26', 'Volkswagen Polo', 25000, 90, 'ECONOMY', 2500.00, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('00000000-0000-0000-0000-000000000011', 'XTA21099090000001', 'В456ГД26', 'Lada Vesta', 18000, 75, 'ECONOMY', 2000.00, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
