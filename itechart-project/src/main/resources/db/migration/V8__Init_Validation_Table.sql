create TABLE flow_validation (
	record_id BIGSERIAL,
	flow_id VARCHAR(100) NOT NULL,
	file_name VARCHAR(100) NOT NULL,
	company_name VARCHAR(100) NOT NULL,
	department_name VARCHAR(100) NOT NULL,
	pay_date VARCHAR(100) NOT NULL,
	content jsonb,
	creation_date VARCHAR(100) NOT NULL,
	PRIMARY KEY (record_id)
);