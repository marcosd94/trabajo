-- Function: remuneracion.informe_cumplimiento(integer, integer)

-- DROP FUNCTION remuneracion.informe_cumplimiento(integer, integer);

CREATE OR REPLACE FUNCTION remuneracion.informe_cumplimiento(p_anho integer, p_mes integer)
  RETURNS void AS
$BODY$BEGIN

--Limpiar la tabla temporal
DELETE FROM remuneracion.informe_cumplimiento;

--Cargar todos los oee's
INSERT INTO remuneracion.informe_cumplimiento(nivel,nivel_desc,entidad,oee,denominacion_unidad,agrupamiento) (
		SELECT nivel,nivel_desc,
		entidad,
		oee,
		denominacion_unidad,
		case when nivel IN(11,12,13,14,15) THEN 'I - ADMINISTRACION CENTRAL'
		when nivel IN(21,22,23,24,25,27,28) THEN 'II - ADMINISTRACION DESCENTRALIZADA'
		when nivel IN(30) THEN 'III - MUNICIPALIDADES'
		when nivel IN(40) THEN 'IV - SOCIEDADES ANÓNIMAS'
		else '' end as agrupamiento
		FROM (	SELECT temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad, nivel_desc
		FROM (
		SELECT DISTINCT sne.nen_codigo as nivel, se.ent_codigo as entidad, cuc.orden as oee,cuc.denominacion_unidad, sne.nen_nombre as nivel_desc
		FROM sinarh.sin_nivel_entidad sne 
		INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo
		INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad
		INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo
		WHERE cuc.activo is true) AS temp1
		where 1=1
		GROUP BY temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad,nivel_desc
		order BY temp1.nivel,temp1.entidad,temp1.oee) tmp);

