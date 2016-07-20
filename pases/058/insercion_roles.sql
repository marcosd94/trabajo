/*INSERT INTO seguridad.rol(descripcion, activo, fecha_alta, usu_alta, homologador)
    VALUES ('ADMINISTRADOR DE CONCURSOS Y PERFILES SFP', TRUE, LOCALTIMESTAMP, 'RVERON',  TRUE); --id:26

INSERT INTO seguridad.rol(descripcion, activo, fecha_alta, usu_alta, homologador)
    VALUES ('ADMINISTRADOR DE CONCURSOS Y PERFILES OEE', TRUE, LOCALTIMESTAMP, 'RVERON',  FALSE); --id:27

INSERT INTO seguridad.rol(descripcion, activo, fecha_alta, usu_alta, homologador)
    VALUES ('ADMINISTRADOR DE UNIDADES ORGANIZATIVAS SFP', TRUE, LOCALTIMESTAMP, 'RVERON',  TRUE); --id:28

INSERT INTO seguridad.rol(descripcion, activo, fecha_alta, usu_alta, homologador)
    VALUES ('ADMINISTRADOR DE UNIDADES ORGANIZATIVAS OEE', TRUE, LOCALTIMESTAMP, 'RVERON',  FALSE); --id:29

INSERT INTO seguridad.rol(descripcion, activo, fecha_alta, usu_alta, homologador)
    VALUES ('ADMINISTRADOR DE USUARIOS', TRUE, LOCALTIMESTAMP, 'RVERON',  FALSE); --id:30

 INSERT INTO seguridad.rol(descripcion, activo, fecha_alta, usu_alta, homologador)
    VALUES ('MAXIMA AUTORIDAD INSTITUCIONAL SFP', TRUE, LOCALTIMESTAMP, 'RVERON',  TRUE); --id:31

 INSERT INTO seguridad.rol(descripcion, activo, fecha_alta, usu_alta, homologador)
    VALUES ('GESTOR DE EVALUACION DE DESEMPEÑO OEE', TRUE, LOCALTIMESTAMP, 'WERNER',  FALSE); --id:32

 INSERT INTO seguridad.rol(descripcion, activo, fecha_alta, usu_alta, homologador)
    VALUES ('GESTOR DE EVALUACION DE DESEMPEÑO SFP', TRUE, LOCALTIMESTAMP, 'WERNER',  TRUE); --id:33 

UPDATE seguridad.rol
set descripcion = 'EVALUADOR DE CONCURSOS', fecha_mod = localtimestamp, usu_mod='RVERON'
where id_rol = 23; */


