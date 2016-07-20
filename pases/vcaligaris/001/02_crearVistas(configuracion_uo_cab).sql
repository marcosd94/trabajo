--Crear de nuevo las mismas Vistas eliminadas anteriormente


CREATE OR REPLACE VIEW seleccion.vw_lista_elegible AS 
 SELECT concurso.nombre AS concurso,
    (((concurso.numero || '/'::text) || concurso.anio) || ' DE '::text) || configuracion_uo_cab.descripcion_corta::text AS field,
    concurso_puesto_agr.descripcion_grupo AS grupo,
    referencias.desc_larga AS estado,
    lista_elegibles_cab.vto_validez_lista AS vencimiento,
    sin_entidad.ent_codigo,
    sin_entidad.nen_codigo,
    sin_entidad.ani_aniopre,
    configuracion_uo_cab.denominacion_unidad AS oee,
    lista_elegibles_cab.id_concurso_puesto_agr,
    configuracion_uo_cab.id_configuracion_uo AS id_oee
   FROM seleccion.lista_elegibles_cab
     JOIN seleccion.concurso_puesto_agr ON lista_elegibles_cab.id_concurso_puesto_agr = concurso_puesto_agr.id_concurso_puesto_agr
     JOIN seleccion.concurso ON concurso_puesto_agr.id_concurso = concurso.id_concurso
     JOIN planificacion.configuracion_uo_cab ON concurso.id_configuracion_uo = configuracion_uo_cab.id_configuracion_uo
     JOIN seleccion.referencias ON concurso_puesto_agr.estado = referencias.valor_num
     JOIN planificacion.entidad entidad ON entidad.id_entidad = configuracion_uo_cab.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON entidad.id_sin_entidad = sin_entidad.id_sin_entidad
  WHERE referencias.dominio::text = 'ESTADOS_GRUPO'::text;

ALTER TABLE seleccion.vw_lista_elegible
  OWNER TO postgres;




 CREATE OR REPLACE VIEW seleccion.vw_eval_ref_postu AS 
 SELECT DISTINCT concurso.nombre,
    grupo.descripcion_grupo AS grupo,
    refe.desc_larga AS estado,
    lista.fecha_venc_elegible AS fecha_venc,
    grupo.id_concurso_puesto_agr AS id_grupo,
    oee.denominacion_unidad AS denominacion,
    sin_entidad.ent_codigo,
    sin_entidad.nen_codigo,
    sin_entidad.ani_aniopre
   FROM seleccion.eval_referencial_postulante eval
     JOIN seleccion.lista lista ON lista.id_concurso_puesto_agr = eval.id_concurso_puesto_agr
     JOIN seleccion.concurso_puesto_agr grupo ON eval.id_concurso_puesto_agr = grupo.id_concurso_puesto_agr
     JOIN seleccion.concurso concurso ON concurso.id_concurso = grupo.id_concurso
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = concurso.id_configuracion_uo
     JOIN seleccion.referencias refe ON refe.valor_num = grupo.estado
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON entidad.id_sin_entidad = sin_entidad.id_sin_entidad
  WHERE eval.lista_corta = true AND eval.seleccionado = false AND refe.dominio::text = 'ESTADOS_GRUPO'::text;

ALTER TABLE seleccion.vw_eval_ref_postu
  OWNER TO postgres;




CREATE OR REPLACE VIEW seleccion.est_movilidad AS 
 SELECT DISTINCT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.denominacion_unidad AS oee,
    e.id_empleado_puesto,
    e.id_persona,
    persona.documento_identidad AS cedula,
    persona.nombres,
    persona.apellidos,
    puesto.descripcion AS puesto,
        CASE
            WHEN e.contratado = true THEN 'CONTRATADO'::text
            ELSE 'PERMANENTE'::text
        END AS tipo_persona,
    persona.sexo,
    "substring"(age(now(), persona.fecha_nacimiento::timestamp with time zone)::text, 1, 2) AS edad,
    e.id_datos_esp_tipo_ingreso_movilidad,
    e.fecha_inicio,
    e.id_planta_cargo_det,
    e.fecha_alta
   FROM general.empleado_puesto e
     JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = e.id_planta_cargo_det
     JOIN general.persona persona ON persona.id_persona = e.id_persona
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = puesto.id_configuracion_uo_det
     JOIN planificacion.configuracion_uo_cab oee ON uo.id_configuracion_uo = oee.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo,
    seleccion.datos_generales dg,
    seleccion.datos_especificos de1,
    seleccion.datos_especificos de2
  WHERE e.id_datos_esp_tipo_ingreso_movilidad = de1.id_datos_especificos AND dg.id_datos_generales = de1.id_datos_generales AND dg.descripcion::text = 'TIPOS DE INGRESOS Y MOVILIDAD'::text AND e.id_datos_esp_tipo_registro = de2.id_datos_especificos AND de2.descripcion::text = 'MOVILIDAD'::text;

ALTER TABLE seleccion.est_movilidad
  OWNER TO postgres;




  CREATE OR REPLACE VIEW seleccion.est_ingresos AS 
 SELECT DISTINCT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.denominacion_unidad AS oee,
    e.id_empleado_puesto,
    e.id_persona,
    persona.documento_identidad AS cedula,
    persona.nombres,
    persona.apellidos,
    puesto.descripcion AS puesto,
        CASE
            WHEN e.contratado = true THEN 'CONTRATADO'::text
            ELSE 'PERMANENTE'::text
        END AS tipo_persona,
    persona.sexo,
    "substring"(age(now(), persona.fecha_nacimiento::timestamp with time zone)::text, 1, 2) AS edad,
    e.id_datos_esp_tipo_ingreso_movilidad,
    e.fecha_inicio,
    e.id_planta_cargo_det,
    e.fecha_alta
   FROM general.empleado_puesto e
     JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = e.id_planta_cargo_det
     JOIN general.persona persona ON persona.id_persona = e.id_persona
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = puesto.id_configuracion_uo_det
     JOIN planificacion.configuracion_uo_cab oee ON uo.id_configuracion_uo = oee.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo,
    seleccion.datos_generales dg,
    seleccion.datos_especificos de1,
    seleccion.datos_especificos de2
  WHERE e.id_datos_esp_tipo_ingreso_movilidad = de1.id_datos_especificos AND dg.id_datos_generales = de1.id_datos_generales AND dg.descripcion::text = 'TIPOS DE INGRESOS Y MOVILIDAD'::text AND e.id_datos_esp_tipo_registro = de2.id_datos_especificos AND de2.descripcion::text = 'INGRESO'::text;

ALTER TABLE seleccion.est_ingresos
  OWNER TO postgres;




  CREATE OR REPLACE VIEW remuneracion.remuneraciones_mod_edad_sexo AS 
 SELECT DISTINCT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.id_configuracion_uo,
    oee.denominacion_unidad AS oee,
    r.id_remuneracion,
    r.id_empleado_puesto,
    e.id_persona,
    persona.documento_identidad AS cedula,
    persona.nombres,
    persona.apellidos,
    puesto.descripcion AS puesto,
        CASE
            WHEN e.contratado = true THEN 'CONTRATADO'::text
            ELSE 'PERMANENTE'::text
        END AS modalidad_ocupacion,
    r.obj_codigo,
    obj.obj_nombre,
    r.presupuestado AS valor_economico,
    r.anho AS anho_remuneracion,
    r.mes AS mes_remuneracion,
    r.fecha_alta,
    persona.sexo,
    "substring"(age(now(), persona.fecha_nacimiento::timestamp with time zone)::text, 1, 2) AS edad
   FROM remuneracion.remuneraciones r
     JOIN general.empleado_puesto e ON e.id_empleado_puesto = r.id_empleado_puesto
     JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = e.id_planta_cargo_det
     JOIN general.persona persona ON persona.id_persona = e.id_persona
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = puesto.id_configuracion_uo_det
     JOIN planificacion.configuracion_uo_cab oee ON uo.id_configuracion_uo = oee.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
     JOIN sinarh.sin_obj obj ON obj.obj_codigo = r.obj_codigo
  WHERE obj.ani_aniopre = (( SELECT max(s.ani_aniopre) AS max
           FROM sinarh.sin_obj s));

ALTER TABLE remuneracion.remuneraciones_mod_edad_sexo
  OWNER TO postgres;




