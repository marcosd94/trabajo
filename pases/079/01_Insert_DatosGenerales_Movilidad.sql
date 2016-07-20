
--TABLA DATOS_GENERALES, INSERCION DE ESTADOS SOLICITUD MOVILIDAD

--1ro aseguramos que el seq esté correcto
SELECT setval('seleccion.sel_datos_generales_id_datos_generales_seq', (select max(id_datos_generales) from seleccion.datos_generales));

INSERT INTO seleccion.datos_generales(id_datos_generales, descripcion, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_generales_id_datos_generales_seq'), 'ESTADOS SOLICITUD MOVILIDAD', TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);




--TABLA PROCESO, INSERCION DE PROCESO TRASLADO SICCA

--1ro aseguramos que el seq esté correcto
SELECT setval('proceso.tbl_proceso_id_proceso_seq', (select max(id_proceso) from proceso.proceso));

INSERT INTO proceso.proceso(id_proceso, descripcion, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_proceso_id_proceso_seq'), 'TRASLADO SICCA', TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);
