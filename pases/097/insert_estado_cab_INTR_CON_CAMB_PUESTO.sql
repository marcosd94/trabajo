

INSERT INTO planificacion.estado_cab(descripcion, usu_alta, fecha_alta, usu_mod, fecha_mod, activo)
    VALUES ('EN INTERINAZGO', 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL, TRUE);