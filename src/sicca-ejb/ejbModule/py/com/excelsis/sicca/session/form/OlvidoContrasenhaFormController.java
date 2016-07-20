package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.PasswordGenerator;
import py.com.excelsis.sicca.util.Sha1;

@Name("olvidoContrasenhaFormController")
@Scope(ScopeType.PAGE)
public class OlvidoContrasenhaFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6620350497281472695L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;
	private Long idPais;
	String ci;
	private String pass;
	private String protocolo;
	private String host;
	private String usuario;
	private String contrasenha;
	private Persona personaExite;
	private Usuario usuarioPortal = new Usuario();

	public void init() {
		idPais = buscarIdPy();
	}
	
	@SuppressWarnings("unchecked")
	private Long buscarIdPy(){
		String sql = "select * " +
				"from general.pais p " +
				"where p.activo is true " +
				"and p.descripcion = 'PARAGUAY'";
		List<Pais> ps = new ArrayList<Pais>();
		ps = em.createNativeQuery(sql, Pais.class).getResultList();
		if(ps.size() > 0)
			return ps.get(0).getIdPais();
		return null;
	}

	public String save() throws NoSuchAlgorithmException {
		String result = null;
		try {
			if (!checkData()) {
				return null;
			}

			Sha1 sha = new Sha1();
			PasswordGenerator passDefault = new PasswordGenerator();
			pass = passDefault.getPassword();
			usuarioPortal = buscarUsuario();
			//ZD 02/11/15 - SI NO EXISTE EL USUARIO, NO ENVIAR EMAIL
			if (usuarioPortal != null) {
				usuarioPortal.setContrasenha(sha.getHash(pass));
				em.merge(usuarioPortal);
				em.flush();
				
				if (!enviarEmails()) {
					statusMessages.add("El email no pudo ser enviado, verifique por favor");
					return null;
				}

				statusMessages.add("Su contrase\u00F1a ha sido cambiada, en breve recibir\u00E1 un mail con su nueva contrase\u00F1a.");
				result = "persisted";
			}else{
				statusMessages.add("No existe el Usuario para el nro de documento especificado.");
				result = null;
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("msg_operacion_exitosa")
					+ ". Su contrase\u00F1a ha sido cambiada, en breve recibir\u00E1 un mail con su nueva contrase\u00F1a.");
			}
		}
		return null;
	}

	// generalMail.enviarMails(o.getCorreoElectronico1(), "elfrom",
	// "Estimado/a " + nombre +
	// ", su solicitud fue aprobada y que tiene 15 d�as para entregar  sus documentos en la oficina de la ORP. Saludos cordiales!",
	// "PST Aprobada!", laConfigu);

	public Boolean enviarMails(String destinatario, String from, String contenido, String asunto) {
		Properties props = new Properties();
		List<Referencias> ref =
			em.createQuery(" select r from Referencias r"
				+ " where r.dominio like 'CONFIG_EMAIL' and r.activo= true").getResultList();
		if (ref.isEmpty()) {
			return false;
		}
		String protocol = "", host = "", user = "", pass = "", port = "", usu = "";
		int i = 0;
		for (Referencias r : ref) {
			if (r.getDescAbrev().equals("HOST"))
				host = r.getDescLarga();

			if (r.getDescAbrev().equals("USER"))
				user = r.getDescLarga();

			if (r.getDescAbrev().equals("PORT"))
				port = r.getDescLarga();

			if (r.getDescAbrev().equals("PASS"))
				pass = r.getDescLarga();

			if (r.getDescAbrev().equals("PROTOCOLO"))
				protocol = r.getDescLarga();
			
			if (r.getDescAbrev().equals("USUARIO"))
				usu = r.getDescLarga();
			
		}

		/*
		 * Ejemplo props.setProperty("mail.transport.protocol", "smtp"); props.setProperty("mail.host", "mail.copaco.com.py"); props.setProperty("mail.user", "menfran@click.com.py");
		 * props.setProperty("mail.password", "12345678");
		 */

		props.setProperty("mail.transport.protocol", protocol);
		props.setProperty("mail.host", host);
		props.setProperty("mail.user", user);
		props.setProperty("mail.password", pass);
		props.setProperty("mail.smtp.auth", "true");
		//javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(props, null);
		javax.mail.Transport transport;
		
		//Autenticador
		Authenticator aut = new Authenticator() {
			
            
            protected PasswordAuthentication getPasswordAuthentication(){
            	List<Referencias> ref = em.createQuery(" select r from Referencias r"
						+ " where r.dominio like 'CONFIG_EMAIL' and r.activo= true").getResultList();

				if (ref.isEmpty()) {
					return null;
				}
				
				String protocol = "", host = "", user = "", pass = "", port = "", usuarioSend = "";
				int i = 0;
				for (Referencias r : ref) {
					if (r.getDescAbrev().equals("HOST"))
						host = r.getDescLarga();

					if (r.getDescAbrev().equals("USER"))
						user = r.getDescLarga();

					if (r.getDescAbrev().equals("PORT"))
						port = r.getDescLarga();

					if (r.getDescAbrev().equals("PASS"))
						pass = r.getDescLarga();

					if (r.getDescAbrev().equals("PROTOCOLO"))
						protocol = r.getDescLarga();

					if (r.getDescAbrev().equals("USUARIO"))
						usuarioSend = r.getDescLarga();

				}
				         	
                PasswordAuthentication passwordAuthentication=new PasswordAuthentication(user,pass);
                return passwordAuthentication;
              }};
		
		javax.mail.Session mailSession = javax.mail.Session.getInstance(props, aut);
		
		try {
			transport = mailSession.getTransport();
			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(asunto);
			message.setContent(contenido, "text/html");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
			message.setFrom(new InternetAddress(user, usu));
			message.setSentDate(new Date());
			message.setHeader("-", "-");
			transport.connect();
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}



	private Usuario buscarUsuario() {
		String cod = "PORTAL";
		List<Usuario> usuarios =
			em.createQuery("select u from Usuario u " + " where u.persona.idPersona="
				+ personaExite.getIdPersona() + " and u.usuAlta='" + cod + "'").getResultList();
		if (!usuarios.isEmpty()) {
			return usuarios.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private boolean checkData() {
		if (ci == null || ci.isEmpty() || idPais == null) {
			statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
			return false;
		} else {
			List<Persona> p =
				em.createQuery("select p from Persona p "
					+ " where p.paisByIdPaisExpedicionDoc.idPais=" + idPais + " "
					+ " and  lower(p.documentoIdentidad) like '" + ci.trim().toLowerCase() + "'").getResultList();
			if (!p.isEmpty()) {
				personaExite = em.find(Persona.class, p.get(0).getIdPersona());
			} else {
				statusMessages.add(Severity.ERROR, "El nro de documento especificado no existe, volver a intentarlo");
				return false;
			}
		}
		return true;
	}

	/***
	 * PARA EL ENVIO DE MAIL
	 ***/
	public Boolean enviarEmails() {
		String error = "";

		String email = personaExite.getEMail();
		String asunto = null;
		asunto = " Asistencia de Contrase\u00F1a - Portal Paraguay Concursa ";

		String mensaje =
			" <p> <b>Estimado/a : "
				+ personaExite.getNombres() +" "+
				personaExite.getApellidos()
				+ ".</b></p>"
				+ "<p align=\"justify\">&nbsp; </p>"
				+ "<p> Usted ha solicitado recuperar su contrase&ntilde;a, hemos asignado la siguiente:</p>"
				+ "<p><b>Contrase&ntilde;a: "
				+ pass
				+ "</b></p>"
				+ " <p>Deber&aacute; ingresar al Portal Paraguay Concursa a trav&eacute;s de este enlace: <a href="
				+ "http://www.paraguayconcursa.gov.py/"
				+ "> Portal Paraguay Concursa</a>, con la nueva contrase&ntilde;a "
				+ " y recomendamos que lo modifique a trav&eacute;s del apartado <i>Modificar Contrase&ntilde;a</i>.</p>";
		try {
			enviarMails(email, null, mensaje, asunto);
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			error = e.getMessage();
			return false;
		}

	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public Persona getPersonaExite() {
		return personaExite;
	}

	public void setPersonaExite(Persona personaExite) {
		this.personaExite = personaExite;
	}

	public Usuario getUsuarioPortal() {
		return usuarioPortal;
	}

	public void setUsuarioPortal(Usuario usuarioPortal) {
		this.usuarioPortal = usuarioPortal;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getContrasenha() {
		return contrasenha;
	}

	public void setContrasenha(String contrasenha) {
		this.contrasenha = contrasenha;
	}

}
