ALTER TABLE remuneracion.importacion ALTER COLUMN funcion_cumplida TYPE varchar(500);
ALTER TABLE remuneracion.remuneraciones_tmp ALTER COLUMN funcion_cumplida TYPE varchar(500);
ALTER TABLE remuneracion.historico_remuneraciones_tmp ALTER COLUMN funcion_cumplida TYPE varchar(500);

ALTER TABLE remuneracion.remuneraciones_tmp ALTER COLUMN documento_identidad TYPE varchar(30);
ALTER TABLE remuneracion.historico_remuneraciones_tmp ALTER COLUMN documento_identidad TYPE varchar(30);
