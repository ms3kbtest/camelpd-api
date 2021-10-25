DELETE FROM tasks_table
WHERE user_id = :#userId 
AND (:#${exchangeProperty.done}
IS NULL OR done = :#${exchangeProperty.done});