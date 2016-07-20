﻿ALTER TABLE planificacion.configuracion_uo_cab ADD COLUMN url character varying(50);



UPDATE planificacion.configuracion_uo_cab
    SET url = CASE denominacion_unidad
	WHEN 'ADMINISTRACION NACIONAL DE ELECTRICIDAD' THEN 'http://www.ande.gov.py/'
        WHEN 'ADMINISTRACIÓN NACIONAL DE NAVEGACIÓN Y PUERTOS' THEN 'http://annp.gov.py'
        WHEN 'AGENCIA FINANCIERA DE DESARROLLO' THEN 'http://www.afd.gov.py/'
        WHEN 'AGENCIA NACIONAL DE EVALUACIÓN Y ACREDITACIÓN DE LA EDUCACIÓN SUPERIOR (ANEAES)' THEN 'http://www.aneaes.gov.py'
        WHEN 'AUDITORÍA GENERAL DEL PODER EJECUTIVO' THEN 'http://agpe.gov.py/'
        WHEN 'BANCO NACIONAL DE FOMENTO (BNF)' THEN 'http://www.bnf.gov.py/'
        WHEN 'CÁMARA DE DIPUTADOS' THEN 'http://www.diputados.gov.py'
        WHEN 'COMISIÓN NACIONAL DE TELECOMUNICACIONES' THEN 'http://www.conatel.gov.py/'
        WHEN 'COMISIÓN NACIONAL DE VALORES' THEN 'http://www.cnv.gov.py'
        WHEN 'COMPAÑIA PARAGUAYA DE COMUNICACIONES S.A.' THEN 'http://www.copaco.com.py'
        WHEN 'CORTE SUPREMA DE JUSTICIA' THEN 'www.pj.gov.py'
        WHEN 'CREDITO AGRICOLA DE HABILITACION' THEN 'http://www.cah.gov.py/'
        WHEN 'DIRECCIÓN NACIONAL DE ADUANAS' THEN 'http://www.aduana.gov.py/'
        WHEN 'DIRECCIÓN NACIONAL DE AERONÁUTICA CIVIL' THEN 'http://www.dinac.gov.py/v2/'
        WHEN 'DIRECCIÓN NACIONAL DE BENEFICENCIA' THEN 'http://www.diben.org.py/'
        WHEN 'DIRECCIÓN NACIONAL DE CONTRATACIONES PÚBLICAS' THEN 'https://www.contrataciones.gov.py/'
        WHEN 'DIRECCIÓN NACIONAL DE CORREOS DEL PARAGUAY' THEN 'www.correoparaguayo.gov.py/'
        WHEN 'DIRECCIÓN NACIONAL DE PROPIEDAD INTELECTUAL' THEN 'http://www.dinapi.gov.py/'
        WHEN 'DIRECCION NACIONAL DE TRANSPORTE' THEN 'http://www.dinatran.gov.py/'
        WHEN 'EMPRESA DE SERVICIOS SANITARIOS DEL PARAGUAY S.A. - ESSAP' THEN 'http://www.essap.com.py'
        WHEN 'ENTE REGULADOR DE SERVICIOS SANITARIOS' THEN 'http://www.erssan.gov.py/'
        WHEN 'ESCRIBANÍA MAYOR DE GOBIERNO' THEN 'http://www.emg.gov.py/'
        WHEN 'FONDO GANADERO' THEN 'http://www.fondogan.gov.py/'
        WHEN 'GABINETE CIVIL' THEN 'http://www.gabinetecivil.gov.py/'
        WHEN 'GABINETE MILITAR' THEN 'http://www.gabimil.mil.py/'
        WHEN 'GABINETE SOCIAL' THEN 'http://www.gabinetesocial.gov.py/'
        WHEN 'INSTITUTO DE PREVISIÓN SOCIAL' THEN 'http://www.ips.gov.py/'
        WHEN 'INSTITUTO FORESTAL NACIONAL' THEN 'http://www.infona.gov.py/'
        WHEN 'INSTITUTO NACIONAL DE COOPERATIVISMO' THEN 'http://www.incoop.gov.py/v1/'
        WHEN 'INSTITUTO NACIONAL DE DESARROLLO RURAL Y DE LA TIERRA' THEN 'http://www.indert.gov.py'
        WHEN 'INSTITUTO NACIONAL DE TECNOLOGÍA Y NORMALIZACIÓN' THEN 'http://www.intn.gov.py/'
        WHEN 'INSTITUTO PARAGUAYO DE ARTESANIA' THEN 'http://www.artesania.gov.py/'
        WHEN 'INSTITUTO PARAGUAYO DEL INDIGENA(INDI)' THEN 'http://www.indi.gov.py/'
        WHEN 'INSTITUTO PARAGUAYO DE TECNOLOGIA AGRARIA(IPTA)' THEN 'www.ipta.gov.py'
        WHEN 'MINISTERIO DE AGRICULTURA Y GANADERÍA' THEN 'http://www.mag.gov.py/'
        WHEN 'MINISTERIO DE DEFENSA NACIONAL' THEN 'http://www.mdn.gov.py/'
        WHEN 'MINISTERIO DE EDUCACION Y CULTURA' THEN 'http://www.mec.gov.py'
        WHEN 'MINISTERIO DE HACIENDA' THEN 'http://www.hacienda.gov.py'
        WHEN 'MINISTERIO DE INDUSTRIA Y COMERCIO' THEN 'http://www.mic.gov.py'
        WHEN 'MINISTERIO DE JUSTICIA' THEN 'http://www.ministeriodejusticia.gov.py/'
        WHEN 'MINISTERIO DE LA MUJER' THEN 'http://www.mujer.gov.py'
        WHEN 'MINISTERIO DEL INTERIOR' THEN 'http://www.mdi.gov.py/'
        WHEN 'MINISTERIO DEL TRABAJO, EMPLEO Y SEGURIDAD SOCIAL' THEN 'http://www.mtess.gov.py'
        WHEN 'MINISTERIO DE OBRAS PÚBLICAS Y COMUNICACIONES' THEN 'http://www.mopc.gov.py/'
        WHEN 'MINISTERIO DE RELACIONES EXTERIORES' THEN 'http://www.mre.gov.py/v1/default.aspx'
        WHEN 'MINISTERIO DE SALUD PÚBLICA Y BIENESTAR SOCIAL' THEN 'http://www.mspbs.gov.py'
        WHEN 'PETRÓLEOS PARAGUAYOS' THEN 'http://petropar.gov.py'
        WHEN 'PROCURADURÍA GENERAL DE LA REPÚBLICA' THEN 'http://www.pgr.gov.py/'
        WHEN 'SECRETARÍA DE ACCIÓN SOCIAL' THEN 'www.sas.gov.py'
        WHEN 'SECRETARÍA DE DESARROLLO PARA REPATRIADOS Y REFUGIADOS CONNACIONALES' THEN 'http://www.repatriados.gov.py/'
        WHEN 'SECRETARÍA DE EMERGENCIA NACIONAL (SEN)' THEN 'http://www.sen.gov.py/'
        WHEN 'SECRETARÍA DE INFORMACIÓN Y COMUNICACIÓN (SICOM)' THEN 'http://www.sicom.gov.py'
        WHEN 'SECRETARÍA DE LA FUNCIÓN PÚBLICA' THEN 'http://www.sfp.gov.py'
        WHEN 'SECRETARÍA DEL AMBIENTE' THEN 'http://www.seam.gov.py/'
        WHEN 'SECRETARÍA DE LA NIÑEZ Y LA ADOLESCENCIA' THEN 'http://www.snna.gov.py'
        WHEN 'SECRETARÍA DE POLITICAS LINGÜISTICAS' THEN 'http://www.spl.gov.py'
        WHEN 'SECRETARÍA DE PREVENCIÓN DE LAVADO DE DINERO O BIENES' THEN 'http://www.seprelad.gov.py/'
        WHEN 'SECRETARÍA NACIONAL ANTICORRUPCIÓN' THEN 'http://www.senac.gov.py/'
        WHEN 'SECRETARÍA NACIONAL ANTIDROGAS' THEN 'http://www.senad.gov.py/'
        WHEN 'SECRETARÍA NACIONAL DE CULTURA' THEN 'http://www.cultura.gov.py'
        WHEN 'SECRETARÍA NACIONAL DE DEPORTES' THEN 'http://www.snd.gov.py/'
        WHEN 'SECRETARÍA NACIONAL DE LA JUVENTUD' THEN 'http://www.snj.gov.py/'
        WHEN 'SECRETARÍA NACIONAL DE LA VIVIENDA Y EL HÁBITAT' THEN 'http://www.senavitat.gov.py'
        WHEN 'SECRETARÍA NACIONAL DE TECNOLOGÍAS DE LA INFORMACIÓN Y COMUNICACIÓN' THEN 'http://www.senatics.gov.py'
        WHEN 'SECRETARÍA NACIONAL DE TURISMO' THEN 'http://www.senatur.gov.py/'
        WHEN 'SECRETARÍA NACIONAL POR LOS DERECHOS HUMANOS DE LAS PERSONAS CON DISCAPACIDAD (SENADIS)' THEN 'http://www.senadis.gov.py/'
        WHEN 'SECRETARÍA TÉCNICA DE PLANIFICACIÓN' THEN 'http://www.stp.gov.py/'
        WHEN 'SERVICIO NACIONAL DE CALIDAD Y SALUD ANIMAL' THEN 'http://www.senacsa.gov.py'
        WHEN 'SERVICIO NACIONAL DE CALIDAD Y SANIDAD VEGETAL Y DE SEMILLA' THEN 'http://www.senave.gov.py/'
        WHEN 'SERVICIO NACIONAL DE SANEAMIENTO AMBIENTAL' THEN 'www.senasa.gov.py'
        WHEN 'VICE PRESIDENCIA DE LA REPÚBLICA' THEN 'http://www.vicepresidencia.gov.py/'
       

    END,
    
    direccion = CASE denominacion_unidad
	WHEN 'ADMINISTRACION NACIONAL DE ELECTRICIDAD' THEN 'España 1268 casi Padre Cardozo'
        WHEN 'ADMINISTRACIÓN NACIONAL DE NAVEGACIÓN Y PUERTOS' THEN 'El paraguayo independiente y Colon'
        WHEN 'AGENCIA FINANCIERA DE DESARROLLO' THEN 'Herib Campos Cervera 886 c/ Aviadores del Chaco - Edificio Australia, 3er. y 4to. Piso'
        WHEN 'AGENCIA NACIONAL DE EVALUACIÓN Y ACREDITACIÓN DE LA EDUCACIÓN SUPERIOR (ANEAES)' THEN 'Yegros N° 930 entre Manuel Domínguez y Teniente Fariña'
        WHEN 'AUDITORÍA GENERAL DEL PODER EJECUTIVO' THEN 'Juan Bautista Alberdi Nº 972 c/ Manduvirá'
        WHEN 'BANCO NACIONAL DE FOMENTO (BNF)' THEN 'Independencia Nacional esq. Cerro Corá'
        WHEN 'CÁMARA DE DIPUTADOS' THEN 'El Paraguayo Independiente entre 14 de Mayo y 15 de Agosto'
        WHEN 'COMISIÓN NACIONAL DE TELECOMUNICACIONES' THEN 'Presidente Franco N° 780 esq. Ayolas'
        WHEN 'COMISIÓN NACIONAL DE VALORES' THEN 'Lugano (1ra. Proyectada) N° 627 casi 15 de Agosto, 2° Piso'
        WHEN 'COMPAÑIA PARAGUAYA DE COMUNICACIONES S.A.' THEN 'Alberdi N° 531, Edificio Gral Bernardino Caballero, Piso 1'
        WHEN 'CORTE SUPREMA DE JUSTICIA' THEN 'Mariano Roque Alonso y Testanova'
        WHEN 'CREDITO AGRICOLA DE HABILITACION' THEN 'Carios 362 casi William Richardson'
        WHEN 'DIRECCIÓN NACIONAL DE ADUANAS' THEN 'El Paraguayo Independiente y Colón'
        WHEN 'DIRECCIÓN NACIONAL DE AERONÁUTICA CIVIL' THEN 'Avda. Mcal. López e/Vice Pdte. Sánchez y 22 de Setiembre, 2do. Piso'
        WHEN 'DIRECCIÓN NACIONAL DE BENEFICENCIA' THEN 'Mcal. Lopez esq. EE.UU.'
        WHEN 'DIRECCIÓN NACIONAL DE CONTRATACIONES PÚBLICAS' THEN 'E.E.U.U. 961 c/ Tte. Fariña.'
        WHEN 'DIRECCIÓN NACIONAL DE CORREOS DEL PARAGUAY' THEN '25 de Mayo y Fulgencio Yegros / Asunción'
        WHEN 'DIRECCIÓN NACIONAL DE PROPIEDAD INTELECTUAL' THEN 'España 323 casi Estados Unidos'
        WHEN 'DIRECCION NACIONAL DE TRANSPORTE' THEN 'Ruta Nº2 Mcal. Estigarribia, Km 14'
        WHEN 'EMPRESA DE SERVICIOS SANITARIOS DEL PARAGUAY S.A. - ESSAP' THEN 'José Berges 516 e/ San José y Brasil'
        WHEN 'ENTE REGULADOR DE SERVICIOS SANITARIOS' THEN 'Avda. Artigas 1921 Edificio Automotor 1er. Piso'
        WHEN 'ESCRIBANÍA MAYOR DE GOBIERNO' THEN 'Avda. Colón 1163 e/ Jejuí e Ygatimi'
        WHEN 'FONDO GANADERO' THEN 'Avda. Mcal. López 1699 esq. República Dominicana'
        WHEN 'GABINETE CIVIL' THEN 'El Paraguayo Independiente y Ayolas'
        WHEN 'GABINETE MILITAR' THEN 'El Paraguayo Independiente entre O''leary y Ayolas'
        WHEN 'GABINETE SOCIAL' THEN 'Pdte. Franco esq. Ayolas. Edificio Ayfra, Piso 12'
        WHEN 'INSTITUTO DE PREVISIÓN SOCIAL' THEN 'Herrera y Constitución - Edificio de la Caja Central'
        WHEN 'INSTITUTO FORESTAL NACIONAL' THEN 'Ruta Nº 2 "Mcal. Estigarribia". Km. 10 1/2'
        WHEN 'INSTITUTO NACIONAL DE COOPERATIVISMO' THEN 'Avda. 25 de Mayo 1684 c/ Rca. Francesa'
        WHEN 'INSTITUTO NACIONAL DE DESARROLLO RURAL Y DE LA TIERRA' THEN 'Tacuary 276'
        WHEN 'INSTITUTO NACIONAL DE TECNOLOGÍA Y NORMALIZACIÓN' THEN 'Avda. Artigas 3973 e/ General Roa'
        WHEN 'INSTITUTO PARAGUAYO DE ARTESANIA' THEN 'Dr. Prieto c/ Cap. Nicolas Bliloff'
        WHEN 'INSTITUTO PARAGUAYO DEL INDIGENA(INDI)' THEN 'Don Bosco 745 e/ Haedo y Humaita'
        WHEN 'INSTITUTO PARAGUAYO DE TECNOLOGIA AGRARIA(IPTA)' THEN 'Ruta Mcal. Estigarribia km 10,5 - San Lorenzo'
        WHEN 'MINISTERIO DE AGRICULTURA Y GANADERÍA' THEN 'Yegros e/ 25 de Mayo y Cerro Corá, Edificio San Rafael'
        WHEN 'MINISTERIO DE DEFENSA NACIONAL' THEN 'Avda. Mariscal López e/ Vice Presidente Sánchez y 22 de septiembre'
        WHEN 'MINISTERIO DE EDUCACION Y CULTURA' THEN '15 de Agosto e/ General Díaz y E. V. Haedo'
        WHEN 'MINISTERIO DE HACIENDA' THEN 'Chile 252 | 1220'
        WHEN 'MINISTERIO DE INDUSTRIA Y COMERCIO' THEN 'Av. Mcal. López 3333 c/ Dr. Weiss, Villa Morra'
        WHEN 'MINISTERIO DE JUSTICIA' THEN 'Avda. Dr. José Gaspar Rodríguez de Francia esq. Estados Unidos'
        WHEN 'MINISTERIO DE LA MUJER' THEN 'Presidente Franco esq. Ayolas, Edificio Ayfra, Piso 13, bloque B y Planta Baja'
        WHEN 'MINISTERIO DEL INTERIOR' THEN 'Chile y Manduvira'
        WHEN 'MINISTERIO DEL TRABAJO, EMPLEO Y SEGURIDAD SOCIAL' THEN 'Luis Alberto de Herrera esquina Paraguarí'
        WHEN 'MINISTERIO DE OBRAS PÚBLICAS Y COMUNICACIONES' THEN 'Oliva y Alberdi Nº 411, C.P. Nº 1221'
        WHEN 'MINISTERIO DE RELACIONES EXTERIORES' THEN 'Palma y 14 de Mayo - Palacio Benigno López'
        WHEN 'MINISTERIO DE SALUD PÚBLICA Y BIENESTAR SOCIAL' THEN 'Pettirossi Esq. Brasil'
        WHEN 'PETRÓLEOS PARAGUAYOS' THEN 'Chile Nro. 753 casi Eduardo V. Haedo'
        WHEN 'PROCURADURÍA GENERAL DE LA REPÚBLICA' THEN 'Cerro Corá 2249 e/ 22 de Setiembre y Vice Pte Sánchez.'
        WHEN 'SECRETARÍA DE ACCIÓN SOCIAL' THEN 'Avenida Mariscal López y Coronel. Pampliega'
        WHEN 'SECRETARÍA DE DESARROLLO PARA REPATRIADOS Y REFUGIADOS CONNACIONALES' THEN '25 de Mayo 1091 c/ Brasil'
        WHEN 'SECRETARÍA DE EMERGENCIA NACIONAL (SEN)' THEN 'Fulgencio R. Moreno c/ Parapiti.'
        WHEN 'SECRETARÍA DE INFORMACIÓN Y COMUNICACIÓN (SICOM)' THEN 'Alberdi 633 entre Gral. Diaz y Haedo'
        WHEN 'SECRETARÍA DE LA FUNCIÓN PÚBLICA' THEN 'Constitucion esq. 25 de Mayo.'
        WHEN 'SECRETARÍA DEL AMBIENTE' THEN 'Avenida Madame Lynch N° 3500 esq Reservista de la Guerra del Chaco'
        WHEN 'SECRETARÍA DE LA NIÑEZ Y LA ADOLESCENCIA' THEN 'Avda. Mariscal López 2.029 e/ Acá Carayá. Asunción.'
        WHEN 'SECRETARÍA DE POLITICAS LINGÜISTICAS' THEN 'Avenida España N° 114 casi Tacuary, Edificio Villa Lorences.'
        WHEN 'SECRETARÍA DE PREVENCIÓN DE LAVADO DE DINERO O BIENES' THEN 'Federación Rusa c/ Augusto Roa Bastos'
        WHEN 'SECRETARÍA NACIONAL ANTICORRUPCIÓN' THEN 'El Paraguayo Independiente esquina 15 de Agosto'
        WHEN 'SECRETARÍA NACIONAL ANTIDROGAS' THEN 'Avda. Fernando de la Mora 2998 c/ Avda. De la Victoria'
        WHEN 'SECRETARÍA NACIONAL DE CULTURA' THEN 'Sede central EEUU 284 esq. Mcal. Estigarribia'
        WHEN 'SECRETARÍA NACIONAL DE DEPORTES' THEN 'Avda. Eusebio Ayala y Oroite Km. 4 1/2'
        WHEN 'SECRETARÍA NACIONAL DE LA JUVENTUD' THEN 'Ayolas 451 entre Estrella y Oliva. Edificio Capital'
        WHEN 'SECRETARÍA NACIONAL DE LA VIVIENDA Y EL HÁBITAT' THEN 'Independencia Nacional Nº 909 e/ Manuel Domínguez.'
        WHEN 'SECRETARÍA NACIONAL DE TECNOLOGÍAS DE LA INFORMACIÓN Y COMUNICACIÓN' THEN 'Complejo Santos E2 - Gral. Santos 1170 c/ Concordia'
        WHEN 'SECRETARÍA NACIONAL DE TURISMO' THEN 'Palma 468 c/ 14 de Mayo'
        WHEN 'SECRETARÍA NACIONAL POR LOS DERECHOS HUMANOS DE LAS PERSONAS CON DISCAPACIDAD (SENADIS)' THEN 'Yeruti  y Jacaranda – Fernando de la Mora Zona Norte – Barrio IPVU'
        WHEN 'SECRETARÍA TÉCNICA DE PLANIFICACIÓN' THEN 'Estrella 505 esq. 14 de Mayo'
        WHEN 'SERVICIO NACIONAL DE CALIDAD Y SALUD ANIMAL' THEN 'Km 10 1/2 Ruta Mcal. Estigarribia'
        WHEN 'SERVICIO NACIONAL DE CALIDAD Y SANIDAD VEGETAL Y DE SEMILLA' THEN 'Humaitá Nº 145 c/ Ntra. Sra. de la Asunción.'
        WHEN 'SERVICIO NACIONAL DE SANEAMIENTO AMBIENTAL' THEN 'Mcal. Estigarribia 796 esq. Tacuary'
        WHEN 'VICE PRESIDENCIA DE LA REPÚBLICA' THEN 'Juan E. O''leary 222 esq. Pdte. Franco'
       

    END,
    telefono = CASE denominacion_unidad
	WHEN 'ADMINISTRACION NACIONAL DE ELECTRICIDAD' THEN '+595 21 211001 /20'
        WHEN 'ADMINISTRACIÓN NACIONAL DE NAVEGACIÓN Y PUERTOS' THEN '+595 21 439 2000'
        WHEN 'AGENCIA FINANCIERA DE DESARROLLO' THEN '(595-21) 606 020'
        WHEN 'AGENCIA NACIONAL DE EVALUACIÓN Y ACREDITACIÓN DE LA EDUCACIÓN SUPERIOR (ANEAES)' THEN '+595 21 494 940'
        WHEN 'AUDITORÍA GENERAL DEL PODER EJECUTIVO' THEN '(595 21) 493 171-5'
        WHEN 'BANCO NACIONAL DE FOMENTO (BNF)' THEN '(+595 21) 419 1000'
        WHEN 'CÁMARA DE DIPUTADOS' THEN '021 414 4000'
        WHEN 'COMISIÓN NACIONAL DE TELECOMUNICACIONES' THEN '59521 438 2000'
        WHEN 'COMISIÓN NACIONAL DE VALORES' THEN '59521444242'
        WHEN 'COMPAÑIA PARAGUAYA DE COMUNICACIONES S.A.' THEN '+595 21 2385027'
        WHEN 'CORTE SUPREMA DE JUSTICIA' THEN '021 424311/15'
        WHEN 'CREDITO AGRICOLA DE HABILITACION' THEN '595215690100'
        WHEN 'DIRECCIÓN NACIONAL DE ADUANAS' THEN '+59521 4162100'
        WHEN 'DIRECCIÓN NACIONAL DE AERONÁUTICA CIVIL' THEN '+59521 203615'
        WHEN 'DIRECCIÓN NACIONAL DE BENEFICENCIA' THEN '+59521225801/ 4'
        WHEN 'DIRECCIÓN NACIONAL DE CONTRATACIONES PÚBLICAS' THEN '+59521 4154000'
        WHEN 'DIRECCIÓN NACIONAL DE CORREOS DEL PARAGUAY' THEN '+595 (21) 498 112 / 16'
        WHEN 'DIRECCIÓN NACIONAL DE PROPIEDAD INTELECTUAL' THEN '+595 21 210-977'
        WHEN 'DIRECCION NACIONAL DE TRANSPORTE' THEN '59521 582145'
        WHEN 'EMPRESA DE SERVICIOS SANITARIOS DEL PARAGUAY S.A. - ESSAP' THEN '0800-11-0888/162'
        WHEN 'ENTE REGULADOR DE SERVICIOS SANITARIOS' THEN '+59521 283556'
        WHEN 'ESCRIBANÍA MAYOR DE GOBIERNO' THEN '+59521 423 117'
        WHEN 'FONDO GANADERO' THEN '+59521 227 288'
        WHEN 'GABINETE CIVIL' THEN '+595 21 4140200'
        WHEN 'GABINETE MILITAR' THEN '+59521 4140200'
        WHEN 'GABINETE SOCIAL' THEN '+595 21 493456/8'
        WHEN 'INSTITUTO DE PREVISIÓN SOCIAL' THEN '+59521 223-141/3 Linea Gratuita 0800-11-5000'
        WHEN 'INSTITUTO FORESTAL NACIONAL' THEN '+59521 570518 - 570519'
        WHEN 'INSTITUTO NACIONAL DE COOPERATIVISMO' THEN '+59521 226989'
        WHEN 'INSTITUTO NACIONAL DE DESARROLLO RURAL Y DE LA TIERRA' THEN '+59521 443161'
        WHEN 'INSTITUTO NACIONAL DE TECNOLOGÍA Y NORMALIZACIÓN' THEN '+59521 290 160 / 290 266'
        WHEN 'INSTITUTO PARAGUAYO DE ARTESANIA' THEN '(021) 526-535'
        WHEN 'INSTITUTO PARAGUAYO DEL INDIGENA(INDI)' THEN '+59521 452280'
        WHEN 'INSTITUTO PARAGUAYO DE TECNOLOGIA AGRARIA(IPTA)' THEN '+595 (21)  586 13'
        WHEN 'MINISTERIO DE AGRICULTURA Y GANADERÍA' THEN '+595 21 450 937'
        WHEN 'MINISTERIO DE DEFENSA NACIONAL' THEN '+59521 2490000'
        WHEN 'MINISTERIO DE EDUCACION Y CULTURA' THEN '(021) 450014/15 – 451025 – 452895'
        WHEN 'MINISTERIO DE HACIENDA' THEN '+595-21 440-010 al 17'
        WHEN 'MINISTERIO DE INDUSTRIA Y COMERCIO' THEN '+59521 616-3000'
        WHEN 'MINISTERIO DE JUSTICIA' THEN '+595 21 493 210'
        WHEN 'MINISTERIO DE LA MUJER' THEN '(595 21) 450 036/8'
        WHEN 'MINISTERIO DEL INTERIOR' THEN '595 21 415 - 2240'
        WHEN 'MINISTERIO DEL TRABAJO, EMPLEO Y SEGURIDAD SOCIAL' THEN '021 448183 – 021 493202'
        WHEN 'MINISTERIO DE OBRAS PÚBLICAS Y COMUNICACIONES' THEN '595 (021) 4149000'
        WHEN 'MINISTERIO DE RELACIONES EXTERIORES' THEN '+595 21 493928'
        WHEN 'MINISTERIO DE SALUD PÚBLICA Y BIENESTAR SOCIAL' THEN '(595) 21 - 204-601/3'
        WHEN 'PETRÓLEOS PARAGUAYOS' THEN '+595 21 448503'
        WHEN 'PROCURADURÍA GENERAL DE LA REPÚBLICA' THEN '+ 595 21 215 034/ 7'
        WHEN 'SECRETARÍA DE ACCIÓN SOCIAL' THEN '(595-21) 678 430 / 31 / 53'
        WHEN 'SECRETARÍA DE DESARROLLO PARA REPATRIADOS Y REFUGIADOS CONNACIONALES' THEN '+ 595 21 226858'
        WHEN 'SECRETARÍA DE EMERGENCIA NACIONAL (SEN)' THEN '(595 21) 440-997'
        WHEN 'SECRETARÍA DE INFORMACIÓN Y COMUNICACIÓN (SICOM)' THEN '(595 21) 449-111'
        WHEN 'SECRETARÍA DE LA FUNCIÓN PÚBLICA' THEN '(021) 451 926'
        WHEN 'SECRETARÍA DEL AMBIENTE' THEN '(+59521) 615-803/15'
        WHEN 'SECRETARÍA DE LA NIÑEZ Y LA ADOLESCENCIA' THEN '595 - 21 - 207 160'
        WHEN 'SECRETARÍA DE POLITICAS LINGÜISTICAS' THEN '(+59521) 491 928'
        WHEN 'SECRETARÍA DE PREVENCIÓN DE LAVADO DE DINERO O BIENES' THEN '+595 21 600435'
        WHEN 'SECRETARÍA NACIONAL ANTICORRUPCIÓN' THEN '021 -450 001 / 450 002'
        WHEN 'SECRETARÍA NACIONAL ANTIDROGAS' THEN '(021) 554 - 585 / 6'
        WHEN 'SECRETARÍA NACIONAL DE CULTURA' THEN '(021)442515/6  (021) 492548'
        WHEN 'SECRETARÍA NACIONAL DE DEPORTES' THEN '59521 520671/6'
        WHEN 'SECRETARÍA NACIONAL DE LA JUVENTUD' THEN '(595 21) 453-212 (595 21) 453-213'
        WHEN 'SECRETARÍA NACIONAL DE LA VIVIENDA Y EL HÁBITAT' THEN '(021)  444-340'
        WHEN 'SECRETARÍA NACIONAL DE TECNOLOGÍAS DE LA INFORMACIÓN Y COMUNICACIÓN' THEN '(595 21) 201-014 / 201-813'
        WHEN 'SECRETARÍA NACIONAL DE TURISMO' THEN '(595 21) 494.110/441.530'
        WHEN 'SECRETARÍA NACIONAL POR LOS DERECHOS HUMANOS DE LAS PERSONAS CON DISCAPACIDAD (SENADIS)' THEN ' (59521) 670-593'
        WHEN 'SECRETARÍA TÉCNICA DE PLANIFICACIÓN' THEN '(595-21) 450-422'
        WHEN 'SERVICIO NACIONAL DE CALIDAD Y SALUD ANIMAL' THEN '+595 21 505 727 / 501 374'
        WHEN 'SERVICIO NACIONAL DE CALIDAD Y SANIDAD VEGETAL Y DE SEMILLA' THEN '+59521 445-769 / 441-549 / 451-910'
        WHEN 'SERVICIO NACIONAL DE SANEAMIENTO AMBIENTAL' THEN '(+595 21) 494 399'
        WHEN 'VICE PRESIDENCIA DE LA REPÚBLICA' THEN '+595 21 457-140 / 1'
       

    END
    
