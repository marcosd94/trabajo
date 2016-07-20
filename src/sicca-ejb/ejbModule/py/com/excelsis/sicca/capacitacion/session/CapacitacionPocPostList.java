package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.CapacitacionPocPost;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Name("capacitacionPocPostList")
public class CapacitacionPocPostList extends EntityQuery<CapacitacionPocPost> {

	private static final String EJBQL = "select capacitacionPocPost from CapacitacionPocPost capacitacionPocPost";

	private static final String[] RESTRICTIONS = {
			"lower(capacitacionPocPost.nombre) like lower(concat('%',concat(#{capacitacionPocPostList.capacitacionPocPost.nombre},'%')))",
			"capacitacionPocPost.datosEspecificos.idDatosEspecificos = #{capacitacionPocPostList.idTipoCapa} ",
			"capacitacionPocPost.modVal = #{capacitacionPocPostList.capacitacionPocPost.modVal}", 
			"capacitacionPocPost.ciudad.departamento.pais.idPais  = #{capacitacionPocPostList.idPais} ",
			"capacitacionPocPost.ciudad.departamento.idDepartamento  = #{capacitacionPocPostList.idDepartamento} ",
			"capacitacionPocPost.ciudad.idCiudad  = #{capacitacionPocPostList.idCiudad} "
			,};

	private CapacitacionPocPost capacitacionPocPost;
	private static final String ORDER = "capacitacionPocPost.desde desc";
	private Long idTipoCapa=null;
	private Long idPais=null;
	private Long idDepartamento=null;
	private Long idCiudad=null;
	private Date fecActual= new Date();

	public CapacitacionPocPostList() {
		capacitacionPocPost = new CapacitacionPocPost();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CapacitacionPocPost> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<CapacitacionPocPost> limpiar() {
		capacitacionPocPost= new CapacitacionPocPost();
		idCiudad=null;
		idDepartamento=null;
		idPais=null;
		idTipoCapa=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public CapacitacionPocPost getCapacitacionPocPost() {
		return capacitacionPocPost;
	}


	public Long getIdTipoCapa() {
		return idTipoCapa;
	}


	public void setIdTipoCapa(Long idTipoCapa) {
		this.idTipoCapa = idTipoCapa;
	}


	public Long getIdPais() {
		return idPais;
	}


	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}


	public Long getIdDepartamento() {
		return idDepartamento;
	}


	public void setIdDepartamento(Long idDepartamento) {
		this.idDepartamento = idDepartamento;
	}


	public Long getIdCiudad() {
		return idCiudad;
	}


	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}


	public Date getFecActual() {
		return fecActual;
	}


	public void setFecActual(Date fecActual) {
		this.fecActual = fecActual;
	}



	
	
}
