-- Create databases for each microservice.
-- This script is mounted into the PostgreSQL container via docker-compose.yml.

CREATE DATABASE user_db;
CREATE DATABASE notification_db;

-- Create service-specific users
CREATE USER user_admin WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE user_db TO user_admin;

CREATE USER notification_admin WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE notification_db TO notification_admin;

-- Grant schema permissions (PostgreSQL 15+)
\c user_db
GRANT ALL ON SCHEMA public TO user_admin;

\c notification_db
GRANT ALL ON SCHEMA public TO notification_admin;
