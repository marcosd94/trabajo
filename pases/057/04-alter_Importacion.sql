
--TABLA IMPORTACION, NUEVAS COLUMNAS PARA PLANTILLA REMUNERACIONES
ALTER TABLE remuneracion.importacion
  ADD COLUMN remuneracion_total Integer,
  ADD COLUMN lugar character varying(125),
  ADD COLUMN funcion_cumplida character varying(200),
  ADD COLUMN carga_horaria character varying(150),
  ADD COLUMN tipo_discapacidad integer,
  ADD COLUMN anho_ingreso integer,
  ADD COLUMN concepto character varying(200),
  ADD COLUMN linea integer,
  ADD COLUMN descrip_categoria character varying(200),
  ADD COLUMN descrip_concepto character varying(200),
  ADD COLUMN oee integer;
