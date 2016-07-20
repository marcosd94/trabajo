--NUEVAS REFERENCIAS
INSERT INTO seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_alf, valor_num, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
 VALUES ((SELECT MAX(ID_REFERENCIAS) FROM SELECCION.REFERENCIAS)+1, 'ESTADOS_GRUPO', 'TACHAS_RECLAMOS_MODIF', 'TACHAS,RECLAMOS Y MODIFICACIONES', '', 37, true, 'ADMIN', '2014-12-11 10:00:00', NULL, NULL); 

 --NUEVA ACTIVIDAD
 INSERT INTO proceso.actividad (id_actividad, descripcion, activo, usu_alta, fecha_alta, usu_mod, fecha_mod, cod_actividad, tipo, descripcion_historial, nro_actividad, valor) 
VALUES ((select max(id_actividad) from proceso.actividad)+1, 'TACHAS, RECLAMOS Y MODIFICACIONES', true, 'ADMIN', '2014-12-11 13:30', NULL, NULL, 'TACHAS_RECLAMOS_MODIF', 'GRUPO', NULL, '119', NULL);

--ACTIVIDAD_PROCESO
INSERT INTO proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion, id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod) 
VALUES ((select max(id_actividad_proceso) from proceso.actividad_proceso)+1, 5.00, 'D', 'Periodo de Tachas, Reclamos y Modificaciones', (select max(id_actividad) from proceso.actividad), 1, true, 'ADMIN', '2014-12-11 13:30', 'ADMIN', '2014-12-11 13:30');

--ROLES PARA LA ACTIVIDAD
INSERT INTO proceso.proc_actividad_rol (id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod) 
VALUES ((select max(id_proc_actividad_rol) from proceso.proc_actividad_rol)+1, 3, (select max(id_actividad_proceso) from proceso.actividad_proceso), true, 'ADMIN', '2014-12-11 13:30', NULL, NULL);

INSERT INTO proceso.proc_actividad_rol (id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
VALUES ((select max(id_proc_actividad_rol) from proceso.proc_actividad_rol)+1, 27, (select max(id_actividad_proceso) from proceso.actividad_proceso), true, 'ADMIN', '2014-12-11 13:30', NULL, NULL);

INSERT INTO proceso.proc_actividad_rol (id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod) 
VALUES ((select max(id_proc_actividad_rol) from proceso.proc_actividad_rol)+1, 26, (select max(id_actividad_proceso) from proceso.actividad_proceso), true, 'ADMIN', '2014-12-11 13:30', NULL, NULL);


--REFERENCIAS PARA LOS DOCUMENTOS A SER PUBLICADOS
INSERT INTO seleccion.datos_especificos VALUES ((select max (id_datos_especificos) from seleccion.datos_especificos)+1, 'Nota de Postulante', 'NOTA_POSTULANTE', NULL, 2, true, 'ADMIN', '2014-12-16 10:00:00', NULL, NULL);
INSERT INTO seleccion.datos_especificos VALUES ((select max (id_datos_especificos) from seleccion.datos_especificos)+1, 'Nota de Comisión', 'NOTA_COMISION', NULL, 2, true, 'ADMIN', '2014-12-16 10:00:00', NULL, NULL);
--ALTER DE EVAL_REFERENCIAL_POSTULANTE PARA PODER GUARDAR LOS SELECCIONADOS  Y ELEGIBLES PARA TERNA Y MERITO
ALTER TABLE seleccion.eval_referencial_postulante ADD COLUMN lista_corta_replica boolean;
ALTER TABLE seleccion.eval_referencial_postulante ADD COLUMN seleccionado_replica boolean;