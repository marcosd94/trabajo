package py.com.excelsis.sicca.seleccion.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionDet;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.ConcursoHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;

@Name("excepcionHome")
public class ExcepcionHome extends EntityHome<Excepcion> {

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	ConcursoHome concursoHome;
	@In(create = true)
	ComisionSeleccionCabHome comisionSeleccionCabHome;
	
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	@Override
	protected Excepcion loadInstance() {
		Excepcion o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "excepcions";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	

	public void setExcepcionIdExcepcion(Long id) {
		setId(id);
	}

	public Long getExcepcionIdExcepcion() {
		return (Long) getId();
	}

	@Override
	protected Excepcion createInstance() {
		Excepcion excepcion = new Excepcion();
		return excepcion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ConcursoPuestoAgr concursoPuestoAgr = concursoPuestoAgrHome
				.getDefinedInstance();
		if (concursoPuestoAgr != null) {
			getInstance().setConcursoPuestoAgr(concursoPuestoAgr);
		}
		Concurso concurso = concursoHome.getDefinedInstance();
		if (concurso != null) {
			getInstance().setConcurso(concurso);
		}
		ComisionSeleccionCab comisionSeleccionCab = comisionSeleccionCabHome
				.getDefinedInstance();
		if (comisionSeleccionCab != null) {
			getInstance().setComisionSeleccionCab(comisionSeleccionCab);
		}
	}

	@Override
	public String persist() {
	
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	public boolean isWired() {
		return true;
	}

	public Excepcion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

//	public List<ComisionSeleccionDet> getComisionSeleccionDets() {
//		return getInstance() == null ? null
//				: new ArrayList<ComisionSeleccionDet>(getInstance()
//						.getComisionSeleccionDets());
//	}

	public List<ExcepcionDet> getExcepcionDets() {
		return getInstance() == null ? null : new ArrayList<ExcepcionDet>(
				getInstance().getExcepcionDets());
	}

}
