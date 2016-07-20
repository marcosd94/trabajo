---# DATOS SALUD
--ENFERMEDAD
SELECT * FROM general.documentos
WHERE nombre_tabla = 'DatosEnfermedad'
AND nombre_pantalla = 'datos_salud_legajo'

---ACTUALIZAR LOS REGISTROS ANTERIOES --Falta actualizar id_persona y id_tabla
UPDATE general.documentos
SET nombre_pantalla = 'DatosSaludLegajo'
WHERE nombre_tabla = 'DatosEnfermedad'
AND nombre_pantalla = 'datos_salud_legajo'


---# 
--SANGUINEO
SELECT * FROM general.documentos
WHERE nombre_tabla = 'Persona'
AND nombre_pantalla = 'datos_salud_legajo'

---ACTUALIZAR LOS REGISTROS ANTERIOES --Falta actualizar id_persona y id_tabla
UPDATE general.documentos
SET nombre_pantalla = 'DatosSaludLegajo'
WHERE nombre_tabla = 'Persona'
AND nombre_pantalla = 'datos_salud_legajo'


