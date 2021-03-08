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
CREATE INDEX IF NOT EXISTS login_token_series_index ON login_tokens USING hash (series);;

/* Insert only if creator of the class with specified id is the teacher of
   the student with specified id */
CREATE OR REPLACE FUNCTION class_participants_function()
    RETURNS TRIGGER AS
$$
BEGIN
    RAISE DEBUG 'Trigger function class_participants_function() fired';
    IF EXISTS(
            SELECT 1
            FROM lessons
                     INNER JOIN teachers ON lessons.teacher_id = teachers.id
                     INNER JOIN students_teachers ON teachers.id = students_teachers.teacher_id
                     INNER JOIN students ON students_teachers.student_id = students.id
            WHERE lessons.id = new.lesson_id
              AND students.id = new.student_id
        )
    THEN
        RETURN new;
    ELSE
        RAISE EXCEPTION 'Creator of the class with id % is not teacher of student with id %',
            new.lesson_id, new.student_id;
        RETURN NULL;
    END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS class_participants_trigger ON lessons_students;
CREATE TRIGGER class_participants_trigger
    BEFORE INSERT
    ON lessons_students
    FOR EACH ROW
EXECUTE PROCEDURE class_participants_function();

/* Insert only if new class doesn't overlap other teacher's classes and starts
   after now */
CREATE OR REPLACE FUNCTION class_check_time_function()
    RETURNS TRIGGER AS
$$
DECLARE
    row lessons%rowtype;
BEGIN
    RAISE DEBUG 'Trigger function class_check_time_function() fired';
    IF new.start_date < localtimestamp
    THEN
        RAISE EXCEPTION 'Start time of new lesson is before now';
        RETURN NULL;
    END IF;
    FOR row IN
        SELECT *
        FROM lessons
        WHERE lessons.teacher_id = new.teacher_id
          AND lessons.start_date >= localtimestamp
        LOOP
            IF NOT ((new.start_date > row.start_date AND new.start_date >= row.end_date) OR
               (new.end_date <= row.start_date AND new.end_date < row.end_date))
            THEN
                RAISE EXCEPTION 'Lesson start time is not unique and overlaps class with id %', row.id;
                RETURN NULL;
            END IF;
        END LOOP;
    RETURN new;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS class_check_time_trigger ON lessons;
CREATE TRIGGER class_check_time_trigger
    BEFORE INSERT
    ON lessons
    FOR EACH ROW
EXECUTE PROCEDURE class_check_time_function();

/* Insert only if student with specified id is participant of
   class with specified id */
CREATE OR REPLACE FUNCTION class_submission_function()
    RETURNS TRIGGER AS
$$
BEGIN
    RAISE DEBUG 'Trigger function class_submission_function() fired';
    IF EXISTS(
            SELECT 1
            FROM lessons_students
            WHERE student_id = new.student_id
              AND lesson_id = new.lesson_id
        )
    THEN
        RETURN new;
    ELSE
        RAISE EXCEPTION 'Student with id % is not participant of class with id %',
            new.student_id, new.lesson_id;
        RETURN NULL;
    END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS class_submission_trigger ON lesson_submissions;
CREATE TRIGGER class_submission_trigger
    BEFORE INSERT
    ON lesson_submissions
    FOR EACH ROW
EXECUTE PROCEDURE class_submission_function();