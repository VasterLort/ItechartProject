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

INSERT INTO dictionary_key (key_name)
VALUES
   ('COMPANY_NAME'),
   ('Company name'),
   ('DEPARTMENT'),
   ('DEPARTMENT_NAME'),
   ('Department name'),
   ('PAY_DATE'),
   ('pay date'),
   ('IDENTIFICATION_NUMBER'),
   ('IDENTIFICATION NUMBER'),
   ('ID'),
   ('id'),
   ('FIRST_NAME'),
   ('FIRST NAME'),
   ('FNAME'),
   ('first name'),
   ('LAST_NAME'),
   ('LAST NAME'),
   ('LNAME'),
   ('last name'),
   ('BIRTH_DATE'),
   ('BDATE'),
   ('BIRTH DATE'),
   ('birth date'),
   ('WORKING_HOURS'),
   ('WHOURS'),
   ('GROSS_AMOUNT'),
   ('GAMOUNT'),
   ('AT_AMOUNT'),
   ('at amount'),
   ('HIRE_DATE'),
   ('hire date'),
   ('DISMISSAL_DATE'),
   ('dismissal date'),
   ('GENDER'),
   ('Gender'),
   ('gender'),
   ('g'),
   ('POSTAL_CODE'),
   ('postal code'),
   ('MALE'),
   ('M'),
   ('man'),
   ('FEMALE'),
   ('W'),
   ('woman');

INSERT INTO dictionary_value (value_name)
VALUES
   ('COMPANY_NAME'),
   ('DEPARTMENT'),
   ('PAY_DATE'),
   ('IDENTIFICATION_NUMBER'),
   ('FIRST_NAME'),
   ('LAST_NAME'),
   ('BIRTH_DATE'),
   ('WORKING_HOURS'),
   ('GROSS_AMOUNT'),
   ('AT_AMOUNT'),
   ('HIRE_DATE'),
   ('DISMISSAL_DATE'),
   ('GENDER'),
   ('POSTAL_CODE'),
   ('MALE'),
   ('FEMALE');

INSERT INTO dictionary (key_id, value_id)
VALUES
   (1, 1),
   (2, 1),
   (3, 2),
   (4, 2),
   (5, 2),
   (6, 3),
   (7, 3),
   (8, 4),
   (9, 4),
   (10, 4),
   (11, 4),
   (12, 5),
   (13, 5),
   (14, 5),
   (15, 5),
   (16, 6),
   (17, 6),
   (18, 6),
   (19, 6),
   (20, 7),
   (21, 7),
   (22, 7),
   (23, 7),
   (24, 8),
   (25, 8),
   (26, 9),
   (27, 9),
   (28, 10),
   (29, 10),
   (30, 11),
   (31, 11),
   (32, 12),
   (33, 12),
   (34, 13),
   (35, 13),
   (36, 13),
   (37, 13),
   (38, 14),
   (39, 14),
   (40, 15),
   (41, 15),
   (42, 15),
   (43, 16),
   (44, 16),
   (45, 16);

CREATE INDEX ON dictionary (key_id);
CREATE INDEX ON dictionary (value_id);

CREATE VIEW dictionary_view AS
SELECT dk.key_name, dv.value_name
FROM dictionary
LEFT JOIN dictionary_key dk ON dk.id = dictionary.key_id
LEFT JOIN dictionary_value dv ON dv.id = dictionary.value_id;