DROP TABLE IF EXISTS phone;
DROP TABLE IF EXISTS user;

CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,
    last_login TIMESTAMP NOT NULL,
    token VARCHAR(1000),
    is_active BOOLEAN NOT NULL
);


CREATE TABLE phone (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    number VARCHAR(50) NOT NULL,
    citycode VARCHAR(10) NOT NULL,
    countrycode VARCHAR(10) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);