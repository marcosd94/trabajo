ALTER TABLE planificacion.niveles_de_cargos ADD COLUMN activo boolean NOT NULL;
ALTER TABLE planificacion.niveles_de_cargos ADD COLUMN usu_alta character varying(50);
ALTER TABLE planificacion.niveles_de_cargos ADD COLUMN fecha_alta timestamp without time zone;
ALTER TABLE planificacion.niveles_de_cargos ADD COLUMN usu_mod character varying(50);
ALTER TABLE planificacion.niveles_de_cargos ADD COLUMN fecha_mod timestamp without time zone;