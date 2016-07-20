INSERT INTO seleccion.sel_funcion_datos_esp (id_funcion_datos_esp, cantidad, obligatorio_s_n, activo, id_funcion, id_datos_especificos_tipo_doc, usu_alta, fecha_alta, usu_mod, fecha_mod)
 VALUES ((SELECT MAX(ID_FUNCION_DATOS_ESP) FROM SELECCION.SEL_FUNCION_DATOS_ESP)+1, 2, false, true, 356, 28, 'ADMIN', localtimestamp, NULL, NULL); 
