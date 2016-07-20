---# PREMIOS

--UBICAR LOS DOCUMENTOS DE DATOS PREMIOS
SELECT * FROM general.documentos
WHERE nombre_tabla = 'DatosPremio'
AND nombre_pantalla = 'premios_reconocimientos'
AND activo is true;

SELECT * FROM legajo.datos_premio dp
JOIN general.documentos doc ON dp.id_documento = doc.id_documento 
where doc.activo is true 
and doc.id_persona is null 
--and doc.id_tabla is null 
and doc.nombre_tabla ='DatosPremio';

---ACTUALIZAR LOS REGISTROS ANTERIOES
update general.documentos doc
set id_persona = dp.id_persona, nombre_pantalla = 'PremiosReconocimientos'
from legajo.datos_premio dp
where 
doc.id_documento = dp.id_documento
and doc.activo is true 
and doc.id_persona is null 
--and doc.id_tabla is null 
and doc.nombre_tabla ='DatosPremio';