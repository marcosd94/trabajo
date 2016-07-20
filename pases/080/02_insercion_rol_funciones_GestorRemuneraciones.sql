--1ro aseguramos que el seq esté correcto
SELECT setval('seguridad.rol_funcion_id_rol_funcion_seq', (select max(id_rol_funcion) from seguridad.rol_funcion));

--Funciones---

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (644, 37); --funcion 644: IMPORTACION MASIVA DE REMUNERACIONES POR NOMINA DE FUNCIONARIOS para rol GESTOR REMUNERACIONES SFP

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (81, 37); --funcion 644: LISTAR SIN NIVEL ENTIDAD para rol GESTOR REMUNERACIONES SFP

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (102, 37); --funcion 644: BUSCAR ENTIDAD para rol GESTOR REMUNERACIONES SFP

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (90, 37); --funcion 644: LISTAR DEPENDENCIAS para rol GESTOR REMUNERACIONES SFP

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (644, 38); --funcion 644: IMPORTACION MASIVA DE REMUNERACIONES POR NOMINA DE FUNCIONARIOS para rol GESTOR REMUNERACIONES OEE