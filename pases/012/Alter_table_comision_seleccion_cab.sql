ALTER TABLE seleccion.comision_seleccion_cab ADD COLUMN documentos bytea;

ALTER TABLE seleccion.comision_seleccion_cab ADD COLUMN id_documento bigint;

ALTER TABLE seleccion.comision_seleccion_cab
  ADD CONSTRAINT comision_seleccion_cab_general_documentos_fk FOREIGN KEY (id_documento)
      REFERENCES general.documentos (id_documento) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
