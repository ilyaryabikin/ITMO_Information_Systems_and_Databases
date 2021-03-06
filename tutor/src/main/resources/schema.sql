CREATE TABLE IF NOT EXISTS roles
(
    id   integer PRIMARY KEY,
    name character varying(63) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id          serial PRIMARY KEY,
    email       character varying(63) UNIQUE NOT NULL,
    password    character varying(63)        NOT NULL,
    avatar_uuid character varying(63) UNIQUE,
    role_id     integer                      NOT NULL REFERENCES roles ON DELETE RESTRICT,
    CHECK (email ~* '[A-Z0-9_!#$%&''*+/=?`{|}~^-]+(?:\.[A-Z0-9_!#$%&''*+/=?`{|}~^-]+)*@[A-Z0-9-]+(?:\.[A-Z0-9-]+)*')
);

CREATE TABLE IF NOT EXISTS login_tokens
(
    id        serial PRIMARY KEY,
    user_id   integer                      NOT NULL REFERENCES users ON DELETE CASCADE,
    series    character varying(63) UNIQUE NOT NULL,
    value     character varying(63)        NOT NULL,
    last_used timestamp                    NOT NULL
);

CREATE TABLE IF NOT EXISTS students
(
    id           integer PRIMARY KEY REFERENCES users ON DELETE CASCADE,
    name         character varying(63) NOT NULL,
    surname      character varying(63) NOT NULL,
    middle_name  character varying(63),
    birth_date   date,
    city         character varying(63),
    phone_number character varying(63),
    CHECK (phone_number IS NULL OR
           (phone_number IS NOT NULL AND
            phone_number ~ '\+(?:[0-9] ?){6,14}[0-9]'))
);

CREATE TABLE IF NOT EXISTS teachers
(
    id           integer PRIMARY KEY REFERENCES users ON DELETE CASCADE,
    name         character varying(63) NOT NULL,
    surname      character varying(63) NOT NULL,
    middle_name  character varying(63),
    birth_date   date,
    city         character varying(63),
    phone_number character varying(63),
    CHECK (phone_number IS NULL OR
           (phone_number IS NOT NULL AND
            phone_number ~ '\+(?:[0-9] ?){6,14}[0-9]'))
);

CREATE TABLE IF NOT EXISTS students_teachers
(
    student_id integer NOT NULL REFERENCES students ON DELETE CASCADE,
    teacher_id integer NOT NULL REFERENCES teachers ON DELETE CASCADE,
    PRIMARY KEY (student_id, teacher_id)
);

CREATE TABLE IF NOT EXISTS subjects
(
    id   serial PRIMARY KEY,
    name character varying(63) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS teachers_subjects
(
    teacher_id integer NOT NULL REFERENCES teachers ON DELETE CASCADE,
    subject_id integer NOT NULL REFERENCES subjects ON DELETE RESTRICT,
    PRIMARY KEY (teacher_id, subject_id)
);

CREATE TABLE IF NOT EXISTS lessons
(
    id         serial PRIMARY KEY,
    teacher_id integer   NOT NULL REFERENCES teachers ON DELETE RESTRICT,
    subject_id integer   NOT NULL REFERENCES subjects ON DELETE RESTRICT,
    start_date timestamp NOT NULL,
    end_date   timestamp NOT NULL,
    name       character varying(255),
    CHECK (end_date > start_date)
);

CREATE TABLE IF NOT EXISTS materials
(
    id         serial PRIMARY KEY,
    teacher_id integer                       NOT NULL REFERENCES teachers ON DELETE CASCADE,
    lesson_id  integer                       NOT NULL REFERENCES lessons ON DELETE CASCADE,
    name       character varying(255)        NOT NULL,
    uuid       character varying(255) UNIQUE NOT NULL
);

/*CREATE TABLE IF NOT EXISTS lessons_materials
(
    lesson_id   integer NOT NULL REFERENCES lessons ON DELETE RESTRICT,
    material_id integer NOT NULL REFERENCES materials ON DELETE RESTRICT,
    PRIMARY KEY (lesson_id, material_id)
);*/

CREATE TABLE IF NOT EXISTS lessons_students
(
    lesson_id  integer NOT NULL REFERENCES lessons ON DELETE RESTRICT,
    student_id integer NOT NULL REFERENCES students ON DELETE RESTRICT,
    PRIMARY KEY (lesson_id, student_id)
);

CREATE TABLE IF NOT EXISTS lesson_submissions
(
    id         serial PRIMARY KEY,
    lesson_id  integer                       NOT NULL REFERENCES lessons ON DELETE RESTRICT,
    student_id integer                       NOT NULL REFERENCES students ON DELETE RESTRICT,
    name       character varying(255)        NOT NULL,
    uuid       character varying(255) UNIQUE NOT NULL,
    grade      integer,
    CHECK (grade IS NULL OR
           (grade >= 0 AND
            grade < 6))
);

CREATE INDEX IF NOT EXISTS user_email_index ON users USING hash (email);
CREATE INDEX IF NOT EXISTS user_password_index ON users USING hash (password);
CREATE INDEX IF NOT EXISTS login_token_series_index ON login_tokens USING hash (series);