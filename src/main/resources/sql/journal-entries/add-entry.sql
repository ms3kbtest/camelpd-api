INSERT INTO journal_entries_table(date, mood_score, positives, summary, user_id)
VALUES(
    :#${exchangeProperty.date}, :#${exchangeProperty.moodScore}, :#${exchangeProperty.positives},
    :#${exchangeProperty.summary}, :#userId
);