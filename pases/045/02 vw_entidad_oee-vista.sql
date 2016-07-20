-- View: planificacion.vw_entidad_oee

-- DROP VIEW planificacion.vw_entidad_oee;
--nueva vista
CREATE OR REPLACE VIEW planificacion.vw_entidad_oee AS 
 SELECT e.id_entidad AS id_entidad_oee, e.id_sin_entidad, n.id_sin_nivel_entidad, e.nen_codigo AS codigo_nivel, n.nen_nombre AS nivel, e.ent_codigo, (n.nen_codigo || '.'::text) || e.ent_codigo AS codigo_entidad, s.ent_nombre AS entidad, oee.id_configuracion_uo, (((n.nen_codigo || '.'::text) || e.ent_codigo) || '.'::text) || oee.orden AS codigo_oee, oee.denominacion_unidad, oee.orden
   FROM planificacion.entidad e
   JOIN sinarh.sin_entidad s ON e.id_sin_entidad = s.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad n ON n.nen_codigo = s.nen_codigo --AND n.ani_aniopre = s.ani_aniopre
   JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = e.id_configuracion_uo
  WHERE  oee.activo = true
  ORDER BY oee.id_configuracion_uo;

ALTER TABLE planificacion.vw_entidad_oee
  OWNER TO postgres;
  
 
