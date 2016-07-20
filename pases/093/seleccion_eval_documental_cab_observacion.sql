-- Cambio en el limite de caracteres para el campo observacion de seleccion.eval_documental_cab

ALTER TABLE seleccion.eval_documental_cab
   ALTER COLUMN observacion TYPE character varying;