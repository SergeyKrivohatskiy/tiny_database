SELECT * FROM students;

SELECT first_name, last_name, group_name FROM students INNER JOIN student_groups ON students->student_id = student_groups->student_id INNER JOIN groups ON student_groups->group_id = groups->group_id;

SELECT first_name, last_name FROM students WHERE students->student_id >= 0 AND students->student_id < 100;