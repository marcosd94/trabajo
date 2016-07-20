package py.com.excelsis.sicca.seguridad.session;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.seguridad.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("usuarioList")
public class UsuarioList extends EntityQuery<Usuario> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Usuario> listaUsuarios;
	
	private static final String EJBQL = "select usuario from Usuario usuario";

	private static final String[] RESTRICTIONS = {
		"lower(usuario.codigoUsuario) like lower(concat('%',concat(#{usuarioList.usuario.codigoUsuario},'%')))",
		"usuario.activo = #{usuarioList.usuario.activo}",
		"usuario.usuAlta = #{usuarioList.usuarioAlta}",
		"usuario.usuAlta <> #{usuarioList.usuarioNoAlta}",
		"usuario.persona.documentoIdentidad = #{usuarioList.persona.documentoIdentidad}",
		"lower(usuario.persona.nombres) like lower(concat('%',concat(#{usuarioList.persona.nombres},'%')))",
		"lower(usuario.persona.apellidos) like lower(concat('%',concat(#{usuarioList.persona.apellidos},'%')))",
		"usuario.configuracionUoCab.idConfiguracionUo = #{usuarioList.configuracionUoCab.idConfiguracionUo}",
		"usuario.configuracionUoCab.entidad.sinEntidad.idSinEntidad = #{usuarioList.sinEntidad.idSinEntidad}",
		"usuario.configuracionUoCab.entidad.sinEntidad.nenCodigo = #{usuarioList.sinEntidad.nenCodigo}",
	};
	
	private static final String[] RESTRICTIONS_CU326 = {
		"usuario.activo = #{usuarioList.estado.valor}",
		"usuario.persona.documentoIdentidad = #{usuarioList.persona.documentoIdentidad}",
		"lower(usuario.persona.nombres) like lower(concat('%', concat(#{usuarioList.persona.nombres},'%')))",
		"lower(usuario.persona.apellidos) like lower(concat('%', concat(#{usuarioList.persona.apellidos},'%')))",
		"usuario.persona.paisByIdPaisExpedicionDoc.idPais = #{usuarioList.idPaisDoc}",
		"usuario.usuAlta = #{usuarioList.usuarioAlta}", 
	};
	private static final String ORDER = "usuario.codigoUsuario";
	private static final String ORDER_CU326 = "usuario.persona.documentoIdentidad";
	
	private Usuario usuario = new Usuario();
	private Persona persona = new Persona();
	private Long idPaisDoc ;
	Estado estado;
	private String usuarioAlta;
	private String usuarioNoAlta;
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private SinEntidad sinEntidad= new SinEntidad();

	public UsuarioList() {
		usuario.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
	}
	
	public List<Usuario> filtrarResultados(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Usuario> listaResultadosCU326() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU326));
		setOrder(ORDER_CU326);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<Usuario> limpiarCU326() {
		usuario = new Usuario();
		estado = null;
		idPaisDoc = null;
		persona = new Persona();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU326));
		setOrder(ORDER_CU326);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	
//	GETTERS Y SETTERS
	public Usuario getUsuario() {
		return usuario;
	}
	public Persona getPersona() {
		return persona;
	}
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public Long getIdPaisDoc() {
		return idPaisDoc;
	}

	public void setIdPaisDoc(Long idPaisDoc) {
		this.idPaisDoc = idPaisDoc;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<Usuario> getListaUsuarios() {
		return listaUsuarios;
	}

	public void setListaUsuarios(List<Usuario> listaUsuarios) {
		this.listaUsuarios = listaUsuarios;
	}

	public List<Usuario> obtenerListaUsuarios(){
    	listaUsuarios = listaResultadosCU326(); 
    	return listaUsuarios;
    }

	public String getUsuarioAlta() {
		return usuarioAlta;
	}

	public void setUsuarioAlta(String usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}

	public void setUsuarioNoAlta(String usuarioNoAlta) {
		this.usuarioNoAlta = usuarioNoAlta;
	}

	public String getUsuarioNoAlta() {
		return usuarioNoAlta;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}
	
	
	
	
}
