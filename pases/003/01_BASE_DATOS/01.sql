INSERT into sinarh.sin_nivel_entidad
(ani_aniopre, nen_codigo, nen_nombre, nen_nomabr, nen_imputable, fecha_alta, usu_alta,activo, id_sin_nivel_entidad)
values
(2014, 12, 'PODER EJECUTIVO', 'PODER EJECUTIVO', 'S', now(), 'ADMIN', true, (select max(id_sin_nivel_entidad) from sinarh.sin_nivel_entidad) + 1);


SELECT setval('sinarh.sin_entidad_id_sin_entidad_seq', max(id_sin_entidad)) FROM SINARH.SIN_ENTIDAD;

INSERT into sinarh.sin_entidad
	( ani_aniopre,nen_codigo,ent_codigo,ent_nombre,ent_nomabre,ent_sigla,ent_ruc, cof_codigo, fecha_alta,usu_alta,activo	)
	select 2014,nen_codigo,ent_codigo,ent_nombre,ent_nomabre,ent_sigla,ent_ruc, cof_codigo, now(),'ADMIN' ,true
	from
	sinarh.sin_entidad e
	where e.ani_aniopre = 2012 and id_sin_entidad = 329;
	
update planificacion.entidad set id_sin_entidad = 653, anho = 2014
where id_entidad = 330;

update planificacion.configuracion_uo_cab set anho = 2014, orden = 8 , codigo_sinarh = '12.1.1.8'
where id_configuracion_uo = 1000 ;

insert into planificacion.entidad_oee
(id_entidad_oee, anho, id_sin_entidad, nen_codigo, ent_codigo, id_configuracion_uo, orden)
values(1, 2014, 653, 12, 1, 1000, 8)

insert into sinarh.sin_obj (id_obj, ani_aniopre, obj_codigo, obj_nombre)
values (445, 2014, 111, 'SUELDOS');
insert into sinarh.sin_obj (id_obj, ani_aniopre, obj_codigo, obj_nombre)
values (446, 2014, 113, 'GASTOS DE REPRESENTACIÓN');

insert into sinarh.sin_anx
(ani_aniopre, nen_codigo, ent_codigo, tip_codigo, pro_codigo, sub_codigo, pry_codigo,
obj_codigo, fue_codigo, fin_codigo, vrs_codigo, anx_linea, cat_grupo, ctg_codigo,
mdf_codigo, anx_tiporeg, anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,
pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring, id_anx)
values(2014, 12, 1, 1,8,0, 0,
111, 10, 1, 50, 20000, 50, 'D5G',
'','C', 1, 12, 30968400,1, 'PROFESIONAL II',
1,99, '', now(),'SINARH', 51447);

insert into sinarh.sin_anx
(ani_aniopre, nen_codigo, ent_codigo, tip_codigo, pro_codigo, sub_codigo, pry_codigo,
obj_codigo, fue_codigo, fin_codigo, vrs_codigo, anx_linea, cat_grupo, ctg_codigo,
mdf_codigo, anx_tiporeg, anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,
pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring, id_anx)
values(2014, 12, 1, 1,8,0, 0,
111, 10, 1, 50, 20100, 50, 'DB8',
'','C', 1, 12, 25713600,1, 'PROFESIONAL II',
1,99, '', now(),'SINARH', 51448);

insert into sinarh.sin_anx
(ani_aniopre, nen_codigo, ent_codigo, tip_codigo, pro_codigo, sub_codigo, pry_codigo,
obj_codigo, fue_codigo, fin_codigo, vrs_codigo, anx_linea, cat_grupo, ctg_codigo,
mdf_codigo, anx_tiporeg, anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,
pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring, id_anx)
values(2014, 12, 1, 1,8,0, 0,
111, 10, 1, 50, 22000, 50, 'DI4',
'','C', 1, 12, 30968400,1, 'TECNICO I',
1,99, '', now(),'SINARH', 51449);

insert into sinarh.sin_anx
(ani_aniopre, nen_codigo, ent_codigo, tip_codigo, pro_codigo, sub_codigo, pry_codigo,
obj_codigo, fue_codigo, fin_codigo, vrs_codigo, anx_linea, cat_grupo, ctg_codigo,
mdf_codigo, anx_tiporeg, anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,
pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring, id_anx)
values(2014, 12, 1, 1,8,0, 0,
111, 10, 1, 50, 24400, 50, 'E3P',
'','C', 1, 12, 25352400,1, 'TECNICO II',
1,99, '', now(),'SINARH', 51450);

