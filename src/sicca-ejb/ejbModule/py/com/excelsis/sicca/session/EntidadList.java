package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.List;

@Name("entidadList")
public class EntidadList extends EntityQuery<Entidad> {

	private static final String EJBQL = "SELECT entidad FROM Entidad entidad";

	private static final String[] RESTRICTIONS = {};
	private static final String[] RESTRICTIONS_BY_SIN_ENTIDAD = {
		"entidad.sinEntidad.idSinEntidad = #{entidadList.sinEntidad.idSinEntidad}",
	};

	private Entidad entidad = new Entidad();
	private SinEntidad sinEntidad = new SinEntidad();

	public EntidadList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	public Entidad getEntidadBySinEntidad(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_BY_SIN_ENTIDAD));
		setMaxResults(1);
		List<Entidad> listResult = getResultList(); 
		return listResult.isEmpty() ? null : listResult.get(0);
	}

//	GETTERS Y SETTERS
	public Entidad getEntidad() {
		return entidad;
	}
	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}
	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}
}
