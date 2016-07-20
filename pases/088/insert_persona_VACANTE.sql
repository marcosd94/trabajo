
INSERT INTO general.persona(nombres,apellidos,documento_identidad,id_pais_expedicion_doc,activo,tiene_pariente,usu_alta,fecha_alta)
    VALUES ('VACANTE','VACANTE','0',100,TRUE,FALSE,'ADMIN',(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'));
