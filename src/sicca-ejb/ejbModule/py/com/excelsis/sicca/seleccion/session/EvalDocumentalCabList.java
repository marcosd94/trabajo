package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("evalDocumentalCabList")
public class EvalDocumentalCabList extends CustomEntityQuery<EvalDocumentalCab> {

	private static final String EJBQL = "select evalDocumentalCab from EvalDocumentalCab evalDocumentalCab join evalDocumentalCab.postulacion postulacion";

	private static final String[] RESTRICTIONS = {
			"date(evalDocumentalCab.fechaEvaluacion) >= #{evalDocumentalCabList.fechaDesde}",
			"date(evalDocumentalCab.fechaEvaluacion) <= #{evalDocumentalCabList.fechaHasta}",
			"evalDocumentalCab.concursoPuestoAgr.idConcursoPuestoAgr = #{evalDocumentalCabList.idConcursoPuestoAgr}",
			"lower(evalDocumentalCab.observacion) like lower(concat('%', concat(#{evalDocumentalCabList.evalDocumentalCab.observacion},'%')))", };

	private EvalDocumentalCab evalDocumentalCab = new EvalDocumentalCab();
	private static final String ORDER = "postulacion.usuPostulacion";
	private Date fechaDesde;
	private Date fechaHasta;
	private Long idConcursoPuestoAgr;

	public EvalDocumentalCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT_EVAL_DOC);
		setOrder(ORDER);
	}

	public List<EvalDocumentalCab> buscarResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT_EVAL_DOC);
		return getResultList();
	}

	public List<EvalDocumentalCab> limpiarResultados() {
		evalDocumentalCab = new EvalDocumentalCab();
		fechaDesde = null;
		fechaHasta = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT_EVAL_DOC);
		return getResultList();
	}
public List<EvalDocumentalCab> listaResultados(String ejbql) {
		
		//setEjbql(ejbql);

		setCustomEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT_EVAL_DOC);
		setOrder(ORDER);
		List<EvalDocumentalCab> lista = getResultList();
		return lista;
	}

	public EvalDocumentalCab getEvalDocumentalCab() {
		return evalDocumentalCab;
	}

	public Date getFechaDesde() {
		if (fechaDesde != null) {
			GregorianCalendar gc1 = new GregorianCalendar();
			gc1.setTime(fechaDesde);
			gc1.clear(GregorianCalendar.HOUR);
			gc1.clear(GregorianCalendar.MILLISECOND);
			gc1.clear(GregorianCalendar.MINUTE);
			fechaDesde = gc1.getTime();
		}
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		if (fechaHasta != null) {
			GregorianCalendar gc1 = new GregorianCalendar();
			gc1.setTime(fechaHasta);
			gc1.clear(GregorianCalendar.HOUR);
			gc1.clear(GregorianCalendar.MILLISECOND);
			gc1.clear(GregorianCalendar.MINUTE);
			fechaHasta = gc1.getTime();
		}
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}
	

}
