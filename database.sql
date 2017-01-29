--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.10
-- Dumped by pg_dump version 9.4.10
-- Started on 2017-01-30 00:31:57 EET

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

DROP DATABASE postgres;
--
-- TOC entry 2121 (class 1262 OID 12177)
-- Name: postgres; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE postgres OWNER TO postgres;

\connect postgres

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 2122 (class 1262 OID 12177)
-- Dependencies: 2121
-- Name: postgres; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON DATABASE postgres IS 'default administrative connection database';


--
-- TOC entry 1 (class 3079 OID 11897)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2125 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 173 (class 1259 OID 16384)
-- Name: department; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE department (
    id_department bigint NOT NULL,
    name character(50)
);


ALTER TABLE department OWNER TO postgres;

--
-- TOC entry 174 (class 1259 OID 16387)
-- Name: employee; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE employee (
    employee_id bigint NOT NULL,
    name character varying(25) NOT NULL,
    surname character varying(25) NOT NULL,
    role_id bigint NOT NULL,
    password character varying(100) DEFAULT NULL::bpchar NOT NULL,
    position_id bigint,
    qualification_id bigint,
    department_id bigint,
    column_10 bigint,
    email character varying(50)
);


ALTER TABLE employee OWNER TO postgres;

--
-- TOC entry 175 (class 1259 OID 16391)
-- Name: position; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "position" (
    position_id bigint NOT NULL,
    "position" character(25)
);


ALTER TABLE "position" OWNER TO postgres;

--
-- TOC entry 176 (class 1259 OID 16394)
-- Name: project; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE project (
    project_id bigint NOT NULL,
    name character(50) NOT NULL,
    start_date timestamp without time zone NOT NULL,
    completion_date timestamp without time zone,
    predicated_completion_date timestamp without time zone,
    customer_id bigint NOT NULL,
    manager_id bigint NOT NULL,
    CONSTRAINT date_interval CHECK ((start_date < completion_date))
);


ALTER TABLE project OWNER TO postgres;

--
-- TOC entry 177 (class 1259 OID 16397)
-- Name: qualification; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE qualification (
    qualification_id bigint NOT NULL,
    qualification character(25)
);


ALTER TABLE qualification OWNER TO postgres;

--
-- TOC entry 178 (class 1259 OID 16400)
-- Name: role; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE role (
    role_id bigint NOT NULL,
    name character(25)
);


ALTER TABLE role OWNER TO postgres;

--
-- TOC entry 182 (class 1259 OID 16415)
-- Name: seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq
    START WITH 400
    INCREMENT BY 1
    MINVALUE 5
    MAXVALUE 999999999999999999
    CACHE 20;


ALTER TABLE seq OWNER TO postgres;

--
-- TOC entry 179 (class 1259 OID 16403)
-- Name: sprint; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE sprint (
    sprint_id bigint NOT NULL,
    name character(50) NOT NULL,
    start_date timestamp without time zone NOT NULL,
    project_id bigint NOT NULL,
    previous_sprint_id bigint,
    completion_date timestamp without time zone,
    CONSTRAINT date_interval CHECK ((start_date < completion_date))
);


ALTER TABLE sprint OWNER TO postgres;

--
-- TOC entry 180 (class 1259 OID 16406)
-- Name: task; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE task (
    task_id bigint NOT NULL,
    name character(50) NOT NULL,
    start_date timestamp without time zone NOT NULL,
    estimate bigint NOT NULL,
    sprint_id bigint NOT NULL,
    subtask_id bigint,
    previous_task_id bigint,
    description character(500),
    completion_date timestamp without time zone,
    predicted_delay bigint,
    actual_completion_date timestamp without time zone,
    actual_start_date timestamp without time zone,
    CONSTRAINT date_interval CHECK ((start_date < completion_date))
);


ALTER TABLE task OWNER TO postgres;

--
-- TOC entry 181 (class 1259 OID 16412)
-- Name: task_employee; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE task_employee (
    id_task_employee bigint NOT NULL,
    task_id bigint NOT NULL,
    employee_id bigint NOT NULL,
    confirm bigint,
    load bigint
);


