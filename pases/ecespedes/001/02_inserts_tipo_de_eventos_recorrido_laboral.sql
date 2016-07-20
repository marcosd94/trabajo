INSERT INTO seleccion.datos_generales(
            id_datos_generales, descripcion, 
            			activo, usu_alta, fecha_alta,usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_generales_id_datos_generales_seq'), 'TIPO DE EVENTO RECORRIDO LABORAL', 
    			TRUE, 'ECESPEDES', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, 
				valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'CONFIRMACION EN EL CARGO', 
    			NULL, NULL, (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE EVENTO RECORRIDO LABORAL'), 
    		            TRUE, 'ECESPEDES', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, 
				valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'DESIGNACION DE NUEVAS FUNCIONES', 
    			NULL, NULL, (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE EVENTO RECORRIDO LABORAL'), 
    		            TRUE, 'ECESPEDES', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, 
				valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'RECATEGORIZACION', 
    			NULL, NULL, (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE EVENTO RECORRIDO LABORAL'), 
    		            TRUE, 'ECESPEDES', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, 
				valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'DESIGNACION DE CARGOS', 
    			NULL, NULL, (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE EVENTO RECORRIDO LABORAL'), 
    		            TRUE, 'ECESPEDES', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, 
				valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'DESIGNACION DE CATEGORIAS', 
    			NULL, NULL, (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE EVENTO RECORRIDO LABORAL'), 
    		            TRUE, 'ECESPEDES', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);


