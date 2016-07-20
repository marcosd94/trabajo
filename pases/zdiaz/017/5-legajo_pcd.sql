---# DISCAPACIDAD
SELECT * FROM general.documentos
WHERE nombre_tabla = 'PersonaDiscapacidadLegajo'
AND nombre_pantalla = 'discapacidad_legajo'

---ACTUALIZAR LOS REGISTROS ANTERIOES --Falta actualizar id_persona 
UPDATE general.documentos
SET nombre_pantalla = 'DiscapacidadLegajo'
WHERE nombre_tabla = 'PersonaDiscapacidadLegajo'
AND nombre_pantalla = 'discapacidad_legajo'
