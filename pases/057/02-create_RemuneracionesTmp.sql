-- Table: remuneracion.remuneraciones_tmp

-- DROP TABLE remuneracion.remuneraciones_tmp;

CREATE TABLE remuneracion.remuneraciones_tmp
(
  id_remuneracion_tmp bigserial NOT NULL,
  --id_empleado_puesto bigint NOT NULL,
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
  cargo character varying(50),
  funcion_cumplida character varying(200),
  carga_horaria character varying(150),
  discapacidad boolean,
  tipo_discapacidad integer,
  anho_ingreso integer,
  activo boolean NOT NULL,
  usu_alta character varying(50) NOT NULL,
  fecha_alta timestamp without time zone NOT NULL,
  usu_mod character varying(50),
  fecha_mod timestamp without time zone,
  
  CONSTRAINT remuneraciones_tmp_pk PRIMARY KEY (id_remuneracion_tmp)
  --CONSTRAINT empleado_puesto_remuneraciones_fk FOREIGN KEY (id_empleado_puesto)
      --REFERENCES general.empleado_puesto (id_empleado_puesto) MATCH SIMPLE
      --ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remuneracion.remuneraciones_tmp
  OWNER TO postgres;
