package py.com.excelsis.sicca.session.util;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("numeracionConcursoUtilFormController")
public class NumeracionConcursoUtilFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 770592677347233682L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	public void init() {

	}

	@SuppressWarnings("unchecked")
	public String getCodigoConcurso(Long id) {
		Date fechaActual = new Date();
		Integer anhoActual = fechaActual.getYear() + 1900;
		try {
			String sql = "select concurso.* "
					+ "from seleccion.concurso concurso "
					+ "where concurso.id_concurso = " + id;
			List<Concurso> lista = em.createNativeQuery(sql, Concurso.class)
					.getResultList();

			if (lista != null && lista.size() > 0) {
				Integer nro = lista.get(0).getNumero();
				Integer anho = lista.get(0).getAnhio();
				if (anho != null && anho.equals(anhoActual)) {
					if (nro != null)
						nro = nro + 1;
					else
						nro = 1;
				}
				if (anho != null && !anho.equals(anhoActual)) {
					anho = anhoActual;
					nro = 1;
				}
				if (anho == null) {
					anho = anhoActual;
					nro = 1;
				}
				
				Concurso concurso = new Concurso();
				concurso = lista.get(0);
				concurso.setAnhio(anho);
				concurso.setNumero(nro);
				em.merge(concurso);
				em.flush();
				String cod = nro+"/"+anho+" de "+ concurso.getConfiguracionUoCab().getDescripcionCorta();
				return cod;
			}

		} catch (Exception e) {

		}
		return null;
	}
}
