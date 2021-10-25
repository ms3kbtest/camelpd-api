SELECT * FROM tasks_table WHERE user_id = :#userId
AND (:#${exchangeProperty.done} IS NULL OR done = :#${exchangeProperty.done})
AND (:#${exchangeProperty.priority} IS NULL OR priority = :#${exchangeProperty.priority})
AND (:#${exchangeProperty.progress} IS NULL OR progress = :#${exchangeProperty.progress})
AND (:#${exchangeProperty.deadline} IS NULL OR deadline = :#${exchangeProperty.deadline})
AND (:#${exchangeProperty.priorityGTE} IS NULL OR priority >= :#${exchangeProperty.priorityGTE})
AND IF (:#${exchangeProperty.fromProgress} IS NOT NULL AND :#${exchangeProperty.toProgress} IS NOT NULL,
    progress BETWEEN :#${exchangeProperty.fromProgress} AND :#${exchangeProperty.toProgress},
    user_id = :#userId
    )
;