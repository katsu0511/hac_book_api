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
	
	CONSTRAINT ck_settings_language_not_blank
		CHECK (length(trim(language)) > 0),
	CONSTRAINT ck_settings_currency_format
		CHECK (currency ~ '^[A-Z]{3}$'),
	CONSTRAINT fk_settings_user
		FOREIGN KEY (user_id)
		REFERENCES users(id)
			ON DELETE CASCADE
			ON UPDATE CASCADE
);

DO $$
BEGIN
	IF NOT EXISTS (
		SELECT 1
		FROM pg_type
		WHERE typname = 'category_type'
	) THEN
		CREATE TYPE category_type AS ENUM ('INCOME', 'EXPENSE');
	END IF;
END
$$;

CREATE TABLE IF NOT EXISTS category_templates(
	id					BIGSERIAL					,
	display_order		INT							NOT NULL,
	name				VARCHAR(100)				NOT NULL,
	type				category_type				NOT NULL,
	PRIMARY KEY			(id)						,
	
	CONSTRAINT ck_category_templates_order_positive
		CHECK (display_order > 0),
	CONSTRAINT ck_category_templates_name_not_blank
		CHECK (length(trim(name)) > 0),
	CONSTRAINT uq_category_templates_name_type
		UNIQUE (name, type)
);

CREATE TABLE IF NOT EXISTS categories(
	id					BIGSERIAL					,
	user_id				BIGINT						NOT NULL,
	parent_id			BIGINT						DEFAULT NULL,
	name				VARCHAR(100)				NOT NULL,
	type				category_type				NOT NULL,
	description			VARCHAR(200)				DEFAULT NULL,
	is_active			BOOLEAN						NOT NULL DEFAULT TRUE,
	PRIMARY KEY			(id)						,
	
	CONSTRAINT ck_categories_parent_not_self
		CHECK (parent_id IS NULL OR parent_id <> id),
	CONSTRAINT ck_categories_name_not_blank
		CHECK (length(trim(name)) > 0),
	CONSTRAINT ck_categories_description_not_blank
		CHECK (description IS NULL OR length(trim(description)) > 0),
	CONSTRAINT uq_categories_user_name_type
		UNIQUE (user_id, name, type),
	CONSTRAINT fk_categories_user
		FOREIGN KEY (user_id)
		REFERENCES users(id)
			ON DELETE CASCADE
			ON UPDATE CASCADE,
	CONSTRAINT fk_categories_parent
		FOREIGN KEY (parent_id)
		REFERENCES categories(id)
			ON DELETE CASCADE
			ON UPDATE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_categories_id_user
ON categories (id, user_id);

CREATE INDEX IF NOT EXISTS idx_categories_user_parent_type_id
ON categories (user_id, type, parent_id ASC, id ASC);

CREATE TABLE IF NOT EXISTS transactions(
	id					BIGSERIAL					,
	user_id				BIGINT						NOT NULL,
	category_id			BIGINT						NOT NULL,
	amount				DECIMAL(10, 2)				NOT NULL,
	currency			VARCHAR(3)					NOT NULL,
	description			VARCHAR(200)				DEFAULT NULL,
	transaction_date	DATE						NOT NULL,
	created_at			TIMESTAMPTZ					NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at			TIMESTAMPTZ					NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY			(id)						,
	
	CONSTRAINT ck_transactions_amount_positive
		CHECK (amount > 0),
	CONSTRAINT ck_transactions_currency_format
		CHECK (currency ~ '^[A-Z]{3}$'),
	CONSTRAINT ck_transactions_description_not_blank
		CHECK (description IS NULL OR length(trim(description)) > 0),
	CONSTRAINT fk_transactions_user
		FOREIGN KEY (user_id)
		REFERENCES users(id)
			ON DELETE CASCADE
			ON UPDATE CASCADE,
	CONSTRAINT fk_transactions_category_owner
		FOREIGN KEY (category_id, user_id)
		REFERENCES categories (id, user_id)
			ON DELETE NO ACTION
			ON UPDATE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_transactions_user_date_desc_updated_at_desc
ON transactions (user_id, transaction_date DESC, updated_at DESC);

CREATE INDEX IF NOT EXISTS idx_transactions_user_category_date
ON transactions (user_id, category_id, transaction_date);

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS $$
BEGIN
	NEW.updated_at = CURRENT_TIMESTAMP;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_transactions_updated_at ON transactions;
CREATE TRIGGER trg_transactions_updated_at
BEFORE UPDATE ON transactions
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

INSERT INTO category_templates (display_order, name, type)
SELECT display_order, name, type
FROM (
	VALUES
		(1, 'Housing', 'EXPENSE'::category_type),
		(2, 'Utilities', 'EXPENSE'::category_type),
		(3, 'Insurance', 'EXPENSE'::category_type),
		(4, 'Vehicle', 'EXPENSE'::category_type),
		(5, 'Education', 'EXPENSE'::category_type),
		(6, 'Tax', 'EXPENSE'::category_type),
		(7, 'Transportation', 'EXPENSE'::category_type),
		(8, 'Food', 'EXPENSE'::category_type),
		(9, 'Supplies', 'EXPENSE'::category_type),
		(10, 'Medical', 'EXPENSE'::category_type),
		(11, 'Clothing', 'EXPENSE'::category_type),
		(12, 'Entertainment', 'EXPENSE'::category_type),
		(13, 'Leisure', 'EXPENSE'::category_type),
		(14, 'Others', 'EXPENSE'::category_type),
		(15, 'Salary', 'INCOME'::category_type),
		(16, 'Others', 'INCOME'::category_type)
) AS tmp(display_order, name, type)
WHERE NOT EXISTS (
	SELECT 1 FROM category_templates c
	WHERE c.name = tmp.name
	AND c.type = tmp.type
);
