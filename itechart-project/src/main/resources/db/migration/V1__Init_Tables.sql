CREATE TABLE status(
	status_id BIGSERIAL,
	status_name VARCHAR(50) NOT NULL,
	PRIMARY KEY (status_id)
);

CREATE TABLE flow(
    record_id BIGSERIAL,
	flow_id VARCHAR(50),
	status_id BIGINT NOT NULL,
	status_date VARCHAR(20) NOT NULL,
	PRIMARY KEY (record_id),
	FOREIGN KEY (status_id) REFERENCES status(status_id)
);