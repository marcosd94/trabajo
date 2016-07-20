CREATE OR REPLACE FUNCTION f_actualizar_codigo_usuario()
  RETURNS integer AS
$BODY$ 
  
DECLARE
  v_numero integer = 10000000;
  v_lugar character varying = 'Antes de empezar';
  v_cursor RECORD;
BEGIN

    FOR v_cursor IN (SELECT * FROM seguridad.usuario) LOOP 

        v_lugar = 'Update de la tabla usuario';
        UPDATE seguridad.usuario u
        SET codigo_usuario_numero = v_numero
        WHERE u.id_usuario = v_cursor.id_usuario;

        v_numero = v_numero + 1;

    END LOOP;
    RETURN 1; 

EXCEPTION 
    WHEN OTHERS THEN 
        RAISE EXCEPTION 'ERROR: % en %',SQLERRM,v_lugar; 
        RETURN 0;
END; 
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION f_actualizar_codigo_usuario()
  OWNER TO postgres;