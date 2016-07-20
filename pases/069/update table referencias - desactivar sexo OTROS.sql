-- Desactivar sexo OTROS para que no aparezca como opción para seleccionar en listados
update seleccion.referencias
   set activo = false
 where desc_abrev = 'O'