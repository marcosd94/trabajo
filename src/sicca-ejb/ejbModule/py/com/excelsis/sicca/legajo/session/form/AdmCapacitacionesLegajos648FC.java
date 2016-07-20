package py.com.excelsis.sicca.legajo.session.form;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.EvaluacionPart;
import py.com.excelsis.sicca.entity.ListaDet;

import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;

@Scope(ScopeType.CONVERSATION)
@Name("admCapacitacionesLegajos648FC")
public class AdmCapacitacionesLegajos648FC {
	public List<ListaDet> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<ListaDet> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

	public List<ListaDet> getlGrilla2() {
		return lGrilla2;
	}

	public void setlGrilla2(List<ListaDet> lGrilla2) {
		this.lGrilla2 = lGrilla2;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	@In(required = false)
	Usuario usuarioLogueado;

	@In(value = "entityManager")
	EntityManager em;
	@In
	StatusMessages statusMessages;
	private List<ListaDet> lGrilla1;
	private List<ListaDet> lGrilla2;
	private Long idPersona;

	public void init() {
		if (!precondInit())
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));

		cargarGrilla1();
		cargarGrilla2();
	}

	private boolean precondInit() {
		if (idPersona == null) {
			return false;
		}
		return true;
	}

	private void cargarGrilla1() {
		Query q =
			em.createQuery("select ListaDet from ListaDet ListaDet "
				+ " where ListaDet.tipo ='P' and ListaDet.postulacionCap.persona.idPersona =:idPersona ");
		q.setParameter("idPersona", idPersona);
		lGrilla1 = q.getResultList();
	}

	private void cargarGrilla2() {
		Query q =
			em.createQuery("select ListaDet from ListaDet ListaDet "
				+ " where ListaDet.tipo ='D' and ListaDet.postulacionCap.persona.idPersona =:idPersona ");
		q.setParameter("idPersona", idPersona);
		lGrilla2 = q.getResultList();
	}

	public String calcCalificacion(Long idPostulacionCap) {
		try {
			Query q =
				em.createQuery("select EvaluacionPart from EvaluacionPart EvaluacionPart "
					+ " where EvaluacionPart.postulacionCap.idPostulacion =:idPostulacion "
					+ " and EvaluacionPart.activo is true ");
			q.setParameter("idPostulacion", idPostulacionCap);
			List<EvaluacionPart> lista = q.getResultList();
			if (lista.size() > 0) {
				return (lista.get(0).getCalificacion() != null
					? lista.get(0).getCalificacion().toString() : null);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			
		}
		
	}

	public String calcEstado(String motivo) {
		if (motivo == null)
			return "BLOQUEADO";

		return "DESBLOQUEADO";
	}

	public void descargarDocumento(Long idDocumento) {
		try {
			AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDocumento, usuarioLogueado.getIdUsuario());
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, "No se pudo descargar el archivo");
			e.printStackTrace();
		}

	}

}
