---# EXPERIENCIA LABORAL LEGAJO
SELECT * FROM general.documentos
WHERE nombre_tabla = 'ExperienciaLaboral'
AND nombre_pantalla = 'experiencia_laboral_legajo'

---ACTUALIZAR LOS REGISTROS ANTERIOES
UPDATE general.documentos
SET nombre_pantalla = 'ExperienciaLaboralLegajo', nombre_tabla = 'ExperienciaLaboralLegajo'
WHERE nombre_tabla = 'ExperienciaLaboral'
AND nombre_pantalla = 'experiencia_laboral_legajo'
