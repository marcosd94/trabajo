package py.com.excelsis.sicca.seleccion.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.MemoHomologacion;
import py.com.excelsis.sicca.entity.PlantillaMemoHomologacion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;

@Name("memoHomologacionHome")
public class MemoHomologacionHome extends EntityHome<MemoHomologacion> {

	@In(create = true)
	PlantillaMemoHomologacionHome plantillaMemoHomologacionHome;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	public static final String CONTEXT_VAR_NAME = "memoHomologacions";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
		CONTEXT_VAR_NAME + "SelectItems" };
	
	public void setMemoHomologacionIdMemoHomologacion(Long id) {
		setId(id);
	}

	public Long getMemoHomologacionIdMemoHomologacion() {
		return (Long) getId();
	}

	@Override
	protected MemoHomologacion createInstance() {
		MemoHomologacion memoHomologacion = new MemoHomologacion();
		return memoHomologacion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PlantillaMemoHomologacion plantillaMemoHomologacion = plantillaMemoHomologacionHome
				.getDefinedInstance();
		if (plantillaMemoHomologacion != null) {
			getInstance().setPlantillaMemoHomologacion(
					plantillaMemoHomologacion);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantillaMemoHomologacion() == null)
			return false;
		return true;
	}

	public MemoHomologacion getDefinedInstance() {
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
