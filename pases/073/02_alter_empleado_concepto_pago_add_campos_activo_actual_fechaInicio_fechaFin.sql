ALTER TABLE general.empleado_concepto_pago
  ADD COLUMN activo boolean;
ALTER TABLE general.empleado_concepto_pago
  ADD COLUMN actual boolean;
ALTER TABLE general.empleado_concepto_pago
  ADD COLUMN fecha_inicio timestamp without time zone;
ALTER TABLE general.empleado_concepto_pago
  ADD COLUMN fecha_fin timestamp without time zone;


UPDATE general.empleado_concepto_pago
   SET activo= true, actual= true
 WHERE activo is null ;

