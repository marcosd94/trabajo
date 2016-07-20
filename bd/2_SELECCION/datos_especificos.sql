

update seleccion.datos_especificos set descripcion='ABUELO/A'  where descripcion='ABUELO';
update seleccion.datos_especificos set descripcion='PADRE/MADRE'  where descripcion='PADRE';
update seleccion.datos_especificos set descripcion='SOBRINO/A'  where descripcion='MADRE';
update seleccion.datos_especificos set descripcion='SOBRINO/A'  where descripcion='MADRE';
update seleccion.datos_especificos set descripcion='BISABUELO/A'  where descripcion='ABUELA';
update seleccion.datos_especificos set descripcion='HERMANO/A'  where descripcion='HERMANO';
update seleccion.datos_especificos set descripcion='BISNIETO/A'  where descripcion='HERMANA';
update seleccion.datos_especificos set descripcion='TATARANIETO/A'  where descripcion='HIJA';


 INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta)
     VALUES (1560,'NIETO/A','C',10,68,true, 'Duarte', '2013-02-02' );
 INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta)
     VALUES (1551,'SOBRINO/A NIETO/A','C',11,68,true, 'Duarte', '2013-02-02' );
 INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta)
     VALUES (1552,'TATARABUELO/A','C',12,68,true, 'Duarte', '2013-02-02' );
 INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta)
     VALUES (1553,'TIO/A ABUELO/A','C',13,68,true, 'Duarte', '2013-02-02' );
 INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta)
     VALUES (1554,'PRIMO/A','C',14,68,true, 'Duarte', '2013-02-02' );
 INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta)
     VALUES (1555,'HIJO/A DEL CONYUGE','A',15,68,true, 'Duarte', '2013-02-02' );
 INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta)
     VALUES (1556,'SUEGRO/A','A',16,68,true, 'Duarte', '2013-02-02' );
 INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta)
     VALUES (1557,'NIETO/A DEL CONYUGE','A',17,68,true, 'Duarte', '2013-02-02' );
 INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta)
     VALUES (1558,'CUÑADO/A','A',18,68,true, 'Duarte', '2013-02-02' );
 INSERT INTO seleccion.datos_especificos (id_datos_especificos, descripcion, valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta)
     VALUES (1559,'ABUELO/A DEL CONYUGE','C',19,68,true, 'Duarte', '2013-02-02' );

