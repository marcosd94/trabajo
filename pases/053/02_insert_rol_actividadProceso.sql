--id_rol 28: ADMINISTRADOR DE UNIDADES ORGANIZATIVAS SFP
--id_rol 29: ADMINISTRADOR DE UNIDADES ORGANIZATIVAS OEE
--AGREGAR LA FUNCION 'LISTADO DE PERSONA' DEL MODULO SELECCION AL ROL ADMINISTRADOR UNIDADES ORGANIZATIVAS OEE

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (28, 14, TRUE, 'SORUE', LOCALTIMESTAMP);--id_actividad_proceso 14: CARGAR CAPACITACION/ENVIAR APROBACION (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:102)

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (29, 14, TRUE, 'SORUE', LOCALTIMESTAMP);


INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (28, 15, TRUE, 'SORUE', LOCALTIMESTAMP);--id_actividad_proceso 15: APROBAR CAPACITACION (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:103)

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (29, 15, TRUE, 'SORUE', LOCALTIMESTAMP);


INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (28, 16, TRUE, 'SORUE', LOCALTIMESTAMP);--id_actividad_proceso 16: REVISAR CAPACITACION (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:104)

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (29, 16, TRUE, 'SORUE', LOCALTIMESTAMP);


INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (28, 17, TRUE, 'SORUE', LOCALTIMESTAMP);--id_actividad_proceso 17: INSCRIPCION/LISTA (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:110)

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (29, 17, TRUE, 'SORUE', LOCALTIMESTAMP);


INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (28, 18, TRUE, 'SORUE', LOCALTIMESTAMP);--id_actividad_proceso 18: REPROGRAMAR/CANCELAR CAPACITACION (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:107)

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (29, 18, TRUE, 'SORUE', LOCALTIMESTAMP);


INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (28, 19, TRUE, 'SORUE', LOCALTIMESTAMP);--id_actividad_proceso 19: RECEPCIONAR POSTULACIONES/INSCRIPCIONES (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:106)

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (29, 19, TRUE, 'SORUE', LOCALTIMESTAMP);


INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (28, 20, TRUE, 'SORUE', LOCALTIMESTAMP);--id_actividad_proceso 20: PUBLICAR CAPACITACION (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:105)

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (29, 20, TRUE, 'SORUE', LOCALTIMESTAMP);


INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (28, 21, TRUE, 'SORUE', LOCALTIMESTAMP);--id_actividad_proceso 21: REGSITRAR COMISION (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:108)

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (29, 21, TRUE, 'SORUE', LOCALTIMESTAMP);


INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (28, 22, TRUE, 'SORUE', LOCALTIMESTAMP);--id_actividad_proceso 22: PUBLICAR SELECCIONADOS (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:111)

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (29, 22, TRUE, 'SORUE', LOCALTIMESTAMP);


INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (28, 23, TRUE, 'SORUE', LOCALTIMESTAMP);--id_actividad_proceso 23: EVALUAR POSTULANTES (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:109)

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (29, 23, TRUE, 'SORUE', LOCALTIMESTAMP);