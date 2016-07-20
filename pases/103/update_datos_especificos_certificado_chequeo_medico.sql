--TABLA DATOS_ESPECIFICOS, ACTUALIZAR REGISTRO 'CERTIFICADO MÉDICO' A 'CHEQUEO MÉDICO' 

UPDATE seleccion.datos_especificos SET descripcion = 'CHEQUEO MÉDICO' WHERE id_datos_generales = 2 and valor_alf = 'CCMS' and activo = TRUE