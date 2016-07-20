package py.com.excelsis.sicca.seleccion.session;

import java.util.Arrays;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;
import py.com.excelsis.sicca.entity.ActividadProceso;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.SICCASessionParameters;


@Name("actividadProcesoList")
public class ActividadProcesoList extends EntityQuery<ActividadProceso> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6877800716194905944L;
	@In(required=false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;
	
	private static final String EJBQL = "select actividadProceso from ActividadProceso actividadProceso";
	
	private static final String[] RESTRICTIONS = {
			"actividadProceso.activo = #{actividadProcesoList.activo}",
			"actividadProceso.proceso.idProceso =#{actividadProcesoList.idProceso}",
			"actividadProceso.actividad.idActividad =#{actividadProcesoList.idActividad}",
	};
	private static final String ORDER = "actividadProceso.proceso.descripcion, actividadProceso.actividad.nroActividad";
	
	private Boolean activo = true;
	private Long idProceso;
	private Long idActividad;
	private Estado estado = Estado.ACTIVO;

	private ActividadProceso actividadProceso = new ActividadProceso();

	public ActividadProcesoList() {
		activo = estado.getValor();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<ActividadProceso> buscarResultados(){
		activo = estado.getValor();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}

	public List<ActividadProceso> limpiarResultados(){
		actividadProceso = new ActividadProceso();
		estado = Estado.ACTIVO;
		activo = estado.getValor();
		idProceso = null;
		idActividad = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}
	
	
//	GETTERS Y SETTERS

    public ActividadProceso getActividadProceso() {
		return actividadProceso;
	}


    public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Boolean getActivo() {
		return activo;
	}

	public Long getIdProceso() {
		return idProceso;
	}

	public void setIdProceso(Long idProceso) {
		this.idProceso = idProceso;
	}

	public Long getIdActividad() {
		return idActividad;
	}

	public void setIdActividad(Long idActividad) {
		this.idActividad = idActividad;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Estado getEstado() {
		return estado;
	}
	
	
}

