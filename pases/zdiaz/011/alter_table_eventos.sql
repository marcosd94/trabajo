ALTER TABLE legajo.eventos_recorrido_laboral
  ADD COLUMN desc_tipo_evento character varying(200);
ALTER TABLE legajo.eventos_recorrido_laboral
   ALTER COLUMN encabezado TYPE character varying(500);
ALTER TABLE general.empleado_puesto
  ADD COLUMN desc_uo_historico character varying(500);