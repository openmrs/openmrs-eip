INSERT INTO reconcile_table_summary (id, reconcile_id, site_id, table_name, missing_count, missing_sync_count, missing_error_count, undeleted_count, undeleted_sync_count, undeleted_error_count, date_created)
VALUES (1, 1, 1, 'person', 5, 1, 2, 9, 4, 3, '2024-05-17 00:00:00'),
       (2, 1, 1, 'patient', 0, 0, 0, 0, 0, 0, '2024-05-17 00:00:00'),
       (3, 1, 1, 'person_name', 3, 1, 1, 5, 2, 1, '2024-05-17 00:00:00'),
       (4, 1, 2, 'person', 0, 0, 0, 0, 0, 0, '2024-05-17 00:00:00'),
       (5, 1, 2, 'patient', 0, 0, 0, 0, 0, 0, '2024-05-17 00:00:00'),
       (6, 1, 2, 'person_name', 3, 1, 1, 4, 1, 1, '2024-02-17 00:00:00'),
       (7, 2, 1, 'person', 0, 0, 0, 0, 0, 0, '2024-05-16 00:00:00'),
       (8, 2, 1, 'patient', 0, 0, 0, 0, 0, 0, '2024-05-16 00:00:00'),
       (9, 2, 1, 'person_name', 3, 1, 1, 4, 1, 1, '2024-02-16 00:00:00'),
       (10, 2, 2, 'person', 0, 0, 0, 0, 0, 0, '2024-05-16 00:00:00'),
       (11, 2, 2, 'patient', 0, 0, 0, 0, 0, 0, '2024-05-16 00:00:00'),
       (12, 2, 2, 'person_name', 0, 0, 0, 0, 0, 0, '2024-02-16 00:00:00');
