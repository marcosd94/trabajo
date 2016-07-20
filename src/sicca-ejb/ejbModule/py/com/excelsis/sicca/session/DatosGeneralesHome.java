package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

import java.util.ArrayList;
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

@Name("datosGeneralesHome")
public class DatosGeneralesHome extends EntityHome<DatosGenerales> {

	
	@In
	StatusMessages statusMessages;
	
	
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	
	public void setDatosGeneralesIdDatosGenerales(Long id) {
		setId(id);
	}

	public Long getDatosGeneralesIdDatosGenerales() {
		return (Long) getId();
	}

	@Override
	protected DatosGenerales createInstance() {
		DatosGenerales datosGenerales = new DatosGenerales();
		return datosGenerales;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "datosGenerales";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<DatosGenerales> getDatosGenerales(){
		try{
			return getEntityManager().createQuery(" select o from " + DatosGenerales.class.getName() + " o " +
					" WHERE o.activo = true order by o.descripcion ").getResultList();
		}catch(Exception ex){
			return new Vector<DatosGenerales>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getDatosGeneralesSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(DatosGenerales o : getDatosGenerales())
			si.add(new SelectItem(o.getIdDatosGenerales(),"" + o.getDescripcion()));
		return si;
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

	public DatosGenerales getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<DatosEspecificos> getDatosEspecificoses() {
		return getInstance() == null ? null : new ArrayList<DatosEspecificos>(
				getInstance().getDatosEspecificoses());
	}
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
			
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());	
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM DatosGenerales o WHERE lower(o.descripcion) =:desc ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idDatosGenerales != " + getInstance().getIdDatosGenerales().longValue();
		}
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
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
		if(operation.equalsIgnoreCase("update")){
			List<DatosEspecificos> gList= getEntityManager().createQuery(" select e from DatosEspecificos e " +
					" where e.activo= true and e.datosGenerales.idDatosGenerales= "+getInstance().getIdDatosGenerales()).getResultList();
			if(!gList.isEmpty()){
				statusMessages.add(Severity.ERROR, "El registro esta en uso, no se puede inactivar");
				return false;
			}
		}
		
		
		return true;
	}
	//Public getters and setters if exists

}
