package py.com.excelsis.sicca.session;



import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Mensaje;
import py.com.excelsis.sicca.entity.Parentesco;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("mensajeHome")
public class MensajeHome extends EntityHome<Mensaje>{
	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	PersonaHome personaHome;
	
	
	public static final String CONTEXT_VAR_NAME = "mensaje";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	public void setMensajeIdMensaje(Long id) {
		setId(id);
	}

	public Long getMensajeIdMensaje() {
		return (Long) getId();
	}
	
	@Override
	protected Mensaje createInstance(){
		Mensaje mensaje = new Mensaje();
		return mensaje;
	}
	
	@Override
	public String persist() {
		getInstance().setCodigoMensaje(getInstance().getCodigoMensaje().trim());
		getInstance().setValorMensaje(getInstance().getValorMensaje().trim());
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	public Mensaje getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	@Override
	public String update() {
		getInstance().setCodigoMensaje(getInstance().getCodigoMensaje().trim());
		getInstance().setValorMensaje(getInstance().getValorMensaje().trim());
			return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	public String save(){
		if(getInstance().getIdMensaje() == null){
			return persist();
		}
		return update();
	}

	
}
	


	


		
		
		

		
		
	