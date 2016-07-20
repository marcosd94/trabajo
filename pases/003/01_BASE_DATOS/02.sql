insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(31, 2.00,'D','', 14,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);

insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(32, 2.00,'D','', 15,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);

insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(33, 5.00,'D','', 18,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);

insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(34, 5.00,'D','', 19,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);

insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(35, 5.00,'D','', 21,1,FALSE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);

insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(37, 5.00,'D','', 22,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);

insert into proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion,
id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
values(38, 5.00,'D','', 24,1,TRUE, 'ADMIN', LOCALTIMESTAMP, 'ADMIN', LOCALTIMESTAMP);

insert into seleccion.referencias (id_referencias, dominio, desC_abrev, desc_larga, valor_alf,
valor_num, activo, usu_alta, fecha_alta)
values(308, 'PRORROGA_CONC_NUM', '', 'CANTIDAD MAXIMA DE PRORROGAS', 'CPO', 2, TRUE,'ADMIN', LOCALTIMESTAMP);

ALTER TABLE seleccion.eval_referencial_postulante ADD COLUMN incluido boolean;
