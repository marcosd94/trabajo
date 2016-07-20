--TABLA DATOS_ESPECIFICOS, INSERCION DE REGISTRO PARA TIPO DE DOCUMENTO "OTROS" PARA DOCUMENTOS ADJUNTOS EN LEGAJO -FAMILIARES
--1ro aseguramos que el seq esté correcto
SELECT setval('seleccion.sel_datos_especificos_id_datos_especificos_seq', (select max(id_datos_especificos) from seleccion.datos_especificos));

INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'OTROS', 'FPOS', NULL, 2, 
            TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);