

INSERT INTO seleccion.referencias(dominio, desc_abrev, desc_larga, valor_alf, valor_num, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES ('ESTADOS_GRUPO' ,'DESIERTO_CON_DOC', 'DESIERTO CON DOCUMENTO', NULL,38, TRUE,'ADMIN',(SELECT TIMESTAMP WITHOUT TIME ZONE 'now')  ,NULL,NULL);