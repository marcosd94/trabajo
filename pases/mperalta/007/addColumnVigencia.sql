ALTER TABLE planificacion.configuracion_uo_cab ADD COLUMN vigencia_desde_remuneraciones date;
UPDATE planificacion.configuracion_uo_cab SET vigencia_desde_remuneraciones= '2016-01-01';