package py.com.excelsis.sicca.seleccion.session.form;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatrizDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.SolicProrrogaDet;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("solicProrrogaDetList")
public class SolicProrrogaDetList extends EntityQuery<SolicProrrogaDet> {

	private static final String EJBQL = "select solicProrrogaDet from SolicProrrogaDet solicProrrogaDet";

	private static final String[] RESTRICTIONS = {
			"lower(solicProrrogaDet.observacion) like lower(concat(#{solicProrrogaDetList.solicProrrogaDet.observacion},'%'))",
			"lower(solicProrrogaDet.usuObs) like lower(concat(#{solicProrrogaDetList.solicProrrogaDet.usuObs},'%'))",
			"lower(solicProrrogaDet.respuesta) like lower(concat(#{solicProrrogaDetList.solicProrrogaDet.respuesta},'%'))",
			"lower(solicProrrogaDet.usuRpta) like lower(concat(#{solicProrrogaDetList.solicProrrogaDet.usuRpta},'%'))",
			"lower(solicProrrogaDet.usuAlta) like lower(concat(#{solicProrrogaDetList.solicProrrogaDet.usuAlta},'%'))",
			"lower(solicProrrogaDet.usuMod) like lower(concat(#{solicProrrogaDetList.solicProrrogaDet.usuMod},'%'))", 
			"solicProrrogaDet.activo = #{solicProrrogaDetList.activo}",
			"solicProrrogaDet.solicProrrogaCab.fechasGrupoPuesto.concurso.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{solicProrrogaDetList.sinEntidad.idSinEntidad}",
			"solicProrrogaDet.solicProrrogaCab.fechasGrupoPuesto.concurso.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{solicProrrogaDetList.sinEntidad.nenCodigo}",
			"solicProrrogaDet.solicProrrogaCab.fechasGrupoPuesto.concurso.configuracionUoCab.idConfiguracionUo=#{solicProrrogaCabList.configuracionUoCab.idConfiguracionUo}",
			"solicProrrogaDet.solicProrrogaCab.idSolicCab=#{solicProrrogaDetList.idSolicCab}",
		};

	private SolicProrrogaDet solicProrrogaDet = new SolicProrrogaDet();
	private static final String ORDER = "solicProrrogaDet.nro";
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private Long idSolicCab;
	private Boolean activo = true;
	
	public SolicProrrogaDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(2);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<SolicProrrogaDet> listaResultadosCU261() {
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
	public List<SolicProrrogaDet> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER + " Desc");
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	
	public SolicProrrogaDet getSolicProrrogaDet() {
		return solicProrrogaDet;
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

	public Long getIdSolicCab() {
		return idSolicCab;
	}

	public void setIdSolicCab(Long idSolicCab) {
		this.idSolicCab = idSolicCab;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Boolean getActivo() {
		return activo;
	}
	
	
	
	
	
}
