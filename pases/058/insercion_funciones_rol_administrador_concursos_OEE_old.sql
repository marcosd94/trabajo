
-- id_rol = 27: Administrador Concursos y Perfiles OEE


--1ro aseguramos que el seq esté correcto
SELECT setval('seguridad.rol_funcion_id_rol_funcion_seq', (select max(id_rol_funcion) from seguridad.rol_funcion)); --proceso.proc_actividad_rol;

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (103, 27); --funcion 103: LISTAR CPT

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (20, 27); --funcion 21: EDIT TIPO NOMBRAMIENTO

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (21, 27); --funcion 21: LISTAR TIPO NOMBRAMIENTO


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (22, 27); --funcion 22: VER TIPOS DE NOMBRAMIENTO

--gestionar homologación de CPT Específico
INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (731, 27); --funcion 731: HOMOLOGACION CTP ESPECIFICO

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (100, 27); --funcion 100: BUSCAR CPT

-- Puesto de Trabajo
INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (113, 27); --funcion 113: LISTAR PUESTOS DE TRABAJO

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (116, 27); --funcion 116: EDITAR PUESTOS DE TRABAJO

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (112, 27); --LISTAR VACANCIAS (112)
-- Puesto de Trabajo

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (158, 27); --funcion 158: iniciar concurso

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (60, 27); --funcion 60: cambiar contraseña 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (159, 27); --funcion 60: EDITAR INICIAR CONCURSO 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (160, 27); --VER INICIAR CONCURSO (160)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (165, 27); --funcion 165: RESERVAR Y AGRUPAR PUESTOS 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (167, 27); --VER GRUPOS (167)


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (168, 27); --funcion 168: ASIGNAR CATEGORIA

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (166, 27); --funcion 166: CREAR Y EDITAR GRUPOS 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (174, 27); --funcion 174: GESTIONAR BANDEJA DE ENTRADA 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (172, 27); --funcion 172: ADMINISTRAR CARGA GRUPO 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (186, 27); --funcion 186: CARGAR DATOS DEL GRUPO DE PUESTOS

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (188, 27); --funcion 188: BASES Y CONDICIONES

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (240, 27); --funcion 240: LISTAR CONFIGURACION DE MATRIZ DOCUMENTAL  

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (241, 27); --funcion 240: LISTAR CONFIGURACION DE MATRIZ DOCUMENTAL  

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (242, 27); --funcion 242: EDITAR CONFIGURACION DE MATRIZ DOCUMENTAL

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (250, 27); --funcion 250: EDITAR OTROS DATOS DE BASES Y CONDICIONES

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (189, 27); --funcion 189: FUNCIONES - BASES Y CONDICIONES 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (190, 27); --funcion 190: REQ MIN - BASES Y CONDICIONES

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (191, 27); --funcion 191: CONCEPTO PAGO - BASES Y CONDICIONES

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (422, 27); --funcion 422: ASIGNAR CATEGORIA POR GRUPO

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (193, 27); --funcion 193: SEGURIDAD - BASES Y CONDICIONES

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (209, 27); --funcion 209: ENVIAR HOMOLOGACION PERFIL MATRIZ  

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (220, 27); --funcion 220: ADMIN PERFIL MATRIZ HOMOLOGACION INST EDIT

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (308, 27); --funcion 308: MODIFICAR DATOS DEL CONCURSO 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (318, 27); --funcion 318: EDITAR FECHAS GRUPO PUESTO 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (312, 27); --funcion 312: CONSULTAR HISTORIAL DE CONCURSO/GRUPO POR SFP


--1ro aseguramos que el seq esté correcto
SELECT setval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq', (select max(id_proc_actividad_rol) from proceso.proc_actividad_rol)); --proceso.proc_actividad_rol;

-- Bandeja de tarea
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (27, 1, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 1: CARGA POR GRUPO (en tabla proceso.actividad_proceso)

--HOMOLOGACION OEE a la bandeja de Entrada del rol
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (27, 7, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 7: HOMOLOGACION OEE (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:4)

--- Modificar Datos del Concurso------
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (27, 26, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 26: MODIFICAR DATOS DE CONCURSO (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:7)

--- Recibir postulaciones------
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (27, 27, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 27: RECIBIR POSTULACIONES (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:10)










 