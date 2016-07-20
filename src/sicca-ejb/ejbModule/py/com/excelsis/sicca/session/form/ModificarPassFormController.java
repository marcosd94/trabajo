package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.session.UsuarioHome;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Sha1;

@Scope(ScopeType.PAGE)
@Name("modificarPassFormController")
public class ModificarPassFormController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5448145474753309328L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
    EntityManager em;
	
	@In(create = true)
	UsuarioHome usuarioHome;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	private Integer codUsuario;
	private String passAnterior;
	private String passNuevo;
	private String confirmPass;
	
	private String from;

	public void init(){
	}
	
	public String save(){
		String result = null;
		try {
		
			Sha1 sha = new Sha1();
			if(!todoCorrecto()){
				return null;
			}
			usuarioLogueado.setContrasenha(sha.getHash(passNuevo));
			usuarioLogueado.setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
			usuarioLogueado.setFechaMod(new Date());
			em.merge(usuarioLogueado);
			em.flush();
			result = "updated";
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null){
				statusMessages.clear();
				statusMessages.add(Severity.INFO, 
						SeamResourceBundle.getBundle().getString("Usuario_modif_pass_actualizado"));
				usuarioHome.clearInstance();
			}
		}
		return null;
	}
	
//	METODOS PRIVADOS
	private boolean todoCorrecto(){
		Sha1 sha = new Sha1();
		if(passAnterior == null || passAnterior.isEmpty()){
			statusMessages.add(Severity.ERROR, 
					SeamResourceBundle.getBundle().getString("Usuario_modif_pass_msg_sin_pass_viejo"));
			return false;
		}
		if(passNuevo == null || passNuevo.isEmpty()){
			statusMessages.add(Severity.ERROR, 
					SeamResourceBundle.getBundle().getString("Usuario_modif_pass_msg_sin_pass_nuevo"));
			return false;
		}
		if(confirmPass == null || confirmPass.isEmpty()){
			statusMessages.add(Severity.ERROR, 
					SeamResourceBundle.getBundle().getString("Usuario_modif_pass_msg_sin_confirm_pass"));
			return false;
		}
		
		try {
			if(!sha.getHash(passAnterior.trim()).equals(usuarioLogueado.getContrasenha().trim())){
				statusMessages.add(Severity.ERROR, 
						SeamResourceBundle.getBundle().getString("Usuario_modif_pass_actual_incorrecto"));
				return false;
			}
			if(!passNuevo.trim().equals(confirmPass.trim())){
				statusMessages.add(Severity.ERROR, 
						SeamResourceBundle.getBundle().getString("Usuario_modif_pass_no_coinciden"));
				return false;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}
	
//	GETTERS Y SETTERS
	public String getConfirmPass() {
		return confirmPass;
	}
	public void setConfirmPass(String confirmPass) {
		this.confirmPass = confirmPass;
	}
	public String getPassAnterior() {
		return passAnterior;
	}
	public void setPassAnterior(String passAnterior) {
		this.passAnterior = passAnterior;
	}
	public String getPassNuevo() {
		return passNuevo;
	}
	public void setPassNuevo(String passNuevo) {
		this.passNuevo = passNuevo;
	}

	public void setCodUsuario(Integer codUsuario) {
		this.codUsuario = codUsuario;
	}

	public Integer getCodUsuario() {
		return codUsuario;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}
	
}
