---PORTAL ESTUDIOS REALIZADOS - DOCUMENTOS
SELECT * FROM general.documentos
where 
activo is true 
and id_persona is null 
and nombre_tabla ='EstudiosRealizados'
and ubicacion_fisica NOT LIKE '%LEGAJO%' 

--UPDATE
update general.documentos
set id_persona = er.id_persona
from seleccion.estudios_realizados er
where 
general.documentos.id_tabla = er.id_estudios_realizados 
and general.documentos.activo is true 
and general.documentos.id_persona is null 
and nombre_tabla ='EstudiosRealizados' 
and ubicacion_fisica NOT LIKE '%LEGAJO%';


---LEGAJO ESTUDIOS REALIZADOS - DOCUMENTOS
SELECT * FROM general.documentos
where 
activo is true 
and id_persona is null 
and nombre_tabla ='EstudiosRealizados' AND nombre_pantalla = 'tab2FormacionAcademicaPostulante_edit'
and ubicacion_fisica LIKE '%LEGAJO%' 

--UPDATE
update general.documentos
set id_persona = er.id_persona, nombre_tabla = 'EstudiosRealizadosLegajo', nombre_pantalla = 'EstudiosRealizadosLegajo_edit'
from legajo.estudios_realizados er
where 
general.documentos.id_tabla = er.id_estudios_realizados_legajo
and general.documentos.activo is true 
and general.documentos.id_persona is null 
and nombre_tabla ='EstudiosRealizados' AND nombre_pantalla = 'tab2FormacionAcademicaPostulante_edit'
and ubicacion_fisica LIKE '%LEGAJO%';


---UPDATE NOMBRE_PANTALLA Y NOMBRE_TABLA DE REGISTROS ANTERIORES
SELECT * FROM general.documentos
where 
nombre_tabla LIKE 'EstudiosRealizados' AND nombre_pantalla = 'estudios_realizados_legajo'
and ubicacion_fisica LIKE '%LEGAJO%';

UPDATE general.documentos
SET nombre_tabla = 'EstudiosRealizadosLegajo', nombre_pantalla = 'EstudiosRealizadosLegajo'
WHERE nombre_tabla LIKE 'EstudiosRealizados' AND nombre_pantalla = 'estudios_realizados_legajo'
and ubicacion_fisica LIKE '%LEGAJO%';
