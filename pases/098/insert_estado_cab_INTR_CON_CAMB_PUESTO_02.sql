

INSERT INTO planificacion.estado_cab(descripcion, usu_alta, fecha_alta, usu_mod, fecha_mod, activo)
    VALUES ('CAMBIADO POR MOVILIDAD', 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL, TRUE);