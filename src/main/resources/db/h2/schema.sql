DROP TABLE priority_types IF EXISTS;
DROP TABLE stakeholder_objectives IF EXISTS;
DROP TABLE specification_objectives IF EXISTS;
DROP TABLE stakeholders IF EXISTS;
DROP TABLE specifications IF EXISTS;
DROP TABLE specification_templates IF EXISTS;
DROP TABLE stakeholder_templates IF EXISTS;
DROP TABLE objective_templates IF EXISTS;


CREATE TABLE priority_types (
  id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(20),
  sequence INTEGER
);
CREATE INDEX priorityTypes_name ON priority_types (name);

CREATE TABLE specifications (
  id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name       VARCHAR(40),
  description    VARCHAR(80),
  notes       VARCHAR(255),
  priority_id  INTEGER
);
CREATE INDEX specifications_name ON specifications (name);

CREATE TABLE specification_templates (
  id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name       VARCHAR(40),
  description    VARCHAR(80),
  notes       VARCHAR(255),
  priority_id  INTEGER
);
CREATE INDEX specification_templates_name ON specification_templates (name);
ALTER TABLE specification_templates ADD CONSTRAINT spec_template_unique unique (name);

CREATE TABLE stakeholders (
  id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  specification_id    INTEGER NOT NULL,
  name       VARCHAR(40),
  description VARCHAR(80),
  notes varchar(255),
  priority_id  INTEGER
);
ALTER TABLE stakeholders ADD CONSTRAINT fk_stake_spec FOREIGN KEY (specification_id) REFERENCES specifications (id);
CREATE INDEX stakeholders_name ON stakeholders (specification_id, name);

CREATE TABLE stakeholder_templates (
    id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name       VARCHAR(40),
    description    VARCHAR(80),
    notes       VARCHAR(255),
    priority_id  INTEGER
);
CREATE INDEX stakeholder_templates_name ON stakeholder_templates (name);
ALTER TABLE stakeholder_templates ADD CONSTRAINT stake_template_unique unique (name);

CREATE TABLE specification_objectives (
  id          INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  specification_id      INTEGER,
  name varchar(40),
  description VARCHAR(80),
  notes VARCHAR(255),
  priority_id  INTEGER
);
ALTER TABLE specification_objectives ADD CONSTRAINT fk_specification_objective_spec FOREIGN KEY (specification_id) REFERENCES specifications (id);
CREATE INDEX specification_objective_spec_id ON specification_objectives (specification_id);

CREATE TABLE objective_templates (
  id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name       VARCHAR(40),
  description    VARCHAR(80),
  notes       VARCHAR(255),
  priority_id  INTEGER
);
CREATE INDEX objective_templates_name ON objective_templates (name);
ALTER TABLE objective_templates ADD CONSTRAINT obj_template_unique unique (name);

CREATE TABLE stakeholder_objectives (
  id          INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  stakeholder_id      INTEGER,
  specification_objective_id      INTEGER,
  notes VARCHAR(255),
  priority_id  INTEGER
);
ALTER TABLE stakeholder_objectives ADD CONSTRAINT fk_stakeholder_objective_stake FOREIGN KEY (stakeholder_id) REFERENCES stakeholders (id);
ALTER TABLE stakeholder_objectives ADD CONSTRAINT fk_stakeholder_objective_spec_obj FOREIGN KEY (specification_objective_id) REFERENCES specification_objectives (id);
CREATE INDEX stakeholder_objectives_stake_id_spec_obj_id ON stakeholder_objectives (stakeholder_id, specification_objective_id);
