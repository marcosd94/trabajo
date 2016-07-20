--No ejectuar. Un compendio se creó como 037
/*
CREATE OR REPLACE FUNCTION eval_desempeno.fnc_plan_mejora_pdec(id_plantilla_eval_pdec_cab bigint)
  RETURNS text AS
$BODY$
        DECLARE
            resultado TEXT;
BEGIN
    SELECT
        array_to_string(array_agg(
            plan.descripcion), '. ')
    INTO
        resultado
    FROM
       eval_desempeno.plan_mejora plan
	join eval_desempeno.resultado_eval_plan r
	on r.id_plan_mejora = plan.id_plan_mejora
	join eval_desempeno.plantilla_eval_pdec_cab cab
	on cab.id_plantilla_eval_pdec_cab = r.id_plantilla_eval_pdec_cab
    WHERE
	r.id_plantilla_eval_pdec_cab = id_plantilla_eval_pdec_cab
        and r.activo = true ;
RETURN resultado;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION eval_desempeno.fnc_plan_mejora_pdec(bigint) OWNER TO postgres;
*/

-- sirve para traer los planes de mejora en el reporte PDEC parte final.