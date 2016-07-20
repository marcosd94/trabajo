INSERT INTO seleccion.referencias(
            id_referencias, dominio, desc_abrev, desc_larga, valor_alf, valor_num, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES ((SELECT MAX(ID_REFERENCIAS)FROM SELECCION.REFERENCIAS)+1,'ESTADOS_CONCURSO','CONCURSO CANCELADO', 'CONCURSO CANCELADO', NULL, 5, 
            TRUE, 'ADMIN', '2014-08-18 16:33:00', NULL, NULL);
