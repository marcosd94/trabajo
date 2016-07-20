INSERT INTO seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_alf, valor_num, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
 VALUES ((SELECT MAX(ID_REFERENCIAS) FROM SELECCION.REFERENCIAS)+1, 'ESTADOS_GRUPO', 'PUBLICACION_LISTA_LARGA', 'LISTA LARGA', '', 1003, true, 'rveron', localtimestamp, NULL, NULL);

INSERT INTO seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_alf, valor_num, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
 VALUES ((SELECT MAX(ID_REFERENCIAS) FROM SELECCION.REFERENCIAS)+1, 'ESTADOS_GRUPO', 'PUBLICACION_LISTA_CORTA', 'LISTA CORTA', '', 1004, true, 'rveron', localtimestamp, NULL, NULL);


INSERT INTO seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_alf, valor_num, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
 VALUES ((SELECT MAX(ID_REFERENCIAS) FROM SELECCION.REFERENCIAS)+1, 'ESTADOS_GRUPO', 'PUBLICACION_LISTA_SELECCIONADO', 'LISTA SELECCIONADOS', '', 1005, true, 'rveron', localtimestamp, NULL, NULL); 

UPDATE SELECCION.REFERENCIAS SET DESC_LARGA = 'EVALUACION', USU_MOD = 'rveron', FECHA_MOD = localtimestamp WHERE VALOR_NUM = 1001;
UPDATE SELECCION.REFERENCIAS SET DESC_LARGA = 'FINALIZADO', USU_MOD = 'rveron' , FECHA_MOD = localtimestamp WHERE VALOR_NUM = 1002;