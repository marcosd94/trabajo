ALTER TABLE planificacion.cpt
  ADD COLUMN estado_homologacion character varying(50);
COMMENT ON COLUMN planificacion.cpt.estado_homologacion IS 'Columna para guardar el estado de Homologacion del CPT';
