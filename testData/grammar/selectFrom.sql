SELECT * FROM students;

SELECT name FROM students INNER JOIN groups ON students.id = groups.id;

SELECT name FROM students WHERE studens.id < 5 AND students.id > 0;