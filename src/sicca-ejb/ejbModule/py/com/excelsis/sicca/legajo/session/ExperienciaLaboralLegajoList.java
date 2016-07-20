package py.com.excelsis.sicca.legajo.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.ExperienciaLaboralLegajo;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import enums.Estado;

@Name("experienciaLaboralLegajoList")
public class ExperienciaLaboralLegajoList extends EntityQuery<ExperienciaLaboralLegajo> {

	private static final String EJBQL = "select experienciaLaboralLegajo from ExperienciaLaboralLegajo experienciaLaboralLegajo";

	private static final String[] RESTRICTIONS = {
			"lower(experienciaLaboralLegajo.empresa) like lower(concat(#{experienciaLaboralLegajoList.experienciaLaboral.empresa},'%'))",
			"lower(experienciaLaboralLegajo.funcionesRealizadas) like lower(concat(#{experienciaLaboralLegajoList.experienciaLaboral.funcionesRealizadas},'%'))",
			"lower(experienciaLaboralLegajo.referenciasLaborales) like lower(concat(#{experienciaLaboralLegajoList.experienciaLaboral.referenciasLaborales},'%'))",
			"experienciaLaboralLegajo.persona.idPersona = #{experienciaLaboralLegajoList.persona.idPersona}",
			"experienciaLaboralLegajo.activo = #{experienciaLaboralLegajoList.experienciaLaboral.activo}",
			"experienciaLaboralLegajo.personaPostulante.idPersonaPostulante = #{experienciaLaboralLegajoList.idPersonaPostulante}",
	};
	private static final String ORDER = "experienciaLaboralLegajo.empresa"; 
	
	private ExperienciaLaboralLegajo experienciaLaboral = new ExperienciaLaboralLegajo();
	private Persona persona = new Persona();
	private Long idPersonaPostulante;

	public ExperienciaLaboralLegajoList() {
		experienciaLaboral.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
	}

//	GETTERS Y SETTERS
	public ExperienciaLaboralLegajo getExperienciaLaboral() {
		return experienciaLaboral;
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
