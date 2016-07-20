package py.com.excelsis.sicca.movilidadLaboral.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("empleadoMovilidadCabList")
public class EmpleadoMovilidadCabList extends EntityQuery<EmpleadoMovilidadCab> {

	private static final String EJBQL = "select empleadoMovilidadCab from EmpleadoMovilidadCab empleadoMovilidadCab where empleadoMovilidadCab.empleadoPuestoVirtual is null ";
	private static final String[] RESTRICTIONS = {
			"lower(empleadoMovilidadCab.empleadoPuestoAnterior.datosEspecificosByIdDatosEspTipoIngresoMovilidad.descripcion) like lower(concat(#{empleadoMovilidadCabList.tipoIngMovilidad},'%'))",
			" empleadoMovilidadCab.empleadoPuestoAnterior.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo =#{empleadoMovilidadCabList.idConfiguracionUo}  ",
		//	" empleadoMovilidadCab.empleadoPuestoVirtual.idEmpleadoPuesto not in(select virtual from EmpleadoPuesto virtual where 1=#{empleadoMovilidadCabList.var})"
			};

	private EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
	private static final String ORDER = "empleadoMovilidadCab.empleadoPuestoNuevo.persona.nombres,empleadoMovilidadCab.empleadoPuestoNuevo.persona.apellidos";
	private String tipoIngMovilidad;
	private Long idConfiguracionUo;
	private Integer var=1;
	public EmpleadoMovilidadCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.para el CU 748
	 */
	public List<EmpleadoMovilidadCab> listaResultados748() {
		tipoIngMovilidad="TRASLADO TEMPORAL";
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
 		return getResultList();
	}


	public EmpleadoMovilidadCab getEmpleadoMovilidadCab() {
		return empleadoMovilidadCab;
	}
	public String getTipoIngMovilidad() {
		return tipoIngMovilidad;
	}
	public void setTipoIngMovilidad(String tipoIngMovilidad) {
		this.tipoIngMovilidad = tipoIngMovilidad;
	}
	public Long getIdConfiguracionUo() {
		return idConfiguracionUo;
	}
	public void setIdConfiguracionUo(Long idConfiguracionUo) {
		this.idConfiguracionUo = idConfiguracionUo;
	}
	public Integer getVar() {
		return var;
	}
	public void setVar(Integer var) {
		this.var = var;
	}
	
}
