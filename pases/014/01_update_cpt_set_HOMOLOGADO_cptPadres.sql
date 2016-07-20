UPDATE planificacion.cpt
   SET estado_homologacion= 'HOMOLOGADO'
 WHERE id_cpt_padre is null;


