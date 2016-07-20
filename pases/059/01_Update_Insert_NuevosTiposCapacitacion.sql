
--TABLA DATOS_ESPECIFICOS, MODIFICACION E INSERCION DE NUEVOS TIPOS DE CAPACITACIONES
UPDATE seleccion.datos_especificos SET descripcion = 'JORNADA / TALLER' WHERE descripcion = 'TALLER';
UPDATE seleccion.datos_especificos SET descripcion = 'CHARLA / CONFERENCIA' WHERE descripcion = 'CONFERENCIAS';

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'CURSO', NULL, NULL, 47, 
            TRUE, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'CAPACITACIÓN TÉCNICA', NULL, NULL, 47, 
            TRUE, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'CICLO DE CHARLAS', NULL, NULL, 47, 
            TRUE, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'CONGRESO', NULL, NULL, 47, 
            TRUE, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'SEMINARIO', NULL, NULL, 47, 
            TRUE, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);