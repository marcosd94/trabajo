ALTER TABLE seleccion.eval_referencial
  ADD COLUMN siendo_evaluado boolean;
COMMENT ON COLUMN seleccion.eval_referencial.siendo_evaluado IS 'campo mediante el cual se controlara la concurrencia en las evaluaciones';
