
SELECT setval('seguridad.rol_id_rol_seq', (select max(id_rol) from seguridad.rol));

INSERT INTO seguridad.rol(descripcion, activo, fecha_alta, usu_alta, homologador)
    VALUES ('GESTOR DE REMUNERACIONES SFP', TRUE, LOCALTIMESTAMP, 'SORUE',  TRUE);

INSERT INTO seguridad.rol(descripcion, activo, fecha_alta, usu_alta, homologador)
    VALUES ('GESTOR DE REMUNERACIONES OEE', TRUE, LOCALTIMESTAMP, 'SORUE',  FALSE);