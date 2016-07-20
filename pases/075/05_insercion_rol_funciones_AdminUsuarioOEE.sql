--1ro aseguramos que el seq esté correcto
SELECT setval('seguridad.rol_funcion_id_rol_funcion_seq', (select max(id_rol_funcion) from seguridad.rol_funcion));

--Funciones---

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (57, 36); --funcion 56: VER USUARIO

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (58, 36); --funcion 57: LISTAR USUARIOS

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (59, 36); --funcion 58: EDITAR USUARIO