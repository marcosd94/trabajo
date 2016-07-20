--	ELIMINAR CÉDULAS REPETIDAS PASO A PASO...

-- 1) Se eliminan registros en la tabla SEGURIDAD.USUARIO_ROL por medio del campo "usuario_rol.id_usuario", 
--registros que hacen referencia a SEGURIDAD.USUARIO que a su vez hace referencia a GENERAL.PERSONA 
--(a las personas que no cargaron documentos y experiencia laboral, así también sin modificar algún dato en el primer formulario del C.V. y tienen cédula repetida).

DELETE 
FROM 
	seguridad.usuario_rol
WHERE 
	usuario_rol.id_usuario IN (select 
	usuario.id_usuario
from 
	seguridad.usuario,
	general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
	LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona),
	(select count(documento_identidad) as cantidad, 
	documento_identidad from general.persona group by 
	documento_identidad) duplicados
where 
	persona.documento_identidad = duplicados.documento_identidad
	and cantidad > 1
	and persona.usu_mod IS NULL
	and documentos.id_documento IS NULL
	and experiencia_laboral.id_experiencia_lab IS NULL
	and usuario.id_persona = persona.id_persona);

-- 2) Se procede a eliminar los registros en SEGURIDAD.USUARIO por medio del campo "usuario.id_persona" 
--esto elimina alguna referencia desde GENERAL.PERSONA a SEGURIDAD.USUARIO 
--(deja sin código de usuario a todos los registros filtrados para proceder a eliminar en el siguiente paso; 3)

DELETE 
FROM 
	seguridad.usuario
WHERE usuario.id_persona IN 
	( select
		persona.id_persona 
	from 
		general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona),
		(select count(documento_identidad) as cantidad, 
		documento_identidad from general.persona group by 
		documento_identidad) duplicados
	where 
		persona.documento_identidad = duplicados.documento_identidad
		and cantidad > 1
		and persona.usu_mod IS NULL
		and documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL);

-- 3) Por último se suprimen las filas restantes en GENERAL.PERSONA aquellas que nunca cargaron documento alguno 
--(documentos y experiencia laboral), así también sin datos añadidos o modificados tales como: teléfonos; 
--dirección; y otros datos dentro del primer formulario del C.V. (este último formulario sin necesidad de subir algún archivo).

DELETE 
FROM 
	general.persona
WHERE persona.id_persona IN ( select
		persona.id_persona 
	from 
		general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona),
		(select count(documento_identidad) as cantidad, 
		documento_identidad from general.persona group by 
		documento_identidad) duplicados
	where 
		persona.documento_identidad = duplicados.documento_identidad
		and cantidad > 1
		and persona.usu_mod IS NULL
		and documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL);

-- 4) Procedimiento igual al punto 1) solo que en este caso el código usuario ya NO es más NULL; se busca y 
--compara las cédulas iguales, los que cargaron algún archivo con los que no lo hicieron; se descartan los que no cargaron en: seguridad.usuario_rol.

DELETE 
FROM 
	seguridad.usuario_rol
WHERE 
	usuario_rol.id_usuario IN (select 
		usuario.id_usuario
	from 
		seguridad.usuario,
		general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona),
		/*JOIN ((persona -> documentos) -> experiencia laboral).*/
		(select count(documento_identidad) as cantidad2, documento_identidad from general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona) where documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL group by documento_identidad) duplicados2,
		/*JOIN ((persona -> documentos) -> experiencia laboral) donde exp. laboral y documentos son NULL, sirve para contar los usuarios con código usuario NOT NULL, 
		que no tengan documentos o exp. laboral cargados, esto se usa para comparar con cantidad y así saber si el usuario cargó archivos en ambas tablas; 
		si cargó cantidad2 va a ser menor a cantidad y se deberá eliminar ya que está demás y solo restaría dejar los que están cargados.*/
		(select count(documento_identidad) as cantidad, 
		documento_identidad from general.persona group by 
		documento_identidad) duplicados
	where 
		persona.documento_identidad = duplicados.documento_identidad
		and duplicados.documento_identidad = duplicados2.documento_identidad
		and cantidad > 1
		and documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL
		and cantidad2 < cantidad
		and usuario.id_persona = persona.id_persona);

