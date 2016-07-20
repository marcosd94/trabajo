ALTER TABLE legajo.datos_enfermedad ADD COLUMN enfermo boolean;

ALTER TABLE legajo.datos_enfermedad DROP COLUMN id_datos_esp_enfermedad;

INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, id_datos_generales, activo, usu_alta, fecha_alta)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'CERTIFICADO MEDICO', 'CCMS', 2, TRUE, 'rtrinidad', LOCALTIMESTAMP);

INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, id_datos_generales, activo, usu_alta, fecha_alta)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'CONTRATO', 'FPEXP', 2, TRUE, 'rtrinidad', LOCALTIMESTAMP);

INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, id_datos_generales, activo, usu_alta, fecha_alta)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'CERTIFICADO DE VIDA Y RESIDENCIA', 'FPEXP', 2, TRUE, 'rtrinidad', LOCALTIMESTAMP);