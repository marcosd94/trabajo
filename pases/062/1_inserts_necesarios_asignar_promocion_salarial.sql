--NUEVAS REFERENCIAS
INSERT INTO seleccion.referencias (id_referencias, dominio, desc_abrev, desc_larga, valor_alf, valor_num, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
 VALUES (nextVal('seleccion.sel_referencias_id_referencias_seq'), 'ESTADOS_GRUPO', 'ASIGNAR_PROMOCION_SALARIAL', 'ASIGNAR PROMOCION SALARIAL', '', 38, true, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL); 

 --NUEVA ACTIVIDAD
 INSERT INTO proceso.actividad (id_actividad, descripcion, activo, usu_alta, fecha_alta, usu_mod, fecha_mod, cod_actividad, tipo, descripcion_historial, nro_actividad, valor) 
VALUES (nextVal('proceso.tbl_actividad_id_actividad_seq'), 'ASIGNAR PROMOCION SALARIAL', true, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL, 'ASIGNAR_PROMOCION_SALARIAL', 'GRUPO', NULL, '120', NULL);

--ACTIVIDAD_PROCESO
INSERT INTO proceso.actividad_proceso (id_actividad_proceso, plazo_actividad, unidad_plazo, observacion, id_actividad, id_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod) 
VALUES (nextVal('proceso.tbl_actividad_proceso_id_actividad_proceso_seq'), 0.00, null, 'Asignar Promocion Salarial', (select id_actividad from proceso.actividad where descripcion = 'ASIGNAR PROMOCION SALARIAL'), 1, true, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), null, null);

--ROLES PARA LA ACTIVIDAD
INSERT INTO proceso.proc_actividad_rol (id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod) 
VALUES (nextVal('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'), 3, (select id_actividad_proceso from proceso.actividad_proceso where observacion = 'Asignar Promocion Salarial'), true, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO proceso.proc_actividad_rol (id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
VALUES (nextVal('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'), 27, (select id_actividad_proceso from proceso.actividad_proceso where observacion = 'Asignar Promocion Salarial'), true, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO proceso.proc_actividad_rol (id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod) 
VALUES (nextVal('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'), 26, (select id_actividad_proceso from proceso.actividad_proceso where observacion = 'Asignar Promocion Salarial'), true, 'ADMIN', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);


