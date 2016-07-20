package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("homologacionPerfilMatrizDetList")
public class HomologacionPerfilMatrizDetList extends
		EntityQuery<HomologacionPerfilMatrizDet> {

	private static final String EJBQL = "select homologacionPerfilMatrizDet from HomologacionPerfilMatrizDet homologacionPerfilMatrizDet";

	private static final String[] RESTRICTIONS = {
			"lower(homologacionPerfilMatrizDet.observacion) like lower(concat(#{homologacionPerfilMatrizDetList.homologacionPerfilMatrizDet.observacion},'%'))",
			"lower(homologacionPerfilMatrizDet.usuObs) like lower(concat(#{homologacionPerfilMatrizDetList.homologacionPerfilMatrizDet.usuObs},'%'))",
			"lower(homologacionPerfilMatrizDet.respuesta) like lower(concat(#{homologacionPerfilMatrizDetList.homologacionPerfilMatrizDet.respuesta},'%'))",
			"lower(homologacionPerfilMatrizDet.usuRpta) like lower(concat(#{homologacionPerfilMatrizDetList.homologacionPerfilMatrizDet.usuRpta},'%'))",
			"lower(homologacionPerfilMatrizDet.usuAlta) like lower(concat(#{homologacionPerfilMatrizDetList.homologacionPerfilMatrizDet.usuAlta},'%'))",
			"homologacionPerfilMatrizDet.homologacionPerfilMatriz.concursoPuestoAgr.idConcursoPuestoAgr  = #{homologacionPerfilMatrizDetList.idConcursoPuestoAgr} ",
			"lower(homologacionPerfilMatrizDet.usuMod) like lower(concat(#{homologacionPerfilMatrizDetList.homologacionPerfilMatrizDet.usuMod},'%'))",
			"homologacionPerfilMatrizDet.firmaResolucion =#{homologacionPerfilMatrizDetList.homologacionPerfilMatrizDet.firmaResolucion}",
			"homologacionPerfilMatrizDet.activo =#{homologacionPerfilMatrizDetList.estado.valor}",
			};

	private HomologacionPerfilMatrizDet homologacionPerfilMatrizDet = new HomologacionPerfilMatrizDet();
	private Long idConcursoPuestoAgr=null;
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "homologacionPerfilMatrizDet.fechaAlta asc";
	
	public HomologacionPerfilMatrizDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<HomologacionPerfilMatrizDet> listaResultados() {
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
	public List<HomologacionPerfilMatrizDet> listaResultados(String EJBQL2) {
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
	public List<HomologacionPerfilMatrizDet> limpiar() {
		homologacionPerfilMatrizDet= new HomologacionPerfilMatrizDet();
		estado = Estado.ACTIVO;
		idConcursoPuestoAgr=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public HomologacionPerfilMatrizDet getHomologacionPerfilMatrizDet() {
		return homologacionPerfilMatrizDet;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}


	public Estado getEstado() {
		return estado;
	}


	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	
	
}
