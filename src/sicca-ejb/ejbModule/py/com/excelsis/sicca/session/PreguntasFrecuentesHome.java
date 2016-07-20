package py.com.excelsis.sicca.session;

import java.util.Date;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.PreguntasFrecuentes;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

@Name("preguntasFrecuentesHome")
public class PreguntasFrecuentesHome extends EntityHome<PreguntasFrecuentes> {

	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	
	@Override
	protected PreguntasFrecuentes loadInstance() {
		PreguntasFrecuentes o = super.loadInstance();
		return o;
	}
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "preguntasFrecuentess";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setPreguntasFrecuentesIdPreguntaFrecuente(Long id) {
		setId(id);
	}

	public Long getPreguntasFrecuentesIdPreguntaFrecuente() {
		return (Long) getId();
	}

	@Override
	protected PreguntasFrecuentes createInstance() {
		PreguntasFrecuentes preguntasFrecuentes = new PreguntasFrecuentes();
		return preguntasFrecuentes;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
	}

	public boolean isWired() {
		if (getInstance().getDatosEspecificos() == null)
			return false;
		return true;
	}

	public PreguntasFrecuentes getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
	
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
			getInstance().setRespuestaPregunta(getInstance().getRespuestaPregunta().trim());
			getInstance().setPreguntaFrecuente(getInstance().getPreguntaFrecuente().trim());
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());	
			getInstance().setRespuestaPregunta(getInstance().getRespuestaPregunta().trim());
			getInstance().setPreguntaFrecuente(getInstance().getPreguntaFrecuente().trim());
			
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	@Override
    public void setInstance(PreguntasFrecuentes instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }


}
