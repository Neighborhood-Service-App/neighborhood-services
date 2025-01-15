CREATE TABLE users (
                       user_id UUID PRIMARY KEY,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       phone_number VARCHAR(13),
                       about VARCHAR(255),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       last_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE addresses (
                           address_id SERIAL PRIMARY KEY,
                           user_id UUID NOT NULL,
                           address VARCHAR(255) NOT NULL,
                           city VARCHAR(100) NOT NULL,
                           postal_code VARCHAR(5) NOT NULL,
                           latitude DOUBLE PRECISION,
                           longitude DOUBLE PRECISION,
                           address_type VARCHAR(255) NOT NULL,
                           is_default BOOLEAN NOT NULL DEFAULT FALSE,
                           FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);