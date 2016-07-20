package py.com.excelsis.sicca.session;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
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

import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("sinEntidadHome")
public class SinEntidadHome extends EntityHome<SinEntidad> {
	
	@In
	FacesMessages facesMessages;
	
	
	@In(required = false)
	Usuario usuarioLogueado;
	


	@In(value = "entityManager")
	EntityManager em;

	@In
	StatusMessages statusMessages;
	
	private Long idSinNivelEntidad;
	
	@Override
	protected SinEntidad loadInstance() {
		SinEntidad o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "sinEntidads";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<SinEntidad> getSinEntidads(){
		try{
			return getEntityManager().createQuery(" select o from " + SinEntidad.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<SinEntidad>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getSinEntidadSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(SinEntidad o : getSinEntidads())
			si.add(new SelectItem(o.getIdSinEntidad(),"" + o.getEntNombre()));
		return si;
	}

	public void setSinEntidadIdSinEntidad(Long id) {
		setId(id);
	}

	public Long getSinEntidadIdSinEntidad() {
		return (Long) getId();
	}

	@Override
	protected SinEntidad createInstance() {
		SinEntidad sinEntidad = new SinEntidad();
		return sinEntidad;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	@SuppressWarnings("unchecked")
	public void wire() {
		getInstance();
		if (!isIdDefined()) {
			getInstance().setActivo(true);
		}else{
			List<SinNivelEntidad> nivelEntidads=em.createQuery("select n from SinNivelEntidad n" +
					" where n.aniAniopre ="+getInstance().getAniAniopre()+ " and n.nenCodigo ="+getInstance().getNenCodigo()).getResultList();
				idSinNivelEntidad=nivelEntidads.get(0).getIdSinNivelEntidad();
		}
		
			
	}

	public boolean isWired() {
		return true;
	}

	public SinEntidad getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setEntNombre(getInstance().getEntNombre().trim().toUpperCase());
		SinNivelEntidad en=em.find(SinNivelEntidad.class, idSinNivelEntidad);
		getInstance().setNenCodigo(en.getNenCodigo());
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setEntNombre(getInstance().getEntNombre().trim().toUpperCase());
		SinNivelEntidad en=em.find(SinNivelEntidad.class, idSinNivelEntidad);
		getInstance().setNenCodigo(en.getNenCodigo());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}


	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getEntNombre().trim().isEmpty() || getInstance().getEntNomabre().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
//		if(!checkDuplicate(operation)){
//			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
//			return false;
//		}
		//FIXME
		return true;
	}
	
//	@SuppressWarnings("unchecked")
//	private boolean checkDuplicate(String operation){
//		String hql = "SELECT o FROM SinNivelEntidad o WHERE o.nenCodigo = "+getInstance().getNenCodigo() +"" +
//				" AND o.aniAniopre="+getInstance().getAniAniopre();
//		if(operation.equalsIgnoreCase("update")){
//			hql += " AND o.idSinNivelEntidad != " + getInstance().getIdSinNivelEntidad().longValue();
//		}
//		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).getResultList();
//		return list.isEmpty();
//	}
	
	//Public getters and setters 
	public Long getIdSinNivelEntidad() {
		return idSinNivelEntidad;
	}

	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}
	
}
