INSERT INTO tasks_table(deadline, done, name, priority, progress, user_id)
VALUES
(
    :#${exchangeProperty.deadline}, :#${exchangeProperty.done}, :#${exchangeProperty.name},
    :#${exchangeProperty.priority}, :#${exchangeProperty.progress}, :#userId
);