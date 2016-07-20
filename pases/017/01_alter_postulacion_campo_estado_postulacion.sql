ALTER TABLE seleccion.postulacion
  ADD COLUMN estado_postulacion character varying(50);
COMMENT ON COLUMN seleccion.postulacion.estado_postulacion IS 'Columna para los distintos estados de la postulacion, ejemplo: cancelado-excluido';
