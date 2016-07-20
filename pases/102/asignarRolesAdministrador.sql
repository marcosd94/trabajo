insert into seguridad.rol_funcion (id_funcion, id_rol) values
((select id_funcion from seguridad.funcion where url like 'groupMantenimientoSeg'), (select id_rol from seguridad.rol where descripcion like 'ADMINISTRADOR')),
((select id_funcion from seguridad.funcion where url like 'groupValidacionesConfSeg'), (select id_rol from seguridad.rol where descripcion like 'ADMINISTRADOR')),
((select id_funcion from seguridad.funcion where url like 'groupSeguridad'), (select id_rol from seguridad.rol where descripcion like 'ADMINISTRADOR'));