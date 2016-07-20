package py.com.excelsis.sicca.proceso.session;

import java.util.Arrays;
import java.util.List;
import org.jboss.seam.annotations.Name;
import py.com.excelsis.sicca.entity.BandejaExcepcion;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;


@Name("bandejaExcepcionList")
public class BandejaExcepcionList extends CustomEntityQuery<BandejaExcepcion> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7940300437679563845L;

	private static final String EJBQL = "select bandejaExcepcion from BandejaExcepcion bandejaExcepcion";
	
	private static final String[] RESTRICTIONS = {
			"bandejaExcepcion.configuracionUoCab.ordenConfiguracion = #{bandejaExcepcionList.ordenUnidOrg}",
			"bandejaExcepcion.entidad.idEntidad = #{bandejaExcepcionList.idEntidad}",
			"bandejaExcepcion.idConfiguracionUo = #{bandejaExcepcionList.idConfiguracionUOCab}",
			"lower(bandejaExcepcion.concurso.nombreConcurso) like lower(concat(#{bandejaExcepcionList.concurso},'%'))",
			//"bandejaEntrada.actividad.idActividad = #{bandejaEntradaList.idActividad}",
	};
	private static final String ORDER = "bandejaExcepcion.id desc";
	
	private Integer ordenUnidOrg;
	private Long idEntidad;
	private String codigoUnidadOrg;
	private Long idRol;
	private String concurso;
	private Long idConfiguracionUOCab;
	private Long idConfiguracionUoDet;

	private BandejaExcepcion bandejaExcepcion = new BandejaExcepcion();

	public BandejaExcepcionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<BandejaExcepcion> listaResultados(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}
	
	public List<BandejaExcepcion> listaResultados(String ejbql){
		setEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		List<BandejaExcepcion> lista = getResultList(); 
		return lista;
	}

	public List<BandejaExcepcion> limpiar(){
		bandejaExcepcion = new BandejaExcepcion();
		ordenUnidOrg = null;
		idEntidad = null;
		codigoUnidadOrg = null;
		idRol = null;
		concurso = null;
		idConfiguracionUOCab = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}
	

	
//	GETTERS Y SETTERS

    public BandejaExcepcion getBandejaExcepcion() {
		return bandejaExcepcion;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public Long getIdEntidad() {
		return idEntidad;
	}

	public void setIdEntidad(Long idEntidad) {
		this.idEntidad = idEntidad;
	}
	
	public String getCodigoUnidadOrg() {
		return codigoUnidadOrg;
	}
	public void setCodigoUnidadOrg(String codigoUnidadOrg) {
		this.codigoUnidadOrg = codigoUnidadOrg;
	}

	public Long getIdRol() {
		return idRol;
	}

	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}

	public String getConcurso() {
		return concurso;
	}

	public void setConcurso(String concurso) {
		this.concurso = concurso;
	}
	
	public Long getIdConfiguracionUOCab() {
		return idConfiguracionUOCab;
	}

	public void setIdConfiguracionUOCab(Long idConfiguracionUOCab) {
		this.idConfiguracionUOCab = idConfiguracionUOCab;
	}

	public Long getIdConfiguracionUoDet() {
		return idConfiguracionUoDet;
	}

	public void setIdConfiguracionUoDet(Long idConfiguracionUoDet) {
		this.idConfiguracionUoDet = idConfiguracionUoDet;
	}
}

