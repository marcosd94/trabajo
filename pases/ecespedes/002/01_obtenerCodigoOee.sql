-- Function: planificacion.obtenerCodigoOee(bigint)

-- DROP FUNCTION planificacion.obtenerCodigoOee(bigint);

CREATE OR REPLACE FUNCTION planificacion.obtenerCodigoOee(par_idConfiguracionUoDet bigint)
  RETURNS character varying AS
$BODY$
DECLARE
    var_configuracion_uo_det planificacion.configuracion_uo_det%ROWTYPE;
    var_configuracion_uo_cab planificacion.configuracion_uo_cab%ROWTYPE;
    var_entidad planificacion.entidad%ROWTYPE;
    var_retorno character varying = '';
   

BEGIN
	
	--SE RECUPERA EL REGISTRO DE CONFIGURACION_UO_DET

    SELECT * INTO var_configuracion_uo_det FROM  planificacion.configuracion_uo_det
    				where id_configuracion_uo_det = par_idConfiguracionUoDet;

          
	--SE CARGA LA VARIABLE DE RETORNO EN SENTIDO INVERSO HASTA QUE QUE EL 
	--ID DEL PADRE SEA NULL 

   	WHILE var_configuracion_uo_det.id_configuracion_uo_det_padre is not null		 LOOP
    		

    		if (select substring (var_retorno,1) = '.')then
	    
	    		var_retorno := var_configuracion_uo_det.orden || var_retorno;
	    	else
			if (select length (var_configuracion_uo_det.orden::text)) < 2 then
				var_retorno := '.0' || var_configuracion_uo_det.orden || var_retorno;
			else
				var_retorno := '.' || var_configuracion_uo_det.orden || var_retorno;
			end if;
	    	end if;
	    	
	    	SELECT * INTO var_configuracion_uo_det FROM  planificacion.configuracion_uo_det
	    				where id_configuracion_uo_det = var_configuracion_uo_det.id_configuracion_uo_det_padre;	
	    	
	END LOOP;

	--AGREGA EL DEL ULTIMO PADRE QUE ENCONTRO
	if (select substring (var_retorno,1) = '.')then
    
		var_retorno := var_configuracion_uo_det.orden || var_retorno;
	else
		if (select length (var_configuracion_uo_det.orden::text)) < 2 then
			var_retorno := '.0' || var_configuracion_uo_det.orden || var_retorno;
		else
			var_retorno := '.' || var_configuracion_uo_det.orden || var_retorno;
		end if;
	end if;

	--AGREGA EL ORDEN DE CONFIGURACION_UO_CAB
	SELECT * INTO var_configuracion_uo_cab FROM  planificacion.configuracion_uo_cab
				where id_configuracion_uo = var_configuracion_uo_det.id_configuracion_uo;
	
	
	if (select substring (var_retorno,1) = '.')then
    
		var_retorno := var_configuracion_uo_cab.orden || var_retorno;
	else
		if (select length (var_configuracion_uo_cab.orden::text)) < 2 then
			var_retorno := '.0' || var_configuracion_uo_cab.orden || var_retorno;
		else
			var_retorno := '.' || var_configuracion_uo_cab.orden || var_retorno;
		end if;
	end if;

	--AGREGA EL NEN_CODIGO Y ENT_CODIGO DE LA TABLA ENTIDAD
	SELECT * INTO var_entidad FROM  planificacion.entidad
				where id_entidad = var_configuracion_uo_cab.id_entidad;

	if(select length(var_entidad.ent_codigo::text)) < 2 then
		var_retorno := '.0' || var_entidad.ent_codigo || var_retorno;
	else
		var_retorno := '.' || var_entidad.ent_codigo || var_retorno;
	end if;


	if(select length(var_entidad.nen_codigo::text)) < 2 then
		var_retorno := '0' || var_entidad.nen_codigo || var_retorno;
	else
		var_retorno := var_entidad.nen_codigo || var_retorno;
	end if;
	
	
RETURN var_retorno;

EXCEPTION
    WHEN data_exception THEN
        RAISE WARNING 'ERROR AL OBTENER EL CODIGO DE LA OEE - SQLERRM: %',SQLERRM;
        RETURN 'error';
    
   
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION planificacion.obtenercodigoOee(bigint)
  OWNER TO postgres;



--select planificacion.obtenercodigooee(175)







