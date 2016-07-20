-- el registro con descripcion OCUPADO debe estar con estado ACTIVO para que no generen inconvenientes en la Tarea de Ingresar Postulante
UPDATE planificacion.estado_cab set activo= TRUE where descripcion = 'OCUPADO';

INSERT INTO planificacion.estado_cab (id_estado_cab, descripcion, usu_alta, fecha_alta, usu_mod, fecha_mod, activo) VALUES (32, 'LIBRE', 'INSTRUCTOR', '2012-10-25 08:49:08.14', NULL, NULL, true);