-- 5) Procedimiento igual al punto 2) solo que en este caso el código usuario ya NO es más NULL; se busca y 
-- compara las cédulas iguales, los que cargaron algún archivo con los que no lo hicieron; se descartan los que no cargaron en: seguridad.usuario.

DELETE 
FROM 
	seguridad.usuario
WHERE 
	usuario.id_persona IN (select 
		persona.id_persona
	from 
		general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona),
		/*JOIN ((persona -> documentos) -> experiencia laboral).*/
		(select count(documento_identidad) as cantidad2, documento_identidad from general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona) where documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL group by documento_identidad) duplicados2,
		/*JOIN ((persona -> documentos) -> experiencia laboral) donde exp. laboral y documentos son NULL, sirve para contar los usuarios con código usuario NOT NULL, 
		que no tengan documentos o exp. laboral cargados, esto se usa para comparar con cantidad y así saber si el usuario cargó archivos en ambas tablas; 
		si cargó cantidad2 va a ser menor a cantidad y se deberá eliminar ya que está demás y solo restaría dejar los que están cargados.*/
		(select count(documento_identidad) as cantidad, 
		documento_identidad from general.persona group by 
		documento_identidad) duplicados
	where 
		persona.documento_identidad = duplicados.documento_identidad
		and duplicados.documento_identidad = duplicados2.documento_identidad
		and cantidad > 1
		and documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL
		and cantidad2 < cantidad);

-- 6) Eliminar referencias a seleccion.idiomas_persona; hay dependencia con general.persona; se busca y 
--compara las cédulas iguales, los que cargaron algún archivo con los que no lo hicieron; se descartan los que no cargaron en: seleccion.idiomas_persona.

DELETE
FROM
	seleccion.idiomas_persona
WHERE id_persona IN (select 
		persona.id_persona
	from 
		general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona),
		/*JOIN ((persona -> documentos) -> experiencia laboral).*/
		(select count(documento_identidad) as cantidad2, documento_identidad from general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona) where documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL group by documento_identidad) duplicados2,
		/*JOIN ((persona -> documentos) -> experiencia laboral) donde exp. laboral y documentos son NULL, sirve para contar los usuarios con código usuario NOT NULL, 
		que no tengan documentos o exp. laboral cargados, esto se usa para comparar con cantidad y así saber si el usuario cargó archivos en ambas tablas; 
		si cargó cantidad2 va a ser menor a cantidad y se deberá eliminar ya que está demás y solo restaría dejar los que están cargados.*/
		(select count(documento_identidad) as cantidad, 
		documento_identidad from general.persona group by 
		documento_identidad) duplicados
	where 
		persona.documento_identidad = duplicados.documento_identidad
		and duplicados.documento_identidad = duplicados2.documento_identidad
		and cantidad > 1
		and documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL
		and cantidad2 < cantidad);

-- 7) Eliminar referencias a seleccion.estudios_realizados; hay dependencia con general.persona; se busca y 
--compara las cédulas iguales, los que cargaron algún archivo con los que no lo hicieron; se descartan los que no cargaron en: seleccion.estudios_realizados.

DELETE
FROM
	seleccion.estudios_realizados
WHERE id_persona IN (select 
		persona.id_persona
	from 
		general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona),
		/*JOIN ((persona -> documentos) -> experiencia laboral).*/
		(select count(documento_identidad) as cantidad2, documento_identidad from general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona) where documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL group by documento_identidad) duplicados2,
		/*JOIN ((persona -> documentos) -> experiencia laboral) donde exp. laboral y documentos son NULL, sirve para contar los usuarios con código usuario NOT NULL, 
		que no tengan documentos o exp. laboral cargados, esto se usa para comparar con cantidad y así saber si el usuario cargó archivos en ambas tablas; 
		si cargó cantidad2 va a ser menor a cantidad y se deberá eliminar ya que está demás y solo restaría dejar los que están cargados.*/
		(select count(documento_identidad) as cantidad, 
		documento_identidad from general.persona group by 
		documento_identidad) duplicados
	where 
		persona.documento_identidad = duplicados.documento_identidad
		and duplicados.documento_identidad = duplicados2.documento_identidad
		and cantidad > 1
		and documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL
		and cantidad2 < cantidad);

