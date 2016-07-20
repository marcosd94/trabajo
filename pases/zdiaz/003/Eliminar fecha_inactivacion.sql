  ALTER TABLE remuneracion.remuneraciones_tmp
  DROP COLUMN fecha_inactivacion;
  
  ALTER TABLE remuneracion.historico_remuneraciones_tmp
  DROP COLUMN fecha_inactivacion;
  
  ALTER TABLE remuneracion.audit_inactivos
  DROP COLUMN fecha_inactivacion;