package py.com.excelsis.sicca.circuitoCIO.session.form;

import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.form.DatosGrupoPuestoFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("datosGrupoPuestoFormController617FC")
public class DatosGrupoPuestoFormController617FC {

	@In(required = false, create = true)
	DatosGrupoPuestoFormController datosGrupoPuestoFormController;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	public Boolean valida() {
		ConcursoPuestoAgr cpg =
			em.find(ConcursoPuestoAgr.class, datosGrupoPuestoFormController.getIdConcursoPuestoAgr());
		if (!cpg.getConcurso().getDatosEspecificosTipoConc().getDescripcion().contains("CONCURSO INTERNO")) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
		return datosGrupoPuestoFormController.validar();
	}

	public String save() {
		try {
			if (!datosGrupoPuestoFormController.validar()) {
				return null;
			}
			DatosGrupoPuesto datosGrupoPuesto =
				datosGrupoPuestoFormController.getDatosGrupoPuesto();
			if (datosGrupoPuesto == null) {
				datosGrupoPuesto = new DatosGrupoPuesto();
				datosGrupoPuesto.setConcursoPuestoAgr(new ConcursoPuestoAgr());
				datosGrupoPuesto.getConcursoPuestoAgr().setIdConcursoPuestoAgr(datosGrupoPuestoFormController.getIdConcursoPuestoAgr());
				datosGrupoPuestoFormController.setDatosGrupoPuesto(datosGrupoPuesto);
			}

			if (datosGrupoPuestoFormController.CODIGO_POR_EVALUACION.equals(datosGrupoPuestoFormController.getMinimoAplicar())) {
				datosGrupoPuesto.setPorMinPorEvaluacion(true);
				datosGrupoPuesto.setPorMinFinEvaluacion(null);
			} else {
				datosGrupoPuesto.setPorMinPorEvaluacion(null);
				datosGrupoPuesto.setPorMinFinEvaluacion(true);
			}

			if (datosGrupoPuestoFormController.CODIGO_TERNA.equals(datosGrupoPuestoFormController.getSeleccionAdjudicados())) {
				datosGrupoPuesto.setTerna(true);
				datosGrupoPuesto.setMerito(false);
			} else {
				datosGrupoPuesto.setTerna(false);
				datosGrupoPuesto.setMerito(true);
			}

			datosGrupoPuesto.setPorcMinimo(datosGrupoPuestoFormController.getMinimoEvaluacion());
			datosGrupoPuesto.setActivo(true);
			datosGrupoPuesto.setTituloAPublicar(datosGrupoPuestoFormController.getTitulo());
			datosGrupoPuesto.setObservacionAPublicar(datosGrupoPuestoFormController.getObservacion());

			if (datosGrupoPuestoFormController.getSeleccionPersonaConDisc() != null
				&& datosGrupoPuestoFormController.getSeleccionPersonaConDisc().equalsIgnoreCase("SI"))
				datosGrupoPuesto.setPreferenciaPersDiscapacidad(true);
			else
				datosGrupoPuesto.setPreferenciaPersDiscapacidad(false);
			if (datosGrupoPuestoFormController.isNew()) {
				// nuevo.
				datosGrupoPuesto.setFechaAlta(new Date());
				datosGrupoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(datosGrupoPuesto);
				datosGrupoPuestoFormController.setDatosGrupoPuesto(datosGrupoPuesto);
				datosGrupoPuestoFormController.publicacionPortal(true, datosGrupoPuestoFormController.genTextoPublicacion());
			} else {
				// editar
				datosGrupoPuesto.setFechaMod(new Date());
				datosGrupoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
				datosGrupoPuestoFormController.setDatosGrupoPuesto(em.merge(datosGrupoPuesto));
				datosGrupoPuestoFormController.publicacionPortal(false, datosGrupoPuestoFormController.genTextoPublicacion());
			}
			/* Incidencia 0001613 */
			if (datosGrupoPuestoFormController.getIsCIO()
				&& !datosGrupoPuestoFormController.getIsDESPRE()) {
				if (datosGrupoPuestoFormController.getPermanente().equalsIgnoreCase("P")) {
					datosGrupoPuesto.getConcursoPuestoAgr().setPermanente(true);
					datosGrupoPuesto.getConcursoPuestoAgr().setContratado(false);
					datosGrupoPuesto.getConcursoPuestoAgr().setFechaMod(new Date());
					datosGrupoPuesto.getConcursoPuestoAgr().setUsuMod(usuarioLogueado.getCodigoUsuario());
				} else {
					datosGrupoPuesto.getConcursoPuestoAgr().setPermanente(false);
					datosGrupoPuesto.getConcursoPuestoAgr().setContratado(true);
					datosGrupoPuesto.getConcursoPuestoAgr().setFechaMod(new Date());
					datosGrupoPuesto.getConcursoPuestoAgr().setUsuMod(usuarioLogueado.getCodigoUsuario());
				}
			} else if (datosGrupoPuestoFormController.getIsDESPRE()) {
				datosGrupoPuesto.getConcursoPuestoAgr().setPermanente(false);
				datosGrupoPuesto.getConcursoPuestoAgr().setContratado(true);
				datosGrupoPuesto.getConcursoPuestoAgr().setFechaMod(new Date());
				datosGrupoPuesto.getConcursoPuestoAgr().setUsuMod(usuarioLogueado.getCodigoUsuario());
			} else {
				datosGrupoPuesto.getConcursoPuestoAgr().setPermanente(null);
				datosGrupoPuesto.getConcursoPuestoAgr().setContratado(null);
				datosGrupoPuesto.getConcursoPuestoAgr().setFechaMod(new Date());
				datosGrupoPuesto.getConcursoPuestoAgr().setUsuMod(usuarioLogueado.getCodigoUsuario());
			}
			em.merge(datosGrupoPuesto.getConcursoPuestoAgr());
			/********************/
			em.flush();
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return "persisted";
	}
}
