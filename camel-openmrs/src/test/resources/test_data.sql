set FOREIGN_KEY_CHECKS = 0;
INSERT INTO person (person_id,gender,birthdate_estimated,deathdate_estimated,dead,voided,creator,date_created,uuid)
VALUES  (1, 'M', 0, 0, 0 , 0, 1, now(), 'ba3b12d1-5c4f-415f-871b-b98a22137604');

INSERT INTO users (user_id,person_id,system_id,creator,date_created,retired,uuid)
VALUES (1, 1, 'user-1', 1, '2020-06-21 00:00:00', 0, '1a3b12d1-5c4f-415f-871b-b98a22137605');
set FOREIGN_KEY_CHECKS = 1;

INSERT INTO provider (identifier,creator,date_created,retired,uuid)
VALUES ('nurse', 1, now(), 0, '2b3b12d1-5c4f-415f-871b-b98a22137606');
