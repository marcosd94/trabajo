--TABLAS REMUNERACION.REMUNERACIONES_TMP y REMUNERACION.HISTORICO_REMUNERACIONES_TMP, agregada colummna fecha_inactivacion 

ALTER TABLE remuneracion.remuneraciones_tmp ADD COLUMN fecha_inactivacion timestamp without time zone;
ALTER TABLE remuneracion.historico_remuneraciones_tmp ADD COLUMN fecha_inactivacion timestamp without time zone;