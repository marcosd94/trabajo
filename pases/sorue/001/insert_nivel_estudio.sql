--TABLA SELECCION.NIVEL_ESTUDIO, INSERCION DE REGISTRO PARA NIVEL DE ESTUDIO "JORNADAS, TALLERES Y SEMINARIOS"
--1ro aseguramos que el seq esté correcto
SELECT setval('seleccion.sel_nivel_estudio_id_nivel_estudio_seq', (select max(id_nivel_estudio) from seleccion.nivel_estudio));

INSERT INTO seleccion.nivel_estudio(id_nivel_estudio, descripcion, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('seleccion.sel_datos_especificos_id_datos_especificos_seq'), 'Jornadas, talleres y seminarios', 
            TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);