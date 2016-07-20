INSERT INTO seleccion.referencias(
            id_referencias, dominio, desc_abrev, desc_larga, valor_alf, valor_num, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_referencias_id_referencias_seq'), 'URL_MODULOS','URL_JURIDICO','http://10.1.40.92:8080/sicca2',NULL, NULL, 
            TRUE, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);
