--TABLA REMUNERACION.HISTORICO_REMUNERACIONES_TMP, agregadas colummnas usu_paso_historico y fecha_paso_historico 

ALTER TABLE remuneracion.historico_remuneraciones_tmp ADD COLUMN usu_paso_historico  character varying(50);
ALTER TABLE remuneracion.historico_remuneraciones_tmp ADD COLUMN fecha_paso_historico timestamp without time zone;