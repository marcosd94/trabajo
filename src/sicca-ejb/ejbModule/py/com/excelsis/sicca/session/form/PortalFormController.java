package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.Identity;

import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("portalFormController")
public class PortalFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2203824703973791354L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true, required = false,value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In
	Identity identity;
	@In(required = false)
	SeguridadUtilFormController seguridadUtilFormController;

	private Long idPersona;
	private Boolean habilitarCarga = false;
	private Boolean habilitarCargaLegajo = false;

	public void init() {
		if (usuarioLogueado != null)
			idPersona = usuarioLogueado.getPersona().getIdPersona();
		verificarRolPortal();
		quedaHabilitadoCarga();
		
	}

	public Boolean cuentaConLegajo() {
		Query q = em.createQuery("select l from Legajos l"
				+ " where  l.persona.idPersona = :id");
		q.setParameter("id", usuarioLogueado.getPersona().getIdPersona());
		List<Legajos> lista = q.getResultList();
		if (!lista.isEmpty())
			return true;
		return false;
	}

	private void quedaHabilitadoCarga() {
		if (usuarioLogueado != null) {
			Query q = em
					.createQuery("select e from EmpleadoPuesto e"
							+ " where e.actual is true and e.activo is true and  e.persona.idPersona = :id");
			q.setParameter("id", usuarioLogueado.getPersona().getIdPersona());
			List<Legajos> lista = q.getResultList();
			if (lista.isEmpty())
				habilitarCarga = true;
			else{
				habilitarCarga = true;
				habilitarCargaLegajo = true;
			}
		}
	}

	public String logout() {
		// if (usuarioLogueado.getConfiguracionUoCab() != null) {
		//
		// }
		identity.logout();
		return "/Portal.xhtml";
	}

	public String getEstiloMenuConcursos() {
		if (seguridadUtilFormController != null
				&& seguridadUtilFormController.getUsuarioLogueado() == null)
			return "height:100px;";

		return "height:300px;";
	}

	/**
	 * Permite desloguear al usuario que no tiene asignado un rol para el acceso
	 * al portal
	 */
	public void verificarRolPortal() {
		if (usuarioLogueado != null && usuarioLogueado.getIdUsuario() != null) {
			if (!tieneRolPortal()) {
				// Se debe desloguear del portal
				identity.logout();
			}
		}
	}

	/**
	 * Verifica si el usuario logueado tiene un rol para el acceso al portal
	 */
	public boolean tieneRolPortal() {
		if (usuarioLogueado != null && usuarioLogueado.getIdUsuario() != null) {
			String consulta = " " + " select distinct r.* " +

			" from seguridad.usuario_rol ur " +

			" join seguridad.rol r " + " on (ur.id_rol = r.id_rol) " +

			" where r.activo is true " + " 	and r.descripcion = 'PORTAL' "
					+ " 	and ur.id_usuario = " + usuarioLogueado.getIdUsuario();

			List<Rol> lista = new ArrayList<Rol>();
			lista = em.createNativeQuery(consulta, Rol.class).getResultList();
			if (lista != null && lista.size() > 0)
				return true;
		}
		return false;
	}

	/**
	 * Verifica si el usuario logueado tiene un rol distinto al de acceso al
	 * portal
	 */
	public boolean tieneRolDistintoPortal() {
		if (usuarioLogueado != null && usuarioLogueado.getIdUsuario() != null) {
			String consulta = " " + " select distinct r.* " +

			" from seguridad.usuario_rol ur " +

			" join seguridad.rol r " + " on (ur.id_rol = r.id_rol) " +

			" where r.activo is true " + " 	and r.descripcion <> 'PORTAL' "
					+ " 	and ur.id_usuario = " + usuarioLogueado.getIdUsuario();

			List<Rol> lista = new ArrayList<Rol>();
			lista = em.createNativeQuery(consulta, Rol.class).getResultList();
			if (lista != null && lista.size() > 0)
				return true;
		}
		return false;
	}

	/**
	 * Controla navegavilidad del home de la aplicacion
	 * 
	 * @return
	 */
	public String initHome() {
		// verificarRolPortal();

		if (tieneRolPortal())
			return "portal";

		return null;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Boolean getHabilitarCarga() {
		return habilitarCarga;
	}

	public void setHabilitarCarga(Boolean habilitarCarga) {
		this.habilitarCarga = habilitarCarga;
	}

	public Boolean getHabilitarCargaLegajo() {
		return habilitarCargaLegajo;
	}

	public void setHabilitarCargaLegajo(Boolean habilitarCargaLegajo) {
		this.habilitarCargaLegajo = habilitarCargaLegajo;
	}

}
