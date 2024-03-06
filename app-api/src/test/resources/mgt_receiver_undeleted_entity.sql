INSERT INTO undeleted_entity (id, site_id, table_name, identifier, in_sync_queue, in_error_queue, date_created)
VALUES (1, 1, 'visit', 'visit-uuid-1', 0, 0, '2024-03-06 00:00:00'),
       (2, 1, 'visit', 'visit-uuid-2', 1, 0, '2024-03-06 00:00:00'),
       (3, 1, 'visit', 'visit-uuid-3', 0, 1, '2024-03-06 00:00:00'),
       (4, 2, 'visit', 'visit-uuid-4', 1, 0, '2024-03-06 00:00:00'),
       (5, 2, 'visit', 'visit-uuid-5', 0, 1, '2024-03-06 00:00:00'),
       (6, 1, 'encounter', 'enc-uuid-1', 0, 0, '2024-03-06 00:00:00'),
       (7, 1, 'encounter', 'enc-uuid-2', 1, 0, '2024-03-06 00:00:00'),
       (8, 1, 'encounter', 'enc-uuid-3', 0, 1, '2024-03-06 00:00:00'),
       (9, 1, 'person', 'person-uuid-1', 0, 0, '2024-03-06 00:00:00'),
       (10, 1, 'person', 'person-uuid-2', 1, 0, '2024-03-06 00:00:00'),
       (11, 1, 'person', 'person-uuid-3', 0, 1, '2024-03-06 00:00:00');
