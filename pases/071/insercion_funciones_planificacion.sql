--Reparar secuencial

SELECT setval('seguridad.funcion_id_funcion_seq', (select max(id_funcion) from seguridad.funcion));

INSERT INTO seguridad.funcion(descripcion, url, modulo)
    VALUES ('INGRESAR FUNCIONARIOS POR INSERCION MASIVA','gestionarInsercionMasiva' , 'PLANIFICACION');


INSERT INTO seguridad.funcion(descripcion, url, modulo)
    VALUES ('MOVER PUESTOS','gestionarMoverPuestos' , 'PLANIFICACION');
