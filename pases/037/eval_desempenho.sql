
SELECT setval('seguridad.funcion_id_funcion_seq', 748, true);--Actualiza el valor actual de la secuencia a 748
INSERT INTO proceso.actividad_proceso VALUES (45, 3.00, 'D', '', 116, 4, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO proceso.proc_actividad_rol VALUES ( nextval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'::regclass), 3, 45, true, 'WERNER', localtimestamp, NULL, NULL);


INSERT INTO proceso.actividad_proceso VALUES (41, 3.00, 'D', '', 115, 4, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO proceso.proc_actividad_rol VALUES (nextval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'::regclass), 3, 41, true, 'WERNER', localtimestamp, NULL, NULL);

INSERT INTO proceso.actividad_proceso VALUES (42, 3.00, 'D', '', 112, 4, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO proceso.proc_actividad_rol VALUES (nextval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'::regclass), 3, 42, true, 'WERNER', localtimestamp, NULL, NULL);

INSERT INTO proceso.actividad_proceso VALUES (43, 3.00, 'D', '', 114, 4, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO proceso.proc_actividad_rol VALUES (nextval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'::regclass), 3, 43, true, 'WERNER', localtimestamp, NULL, NULL);

INSERT INTO proceso.actividad_proceso VALUES (44, 3.00, 'D', '', 113, 4, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO proceso.proc_actividad_rol VALUES (nextval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'::regclass), 3, 44, true, 'WERNER', localtimestamp, NULL, NULL);


INSERT INTO seguridad.funcion VALUES (nextval('seguridad.funcion_id_funcion_seq'::regclass), 'CONFIGURAR PLANTILLA DE EVALUACION RECEPCION', 'configurar_plantilla_evalRec_list', 'EVALUACION', NULL);
INSERT INTO seguridad.rol_funcion VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), (select max (id_funcion) from seguridad.funcion), 3); 

INSERT INTO seguridad.funcion VALUES (nextval('seguridad.funcion_id_funcion_seq'::regclass), 'CARGAR PLANTILLA DE EVALUACION RECEPCION', 'configurar_plantilla_evalRec_edit', 'EVALUACION', NULL);
INSERT INTO seguridad.rol_funcion VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), (select max (id_funcion) from seguridad.funcion), 3);

INSERT INTO seguridad.funcion VALUES (nextval('seguridad.funcion_id_funcion_seq'::regclass), 'VER CONFIGURACION DE PLANTILLA DE EVALUACION RECEPCION', 'configurar_plantilla_evalRec', 'EVALUACION', NULL);
INSERT INTO seguridad.rol_funcion VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), (select max (id_funcion) from seguridad.funcion), 3);

INSERT INTO seguridad.funcion VALUES (nextval('seguridad.funcion_id_funcion_seq'::regclass), 'VER CONFIGURACION DE PLANTILLA', 'ver_configPlantillaEvaluacion', 'EVALUACION', NULL);
INSERT INTO seguridad.rol_funcion VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), (select max (id_funcion) from seguridad.funcion), 3);

INSERT INTO seguridad.funcion VALUES (nextval('seguridad.funcion_id_funcion_seq'::regclass), 'CARGAR RESULTADOS DE EVALUACION METODOLOGIA', 'cargarResultEvalMetodo571Area', 'EVALUACION', true);
INSERT INTO seguridad.rol_funcion VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), (select max (id_funcion) from seguridad.funcion), 3);

INSERT INTO seguridad.funcion VALUES (nextval('seguridad.funcion_id_funcion_seq'::regclass), 'ADJUNTAR REGLAMENTO', 'adjuntarReglamento', 'EVALUACION', true);
INSERT INTO seguridad.rol_funcion VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), (select max (id_funcion) from seguridad.funcion), 3);
INSERT INTO seleccion.datos_especificos VALUES ( nextval('seleccion.sel_datos_especificos_id_datos_especificos_seq'::regclass), 'REGLAMENTO', NULL, NULL, 2, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO seleccion.sel_funcion_datos_esp VALUES (nextval('seleccion.funcion_datos_esp_id_funcion_datos_esp_seq'::regclass), 2, true, true, (select max (id_funcion) from seguridad.funcion), (select max(id_datos_especificos) from seleccion.datos_especificos), 'WERNER', localtimestamp, NULL, NULL);



INSERT INTO seguridad.funcion VALUES (nextval('seguridad.funcion_id_funcion_seq'::regclass), 'CARGAR RESULTADOS DE EVALUACION METODOLOGIA', 'cargarResultEvalMetodo571Gestion', 'EVALUACION', true);
INSERT INTO seguridad.rol_funcion VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), (select max (id_funcion) from seguridad.funcion), 3);

