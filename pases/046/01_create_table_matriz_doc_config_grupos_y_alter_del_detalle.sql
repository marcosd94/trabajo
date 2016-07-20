--Nueva Tabla para Agrupar Documenos
CREATE TABLE seleccion.matriz_doc_config_grupos (
                id_matriz_doc_config_grupos bigserial NOT NULL,
                descripcion character varying(100) NOT NULL,
                nro_orden INTEGER NOT NULL,
                obligatorio BOOLEAN NOT NULL,
                evaluacion_doc BOOLEAN NOT NULL,
                adjudicacion BOOLEAN NOT NULL,
                id_matriz_doc_config_enc BIGINT NOT NULL,
				activo BOOLEAN NOT NULL,
				usu_alta character varying(50) NOT NULL,
				fecha_alta timestamp without time zone NOT NULL,
				usu_mod character varying(50),
				fecha_mod timestamp without time zone,
                CONSTRAINT id_matriz_doc_config_grupos PRIMARY KEY (id_matriz_doc_config_grupos)
);

--FK al Encabezado
ALTER TABLE seleccion.matriz_doc_config_grupos ADD CONSTRAINT matriz_doc_config_enc_matriz_doc_config_grupo_docs_fk
FOREIGN KEY (id_matriz_doc_config_enc)
REFERENCES seleccion.matriz_doc_config_enc (id_matriz_doc_config_enc)
ON DELETE NO ACTION
ON UPDATE NO ACTION;


--Alters del Detalle
ALTER TABLE seleccion.matriz_doc_config_det ALTER COLUMN obligatorio DROP NOT NULL;
ALTER TABLE seleccion.matriz_doc_config_det ALTER COLUMN evaluacion_doc DROP NOT NULL;
ALTER TABLE seleccion.matriz_doc_config_det ALTER COLUMN adjudicacion DROP NOT NULL;
ALTER TABLE seleccion.matriz_doc_config_det ADD COLUMN id_matriz_doc_config_grupos bigint;
ALTER TABLE seleccion.matriz_doc_config_det ADD COLUMN agrupado boolean DEFAULT false;

ALTER TABLE seleccion.matriz_doc_config_det ADD CONSTRAINT matriz_doc_config_grupo_docs_matriz_doc_config_det_fk
FOREIGN KEY (id_matriz_doc_config_grupos)
REFERENCES seleccion.matriz_doc_config_grupos (id_matriz_doc_config_grupos)
ON DELETE NO ACTION
ON UPDATE NO ACTION;


