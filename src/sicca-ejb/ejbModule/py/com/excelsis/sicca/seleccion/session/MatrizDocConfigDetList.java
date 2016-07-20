package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.mvel2.optimizers.impl.refl.nodes.ArrayLength;

import enums.Estado;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("matrizDocConfigDetList")
public class MatrizDocConfigDetList extends EntityQuery<MatrizDocConfigDet> {

	private static final String EJBQL = "select matrizDocConfigDet from MatrizDocConfigDet matrizDocConfigDet";

	private static final String[] RESTRICTIONS = {
//			"lower(matrizDocConfigDet.usuAlta) like lower(concat(#{matrizDocConfigDetList.matrizDocConfigDet.usuAlta},'%'))",
//			"lower(matrizDocConfigDet.usuMod) like lower(concat(#{matrizDocConfigDetList.matrizDocConfigDet.usuMod},'%'))",
			"matrizDocConfigDet.datosEspecificos.idDatosEspecificos = #{matrizDocConfigDetList.idDatosEspecificos}",
			"matrizDocConfigDet.matrizDocConfigEnc.concursoPuestoAgr.idConcursoPuestoAgr = #{matrizDocConfigDetList.idConcursoPuestoAgr}",
			"matrizDocConfigDet.activo=#{matrizDocConfigDetList.estado.valor}",
	};
	private static final String[] RESTRICTIONSEliminar = {
		"matrizDocConfigDet.matrizDocConfigEnc.concursoPuestoAgr.idConcursoPuestoAgr = #{matrizDocConfigDetList.idConcursoPuestoAgr}",
		"matrizDocConfigDet.activo=#{matrizDocConfigDetList.estado.valor}",
};

	private MatrizDocConfigDet matrizDocConfigDet = new MatrizDocConfigDet();
	private static final String ORDER = "matrizDocConfigDet.nroOrden ASC";
	private Long idDatosEspecificos;
	private Long idConcursoPuestoAgr;
	private Estado estado= Estado.ACTIVO;

	
	
	public MatrizDocConfigDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<MatrizDocConfigDet> listaResultados() {
		try {
			setEjbql(EJBQL);
			setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
			setOrder(ORDER);
			setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
			return getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ArrayList<MatrizDocConfigDet>();
		}
		
	
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<MatrizDocConfigDet> limpiarXPuesto() {
		matrizDocConfigDet= new MatrizDocConfigDet();
		idDatosEspecificos=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	
	public List<MatrizDocConfigDet> Eliminar(Long idCon,Long idMatEnc) {
		matrizDocConfigDet= new MatrizDocConfigDet();
		idDatosEspecificos=null;
		 String query="select MatrizDocConfigDet from MatrizDocConfigDet MatrizDocConfigDet"
	                + " where MatrizDocConfigDet.matrizDocConfigEnc.idMatrizDocConfigEnc = "
	                + idMatEnc + " and MatrizDocConfigDet.activo is true";
		setEjbql(query);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSEliminar));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public MatrizDocConfigDet getMatrizDocConfigDet() {
		return matrizDocConfigDet;
	}

	public Long getIdDatosEspecificos() {
		return idDatosEspecificos;
	}

	public void setIdDatosEspecificos(Long idDatosEspecificos) {
		this.idDatosEspecificos = idDatosEspecificos;
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
