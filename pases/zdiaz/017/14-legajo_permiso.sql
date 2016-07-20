---# PERMISOS

--UBICAR LOS DOCUMENTOS DE DATOS PERMISOS
SELECT * FROM general.documentos
WHERE nombre_tabla = 'DatosPermiso'
AND nombre_pantalla = 'admPermisos655FC'
AND activo is true;

SELECT * FROM legajo.datos_permiso dp
JOIN general.documentos doc ON dp.id_documento = doc.id_documento 
where doc.activo is true 
and doc.id_persona is null 
and doc.id_tabla is null 
and doc.nombre_tabla ='DatosPermiso';

---ACTUALIZAR LOS REGISTROS ANTERIOES
update general.documentos doc
set id_persona = dp.id_persona, id_tabla = dp.id_datos_permiso
from legajo.datos_permiso dp
where 
doc.id_documento = dp.id_documento
and doc.activo is true 
and doc.id_persona is null 
and doc.id_tabla is null 
and doc.nombre_tabla ='DatosPermiso';