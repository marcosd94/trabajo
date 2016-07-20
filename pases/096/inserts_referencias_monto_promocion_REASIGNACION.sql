

INSERT INTO seleccion.referencias(dominio, desc_abrev, desc_larga, valor_alf, valor_num, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES ('MONTO_PROMOCION' ,'MONTO_PROMOCION', 'MONTO DISCRIMINADOR PARA PROMOCION CATEGORIAL', NULL,100000, TRUE,'SORUE',(SELECT TIMESTAMP WITHOUT TIME ZONE 'now')  ,NULL,NULL);