package py.com.excelsis.sicca.general.session.form;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TipoEvaluacion;
import enums.TipoValidacion;

import py.com.excelsis.sicca.entity.CabValidacion;
import py.com.excelsis.sicca.entity.DetValidacion;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.general.session.DetValidacionHome;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admValidacionesFC")
public class AdmValidacionesFC implements Serializable{
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	DetValidacionHome detValidacionHome;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private DetValidacion detValidacion = new DetValidacion();
	private Long idGrupoVal;
	private TipoValidacion tipoValidacion;

	public void init() {
		if (detValidacionHome.isIdDefined()) {
			detValidacion = detValidacionHome.getInstance();
			idGrupoVal = detValidacion.getCabValidacion().getIdCabValidacion();
			if(detValidacion.getTipo().equalsIgnoreCase("PERSONA"))
				tipoValidacion = TipoValidacion.PERSONA;
			else
				tipoValidacion = TipoValidacion.PUESTO;
		} else {
			detValidacion.setActivo(true);
			detValidacion.setBloquea(true);
		}
	}

	public String save() {
		if (!checkData())
			return null;
		detValidacion.setCabValidacion(em.find(CabValidacion.class, idGrupoVal));
		detValidacion.setTipo(tipoValidacion.getValor());
		detValidacionHome.setInstance(detValidacion);

		if (detValidacionHome.persist().equalsIgnoreCase("persisted")) {
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		}
		return null;

	}

	public String updated() {
		if (!checkData())
			return null;
		detValidacion.setCabValidacion(em.find(CabValidacion.class, idGrupoVal));
		detValidacion.setTipo(tipoValidacion.getValor());
		detValidacionHome.setInstance(detValidacion);

		if (detValidacionHome.update().equalsIgnoreCase("updated")) {
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		}
		return null;

	}
	private boolean checkData() {
		String operation = detValidacionHome.isIdDefined() ? "update"
				: "persist";
		if (operation.equalsIgnoreCase("persist")) {
			if (detValidacion.getNombreValidacion().trim().isEmpty()) {
				statusMessages
						.add(Severity.ERROR,
								"No se ingresó el dato correspondiente al campo obligatorio Nombre");
				return false;
			}
			if (seguridadUtilFormController.contieneCaracter(detValidacion
					.getNombreValidacion().trim())) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("msg_caracter"));
				return false;
			}
			if(tipoValidacion == null || tipoValidacion.getId() == null){
				statusMessages
				.add(Severity.ERROR,
						"No se ingresó el dato correspondiente al campo obligatorio Tipo");
		return false;
			}

		}
		if (detValidacion.getExplicValidacion().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							"No se ingresó el dato correspondiente al campo obligatorio Explicación breve");
			return false;
		}
		if (seguridadUtilFormController.contieneCaracter(detValidacion
				.getExplicValidacion().trim())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_caracter"));
			return false;
		}
		
		
		if (operation.equalsIgnoreCase("update")
				&& (detValidacion.getJustifEstado() == null || detValidacion
						.getJustifEstado().trim().isEmpty())) {
			statusMessages
					.add(Severity.ERROR,
							"No se ingresó el dato correspondiente al campo obligatorio Justificación estado");
			return false;

		}
		return true;
	}

	

	public DetValidacion getDetValidacion() {
		return detValidacion;
	}

	public void setDetValidacion(DetValidacion detValidacion) {
		this.detValidacion = detValidacion;
	}

	public Long getIdGrupoVal() {
		return idGrupoVal;
	}

	public void setIdGrupoVal(Long idGrupoVal) {
		this.idGrupoVal = idGrupoVal;
	}

	public TipoValidacion getTipoValidacion() {
		return tipoValidacion;
	}

	public void setTipoValidacion(TipoValidacion tipoValidacion) {
		this.tipoValidacion = tipoValidacion;
	}

	
}
