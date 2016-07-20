package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizReferencial;
import py.com.excelsis.sicca.entity.MatrizReferencialEnc;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;

@Name("matrizReferencialHome")
public class MatrizReferencialHome extends EntityHome<MatrizReferencial> {

	@In
	StatusMessages statusMessages;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In(create = true)
	MatrizReferencialEncHome matrizReferencialEncHome;

	private Long idDatosEspecificos;
	public static final String CONTEXT_VAR_NAME = "matrizReferencial";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
		CONTEXT_VAR_NAME + "SelectItems" };

	@Override
	protected MatrizReferencial loadInstance() {
		MatrizReferencial o = super.loadInstance();
		this.idDatosEspecificos = o.getDatosEspecificos().getIdDatosEspecificos();
		return o;
	}

	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME, scope = ScopeType.EVENT)
	public List<MatrizReferencial> getMatrizReferencial() {
		try {
			return getEntityManager().createQuery(" select o from "
				+ MatrizReferencial.class.getName() + " o"
				+ " WHERE o.activo = true ORDER BY o.descripcion").getResultList();
		} catch (Exception ex) {
			return new Vector<MatrizReferencial>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDepartamentoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (MatrizReferencial o : getMatrizReferencial())
			si.add(new SelectItem(o.getIdMatrizReferencial(), "" + o.getDescripcion()));
		return si;
	}

	public void setMatrizReferencialIdMatrizReferencial(Long id) {
		setId(id);
	}

	public Long getMatrizReferencialIdMatrizReferencial() {
		return (Long) getId();
	}

	@Override
	protected MatrizReferencial createInstance() {
		MatrizReferencial matrizReferencial = new MatrizReferencial();
		return matrizReferencial;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DatosEspecificos datosEspecificos = datosEspecificosHome.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
		if (!isIdDefined())
			getInstance().setActivo(Estado.ACTIVO.getValor());
	}

	public boolean isWired() {
		if (getInstance().getDatosEspecificos() == null)
			return false;
		return true;
	}

	public MatrizReferencial getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<MatrizReferencialEnc> getMatrizReferencialEncs() {
		return getInstance() == null ? null
			: new ArrayList<MatrizReferencialEnc>(getInstance().getMatrizReferencialEncs());
	}

	@Override
	public String persist() {
		if (!checkData())
			return null;

		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		getInstance().setDatosEspecificos(getEntityManager().find(DatosEspecificos.class, this.idDatosEspecificos));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if (!checkData())
			return null;

		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		getInstance().setDatosEspecificos(getEntityManager().find(DatosEspecificos.class, this.idDatosEspecificos));
		if (!getInstance().getActivo()) {
			inactivarDependientes();
		} else {
			activarDependientes();
		}
		String respuesta =
			AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

		return respuesta;
	}

	// METODOS PRIVADOS
	private void inactivarDependientes() {
		List<MatrizReferencialEnc> listDetails =
			new ArrayList<MatrizReferencialEnc>(getInstance().getMatrizReferencialEncs());
		for (MatrizReferencialEnc detail : listDetails) {
			detail.setActivo(Estado.INACTIVO.getValor());
			matrizReferencialEncHome.setInstance(detail);
			matrizReferencialEncHome.update();
			matrizReferencialEncHome.clearInstance();
		}
	}

	// METODOS PRIVADOS
	private void activarDependientes() {
		List<MatrizReferencialEnc> listDetails =
			new ArrayList<MatrizReferencialEnc>(getInstance().getMatrizReferencialEncs());
		for (MatrizReferencialEnc detail : listDetails) {
			detail.setActivo(Estado.ACTIVO.getValor());
			matrizReferencialEncHome.setInstance(detail);
			matrizReferencialEncHome.update();
			matrizReferencialEncHome.clearInstance();
		}
	}

	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation) {
		String hql =
			"SELECT o FROM MatrizReferencial o WHERE lower(o.descripcion) = :desc";
		hql += " AND o.datosEspecificos.idDatosEspecificos = " + idDatosEspecificos;
		if (operation.equalsIgnoreCase("update")) {
			hql +=
				" AND o.idMatrizReferencial != "
					+ getInstance().getIdMatrizReferencial().longValue();
		}
		List<CondicionTrabajoEspecif> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase() ).getResultList();
		return list.isEmpty();
	}

	private boolean checkData() {
		String operation = isIdDefined() ? "update" : "persist";
		if (getInstance().getDescripcion().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if (getInstance().getPuntajeMaximo() == null
			|| getInstance().getPuntajeMaximo().intValue() <= 0) {
			statusMessages.add("El Máximo NO puede ser menor o igual a 0");
			return false;
		}
		if (!checkDuplicate(operation)) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}

	public Long getIdDatosEspecificos() {
		return idDatosEspecificos;
	}

	public void setIdDatosEspecificos(Long idDatosEspecificos) {
		this.idDatosEspecificos = idDatosEspecificos;
	}

}
