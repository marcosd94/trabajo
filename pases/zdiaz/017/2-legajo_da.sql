---# DOCUMENTOS ADJUNTOS

--UBICAR LOS DOCUMENTOS DEL TIPO ADJ_DOC EN EL LEGAJO
select * from general.documentos
WHERE nombre_pantalla = 'Tab4AdjuntarDocumentos_edit_legajo'
AND nombre_tabla = 'Persona'
AND ubicacion_fisica LIKE '%LEGAJO%'

---ACTUALIZAR LOS REGISTROS ANTERIOES
UPDATE general.documentos
SET nombre_pantalla = 'adjuntarDocumentos_legajo'
WHERE nombre_pantalla = 'Tab4AdjuntarDocumentos_edit_legajo'
AND nombre_tabla = 'Persona'
AND ubicacion_fisica LIKE '%LEGAJO%'



--VERIFICAR COMO IMPACTA EN EL PORTAL PY CONCURSA



--INACTIVAR PARTIDA DE NACIMIENTO DE FPOS
UPDATE seleccion.datos_especificos SET activo = FALSE
WHERE descripcion LIKE '%COPIA DE PARTIDA DE NACIMIENTO%'
AND valor_alf = 'FPOS';
---INACTIVAR OTROS DE FPOS
UPDATE seleccion.datos_especificos SET activo = FALSE
WHERE descripcion LIKE '%OTROS%'
AND valor_alf = 'FPOS';
