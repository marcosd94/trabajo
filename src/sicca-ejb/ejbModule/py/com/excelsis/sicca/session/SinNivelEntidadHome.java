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

import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("sinNivelEntidadHome")
public class SinNivelEntidadHome extends EntityHome<SinNivelEntidad> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	
	

	@In
	StatusMessages statusMessages;
	
	@Override
	protected SinNivelEntidad loadInstance() {
		SinNivelEntidad o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "sinNivelEntidads";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<SinNivelEntidad> getSinNivelEntidads(){
		try{
			return getEntityManager().createQuery(" select o from " + SinNivelEntidad.class.getName() + " o" +
					" WHERE o.activo = true ORDER BY o.nenNombre").getResultList();
		}catch(Exception ex){
			return new Vector<SinNivelEntidad>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getSinNivelEntidadSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(SinNivelEntidad o : getSinNivelEntidads())
			si.add(new SelectItem(o.getIdSinNivelEntidad(),"" + o.getNenNombre()));
		return si;
	}

	public void setSinNivelEntidadIdSinNivelEntidad(Long id) {
		setId(id);
	}

	public Long getSinNivelEntidadIdSinNivelEntidad() {
		return (Long) getId();
	}

	@Override
	protected SinNivelEntidad createInstance() {
		SinNivelEntidad sinNivelEntidad = new SinNivelEntidad();
		return sinNivelEntidad;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if (!isIdDefined()) {
			getInstance().setActivo(true);
		}
	}

	public boolean isWired() {
		return true;
	}

	public SinNivelEntidad getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setNenNombre(getInstance().getNenNombre().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setNenNombre(getInstance().getNenNombre().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	

	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getNenNombre().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if(getInstance().getAniAniopre().intValue()<=0 || getInstance().getAniAniopre().toString().length()!=4){
			statusMessages.add(Severity.ERROR,"Año no valido");
			return false;
		}
		if(getInstance().getNenCodigo().intValue()<=0){
			statusMessages.add(Severity.ERROR,"El Código debe ser mayo a 0. Verifique");
			return false;
		}
		
			
		if(seguridadUtilFormController.contieneCaracter(getInstance().getNenNombre().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(getInstance().getNenNomabr()!=null){
			if(seguridadUtilFormController.contieneCaracter(getInstance().getNenNomabr().trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return false;
			}
		}
		if(getInstance().getNenImputable()!=null){
			if(seguridadUtilFormController.contieneCaracter(getInstance().getNenImputable().trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return false;
			}
		}
		
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM SinNivelEntidad o WHERE o.nenCodigo = "+getInstance().getNenCodigo() +"" +
				" AND o.aniAniopre="+getInstance().getAniAniopre();
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idSinNivelEntidad != " + getInstance().getIdSinNivelEntidad().longValue();
		}
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).getResultList();
		return list.isEmpty();
	}
	
	//Public getters and setters if exists
	
}
