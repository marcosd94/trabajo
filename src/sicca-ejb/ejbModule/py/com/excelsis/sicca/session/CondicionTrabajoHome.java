package py.com.excelsis.sicca.session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.PersistenceException;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ClasificadorUo;
import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.EntityInterface;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import java.util.LinkedList;

@Name("condicionTrabajoHome")
public class CondicionTrabajoHome extends EntityHome<CondicionTrabajo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1639800467177826857L;

	@In
	FacesMessages facesMessages;

	@In(required = false)
	Usuario usuarioLogueado;

	@In
	StatusMessages statusMessages;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@Override
	protected CondicionTrabajo loadInstance() {
		CondicionTrabajo o = super.loadInstance();
		return o;
	}

	// Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "condicionTrabajos";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
			CONTEXT_VAR_NAME + "SelectItems" };

	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME, scope = ScopeType.EVENT)
	public List<CondicionTrabajo> getCondicionTrabajos() {
		try {
			return getEntityManager().createQuery(
					" select o from " + CondicionTrabajo.class.getName()
							+ " o where activo = TRUE order by descripcion")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<CondicionTrabajo>();
		}
	}

	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME + "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getCondicionTrabajoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "SELECCIONAR..."));
		for (CondicionTrabajo o : getCondicionTrabajos())
			si.add(new SelectItem(o.getIdCondicionTrabajo(), ""
					+ o.getDescripcion()));
		return si;
	}

	public void setCondicionTrabajoIdCondicionTrabajo(Long id) {
		setId(id);
	}

	public Long getCondicionTrabajoIdCondicionTrabajo() {
		return (Long) getId();
	}

	@Override
	protected CondicionTrabajo createInstance() {
		CondicionTrabajo condicionTrabajo = new CondicionTrabajo();
		return condicionTrabajo;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if (getInstance().getIdCondicionTrabajo() == null)
			getInstance().setActivo(true);
	}

	public boolean isWired() {
		return true;
	}

	public CondicionTrabajo getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	@Override
	public String persist() {
		if (!checkData())
			return null;
		getInstance().setDescripcion(
				getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());

		return AppHelper.removeFromContext("persist", super.persist(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if (!checkData())
			return null;
		getInstance().setDescripcion(
				getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());
		return AppHelper.removeFromContext("updated", super.update(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation) {
		String hql = "SELECT o FROM CondicionTrabajo o WHERE lower(o.descripcion) =:descripcion ";
		if (operation.equalsIgnoreCase("update")) {
			hql += " AND o.idCondicionTrabajo != "
					+ getInstance().getIdCondicionTrabajo().longValue();
		}
		List<CondicionTrabajo> list = getEntityManager()
				.createQuery(hql)
				.setParameter("descripcion",
						getInstance().getDescripcion().toLowerCase())
				.getResultList();
		return list.isEmpty();
	}
	@SuppressWarnings("unchecked")
	private boolean checkDuplicateOrden(String operation) {
		String hql = "SELECT o FROM CondicionTrabajo o WHERE o.orden = "+getInstance().getOrden();
		if (operation.equalsIgnoreCase("update")) {
			hql += " AND o.idCondicionTrabajo != "
					+ getInstance().getIdCondicionTrabajo().longValue();
		}
		List<CondicionTrabajo> list = getEntityManager()
				.createQuery(hql).getResultList();
		return list.isEmpty();
	}

	private boolean checkData() {
		String operation = isIdDefined() ? "update" : "persist";
		if (getInstance().getDescripcion().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_descripcion_invalida"));
			return false;
		}
		if (seguridadUtilFormController.contieneCaracter(getInstance()
				.getDescripcion().trim())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_caracter"));
			return false;
		}
		if (!checkDuplicate(operation)) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_registro_inactivo"));
			return false;
		}
		if (!checkDuplicateOrden(operation)) {
			statusMessages.add(Severity.ERROR, "Ya existe un registro con el mismo número de orden");
			return false;
		}
		if(getInstance().getOrden() < 0){
			statusMessages.add(Severity.ERROR, "El orden debe ser mayor a cero");
			return false;
		}
		return true;
	}

	@Override
	public void setInstance(CondicionTrabajo instance) {
		if (instance != null) {
			super.setId(instance.getId());
		}
		super.setInstance(instance);
	}

	// Public getters and setters if exists

}
