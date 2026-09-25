-- DROP SCHEMA talent;

CREATE SCHEMA talent AUTHORIZATION "admin";
-- talent.tb_employee definition

-- Drop table

-- DROP TABLE talent.tb_employee;

CREATE TABLE talent.tb_employee (
	id uuid NOT NULL,
	"name" varchar(150) NOT NULL,
	email varchar(150) NOT NULL,
	"role" varchar(100) NOT NULL,
	seniority varchar(50) NOT NULL,
    "password" varchar(255) NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT tb_employee_email_key UNIQUE (email),
	CONSTRAINT tb_employee_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_employee_email ON talent.tb_employee USING btree (email);

-- Permissions

ALTER TABLE talent.tb_employee OWNER TO "admin";
GRANT ALL ON TABLE talent.tb_employee TO "admin";
GRANT ALL ON TABLE talent.tb_employee TO postgres;


-- talent.tb_project definition

-- Drop table

-- DROP TABLE talent.tb_project;

CREATE TABLE talent.tb_project (
	id uuid NOT NULL,
	"name" varchar(150) NOT NULL,
	description text NULL,
	status varchar(30) NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp NULL,
	CONSTRAINT tb_project_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE talent.tb_project OWNER TO "admin";
GRANT ALL ON TABLE talent.tb_project TO "admin";
GRANT ALL ON TABLE talent.tb_project TO postgres;


-- talent.tb_skill definition

-- Drop table

-- DROP TABLE talent.tb_skill;

CREATE TABLE talent.tb_skill (
	id uuid NOT NULL,
	"name" varchar(100) NOT NULL,
	category varchar(50) NOT NULL,
	CONSTRAINT tb_skill_name_key UNIQUE (name),
	CONSTRAINT tb_skill_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_skill_category ON talent.tb_skill USING btree (category);

-- Permissions

ALTER TABLE talent.tb_skill OWNER TO "admin";
GRANT ALL ON TABLE talent.tb_skill TO "admin";
GRANT ALL ON TABLE talent.tb_skill TO postgres;


-- talent.tb_allocation definition

-- Drop table

-- DROP TABLE talent.tb_allocation;

CREATE TABLE talent.tb_allocation (
	id uuid NOT NULL,
	employee_id uuid NOT NULL,
	project_id uuid NOT NULL,
	allocation_percentage int4 NULL,
	start_date date NOT NULL,
	end_date date NULL,
	CONSTRAINT tb_allocation_allocation_percentage_check CHECK (((allocation_percentage > 0) AND (allocation_percentage <= 100))),
	CONSTRAINT tb_allocation_pkey PRIMARY KEY (id),
	CONSTRAINT fk_allocation_employee FOREIGN KEY (employee_id) REFERENCES talent.tb_employee(id) ON DELETE CASCADE,
	CONSTRAINT fk_allocation_project FOREIGN KEY (project_id) REFERENCES talent.tb_project(id) ON DELETE CASCADE
);
CREATE INDEX idx_allocation_employee ON talent.tb_allocation USING btree (employee_id);
CREATE INDEX idx_allocation_project ON talent.tb_allocation USING btree (project_id);

-- Permissions

ALTER TABLE talent.tb_allocation OWNER TO "admin";
GRANT ALL ON TABLE talent.tb_allocation TO "admin";
GRANT ALL ON TABLE talent.tb_allocation TO postgres;


-- talent.tb_employee_skill definition

-- Drop table

-- DROP TABLE talent.tb_employee_skill;

CREATE TABLE talent.tb_employee_skill (
	employee_id uuid NOT NULL,
	skill_id uuid NOT NULL,
	proficiency_level varchar(30) NOT NULL,
	CONSTRAINT tb_employee_skill_pkey PRIMARY KEY (employee_id, skill_id),
	CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES talent.tb_employee(id) ON DELETE CASCADE,
	CONSTRAINT fk_skill FOREIGN KEY (skill_id) REFERENCES talent.tb_skill(id) ON DELETE CASCADE
);

-- Permissions

ALTER TABLE talent.tb_employee_skill OWNER TO "admin";
GRANT ALL ON TABLE talent.tb_employee_skill TO "admin";
GRANT ALL ON TABLE talent.tb_employee_skill TO postgres;




-- Permissions

GRANT ALL ON SCHEMA talent TO "admin";
GRANT ALL ON SCHEMA talent TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE "admin" IN SCHEMA talent GRANT REFERENCES, INSERT, TRUNCATE, DELETE, SELECT, TRIGGER, UPDATE, MAINTAIN ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE "admin" IN SCHEMA talent GRANT SELECT, UPDATE, USAGE ON SEQUENCES TO postgres;