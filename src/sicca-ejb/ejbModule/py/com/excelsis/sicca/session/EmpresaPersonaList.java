package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.entity.EmpresaPersona;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.List;

@Name("empresaPersonaList")
public class EmpresaPersonaList extends EntityQuery<EmpresaPersona> {

	private static final String EJBQL = "select empresaPersona from EmpresaPersona empresaPersona";

	private static final String[] RESTRICTIONS = {
			"lower(empresaPersona.cargo) like lower(concat(#{empresaPersonaList.empresaPersona.cargo},'%'))",
			"empresaPersona.emprTercerizada.idEmpresaTercerizada = #{empresaPersonaList.empresa.idEmpresaTercerizada}",
			"empresaPersona.persona.idPersona = #{empresaPersonaList.persona.idPersona}", 
	};
	private static final String ORDER = "empresaPersona.emprTercerizada.nombre";

	private EmpresaPersona empresaPersona = new EmpresaPersona();
	private EmprTercerizada empresa = new EmprTercerizada();
	private Persona persona = new Persona();
	
	public EmpresaPersonaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	public List<EmpresaPersona> listByEmpresa(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		return getResultList();
	}

//	GETTERS Y SETTERS
	public EmpresaPersona getEmpresaPersona() {
		return empresaPersona;
	}

	public EmprTercerizada getEmpresa() {
		return empresa;
	}

	public Persona getPersona() {
		return persona;
	}
	
}
