---# AMONESTACIONES

--UBICAR LOS DOCUMENTOS DE DATOS AMONESTACIONES
SELECT * FROM general.documentos
WHERE nombre_tabla = 'DatosAmonestacion'
AND nombre_pantalla = 'adm_amonestaciones'
AND activo is true;

SELECT * FROM legajo.datos_amonestacion  da
JOIN general.documentos doc ON da.id_documento = doc.id_documento 
where doc.activo is true 
and doc.id_persona is null 
--and doc.id_tabla is null 
and doc.nombre_tabla ='DatosAmonestacion';

---ACTUALIZAR LOS REGISTROS ANTERIOES
update general.documentos doc
set id_persona =da.id_persona, nombre_pantalla = 'AdmAmonestaciones'
from legajo.datos_amonestacion da
where 
doc.id_documento = da.id_documento
and doc.activo is true 
and doc.id_persona is null 
--and doc.id_tabla is null 
and doc.nombre_tabla ='DatosAmonestacion';