ALTER TABLE task_employee OWNER TO postgres;

--
-- TOC entry 2107 (class 0 OID 16384)
-- Dependencies: 173
-- Data for Name: department; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO department (id_department, name) VALUES (300, '100                                               ');
INSERT INTO department (id_department, name) VALUES (301, '301                                               ');


--
-- TOC entry 2108 (class 0 OID 16387)
-- Dependencies: 174
-- Data for Name: employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO employee (employee_id, name, surname, role_id, password, position_id, qualification_id, department_id, column_10, email) VALUES (1, 'Ivanv', 'Ivanov', 2, '$2a$11$RGYm3k15Z/mXj2KbGqqGWOQwnECxAnuGIyXTmAr12SyMp4y8M2FJG', NULL, NULL, 300, NULL, 'manager@mail.com');
INSERT INTO employee (employee_id, name, surname, role_id, password, position_id, qualification_id, department_id, column_10, email) VALUES (2, 'Admin', 'Smith', 1, '$2a$11$RGYm3k15Z/mXj2KbGqqGWOQwnECxAnuGIyXTmAr12SyMp4y8M2FJG', 1, 2, NULL, NULL, 'admin@mail.com');
INSERT INTO employee (employee_id, name, surname, role_id, password, position_id, qualification_id, department_id, column_10, email) VALUES (3, 'John', 'Carter', 2, '$2a$11$RGYm3k15Z/mXj2KbGqqGWOQwnECxAnuGIyXTmAr12SyMp4y8M2FJG', NULL, NULL, 300, NULL, 'manager2.mail.com');
INSERT INTO employee (employee_id, name, surname, role_id, password, position_id, qualification_id, department_id, column_10, email) VALUES (900, 'Stan', 'Smith', 3, '$2a$11$VkI6MX7hq5vm1P7PE9XBb.2PSoB8wgrz4ezfWOs3HH9/2uyxN6jfe', 1, 2, NULL, NULL, 'employee@mail.com');
INSERT INTO employee (employee_id, name, surname, role_id, password, position_id, qualification_id, department_id, column_10, email) VALUES (0, 'Peter', 'Griffin', 3, '$2a$11$RGYm3k15Z/mXj2KbGqqGWOQwnECxAnuGIyXTmAr12SyMp4y8M2FJG', 2, 2, NULL, NULL, 'peter@gmail.com');
INSERT INTO employee (employee_id, name, surname, role_id, password, position_id, qualification_id, department_id, column_10, email) VALUES (4, 'Peter', 'Glenn', 4, '$2a$11$RGYm3k15Z/mXj2KbGqqGWOQwnECxAnuGIyXTmAr12SyMp4y8M2FJG', NULL, NULL, 300, NULL, 'customer@mail.com');


--
-- TOC entry 2109 (class 0 OID 16391)
-- Dependencies: 175
-- Data for Name: position; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "position" (position_id, "position") VALUES (1, 'Java Developer           ');
INSERT INTO "position" (position_id, "position") VALUES (2, 'Web Developer            ');


--
-- TOC entry 2110 (class 0 OID 16394)
-- Dependencies: 176
-- Data for Name: project; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO project (project_id, name, start_date, completion_date, predicated_completion_date, customer_id, manager_id) VALUES (1360, 'IssueTracker                                      ', '2017-01-01 02:00:00', '2017-01-31 02:00:00', NULL, 4, 1);


--
-- TOC entry 2111 (class 0 OID 16397)
-- Dependencies: 177
-- Data for Name: qualification; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO qualification (qualification_id, qualification) VALUES (1, 'Junior                   ');
INSERT INTO qualification (qualification_id, qualification) VALUES (2, 'Regular                  ');
INSERT INTO qualification (qualification_id, qualification) VALUES (3, 'Senior                   ');


--
-- TOC entry 2112 (class 0 OID 16400)
-- Dependencies: 178
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO role (role_id, name) VALUES (1, 'ROLE_ADMIN               ');
INSERT INTO role (role_id, name) VALUES (2, 'ROLE_MANAGER             ');
INSERT INTO role (role_id, name) VALUES (3, 'ROLE_EMPLOYEE            ');
INSERT INTO role (role_id, name) VALUES (4, 'ROLE_CUSTOMER            ');


