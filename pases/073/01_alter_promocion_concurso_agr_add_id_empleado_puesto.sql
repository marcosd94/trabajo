ALTER TABLE seleccion.promocion_concurso_agr
  ADD COLUMN id_empleado_puesto bigint;
ALTER TABLE seleccion.promocion_concurso_agr
  ADD CONSTRAINT fk_id_empleado_puesto FOREIGN KEY (id_empleado_puesto) REFERENCES general.empleado_puesto (id_empleado_puesto) ON UPDATE NO ACTION ON DELETE NO ACTION;
