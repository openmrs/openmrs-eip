INSERT INTO receiver_jms_msg (id, msg_type, body, site_id, msg_id, date_created)
VALUES (3, 'SYNC', x'74657374', 'remote1', '1cef940e-32dc-491f-8038-a8f3afe3e37d', '2023-01-25 12:00:00.003'),
       (1, 'RECONCILIATION', x'74657374', null, null, '2023-01-25 12:00:00.001'),
       (2, 'SYNC', x'74657374', null, null, '2023-01-25 12:00:00.002');
