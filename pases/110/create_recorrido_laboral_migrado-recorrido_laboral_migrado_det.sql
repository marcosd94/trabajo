-- Table: legajo.recorrido_laboral_migrado

-- DROP TABLE legajo.recorrido_laboral_migrado;

CREATE TABLE legajo.recorrido_laboral_migrado
(
  id_recorrido serial NOT NULL,
  id_persona integer,
  cargo character varying(250),
  oee character varying(150),
  observacion character varying(5000),
  activo boolean,
  id_legajo integer,
  CONSTRAINT pk_recorrido PRIMARY KEY (id_recorrido)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE legajo.recorrido_laboral_migrado
  OWNER TO postgres;


-- Table: legajo.recorrido_laboral_migrado_det

-- DROP TABLE legajo.recorrido_laboral_migrado_det;

CREATE TABLE legajo.recorrido_laboral_migrado_det
(
  id_recorrido_det serial NOT NULL,
  nro_resolucion character varying(100),
  fecha_resolucion date,
  fecha_inicio date,
  fecha_fin date,
  observacion character varying(5000),
  idlegajo integer,
  CONSTRAINT pk_recorrido_laboral_det PRIMARY KEY (id_recorrido_det)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE legajo.recorrido_laboral_migrado_det
  OWNER TO postgres;
