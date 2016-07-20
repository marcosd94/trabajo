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
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("ciudadHome")
public class CiudadHome extends EntityHome<Ciudad> {
	
	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@Override
	protected Ciudad loadInstance() {
		Ciudad o = super.loadInstance();
		this.idDepartamento = o.getDepartamento().getIdDepartamento();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idDepartamento;
	public static final String CONTEXT_VAR_NAME = "ciudads";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Ciudad> getCiudads(){
		try{
			return getEntityManager().createQuery(" select o from " + Ciudad.class.getName() + " o " +
					"WHERE o.activo = true ORDER BY o.descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<Ciudad>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getCiudadSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(Ciudad o : getCiudads())
			si.add(new SelectItem(o.getIdCiudad(),"" + o.getDescripcion()));
		return si;
	}

	public void setCiudadIdCiudad(Long id) {
		setId(id);
	}

	public Long getCiudadIdCiudad() {
		return (Long) getId();
	}

	@Override
	protected Ciudad createInstance() {
		Ciudad ciudad = new Ciudad();
		return ciudad;
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

	public Ciudad getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setDepartamento(getEntityManager().find(Departamento.class,this.idDepartamento));
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		
		getInstance().setDepartamento(getEntityManager().find(Departamento.class,this.idDepartamento));
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	public String save(){
		if(!isIdDefined()){
			return persist();
		}
		return update();
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM Ciudad o WHERE lower(o.descripcion) =:desc and o.departamento="+idDepartamento;
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idCiudad != " + getInstance().getIdCiudad().longValue();
		}
		List<CondicionTrabajoEspecif> list = getEntityManager().createQuery(hql).setParameter("desc",getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	
	//Public getters and setters if exists
	
	public Long getIdDepartamento(){
		return this.idDepartamento;
	}
	
	public void setIdDepartamento(Long idDepartamento){
		this.idDepartamento = idDepartamento;
	}
	
}
