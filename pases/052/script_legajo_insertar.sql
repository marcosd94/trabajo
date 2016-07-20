SELECT setval('seleccion.sel_datos_especificos_id_datos_especificos_seq', (select max(id_datos_especificos) from seleccion.datos_especificos));

INSERT INTO seleccion.datos_generales(descripcion, activo, usu_alta, fecha_alta)
VALUES ('ETNIA', TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('NIVACLÉ', (select id_datos_generales from seleccion.datos_generales where descripcion = 'ETNIA'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PAÎ TAVYTERÃ', (select id_datos_generales from seleccion.datos_generales where descripcion = 'ETNIA'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('AVÁ GUARANÍ', (select id_datos_generales from seleccion.datos_generales where descripcion = 'ETNIA'), TRUE, 'VCALIGARIS', current_timestamp);



INSERT INTO seleccion.datos_generales(descripcion, activo, usu_alta, fecha_alta)
VALUES ('ENFERMEDADES', TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('DIABETES', (select id_datos_generales from seleccion.datos_generales where descripcion = 'ENFERMEDADES'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('BRONQUITIS', (select id_datos_generales from seleccion.datos_generales where descripcion = 'ENFERMEDADES'), TRUE, 'VCALIGARIS', current_timestamp);



INSERT INTO seleccion.datos_especificos(descripcion, valor_alf, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('CERTIFICADO', 'FPOS', (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPOS DE DOCUMENTOS'), TRUE, 'VCALIGARIS', current_timestamp);



INSERT INTO seleccion.datos_generales(descripcion, activo, usu_alta, fecha_alta)
VALUES ('DISCAPACIDAD CAUSA', TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('CAUSA 1', (select id_datos_generales from seleccion.datos_generales where descripcion = 'DISCAPACIDAD CAUSA'), TRUE, 'VCALIGARIS', current_timestamp);



INSERT INTO seleccion.datos_generales(descripcion, activo, usu_alta, fecha_alta)
VALUES ('MOTIVO DE PERMISO', TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('CAPACITACION', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PERIODO DE LACTANCIA', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PERMISO ESPECIAL CON GOCE DE SUELDO', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PERMISO ESPECIAL POR EMBARAZO', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PERMISO ESPECIAL SIN GOCE DE SUELDO', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PERMISO POR ADOPCION', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PERMISO POR FALLECIMIENTO', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PERMISO POR MATERNIDAD', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PERMISO POR MATRIMONIO', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PERMISO POR PATERNIDAD', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PERMISO POR RAZONES DE SALUD', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('PERMISO SINDICAL', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);



INSERT INTO seleccion.datos_generales(descripcion, activo, usu_alta, fecha_alta)
VALUES ('ESTADOS DE PERMISO', TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('ESTADO 1', (select id_datos_generales from seleccion.datos_generales where descripcion = 'ESTADOS DE PERMISO'), TRUE, 'VCALIGARIS', current_timestamp);



INSERT INTO seleccion.datos_especificos(descripcion, valor_alf, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('DOCUMENTO ACTO ADMINISTRATIVO', 'DOC_V', (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPOS DE DOCUMENTOS'), TRUE, 'VCALIGARIS', current_timestamp);



INSERT INTO seleccion.datos_generales(descripcion, activo, usu_alta, fecha_alta)
VALUES ('MOTIVO DE AMONESTACION', TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('MOTIVO 1', (select id_datos_generales from seleccion.datos_generales where descripcion = 'MOTIVO DE AMONESTACION'), TRUE, 'VCALIGARIS', current_timestamp);
