CREATE TABLE legajo.eventos_recorrido_laboral
(
   id_evento bigserial, 
   id_empleado_puesto bigint, 
   id_documento bigint, 
   id_tipo_evento_datos_especificos bigint,
   nro_acto_administrativo bigint, 
   fecha_acto_administrativo timestamp without time zone, 
   encabezado character varying(250), 
   observaciones character varying(1000), 
   activo boolean,
   fecha_alta timestamp without time zone, 
   usu_alta character varying(60), 
   fecha_mod timestamp without time zone, 
   usu_mod character varying(60), 
   CONSTRAINT pk_ud_evento PRIMARY KEY (id_evento), 
   CONSTRAINT fk_id_empleado_puesto FOREIGN KEY (id_empleado_puesto) 
   		REFERENCES general.empleado_puesto (id_empleado_puesto) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT fk_id_documento FOREIGN KEY (id_documento) 
   		REFERENCES general.documentos (id_documento) ON UPDATE NO ACTION ON DELETE NO ACTION,
   CONSTRAINT fk_id_tipo_evento FOREIGN KEY (id_tipo_evento_datos_especificos) 
   		REFERENCES seleccion.datos_especificos (id_datos_especificos) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
)
;
