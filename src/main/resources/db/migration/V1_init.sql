CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE stores (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        name VARCHAR(255) NOT NULL,
                        phone VARCHAR(50),
                        address VARCHAR(500),
                        city VARCHAR(100),
                        status VARCHAR(50),
                        zip_code VARCHAR(20),
                        email VARCHAR(255),
                        website VARCHAR(255),
                        created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE stations (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          name VARCHAR(255) NOT NULL,
                          store_id UUID REFERENCES stores(id) ON DELETE CASCADE,
                          status VARCHAR(50),
                          created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       username VARCHAR(100) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       enabled BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE user_stations (
                               user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                               station_id UUID REFERENCES stations(id) ON DELETE CASCADE,
                               PRIMARY KEY (user_id, station_id)
);

-- Optional table to store password reset tokens
CREATE TABLE password_reset_tokens (
                                       token VARCHAR(255) PRIMARY KEY,
                                       user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                                       expires_at TIMESTAMPTZ NOT NULL,
                                       used BOOLEAN DEFAULT FALSE
);
