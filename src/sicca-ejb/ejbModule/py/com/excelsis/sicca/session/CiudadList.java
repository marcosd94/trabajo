package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("ciudadList")
public class CiudadList extends EntityQuery<Ciudad> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Ciudad> listaCiudads;
	
	private static final String EJBQL = "select ciudad from Ciudad ciudad";

	private static final String[] RESTRICTIONS = {
			"lower(ciudad.descripcion) like lower(concat('%',concat(#{ciudadList.ciudad.descripcion},'%')))",
			"ciudad.departamento.idDepartamento = #{ciudadList.departamento.idDepartamento}",
			"ciudad.departamento.pais.idPais = #{ciudadList.pais.idPais}",
			"ciudad.activo = #{ciudadList.ciudad.activo}",
	};
	private static final String ORDER = "ciudad.descripcion";

	private Pais pais = new Pais();
	private Departamento departamento = new Departamento();
	private Ciudad ciudad = new Ciudad();
	

	public CiudadList() {
		ciudad.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public List<Ciudad> buscarResultados(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}

	public List<Ciudad> limpiarResultados(){
		ciudad = new Ciudad();
		ciudad.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}
	public List<Ciudad> listarPorDpto(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(null);
		return getResultList(); 
	}
	
//	GETTERS Y SETTERS
	public Ciudad getCiudad() {
		return ciudad;
	}
	public Pais getPais() {
		return pais;
	}
	public Departamento getDepartamento() {
		return departamento;
	}

	public List<Ciudad> obtenerListaCiudads(){
    	listaCiudads = getResultList(); 
    	return listaCiudads;
    }
	
    public List<Ciudad> getListaCiudads() {
		return listaCiudads;
	}
}
