﻿--CONSTRAINT PARA UNICIDAD EN EL USERNAME/CODIGO_USUARIO, EN TABLA USUARIO
ALTER TABLE seguridad.usuario ADD CONSTRAINT unicidad_codigo_usuario UNIQUE (codigo_usuario);