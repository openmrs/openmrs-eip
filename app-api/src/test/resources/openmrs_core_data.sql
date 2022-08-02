SET FOREIGN_KEY_CHECKS = 0;
INSERT INTO person (person_id,gender,birthdate_estimated,deathdate_estimated,dead,voided,creator,date_created,uuid)
VALUES (1, 'M', 0, 0, 0 , 0, 1, '2022-06-28 00:00:00', '1b3b12d1-5c4f-415f-871b-b98a22137605');

INSERT INTO users (user_id,person_id,username,system_id,creator,date_created,retired,uuid)
VALUES (1, 1,'admin', 'user-1', 1, '2022-06-28 00:00:00', 0, '1c2b12d1-5c4f-415f-871b-b98a22137606');
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO provider (provider_id, identifier,creator,date_created,retired,uuid)
VALUES (1, 'doctor', 1, '2022-06-28 00:00:00', 0, '1b4b12d1-5c4f-415f-871b-b98a22137607');
