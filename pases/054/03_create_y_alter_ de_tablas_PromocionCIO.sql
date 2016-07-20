CREATE TABLE seleccion.promocion_salarial
(
   id_promocion_salarial serial, 
   id_cpt bigint, 
   categoria character varying(60), 
   monto bigint, 
   obj_codigo bigint, 
   anho bigint, 
   disponible boolean, 
   orden integer, 
   id_estado_cab bigint, 
   descripcion character varying(200), 
   id_configuracion_uo_det bigint, 
   usu_alta character varying(50), 
   fecha_alta timestamp without time zone, 
   usu_mod character varying(50), 
   fecha_mod timestamp without time zone, 
   activo boolean
) 
WITH (
  OIDS = FALSE
)
;

ALTER TABLE seleccion.promocion_salarial
  ADD CONSTRAINT pk_id_promocion_salarial PRIMARY KEY (id_promocion_salarial);
ALTER TABLE seleccion.promocion_salarial
  ADD CONSTRAINT fk_id_estado_cab FOREIGN KEY (id_estado_cab) REFERENCES planificacion.estado_cab (id_estado_cab) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE seleccion.promocion_salarial
  ADD CONSTRAINT fk_id_ctp FOREIGN KEY (id_cpt) REFERENCES planificacion.cpt (id_cpt) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE seleccion.promocion_salarial
  ADD CONSTRAINT fk_id_configuracion_uo_det FOREIGN KEY (id_configuracion_uo_det) REFERENCES planificacion.configuracion_uo_det (id_configuracion_uo_det) ON UPDATE NO ACTION ON DELETE NO ACTION;



CREATE TABLE seleccion.promocion_concurso_agr
(
   id_promocion_concurso_agr serial, 
   id_promocion_salarial bigint, 
   id_concurso_puesto_agr bigint, 
   nro_orden integer, 
   id_estado_det bigint, 
   activo boolean, 
   observacion character varying(250), 
   id_excepcion bigint, 
   excepcionado boolean, 
   usu_alta character varying(50), 
   fecha_alta timestamp without time zone, 
   usu_mod character varying(50), 
   fecha_mod timestamp without time zone, 
   CONSTRAINT pk_id_promocion_concurso_agr PRIMARY KEY (id_promocion_concurso_agr), 
   CONSTRAINT fk_id_concurso_puesto_agr FOREIGN KEY (id_concurso_puesto_agr) REFERENCES seleccion.concurso_puesto_agr (id_concurso_puesto_agr) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT fk_id_estado_det FOREIGN KEY (id_estado_det) REFERENCES planificacion.estado_det (id_estado_det) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
)
;