insert into sinarh.sin_anx
(ani_aniopre, nen_codigo, ent_codigo, tip_codigo, pro_codigo, sub_codigo, pry_codigo,
obj_codigo, fue_codigo, fin_codigo, vrs_codigo, anx_linea, cat_grupo, ctg_codigo,
mdf_codigo, anx_tiporeg, anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,
pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring, id_anx)
values(2014, 12, 1, 1,8,0, 0,
111, 10, 1, 50, 24300, 50, 'ER4',
'','C', 1, 12, 20751192,1, 'TECNICO II',
1,99, '', now(),'SINARH', 51452);

insert into sinarh.sin_anx
(ani_aniopre, nen_codigo, ent_codigo, tip_codigo, pro_codigo, sub_codigo, pry_codigo,
obj_codigo, fue_codigo, fin_codigo, vrs_codigo, anx_linea, cat_grupo, ctg_codigo,
mdf_codigo, anx_tiporeg, anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,
pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring, id_anx)
values(2014, 12, 1, 1,8,0, 0,
111, 10, 1, 50, 26000, 50, 'GE1',
'','C', 1, 12, 19898784,1, 'AUXILIAR ADMINISTRATIVO',
1,99, '', now(),'SINARH', 51453);

insert into sinarh.sin_anx
(ani_aniopre, nen_codigo, ent_codigo, tip_codigo, pro_codigo, sub_codigo, pry_codigo,
obj_codigo, fue_codigo, fin_codigo, vrs_codigo, anx_linea, cat_grupo, ctg_codigo,
mdf_codigo, anx_tiporeg, anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,
pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring, id_anx)
values(2014, 12, 1, 1,8,0, 0,
111, 10, 1, 50, 7000, 50, 'BF7',
'','C', 1, 12, 45195600,1, 'DIRECTOR',
1,99, '', now(),'SINARH', 51454);

insert into sinarh.sin_anx
(ani_aniopre, nen_codigo, ent_codigo, tip_codigo, pro_codigo, sub_codigo, pry_codigo,
obj_codigo, fue_codigo, fin_codigo, vrs_codigo, anx_linea, cat_grupo, ctg_codigo,
mdf_codigo, anx_tiporeg, anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,
pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring, id_anx)
values(2014, 12, 1, 1,8,0, 0,
113, 10, 1, 50, 46000, 50, 'S90',
'','C', 1, 12, 23386800,1, 'DIRECTOR GENERAL',
1,99, '', now(),'SINARH', 51455);


insert into sinarh.sin_categoria
(ani_aniopre, cat_grupo, ctg_codigo,
ctg_valor1, ctg_valor2, ctg_valor3, ctg_valor4, ctg_valor5, ctg_valor6,
ctg_valor7, ctg_valor8, ctg_valor9, ctg_valor10, ctg_valor11, ctg_valor12,
vrs_codigo, id_sin_categoria)
values(2014, 50, 'D5G',
2580700, 2580700, 2580700, 2580700, 2580700, 2580700,
2580700, 2580700,2580700, 2580700, 2580700, 2580700, 
50, 82382);

insert into sinarh.sin_categoria
(ani_aniopre, cat_grupo, ctg_codigo,
ctg_valor1, ctg_valor2, ctg_valor3, ctg_valor4, ctg_valor5, ctg_valor6,
ctg_valor7, ctg_valor8, ctg_valor9, ctg_valor10, ctg_valor11, ctg_valor12,
vrs_codigo, id_sin_categoria)
values(2014, 50, 'DB8',
2142800, 2142800, 2142800, 2142800, 2142800, 2142800,
2142800, 2142800, 2142800, 2142800, 2142800, 2142800,
50, 82383);

insert into sinarh.sin_categoria
(ani_aniopre, cat_grupo, ctg_codigo,
ctg_valor1, ctg_valor2, ctg_valor3, ctg_valor4, ctg_valor5, ctg_valor6,
ctg_valor7, ctg_valor8, ctg_valor9, ctg_valor10, ctg_valor11, ctg_valor12,
vrs_codigo, id_sin_categoria)
values(2014, 50, 'DI4',
2580700, 2580700, 2580700, 2580700, 2580700, 2580700,
2580700, 2580700, 2580700, 2580700, 2580700, 2580700,
50, 82384);


