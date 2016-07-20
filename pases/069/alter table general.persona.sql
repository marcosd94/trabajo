-- Agregada columna cantidad_pers_a_cargo, indica la cantidad de personas a cargo que posee la persona
ALTER TABLE general.persona 
  ADD COLUMN cant_personas_a_cargo bigint;