package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizReferencial;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("matrizReferencialList")
public class MatrizReferencialList extends EntityQuery<MatrizReferencial> {

	private static final String EJBQL = "select matrizReferencial from MatrizReferencial matrizReferencial ";

	private static final String[] RESTRICTIONS = {
		"lower(matrizReferencial.descripcion) like lower(concat('%',concat(#{matrizReferencialList.matrizReferencial.descripcion},'%')))",
		"matrizReferencial.datosEspecificos.idDatosEspecificos = #{matrizReferencialList.datosEspecificos.idDatosEspecificos}",
		"matrizReferencial.activo = #{matrizReferencialList.matrizReferencial.activo}", 
	};
	
	private static final String ORDER = "matrizReferencial.datosEspecificos.descripcion, matrizReferencial.descripcion";

	private MatrizReferencial matrizReferencial = new MatrizReferencial();
	private DatosEspecificos datosEspecificos = new DatosEspecificos();

	public MatrizReferencialList() {
		matrizReferencial.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
	}
	
//	GETTERS Y SETTERS
	public MatrizReferencial getMatrizReferencial() {
		return matrizReferencial;
	}
	public DatosEspecificos getDatosEspecificos() {
		return datosEspecificos;
	}
	
}
