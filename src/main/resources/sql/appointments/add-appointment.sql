INSERT INTO appointments_table(date, start_time, end_time, description, appointment_notes, user_id)
VALUES
(
    :#${exchangeProperty.date}, :#${exchangeProperty.startTime}, :#${exchangeProperty.endTime},
    :#${exchangeProperty.description}, :#${exchangeProperty.appointmentNotes}, :#userId
);