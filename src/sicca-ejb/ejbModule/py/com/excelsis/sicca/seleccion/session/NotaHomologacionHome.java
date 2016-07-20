package py.com.excelsis.sicca.seleccion.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.NotaHomologacion;
import py.com.excelsis.sicca.entity.PlantillaNotaHomologacion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;

@Name("notaHomologacionHome")
public class NotaHomologacionHome extends EntityHome<NotaHomologacion> {

	@In(create = true)
	PlantillaNotaHomologacionHome plantillaNotaHomologacionHome;

	@In(required = false)
	Usuario usuarioLogueado;
	
	public static final String CONTEXT_VAR_NAME = "notaHomologacions";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
		CONTEXT_VAR_NAME + "SelectItems" };
	
	
	public void setNotaHomologacionIdNotaHomologacion(Long id) {
		setId(id);
	}

	public Long getNotaHomologacionIdNotaHomologacion() {
		return (Long) getId();
	}

	@Override
	protected NotaHomologacion createInstance() {
		NotaHomologacion notaHomologacion = new NotaHomologacion();
		return notaHomologacion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PlantillaNotaHomologacion plantillaNotaHomologacion = plantillaNotaHomologacionHome
				.getDefinedInstance();
		if (plantillaNotaHomologacion != null) {
			getInstance().setPlantillaNotaHomologacion(
					plantillaNotaHomologacion);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantillaNotaHomologacion() == null)
			return false;
		return true;
	}

	public NotaHomologacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ConcursoPuestoAgr> getConcursoPuestoAgrs() {
		return getInstance() == null ? null : new ArrayList<ConcursoPuestoAgr>(
				getInstance().getConcursoPuestoAgrs());
	}
	
	public String save(){
		if(!isIdDefined()){
			return persist();
		}
		return update();
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		
		return AppHelper.removeFromContext("persist", super.persist(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		
		
	
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
		
		return AppHelper.removeFromContext("updated", super.update(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

}
