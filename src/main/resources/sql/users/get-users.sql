SELECT Q.*,U.*,S.* FROM
(
	SELECT A.appointment_id AS child_id, A.user_id, A.date, A.start_time, A.end_time, A.description, A.appointment_notes,
	NULL AS done, NULL AS name, NULL AS deadline, NULL AS progress, NULL AS priority
	FROM appointments_table AS A
	UNION
	SELECT T.task_id AS child_id, T.user_id, NULL, NULL, NULL, NULL, NULL, T.done, T.name, T.deadline, T.progress, T.priority
	FROM tasks_table AS T
) as Q RIGHT JOIN users_table AS U on Q.user_id = U.id INNER JOIN settings_table AS S ON U.id = S.user_id
ORDER BY U.id, Q.child_id ASC
;