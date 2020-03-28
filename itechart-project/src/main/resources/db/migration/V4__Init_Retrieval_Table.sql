CREATE TABLE retrieval(
	record_id BIGSERIAL,
	flow_id VARCHAR(50) NOT NULL,
	file_name VARCHAR(50) NOT NULL,
	content TEXT NOT NULL,
	PRIMARY KEY (record_id)
);