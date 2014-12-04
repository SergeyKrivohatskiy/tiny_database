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