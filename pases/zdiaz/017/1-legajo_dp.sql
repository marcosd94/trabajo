--IDENTIFICAR REGISTROS INSERTADOS PARA DATOS PERSONALES (TIPOS DE DOCUMENTOS) 
SELECT * FROM seleccion.datos_especificos
WHERE id_datos_generales = 2 AND valor_alf = 'LEG_DP'

INSERT INTO seleccion.datos_especificos (descripcion,valor_alf,id_datos_generales,activo,usu_alta,fecha_alta) VALUES
('FOTO CARNET','LEG_DP',2,TRUE,'rtrinidad',CURRENT_DATE);

INSERT INTO seleccion.datos_especificos (descripcion,valor_alf,id_datos_generales,activo,usu_alta,fecha_alta) VALUES
('COPIA DE CEDULA DE IDENTIDAD','LEG_DP',2,TRUE,'rtrinidad',CURRENT_DATE);

INSERT INTO seleccion.datos_especificos (descripcion,valor_alf,id_datos_generales,activo,usu_alta,fecha_alta) VALUES
('COPIA DE PARTIDA DE NACIMIENTO','LEG_DP',2,TRUE,'rtrinidad',CURRENT_DATE);

INSERT INTO seleccion.datos_especificos (descripcion,valor_alf,id_datos_generales,activo,usu_alta,fecha_alta) VALUES
('CERTIFICADO DE VIDA Y RESIDENCIA','LEG_DP',2,TRUE,'rtrinidad',CURRENT_DATE);


--#FOTO CARNET
SELECT * FROM "general".documentos gdoc
INNER JOIN "general".persona gper
ON gdoc.id_documento = gper.id_documento
WHERE gdoc.nombre_tabla LIKE 'Persona' 
AND gdoc.nombre_pantalla like 'datos_personales_legajo'
--AND gdoc.id_datos_especificos_tipo_documento = 1679



--ACTUALIZACION DE REGISTROS ANTERIORES
UPDATE "general".documentos SET nombre_pantalla='DatosPersonalesLegajo',id_datos_especificos_tipo_documento=1759
WHERE id_documento IN (SELECT gdoc.id_documento FROM "general".documentos gdoc
INNER JOIN "general".persona gper
ON gdoc.id_documento = gper.id_documento
WHERE gdoc.nombre_tabla LIKE 'Persona' 
AND gdoc.nombre_pantalla like 'datos_personales_legajo');

 
--ACTUALIZAR ID_TABLA Y ID_PERSONA DE FOTO CARNET
SELECT * FROM "general".documentos gdoc
INNER JOIN "general".persona gper
ON gdoc.id_documento = gper.id_documento
WHERE gdoc.id_datos_especificos_tipo_documento = 1759

--ACTUALIZACION DE REGISTROS ANTERIORES
update general.documentos
set id_persona = gp.id_persona,id_tabla=gp.id_persona
from general.persona gp
where 
general.documentos.id_documento = gp.id_documento
AND general.documentos.id_datos_especificos_tipo_documento = 1759




--#CEDULA DE IDENTIDAD
SELECT * FROM "general".documentos gdoc
WHERE gdoc.nombre_tabla LIKE 'documentos_persona' 
AND gdoc.nombre_pantalla like 'datos_personales_legajo'
AND ubicacion_fisica LIKE '%LEGAJO%'
AND id_datos_especificos_tipo_documento=1680

--ACTUALIZACION DE REGISTROS ANTERIORES
UPDATE "general".documentos SET nombre_tabla='Persona',nombre_pantalla='DatosPersonalesLegajo',id_datos_especificos_tipo_documento=1761,id_tabla=id_persona
WHERE id_documento IN (SELECT gdoc.id_documento FROM "general".documentos gdoc
WHERE gdoc.nombre_tabla LIKE 'documentos_persona' 
AND gdoc.nombre_pantalla like 'datos_personales_legajo'
AND ubicacion_fisica LIKE '%LEGAJO%'
AND id_datos_especificos_tipo_documento=1680);



--#DATOS NACIMIENTO
SELECT * FROM "general".documentos gdoc
WHERE gdoc.nombre_tabla LIKE 'documentos_persona' 
AND gdoc.nombre_pantalla like 'datos_personales_legajo'
AND ubicacion_fisica LIKE '%LEGAJO%'
AND id_datos_especificos_tipo_documento=1548

--ACTUALIZACION DE REGISTROS ANTERIORES
UPDATE "general".documentos SET nombre_tabla='Persona',nombre_pantalla='DatosPersonalesLegajo',id_datos_especificos_tipo_documento=1764,id_tabla=id_persona
WHERE id_documento IN (SELECT gdoc.id_documento FROM "general".documentos gdoc
WHERE gdoc.nombre_tabla LIKE 'documentos_persona' 
AND gdoc.nombre_pantalla like 'datos_personales_legajo'
AND ubicacion_fisica LIKE '%LEGAJO%'
AND id_datos_especificos_tipo_documento=1548);



--#VIDA Y RESIDENCIA
SELECT * FROM "general".documentos gdoc
WHERE gdoc.nombre_tabla LIKE 'documentos_persona' 
AND gdoc.nombre_pantalla like 'datos_personales_legajo'
AND ubicacion_fisica LIKE '%LEGAJO%'
AND id_datos_especificos_tipo_documento=1566

--ACTUALIZACION DE REGISTROS ANTERIORES
UPDATE "general".documentos SET nombre_tabla='Persona',nombre_pantalla='DatosPersonalesLegajo',id_datos_especificos_tipo_documento=1762,id_tabla=id_persona
WHERE id_documento IN (SELECT gdoc.id_documento FROM "general".documentos gdoc
WHERE gdoc.nombre_tabla LIKE 'documentos_persona' 
AND gdoc.nombre_pantalla like 'datos_personales_legajo'
AND ubicacion_fisica LIKE '%LEGAJO%'
AND id_datos_especificos_tipo_documento=1566);




