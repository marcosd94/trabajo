-- PARA VISUALIZAR REGISTROS...

-- para ver la actividad "ASIGNAR PLANTILLAS / PESOS" 					OEE
INSERT INTO proceso.proc_actividad_rol VALUES ( nextval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'::regclass), 32, 45, true, 'WERNER', localtimestamp, NULL, NULL);

-- para ver la actividad "GENERAR REGLAMENTO EVALUACION DE DESEMPEÑO" 	OEE
INSERT INTO proceso.proc_actividad_rol VALUES (nextval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'::regclass), 32, 41, true, 'WERNER', localtimestamp, NULL, NULL);

-- para ver la actividad "APROBAR EVALUACIÓN" 							SFP
INSERT INTO proceso.proc_actividad_rol VALUES (nextval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'::regclass), 33, 42, true, 'WERNER', localtimestamp, NULL, NULL);

-- para ver la actividad "REVISAR EVALUACIÓN" 							OEE
INSERT INTO proceso.proc_actividad_rol VALUES (nextval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'::regclass), 32, 44, true, 'WERNER', localtimestamp, NULL, NULL);

-- para ver la actividad "ADJUNTAR RESOLUCIÓN" 							SFP
INSERT INTO proceso.proc_actividad_rol VALUES (nextval('proceso.tbl_proc_actividad_rol_id_proc_actividad_rol_seq'::regclass), 33, 43, true, 'WERNER', localtimestamp, NULL, NULL);

-- ROL OEE

INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 739, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 736, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 476, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 493, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 461, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 481, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 503, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 483, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 501, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 498, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 732, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 588, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 559, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 735, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 524, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 737, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 731, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 487, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 469, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 504, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 497, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 458, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 464, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 485, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 475, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 477, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 522, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 500, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 457, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 466, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 460, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 691, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 463, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 467, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 495, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 540, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 692, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 724, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 494, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 693, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 519, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 502, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 492, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 482, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 468, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 734, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 733, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 488, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 496, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 484, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 459, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 486, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 462, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 690, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 465, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 499, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 60, 32);
-- para el manejo del comité, asignar y crear personas.
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 324, 32);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 325, 32);

-- ROL SFP

INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 738, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 491, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 489, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 490, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 475, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 462, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 690, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 465, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 486, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 459, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 484, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 488, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 733, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 734, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 492, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 468, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 482, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 731, 33);
--para entrar al formulario Gestionar
--Nuevos por el tema de Informe Final; Ver Resultados e Históricos....
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 493, 33); 
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 477, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 519, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 499, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 559, 33);
INSERT INTO seguridad.rol_funcion (id_rol_funcion, id_funcion, id_rol) VALUES (nextval('seguridad.rol_funcion_id_rol_funcion_seq'::regclass), 588, 33);