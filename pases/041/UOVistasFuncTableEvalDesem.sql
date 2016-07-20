ALTER TABLE eval_desempeno.evaluacion_desempeno RENAME id_configuracion_uo_det  TO id_configuracion_uo_cab;

ALTER TABLE eval_desempeno.evaluacion_desempeno DROP CONSTRAINT configuracion_uo_det_evaluacion_desempeno_fk;

ALTER TABLE eval_desempeno.evaluacion_desempeno ADD CONSTRAINT configuracion_uo_cab_evaluacion_desempeno_fk FOREIGN KEY (id_configuracion_uo_cab) REFERENCES planificacion.configuracion_uo_cab (id_configuracion_uo) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE OR REPLACE FUNCTION eval_desempeno.fnc_plan_mejora_rpt_hist(id_resultado_eval bigint, id_grupo_sujeto bigint)
  RETURNS text AS
$BODY$
        DECLARE
            resultado TEXT;
BEGIN
    SELECT
        array_to_string(array_agg(
            plan.descripcion), '. ')
    INTO
        resultado
    FROM
       eval_desempeno.plan_mejora plan
   join eval_desempeno.resultado_eval_plan r
   on r.id_plan_mejora = plan.id_plan_mejora
   join eval_desempeno.resultado_eval re
   on r.id_resultado_eval = re.id_resultado_eval
    WHERE
   re.id_resultado_eval = id_resultado_eval and re.id_grupo_sujeto = id_grupo_sujeto 
        and r.activo = true ;
RETURN resultado;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION eval_desempeno.fnc_plan_mejora_rpt_hist(bigint,bigint) OWNER TO postgres;


