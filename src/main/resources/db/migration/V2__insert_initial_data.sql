INSERT INTO isbn_catalog
(isbn, title, author)
VALUES
    ('9782472753066', 'How to Expert Java', 'Jafar Jr'),
    ('9784452689334', 'Agile Methodelogy', 'Vivi Al');


INSERT INTO book
(id,
 isbn,
 created_at,
 updated_at,
 created_by,
 updated_by,
 is_active,
 is_deleted)
VALUES
    ('7cf0b96f-013d-4522-b811-1457237408c6',
     '9784452689334',
     '2026-08-27 16:06:27.966714',
     NULL,
     'SYSTEM',
     NULL,
     1,
     0),
    ('97b9259d-eb67-40ef-ad8f-1d184e346aa0',
     '9782472753066',
     '2026-08-27 15:00:03.709100',
     NULL,
     'SYSTEM',
     NULL,
     1,
     0);


INSERT INTO borrower
(id,
 name,
 email,
 created_at,
 updated_at,
 created_by,
 updated_by,
 is_active,
 is_deleted)
VALUES
    ('4e301360-7fe2-486d-b36e-fce971526ad0',
     'nika',
     'nika@test.com',
     '2026-08-27 16:17:08.324601',
     NULL,
     'SYSTEM',
     NULL,
     1,
     0),
    ('7d3d0a15-ddcd-4bbb-a11b-84a981cfdfa9',
     'nabhan',
     'nabhan@test.com',
     '2026-08-27 16:17:00.090560',
     NULL,
     'SYSTEM',
     NULL,
     1,
     0),
    ('82d22c57-19d5-4e57-8c7c-489adefe62d6',
     'jafar',
     'jafar@test.com',
     '2026-08-27 16:16:36.290430',
     NULL,
     'SYSTEM',
     NULL,
     1,
     0);