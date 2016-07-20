package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.RedCapacitacion;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Name("redCapacitacionList")
public class RedCapacitacionList extends EntityQuery<RedCapacitacion> {

	private static final String EJBQL = "select redCapacitacion from RedCapacitacion redCapacitacion";

	private static final String[] RESTRICTIONS = {
			"lower(redCapacitacion.persona.nombres) like lower(concat('%',concat(#{redCapacitacionList.persona.nombres},'%')))",
			"lower(redCapacitacion.persona.apellidos) like lower(concat('%',concat(#{redCapacitacionList.persona.apellidos},'%')))",
			" redCapacitacion.persona.documentoIdentidad = #{redCapacitacionList.persona.documentoIdentidad} ",
			" redCapacitacion.persona.paisByIdPaisExpedicionDoc.idPais = #{redCapacitacionList.idPais} ", 
			"redCapacitacion.configuracionUoCab.entidad.sinEntidad.nenCodigo = #{redCapacitacionList.nenCodigo} ",
			"redCapacitacion.configuracionUoCab.entidad.sinEntidad.entCodigo = #{redCapacitacionList.entCodigo} ",
			"redCapacitacion.configuracionUoCab.entidad.sinEntidad.idSinEntidad = #{redCapacitacionList.idSinEntidad} ",
			"redCapacitacion.configuracionUoCab.idConfiguracionUo =#{redCapacitacionList.idConfiguracionUo} ",
			"redCapacitacion.activo =#{redCapacitacionList.redCapacitacion.activo} ",};

	private RedCapacitacion redCapacitacion = new RedCapacitacion();
	private static final String ORDER = "redCapacitacion.persona.documentoIdentidad";
	private Persona persona= new Persona();
	private BigDecimal nenCodigo=null;
	private BigDecimal entCodigo=null;
	private Long idSinEntidad=null;
	private Long idConfiguracionUo=null;
	private Long idPais=null;
	private Estado estado= Estado.ACTIVO;
	
	public RedCapacitacionList() {
		redCapacitacion.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<RedCapacitacion> listaResultados() {
		redCapacitacion.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa. cu 489
	 */
	public List<RedCapacitacion> limpiarCU489() {
		redCapacitacion= new RedCapacitacion(); 
		estado = Estado.ACTIVO;
		redCapacitacion.setActivo(estado.getValor());
		idConfiguracionUo=null;
		nenCodigo=null;
		entCodigo=null;
		idSinEntidad=null;
		idPais=null;
		persona= new Persona();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	

	public RedCapacitacion getRedCapacitacion() {
		return redCapacitacion;
	}
	public Persona getPersona() {
		return persona;
	}
	public void setPersona(Persona persona) {
		this.persona = persona;
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
	public Long getIdSinEntidad() {
		return idSinEntidad;
	}
	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}
	public Long getIdConfiguracionUo() {
		return idConfiguracionUo;
	}
	public void setIdConfiguracionUo(Long idConfiguracionUo) {
		this.idConfiguracionUo = idConfiguracionUo;
	}
	public Long getIdPais() {
		return idPais;
	}
	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	
}
