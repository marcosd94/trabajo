package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.CapacitacionEnProceso;
import py.com.excelsis.sicca.entity.CapacitacionFinalizada;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("capacitacionEnProcesoList")
public class CapacitacionEnProcesoList extends
		EntityQuery<CapacitacionEnProceso> {

	private static final String EJBQL = "select capacitacionEnProceso from CapacitacionEnProceso capacitacionEnProceso";

	private static final String[] RESTRICTIONS = {
			"lower(capacitacionEnProceso.nombre) like lower(concat('%',concat(#{capacitacionEnProcesoList.capacitacionEnProceso.nombre},'%')))",
			" capacitacionEnProceso.datosEspecificos.idDatosEspecificos =#{capacitacionEnProcesoList.idTipoCapa} ",
			" capacitacionEnProceso.modalidad = #{capacitacionEnProcesoList.capacitacionEnProceso.modalidad} ",
			"capacitacionEnProceso.ciudad.departamento.pais.idPais  = #{capacitacionEnProcesoList.idPais} ",
			"capacitacionEnProceso.ciudad.departamento.idDepartamento  = #{capacitacionEnProcesoList.idDepartamento} ",
			"capacitacionEnProceso.ciudad.idCiudad  = #{capacitacionEnProcesoList.idCiudad} ",
			};

	private CapacitacionEnProceso capacitacionEnProceso;
	private static final String ORDER = "capacitacionEnProceso.idCapacitacion desc";
	private Long idTipoCapa=null;
	private Long idPais=null;
	private Long idDepartamento=null;
	private Long idCiudad=null;
	
	public CapacitacionEnProcesoList() {
		capacitacionEnProceso = new CapacitacionEnProceso();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CapacitacionEnProceso> listaResultados() {
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
	public List<CapacitacionEnProceso> limpiar() {
		capacitacionEnProceso= new CapacitacionEnProceso();
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
	public CapacitacionEnProceso getCapacitacionEnProceso() {
		return capacitacionEnProceso;
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
