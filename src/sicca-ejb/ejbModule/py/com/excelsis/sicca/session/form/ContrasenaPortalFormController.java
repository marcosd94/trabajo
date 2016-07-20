package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioHome;
import py.com.excelsis.sicca.session.CiudadHome;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.PasswordGenerator;
import py.com.excelsis.sicca.util.Sha1;

@Scope(ScopeType.CONVERSATION)
@Name("contrasenaPortalFormController")
public class ContrasenaPortalFormController implements Serializable{

	/**
	 * 
	 */
//	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	

	
	@In(value = "entityManager")
	EntityManager em;
	

	
	

	
	public String save(Long idUsuario){
		try {
		
			Sha1 sha = new Sha1();
			PasswordGenerator passDefault= new PasswordGenerator();
			Usuario usuario= em.find(Usuario.class, idUsuario);
			String pass=passDefault.getPassword();
			usuario.setContrasenha(sha.getHash(pass));
			em.merge(usuario);
			em.flush();
			return pass;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}


	



	

	
	
	
}
