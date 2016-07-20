--CREACION DE SECUENCIADOR EN SINARH.SIN_OBJ

-- Sequence: sinarh.sin_obj_id_obj_seq
-- DROP SEQUENCE sinarh.sin_obj_id_obj_seq;

CREATE SEQUENCE sinarh.sin_obj_id_obj_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 446
  CACHE 1;
ALTER TABLE sinarh.sin_obj_id_obj_seq
  OWNER TO postgres;
