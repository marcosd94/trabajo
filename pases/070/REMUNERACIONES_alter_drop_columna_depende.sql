ALTER TABLE remuneracion.importacion DROP COLUMN dependencia;
ALTER TABLE remuneracion.remuneraciones DROP COLUMN depende;
ALTER TABLE remuneracion.remuneraciones_tmp DROP COLUMN depende;
ALTER TABLE remuneracion.historico_remuneraciones DROP COLUMN depende;
ALTER TABLE remuneracion.historico_remuneraciones_tmp DROP COLUMN depende;