-- 8) Caso especial con: 
-- "3221081";"Analia Monserrat ";"Tellez Zaracho";11136795;"2014-02-12 21:27:32.784";2
-- "3221081";"ANALIA MONSERRAT";"TELLEZ ZARACHO";11136751;"2014-02-12 20:48:56.36";2
-- Referencia a si misma, se optó por reemplazar la referencia con el usuario Portal que si queda que es "11136751".

UPDATE 
	seleccion.repr_persona_discapacidad
SET 
	id_persona_rep = 11136751 -- se apunta asi mismo 11136751
WHERE 
	repr_persona_discapacidad.id_persona_rep = 11136795;

--9) Procedimiento igual al punto 3) solo que en este caso el código usuario ya NO es más NULL; se busca y 
-- compara las cédulas iguales, los que cargaron algún archivo con los que no lo hicieron; se descartan los que no cargaron en: general.persona.

DELETE 
FROM 
	general.persona
WHERE 
	persona.id_persona IN (select 
		persona.id_persona
	from 
		general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona),
		/*JOIN ((persona -> documentos) -> experiencia laboral).*/
		(select count(documento_identidad) as cantidad2, documento_identidad from general.persona LEFT OUTER JOIN general.documentos ON (persona.id_persona = documentos.id_persona) 
		LEFT OUTER JOIN seleccion.experiencia_laboral ON (persona.id_persona = experiencia_laboral.id_persona) where documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL group by documento_identidad) duplicados2,
		/*JOIN ((persona -> documentos) -> experiencia laboral) donde exp. laboral y documentos son NULL, sirve para contar los usuarios con código usuario NOT NULL, 
		que no tengan documentos o exp. laboral cargados, esto se usa para comparar con cantidad y así saber si el usuario cargó archivos en ambas tablas; 
		si cargó cantidad2 va a ser menor a cantidad y se deberá eliminar ya que está demás y solo restaría dejar los que están cargados.*/
		(select count(documento_identidad) as cantidad, 
		documento_identidad from general.persona group by 
		documento_identidad) duplicados
	where 
		persona.documento_identidad = duplicados.documento_identidad
		and duplicados.documento_identidad = duplicados2.documento_identidad
		and cantidad > 1
		and documentos.id_documento IS NULL
		and experiencia_laboral.id_experiencia_lab IS NULL
		and cantidad2 < cantidad);

-- 10) Se realizará el último filtro-borrado de forma manual

-- REGLAS A ELIMINAR

-- ÚLTIMA fase (SOLO QUEDAN 6 PERSONAS) 1 de ellas tiene carga en experiencia_laboral PABLO MANUEL se optó por eliminar aquella con menos archivos cargados; 

-- la otra ALBARIÑO DE CARO registra carga de archivos en documentos como exp_laboral, asi también se encontró registro de datos en 
-- seleccion.idioma_persona, seleccion.estudios_realizados, se optó por NO eliminar el registro con fecha de 
-- actividad más reciente y completa tanto en seleccion.idioma_persona como seleccion.experiencia_laboral;  

-- la otra figura con la nueva generación de códigos JOSE LUIS MILTOS ZAVALA ningún archivo cargado, se elimina la cuenta con código viejo

-- las restantes se verifica el campo fecha_alta en general.persona, se opta por NO borrar aquellas con fecha más reciente 

-- id_persona IN (11134121,11132422,11131895,11146460,11140939,11145304) 

