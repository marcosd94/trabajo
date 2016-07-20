-- View: movilidad.bandeja_traslado

-- DROP VIEW movilidad.bandeja_traslado;

CREATE OR REPLACE VIEW movilidad.bandeja_traslado AS 
 SELECT DISTINCT st.id_solicitud_traslado_cab, cuc_o.id_configuracion_uo AS id_oee_origen, 
 cuc_o.orden AS orden_origen, cuc_d.id_configuracion_uo AS id_oee_destino, cuc_d.orden AS orden_destino, cuc_o.denominacion_unidad AS oee_origen, 
 cuc_d.denominacion_unidad AS oee_destino, de_t.descripcion AS tipo_traslado, de_e.descripcion AS estado, 
 se.nen_codigo, se.ent_codigo, se.ent_nombre, sne.nen_codigo AS nivel_cod, sne.nen_nombre AS nivel_nombre, a.descripcion AS actividad, t.actorid_ AS usuario, 
 t.create_ AS fecha_recepcion, t.start_ AS fecha_inicio, to_char(now() - t.create_::timestamp with time zone, 'DD'::text)::integer AS dias_creacion, 
 to_char(now() - t.start_::timestamp with time zone, 'DD'::text)::integer AS dias_inicio, 
 proceso.calc_atraso_actividad_proc(t.create_, ap.id_actividad_proceso, cuc_d.id_configuracion_uo) AS atraso, 
 a.id_actividad, e.id_entidad, sne.id_sin_nivel_entidad, t.id_ AS id_taskinstance, 
 st.id_process_instance, a.cod_actividad, p.id_proceso, par.id_actividad_proceso, a.tipo AS tipo_actividad, e.id_sin_entidad, st.id_persona, persona.nombres, persona.apellidos
   FROM movilidad.solicitud_traslado_cab st
   JOIN planificacion.configuracion_uo_cab cuc_o ON cuc_o.id_configuracion_uo = st.id_oee_origen
   JOIN planificacion.configuracion_uo_cab cuc_d ON cuc_d.id_configuracion_uo = st.id_oee_destino
   JOIN seleccion.datos_especificos de_t ON de_t.id_datos_especificos = st.id_datos_esp_tipo_traslado
   JOIN seleccion.datos_especificos de_e ON de_e.id_datos_especificos = st.id_datos_esp_estado_solicitud
   JOIN planificacion.entidad e ON cuc_d.id_entidad = e.id_entidad
   JOIN sinarh.sin_entidad se ON e.id_sin_entidad = se.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad sne ON sne.ani_aniopre = se.ani_aniopre AND sne.nen_codigo = se.nen_codigo
   JOIN jbpm.jbpm_processinstance pi ON pi.id_ = st.id_process_instance
   JOIN jbpm.jbpm_taskinstance t ON pi.id_ = t.procinst_
   JOIN proceso.actividad a ON upper(t.name_::text) = upper(a.cod_actividad::text)
   JOIN proceso.actividad_proceso ap ON ap.id_actividad = a.id_actividad
   JOIN proceso.proceso p ON p.id_proceso = ap.id_proceso
   JOIN proceso.proc_actividad_rol par ON ap.id_actividad_proceso = par.id_actividad_proceso
   JOIN general.persona persona ON persona.id_persona = st.id_persona
  WHERE t.end_ IS NULL AND st.activo = true
  ORDER BY st.id_solicitud_traslado_cab;

ALTER TABLE movilidad.bandeja_traslado
  OWNER TO postgres;