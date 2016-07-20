ALTER TABLE seleccion.grupo_concepto_pago ALTER COLUMN categoria DROP NOT NULL;

insert into seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_num, activo, usu_alta, fecha_alta)
values (309, 'OBJETOS_GASTO_CONT', 144, 144, 144,'t', 'ADMIN', LOCALTIMESTAMP);

insert into seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_num, activo, usu_alta, fecha_alta)
values (310, 'OBJETOS_GASTO_CONT', 145, 145, 145,'t', 'ADMIN', LOCALTIMESTAMP);

/*
insert into sinarh.sin_obj (id_obj, ani_aniopre, obj_codigo, obj_nombre) values (447, 2014, 141, 'CONTRATACIÓN DE PERSONAL TÉCNICO');
insert into sinarh.sin_obj (id_obj, ani_aniopre, obj_codigo, obj_nombre) values (448, 2014, 142, 'CONTRATACIÓN DE PERSONAL DE SALUD');
insert into sinarh.sin_obj (id_obj, ani_aniopre, obj_codigo, obj_nombre) values (449, 2014, 143, 'CONTRATACIÓN OCASIONAL DEL PERSONAL DOCENTE Y DE BLANCO'); */
insert into sinarh.sin_obj (id_obj, ani_aniopre, obj_codigo, obj_nombre) values (450, 2014, 141, 'JORNALES');
insert into sinarh.sin_obj (id_obj, ani_aniopre, obj_codigo, obj_nombre) values (451, 2014, 142, 'HONORARIOS PROFESIONALES');


select max(id_sin_categoria_contratados) from sinarh.sin_categoria_contratados;
SELECT nextval('sinarh.sin_categoria_contratados_id_sin_categoria_contratados_seq');

/*
INSERT INTO sinarh.sin_categoria_contratados (ani_aniopre, obj_codigo, con_ctg, ctg_descrip, ctg_linea, fecha_alta)
VALUES ('2014', 141, 'XT3', 'AUXILIAR TECNICO', 3,  LOCALTIMESTAMP);
INSERT INTO sinarh.sin_categoria_contratados (ani_aniopre, obj_codigo, con_ctg, ctg_descrip, ctg_linea, fecha_alta)
VALUES ('2014', 141, 'XT4', 'PROFESOR/A', 4,  LOCALTIMESTAMP);

INSERT INTO sinarh.sin_categoria_contratados (ani_aniopre, obj_codigo, con_ctg, ctg_descrip, ctg_linea, fecha_alta)
VALUES ('2014', 142, 'XS8', 'AUXILIAR DE SALUD', 13,  LOCALTIMESTAMP);
INSERT INTO sinarh.sin_categoria_contratados (ani_aniopre, obj_codigo, con_ctg, ctg_descrip, ctg_linea, fecha_alta)
VALUES ('2014', 142, 'XS2', 'PROFESIONAL SUPERIOR (II)', 10,  LOCALTIMESTAMP);
INSERT INTO sinarh.sin_categoria_contratados (ani_aniopre, obj_codigo, con_ctg, ctg_descrip, ctg_linea, fecha_alta)
VALUES ('2014', 142, 'XS6', 'PROFESIONAL DE SALUD', 11,  LOCALTIMESTAMP);

INSERT INTO sinarh.sin_categoria_contratados (ani_aniopre, obj_codigo, con_ctg, ctg_descrip, ctg_linea, fecha_alta)
VALUES ('2014', 143, 'XR6', 'ENCARGADO DE CATEDRA', 20,  LOCALTIMESTAMP);

INSERT INTO sinarh.sin_categoria_contratados (ani_aniopre, obj_codigo, con_ctg, ctg_descrip, ctg_linea, fecha_alta)
VALUES ('2014', 144, 'XJ7', 'TÉCNICO JUDICIAL', 40,  LOCALTIMESTAMP);
INSERT INTO sinarh.sin_categoria_contratados (ani_aniopre, obj_codigo, con_ctg, ctg_descrip, ctg_linea, fecha_alta)
VALUES ('2014', 144, 'XJ8', 'ASISTENTE JUDICIAL', 41,  LOCALTIMESTAMP);
INSERT INTO sinarh.sin_categoria_contratados (ani_aniopre, obj_codigo, con_ctg, ctg_descrip, ctg_linea, fecha_alta)
VALUES ('2014', 144, 'XJ9', 'AUXILIAR JUDICIAL', 42,  LOCALTIMESTAMP); */

INSERT INTO sinarh.sin_categoria_contratados (ani_aniopre, obj_codigo, con_ctg, ctg_descrip, ctg_linea, fecha_alta)
VALUES ('2014', 145, 'XC1', 'CONSULTOR/ESPECIALISTA NACIONAL X UNIDAD EJECUTORA DE PROYECTO', 38,  LOCALTIMESTAMP);
INSERT INTO sinarh.sin_categoria_contratados (ani_aniopre, obj_codigo, con_ctg, ctg_descrip, ctg_linea, fecha_alta)
VALUES ('2014', 145, 'XC2', 'CONSULTOR/ESPECIALISTA NACIONAL X AGENCIA ESPECIALIZADA U ORG. INTERNACIONAL', 39,  LOCALTIMESTAMP);


insert into sinarh.sin_anx
(ani_aniopre, nen_codigo, ent_codigo, tip_codigo, pro_codigo, sub_codigo, pry_codigo,
obj_codigo, fue_codigo, fin_codigo, vrs_codigo, anx_linea, cat_grupo, ctg_codigo,
mdf_codigo, anx_tiporeg, anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,
pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring, id_anx)
values(2014, 12, 1, 1,8,0, 0,
111, 10, 1, 50, 14000, 50, 'CE8',
'','C', 1, 12,44950800,1, 'JEFE DE DEPARTAMENTO',
1,99, '', LOCALTIMESTAMP,'SINARH', 51456);

insert into sinarh.sin_categoria
(ani_aniopre, cat_grupo, ctg_codigo,
ctg_valor1, ctg_valor2, ctg_valor3, ctg_valor4, ctg_valor5, ctg_valor6,
ctg_valor7, ctg_valor8, ctg_valor9, ctg_valor10, ctg_valor11, ctg_valor12,
vrs_codigo, id_sin_categoria)
values(2014, 50, 'CE8',
3745900, 3745900, 3745900, 3745900, 3745900, 3745900,
3745900, 3745900, 3745900, 3745900, 3745900, 3745900,
50, 82391);




