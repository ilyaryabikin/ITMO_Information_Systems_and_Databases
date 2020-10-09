DROP TABLE IF EXISTS caves CASCADE;
DROP TABLE IF EXISTS crystal_families CASCADE;
DROP TABLE IF EXISTS centric_types CASCADE;
DROP TABLE IF EXISTS crystal_structures CASCADE;
DROP TABLE IF EXISTS crystals CASCADE;
DROP TABLE IF EXISTS figures CASCADE;
DROP TABLE IF EXISTS geometric_shapes CASCADE;
DROP TABLE IF EXISTS figures_shapes CASCADE;

CREATE TABLE IF NOT EXISTS caves (
    id integer PRIMARY KEY,
    latitude numeric(8, 6) NOT NULL,
    longitude numeric(9, 6) NOT NULL,
    country character varying(64) NOT NULL,
    name character varying(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS crystal_families (
    id integer PRIMARY KEY,
    prefix character(1) UNIQUE NOT NULL,
    name character varying(64) UNIQUE NOT NULL,
    CHECK (prefix ~ '[a-z]')
);

CREATE TABLE IF NOT EXISTS centric_types (
    id integer PRIMARY KEY,
    prefix character(1) UNIQUE NOT NULL,
    name character varying(64) UNIQUE NOT NULL,
    equivalent_points smallint NOT NULL,
    CHECK (prefix ~ '[A-Z]')
);

CREATE TABLE IF NOT EXISTS crystal_structures (
    id integer,
    crystal_family_id integer REFERENCES crystal_families,
    centric_type_id integer REFERENCES centric_types,
    number_of_atoms_in_cell smallint,
    PRIMARY KEY (id, crystal_family_id, centric_type_id, number_of_atoms_in_cell)
);

CREATE TABLE IF NOT EXISTS crystals (
    id integer PRIMARY KEY,
    length real NOT NULL,
    width real NOT NULL,
    height real NOT NULL,
    chemical_formula character varying(64) NOT NULL,
    crystal_structure_id integer NOT NULL REFERENCES crystal_structures,
    is_synthetic boolean,
    cave_id integer REFERENCES caves,
    CHECK ((is_synthetic AND cave_id IS NULL) OR
           (NOT is_synthetic AND cave_id IS NOT NULL))
);

CREATE TABLE IF NOT EXISTS figures (
    id integer PRIMARY KEY,
    description character varying(500) NOT NULL,
    crystal_id integer REFERENCES crystals,
    is_changeable boolean NOT NULL,
    change_angle real NULL,
    changeable_to_figure_id integer REFERENCES figures,
    CHECK ((is_changeable AND change_angle IS NOT NULL AND changeable_to_figure_id IS NOT NULL) OR
           (NOT is_changeable AND change_angle IS NULL AND changeable_to_figure_id IS NULL))
);

CREATE TABLE IF NOT EXISTS geometric_shapes (
    id integer PRIMARY KEY,
    name character varying(64) NOT NULL,
    color character varying(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS figures_shapes (
    figure_id integer REFERENCES figures,
    shape_id integer REFERENCES geometric_shapes,
    PRIMARY KEY (figure_id, shape_id)
);

INSERT INTO caves VALUES (1, 32.055700, 104.174100, 'США', 'Лечугилья'),
                         (2, 27.510300, 105.294700, 'Мексика', 'Пещера Кристаллов'),
                         (3, 43.155300, 40.430600, 'Грузия', 'Снежная');

INSERT into crystal_families VALUES (1, 'a', 'Триклинный'),
                                    (2, 'm', 'Моноклинный'),
                                    (3, 'o', 'Орторомбический'),
                                    (4, 't', 'Тетрагональный'),
                                    (5, 'h', 'Гексагональный '),
                                    (6, 'c', 'Кубический');

INSERT INTO centric_types VALUES (1, 'P', 'Примитивная', 1),
                                 (2, 'C', 'Базоцентрированная', 2),
                                 (3, 'I', 'Объёмноцентрированная', 2),
                                 (4, 'R', 'Ромбоэдрическая', 3),
                                 (5, 'F', 'Гранецентрированная', 4);

INSERT INTO crystal_structures VALUES (1, 6, 5, 8);

INSERT INTO crystals VALUES (1, 4.89, 5.789, 3.98725, 'С', 1, false, 2),
                            (2, 87.6536, 532, 5.145, 'NaCl', 1, true, NULL);

INSERT INTO figures VALUES (1, 'Сетки и переменчивые, пляшущие геометрические фигуры', 1, true, 87, 2),
                           (2, 'Черный диск, опоясанный несколькими концентрическими кругами', 1, true, -116, 1),
                           (3, 'Сплошные параллелограммы, раз несколько секунд сменяющие друг друга', 2, false, NULL, NULL);

INSERT INTO geometric_shapes VALUES (1, 'Квадрат', 'Бирюзовый'),
                                    (2, 'Додекаэдр', 'Пурпурный'),
                                    (3, 'Эллипс', 'Черный'),
                                    (4, 'Окружность', 'Белый'),
                                    (5, 'Параллелограмм', 'Розовый');

INSERT INTO figures_shapes VALUES (1, 1),
                                  (1, 2),
                                  (2, 3),
                                  (2, 4),
                                  (3, 5);