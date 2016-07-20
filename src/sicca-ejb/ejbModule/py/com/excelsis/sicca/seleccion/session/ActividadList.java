package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("actividadList")
public class ActividadList extends EntityQuery<Actividad> {

	private static final String EJBQL = "select actividad from Actividad actividad";

	private static final String[] RESTRICTIONS = {
			"lower(actividad.descripcion) like lower(concat('%', concat(#{actividadList.actividad.descripcion},'%')))",
			"actividad.activo = #{actividadList.actividad.activo}",
			"lower(actividad.usuAlta) lower(concat('%', concat(concat(#{actividadList.actividad.usuAlta},'%')))",
			"lower(actividad.usuMod) lower(concat('%', concat(#{actividadList.actividad.usuMod},'%')))", };
	
	private static final String ORDER = "actividad.nroActividad";

	private Actividad actividad = new Actividad();
	private Estado estado = Estado.ACTIVO;
	
	public ActividadList() {
		actividad.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
	}
	
	public List<Actividad> buscarResultados(){
		actividad.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}

	public List<Actividad> limpiarResultados(){
		actividad = new Actividad();
		estado = Estado.ACTIVO;
		actividad.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}

	public Actividad getActividad() {
		return actividad;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Estado getEstado() {
		return estado;
	}
}
