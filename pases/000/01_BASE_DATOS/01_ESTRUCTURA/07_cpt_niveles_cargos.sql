CREATE TABLE planificacion.cpt_niveles_cargos
(
  id_cpt bigint NOT NULL,
  id_niveles_de_cargos bigint NOT NULL,
  id_cpt_niveles bigserial NOT NULL,
  CONSTRAINT cpt_niveles_de_cargos_pk PRIMARY KEY (id_cpt_niveles),
  CONSTRAINT cpt_fk FOREIGN KEY (id_cpt)
      REFERENCES planificacion.cpt (id_cpt) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT niveles_cargos_fk FOREIGN KEY (id_niveles_de_cargos)
      REFERENCES planificacion.niveles_de_cargos (id_niveles_de_cargos) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);