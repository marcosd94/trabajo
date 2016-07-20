package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.MatrizReferencialDet;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("matrizReferencialDetList")
public class MatrizReferencialDetList extends EntityQuery<MatrizReferencialDet> {

	private static final String EJBQL = "select matrizReferencialDet from MatrizReferencialDet matrizReferencialDet";

	private static final String[] RESTRICTIONS = {
			"lower(matrizReferencialDet.descripcion) like concat('%',lower(concat(#{matrizReferencialDetList.matrizReferencialDet.descripcion},'%')))",
			"lower(matrizReferencialDet.usuAlta) like lower(concat(#{matrizReferencialDetList.matrizReferencialDet.usuAlta},'%'))",
			"lower(matrizReferencialDet.usuMod) like lower(concat(#{matrizReferencialDetList.matrizReferencialDet.usuMod},'%'))", 
			"matrizReferencialDet.matrizReferencialEnc.idMatrizReferencialEnc = #{matrizReferencialDetList.idMatrizReferencialEnc}",
			"matrizReferencialDet.activo=#{matrizReferencialDetList.estado.valor}",		
	};

	private MatrizReferencialDet matrizReferencialDet = new MatrizReferencialDet();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "matrizReferencialDet.idMatrizReferencialDet";
	private Long idMatrizReferencialEnc;
	public MatrizReferencialDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(25);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<MatrizReferencialDet> listaResultados() {
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
	public List<MatrizReferencialDet> limpiar() {
		matrizReferencialDet= new MatrizReferencialDet();
		estado = Estado.TODOS;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public MatrizReferencialDet getMatrizReferencialDet() {
		return matrizReferencialDet;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public Long getIdMatrizReferencialEnc() {
		return idMatrizReferencialEnc;
	}
	public void setIdMatrizReferencialEnc(Long idMatrizReferencialEnc) {
		this.idMatrizReferencialEnc = idMatrizReferencialEnc;
	}
	
}
