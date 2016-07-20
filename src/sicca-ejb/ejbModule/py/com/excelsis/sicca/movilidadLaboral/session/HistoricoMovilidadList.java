package py.com.excelsis.sicca.movilidadLaboral.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.HistorialCircuitoCap;
import py.com.excelsis.sicca.entity.HistoricoMovilidad;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("historicoMovilidadList")
public class HistoricoMovilidadList extends EntityQuery<HistoricoMovilidad> {

	private static final String EJBQL = "select historicoMovilidad from HistoricoMovilidad historicoMovilidad";

	private static final String[] RESTRICTIONS = {
			"lower(historicoMovilidad.codActividad) like lower(concat(#{historicoMovilidadList.historicoMovilidad.codActividad},'%'))",
			"lower(historicoMovilidad.actividad) like lower(concat(#{historicoMovilidadList.historicoMovilidad.actividad},'%'))",
			"lower(historicoMovilidad.usuarioInicio) like lower(concat(#{historicoMovilidadList.historicoMovilidad.usuarioInicio},'%'))",
			"historicoMovilidad.idSolicitudTrasladoCab =#{historicoMovilidadList.idSolicitudTrasladoCab} ", };

	private HistoricoMovilidad historicoMovilidad;
	private Long idSolicitudTrasladoCab;
	private static final String ORDER = "historicoMovilidad.actividad";
	
	public HistoricoMovilidadList() {
		historicoMovilidad = new HistoricoMovilidad();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	public List<HistoricoMovilidad> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public HistoricoMovilidad getHistoricoMovilidad() {
		return historicoMovilidad;
	}
	public Long getIdSolicitudTrasladoCab() {
		return idSolicitudTrasladoCab;
	}
	public void setIdSolicitudTrasladoCab(Long idSolicitudTrasladoCab) {
		this.idSolicitudTrasladoCab = idSolicitudTrasladoCab;
	}
	
	
}
