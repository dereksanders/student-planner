create table term (start_date real NOT NULL UNIQUE, end_date real NOT NULL UNIQUE, name text NOT NULL, grade real, grade_is_automatic integer, color text, primary key (start_date));
insert into term(start_date, end_date, name, grade, grade_is_automatic, color) values(100, 101.0, 'Fall', 0.0, 1, 'ffffff');