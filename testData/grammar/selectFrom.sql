SELECT * FROM students;

SELECT name FROM students INNER JOIN groups ON students.id = groups.id;

SELECT name FROM students WHERE (TRUE) OR FALSE AND (NOT FALSE AND (NOT TRUE));