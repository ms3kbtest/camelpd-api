SELECT count(email)
FROM users_table
WHERE email = COALESCE (:#${exchangeProperty.email}, null)
AND id != :#userId;