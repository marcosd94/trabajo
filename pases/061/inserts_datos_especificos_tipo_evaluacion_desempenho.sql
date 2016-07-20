INSERT INTO seleccion.datos_especificos(
            id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'EVALUACION DE DESEMPEÑO', NULL, NULL, 4, 
            TRUE, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);
