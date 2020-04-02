CREATE TABLE flow_transformation (
	record_id BIGSERIAL,
	flow_id VARCHAR(100) NOT NULL,
	file_name VARCHAR(100) NOT NULL,
	content jsonb,
	creation_date VARCHAR(100) NOT NULL,
	PRIMARY KEY (record_id)
);