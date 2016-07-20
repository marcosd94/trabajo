package py.com.excelsis.sicca.circuitoCSI.session.form;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.seleccion.session.CargarCarpeta508FC;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("cargarCarpeta696FC")
public class CargarCarpeta696FC {
	@In
	StatusMessages statusMessages;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false, create = true)
	CargarCarpeta508FC cargarCarpeta508FC;

	public void init() {
		ConcursoPuestoAgr grupo = em.find(ConcursoPuestoAgr.class, cargarCarpeta508FC.getIdGrupo());
		if (!grupo.getConcurso().getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CSI")) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
		cargarCarpeta508FC.setCsi(true);
		cargarCarpeta508FC.init();
	}
}