--
-- TOC entry 2126 (class 0 OID 0)
-- Dependencies: 182
-- Name: seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq', 2259, true);


--
-- TOC entry 2113 (class 0 OID 16403)
-- Dependencies: 179
-- Data for Name: sprint; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO sprint (sprint_id, name, start_date, project_id, previous_sprint_id, completion_date) VALUES (1380, 'Create Product Requirements Document              ', '2017-01-02 02:00:00', 1360, NULL, '2017-01-08 02:00:00');
INSERT INTO sprint (sprint_id, name, start_date, project_id, previous_sprint_id, completion_date) VALUES (1500, 'Implement function                                ', '2017-01-09 02:00:00', 1360, 1380, '2017-01-25 02:00:00');


--
-- TOC entry 2114 (class 0 OID 16406)
-- Dependencies: 180
-- Data for Name: task; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO task (task_id, name, start_date, estimate, sprint_id, subtask_id, previous_task_id, description, completion_date, predicted_delay, actual_completion_date, actual_start_date) VALUES (1660, 'Create service level                              ', '2017-01-10 14:00:00', 80, 1500, NULL, 1560, 'Create service level                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ', '2017-01-24 14:00:00', 0, '2017-01-24 14:00:00', '2017-01-10 14:00:00');
INSERT INTO task (task_id, name, start_date, estimate, sprint_id, subtask_id, previous_task_id, description, completion_date, predicted_delay, actual_completion_date, actual_start_date) VALUES (1400, 'Requirements gathering                            ', '2017-01-02 08:00:00', 5, 1380, NULL, NULL, 'Purpose, scope, and objectives. Assumptions and constraints.                                                                                                                                                                                                                                                                                                                                                                                                                                                        ', '2017-01-02 13:00:00', 0, '2017-01-02 15:00:00', NULL);
INSERT INTO task (task_id, name, start_date, estimate, sprint_id, subtask_id, previous_task_id, description, completion_date, predicted_delay, actual_completion_date, actual_start_date) VALUES (1520, 'Create database                                   ', '2017-01-06 08:00:00', 20, 1500, NULL, NULL, 'Create ER-diagram and sql script for creation database.                                                                                                                                                                                                                                                                                                                                                                                                                                                             ', '2017-01-09 12:00:00', 0, '2017-01-09 12:00:00', NULL);
INSERT INTO task (task_id, name, start_date, estimate, sprint_id, subtask_id, previous_task_id, description, completion_date, predicted_delay, actual_completion_date, actual_start_date) VALUES (1440, 'Create plan                                       ', '2017-01-02 13:00:00', 4, 1380, NULL, 1400, 'Create plan.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        ', '2017-01-03 09:00:00', 0, '2017-01-03 09:00:00', '2017-01-02 15:00:00');
INSERT INTO task (task_id, name, start_date, estimate, sprint_id, subtask_id, previous_task_id, description, completion_date, predicted_delay, actual_completion_date, actual_start_date) VALUES (1540, 'Create views                                      ', '2017-01-09 08:00:00', 40, 1500, NULL, NULL, 'Create views.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ', '2017-01-16 08:00:00', 0, '2017-01-16 08:00:00', NULL);
INSERT INTO task (task_id, name, start_date, estimate, sprint_id, subtask_id, previous_task_id, description, completion_date, predicted_delay, actual_completion_date, actual_start_date) VALUES (1720, 'Test project                                      ', '2017-01-24 14:00:00', 8, 1500, NULL, 1660, 'Test project                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        ', '2017-01-25 14:00:00', 0, '2017-01-29 14:00:00', '2017-01-24 14:00:00');
INSERT INTO task (task_id, name, start_date, estimate, sprint_id, subtask_id, previous_task_id, description, completion_date, predicted_delay, actual_completion_date, actual_start_date) VALUES (1460, 'Manage Changing Requirements                      ', '2017-01-03 09:00:00', 20, 1380, NULL, 1440, 'Update the plan with the customer.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  ', '2017-01-05 13:00:00', 0, '2017-01-05 13:00:00', '2017-01-05 13:00:00');
INSERT INTO task (task_id, name, start_date, estimate, sprint_id, subtask_id, previous_task_id, description, completion_date, predicted_delay, actual_completion_date, actual_start_date) VALUES (1560, 'Create dao                                        ', '2017-01-09 12:00:00', 10, 1500, NULL, 1520, 'Create dao level.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   ', '2017-01-10 14:00:00', 0, '2017-01-10 14:00:00', '2017-01-09 12:00:00');
INSERT INTO task (task_id, name, start_date, estimate, sprint_id, subtask_id, previous_task_id, description, completion_date, predicted_delay, actual_completion_date, actual_start_date) VALUES (1480, 'Define Roles and Responsibilities                 ', '2017-01-05 13:00:00', 5, 1380, NULL, 1460, 'Define Roles and Responsibilities                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   ', '2017-01-08 10:00:00', 0, '2017-01-08 10:00:00', '2017-01-08 10:00:00');
INSERT INTO task (task_id, name, start_date, estimate, sprint_id, subtask_id, previous_task_id, description, completion_date, predicted_delay, actual_completion_date, actual_start_date) VALUES (1580, 'Test views                                        ', '2017-01-16 08:00:00', 8, 1500, NULL, 1540, 'Customer test views.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ', '2017-01-17 08:00:00', 0, '2017-01-17 08:00:00', '2017-01-16 08:00:00');


