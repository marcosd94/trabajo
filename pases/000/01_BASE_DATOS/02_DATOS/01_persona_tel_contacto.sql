 UPDATE general.persona 
   SET tel_contacto='MOVIL' 
 WHERE general.persona.id_persona in (select general.persona.id_persona
                                        from general.persona, seguridad.usuario 
                                       where general.persona.id_persona = seguridad.usuario.id_persona);


