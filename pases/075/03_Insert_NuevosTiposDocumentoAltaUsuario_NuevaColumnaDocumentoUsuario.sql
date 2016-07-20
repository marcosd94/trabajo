
--TABLA DATOS_ESPECIFICOS, INSERCION DE NUEVO TIPO DE DOCUMENTO PARA ALTA DE USUARIO
--TABLA USUARIO, NUEVA COLUMNA ID_DOCUMENTO_ALTA
INSERT INTO seleccion.datos_especificos(id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'FORMULARIO DE ALTA DE USUARIO', 'AUDOC', NULL, 2, 
            TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

ALTER TABLE seguridad.usuario ADD COLUMN id_documento bigint;
