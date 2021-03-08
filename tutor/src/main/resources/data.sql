INSERT INTO roles
VALUES (1, 'ROLE_STUDENT'),
       (2, 'ROLE_TEACHER')
ON CONFLICT (id) DO UPDATE SET name = excluded.name;

INSERT INTO subjects (name)
VALUES ('Maths'),
       ('Russian'),
       ('Literature'),
       ('English'),
       ('German'),
       ('French'),
       ('Spanish'),
       ('Italian'),
       ('History'),
       ('Geography'),
       ('Sociology'),
       ('Economics'),
       ('Informatics'),
       ('Biology'),
       ('Chemistry'),
       ('Physics'),
       ('Astronomy'),
       ('Law')
ON CONFLICT (name) DO UPDATE SET name = excluded.name;;