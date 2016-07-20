-- Nuevo tipo de documento Partida de Nacimiento, para agregar al legajo
INSERT INTO seleccion.datos_especificos(descripcion, valor_alf, valor_num, id_datos_generales, activo, usu_alta, fecha_alta)
    VALUES ( 'COPIA DE PARTIDA DE NACIMIENTO', 'FPOS', null, 2, true, 'VMERCADO', now());
