---# FAMILIARES

--IDENTIFICAR REGISTROS INSERTADOS PARA DATOS FAMILIARES (TIPOS DE DOCUMENTOS) 
SELECT * FROM seleccion.datos_especificos
WHERE id_datos_generales = 2 AND valor_alf = 'LEG_DF'

INSERT INTO seleccion.datos_especificos (descripcion,valor_alf,id_datos_generales,activo,usu_alta,fecha_alta) VALUES
('CERTIFICADO DE MATRIMONIO','LEG_DF',2,TRUE,'rtrinidad',CURRENT_DATE);

INSERT INTO seleccion.datos_especificos (descripcion,valor_alf,id_datos_generales,activo,usu_alta,fecha_alta) VALUES
('COPIA DE CEDULA DE IDENTIDAD','LEG_DF',2,TRUE,'rtrinidad',CURRENT_DATE);

INSERT INTO seleccion.datos_especificos (descripcion,valor_alf,id_datos_generales,activo,usu_alta,fecha_alta) VALUES
('COPIA DE PARTIDA DE NACIMIENTO','LEG_DF',2,TRUE,'rtrinidad',CURRENT_DATE);

INSERT INTO seleccion.datos_especificos (descripcion,valor_alf,id_datos_generales,activo,usu_alta,fecha_alta) VALUES
('OTROS','LEG_DF',2,TRUE,'rtrinidad',CURRENT_DATE);



--UBICAR LOS DOCUMENTOS DE DATOS FAMILIARES
SELECT * FROM general.documentos
WHERE nombre_tabla = 'datos_familiares_legajo'
AND nombre_pantalla = 'datos_familiares_legajo'

---ACTUALIZAR LOS REGISTROS ANTERIOES
UPDATE general.documentos
SET nombre_pantalla = 'AdmDatosFamilia644',nombre_tabla = 'Familiares'
WHERE nombre_pantalla = 'datos_familiares_legajo'
AND nombre_tabla = 'datos_familiares_legajo';



---TEST
SELECT id_datos_especificos,descripcion,valor_alf FROM seleccion.datos_especificos
WHERE id_datos_especificos IN (
SELECT id_datos_especificos_tipo_documento FROM general.documentos
WHERE nombre_tabla = 'Familiares'
AND nombre_pantalla = 'AdmDatosFamilia644'
GROUP BY id_datos_especificos_tipo_documento
)


--ACTUALIZAR TIPO DE documentos
--COPIA DE PARTIDA DE NACIMIENTO 1768
UPDATE general.documentos
SET id_datos_especificos_tipo_documento = 1768
WHERE nombre_pantalla = 'AdmDatosFamilia644' 
AND nombre_tabla = 'Familiares' AND id_datos_especificos_tipo_documento = 1548;

---OTROS 1769
UPDATE general.documentos
SET id_datos_especificos_tipo_documento = 1769
WHERE nombre_pantalla = 'AdmDatosFamilia644' 
AND nombre_tabla = 'Familiares' AND id_datos_especificos_tipo_documento = 1737;

--CERTIFICADO DE MATRIMONIO 1765
UPDATE general.documentos
SET id_datos_especificos_tipo_documento = 1765
WHERE nombre_pantalla = 'AdmDatosFamilia644' 
AND nombre_tabla = 'Familiares' AND id_datos_especificos_tipo_documento = 1679;

