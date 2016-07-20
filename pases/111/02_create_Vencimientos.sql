-- Table: remuneracion.vencimientos

-- DROP TABLE remuneracion.vencimientos;

CREATE TABLE remuneracion.vencimientos
(
  id_vencimiento bigserial NOT NULL,
  anho integer,
  mes integer,
  vencimiento timestamp without time zone NOT NULL,
  prorroga timestamp without time zone,
  activo boolean NOT NULL,
  usu_alta character varying(50) NOT NULL,
  fecha_alta timestamp without time zone NOT NULL,
  usu_mod character varying(50),
  fecha_mod timestamp without time zone,
  
  CONSTRAINT vencimientos_pk PRIMARY KEY (id_vencimiento)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remuneracion.vencimientos
  OWNER TO postgres;
