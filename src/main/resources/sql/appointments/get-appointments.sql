SELECT * FROM appointments_table WHERE user_id = :#userId
AND (:#${exchangeProperty.date} IS NULL OR date = :#${exchangeProperty.date})
AND (:#${exchangeProperty.descKeywords} IS NULL OR description LIKE :#${exchangeProperty.descKeywords})
AND IF (:#${exchangeProperty.fromDate} IS NOT NULL AND :#${exchangeProperty.toDate} IS NOT NULL,
    date BETWEEN :#${exchangeProperty.fromDate} AND :#${exchangeProperty.toDate},
    user_id = :#userId
    )
;