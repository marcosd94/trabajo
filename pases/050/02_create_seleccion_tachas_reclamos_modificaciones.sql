CREATE TABLE seleccion.tachas_reclamos_modificaciones
(
   id_tachas_reclamos_modificaciones bigserial NOT NULL, 
   fecha_trm timestamp without time zone NOT NULL,
   id_eval_documental_cab bigint, 
   modifica_eval_documental boolean, 
   modifica_evaluaciones BOOLEAN,
   id_documento_postulante bigint, 
   id_documento_comision bigint, 
   observacion character varying(1000), 
   activo boolean NOT NULL,
   usu_alta character varying(50) NOT NULL, 
   fecha_alta timestamp without time zone NOT NULL, 
   usu_mod character varying(50), 
   fecha_mod timestamp without time zone, 
   
   CONSTRAINT pk_id_tachas_reclamos_modificaciones PRIMARY KEY (id_tachas_reclamos_modificaciones) USING INDEX TABLESPACE pg_default, 
   CONSTRAINT fk_id_eval_documental_cab FOREIGN KEY (id_eval_documental_cab) REFERENCES seleccion.eval_documental_cab (id_eval_documental_cab) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT fk_id_documento_postulante FOREIGN KEY (id_documento_postulante) REFERENCES general.documentos (id_documento) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT fk_id_documento_comision FOREIGN KEY (id_documento_comision) REFERENCES general.documentos (id_documento) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
);


ALTER TABLE seleccion.tachas_reclamos_modificaciones OWNER TO postgres;

