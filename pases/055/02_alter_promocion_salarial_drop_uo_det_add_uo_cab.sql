ALTER TABLE seleccion.promocion_salarial
  DROP COLUMN id_configuracion_uo_det;
ALTER TABLE seleccion.promocion_salarial
  ADD COLUMN id_configuracion_uo_cab bigint;
ALTER TABLE seleccion.promocion_salarial
 ADD CONSTRAINT fk_id_configuracion_uo_cab FOREIGN KEY (id_configuracion_uo_cab)
 REFERENCES planificacion.configuracion_uo_cab (id_configuracion_uo) ON UPDATE NO ACTION ON DELETE NO ACTION;
