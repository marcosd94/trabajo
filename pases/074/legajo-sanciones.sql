ALTER TABLE legajo.datos_amonestacion DROP id_datos_esp_motivo_amonestacion;
ALTER TABLE legajo.datos_amonestacion ADD motivo character varying(100) NOT NULL;

ALTER TABLE legajo.datos_amonestacion ADD id_datos_esp_tipo_sancion bigint NOT NULL;

SELECT setval('seleccion.sel_datos_especificos_id_datos_especificos_seq', (select max(id_datos_especificos) from seleccion.datos_especificos));

INSERT INTO seleccion.datos_generales(descripcion, activo, usu_alta, fecha_alta) VALUES ('TIPO DE SANCION', TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('Verbal', (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE SANCION'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('Escrita', (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE SANCION'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('Suspención ', (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE SANCION'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('Multa', (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE SANCION'), TRUE, 'VCALIGARIS', current_timestamp);

INSERT INTO seleccion.datos_especificos(descripcion, valor_alf, id_datos_generales, activo, usu_alta, fecha_alta)
VALUES ('ACTO ADMINISTRATIVO', 'DOC_V', (select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPOS DE DOCUMENTOS'), TRUE, 'VCALIGARIS', current_timestamp);
