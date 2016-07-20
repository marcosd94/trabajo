package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;
import py.com.excelsis.sicca.entity.Parentesco;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;

@Name("parentescoList")
public class ParentescoList extends EntityQuery<Parentesco> {
	private static final String EJBQL = "select parentesco from Parentesco parentesco";
		
	private static final String[] RESTRICTIONS = {
		"lower(parentesco.nombres) like lower(concat(#{parentescoList.parentesco.nombres},'%'))",
		"lower(parentesco.apellidos) like lower(concat(#{parentescolList.parentesco.apellidos},'%'))",
		"lower(parentesco.documentoIdentidad) like lower(concat(#{parentescoList.parentesco.documentoIdentidad},'%'))",
		"lower(parentesco.institucion) like lower(concat(#{parentescoList.parentesco.institucion},'%'))",
		"parentesco.datosEspecificos.id= #{parentescoList.datosEspecificos.idDatosEspecificos}",
		"parentesco.persona.idPersona = #{parentescolList.persona.idPersona}",
		"parentesco.activo = #{parentescoList.parentesco.activo}",
		"parentesco.personaPostulante.idPersonaPostulante = #{parentescoList.idPersonaPostulante}", 
		};
	private Parentesco parentesco = new Parentesco();
	private Persona persona = new Persona();
	private Long idPersonaPostulante;
	
	public ParentescoList() {
		parentesco.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder("parentesco.nombres");
		setOrderDirection("DESC");
	}
//	GETTERS Y SETTERS
	public  Parentesco getParentesco() {
		return parentesco;
	}
	public Persona getPersona() {
		return persona;
	}

	public Long getIdPersonaPostulante() {
		return idPersonaPostulante;
	}

	public void setIdPersonaPostulante(Long idPersonaPostulante) {
		this.idPersonaPostulante = idPersonaPostulante;
	}
	
	


}
