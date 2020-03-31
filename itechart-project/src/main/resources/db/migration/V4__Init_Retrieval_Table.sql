CREATE TABLE flow_retrieval (
	record_id BIGSERIAL,
	flow_id VARCHAR(100) NOT NULL,
	file_name VARCHAR(100) NOT NULL,
	content TEXT NOT NULL,
	creation_date VARCHAR(100) NOT NULL,
	PRIMARY KEY (record_id)
);