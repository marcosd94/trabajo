ALTER TABLE seleccion.eval_referencial_convocatoria
  ADD COLUMN id_concurso_puesto_agr bigint;
ALTER TABLE seleccion.eval_referencial_convocatoria
  ADD COLUMN fecha_publicacion timestamp without time zone;
ALTER TABLE seleccion.eval_referencial_convocatoria
  ADD CONSTRAINT fk_id_concurso_puesto_agr FOREIGN KEY (id_concurso_puesto_agr) REFERENCES seleccion.concurso_puesto_agr (id_concurso_puesto_agr) ON UPDATE NO ACTION ON DELETE NO ACTION;
