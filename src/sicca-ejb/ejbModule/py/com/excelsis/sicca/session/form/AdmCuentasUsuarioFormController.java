package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
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
import org.omg.CSI.EstablishContext;

import enums.Estado;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.session.UsuarioList;
import py.com.excelsis.sicca.session.PaisList;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Sha1;
import py.com.excelsis.sicca.util.PasswordGenerator;

@Scope(ScopeType.PAGE)
@Name("admCuentasUsuarioFormController")
public class AdmCuentasUsuarioFormController implements Serializable {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	PaisList paisList;
	@In(create = true)
	UsuarioList usuarioList;

	private Long idPaisDoc;
	private String docCi;
	private String nombres;
	private String apellidos;
	Estado estado;
	private String usuarioAlta = "PORTAL";
	Usuario usuario;
	private String pass;
	private String usuarioEmail;
	private String protocolo;
	private String host;
	private String puerto;

	private String contrasenha;
	private String usuarioSend;

	private List<Usuario> lista = new ArrayList<Usuario>();

	public void init() {
		idPaisDoc = searchPais();
		estado = Estado.ACTIVO;
		search();
	}

	private Long searchPais() {
		Pais pais = new Pais();
		pais = paisList.searchPais();
		if (pais != null)
			return pais.getIdPais();
		return null;
	}

	public void search() {

		if (idPaisDoc != null)
			usuarioList.setIdPaisDoc(idPaisDoc);
		usuarioList.getPersona().setDocumentoIdentidad(
				docCi != null ? docCi.trim() : null);
		usuarioList.getPersona().setNombres(
				nombres != null ? nombres.trim() : null);
		usuarioList.getPersona().setApellidos(
				apellidos != null ? apellidos.trim() : null);
		usuarioList.setUsuarioAlta("PORTAL");
		usuarioList.setEstado(estado);
		lista = usuarioList.listaResultadosCU326();
	}

	public void searchAll() {
		idPaisDoc = null;
		docCi = null;
		nombres = null;
		apellidos = null;
		estado = null;
		usuarioList.setUsuarioAlta("PORTAL");
		lista = usuarioList.limpiarCU326();

	}

