
--TABLA IMPORTACION, NUEVAS COLUMNAS DEL MODELO NUEVO DE PLANTILLA, PARA TABLA REMUNERACIONES
ALTER TABLE remuneracion.remuneraciones
  ADD COLUMN remuneracion_total Integer,
  ADD COLUMN lugar character varying(125),
  ADD COLUMN carga_horaria character varying(150),
  ADD COLUMN depende integer,
  ADD COLUMN movimiento character varying(2);

ALTER TABLE remuneracion.historico_remuneraciones
  ADD COLUMN remuneracion_total Integer,
  ADD COLUMN lugar character varying(125),
  ADD COLUMN carga_horaria character varying(150),
  ADD COLUMN depende integer,
  ADD COLUMN movimiento character varying(2);
