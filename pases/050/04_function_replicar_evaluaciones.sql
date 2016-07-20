--recuperar los tipos de evaluaciones en seleccion.eval_referecial_tipoeval
--consultar seleccion.eval_referencial para verificar si el postulante posee registros de evaluaciones, sino, deberá insertar nuevos.
--consultar seleccion.eval_referencial_postulante para verificar si el postulante posee registros finales, sino, deberá insertar nuevos.



CREATE OR REPLACE FUNCTION seleccion.replicar_evaluaciones(idPostulacion bigint)
  RETURNS character varying AS
$BODY$
DECLARE
	eval_referencial_tipoeval RECORD;
	original_eval_referencial seleccion.eval_referencial%ROWTYPE;
	original_eval_referencial_cab RECORD;
	original_eval_referencial_det RECORD;
	original_eval_referencial_comis RECORD;
	original_eval_referencial_postulante seleccion.eval_referencial_postulante%ROWTYPE;
	Postulacion seleccion.postulacion%ROWTYPE; 
	id_replica_eval_referencial bigint;
	id_replica_eval_referencial_cab bigint;
	id_replica_eval_referencial_det bigint;
	id_replica_eval_referencial_postulante bigint;
	id_replica_eval_referencial_comis bigint;
	comision_sel_det RECORD;
	MatrizRefConf seleccion.matriz_ref_conf%ROWTYPE;
	matriz_ref_conf_enc RECORD;
	matriz_ref_conf_det RECORD;
	
	retorno character varying(50);
	
	
