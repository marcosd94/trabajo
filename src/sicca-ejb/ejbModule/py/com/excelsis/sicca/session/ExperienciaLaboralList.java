package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;

@Name("experienciaLaboralList")
public class ExperienciaLaboralList extends EntityQuery<ExperienciaLaboral> {

	private static final String EJBQL = "select experienciaLaboral from ExperienciaLaboral experienciaLaboral";

	private static final String[] RESTRICTIONS = {
			"lower(experienciaLaboral.empresa) like lower(concat(#{experienciaLaboralList.experienciaLaboral.empresa},'%'))",
			"lower(experienciaLaboral.funcionesRealizadas) like lower(concat(#{experienciaLaboralList.experienciaLaboral.funcionesRealizadas},'%'))",
			"lower(experienciaLaboral.referenciasLaborales) like lower(concat(#{experienciaLaboralList.experienciaLaboral.referenciasLaborales},'%'))",
			"experienciaLaboral.persona.idPersona = #{experienciaLaboralList.persona.idPersona}",
			"experienciaLaboral.activo = #{experienciaLaboralList.experienciaLaboral.activo}",
			"experienciaLaboral.personaPostulante.idPersonaPostulante = #{experienciaLaboralList.idPersonaPostulante}",
	};
	private static final String ORDER = "experienciaLaboral.fechaDesde"; 
	
	private ExperienciaLaboral experienciaLaboral = new ExperienciaLaboral();
	private Persona persona = new Persona();
	private Long idPersonaPostulante;

	public ExperienciaLaboralList() {
		experienciaLaboral.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder("experienciaLaboral.fechaDesde, experienciaLaboral.fechaHasta");
		setOrderDirection("DESC");
	}

//	GETTERS Y SETTERS
	public ExperienciaLaboral getExperienciaLaboral() {
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
