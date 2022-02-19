INSERT INTO orders (order_id, previous_order_id, uuid)
VALUES (101, null, '10783853-608f-49f2-af6a-65c54ff54000'),
       (102, 101, '20783853-608f-49f2-af6a-65c54ff54000'),
       (103, null, '30783853-608f-49f2-af6a-65c54ff54000'),
       (104, 103, '40783853-608f-49f2-af6a-65c54ff54000');

INSERT INTO test_order (order_id)
VALUES (101),
       (102);

INSERT INTO drug_order (order_id)
VALUES (103),
       (104);
