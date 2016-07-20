ALTER TABLE remuneracion.historico_remuneraciones
   ALTER COLUMN carga_horaria TYPE character varying;
   
ALTER TABLE remuneracion.historico_remuneraciones_tmp
   ALTER COLUMN carga_horaria TYPE character varying;

ALTER TABLE remuneracion.importacion
   ALTER COLUMN carga_horaria TYPE character varying;

ALTER TABLE remuneracion.remuneraciones
   ALTER COLUMN carga_horaria TYPE character varying;

ALTER TABLE remuneracion.remuneraciones_tmp
   ALTER COLUMN carga_horaria TYPE character varying;
