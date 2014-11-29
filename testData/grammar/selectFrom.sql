SELECT * FROM students;

SELECT name FROM students INNER JOIN groups ON students->id = groups->id;

SELECT name FROM students WHERE students->id = 0 AND students->id < 100;