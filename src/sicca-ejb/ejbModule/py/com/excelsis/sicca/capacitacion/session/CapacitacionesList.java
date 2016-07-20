package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.BandejaCapacitacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("capacitacionesList")
public class CapacitacionesList extends CustomEntityQuery<Capacitaciones> {

	private static final String EJBQL = "select capacitaciones from Capacitaciones capacitaciones";

	private static final String[] RESTRICTIONS = {
			"capacitaciones.tipo like #{capacitacionesList.capacitaciones.tipo}",
			"lower(capacitaciones.nombre) like concat('%',lower(concat(#{capacitacionesList.capacitaciones.nombre},'%')))",
			"lower(capacitaciones.observacion) like lower(concat(#{capacitacionesList.capacitaciones.observacion},'%'))",
			"lower(capacitaciones.tipoSeleccion) like lower(concat(#{capacitacionesList.capacitaciones.tipoSeleccion},'%'))",
			"lower(capacitaciones.modalidad) like lower(concat(#{capacitacionesList.capacitaciones.modalidad},'%'))",
			"lower(capacitaciones.sede) like lower(concat(#{capacitacionesList.capacitaciones.sede},'%'))",
			"lower(capacitaciones.direccion) like lower(concat(#{capacitacionesList.capacitaciones.direccion},'%'))",
			"lower(capacitaciones.fuenteFinanciacion) like lower(concat(#{capacitacionesList.capacitaciones.fuenteFinanciacion},'%'))",
			"lower(capacitaciones.destinadoA) like lower(concat(#{capacitacionesList.capacitaciones.destinadoA},'%'))",
			"lower(capacitaciones.estado) like lower(concat(#{capacitacionesList.capacitaciones.estado},'%'))",
			"lower(capacitaciones.usuAlta) like lower(concat(#{capacitacionesList.capacitaciones.usuAlta},'%'))",
			"lower(capacitaciones.usuMod) like lower(concat(#{capacitacionesList.capacitaciones.usuMod},'%'))",
			"lower(capacitaciones.motivoCancelacion) like lower(concat(#{capacitacionesList.capacitaciones.motivoCancelacion},'%'))",
			"lower(capacitaciones.usuCancelacion) like lower(concat(#{capacitacionesList.capacitaciones.usuCancelacion},'%'))",
			"lower(capacitaciones.fechaCancelacion) like lower(concat(#{capacitacionesList.capacitaciones.fechaCancelacion},'%'))",
			"capacitaciones.activo =#{capacitacionesList.capacitaciones.activo}",
			"capacitaciones.datosEspecificosTipoCap.idDatosEspecificos =#{capacitacionesList.idTipo}",
			"capacitaciones.configuracionUoDet.idConfiguracionUoDet =#{capacitacionesList.idConfiguracionUoDet}",
			"capacitaciones.configuracionUoCab.idConfiguracionUo =#{capacitacionesList.idConfiguracionCab}",
			"capacitaciones.tipo =#{capacitacionesList.tipoCap}",
			};
	
	private static final String[] RESTRICTIONS_CU534 = {
		"capacitaciones.tipo like #{capacitacionesList.capacitaciones.tipo}",
		"lower(capacitaciones.nombre) like concat('%',lower(concat(#{capacitacionesList.capacitaciones.nombre},'%')))",
		"lower(capacitaciones.observacion) like lower(concat(#{capacitacionesList.capacitaciones.observacion},'%'))",
		"lower(capacitaciones.tipoSeleccion) like lower(concat(#{capacitacionesList.capacitaciones.tipoSeleccion},'%'))",
		"lower(capacitaciones.modalidad) like lower(concat(#{capacitacionesList.capacitaciones.modalidad},'%'))",
		"lower(capacitaciones.sede) like lower(concat(#{capacitacionesList.capacitaciones.sede},'%'))",
		"lower(capacitaciones.direccion) like lower(concat(#{capacitacionesList.capacitaciones.direccion},'%'))",
		"lower(capacitaciones.fuenteFinanciacion) like lower(concat(#{capacitacionesList.capacitaciones.fuenteFinanciacion},'%'))",
		"lower(capacitaciones.destinadoA) like lower(concat(#{capacitacionesList.capacitaciones.destinadoA},'%'))",
		"lower(capacitaciones.estado) like lower(concat(#{capacitacionesList.capacitaciones.estado},'%'))",
		"lower(capacitaciones.usuAlta) like lower(concat(#{capacitacionesList.capacitaciones.usuAlta},'%'))",
		"lower(capacitaciones.usuMod) like lower(concat(#{capacitacionesList.capacitaciones.usuMod},'%'))",
		"lower(capacitaciones.motivoCancelacion) like lower(concat(#{capacitacionesList.capacitaciones.motivoCancelacion},'%'))",
		"lower(capacitaciones.usuCancelacion) like lower(concat(#{capacitacionesList.capacitaciones.usuCancelacion},'%'))",
		"lower(capacitaciones.fechaCancelacion) like lower(concat(#{capacitacionesList.capacitaciones.fechaCancelacion},'%'))",
		"capacitaciones.activo =#{capacitacionesList.capacitaciones.activo}",
		"capacitaciones.datosEspecificosTipoCap.idDatosEspecificos =#{capacitacionesList.idTipo}",
		"capacitaciones.configuracionUoDet.idConfiguracionUoDet =#{capacitacionesList.idConfiguracionUoDet}",
		};

	private Capacitaciones capacitaciones = new Capacitaciones();
	private Estado estado=Estado.ACTIVO;
	private static final String ORDER = "capacitaciones.nombre";
	private Long idTipo;
	private String tipoCap;
	private Integer valorEstado;
	private Long idConfiguracionUoDet;
	private Long idConfiguracionCab;
	
	public CapacitacionesList() {
		capacitaciones.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Capacitaciones> listaResultados() {
		capacitaciones.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public List<Capacitaciones> listaResultados534(String ejbql){
		//	setEjbql(ejbql);
			
			setCustomEjbql(ejbql);
			setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
			//setOrder(ORDER);
			List<Capacitaciones> lista = getResultList(); 
			return lista;
		}
	
	

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<Capacitaciones> limpiarCU471() {
		capacitaciones= new Capacitaciones();
		estado = Estado.ACTIVO;
		capacitaciones.setActivo(estado.getValor());
		idTipo=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Long idTipo) {
		this.idTipo = idTipo;
	}

	public Long getIdConfiguracionUoDet() {
		return idConfiguracionUoDet;
	}

	public void setIdConfiguracionUoDet(Long idConfiguracionUoDet) {
		this.idConfiguracionUoDet = idConfiguracionUoDet;
	}

	public Integer getValorEstado() {
		return valorEstado;
	}

	public void setValorEstado(Integer valorEstado) {
		this.valorEstado = valorEstado;
	}

	public String getTipoCap() {
		return tipoCap;
	}

	public void setTipoCap(String tipoCap) {
		this.tipoCap = tipoCap;
	}

	public Long getIdConfiguracionCab() {
		return idConfiguracionCab;
	}

	public void setIdConfiguracionCab(Long idConfiguracionCab) {
		this.idConfiguracionCab = idConfiguracionCab;
	}
	
	
}
