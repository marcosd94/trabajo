CREATE OR REPLACE FUNCTION seleccion.recalcularListaCorta(par_IdConsursoPuestoAgr bigint)
  RETURNS character varying  AS
$BODY$
DECLARE
    var_datos_grupo_puesto seleccion.datos_grupo_puesto%ROWTYPE;
    var_eval_ref_pos RECORD;
    var_cantidad_puestos bigint;
    var_iterador bigint;
BEGIN

    SELECT * INTO var_datos_grupo_puesto FROM seleccion.datos_grupo_puesto where id_concurso_puesto_agr = par_IdConsursoPuestoAgr;

    SELECT cantidad_puestos INTO var_cantidad_puestos FROM seleccion.concurso_puesto_agr WHERE id_concurso_puesto_agr = par_IdConsursoPuestoAgr;

    var_iterador := 0;
	
	--SE RECORRE LA LISTA DE EVAL_REFERENCIAL_POSTULANTE DEL CONCURSO, 
	--Y SE RECUPERAN TODOS LOS REGISTROS 
	--ORDENADOS DE FORMA DESCENDENTE POR PUNTAJE.

	FOR var_eval_ref_pos IN SELECT * FROM seleccion.eval_referencial_postulante eval_ref_pos WHERE eval_ref_pos.id_postulacion IN 
				(SELECT id_postulacion FROM seleccion.postulacion WHERE id_concurso_puesto_agr = par_IdConsursoPuestoAgr)
				--AND eval_ref_pos.puntaje_realizado >= var_datos_grupo_puesto.porc_minimo
				order by puntaje_realizado desc

    LOOP

	
	
	--INICIALMENTE SE ACTULIZAN EN NULL LOS CAMPOS DE LISTA_CORTA_REPLICA Y SELECCIONADO_REPLICA
	UPDATE seleccion.eval_referencial_postulante
			SET seleccionado_replica = null, lista_corta_replica = null
		       	WHERE id_eval_referencial_postulante = var_eval_ref_pos.id_eval_referencial_postulante;


    END LOOP;



    FOR var_eval_ref_pos IN SELECT * FROM seleccion.eval_referencial_postulante eval_ref_pos WHERE eval_ref_pos.id_postulacion IN 
				(SELECT id_postulacion FROM seleccion.postulacion WHERE id_concurso_puesto_agr = par_IdConsursoPuestoAgr)
				AND eval_ref_pos.puntaje_realizado >= var_datos_grupo_puesto.porc_minimo
				AND eval_ref_pos.fecha_alta =(select max(fecha_alta) from seleccion.eval_referencial_postulante where id_postulacion = eval_ref_pos.id_postulacion)
				order by puntaje_realizado desc

    LOOP

	
	
	
	--PARA TERNA EN LA LISTA CORTA SE ACTUALIZA SOLO EL CAMPO LISTA_CORTA_REPLICA
	--DEPENDIENDO DE LA CANTIDAD DE PUESTOS
	
	var_iterador := var_iterador +1;
	IF var_datos_grupo_puesto.terna is TRUE THEN
		
		IF var_iterador <= var_cantidad_puestos + 2 THEN 
			UPDATE seleccion.eval_referencial_postulante
			SET lista_corta_replica= TRUE
		       	WHERE id_eval_referencial_postulante = var_eval_ref_pos.id_eval_referencial_postulante;

			
		END IF;
	END IF;
	-- PARA MERITO EN LISTA CORTA SE ACTUALIZA LA COLUMNA DE SELECCIONADO_REPLICA
	--DEPENDIENDO DE LA CANTIDAD DE PUESTOS.
	IF var_datos_grupo_puesto.merito is TRUE THEN
		
		IF var_iterador <= var_cantidad_puestos THEN 
			UPDATE seleccion.eval_referencial_postulante
			SET seleccionado_replica = TRUE
		       	WHERE id_eval_referencial_postulante = var_eval_ref_pos.id_eval_referencial_postulante;
		END IF;
		
		IF var_iterador > var_cantidad_puestos THEN 
			UPDATE seleccion.eval_referencial_postulante
			SET seleccionado_replica = FALSE
		       	WHERE id_eval_referencial_postulante = var_eval_ref_pos.id_eval_referencial_postulante;
			
		END IF;

	END IF;
		
    END LOOP;

RETURN 'ok';

EXCEPTION
    WHEN data_exception THEN
        RAISE WARNING 'ERROR AL RECALCULAR LISTA CORTA - SQLERRM: %',SQLERRM;
        RETURN 'error';
    
   
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

ALTER FUNCTION seleccion.recalcularListaCorta(bigint) OWNER TO postgres;
