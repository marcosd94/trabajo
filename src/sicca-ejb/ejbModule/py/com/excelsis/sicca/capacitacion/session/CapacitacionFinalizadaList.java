package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.CapacitacionFinalizada;
import py.com.excelsis.sicca.entity.CapacitacionPocPost;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("capacitacionFinalizadaList")
public class CapacitacionFinalizadaList extends
		EntityQuery<CapacitacionFinalizada> {

	private static final String EJBQL = "select capacitacionFinalizada from CapacitacionFinalizada capacitacionFinalizada";

	private static final String[] RESTRICTIONS = {
			"lower(capacitacionFinalizada.nombre) like lower(concat('%',concat(#{capacitacionFinalizadaList.capacitacionFinalizada.nombre},'%')))",
			"capacitacionFinalizada.datosEspecificos.idDatosEspecificos = #{capacitacionFinalizadaList.idTipoCapa} ",
			"capacitacionFinalizada.ciudad.departamento.pais.idPais  = #{capacitacionFinalizadaList.idPais} ",
			"capacitacionFinalizada.ciudad.departamento.idDepartamento  = #{capacitacionFinalizadaList.idDepartamento} ",
			"capacitacionFinalizada.ciudad.idCiudad  = #{capacitacionFinalizadaList.idCiudad} ",
			"capacitacionFinalizada.modalidalidad = #{capacitacionFinalizadaList.capacitacionFinalizada.modalidalidad} ",
			};

	private CapacitacionFinalizada capacitacionFinalizada;
	private static final String ORDER = "capacitacionFinalizada.idCapacitacion desc";
	private Long idTipoCapa=null;
	private Long idPais=null;
	private Long idDepartamento=null;
	private Long idCiudad=null;
	public CapacitacionFinalizadaList() {
		capacitacionFinalizada = new CapacitacionFinalizada();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CapacitacionFinalizada> listaResultados() {
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
	public List<CapacitacionFinalizada> limpiar() {
		capacitacionFinalizada= new CapacitacionFinalizada();
		idCiudad=null;
		idDepartamento=null;
		idPais=null;
		idTipoCapa=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public CapacitacionFinalizada getCapacitacionFinalizada() {
		return capacitacionFinalizada;
	}
	public Long getIdTipoCapa() {
		return idTipoCapa;
	}
	public void setIdTipoCapa(Long idTipoCapa) {
		this.idTipoCapa = idTipoCapa;
	}
	public Long getIdPais() {
		return idPais;
	}
	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}
	public Long getIdDepartamento() {
		return idDepartamento;
	}
	public void setIdDepartamento(Long idDepartamento) {
		this.idDepartamento = idDepartamento;
	}
	public Long getIdCiudad() {
		return idCiudad;
	}
	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}
	
	
}
