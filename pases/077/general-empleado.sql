INSERT INTO seleccion.datos_especificos(descripcion, valor_alf, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('DECRETO', 'DEC', (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPOS DE DOCUMENTOS'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, valor_alf, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('CONTRATO', 'CONT', (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPOS DE DOCUMENTOS'), TRUE, 'VCALIGARIS', current_timestamp);

ALTER TABLE general.empleado_puesto ADD encabezado_acto_administrativo character varying(500);

ALTER TABLE general.empleado_puesto ADD descripcion_funciones character varying;