DROP TABLE IF EXISTS tasks;

CREATE TABLE tasks (
    id serial NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    explanation VARCHAR(255) NOT NULL UNIQUE
);