CREATE OR REPLACE VIEW remuneracion.historico_remuneraciones_mod_edad_sexo AS 
 SELECT DISTINCT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.id_configuracion_uo,
    oee.denominacion_unidad AS oee,
    r.id_historico_remuneracion,
    r.id_empleado_puesto,
    e.id_persona,
    persona.documento_identidad AS cedula,
    persona.nombres,
    persona.apellidos,
    puesto.descripcion AS puesto,
        CASE
            WHEN e.contratado = true THEN 'CONTRATADO'::text
            ELSE 'PERMANENTE'::text
        END AS modalidad_ocupacion,
    r.obj_codigo,
    obj.obj_nombre,
    r.presupuestado AS valor_economico,
    r.anho AS anho_remuneracion,
    r.mes AS mes_remuneracion,
    r.fecha_alta,
    persona.sexo,
    "substring"(age(now(), persona.fecha_nacimiento::timestamp with time zone)::text, 1, 2) AS edad
   FROM remuneracion.historico_remuneraciones r
     JOIN general.empleado_puesto e ON e.id_empleado_puesto = r.id_empleado_puesto
     JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = e.id_planta_cargo_det
     JOIN general.persona persona ON persona.id_persona = e.id_persona
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = puesto.id_configuracion_uo_det
     JOIN planificacion.configuracion_uo_cab oee ON uo.id_configuracion_uo = oee.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
     JOIN sinarh.sin_obj obj ON obj.obj_codigo = r.obj_codigo
  WHERE obj.ani_aniopre = (( SELECT max(s.ani_aniopre) AS max
           FROM sinarh.sin_obj s))
  ORDER BY e.id_persona;

ALTER TABLE remuneracion.historico_remuneraciones_mod_edad_sexo
  OWNER TO postgres;




  CREATE OR REPLACE VIEW proceso.bandeja_entrada_csi AS 
 SELECT DISTINCT c.id_concurso,
    c.nombre AS nombre_concurso,
    cuc.id_configuracion_uo,
    cuc.orden AS orden_configuracion,
    cuc.denominacion_unidad AS denominacion_unidad_cab,
    se.nen_codigo,
    se.ent_codigo,
    se.ent_nombre,
    cpa.descripcion_grupo,
    sne.nen_codigo AS nivel_cod,
    sne.nen_nombre AS nivel_nombre,
    a.descripcion AS actividad,
    t.actorid_ AS usuario,
    t.create_ AS fecha_recepcion,
    t.start_ AS fecha_inicio,
    to_char(now() - t.create_::timestamp with time zone, 'DD'::text)::integer AS dias_creacion,
    to_char(now() - t.start_::timestamp with time zone, 'DD'::text)::integer AS dias_inicio,
    proceso.calc_atraso_actividad_proc(t.create_, ap.id_actividad_proceso, c.id_configuracion_uo) AS atraso,
    a.id_actividad,
    e.id_entidad,
    sne.id_sin_nivel_entidad,
    t.id_ AS id_taskinstance,
    cpa.id_concurso_puesto_agr,
    cpa.id_process_instance,
    a.cod_actividad,
    p.id_proceso,
    par.id_actividad_proceso,
    a.tipo AS tipo_actividad,
    e.id_sin_entidad
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
  WHERE t.end_ IS NULL AND c.activo = true AND cpa.activo = true AND dg.activo = true AND de.activo = true AND dg.descripcion::text ~~ '%TIPOS%DE%CONCURSO%'::text AND de.descripcion::text ~~ '%CONCURSO%SIMPLIFICADO%'::text;

ALTER TABLE proceso.bandeja_entrada_csi
  OWNER TO postgres;




  CREATE OR REPLACE VIEW proceso.bandeja_entrada_cio AS 
 SELECT DISTINCT c.id_concurso,
    c.nombre AS nombre_concurso,
    cuc.id_configuracion_uo,
    cuc.orden AS orden_configuracion,
    cuc.denominacion_unidad AS denominacion_unidad_cab,
    se.nen_codigo,
    se.ent_codigo,
    se.ent_nombre,
    cpa.descripcion_grupo,
    sne.nen_codigo AS nivel_cod,
    sne.nen_nombre AS nivel_nombre,
    a.descripcion AS actividad,
    t.actorid_ AS usuario,
    t.create_ AS fecha_recepcion,
    t.start_ AS fecha_inicio,
    to_char(now() - t.create_::timestamp with time zone, 'DD'::text)::integer AS dias_creacion,
    to_char(now() - t.start_::timestamp with time zone, 'DD'::text)::integer AS dias_inicio,
    proceso.calc_atraso_actividad_proc(t.create_, ap.id_actividad_proceso, c.id_configuracion_uo) AS atraso,
    a.id_actividad,
    e.id_entidad,
    sne.id_sin_nivel_entidad,
    t.id_ AS id_taskinstance,
    cpa.id_concurso_puesto_agr,
    cpa.id_process_instance,
    a.cod_actividad,
    p.id_proceso,
    par.id_actividad_proceso,
    a.tipo AS tipo_actividad,
    e.id_sin_entidad
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
  WHERE t.end_ IS NULL AND c.activo = true AND cpa.activo = true AND dg.activo = true AND de.activo = true AND dg.descripcion::text ~~ '%TIPOS%DE%CONCURSO%'::text AND (de.descripcion::text ~~ '%CONCURSO%INTERNO%DE%OPOSICION%INSTITUCIONAL%'::text OR de.descripcion::text ~~ '%CONCURSO%INTERNO%DE%OPOSICION%INTER%INSTITUCIONAL%'::text OR de.descripcion::text ~~ '%CONCURSO%INTERNO%DE%PROMOCION%SALARIAL%'::text);

ALTER TABLE proceso.bandeja_entrada_cio
  OWNER TO postgres;




  CREATE OR REPLACE VIEW proceso.bandeja_entrada AS 
 SELECT DISTINCT c.id_concurso,
    c.nombre AS nombre_concurso,
    cuc.id_configuracion_uo,
    cuc.orden AS orden_configuracion,
    cuc.denominacion_unidad AS denominacion_unidad_cab,
    se.nen_codigo,
    se.ent_codigo,
    se.ent_nombre,
    cpa.descripcion_grupo,
    sne.nen_codigo AS nivel_cod,
    sne.nen_nombre AS nivel_nombre,
    a.descripcion AS actividad,
    t.actorid_ AS usuario,
    t.create_ AS fecha_recepcion,
    t.start_ AS fecha_inicio,
    to_char(now() - t.create_::timestamp with time zone, 'DD'::text)::integer AS dias_creacion,
    to_char(now() - t.start_::timestamp with time zone, 'DD'::text)::integer AS dias_inicio,
    proceso.calc_atraso_actividad_proc(t.create_, ap.id_actividad_proceso, c.id_configuracion_uo) AS atraso,
    a.id_actividad,
    e.id_entidad,
    sne.id_sin_nivel_entidad,
    t.id_ AS id_taskinstance,
    cpa.id_concurso_puesto_agr,
    cpa.id_process_instance,
    a.cod_actividad,
    p.id_proceso,
    par.id_actividad_proceso,
    a.tipo AS tipo_actividad,
    e.id_sin_entidad,
    cpa.carpeta
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
     JOIN seleccion.comision_grupo cg ON cg.id_concurso_puesto_agr = cpa.id_concurso_puesto_agr
  WHERE t.end_ IS NULL AND c.activo = true AND cpa.activo = true AND dg.activo = true AND de.activo = true AND cpa.estado <> 38 AND dg.descripcion::text ~~ '%TIPOS%DE%CONCURSO%'::text AND (de.descripcion::text ~~ '%CONCURSO%PUBLICO%DE%OPOSICION%'::text OR de.descripcion::text ~~ '%CONCURSO%DE%MERITOS%'::text) AND p.descripcion::text ~~ '%CONCURSO%'::text;

ALTER TABLE proceso.bandeja_entrada
  OWNER TO postgres;




CREATE OR REPLACE VIEW planificacion.vw_entidad_oee AS 
 SELECT e.id_entidad AS id_entidad_oee,
    e.id_sin_entidad,
    n.id_sin_nivel_entidad,
    e.nen_codigo AS codigo_nivel,
    n.nen_nombre AS nivel,
    e.ent_codigo,
    (n.nen_codigo || '.'::text) || e.ent_codigo AS codigo_entidad,
    s.ent_nombre AS entidad,
    oee.id_configuracion_uo,
    (((n.nen_codigo || '.'::text) || e.ent_codigo) || '.'::text) || oee.orden AS codigo_oee,
    oee.denominacion_unidad,
    oee.orden
   FROM planificacion.entidad e
     JOIN sinarh.sin_entidad s ON e.id_sin_entidad = s.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad n ON n.nen_codigo = s.nen_codigo
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = e.id_configuracion_uo
  WHERE oee.activo = true
  ORDER BY oee.id_configuracion_uo;

