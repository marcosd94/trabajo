package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;


import enums.Estado;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("postulacionCapList")
public class PostulacionCapList extends EntityQuery<PostulacionCap> {

	private static final String EJBQL = "select postulacionCap from PostulacionCap postulacionCap";

	private static final String[] RESTRICTIONS = {
			"lower(postulacionCap.proyectoCord) like lower(concat(#{postulacionCapList.postulacionCap.proyectoCord},'%'))",
			"lower(postulacionCap.nombreAp) like lower(concat(#{postulacionCapList.postulacionCap.nombreAp},'%'))",
			"lower(postulacionCap.cargo) like lower(concat(#{postulacionCapList.postulacionCap.cargo},'%'))",
			"lower(postulacionCap.direccion) like lower(concat(#{postulacionCapList.postulacionCap.direccion},'%'))",
			"lower(postulacionCap.EMail) like lower(concat(#{postulacionCapList.postulacionCap.EMail},'%'))",
			"lower(postulacionCap.telefono) like lower(concat(#{postulacionCapList.postulacionCap.telefono},'%'))",
			"lower(postulacionCap.fax) like lower(concat(#{postulacionCapList.postulacionCap.fax},'%'))",
			"lower(postulacionCap.proyectoPart) like lower(concat(#{postulacionCapList.postulacionCap.proyectoPart},'%'))",
			"lower(postulacionCap.proyectoActi) like lower(concat(#{postulacionCapList.postulacionCap.proyectoActi},'%'))",
			"lower(postulacionCap.funcionGral) like lower(concat(#{postulacionCapList.postulacionCap.funcionGral},'%'))",
			"lower(postulacionCap.funcionEspecif) like lower(concat(#{postulacionCapList.postulacionCap.funcionEspecif},'%'))",
			"lower(postulacionCap.potencialBenef) like lower(concat(#{postulacionCapList.postulacionCap.potencialBenef},'%'))",
			"lower(postulacionCap.cursoRealiza) like lower(concat(#{postulacionCapList.postulacionCap.cursoRealiza},'%'))",
			"lower(postulacionCap.diasAsisCur) like lower(concat(#{postulacionCapList.postulacionCap.diasAsisCur},'%'))",
			"lower(postulacionCap.horarioCur) like lower(concat(#{postulacionCapList.postulacionCap.horarioCur},'%'))",
			"lower(postulacionCap.otraAct) like lower(concat(#{postulacionCapList.postulacionCap.otraAct},'%'))",
			"lower(postulacionCap.horarioAct) like lower(concat(#{postulacionCapList.postulacionCap.horarioAct},'%'))",
			"lower(postulacionCap.diasAsisAct) like lower(concat(#{postulacionCapList.postulacionCap.diasAsisAct},'%'))",
			"lower(postulacionCap.usuFicha) like lower(concat(#{postulacionCapList.postulacionCap.usuFicha},'%'))",
			"lower(postulacionCap.usuMod) like lower(concat(#{postulacionCapList.postulacionCap.usuMod},'%'))",
			"lower(postulacionCap.usuPost) like lower(concat(#{postulacionCapList.postulacionCap.usuPost},'%'))",
			"lower(postulacionCap.usuCancelacion) like lower(concat(#{postulacionCapList.postulacionCap.usuCancelacion},'%'))",
			"lower(postulacionCap.obsCancelacion) like lower(concat(#{postulacionCapList.postulacionCap.obsCancelacion},'%'))", };
	
	private static final String[] RESTRICTIONS_CU479 = {
		" lower(postulacionCap.persona.nombres) like lower(concat('%',concat(#{postulacionCapList.persona.nombres},'%')))",
		" lower(postulacionCap.persona.apellidos) like lower(concat('%',concat(#{postulacionCapList.persona.apellidos},'%')))",
		" postulacionCap.persona.documentoIdentidad = #{postulacionCapList.persona.documentoIdentidad}",
		" postulacionCap.persona.paisByIdPaisExpedicionDoc.idPais = #{postulacionCapList.idPaisExp}",
		" postulacionCap.activo = #{postulacionCapList.postulacionCap.activo}",
		" postulacionCap.capacitacion.idCapacitacion = #{postulacionCapList.idCapacitacion}",
	};
	private static final String[] RESTRICTIONS_CU483 = {
		" lower(postulacionCap.capacitacion.nombre) like lower(concat('%',concat(#{postulacionCapList.capacitaciones.nombre},'%')))",
		" postulacionCap.capacitacion.modalidad = #{postulacionCapList.capacitaciones.modalidad} ",
		" postulacionCap.capacitacion.datosEspecificosTipoCap.idDatosEspecificos = #{postulacionCapList.idTipoCap}",
		" postulacionCap.capacitacion.tipo in (#{postulacionCapList.tipoCaps})",
		" postulacionCap.persona.idPersona =#{postulacionCapList.idPersona}",
		" postulacionCap.capacitacion.activo = #{postulacionCapList.capacitaciones.activo} ",
	};
	
	

	private PostulacionCap postulacionCap = new PostulacionCap();
	private PersonaPostulante personaPostulante= new PersonaPostulante();
	private static final String ORDER = "postulacionCap.fechaPost asc";
	private Estado estado= Estado.ACTIVO;
	private Long idPaisExp;
	private List<String> tipoCaps = new ArrayList<String>();
	private Integer estadoCap;
	private Long idTipoCap;
	private Capacitaciones capacitaciones= new Capacitaciones();
	private Long idPersona;
	private Persona persona= new Persona();
	private Long idCapacitacion;

	public PostulacionCapList() {
		postulacionCap.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
	}
	public List<PostulacionCap> buscarResultadosCU479(){
		postulacionCap.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU479));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}

	public List<PostulacionCap> limpiarResultadosCU479(){
		postulacionCap = new PostulacionCap();
		estado = Estado.ACTIVO;
		idPaisExp=null;
		persona= new Persona();
		postulacionCap.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU479));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}


	public List<PostulacionCap> buscarResultadosCU483(){
		tipoCaps.add("CAP_SFP");
		tipoCaps.add("CAP_OG290");
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU483));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}

	public PostulacionCap getPostulacionCap() {
		return postulacionCap;
	}

	

	public PersonaPostulante getPersonaPostulante() {
		return personaPostulante;
	}
	public void setPersonaPostulante(PersonaPostulante personaPostulante) {
		this.personaPostulante = personaPostulante;
	}
	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Integer getEstadoCap() {
		return estadoCap;
	}
	public void setEstadoCap(Integer estadoCap) {
		this.estadoCap = estadoCap;
	}
	public void setPostulacionCap(PostulacionCap postulacionCap) {
		this.postulacionCap = postulacionCap;
	}
	public Long getIdTipoCap() {
		return idTipoCap;
	}
	public void setIdTipoCap(Long idTipoCap) {
		this.idTipoCap = idTipoCap;
	}
	public List<String> getTipoCaps() {
		return tipoCaps;
	}
	public void setTipoCaps(List<String> tipoCaps) {
		this.tipoCaps = tipoCaps;
	}
	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}
	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}
	public Long getIdPersona() {
		return idPersona;
	}
	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}
	public Persona getPersona() {
		return persona;
	}
	public void setPersona(Persona persona) {
		this.persona = persona;
	}
	public Long getIdCapacitacion() {
		return idCapacitacion;
	}
	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}
	
	
	
	
}
