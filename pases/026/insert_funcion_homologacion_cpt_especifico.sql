INSERT INTO seguridad.funcion(
            id_funcion, descripcion, url, modulo, adjuntable)
    VALUES ((select max (id_funcion) from seguridad.funcion) +1, 'HOMOLOGACION CTP ESPECIFICO', 'homologacion_cpt_especifico', 'PLANIFICACION',null);
