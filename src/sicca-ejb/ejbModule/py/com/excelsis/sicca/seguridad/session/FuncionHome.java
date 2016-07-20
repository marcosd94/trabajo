package py.com.excelsis.sicca.seguridad.session;

import py.com.excelsis.sicca.seguridad.entity.Funcion;
import py.com.excelsis.sicca.seguridad.entity.RolFuncion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.*;

import java.util.ArrayList;
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

@Name("funcionHome")
public class FuncionHome extends EntityHome<Funcion> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@In
	FacesMessages facesMessages;

	@In(required = false)
	Usuario user;

	@In
	StatusMessages statusMessages;

	private Long idModulo;
	
	private List<SelectItem> funcionesPorModuloSelectItems = new ArrayList<SelectItem>();

	// Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "funciones";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
			CONTEXT_VAR_NAME + "SelectItems" };

	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME, scope = ScopeType.EVENT)
	public List<Funcion> getFunciones() {
		try {
			return getEntityManager().createQuery(
					" select o from " + Funcion.class.getName()
							+ " o order by idFuncion").getResultList();
		} catch (Exception ex) {
			return new Vector<Funcion>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getFuncionesSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		for (Funcion o : getFunciones())
			si.add(new SelectItem(o.getIdFuncion(), "" + o.getDescripcion()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<Funcion> getFuncionesByModulo() {
		try {
			return getEntityManager()
					.createQuery(
							" select o from "
									+ Funcion.class.getName()
									+ " o where o.modulo = 'SELECCION' order by o.descripcion ")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<Funcion>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByModuloSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getFuncionesByModuloSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (Funcion o : getFuncionesByModulo())
			si.add(new SelectItem(o.getIdFuncion(), "" + o.getDescripcion()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<Funcion> getPaginasByModulo() {
		try {
			if (idModulo != null)
				return getEntityManager()
						.createQuery(
								" select o from "
										+ Funcion.class.getName()
										+ " o where o.adjuntable is true and o.modulo = '"
										+ getEntityManager().find(
												Funcion.class, idModulo)
												.getModulo()
										+ "' order by o.descripcion ")
						.getResultList();
			return new ArrayList<Funcion>();
		} catch (Exception ex) {
			return new Vector<Funcion>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "PaginasByModuloSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getPaginasByModuloSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (Funcion o : getPaginasByModulo())
			si.add(new SelectItem(o.getIdFuncion(), "" + o.getDescripcion()));
		return si;
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByModuloPorPaginasSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getModuloPorPaginasSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		List<Funcion> lista = new ArrayList<Funcion>();
		for (Funcion o : getByModuloPorPaginas()) {
			if (!esta(o.getModulo(), lista)) {
				lista.add(o);
				si.add(new SelectItem(o.getIdFuncion(), "" + o.getModulo()));
			}
		}
		updateSelectItems();
		return si;
	}
	
	public void updateSelectItems(){
		funcionesPorModuloSelectItems = new ArrayList<SelectItem>();
		funcionesPorModuloSelectItems.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		
		for (Funcion o : getPaginasByModulo())
			funcionesPorModuloSelectItems.add(new SelectItem(o.getIdFuncion(), "" + o.getDescripcion()));
	}

	private Boolean esta(String modulo, List<Funcion> si) {
		for (Funcion s : si) {
			if (s.getModulo().equalsIgnoreCase(modulo))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<Funcion> getByModuloPorPaginas() {
		try {
			return getEntityManager()
					.createQuery(
							" select distinct o from "
									+ Funcion.class.getName()
									+ " o where o.adjuntable is true order by o.modulo ")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<Funcion>();
		}
	}

	public void setFuncionIdFuncion(Long id) {
		setId(id);
	}

	public Long getFuncionIdFuncion() {
		return (Long) getId();
	}
	
	public void nada(){
		getPaginasByModulo();
	}

	@Override
	protected Funcion createInstance() {
		Funcion funcion = new Funcion();
		return funcion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Funcion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<RolFuncion> getRolFuncions() {
		return getInstance() == null ? null : new ArrayList<RolFuncion>(
				getInstance().getRolFuncions());
	}

	@Override
	public void setInstance(Funcion instance) {
		if (instance != null) {
			super.setId(instance.getId());
		}
		super.setInstance(instance);
	}

	public Long getIdModulo() {
		return idModulo;
	}

	public void setIdModulo(Long idModulo) {
		this.idModulo = idModulo;
	}

	public List<SelectItem> getFuncionesPorModuloSelectItems() {
		return funcionesPorModuloSelectItems;
	}

	public void setFuncionesPorModuloSelectItems(
			List<SelectItem> funcionesPorModuloSelectItems) {
		this.funcionesPorModuloSelectItems = funcionesPorModuloSelectItems;
	}
	
	

}
