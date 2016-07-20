package py.com.excelsis.sicca.general.session.form;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.CabValidacion;
import py.com.excelsis.sicca.entity.CabValidacionOee;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admGrupoValidacionesOeeFC")
public class AdmGrupoValidacionesOeeFC implements Serializable {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private Long idValidacionOee;
	private CabValidacionOee cabValidacionOee = new CabValidacionOee();

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idValidacionOee != null) {
			if (!sufc.validarInput(idValidacionOee.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			cabValidacionOee = em.find(CabValidacionOee.class, idValidacionOee);
		}
	}

	public String updated() {
		if (!checkData())
			return null;
		try {
			em.persist(cabValidacionOee);
			em.flush();
		} catch (Exception e) {
			return null;
		}

		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return "updated";
	}
	
	private boolean checkData() {
		if (cabValidacionOee.getJustifEstado().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							"No se ingresó el dato correspondiente al campo obligatorio Justificación estado");
			return false;
		}
		if (seguridadUtilFormController.contieneCaracter(cabValidacionOee
				.getJustifEstado().trim())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle
					.getBundle().getString("msg_caracter"));
			return false;
		}
		return true;
	}

	public Long getIdValidacionOee() {
		return idValidacionOee;
	}

	public void setIdValidacionOee(Long idValidacionOee) {
		this.idValidacionOee = idValidacionOee;
	}

	public CabValidacionOee getCabValidacionOee() {
		return cabValidacionOee;
	}

	public void setCabValidacionOee(CabValidacionOee cabValidacionOee) {
		this.cabValidacionOee = cabValidacionOee;
	}

}
