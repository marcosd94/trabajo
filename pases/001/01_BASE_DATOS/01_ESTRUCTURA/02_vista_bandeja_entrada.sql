CREATE OR REPLACE VIEW proceso.bandeja_entrada AS 
SELECT DISTINCT c.id_concurso, c.nombre AS nombre_concurso, cuc.id_configuracion_uo, cuc.orden AS orden_configuracion, cuc.denominacion_unidad AS denominacion_unidad_cab, se.nen_codigo, se.ent_codigo, se.ent_nombre, cpa.descripcion_grupo, sne.nen_codigo AS nivel_cod, sne.nen_nombre AS nivel_nombre, a.descripcion AS actividad, t.actorid_ AS usuario, t.create_ AS fecha_recepcion, t.start_ AS fecha_inicio, to_char(now() - t.create_::timestamp with time zone, 'DD'::text)::integer AS dias_creacion, to_char(now() - t.start_::timestamp with time zone, 'DD'::text)::integer AS dias_inicio, proceso.calc_atraso_actividad_proc(t.create_, ap.id_actividad_proceso, c.id_configuracion_uo) AS atraso, a.id_actividad, e.id_entidad, sne.id_sin_nivel_entidad, t.id_ AS id_taskinstance, cpa.id_concurso_puesto_agr, cpa.id_process_instance, a.cod_actividad, p.id_proceso, par.id_actividad_proceso, a.tipo AS tipo_actividad, e.id_sin_entidad, cpa.carpeta
   FROM seleccion.concurso_puesto_agr cpa
   JOIN seleccion.concurso c ON cpa.id_concurso = c.id_concurso
   JOIN planificacion.configuracion_uo_cab cuc ON c.id_configuracion_uo = cuc.id_configuracion_uo
   JOIN planificacion.entidad e ON cuc.id_entidad = e.id_entidad
   JOIN sinarh.sin_entidad se ON e.id_sin_entidad = se.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad sne ON sne.ani_aniopre = se.ani_aniopre AND sne.nen_codigo = se.nen_codigo
   JOIN jbpm.jbpm_processinstance pi ON pi.id_ = cpa.id_process_instance
   JOIN jbpm.jbpm_taskinstance t ON pi.id_ = t.procinst_
   JOIN proceso.actividad a ON upper(t.name_::text) = upper(a.cod_actividad::text)
   JOIN proceso.actividad_proceso ap ON ap.id_actividad = a.id_actividad
   JOIN proceso.proceso p ON p.id_proceso = ap.id_proceso
   JOIN proceso.proc_actividad_rol par ON ap.id_actividad_proceso = par.id_actividad_proceso
   JOIN seleccion.datos_especificos de ON de.id_datos_especificos = c.id_datos_esp_tipo_conc
   JOIN seleccion.datos_generales dg ON de.id_datos_generales = dg.id_datos_generales
  WHERE t.end_ IS NULL AND c.activo = true AND cpa.activo = true AND dg.activo = true AND de.activo = true AND dg.descripcion::text ~~ '%TIPOS%DE%CONCURSO%'::text AND (de.descripcion::text ~~ '%CONCURSO%PUBLICO%DE%OPOSICION%'::text OR de.descripcion::text ~~ '%CONCURSO%DE%MERITOS%'::text) AND p.descripcion::text ~~ '%CONCURSO%'::text;