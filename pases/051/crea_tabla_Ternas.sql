CREATE TABLE seleccion.ternas
(
   id_terna bigserial, 
   nro_terna integer, 
   nombre_terna character varying(100), 
   id_concurso_puesto_agr bigint, 
   id_eval_ref_post_seleccionado bigint, 
   id_eval_ref_post_elegible_1 bigint, 
   id_eval_ref_post_elegible_2 bigint, 
   observacion character varying(750), 
   usu_alta character varying(50), 
   fecha_alta timestamp without time zone, 
   usu_mod character varying(50), 
   fecha_mod timestamp without time zone, 
   CONSTRAINT constraint_1 PRIMARY KEY (id_terna), 
   CONSTRAINT constraint_2 FOREIGN KEY (id_concurso_puesto_agr) REFERENCES seleccion.concurso_puesto_agr (id_concurso_puesto_agr) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT constraint_3 FOREIGN KEY (id_eval_ref_post_seleccionado) REFERENCES seleccion.eval_referencial_postulante (id_eval_referencial_postulante) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT constraint_4 FOREIGN KEY (id_eval_ref_post_elegible_1) REFERENCES seleccion.eval_referencial_postulante (id_eval_referencial_postulante) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT constraint_5 FOREIGN KEY (id_eval_ref_post_elegible_2) REFERENCES seleccion.eval_referencial_postulante (id_eval_referencial_postulante) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
)
;