ALTER TABLE seleccion.eval_referencial_convocatoria
   ALTER COLUMN id_eval_referencial_tipoeval DROP NOT NULL;
ALTER TABLE seleccion.eval_referencial_convocatoria
  ADD COLUMN motivo character varying(150);