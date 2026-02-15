CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE IF NOT EXISTS users(
	id					BIGSERIAL					,
	name				VARCHAR(32)					NOT NULL,
	email				CITEXT						NOT NULL UNIQUE,
	password			VARCHAR(255)				NOT NULL,
	created_at			TIMESTAMPTZ					NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY			(id)						,
	CONSTRAINT ck_users_name_not_blank
		CHECK (length(trim(name)) > 0),
	CONSTRAINT ck_users_email_not_blank
		CHECK (length(trim(email)) > 0),
	CONSTRAINT ck_users_email_length
		CHECK (length(email) <= 100),
	CONSTRAINT ck_users_password_not_blank
		CHECK (length(trim(password)) > 0)
);

CREATE TABLE IF NOT EXISTS settings(
	user_id				BIGINT						,
	language			VARCHAR(32)					NOT NULL DEFAULT 'English',
	currency			VARCHAR(3)					NOT NULL DEFAULT 'CAD',
	PRIMARY KEY			(user_id)					,
	FOREIGN KEY			(user_id)					REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS categories(
	id					BIGSERIAL					,
	user_id				BIGINT						DEFAULT NULL,
	parent_id			BIGINT						DEFAULT NULL,
	name				VARCHAR(100)				NOT NULL,
	type				VARCHAR(10)					NOT NULL CHECK (type IN ('INCOME','EXPENSE')),
	description			VARCHAR(200)				DEFAULT NULL,
	is_active			BOOLEAN						NOT NULL DEFAULT TRUE,
	PRIMARY KEY			(id)						,
	UNIQUE				(user_id, name)				,
	FOREIGN KEY			(user_id)					REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY			(parent_id)					REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions(
	id					BIGSERIAL					,
	user_id				BIGINT						NOT NULL,
	category_id			BIGINT						NOT NULL,
	amount				DECIMAL(10, 2)				NOT NULL DEFAULT 0,
	currency			VARCHAR(3)					NOT NULL,
	description			VARCHAR(200)				DEFAULT NULL,
	transaction_date	DATE						NOT NULL,
	created_at			TIMESTAMP					NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at			TIMESTAMP					NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY			(id)						,
	FOREIGN KEY			(user_id)					REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY			(category_id)				REFERENCES categories(id) ON DELETE NO ACTION ON UPDATE CASCADE
);

INSERT INTO categories (user_id, name, type)
SELECT NULL::BIGINT, name, type
FROM (
	VALUES
		('Housing', 'EXPENSE'),
		('Utilities', 'EXPENSE'),
		('Insurance', 'EXPENSE'),
		('Vehicle', 'EXPENSE'),
		('Education', 'EXPENSE'),
		('Tax', 'EXPENSE'),
		('Transportation', 'EXPENSE'),
		('Food', 'EXPENSE'),
		('Supplies', 'EXPENSE'),
		('Medical', 'EXPENSE'),
		('Clothing', 'EXPENSE'),
		('Entertainment', 'EXPENSE'),
		('Leisure', 'EXPENSE'),
		('Others', 'EXPENSE'),
		('Salary', 'INCOME'),
		('Others', 'INCOME')
) AS tmp(name, type)
WHERE NOT EXISTS (
	SELECT 1 FROM categories c
	WHERE c.user_id IS NULL AND c.name = tmp.name
);