BEGIN

	select * into postulacion from seleccion.postulacion where id_postulacion = idPostulacion;
	RAISE NOTICE 'postulacion: %', idPostulacion;
	FOR eval_referencial_tipoeval IN select * 
		from seleccion.eval_referencial_tipoeval where id_concurso_puesto_agr = postulacion.id_concurso_puesto_agr 
			
	LOOP
		--POR CADA TIPO DE EVALUACION SE RECUPERA EL EVAL_REFERENCIAL DEL POSTULANTE,
		--EN CASO DE NO TENER REGISTRO, SE DEBERA INSERTAR UNO NUEVO
			
		SELECT  * INTO original_eval_referencial
			FROM seleccion.eval_referencial WHERE id_eval_referencial_tipoeval = eval_referencial_tipoeval.id_eval_referencial_tipoeval 
							AND id_postulacion = postulacion.id_postulacion;
			
	
		
		-- SI YA CUENTA CON EVAL_REFERENCIAL EL POSTULANTE EL REGISTRO SERA REPLICADO 
						
		IF original_eval_referencial.id_eval_referencial IS NOT NULL THEN
				id_replica_eval_referencial := nextVal('seleccion.eval_referencial_id_eval_referencial_seq');
				INSERT INTO seleccion.eval_referencial 
				values (
					id_replica_eval_referencial,
					original_eval_referencial.nro_orden,
					original_eval_referencial.id_eval_referencial_tipoeval,
					original_eval_referencial.id_postulacion,
					original_eval_referencial.observacion,
					original_eval_referencial.presente,
					original_eval_referencial.aprobado,
					original_eval_referencial.puntaje_realizado,
					original_eval_referencial.porc_realizado,
					original_eval_referencial.fecha_evaluacion,
					true,
					'SYSTEM',
					(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
					null,
					null,
					false);

				FOR original_eval_referencial_comis in SELECT * FROM seleccion.eval_referencial_comis 
							where id_eval_referencial = original_eval_referencial.id_eval_referencial

				LOOP
					id_replica_eval_referencial_comis := nextVal('seleccion.eval_referencial_comis_id_eval_referencial_comis_seq');

					INSERT INTO seleccion.eval_referencial_comis 
					values (
						id_replica_eval_referencial_comis,
						original_eval_referencial_comis.id_sel_empresa_persona,
						original_eval_referencial_comis.id_comision_sel_det,
						id_replica_eval_referencial,
						true,
						'SYSTEM',
						(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
						null,
						null);

				END LOOP;

				-- TAMBIEN SE RECUPERARAN LAS CABECERAS ASOCIADAS A ESE REGISTRO PARA REPLICARLAS
				FOR original_eval_referencial_cab IN select * from seleccion.eval_referencial_cab 
								where id_eval_referencial = original_eval_referencial.id_eval_referencial
				LOOP
				
					
					IF original_eval_referencial_cab.id_eval_referencial_cab IS NOT NULL THEN 
						id_replica_eval_referencial_cab := nextVal('seleccion.eval_referencial_cab_id_eval_referencial_cab_seq');
						INSERT INTO seleccion.eval_referencial_cab
						values (
							id_replica_eval_referencial_cab,
							original_eval_referencial_cab.id_matriz_ref_conf_enc,
							id_replica_eval_referencial,
							original_eval_referencial_cab.puntaje_realizado,
							true,
							'SYSTEM',
							(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
							null,
							null);

							-- Y POR ULTIMO DEBERÁN SER REPLICADAS CADA UNA DE LOS DETALLES DE LAS CABECERAS
							FOR original_eval_referencial_det IN select * from seleccion.eval_referencial_det 
									where id_eval_referencial_cab = original_eval_referencial_cab.id_eval_referencial_cab
							LOOP
								id_replica_eval_referencial_det := nextVal('seleccion.eval_referencial_det_id_eval_referencial_det_seq');
								RAISE NOTICE 'ID Eval Referencial Det : %', original_eval_referencial_det.id_eval_referencial_det;
								INSERT INTO seleccion.eval_referencial_det
								values (
									id_replica_eval_referencial_det,
									original_eval_referencial_det.id_matriz_ref_conf_det,
									id_replica_eval_referencial_cab,
									original_eval_referencial_det.puntaje_realizado,
									true,
									'SYSTEM',
									(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
									null,
									null);
								


							END LOOP;


					 ELSE
						--SI NO TIENE REGISTRO DE CABECERA DEBERA INSERTARSE UNO NUEVO
						RAISE NOTICE 'No Encontro Eval Referencial Cab ';
					 END IF;

				END LOOP;
			ELSE		
			-- EN CASO DE QUE NO POSEA REGISTRO DE EVAL REFERENCIAL DEBERÁ GENERARSE UNO... 
				
				id_replica_eval_referencial := nextVal('seleccion.eval_referencial_id_eval_referencial_seq');
				
				INSERT INTO seleccion.eval_referencial 
				values (
					id_replica_eval_referencial,
					0,--original_eval_referencial.nro_orden,
					eval_referencial_tipoeval.id_eval_referencial_tipoeval,--original_eval_referencial.id_eval_referencial_tipoeval,
					idPostulacion,
					' ',--original_eval_referencial.observacion,
					false,--original_eval_referencial.presente,
					null,--original_eval_referencial.aprobado,
					0,--original_eval_referencial.puntaje_realizado,
					0,--original_eval_referencial.porc_realizado,
					(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),--original_eval_referencial.fecha_evaluacion,
					true,
					'SYSTEM',
					(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
					null,
					null,
					false);


				FOR comision_sel_det 
				in select distinct id_comision_sel_det from seleccion.eval_referencial_comis comis where id_eval_referencial in 
					(select id_eval_referencial from seleccion.eval_referencial where id_postulacion in 
						(select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = Postulacion.id_concurso_puesto_agr)) 

				LOOP

					id_replica_eval_referencial_comis := nextVal('seleccion.eval_referencial_comis_id_eval_referencial_comis_seq');

					INSERT INTO seleccion.eval_referencial_comis
					values (
						id_replica_eval_referencial_comis,
						null,--original_eval_referencial_comis.id_sel_empresa_persona,
						comision_sel_det.id_comision_sel_det,--original_eval_referencial_comis.id_comision_sel_det,
						id_replica_eval_referencial,--original_eval_referencial_comis.id_eval_referencial,
						true,
						'SYSTEM',
						(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
						null,
						null);

				END LOOP;


			RAISE NOTICE 'Se Generó Tipo Evaluación: %', eval_referencial_tipoeval.id_eval_referencial_tipoeval ;

			-- POR CADA TIPO DE EVALUACION DEBERA GENERAR LAS CABECERAS Y DETALLE CONFORME LA MATRIZ DE EVALUACION

			SELECT * INTO MatrizRefConf FROM seleccion.matriz_ref_conf 
							WHERE TIPO = 'GRUPO' 
							AND id_concurso_puesto_agr = Postulacion.id_concurso_puesto_agr
							AND ACTIVO = TRUE;

			
			FOR matriz_ref_conf_enc IN SELECT * FROM seleccion.matriz_ref_conf_enc 
								where id_matriz_ref_conf = MatrizRefConf.id_matriz_ref_conf 
								and id_datos_especificos_tipo_eval = eval_referencial_tipoeval.id_datos_especificos_tipo_eval
								and activo = true

			LOOP


					--SE DEBERA INSERTAR UN CABECERA
					
					id_replica_eval_referencial_cab := nextVal('seleccion.eval_referencial_cab_id_eval_referencial_cab_seq');
					INSERT INTO seleccion.eval_referencial_cab
					values (
						id_replica_eval_referencial_cab,
						matriz_ref_conf_enc.id_matriz_ref_conf_enc,
						id_replica_eval_referencial,
						0,--original_eval_referencial_cab.puntaje_realizado,
						true,
						'SYSTEM',
						(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
						null,
						null);






					-- SE DEBERAN INSERTAR SUS CORRESPONDIENTES DETALLES
					FOR matriz_ref_conf_det IN SELECT * FROM seleccion.matriz_ref_conf_det 
							where id_matriz_ref_conf_enc = matriz_ref_conf_enc.id_matriz_ref_conf_enc
							and activo = true
					LOOP
						id_replica_eval_referencial_det := nextVal('seleccion.eval_referencial_det_id_eval_referencial_det_seq');
						
						INSERT INTO seleccion.eval_referencial_det
						values (
							id_replica_eval_referencial_det,
							matriz_ref_conf_det.id_matriz_ref_conf_det,
							id_replica_eval_referencial_cab,
							0,--original_eval_referencial_det.puntaje_realizado,
							true,
							'SYSTEM',
							(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
							null,
							null);



					END LOOP;
			END LOOP;
		
		END IF;
		
	END LOOP;

	-- SE BUSCA EL REGISRO DE LA TABLA EVAL_REFERENCIAL_POSTULANTE
	SELECT * INTO original_eval_referencial_postulante from seleccion.eval_referencial_postulante 
								WHERE id_postulacion = postulacion.id_postulacion;

	IF original_eval_referencial_postulante.id_eval_referencial_postulante IS NOT NULL THEN 

		id_replica_eval_referencial_postulante := nextVal('seleccion.eval_referencial_post_id_eval_ref_postulan_seq');
		RAISE NOTICE 'ID Eval Referencial Postulante : %', original_eval_referencial_postulante.id_eval_referencial_postulante;
		INSERT INTO seleccion.eval_referencial_postulante
		values (
		  id_replica_eval_referencial_postulante,
		  original_eval_referencial_postulante.puntaje_realizado,
		  original_eval_referencial_postulante.fecha_evaluacion,
		  original_eval_referencial_postulante.id_concurso_puesto_agr  ,
		  original_eval_referencial_postulante.id_postulacion ,
		  true,
		  'SYSTEM',
		  ( SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
	 	  null,
		  null,
		  original_eval_referencial_postulante.lista_corta,
		  original_eval_referencial_postulante.seleccionado,
		  original_eval_referencial_postulante.sel_adjudicado,
		  original_eval_referencial_postulante.obs_elegible,
		  original_eval_referencial_postulante.obs_empate,
		  original_eval_referencial_postulante.orden,
		  original_eval_referencial_postulante.presente,
		  original_eval_referencial_postulante.var,
		  original_eval_referencial_postulante.excluido, 
		  original_eval_referencial_postulante.obs_exclusion,  
		  original_eval_referencial_postulante.a_elegible,  
		  original_eval_referencial_postulante.cambiado,  
		  original_eval_referencial_postulante.id_excepcion,
		  original_eval_referencial_postulante.incluido,
		  original_eval_referencial_postulante.lista_corta_replica,
		  original_eval_referencial_postulante.seleccionado_replica
		  
		  );
	ELSE
	-- SINO POSEE REGISTRO DEBERÁ GENERARSE UNO.
		
		id_replica_eval_referencial_postulante := nextVal('seleccion.eval_referencial_post_id_eval_ref_postulan_seq');
		
		INSERT INTO seleccion.eval_referencial_postulante
		values (
		  id_replica_eval_referencial_postulante,
		  0,--original_eval_referencial_postulante.puntaje_realizado,
		  ( SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),--original_eval_referencial_postulante.fecha_evaluacion,
		  postulacion.id_concurso_puesto_agr,--original_eval_referencial_postulante.id_concurso_puesto_agr  ,
		  idPostulacion,--original_eval_referencial_postulante.id_postulacion ,
		  true,
		  'SYSTEM',
		  ( SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),
	 	  null,
		  null,
		  null,--original_eval_referencial_postulante.lista_corta,
		  null,--original_eval_referencial_postulante.seleccionado,
		  null,--original_eval_referencial_postulante.sel_adjudicado,
		  null,--original_eval_referencial_postulante.obs_elegible,
		  null,--original_eval_referencial_postulante.obs_empate,
		  null,--original_eval_referencial_postulante.orden,
		  null,--original_eval_referencial_postulante.presente,
		  null,--original_eval_referencial_postulante.var,
		  null,--original_eval_referencial_postulante.excluido, 
		  null,--original_eval_referencial_postulante.obs_exclusion,  
		  null,--original_eval_referencial_postulante.a_elegible,  
		  null,--original_eval_referencial_postulante.cambiado,  
		  null,--original_eval_referencial_postulante.id_excepcion,
		  null,--original_eval_referencial_postulante.incluido  
		  null,--lista_corta_replica
		  null--seleccionado_replica
		  );



		RAISE NOTICE 'Se Generó Eval Referencial Postulante';

	END IF;
	
	retorno := 'ok';

 RETURN retorno;

EXCEPTION
    WHEN data_exception THEN
        RAISE WARNING 'ERROR AL REPLICAR EVALUACION - SQLERRM: %',SQLERRM;
        RETURN 'error';
    
   
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION seleccion.replicar_evaluaciones(bigint) OWNER TO postgres;
