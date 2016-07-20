
--TABLA PROC_ACTIVIDAD_ROL, INSERCION DE ENLACES DE ROLES Y ACTIVIDADES/PROCESOS PARA EL PROCESO TRASLADO SICCA DEL MODULO MOVILIDAD

--1ro aseguramos que el seq esté correcto
SELECT setval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq', (select max(id_proc_actividad_rol) from proceso.proc_actividad_rol));

INSERT INTO proceso.proc_actividad_rol(id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'), 3, 46, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO proceso.proc_actividad_rol(id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'), 3, 47, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO proceso.proc_actividad_rol(id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'), 3, 48, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO proceso.proc_actividad_rol(id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'), 3, 49, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO proceso.proc_actividad_rol(id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'), 3, 50, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO proceso.proc_actividad_rol(id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'), 3, 51, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);

INSERT INTO proceso.proc_actividad_rol(id_proc_actividad_rol, id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta, usu_mod, fecha_mod)
    VALUES (nextVal('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'), 3, 52, TRUE, 'SORUE', (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), NULL, NULL);