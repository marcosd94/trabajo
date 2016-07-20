update seleccion.tipo_archivo set tamanho = 3, unid_medida = 'MB', unid_medida_byte =3145728, fecha_mod= LOCALTIMESTAMP, usu_mod= 'ADMIN';

insert into seleccion.tipo_archivo (id_tipo_archivo, descripcion, extension, tamanho, unid_medida, mimetype, activo, fecha_alta, usu_alta, unid_medida_byte)
values(11, 'IMAGENES JPG', '.jpg', 3, 'MB', 'image/jpeg', TRUE, LOCALTIMESTAMP, 'admin', 3145728);

insert into seleccion.tipo_archivo (id_tipo_archivo, descripcion, extension, tamanho, unid_medida, mimetype, activo, fecha_alta, usu_alta, unid_medida_byte)
values(12, 'IMAGENES JPG', '.jpeg', 3, 'MB', 'image/jpeg', TRUE, LOCALTIMESTAMP, 'admin', 3145728);

insert into seleccion.tipo_archivo (id_tipo_archivo, descripcion, extension, tamanho, unid_medida, mimetype, activo, fecha_alta, usu_alta, unid_medida_byte)
values(13, 'IMAGENES PNG', '.png', 3, 'MB', 'image/png', TRUE, LOCALTIMESTAMP, 'admin', 3145728);

insert into seleccion.tipo_archivo (id_tipo_archivo, descripcion, extension, tamanho, unid_medida, mimetype, activo, fecha_alta, usu_alta, unid_medida_byte)
values(14, 'IMAGENES TIFF', '.tif', 3, 'MB', 'image/tiff', TRUE, LOCALTIMESTAMP, 'admin', 3145728);

insert into seleccion.tipo_archivo (id_tipo_archivo, descripcion, extension, tamanho, unid_medida, mimetype, activo, fecha_alta, usu_alta, unid_medida_byte)
values(15, 'IMAGENES TIFF', '.tiff', 3, 'MB', 'image/tiff', TRUE, LOCALTIMESTAMP, 'admin', 3145728);

insert into seleccion.tipo_archivo (id_tipo_archivo, descripcion, extension, tamanho, unid_medida, mimetype, activo, fecha_alta, usu_alta, unid_medida_byte)
values(16, 'TEXTO OPEN OFFICE', '.odt', 3, 'MB', 'application/vnd.oasis.opendocument.text', TRUE, LOCALTIMESTAMP, 'admin', 3145728);

insert into seleccion.tipo_archivo (id_tipo_archivo, descripcion, extension, tamanho, unid_medida, mimetype, activo, fecha_alta, usu_alta, unid_medida_byte)
values(17, 'HOJA DE CALCULO OPEN OFFICE', '.ods', 3, 'MB', 'application/vnd.oasis.opendocument.spreadsheet', TRUE, LOCALTIMESTAMP, 'admin', 3145728);

insert into seleccion.tipo_archivo (id_tipo_archivo, descripcion, extension, tamanho, unid_medida, mimetype, activo, fecha_alta, usu_alta, unid_medida_byte)
values(18, 'DOCUMENTO MS WORD', '.docx', 3, 'MB', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', TRUE, LOCALTIMESTAMP, 'admin', 3145728);

insert into seleccion.tipo_archivo (id_tipo_archivo, descripcion, extension, tamanho, unid_medida, mimetype, activo, fecha_alta, usu_alta, unid_medida_byte)
values(19, 'HOJA DE CALCULO MS EXCEL', '.xlsx', 3, 'MB', 'application/vnd.oasis.opendocument.spreadsheet', TRUE, LOCALTIMESTAMP, 'admin', 3145728);

insert into seleccion.tipo_archivo (id_tipo_archivo, descripcion, extension, tamanho, unid_medida, mimetype, activo, fecha_alta, usu_alta, unid_medida_byte)
values(20, 'HOJA DE CALCULO MS EXCEL', '.xls', 3, 'MB', 'application/vnd.ms-excel', TRUE, LOCALTIMESTAMP, 'admin', 3145728);
