-- id_rol = 23: Evaluador de Concursos



--1ro aseguramos que el seq esté correcto
SELECT setval('seguridad.rol_funcion_id_rol_funcion_seq', (select max(id_rol_funcion) from seguridad.rol_funcion)); --proceso.proc_actividad_rol;


--Funciones---

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (60, 23); --funcion 60: cambiar contraseña

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (295, 23); --DISMINUIR PUESTOS(295)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (174, 23); --funcion 174: GESTIONAR BANDEJA DE ENTRADA

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (211, 23); --funcion 211: LISTAR EVALUACION DOCUMENTOS POSTULANTE

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (212, 23); --funcion 212: EDITAR EVALUACION DOCUMENTOS POSTULANTES 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (216, 23); --funcion 216: LISTA DE PUBLICACION LISTA LARGA 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (315, 23); --funcion 315: REALIZAR EVALUACIONES 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (222, 23); --funcion 315: LISTA DE PUBLICACION LISTA CORTA

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (224, 23); --funcion REALIZAR ENTREVISTA FINAL (224)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (227, 23); --funcion LISTA EVALUACION DOCUMENTOS ADJUDICADOS (227)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (229, 23); --funcion EVALUAR ADJUDICADO (229)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (444, 23); --funcion AJUSTAR PUESTO POSTULANTE CONCURSO (444)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (225, 23); --funcion LISTA DE PUBLICACION DE SELECCIONADOS (225)


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (206, 23); --funcion FIRMAR RESOLUCION HOMOLOGACION (206)




--Bandeja de tarea---

--1ro aseguramos que el seq esté correcto
SELECT setval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq', (select max(id_proc_actividad_rol) from proceso.proc_actividad_rol)); --proceso.proc_actividad_rol;

-- Evaluación Documental
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (23, 29, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 29: EVALUACION DOCUMENTAL (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:11)

-- Publicación Lista Larga
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (23, 30, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 30: ELABORAR PUBLICACION LISTA LARGA(es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:12)

-- Evaluación OEE x Grupo
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (23, 31, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 31: REALIZAR EVALUACIONES OEE POR GRUPO (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:14)


-- Publicar lista corta
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (23, 32, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 32: ELABORAR PUBLICACION LISTA CORTA (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:15)

-- Entrevista Final con MAI
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (23, 13, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 13: REALIZAR ENTREVISTA FINAL MAI/OEE (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:17)

-- Evaluación documental de adjudicados
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (23, 33, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 33: VALIDAR MATRIZ DOCUMENT. ADJ. POR GRUPO (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:18)


-- PUBLICAR ADJUDICADOS
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (23, 34, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 34: PUBLICAR ADJUDICADOS (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:19)


-- FIRMA RESOLUCIÓN DE ADJUDICACIÓN
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (23, 35, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 35: FIRMAR RESOLUCION ADJUDICACION (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:21)





