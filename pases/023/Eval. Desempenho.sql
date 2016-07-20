--No ejecutar.
-- Un compendio se creó como 037

--ANTES DE INSERTAR SE RECOMIENDA VERIFICAR EL ÚLTIMO PK DE CADA TABLA; algunas sequencias están desactualizadas BD réplica producción; Werner.

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: actividad_proceso; Type: TABLE DATA; Schema: proceso; Owner: postgres
-- El valor 4 asigna EVALUACION_DESEMPENO; el valor 116 ASIGNAR_PLANTILLA.

INSERT INTO proceso.actividad_proceso VALUES (40, 3.00, 'D', '', 116, 4, true, 'WERNER', localtimestamp, NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: proc_actividad_rol; Type: TABLE DATA; Schema: proceso; Owner: postgres
-- Esta inserción permite saber el rol del Usuario Actual; el valor 40 hace referencia al registro en proceso.actividad_proceso.

INSERT INTO proceso.proc_actividad_rol VALUES ( (select max(id_proceso_actividad_rol) from proceso.actividad_rol), 3, 40, true, 'WERNER', localtimestamp, NULL, NULL);


------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- No existe en la bd actual, algún registro con "url" like 'configurar_plantilla_evalRec_list', se inserta desde la BD sicca_dw

INSERT INTO seguridad.funcion VALUES (731, 'CONFIGURAR PLANTILLA DE EVALUACION RECEPCION', 'configurar_plantilla_evalRec_list', 'EVALUACION', NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: rol_funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- Se asigna la función a un rol, en este caso el de RTRINIDAD -- (3); -- permite acceder a ConfigurarPlantillaEvalRecList.xhtml desde AsignarMetodologiaGrupo.xhtml

--INSERT INTO seguridad.rol_funcion VALUES (1284, 731, 3); 
INSERT INTO seguridad.rol_funcion VALUES (731, 3); 

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- No existe en la bd actual, algún registro con "url" like 'configurar_plantilla_evalRec_edit', se inserta desde la BD sicca_dw

INSERT INTO seguridad.funcion VALUES (732, 'CARGAR PLANTILLA DE EVALUACION RECEPCION', 'configurar_plantilla_evalRec_edit', 'EVALUACION', NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: rol_funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- Se asigna la función a un rol, en este caso el de RTRINIDAD -- (3); -- permite acceder a ConfigurarPlantillaEvalRecEdit.xhtml desde AsignarMetodologiaGrupo.xhtml

INSERT INTO seguridad.rol_funcion VALUES (1285, 732, 3); 

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- modificado; ambas columnas en eval_desempeno.plantilla_eval_conf_det pueden ser NULL; 
-- al estar en NOT NULL impedía la carga en "Plantilla de Evaluación de Desempeño Individual"
-- "Se ha producido un error: org.hibernate.exception.ConstraintViolationException: Could not execute JDBC batch update", 
-- más adelante se investigará cuál es la causa (raíz), por el momento se optó quitar la condición NOT NULL

ALTER TABLE eval_desempeno.plantilla_eval_conf_det
   ALTER COLUMN actividades DROP NOT NULL;
ALTER TABLE eval_desempeno.plantilla_eval_conf_det
   ALTER COLUMN indicadores_cump DROP NOT NULL;

------------------------------------------------------------------------------------------------------------------------------------------------------------------   
--En "Configurar Plantilla Desempeño y Potencial (PDEC)"
--Contenido Funcional, se selecciona por "puesto" o "cpt_especifico", se genera error de FK; lo de fecha_mod.... ESTE TEMAAAA XD una sola fecha qué problema...

ALTER TABLE eval_desempeno.plantilla_eval_pdec_cab
   ALTER COLUMN fecha_mod DROP NOT NULL;

------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- PARA PASAR A LA SIGUIENTE TAREA		GENERAR RESOLUCIÓN problemas roles de vuelta....
------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: actividad_proceso; Type: TABLE DATA; Schema: proceso; Owner: postgres
-- El valor 4 asigna EVALUACION_DESEMPENO; el valor 115 GENERAR_RESOLUCION.

INSERT INTO proceso.actividad_proceso VALUES (41, 3.00, 'D', '', 115, 4, true, 'RTRINIDAD', '2012-08-27 13:45:26.851', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: proc_actividad_rol; Type: TABLE DATA; Schema: proceso; Owner: postgres
-- Esta inserción permite saber el rol del Usuario Actual; el valor 41 hace referencia al registro en proceso.actividad_proceso.

INSERT INTO proceso.proc_actividad_rol VALUES (50, 3, 41, true, 'RTRINIDAD', '2014-08-25 00:00:00', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------


--APROBAR EVALUACIÓN--


------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: actividad_proceso; Type: TABLE DATA; Schema: proceso; Owner: postgres
-- El valor 4 asigna EVALUACION_DESEMPENO; el valor 112 APROBAR_EVALUACION.

INSERT INTO proceso.actividad_proceso VALUES (42, 3.00, 'D', '', 112, 4, true, 'RTRINIDAD', '2012-08-27 13:45:26.851', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: proc_actividad_rol; Type: TABLE DATA; Schema: proceso; Owner: postgres
-- Esta inserción permite saber el rol del Usuario Actual; el valor 42 hace referencia al registro en proceso.actividad_proceso.

INSERT INTO proceso.proc_actividad_rol VALUES (51, 3, 42, true, 'RTRINIDAD', '2014-08-25 00:00:00', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- No existe en la bd actual, algún registro con "url" like 'configurar_plantilla_evalRec', se inserta desde la BD sicca_dw

INSERT INTO seguridad.funcion VALUES (733, 'VER CONFIGURACION DE PLANTILLA DE EVALUACION RECEPCION', 'configurar_plantilla_evalRec', 'EVALUACION', NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: rol_funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- Se asigna la función a un rol, en este caso el de RTRINIDAD -- (3); -- permite acceder a ConfigurarPlantillaEvalRec.xhtml desde ConfigurarPlantillaEvalRecList.xhtml
-- Esto es solo para poder ver...

INSERT INTO seguridad.rol_funcion VALUES (1286, 733, 3);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- No existe en la bd actual, algún registro con "url" like 'ver_configPlantillaEvaluacion', se inserta desde la BD sicca_dw

INSERT INTO seguridad.funcion VALUES (734, 'VER CONFIGURACION DE PLANTILLA', 'ver_configPlantillaEvaluacion', 'EVALUACION', NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: rol_funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- Se asigna la función a un rol, en este caso el de RTRINIDAD -- (3); -- permite acceder a ConfigurarPlantillaEvalRec.xhtml desde ConfigurarPlantillaEvalRecList.xhtml
-- Esto es solo para poder ver...

INSERT INTO seguridad.rol_funcion VALUES (1287, 734, 3);

------------------------------------------------------------------------------------------------------------------------------------------------------------------


--ADJUNTAR RESOLUCIÓN FIRMADA--


------------------------------------------------------------------------------------------------------------------------------------------------------------------ 

-- Data for Name: actividad_proceso; Type: TABLE DATA; Schema: proceso; Owner: postgres
-- El valor 4 asigna EVALUACION_DESEMPENO; el valor 114 ADJUNTAR_RESOLUCION_FIRMADA.

INSERT INTO proceso.actividad_proceso VALUES (43, 3.00, 'D', '', 114, 4, true, 'RTRINIDAD', '2012-08-27 13:45:26.851', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: proc_actividad_rol; Type: TABLE DATA; Schema: proceso; Owner: postgres
-- Esta inserción permite saber el rol del Usuario Actual; el valor 43 hace referencia al registro en proceso.actividad_proceso.

INSERT INTO proceso.proc_actividad_rol VALUES (52, 3, 43, true, 'RTRINIDAD', '2014-08-25 00:00:00', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: sel_funcion_datos_esp; Type: TABLE DATA; Schema: seleccion; Owner: postgres
-- Esta inserción permite desplegar al menos un valor en el comboBox TipoDocumento en el formulario "Documentos Adjuntos" el 491 apunta al id de la tabla
-- seguridad.funcion 491;"ADJUNTAR RESOLUCION FIRMADA DE EVALUACION DE DESEMPEÑO";"adjuntarResolucion";"EVALUACION";TRUE; 
-- y el 28 apunta a seleccion.datos_especificos 28;"RESOLUCION";"RES";;2;TRUE;"ADMIN";"2011-10-19 16:52:00";"";"" 
-- para mas detalle ver imagen "ADJUNTAR RESOLUCIÓN FIRMADA"
-- ¿por qué esos parámetros? porque son los pedidos desde el sistema para el comboBox...

INSERT INTO seleccion.sel_funcion_datos_esp VALUES (25, 3, false, true, 491, 28, 'RTRINIDAD', '2012-11-25 13:04:58.062', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------


--------------------------------------------		Administrar - Gestion de Evaluaciones de Desempeño 		------------------------------------------------------

													--		PLANTILLA RESULTADOS INDIVIDUALES		--

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: datos_especificos; Type: TABLE DATA; Schema: seleccion; Owner: postgres
-- Falta el registro de abajo para que el modal pueda tener algún dato cargado en el comboBox;
-- esto se aplica cuando se CARGA RESULTADOS plantilla RESULTADOS INDIVIDUALES en la columna adjunto pestaña adjuntar de cada empleado

INSERT INTO seleccion.datos_especificos VALUES (1496, 'CONSTANCIA DE EVALUACION', 'EVAL_R', NULL, 2, true, 'RTRINIDAD', '2014-09-10 16:36:50.323', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------



------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: sel_funcion_datos_esp; Type: TABLE DATA; Schema: seleccion; Owner: postgres
-- Registro faltante para cargar algún valor dentro del ComboBox en Adjuntar Documentos; 1144 RESULTADOS INDIVIDUALES; 
-- 524 CARGAR RESULTADOS DE EVALUACION METODOLOGIA, con su url 'cargarResultEvalMetodo571'

INSERT INTO seleccion.sel_funcion_datos_esp VALUES (26, 2, false, true, 524, 1144, 'RTRINIDAD', '2012-11-25 13:40:51.005', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

													--		PLANTILLA RESULTADOS POR AREA		--

------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Data for Name: funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- 735 CARGAR RESULTADOS DE EVALUACION METODOLOGIA, con su url 'cargarResultEvalMetodo571Area' se inserta este registro para poder diferenciar del Individual

INSERT INTO seguridad.funcion VALUES (735, 'CARGAR RESULTADOS DE EVALUACION METODOLOGIA', 'cargarResultEvalMetodo571Area', 'EVALUACION', true);
------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Data for Name: sel_funcion_datos_esp; Type: TABLE DATA; Schema: seleccion; Owner: postgres
-- Registro faltante para cargar algún valor dentro del ComboBox en Adjuntar Documentos; 1143 RESULTADOS POR AREA; 
-- 735 CARGAR RESULTADOS DE EVALUACION METODOLOGIA, con su url 'cargarResultEvalMetodo571Area'

INSERT INTO seleccion.sel_funcion_datos_esp VALUES (27, 2, false, true, 735, 1143, 'RTRINIDAD', '2012-11-25 13:40:51.005', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

													--		PLANTILLA RESULTADOS 	PDEC		--

------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Data for Name: sel_funcion_datos_esp; Type: TABLE DATA; Schema: seleccion; Owner: postgres
-- Registro faltante para cargar algún valor dentro del ComboBox en Adjuntar Documentos; 1140 PDEC; 
-- 559 CARGAR RESULTADOS - EVALUACION DE DESEMPEÑO PDEC, con su url 'cargarResultEvalPDEC572FC'

INSERT INTO seleccion.sel_funcion_datos_esp VALUES (28, 2, false, true, 559, 1140, 'RTRINIDAD', '2012-11-25 13:40:51.005', NULL, NULL);
------------------------------------------------------------------------------------------------------------------------------------------------------------------

													--		PLANTILLA RESULTADOS PERCEPCION		--

------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Data for Name: sel_funcion_datos_esp; Type: TABLE DATA; Schema: seleccion; Owner: postgres
-- Registro faltante para cargar algún valor dentro del ComboBox en Adjuntar Documentos; 1141 PERCEPCION; 
-- 588 CARGAR RESULTADOS - EVALUACION DE DESEMPEÑO INDIVIDUAL, con su url 'cargarResultEvalPerce608FC'

INSERT INTO seleccion.sel_funcion_datos_esp VALUES (29, 2, false, true, 588, 1141, 'RTRINIDAD', '2012-11-25 13:40:51.005', NULL, NULL);
------------------------------------------------------------------------------------------------------------------------------------------------------------------

--PARTE FINAL

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: rol_funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- Se asigna la función a un rol, en este caso el de RTRINIDAD -- (3); -- permite acceder a REPORTE DE PLANES DE MEJORA

INSERT INTO seguridad.rol_funcion VALUES (1288, 724, 3);
------------------------------------------------------------------------------------------------------------------------------------------------------------------

---			EXTRA		BIFURCACIÓN FASE 3 		----------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Data for Name: actividad_proceso; Type: TABLE DATA; Schema: proceso; Owner: postgres
-- El valor 4 asigna EVALUACION_DESEMPENO; el valor 113 REVISAR_EVALUACION.

INSERT INTO proceso.actividad_proceso VALUES (44, 3.00, 'D', '', 113, 4, true, 'RTRINIDAD', '2012-08-27 13:45:26.851', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: proc_actividad_rol; Type: TABLE DATA; Schema: proceso; Owner: postgres
-- Esta inserción permite saber el rol del Usuario Actual; el valor 44 hace referencia al registro en proceso.actividad_proceso.

INSERT INTO proceso.proc_actividad_rol VALUES (53, 3, 44, true, 'RTRINIDAD', '2014-08-25 00:00:00', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

UPDATE 
	proceso.actividad
SET
	cod_actividad = 'ASIGNAR_PLANTILLA'
WHERE
	cod_actividad = 'ASIGNAR_PLANTILLAS';

-- Data for Name: funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- 735 CARGAR RESULTADOS DE EVALUACION METODOLOGIA, con su url 'cargarResultEvalMetodo571Area' se inserta este registro para poder diferenciar del Individual


															-- ADJUNTAR REGLAMENTO NUEVO FASE 2 DEL DIAGRAMA--

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- Nuevo registro para adjuntar reglamento en la fase 2 del diagrama

INSERT INTO seguridad.funcion VALUES (736, 'ADJUNTAR REGLAMENTO', 'adjuntarReglamento', 'EVALUACION', true);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: datos_especificos; Type: TABLE DATA; Schema: seleccion; Owner: postgres
-- Falta el registro de abajo para que el modal pueda tener algún dato cargado en el comboBox;
-- esto se aplica cuando se CARGA RESULTADOS plantilla RESULTADOS INDIVIDUALES en la columna adjunto pestaña adjuntar de cada empleado

INSERT INTO seleccion.datos_especificos VALUES (1497, 'REGLAMENTO', NULL, NULL, 2, true, 'RTRINIDAD', '2014-09-11 10:31:52.105', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Data for Name: sel_funcion_datos_esp; Type: TABLE DATA; Schema: seleccion; Owner: postgres
-- Registro faltante para cargar algún valor dentro del ComboBox en Adjuntar Documentos; 1496 REGLAMENTO; 
-- 736 ADJUNTAR REGLAMENTO, con su url 'adjuntarReglamento'

INSERT INTO seleccion.sel_funcion_datos_esp VALUES (30, 2, true, true, 736, 1497, 'RTRINIDAD', '2014-09-11 10:31:51.005', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

													--		PLANTILLA RESULTADOS POR GESTION		--

------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Data for Name: funcion; Type: TABLE DATA; Schema: seguridad; Owner: postgres
-- 737 CARGAR RESULTADOS DE EVALUACION METODOLOGIA, con su url 'cargarResultEvalMetodo571Gestion' se inserta este registro para poder diferenciar del Individual

INSERT INTO seguridad.funcion VALUES (737, 'CARGAR RESULTADOS DE EVALUACION METODOLOGIA', 'cargarResultEvalMetodo571Gestion', 'EVALUACION', true);
------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Data for Name: sel_funcion_datos_esp; Type: TABLE DATA; Schema: seleccion; Owner: postgres
-- Registro faltante para cargar algún valor dentro del ComboBox en Adjuntar Documentos; 1142 POR CONTRATO DE GESTION; 
-- 735 CARGAR RESULTADOS DE EVALUACION METODOLOGIA, con su url 'cargarResultEvalMetodo571Gestion'

INSERT INTO seleccion.sel_funcion_datos_esp VALUES (31, 2, false, true, 737, 1142, 'RTRINIDAD', '2012-11-25 13:40:51.005', NULL, NULL);

------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Data for Name: evaluacion_desempeno; Type: TABLE DATA; Schema: eval_desempeno; Owner: postgres
-- No es necesario que el Usuario pertenezca a una UO al crear la Evaluación.

ALTER TABLE eval_desempeno.evaluacion_desempeno
   ALTER COLUMN id_configuracion_uo_det DROP NOT NULL;

------------------------------------------------------------------------------------------------------------------------------------------------------------------