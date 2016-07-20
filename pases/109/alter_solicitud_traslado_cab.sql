--TABLA MOVILIDAD.SOLICITUD_TRASLADO_CAB, AGREGADAS COLUMNAS DE ID_PLANTA_CARGO_DET, VALOR_OBJ, CATEGORIA y MONTO

ALTER TABLE movilidad.solicitud_traslado_cab ADD COLUMN id_planta_cargo_det bigint;
ALTER TABLE movilidad.solicitud_traslado_cab ADD COLUMN cod_obj int;
ALTER TABLE movilidad.solicitud_traslado_cab ADD COLUMN valor_obj character varying(200);
ALTER TABLE movilidad.solicitud_traslado_cab ADD COLUMN cod_categ character varying(10);
ALTER TABLE movilidad.solicitud_traslado_cab ADD COLUMN categoria character varying(10);
ALTER TABLE movilidad.solicitud_traslado_cab ADD COLUMN monto bigint;
ALTER TABLE movilidad.solicitud_traslado_cab ADD COLUMN linea character varying(15);
ALTER TABLE movilidad.solicitud_traslado_cab ADD COLUMN descripcion character varying(50);