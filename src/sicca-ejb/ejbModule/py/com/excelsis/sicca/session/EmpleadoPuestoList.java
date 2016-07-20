package py.com.excelsis.sicca.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Ternas;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("empleadoPuestoList")
public class EmpleadoPuestoList extends EntityQuery<EmpleadoPuesto> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<EmpleadoPuesto> listaEmpleadoPuestos;
	
	@In(value = "entityManager")
	EntityManager em;

	private static final String EJBQL = "select empleadoPuesto from EmpleadoPuesto empleadoPuesto";

	private static final String[] RESTRICTIONS = {
			"lower(empleadoPuesto.usuAlta) like lower(concat(#{empleadoPuestoList.empleadoPuesto.usuAlta},'%'))",
			"lower(empleadoPuesto.usuMod) like lower(concat(#{empleadoPuestoList.empleadoPuesto.usuMod},'%'))", };

	private static final String[] RESTRICTIONSCU429 = {
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{empleadoPuestoList.sinEntidad.idSinEntidad}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{empleadoPuestoList.sinEntidad.nenCodigo}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{empleadoPuestoList.configuracionUoCab.idConfiguracionUo}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.idConfiguracionUoDet=#{empleadoPuestoList.configuracionUoDet.idConfiguracionUoDet}",
			"empleadoPuesto.actual=#{empleadoPuestoList.valor}",
			"empleadoPuesto.contratado=#{empleadoPuestoList.valor}",
			"lower(empleadoPuesto.persona.nombres) like lower(concat('%', concat(#{empleadoPuestoList.persona.nombres},'%')))",
			"lower(empleadoPuesto.persona.apellidos) like lower(concat('%', concat(#{empleadoPuestoList.persona.apellidos},'%')))",
			"empleadoPuesto.persona.documentoIdentidad=#{empleadoPuestoList.persona.documentoIdentidad}",
			"date(empleadoPuesto.fechaFinContrato) BETWEEN #{empleadoPuestoList.fechaDesde}",
			" #{empleadoPuestoList.fechaHasta}", };

	private static final String[] RESTRICTIONSCU159 = {
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{empleadoPuestoList.sinEntidad.idSinEntidad}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{empleadoPuestoList.nenCodigo}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{empleadoPuestoList.configuracionUoCab.idConfiguracionUo}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.idConfiguracionUoDet=#{empleadoPuestoList.configuracionUoDet.idConfiguracionUoDet}",
			"empleadoPuesto.actual=#{empleadoPuestoList.valor}",
			"lower(empleadoPuesto.persona.nombres) like lower(concat('%', concat(#{empleadoPuestoList.persona.nombres},'%')))",
			"lower(empleadoPuesto.persona.apellidos) like lower(concat('%', concat(#{empleadoPuestoList.persona.apellidos},'%')))",
			"empleadoPuesto.persona.documentoIdentidad=#{empleadoPuestoList.persona.documentoIdentidad}", };
	
	private static final String[] RESTRICTIONSReasignacionInsercionMasiva = {
		"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{empleadoPuestoList.sinEntidad.idSinEntidad}",
		"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{empleadoPuestoList.nenCodigo}",
		"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{empleadoPuestoList.configuracionUoCab.idConfiguracionUo}",
		"empleadoPuesto.plantaCargoDet.configuracionUoDet.idConfiguracionUoDet=#{empleadoPuestoList.configuracionUoDet.idConfiguracionUoDet}",
		"empleadoPuesto.actual=#{empleadoPuestoList.valor}",
		"empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos=#{empleadoPuestoList.movilidadInsercionMasiva}",
		"lower(empleadoPuesto.persona.nombres) like lower(concat('%', concat(#{empleadoPuestoList.persona.nombres},'%')))",
		"lower(empleadoPuesto.persona.apellidos) like lower(concat('%', concat(#{empleadoPuestoList.persona.apellidos},'%')))",
		"empleadoPuesto.persona.documentoIdentidad=#{empleadoPuestoList.persona.documentoIdentidad}", 
		};

	private static final String[] RESTRICTIONSCU541 = {
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{empleadoPuestoList.sinEntidad.idSinEntidad}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{empleadoPuestoList.sinEntidad.nenCodigo}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{empleadoPuestoList.configuracionUoCab.idConfiguracionUo}",
			"empleadoPuesto.plantaCargoDet.permanente=#{empleadoPuestoList.permanente}",
			"lower(empleadoPuesto.persona.nombres) like lower(concat('%', concat(#{empleadoPuestoList.persona.nombres},'%')))",
			"lower(empleadoPuesto.persona.apellidos) like lower(concat('%', concat(#{empleadoPuestoList.persona.apellidos},'%')))",
			"empleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais =#{empleadoPuestoList.idPais} ",
			"empleadoPuesto.persona.documentoIdentidad=#{empleadoPuestoList.persona.documentoIdentidad}",
			"empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos=#{empleadoPuestoList.empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos}",
			"date(empleadoPuesto.fechaInicio) >= date(#{empleadoPuestoList.fechaDesde})",
			"date(empleadoPuesto.fechaInicio) <= date(#{empleadoPuestoList.fechaHasta})", };

	private static final String[] RESTRICTIONSCU586 = {
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{empleadoPuestoList.sinEntidad.idSinEntidad}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{empleadoPuestoList.sinEntidad.nenCodigo}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{empleadoPuestoList.configuracionUoCab.idConfiguracionUo}",
			"empleadoPuesto.plantaCargoDet.permanente=#{empleadoPuestoList.permanente}",
			"lower(empleadoPuesto.persona.nombres) like lower(concat('%', concat(#{empleadoPuestoList.persona.nombres},'%')))",
			"lower(empleadoPuesto.persona.apellidos) like lower(concat('%', concat(#{empleadoPuestoList.persona.apellidos},'%')))",
			"empleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais =#{empleadoPuestoList.idPais} ",
			"empleadoPuesto.persona.documentoIdentidad=#{empleadoPuestoList.persona.documentoIdentidad}",
			"empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos=#{empleadoPuestoList.empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos}",
			"date(empleadoPuesto.fechaFinContrato) >= date(#{empleadoPuestoList.fechaDesde})",
			"date(empleadoPuesto.fechaFinContrato) <= date(#{empleadoPuestoList.fechaHasta})", };

	private static final String[] RESTRICTIONSCU587 = {
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{empleadoPuestoList.sinEntidad.idSinEntidad}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{empleadoPuestoList.sinEntidad.nenCodigo}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{empleadoPuestoList.configuracionUoCab.idConfiguracionUo}",
			"lower(empleadoPuesto.persona.nombres) like lower(concat('%', concat(#{empleadoPuestoList.persona.nombres},'%')))",
			"lower(empleadoPuesto.persona.apellidos) like lower(concat('%', concat(#{empleadoPuestoList.persona.apellidos},'%')))",
			"empleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais =#{empleadoPuestoList.idPais} ",
			"empleadoPuesto.persona.documentoIdentidad=#{empleadoPuestoList.persona.documentoIdentidad}",
			"empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos=#{empleadoPuestoList.empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos}",
			"date(empleadoPuesto.fechaFinContrato) >= date(#{empleadoPuestoList.fechaDesde})",
			"date(empleadoPuesto.fechaFinContrato) <= date(#{empleadoPuestoList.fechaHasta})", };

	private static final String[] RESTRICTIONSCU585 = {
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{empleadoPuestoList.sinEntidad.idSinEntidad}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{empleadoPuestoList.sinEntidad.nenCodigo}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{empleadoPuestoList.configuracionUoCab.idConfiguracionUo}",
			"empleadoPuesto.plantaCargoDet.permanente=#{empleadoPuestoList.permanente}",
			"lower(empleadoPuesto.persona.nombres) like lower(concat('%', concat(#{empleadoPuestoList.persona.nombres},'%')))",
			"lower(empleadoPuesto.persona.apellidos) like lower(concat('%', concat(#{empleadoPuestoList.persona.apellidos},'%')))",
			"empleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais =#{empleadoPuestoList.idPais} ",
			"empleadoPuesto.persona.documentoIdentidad=#{empleadoPuestoList.persona.documentoIdentidad}",
			"empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos=#{empleadoPuestoList.empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos}",
			"date(empleadoPuesto.fechaFinContrato) >= date(#{empleadoPuestoList.fechaDesde})",
			"date(empleadoPuesto.fechaFinContrato) <= date(#{empleadoPuestoList.fechaHasta})", };
	
	private static final String[] RESTRICTIONSCU708 = {
		"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{empleadoPuestoList.sinEntidad.idSinEntidad}",
		"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{empleadoPuestoList.sinEntidad.nenCodigo}",
		"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{empleadoPuestoList.configuracionUoCab.idConfiguracionUo}",
		"empleadoPuesto.plantaCargoDet.descripcion not like #{empleadoPuestoList.descripcion}",
		"lower(empleadoPuesto.persona.nombres) like lower(concat('%', concat(#{empleadoPuestoList.persona.nombres},'%')))",
		"lower(empleadoPuesto.persona.apellidos) like lower(concat('%', concat(#{empleadoPuestoList.persona.apellidos},'%')))",
		"empleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais =#{empleadoPuestoList.idPais} ",
		"empleadoPuesto.persona.documentoIdentidad=#{empleadoPuestoList.persona.documentoIdentidad}",
		"empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos=#{empleadoPuestoList.empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos}",
		"date(empleadoPuesto.fechaFinContrato) >= date(#{empleadoPuestoList.fechaDesde})",
		"date(empleadoPuesto.fechaFinContrato) <= date(#{empleadoPuestoList.fechaHasta})", };
	

	private static final String[] RESTRICTIONSCU667 = {
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{empleadoPuestoList.sinEntidad.idSinEntidad}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{empleadoPuestoList.sinEntidad.nenCodigo}",
			"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{empleadoPuestoList.configuracionUoCab.idConfiguracionUo}",
			"empleadoPuesto.plantaCargoDet.permanente=#{empleadoPuestoList.permanente}",
			"lower(empleadoPuesto.persona.nombres) like lower(concat('%', concat(#{empleadoPuestoList.persona.nombres},'%')))",
			"lower(empleadoPuesto.persona.apellidos) like lower(concat('%', concat(#{empleadoPuestoList.persona.apellidos},'%')))",
			"empleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais =#{empleadoPuestoList.idPais} ",
			"empleadoPuesto.persona.documentoIdentidad=#{empleadoPuestoList.persona.documentoIdentidad}",
			"empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos in (#{empleadoPuestoList.idDatosEspecificos})",
			"date(empleadoPuesto.fechaInicio) >= date(#{empleadoPuestoList.fechaDesde})",
			"date(empleadoPuesto.fechaInicio) <= date(#{empleadoPuestoList.fechaHasta})", };
	
	private static final String[] RESTRICTIONSCU663 = {
		"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{empleadoPuestoList.sinEntidad.idSinEntidad}",
		"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{empleadoPuestoList.sinEntidad.nenCodigo}",
		"empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{empleadoPuestoList.configuracionUoCab.idConfiguracionUo}",
		"lower(empleadoPuesto.persona.nombres) like lower(concat('%', concat(#{empleadoPuestoList.persona.nombres},'%')))",
		"lower(empleadoPuesto.persona.apellidos) like lower(concat('%', concat(#{empleadoPuestoList.persona.apellidos},'%')))",
		"empleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais =#{empleadoPuestoList.idPais} ",
		"empleadoPuesto.persona.documentoIdentidad=#{empleadoPuestoList.persona.documentoIdentidad}",
		"empleadoPuesto.datosEspecificosByIdDatosEspTipoIngresoMovilidad.idDatosEspecificos=#{empleadoPuestoList.idTipoIngreso}",
		"date(empleadoPuesto.fechaFinContrato) >= date(#{empleadoPuestoList.fechaDesde})",
		"date(empleadoPuesto.fechaFinContrato) <= date(#{empleadoPuestoList.fechaHasta})", };
	

	private EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
	private static final String ORDER = "empleadoPuesto.fechaFinContrato";
	private static final String ORDER_CU159 = "empleadoPuesto.persona.documentoIdentidad";
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();
	private BigDecimal nenCodigo;
	private Long idTipoIngreso;
	private Persona persona;
	private Boolean valor = true;
	private Date fechaDesde = null;
	private Date fechaHasta = null;
	private Boolean permanente = true;
	private String descripcion = "EN ESPERA DE NUEVAS FUNCIONES";
	private Long idPais;
	private Long movilidadInsercionMasiva = (long) 1139;

	private List<Long> idDatosEspecificos= new ArrayList<Long>();

	public EmpleadoPuestoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<EmpleadoPuesto> listaResultadosCU429() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU429));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<EmpleadoPuesto> listaResultadosCU159() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU159));
		setOrder(ORDER_CU159);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public List<EmpleadoPuesto> listaResultadosReasignacionInsercionMasiva() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSReasignacionInsercionMasiva));
		setOrder(ORDER_CU159);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public List<EmpleadoPuesto> listaResultadosContratosAVencer(String ejbql) {
		setEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		List<EmpleadoPuesto> lista = getResultList();
		return lista;
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda del CU 541
	 */
	public List<EmpleadoPuesto> listaResultadosCU541() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU541));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda del CU 667
	 */
	public List<EmpleadoPuesto> listaResultadosCU667() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU667));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda del CU 586
	 */
	public List<EmpleadoPuesto> listaResultadosCU586() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU586));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda del CU 587
	 */
	public List<EmpleadoPuesto> listaResultadosCU587() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU587));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda del CU 708
	 */
	public List<EmpleadoPuesto> listaResultadosCU708() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU708));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	
	/**
	 * 
	 * @return la lista resultado de la búsqueda del CU 663
	 */
	public List<EmpleadoPuesto> listaResultadosCU663() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU663));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}


	/**
	 * 
	 * @return la lista resultado de la búsqueda del CU 585
	 */
	public List<EmpleadoPuesto> listaResultadosCU585() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU585));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public List<EmpleadoPuesto> obtenerListaEmpleadoPuestos() {
		listaEmpleadoPuestos = getResultList();
		return listaEmpleadoPuestos;
	}

	public List<EmpleadoPuesto> getListaEmpleadoPuestos() {
		return listaEmpleadoPuestos;
	}

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Boolean getValor() {
		return valor;
	}

	public void setValor(Boolean valor) {
		this.valor = valor;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public BigDecimal getNenCodigo() {
		return nenCodigo;
	}

	public void setNenCodigo(BigDecimal nenCodigo) {
		this.nenCodigo = nenCodigo;
	}

	public Boolean getPermanente() {
		return permanente;
	}

	public void setPermanente(Boolean permanente) {
		this.permanente = permanente;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<Long> getIdDatosEspecificos() {
		return idDatosEspecificos;
	}

	public void setIdDatosEspecificos(List<Long> idDatosEspecificos) {
		this.idDatosEspecificos = idDatosEspecificos;
	}

	public Long getIdTipoIngreso() {
		return idTipoIngreso;
	}

	public void setIdTipoIngreso(Long idTipoIngreso) {
		this.idTipoIngreso = idTipoIngreso;
	}
	
	public Long getMovilidadInsercionMasiva() {
		return movilidadInsercionMasiva;
	}

	public void setMovilidadInsercionMasiva(Long movilidadInsercionMasiva) {
		this.movilidadInsercionMasiva = movilidadInsercionMasiva;
	}

}
