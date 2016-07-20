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

import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("paisHome")
public class PaisHome extends EntityHome<Pais> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	@Override
	protected Pais loadInstance() {
		Pais o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "paiss";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Pais> getPaiss(){
		try{
			return getEntityManager().createQuery(" SELECT o FROM " + Pais.class.getName() + " o " +
					"WHERE o.activo = true ORDER BY o.descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<Pais>();
		}
	}
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getPaisSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(Pais o : getPaiss())
			si.add(new SelectItem(o.getIdPais(),"" + o.getDescripcion()));
		return si;
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "TodosSelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getPaisTodosSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "TODOS"));
		for(Pais o : getPaiss())
			si.add(new SelectItem(o.getIdPais(),"" + o.getDescripcion()));
		return si;
	}

	public void setPaisIdPais(Long id) {
		setId(id);
	}

	public Long getPaisIdPais() {
		return (Long) getId();
	}

	@Override
	protected Pais createInstance() {
		Pais pais = new Pais();
		return pais;
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

	public Pais getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());	
		//ZD 03/09/15
		getInstance().setPrefijo(getInstance().getPrefijo().trim().toUpperCase());
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());	
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());	
		//ZD 03/09/15
		getInstance().setPrefijo(getInstance().getPrefijo().trim().toUpperCase());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM Pais o WHERE lower(o.descripcion) =:desc";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idPais != " + getInstance().getIdPais().longValue();
		}
		List<Pais> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
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
		if(getInstance().getPaisCodigoSinarh()!=null){
			if(getInstance().getPaisCodigoSinarh().intValue()<=0){
				statusMessages.add(Severity.ERROR,"El Código Sinarh no puede ser menor a 0. Verifique");
				return false;
			}
		}
		if(operation.equals("update")){
			if (!getInstance().getActivo()) {
				if (!getInstance().getDepartamentos().isEmpty()
						|| !getInstance().getEstudiosRealizados().isEmpty()
						|| !getInstance().getExperienciaLaborals().isEmpty()
						|| !getInstance().getInstitucionEducativas().isEmpty()
						|| !getInstance().getPersonaPostulantes().isEmpty()
						|| !getInstance().getPersonas().isEmpty()) {
					statusMessages
							.add("No se puede inactivar el registro está en uso. Verifíque");
					return false;
				}
			}
					
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	//Public getters and setters if exists
	
}
