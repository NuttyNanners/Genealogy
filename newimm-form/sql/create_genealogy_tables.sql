-- 1) Drop old database (if it exists)
DROP DATABASE IF EXISTS cs321;

-- 2) Recreate database
CREATE DATABASE cs321
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE cs321;

-- 3) Create a users table for login credentials
CREATE TABLE Users (
  user_id       VARCHAR(36)   PRIMARY KEY,       -- UUID
  username      VARCHAR(50)   NOT NULL UNIQUE,
  password_hash CHAR(40)      NOT NULL,         -- hex-encoded SHA1 (40 chars)
  last_auth     DATETIME      NULL              -- timestamp of last successful login
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) Create the OD master table
CREATE TABLE OD (
  form_id VARCHAR(36) PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5) Create the form-data table with approval fields
CREATE TABLE OD_FormData (
  form_id                     VARCHAR(36)   PRIMARY KEY,
  requester_name              VARCHAR(100)  NOT NULL,
  requester_address           VARCHAR(255)  NOT NULL,
  requester_ssn               VARCHAR(11)   NOT NULL,
  deceased_name               VARCHAR(100)  NOT NULL,
  deceased_address            VARCHAR(255)  NOT NULL,
  death_date                  DATE          NOT NULL,
  country_of_origin           VARCHAR(100)  NOT NULL,
  proof_of_relationship_path  VARCHAR(255)  NOT NULL,
  death_record_path           VARCHAR(255)  NOT NULL,
  accepted                    BOOLEAN       DEFAULT NULL,  -- now NULL until processed
  denial_reason               VARCHAR(800)  NULL,
  FOREIGN KEY (form_id) REFERENCES OD(form_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
