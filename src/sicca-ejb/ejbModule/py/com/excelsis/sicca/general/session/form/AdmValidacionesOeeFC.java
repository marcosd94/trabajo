package py.com.excelsis.sicca.general.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
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

import py.com.excelsis.sicca.entity.DetValidacionOee;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admValidacionesOeeFC")
public class AdmValidacionesOeeFC implements Serializable {

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

	private Long idDetValidacionOee;

	private DetValidacionOee detValidacionOee = new DetValidacionOee();
	private List<SelectItem> listaGrupoSelectItems = new ArrayList<SelectItem>();

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idDetValidacionOee != null) {
			if (!sufc.validarInput(idDetValidacionOee.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			detValidacionOee = em.find(DetValidacionOee.class,
					idDetValidacionOee);
		}
	}

	public String updated() {
		if (!checkData())
			return null;
		detValidacionOee.setFechaMod(new Date());
		detValidacionOee.setUsuMod(usuarioLogueado.getCodigoUsuario());
		try {
			em.merge(detValidacionOee);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;

	}

	private boolean checkData() {
		if (detValidacionOee.getJustifEstado().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							"No se ingresó el dato correspondiente al campo obligatorio Justificación estado");
			return false;
		}
		if (seguridadUtilFormController.contieneCaracter(detValidacionOee
				.getJustifEstado().trim())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_caracter"));
			return false;
		}
		return true;

	}
	public Long getIdDetValidacionOee() {
		return idDetValidacionOee;
	}

	public void setIdDetValidacionOee(Long idDetValidacionOee) {
		this.idDetValidacionOee = idDetValidacionOee;
	}

	public DetValidacionOee getDetValidacionOee() {
		return detValidacionOee;
	}

	public void setDetValidacionOee(DetValidacionOee detValidacionOee) {
		this.detValidacionOee = detValidacionOee;
	}

	public List<SelectItem> getListaGrupoSelectItems() {
		return listaGrupoSelectItems;
	}

	public void setListaGrupoSelectItems(List<SelectItem> listaGrupoSelectItems) {
		this.listaGrupoSelectItems = listaGrupoSelectItems;
	}

}
