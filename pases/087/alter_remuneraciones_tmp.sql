ALTER TABLE remuneracion.remuneraciones_tmp ADD COLUMN id_configuracion_uo bigint;
ALTER TABLE remuneracion.remuneraciones_tmp ADD COLUMN id_persona bigint;

ALTER TABLE remuneracion.historico_remuneraciones_tmp ADD COLUMN id_configuracion_uo bigint;
ALTER TABLE remuneracion.historico_remuneraciones_tmp ADD COLUMN id_persona bigint;

ALTER TABLE remuneracion.remuneraciones_tmp ADD FOREIGN KEY (id_configuracion_uo) REFERENCES planificacion.configuracion_uo_cab (id_configuracion_uo) MATCH SIMPLE;
ALTER TABLE remuneracion.remuneraciones_tmp ADD FOREIGN KEY (id_persona) REFERENCES general.persona (id_persona) MATCH SIMPLE;

ALTER TABLE remuneracion.historico_remuneraciones_tmp ADD FOREIGN KEY (id_configuracion_uo) REFERENCES planificacion.configuracion_uo_cab (id_configuracion_uo) MATCH SIMPLE;
ALTER TABLE remuneracion.historico_remuneraciones_tmp ADD FOREIGN KEY (id_persona) REFERENCES general.persona (id_persona) MATCH SIMPLE;