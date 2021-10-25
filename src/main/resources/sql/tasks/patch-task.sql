UPDATE tasks_table
SET
deadline = COALESCE(:#${exchangeProperty.deadline},deadline),
done = COALESCE(:#${exchangeProperty.done},done),
name = COALESCE(:#${exchangeProperty.name},name),
priority = COALESCE(:#${exchangeProperty.priority},priority),
progress = COALESCE(:#${exchangeProperty.progress},progress)
WHERE task_id=:#taskId AND user_id = :#userId;