package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.Modulo;

import py.com.excelsis.sicca.seguridad.entity.Funcion;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.session.RolHome;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.seguridad.entity.RolFuncion;
import py.com.excelsis.sicca.session.util.FuncionarioUtilFormController;

@Name("administrarRolesFormController")
@Scope(ScopeType.CONVERSATION)
public class AdministrarRolesFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1633156528442875081L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	RolHome rolHome;
	
	@In(create = true)
	FuncionarioUtilFormController funcionarioUtilFormController;

	private Rol rol;
	private Modulo modulo;
	private Boolean activo;

	private List<Long> funciones = new ArrayList<Long>();
	private List<Long> funcionesAux = new ArrayList<Long>();

	private List<Funcion> funcionesSinAsignar = new ArrayList<Funcion>();
	private List<Funcion> funcionesAsignados = new ArrayList<Funcion>();
	private List<Funcion> funcionesAsignadosAux = new ArrayList<Funcion>();
	private List<RolFuncion> rolFuncionList = new ArrayList<RolFuncion>();
	private List<RolFuncion> rolFuncionListVer = new ArrayList<RolFuncion>();
	
	private String funcionSinAsignar;

	public void init() {
		rol = new Rol();
		rol = rolHome.getInstance();
		if (rolHome.isIdDefined()) {
			activo = rol.getActivo();
			funcionesAsignadas(rol.getIdRol());

		} else {
			activo = true;
			modulo = null;
		}
	}

	public void traerDatos(){
		List<Funcion> lista1  = new ArrayList<Funcion>();
		lista1 = traerFunciones();
		if(rolHome.isIdDefined()){
			List<RolFuncion> lista2  = new ArrayList<RolFuncion>();
			lista2 = traerFuncionesAsignadas(rolHome.getInstance().getIdRol());
			funcionesAsignados = new ArrayList<Funcion>();
			funcionesAsignadosAux = new ArrayList<Funcion>();
			funcionesSinAsignar = new ArrayList<Funcion>();
			for (RolFuncion o : lista2) {
				funcionesAsignados.add(o.getFuncion());
				funcionesAsignadosAux.add(o.getFuncion());
			}
			for (Funcion f : lista1) {
				Boolean esta = false;
				for (RolFuncion o : lista2) {
					if(f.getIdFuncion().equals(o.getFuncion().getIdFuncion()))
						esta = true;
				}
				if(!esta)
					funcionesSinAsignar.add(f);
				
			}
			
		}
		else{
			funcionesSinAsignar = new ArrayList<Funcion>();
			funcionesSinAsignar = traerFunciones();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private List<Funcion> traerFunciones() {
		try {
			return em.createQuery(
					" select o from " + Funcion.class.getName() + " o "
							+ "where modulo = '" + modulo.getDescripcion()
							+ "' order by descripcion").getResultList();
		} catch (Exception ex) {
			return new Vector<Funcion>();
		}
	}
	
	public void storeItemInList() {
		;
	}

	
	
	@SuppressWarnings("unchecked")
	private List<Funcion> traerFuncionesByDescripcion(String idFuncionesAsignadas) {
		try {
			
			String consulta = " select o from " + Funcion.class.getName() + " o "
			+ "where modulo = '" + modulo.getDescripcion() + "'";
			
			if (!funcionarioUtilFormController.vacio(funcionSinAsignar))
				consulta += " and upper(descripcion) like '%" + funcionSinAsignar.toUpperCase() + "%'";
			
			if (!funcionarioUtilFormController.vacio(idFuncionesAsignadas))
				consulta += " and id_funcion not in (" + idFuncionesAsignadas  + ")";
			
			consulta += " order by descripcion";

			return em.createQuery(consulta).getResultList();
		} catch (Exception ex) {
			return new Vector<Funcion>();
		}
	}
	
	public void actualizarFuncionSinAsignar(){
		funcionesSinAsignar = new ArrayList<Funcion>();
		String idFuncionesAsignadas = "";
		if (funcionesAsignados != null && funcionesAsignados.size() > 0){
			for(Funcion funcion: funcionesAsignados){
				idFuncionesAsignadas += ", " + funcion.getId();
			}
			idFuncionesAsignadas = idFuncionesAsignadas.replaceFirst(",", "");
		}
		funcionesSinAsignar = traerFuncionesByDescripcion(idFuncionesAsignadas);
	}
	

	@SuppressWarnings("unchecked")
	private List<RolFuncion> traerFuncionesAsignadas(Long idRol) {
		String cadena = "select rf.* from seguridad.rol_funcion rf"
			+" join seguridad.funcion f on f.id_funcion = rf.id_funcion"
				+ " where rf.id_rol = " + idRol
				+ " and f.modulo = '"+modulo+"' order by f.descripcion" ;

		List<RolFuncion> lista = new ArrayList<RolFuncion>();
		lista = em.createNativeQuery(cadena, RolFuncion.class).getResultList();
		
		rolFuncionList = new ArrayList<RolFuncion>();
		if (lista.size() > 0)
			rolFuncionList.addAll(lista);
		return lista;
	}

	@SuppressWarnings("unchecked")
	private void funcionesAsignadas(Long idRol) {
		String cadena = "select rf.* from seguridad.rol_funcion rf"
			+" join seguridad.funcion f on f.id_funcion = rf.id_funcion"
				+ " where rf.id_rol = " + idRol+
				" order by f.modulo, f.descripcion";

		rolFuncionListVer = new ArrayList<RolFuncion>();
		rolFuncionListVer = em.createNativeQuery(cadena, RolFuncion.class).getResultList();
	
		
	}
	
	public void buscarFuncionesSinAsignar(){
		funcionesSinAsignar = new ArrayList<Funcion>();
		if (modulo != null) {
			for (Funcion o : traerFunciones())
				funcionesSinAsignar.add(o);
		}
	}

	/**
	 * Método que setea todos los datos necesarios para luego guardarlos.
	 * 
	 * @return
	 */
	public String guardar() {
		try {
			rol.setDescripcion(rol.getDescripcion().trim().toUpperCase());
			rol.setActivo(activo);
			rolHome.setInstance(rol);

			String result = rolHome.persist();
			if (result.equals("persisted")) {
				try {
					for (Funcion o : funcionesAsignados) {
						RolFuncion rolFuncion = new RolFuncion();

						rolFuncion.setRol(rolHome.getInstance());
						rolFuncion.setFuncion(o);
						em.persist(rolFuncion);
						em.flush();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return result;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}

		return null;
	}

	/**
	 * Método que setea todos los datos necesarios para luego guardarlos.
	 * 
	 * @return
	 */
	public String actualizar() {
		try {
			rol.setActivo(activo);
			rol.setDescripcion(rol.getDescripcion().trim().toUpperCase());
			rolHome.setInstance(rol);

			String result = rolHome.update();
			if (result.equals("updated")) {
				for (Funcion o : funcionesAsignados) {
					Long id = o.getIdFuncion();
					Boolean esta = false;
					for (Funcion ax : funcionesAsignadosAux) {
						Long idAux = ax.getIdFuncion();
						if (id.equals(idAux))
							esta = true;
					}
					if (!esta) {
						RolFuncion rolFuncion = new RolFuncion();

						rolFuncion.setRol(rolHome.getInstance());
						rolFuncion.setFuncion(o);
						em.persist(rolFuncion);
						em.flush();
					}
				}
				
				for (Funcion ax : funcionesAsignadosAux) {
					Long idAux = ax.getIdFuncion();
					Boolean esta = false;
					for (Funcion o : funcionesAsignados) {
						Long idFunciones = o.getIdFuncion();
						if (idAux.equals(idFunciones))
							esta = true;
						
					}
					if (!esta) {

						List<RolFuncion> listaRolFuncion = traerFuncionesAsignadas(
								rol.getIdRol(), ax.getIdFuncion());
						for (RolFuncion rolF : listaRolFuncion) {
							em.remove(rolF);
							em.flush();
						}
					}
				}
			}
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return result;

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private List<RolFuncion> traerFuncionesAsignadas(Long idRol, Long idFuncion) {
		String cadena = "select rf.* from seguridad.rol_funcion rf"
				+ " where rf.id_rol = " + idRol + " and rf.id_funcion = "
				+ idFuncion;

		List<RolFuncion> lista = new ArrayList<RolFuncion>();
		lista = em.createNativeQuery(cadena, RolFuncion.class).getResultList();
		;

		return lista;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public List<Long> getFunciones() {
		return funciones;
	}

	public void setFunciones(List<Long> funciones) {
		this.funciones = funciones;
	}

	public List<Long> getFuncionesAux() {
		return funcionesAux;
	}

	public void setFuncionesAux(List<Long> funcionesAux) {
		this.funcionesAux = funcionesAux;
	}

	public List<RolFuncion> getRolFuncionList() {
		return rolFuncionList;
	}

	public void setRolFuncionList(List<RolFuncion> rolFuncionList) {
		this.rolFuncionList = rolFuncionList;
	}

	public List<Funcion> getFuncionesSinAsignar() {
		return funcionesSinAsignar;
	}

	public void setFuncionesSinAsignar(List<Funcion> funcionesSinAsignar) {
		this.funcionesSinAsignar = funcionesSinAsignar;
	}

	public List<Funcion> getFuncionesAsignados() {
		return funcionesAsignados;
	}

	public void setFuncionesAsignados(List<Funcion> funcionesAsignados) {
		this.funcionesAsignados = funcionesAsignados;
	}

	public List<Funcion> getFuncionesAsignadosAux() {
		return funcionesAsignadosAux;
	}

	public void setFuncionesAsignadosAux(List<Funcion> funcionesAsignadosAux) {
		this.funcionesAsignadosAux = funcionesAsignadosAux;
	}

	public List<RolFuncion> getRolFuncionListVer() {
		return rolFuncionListVer;
	}

	public void setRolFuncionListVer(List<RolFuncion> rolFuncionListVer) {
		this.rolFuncionListVer = rolFuncionListVer;
	}

	public void setFuncionSinAsignar(String funcionSinAsignar) {
		this.funcionSinAsignar = funcionSinAsignar;
	}

	public String getFuncionSinAsignar() {
		return funcionSinAsignar;
	}

	
	
}
