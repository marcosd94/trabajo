package py.com.excelsis.sicca.desvinculacion.session.form;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import py.com.excelsis.sicca.entity.Inhabilitados;


@Name("admVencimientoInhabilitadosFC")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class AdmVencimientoInhabilitadosFC {

	@In(value = "entityManager")
	EntityManager em;
	
	private final String USUARIO = "SYSTEM";

	public void init(){
		
	}

	@Transactional
	@Asynchronous
	public QuartzTriggerHandle procesoPrincipal(@Expiration Date when, @IntervalCron String interval) {
		procesoVencimiento();

		return null;
	}

	
	public void procesoVencimiento(){
		try{
			Date fecha = new Date();
			List<Inhabilitados> lista = getVencimientoInhabilitados(fecha);

			if (lista != null && lista.size() > 0){
				for (Inhabilitados inhabilitados : lista){
					inhabilitados = em.find(Inhabilitados.class, inhabilitados.getIdInhabilitado());
					inhabilitados.setInhabilitado(false);
					inhabilitados.setFechaHabilit(fecha);
					inhabilitados.setUsuHabilit(USUARIO);
					em.merge(inhabilitados);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Retorna la lista de los vencimientos que se deben inhabilitar
	 * @param fecha: fecha actual del sistema
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Inhabilitados> getVencimientoInhabilitados(Date fecha){
		
		String consulta = "" +
		" SELECT inhabilitados " +
		" FROM Inhabilitados inhabilitados " +
		" WHERE inhabilitados.inhabilitado = :inhabilitado " +
		"     AND cast(inhabilitados.fechaHasta as date) = cast(:fecha as date)";
	
		Query q = em.createQuery(consulta);
		q.setParameter("inhabilitado", true);
		q.setParameter("fecha", fecha);
		
		List<Inhabilitados> lista = q.getResultList();
		return lista;
	}
	
}
