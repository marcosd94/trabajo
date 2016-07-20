package py.com.excelsis.sicca.circuitoCSI.session;

import java.util.Arrays;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import py.com.excelsis.sicca.entity.BandejaEntrada;
import py.com.excelsis.sicca.entity.BandejaEntradaCsi;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;


@Name("bandejaEntradaCsiList")
public class BandejaEntradaCsiList extends CustomEntityQuery<BandejaEntradaCsi> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7744858045719366737L;

	private static final String EJBQL = "select bandejaEntrada from BandejaEntradaCsi bandejaEntrada";
	
	private static final String[] RESTRICTIONS = {
			"bandejaEntrada.ordenConfiguracion = #{bandejaEntradaCsiList.ordenUnidOrg}",
			"bandejaEntrada.idEntidad = #{bandejaEntradaCsiList.idEntidad}",
			"bandejaEntrada.idRol = #{bandejaEntradaCsiList.idRol}",
			"bandejaEntrada.idConfiguracionUo = #{bandejaEntradaCsiList.idConfiguracionUOCab}",
			"bandejaEntrada.idConfiguracionUoDet = #{bandejaEntradaCsiList.idConfiguracionUoDet}",
			"lower(bandejaEntrada.nombreConcurso) like lower(concat(#{bandejaEntradaCsiList.concurso},'%'))",
			//"bandejaEntrada.actividad.idActividad = #{bandejaEntradaList.idActividad}",
	};
	private static final String ORDER = "bandejaEntrada.denominacionUnidadCab, bandejaEntrada.nombreConcurso";
	
	private Integer ordenUnidOrg;
	private Long idEntidad;
	private String codigoUnidadOrg;
	private Long idRol;
	private String concurso;
	private Long idConfiguracionUOCab;
	private Long idConfiguracionUoDet;

	private BandejaEntradaCsi bandejaEntrada = new BandejaEntradaCsi();

	public BandejaEntradaCsiList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<BandejaEntradaCsi> listaResultados(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}
	
	public List<BandejaEntradaCsi> listaResultados(String ejbql){
		setEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		List<BandejaEntradaCsi> lista = getResultList(); 
		return lista;
	}

	public List<BandejaEntradaCsi> limpiar(){
		bandejaEntrada = new BandejaEntradaCsi();
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

    public BandejaEntradaCsi getBandejaEntrada() {
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
}

