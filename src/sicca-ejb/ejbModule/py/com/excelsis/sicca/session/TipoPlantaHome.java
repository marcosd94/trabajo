package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

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

import py.com.excelsis.sicca.entity.TipoPlanta;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Name("tipoPlantaHome")
public class TipoPlantaHome extends EntityHome<TipoPlanta> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6660308627333211703L;

	@In
	FacesMessages facesMessages;

	@In(required = false)
	Usuario usuarioLogueado;

	@In
	StatusMessages statusMessages;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@Override
	protected TipoPlanta loadInstance() {
		TipoPlanta o = super.loadInstance();
		return o;
	}

	// Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "tipoPlantas";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
			CONTEXT_VAR_NAME + "SelectItems" };

	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME, scope = ScopeType.EVENT)
	public List<TipoPlanta> getTipoPlantas() {
		try {
			return getEntityManager().createQuery(
					" select o from " + TipoPlanta.class.getName()
							+ " o where activo = TRUE"
							+ " order by descripcion").getResultList();
		} catch (Exception ex) {
			return new Vector<TipoPlanta>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoPlantaSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SICCAAppHelper
				.getBundleMessage("opt_select_one")));
		for (TipoPlanta o : getTipoPlantas())
			si.add(new SelectItem(o.getIdTipoPlanta(), "" + o.getDescripcion()));
		return si;
	}

	public void setTipoPlantaIdTipoPlanta(Long id) {
		setId(id);
	}

	public Long getTipoPlantaIdTipoPlanta() {
		return (Long) getId();
	}

	@Override
	protected TipoPlanta createInstance() {
		TipoPlanta tipoPlanta = new TipoPlanta();
		return tipoPlanta;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if (getInstance().getIdTipoPlanta() == null)
			getInstance().setActivo(true);
	}

	public boolean isWired() {
		return true;
	}

	public TipoPlanta getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	@Override
	public String persist() {
		if (!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());
		getInstance().setDescripcion(
				getInstance().getDescripcion().trim().toUpperCase());
		return AppHelper.removeFromContext("persist", super.persist(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if (!checkData())
			return null;
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());
		getInstance().setDescripcion(
				getInstance().getDescripcion().trim().toUpperCase());
		return AppHelper.removeFromContext("updated", super.update(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation) {
		String hql = "SELECT o FROM TipoPlanta o WHERE lower(o.descripcion)=:descripcion ";
		if (operation.equalsIgnoreCase("update")) {
			hql += " AND o.idTipoPlanta != "
					+ getInstance().getIdTipoPlanta().longValue();
		}
		List<ValorNivelOrg> list = getEntityManager().createQuery(hql).setParameter("descripcion", getInstance().getDescripcion().toLowerCase())
				.getResultList();
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
					.getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}

	@Override
	public void setInstance(TipoPlanta instance) {
		if (instance != null) {
			super.setId(instance.getId());
		}
		super.setInstance(instance);
	}

	// Public getters and setters if exists

}