WHERE denominacion_unidad IN (
'ADMINISTRACION NACIONAL DE ELECTRICIDAD',
'ADMINISTRACIÓN NACIONAL DE NAVEGACIÓN Y PUERTOS',
'AGENCIA FINANCIERA DE DESARROLLO',
'AGENCIA NACIONAL DE EVALUACIÓN Y ACREDITACIÓN DE LA EDUCACIÓN SUPERIOR (ANEAES)',
'AUDITORÍA GENERAL DEL PODER EJECUTIVO',
'BANCO NACIONAL DE FOMENTO (BNF)',
'CÁMARA DE DIPUTADOS',
'COMISIÓN NACIONAL DE TELECOMUNICACIONES',
'COMISIÓN NACIONAL DE VALORES',
'COMPAÑIA PARAGUAYA DE COMUNICACIONES S.A.',
'CORTE SUPREMA DE JUSTICIA',
'CREDITO AGRICOLA DE HABILITACION',
'DIRECCIÓN NACIONAL DE ADUANAS',
'DIRECCIÓN NACIONAL DE AERONÁUTICA CIVIL',
'DIRECCIÓN NACIONAL DE BENEFICENCIA',
'DIRECCIÓN NACIONAL DE CONTRATACIONES PÚBLICAS', 
'DIRECCIÓN NACIONAL DE CORREOS DEL PARAGUAY', 
'DIRECCIÓN NACIONAL DE PROPIEDAD INTELECTUAL', 
'DIRECCION NACIONAL DE TRANSPORTE',
'EMPRESA DE SERVICIOS SANITARIOS DEL PARAGUAY S.A. - ESSAP',
'ENTE REGULADOR DE SERVICIOS SANITARIOS',
'ESCRIBANÍA MAYOR DE GOBIERNO',
'FONDO GANADERO',
'GABINETE CIVIL',
'GABINETE MILITAR',
'GABINETE SOCIAL',
'INSTITUTO DE PREVISIÓN SOCIAL',
'INSTITUTO FORESTAL NACIONAL',
'INSTITUTO NACIONAL DE COOPERATIVISMO',
'INSTITUTO NACIONAL DE DESARROLLO RURAL Y DE LA TIERRA',
'INSTITUTO NACIONAL DE TECNOLOGÍA Y NORMALIZACIÓN',
'INSTITUTO PARAGUAYO DE ARTESANIA',
'INSTITUTO PARAGUAYO DEL INDIGENA(INDI)',
'INSTITUTO PARAGUAYO DE TECNOLOGIA AGRARIA(IPTA)',
'MINISTERIO DE AGRICULTURA Y GANADERÍA',
'MINISTERIO DE DEFENSA NACIONAL',
'MINISTERIO DE EDUCACION Y CULTURA',
'MINISTERIO DE HACIENDA',
'MINISTERIO DE INDUSTRIA Y COMERCIO',
'MINISTERIO DE JUSTICIA',
'MINISTERIO DE LA MUJER',
'MINISTERIO DEL INTERIOR',
'MINISTERIO DEL TRABAJO, EMPLEO Y SEGURIDAD SOCIAL',
'MINISTERIO DE OBRAS PÚBLICAS Y COMUNICACIONES',
'MINISTERIO DE RELACIONES EXTERIORES',
'MINISTERIO DE SALUD PÚBLICA Y BIENESTAR SOCIAL',
'PETRÓLEOS PARAGUAYOS',
'PROCURADURÍA GENERAL DE LA REPÚBLICA',
'SECRETARÍA DE ACCIÓN SOCIAL',
'SECRETARÍA DE DESARROLLO PARA REPATRIADOS Y REFUGIADOS CONNACIONALES',
'SECRETARÍA DE EMERGENCIA NACIONAL (SEN)',
'SECRETARÍA DE INFORMACIÓN Y COMUNICACIÓN (SICOM)',
'SECRETARÍA DE LA FUNCIÓN PÚBLICA',
'SECRETARÍA DEL AMBIENTE',
'SECRETARÍA DE LA NIÑEZ Y LA ADOLESCENCIA',
'SECRETARÍA DE POLITICAS LINGÜISTICAS',
'SECRETARÍA DE PREVENCIÓN DE LAVADO DE DINERO O BIENES',
'SECRETARÍA NACIONAL ANTICORRUPCIÓN',
'SECRETARÍA NACIONAL ANTIDROGAS',
'SECRETARÍA NACIONAL DE CULTURA',
'SECRETARÍA NACIONAL DE DEPORTES',
'SECRETARÍA NACIONAL DE LA JUVENTUD',
'SECRETARÍA NACIONAL DE LA VIVIENDA Y EL HÁBITAT',
'SECRETARÍA NACIONAL DE TECNOLOGÍAS DE LA INFORMACIÓN Y COMUNICACIÓN',
'SECRETARÍA NACIONAL DE TURISMO',
'SECRETARÍA NACIONAL POR LOS DERECHOS HUMANOS DE LAS PERSONAS CON DISCAPACIDAD (SENADIS)',
'SECRETARÍA TÉCNICA DE PLANIFICACIÓN',
'SERVICIO NACIONAL DE CALIDAD Y SALUD ANIMAL',
'SERVICIO NACIONAL DE CALIDAD Y SANIDAD VEGETAL Y DE SEMILLA',
'SERVICIO NACIONAL DE SANEAMIENTO AMBIENTAL',
'VICE PRESIDENCIA DE LA REPÚBLICA'
);