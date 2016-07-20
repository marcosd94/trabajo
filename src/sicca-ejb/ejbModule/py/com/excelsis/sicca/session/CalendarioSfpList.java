package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("calendarioSfpList")
public class CalendarioSfpList extends EntityQuery<CalendarioSfp> {

	private static final String EJBQL = "select calendarioSfp from CalendarioSfp calendarioSfp";

	private static final String[] RESTRICTIONS = {
			"calendarioSfp.anho=#{calendarioSfpList.anho}",
			};

	private CalendarioSfp calendarioSfp = new CalendarioSfp();
	private static final String ORDER = "calendarioSfp.anho";
	private Integer anho;

	public CalendarioSfpList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CalendarioSfp> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<CalendarioSfp> limpiar() {
		calendarioSfp = new CalendarioSfp();
		anho = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public CalendarioSfp getCalendarioSfp() {
		return calendarioSfp;
	}

	public Integer getAnho() {
		return anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}
	
	
}
