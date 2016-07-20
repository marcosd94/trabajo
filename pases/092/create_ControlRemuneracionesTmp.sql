-- Table: remuneracion.control_remuneraciones_tmp

CREATE TABLE remuneracion.control_remuneraciones_tmp
(
  id_control_remuneracion_tmp bigserial NOT NULL,
  anho integer,
  mes integer,
  id_configuracion_uo bigint,
  cant_lineas integer,
  ultima_linea_procesada integer,
  fecha_update timestamp without time zone,
  estado_proceso character varying(10),
  insercion_exitosa boolean,
  usuario character varying(50),
  fecha_inicio timestamp without time zone,
  fecha_fin timestamp without time zone,
  
  CONSTRAINT control_remuneraciones_tmp_pk PRIMARY KEY (id_control_remuneracion_tmp)
  --CONSTRAINT empleado_puesto_remuneraciones_fk FOREIGN KEY (id_empleado_puesto)
      --REFERENCES general.empleado_puesto (id_empleado_puesto) MATCH SIMPLE
      --ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remuneracion.control_remuneraciones_tmp
  OWNER TO postgres;
