ALTER TABLE users
ADD COLUMN icon VARCHAR(255);

ALTER TABLE settings
ADD COLUMN monthly_saving_goal DECIMAL(10,2);
