
--TABLA CONCURSO, NUEVO BOOLEAN PROMOCION
ALTER TABLE seleccion.concurso
  ADD COLUMN promocion boolean DEFAULT false;
COMMENT ON COLUMN seleccion.concurso.promocion IS 'nuevo campo para indicar los concursos internos de promocion salarial';
