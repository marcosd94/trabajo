-- View: capacitacion.ver_cursos_evaluacion_postulantes

-- DROP VIEW capacitacion.ver_cursos_evaluacion_postulantes;

CREATE OR REPLACE VIEW capacitacion.ver_cursos_evaluacion_postulantes AS 
         SELECT c.nombre, de.descripcion, 
                CASE
                    WHEN c.modalidad::text = 'P'::text THEN 'PRESENCIAL'::text
                    WHEN c.modalidad::text = 'S'::text THEN 'SEMIPRESENCIAL'::text
                    WHEN c.modalidad::text = 'V'::text THEN 'VIRTUAL'::text
                    ELSE NULL::text
                END AS modalidad, (date(c.fecha_inicio) || ' - '::text) || date(c.fecha_fin) AS fecha, 'Finalizado' AS estado, p.id_persona,  c.carga_horaria AS carga_horaria, c.credito AS creditos
           FROM capacitacion.capacitaciones c
      JOIN seleccion.datos_especificos de ON de.id_datos_especificos = c.id_datos_especificos_tipo_cap
   JOIN capacitacion.postulacion_cap p ON p.id_capacitacion = c.id_capacitacion
   LEFT JOIN capacitacion.evaluacion_part e ON e.id_postulacion = p.id_postulacion
   JOIN seleccion.referencias r ON r.valor_num = c.estado
  WHERE p.activo = true AND r.dominio::text = 'ESTADOS_CAPACITACION'::text AND r.desc_abrev::text = 'FINALIZADA'::text
UNION 
         SELECT c.nombre, de.descripcion, 
                CASE
                    WHEN c.modalidad::text = 'P'::text THEN 'PRESENCIAL'::text
                    WHEN c.modalidad::text = 'S'::text THEN 'SEMIPRESENCIAL'::text
                    WHEN c.modalidad::text = 'V'::text THEN 'VIRTUAL'::text
                    ELSE NULL::text
                END AS modalidad, (date(c.fecha_inicio) || ' - '::text) || date(c.fecha_fin) AS fecha, 'Inscripto' AS estado, p.id_persona, c.carga_horaria AS carga_horaria, c.credito AS creditos
           FROM capacitacion.capacitaciones c
      JOIN seleccion.datos_especificos de ON de.id_datos_especificos = c.id_datos_especificos_tipo_cap
   JOIN capacitacion.postulacion_cap p ON p.id_capacitacion = c.id_capacitacion
   JOIN capacitacion.evaluacion_part e ON e.id_postulacion = p.id_postulacion
  WHERE p.activo = true;

ALTER TABLE capacitacion.ver_cursos_evaluacion_postulantes
  OWNER TO postgres;