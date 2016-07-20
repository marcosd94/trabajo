package py.com.excelsis.sicca.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EntidadOee;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("entidadOeeList")
public class EntidadOeeList extends EntityQuery<EntidadOee> {

	private static final String EJBQL = "select entidadOee from EntidadOee entidadOee";

	private static final String[] RESTRICTIONS = {
		" entidadOee.nenCodigo =#{entidadOeeList.entidadOee.nenCodigo} ",
		" entidadOee.sinEntidad.aniAniopre =#{entidadOeeList.sinEntidad.aniAniopre} ",
		" entidadOee.anho =#{entidadOeeList.entidadOee.anho} ",
		" entidadOee.sinEntidad.idSinEntidad =#{entidadOeeList.sinEntidad.idSinEntidad} ",
		" entidadOee.configuracionUoCab.orden =#{entidadOeeList.configuracionUoCab.orden} ",
		" lower(entidadOee.configuracionUoCab.denominacionUnidad) like lower(concat('%',lower(concat(#{entidadOeeList.configuracionUoCab.denominacionUnidad},'%')))) ",
		" date(entidadOee.configuracionUoCab.vigenciaDesde) >= #{entidadOeeList.configuracionUoCab.vigenciaDesde}",
		" date(entidadOee.configuracionUoCab.vigenciaHasta)<= #{entidadOeeList.configuracionUoCab.vigenciaDesde}",
		" entidadOee.configuracionUoCab.activo=#{entidadOeeList.estado.valor}",
	};

	private EntidadOee entidadOee = new EntidadOee();
	private ConfiguracionUoCab configuracionUoCab= new ConfiguracionUoCab();
	private SinEntidad sinEntidad= new SinEntidad();
	private static final String ORDER = " entidadOee.configuracionUoCab.denominacionUnidad ";
	private Estado estado= Estado.ACTIVO;

	public EntidadOeeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<EntidadOee> listaResultados() {
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
	public List<EntidadOee> limpiar() {
		entidadOee=new EntidadOee();
		configuracionUoCab=new ConfiguracionUoCab();
		sinEntidad=new SinEntidad();
		estado= Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public EntidadOee getEntidadOee() {
		return entidadOee;
	}

	
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	
}
