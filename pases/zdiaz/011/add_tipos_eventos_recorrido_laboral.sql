INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, 
				valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'OTROS', 
    			NULL, NULL, (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE EVENTO RECORRIDO LABORAL'), 
    		            TRUE, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, 
				valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'AUTORIZACIÓN DE PAGOS', 
    			NULL, NULL, (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE EVENTO RECORRIDO LABORAL'), 
    		            TRUE, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, 
				valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'HORARIO DIFERENCIADO', 
    			NULL, NULL, (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE EVENTO RECORRIDO LABORAL'), 
    		            TRUE, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);