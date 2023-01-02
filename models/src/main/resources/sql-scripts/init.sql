INSERT INTO request (status, last_updated) VALUES (0, TIMESTAMP '2022-11-26 10:45:48');
INSERT INTO item (name) VALUES ('1001 Cvet čaj šipek s hibiskusom 60g');
INSERT INTO store (name, url) VALUES ('ENGROTUŠ d.o.o.', 'https://www.tus.si');
INSERT INTO store (name, url) VALUES ('Mercator d.o.o.', 'https://www.mercator.si');
INSERT INTO store (name, url) VALUES ('SPAR SLOVENIJA d.o.o.', 'https://www.spar.si');
INSERT INTO price (item_id, store_id, amount, last_updated) VALUES ((SELECT id FROM item WHERE name='1001 Cvet čaj šipek s hibiskusom 60g'), (SELECT id FROM store WHERE name='ENGROTUŠ d.o.o.'), 1.48, TIMESTAMP '2022-11-26 10:45:48');
