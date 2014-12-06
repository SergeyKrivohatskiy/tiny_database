CREATE TABLE students (
  student_id INTEGER,
  first_name VARCHAR(10),
  last_name VARCHAR (15),
  avg_point DOUBLE
);

CREATE TABLE groups (
  group_id INTEGER,
  group_name VARCHAR(3)
);

CREATE TABLE student_groups (
  student_id INTEGER,
  group_id INTEGER
);

INSERT INTO students(student_id, avg_point, first_name, last_name) VALUES (3, 4.8, 'Andrew', 'Kozlov');

INSERT INTO students(student_id, avg_point, first_name, last_name) VALUES (5, 0.0, 'Kirill', 'Kupriy');

INSERT INTO groups(group_id, group_name) VALUES (1, '5SE');

INSERT INTO student_groups(student_id, group_id) VALUES (3, 1);

INSERT INTO student_groups(student_id, group_id) VALUES (5, 1);

SELECT * FROM students;

SELECT first_name, last_name, group_name FROM students INNER JOIN student_groups ON students->student_id = student_groups->student_id INNER JOIN groups ON student_groups->group_id = groups->group_id;

DELETE FROM students WHERE students->avg_point < 1.0;

SELECT first_name, last_name FROM students WHERE students->student_id >= 0 AND students->student_id < 100;
