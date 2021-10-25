SELECT count(username)
FROM users_table
WHERE username = COALESCE (:#${exchangeProperty.username}, null)
AND id != :#userId;