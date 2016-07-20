package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.SinObj;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.List;

@Name("sinObjList")
public class SinObjList extends EntityQuery<SinObj> {

	private static final String EJBQL = "select sinObj from SinObj sinObj";

	private static final String[] RESTRICTIONS = { "lower(sinObj.objNombre) like lower(concat(#{sinObjList.sinObj.objNombre},'%'))",
		"sinObj.objCodigo = #{sinObjList.cod}",	
	};

	private SinObj sinObj = new SinObj();
	private static final String ORDER = "sinObj.objNombre";
	private Integer cod;

	public SinObjList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<SinObj> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}


	public SinObj getSinObj() {
		return sinObj;
	}

	public Integer getCod() {
		return cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}
	
	
}
