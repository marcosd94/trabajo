package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Name("homologacionPerfilMatrizList")
public class HomologacionPerfilMatrizList extends EntityQuery<HomologacionPerfilMatriz> {

	private static final String EJBQL =
		"select homologacionPerfilMatriz from HomologacionPerfilMatriz homologacionPerfilMatriz";
	private static final String EJBQL225 =
		"select homologacionPerfilMatriz from HomologacionPerfilMatriz homologacionPerfilMatriz LEFT JOIN  homologacionPerfilMatriz.configuracionUoCab  configuracionUoCab";

	private static final String[] RESTRICTIONS =
		{
			"lower(homologacionPerfilMatriz.estado) like lower(concat(#{homologacionPerfilMatrizList.homologacionPerfilMatriz.estado},'%'))",
			"lower(homologacionPerfilMatriz.usuAlta) like lower(concat(#{homologacionPerfilMatrizList.homologacionPerfilMatriz.usuAlta},'%'))",
			"lower(homologacionPerfilMatriz.usuMod) like lower(concat(#{homologacionPerfilMatrizList.homologacionPerfilMatriz.usuMod},'%'))",
			"lower(homologacionPerfilMatriz.usuHomolog) like lower(concat(#{homologacionPerfilMatrizList.homologacionPerfilMatriz.usuHomolog},'%'))",
			"lower(homologacionPerfilMatriz.objetivo) like lower(concat(#{homologacionPerfilMatrizList.homologacionPerfilMatriz.objetivo},'%'))",
			"lower(homologacionPerfilMatriz.denominacion) like lower(concat(#{homologacionPerfilMatrizList.homologacionPerfilMatriz.denominacion},'%'))",
			"lower(homologacionPerfilMatriz.mision) like lower(concat(#{homologacionPerfilMatrizList.homologacionPerfilMatriz.mision},'%'))", };

	private static final String[] RESTRICTIONS_CU255 =
		{
			"homologacionPerfilMatriz.activo = #{homologacionPerfilMatrizList.activo}",
			"lower(homologacionPerfilMatriz.denominacion) like lower(concat('%',#{homologacionPerfilMatrizList.denominacion},'%'))",
			"homologacionPerfilMatriz.cpt.idCpt = #{homologacionPerfilMatrizList.idCpt}",
			"homologacionPerfilMatriz.cpt.tipoCpt.idTipoCpt = #{homologacionPerfilMatrizList.idTipoCpt}",
			"homologacionPerfilMatriz.configuracionUoCab.entidad.sinEntidad.nenCodigo = #{homologacionPerfilMatrizList.nenCodigo}",
			"homologacionPerfilMatriz.configuracionUoCab.entidad.sinEntidad.entCodigo = #{homologacionPerfilMatrizList.entCodigo}",
			"homologacionPerfilMatriz.configuracionUoCab.idConfiguracionUo = #{homologacionPerfilMatrizList.idConfiguracionUo}", };

	private static final String[] RESTRICTIONS_CU357 =
		{
			"homologacionPerfilMatriz.activo = #{homologacionPerfilMatrizList.activo}",
			"lower(homologacionPerfilMatriz.denominacion) like lower(concat(#{homologacionPerfilMatrizList.denominacion},'%'))",
			"homologacionPerfilMatriz.cpt.idCpt = #{homologacionPerfilMatrizList.idCpt}",			
			"to_date(TO_CHAR(homologacionPerfilMatriz.fechaVencimiento,'DD/MM/YYYY' ),'DD/MM/YYYY')>=to_date(#{homologacionPerfilMatrizList.fechaHoyString},'DD/MM/YYYY')",			
			"homologacionPerfilMatriz.cpt.tipoCpt.idTipoCpt = #{homologacionPerfilMatrizList.idTipoCpt}", };

	private static final String ORDER_CU255 =
		"homologacionPerfilMatriz.configuracionUoCab.denominacionUnidad, homologacionPerfilMatriz.denominacion";

	private static final String ORDER = "homologacionPerfilMatriz.denominacion";

	private HomologacionPerfilMatriz homologacionPerfilMatriz = new HomologacionPerfilMatriz();

	private Long idTipoCpt;
	private Long idCpt;
	private String denominacion;
	private BigDecimal nenCodigo;
	private BigDecimal entCodigo;
	private Long idConfiguracionUo;
	private Boolean activo;
	private Long idConcursoPuestoAgr;
	private String fechaHoyString;

	public HomologacionPerfilMatrizList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);

	}

	public HomologacionPerfilMatriz getHomologacionPerfilMatriz() {
		return homologacionPerfilMatriz;
	}

	/**
	 * @return la lista completa.
	 */
	public List<HomologacionPerfilMatriz> limpiarCU255() {
		homologacionPerfilMatriz = new HomologacionPerfilMatriz();
		idTipoCpt = null;
		idCpt = null;
		denominacion = null;
		nenCodigo = null;
		entCodigo = null;
		idConfiguracionUo = null;
		activo = null;

		setEjbql(EJBQL225);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU255));
		setOrder(ORDER_CU255);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	/**
	 * @return la lista completa.
	 */
	public List<HomologacionPerfilMatriz> limpiarCU255Activos() {
		homologacionPerfilMatriz = new HomologacionPerfilMatriz();
		idTipoCpt = null;
		idCpt = null;
		denominacion = null;
		nenCodigo = null;
		entCodigo = null;
		idConfiguracionUo = null;
		activo = true;

		setEjbql(EJBQL225);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU255));
		setOrder(ORDER_CU255);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<HomologacionPerfilMatriz> listaResultadosCU255() {

		setEjbql(EJBQL225);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU255));
		setOrder(ORDER_CU255);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		 
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<HomologacionPerfilMatriz> limpiarCU357() {
		homologacionPerfilMatriz = new HomologacionPerfilMatriz();
		idTipoCpt = null;
		idCpt = null;
		denominacion = null;
		nenCodigo = null;
		entCodigo = null;
		idConfiguracionUo = null;
		activo = null;
		listaResultadosCU357();
		return getResultList();
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<HomologacionPerfilMatriz> listaResultadosCU357() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		fechaHoyString = sdf.format(new Date());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU357));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	
		return getResultList();
	}

	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
	}

	public Long getIdCpt() {
		return idCpt;
	}

	public void setIdCpt(Long idCpt) {
		this.idCpt = idCpt;
	}

	public String getDenominacion() {
		return denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
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

	public Long getIdConfiguracionUo() {
		return idConfiguracionUo;
	}

	public void setIdConfiguracionUo(Long idConfiguracionUo) {
		this.idConfiguracionUo = idConfiguracionUo;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public String getFechaHoyString() {
		return fechaHoyString;
	}

	public void setFechaHoyString(String fechaHoyString) {
		this.fechaHoyString = fechaHoyString;
	}

}
