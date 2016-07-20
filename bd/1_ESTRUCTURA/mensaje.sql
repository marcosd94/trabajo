CREATE TABLE general.mensaje
(
  id_mensaje bigint NOT NULL,
  valor_mensaje character varying(300),
  codigo_mensaje character varying(50) NOT NULL,
  CONSTRAINT pk_mensaje PRIMARY KEY (id_mensaje)
);
