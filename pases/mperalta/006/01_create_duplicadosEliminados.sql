﻿-- Table: remuneracion.duplicados_eliminados

--DROP TABLE remuneracion.duplicados_eliminados;

CREATE TABLE remuneracion.duplicados_eliminados
(
  id_duplicados_eliminados bigserial NOT NULL,
  anho integer,
  mes integer,
  nivel integer,
  entidad integer,
  oee integer,
  depende integer,
  linea integer,
  documento_identidad character varying(10),
  nombres character varying(50),
  apellidos character varying(50),
  estado character varying(12),
  remuneracion_total integer,
  obj_codigo integer,
  fuente_financiamiento integer,
  categoria character varying(10),
  presupuestado integer,
  devengado integer,
  concepto character varying(200),
  movimiento character varying(2),
  lugar character varying(125),
  cargo character varying(250),
  funcion_cumplida character varying(500),
  carga_horaria character varying,
  discapacidad boolean,
  tipo_discapacidad integer,
  anho_ingreso integer,
  activo boolean NOT NULL,
  usu_alta character varying(50) NOT NULL,
  fecha_alta timestamp without time zone NOT NULL,
  usu_mod character varying(50),
  fecha_mod timestamp without time zone,
  id_configuracion_uo bigint,
  id_persona bigint,
  fecha_inactivacion timestamp without time zone,
  usu_paso_historico character varying(50),
  fecha_paso_historico timestamp without time zone,
  CONSTRAINT id_duplicados_eliminados_pk PRIMARY KEY (id_duplicados_eliminados)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remuneracion.duplicados_eliminados
  OWNER TO opendata;
