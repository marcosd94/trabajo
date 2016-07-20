-- View: seleccion.eval_abiertas

-- DROP VIEW seleccion.eval_abiertas;

CREATE OR REPLACE VIEW seleccion.eval_abiertas AS 
         SELECT eval_documental_cab.id_postulacion AS id_eval_abiertas, eval_documental_cab.id_postulacion, eval_referencial_tipoeval.id_eval_referencial_tipoeval, eval_referencial.id_eval_referencial, eval_documental_cab.id_eval_documental_cab, datos_especificos.id_datos_especificos, eval_documental_cab.id_concurso_puesto_agr
           FROM seleccion.eval_documental_cab
      LEFT JOIN seleccion.eval_referencial ON eval_documental_cab.id_postulacion = eval_referencial.id_postulacion
   LEFT JOIN seleccion.eval_referencial_tipoeval ON eval_referencial.id_eval_referencial_tipoeval = eval_referencial_tipoeval.id_eval_referencial_tipoeval
   LEFT JOIN seleccion.datos_especificos ON eval_referencial_tipoeval.id_datos_especificos_tipo_eval = datos_especificos.id_datos_especificos
   JOIN seleccion.concurso_puesto_agr ON eval_documental_cab.id_concurso_puesto_agr = concurso_puesto_agr.id_concurso_puesto_agr
   JOIN seleccion.concurso ON concurso_puesto_agr.id_concurso = concurso.id_concurso
   JOIN seleccion.datos_especificos de2 ON concurso.id_datos_esp_tipo_conc = de2.id_datos_especificos
  WHERE eval_documental_cab.aprobado IS TRUE AND eval_documental_cab.fecha_evaluacion IS NOT NULL AND eval_documental_cab.tipo::text = 'EVALUACION DOCUMENTAL'::text AND (de2.valor_alf::text = ANY (ARRAY['CPO'::character varying::text, 'CME'::character varying::text, 'CII'::character varying::text, 'CIR'::character varying::text, 'CIPS'::character varying::text]))
UNION 
         SELECT postulacion.id_postulacion AS id_eval_abiertas, postulacion.id_postulacion, eval_referencial_tipoeval.id_eval_referencial_tipoeval, eval_referencial.id_eval_referencial, 0 AS id_eval_documental_cab, datos_especificos.id_datos_especificos, postulacion.id_concurso_puesto_agr
           FROM seleccion.postulacion
      LEFT JOIN seleccion.eval_referencial ON postulacion.id_postulacion = eval_referencial.id_postulacion
   LEFT JOIN seleccion.eval_referencial_tipoeval ON eval_referencial.id_eval_referencial_tipoeval = eval_referencial_tipoeval.id_eval_referencial_tipoeval
   LEFT JOIN seleccion.datos_especificos ON eval_referencial_tipoeval.id_datos_especificos_tipo_eval = datos_especificos.id_datos_especificos
   JOIN seleccion.concurso_puesto_agr ON postulacion.id_concurso_puesto_agr = concurso_puesto_agr.id_concurso_puesto_agr
   JOIN seleccion.concurso ON concurso_puesto_agr.id_concurso = concurso.id_concurso
   JOIN seleccion.datos_especificos de2 ON concurso.id_datos_esp_tipo_conc = de2.id_datos_especificos
  WHERE postulacion.activo IS TRUE AND (postulacion.seleccionado IS TRUE OR postulacion.seleccionado IS NULL) AND de2.valor_alf::text = 'CSI'::text
  ORDER BY 2, 3;

ALTER TABLE seleccion.eval_abiertas
  OWNER TO postgres;
