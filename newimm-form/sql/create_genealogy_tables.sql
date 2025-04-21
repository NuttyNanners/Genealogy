CREATE TABLE OD (
    form_id VARCHAR(36) PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE OD_FormData (
    form_id VARCHAR(36) PRIMARY KEY,
    requester_name VARCHAR(255),
    requester_address TEXT,
    requester_ssn VARCHAR(20),
    deceased_name VARCHAR(255),
    deceased_address TEXT,
    death_date DATE,
    country_of_origin VARCHAR(100),
    proof_of_relationship_path TEXT,
    death_record_path TEXT,
    FOREIGN KEY (form_id) REFERENCES OD(form_id)
);