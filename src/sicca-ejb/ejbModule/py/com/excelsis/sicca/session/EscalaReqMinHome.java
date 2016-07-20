package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;



import org.hibernate.Query;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.CapacitacionCerrada;
import py.com.excelsis.sicca.entity.EscalaCondTrab;
import py.com.excelsis.sicca.entity.EscalaCondTrabEspecif;
import py.com.excelsis.sicca.entity.EscalaReqMin;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("escalaReqMinHome")
public class EscalaReqMinHome extends EntityHome<EscalaReqMin> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5516811338840724668L;

	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	@Override
	protected EscalaReqMin loadInstance() {
		EscalaReqMin o = super.loadInstance();
		this.idRequisitoMinimoCpt = o.getRequisitoMinimoCpt().getIdRequisitoMinimoCpt();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idRequisitoMinimoCpt;
	public static final String CONTEXT_VAR_NAME = "escalaReqMins";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<EscalaReqMin> getEscalaReqMins(){
		try{
			return getEntityManager().createQuery(" select o from " + EscalaReqMin.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<EscalaReqMin>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getEscalaReqMinSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(EscalaReqMin o : getEscalaReqMins())
			si.add(new SelectItem(o.getIdEscalaReqMin(),"" + o.getDescripcion()));
		return si;
	}

	public void setEscalaReqMinIdEscalaReqMin(Long id) {
		setId(id);
	}

	public Long getEscalaReqMinIdEscalaReqMin() {
		return (Long) getId();
	}

	@Override
	protected EscalaReqMin createInstance() {
		EscalaReqMin escalaReqMin = new EscalaReqMin();
		return escalaReqMin;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if(getInstance().getIdEscalaReqMin() == null)
			getInstance().setActivo(true);
	}

	public boolean isWired() {
		return true;
	}

	public EscalaReqMin getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());		
		getInstance().setRequisitoMinimoCpt(getEntityManager().find(RequisitoMinimoCpt.class,this.idRequisitoMinimoCpt));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());		
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setRequisitoMinimoCpt(getEntityManager().find(RequisitoMinimoCpt.class,this.idRequisitoMinimoCpt));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM EscalaReqMin o WHERE lower(o.descripcion) =:descripcion ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idEscalaReqMin != " + getInstance().getIdEscalaReqMin().longValue();
		}
		List<EscalaReqMin> list = getEntityManager().createQuery(hql).setParameter("descripcion", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
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
		
		if(getInstance().getDesde().intValue()>getInstance().getHasta().intValue()){
			statusMessages.add(Severity.ERROR,"El valor DESDE no puede ser mayor al el valor HASTA, verifique");
			return false;
		}
		if(!estaValor(operation, getInstance().getDesde()) || !estaValor(operation, getInstance().getHasta())){
			statusMessages.add(Severity.ERROR, "El rango ingresado ya existe");
			return false;
		}
		if(getInstance().getDesde() < 0){
			statusMessages.add(Severity.ERROR, "El campo Desde debe contener un valor mayor a cero");
			return false;
		}
		if(getInstance().getHasta() < 0){
			statusMessages.add(Severity.ERROR, "El campo Hasta debe contener un valor mayor a cero");
			return false;
		}
		return true;
	}
	
	private boolean estaValor(String operation, Integer valor){
		String hql = "SELECT o FROM EscalaReqMin o " +
				"JOIN o.requisitoMinimoCpt req" +
				" WHERE :valor between o.desde and o.hasta";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idEscalaReqMin != " + getInstance().getIdEscalaReqMin().longValue();
		}
		List<EscalaReqMin> list = getEntityManager().createQuery(hql).setParameter("valor", valor).getResultList();
		for (EscalaReqMin o : list){
			if(o.getRequisitoMinimoCpt().getIdRequisitoMinimoCpt().equals(idRequisitoMinimoCpt))
				return false;
		}
			
		return true;
	}
	
	@Override
    public void setInstance(EscalaReqMin instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }
	
	//Public getters and setters if exists
	
	public Long getIdRequisitoMinimoCpt(){
		return this.idRequisitoMinimoCpt;
	}
	
	public void setIdRequisitoMinimoCpt(Long idRequisitoMinimoCpt){
		this.idRequisitoMinimoCpt = idRequisitoMinimoCpt;
	}
	
	


}
