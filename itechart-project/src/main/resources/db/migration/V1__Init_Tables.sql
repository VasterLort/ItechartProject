CREATE TABLE status(
	status_id BIGSERIAL,
	status_name VARCHAR (50) NOT NULL,
	active BOOLEAN NOT NULL,
	PRIMARY KEY (status_id)
);