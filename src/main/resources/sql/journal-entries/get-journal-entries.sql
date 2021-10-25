SELECT * FROM journal_entries_table WHERE user_id = :#userId
AND (:#${exchangeProperty.date} IS NULL OR date = :#${exchangeProperty.date})
AND (:#${exchangeProperty.moodScore} IS NULL OR mood_score = :#${exchangeProperty.moodScore})
AND IF (:#${exchangeProperty.fromMoodScore} IS NOT NULL AND :#${exchangeProperty.toMoodScore} IS NOT NULL,
    mood_score BETWEEN :#${exchangeProperty.fromMoodScore} AND :#${exchangeProperty.toMoodScore},
    user_id = :#userId
    )
;