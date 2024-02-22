INSERT INTO mgt_sender_table_reconcile (id, table_name, row_count, end_id, snapshot_date, is_started, last_processed_id, date_created)
VALUES (1, 'person', 50, 50, '2024-02-15 00:00:30', 0, 0, '2024-02-15 00:00:00'),
       (2, 'visit', 70, 80, '2024-02-15 00:00:35', 1, 10, '2024-02-15 00:00:00'),
       (3, 'encounter', 60, 65, '2024-02-15 00:00:35', 1, 65, '2024-02-15 00:00:00'),
       (4, 'obs', 0, 0, '2024-02-15 00:00:35', 0, 0, '2024-02-15 00:00:00');
