INSERT INTO receiver_table_reconcile (id, site_reconcile_id, table_name, row_count, remote_start_date, processed_count, last_batch_received, completed, date_created)
VALUES (1, 1, 'person', 50, '2024-02-07 00:02:00', 0, 0, 0, '2024-02-07 00:00:00'),
       (2, 2, 'person', 70, '2024-02-07 00:04:00', 10, 0, 0, '2024-02-07 00:00:00'),
       (3, 3, 'person', 10, '2024-02-07 00:04:00', 10, 0, 1, '2024-02-07 00:00:00'),
       (4, 3, 'visit', 10, '2024-02-07 00:04:00', 10, 0, 1, '2024-02-07 00:00:00'),
       (5, 3, 'encounter', 9, '2024-02-07 00:04:00', 10, 0, 0, '2024-02-07 00:00:00'),
       (6, 4, 'obs', 10, '2024-02-07 00:04:00', 10, 0, 1, '2024-02-07 00:00:00');
