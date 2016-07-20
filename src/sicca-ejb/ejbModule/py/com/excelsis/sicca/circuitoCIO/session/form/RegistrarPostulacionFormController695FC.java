package py.com.excelsis.sicca.circuitoCIO.session.form;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.seleccion.session.form.RegistrarPostulacionFormController;

@Scope(ScopeType.CONVERSATION)
@Name("registrarPostulacionFormController695FC")
public class RegistrarPostulacionFormController695FC {
	@In
	StatusMessages statusMessages;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false, create = true)
	RegistrarPostulacionFormController registrarPostulacionFormController;

	public String init() throws Exception {
		registrarPostulacionFormController.limpiar();
		registrarPostulacionFormController.obtenerDatosPantalla1();

		registrarPostulacionFormController.setValido(true);
		if (!validar()) {
			registrarPostulacionFormController.setValido(false);
			return "FALLO";
		}
		if (!registrarPostulacionFormController.precondiciones()) {
			registrarPostulacionFormController.setValido(false);
			return "FALLO";
		}
		return "ok";
	}

	public boolean validar() {
		if (registrarPostulacionFormController.controlarInhabilitado()) {
			registrarPostulacionFormController.setMsjError("Error");
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Usted se encuentra Inhabilitado. Para más Información, contactar con la Secretaría de la Función Pública");
			return false;
		}
		if (registrarPostulacionFormController.controlarJubilado()) {
			registrarPostulacionFormController.setMsjError("Error");
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Usted se encuentra Jubilado. Para más Información, contactar con la Secretaría de la Función Pública");
			return false;
		}

		if (registrarPostulacionFormController.getConcursoPuestoAgr().getConcurso().getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CSI")) {
			Query q =
				em.createQuery("select ConcursoPuestoDet from ConcursoPuestoDet ConcursoPuestoDet "
					+ " where ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo and ConcursoPuestoDet.activo is true ");
			q.setParameter("idGrupo", registrarPostulacionFormController.getConcursoPuestoAgr().getIdConcursoPuestoAgr());

			List<ConcursoPuestoDet> lista = q.getResultList();
			if (lista.size() > 0) {
				ConcursoPuestoDet concursoPuestoDet = lista.get(0);
				if (concursoPuestoDet.getPlantaCargoDet().getPermanente() != null
					&& concursoPuestoDet.getPlantaCargoDet().getPermanente()) {
					if (!registrarPostulacionFormController.controlarNacionalidad()) {
						registrarPostulacionFormController.setMsjError("Error");
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU241_msg6"));
						return false;
					}
				} else {
					// No controlar la nacionalidad
				}
			}
		}

		if (!registrarPostulacionFormController.esMayorDeEdad()) {
			registrarPostulacionFormController.setMsjError("Error");
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU241_msg7"));
			return false;
		}

		return true;
	}

}
