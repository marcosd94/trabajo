--INSERT DESTINATARIO SFP EN REMUN_CONFIG
INSERT INTO REMUNERACION.REMUN_CONFIG (id_remun_config, id_configuracion_uo, origen, activo, usu_alta, fecha_alta, usu_mod, fecha_mod, e_mail)
 VALUES (nextVal('remuneracion.remun_config_id_remun_config_seq'), 1000, 'N', true, 'ADMIN', '2015-05-07 10:15:00', NULL, NULL, 'sorue@dataworks.com.py');