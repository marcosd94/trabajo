-- se eliminan los datos laborales en la sección FAMILIARES del legajo, por lo cual se elimina la restricción NOT NULL 
-- de la columna funcionario_publico para poder guardar 
ALTER TABLE legajo.familiares 
  ALTER COLUMN funcionario_publico  DROP  NOT NULL;

-- se agrega un campo observaciones para cada familiar
ALTER TABLE legajo.familiares
  ADD COLUMN observaciones character varying(250);