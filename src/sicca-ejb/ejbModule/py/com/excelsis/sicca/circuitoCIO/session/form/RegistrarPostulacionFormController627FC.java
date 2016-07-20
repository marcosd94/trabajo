package py.com.excelsis.sicca.circuitoCIO.session.form;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.seleccion.session.form.RegistrarPostulacionFormController;
import py.com.excelsis.sicca.util.ValidadorController;

@Scope(ScopeType.CONVERSATION)
@Name("registrarPostulacionFormController627FC")
public class RegistrarPostulacionFormController627FC {
	@In
	StatusMessages statusMessages;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false, create = true)
	RegistrarPostulacionFormController registrarPostulacionFormController;

	public ValidadorController validadorController;
	private ValidadorDTO validadorDTO;

	public String init() throws Exception {
		registrarPostulacionFormController.limpiar();
		registrarPostulacionFormController.obtenerDatosPantalla1();
		registrarPostulacionFormController.docsObligatoriosFaltantes();
		registrarPostulacionFormController.docsNoObligatorios();
		registrarPostulacionFormController.tienePariente();
		registrarPostulacionFormController.setValido(true);
		registrarPostulacionFormController.tieneEvaluacionDesempenho();
		
		
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
		if (validadorController == null)
			validadorController =
				(py.com.excelsis.sicca.util.ValidadorController) Component.getInstance(ValidadorController.class, true);
		validadorDTO =
			validadorController.validarCfg("PERSONA", "INHAB", registrarPostulacionFormController.getPersona(), null, null, registrarPostulacionFormController.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab());
		if (validadorDTO != null && !validadorDTO.isValido()) {
			registrarPostulacionFormController.setMsjError("Error");
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Usted se encuentra Inhabilitado. Para más Información, contactar con la Secretaría de la Función Pública");
			return false;
		}
		validadorDTO =
			validadorController.validarCfg("PERSONA", "JUBILADO", registrarPostulacionFormController.getPersona(), null, null, registrarPostulacionFormController.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab());
		if (validadorDTO != null && !validadorDTO.isValido()) {
			registrarPostulacionFormController.setMsjError("Error");
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Usted se encuentra Jubilado. Para más Información, contactar con la Secretaría de la Función Pública");
			return false;
		}
		validadorDTO =
			validadorController.validarCfg("PERSONA", "NAC_PAR", registrarPostulacionFormController.getPersona(), null, null, registrarPostulacionFormController.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab());
		if (validadorDTO != null && !validadorDTO.isValido()) {
			registrarPostulacionFormController.setMsjError("Error");
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU241_msg6"));
			return false;
		}
		validadorDTO =
			validadorController.validarCfg("PERSONA", "E_MINIMA", registrarPostulacionFormController.getPersona(), null, null, registrarPostulacionFormController.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab());
		if (validadorDTO != null && !validadorDTO.isValido()) {
			registrarPostulacionFormController.setMsjError("Error");
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU241_msg7"));
			return false;
		}

		if (registrarPostulacionFormController.getConcursoPuestoAgr().getConcurso().getDatosEspecificosTipoConc().getDescripcion().equalsIgnoreCase("CONCURSO INTERNO DE OPOSICION INSTITUCIONAL")) {
			validadorDTO =
				validadorController.validarCfg("PERSONA", "PERSONA_OEE", registrarPostulacionFormController.getPersona(), null, null, registrarPostulacionFormController.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab());
			if (validadorDTO != null && !validadorDTO.isValido()) {
				registrarPostulacionFormController.setMsjError("Error");
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Usted no puede postular porque no es funcionario/a de la institución que llama a concurso");
				return false;
			} else if (validadorDTO != null && validadorDTO.isValido()) {
				Query q =
					em.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto "
						+ " where EmpleadoPuesto.activo is true "
						+ " and EmpleadoPuesto.actual is true "
						+ " and EmpleadoPuesto.persona.idPersona = :idPersona ");
				q.setParameter("idPersona", registrarPostulacionFormController.getPersona().getIdPersona());
				List<EmpleadoPuesto> lEmpleadoPuesto = q.getResultList();
				EmpleadoPuesto ep = null;
				if (lEmpleadoPuesto.size() > 0)
					ep = lEmpleadoPuesto.get(0);

				if (registrarPostulacionFormController.getConcursoPuestoAgr().getConcurso().getDesprecarizacion()) {
					if (!ep.getContratado()) {
						registrarPostulacionFormController.setMsjError("Error");
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Usted no puede postular porque no es funcionario/a contratado y el concurso es sólo para contratados");
						return false;
					} else {
						return true;
					}
				} else {
					if (registrarPostulacionFormController.getConcursoPuestoAgr().getPermanente() != null
						&& registrarPostulacionFormController.getConcursoPuestoAgr().getPermanente()) {
						if (ep != null && ep.getContratado() != null && ep.getContratado()) {
							registrarPostulacionFormController.setMsjError("Error");
							statusMessages.clear();
							statusMessages.add(Severity.ERROR, "Usted no puede postular porque no es funcionario/a permanente y el concurso es sólo para permanentes");
							return false;
						} else {
							return true;
						}
					} else if (registrarPostulacionFormController.getConcursoPuestoAgr().getContratado()) {
						if (!ep.getContratado()) {
							registrarPostulacionFormController.setMsjError("Error");
							statusMessages.clear();
							statusMessages.add(Severity.ERROR, "Usted no puede postular porque no es funcionario/a contratado y el concurso es sólo para contratados");
							return false;
						} else {
							return true;
						}
					}
				}
			}
		} else if (registrarPostulacionFormController.getConcursoPuestoAgr().getConcurso().getDatosEspecificosTipoConc().getDescripcion().equalsIgnoreCase("CONCURSO INTERNO DE OPOSICION INTER INSTITUCIONAL")) {
			validadorDTO =
					validadorController.validarCfg("PERSONA", "PERSONA_OEE", registrarPostulacionFormController.getPersona(), null, null, registrarPostulacionFormController.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab());
				if (validadorDTO != null && validadorDTO.isValido()) {
					Query q =
						em.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto "
							+ " where EmpleadoPuesto.activo is true "
							+ " and EmpleadoPuesto.actual is true "
							+ " and EmpleadoPuesto.persona.idPersona = :idPersona ");
					q.setParameter("idPersona", registrarPostulacionFormController.getPersona().getIdPersona());
					List<EmpleadoPuesto> lEmpleadoPuesto = q.getResultList();
					EmpleadoPuesto ep = null;
					if (lEmpleadoPuesto.size() > 0){
						ep = lEmpleadoPuesto.get(0);
						if (ep != null && ep.getContratado() != null && ep.getContratado()) {
							registrarPostulacionFormController.setMsjError("Error");
							statusMessages.clear();
							statusMessages.add(Severity.ERROR, "Usted no puede postular porque no es funcionario/a permanente y el concurso es sólo para permanentes");
							return false;
						} else {
							return true;
						}
						
					}else{
						registrarPostulacionFormController.setMsjError("Error");
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Usted no puede postular porque no es funcionario/a  y el concurso es sólo para funcionarios/as permanentes");
						return false;
					}
					
							
						
					}
				}
		

		return true;
	}
}
