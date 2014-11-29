CREATE INDEX names_hash ON students(last_name, first_name) USING HASH;

CREATE UNIQUE INDEX id_btree ON students(student_id ASC) USING BTREE;

CREATE INDEX avg_point_btree ON students(avg_point DESC) USING BTREE;