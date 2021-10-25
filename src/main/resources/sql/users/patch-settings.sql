UPDATE settings_table
SET
app_alerts = COALESCE(:#${exchangeProperty.alerts},app_alerts),
email_notifications = COALESCE(:#${exchangeProperty.notifications},email_notifications),
theme = COALESCE(:#${exchangeProperty.theme},theme)
WHERE user_id=:#userId;