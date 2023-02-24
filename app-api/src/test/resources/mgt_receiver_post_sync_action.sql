INSERT INTO receiver_post_sync_action (id, msg_id, status, action_type, date_created, date_processed, status_msg)
VALUES (1, 1, 'NEW', 'SEND_RESPONSE', '2020-08-22 00:00:00', null, null),
       (2, 1, 'NEW', 'CACHE_EVICT', '2020-08-22 00:00:00', null, null),
       (3, 1, 'NEW', 'SEARCH_INDEX_UPDATE', '2020-08-22 00:00:00', null, null),
       (4, 6, 'SUCCESS', 'SEND_RESPONSE', '2020-08-22 00:00:00', '2020-08-22 00:00:01.000', null),
       (5, 6, 'NEW', 'CACHE_EVICT', '2020-08-22 00:00:00', null, null),
       (6, 6, 'NEW', 'SEARCH_INDEX_UPDATE', '2020-08-22 00:00:00', null, null),
       (7, 7, 'FAILURE', 'SEND_RESPONSE', '2020-08-22 00:00:00', '2020-08-22 00:00:01.000', 'Test Error'),
       (8, 7, 'NEW', 'CACHE_EVICT', '2020-08-22 00:00:00', null, null),
       (9, 7, 'NEW', 'SEARCH_INDEX_UPDATE', '2020-08-22 00:00:00', null, null),
       (10, 8, 'NEW', 'SEND_RESPONSE', '2020-08-22 00:00:00', null, null),
       (11, 8, 'NEW', 'CACHE_EVICT', '2020-08-22 00:00:00', null, null),
       (12, 8, 'NEW', 'SEARCH_INDEX_UPDATE', '2020-08-22 00:00:00', null, null);
