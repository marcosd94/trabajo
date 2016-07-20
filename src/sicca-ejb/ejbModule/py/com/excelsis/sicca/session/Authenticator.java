package py.com.excelsis.sicca.session;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.UsuariosConectados;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.RolFuncion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Sha1;

@Name("authenticator")
public class Authenticator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7176094672246582468L;

	@Logger
	private Log log;

	@In
	Identity identity;
	@In
	Credentials credentials;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(create = true)
	EntityManager entityManager;
	@In
	StatusMessages statusMessages;

	@Out(scope = ScopeType.SESSION, required = false)
	@In(required = false)
	Usuario usuarioLogueado;

	@In(required = false, create = true)
	@Out(scope = ScopeType.SESSION, required = false)
	private Actor actor;

	FacesContext facesContext = FacesContext.getCurrentInstance();

	private List<String> roles = null;
	// Para longin Portal
	private Boolean fromPortal;
	private Usuario usuarioPortal = new Usuario();
	private String cedPortal;
	private Long idPais;

	public String getCedPortal() {
		return cedPortal;
	}

	public void setCedPortal(String cedPortal) {
		this.cedPortal = cedPortal;
	}

	@SuppressWarnings("unchecked")
	public boolean authenticate() {

		Sha1 sha = new Sha1();

		if (fromPortal == null) {
			log.info("authenticating {0}", credentials.getUsername());
			try {
				List<Usuario> resultado =
					entityManager.createNamedQuery("Usuario.login").setParameter("U", credentials.getUsername().trim().toLowerCase()).setParameter("P", sha.getHash(credentials.getPassword().trim())).getResultList();
				if (resultado.size() == 0) {
					return false;
				}
				usuarioLogueado = resultado.get(0);

				if (credentials.getPassword().equals("12345")) {
					statusMessages.add(Severity.WARN, SeamResourceBundle.getBundle().getString("msg_change_pass_default"));
				}

				/******** ADD ROLES ***************/
				asignarRoles(resultado.get(0));
				/******************************/

				// Asignar Actor para el jBPM
				actor.setId(usuarioLogueado.getCodigoUsuario());

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {

			log.info("authenticating {0}", cedPortal);
			credentials.setUsername(usuarioPortal.getCodigoUsuario());
			credentials.setPassword(usuarioPortal.getContrasenha());
			try {
				List<Usuario> resultadosPortal =
					entityManager.createNamedQuery("Usuario.portal").setParameter("U", cedPortal).setParameter("P", sha.getHash(usuarioPortal.getContrasenha().trim())).setParameter("T", idPais).getResultList();

				if (resultadosPortal.size() == 0) {
					return false;
				}
				usuarioLogueado = resultadosPortal.get(0);
				// credentials.setPassword(usuarioPortal.getContrasenha());
				// credentials.setUsername(usuarioLogueado.getCodigoUsuario());
				if (usuarioLogueado.getContrasenha().equals("12345")) {
					statusMessages.add(Severity.WARN, SeamResourceBundle.getBundle().getString("msg_change_pass_default"));
				}
				/******** ADD ROLES ***************/
				asignarRoles(resultadosPortal.get(0));
				/******************************/

				// Asignar Actor para el jBPM
				actor.setId(usuarioLogueado.getCodigoUsuario());

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return false;
			}

		}

		return true;
	}

	public String autenticacionPortal() {
		fromPortal = true;

		if (!authenticate()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("org.jboss.seam.loginFailed"));
		} else {
			insertarUsuarioConectado();
			identity.login();

			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Bienvenido " + usuarioLogueado.getCodigoUsuario());

			/*if (usuarioLogueado.getPersona() != null //MODIFICADO RV
				&& usuarioLogueado.getPersona().getEsFuncionario() == null) {
				return "/seleccion/administrarFuncionarioOee/FuncionarioOeeEdit.seam";
			}*/

			return "/cuentaUsuarioPortal.xhtml";
		}

		return "/Portal.xhtml";

	}
	
	public String autenticacionPortalCapacitacion() {
		fromPortal = true;

		if (!authenticate()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("org.jboss.seam.loginFailed"));
		} else {
			insertarUsuarioConectado();
			identity.login();

			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Bienvenido " + usuarioLogueado.getCodigoUsuario());

			/*if (usuarioLogueado.getPersona() != null //MODIFICADO RV
				&& usuarioLogueado.getPersona().getEsFuncionario() == null) {
				return "/seleccion/administrarFuncionarioOee/FuncionarioOeeEdit.seam";
			}*/

			return "/capacitacion/buscarCapacitaciones/BuscarCapacitacionesProcPostulacion.xhtml";
		}

		return "/Portal.xhtml";

	}

	private void insertarUsuarioConectado() {
		try {
			HttpServletRequest request =
				(HttpServletRequest) facesContext.getExternalContext().getRequest();
			UsuariosConectados usuariosConectados = new UsuariosConectados();
			usuariosConectados.setDireccionIp(request.getRemoteAddr());
			String user_agent = request.getHeader("User-Agent");
			if (user_agent.indexOf("MSIE") != -1) {
				usuariosConectados.setNavegador("Internet Explorer");
			} else if (user_agent.indexOf("Mozilla") != -1) {
				usuariosConectados.setNavegador("Mozilla");
			} else if (user_agent.indexOf("Crome") != -1) {
				usuariosConectados.setNavegador("Crome");
			} else {
				usuariosConectados.setNavegador("UnKnown");
			}

			usuariosConectados.setFechaIngreso(new Date());
			usuariosConectados.setUsuario(usuarioLogueado.getIdUsuario());
			entityManager.persist(usuariosConectados);
			entityManager.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void asignarRoles(Usuario usuario) {
		try {
			traerPermisos(usuario);
		} catch (Exception ex) {
			ex.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_error_permisos"));
		}
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private List<UsuarioRol> traerUsuRoles(Usuario usuario) {
		Session session = jpaResourceBean.getSession();
		Criteria crit = session.createCriteria(UsuarioRol.class);
		Criteria critUsu = crit.createCriteria("usuario", "usu");
		crit.add(Restrictions.eq("usu.codigoUsuario", usuario.getCodigoUsuario()));
		return crit.list();
	}

	private List<String> traerPermisos(Usuario usuario) {
		roles = new ArrayList<String>();
		List<UsuarioRol> lUsuRol = traerUsuRoles(usuario);
		List<RolFuncion> lRolFuncion = null;
		for (UsuarioRol usuRol : lUsuRol) {
			Rol rol = usuRol.getRol();
			identity.addRole(rol.getDescripcion());
			roles.add(rol.getDescripcion());
			lRolFuncion = traerRolFuncion(usuRol.getRol().getId());
			for (RolFuncion rolFuncion : lRolFuncion) {
				identity.addRole(rolFuncion.getFuncion().getUrl());
			}
		}
		return roles;
	}

	private List<RolFuncion> traerRolFuncion(Long idRol) {
		Query q =
			entityManager.createQuery("select RolFuncion from RolFuncion RolFuncion where RolFuncion.rol.idRol =:idRol ");
		q.setParameter("idRol", idRol);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public Boolean tieneRolPortal() {
		String sql =
			"select r.* from seguridad.rol r " + "join seguridad.usuario_rol ur "
				+ "on ur.id_rol = r.id_rol " + "join seguridad.usuario u "
				+ "on u.id_usuario = ur.id_usuario " + "where r.descripcion like '%PORTAL%' ";
		if (usuarioPortal == null || usuarioPortal.getIdUsuario() == null)
			sql = sql + "and u.id_usuario = " + usuarioLogueado.getIdUsuario();
		if (usuarioPortal != null && usuarioPortal.getIdUsuario() != null)
			sql = sql + "and u.id_usuario = " + usuarioPortal.getIdUsuario();

		List<Rol> r = new ArrayList<Rol>();
		r = entityManager.createNativeQuery(sql, Rol.class).getResultList();
		if (r.size() > 0)
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	private Long buscarIdPy() {
		String sql =
			"select * " + "from general.pais p " + "where p.activo is true "
				+ "and p.descripcion = 'PARAGUAY'";
		List<Pais> ps = new ArrayList<Pais>();
		ps = entityManager.createNativeQuery(sql, Pais.class).getResultList();
		if (ps.size() > 0)
			return ps.get(0).getIdPais();
		return null;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Boolean getFromPortal() {
		return fromPortal;
	}

	public void setFromPortal(Boolean fromPortal) {
		this.fromPortal = fromPortal;
	}

	public Usuario getUsuarioPortal() {
		return usuarioPortal;
	}

	public void setUsuarioPortal(Usuario usuarioPortal) {
		this.usuarioPortal = usuarioPortal;
	}

	public Long getIdPais() {
		if (idPais == null)
			return buscarIdPy();
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

}
