CREATE TABLE login_history (
                               id BIGSERIAL PRIMARY KEY,
                               username VARCHAR(255) NOT NULL,
                               ip_address VARCHAR(255),
                               user_agent TEXT,
                               login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               success BOOLEAN NOT NULL
);

ALTER TABLE public.stations
    ADD COLUMN ip_address VARCHAR(50),
    ADD COLUMN printer VARCHAR(255),
    ADD COLUMN cash_drawer VARCHAR(50),
    ADD COLUMN description VARCHAR(255);
