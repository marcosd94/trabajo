-- Table: remuneracion.procesamiento

-- DROP TABLE remuneracion.procesamiento;

CREATE TABLE remuneracion.procesamiento
(
  id_procesamiento bigserial NOT NULL,
  anho integer,
  mes integer,
  id_configuracion_uo bigint,
  min_fecha_alta timestamp without time zone NOT NULL,
  cant_proc int,
  activo boolean NOT NULL,
  usu_alta character varying(50) NOT NULL,
  fecha_alta timestamp without time zone NOT NULL,
  usu_mod character varying(50),
  fecha_mod timestamp without time zone,
  CONSTRAINT procesamiento_pk PRIMARY KEY (id_procesamiento)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remuneracion.procesamiento
  OWNER TO postgres;
