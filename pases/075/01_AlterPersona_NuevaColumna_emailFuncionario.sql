
--TABLA PERSONA DEL ESQUEMA GENERAL, NUEVA COLUMNA E_MAIL_FUNCIONARIO PARA CORREO ELECTRONICO DEL USUARIO
ALTER TABLE general.persona
  ADD COLUMN e_mail_funcionario character varying(50);