CREATE TABLE dictionary_key (
	id BIGSERIAL,
	key_name VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE dictionary_value (
	id BIGSERIAL,
	value_name VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE dictionary (
	id BIGSERIAL,
	key_id BIGINT NOT NULL,
	value_id BIGINT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (key_id) REFERENCES dictionary_key(id),
	FOREIGN KEY (value_id) REFERENCES dictionary_value(id)
);

INSERT INTO dictionary_key(key_name)
VALUES
   ('COMPANY_NAME'),
   ('Company name'),
   ('DEPARTMENT'),
   ('DEPARTMENT_NAME'),
   ('Department name'),
   ('PAY_DATE'),
   ('pay date');

INSERT INTO dictionary_value (value_name)
VALUES
   ('COMPANY_NAME'),
   ('DEPARTMENT'),
   ('PAY_DATE');

INSERT INTO dictionary (key_id, value_id)
VALUES
   (1, 1),
   (2, 1),
   (3, 2),
   (4, 2),
   (5, 2),
   (6, 3),
   (7, 3);

CREATE INDEX ON dictionary (key_id);
CREATE INDEX ON dictionary (value_id);

CREATE VIEW dictionary_view AS
SELECT dk.key_name, dv.value_name
FROM dictionary
LEFT JOIN dictionary_key dk ON dk.id = dictionary.key_id
LEFT JOIN dictionary_value dv ON dv.id = dictionary.value_id;