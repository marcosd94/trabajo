INSERT INTO remuneracion.duplicados_eliminados (anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,
						apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,
						funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona)
						 SELECT anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,
						categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,
						activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona
						 from remuneracion.historico_remuneraciones_tmp where (anho= 2015 and mes=5 and id_configuracion_uo = 24 and usu_alta = 'SYSTEM')
						 OR (anho= 2015 and mes=6 and id_configuracion_uo = 24 and usu_alta = 'SYSTEM')
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 95 and usu_alta = 'JAGUILERA')
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 45 and usu_alta = 'JAGUILERA')
						 OR (anho= 2015 and mes=2 and id_configuracion_uo = 30 and usu_alta = 'SYSTEM')
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 30 and usu_alta = 'SYSTEM')
						 OR (anho= 2015 and mes=6 and id_configuracion_uo = 30 and usu_alta = 'SYSTEM')
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 28)
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 108 and id_historico_remuneracion_tmp < 36939)
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 131 and usu_alta = 'SYSTEM')
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 104)
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 1000 and usu_alta = 'SYSTEM')
						 OR (anho= 2015 and mes=6 and id_configuracion_uo = 1000 and usu_alta = 'SYSTEM')
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 77 and usu_alta = 'JAGUILERA')
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 122 and id_historico_remuneracion_tmp < 39579)
						 --OR (anho= 2015 and mes=5 and id_configuracion_uo = 73)
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 33 and id_historico_remuneracion_tmp < 137234)
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 80 and usu_alta = 'SYSTEM')
						 OR (anho= 2015 and mes=6 and id_configuracion_uo = 80 and usu_alta = 'SYSTEM')
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 172)
						 OR (anho= 2015 and mes=5 and id_configuracion_uo = 113 and usu_alta = 'SYSTEM')
						 OR (anho= 2015 and mes=6 and id_configuracion_uo = 113 and usu_alta = 'SYSTEM')
						 OR (mes = 5 and id_configuracion_uo = 132 and usu_alta = 'SYSTEM')
						 OR (mes = 6 and id_configuracion_uo = 123 and usu_alta = 'SYSTEM')
						 OR (mes = 5 and id_configuracion_uo = 133 and usu_alta = 'SYSTEM')
						 OR (mes = 6 and id_configuracion_uo = 128 and usu_alta = 'SYSTEM')
						 OR (mes = 5 and id_configuracion_uo = 110 and usu_alta = 'SYSTEM')
						 OR (mes = 5 and id_configuracion_uo = 86 and usu_alta = 'JAGUILERA')
						 OR (mes = 5 and id_configuracion_uo = 112 and usu_alta = 'JAGUILERA')
						 OR (mes = 5 and id_configuracion_uo = 65 and usu_alta = 'SYSTEM')
						 OR (mes = 6 and id_configuracion_uo = 65 and usu_alta = 'SYSTEM')
						 OR (mes = 5 and id_configuracion_uo = 171 and usu_alta = 'SYSTEM')
						 OR (mes = 6 and id_configuracion_uo = 171 and usu_alta = 'SYSTEM')
						 OR (mes = 5 and id_configuracion_uo = 85 and usu_alta = 'SYSTEM')
						 OR (mes = 6 and id_configuracion_uo = 85 and usu_alta = 'SYSTEM')
						 OR (mes = 5 and id_configuracion_uo = 90 and usu_alta = 'SYSTEM')
						 OR (mes = 6 and id_configuracion_uo = 90 and usu_alta = 'SYSTEM')
						 OR (mes = 5 and id_configuracion_uo = 82 and usu_alta = 'JAGUILERA')
						 OR (mes = 5 and id_configuracion_uo = 134 and usu_alta = 'SYSTEM')
						 OR (mes = 5 and id_configuracion_uo = 141 and usu_alta = 'SYSTEM')
						 OR (mes = 5 and id_configuracion_uo = 165 and usu_alta = 'JAGUILERA')
						 OR (mes = 5 and id_configuracion_uo = 214 and usu_alta = 'JAGUILERA')
						 OR (mes = 5 and id_configuracion_uo = 177 and usu_alta = 'JAGUILERA')
						 OR (mes = 5 and id_configuracion_uo = 111 and usu_alta = 'SYSTEM')
						 OR (mes = 5 and id_configuracion_uo = 138 and usu_alta = 'SYSTEM');

INSERT INTO remuneracion.duplicados_eliminados (anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,
						apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,
						funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona)
						 SELECT anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,
						categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,
						activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona
						 from remuneracion.remuneraciones_tmp where mes = 5 and id_configuracion_uo = 121;