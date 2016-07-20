CREATE OR REPLACE FUNCTION seleccion.obtener_replica_eval_documental(idEvalDocumentalCab bigint)
  RETURNS bigint AS
$BODY$
DECLARE
    original RECORD;
    detalle RECORD;
    id_replica bigint;
    
BEGIN

    id_replica := nextVal('seleccion.eval_documental_cab_id_eval_documental_seq');

    FOR original IN select * from seleccion.eval_documental_cab where tipo = 'EVALUACION DOCUMENTAL' and id_eval_documental_cab = idEvalDocumentalCab
    
	LOOP
	       INSERT INTO seleccion.eval_documental_cab 
		values (
			id_replica,
			original.tipo,
			original.aprobado,
			original.evaluado,
			original.fecha_evaluacion,
			original.observacion,
			original.id_postulacion,
			original.activo,
			'SYSTEM',
			(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
			null,
			null,
			original.id_concurso_puesto_agr,
			null,
			null);
	  END LOOP;

	FOR detalle IN select * from seleccion.eval_documental_det where id_eval_documental_cab = idEvalDocumentalCab
    
	LOOP
	       INSERT INTO seleccion.eval_documental_det 
		values (
			nextVal('seleccion.eval_documental_det_id_eval_documental_det_seq'),
			detalle.aprobado_con_documentos,
			detalle.aprobado_por_comision,
			id_replica,
			detalle.id_matriz_doc_config_det,
			detalle.activo,
			'SYSTEM',
			(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
			null,
			null
		);
	  END LOOP;

 --commit;
 RETURN id_replica;

EXCEPTION
    WHEN data_exception THEN
        RAISE WARNING 'ERROR AL REPLICAR EVALUACION - SQLERRM: %',SQLERRM;
        RETURN NULL;
    
   
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION seleccion.obtener_replica_eval_documental(bigint) OWNER TO postgres;
