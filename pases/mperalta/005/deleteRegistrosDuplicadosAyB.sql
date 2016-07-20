--MEC id_configuracion_uo = 35 and mes = 5, se proceso por partes la planilla, no cuenta con registros duplicados.

--IPS 444 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 24 and usu_alta = 'SYSTEM';
--IPS 4 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=6 and id_configuracion_uo = 24 and usu_alta = 'SYSTEM';
--INFONA 479 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 95 and usu_alta = 'JAGUILERA';
--IPTA 1517 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 45 and usu_alta = 'JAGUILERA';
--MAG 1 registros borrado.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=2 and id_configuracion_uo = 30 and usu_alta = 'SYSTEM';
--MAG 38 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 30 and usu_alta = 'SYSTEM';
--MAG 61 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=6 and id_configuracion_uo = 30 and usu_alta = 'SYSTEM';
--MIC se mantendra solo el actual. 16 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 28;
--MM 359 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 108 and id_historico_remuneracion_tmp < 36939;
--MIN. PUBLICO 3 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 131 and usu_alta = 'SYSTEM';
--PETROPAR se mantendra solo el actual. 2017 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 104;
--SFP 179 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 1000 and usu_alta = 'SYSTEM';
--SFP 173 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=6 and id_configuracion_uo = 1000 and usu_alta = 'SYSTEM';
--SENAC 94 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 77 and usu_alta = 'JAGUILERA';
--S. N. CULTURA 973 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 122 and id_historico_remuneracion_tmp < 39579;
--DEPORTES se mantendra solo el actual. 4 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 73;
--SENAVITAT 1538 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 33 and id_historico_remuneracion_tmp < 137234;
--SENACSA 1 registros borrado.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 80 and usu_alta = 'SYSTEM';
--SENACSA 6 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=6 and id_configuracion_uo = 80 and usu_alta = 'SYSTEM';
--TSJE se mantendra solo el actual. 39 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 172;
--UNIV. DE PILAR 1072 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=5 and id_configuracion_uo = 113 and usu_alta = 'SYSTEM';
--UNIV. DE PILAR 15 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp where anho= 2015 and mes=6 and usu_alta = 'SYSTEM';

--OBSERVACION: LOS REGISTROS REFERENTES A LA FACULTAD DE CIENCIAS AGRARIAS (ID_CONFIGURACION_UO = 129) NO FUERON BORRADOS
--PORQUE NO SE ENCONTRARON REGISTROS REPETIDOS ENTRE LOS USUARIOS INGRESANTES 'SYSTEM' Y 'FCA.1632536'.

--ADMINISTRACION NACIONAL DE NAVEGACION Y PUERTOS - MAYO, 26 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 132 and usu_alta = 'SYSTEM';
--ANEAES - JUNIO, 2 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 6 and id_configuracion_uo = 123 and usu_alta = 'SYSTEM';
--BCP - MAYO, 16 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 133 and usu_alta = 'SYSTEM';
--CAJA DE JUBILACIONES Y PENSIONES DEL PERSONAL MUNICIPAL - JUNIO, 8 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 6 and id_configuracion_uo = 128 and usu_alta = 'SYSTEM';
--COMISION NAC DE PREVENSION CONTRA LA TORTURA Y OTROS TRATOS O PENAS CRUELES O INHUMANOS O DEGRADANTES - MAYO, 1 registro borrado.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 110 and usu_alta = 'SYSTEM';
--COMISION NACIONAL DE TELECOMUNICACIONES - MAYO, 704 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 86 and usu_alta = 'JAGUILERA';
--CONSEJO NACIONAL DE EDUCACION SUPERIOR - MAYO, 46 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 112 and usu_alta = 'JAGUILERA';
--CONTRALORIA GENERAL DE LA REPUBLICA - MAYO, 24 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 65 and usu_alta = 'SYSTEM';
--CONTRALORIA GENERAL DE LA REPUBLICA - JUNIO, 1 registros borrado.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 6 and id_configuracion_uo = 65 and usu_alta = 'SYSTEM';
--CORTE SUPREMA DE JUSTICIA - MAYO, 46 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 171 and usu_alta = 'SYSTEM';
--CORTE SUPREMA DE JUSTICIA - JUNIO, 9 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 6 and id_configuracion_uo = 171 and usu_alta = 'SYSTEM';
--CREDITO AGRICOLA DE HABILITACION - MAYO, 8 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 85 and usu_alta = 'SYSTEM';
--CREDITO AGRICOLA DE HABILITACION - MAYO, 4 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 6 and id_configuracion_uo = 85 and usu_alta = 'SYSTEM';
--DIRECCION NACIONAL DE ADUANAS - MAYO, 16 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 90 and usu_alta = 'SYSTEM';
--DIRECCION NACIONAL DE ADUANAS - JUNIO, 1 registro borrado.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 6 and id_configuracion_uo = 90 and usu_alta = 'SYSTEM';
--DIRECCION NACIONAL DE CONTRATACIONES PUBLICAS - MAYO, 512 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 82 and usu_alta = 'JAGUILERA';
--DIRECCION NACIONAL DE TRANSPORTE - MAYO, 7 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 134 and usu_alta = 'SYSTEM';
--FACULTAD DE ARQUITECTURA - MAYO, 108 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 141 and usu_alta = 'SYSTEM';
--FONDO GANADERO - MAYO, 363 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 165 and usu_alta = 'JAGUILERA';
--GOBIERNO DEPARTAMENTAL DE CAAZAPA - MAYO, 208 registros borrados.
DELETE FROM remuneracion.remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 121;
--GOBIERNO DEPARTAMENTAL DE CANINDEYU - MAYO, 121 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 214 and usu_alta = 'JAGUILERA';
--GOBIERNO DEPARTAMENTAL DE CONCEPCION - MAYO, 229 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 177 and usu_alta = 'JAGUILERA';
--GOBIERNO DEPARTAMENTAL DE PRESIDENTE HAYES - MAYO, 12 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 111 and usu_alta = 'SYSTEM';
--INSTITUTO ANDRES BARBERO - MAYO, 319 registros borrados.
DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE mes = 5 and id_configuracion_uo = 138 and usu_alta = 'SYSTEM';
