-- View: capacitacion.bandeja_capacitacion

-- DROP VIEW capacitacion.bandeja_capacitacion;

CREATE OR REPLACE VIEW capacitacion.bandeja_capacitacion AS 
 SELECT DISTINCT c.id_capacitacion, c.activo, c.nombre AS nombre_capac, de.descripcion, cuc.id_configuracion_uo, cuc.orden AS orden_configuracion, cuc.denominacion_unidad AS denominacion_unidad_cab, cud.id_configuracion_uo_det, cud.orden AS orden_configuracion_det, cud.denominacion_unidad AS denominacion_unidad_det, se.nen_codigo, se.ent_codigo, se.ent_nombre, sne.nen_codigo AS nivel_cod, sne.nen_nombre AS nivel_nombre, a.descripcion AS actividad, t.actorid_ AS usuario, c.fecha_recep_desde AS fecha_recepcion, c.fecha_inicio AS fecha_inicio, to_char(now() - c.fecha_recep_desde::timestamp with time zone, 'DD'::text)::integer AS dias_creacion, to_char(now() - c.fecha_inicio::timestamp with time zone, 'DD'::text)::integer AS dias_inicio, proceso.calc_atraso_actividad_proc(c.fecha_recep_desde, ap.id_actividad_proceso, c.id_configuracion_uo) AS atraso, a.id_actividad, e.id_entidad, sne.id_sin_nivel_entidad, t.id_ AS id_taskinstance, c.id_process_instance, a.cod_actividad, p.id_proceso, par.id_actividad_proceso, a.tipo AS tipo_actividad, e.id_sin_entidad, ref.desc_abrev AS estado_grupo
   FROM capacitacion.capacitaciones c
   JOIN seleccion.datos_especificos de ON de.id_datos_especificos = c.id_datos_especificos_tipo_cap
   JOIN planificacion.configuracion_uo_det cud ON c.id_configuracion_uo_det = cud.id_configuracion_uo_det
   JOIN planificacion.configuracion_uo_cab cuc ON c.id_configuracion_uo = cuc.id_configuracion_uo
   JOIN planificacion.entidad e ON cuc.id_entidad = e.id_entidad
   JOIN sinarh.sin_entidad se ON e.id_sin_entidad = se.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad sne ON sne.ani_aniopre = se.ani_aniopre AND sne.nen_codigo = se.nen_codigo
   JOIN jbpm.jbpm_processinstance pi ON pi.id_ = c.id_process_instance
   JOIN jbpm.jbpm_taskinstance t ON pi.id_ = t.procinst_
   JOIN proceso.actividad a ON upper(t.name_::text) = upper(a.cod_actividad::text)
   JOIN proceso.actividad_proceso ap ON ap.id_actividad = a.id_actividad
   JOIN proceso.proceso p ON p.id_proceso = ap.id_proceso
   JOIN proceso.proc_actividad_rol par ON ap.id_actividad_proceso = par.id_actividad_proceso
   JOIN seleccion.referencias ref ON ref.valor_num = c.estado
  WHERE t.end_ IS NULL AND c.activo = true AND ref.dominio::text = 'ESTADOS_CAPACITACION'::text
  ORDER BY c.id_capacitacion;

ALTER TABLE capacitacion.bandeja_capacitacion
  OWNER TO postgres;

