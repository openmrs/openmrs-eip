INSERT INTO receiver_synced_msg (id, model_class_name, identifier, operation, message_uuid, site_id, entity_payload, is_snapshot, date_sent_by_sender, date_received, sync_outcome, is_cached, evicted_from_cache, is_indexed, search_index_updated, response_sent, date_created)
VALUES (1, 'org.openmrs.eip.component.model.PersonModel', '1bfd940e-32dc-491f-8038-a8f3afe3e36c', 'c', '17beb8bd-287c-47f2-9786-a7b98c933c05', 1, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 0, 0, 0, 0, 0, '2020-08-22 00:00:00.000'),
       (2, 'org.openmrs.eip.component.model.PersonModel', '2bfd940e-32dc-491f-8038-a8f3afe3e36c', 'u', '47beb8bd-287c-47f2-9786-a7b98c933c05', 1, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'ERROR', 0, 0, 0, 0, 0, '2020-08-22 00:00:00.000'),
       (3, 'org.openmrs.eip.component.model.PersonModel', '3bfd940e-32dc-491f-8038-a8f3afe3e36c', 'u', '47beb8bd-287c-47f2-9786-a7b98c933c05', 1, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'CONFLICT', 0, 0, 0, 0, 0, '2020-08-22 00:00:00.000'),
       (4, 'org.openmrs.eip.component.model.PersonModel', '4bfd940e-32dc-491f-8038-a8f3afe3e36c', 'u', '47beb8bd-287c-47f2-9786-a7b98c933c05', 1, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 0, 0, 0, 0, 1, '2020-08-22 00:00:00.000'),
       (5, 'org.openmrs.eip.component.model.PersonModel', '5bfd940e-32dc-491f-8038-a8f3afe3e36c', 'c', '67beb8bd-287c-47f2-9786-a7b98c933c05', 2, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 0, 0, 0, 0, 0, '2020-08-22 01:00:00.000'),

       (101, 'org.openmrs.eip.component.model.PatientModel', '1cfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'b7beb8bd-287c-47f2-9786-a7b98c933c06', 3, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 1, 0, 0, 0, 0, '2020-08-22 00:00:00.002'),
       (102, 'org.openmrs.eip.component.model.PatientModel', '2cfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'c7beb8bd-287c-47f2-9786-a7b98c933c06', 3, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 1, 0, 0, 0, 0, '2020-08-22 00:00:00.000'),
       (103, 'org.openmrs.eip.component.model.PatientModel', '3cfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'd7beb8bd-287c-47f2-9786-a7b98c933c06', 3, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 1, 0, 0, 0, 0, '2020-08-22 00:00:00.001'),
       (104, 'org.openmrs.eip.component.model.PatientModel', '4cfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'd7beb8bd-287c-47f2-9786-a7b98c933c06', 3, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 1, 1, 0, 0, 0, '2020-08-22 00:00:00.000'),
       (105, 'org.openmrs.eip.component.model.PatientModel', '5cfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'd7beb8bd-287c-47f2-9786-a7b98c933c06', 3, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 0, 0, 0, 0, 0, '2020-08-22 00:00:00.000'),
       (106, 'org.openmrs.eip.component.model.PatientModel', '6cfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'd7beb8bd-287c-47f2-9786-a7b98c933c06', 3, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'ERROR', 1, 0, 0, 0, 0, '2020-08-22 00:00:00.000'),
       (107, 'org.openmrs.eip.component.model.PatientModel', '7cfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'd7beb8bd-287c-47f2-9786-a7b98c933c06', 3, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'CONFLICT', 1, 0, 0, 0, 0, '2020-08-22 00:00:00.000'),
       (108, 'org.openmrs.eip.component.model.PatientModel', '8cfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'e7beb8bd-287c-47f2-9786-a7b98c933c06', 2, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 1, 0, 0, 0, 0, '2020-08-22 00:00:00.000'),

       (201, 'org.openmrs.eip.component.model.PatientModel', '1dfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'a8beb8bd-287c-47f2-9786-a7b98c933c06', 4, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 0, 0, 1, 0, 0, '2020-08-22 00:00:00.002'),
       (202, 'org.openmrs.eip.component.model.PatientModel', '2dfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'b8beb8bd-287c-47f2-9786-a7b98c933c06', 4, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 0, 0, 1, 0, 0, '2020-08-22 00:00:00.000'),
       (203, 'org.openmrs.eip.component.model.PatientModel', '3dfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'c8beb8bd-287c-47f2-9786-a7b98c933c06', 4, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 00:00:00.000', 'SUCCESS', 0, 0, 1, 0, 0, '2020-08-22 00:00:00.001'),
       (204, 'org.openmrs.eip.component.model.PatientModel', '4dfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'd7beb8bd-287c-47f2-9786-a7b98c933c06', 4, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 1, 1, 1, 0, 0, '2020-08-22 01:00:00.000'),
       (205, 'org.openmrs.eip.component.model.PatientModel', '5dfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'd7beb8bd-287c-47f2-9786-a7b98c933c06', 4, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 0, 0, 1, 1, 0, '2020-08-22 01:00:00.000'),
       (206, 'org.openmrs.eip.component.model.PatientModel', '6dfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'e8beb8bd-287c-47f2-9786-a7b98c933c06', 4, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 1, 0, 1, 0, 0, '2020-08-22 01:00:00.000'),
       (207, 'org.openmrs.eip.component.model.PatientModel', '7dfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'e8beb8bd-287c-47f2-9786-a7b98c933c06', 4, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 0, 0, 0, 0, 0, '2020-08-22 01:00:00.000'),
       (208, 'org.openmrs.eip.component.model.PatientModel', '7dfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'e8beb8bd-287c-47f2-9786-a7b98c933c06', 4, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'ERROR', 0, 0, 1, 0, 0, '2020-08-22 01:00:00.000'),
       (209, 'org.openmrs.eip.component.model.PatientModel', '7dfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'e8beb8bd-287c-47f2-9786-a7b98c933c06', 4, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'CONFLICT', 0, 0, 1, 0, 0, '2020-08-22 01:00:00.000'),
       (210, 'org.openmrs.eip.component.model.PatientModel', '8dfd940e-32dc-491f-8038-a8f3afe3e36d', 'c', 'd8beb8bd-287c-47f2-9786-a7b98c933c06', 2, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 0, 0, 1, 0, 0, '2020-08-22 01:00:00.000'),

       (301, 'org.openmrs.eip.component.model.PatientModel', '1efd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '69beb8bd-287c-47f2-9786-a7b98c933c06', 5, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 0, 0, 0, 0, 1, '2020-08-22 01:00:00.000'),
       (302, 'org.openmrs.eip.component.model.PatientModel', '2efd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '79beb8bd-287c-47f2-9786-a7b98c933c06', 5, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 1, 1, 0, 0, 1, '2020-08-22 01:00:00.000'),
       (303, 'org.openmrs.eip.component.model.PatientModel', '3efd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '89beb8bd-287c-47f2-9786-a7b98c933c06', 5, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 0, 0, 1, 1, 1, '2020-08-22 01:00:00.000'),
       (304, 'org.openmrs.eip.component.model.PatientModel', '4efd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '99beb8bd-287c-47f2-9786-a7b98c933c06', 5, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 1, 1, 1, 1, 1, '2020-08-22 01:00:00.000'),
       (305, 'org.openmrs.eip.component.model.PatientModel', '5efd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '99beb8bd-287c-47f2-9786-a7b98c933c06', 5, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'ERROR', 0, 0, 0, 0, 1, '2020-08-22 01:00:00.000'),
       (306, 'org.openmrs.eip.component.model.PatientModel', '6efd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '99beb8bd-287c-47f2-9786-a7b98c933c06', 5, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'CONFLICT', 0, 0, 0, 0, 1, '2020-08-22 01:00:00.000'),
       (307, 'org.openmrs.eip.component.model.PatientModel', '7efd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '19beb8bd-287c-47f2-9786-a7b98c933c06', 5, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 0, 0, 0, 0, 0, '2020-08-22 01:00:00.000'),
       (308, 'org.openmrs.eip.component.model.PatientModel', '8efd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '29beb8bd-287c-47f2-9786-a7b98c933c06', 5, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 1, 0, 0, 0, 1, '2020-08-22 01:00:00.000'),
       (309, 'org.openmrs.eip.component.model.PatientModel', '9efd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '39beb8bd-287c-47f2-9786-a7b98c933c06', 5, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 0, 0, 1, 0, 1, '2020-08-22 01:00:00.000'),
       (310, 'org.openmrs.eip.component.model.PatientModel', '0ffd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '49beb8bd-287c-47f2-9786-a7b98c933c06', 5, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 1, 0, 1, 0, 1, '2020-08-22 01:00:00.000'),
       (311, 'org.openmrs.eip.component.model.PatientModel', '1ffd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '59beb8bd-287c-47f2-9786-a7b98c933c06', 5, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 1, 1, 1, 0, 1, '2020-08-22 01:00:00.000'),
       (312, 'org.openmrs.eip.component.model.PatientModel', '2ffd940e-32dc-491f-8038-a8f3afe3e36d', 'c', '99beb8bd-287c-47f2-9786-a7b98c933c06', 2, '{}', 0, '2020-08-21 00:00:00', '2020-08-22 01:00:00.000', 'SUCCESS', 1, 1, 1, 1, 1, '2020-08-22 01:00:00.000');