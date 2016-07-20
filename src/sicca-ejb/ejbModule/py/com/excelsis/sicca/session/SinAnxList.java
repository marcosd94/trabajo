package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import antlr.collections.impl.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("sinAnxList")
public class SinAnxList extends EntityQuery<SinAnx> {

	private static final String EJBQL = "select sinAnx from SinAnx sinAnx";

	private static final String[] RESTRICTIONS = {
		"sinAnx.aniAniopre = #{sinAnxList.sinAnx.aniAniopre}",
		"sinAnx.nenCodigo = #{sinAnxList.sinAnx.nenCodigo}",
		"sinAnx.entCodigo = #{sinAnxList.sinAnx.entCodigo}",
		"lower(sinAnx.anxDescrip) like lower(concat('%', concat(#{sinAnxList.sinAnx.anxDescrip},'%')))",
		"lower(sinAnx.ctgCodigo) like lower(concat(#{sinAnxList.sinAnx.ctgCodigo},'%'))",
		"lower(sinAnx.mdfCodigo) like lower(concat(#{sinAnxList.sinAnx.mdfCodigo},'%'))",
		"lower(sinAnx.anxTiporeg) like lower(concat(#{sinAnxList.sinAnx.anxTiporeg},'%'))",	
		"concat(concat(sinAnx.nenCodigo,'.'),concat(concat(sinAnx.entCodigo,'.'),concat(concat(sinAnx.tipCodigo,'.'),sinAnx.proCodigo))) in (#{sinAnxList.cods})",
	};
	private static final String ORDER = "sinAnx.aniAniopre, sinAnx.nenCodigo, sinAnx.entCodigo, sinAnx.ctgCodigo";
	private List<String> cods= new ArrayList<String>();
	private SinAnx sinAnx = new SinAnx();

	public SinAnxList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
	}

	public SinAnx getSinAnx() {
		return sinAnx;
	}
	
	public List<SinAnx> buscarResultados(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}

	public List<SinAnx> limpiarResultados(){
		sinAnx = new SinAnx();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}

	public List<String> getCods() {
		return cods;
	}

	public void setCods(List<String> cods) {
		this.cods = cods;
	}
	
	
}
