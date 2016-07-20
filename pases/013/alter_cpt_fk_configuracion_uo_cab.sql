ALTER TABLE planificacion.cpt
  ADD COLUMN id_configuracion_uo_cab bigint;
ALTER TABLE planificacion.cpt
  ADD CONSTRAINT fk_id_configuracion_uo_cab FOREIGN KEY (id_configuracion_uo_cab) REFERENCES planificacion.configuracion_uo_cab (id_configuracion_uo) ON UPDATE NO ACTION ON DELETE NO ACTION;
