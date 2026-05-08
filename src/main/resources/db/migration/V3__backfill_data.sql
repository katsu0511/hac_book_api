UPDATE users
SET icon = 'default.png'
WHERE icon IS NULL;

UPDATE settings
SET monthly_saving_goal = 100.0
WHERE monthly_saving_goal IS NULL;
