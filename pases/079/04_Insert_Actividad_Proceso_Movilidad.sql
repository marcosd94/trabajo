
--TABLA ACTIVIDAD_PROCESO, INSERCION DE ENLACES DE ACTIVIDADES PARA EL PROCESO TRASLADO SICCA DEL MODULO MOVILIDAD

--1ro aseguramos que el seq esté correcto
SELECT setval('proceso.tbl_actividad_proceso_id_actividad_proceso_seq', (select max(id_actividad_proceso) from proceso.actividad_proceso));

INSERT INTO proceso.actividad_proceso(id_actividad_proceso, plazo_actividad, unidad_plazo, observacion, id_actividad, id_proceso, 
    activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_actividad_proceso_id_actividad_proceso_seq'), 1.00, 'D', NULL, 121, 5, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
     NULL, NULL);

INSERT INTO proceso.actividad_proceso(id_actividad_proceso, plazo_actividad, unidad_plazo, observacion, id_actividad, id_proceso, 
    activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_actividad_proceso_id_actividad_proceso_seq'), 1.00, 'D', NULL, 122, 5, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
     NULL, NULL);

INSERT INTO proceso.actividad_proceso(id_actividad_proceso, plazo_actividad, unidad_plazo, observacion, id_actividad, id_proceso, 
    activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_actividad_proceso_id_actividad_proceso_seq'), 1.00, 'D', NULL, 123, 5, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
     NULL, NULL);

INSERT INTO proceso.actividad_proceso(id_actividad_proceso, plazo_actividad, unidad_plazo, observacion, id_actividad, id_proceso, 
    activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_actividad_proceso_id_actividad_proceso_seq'), 1.00, 'D', NULL, 124, 5, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
     NULL, NULL);

INSERT INTO proceso.actividad_proceso(id_actividad_proceso, plazo_actividad, unidad_plazo, observacion, id_actividad, id_proceso, 
    activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_actividad_proceso_id_actividad_proceso_seq'), 1.00, 'D', NULL, 125, 5, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
     NULL, NULL);

INSERT INTO proceso.actividad_proceso(id_actividad_proceso, plazo_actividad, unidad_plazo, observacion, id_actividad, id_proceso, 
    activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_actividad_proceso_id_actividad_proceso_seq'), 1.00, 'D', NULL, 126, 5, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
     NULL, NULL);

INSERT INTO proceso.actividad_proceso(id_actividad_proceso, plazo_actividad, unidad_plazo, observacion, id_actividad, id_proceso, 
    activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_actividad_proceso_id_actividad_proceso_seq'), 1.00, 'D', NULL, 127, 5, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
     NULL, NULL);