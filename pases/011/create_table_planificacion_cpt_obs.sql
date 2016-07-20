CREATE TABLE planificacion.cpt_obs
(
   id_cpt_obs bigserial NOT NULL, 
   id_cpt bigint NOT NULL, 
   observacion_sfp character varying(1000) NOT NULL, 
   usu_observacion_sfp character varying(50) NOT NULL, 
   fecha_observacion_sfp timestamp with time zone NOT NULL, 
   respuesta_oee character varying(1000), 
   usu_respuesta_oee character varying(50), 
   fecha_respuesta_oee timestamp with time zone, 
   activo boolean NOT NULL, 
   usu_alta character varying(50) NOT NULL, 
   fecha_alta timestamp with time zone NOT NULL, 
   usu_mod character varying(50), 
   fecha_mod timestamp with time zone, 
   CONSTRAINT id_cpt_obs_key PRIMARY KEY (id_cpt_obs), 
   CONSTRAINT id_cpt_foreign_key FOREIGN KEY (id_cpt) REFERENCES planificacion.cpt (id_cpt) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
)
;
