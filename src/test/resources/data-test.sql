INSERT INTO TOPIC (id, name) VALUES (1, 'Test topic 1');
INSERT INTO TOPIC (id, name) VALUES (2, 'Test topic 2');
INSERT INTO TOPIC (id, name) VALUES (3, 'Test topic 3');

INSERT INTO BOOK (id, title, description) VALUES (1, 'Test book 1', 'Test book 1 description');
INSERT INTO BOOK (id, title, description) VALUES (2, 'Test book 2', 'Test book 2 description');
INSERT INTO BOOK (id, title, description) VALUES (3, 'Test book 3', 'Test book 3 description');

INSERT INTO BOOK_TOPICS (book_id, topics_id) VALUES (1, 1);
INSERT INTO BOOK_TOPICS (book_id, topics_id) VALUES (1, 2);
INSERT INTO BOOK_TOPICS (book_id, topics_id) VALUES (2, 3);
INSERT INTO BOOK_TOPICS (book_id, topics_id) VALUES (3, 1);
