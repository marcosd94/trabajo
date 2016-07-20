-- Table: general.parentesco

-- DROP TABLE general.parentesco;

CREATE TABLE general.parentesco
(
  id_parentesco bigserial NOT NULL,
  documendo_identidad character varying(30),
  nombres character varying(100),
  apellidos character varying(80),
  institucion character varying(100),
  parentesco character varying(30),
  id_persona bigint,
  CONSTRAINT parentesco_pk PRIMARY KEY (id_parentesco),
  CONSTRAINT persona_tbl_parentesco_fk FOREIGN KEY (id_persona)
      REFERENCES general.persona (id_persona) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)