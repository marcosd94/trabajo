ALTER TABLE capacitacion.capacitaciones
  RENAME COLUMN cupo_tope TO cupo_minimo;
ALTER TABLE capacitacion.capacitaciones
	ADD COLUMN cupo_maximo bigint;