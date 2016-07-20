-- reparar secuencial
SELECT setval('seleccion.sel_referencias_id_referencias_seq', (select max(id_referencias) from seleccion.referencias));

INSERT INTO seleccion.referencias(dominio, desc_abrev, desc_larga,activo, usu_alta, fecha_alta)
    VALUES ('ESTADO_CIVIL','M','MENOR',TRUE, 'ADMIN',(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'));
