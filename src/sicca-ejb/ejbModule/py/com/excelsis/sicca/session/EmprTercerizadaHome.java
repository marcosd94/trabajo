package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.entity.EmpresaPersona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

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

import enums.Estado;

@Name("emprTercerizadaHome")
public class EmprTercerizadaHome extends EntityHome<EmprTercerizada> {
	
	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	CiudadHome ciudadHome;

	public static final String CONTEXT_VAR_NAME = "empresasTercerizadas";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<EmprTercerizada> getEmpresasTercerizadas(){
		try{
			return getEntityManager().createQuery(" select o from " + EmprTercerizada.class.getName() + " o" +
					" WHERE o.activo = true ORDER BY o.nombre").getResultList();
		}catch(Exception ex){
			return new Vector<EmprTercerizada>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getEmpresaTercerizadaSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(EmprTercerizada o : getEmpresasTercerizadas())
			si.add(new SelectItem(o.getIdEmpresaTercerizada(),"" + o.getNombre()));
		return si;
	}
	
	public void setEmprTercerizadaIdEmpresaTercerizada(Long id) {
		setId(id);
	}

	public Long getEmprTercerizadaIdEmpresaTercerizada() {
		return (Long) getId();
	}

	@Override
	protected EmprTercerizada createInstance() {
		EmprTercerizada emprTercerizada = new EmprTercerizada();
		return emprTercerizada;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if(!isIdDefined()){
			getInstance().setActivo(Estado.ACTIVO.getValor());
		}
	}

	public boolean isWired() {
		return true;
	}

	public EmprTercerizada getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<EmpresaPersona> getEmpresaPersonas() {
		return getInstance() == null ? null : new ArrayList<EmpresaPersona>(
				getInstance().getEmpresaPersonas());
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setNombre(getInstance().getNombre().trim().toUpperCase());
		getInstance().setDireccion(getInstance().getDireccion().trim().toUpperCase());
		getInstance().setRuc(getInstance().getRuc().trim().toUpperCase());
		getInstance().setEMail(getInstance().getEMail().trim().toLowerCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		
		getInstance().setNombre(getInstance().getNombre().trim().toUpperCase());
		getInstance().setDireccion(getInstance().getDireccion().trim().toUpperCase());
		getInstance().setRuc(getInstance().getRuc().trim().toUpperCase());
		getInstance().setEMail(getInstance().getEMail().trim().toLowerCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	private boolean checkData(){
		if(getInstance().getNombre().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		return true;
	}

}
