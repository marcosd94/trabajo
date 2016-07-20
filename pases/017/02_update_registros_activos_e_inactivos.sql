UPDATE seleccion.postulacion
   SET 
       estado_postulacion='ACTIVO'
 WHERE activo is true;

UPDATE seleccion.postulacion
   SET 
       estado_postulacion='CANCELADO'
 WHERE activo is false;