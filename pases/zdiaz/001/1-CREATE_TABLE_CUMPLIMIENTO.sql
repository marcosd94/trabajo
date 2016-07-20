-- Table: remuneracion.informe_cumplimiento

-- DROP TABLE remuneracion.informe_cumplimiento;

CREATE TABLE remuneracion.informe_cumplimiento
(
  anho integer,
  mes integer,
  nivel integer,
  nivel_desc character varying(60),
  entidad integer,
  oee integer,
  denominacion_unidad character varying(100),
  agrupamiento character varying(100),
  documento_identidad character varying(2) DEFAULT 'no'::character varying,
  anho_ingreso character varying(2) DEFAULT 'no'::character varying,
  "111" character varying(2) DEFAULT 'no'::character varying,
  "112" character varying(2) DEFAULT 'no'::character varying,
  "113" character varying(2) DEFAULT 'no'::character varying,
  "114" character varying(2) DEFAULT 'no'::character varying,
  "122" character varying(2) DEFAULT 'no'::character varying,
  "123" character varying(2) DEFAULT 'no'::character varying,
  "125" character varying(2) DEFAULT 'no'::character varying,
  "131" character varying(2) DEFAULT 'no'::character varying,
  "132" character varying(2) DEFAULT 'no'::character varying,
  "133" character varying(2) DEFAULT 'no'::character varying,
  "134" character varying(2) DEFAULT 'no'::character varying,
  "135" character varying(2) DEFAULT 'no'::character varying,
  "136" character varying(2) DEFAULT 'no'::character varying,
  "137" character varying(2) DEFAULT 'no'::character varying,
  "138" character varying(2) DEFAULT 'no'::character varying,
  "139" character varying(2) DEFAULT 'no'::character varying,
  "141" character varying(2) DEFAULT 'no'::character varying,
  "142" character varying(2) DEFAULT 'no'::character varying,
  "143" character varying(2) DEFAULT 'no'::character varying,
  "144" character varying(2) DEFAULT 'no'::character varying,
  "145" character varying(2) DEFAULT 'no'::character varying,
  "146" character varying(2) DEFAULT 'no'::character varying,
  "147" character varying(2) DEFAULT 'no'::character varying,
  "148" character varying(2) DEFAULT 'no'::character varying,
  "161" character varying(2) DEFAULT 'no'::character varying,
  "162" character varying(2) DEFAULT 'no'::character varying,
  "163" character varying(2) DEFAULT 'no'::character varying,
  "191" character varying(2) DEFAULT 'no'::character varying,
  "192" character varying(2) DEFAULT 'no'::character varying,
  "193" character varying(2) DEFAULT 'no'::character varying,
  "194" character varying(2) DEFAULT 'no'::character varying,
  "195" character varying(2) DEFAULT 'no'::character varying,
  "199" character varying(2) DEFAULT 'no'::character varying,
  "232" character varying(2) DEFAULT 'no'::character varying,
  "842" character varying(2) DEFAULT 'no'::character varying,
  "845" character varying(2) DEFAULT 'no'::character varying,
  "846" character varying(2) DEFAULT 'no'::character varying
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remuneracion.informe_cumplimiento
  OWNER TO postgres;