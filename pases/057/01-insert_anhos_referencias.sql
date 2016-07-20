--NUEVAS REFERENCIAS
INSERT INTO seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_alf, valor_num, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
 VALUES ((SELECT MAX(ID_REFERENCIAS) FROM SELECCION.REFERENCIAS)+1, 'ANHO', NULL, NULL, NULL, 2014, true, 'ADMIN', '2015-04-06 10:15:00', NULL, NULL); 

INSERT INTO seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_alf, valor_num, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
 VALUES ((SELECT MAX(ID_REFERENCIAS) FROM SELECCION.REFERENCIAS)+1, 'ANHO', NULL, NULL, NULL, 2015, true, 'ADMIN', '2015-04-06 10:15:00', NULL, NULL); 