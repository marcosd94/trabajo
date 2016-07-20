-- id_rol = 26: ADMINISTRADOR DE CONCURSOS Y PERFILES SFP ---------------
--- Homologación SFP
/*

--1ro aseguramos que el seq esté correcto
SELECT setval('seguridad.rol_funcion_id_rol_funcion_seq', (select max(id_rol_funcion) from seguridad.rol_funcion)); --proceso.proc_actividad_rol;


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (60, 26); --funcion 60: cambiar contraseña

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (103, 26); --funcion 103: LISTAR CPT

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (20, 26); --funcion 21: EDIT TIPO NOMBRAMIENTO



INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (22, 26); --funcion 22: VER TIPOS DE NOMBRAMIENTO

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (21, 26); --funcion 21: LISTAR TIPO NOMBRAMIENTO


--gestionar homologación de CPT Específico
INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (731, 26); --funcion 731: HOMOLOGACION CTP ESPECIFICO


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (174, 26); --funcion 174: GESTIONAR BANDEJA DE ENTRADA  


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (208, 26); --funcion 208: VERIFICAR PERFIL MATRIZ HOMOLOGACION  




INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (220, 26); --funcion 220: ADMIN PERFIL MATRIZ HOMOLOGACION INST EDIT

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (312, 26); --funcion 312: CONSULTAR HISTORIAL DE CONCURSO/GRUPO POR SFP




--Bandeja de Tareas---


SELECT setval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq', (select max(id_proc_actividad_rol) from proceso.proc_actividad_rol)); --proceso.proc_actividad_rol;
---APROBAR HOMOLOGACION SFP

INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (26, 5, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 5: APROBAR HOMOLOGACION SFP (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:3)


--- Recibir postulaciones------
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (26, 27, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 27: RECIBIR POSTULACIONES (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:10)


--Ver, Crear y editar plantilla documental
INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (142, 26); --funcion 142: LISTAR PLANTILLA DE MATRIZ DOCUMENTAL


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (144, 26); --funcion 144: LEDITAR PLANTILLA DE MATRIZ DOCUMENTAL

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (145, 26); --funcion 145: EDITAR DETALLE PLANTILLA MATRIZ DOCUMENTAL


--Ver, crear y editar plantilla referencial
INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (146, 26); --LISTAR PLANTILLA MATRIZ REFERENCIAL (146)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (147, 26); --VER PLANTILLA MATRIZ REFERENCIAL (147)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (148, 26); --EDITAR PLANTILLA MATRIZ REFERENCIAL (148)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (152, 26); --CARGAR FACTOR DE EVALUACION (152)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (153, 26); --VER FACTOR DE EVALUACION (153)
*/

-- Historial de Concursos.
INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (312, 26); --CONSULTAR HISTORIAL DE CONCURSOS (312)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (313, 26); --VER HISTORIAL DE CONCURSO/GRUPO POR SFP (313)