--Actualizar los campos 
UPDATE remuneracion.informe_cumplimiento ic 
set 
anho = rem.anho, 
mes = rem.mes,
documento_identidad = rem.documento_identidad,
anho_ingreso = rem.anho_ingreso,
vacancia = rem.vacancia,
comisionado = rem.comisionado,
"111" = rem.a111,
"112" = rem.a112, 
"113" = rem.a113, 
"114" = rem.a114, 
"122" = rem.a122, 
"123" = rem.a123, 
"125" = rem.a125, 
"131" = rem.a131, 
"132" = rem.a132, 
"133" = rem.a133, 
"134" = rem.a134, 
"135" = rem.a135, 
"136" = rem.a136, 
"137" = rem.a137, 
"138" = rem.a138, 
"139" = rem.a139,
"141" = rem.a141,
"142" = rem.a142,
"143" = rem.a143,
"144" = rem.a144,
"145" = rem.a145,
"146" = rem.a146,
"147" = rem.a147,
"148" = rem.a148,
"161" = rem.a161,
"162" = rem.a162,
"163" = rem.a163,
"191" = rem.a191,
"192" = rem.a192,
"193" = rem.a193,
"194" = rem.a194,
"195" = rem.a195,
"199" = rem.a199,
"232" = rem.a232,
"842" = rem.a842,
"845" = rem.a845,
"846" = rem.a846
FROM(	
		SELECT anho, mes, nivel,
		entidad,
		oee,
		case when documento_identidad > 0 then 'SI' else 'no' end as documento_identidad,
		case when anho_ingreso > 0 then 'SI' else 'no' end as anho_ingreso,
		case when vacancia > 0 then 'SI' else 'no' end as vacancia,
		case when comisionado > 0 then 'SI' else 'no' end as comisionado,
		case when a111 > 0 then 'SI' else 'no' end as a111,
		case when a112 > 0 then 'SI' else 'no' end as a112,
		case when a113 > 0 then 'SI' else 'no' end as a113,
		case when a114 > 0 then 'SI' else 'no' end as a114,
		case when a122 > 0 then 'SI' else 'no' end as a122,
		case when a123 > 0 then 'SI' else 'no' end as a123,
		case when a124 > 0 then 'SI' else 'no' end as a124,
		case when a125 > 0 then 'SI' else 'no' end as a125,
		case when a131 > 0 then 'SI' else 'no' end as a131,
		case when a132 > 0 then 'SI' else 'no' end as a132,
		case when a133 > 0 then 'SI' else 'no' end as a133,
		case when a134 > 0 then 'SI' else 'no' end as a134,
		case when a135 > 0 then 'SI' else 'no' end as a135,
		case when a136 > 0 then 'SI' else 'no' end as a136,
		case when a137 > 0 then 'SI' else 'no' end as a137,
		case when a138 > 0 then 'SI' else 'no' end as a138,
		case when a139 > 0 then 'SI' else 'no' end as a139,
		case when a141 > 0 then 'SI' else 'no' end as a141,
		case when a142 > 0 then 'SI' else 'no' end as a142,
		case when a143 > 0 then 'SI' else 'no' end as a143,
		case when a144 > 0 then 'SI' else 'no' end as a144,
		case when a145 > 0 then 'SI' else 'no' end as a145,
		case when a146 > 0 then 'SI' else 'no' end as a146,
		case when a147 > 0 then 'SI' else 'no' end as a147,
		case when a148 > 0 then 'SI' else 'no' end as a148,
		case when a161 > 0 then 'SI' else 'no' end as a161,
		case when a162 > 0 then 'SI' else 'no' end as a162,
		case when a163 > 0 then 'SI' else 'no' end as a163,
		case when a191 > 0 then 'SI' else 'no' end as a191,
		case when a192 > 0 then 'SI' else 'no' end as a192,
		case when a193 > 0 then 'SI' else 'no' end as a193,
		case when a194 > 0 then 'SI' else 'no' end as a194,
		case when a195 > 0 then 'SI' else 'no' end as a195,
		case when a199 > 0 then 'SI' else 'no' end as a199,
		case when a232 > 0 then 'SI' else 'no' end as a232,
		case when a842 > 0 then 'SI' else 'no' end as a842,
		case when a845 > 0 then 'SI' else 'no' end as a845,
		case when a846 > 0 then 'SI' else 'no' end as a846
		FROM (	SELECT temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,
			SUM(case when (temp1.documento_identidad <> '0') THEN 1 ELSE 0 END) AS documento_identidad,
			SUM(case when (temp1.anho_ingreso is not null AND temp1.anho_ingreso <> 0) THEN 1 ELSE 0 END) AS anho_ingreso,
			SUM(case when (temp1.documento_identidad = '0' AND obj_codigo = 111) THEN 1 ELSE 0 END) AS vacancia,
			SUM(case when (temp1.estado LIKE 'COMISIONADO' OR temp1.movimiento LIKE 'TT') THEN 1 ELSE 0 END) AS comisionado,
			SUM(case when obj_codigo = 111 THEN 1 ELSE 0 END) AS a111,
			SUM(case when obj_codigo = 112 THEN 1 ELSE 0 END) AS a112,
			SUM(case when obj_codigo = 113 THEN 1 ELSE 0 END) AS a113,
			SUM(case when obj_codigo = 114 THEN 1 ELSE 0 END) AS a114,
			SUM(case when obj_codigo = 122 THEN 1 ELSE 0 END) AS a122,
			SUM(case when obj_codigo = 123 THEN 1 ELSE 0 END) AS a123,
			SUM(case when obj_codigo = 124 THEN 1 ELSE 0 END) AS a124,
			SUM(case when obj_codigo = 125 THEN 1 ELSE 0 END) AS a125,
			SUM(case when obj_codigo = 131 THEN 1 ELSE 0 END) AS a131,
			SUM(case when obj_codigo = 132 THEN 1 ELSE 0 END) AS a132,
			SUM(case when obj_codigo = 133 THEN 1 ELSE 0 END) AS a133,
			SUM(case when obj_codigo = 134 THEN 1 ELSE 0 END) AS a134,
			SUM(case when obj_codigo = 135 THEN 1 ELSE 0 END) AS a135,
			SUM(case when obj_codigo = 136 THEN 1 ELSE 0 END) AS a136,
			SUM(case when obj_codigo = 137 THEN 1 ELSE 0 END) AS a137,
			SUM(case when obj_codigo = 138 THEN 1 ELSE 0 END) AS a138,
			SUM(case when obj_codigo = 139 THEN 1 ELSE 0 END) AS a139,
			SUM(case when obj_codigo = 141 THEN 1 ELSE 0 END) AS a141,
			SUM(case when obj_codigo = 142 THEN 1 ELSE 0 END) AS a142,
			SUM(case when obj_codigo = 143 THEN 1 ELSE 0 END) AS a143,
			SUM(case when obj_codigo = 144 THEN 1 ELSE 0 END) AS a144,
			SUM(case when obj_codigo = 145 THEN 1 ELSE 0 END) AS a145,
			SUM(case when obj_codigo = 146 THEN 1 ELSE 0 END) AS a146,
			SUM(case when obj_codigo = 147 THEN 1 ELSE 0 END) AS a147,
			SUM(case when obj_codigo = 148 THEN 1 ELSE 0 END) AS a148,
			SUM(case when obj_codigo = 161 THEN 1 ELSE 0 END) AS a161,
			SUM(case when obj_codigo = 162 THEN 1 ELSE 0 END) AS a162,
			SUM(case when obj_codigo = 163 THEN 1 ELSE 0 END) AS a163,
			SUM(case when obj_codigo = 191 THEN 1 ELSE 0 END) AS a191,
			SUM(case when obj_codigo = 192 THEN 1 ELSE 0 END) AS a192,
			SUM(case when obj_codigo = 193 THEN 1 ELSE 0 END) AS a193,
			SUM(case when obj_codigo = 194 THEN 1 ELSE 0 END) AS a194,
			SUM(case when obj_codigo = 195 THEN 1 ELSE 0 END) AS a195,
			SUM(case when obj_codigo = 199 THEN 1 ELSE 0 END) AS a199,
			SUM(case when obj_codigo = 232 THEN 1 ELSE 0 END) AS a232,
			SUM(case when obj_codigo = 842 THEN 1 ELSE 0 END) AS a842,
			SUM(case when obj_codigo = 845 THEN 1 ELSE 0 END) AS a845,
			SUM(case when obj_codigo = 846 THEN 1 ELSE 0 END) AS a846
		FROM (
		SELECT DISTINCT tmp.documento_identidad,tmp.anho_ingreso,tmp.anho,tmp.mes,nivel,entidad,oee,obj_codigo, estado,movimiento
		FROM sinarh.sin_nivel_entidad sne 
		INNER JOIN  remuneracion.remuneraciones_tmp tmp ON sne.nen_codigo = tmp.nivel
		INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = tmp.entidad
		INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad
		INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = tmp.oee
		WHERE tmp.anho = p_anho
		AND tmp.mes = p_mes
		UNION ALL
		SELECT DISTINCT tmp1.documento_identidad,tmp1.anho_ingreso,tmp1.anho,tmp1.mes,nivel,entidad,oee,obj_codigo, estado,movimiento
		FROM sinarh.sin_nivel_entidad sne 
		INNER JOIN  remuneracion.historico_remuneraciones_tmp tmp1 ON sne.nen_codigo = tmp1.nivel
		INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = tmp1.entidad
		INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad
		INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = tmp1.oee
		WHERE tmp1.anho = p_anho
		AND tmp1.mes = p_mes
		) AS temp1
		GROUP BY temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee
		order BY temp1.nivel,temp1.entidad,temp1.oee) tmp)rem 
WHERE ic.nivel = rem.nivel
AND ic.entidad = rem.entidad
AND ic.oee = rem.oee;

END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remuneracion.informe_cumplimiento(integer, integer)
  OWNER TO postgres;
