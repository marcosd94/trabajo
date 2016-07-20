package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import enums.UnidadPlazo;
import py.com.excelsis.sicca.entity.Actividad;
import py.com.excelsis.sicca.entity.ActividadProceso;
import py.com.excelsis.sicca.entity.ProcActividadRol;
import py.com.excelsis.sicca.entity.Proceso;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.ActividadProcesoHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("actividadProcesoFormController")
@Scope(ScopeType.CONVERSATION)
public class ActividadProcesoFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2881610119430601586L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	ActividadProcesoHome actividadProcesoHome;

	private Long idActividadProceso;
	private List<ActividadProceso> actividadProcesoList = new ArrayList<ActividadProceso>();
	private Long idProceso;
	private Long idActividad;
	private BigDecimal plazoActividad;
	private String unidadPlazo = UnidadPlazo.DIA.getValor();;
	private String observacion;
	private Boolean activo=false;

	private boolean esEdit=false;
	private boolean vista=false;
	private boolean actividadRepetida = false;
	
	//private List<Rol> roles = new ArrayList<Rol>();
	private List<Rol> rolesAsignados = new ArrayList<Rol>();
	private List<Rol> rolesSinAsignar = new ArrayList<Rol>();
	private List<ActividadProceso>  actividadProcesoListCb= new ArrayList<ActividadProceso>();

	public void init() {
			
		clrVar();
		if(idActividadProceso != null){
			//EDIT
			ActividadProceso actividadProceso = em.find(ActividadProceso.class, idActividadProceso);
			actividadProcesoHome.setInstance(actividadProceso);
			idProceso = actividadProceso.getProceso().getId();
			idActividad = actividadProceso.getActividad().getId();
			plazoActividad = actividadProceso.getPlazoActividad();
			observacion = actividadProceso.getObservacion();
			activo = actividadProceso.isActivo();
			esEdit=true;
			changeProceso(idProceso);
			
//			UnidadPlazo u = UnidadPlazo.getPorValor(actividadProceso.getUnidadPlazo());
//			unidadPlazo = u.getId().toString();
			unidadPlazo = UnidadPlazo.DIA.getValor();
		}else{
			//NEW
			activo = true;
			esEdit=false;
		}
	}
	
	public void initRol() {
		clrVar();
		//roles = traerRoles();
		ActividadProceso actividadProceso = em.find(ActividadProceso.class, idActividadProceso);
		actividadProcesoHome.setInstance(actividadProceso);
		idProceso = actividadProceso.getProceso().getId();
		idActividad = actividadProceso.getActividad().getId();
		rolesAsignados = traerRolesAsignados();
		rolesSinAsignar = traerRolesSinAsignar();
	}
	
	
	private void clrVar(){
		idProceso = null;
		idActividad = null;
		plazoActividad = null;
		//unidadPlazo = null;
		observacion = null;
		activo = null;
		actividadRepetida = false;
	}


	public String save() {
		try {
			if(isNew()){
				//nuevo. Se guardan todos los detalles
				for(ActividadProceso actividadProceso : actividadProcesoList){
					actividadProceso.setFechaAlta(new Date()); //usuarioLogueado
					actividadProceso.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					actividadProceso.setUnidadPlazo(unidadPlazo);
					em.persist(actividadProceso);
				}
			}
			else{
				//editar
				ActividadProceso actividadProceso = em.find(ActividadProceso.class, idActividadProceso);
				Proceso proceso = em.find(Proceso.class, idProceso);
				Actividad actividad = em.find(Actividad.class, idActividad);
				actividadProceso.setProceso(proceso);
				actividadProceso.setActividad(actividad);
				actividadProceso.setPlazoActividad(plazoActividad);
				
				if(observacion != null)
					observacion = observacion.toUpperCase();
				
				actividadProceso.setObservacion(observacion);
				actividadProceso.setActivo(activo);
				
				//enums.UnidadPlazo u = UnidadPlazo.getById(unidadPlazo);
				//actividadProceso.setUnidadPlazo(unidadPlazo);
				
				actividadProceso.setFechaMod(new Date()); //usuarioLogueado
				actividadProceso.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(actividadProceso);
			}

			em.flush();
			clrVar();
			actividadProcesoList = new ArrayList<ActividadProceso>();
			
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
		String mens = "Registro creado con exito";
		statusMessages.add(Severity.INFO, mens);
		return "persisted";
	}
	

	public void cancelar(){
		clrVar();
		actividadProcesoList = new ArrayList<ActividadProceso>();
	}
	
	
	public void agregar() {
		actividadRepetida = false;
		//Se verifica que no sea repetida
		if(actividadRepetida()){
			actividadRepetida = true;
			return;
		}
		
		//Se agrega la actividad
		ActividadProceso actividadProceso = new ActividadProceso();
		Proceso proceso = em.find(Proceso.class, idProceso);
		Actividad actividad = em.find(Actividad.class, idActividad);
		actividadProceso.setProceso(proceso);
		actividadProceso.setActividad(actividad);
		actividadProceso.setPlazoActividad(plazoActividad);
		
		actividadProceso.setObservacion(observacion);
		actividadProceso.setActivo(false);
		
		//enums.UnidadPlazo u = UnidadPlazo.getById(unidadPlazo);
		actividadProceso.setUnidadPlazo(unidadPlazo);
		
		actividadProcesoList.add(actividadProceso);
	}
	
	private boolean actividadRepetida() {
		//verificar en memoria
		for(ActividadProceso a : actividadProcesoList){
			if(a.getProceso().getId().longValue() == idProceso.longValue() && a.getActividad().getId().longValue() == idActividad.longValue()){
				return true;
			}
		}
		
		//verificar en la bd
		String query = " select actividadProceso from ActividadProceso actividadProceso " +
				       " where actividadProceso.proceso.idProceso = :idProceso " +
				       "       and actividadProceso.actividad.idActividad = :idActividad";
		
		Query q = em.createQuery(query);
		q.setParameter("idProceso", idProceso);
		q.setParameter("idActividad", idActividad);
		
		List lista = q.getResultList();
		if(lista != null && lista.size() > 0)
			return true;
		
		return false;
	}
	public void changeProceso(Long idProceso){
		getActividades(idProceso);
	}
	
	
	@SuppressWarnings("unchecked")
	public void  getActividades(Long idPro){
		try{
			actividadProcesoListCb= em.createQuery(" SELECT o FROM " + ActividadProceso.class.getName() 
						+ " o WHERE o.activo = true and o.proceso.idProceso=:idProceso ORDER BY o.actividad.nroActividad  ")
						.setParameter("idProceso", idPro).getResultList();
		}catch(Exception ex){
			ex.printStackTrace();
			actividadProcesoListCb=new  Vector<ActividadProceso>();
		}
	}
	
	public List<SelectItem> getActividadSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(ActividadProceso o :actividadProcesoListCb)
			si.add(new SelectItem(o.getActividad().getIdActividad(),"" + o.getActividad().getDescripcion()));
		return si;
	}
	
	public String getUnidadPlazo(String valor){
		if (valor == null)
			return null;
		return UnidadPlazo.getPorValor(valor).getDescripcion();
	}

	public void eliminar(Integer fila) {
		ActividadProceso actividadProceso = actividadProcesoList.get(fila);
		actividadProcesoList.remove(actividadProceso);
    }
	
	public boolean isNew() {
		if(idActividadProceso == null)
			return true;
		
		return false;
    }
	
	public void cancelAdd(){
		clrVar();
		actividadProcesoList = new ArrayList<ActividadProceso>();
	}
	
	
