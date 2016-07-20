
--TABLA ACTIVIDAD, INSERCION DE ACTIVIDADES PARA MOVILIDAD

--1ro aseguramos que el seq esté correcto
SELECT setval('proceso.tbl_actividad_id_actividad_seq', (select max(id_actividad) from proceso.actividad));

INSERT INTO proceso.actividad(id_actividad, descripcion, activo, usu_alta, fecha_alta, usu_mod, fecha_mod,
 cod_actividad, tipo, descripcion_historial, nro_actividad, valor)
    VALUES (nextVal('proceso.tbl_actividad_id_actividad_seq'), 'SOLICITAR TRASLADO', TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL,
        'SOLICITAR_TRASLADO', 'TRASLADO SICCA', NULL, '001', NULL);

INSERT INTO proceso.actividad(id_actividad, descripcion, activo, usu_alta, fecha_alta, usu_mod, fecha_mod,
 cod_actividad, tipo, descripcion_historial, nro_actividad, valor)
    VALUES (nextVal('proceso.tbl_actividad_id_actividad_seq'), 'RESPONDER TRASLADO', TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL,
        'RESPONDER_TRASLADO', 'TRASLADO SICCA', NULL, '002', NULL);

INSERT INTO proceso.actividad(id_actividad, descripcion, activo, usu_alta, fecha_alta, usu_mod, fecha_mod,
 cod_actividad, tipo, descripcion_historial, nro_actividad, valor)
    VALUES (nextVal('proceso.tbl_actividad_id_actividad_seq'), 'REVISAR SOLICITUD SFP', TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL,
        'REVISAR_SOLICITUD_SFP', 'TRASLADO SICCA', NULL, '003', NULL);

INSERT INTO proceso.actividad(id_actividad, descripcion, activo, usu_alta, fecha_alta, usu_mod, fecha_mod,
 cod_actividad, tipo, descripcion_historial, nro_actividad, valor)
    VALUES (nextVal('proceso.tbl_actividad_id_actividad_seq'), 'REGISTRAR TRASLADO TEMPORAL', TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL,
        'REGISTRAR_TRASLADO_TEMPORAL', 'TRASLADO SICCA', NULL, '004', NULL);

INSERT INTO proceso.actividad(id_actividad, descripcion, activo, usu_alta, fecha_alta, usu_mod, fecha_mod,
 cod_actividad, tipo, descripcion_historial, nro_actividad, valor)
    VALUES (nextVal('proceso.tbl_actividad_id_actividad_seq'), 'REGISTRAR TRASLADO SIN LINEA', TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL,
        'REGISTRAR_TRASLADO_SIN_LINEA', 'TRASLADO SICCA', NULL, '005', NULL);

INSERT INTO proceso.actividad(id_actividad, descripcion, activo, usu_alta, fecha_alta, usu_mod, fecha_mod,
 cod_actividad, tipo, descripcion_historial, nro_actividad, valor)
    VALUES (nextVal('proceso.tbl_actividad_id_actividad_seq'), 'REGISTRAR TRASLADO CON LINEA', TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL,
        'REGISTRAR_TRASLADO_CON_LINEA', 'TRASLADO SICCA', NULL, '006', NULL);

INSERT INTO proceso.actividad(id_actividad, descripcion, activo, usu_alta, fecha_alta, usu_mod, fecha_mod,
 cod_actividad, tipo, descripcion_historial, nro_actividad, valor)
    VALUES (nextVal('proceso.tbl_actividad_id_actividad_seq'), 'REGISTRAR TRASLADADO POR CONCURSO', TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL,
        'REGISTRAR_TRASLADADO_POR_CONCURSO', 'TRASLADO SICCA', NULL, '007', NULL);

