---# VACACIONES

--UBICAR LOS DOCUMENTOS DE DATOS VACACIONES
--faltaria actualizar a false los que no se encuentran referenciado a la tabla vacaciones
SELECT * FROM general.documentos
WHERE nombre_tabla = 'DatosVacaciones'
AND nombre_pantalla = 'admVacaciones654FC'
AND activo is true;

SELECT * FROM legajo.datos_vacaciones dv
JOIN general.documentos doc ON dv.id_documento = doc.id_documento 
where doc.activo is true 
and doc.id_persona is null 
and doc.id_tabla is null 
and doc.nombre_tabla ='DatosVacaciones';

---ACTUALIZAR LOS REGISTROS ANTERIOES
update general.documentos doc
set id_persona = dv.id_persona, id_tabla = dv.id_datos_vacaciones
from legajo.datos_vacaciones dv
where 
doc.id_documento = dv.id_documento
and doc.activo is true 
and doc.id_persona is null 
and doc.id_tabla is null 
and doc.nombre_tabla ='DatosVacaciones';