--Eliminar primero la Vista, luego editar las columnas

DROP VIEW movilidad.vw_evaluacion_funcionario;

DROP VIEW seleccion.vw_lista_elegible;
DROP VIEW seleccion.vw_eval_ref_postu;
DROP VIEW seleccion.est_movilidad;
DROP VIEW seleccion.est_ingresos;
DROP VIEW remuneracion.remuneraciones_mod_edad_sexo;
DROP VIEW remuneracion.historico_remuneraciones_mod_edad_sexo;
DROP VIEW proceso.bandeja_entrada_csi;
DROP VIEW proceso.bandeja_entrada_cio;
DROP VIEW proceso.bandeja_entrada;
DROP VIEW planificacion.vw_entidad_oee;
DROP VIEW planificacion.puestos_estructura_organizacional;
DROP VIEW movilidad.cabecera_historico_movilidad;
DROP VIEW movilidad.bandeja_traslado;
DROP VIEW juridicos.personas_sumariadas;
DROP VIEW juridicos.personas_accion_inconstitucionalidad;
DROP VIEW general.funcionarios_activos;
DROP VIEW general.empleado_puesto_651;
DROP VIEW eval_desempeno.planes_mejora_rpt;
DROP VIEW eval_desempeno.historico_evaluacion;
DROP VIEW eval_desempeno.funcionarios_evaluados;
DROP VIEW eval_desempeno.evaluaciones_funcionario3;
DROP VIEW eval_desempeno.evaluaciones_funcionario2;
DROP VIEW eval_desempeno.evaluaciones_funcionario;
DROP VIEW eval_desempeno.bandeja_evaluacion;
DROP VIEW desvinculacion.personas_inhabilitadas;
DROP VIEW desvinculacion.personas_desvinculadas;
DROP VIEW capacitacion.promedio_calificaciones_asistencia_det;
DROP VIEW capacitacion.personas_capacitadas;
DROP VIEW capacitacion.desbloqueo;
DROP VIEW capacitacion.capacitacion_poc_post;
DROP VIEW capacitacion.capacitacion_og290;
DROP VIEW capacitacion.capacitacion_finalizada;
DROP VIEW capacitacion.capacitacion_en_proceso;
DROP VIEW capacitacion.bandeja_capacitacion;



ALTER TABLE planificacion.configuracion_uo_cab ALTER COLUMN denominacion_unidad TYPE character varying(256);

ALTER TABLE planificacion.configuracion_uo_det ALTER COLUMN denominacion_unidad TYPE character varying(256);

