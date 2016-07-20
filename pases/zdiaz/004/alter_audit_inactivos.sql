ALTER TABLE remuneracion.audit_inactivos
  DROP COLUMN depende;
ALTER TABLE remuneracion.audit_inactivos
   ALTER COLUMN documento_identidad TYPE character varying(30);
ALTER TABLE remuneracion.audit_inactivos
   ALTER COLUMN movimiento TYPE character varying(3);
ALTER TABLE remuneracion.audit_inactivos
   ALTER COLUMN cargo TYPE character varying(250);
ALTER TABLE remuneracion.audit_inactivos
   ALTER COLUMN carga_horaria TYPE character varying;
