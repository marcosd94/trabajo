---# DECLARACION JURADA

--UBICAR LOS DOCUMENTOS DE DECLARACION JURADO	
SELECT * FROM general.documentos
WHERE nombre_tabla = 'DeclaracionJurada'
AND nombre_pantalla = 'admDeclaraJuradas656FC'
AND activo is true;

SELECT * FROM legajo.declaracion_jurada dj
JOIN general.documentos doc ON dj.id_documento = doc.id_documento 
where doc.activo is true 
and doc.id_persona is null 
and doc.id_tabla is null 
and doc.nombre_tabla ='DeclaracionJurada';

---ACTUALIZAR LOS REGISTROS ANTERIOES
update general.documentos doc
set id_persona = dj.id_persona, id_tabla = dj.id_declaracion_jurada
from legajo.declaracion_jurada dj
where 
doc.id_documento = dj.id_documento
and doc.activo is true 
and doc.id_persona is null 
and doc.id_tabla is null 
and doc.nombre_tabla ='DeclaracionJurada';