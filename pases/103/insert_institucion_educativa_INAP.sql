--TABLA INSTITUCION_EDUCATIVA, INSERCION PARA 'INAP' 

--1ro aseguramos que el seq esté correcto
SELECT setval('seleccion.institucion_educativa_id_inst_educativa_seq', (select max(id_inst_educativa) from seleccion.institucion_educativa));

INSERT INTO seleccion.institucion_educativa(id_inst_educativa, descripcion, id_pais, activo, usu_alta, fecha_alta, usu_mod, fecha_mod, convenio_sfp, id_documento)
    VALUES (nextVal('seleccion.institucion_educativa_id_inst_educativa_seq'), 'INAPP', 100, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL, TRUE, NULL);