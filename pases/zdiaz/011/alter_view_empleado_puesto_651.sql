DROP VIEW general.empleado_puesto_651;
CREATE OR REPLACE VIEW general.empleado_puesto_651 AS 
SELECT configuracion_uo_cab.denominacion_unidad AS oee, configuracion_uo_det.denominacion_unidad AS uo, planta_cargo_det.descripcion AS puesto, empleado_puesto.fecha_inicio, empleado_puesto.fecha_fin, datos_especificos.descripcion AS estado, empleado_puesto.id_persona, empleado_puesto.id_empleado_puesto AS id, t.descripcion::text AS tipo
FROM general.empleado_puesto
INNER JOIN planificacion.planta_cargo_det ON general.empleado_puesto.id_planta_cargo_det = planificacion.planta_cargo_det.id_planta_cargo_det
INNER JOIN planificacion.configuracion_uo_det ON planificacion.planta_cargo_det.id_configuracion_uo_det = planificacion.configuracion_uo_det.id_configuracion_uo_det
INNER JOIN planificacion.configuracion_uo_cab ON planificacion.configuracion_uo_det.id_configuracion_uo = planificacion.configuracion_uo_cab.id_configuracion_uo
INNER JOIN seleccion.datos_especificos ON general.empleado_puesto.id_datos_esp_estado = seleccion.datos_especificos.id_datos_especificos
LEFT JOIN seleccion.datos_especificos t ON empleado_puesto.id_datos_esp_tipo_ingreso_movilidad = t.id_datos_especificos
UNION
SELECT configuracion_uo_cab.denominacion_unidad AS oee, configuracion_uo_det.denominacion_unidad AS uo, planta_cargo_det.descripcion AS puesto, empleado_puesto.fecha_inicio, empleado_puesto.fecha_fin, datos_especificos.descripcion AS estado, empleado_puesto.id_persona, empleado_puesto.id_empleado_puesto AS id, t.descripcion AS tipo
FROM general.empleado_puesto
JOIN planificacion.planta_cargo_det ON empleado_puesto.id_planta_cargo_det = planta_cargo_det.id_planta_cargo_det
JOIN planificacion.configuracion_uo_det ON planta_cargo_det.id_configuracion_uo_det = configuracion_uo_det.id_configuracion_uo_det
JOIN planificacion.configuracion_uo_cab ON configuracion_uo_det.id_configuracion_uo = configuracion_uo_cab.id_configuracion_uo
JOIN seleccion.datos_especificos ON empleado_puesto.id_datos_esp_estado = datos_especificos.id_datos_especificos
LEFT JOIN seleccion.datos_especificos t ON empleado_puesto.id_datos_esp_tipo_ingreso_movilidad = t.id_datos_especificos
WHERE empleado_puesto.activo = true
UNION 
SELECT empleado_puesto.desc_oee_historico AS oee, empleado_puesto.desc_uo_historico AS uo, empleado_puesto.desc_puesto_historico AS puesto, empleado_puesto.fecha_inicio, empleado_puesto.fecha_fin, 
datos_especificos.descripcion AS estado, empleado_puesto.id_persona, empleado_puesto.id_empleado_puesto AS id, t.descripcion AS tipo
FROM general.empleado_puesto
LEFT JOIN seleccion.datos_especificos ON empleado_puesto.id_datos_esp_estado = datos_especificos.id_datos_especificos
LEFT JOIN seleccion.datos_especificos t ON empleado_puesto.id_datos_esp_tipo_ingreso_movilidad = t.id_datos_especificos
WHERE empleado_puesto.id_planta_cargo_det IS NULL AND empleado_puesto.activo = true
ORDER BY 4;

ALTER TABLE general.empleado_puesto_651
  OWNER TO postgres;