insert into sinarh.sin_categoria
(ani_aniopre, cat_grupo, ctg_codigo,
ctg_valor1, ctg_valor2, ctg_valor3, ctg_valor4, ctg_valor5, ctg_valor6,
ctg_valor7, ctg_valor8, ctg_valor9, ctg_valor10, ctg_valor11, ctg_valor12,
vrs_codigo, id_sin_categoria)
values(2014, 50, 'E3P',
2112700, 2112700, 2112700, 2112700, 2112700, 2112700,
2112700, 2112700, 2112700, 2112700, 2112700, 2112700,
50, 82385);

insert into sinarh.sin_categoria
(ani_aniopre, cat_grupo, ctg_codigo,
ctg_valor1, ctg_valor2, ctg_valor3, ctg_valor4, ctg_valor5, ctg_valor6,
ctg_valor7, ctg_valor8, ctg_valor9, ctg_valor10, ctg_valor11, ctg_valor12,
vrs_codigo, id_sin_categoria)
values(2014, 50, 'E3N',
2086300, 2086300, 2086300, 2086300, 2086300, 2086300,
2086300, 2086300, 2086300, 2086300, 2086300, 2086300,
50, 82386);

insert into sinarh.sin_categoria
(ani_aniopre, cat_grupo, ctg_codigo,
ctg_valor1, ctg_valor2, ctg_valor3, ctg_valor4, ctg_valor5, ctg_valor6,
ctg_valor7, ctg_valor8, ctg_valor9, ctg_valor10, ctg_valor11, ctg_valor12,
vrs_codigo, id_sin_categoria)
values(2014, 50, 'ER4',
1729266, 1729266, 1729266, 1729266, 1729266, 1729266,
1729266, 1729266, 1729266, 1729266, 1729266, 1729266,
50, 82387);


insert into sinarh.sin_categoria
(ani_aniopre, cat_grupo, ctg_codigo,
ctg_valor1, ctg_valor2, ctg_valor3, ctg_valor4, ctg_valor5, ctg_valor6,
ctg_valor7, ctg_valor8, ctg_valor9, ctg_valor10, ctg_valor11, ctg_valor12,
vrs_codigo, id_sin_categoria)
values(2014, 50, 'GE1',
1658232, 1658232, 1658232, 1658232, 1658232, 1658232,
1658232, 1658232, 1658232, 1658232, 1658232, 1658232,
50, 82388);

insert into sinarh.sin_categoria
(ani_aniopre, cat_grupo, ctg_codigo,
ctg_valor1, ctg_valor2, ctg_valor3, ctg_valor4, ctg_valor5, ctg_valor6,
ctg_valor7, ctg_valor8, ctg_valor9, ctg_valor10, ctg_valor11, ctg_valor12,
vrs_codigo, id_sin_categoria)
values(2014, 50, 'BF7',
3766300, 3766300, 3766300, 3766300, 3766300, 3766300,
3766300, 3766300, 3766300, 3766300, 3766300, 3766300,
50, 82389);

insert into sinarh.sin_categoria
(ani_aniopre, cat_grupo, ctg_codigo,
ctg_valor1, ctg_valor2, ctg_valor3, ctg_valor4, ctg_valor5, ctg_valor6,
ctg_valor7, ctg_valor8, ctg_valor9, ctg_valor10, ctg_valor11, ctg_valor12,
vrs_codigo, id_sin_categoria)
values(2014, 50, 'S90',
1948900, 1948900, 1948900, 1948900, 1948900, 1948900,
1948900, 1948900, 1948900, 1948900, 1948900, 1948900,
50, 82390);


ALTER TABLE sinarh.sin_anx DROP COLUMN fecha_alta;

ALTER TABLE sinarh.sin_anx ADD COLUMN fecha_alta timestamp(6) without time zone;

update sinarh.sin_anx set cant_disponible = 200 , fecha_alta = LOCALTIMESTAMP ;

insert into seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_alf, valor_num, activo, usu_alta, fecha_alta)
values(305, 'ESTADOS_CATEGORIA_GRUPO', 'RESERVADO', 'RESERVADO', '', 1, TRUE, 'ADMIN', LOCALTIMESTAMP);
insert into seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_alf, valor_num, activo, usu_alta, fecha_alta)
values(306, 'ESTADOS_CATEGORIA_GRUPO', 'PENDIENTE', 'PENDIENTE', '', 1, TRUE, 'ADMIN', LOCALTIMESTAMP);

