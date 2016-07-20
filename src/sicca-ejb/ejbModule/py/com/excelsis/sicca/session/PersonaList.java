package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.CONVERSATION)
@Name("personaList")
public class PersonaList extends EntityQuery<Persona> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Persona> listaPersonas;
	
	private static final String EJBQL = "select persona from Persona persona";

	private static final String[] RESTRICTIONS = {
			"lower(persona.nombres) like concat('%',lower(concat(#{personaList.persona.nombres},'%')))",
			"lower(persona.apellidos) like concat('%',lower(concat(#{personaList.persona.apellidos},'%')))",
			"persona.documentoIdentidad = #{personaList.persona.documentoIdentidad}",
			"lower(persona.EMail) like lower(concat(#{personaList.persona.EMail},'%'))",
			"lower(persona.sexo) like lower(concat(#{personaList.persona.sexo},'%'))",
			"lower(persona.estadoCivil) like lower(concat(#{personaList.persona.estadoCivil},'%'))",
			"lower(persona.departamentoNro) like lower(concat(#{personaList.persona.departamentoNro},'%'))",
			"lower(persona.piso) like lower(concat(#{personaList.persona.piso},'%'))",
			"lower(persona.callePrincipal) like lower(concat(#{personaList.persona.callePrincipal},'%'))",
			"lower(persona.primeraLateral) like lower(concat(#{personaList.persona.primeraLateral},'%'))",
			"lower(persona.segundaLateral) like lower(concat(#{personaList.persona.segundaLateral},'%'))",
			"lower(persona.direccionLaboral) like lower(concat(#{personaList.persona.direccionLaboral},'%'))",
			"lower(persona.otrasDirecciones) like lower(concat(#{personaList.persona.otrasDirecciones},'%'))",
			"lower(persona.telefonos) like lower(concat(#{personaList.persona.telefonos},'%'))",
			"persona.ciudadByIdCiudadDirecc.departamento.idDepartamento = #{personaList.idDepartamento} ",
			"persona.ciudadByIdCiudadDirecc.departamento.pais.idPais =#{personaList.idPais} ",
			"persona.ciudadByIdCiudadDirecc.idCiudad =#{personaList.idCiudad} ",
			"persona.activo=#{personaList.estado.valor}",
			"persona.paisByIdPaisExpedicionDoc.idPais=#{personaList.idPaisExp}",
	};
	
	private static final String[] RESTRICTIONS_CI = {
		"persona.documentoIdentidad = #{personaList.persona.documentoIdentidad}",
		"persona.activo=#{personaList.estado.valor}",
		"persona.paisByIdPaisExpedicionDoc.idPais=#{personaList.persona.paisByIdPaisExpedicionDoc.idPais}",
};

	private Persona persona = new Persona();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "persona.documentoIdentidad";
	private Long idDepartamento=null;
	private Long idCiudad=null;
	private Long idPais=null;
	private Long idPaisExp=null;
	
	public PersonaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public Persona getPersona() {
		return persona;
	}
	
	public List<Persona> obtenerListaPersonas(){
    	listaPersonas = getResultList(); 
    	return listaPersonas;
    }
	
    public List<Persona> getListaPersonas() {
		return listaPersonas;
	}
    /**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Persona> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	//	getResultList();
		return getResultList();
	}
	
	public List<Persona> listaResultadosByCi() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CI));
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<Persona> limpiar() {
		persona = new Persona();
		estado = Estado.ACTIVO;
		idDepartamento=null;
		idCiudad=null;
		idPais=null;
		idPaisExp=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	/**
	 * 
	 * limpia los parametros de la lista sin ejecutar el query.
	 */
	public void clean() {
		persona = new Persona();
		estado = Estado.ACTIVO;
		idDepartamento=null;
		idCiudad=null;
		idPais=null;
		idPaisExp=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
//	GETTERS Y SETTERS
	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdDepartamento() {
		return idDepartamento;
	}

	public void setIdDepartamento(Long idDepartamento) {
		this.idDepartamento = idDepartamento;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}


	
	
	
	
	
}
