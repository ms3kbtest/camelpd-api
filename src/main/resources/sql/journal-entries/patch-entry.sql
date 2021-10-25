UPDATE journal_entries_table
SET
date = COALESCE(:#${exchangeProperty.date},date),
mood_score = COALESCE(:#${exchangeProperty.moodScore},mood_score),
positives = COALESCE(:#${exchangeProperty.positives},positives),
summary = COALESCE(:#${exchangeProperty.summary},summary)
WHERE entry_id=:#journalEntryId AND user_id = :#userId;