insert into proceso.actividad (id_actividad, descripcion, activo, usu_alta, fecha_alta, usu_mod, cod_actividad, tipo, nro_actividad)
values (118, 'COMPLETAR CARPETAS', TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', 'COMPLETAR_CARPETAS', 'GRUPO', '013');

update seleccion.referencias set valor_num = 0 where id_referencias = 98;
update seleccion.referencias set valor_num = 5 where id_referencias = 95;
update seleccion.referencias set valor_num = 5 where id_referencias = 96;

update seleccion.datos_especificos set valor_alf = 'A'
where id_datos_especificos in (1149);

update seleccion.datos_especificos set valor_alf = 'C'
where id_datos_especificos in (1146,1147,1148,1150,1151,1152,1153);


insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(25, 2.00,'D','', 6,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);
insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(26, 3.00,'D','', 7,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);
insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(27, 5.00,'D','', 10,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);
insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(28, 10.00,'D','', 118,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);
insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(29, 10.00,'D','', 11,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);
insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(30, 2.00,'D','', 12,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);



update seleccion.sel_funcion_datos_esp set id_datos_especificos_tipo_doc = 28
where id_funcion_datos_esp = 5;

insert into seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_num, activo, usu_alta, fecha_alta)
values (307, 'OBJETOS_GASTO_PERM', '113', '113', 113, TRUE, 'ADMIN', LOCALTIMESTAMP); 

insert into seleccion.datos_especificos (id_datos_especificos, descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
values (1456, 'PUBLICACIÓN DEL LLAMADO', 34,TRUE, 'ADMIN', LOCALTIMESTAMP); 

insert into seleccion.datos_especificos (id_datos_especificos, descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
values (1457, 'EVALUACIÓN DE DOCUMENTOS', 34,TRUE, 'ADMIN', LOCALTIMESTAMP); 

insert into seleccion.datos_especificos (id_datos_especificos, descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
values (1458, 'NÓMINA DE ADMITIDOS AL CONCURSO', 34,TRUE, 'ADMIN', LOCALTIMESTAMP); 

insert into seleccion.datos_especificos (id_datos_especificos, descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
values (1459, 'REUNIÓN INFORMATIVA', 34,TRUE, 'ADMIN', LOCALTIMESTAMP); 

insert into seleccion.datos_especificos (id_datos_especificos, descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
values (1460, 'APLICACIÓN DE LAS EVALUACIONES', 34,TRUE, 'ADMIN', LOCALTIMESTAMP); 

insert into seleccion.datos_especificos (id_datos_especificos, descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
values (1461, 'PUBLICACIÓN DE LA LISTA CORTA', 34,TRUE, 'ADMIN', LOCALTIMESTAMP); 

insert into seleccion.datos_especificos (id_datos_especificos, descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
values (1462, 'CONFORMACIÓN DE LA TERNA', 34,TRUE, 'ADMIN', LOCALTIMESTAMP); 

insert into seleccion.datos_especificos (id_datos_especificos, descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
values (1463, 'ENTREVISTA FINAL CON MÁXIMA AUTORIDAD', 34,TRUE, 'ADMIN', LOCALTIMESTAMP); 

insert into seleccion.datos_especificos (id_datos_especificos, descripcion, id_datos_generales, activo, usu_alta, fecha_alta)
values (1464, 'PUBLICACIÓN DE LAS PERSONAS SELECCIONADAS', 34,TRUE, 'ADMIN', LOCALTIMESTAMP); 

update seleccion.datos_especificos set descripcion = 'LISTA DE ADMITIDOS'
where id_datos_especificos = 48;

update seleccion.datos_especificos set descripcion = 'RECEPCIÓN DE POSTULACIONES'
where id_datos_especificos = 46;

update seleccion.datos_especificos set descripcion = 'APLICACIÓN DE LAS EVALUACIONES'
where id_datos_especificos = 47;


/* Auxiliares en caso de ser necesario actualizar los secuenciales
select max(id_sin_entidad) from SINARH.SIN_ENTIDAD;
select * from sinarh.sin_entidad_id_sin_entidad_seq;
SELECT nextval('sinarh.sin_entidad_id_sin_entidad_seq');

-- este sql ajusta el seq
SELECT setval('sinarh.sin_entidad_id_sin_entidad_seq', max(id_sin_entidad)) FROM SINARH.SIN_ENTIDAD;
*/
