
--TABLA PRORROGA_CAP, NUEVAS COLUMNAS PARA REPROGRAMACION DE FECHAS DE PUBLICACION
ALTER TABLE capacitacion.prorroga_cap
  ADD COLUMN fecha_pub_des_a timestamp without time zone, -- CORRESPONDE A LA ANTERIOR FECHA DE PUBLICACION DESDE
  ADD COLUMN fecha_pub_has_a timestamp without time zone, -- CORRESPONDE A LA ANTERIOR FECHA DE PUBLICACION HASTA
  ADD COLUMN fecha_pub_des_n timestamp without time zone, -- CORRESPONDE A LA NUEVA FECHA DE PUBLICACION DESDE
  ADD COLUMN fecha_pub_has_n timestamp without time zone; -- CORRESPONDE A LA NUEVA FECHA DE PUBLICACION HASTA