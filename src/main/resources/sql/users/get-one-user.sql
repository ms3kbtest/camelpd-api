SELECT Q.*,S.* FROM
(
    SELECT U.*, A.appointment_id AS child_id, A.user_id, A.date, A.start_time, A.end_time, A.description, A.appointment_notes,
    NULL AS done, NULL AS name, NULL AS deadline, NULL AS progress, NULL AS priority
    FROM users_table AS U LEFT JOIN appointments_table AS A on U.id = A.user_id WHERE U.id = :#userId
    UNION
    SELECT U.*, T.task_id AS child_id, T.user_id, NULL, NULL, NULL, NULL, NULL, T.done, T.name, T.deadline, T.progress, T.priority
    FROM users_table AS U LEFT JOIN tasks_table AS T on U.id = T.user_id WHERE U.id = :#userId
) as Q INNER JOIN settings_table as S on Q.id = S.user_id WHERE Q.id = :#userId
ORDER BY Q.id, Q.child_id ASC
;