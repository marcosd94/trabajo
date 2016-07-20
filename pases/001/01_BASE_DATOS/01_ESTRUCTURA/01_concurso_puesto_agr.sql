ALTER TABLE seleccion.concurso_puesto_agr ADD COLUMN otras_remuneraciones character varying(1000);
ALTER TABLE seleccion.concurso_puesto_agr ADD COLUMN prorroga_num integer;

ALTER TABLE seleccion.concurso_puesto_agr ALTER COLUMN otras_remuneraciones TYPE character varying(5000);
ALTER TABLE seleccion.concurso_puesto_agr ALTER COLUMN remuneracion TYPE character varying(5000);