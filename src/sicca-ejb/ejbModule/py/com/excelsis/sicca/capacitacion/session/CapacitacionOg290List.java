package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.CapacitacionFinalizada;
import py.com.excelsis.sicca.entity.CapacitacionOg290;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("capacitacionOg290List")
public class CapacitacionOg290List extends EntityQuery<CapacitacionOg290> {

	private static final String EJBQL = "select capacitacionOg290 from CapacitacionOg290 capacitacionOg290";

	private static final String[] RESTRICTIONS = {
			"lower(capacitacionOg290.configuracionUoCab.denominacionUnidad) like lower(concat('%',concat(#{capacitacionOg290List.entidadOEE},'%'))) ",
			"lower(capacitacionOg290.nombre) like lower(concat('%',concat(#{capacitacionOg290List.capacitacionOg290.nombre},'%')))",
			"lower(capacitacionOg290.descripcion) like lower(concat(#{capacitacionOg290List.capacitacionOg290.descripcion},'%'))",
			"capacitacionOg290.modalidad = #{capacitacionOg290List.capacitacionOg290.modalidad} ", 
			"capacitacionOg290.ciudad.departamento.pais.idPais  = #{capacitacionOg290List.idPais} ",
			"capacitacionOg290.ciudad.departamento.idDepartamento  = #{capacitacionOg290List.idDepartamento} ",
			"capacitacionOg290.ciudad.idCiudad  = #{capacitacionOg290List.idCiudad} ",
			"capacitacionOg290.datosEspecificos.idDatosEspecificos = #{capacitacionOg290List.idTipoCapa} ",
	};

	private CapacitacionOg290 capacitacionOg290;
	private static final String ORDER = "capacitacionOg290.fechaPubDesde desc";
	private Long idTipoCapa=null;
	private Long idPais=null;
	private Long idDepartamento=null;
	private Long idCiudad=null;
	private String entidadOEE;

	public CapacitacionOg290List() {
		capacitacionOg290 = new CapacitacionOg290();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CapacitacionOg290> listaResultados() {
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
	public List<CapacitacionOg290> limpiar() {
		capacitacionOg290= new CapacitacionOg290();
		idCiudad=null;
		idDepartamento=null;
		idPais=null;
		idTipoCapa=null;
		entidadOEE=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public CapacitacionOg290 getCapacitacionOg290() {
		return capacitacionOg290;
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
	public String getEntidadOEE() {
		return entidadOEE;
	}
	public void setEntidadOEE(String entidadOEE) {
		this.entidadOEE = entidadOEE;
	}
	

	
}
