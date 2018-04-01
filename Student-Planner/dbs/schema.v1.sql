create table term (start_date real NOT NULL UNIQUE, end_date real NOT NULL UNIQUE, name text NOT NULL, grade real, grade_is_automatic integer, color text, PRIMARY KEY (start_date));
create table meeting_set (id integer NOT NULL, term_start_date real NOT NULL, start_time real NOT NULL, end_time real NOT NULL, course_start_term_start_date real, course_end_term_start_date real, course_dept_id text, course_code integer, name text, meeting_type text, location text, is_course_meeting_set integer, color text, FOREIGN KEY (term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (course_start_term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (course_end_term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (course_dept_id) references course(dept_id) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (course_code) references course(code) ON UPDATE CASCADE ON DELETE CASCADE, PRIMARY KEY (id, term_start_date, start_time));
create table meeting_date (set_id integer NOT NULL, date_of real NOT NULL, FOREIGN KEY (set_id) references meeting_set(id) ON UPDATE CASCADE ON DELETE CASCADE, PRIMARY KEY (set_id, date_of));
create table calendar_event (term_start_date real NOT NULL, name text NOT NULL, start_date_time real NOT NULL, end_date_time real NOT NULL, color text, FOREIGN KEY (term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, PRIMARY KEY (term_start_date, name, start_date_time));
create table gpa_scale_conversion (id integer NOT NULL, percentage_start integer NOT NULL, percentage_end integer NOT NULL, grade_point real NOT NULL, PRIMARY KEY (id, percentage_start));
create table letter_scale_conversion (id integer NOT NULL, percentage_start integer NOT NULL, percentage_end integer NOT NULL, letter text NOT NULL, PRIMARY KEY (id, percentage_start));
create table course (start_term_start_date real NOT NULL, end_term_start_date real NOT NULL, dept_id text NOT NULL, code integer NOT NULL, name text, grade real, grade_is_automatic integer, color text, FOREIGN KEY (start_term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (end_term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, PRIMARY KEY (start_term_start_date, end_term_start_date, dept_id, code));
create table course_event (course_start_term_start_date real NOT NULL, course_end_term_start_date real NOT NULL, course_dept_id text NOT NULL, course_code integer NOT NULL, event_term_start_date real NOT NULL, event_name text NOT NULL, event_start_date_time real NOT NULL, grade real, weight real, state text NOT NULL CHECK (state IN ('not_started', 'in-progress', 'submitted', 'graded')), FOREIGN KEY (course_start_term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (course_end_term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (course_dept_id) references course(dept_id) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (course_code) references course(code) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (event_term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (event_name) references event(name) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (event_start_date_time) references event(start_date_time) ON UPDATE CASCADE ON DELETE CASCADE, PRIMARY KEY (course_start_term_start_date, course_end_term_start_date , course_dept_id, course_code, event_term_start_date, event_name, event_start_date_time));
create table term_scale (term_start_date real NOT NULL, scale_id integer NOT NULL, FOREIGN KEY (term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (scale_id) references gpa_scale_conversion(id) ON UPDATE CASCADE ON DELETE CASCADE, PRIMARY KEY (term_start_date));
create table course_scale (course_start_term_start_date real NOT NULL, course_end_term_start_date real NOT NULL, course_dept_id text NOT NULL, course_code integer NOT NULL, scale_id integer NOT NULL, FOREIGN KEY (course_start_term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (course_end_term_start_date) references term(start_date) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (course_dept_id) references course(dept_id) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (course_code) references course(code) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (scale_id) references letter_scale_conversion(id) ON UPDATE CASCADE ON DELETE CASCADE, PRIMARY KEY (course_start_term_start_date, course_end_term_start_date, course_dept_id, course_code));