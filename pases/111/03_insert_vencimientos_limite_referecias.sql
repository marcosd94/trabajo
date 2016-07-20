--TABLA VENCIMIENTOS, INSERCION DE REGISTROS DE VENCIMIENTOS MENSUALES PARA UN AÑO
--1ro aseguramos que el seq esté correcto
SELECT setval('remuneracion.vencimientos_id_vencimiento_seq', (select max(id_vencimiento) from remuneracion.vencimientos));

--ENERO
INSERT INTO remuneracion.vencimientos(id_vencimiento, anho, mes, vencimiento, prorroga, 
            activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 1, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL),
   	(nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 2, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL),
    (nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 3, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL),
    (nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 4, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL),
    (nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 5, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL),
    (nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 6, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL),
    (nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 7, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL),
    (nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 8, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL),
    (nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 9, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL),
    (nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 10, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL),
    (nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 11, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL),
    (nextVal('remuneracion.vencimientos_id_vencimiento_seq'), 2015, 12, (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), 
            (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);


--TABLA REFERENCIAS, INSERCION DE REGISTRO PARA TIEMPO LIMITE DE PRESENTACION DE REMUNERACIONES
INSERT INTO seleccion.referencias (dominio, desc_abrev, desc_larga, valor_num, activo, usu_alta, fecha_alta)
VALUES ('TIEMPO_PRESENTACION', NULL, 'LIMITE DE PRESENTACION DE PLANILLAS TRAS VENCIMIENTO (MESES)', 90, TRUE, 'SORUE',
 (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'));

		--TABLA REFERENCIAS, INSERCION DE REGISTRO PARA CANTIDAD MAXIMA DE RECTIFICACIONES
	INSERT INTO seleccion.referencias (dominio, desc_abrev, desc_larga, valor_num, activo, usu_alta, fecha_alta)
	VALUES ('RECTIFICACIONES', NULL, 'CANTIDAD DE VECES POSIBLES A RECTIFICAR', 3, TRUE, 'SORUE',
	 (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'));

	--TABLA REFERENCIAS, INSERCION DE REGISTRO PARA TIEMPO LIMITE DE PRESENTACION DE REMUNERACIONES
	INSERT INTO seleccion.referencias (dominio, desc_abrev, desc_larga, valor_num, activo, usu_alta, fecha_alta)
	VALUES ('TIEMPO_RECTIFICACION', NULL, 'TIEMPO EN EL CUAL PUEDEN RECTIFICAR SUS PLANILLAS (DIAS)', 3, TRUE, 'SORUE',
	 (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'));


