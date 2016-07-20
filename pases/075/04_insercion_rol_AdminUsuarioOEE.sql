
SELECT setval('seguridad.rol_id_rol_seq', (select max(id_rol) from seguridad.rol));

INSERT INTO seguridad.rol(descripcion, activo, fecha_alta, usu_alta, homologador)
    VALUES ('ADMINISTRADOR DE USUARIOS OEE', TRUE, LOCALTIMESTAMP, 'SORUE',  FALSE);