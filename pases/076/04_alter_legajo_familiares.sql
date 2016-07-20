ALTER TABLE legajo.familiares
   ALTER COLUMN id_persona_familiar DROP NOT NULL;
ALTER TABLE legajo.familiares
  ADD COLUMN nombres_familiar character varying;
ALTER TABLE legajo.familiares
  ADD COLUMN apellidos_familiar character varying;
ALTER TABLE legajo.familiares
  ADD COLUMN sexo character varying(1);
ALTER TABLE legajo.familiares
  ADD COLUMN estado_civil character varying(1);
ALTER TABLE legajo.familiares
  ADD COLUMN fecha_nacimiento date;
ALTER TABLE legajo.familiares
  ADD COLUMN e_mail character varying;
ALTER TABLE legajo.familiares
  ADD COLUMN telefonos character varying;
ALTER TABLE legajo.familiares
  ADD COLUMN id_documento bigint;
ALTER TABLE legajo.familiares
  ADD CONSTRAINT fk_id_documento FOREIGN KEY (id_documento) REFERENCES general.documentos (id_documento) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE legajo.familiares
  ADD COLUMN id_pais_expedicion_doc bigint;
ALTER TABLE legajo.familiares
  ADD CONSTRAINT fk_id_pais_expedicion_doc FOREIGN KEY (id_pais_expedicion_doc) REFERENCES general.pais (id_pais) ON UPDATE NO ACTION ON DELETE NO ACTION;

