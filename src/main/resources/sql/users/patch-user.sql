UPDATE users_table
SET
birthday = COALESCE(:#${exchangeProperty.birthday},birthday),
email = COALESCE(:#${exchangeProperty.email},email),
first_name = COALESCE(:#${exchangeProperty.first},first_name),
last_name = COALESCE(:#${exchangeProperty.last},last_name),
username = COALESCE(:#${exchangeProperty.username},username)
WHERE id=:#userId;