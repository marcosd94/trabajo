package py.com.excelsis.sicca.general.session.form;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;

import py.com.excelsis.sicca.entity.CabValidacionOee;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.general.session.CabValidacionOeeList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admGrupoValidacionesOeeListFC")
public class AdmGrupoValidacionesOeeListFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	CabValidacionOeeList cabValidacionOeeList;

	private Estado estado = Estado.ACTIVO;
	private String descripcion;
	private List<CabValidacionOee> listaValidacionOee = new ArrayList<CabValidacionOee>();

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		nivelEntidadOeeUtil.init();
		searchAll();
	}

	public void searchAll() {
		listaValidacionOee = cabValidacionOeeList.listaResultados();

	}
	
	public void limpiar(){
		nivelEntidadOeeUtil.limpiar();
	}
	
	public void search(){
		cabValidacionOeeList.setEstado(estado);
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			cabValidacionOeeList.setConfiguracionUoCab(em.find(
					ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab()));
		if(descripcion != null && !descripcion.trim().isEmpty())
			cabValidacionOeeList.getCabValidacionOee().setNombreGrupoValidacion(descripcion);
		listaValidacionOee = cabValidacionOeeList.listaResultados();
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<CabValidacionOee> getListaValidacionOee() {
		return listaValidacionOee;
	}

	public void setListaValidacionOee(List<CabValidacionOee> listaValidacionOee) {
		this.listaValidacionOee = listaValidacionOee;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	

}
