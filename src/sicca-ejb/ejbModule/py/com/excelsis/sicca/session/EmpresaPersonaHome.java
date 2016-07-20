package py.com.excelsis.sicca.session;

import java.util.Date;

import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.entity.EmpresaPersona;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Name("empresaPersonaHome")
public class EmpresaPersonaHome extends EntityHome<EmpresaPersona> {

	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	EmprTercerizadaHome emprTercerizadaHome;
	
	public static final String CONTEXT_VAR_NAME = "empresaPersona";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setEmpresaPersonaIdSelEmpresaPersona(Long id) {
		setId(id);
	}

	public Long getEmpresaPersonaIdSelEmpresaPersona() {
		return (Long) getId();
	}

	@Override
	protected EmpresaPersona createInstance() {
		EmpresaPersona empresaPersona = new EmpresaPersona();
		return empresaPersona;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		EmprTercerizada emprTercerizada = emprTercerizadaHome
				.getDefinedInstance();
		if (emprTercerizada != null) {
			getInstance().setEmprTercerizada(emprTercerizada);
		}
	}

	public boolean isWired() {
		if (getInstance().getEmprTercerizada() == null)
			return false;
		return true;
	}

	public EmpresaPersona getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setCargo(getInstance().getCargo().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setCargo(getInstance().getCargo().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}

}
