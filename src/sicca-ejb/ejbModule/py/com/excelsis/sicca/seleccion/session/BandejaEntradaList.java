package py.com.excelsis.sicca.seleccion.session;

import java.util.Arrays;
import java.util.List;
import org.jboss.seam.annotations.Name;
import py.com.excelsis.sicca.entity.BandejaEntrada;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;


@Name("bandejaEntradaList")
public class BandejaEntradaList extends CustomEntityQuery<BandejaEntrada> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7744858045719366737L;

	private static final String EJBQL = "select bandejaEntrada from BandejaEntrada bandejaEntrada";
	
	private static final String[] RESTRICTIONS = {
			"bandejaEntrada.ordenConfiguracion = #{bandejaEntradaList.ordenUnidOrg}",
			"bandejaEntrada.idEntidad = #{bandejaEntradaList.idEntidad}",
			"bandejaEntrada.idRol = #{bandejaEntradaList.idRol}",
			"bandejaEntrada.idConfiguracionUo = #{bandejaEntradaList.idConfiguracionUOCab}",
			"bandejaEntrada.idConfiguracionUoDet = #{bandejaEntradaList.idConfiguracionUoDet}",
			"lower(bandejaEntrada.nombreConcurso) like lower(concat(#{bandejaEntradaList.concurso},'%'))",
			//"bandejaEntrada.actividad.idActividad = #{bandejaEntradaList.idActividad}",
			//"bandejaEntrada.codigoUsuario = #{bandejaEntradaList.codigoUsuario}",
	};
	private static final String ORDER = "bandejaEntrada.denominacionUnidadCab, bandejaEntrada.nombreConcurso";
	
	private Integer ordenUnidOrg;
	private Long idEntidad;
	private String codigoUnidadOrg;
	private Long idRol;
	private String concurso;
	private Long idConfiguracionUOCab;
	private Long idConfiguracionUoDet;
	//ZD 30/10/15
//	private String codigoUsuario;

	private BandejaEntrada bandejaEntrada = new BandejaEntrada();

	public BandejaEntradaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<BandejaEntrada> listaResultados(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}
	
	public List<BandejaEntrada> listaResultados(String ejbql){
		setEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		List<BandejaEntrada> lista = getResultList(); 
		return lista;
	}

	public List<BandejaEntrada> limpiar(){
		bandejaEntrada = new BandejaEntrada();
		ordenUnidOrg = null;
		idEntidad = null;
		codigoUnidadOrg = null;
		idRol = null;
		concurso = null;
		idConfiguracionUOCab = null;
//		codigoUsuario = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}
	

	
//	GETTERS Y SETTERS

    public BandejaEntrada getBandejaEntrada() {
		return bandejaEntrada;
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

//	public String getCodigoUsuario() {
//		return codigoUsuario;
//	}
//
//	public void setCodigoUsuario(String codigoUsuario) {
//		this.codigoUsuario = codigoUsuario;
//	}
}