CREATE OR REPLACE VIEW eval_desempeno.evaluaciones_funcionario2 AS
SELECT DISTINCT oee.denominacion_unidad AS oee, sne.nen_codigo AS nen_cod, sne.nen_nombre AS nen_nom, (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod, 
         sin_entidad.ent_nombre, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo, 
         e.titulo, ge.grupo, de.descripcion AS plantilla, p.resultado_esperado AS criterio, e.fecha_eval_desde, e.fecha_eval_hasta, (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario, 
         per.documento_identidad, puesto.descripcion AS puesto, uo_puesto.denominacion_unidad AS area, 
                CASE
                    WHEN eval_desempeno.fnc_plan_mejora_rpt_hist(r.id_resultado_eval, gs.id_grupo_sujeto) IS NOT NULL THEN eval_desempeno.fnc_plan_mejora_rpt_hist(r.id_resultado_eval, gs.id_grupo_sujeto)
                    ELSE 'No tiene'
                END AS plan_mejora, gs.puntaje_final AS puntaje, gs.puntaje_pdec AS pdec, gs.puntaje_percepcion AS percepcion, gs.observacion, s.id_empleado_puesto, emp.id_persona, oee.id_configuracion_uo, gs.id_documento, e.id_evaluacion_desempeno, gm.id_grupo_metodologia
           FROM eval_desempeno.plantilla_eval_conf_det p
      JOIN eval_desempeno.grupo_metodologia gm ON gm.id_grupo_metodologia = p.id_grupo_metodologia
   JOIN seleccion.datos_especificos de ON de.id_datos_especificos = gm.id_datos_especif_metod
   JOIN eval_desempeno.resultado_eval r ON r.id_plantilla_eval_conf_det = p.id_plantilla_eval_conf_det
   JOIN eval_desempeno.grupos_evaluacion ge ON ge.id_grupos_evaluacion = gm.id_grupos_evaluacion
   JOIN eval_desempeno.grupos_sujetos gs ON gs.id_grupos_evaluacion = ge.id_grupos_evaluacion
   JOIN eval_desempeno.sujetos s ON s.id_sujetos = gs.id_sujetos
   JOIN general.empleado_puesto emp ON emp.id_empleado_puesto = s.id_empleado_puesto
   JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = emp.id_planta_cargo_det
   JOIN planificacion.configuracion_uo_det uo_puesto ON uo_puesto.id_configuracion_uo_det = puesto.id_configuracion_uo_det
   JOIN general.persona per ON per.id_persona = emp.id_persona
   JOIN eval_desempeno.evaluacion_desempeno e ON e.id_evaluacion_desempeno = ge.id_evaluacion_desempeno
   JOIN planificacion.configuracion_uo_cab oee ON e.id_configuracion_uo_cab = oee.id_configuracion_uo
   JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
   JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
   JOIN seleccion.referencias ref ON ref.valor_num = e.estado
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text AND gs.presente = true
UNION 
         SELECT DISTINCT oee.denominacion_unidad AS oee, sne.nen_codigo AS nen_cod, sne.nen_nombre AS nen_nom, (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod, sin_entidad.ent_nombre, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo, e.titulo, ge.grupo, de.descripcion AS plantilla, cab.descripcion AS criterio, e.fecha_eval_desde, e.fecha_eval_hasta, (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario, per.documento_identidad, puesto.descripcion AS puesto, uo_puesto.denominacion_unidad AS area, 
                CASE
                    WHEN eval_desempeno.fnc_plan_mejora_pdec(cab.id_plantilla_eval_pdec_cab) IS NOT NULL THEN eval_desempeno.fnc_plan_mejora_pdec(cab.id_plantilla_eval_pdec_cab)
        ELSE 'No tiene'
                END AS plan_mejora, gs.puntaje_final AS puntaje, gs.puntaje_pdec AS pdec, gs.puntaje_percepcion AS percepcion, gs.observacion, s.id_empleado_puesto, emp.id_persona, oee.id_configuracion_uo, gs.id_documento, e.id_evaluacion_desempeno, gm.id_grupo_metodologia
           FROM eval_desempeno.plantilla_eval_pdec p
      JOIN eval_desempeno.plantilla_eval_pdec_cab cab ON p.id_plantilla_eval_pdec = cab.id_plantilla_eval_pdec
   JOIN eval_desempeno.plantilla_eval_pdec_det det ON cab.id_plantilla_eval_pdec_cab = det.id_plantilla_eval_pdec_cab
   LEFT JOIN eval_desempeno.resultado_eval_plan r ON r.id_plantilla_eval_pdec_cab = cab.id_plantilla_eval_pdec_cab
   JOIN eval_desempeno.grupo_metodologia gm ON gm.id_grupo_metodologia = p.id_grupo_metodologia
   JOIN seleccion.datos_especificos de ON de.id_datos_especificos = gm.id_datos_especif_metod
   JOIN eval_desempeno.grupos_evaluacion ge ON ge.id_grupos_evaluacion = gm.id_grupos_evaluacion
   JOIN eval_desempeno.grupos_sujetos gs ON gs.id_grupos_evaluacion = ge.id_grupos_evaluacion AND gs.id_sujetos = p.id_sujetos
   JOIN eval_desempeno.sujetos s ON s.id_sujetos = gs.id_sujetos
   JOIN general.empleado_puesto emp ON emp.id_empleado_puesto = s.id_empleado_puesto
   JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = emp.id_planta_cargo_det
   JOIN planificacion.configuracion_uo_det uo_puesto ON uo_puesto.id_configuracion_uo_det = puesto.id_configuracion_uo_det
   JOIN general.persona per ON per.id_persona = emp.id_persona
   JOIN eval_desempeno.evaluacion_desempeno e ON e.id_evaluacion_desempeno = ge.id_evaluacion_desempeno
   JOIN planificacion.configuracion_uo_cab oee ON e.id_configuracion_uo_cab = oee.id_configuracion_uo
   JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
   JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
   JOIN seleccion.referencias ref ON ref.valor_num = e.estado
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text AND gs.presente = true order by id_evaluacion_desempeno, plantilla, criterio desc;



DROP VIEW eval_desempeno.bandeja_evaluacion;

CREATE OR REPLACE VIEW eval_desempeno.bandeja_evaluacion AS 
 SELECT DISTINCT ed.id_evaluacion_desempeno, ed.activo, ed.titulo AS titulo_eval, cuc.id_configuracion_uo, cuc.orden AS orden_configuracion, cuc.denominacion_unidad AS denominacion_unidad_cab, se.nen_codigo, se.ent_codigo, se.ent_nombre, sne.nen_codigo AS nivel_cod, sne.nen_nombre AS nivel_nombre, a.descripcion AS actividad, t.actorid_ AS usuario, t.create_ AS fecha_recepcion, t.start_ AS fecha_inicio, to_char(now() - t.create_::timestamp with time zone, 'DD'::text)::integer AS dias_creacion, to_char(now() - t.start_::timestamp with time zone, 'DD'::text)::integer AS dias_inicio, proceso.calc_atraso_actividad_proc(t.create_, ap.id_actividad_proceso, cuc.id_configuracion_uo) AS atraso, a.id_actividad, e.id_entidad, sne.id_sin_nivel_entidad, t.id_ AS id_taskinstance, ed.id_process_instance, a.cod_actividad, p.id_proceso, par.id_actividad_proceso, a.tipo AS tipo_actividad, e.id_sin_entidad, ref.desc_abrev AS estado_eval
   FROM eval_desempeno.evaluacion_desempeno ed
   JOIN planificacion.configuracion_uo_cab cuc ON ed.id_configuracion_uo_cab = cuc.id_configuracion_uo
   JOIN planificacion.entidad e ON cuc.id_entidad = e.id_entidad
   JOIN sinarh.sin_entidad se ON e.id_sin_entidad = se.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad sne ON sne.ani_aniopre = se.ani_aniopre AND sne.nen_codigo = se.nen_codigo
   JOIN jbpm.jbpm_processinstance pi ON pi.id_ = ed.id_process_instance
   JOIN jbpm.jbpm_taskinstance t ON pi.id_ = t.procinst_
   JOIN proceso.actividad a ON upper(t.name_::text) = upper(a.cod_actividad::text)
   JOIN proceso.actividad_proceso ap ON ap.id_actividad = a.id_actividad
   JOIN proceso.proceso p ON p.id_proceso = ap.id_proceso
   JOIN proceso.proc_actividad_rol par ON ap.id_actividad_proceso = par.id_actividad_proceso
   JOIN seleccion.referencias ref ON ref.valor_num = ed.estado
  WHERE t.end_ IS NULL AND ed.activo = true AND ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text
  ORDER BY ed.id_evaluacion_desempeno;


DROP VIEW eval_desempeno.historico_evaluacion;

CREATE OR REPLACE VIEW eval_desempeno.historico_evaluacion AS
SELECT DISTINCT act.id_actividad, e.id_evaluacion_desempeno AS id_evaluacion, oee.denominacion_unidad AS oee, e.titulo, e.usu_alta AS usuario_alta, e.fecha_alta, ref.desc_larga AS estado, act.cod_actividad, act.descripcion AS actividad, task.create_ AS fecha_creacion, task.actorid_ AS usuario_inicio, task.start_ AS fecha_inicio, task.userend_ AS usuario_fin, task.end_ AS fecha_fin
   FROM eval_desempeno.evaluacion_desempeno e
   JOIN jbpm.jbpm_taskinstance task ON task.procinst_ = e.id_process_instance
   JOIN proceso.actividad act ON act.cod_actividad::text = task.name_::text
   JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = e.id_configuracion_uo_cab
   JOIN seleccion.referencias ref ON e.estado = ref.valor_num
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text
  ORDER BY task.create_;

DROP VIEW eval_desempeno.planes_mejora_rpt;

CREATE OR REPLACE VIEW eval_desempeno.planes_mejora_rpt AS 
         SELECT DISTINCT oee.denominacion_unidad AS oee, sne.nen_codigo AS nen_cod, sne.nen_nombre AS nen_nom, (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod, 
         sin_entidad.ent_nombre, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden, 
         e.titulo, e.fecha_eval_desde, e.fecha_eval_hasta, ge.grupo, de.descripcion, pm.descripcion AS plan_mejora, count(pm.descripcion) AS cantidad, 
         gm.id_grupo_metodologia, e.id_evaluacion_desempeno, oee.id_configuracion_uo, rp.id_plan_mejora
           FROM eval_desempeno.plantilla_eval_conf_det p
      JOIN eval_desempeno.grupo_metodologia gm ON gm.id_grupo_metodologia = p.id_grupo_metodologia
   JOIN seleccion.datos_especificos de ON de.id_datos_especificos = gm.id_datos_especif_metod
   JOIN eval_desempeno.resultado_eval r ON r.id_plantilla_eval_conf_det = p.id_plantilla_eval_conf_det AND r.id_grupo_metodologia = gm.id_grupo_metodologia
   JOIN eval_desempeno.grupos_evaluacion ge ON ge.id_grupos_evaluacion = gm.id_grupos_evaluacion
   JOIN eval_desempeno.resultado_eval_plan rp ON r.id_resultado_eval = rp.id_resultado_eval
   JOIN eval_desempeno.plan_mejora pm ON pm.id_plan_mejora = rp.id_plan_mejora
   JOIN eval_desempeno.evaluacion_desempeno e ON e.id_evaluacion_desempeno = ge.id_evaluacion_desempeno
   JOIN planificacion.configuracion_uo_cab oee ON e.id_configuracion_uo_cab = oee.id_configuracion_uo
   JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
   JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
   JOIN seleccion.referencias ref ON ref.valor_num = e.estado
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text AND ref.desc_larga::text = 'FINALIZADA'::text AND e.activo = true
  GROUP BY oee.denominacion_unidad, sne.nen_codigo, sne.nen_nombre, (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo, sin_entidad.ent_nombre, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden, e.titulo, e.fecha_eval_desde, e.fecha_eval_hasta, ge.grupo, de.descripcion, pm.descripcion, gm.id_grupo_metodologia, e.id_evaluacion_desempeno, oee.id_configuracion_uo, rp.id_plan_mejora
UNION 
         SELECT DISTINCT oee.denominacion_unidad AS oee, sne.nen_codigo AS nen_cod, sne.nen_nombre AS nen_nom, (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod, 
         sin_entidad.ent_nombre, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden, 
         e.titulo, e.fecha_eval_desde, e.fecha_eval_hasta, ge.grupo, de.descripcion, pm.descripcion AS plan_mejora, count(pm.descripcion) AS cantidad, 
         gm.id_grupo_metodologia, e.id_evaluacion_desempeno, oee.id_configuracion_uo, r.id_plan_mejora
           FROM eval_desempeno.plantilla_eval_pdec p
      JOIN eval_desempeno.plantilla_eval_pdec_cab cab ON p.id_plantilla_eval_pdec = cab.id_plantilla_eval_pdec
   JOIN eval_desempeno.resultado_eval_plan r ON r.id_plantilla_eval_pdec_cab = cab.id_plantilla_eval_pdec_cab
   JOIN eval_desempeno.plan_mejora pm ON pm.id_plan_mejora = r.id_plan_mejora
   JOIN eval_desempeno.grupo_metodologia gm ON gm.id_grupo_metodologia = p.id_grupo_metodologia
   JOIN seleccion.datos_especificos de ON de.id_datos_especificos = gm.id_datos_especif_metod
   JOIN eval_desempeno.grupos_evaluacion ge ON ge.id_grupos_evaluacion = gm.id_grupos_evaluacion
   JOIN eval_desempeno.evaluacion_desempeno e ON e.id_evaluacion_desempeno = ge.id_evaluacion_desempeno
   JOIN planificacion.configuracion_uo_cab oee ON e.id_configuracion_uo_cab = oee.id_configuracion_uo
   JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
   JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
   JOIN seleccion.referencias ref ON ref.valor_num = e.estado
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text AND ref.desc_larga::text = 'FINALIZADA'::text AND e.activo = true
  GROUP BY oee.denominacion_unidad, sne.nen_codigo, sne.nen_nombre, (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo, sin_entidad.ent_nombre, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden, e.titulo, e.fecha_eval_desde, e.fecha_eval_hasta, ge.grupo, de.descripcion, pm.descripcion, gm.id_grupo_metodologia, e.id_evaluacion_desempeno, oee.id_configuracion_uo, r.id_plan_mejora;


  CREATE OR REPLACE VIEW eval_desempeno.evaluaciones_funcionario3 AS
SELECT DISTINCT oee.denominacion_unidad AS oee, sne.nen_codigo AS nen_cod, sne.nen_nombre AS nen_nom, (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod, 
         sin_entidad.ent_nombre, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo, 
         e.titulo, ge.grupo, e.fecha_eval_desde, e.fecha_eval_hasta, (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario, per.documento_identidad, puesto.descripcion AS puesto, uo_puesto.denominacion_unidad AS area, 
         gs.puntaje_final AS puntaje, gs.observacion, s.id_empleado_puesto, emp.id_persona, oee.id_configuracion_uo, gs.id_documento, e.id_evaluacion_desempeno, gs.id_sujetos, ge.id_grupos_evaluacion, gs.puntaje_pdec, gs.puntaje_percepcion
           FROM eval_desempeno.plantilla_eval_conf_det p
      JOIN eval_desempeno.grupo_metodologia gm ON gm.id_grupo_metodologia = p.id_grupo_metodologia
   JOIN seleccion.datos_especificos de ON de.id_datos_especificos = gm.id_datos_especif_metod
   JOIN eval_desempeno.resultado_eval r ON r.id_plantilla_eval_conf_det = p.id_plantilla_eval_conf_det
   JOIN eval_desempeno.grupos_evaluacion ge ON ge.id_grupos_evaluacion = gm.id_grupos_evaluacion
   JOIN eval_desempeno.grupos_sujetos gs ON gs.id_grupos_evaluacion = ge.id_grupos_evaluacion
   JOIN eval_desempeno.sujetos s ON s.id_sujetos = gs.id_sujetos
   JOIN general.empleado_puesto emp ON emp.id_empleado_puesto = s.id_empleado_puesto
   JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = emp.id_planta_cargo_det
   JOIN planificacion.configuracion_uo_det uo_puesto ON uo_puesto.id_configuracion_uo_det = puesto.id_configuracion_uo_det
   JOIN general.persona per ON per.id_persona = emp.id_persona
   JOIN eval_desempeno.evaluacion_desempeno e ON e.id_evaluacion_desempeno = ge.id_evaluacion_desempeno
   JOIN planificacion.configuracion_uo_cab oee ON e.id_configuracion_uo_cab = oee.id_configuracion_uo
   JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
   JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
   JOIN seleccion.referencias ref ON ref.valor_num = e.estado
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text AND gs.presente = true
UNION 
         SELECT DISTINCT oee.denominacion_unidad AS oee, sne.nen_codigo AS nen_cod, sne.nen_nombre AS nen_nom, (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod, 
         sin_entidad.ent_nombre, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo, 
         e.titulo, ge.grupo, e.fecha_eval_desde, e.fecha_eval_hasta, (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario, per.documento_identidad, puesto.descripcion AS puesto, uo_puesto.denominacion_unidad AS area, 
         gs.puntaje_final AS puntaje, gs.observacion, s.id_empleado_puesto, emp.id_persona, oee.id_configuracion_uo, gs.id_documento, e.id_evaluacion_desempeno, gs.id_sujetos, ge.id_grupos_evaluacion, gs.puntaje_pdec, gs.puntaje_percepcion
           FROM eval_desempeno.plantilla_eval_pdec p
      JOIN eval_desempeno.plantilla_eval_pdec_cab cab ON p.id_plantilla_eval_pdec = cab.id_plantilla_eval_pdec
   JOIN eval_desempeno.plantilla_eval_pdec_det det ON cab.id_plantilla_eval_pdec_cab = det.id_plantilla_eval_pdec_cab
   LEFT JOIN eval_desempeno.resultado_eval_plan r ON r.id_plantilla_eval_pdec_cab = cab.id_plantilla_eval_pdec_cab
   JOIN eval_desempeno.grupo_metodologia gm ON gm.id_grupo_metodologia = p.id_grupo_metodologia
   JOIN seleccion.datos_especificos de ON de.id_datos_especificos = gm.id_datos_especif_metod
   JOIN eval_desempeno.grupos_evaluacion ge ON ge.id_grupos_evaluacion = gm.id_grupos_evaluacion
   JOIN eval_desempeno.grupos_sujetos gs ON gs.id_grupos_evaluacion = ge.id_grupos_evaluacion AND gs.id_sujetos = p.id_sujetos
   JOIN eval_desempeno.sujetos s ON s.id_sujetos = gs.id_sujetos
   JOIN general.empleado_puesto emp ON emp.id_empleado_puesto = s.id_empleado_puesto
   JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = emp.id_planta_cargo_det
   JOIN planificacion.configuracion_uo_det uo_puesto ON uo_puesto.id_configuracion_uo_det = puesto.id_configuracion_uo_det
   JOIN general.persona per ON per.id_persona = emp.id_persona
   JOIN eval_desempeno.evaluacion_desempeno e ON e.id_evaluacion_desempeno = ge.id_evaluacion_desempeno
   JOIN planificacion.configuracion_uo_cab oee ON e.id_configuracion_uo_cab = oee.id_configuracion_uo
   JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
   JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
   JOIN seleccion.referencias ref ON ref.valor_num = e.estado
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text AND gs.presente = true
  ORDER BY id_evaluacion_desempeno;