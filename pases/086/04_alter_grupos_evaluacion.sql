-- View: eval_desempeno.evaluaciones_funcionario3

CREATE OR REPLACE VIEW eval_desempeno.evaluaciones_funcionario3 AS 
         SELECT DISTINCT oee.denominacion_unidad AS oee, sne.nen_codigo AS nen_cod, sne.nen_nombre AS nen_nom, (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod, sin_entidad.ent_nombre, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo, e.titulo, ge.grupo AS grupo, e.fecha_eval_desde, e.fecha_eval_hasta, (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario, per.documento_identidad, puesto.descripcion AS puesto, uo_puesto.denominacion_unidad AS area, gs.puntaje_final AS puntaje, gs.observacion, s.id_empleado_puesto, emp.id_persona, oee.id_configuracion_uo, gs.id_documento, e.id_evaluacion_desempeno, gs.id_sujetos, ge.id_grupos_evaluacion AS id_grupos_evaluacion, gs.puntaje_pdec, gs.puntaje_percepcion
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
         SELECT DISTINCT oee.denominacion_unidad AS oee, sne.nen_codigo AS nen_cod, sne.nen_nombre AS nen_nom, (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod, sin_entidad.ent_nombre, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo, e.titulo, ge.grupo AS grupo, e.fecha_eval_desde, e.fecha_eval_hasta, (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario, per.documento_identidad, puesto.descripcion AS puesto, uo_puesto.denominacion_unidad AS area, gs.puntaje_final AS puntaje, gs.observacion, s.id_empleado_puesto, emp.id_persona, oee.id_configuracion_uo, gs.id_documento, e.id_evaluacion_desempeno, gs.id_sujetos, ge.id_grupos_evaluacion AS id_grupos_evaluacion, gs.puntaje_pdec, gs.puntaje_percepcion
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
UNION 
         SELECT DISTINCT oee.denominacion_unidad AS oee, sne.nen_codigo AS nen_cod, sne.nen_nombre AS nen_nom, (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod, sin_entidad.ent_nombre, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden, (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo, e.titulo, 'no disponible' AS grupo, e.fecha_eval_desde, e.fecha_eval_hasta, (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario, per.documento_identidad, puesto.descripcion AS puesto, uo_puesto.denominacion_unidad AS area, gs.puntaje_final AS puntaje, gs.observacion, s.id_empleado_puesto, emp.id_persona, oee.id_configuracion_uo, gs.id_documento, e.id_evaluacion_desempeno, gs.id_sujetos, 0 AS id_grupos_evaluacion, gs.puntaje_pdec, gs.puntaje_percepcion
           FROM eval_desempeno.grupos_sujetos gs
   JOIN eval_desempeno.sujetos s ON s.id_sujetos = gs.id_sujetos
   JOIN general.empleado_puesto emp ON emp.id_empleado_puesto = s.id_empleado_puesto
   JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = emp.id_planta_cargo_det
   JOIN planificacion.configuracion_uo_det uo_puesto ON uo_puesto.id_configuracion_uo_det = puesto.id_configuracion_uo_det
   JOIN general.persona per ON per.id_persona = emp.id_persona
   JOIN eval_desempeno.evaluacion_desempeno e ON e.id_evaluacion_desempeno = s.id_evaluacion_desempeno
   JOIN planificacion.configuracion_uo_cab oee ON e.id_configuracion_uo_cab = oee.id_configuracion_uo
   JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
   JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
   JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
  WHERE e.es_eval_rapida = true
  ORDER BY 22;

ALTER TABLE eval_desempeno.evaluaciones_funcionario3
  OWNER TO postgres;
