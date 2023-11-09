INSERT INTO orders (order_id, previous_order_id, uuid)
VALUES (1, null, 'i0783853-608f-49f2-af6a-65c54ff54000'),
       (2, 1, 'j0783853-608f-49f2-af6a-65c54ff54000'),
       (101, null, 'a0783853-608f-49f2-af6a-65c54ff54000'),
       (102, 101, 'b0783853-608f-49f2-af6a-65c54ff54000'),
       (103, null, 'c0783853-608f-49f2-af6a-65c54ff54000'),
       (104, 103, 'd0783853-608f-49f2-af6a-65c54ff54000'),
       (105, null, 'e0783853-608f-49f2-af6a-65c54ff54000'),
       (106, 105, 'f0783853-608f-49f2-af6a-65c54ff54000'),
       (107, null, 'g0783853-608f-49f2-af6a-65c54ff54000'),
       (108, 107, 'h0783853-608f-49f2-af6a-65c54ff54000'),
       (109, null, 'k0783853-608f-49f2-af6a-65c54ff54000'),
       (110, 109, 'l0783853-608f-49f2-af6a-65c54ff54000'),
       (111, null, '10783852-608f-49f2-af6a-65c54ff54000'),
       (112, 111, '20783852-608f-49f2-af6a-65c54ff54000'),
       (113, null, '30783852-608f-49f2-af6a-65c54ff54000'),
       (150, 109, 'bd389083-3967-4d8e-96bc-68cc901c6027'),
       (114, 113, '40783852-608f-49f2-af6a-65c54ff54000');

INSERT INTO test_order (order_id)
VALUES (101),
       (102),
       (105),
       (106);

INSERT INTO drug_order (order_id)
VALUES (103),
       (104),
       (107),
       (108);

INSERT INTO referral_order (order_id)
VALUES (111),
       (112),
       (113),
       (114);