--"0517476";11134122;"FRANCY ELIZABETH";	"CACERES DE GONZALEZ";	"2013-11-14 10:32:46.974";"AAC793";2;     ;	;"AAC793"
--"0517476";11134121;"FRANCY ELIZABETH";	"CACERES DE GONZALEZ";	"2013-11-14 10:31:05.2";  "AAC792";2;     ;	;"AAC792"  // eliminado por fecha
--"0584983";11147074;"JOSE LUIS";		"MILTOS ZAVALA";	"2014-04-22 11:33:42.476";"9FQDX" ;2;     ;	;"9FQDX"
--"0584983";11132422;"JOSE LUIS";		"MILTOS ZAVALA";	"2013-10-06 22:34:37.734";"AAB112";2;     ;	;"AAB112"  // eliminado por código viejo
--"0930187";11131923;"EDUARDO ENRIQUE";		"JACQUET ORTIGOZA";	"2013-04-06 16:36:52.025";"AAA613";2;     ;	;"AAA613"
--"0930187";11131895;"EDUARDO ENRIQUE";		"JACQUET ORTIGOZA";	"2013-03-16 14:49:05.146";"AAA585";2;     ;	;"AAA585"  // eliminado por fecha
--"3388128";11145305;"ALFREDO RAMON";		"ALBARIÑO DE CARO";	"2014-03-15 09:46:05.393";"AAL159";2;25394;10074;"AAL159"
--"3388128";11145304;"ALFREDO RAMON";		"ALBARIÑO DE CARO";	"2014-03-15 09:44:25.331";"AAL159";2;16129;7153 ;"AAL159"  // eliminado por menos datos y registro más viejo
--"3873408";11146459;"PABLO MANUEL";		"GONZALEZ MEDINA";	"2014-04-07 10:23:29.778";"AAM177";2;	  ;7756 ;"AAM177"
--"3873408";11146460;"PABLO MANUEL";		"GONZALEZ MEDINA";	"2014-04-07 10:25:03.776";"AAM177";2;	  ;8740 ;"AAM177"  // eliminado, solo un registo expLab
--"3873408";11146459;"PABLO MANUEL";		"GONZALEZ MEDINA";	"2014-04-07 10:23:29.778";"AAM177";2;	  ;7757 ;"AAM177"
--"5087248";11140939;"OMAR HERNAN";		"MONTIEL DELO SANTO";	"2014-02-19 11:12:24.099";"AAH724";2;	  ;	;"AAH724"  // eliminado por fecha
--"5087248";11140940;"OMAR HERNAN";		"MONTIEL DELO SANTO";	"2014-02-19 11:13:06.567";"AAH724";2;	  ;	;"AAH724"


-- 10.1) tabla SEGURIDAD.USUARIO_ROL

DELETE 
FROM 
	seguridad.usuario_rol
WHERE 
	usuario_rol.id_usuario IN (select 
	usuario.id_usuario
from 
	seguridad.usuario,
	general.persona, 
	(select count(documento_identidad) as cantidad, documento_identidad
	from general.persona group by documento_identidad) duplicados 
where 
	persona.documento_identidad = duplicados.documento_identidad
	and cantidad > 1
	and usuario.id_persona = persona.id_persona
	and persona.id_persona IN (11134121,11132422,11131895,11146460,11140939,11145304));

-- 10.2) tabla SEGURIDAD.USUARIO

DELETE 
FROM 
	seguridad.usuario
WHERE 
	usuario.id_persona IN (select 
	persona.id_persona
from 
	general.persona, 
	(select count(documento_identidad) as cantidad, documento_identidad
	from general.persona group by documento_identidad) duplicados 
where 
	persona.documento_identidad = duplicados.documento_identidad
	and cantidad > 1
	and persona.id_persona IN (11134121,11132422,11131895,11146460,11140939,11145304));

--10.3) tabla SELECCION.EXPERIENCIA_LABORAL

DELETE 
FROM 
	seleccion.experiencia_laboral
WHERE 
	experiencia_laboral.id_persona IN (select 
	persona.id_persona
from 
	general.persona, 
	(select count(documento_identidad) as cantidad, documento_identidad
	from general.persona group by documento_identidad) duplicados 
where 
	persona.documento_identidad = duplicados.documento_identidad
	and cantidad > 1
	and persona.id_persona IN (11134121,11132422,11131895,11146460,11140939,11145304));

--10.4) tabla SELECCION.IDIOMAS_PERSONA

DELETE 
FROM 
	seleccion.idiomas_persona
WHERE 
	idiomas_persona.id_persona IN (11134121,11132422,11131895,11146460,11140939,11145304);

--10.5) tabla SELECCION.ESTUDIOS_REALIZADOS

DELETE 
FROM 
	seleccion.estudios_realizados
WHERE 
	estudios_realizados.id_persona IN (11134121,11132422,11131895,11146460,11140939,11145304);

--10.6) tabla GENERAL.DOCUMENTOS

DELETE 
FROM 
	general.documentos
WHERE 
	documentos.id_persona IN (11134121,11132422,11131895,11146460,11140939,11145304);

--10.7) tabla GENERAL.PERSONA

DELETE 
FROM 
	general.persona
WHERE 
	persona.id_persona IN (11134121,11132422,11131895,11146460,11140939,11145304);

-- Testeado con la BBDD Producción back up; fecha: Martes 19 de agosto de 2014; Werner.