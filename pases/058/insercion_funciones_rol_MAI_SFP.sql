------ Id Rol 31: Maxima Autoridad SFP --------------------------


INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (60, 31); --funcion 60: cambiar contraseña 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (174, 31); --funcion 174: GESTIONAR BANDEJA DE ENTRADA 

INSERT INTO seguridad.rol_funcion(id_funcion, id_rol)
VALUES (206, 31); --funcion 206: FIRMAR RESOLUCION HOMOLOGACION 



--- Firma Resolución de Homologación SFP
--Agregar Actividad FIRMA DE RESOLUCIÓN HOMOLOGACION OEE a la bandeja de Entrada del rol MAI SFP
INSERT INTO proceso.proc_actividad_rol (id_rol, id_actividad_proceso, activo, usu_alta, fecha_alta)
VALUES (31, 25, TRUE, 'RVERON', LOCALTIMESTAMP); --id_actividad_proceso 25: FIRMA RESOLUCIÓN DE HOMOLOGACION OEE (es clave foranea en tabla proceso.actividad_proceso. En tabla Actividad tiene id:6)




