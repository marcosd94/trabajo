package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.session.UsuarioList;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.PAGE)
@Name("usuarioListFormController")
public class UsuarioListFormController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4348610502265759297L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value="entityManager")
    EntityManager em;	
	
	@In(create = true)
	UsuarioList usuarioList;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
	private Estado estado = Estado.ACTIVO;
	
	private Usuario usuario = new Usuario();
	private Persona persona = new Persona();

	
	private List<Usuario> userList = new ArrayList<Usuario>();
	
	private String tipoUsuario;

	public void init(){
		nivelEntidadOeeUtil.init();
		search();
		
	}
	
	public void search(){
		if(usuario.getCodigoUsuario() != null && !usuario.getCodigoUsuario().isEmpty())
			usuarioList.getUsuario().setCodigoUsuario(usuario.getCodigoUsuario().trim());
		if(persona.getDocumentoIdentidad() != null && !persona.getDocumentoIdentidad().isEmpty())
			usuarioList.getPersona().setDocumentoIdentidad(persona.getDocumentoIdentidad().trim());
		if(persona.getNombres() != null && !persona.getNombres().isEmpty())
			usuarioList.getPersona().setNombres(persona.getNombres().trim());
		if(persona.getApellidos() != null && !persona.getApellidos().isEmpty())
			usuarioList.getPersona().setApellidos(persona.getApellidos().trim());
		usuarioList.getSinEntidad().setIdSinEntidad(nivelEntidadOeeUtil.getIdSinEntidad());
		usuarioList.getSinEntidad().setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad());
		usuarioList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		
		usuarioList.getUsuario().setActivo(estado.getValor());
		
		if (tipoUsuario != null){
			if ("P".equals(tipoUsuario))
				usuarioList.setUsuarioAlta("PORTAL");
			else
				usuarioList.setUsuarioNoAlta("PORTAL");
		}	
		
		usuarioList.getResultList();
	}
	
	public void searchAll(){
		usuario = new Usuario();
		persona = new Persona();
		estado= Estado.ACTIVO;
		tipoUsuario = null;
		usuarioList.getUsuario().setCodigoUsuario(null);
		usuarioList.getPersona().setDocumentoIdentidad(null);
		usuarioList.getPersona().setApellidos(null);
		usuarioList.getPersona().setDocumentoIdentidad(null);
		usuarioList.getConfiguracionUoCab().setIdConfiguracionUo(null);
		usuarioList.setUsuarioAlta(null);
		usuarioList.setUsuarioNoAlta(null);
		usuarioList.setSinEntidad(new SinEntidad());
		nivelEntidadOeeUtil.limpiar();
		usuarioList.getResultList();
	}
	
	
	
	
	
	
//	GETTERS Y SETTERS
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setPersona(Persona persona) {
		this.persona = persona;
	}
	public Persona getPersona() {
		return persona;
	}

	
	
	public List<Usuario> getUserList() {
		return userList;
	}
	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public void setUserList(List<Usuario> userList) {
		this.userList = userList;
	}
	
	
	public void setTipoUsuario(String tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public String getTipoUsuario() {
		return tipoUsuario;
	}
	
}
