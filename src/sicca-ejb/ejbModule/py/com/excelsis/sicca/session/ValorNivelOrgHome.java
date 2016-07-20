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

import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.EscalaCondTrabEspecif;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("valorNivelOrgHome")
public class ValorNivelOrgHome extends EntityHome<ValorNivelOrg> {

	@In
	FacesMessages facesMessages;
	@In
	StatusMessages statusMessages;

	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@Override
	protected ValorNivelOrg loadInstance() {
		ValorNivelOrg o = super.loadInstance();
		this.idContenidoFuncional = o.getContenidoFuncional()
				.getIdContenidoFuncional();
		return o;
	}

	// Value holders for selectItems if exists
	private Long idContenidoFuncional;
	public static final String CONTEXT_VAR_NAME = "valorNivelOrgs";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
			CONTEXT_VAR_NAME + "SelectItems" };

	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME, scope = ScopeType.EVENT)
	public List<ValorNivelOrg> getValorNivelOrgs() {
		try {
			return getEntityManager().createQuery(
					" select o from " + ValorNivelOrg.class.getName() + " o")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<ValorNivelOrg>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getValorNivelOrgSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		for (ValorNivelOrg o : getValorNivelOrgs())
			si.add(new SelectItem(o.getIdValorNivelOrg(), ""
					+ o.getDescripcion()));
		return si;
	}

	public void setValorNivelOrgIdValorNivelOrg(Long id) {
		setId(id);
	}

	public Long getValorNivelOrgIdValorNivelOrg() {
		return (Long) getId();
	}

	@Override
	protected ValorNivelOrg createInstance() {
		ValorNivelOrg valorNivelOrg = new ValorNivelOrg();
		return valorNivelOrg;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}			
	}

	public void loadToView(){
		idContenidoFuncional = getInstance().getContenidoFuncional().getIdContenidoFuncional();
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

	public ValorNivelOrg getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setContenidoFuncional(
				getEntityManager().find(ContenidoFuncional.class,
						this.idContenidoFuncional));
		return AppHelper.removeFromContext("persist", super.persist(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setContenidoFuncional(
				getEntityManager().find(ContenidoFuncional.class,
						this.idContenidoFuncional));
		return AppHelper.removeFromContext("updated", super.update(),
				CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM ValorNivelOrg o WHERE lower(o.descripcion) = :desc ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idValorNivelOrg != " + getInstance().getIdValorNivelOrg().longValue();
		}
		List<ValorNivelOrg> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
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
		if(getInstance().getDesde().intValue()<0){
			statusMessages.add(Severity.ERROR,"El valor DESDE no puede ser menor a cero, verifique");
			return false;
		}
		if(getInstance().getHasta().intValue()<0){
			statusMessages.add(Severity.ERROR,"El valor HASTA no puede ser menor a cero, verifique");
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
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	private boolean estaValor(String operation, Integer valor){ 
		String hql = "SELECT o FROM ValorNivelOrg o WHERE :valor between o.desde and o.hasta and" +
				" o.contenidoFuncional.idContenidoFuncional= "+idContenidoFuncional;
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idValorNivelOrg != " + getInstance().getIdValorNivelOrg().longValue();
		}
		List<EscalaCondTrabEspecif> list = getEntityManager().createQuery(hql).setParameter("valor", valor).getResultList();
		return list.isEmpty();
	}
	

		
	// Public getters and setters if exists

	public Long getIdContenidoFuncional() {
		return this.idContenidoFuncional;
	}

	public void setIdContenidoFuncional(Long idContenidoFuncional) {
		this.idContenidoFuncional = idContenidoFuncional;
	}

}
