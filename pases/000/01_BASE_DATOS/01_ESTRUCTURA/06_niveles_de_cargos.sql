CREATE TABLE planificacion.niveles_de_cargos
(
  id_niveles_de_cargos bigserial NOT NULL,
  tipo character(1),
  descripcion character varying(250),
  CONSTRAINT niveles_de_cargos_pkey PRIMARY KEY (id_niveles_de_cargos)
);