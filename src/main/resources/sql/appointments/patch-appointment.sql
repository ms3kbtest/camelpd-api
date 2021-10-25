UPDATE appointments_table
SET
date = COALESCE(:#${exchangeProperty.date},date),
start_time = COALESCE(:#${exchangeProperty.startTime},start_time),
end_time = COALESCE(:#${exchangeProperty.endTime},end_time),
description = COALESCE(:#${exchangeProperty.description},description),
appointment_notes = COALESCE(:#${exchangeProperty.appointmentNotes},appointment_notes)
WHERE appointment_id=:#appointmentId AND user_id = :#userId;