	public void inactivar(Integer row) {
		Usuario usu = new Usuario();

		usu = lista.get(row);
		if (usu.getActivo())
			usu.setActivo(false);
		else
			usu.setActivo(true);
		usu.setFechaMod(new Date());
		usu.setUsuMod(usuarioLogueado.getCodigoUsuario());
		try {
			em.merge(usu);
			em.flush();
			searchAll();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());

		}
	}

	public void activar(Integer row) {
		Usuario usu = new Usuario();

		usu = lista.get(row);
		if (usu.getActivo())
			usu.setActivo(false);
		else
			usu.setActivo(true);
		usu.setFechaMod(new Date());
		usu.setUsuMod(usuarioLogueado.getCodigoUsuario());
		try {
			em.merge(usu);
			em.flush();
			searchAll();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());

		}
	}

	public String reestablecerContrasenha(Integer row)
			throws NoSuchAlgorithmException {
		String result = null;
		Usuario usu = new Usuario();
		usu = lista.get(row);
		if(!usu.getActivo()){
			searchAll();
			statusMessages
			.add("El usuario esta inactivo, no se puede realizar la accion");
			return null;
		}
		try {

			Sha1 sha = new Sha1();
			PasswordGenerator passDefault = new PasswordGenerator();
			pass = passDefault.getPassword();

			usu.setContrasenha(sha.getHash(pass));
			em.merge(usu);
			em.flush();

			if (!enviarEmails(usu)) {
				statusMessages
						.add("El email no pude ser enviado, verifique por favor");
				searchAll();
				return null;
			}

			statusMessages
					.add("Usuario registrado, en breve recibira un mail con su contrase\u00F1a");
			result = "persisted";
			searchAll();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				statusMessages.clear();
				statusMessages
						.add(Severity.INFO,
								SeamResourceBundle.getBundle().getString(
										"msg_operacion_exitosa")
										+ "Usuario registrado, en breve recibira un mail con su contrase\u00F1a");
			}
		}
		return null;
	}

	/***
	 * PARA EL ENVIO DE MAIL
	 * ***/
	public Boolean enviarEmails(Usuario us) {
		String error = "";
		Persona persona = new Persona();
		persona = us.getPersona();
		String email = persona.getEMail();
		String asunto = null;
		asunto = " Asistencia de Contraseñas – Portal SICCA ";

		String mensaje = " <b>Estimado/a :</b>"
				+ "<b>"
				+ persona.getNombres()
				+ "</b> "
				+ "<b>"
				+ persona.getApellidos()
				+ ".</b><br>"
				+ "Usted ha solicitado restablecer su contraseña, hemos asignado la siguiente:<br>"
				+ "<b>CONTRASEÑA:</b> "
				+ "<b>"
				+ pass
				+ "</b><br>"
				+ " Deberá ingresar al Portal de SICCA a través de este enlace <url>, con la nueva contraseña "
				+ " y recomendamos que lo modifique a través del apartado <i>Modificar Contraseña</i>.";
		try {
		
			enviarMails(email, "nnoguera@excelsis.com.py", mensaje, asunto);
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			error = e.getMessage();
			e.printStackTrace();
			return false;
		}

	}

	public Boolean enviarMails(String destinatario, String from,
			String contenido, String asunto) {

		Properties props = new Properties();
		traerCuentaMailActiva();

		props.setProperty("mail.transport.protocol", protocolo);
		props.setProperty("mail.host", host);
		props.setProperty("mail.user", usuarioEmail);
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
			
			message.setFrom(new InternetAddress(usuarioEmail,usuarioSend));
			message.setSentDate(new Date());
			message.setHeader("-", "-");
			transport.connect();
			transport.sendMessage(message, message.getAllRecipients());

			transport.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	private void traerCuentaMailActiva() {
		List<Referencias> ref =
			em.createQuery(" select r from Referencias r"
				+ " where r.dominio like 'CONFIG_EMAIL' and r.activo= true").getResultList();
		if (ref.isEmpty()) {
			return ;
		}


		
		int i = 0;
		for (Referencias r : ref) {
			if (r.getDescAbrev().equals("HOST"))
				host = r.getDescLarga();

			if (r.getDescAbrev().equals("USER"))
				usuarioEmail = r.getDescLarga();

			if (r.getDescAbrev().equals("PORT"))
				puerto = r.getDescLarga();

			if (r.getDescAbrev().equals("PASS"))
				pass = r.getDescLarga();

			if (r.getDescAbrev().equals("PROTOCOLO"))
				protocolo = r.getDescLarga();
			if (r.getDescAbrev().equals("USUARIO"))
				usuarioSend = r.getDescLarga();

		}

	}

	public Long getIdPaisDoc() {
		return idPaisDoc;
	}

	public void setIdPaisDoc(Long idPaisDoc) {
		this.idPaisDoc = idPaisDoc;
	}

	public String getDocCi() {
		return docCi;
	}

	public void setDocCi(String docCi) {
		this.docCi = docCi;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getUsuarioAlta() {
		return usuarioAlta;
	}

	public void setUsuarioAlta(String usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}

	public List<Usuario> getLista() {
		return lista;
	}

	public void setLista(List<Usuario> lista) {
		this.lista = lista;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
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

	public String getContrasenha() {
		return contrasenha;
	}

	public void setContrasenha(String contrasenha) {
		this.contrasenha = contrasenha;
	}

	public String getUsuarioEmail() {
		return usuarioEmail;
	}

	public void setUsuarioEmail(String usuarioEmail) {
		this.usuarioEmail = usuarioEmail;
	}

	public String getUsuarioSend() {
		return usuarioSend;
	}

	public void setUsuarioSend(String usuarioSend) {
		this.usuarioSend = usuarioSend;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}
	
	

}
