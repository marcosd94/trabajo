package py.com.excelsis.sicca.session;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Empleado;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("personaHome")
public class PersonaHome extends EntityHome<Persona> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	PaisHome paisHome;
	@In(create = true)
	CiudadHome ciudadHome;
	@In(create = true)
	NacionalidadHome nacionalidadHome;
	@In(create = true)
	DepartamentoHome departamentoHome;

	public static final String CONTEXT_VAR_NAME = "personas";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Persona> getPersonas(){
		try{
			return getEntityManager().createQuery(" select o from " + Persona.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<Persona>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getPersonaSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(Persona o : getPersonas())
			si.add(new SelectItem(o.getIdPersona(),"" + o.getNombres()));
		return si;
	}

	public void setPersonaIdPersona(Long id) {
		setId(id);
	}

	public Long getPersonaIdPersona() {
		return (Long) getId();
	}

	@Override
	protected Persona createInstance() {
		Persona persona = new Persona();
		return persona;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Pais paisByIdPaisExpedicionDoc = paisHome.getDefinedInstance();
		if (paisByIdPaisExpedicionDoc != null) {
			getInstance().setPaisByIdPaisExpedicionDoc(
					paisByIdPaisExpedicionDoc);
		}
		Ciudad ciudadByIdCiudadDirecc = ciudadHome.getDefinedInstance();
		if (ciudadByIdCiudadDirecc != null) {
			getInstance().setCiudadByIdCiudadDirecc(ciudadByIdCiudadDirecc);
		}
		Ciudad ciudadByIdCiudadNac = ciudadHome.getDefinedInstance();
		if (ciudadByIdCiudadNac != null) {
			getInstance().setCiudadByIdCiudadNac(ciudadByIdCiudadNac);
		}
	}

	public boolean isWired() {
		return true;
	}

	public Persona getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Empleado> getEmpleados() {
		return getInstance() == null ? null : new ArrayList<Empleado>(
				getInstance().getEmpleados());
	}

	public List<Usuario> getUsuarios() {
		return getInstance() == null ? null : new ArrayList<Usuario>(
				getInstance().getUsuarios());
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
}
