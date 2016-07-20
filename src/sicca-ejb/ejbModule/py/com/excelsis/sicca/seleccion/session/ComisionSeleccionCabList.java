package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("comisionSeleccionCabList")
public class ComisionSeleccionCabList extends EntityQuery<ComisionSeleccionCab> {

	private static final String EJBQL = "select comisionSeleccionCab from ComisionSeleccionCab comisionSeleccionCab";

	private static final String[] RESTRICTIONS = {
			"lower(comisionSeleccionCab.descripcion) like concat('%',lower(concat(#{comisionSeleccionCabList.comisionSeleccionCab.descripcion},'%')))",
			"lower(comisionSeleccionCab.usuAlta) like lower(concat(#{comisionSeleccionCabList.comisionSeleccionCab.usuAlta},'%'))",
			"lower(comisionSeleccionCab.usuMod) like lower(concat(#{comisionSeleccionCabList.comisionSeleccionCab.usuMod},'%'))",
			"comisionSeleccionCab.concurso.idConcurso = #{comisionSeleccionCabList.idConcurso} ",
			};

	private ComisionSeleccionCab comisionSeleccionCab = new ComisionSeleccionCab();
	private static final String ORDER = "comisionSeleccionCab.descripcion";
	private Long idConcurso;
	public ComisionSeleccionCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
	}

	public ComisionSeleccionCab getComisionSeleccionCab() {
		return comisionSeleccionCab;
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<ComisionSeleccionCab> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<ComisionSeleccionCab> listaResultadosCU165() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(null);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<ComisionSeleccionCab> limpiar() {
		comisionSeleccionCab= new ComisionSeleccionCab();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public List<ComisionSeleccionCab> limpiarCU165() {
		comisionSeleccionCab= new ComisionSeleccionCab();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(null);
		return getResultList();
	}
	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}
	
	
}
