set FOREIGN_KEY_CHECKS = 0;
INSERT INTO person (person_id,gender,birthdate_estimated,deathdate_estimated,dead,voided,creator,date_created,uuid)
VALUES  (1, 'M', 0, 0, 0 , 0, 1, now(), 'ba3b12d1-5c4f-415f-871b-b98a22137604');

INSERT INTO users (user_id,person_id,username,system_id,creator,date_created,retired,uuid)
VALUES (1, 1, 'user1', 'userSystemId1', 1, '2020-06-21 00:00:00', 0, '1a3b12d1-5c4f-415f-871b-b98a22137605');
set FOREIGN_KEY_CHECKS = 1;

INSERT INTO person (person_id,gender,birthdate_estimated,deathdate_estimated,dead,voided,creator,date_created,uuid)
VALUES  (2, 'M', 0, 0, 0 , 0, 1, now(), '1b3b12e4-5c4f-415f-871b-b98a22137601');

INSERT INTO users (user_id,person_id,username,system_id,creator,date_created,retired,uuid)
VALUES (2, 2, 'user2', 'userSystemId2', 1, '2020-06-21 00:00:00', 0, '2a3b12d1-5c4f-415f-871b-b98a22137605'),
       (3, 2, 'user2-site-uuid', 'userSystemId3', 1, '2020-06-21 00:00:00', 0, '3a3b12d1-5c4f-415f-871b-b98a22137605'),
       (4, 2, 'user4', 'userSystemId2-site-uuid', 1, '2020-06-21 00:00:00', 0, '4a3b12d1-5c4f-415f-871b-b98a22137605');

INSERT INTO provider (identifier,creator,date_created,retired,uuid)
VALUES ('nurse', 1, '2020-06-21 00:00:00', 0, '2b3b12d1-5c4f-415f-871b-b98a22137606');