--
-- TOC entry 2115 (class 0 OID 16412)
-- Dependencies: 181
-- Data for Name: task_employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2240, 1540, 0, 0, 100);
INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2241, 1540, 900, 0, 20);
INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2040, 1400, 900, 0, 100);
INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2060, 1440, 1, 0, 100);
INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2080, 1460, 1, 0, 100);
INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2100, 1480, 1, 0, 100);
INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2140, 1560, 900, 0, 100);
INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2160, 1660, 900, 0, 100);
INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2180, 1720, 0, 0, 100);
INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2181, 1720, 1, 0, 100);
INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2182, 1720, 4, 0, 100);
INSERT INTO task_employee (id_task_employee, task_id, employee_id, confirm, load) VALUES (2183, 1720, 900, 0, 100);


--
-- TOC entry 1959 (class 2606 OID 16431)
-- Name: department_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY department
    ADD CONSTRAINT department_pkey PRIMARY KEY (id_department);


--
-- TOC entry 1962 (class 2606 OID 16439)
-- Name: employee_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY employee
    ADD CONSTRAINT employee_pk PRIMARY KEY (employee_id);


--
-- TOC entry 1965 (class 2606 OID 16443)
-- Name: position_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "position"
    ADD CONSTRAINT position_pk PRIMARY KEY (position_id);


--
-- TOC entry 1968 (class 2606 OID 16441)
-- Name: project_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_pk PRIMARY KEY (project_id);


--
-- TOC entry 1971 (class 2606 OID 16427)
-- Name: qualification_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY qualification
    ADD CONSTRAINT qualification_pk PRIMARY KEY (qualification_id);


--
-- TOC entry 1974 (class 2606 OID 16437)
-- Name: role_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pk PRIMARY KEY (role_id);


--
-- TOC entry 1977 (class 2606 OID 16429)
-- Name: sprint_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sprint
    ADD CONSTRAINT sprint_pk PRIMARY KEY (sprint_id);


--
-- TOC entry 1983 (class 2606 OID 16433)
-- Name: task_employee_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY task_employee
    ADD CONSTRAINT task_employee_pk PRIMARY KEY (id_task_employee);


--
-- TOC entry 1980 (class 2606 OID 16435)
-- Name: task_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY task
    ADD CONSTRAINT task_pk PRIMARY KEY (task_id);


--
-- TOC entry 1960 (class 1259 OID 16586)
-- Name: employee_email_uindex_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX employee_email_uindex_index ON employee USING btree (email);


--
-- TOC entry 1963 (class 1259 OID 16417)
-- Name: employee_pk_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX employee_pk_index ON employee USING btree (employee_id);


