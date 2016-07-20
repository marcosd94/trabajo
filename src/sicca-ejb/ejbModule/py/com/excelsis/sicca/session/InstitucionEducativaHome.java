package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.InstitucionEducativa;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("institucionEducativaHome")
public class InstitucionEducativaHome extends EntityHome<InstitucionEducativa> {
	
	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	public static final String CONTEXT_VAR_NAME = "institucionEducativa";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	@In(create = true)
	PaisHome paisHome;
	
	private Long idPais = null;
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<InstitucionEducativa> getInstitucionEducativa(){
		try{
			return getEntityManager().createQuery(" select o from " + InstitucionEducativa.class.getName() + " o" +
					" WHERE o.activo = true ORDER BY o.descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<InstitucionEducativa>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getNivelesEstudioSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(InstitucionEducativa o : getInstitucionEducativa())
			si.add(new SelectItem(o.getIdInstEducativa(), o.getDescripcion()));
		return si;
	}

	public void setInstitucionEducativaIdInstEducativa(Long id) {
		setId(id);
	}

	public Long getInstitucionEducativaIdInstEducativa() {
		return (Long) getId();
	}

	@Override
	protected InstitucionEducativa createInstance() {
		InstitucionEducativa institucionEducativa = new InstitucionEducativa();
		return institucionEducativa;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Pais pais = paisHome.getDefinedInstance();
		if(!isIdDefined()){
			getInstance().setActivo(true);
		}
		if (pais != null) {
			getInstance().setPais(pais);
			idPais = pais.getIdPais();
		}
	}

	public boolean isWired() {
		if (getInstance().getPais() == null)
			return false;
		return true;
	}

	public InstitucionEducativa getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setPais(idPais != null ? getEntityManager().find(Pais.class, idPais) : null);
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());

		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setPais(idPais != null ? getEntityManager().find(Pais.class, idPais) : null);
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM InstitucionEducativa o WHERE lower(o.descripcion) = :desc";
		if(idPais != null)
			hql += " AND o.pais.idPais = " + idPais;
		
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idInstEducativa != " + getInstance().getIdInstEducativa().longValue();
		}
		List<CondicionTrabajoEspecif> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(){
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if(sufc.contieneCaracter(getInstance().getDescripcion().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(operation.equals("update")){
			if(!getInstance().getActivo() && !getInstance().getEstudiosRealizados().isEmpty()){
				statusMessages.add("No se puede eliminar el registro porque está en uso");
				return false;
			}
		}
		
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}

//	GETTERS Y SETTERS
	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdPais() {
		return idPais;
	}

}
