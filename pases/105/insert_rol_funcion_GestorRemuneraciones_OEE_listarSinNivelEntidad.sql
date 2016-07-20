--1ro aseguramos que el seq esté correcto
SELECT setval('seguridad.rol_funcion_id_rol_funcion_seq', (select max(id_rol_funcion) from seguridad.rol_funcion));

--Funciones---

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (81, 39); --funcion 81: LISTAR SIN NIVEL ENTIDAD para rol GESTOR REMUNERACIONES SFP