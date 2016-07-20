update general.persona per
   set id_datos_especificos_nacionalid = 12
 where exists (select 'X' from general.persona p, seleccion.datos_especificos de, seleccion.datos_generales dg
			 where de.id_datos_generales = dg.id_datos_generales
			   and dg.descripcion = 'NACIONALIDAD'
			   and (de.descripcion like '%PARAGUAY%' and de.descripcion not like 'PARAGUAYA NATURALIZADA')
			   and de.id_datos_especificos <> 12
			   and p.id_datos_especificos_nacionalid = de.id_datos_especificos
			   and p.id_persona = per.id_persona)


