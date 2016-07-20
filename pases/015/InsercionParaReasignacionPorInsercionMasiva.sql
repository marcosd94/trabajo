--Funciones para ver, edit y listar las reasignaciones por insercion masiva-- no olvidar asignar estas funciones al admin

INSERT INTO seguridad.funcion (descripcion, url, modulo) 
VALUES ('LISTADO DE GESTION DE REASIGNACION POR INSERCION MASIVA','gestionarReasignacionInsercionMasiva_list', 'MOVILIDAD');


INSERT INTO seguridad.funcion (descripcion, url, modulo) 
VALUES ('EDITAR GESTION DE REASIGNACION POR INSERCION MASIVA','gestionarReasignacionInsercionMasiva_edit', 'MOVILIDAD');


INSERT INTO seguridad.funcion (descripcion, url, modulo) 
VALUES ('VER GESTION DE REASIGNACION POR INSERCION MASIVA','gestionarReasignacionInsercionMasiva_view', 'MOVILIDAD');



--Para datos especificos reasignacion por insercion masiva
INSERT INTO seleccion.datos_especificos (descripcion, id_datos_generales, activo, usu_alta, fecha_alta) 
VALUES ('REASIGNACION POR INSERCION MASIVA', 54, true, 'ADMIN', localtimestamp);

