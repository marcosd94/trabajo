package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.AdjuntoDocPuestoAgr;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Scope(ScopeType.CONVERSATION)
@Name("concursoPuestoAgrHome")
public class ConcursoPuestoAgrHome extends EntityHome<ConcursoPuestoAgr> {

	@In(create = true)
	ConcursoHome concursoHome;
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "concursoPuestoAgrs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setConcursoPuestoAgrIdConcursoPuestoAgr(Long id) {
		setId(id);
	}

	public Long getConcursoPuestoAgrIdConcursoPuestoAgr() {
		return (Long) getId();
	}

	@Override
	protected ConcursoPuestoAgr createInstance() {
		ConcursoPuestoAgr concursoPuestoAgr = new ConcursoPuestoAgr();
		return concursoPuestoAgr;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Concurso concurso = concursoHome.getDefinedInstance();
		if (concurso != null) {
			getInstance().setConcurso(concurso);
		}
	}

	public boolean isWired() {
		if (getInstance().getConcurso() == null)
			return false;
		return true;
	}

	public ConcursoPuestoAgr getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<AdjuntoDocPuestoAgr> getAdjuntoDocPuestoAgrs() {
		return getInstance() == null ? null
				: new ArrayList<AdjuntoDocPuestoAgr>(getInstance()
						.getAdjuntoDocPuestoAgrs());
	}

	public List<ConcursoPuestoDet> getConcursoPuestoDets() {
		return getInstance() == null ? null : new ArrayList<ConcursoPuestoDet>(
				getInstance().getConcursoPuestoDets());
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
	
	@Override
    public void setInstance(ConcursoPuestoAgr instance)
    {
        if (instance != null)
        {
            super.setId(instance.getIdConcursoPuestoAgr());
        }
        super.setInstance(instance);
    }

	public boolean esNuevo(){
		if (this.getInstance() == null || this.getInstance().getIdConcursoPuestoAgr() == null)
			return true;
		
		return false;
	}
}
