CREATE TABLE product_images (
                                id BIGSERIAL PRIMARY KEY,
                                file_name VARCHAR(255) NOT NULL,
                                category VARCHAR(100),
                                url TEXT NOT NULL,
                                size_kb BIGINT,
                                status VARCHAR(50) DEFAULT 'active',
                                uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE import_history (
                                id BIGSERIAL PRIMARY KEY,
                                file_name VARCHAR(255) NOT NULL,
                                type VARCHAR(100) NOT NULL,         -- e.g., Products, Customers, Inventory
                                records INT DEFAULT 0,
                                status VARCHAR(50),                 -- success, partial, failed
                                imported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE backup_history (
                                id BIGSERIAL PRIMARY KEY,
                                file_name VARCHAR(255) NOT NULL,
                                size_kb BIGINT,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
