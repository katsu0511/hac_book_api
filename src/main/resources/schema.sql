CREATE TABLE IF NOT EXISTS users(
	id					BIGINT						AUTO_INCREMENT,
	name				VARCHAR(32)					NOT NULL,
	email				VARCHAR(100)				NOT NULL UNIQUE,
	password			VARCHAR(255)				NOT NULL,
	created_at			TIMESTAMP					NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY			(id)
);

CREATE TABLE IF NOT EXISTS settings(
	user_id				BIGINT						,
	language			VARCHAR(32)					NOT NULL DEFAULT 'English',
	currency			VARCHAR(3)					NOT NULL DEFAULT 'CAD',
	PRIMARY KEY			(user_id)					,
	FOREIGN KEY			(user_id)					REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS categories(
	id					BIGINT						AUTO_INCREMENT,
	user_id				BIGINT						DEFAULT NULL,
	parent_id			BIGINT						DEFAULT NULL,
	name				VARCHAR(100)				NOT NULL,
	type				ENUM ('INCOME', 'EXPENSE')	NOT NULL,
	description			VARCHAR(200)				DEFAULT NULL,
	is_active			BOOLEAN						NOT NULL DEFAULT TRUE,
	PRIMARY KEY			(id)						,
	UNIQUE				(user_id, name)				,
	FOREIGN KEY			(user_id)					REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY			(parent_id)					REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions(
	id					BIGINT						AUTO_INCREMENT,
	user_id				BIGINT						NOT NULL,
	category_id			BIGINT						NOT NULL,
	amount				DECIMAL(10, 2)				NOT NULL DEFAULT 0,
	currency			VARCHAR(3)					NOT NULL DEFAULT 'CAD',
	description			VARCHAR(200)				DEFAULT NULL,
	transaction_date	DATE						NOT NULL,
	created_at			TIMESTAMP					NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at			TIMESTAMP					NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY			(id)						,
	FOREIGN KEY			(user_id)					REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY			(category_id)				REFERENCES categories(id) ON DELETE NO ACTION ON UPDATE CASCADE
);

INSERT INTO categories (user_id, name, type)
SELECT * FROM (
	SELECT NULL AS user_id, 'Salary' AS name, 'INCOME' AS type
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Salary'
	)
	UNION ALL
	SELECT NULL, 'Others', 'INCOME'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Others'
	)
	UNION ALL
	SELECT NULL, 'Housing', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Housing'
	)
	UNION ALL
	SELECT NULL, 'Utilities', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Utilities'
	)
	UNION ALL
	SELECT NULL, 'Insurance', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Insurance'
	)
	UNION ALL
	SELECT NULL, 'Vehicle', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Vehicle'
	)
	UNION ALL
	SELECT NULL, 'Education', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Education'
	)
	UNION ALL
	SELECT NULL, 'Tax', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Tax'
	)
	UNION ALL
	SELECT NULL, 'Transportation', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Transportation'
	)
	UNION ALL
	SELECT NULL, 'Food', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Food'
	)
	UNION ALL
	SELECT NULL, 'Supplies', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Supplies'
	)
	UNION ALL
	SELECT NULL, 'Medical', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Medical'
	)
	UNION ALL
	SELECT NULL, 'Clothing', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Clothing'
	)
	UNION ALL
	SELECT NULL, 'Entertainment', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Entertainment'
	)
	UNION ALL
	SELECT NULL, 'Leisure', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Leisure'
	)
	UNION ALL
	SELECT NULL, 'Others', 'EXPENSE'
	WHERE NOT EXISTS (
		SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Others'
	)
) AS TMP;
