ALTER TABLE planificacion.planta_cargo_det ADD COLUMN comisionado boolean;
UPDATE planificacion.planta_cargo_det SET comisionado='false';
ALTER TABLE planificacion.planta_cargo_det ALTER COLUMN comisionado SET NOT NULL;