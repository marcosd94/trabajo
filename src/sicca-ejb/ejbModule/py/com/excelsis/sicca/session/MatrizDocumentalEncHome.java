package py.com.excelsis.sicca.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import py.com.excelsis.sicca.dto.DetalleMatrizDocumentalDTO;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizDocumentalEnc;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;

@Name("matrizDocumentalEncHome")
public class MatrizDocumentalEncHome extends EntityHome<MatrizDocumentalEnc> {

	@In
	StatusMessages statusMessages;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	@Override
	protected MatrizDocumentalEnc loadInstance() {
		MatrizDocumentalEnc o = super.loadInstance();
		this.idDatosEspecificos = o.getDatosEspecificos()
				.getIdDatosEspecificos();
		return o;
	}

	// Value holders for selectItems if exists
	private Long idDatosEspecificos;
	public static final String CONTEXT_VAR_NAME = "matrizDocumentalEnc";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
			CONTEXT_VAR_NAME + "SelectItems" };

	public void setMatrizDocumentalEncIdMatrizDocumentalEnc(Long id) {
		setId(id);
	}

	public Long getMatrizDocumentalEncIdMatrizDocumentalEnc() {
		return (Long) getId();
	}

	@Override
	protected MatrizDocumentalEnc createInstance() {
		MatrizDocumentalEnc matrizDocumentalEnc = new MatrizDocumentalEnc();
		return matrizDocumentalEnc;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
		if (!isIdDefined()) {
			getInstance().setActivo(Estado.ACTIVO.getValor());
		}
	}

	public boolean isWired() {
		return true;
	}

	public MatrizDocumentalEnc getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	@Override
	public String persist() {
		if (!checkData())
			return null;

		getInstance().setDatosEspecificos(
				getEntityManager().find(DatosEspecificos.class,
						this.idDatosEspecificos));
		getInstance().setDescripcion(
				getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(
				usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		return AppHelper.removeFromContext("persist", super.persist(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if (!checkData())
			return null;

		getInstance().setDatosEspecificos(
				getEntityManager().find(DatosEspecificos.class,
						this.idDatosEspecificos));
		getInstance().setDescripcion(
				getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(
				usuarioLogueado.getCodigoUsuario().trim().toUpperCase());

		List<MatrizDocumentalDet> lista = new ArrayList<MatrizDocumentalDet>();
		lista = getInstance().getMatrizDocumentalDets();
		if (lista != null && lista.size() > 0) {
			for (MatrizDocumentalDet l : lista) {
				l.setActivo(getInstance().getActivo());
				l.setFechaMod(new Date());
				l.setUsuMod(usuarioLogueado.getCodigoUsuario());
				getEntityManager().merge(l);
				getEntityManager().flush();
			}
		}

		return AppHelper.removeFromContext("updated", super.update(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	public String save() {
		if (!isIdDefined())

			return persist();

		return update();

	}

	// METODOS PRIVADOS
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation) {
		String hql = "SELECT o FROM MatrizDocumentalEnc o WHERE lower(o.descripcion) =:desc";
		hql += " AND o.datosEspecificos.idDatosEspecificos = "
				+ idDatosEspecificos;
		if (operation.equalsIgnoreCase("update")) {
			hql += " AND o.idMatrizDocumentalEnc != "
					+ getInstance().getIdMatrizDocumentalEnc().longValue();
		}
		List<MatrizDocumentalEnc> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase())
				.getResultList();
		return list.isEmpty();
	}

	private boolean checkData() {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		String operation = isIdDefined() ? "update" : "persist";
		if (getInstance().getDescripcion().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_descripcion_invalida"));
			return false;
		}
		if(sufc.contieneCaracter(getInstance().getDescripcion().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if (!checkDuplicate(operation)) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}

	// GETTERS Y SETTERS
	public void setIdDatosEspecificos(Long idDatosEspecificos) {
		this.idDatosEspecificos = idDatosEspecificos;
	}

	public Long getIdDatosEspecificos() {
		return idDatosEspecificos;
	}

}
