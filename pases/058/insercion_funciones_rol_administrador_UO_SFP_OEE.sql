-- id_rol = 28: Administrador Unidades Organizativas SFP
-- id_rol = 29: Administrador Unidades Organizativas OEE

--ajustar secuencial (just in case...)
SELECT setval('seguridad.rol_funcion_id_rol_funcion_seq', (select max(seguridad.rol_funcion.id_rol_funcion) from seguridad.rol_funcion));

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (60, 28); --funcion 60: cambiar contraseña 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (60, 29); --funcion 60: cambiar contraseña 


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (91, 28); --funcion 91: LISTAR DETALLE CONFIGURACION UNIDADES ORGANIZATIVAS

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (91, 29); --funcion 91: LISTAR DETALLE CONFIGURACION UNIDADES ORGANIZATIVAS


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (93, 28); --funcion 93: EDITAR DETALLE CONFIGURACION UNIDADES ORGANIZATIVAS

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (93, 29); --funcion 93: EDITAR DETALLE CONFIGURACION UNIDADES ORGANIZATIVAS


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (4, 28); --funcion 4: LISTAR CLASIFICADOR UO

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (4, 29); --funcion 4: LISTAR CLASIFICADOR UO

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (92, 28); --VER UNIDADES ORGANIZATIVAS (92)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (92, 29); --VER UNIDADES ORGANIZATIVAS (92)


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (107, 28); --LISTAR OFICINA (107)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (107, 29); --LISTAR OFICINA (107)


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (106, 28); --EDITAR OFICINA (106)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (106, 29); --EDITAR OFICINA (106)


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (94, 28); --AGREGAR PROCESO DE GESTIÓN (94)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (94, 29); --AGREGAR PROCESO DE GESTIÓN (94)

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES ((select id_funcion from seguridad.funcion where funcion.url = 'gestionarInsercionMasiva'), 28); --INSERCION MASIVA 



