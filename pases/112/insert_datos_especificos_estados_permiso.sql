--TABLA DATOS_ESPECIFICOS, INSERCION DE REGISTROS PARA ESTADOS DE PERMISO
--1ro aseguramos que el seq esté correcto
SELECT setval('seleccion.sel_datos_especificos_id_datos_especificos_seq', (select max(id_datos_especificos) from seleccion.datos_especificos));

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'APROBADO', NULL, NULL, 71, 
            TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'PENDIENTE', NULL, NULL, 71, 
            TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'RECHAZO', NULL, NULL, 71, 
            TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

--FINALMENTE, ELIMINACION DE REGITRO DE PRUEBA 'ESTADO 1', SI EXISTIESE
UPDATE seleccion.datos_especificos SET activo = FALSE WHERE descripcion = 'ESTADO 1' and id_datos_generales = 71;