update seleccion.datos_especificos d
   set activo = false
 where exists (select 'X' from seleccion.datos_especificos de, seleccion.datos_generales dg
	       where de.id_datos_generales = dg.id_datos_generales
	       and dg.descripcion = 'NACIONALIDAD'
	       and de.descripcion like '%PARAGUAY%' 
	       and de.descripcion not like 'PARAGUAYA NATURALIZADA'
	       and de.id_datos_especificos <> 12 
	       and d.id_datos_especificos = de.id_datos_especificos)


	       