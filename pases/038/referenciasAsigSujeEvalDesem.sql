SELECT setval('seleccion.sel_referencias_id_referencias_seq', (select max(id_referencias) from seleccion.referencias));

INSERT INTO seleccion.referencias VALUES (nextval('seleccion.sel_referencias_id_referencias_seq'::regclass), 'EVAL_CU551', 'SOLO PUESTOS CON JEFATURAS', 'SOLO PUESTOS CON JEFATURAS', 'J', NULL, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO seleccion.referencias VALUES (nextval('seleccion.sel_referencias_id_referencias_seq'::regclass), 'EVAL_CU551', 'PUESTOS OPERATIVOS', 'PUESTOS OPERATIVOS', 'O', NULL, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO seleccion.referencias VALUES (nextval('seleccion.sel_referencias_id_referencias_seq'::regclass), 'EVAL_CU551', 'PUESTOS CONTRATADOS', 'PUESTOS CONTRATADOS', 'C', NULL, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO seleccion.referencias VALUES (nextval('seleccion.sel_referencias_id_referencias_seq'::regclass), 'EVAL_CU551', 'PUESTOS PERMANENTES', 'PUESTOS PERMANENTES', 'P', NULL, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO seleccion.referencias VALUES (nextval('seleccion.sel_referencias_id_referencias_seq'::regclass), 'EVAL_CU551', 'PUESTOS COMISIONADOS', 'PUESTOS COMISIONADOS', 'M', NULL, true, 'WERNER', localtimestamp, NULL, NULL);
INSERT INTO seleccion.referencias VALUES (nextval('seleccion.sel_referencias_id_referencias_seq'::regclass), 'EVAL_CU551', 'TODOS LOS FUNCIONARIOS', 'TODOS LOS FUNCIONARIOS', 'T', NULL, true, 'WERNER', localtimestamp, NULL, NULL);
