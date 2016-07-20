package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.PAGE)
@Name("postulacionList")
public class PostulacionList extends EntityQuery<Postulacion> {

	private static final String EJBQL = "select postulacion from Postulacion postulacion";

	private static final String[] RESTRICTIONS = {
			"lower(postulacion.usuPostulacion) like lower(concat(#{postulacionList.postulacion.usuPostulacion},'%'))",
			"lower(postulacion.obsCancelacion) like lower(concat(#{postulacionList.postulacion.obsCancelacion},'%'))",
			"lower(postulacion.usuCancelacion) like lower(concat(#{postulacionList.postulacion.usuCancelacion},'%'))",
			"lower(postulacion.interesCargo) like lower(concat(#{postulacionList.postulacion.interesCargo},'%'))", 
			"lower(postulacion.concursoPuestoAgr.concurso.configuracionUoCab.entidad.sinEntidad.entNombre) like concat('%',lower(concat(#{postulacionList.entidad},'%')))",
			"lower(postulacion.concursoPuestoAgr.descripcionGrupo) like concat('%',lower(concat(#{postulacionList.grupo},'%')))",
			"postulacion.concursoPuestoAgr.concurso.datosEspecificosTipoConc.idDatosEspecificos =#{postulacionList.idTipoConcurso}",
			"postulacion.activo=#{postulacionList.estado.valor}",
			"postulacion.personaPostulante.persona.idPersona=#{postulacionList.idPersona}",
			
	};
	private static final String[] RESTRICTIONS_CARPETA = {
		"lower(postulacion.usuPostulacion) like lower(concat(#{postulacionList.postulacion.usuPostulacion},'%'))",
		"lower(postulacion.obsCancelacion) like lower(concat(#{postulacionList.postulacion.obsCancelacion},'%'))",
		"lower(postulacion.usuCancelacion) like lower(concat(#{postulacionList.postulacion.usuCancelacion},'%'))",
		"lower(postulacion.interesCargo) like lower(concat(#{postulacionList.postulacion.interesCargo},'%'))", 
		"lower(postulacion.concursoPuestoAgr.concurso.configuracionUoCab.entidad.sinEntidad.entNombre) like concat('%',lower(concat(#{postulacionList.entidad},'%')))",
		"lower(postulacion.concursoPuestoAgr.descripcionGrupo) like concat('%',lower(concat(#{postulacionList.grupo},'%')))",
		"postulacion.concursoPuestoAgr.concurso.datosEspecificosTipoConc.idDatosEspecificos =#{postulacionList.idTipoConcurso}",
		"postulacion.activo=#{postulacionList.estado.valor}",
		"postulacion.persona.idPersona=#{postulacionList.idPersona}",
		
};

	private Postulacion postulacion = new Postulacion();
	private Estado estado= null;
	private static final String ORDER = "postulacion.interesCargo";
	private Long idPersona;
	private Long idTipoConcurso;
	private String entidad;
	private String grupo;
	public PostulacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Postulacion> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);

		return getResultList();
	}
	public List<Postulacion> listaResultadosCarpeta() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CARPETA));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);

		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<Postulacion> limpiarXusuario() {
		postulacion= new Postulacion();
		estado = Estado.ACTIVO;
		idTipoConcurso=null;
		entidad=null;
		grupo=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public Postulacion getPostulacion() {
		return postulacion;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getIdTipoConcurso() {
		return idTipoConcurso;
	}

	public void setIdTipoConcurso(Long idTipoConcurso) {
		this.idTipoConcurso = idTipoConcurso;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	
	
	
	
}
