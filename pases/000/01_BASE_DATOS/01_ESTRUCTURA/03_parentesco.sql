-- Table: general.parentesco

-- DROP TABLE general.parentesco;

CREATE TABLE general.parentesco
(
  id_parentesco bigserial NOT NULL,
  documento_identidad character varying(30) NOT NULL,
  nombres character varying(100),
  apellidos character varying(80) NOT NULL,
  institucion character varying(100) NOT NULL,
  id_persona bigint,
  activo boolean NOT NULL,
  id_datos_especificos bigint,
  CONSTRAINT parentesco_pk PRIMARY KEY (id_parentesco),
  CONSTRAINT parentesco_datos_especificos_fk FOREIGN KEY (id_datos_especificos)
      REFERENCES seleccion.datos_especificos (id_datos_especificos) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT persona_tbl_parentesco_fk FOREIGN KEY (id_persona)
      REFERENCES general.persona (id_persona) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);