//	@SuppressWarnings("unchecked")
//	private List<Rol> traerRoles() {
//		try {
//			String consulta = " select o from " + Rol.class.getName() + " o " +
//							  " where o.activo = :activo " +
//							  " order by o.descripcion ";
//			
//			Query q = em.createQuery(consulta);
//			q.setParameter("activo", new Boolean(true));
//			
//			return q.getResultList();
//		} catch (Exception ex) {
//			return new Vector<Rol>();
//		}
//	}
	
	@SuppressWarnings("unchecked")
	private List<Rol> traerRolesAsignados() {
		try {
			String consulta = " select distinct rol " +
							  " from ProcActividadRol procActividadRol " +
							  " join procActividadRol.actividadProceso actividadProceso " +
							  " join procActividadRol.rol rol " +
							  " where actividadProceso.idActividadProceso = :idActividadProceso " +
							  "     and rol.activo = :activo " +
							  " order by rol.descripcion ";
			
			Query q = em.createQuery(consulta);
			q.setParameter("idActividadProceso", idActividadProceso);
			q.setParameter("activo", new Boolean(true));
			
			return q.getResultList();
		} catch (Exception ex) {
			return new Vector<Rol>();
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Rol> traerRolesSinAsignar() {
		try {
			String consulta = " select distinct rol " +
			  				  " from Rol rol " +
			  				  " where rol.idRol not in " +
			  				  " ( " +
					  				  " select distinct rol.idRol " +
									  " from ProcActividadRol procActividadRol " +
									  " join procActividadRol.actividadProceso actividadProceso " +
									  " join procActividadRol.rol rol " +
									  " where actividadProceso.idActividadProceso = :idActividadProceso " +
									  "     and rol.activo = :activo " +
							  " ) " +
							  " order by rol.descripcion ";
			
			Query q = em.createQuery(consulta);
			q.setParameter("idActividadProceso", idActividadProceso);
			q.setParameter("activo", new Boolean(true));
			
			return q.getResultList();
		} catch (Exception ex) {
			return new Vector<Rol>();
		}
	}
	
	
	public String saveRol() {
		try {
			
			// Eliminar Detalles
			ActividadProceso actividadProceso = em.find(ActividadProceso.class, idActividadProceso);
			String consulta = " select procActividadRol from ProcActividadRol procActividadRol " +
							  " where procActividadRol.actividadProceso.idActividadProceso = :idActividadProceso ";
			
			Query q = em.createQuery(consulta);
			q.setParameter("idActividadProceso", idActividadProceso);
			List<ProcActividadRol> lista = q.getResultList();
			
			if (lista != null && lista.size() > 0){
				 Iterator <ProcActividadRol> it = lista.iterator();
				 while (it.hasNext()){
					 ProcActividadRol procActividadRol = it.next();
					 em.remove(procActividadRol);
				 }
					 
//				 Query q = em.createQuery("delete from ProcActividadRol where procActividadRol.idProcActividadRol = :id");
//				 q.setParameter("id", procActividadRol.getId());
//				 q.executeUpdate();
			}
			actividadProceso.getProcActividadRols().clear();
			
			
			// Se almacenan los nuevos
   		 	if (rolesAsignados != null && rolesAsignados.size() > 0){
	    		 Iterator<Rol> it = rolesAsignados.iterator();
	    	 	 while (it.hasNext()) {
	    	 		 Rol element = it.next();
	    	 		 ProcActividadRol procActividadRol = new ProcActividadRol();
	    	 		 procActividadRol.setActividadProceso(actividadProceso);
	    	 		 procActividadRol.setActivo(new Boolean(true));
	    	 		 procActividadRol.setFechaAlta(new Date());
	    	 		 procActividadRol.setRol(element);
	    	 		 procActividadRol.setUsuAlta(usuarioLogueado.getCodigoUsuario());
	    	 		 
	    	 		 em.persist(procActividadRol);
	    	 	 }
	    	 	 em.flush();
   		 	}

			clrVar();
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
		String mens = "Roles asginados con exito";
		statusMessages.add(Severity.INFO, mens);
		return "persistedRol";
	}
	
	

	//GETTERS AND SETTERS
	public Long getIdActividadProceso() {
		return idActividadProceso;
	}

	public void setIdActividadProceso(Long idActividadProceso) {
		this.idActividadProceso = idActividadProceso;
	}


	public List<ActividadProceso> getActividadProcesoList() {
		return actividadProcesoList;
	}
	public void setActividadProcesoList(List<ActividadProceso> actividadProcesoList) {
		this.actividadProcesoList = actividadProcesoList;
	}


	public Long getIdProceso() {
		return idProceso;
	}

	public void setIdProceso(Long idProceso) {
		this.idProceso = idProceso;
	}


	public Long getIdActividad() {
		return idActividad;
	}

	public void setIdActividad(Long idActividad) {
		this.idActividad = idActividad;
	}


	public BigDecimal getPlazoActividad() {
		return plazoActividad;
	}

	public void setPlazoActividad(BigDecimal plazoActividad) {
		this.plazoActividad = plazoActividad;
	}


	public String getUnidadPlazo() {
		return unidadPlazo;
	}

	public void setUnidadPlazo(String unidadPlazo) {
		this.unidadPlazo = unidadPlazo;
	}


	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}


	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}


	public boolean isEsEdit() {
		return esEdit;
	}

	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}


	public boolean isActividadRepetida() {
		return actividadRepetida;
	}

	public void setActividadRepetida(boolean actividadRepetida) {
		this.actividadRepetida = actividadRepetida;
	}


	public boolean isVista() {
		return vista;
	}

	public void setVista(boolean vista) {
		this.vista = vista;
	}
	
	public List<Rol> getRolesSinAsignar() {
		return rolesSinAsignar;
	}

	public void setRolesSinAsignar(List<Rol> rolesSinAsignar) {
		this.rolesSinAsignar = rolesSinAsignar;
	}

	public List<Rol> getRolesAsignados() {
		return rolesAsignados;
	}

	public void setRolesAsignados(List<Rol> rolesAsignados) {
		this.rolesAsignados = rolesAsignados;
	}

	public List<ActividadProceso> getActividadProcesoListCb() {
		return actividadProcesoListCb;
	}

	public void setActividadProcesoListCb(
			List<ActividadProceso> actividadProcesoListCb) {
		this.actividadProcesoListCb = actividadProcesoListCb;
	}
	
}
