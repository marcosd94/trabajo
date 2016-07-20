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

import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.entity.TipoPlanta;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("tipoNombramientoHome")
public class TipoNombramientoHome extends EntityHome<TipoNombramiento> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3953194901424133318L;

	@In
	FacesMessages facesMessages;
	
	@In
	StatusMessages statusMessages;

	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	
	@Override
	protected TipoNombramiento loadInstance() {
		TipoNombramiento o = super.loadInstance();
		this.idTipoPlanta = o.getTipoPlanta().getIdTipoPlanta();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idTipoPlanta;
	public static final String CONTEXT_VAR_NAME = "tipoNombramientos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<TipoNombramiento> getTipoNombramientos(){
		try{
			return getEntityManager().createQuery(" select o from " + TipoNombramiento.class.getName() + " o where o.activo = true order by o.descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<TipoNombramiento>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getTipoNombramientoSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(TipoNombramiento o : getTipoNombramientos())
			si.add(new SelectItem(o.getIdTipoNombramiento(),"" + o.getDescripcion()));
		return si;
	}

	public void setTipoNombramientoIdTipoNombramiento(Long id) {
		setId(id);
	}

	public Long getTipoNombramientoIdTipoNombramiento() {
		return (Long) getId();
	}

	@Override
	protected TipoNombramiento createInstance() {
		TipoNombramiento tipoNombramiento = new TipoNombramiento();
		return tipoNombramiento;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if(getInstance().getIdTipoNombramiento() == null)
			getInstance().setActivo(true);
	}

	public boolean isWired() {
		return true;
	}

	public TipoNombramiento getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());			
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setTipoPlanta(getEntityManager().find(TipoPlanta.class,this.idTipoPlanta));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());	
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setTipoPlanta(getEntityManager().find(TipoPlanta.class,this.idTipoPlanta));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@Override
    public void setInstance(TipoNombramiento instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }

	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM TipoNombramiento o WHERE lower(o.descripcion) =:descripcion ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idTipoNombramiento != " + getInstance().getIdTipoNombramiento().longValue();
		}
		List<ValorNivelOrg> list = getEntityManager().createQuery(hql).setParameter("descripcion", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if (seguridadUtilFormController.contieneCaracter(getInstance()
				.getDescripcion().trim())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_caracter"));
			return false;
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	//Public getters and setters if exists
	
	public Long getIdTipoPlanta(){
		return this.idTipoPlanta;
	}
	
	public void setIdTipoPlanta(Long idTipoPlanta){
		this.idTipoPlanta = idTipoPlanta;
	}
	
}