--
-- TOC entry 1966 (class 1259 OID 16418)
-- Name: position_pk_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX position_pk_index ON "position" USING btree (position_id);


--
-- TOC entry 1969 (class 1259 OID 16419)
-- Name: project_pk_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX project_pk_index ON project USING btree (project_id);


--
-- TOC entry 1972 (class 1259 OID 16422)
-- Name: qualification_pk_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX qualification_pk_index ON qualification USING btree (qualification_id);


--
-- TOC entry 1975 (class 1259 OID 16423)
-- Name: role_pk_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX role_pk_index ON role USING btree (role_id);


--
-- TOC entry 1978 (class 1259 OID 16424)
-- Name: sprint_pk_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX sprint_pk_index ON sprint USING btree (sprint_id);


--
-- TOC entry 1984 (class 1259 OID 16421)
-- Name: task_employee_pk_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX task_employee_pk_index ON task_employee USING btree (id_task_employee);


--
-- TOC entry 1981 (class 1259 OID 16425)
-- Name: task_pk_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX task_pk_index ON task USING btree (task_id);


--
-- TOC entry 1989 (class 2606 OID 16464)
-- Name: customer_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project
    ADD CONSTRAINT customer_fk FOREIGN KEY (customer_id) REFERENCES employee(employee_id);


--
-- TOC entry 1985 (class 2606 OID 16444)
-- Name: department_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY employee
    ADD CONSTRAINT department_fk FOREIGN KEY (department_id) REFERENCES department(id_department);


--
-- TOC entry 1986 (class 2606 OID 16449)
-- Name: employee_role_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY employee
    ADD CONSTRAINT employee_role_fk FOREIGN KEY (role_id) REFERENCES role(role_id);


--
-- TOC entry 1990 (class 2606 OID 16469)
-- Name: manager_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project
    ADD CONSTRAINT manager_fk FOREIGN KEY (manager_id) REFERENCES employee(employee_id);


--
-- TOC entry 1987 (class 2606 OID 16454)
-- Name: position_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY employee
    ADD CONSTRAINT position_id_fk FOREIGN KEY (position_id) REFERENCES "position"(position_id);


--
-- TOC entry 1991 (class 2606 OID 16474)
-- Name: previous_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sprint
    ADD CONSTRAINT previous_fk FOREIGN KEY (previous_sprint_id) REFERENCES sprint(sprint_id);


--
-- TOC entry 1993 (class 2606 OID 16484)
-- Name: previous_task_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY task
    ADD CONSTRAINT previous_task_fk FOREIGN KEY (previous_task_id) REFERENCES task(task_id);


--
-- TOC entry 1988 (class 2606 OID 16459)
-- Name: qualification_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY employee
    ADD CONSTRAINT qualification_id_fk FOREIGN KEY (qualification_id) REFERENCES qualification(qualification_id);


--
-- TOC entry 1995 (class 2606 OID 16601)
-- Name: sprint_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY task
    ADD CONSTRAINT sprint_fk FOREIGN KEY (sprint_id) REFERENCES sprint(sprint_id) ON DELETE CASCADE;


--
-- TOC entry 1992 (class 2606 OID 16596)
-- Name: sprint_project_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sprint
    ADD CONSTRAINT sprint_project_fk FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE;


--
-- TOC entry 1994 (class 2606 OID 16494)
-- Name: subtask_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY task
    ADD CONSTRAINT subtask_fk FOREIGN KEY (subtask_id) REFERENCES task(task_id);


--
-- TOC entry 1996 (class 2606 OID 16499)
-- Name: task_employee_employee_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY task_employee
    ADD CONSTRAINT task_employee_employee_fk FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE;


--
-- TOC entry 1997 (class 2606 OID 16504)
-- Name: task_employee_task_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY task_employee
    ADD CONSTRAINT task_employee_task_fk FOREIGN KEY (task_id) REFERENCES task(task_id) ON DELETE CASCADE;


--
-- TOC entry 2124 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2017-01-30 00:31:57 EET

--
-- PostgreSQL database dump complete
--

