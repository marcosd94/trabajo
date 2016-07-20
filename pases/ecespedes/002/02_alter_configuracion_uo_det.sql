
--AGREGA EL CAMPO PARA EL CODIGO_UO
ALTER TABLE planificacion.configuracion_uo_det
  ADD COLUMN codigo_uo character varying;
COMMENT ON COLUMN planificacion.configuracion_uo_det.codigo_uo IS 'Campo para calcular el codigo de la unidad organizativa, a travez del procedimiento almacenado planificacion.obtenercodigooee(long id)';


--ACTUALIZA LOS REGISTROS YA EXISTENTES EN LA BASE DE DATOS
UPDATE planificacion.configuracion_uo_det det
   SET 
       codigo_uo= (select planificacion.obtenercodigooee(det.id_configuracion_uo_det))
   WHERE det.id_configuracion_uo_det in(select id_configuracion_uo_det from planificacion.configuracion_uo_det);


--SE CREA LA FUNCION PARA EL TRIGGER actualizarcodigouo()

CREATE OR REPLACE FUNCTION planificacion.actualizarcodigouo()
  RETURNS trigger AS
$BODY$
BEGIN
 IF NEW.orden <> OLD.orden THEN

	--ACTUALIZA EL CODIGOUO DEL REGISTRO
 UPDATE planificacion.configuracion_uo_det det
   SET 
       codigo_uo= (select planificacion.obtenercodigooee(det.id_configuracion_uo_det))
   WHERE det.id_configuracion_uo_det = NEW.id_configuracion_uo_det;

	--ACTUALIZA EL CODIGOUO DE LOS HIJOS
 UPDATE planificacion.configuracion_uo_det det
   SET 
       codigo_uo= (select planificacion.obtenercodigooee(det.id_configuracion_uo_det))
   WHERE det.id_configuracion_uo_det in (select id_configuracion_uo_det from planificacion.configuracion_uo_det 
						where id_configuracion_uo_det_padre = NEW.id_configuracion_uo_det);

	

 END IF;
 
 RETURN NEW;
END;
$BODY$  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION planificacion.actualizarcodigouo()
  OWNER TO postgres;
  
--SE CREA LA FUNCION PARA EL TRIGGER insertarcodigouo()
  
  CREATE OR REPLACE FUNCTION planificacion.insertarcodigouo()
 RETURNS trigger AS
$BODY$
BEGIN

--INSERTA A EL CODIGOUO DEL REGISTRO
UPDATE planificacion.configuracion_uo_det det
  SET 
      codigo_uo= (select planificacion.obtenercodigooee(det.id_configuracion_uo_det))
  WHERE det.id_configuracion_uo_det = NEW.id_configuracion_uo_det;
 
RETURN NEW;
END;
$BODY$
 LANGUAGE plpgsql VOLATILE
 COST 100;
ALTER FUNCTION planificacion.insertarcodigouo()
 OWNER TO postgres;
 
  
 
--SE CREA EL TRIGGER AFTER UPDATE

CREATE TRIGGER actualizarcodigouo AFTER UPDATE
   ON planificacion.configuracion_uo_det FOR EACH ROW
   EXECUTE PROCEDURE planificacion.actualizarcodigouo();
   
--SE CREA EL TRIGGER AFTER INSERT

CREATE TRIGGER insertarcodigouo AFTER INSERT
 ON planificacion.configuracion_uo_det FOR EACH ROW
 EXECUTE PROCEDURE planificacion.insertarcodigouo();


