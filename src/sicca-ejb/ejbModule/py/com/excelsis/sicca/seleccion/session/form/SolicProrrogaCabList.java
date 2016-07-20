package py.com.excelsis.sicca.seleccion.session.form;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.SolicProrrogaCab;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

@Scope(ScopeType.CONVERSATION)
@Name("solicProrrogaCabList")
public class SolicProrrogaCabList extends CustomEntityQuery<SolicProrrogaCab> {

	private static final String EJBQL = "select solicProrrogaCab from SolicProrrogaCab solicProrrogaCab";

	private static final String[] RESTRICTIONS = {
			"lower(solicProrrogaCab.tipo) like lower(concat(#{solicProrrogaCabList.solicProrrogaCab.tipo},'%'))",
			"lower(solicProrrogaCab.estado) like lower(concat(#{solicProrrogaCabList.estado},'%'))",
			"lower(solicProrrogaCab.usuAlta) like lower(concat(#{solicProrrogaCabList.solicProrrogaCab.usuAlta},'%'))",
			"lower(solicProrrogaCab.usuMod) like lower(concat(#{solicProrrogaCabList.solicProrrogaCab.usuMod},'%'))",
			"lower(solicProrrogaCab.usuPublic) like lower(concat(#{solicProrrogaCabList.solicProrrogaCab.usuPublic},'%'))",
			"date(solicProrrogaCab.fechaSol) >= #{solicProrrogaCabList.desde}",
			" date(solicProrrogaCab.fechaSol) <= #{solicProrrogaCabList.hasta}",
			"solicProrrogaCab.fechasGrupoPuesto.concurso.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{solicProrrogaCabList.sinEntidad.idSinEntidad}",
			"solicProrrogaCab.fechasGrupoPuesto.concurso.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{solicProrrogaCabList.sinEntidad.nenCodigo}",
			"solicProrrogaCab.fechasGrupoPuesto.concurso.configuracionUoCab.idConfiguracionUo=#{solicProrrogaCabList.configuracionUoCab.idConfiguracionUo}",
			"lower(solicProrrogaCab.fechasGrupoPuesto.concurso.nombre)like concat('%',lower(concat(#{solicProrrogaCabList.nomConcurso},'%')))",
			"lower(solicProrrogaCab.fechasGrupoPuesto.concursoPuestoAgr.descripcionGrupo)like concat('%',lower(concat(#{solicProrrogaCabList.nomGrupo},'%')))",
			
	};

	private SolicProrrogaCab solicProrrogaCab = new SolicProrrogaCab();
	private static final String ORDER = "solicProrrogaCab.nroSol";
	private String estado;
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private String nomConcurso;
	private String nomGrupo;
	private Date desde=null;
	private Date hasta =null;;
	
	
	public SolicProrrogaCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista completa.
	 */
	public List<SolicProrrogaCab> limpiar261() {
		solicProrrogaCab = new SolicProrrogaCab();
		sinNivelEntidad = null;
		sinEntidad = null;
		configuracionUoCab = null;
		nomConcurso=null;
		nomGrupo=null;
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
	public List<SolicProrrogaCab> listaResultados261() {
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
	public List<SolicProrrogaCab> listaResultadosCU234(String ejbql, Map<String, QueryValue> params) {
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setParams(params);
		return getResultList(ejbql);
	}

	public SolicProrrogaCab getSolicProrrogaCab() {
		return solicProrrogaCab;
	}

	

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
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

	public String getNomConcurso() {
		return nomConcurso;
	}

	public void setNomConcurso(String nomConcurso) {
		this.nomConcurso = nomConcurso;
	}

	public String getNomGrupo() {
		return nomGrupo;
	}

	public void setNomGrupo(String nomGrupo) {
		this.nomGrupo = nomGrupo;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}
	
	
	
}
