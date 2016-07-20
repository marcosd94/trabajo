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

import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.EscalaCondTrabEspecif;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("escalaCondTrabEspecifHome")
public class EscalaCondTrabEspecifHome extends EntityHome<EscalaCondTrabEspecif> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1403803080018315594L;

	@In
	FacesMessages facesMessages;
	@In
	StatusMessages statusMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	
	@Override
	protected EscalaCondTrabEspecif loadInstance() {
		EscalaCondTrabEspecif o = super.loadInstance();
		
		return o;
	}
	
	//Value holders for selectItems if exists
	
	public static final String CONTEXT_VAR_NAME = "escalaCondTrabEspecifs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<EscalaCondTrabEspecif> getEscalaCondTrabEspecifs(){
		try{
			return getEntityManager().createQuery(" select o from " + EscalaCondTrabEspecif.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<EscalaCondTrabEspecif>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getEscalaCondTrabEspecifSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(EscalaCondTrabEspecif o : getEscalaCondTrabEspecifs())
			si.add(new SelectItem(o.getIdEscalaCondTrabEspecif(),"" + o.getDescripcion()));
		return si;
	}

	public void setEscalaCondTrabEspecifIdEscalaCondTrabEspecif(Long id) {
		setId(id);
	}

	public Long getEscalaCondTrabEspecifIdEscalaCondTrabEspecif() {
		return (Long) getId();
	}

	@Override
	protected EscalaCondTrabEspecif createInstance() {
		EscalaCondTrabEspecif escalaCondTrabEspecif = new EscalaCondTrabEspecif();
		return escalaCondTrabEspecif;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if(getInstance().getIdEscalaCondTrabEspecif() == null)
			getInstance().setActivo(true);
	}

	public boolean isWired() {
		return true;
	}

	public EscalaCondTrabEspecif getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
		
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());		
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@Override
    public void setInstance(EscalaCondTrabEspecif instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM EscalaCondTrabEspecif o WHERE lower(o.descripcion) =:descripcion";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idEscalaCondTrabEspecif != " + getInstance().getIdEscalaCondTrabEspecif().longValue();
		}
		List<EscalaCondTrabEspecif> list = getEntityManager().createQuery(hql).setParameter("descripcion", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	

	private boolean estaValor(String operation, Integer valor){
		String hql = "SELECT o FROM EscalaCondTrabEspecif o WHERE :valor between o.desde and o.hasta ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idEscalaCondTrabEspecif != " + getInstance().getIdEscalaCondTrabEspecif().longValue();
		}
		List<EscalaCondTrabEspecif> list = getEntityManager().createQuery(hql).setParameter("valor", valor).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		
		if(seguridadUtilFormController.contieneCaracter(getInstance().getDescripcion().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		
		if(getInstance().getDesde() < 0){
			statusMessages.add(Severity.ERROR, "El campo Desde debe ser mayor a cero");
			return false;
		}
		if(getInstance().getHasta() < 0){
			statusMessages.add(Severity.ERROR, "El campo Hasta debe ser mayor a cero");
			return false;
		}
		if(getInstance().getHasta() < getInstance().getDesde()){
			statusMessages.add(Severity.ERROR, "El campo Hasta no debe ser mayor al campo Desde");
			return false;
		}
		if(!estaValor(operation, getInstance().getDesde()) && !estaValor(operation, getInstance().getHasta())){
			statusMessages.add(Severity.ERROR, "El rango ingresado ya existe");
			return false;
		}
			
		return true;
	}
	//Public getters and setters if exists
	
	
}
