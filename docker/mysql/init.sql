CREATE USER IF NOT EXISTS 'blackjack_user'@'%' IDENTIFIED BY 'blackjack_pass';
GRANT ALL PRIVILEGES ON blackjack.* TO 'blackjack_user'@'%';
FLUSH PRIVILEGES;
CREATE DATABASE IF NOT EXISTS blackjack;
SELECT 'MySQL initialization completed successfully' AS status;