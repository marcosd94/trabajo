package py.com.excelsis.sicca.juridicos.session;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Excepcionados;
import py.com.excelsis.sicca.entity.Oficina;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import enums.Estado;

@Scope(ScopeType.CONVERSATION)
@Name("excepcionadosList")
public class ExcepcionadosList extends EntityQuery<Excepcionados> {

	private static final String EJBQL = "select excepcionados from Excepcionados excepcionados";

	private static final String[] RESTRICTIONS = {
			"lower(excepcionados.nroDictamen) like lower(concat(#{excepcionadosList.excepcionados.nroDictamen},'%'))",
			"lower(excepcionados.motivo) like lower(concat(#{excepcionadosList.excepcionados.motivo},'%'))",
			"excepcionados.usuAlta like #{excepcionadosList.excepcionados.usuAlta}",
			"lower(excepcionados.usuMod) like lower(concat(#{excepcionadosList.excepcionados.usuMod},'%'))",
			"excepcionados.configuracionUoCab.entidad.sinEntidad.nenCodigo = #{excepcionadosList.nenCodigo} ",
			"excepcionados.configuracionUoCab.entidad.sinEntidad.entCodigo = #{excepcionadosList.entCodigo} ",
			"excepcionados.configuracionUoCab.entidad.sinEntidad.idSinEntidad = #{excepcionadosList.idSinEntidad} ",
			"excepcionados.configuracionUoCab.idConfiguracionUo =#{excepcionadosList.configuracionUoCab.idConfiguracionUo} ",
			"excepcionados.anio =#{excepcionadosList.excepcionados.anio} ",
			"lower(excepcionados.persona.nombres) like concat('%',lower(concat(#{excepcionadosList.excepcionados.persona.nombres},'%')))",
			"lower(excepcionados.persona.apellidos) like concat('%',lower(concat(#{excepcionadosList.excepcionados.persona.apellidos},'%')))",
			"excepcionados.persona.documentoIdentidad = #{excepcionadosList.excepcionados.persona.documentoIdentidad} ",
			"excepcionados.persona.paisByIdPaisExpedicionDoc.idPais = #{excepcionadosList.idPaisExp} ",
			"excepcionados.activo=#{excepcionadosList.estado.valor}",
			"excepcionados.fecha >= #{excepcionadosList.fecExpDesde} ",
			"excepcionados.fecha <= #{excepcionadosList.fecExpHasta} ",
			"excepcionados.fechaAlta >= #{excepcionadosList.fecUsuDesde} ",
			"excepcionados.fechaAlta <= #{excepcionadosList.fecUsuHasta} ",
	};

	private Excepcionados excepcionados = new Excepcionados();
	private ConfiguracionUoCab configuracionUoCab= new ConfiguracionUoCab();
	private BigDecimal nenCodigo=null;
	private BigDecimal entCodigo=null;
	private Estado estado= Estado.ACTIVO;
	private Long idSinEntidad;
	private Long idPaisExp;
	private Date fecExpDesde;
	private Date fecExpHasta;
	private Date fecUsuDesde;
	private Date fecUsuHasta;


	private static final String ORDER = "excepcionados.anio";
	
	public ExcepcionadosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Excepcionados> listaResultados() {
		setEjbql(EJBQL);
		System.out.println(excepcionados.getPersona().getApellidos()+"*"+excepcionados.getPersona().getNombres());
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<Excepcionados> limpiar() {
		excepcionados= new Excepcionados();
		estado = Estado.ACTIVO;
		nenCodigo=null;
		configuracionUoCab= new ConfiguracionUoCab();
		entCodigo= null;
		idSinEntidad=null;
		fecExpDesde=null;
		fecExpHasta=null;
		fecUsuDesde=null;
		fecUsuHasta=null;
		idPaisExp=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public Excepcionados getExcepcionados() {
		return excepcionados;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public BigDecimal getNenCodigo() {
		return nenCodigo;
	}

	public void setNenCodigo(BigDecimal nenCodigo) {
		this.nenCodigo = nenCodigo;
	}

	public BigDecimal getEntCodigo() {
		return entCodigo;
	}

	public void setEntCodigo(BigDecimal entCodigo) {
		this.entCodigo = entCodigo;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public Date getFecExpDesde() {
		return fecExpDesde;
	}

	public void setFecExpDesde(Date fecExpDesde) {
		this.fecExpDesde = fecExpDesde;
	}

	public Date getFecExpHasta() {
		return fecExpHasta;
	}

	public void setFecExpHasta(Date fecExpHasta) {
		this.fecExpHasta = fecExpHasta;
	}

	public Date getFecUsuDesde() {
		return fecUsuDesde;
	}

	public void setFecUsuDesde(Date fecUsuDesde) {
		this.fecUsuDesde = fecUsuDesde;
	}

	public Date getFecUsuHasta() {
		return fecUsuHasta;
	}

	public void setFecUsuHasta(Date fecUsuHasta) {
		this.fecUsuHasta = fecUsuHasta;
	}


	
	
	
}
