--REFERENCIAS PARA  CONCURSO Y CONCURSO_PUESTO_AGR
INSERT INTO seleccion.referencias VALUES ((select  max (r.id_referencias) from seleccion.referencias r)+1, 'ESTADOS_GRUPO', 'PUBLICACION', 'PUBLICACION', '', 1000, true, 'ADMIN', '2014-04-05 09:00:00', NULL, NULL);
INSERT INTO seleccion.referencias VALUES ((select  max (r.id_referencias) from seleccion.referencias r)+1, 'ESTADOS_GRUPO', 'PUBLICACION_EVALUACION', 'PUBLICACION_EVALUACION', '', 1001, true, 'ADMIN', '2014-04-05 09:00:00', NULL, NULL);
INSERT INTO seleccion.referencias VALUES ((select  max (r.id_referencias) from seleccion.referencias r)+1, 'ESTADOS_GRUPO', 'PUBLICACION_FINALIZADO', 'PUBLICACION_FINALIZADO', '', 1002, true, 'ADMIN', '2014-04-05 09:00:00', NULL, NULL);
INSERT INTO seleccion.referencias VALUES ((select  max (r.id_referencias) from seleccion.referencias r)+1, 'ESTADOS_CONCURSO', 'PUBLICACION', 'PUBLICACION', NULL, 1000, true, 'ADMIN', '2014-04-25 09:00:00', NULL, NULL);

--REFERENCIAS PARA LOS DOCUMENTOS A SER PUBLICADOS
INSERT INTO seleccion.datos_especificos VALUES ((select max (id_datos_especificos) from seleccion.datos_especificos)+1, 'Base y Condiciones', 'BASE_CONDICIONES', NULL, 2, true, 'ADMIN', '2014-04-25 00:00:00', NULL, NULL);
INSERT INTO seleccion.datos_especificos VALUES ((select max (id_datos_especificos) from seleccion.datos_especificos)+1, 'Documentos de Publicacion', 'DOC_PUBLICACION', NULL, 2, true, 'ADMIN', '2014-04-25 00:00:00', NULL, NULL);


--ALTER PARA LA TABLA DE DOCUMENTOS
ALTER TABLE general.documentos
  ADD COLUMN id_concurso_puesto_agr bigint;
ALTER TABLE general.documentos
  ADD CONSTRAINT id_concurso_puesto_agr_fk FOREIGN KEY (id_concurso_puesto_agr) REFERENCES seleccion.concurso_puesto_agr (id_concurso_puesto_agr) ON UPDATE NO ACTION ON DELETE NO ACTION;