ALTER TABLE planificacion.vw_entidad_oee
  OWNER TO postgres;




  CREATE OR REPLACE VIEW planificacion.puestos_estructura_organizacional AS 
 SELECT DISTINCT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.denominacion_unidad AS oee,
    uo.denominacion_unidad AS unidad_organizativa,
    ((((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden) || desvinculacion.getdependientes(uo.id_configuracion_uo_det)::text AS uo_codigo,
    p.id_planta_cargo_det,
    p.descripcion,
    p.id_configuracion_uo_det,
    p.id_estado_cab,
    p.jefatura,
    e.descripcion AS estado,
        CASE
            WHEN p.contratado = true THEN 'CONTRATADO'::text
            ELSE 'PERMANENTE'::text
        END AS modalidad
   FROM planificacion.planta_cargo_det p
     JOIN planificacion.estado_cab e ON e.id_estado_cab = p.id_estado_cab
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = p.id_configuracion_uo_det
     JOIN planificacion.configuracion_uo_cab oee ON uo.id_configuracion_uo = oee.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo;

ALTER TABLE planificacion.puestos_estructura_organizacional
  OWNER TO postgres;




  CREATE OR REPLACE VIEW movilidad.cabecera_historico_movilidad AS 
 SELECT DISTINCT st.id_solicitud_traslado_cab,
    st.id_oee_origen AS idoee,
    st.fecha_alta,
    st.usu_alta,
    origen.denominacion_unidad AS oee_origen,
    origen.orden AS orden_origen,
    de_t.descripcion AS tipo_traslado,
    de_e.descripcion AS estado,
    se.nen_codigo,
    se.ent_codigo,
    se.ent_nombre,
    sne.nen_codigo AS nivel_cod,
    sne.nen_nombre AS nivel_nombre,
    (prs.nombres::text || ' '::text) || prs.apellidos::text AS funcionario
   FROM movilidad.solicitud_traslado_cab st
     JOIN general.persona prs ON prs.id_persona = st.id_persona
     LEFT JOIN planificacion.configuracion_uo_cab origen ON origen.id_configuracion_uo = st.id_oee_origen
     JOIN seleccion.datos_especificos de_t ON de_t.id_datos_especificos = st.id_datos_esp_tipo_traslado
     JOIN seleccion.datos_especificos de_e ON de_e.id_datos_especificos = st.id_datos_esp_estado_solicitud
     JOIN planificacion.entidad e ON origen.id_entidad = e.id_entidad
     JOIN sinarh.sin_entidad se ON e.id_sin_entidad = se.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sne.ani_aniopre = se.ani_aniopre AND sne.nen_codigo = se.nen_codigo;

ALTER TABLE movilidad.cabecera_historico_movilidad
  OWNER TO postgres;




  CREATE OR REPLACE VIEW movilidad.bandeja_traslado AS 
 SELECT DISTINCT st.id_solicitud_traslado_cab,
    cuc_o.id_configuracion_uo AS id_oee_origen,
    cuc_o.orden AS orden_origen,
    cuc_d.id_configuracion_uo AS id_oee_destino,
    cuc_d.orden AS orden_destino,
    cuc_o.denominacion_unidad AS oee_origen,
    cuc_d.denominacion_unidad AS oee_destino,
    de_t.descripcion AS tipo_traslado,
    de_e.descripcion AS estado,
    se.nen_codigo,
    se.ent_codigo,
    se.ent_nombre,
    sne.nen_codigo AS nivel_cod,
    sne.nen_nombre AS nivel_nombre,
    a.descripcion AS actividad,
    t.actorid_ AS usuario,
    t.create_ AS fecha_recepcion,
    t.start_ AS fecha_inicio,
    to_char(now() - t.create_::timestamp with time zone, 'DD'::text)::integer AS dias_creacion,
    to_char(now() - t.start_::timestamp with time zone, 'DD'::text)::integer AS dias_inicio,
    proceso.calc_atraso_actividad_proc(t.create_, ap.id_actividad_proceso, cuc_d.id_configuracion_uo) AS atraso,
    a.id_actividad,
    e.id_entidad,
    sne.id_sin_nivel_entidad,
    t.id_ AS id_taskinstance,
    st.id_process_instance,
    a.cod_actividad,
    p.id_proceso,
    par.id_actividad_proceso,
    a.tipo AS tipo_actividad,
    e.id_sin_entidad,
    st.id_persona,
    persona.nombres,
    persona.apellidos
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




  CREATE OR REPLACE VIEW juridicos.personas_sumariadas AS 
 SELECT DISTINCT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.denominacion_unidad AS oee,
    persona.documento_identidad,
    (persona.nombres::text || ' '::text) || persona.apellidos::text AS funcionario,
    persona.sexo,
    "substring"(age(now(), persona.fecha_nacimiento::timestamp with time zone)::text, 1, 2) AS edad,
    sc.fecha_alta AS fecha,
        CASE
            WHEN sc.estado::text = 'EC'::text THEN 'EN CURSO'::text
            WHEN sc.estado::text = 'RJ'::text THEN 'CON RESOLUCION DEL JUEZ'::text
            WHEN sc.estado::text = 'SO'::text THEN 'SOBRESEIMIENTO'::text
            WHEN sc.estado::text = 'SA'::text THEN 'SANCION'::text
            WHEN sc.estado::text = 'AR'::text THEN 'ARCHIVO'::text
            ELSE NULL::text
        END AS estado_sumario,
        CASE
            WHEN sc.estado_juez::text = 'SO'::text THEN 'SOBRESEIMIENTO'::text
            WHEN sc.estado_juez::text = 'SA'::text THEN ((((('SANCION'::text || ' - '::text) || s.descripcion::text) || ' - '::text) || sc.tiempo_j) || ' '::text) ||
            CASE
                WHEN sc.expresado_j::text = 'D'::text THEN 'DÍA/S'::text
                WHEN sc.expresado_j::text = 'M'::text THEN 'MES/ES'::text
                WHEN sc.expresado_j::text = 'A'::text THEN 'AÑO/S'::text
                ELSE NULL::text
            END
            WHEN sc.estado_juez::text = 'AR'::text THEN 'ARCHIVO'::text
            ELSE NULL::text
        END AS recomendacion_juez,
        CASE
            WHEN sc.estado::text = 'SO'::text THEN 'SOBRESEIMIENTO - MAI'::text
            WHEN sc.estado::text = 'SA'::text THEN (('SANCION - MAI - '::text || sd.descripcion::text) || ' '::text) ||
            CASE
                WHEN sd.inhabilita = true THEN ((sc.tiempo_m || ' '::text) ||
                CASE
                    WHEN sc.expresado_m::text = 'D'::text THEN 'DÍA/S'::text
                    WHEN sc.expresado_m::text = 'M'::text THEN 'MES/ES'::text
                    WHEN sc.expresado_m::text = 'A'::text THEN 'AÑO/S'::text
                    ELSE NULL::text
                END)::character varying
                ELSE sd.descripcion
            END::text
            WHEN sc.estado::text = 'AR'::text THEN 'ARCHIVO - MAI'::text
            ELSE NULL::text
        END AS recomendacion_mai,
    juridicos.fnc_get_sumarios(sc.id_sumario_cab) AS faltas_cometidas,
    sc.id_sumario_cab,
    persona.id_persona
   FROM juridicos.sumario_cab sc
     LEFT JOIN juridicos.sancion s ON s.id_sancion = sc.id_sancion_j
     LEFT JOIN juridicos.sancion sd ON sd.id_sancion = sc.id_sancion_m
     JOIN general.persona ON sc.id_persona = persona.id_persona
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = sc.id_configuracion_uo
     JOIN juridicos.sumario_det sumd ON sumd.id_sumario_cab = sc.id_sumario_cab
     JOIN seleccion.datos_especificos de ON de.id_datos_especificos = sumd.id_datos_especif_falta
     JOIN general.pais ON pais.id_pais = persona.id_pais_expedicion_doc
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo;

ALTER TABLE juridicos.personas_sumariadas
  OWNER TO postgres;




  CREATE OR REPLACE VIEW juridicos.personas_accion_inconstitucionalidad AS 
 SELECT DISTINCT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.id_configuracion_uo,
    oee.denominacion_unidad AS oee,
    persona.documento_identidad,
    (persona.nombres::text || ' '::text) || persona.apellidos::text AS persona,
    persona.sexo,
    "substring"(age(now(), persona.fecha_nacimiento::timestamp with time zone)::text, 1, 2) AS edad,
        CASE
            WHEN ac.estado::text = 'P'::text THEN 'PENDIENTE CON MEDIDA CAUTELAR'::text
            WHEN ac.estado::text = 'A'::text THEN 'ACUERDO Y SENTENCIA'::text
            ELSE NULL::text
        END AS estado,
    ac.fecha_alta,
        CASE
            WHEN de.descripcion IS NULL THEN det.otra_ley
            ELSE de.descripcion
        END AS normativas,
        CASE
            WHEN det.art_todos = true THEN 'Todos'::text
            ELSE 'Articulos: '::text || det.art_especif::text
        END AS articulos,
    ac.id_accion_cab,
    ac.id_persona
   FROM juridicos.accion_inconst_cab ac
     LEFT JOIN juridicos.accion_inconst_det det ON det.id_accion_cab = ac.id_accion_cab
     LEFT JOIN seleccion.datos_especificos de ON de.id_datos_especificos = det.id_datos_especif_ley
     JOIN general.persona ON ac.id_persona = persona.id_persona
     JOIN general.empleado_puesto emp ON emp.id_persona = ac.id_persona
     JOIN planificacion.planta_cargo_det p ON p.id_planta_cargo_det = emp.id_planta_cargo_det
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = p.id_configuracion_uo_det
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = uo.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
  ORDER BY ac.id_accion_cab;

ALTER TABLE juridicos.personas_accion_inconstitucionalidad
  OWNER TO postgres;




  CREATE OR REPLACE VIEW general.funcionarios_activos AS 
 SELECT DISTINCT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.denominacion_unidad AS oee,
    uo.denominacion_unidad AS unidad_organizativa,
    ((((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden) || desvinculacion.getdependientes(uo.id_configuracion_uo_det)::text AS uo_codigo,
    planta_cargo_det.descripcion AS puesto,
    empleado_puesto.fecha_inicio,
    datos_especificos.descripcion AS estado,
    persona.sexo,
    "substring"(age(now(), persona.fecha_nacimiento::timestamp with time zone)::text, 1, 2) AS edad,
    empleado_puesto.id_persona,
    empleado_puesto.id_empleado_puesto,
    oee.id_configuracion_uo,
    uo.id_configuracion_uo_det
   FROM general.empleado_puesto
     JOIN general.persona persona ON empleado_puesto.id_persona = persona.id_persona
     JOIN planificacion.planta_cargo_det ON empleado_puesto.id_planta_cargo_det = planta_cargo_det.id_planta_cargo_det
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = planta_cargo_det.id_configuracion_uo_det
     JOIN planificacion.configuracion_uo_cab oee ON uo.id_configuracion_uo = oee.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
     JOIN seleccion.datos_especificos ON empleado_puesto.id_datos_esp_estado = datos_especificos.id_datos_especificos
     LEFT JOIN seleccion.datos_especificos t ON empleado_puesto.id_datos_esp_tipo_ingreso_movilidad = datos_especificos.id_datos_especificos
  WHERE empleado_puesto.activo = true;

ALTER TABLE general.funcionarios_activos
  OWNER TO postgres;




  CREATE OR REPLACE VIEW general.empleado_puesto_651 AS 
 SELECT configuracion_uo_cab.denominacion_unidad AS oee,
    configuracion_uo_det.denominacion_unidad AS uo,
    planta_cargo_det.descripcion AS puesto,
    empleado_puesto.fecha_inicio,
    empleado_puesto.fecha_fin,
    datos_especificos.descripcion AS estado,
    empleado_puesto.id_persona,
    empleado_puesto.id_empleado_puesto AS id,
    t.descripcion::text AS tipo
   FROM general.empleado_puesto
     JOIN planificacion.planta_cargo_det ON empleado_puesto.id_planta_cargo_det = planta_cargo_det.id_planta_cargo_det
     JOIN planificacion.configuracion_uo_det ON planta_cargo_det.id_configuracion_uo_det = configuracion_uo_det.id_configuracion_uo_det
     JOIN planificacion.configuracion_uo_cab ON configuracion_uo_det.id_configuracion_uo = configuracion_uo_cab.id_configuracion_uo
     JOIN seleccion.datos_especificos ON empleado_puesto.id_datos_esp_estado = datos_especificos.id_datos_especificos
     LEFT JOIN seleccion.datos_especificos t ON empleado_puesto.id_datos_esp_tipo_ingreso_movilidad = t.id_datos_especificos
UNION
 SELECT configuracion_uo_cab.denominacion_unidad AS oee,
    configuracion_uo_det.denominacion_unidad AS uo,
    planta_cargo_det.descripcion AS puesto,
    empleado_puesto.fecha_inicio,
    empleado_puesto.fecha_fin,
    datos_especificos.descripcion AS estado,
    empleado_puesto.id_persona,
    empleado_puesto.id_empleado_puesto AS id,
    t.descripcion AS tipo
   FROM general.empleado_puesto
     JOIN planificacion.planta_cargo_det ON empleado_puesto.id_planta_cargo_det = planta_cargo_det.id_planta_cargo_det
     JOIN planificacion.configuracion_uo_det ON planta_cargo_det.id_configuracion_uo_det = configuracion_uo_det.id_configuracion_uo_det
     JOIN planificacion.configuracion_uo_cab ON configuracion_uo_det.id_configuracion_uo = configuracion_uo_cab.id_configuracion_uo
     JOIN seleccion.datos_especificos ON empleado_puesto.id_datos_esp_estado = datos_especificos.id_datos_especificos
     LEFT JOIN seleccion.datos_especificos t ON empleado_puesto.id_datos_esp_tipo_ingreso_movilidad = t.id_datos_especificos
  WHERE empleado_puesto.activo = true
UNION
 SELECT empleado_puesto.desc_oee_historico AS oee,
    empleado_puesto.desc_uo_historico AS uo,
    empleado_puesto.desc_puesto_historico AS puesto,
    empleado_puesto.fecha_inicio,
    empleado_puesto.fecha_fin,
    datos_especificos.descripcion AS estado,
    empleado_puesto.id_persona,
    empleado_puesto.id_empleado_puesto AS id,
    t.descripcion AS tipo
   FROM general.empleado_puesto
     LEFT JOIN seleccion.datos_especificos ON empleado_puesto.id_datos_esp_estado = datos_especificos.id_datos_especificos
     LEFT JOIN seleccion.datos_especificos t ON empleado_puesto.id_datos_esp_tipo_ingreso_movilidad = t.id_datos_especificos
  WHERE empleado_puesto.id_planta_cargo_det IS NULL AND empleado_puesto.activo = true
  ORDER BY 4;

ALTER TABLE general.empleado_puesto_651
  OWNER TO postgres;




  CREATE OR REPLACE VIEW eval_desempeno.planes_mejora_rpt AS 
 SELECT DISTINCT oee.denominacion_unidad AS oee,
    sne.nen_codigo AS nen_cod,
    sne.nen_nombre AS nen_nom,
    (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod,
    sin_entidad.ent_nombre,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden,
    e.titulo,
    e.fecha_eval_desde,
    e.fecha_eval_hasta,
    ge.grupo,
    de.descripcion,
    pm.descripcion AS plan_mejora,
    count(pm.descripcion) AS cantidad,
    gm.id_grupo_metodologia,
    e.id_evaluacion_desempeno,
    oee.id_configuracion_uo,
    rp.id_plan_mejora
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
 SELECT DISTINCT oee.denominacion_unidad AS oee,
    sne.nen_codigo AS nen_cod,
    sne.nen_nombre AS nen_nom,
    (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod,
    sin_entidad.ent_nombre,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden,
    e.titulo,
    e.fecha_eval_desde,
    e.fecha_eval_hasta,
    ge.grupo,
    de.descripcion,
    pm.descripcion AS plan_mejora,
    count(pm.descripcion) AS cantidad,
    gm.id_grupo_metodologia,
    e.id_evaluacion_desempeno,
    oee.id_configuracion_uo,
    r.id_plan_mejora
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

ALTER TABLE eval_desempeno.planes_mejora_rpt
  OWNER TO postgres;




CREATE OR REPLACE VIEW eval_desempeno.historico_evaluacion AS 
 SELECT DISTINCT act.id_actividad,
    e.id_evaluacion_desempeno AS id_evaluacion,
    oee.denominacion_unidad AS oee,
    e.titulo,
    e.usu_alta AS usuario_alta,
    e.fecha_alta,
    ref.desc_larga AS estado,
    act.cod_actividad,
    act.descripcion AS actividad,
    task.create_ AS fecha_creacion,
    task.actorid_ AS usuario_inicio,
    task.start_ AS fecha_inicio,
    task.userend_ AS usuario_fin,
    task.end_ AS fecha_fin
   FROM eval_desempeno.evaluacion_desempeno e
     JOIN jbpm.jbpm_taskinstance task ON task.procinst_ = e.id_process_instance
     JOIN proceso.actividad act ON act.cod_actividad::text = task.name_::text
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = e.id_configuracion_uo_cab
     JOIN seleccion.referencias ref ON e.estado = ref.valor_num
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text
  ORDER BY task.create_;

ALTER TABLE eval_desempeno.historico_evaluacion
  OWNER TO postgres;




  CREATE OR REPLACE VIEW eval_desempeno.funcionarios_evaluados AS 
 SELECT DISTINCT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.denominacion_unidad AS oee,
    uo.denominacion_unidad,
    ((((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden) || desvinculacion.getdependientes(uo.id_configuracion_uo_det)::text AS uo_codigo,
    upper(e.titulo::text) AS evaluacion,
    gm.id_datos_especif_metod,
    de.descripcion AS tipo_evaluacion,
    e.fecha_eval_desde,
    e.fecha_eval_hasta,
    (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario,
    per.documento_identidad,
    puesto.descripcion AS puesto,
        CASE
            WHEN emp.contratado = true THEN 'CONTRATADO'::text
            ELSE 'PERMANENTE'::text
        END AS modalidad_ocupacion,
    per.sexo,
    "substring"(age(now(), per.fecha_nacimiento::timestamp with time zone)::text, 1, 2) AS edad,
    s.id_empleado_puesto,
    emp.id_persona,
    oee.id_configuracion_uo,
    e.id_evaluacion_desempeno,
    gs.puntaje_final,
    e.fecha_alta
   FROM eval_desempeno.plantilla_eval_conf_det p
     JOIN eval_desempeno.grupo_metodologia gm ON gm.id_grupo_metodologia = p.id_grupo_metodologia
     JOIN seleccion.datos_especificos de ON de.id_datos_especificos = gm.id_datos_especif_metod
     JOIN eval_desempeno.resultado_eval r ON r.id_plantilla_eval_conf_det = p.id_plantilla_eval_conf_det
     JOIN eval_desempeno.grupos_evaluacion ge ON ge.id_grupos_evaluacion = gm.id_grupos_evaluacion
     JOIN eval_desempeno.grupos_sujetos gs ON gs.id_grupos_evaluacion = ge.id_grupos_evaluacion
     JOIN eval_desempeno.sujetos s ON s.id_sujetos = gs.id_sujetos
     JOIN general.empleado_puesto emp ON emp.id_empleado_puesto = s.id_empleado_puesto
     JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = emp.id_planta_cargo_det
     JOIN general.persona per ON per.id_persona = emp.id_persona
     JOIN eval_desempeno.evaluacion_desempeno e ON e.id_evaluacion_desempeno = ge.id_evaluacion_desempeno
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = e.id_configuracion_uo_cab
     JOIN planificacion.configuracion_uo_cab oee ON uo.id_configuracion_uo = oee.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
     JOIN seleccion.referencias ref ON ref.valor_num = e.estado
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text AND gs.presente = true
UNION
 SELECT DISTINCT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.denominacion_unidad AS oee,
    uo.denominacion_unidad,
    ((((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden) || desvinculacion.getdependientes(uo.id_configuracion_uo_det)::text AS uo_codigo,
    e.titulo AS evaluacion,
    gm.id_datos_especif_metod,
    de.descripcion AS tipo_evaluacion,
    e.fecha_eval_desde,
    e.fecha_eval_hasta,
    (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario,
    per.documento_identidad,
    puesto.descripcion AS puesto,
        CASE
            WHEN emp.contratado = true THEN 'CONTRATADO'::text
            ELSE 'PERMANENTE'::text
        END AS modalidad_ocupacion,
    per.sexo,
    "substring"(age(now(), per.fecha_nacimiento::timestamp with time zone)::text, 1, 2) AS edad,
    s.id_empleado_puesto,
    emp.id_persona,
    oee.id_configuracion_uo,
    e.id_evaluacion_desempeno,
    gs.puntaje_final,
    e.fecha_alta
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
     JOIN general.persona per ON per.id_persona = emp.id_persona
     JOIN eval_desempeno.evaluacion_desempeno e ON e.id_evaluacion_desempeno = ge.id_evaluacion_desempeno
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = e.id_configuracion_uo_cab
     JOIN planificacion.configuracion_uo_cab oee ON uo.id_configuracion_uo = oee.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
     JOIN seleccion.referencias ref ON ref.valor_num = e.estado
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text AND gs.presente = true;

ALTER TABLE eval_desempeno.funcionarios_evaluados
  OWNER TO postgres;




  CREATE OR REPLACE VIEW eval_desempeno.evaluaciones_funcionario3 AS 
 SELECT DISTINCT oee.denominacion_unidad AS oee,
    sne.nen_codigo AS nen_cod,
    sne.nen_nombre AS nen_nom,
    (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod,
    sin_entidad.ent_nombre,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo,
    e.titulo,
    ge.grupo,
    e.fecha_eval_desde,
    e.fecha_eval_hasta,
    (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario,
    per.documento_identidad,
    puesto.descripcion AS puesto,
    uo_puesto.denominacion_unidad AS area,
    gs.puntaje_final AS puntaje,
    gs.observacion,
    s.id_empleado_puesto,
    emp.id_persona,
    oee.id_configuracion_uo,
    gs.id_documento,
    e.id_evaluacion_desempeno,
    gs.id_sujetos,
    ge.id_grupos_evaluacion,
    gs.puntaje_pdec,
    gs.puntaje_percepcion
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
 SELECT DISTINCT oee.denominacion_unidad AS oee,
    sne.nen_codigo AS nen_cod,
    sne.nen_nombre AS nen_nom,
    (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod,
    sin_entidad.ent_nombre,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo,
    e.titulo,
    ge.grupo,
    e.fecha_eval_desde,
    e.fecha_eval_hasta,
    (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario,
    per.documento_identidad,
    puesto.descripcion AS puesto,
    uo_puesto.denominacion_unidad AS area,
    gs.puntaje_final AS puntaje,
    gs.observacion,
    s.id_empleado_puesto,
    emp.id_persona,
    oee.id_configuracion_uo,
    gs.id_documento,
    e.id_evaluacion_desempeno,
    gs.id_sujetos,
    ge.id_grupos_evaluacion,
    gs.puntaje_pdec,
    gs.puntaje_percepcion
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
 SELECT DISTINCT oee.denominacion_unidad AS oee,
    sne.nen_codigo AS nen_cod,
    sne.nen_nombre AS nen_nom,
    (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod,
    sin_entidad.ent_nombre,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo,
    e.titulo,
    'no disponible'::text AS grupo,
    e.fecha_eval_desde,
    e.fecha_eval_hasta,
    (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario,
    per.documento_identidad,
    puesto.descripcion AS puesto,
    uo_puesto.denominacion_unidad AS area,
    gs.puntaje_final AS puntaje,
    gs.observacion,
    s.id_empleado_puesto,
    emp.id_persona,
    oee.id_configuracion_uo,
    gs.id_documento,
    e.id_evaluacion_desempeno,
    gs.id_sujetos,
    0 AS id_grupos_evaluacion,
    gs.puntaje_pdec,
    gs.puntaje_percepcion
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




CREATE OR REPLACE VIEW eval_desempeno.evaluaciones_funcionario2 AS 
 SELECT DISTINCT oee.denominacion_unidad AS oee,
    sne.nen_codigo AS nen_cod,
    sne.nen_nombre AS nen_nom,
    (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod,
    sin_entidad.ent_nombre,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo,
    e.titulo,
    ge.grupo,
    de.descripcion AS plantilla,
    p.resultado_esperado AS criterio,
    e.fecha_eval_desde,
    e.fecha_eval_hasta,
    (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario,
    per.documento_identidad,
    puesto.descripcion AS puesto,
    uo_puesto.denominacion_unidad AS area,
        CASE
            WHEN eval_desempeno.fnc_plan_mejora_rpt_hist(r.id_resultado_eval, gs.id_grupo_sujeto) IS NOT NULL THEN eval_desempeno.fnc_plan_mejora_rpt_hist(r.id_resultado_eval, gs.id_grupo_sujeto)
            ELSE 'No tiene'::text
        END AS plan_mejora,
    gs.puntaje_final AS puntaje,
    gs.puntaje_pdec AS pdec,
    gs.puntaje_percepcion AS percepcion,
    gs.observacion,
    s.id_empleado_puesto,
    emp.id_persona,
    oee.id_configuracion_uo,
    gs.id_documento,
    e.id_evaluacion_desempeno,
    gm.id_grupo_metodologia
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
 SELECT DISTINCT oee.denominacion_unidad AS oee,
    sne.nen_codigo AS nen_cod,
    sne.nen_nombre AS nen_nom,
    (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod,
    sin_entidad.ent_nombre,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS uo_codigo,
    e.titulo,
    ge.grupo,
    de.descripcion AS plantilla,
    cab.descripcion AS criterio,
    e.fecha_eval_desde,
    e.fecha_eval_hasta,
    (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario,
    per.documento_identidad,
    puesto.descripcion AS puesto,
    uo_puesto.denominacion_unidad AS area,
        CASE
            WHEN eval_desempeno.fnc_plan_mejora_pdec(cab.id_plantilla_eval_pdec_cab) IS NOT NULL THEN eval_desempeno.fnc_plan_mejora_pdec(cab.id_plantilla_eval_pdec_cab)
            ELSE 'No tiene'::text
        END AS plan_mejora,
    gs.puntaje_final AS puntaje,
    gs.puntaje_pdec AS pdec,
    gs.puntaje_percepcion AS percepcion,
    gs.observacion,
    s.id_empleado_puesto,
    emp.id_persona,
    oee.id_configuracion_uo,
    gs.id_documento,
    e.id_evaluacion_desempeno,
    gm.id_grupo_metodologia
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
  ORDER BY 27, 10, 11 DESC;

ALTER TABLE eval_desempeno.evaluaciones_funcionario2
  OWNER TO postgres;




  CREATE OR REPLACE VIEW eval_desempeno.evaluaciones_funcionario AS 
 SELECT DISTINCT oee.denominacion_unidad AS oee,
    sne.nen_codigo AS nen_cod,
    sne.nen_nombre AS nen_nom,
    (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod,
    sin_entidad.ent_nombre,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden,
    uo.denominacion_unidad,
    ((((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden) || desvinculacion.getdependientes(uo.id_configuracion_uo_det)::text AS uo_codigo,
    e.titulo,
    ge.grupo,
    de.descripcion AS plantilla,
    e.fecha_eval_desde,
    e.fecha_eval_hasta,
    (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario,
    per.documento_identidad,
    puesto.descripcion AS puesto,
    uo_puesto.denominacion_unidad AS area,
    eval_desempeno.fnc_plan_mejora_rpt(gs.id_grupo_sujeto) AS plan_mejora,
    gs.puntaje_final AS puntaje,
    gs.observacion,
    s.id_empleado_puesto,
    emp.id_persona,
    oee.id_configuracion_uo,
    gs.id_documento,
    e.id_evaluacion_desempeno,
    gm.id_grupo_metodologia
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
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = e.id_configuracion_uo_cab
     JOIN planificacion.configuracion_uo_cab oee ON uo.id_configuracion_uo = oee.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
     JOIN seleccion.referencias ref ON ref.valor_num = e.estado
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text AND gs.presente = true
UNION
 SELECT DISTINCT oee.denominacion_unidad AS oee,
    sne.nen_codigo AS nen_cod,
    sne.nen_nombre AS nen_nom,
    (sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo AS ent_cod,
    sin_entidad.ent_nombre,
    (((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden AS orden,
    uo.denominacion_unidad,
    ((((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden) || desvinculacion.getdependientes(uo.id_configuracion_uo_det)::text AS uo_codigo,
    e.titulo,
    ge.grupo,
    de.descripcion AS plantilla,
    e.fecha_eval_desde,
    e.fecha_eval_hasta,
    (per.nombres::text || ' '::text) || per.apellidos::text AS funcionario,
    per.documento_identidad,
    puesto.descripcion AS puesto,
    uo_puesto.denominacion_unidad AS area,
    eval_desempeno.fnc_plan_mejora_rpt(gs.id_grupo_sujeto) AS plan_mejora,
    gs.puntaje_final AS puntaje,
    gs.observacion,
    s.id_empleado_puesto,
    emp.id_persona,
    oee.id_configuracion_uo,
    gs.id_documento,
    e.id_evaluacion_desempeno,
    gm.id_grupo_metodologia
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
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = e.id_configuracion_uo_cab
     JOIN planificacion.configuracion_uo_cab oee ON uo.id_configuracion_uo = oee.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
     JOIN seleccion.referencias ref ON ref.valor_num = e.estado
  WHERE ref.dominio::text = 'ESTADOS_EVALUACION_DESEMPENO'::text AND gs.presente = true;

ALTER TABLE eval_desempeno.evaluaciones_funcionario
  OWNER TO postgres;




  CREATE OR REPLACE VIEW movilidad.vw_evaluacion_funcionario AS 
 SELECT evaluaciones_funcionario.titulo AS item,
    (evaluaciones_funcionario.fecha_eval_desde || '/'::text) || evaluaciones_funcionario.fecha_eval_hasta AS fechas,
    NULL::text AS carga_horaria,
    evaluaciones_funcionario.puntaje || ''::text AS puntaje,
    'EVALUACIONES'::text AS origen,
    evaluaciones_funcionario.id_persona,
    evaluaciones_funcionario.id_evaluacion_desempeno,
    NULL::text AS id_estudios_realizados,
    NULL::bigint AS id_capacitacion,
    evaluaciones_funcionario.id_documento AS documento
   FROM eval_desempeno.evaluaciones_funcionario
UNION
 SELECT capacitaciones_persona.curso AS item,
    NULL::text AS fechas,
    capacitaciones_persona.carga_horaria || ''::text AS carga_horaria,
    NULL::text AS puntaje,
    capacitaciones_persona.origen,
    capacitaciones_persona.id_persona,
    NULL::bigint AS id_evaluacion_desempeno,
    capacitaciones_persona.id_estudios_realizados,
    capacitaciones_persona.id_capacitacion,
    capacitaciones_persona.documento
   FROM movilidad.capacitaciones_persona;

ALTER TABLE movilidad.vw_evaluacion_funcionario
  OWNER TO postgres;




CREATE OR REPLACE VIEW eval_desempeno.bandeja_evaluacion AS 
 SELECT DISTINCT ed.id_evaluacion_desempeno,
    ed.activo,
    ed.titulo AS titulo_eval,
    cuc.id_configuracion_uo,
    cuc.orden AS orden_configuracion,
    cuc.denominacion_unidad AS denominacion_unidad_cab,
    se.nen_codigo,
    se.ent_codigo,
    se.ent_nombre,
    sne.nen_codigo AS nivel_cod,
    sne.nen_nombre AS nivel_nombre,
    a.descripcion AS actividad,
    t.actorid_ AS usuario,
    t.create_ AS fecha_recepcion,
    t.start_ AS fecha_inicio,
    to_char(now() - t.create_::timestamp with time zone, 'DD'::text)::integer AS dias_creacion,
    to_char(now() - t.start_::timestamp with time zone, 'DD'::text)::integer AS dias_inicio,
    proceso.calc_atraso_actividad_proc(t.create_, ap.id_actividad_proceso, cuc.id_configuracion_uo) AS atraso,
    a.id_actividad,
    e.id_entidad,
    sne.id_sin_nivel_entidad,
    t.id_ AS id_taskinstance,
    ed.id_process_instance,
    a.cod_actividad,
    p.id_proceso,
    par.id_actividad_proceso,
    a.tipo AS tipo_actividad,
    e.id_sin_entidad,
    ref.desc_abrev AS estado_eval
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

ALTER TABLE eval_desempeno.bandeja_evaluacion
  OWNER TO postgres;




  CREATE OR REPLACE VIEW desvinculacion.personas_inhabilitadas AS 
 SELECT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.denominacion_unidad AS oee,
    inh.id_inhabilitado,
    inh.id_persona,
    persona.documento_identidad,
    persona.nombres,
    persona.apellidos,
    persona.sexo,
    inh.fecha_desde,
    inh.fecha_hasta,
        CASE
            WHEN inh.tipo::text = 'D'::text THEN 'D'::text
            ELSE 'O'::text
        END AS id_motivo,
        CASE
            WHEN inh.tipo::text = 'D'::text THEN 'DESVINCULACION'::text
            ELSE 'PROCESOS JURIDICOS'::text
        END AS motivo,
    e.id_empleado_puesto,
    e.id_planta_cargo_det,
    inh.fecha_alta
   FROM desvinculacion.inhabilitados inh
     JOIN general.persona ON inh.id_persona = persona.id_persona
     JOIN general.empleado_puesto e ON e.id_empleado_puesto = inh.id_empleado_puesto
     JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = e.id_planta_cargo_det
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = inh.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
  WHERE inh.inhabilitado = true;

ALTER TABLE desvinculacion.personas_inhabilitadas
  OWNER TO postgres;




  CREATE OR REPLACE VIEW desvinculacion.personas_desvinculadas AS 
 SELECT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.denominacion_unidad AS oee,
    d.id_desvinculacion,
    d.id_empleado_puesto,
    d.id_datos_especif_motivo,
    e.id_persona,
    d.fecha_desvinculacion,
    de.descripcion AS motivo_desvinculacion,
    p.nombres,
    p.apellidos,
    p.documento_identidad,
    p.sexo,
    "substring"(age(now(), p.fecha_nacimiento::timestamp with time zone)::text, 1, 2) AS edad,
        CASE
            WHEN e.contratado = true THEN 'CONTRATADO'::text
            ELSE 'PERMANENTE'::text
        END AS modalidad_ocupacion,
    e.id_planta_cargo_det,
    d.fecha_alta
   FROM desvinculacion.desvinculacion d
     JOIN general.empleado_puesto e ON e.id_empleado_puesto = d.id_empleado_puesto
     JOIN seleccion.datos_especificos de ON de.id_datos_especificos = d.id_datos_especif_motivo
     JOIN general.persona p ON p.id_persona = e.id_persona
     JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = e.id_planta_cargo_det
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = puesto.id_configuracion_uo_det
     JOIN planificacion.configuracion_uo_cab oee ON uo.id_configuracion_uo = oee.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo;

ALTER TABLE desvinculacion.personas_desvinculadas
  OWNER TO postgres;




  CREATE OR REPLACE VIEW capacitacion.promedio_calificaciones_asistencia_det AS 
 SELECT DISTINCT oee.denominacion_unidad AS oee,
    sne.nen_codigo AS nen_cod,
    sne.nen_nombre AS nen_nom,
    sin_entidad.ent_codigo AS ent_cod,
    sin_entidad.ent_nombre,
    oee.orden,
    uo.denominacion_unidad AS unidad_organizativa,
    ((((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden) || desvinculacion.getdependientes(uo.id_configuracion_uo_det)::text AS codigo_uo,
    p.documento_identidad,
    p.nombres,
    p.apellidos,
    p.sexo,
    de.descripcion AS tipo_capacitacion,
    c.nombre AS capacitacion,
    date(c.fecha_inicio) AS fecha_desde,
    date(c.fecha_fin) AS fecha_hasta,
        CASE
            WHEN e.calificacion IS NULL THEN 0::bigint
            ELSE e.calificacion
        END AS calificacion,
        CASE
            WHEN e.asistencia IS NULL THEN 0::bigint
            ELSE e.asistencia
        END AS asistencia,
    t.cantidad AS cantidad_dias,
        CASE
            WHEN post.id_empleado_puesto IS NULL THEN 'CIUDADANIA'::text
            WHEN puesto.jefatura = true THEN 'ALTA GERENCIA'::text
            WHEN puesto.jefatura = false THEN 'NIVEL OPERATIVO'::text
            ELSE NULL::text
        END AS nivel_agrup,
    c.id_capacitacion,
    oee.id_configuracion_uo,
    uo.id_configuracion_uo_det
   FROM capacitacion.lista_det det
     JOIN capacitacion.lista_cab cab ON cab.id_lista = det.id_lista
     JOIN capacitacion.capacitaciones c ON c.id_capacitacion = cab.id_capacitacion
     JOIN capacitacion.postulacion_cap post ON post.id_postulacion = det.id_postulacion
     JOIN general.persona p ON p.id_persona = post.id_persona
     LEFT JOIN general.empleado_puesto emp ON emp.id_empleado_puesto = post.id_empleado_puesto
     LEFT JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = emp.id_planta_cargo_det
     LEFT JOIN planificacion.cpt cpt ON cpt.id_cpt = puesto.id_cpt
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = c.id_configuracion_uo_det
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = c.id_configuracion_uo
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
     JOIN planificacion.configuracion_uo_cab oee_cap ON oee_cap.id_configuracion_uo = c.id_configuracion_uo
     JOIN seleccion.datos_especificos de ON de.id_datos_especificos = c.id_datos_especificos_tipo_cap
     LEFT JOIN capacitacion.evaluacion_part e ON e.id_postulacion = post.id_postulacion
     JOIN capacitacion.evaluacion_tipo t ON t.id_capacitacion = cab.id_capacitacion
     JOIN seleccion.referencias ref ON ref.valor_num = c.estado
  WHERE det.tipo::text = 'P'::text AND t.tipo::text = 'EVAL_PART'::text AND ref.dominio::text = 'ESTADOS_CAPACITACION'::text AND ref.desc_larga::text = 'FINALIZADA'::text
  ORDER BY sne.nen_codigo, sin_entidad.ent_codigo, oee.orden, uo.denominacion_unidad, date(c.fecha_inicio), c.id_capacitacion;

ALTER TABLE capacitacion.promedio_calificaciones_asistencia_det
  OWNER TO postgres;




  CREATE OR REPLACE VIEW capacitacion.desbloqueo AS 
 SELECT pais.descripcion AS pais,
    pais.id_pais,
    de.descripcion AS tipo_capacitacion,
    c.id_capacitacion,
    c.nombre AS capacitacion,
    (p.nombres::text || ' '::text) || p.apellidos::text AS participante,
    p.documento_identidad AS ci,
    p.nombres,
    p.apellidos,
    d.descripcion AS motivo,
    det.observacion,
    det.fecha_desvinculacion AS fecha_bloqueo,
    det.motivo_habilit AS motivo_habilitacion,
    det.fecha_habilit AS fecha_desbloqueo,
    det.id_lista_det,
        CASE
            WHEN det.motivo_habilit IS NULL THEN 'BLOQUEADO'::text
            ELSE 'DESBLOQUEADO'::text
        END AS estado,
    det.id_documento,
    oee.denominacion_unidad AS oee
   FROM capacitacion.lista_det det
     JOIN capacitacion.lista_cab cab ON cab.id_lista = det.id_lista
     JOIN capacitacion.capacitaciones c ON c.id_capacitacion = cab.id_capacitacion
     JOIN capacitacion.postulacion_cap post ON post.id_postulacion = det.id_postulacion
     JOIN general.persona p ON p.id_persona = post.id_persona
     JOIN general.pais ON pais.id_pais = p.id_pais_expedicion_doc
     LEFT JOIN general.empleado_puesto emp ON emp.id_empleado_puesto = post.id_empleado_puesto
     LEFT JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = emp.id_planta_cargo_det
     LEFT JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = puesto.id_configuracion_uo_det
     LEFT JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = uo.id_configuracion_uo
     JOIN seleccion.datos_especificos de ON de.id_datos_especificos = c.id_datos_especificos_tipo_cap
     JOIN seleccion.datos_especificos d ON d.id_datos_especificos = det.id_datos_especificos_desv
  WHERE det.tipo::text = 'D'::text AND d.valor_alf::text = 'B'::text
  ORDER BY pais.descripcion;

ALTER TABLE capacitacion.desbloqueo
  OWNER TO postgres;




CREATE OR REPLACE VIEW capacitacion.personas_capacitadas AS 
 SELECT sne.nen_codigo AS nivel_codigo,
    sne.nen_nombre AS nivel,
    sin_entidad.ent_codigo AS entidad_codigo,
    sin_entidad.ent_nombre AS entidad,
    oee.orden,
    oee.id_configuracion_uo,
    oee.denominacion_unidad AS oee,
    uo.denominacion_unidad,
    ((((sne.nen_codigo || '.'::text) || sin_entidad.ent_codigo) || '.'::text) || oee.orden) || desvinculacion.getdependientes(uo.id_configuracion_uo_det)::text AS uo_codigo,
    de.descripcion AS tipo_capacitacion,
    upper(c.nombre::text) AS capacitacion,
    pais.descripcion AS pais,
    dpto.descripcion AS departamento,
    ciu.descripcion AS ciudad,
    c.fecha_inicio,
    p.id_persona,
    (p.nombres::text || ', '::text) || p.apellidos::text AS participante,
    p.sexo,
    rtrim(ltrim("substring"(age(now(), p.fecha_nacimiento::timestamp with time zone)::text, 1, 2))) AS edad,
    oee_f.denominacion_unidad AS oee_funcionario,
        CASE
            WHEN post.id_empleado_puesto IS NULL THEN 'CIUDADANIA'::text
            WHEN puesto.jefatura = true THEN 'ALTA GERENCIA'::text
            WHEN puesto.jefatura = false THEN 'OPERATIVO'::text
            ELSE NULL::text
        END AS nivel_agrupacion,
    c.fecha_alta
   FROM capacitacion.lista_det det
     JOIN capacitacion.lista_cab cab ON cab.id_lista = det.id_lista
     JOIN capacitacion.capacitaciones c ON c.id_capacitacion = cab.id_capacitacion
     JOIN capacitacion.postulacion_cap post ON post.id_postulacion = det.id_postulacion
     JOIN general.persona p ON p.id_persona = post.id_persona
     JOIN general.ciudad ciu ON c.id_ciudad = ciu.id_ciudad
     JOIN general.departamento dpto ON dpto.id_departamento = ciu.id_departamento
     JOIN general.pais ON pais.id_pais = dpto.id_pais
     LEFT JOIN general.empleado_puesto emp ON emp.id_empleado_puesto = post.id_empleado_puesto
     LEFT JOIN planificacion.planta_cargo_det puesto ON puesto.id_planta_cargo_det = emp.id_planta_cargo_det
     LEFT JOIN planificacion.configuracion_uo_det uo_f ON uo_f.id_configuracion_uo_det = puesto.id_configuracion_uo_det
     LEFT JOIN planificacion.configuracion_uo_cab oee_f ON oee_f.id_configuracion_uo = uo_f.id_configuracion_uo
     LEFT JOIN planificacion.cpt cpt ON cpt.id_cpt = puesto.id_cpt
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = c.id_configuracion_uo
     JOIN planificacion.configuracion_uo_det uo ON uo.id_configuracion_uo_det = c.id_configuracion_uo_det
     JOIN seleccion.datos_especificos de ON de.id_datos_especificos = c.id_datos_especificos_tipo_cap
     JOIN planificacion.entidad entidad ON entidad.id_entidad = oee.id_entidad
     JOIN sinarh.sin_entidad sin_entidad ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad
     JOIN sinarh.sin_nivel_entidad sne ON sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo
  WHERE det.tipo::text = 'P'::text AND det.activo = true;

ALTER TABLE capacitacion.personas_capacitadas
  OWNER TO postgres;




  CREATE OR REPLACE VIEW capacitacion.capacitacion_poc_post AS 
 SELECT c.id_capacitacion,
    oee.denominacion_unidad,
    c.nombre,
    de.descripcion,
        CASE
            WHEN c.modalidad::text = 'P'::text THEN 'PRESENCIAL'::text
            WHEN c.modalidad::text = 'S'::text THEN 'SEMIPRESENCIAL'::text
            WHEN c.modalidad::text = 'V'::text THEN 'VIRTUAL'::text
            ELSE NULL::text
        END AS modalidad,
    c.fecha_pub_desde AS desde,
    c.fecha_recep_hasta AS hasta,
    ciu.id_ciudad,
    de.id_datos_especificos,
    c.modalidad AS mod,
    c.fecha_pub_hasta,
    c.fecha_recep_desde,
    c.recepcion_postulacion,
    c.fecha_inicio,
    c.fecha_fin
   FROM capacitacion.capacitaciones c
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = c.id_configuracion_uo
     JOIN seleccion.datos_especificos de ON de.id_datos_especificos = c.id_datos_especificos_tipo_cap
     JOIN general.ciudad ciu ON ciu.id_ciudad = c.id_ciudad
     JOIN general.departamento d ON ciu.id_departamento = d.id_departamento
     JOIN general.pais p ON p.id_pais = d.id_pais
     JOIN seleccion.referencias ref ON ref.valor_num = c.estado
  WHERE c.activo = true AND c.recepcion_postulacion = false AND c.tipo::text = 'CAP_SFP'::text AND ref.dominio::text = 'ESTADOS_CAPACITACION'::text AND c.fecha_pub_desde::date <= 'now'::text::date AND c.fecha_pub_hasta::date >= 'now'::text::date
UNION
 SELECT cc.id_capacitacion,
    oee.denominacion_unidad,
    cc.nombre,
    de.descripcion,
        CASE
            WHEN cc.modalidad::text = 'P'::text THEN 'PRESENCIAL'::text
            WHEN cc.modalidad::text = 'S'::text THEN 'SEMIPRESENCIAL'::text
            WHEN cc.modalidad::text = 'V'::text THEN 'VIRTUAL'::text
            ELSE NULL::text
        END AS modalidad,
    cc.fecha_recep_desde AS desde,
    cc.fecha_recep_hasta AS hasta,
    ciu.id_ciudad,
    de.id_datos_especificos,
    cc.modalidad AS mod,
    cc.fecha_pub_hasta,
    cc.fecha_recep_desde,
    cc.recepcion_postulacion,
    cc.fecha_inicio,
    cc.fecha_fin
   FROM capacitacion.capacitaciones cc
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = cc.id_configuracion_uo
     JOIN seleccion.datos_especificos de ON de.id_datos_especificos = cc.id_datos_especificos_tipo_cap
     JOIN general.ciudad ciu ON ciu.id_ciudad = cc.id_ciudad
     JOIN general.departamento d ON ciu.id_departamento = d.id_departamento
     JOIN general.pais p ON p.id_pais = d.id_pais
     JOIN seleccion.referencias ref ON ref.valor_num = cc.estado
  WHERE cc.activo = true AND cc.recepcion_postulacion = true AND cc.tipo::text = 'CAP_SFP'::text AND ref.dominio::text = 'ESTADOS_CAPACITACION'::text AND cc.fecha_recep_desde::date <= 'now'::text::date AND cc.fecha_recep_hasta::date >= 'now'::text::date;

ALTER TABLE capacitacion.capacitacion_poc_post
  OWNER TO postgres;




  CREATE OR REPLACE VIEW capacitacion.capacitacion_og290 AS 
 SELECT oee.denominacion_unidad,
    c.nombre,
    de.descripcion,
        CASE
            WHEN c.modalidad::text = 'P'::text THEN 'PRESENCIAL'::text
            WHEN c.modalidad::text = 'S'::text THEN 'SEMIPRESENCIAL'::text
            WHEN c.modalidad::text = 'V'::text THEN 'VIRTUAL'::text
            ELSE NULL::text
        END AS modalidad,
    c.fecha_pub_desde,
    c.fecha_pub_hasta,
    c.id_capacitacion,
    ciu.id_ciudad,
    de.id_datos_especificos,
    c.fecha_inicio,
    c.fecha_fin,
    c.modalidad AS mod,
    oee.id_configuracion_uo
   FROM capacitacion.capacitaciones c
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = c.id_configuracion_uo
     JOIN seleccion.datos_especificos de ON de.id_datos_especificos = c.id_datos_especificos_tipo_cap
     JOIN general.ciudad ciu ON ciu.id_ciudad = c.id_ciudad
     JOIN general.departamento d ON ciu.id_departamento = d.id_departamento
     JOIN general.pais p ON p.id_pais = d.id_pais
  WHERE c.activo = true AND c.recepcion_postulacion = true AND c.tipo::text = 'CAP_OG290'::text AND c.fecha_pub_desde::date <= 'now'::text::date AND c.fecha_pub_hasta::date >= 'now'::text::date;

ALTER TABLE capacitacion.capacitacion_og290
  OWNER TO postgres;




  CREATE OR REPLACE VIEW capacitacion.capacitacion_finalizada AS 
 SELECT c.id_capacitacion,
    oee.denominacion_unidad,
    c.nombre,
    de.descripcion,
        CASE
            WHEN c.modalidad::text = 'P'::text THEN 'PRESENCIAL'::text
            WHEN c.modalidad::text = 'S'::text THEN 'SEMIPRESENCIAL'::text
            WHEN c.modalidad::text = 'V'::text THEN 'VIRTUAL'::text
            ELSE NULL::text
        END AS modalidalidad_desc,
    c.modalidad AS modalidalidad,
    c.fecha_pub_desde,
    c.fecha_pub_hasta,
    ciu.id_ciudad,
    de.id_datos_especificos,
    c.fecha_inicio,
    c.fecha_fin
   FROM capacitacion.capacitaciones c
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = c.id_configuracion_uo
     JOIN seleccion.datos_especificos de ON de.id_datos_especificos = c.id_datos_especificos_tipo_cap
     JOIN seleccion.referencias ref ON ref.valor_num = c.estado
     JOIN general.ciudad ciu ON ciu.id_ciudad = c.id_ciudad
     JOIN general.departamento d ON ciu.id_departamento = d.id_departamento
     JOIN general.pais p ON p.id_pais = d.id_pais
  WHERE c.activo = true AND c.tipo::text = 'CAP_SFP'::text AND ref.dominio::text = 'ESTADOS_CAPACITACION'::text AND ref.desc_abrev::text = 'FINALIZADA'::text
  ORDER BY c.id_capacitacion DESC;

ALTER TABLE capacitacion.capacitacion_finalizada
  OWNER TO postgres;




  CREATE OR REPLACE VIEW capacitacion.capacitacion_en_proceso AS 
 SELECT c.id_capacitacion,
    oee.denominacion_unidad,
    c.nombre,
    de.descripcion,
        CASE
            WHEN c.modalidad::text = 'P'::text THEN 'PRESENCIAL'::text
            WHEN c.modalidad::text = 'S'::text THEN 'SEMIPRESENCIAL'::text
            WHEN c.modalidad::text = 'V'::text THEN 'VIRTUAL'::text
            ELSE NULL::text
        END AS modalidad_desc,
    c.modalidad,
    c.fecha_pub_desde,
    c.fecha_pub_hasta,
    ciu.id_ciudad,
    de.id_datos_especificos,
    c.fecha_inicio,
    c.fecha_fin
   FROM capacitacion.capacitaciones c
     JOIN planificacion.configuracion_uo_cab oee ON oee.id_configuracion_uo = c.id_configuracion_uo
     JOIN seleccion.datos_especificos de ON de.id_datos_especificos = c.id_datos_especificos_tipo_cap
     JOIN seleccion.referencias ref ON ref.valor_num = c.estado
     JOIN general.ciudad ciu ON ciu.id_ciudad = c.id_ciudad
     JOIN general.departamento d ON ciu.id_departamento = d.id_departamento
     JOIN general.pais p ON p.id_pais = d.id_pais
  WHERE c.activo = true AND c.tipo::text = 'CAP_SFP'::text AND ref.dominio::text = 'ESTADOS_CAPACITACION'::text AND (ref.desc_abrev::text = ANY (ARRAY['ASIGNAR COMISION'::character varying::text, 'FINALIZADO CIRCUITO'::character varying::text, 'INSCRIPCION/LISTA'::character varying::text, 'PUBLICAR SELECCIONADOS'::character varying::text, 'REALIZAR EVALUACION'::character varying::text, 'EN PROCESO'::character varying::text]))
  ORDER BY c.id_capacitacion DESC;

ALTER TABLE capacitacion.capacitacion_en_proceso
  OWNER TO postgres;




  CREATE OR REPLACE VIEW capacitacion.bandeja_capacitacion AS 
 SELECT DISTINCT c.id_capacitacion,
    c.activo,
    c.nombre AS nombre_capac,
    de.descripcion,
    cuc.id_configuracion_uo,
    cuc.orden AS orden_configuracion,
    cuc.denominacion_unidad AS denominacion_unidad_cab,
    cud.id_configuracion_uo_det,
    cud.orden AS orden_configuracion_det,
    cud.denominacion_unidad AS denominacion_unidad_det,
    se.nen_codigo,
    se.ent_codigo,
    se.ent_nombre,
    sne.nen_codigo AS nivel_cod,
    sne.nen_nombre AS nivel_nombre,
    a.descripcion AS actividad,
    t.actorid_ AS usuario,
    c.fecha_recep_desde AS fecha_recepcion,
    c.fecha_inicio,
    to_char(now() - c.fecha_recep_desde::timestamp with time zone, 'DD'::text)::integer AS dias_creacion,
    to_char(now() - c.fecha_inicio::timestamp with time zone, 'DD'::text)::integer AS dias_inicio,
    proceso.calc_atraso_actividad_proc(c.fecha_recep_desde, ap.id_actividad_proceso, c.id_configuracion_uo) AS atraso,
    a.id_actividad,
    e.id_entidad,
    sne.id_sin_nivel_entidad,
    t.id_ AS id_taskinstance,
    c.id_process_instance,
    a.cod_actividad,
    p.id_proceso,
    par.id_actividad_proceso,
    a.tipo AS tipo_actividad,
    e.id_sin_entidad,
    ref.desc_abrev AS estado_grupo
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
