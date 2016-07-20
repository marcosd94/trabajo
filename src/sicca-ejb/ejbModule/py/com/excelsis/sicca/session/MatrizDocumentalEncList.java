package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizDocumentalEnc;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("matrizDocumentalEncList")
public class MatrizDocumentalEncList extends EntityQuery<MatrizDocumentalEnc> {

	private static final String EJBQL = "select matrizDocumentalEnc from MatrizDocumentalEnc matrizDocumentalEnc";

	private static final String[] RESTRICTIONS = {
		"lower(matrizDocumentalEnc.descripcion) like lower(concat('%',concat(#{matrizDocumentalEncList.matrizDocumentalEnc.descripcion},'%')))",
		"matrizDocumentalEnc.datosEspecificos.idDatosEspecificos = #{matrizDocumentalEncList.datosEspecificos.idDatosEspecificos}",
		"matrizDocumentalEnc.activo = #{matrizDocumentalEncList.matrizDocumentalEnc.activo}",
	};
	private static final String ORDER = "matrizDocumentalEnc.descripcion";

	private MatrizDocumentalEnc matrizDocumentalEnc = new MatrizDocumentalEnc();
	private DatosEspecificos datosEspecificos = new DatosEspecificos();

	public MatrizDocumentalEncList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
	}

//	GETTERS Y SETTERS
	public MatrizDocumentalEnc getMatrizDocumentalEnc() {
		return matrizDocumentalEnc;
	}
	public DatosEspecificos getDatosEspecificos() {
		return datosEspecificos;
	}
	
}
