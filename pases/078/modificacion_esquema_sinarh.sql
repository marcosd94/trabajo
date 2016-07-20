-- Table: sinarh.sin_anx_original_historico

-- DROP TABLE sinarh.sin_anx_original_historico;


CREATE TABLE sinarh.sin_anx_original_historico
(
  id_anx_original_historico bigserial NOT NULL,
  fila_id bigint,
  planilla_unique_id bigint,
  ani_aniopre integer,
  nen_codigo integer,
  ent_codigo integer,
  tip_codigo integer,
  pro_codigo integer,
  sub_codigo integer,
  pry_codigo integer,
  obj_codigo integer,
  fue_codigo integer,
  fin_codigo integer,
  vrs_codigo integer,
  cat_grupo integer,
  ctg_codigo character varying(20),
  mdf_codigo character varying(20),
  anx_tiporeg character varying(10),
  anx_mesaplic integer,
  anx_durac integer,
  anx_vlran bigint,
  anx_cargos integer,
  anx_descrip character varying(200),
  pai_codigo integer,
  dpt_codigo integer,
  anx_justifica character varying(100),
  anx_fching timestamp without time zone,
  anx_usring character varying(50),
  anx_fchact timestamp without time zone,
  anx_usract character varying(50),
  cant_disponible integer,
  anx_linea character varying,
  fecha_descarga timestamp without time zone,
  CONSTRAINT sin_anx_original_historico_pk PRIMARY KEY (id_anx_original_historico)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sinarh.sin_anx_original_historico
  OWNER TO postgres;

-- Trigger: audit_sinarh_sin_anx_original_historico on sinarh.sin_anx_original_historico

-- DROP TRIGGER audit_sinarh_sin_anx_original_historico ON sinarh.sin_anx_original_historico;

CREATE TRIGGER audit_sinarh_sin_anx_original_historico
  BEFORE INSERT OR UPDATE OR DELETE
  ON sinarh.sin_anx_original_historico
  FOR EACH ROW
  EXECUTE PROCEDURE audit.if_modified_func();






-- Table: sinarh.sin_anx_original_tmp

-- DROP TABLE sinarh.sin_anx_original_tmp;

CREATE TABLE sinarh.sin_anx_original_tmp
(
  id_anx_original_tmp bigserial NOT NULL,
  fila_id bigint,
  planilla_unique_id bigint,
  ani_aniopre integer,
  nen_codigo integer,
  ent_codigo integer,
  tip_codigo integer,
  pro_codigo integer,
  sub_codigo integer,
  pry_codigo integer,
  obj_codigo integer,
  fue_codigo integer,
  fin_codigo integer,
  vrs_codigo integer,
  cat_grupo integer,
  ctg_codigo character varying(20),
  mdf_codigo character varying(20),
  anx_tiporeg character varying(10),
  anx_mesaplic integer NOT NULL,
  anx_durac integer NOT NULL,
  anx_vlran bigint NOT NULL,
  anx_cargos integer NOT NULL,
  anx_descrip character varying(200),
  pai_codigo integer NOT NULL,
  dpt_codigo integer NOT NULL,
  anx_justifica character varying(100),
  anx_fching timestamp without time zone,
  anx_usring character varying(50),
  anx_fchact timestamp without time zone,
  anx_usract character varying(50),
  cant_disponible integer,
  anx_linea character varying,
  fecha_descarga timestamp without time zone,
  CONSTRAINT sin_anx_original_tmp_pk PRIMARY KEY (id_anx_original_tmp)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sinarh.sin_anx_original_tmp
  OWNER TO postgres;

-- Trigger: audit_sinarh_sin_anx_original_tmp on sinarh.sin_anx_original_tmp

-- DROP TRIGGER audit_sinarh_sin_anx_original_tmp ON sinarh.sin_anx_original_tmp;

CREATE TRIGGER audit_sinarh_sin_anx_original_tmp
  BEFORE INSERT OR UPDATE OR DELETE
  ON sinarh.sin_anx_original_tmp
  FOR EACH ROW
  EXECUTE PROCEDURE audit.if_modified_func();




ALTER TABLE sinarh.sin_anx_original
  ADD COLUMN fecha_descarga timestamp without time zone;


  