INSERT INTO seguridad.funcion VALUES (nextval('seguridad.funcion_id_funcion_seq'::regclass), 'ADJUNTAR OBSERVACION', 'adjuntarObservacionSFP', 'EVALUACION', true);
INSERT INTO seguridad.rol_funcion VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), (select max (id_funcion) from seguridad.funcion), 3);
INSERT INTO seleccion.datos_especificos VALUES ( nextval('seleccion.sel_datos_especificos_id_datos_especificos_seq'::regclass), 'OBSERVACIÓN SFP', NULL, NULL, 2, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO seleccion.sel_funcion_datos_esp VALUES (nextval('seleccion.funcion_datos_esp_id_funcion_datos_esp_seq'::regclass), 2, false, true, (select max (id_funcion) from seguridad.funcion), (select max(id_datos_especificos) from seleccion.datos_especificos), 'WERNER', localtimestamp, NULL, NULL);

INSERT INTO seguridad.funcion VALUES (nextval('seguridad.funcion_id_funcion_seq'::regclass), 'ADJUNTAR INFORME FINAL', 'adjuntarInformeFinal', 'EVALUACION', true);
INSERT INTO seguridad.rol_funcion VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), (select max (id_funcion) from seguridad.funcion), 3);
INSERT INTO seleccion.datos_especificos VALUES (nextval('seleccion.sel_datos_especificos_id_datos_especificos_seq'::regclass), 'INFORME FINAL', NULL, NULL, 2, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO seleccion.sel_funcion_datos_esp VALUES (nextval('seleccion.funcion_datos_esp_id_funcion_datos_esp_seq'::regclass), 2, false, true, (select max (id_funcion) from seguridad.funcion), (select max(id_datos_especificos) from seleccion.datos_especificos), 'WERNER', localtimestamp, NULL, NULL);


--INSERT INTO seguridad.rol_funcion VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 724, 3);
INSERT INTO seleccion.datos_especificos VALUES (nextval('seleccion.sel_datos_especificos_id_datos_especificos_seq'::regclass), 'CONSTANCIA DE EVALUACION', 'EVAL_R', NULL, 2, true, 'WERNER', localtimestamp, NULL, NULL);




--REGISTRO SIN SEQUENCIADOR, PRÓXIMO A CONOCIMIENTO Y CAPACIDAD.
INSERT INTO seleccion.datos_especificos VALUES (1102, 'ACTITUD', NULL, NULL, 58, true, 'WERNER', localtimestamp, NULL, NULL);


INSERT INTO seleccion.sel_funcion_datos_esp VALUES (nextval('seleccion.funcion_datos_esp_id_funcion_datos_esp_seq'::regclass), 3, false, true, 491, 28, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO seleccion.sel_funcion_datos_esp VALUES (nextval('seleccion.funcion_datos_esp_id_funcion_datos_esp_seq'::regclass), 2, false, true, 524, 1144, 'WERNER', localtimestamp, NULL, NULL);

INSERT INTO seleccion.sel_funcion_datos_esp VALUES (nextval('seleccion.funcion_datos_esp_id_funcion_datos_esp_seq'::regclass), 2, false, true, 736, 1143, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO seleccion.sel_funcion_datos_esp VALUES (nextval('seleccion.funcion_datos_esp_id_funcion_datos_esp_seq'::regclass), 2, false, true, 559, 1140, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO seleccion.sel_funcion_datos_esp VALUES (nextval('seleccion.funcion_datos_esp_id_funcion_datos_esp_seq'::regclass), 2, false, true, 588, 1141, 'WERNER', localtimestamp, NULL, NULL);

INSERT INTO seleccion.sel_funcion_datos_esp VALUES (nextval('seleccion.funcion_datos_esp_id_funcion_datos_esp_seq'::regclass), 2, false, true, 738, 1142, 'WERNER', localtimestamp, NULL, NULL);



UPDATE proceso.actividad
SET cod_actividad = 'ASIGNAR_PLANTILLA'
WHERE cod_actividad = 'ASIGNAR_PLANTILLAS';




ALTER TABLE eval_desempeno.plantilla_eval_conf_det
ALTER COLUMN actividades DROP NOT NULL;

ALTER TABLE eval_desempeno.plantilla_eval_conf_det
ALTER COLUMN indicadores_cump DROP NOT NULL;

ALTER TABLE eval_desempeno.plantilla_eval_pdec_cab
ALTER COLUMN fecha_mod DROP NOT NULL;

ALTER TABLE eval_desempeno.evaluacion_desempeno
ALTER COLUMN id_configuracion_uo_det DROP NOT NULL;

ALTER TABLE eval_desempeno.grupos_sujetos ADD COLUMN puntaje_pdec real;
ALTER TABLE eval_desempeno.grupos_sujetos ADD COLUMN puntaje_percepcion real;

ALTER TABLE eval_desempeno.plantilla_eval_pdec_cab ADD COLUMN peso_pdec real NOT NULL;
ALTER TABLE eval_desempeno.plantilla_eval_conf_det ADD COLUMN peso_percepcion real;




CREATE OR REPLACE FUNCTION eval_desempeno.fnc_plan_mejora_pdec(id_plantilla_eval_pdec_cab bigint)
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
	join eval_desempeno.plantilla_eval_pdec_cab cab
	on cab.id_plantilla_eval_pdec_cab = r.id_plantilla_eval_pdec_cab
    WHERE
	r.id_plantilla_eval_pdec_cab = id_plantilla_eval_pdec_cab
        and r.activo = true ;
RETURN resultado;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
  
ALTER FUNCTION eval_desempeno.fnc_plan_mejora_pdec(bigint) OWNER TO postgres;

ALTER TABLE eval_desempeno.escala_eval
RENAME observacion  TO nivel_evaluacion;

ALTER TABLE eval_desempeno.plantilla_eval_escala
ALTER COLUMN descripcion DROP NOT NULL;

ALTER TABLE eval_desempeno.plantilla_eval_escala 
ALTER COLUMN descripcion DROP NOT NULL; 

ALTER TABLE eval_desempeno.grupos_sujetos ADD COLUMN comentario_percepcion character varying(1000);
ALTER TABLE eval_desempeno.plantilla_eval_pdec ADD COLUMN comentario_pdec character varying(1000);
