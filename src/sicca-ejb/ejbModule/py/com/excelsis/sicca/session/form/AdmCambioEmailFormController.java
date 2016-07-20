package py.com.excelsis.sicca.session.form;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.hibernate.validator.Email;
import org.hibernate.validator.Pattern;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("admCambioEmailFormController")
public class AdmCambioEmailFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	private Persona persona;
	private String emailNuevo;
	private String emailConfirmacion;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {

		persona = new Persona();
		Long idPersona = null;
		idPersona = usuarioLogueado.getPersona().getIdPersona();
		persona = em.find(Persona.class, idPersona);
	}
	
	public String save(){
		if(emailNuevo.equals(emailConfirmacion)){
			persona.setEMail(emailNuevo);
			em.merge(persona);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		}
		else{
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Los emails ingresados no son iguales. Verifique");
			return null;
		}
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	
	@Email
	@Pattern(regex=".+@.+\\.[a-z]+")
	public String getEmailNuevo() {
		return emailNuevo;
	}

	public void setEmailNuevo(String emailNuevo) {
		this.emailNuevo = emailNuevo;
	}

	@Email
	@Pattern(regex=".+@.+\\.[a-z]+")
	public String getEmailConfirmacion() {
		return emailConfirmacion;
	}

	public void setEmailConfirmacion(String emailConfirmacion) {
		this.emailConfirmacion = emailConfirmacion;
	}

}
