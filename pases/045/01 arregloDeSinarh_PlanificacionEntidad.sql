
--Eliminar registros del 2011 de sin_entidad y sin_nivel_entidad
delete from sinarh.sin_entidad
where ani_aniopre=2011;

delete from sinarh.sin_nivel_entidad
where ani_aniopre=2011;


--Modificacion de entidad para que todos apunten a la entidad de sinarh vieja (poder ejecutivo)
update planificacion.entidad as s
set id_sin_entidad=329 
where s.nen_codigo=12 and s.ent_codigo=1; --and s.ani_aniopre=2014;

--Eliminacion de duplicado de poder ejecutivo en sinarh entidad
delete from sinarh.sin_entidad as s
where s.nen_codigo=12 and s.ent_codigo=1 and s.ani_aniopre=2014;

--Eliminacion de dupicado de poder ejecutivo en sin_nivel_entidad
delete from sinarh.sin_nivel_entidad as s
where s.nen_codigo =12 and s.ani_aniopre=2014;

--modificacion de mspbs en entidad para posterior eliminacion de duplicados de mspbs
update planificacion.entidad as s
set id_sin_entidad=336 
where
s.nen_codigo=12 and s.ent_codigo=8 and s.ani_aniopre=2014;


--eliminacion de mspbs duplicado
delete from sinarh.sin_entidad as s
where s.nen_codigo=12 and s.ent_codigo=8 and s.ani_aniopre=2014;

--se modifica el aniopre de las entidades de prevision s.
update sinarh.sin_nivel_entidad as s
set ani_aniopre=2012
where s.nen_codigo =24 and s.ani_aniopre=2014;


--se modifica el aniopre de ministerio del trabajo perteneciente al poder ejecutivo
--No ejecutar...
--update sinarh.sin_entidad as s
--set ani_aniopre=2012
--where s.nen_codigo =12 and s.ent_codigo=16;


--Se ponen los ani_aniopre correspondientes de las entidades de sinarh
update planificacion.entidad as p
set ani_aniopre=s.ani_aniopre
from sinarh.sin_entidad as s
where (s.nen_codigo=p.nen_codigo and s.ent_codigo=p.ent_codigo);

