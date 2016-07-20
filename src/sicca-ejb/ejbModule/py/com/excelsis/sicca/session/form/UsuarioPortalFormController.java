package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JOptionPane;

import org.jboss.aspects.asynchronous.aspects.jboss.Asynchronous;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.TiposDatos;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.LicenseNumber;
import py.com.excelsis.sicca.util.PasswordGenerator;
import py.com.excelsis.sicca.util.Sha1;

import java.util.concurrent.locks.ReentrantLock;


@Scope(ScopeType.CONVERSATION)
@Name("usuarioPortalFormController")
public class UsuarioPortalFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4787131317081465080L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true, required = false)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true, required = false)
	SeguridadUtilFormController seguridadUtilFormController;

	private Long idPais;
	private Long idNacionalidad;
	private Usuario usuarioPortal = new Usuario();
	private Persona personaPortal = new Persona();
	private boolean nuevaPersona = false;
	private String pass;
	private boolean obs;
	private String nombre;
	private String apellido;
	private String email;
	private String confirmarEmail;
	private Date fechaNac;
	private String documentoIdentidad;
	private PersonaDTO personaDTO;
	private int maximo = 50000000;
	private static final String SCHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZ"; // 36 símbolos
	// static final String SCHARS = "234679ACDEFHKLMNPRTUVWXY"; // 24 símbolos

	private static final char[] ACHARS = SCHARS.toCharArray();
	private static final int NCHARS = ACHARS.length;

	
	//Agregado, cambio de código usuario; Werner.
		//*********************************************************************
		@Transactional
		@Asynchronous
		public QuartzTriggerHandle procesoCambioUsuario(@Expiration Date when,
				@IntervalCron String interval) {
			cambiar();
			return null;
		}
	//*********************************************************************
	
	public void init() {
		try {
			idPais = idParaguay();
			personaPortal = new Persona();
			idNacionalidad = General.getIdNacionalidadParaguay();
			usuarioPortal = new Usuario();
			nuevaPersona = false;
			pass = "";
			email = null;
			confirmarEmail = null;
			nombre = null;
			apellido = null;
			fechaNac = null;
			documentoIdentidad = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<String> enviarBCCMail() {
		List<Referencias> lRefs;
		List<String> lista = new ArrayList<String>();
		String sql = " SELECT referencias FROM Referencias referencias  "
				+ " WHERE referencias.dominio = 'EMAIL_COPIA_SFP' AND referencias.descAbrev = 'REG_PORTAL' "
				+ " AND referencias.activo = TRUE";
		Query q = em.createQuery(sql);
		lRefs = q.getResultList();
		for (Referencias o : lRefs) {
			lista.add(o.getDescLarga());
		}
		return lista;
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

			// //Crea una nueva persona en caso que no exista.
			if (personaDTO.getPersona().getIdPersona() == null) {
				personaPortal = personaDTO.getPersona();
				/* Si no es Paraguayo Setear los siguientes datos */
				if(idPais != 100)
				{
					personaPortal.setDocumentoIdentidad(documentoIdentidad);
					personaPortal.setNombres(nombre);
					personaPortal.setApellidos(apellido);
					personaPortal.setFechaNacimiento(fechaNac);
					DatosEspecificos datoEspecifico = new DatosEspecificos();
					datoEspecifico.setIdDatosEspecificos(idNacionalidad);
					personaPortal.setDatosEspecificos(datoEspecifico);;
				}
				personaPortal.setActivo(true);
				personaPortal.setUsuAlta("PORTAL");
				personaPortal.setFechaAlta(new Date());
				personaPortal.setPaisByIdPaisExpedicionDoc(em.find(Pais.class,
						idPais));
				personaPortal.setEMail(email.toLowerCase().trim());
				personaPortal.setTelContacto("MOVIL");
//				personaPortal.setDocumentoIdentidad(Integer.toString(Integer
//						.parseInt(personaPortal.getDocumentoIdentidad())));
				
				//stortora, para que el predeterminado sea que SI tiene parentesco
				personaPortal.setTienePariente(true);
				
				//RVeron, para que no guarde con el '0' a la izquierda en caso de que tenga menos de 6 digitos
				//personaPortal.setDocumentoIdentidad(personaPortal.getDocumentoIdentidad());
				if ( personaPortal.getDocumentoIdentidad().substring(0,1).equals("0")){
					personaPortal.setDocumentoIdentidad(personaPortal.getDocumentoIdentidad().substring(1) );
				}
				else{
					personaPortal.setDocumentoIdentidad(personaPortal.getDocumentoIdentidad());
				}
				
				em.persist(personaPortal);

			} else {// actualizamos datos en caso que hayan
				personaPortal = em.find(Persona.class, personaDTO.getPersona()
						.getIdPersona());
				personaPortal.setEMail(email.toLowerCase().trim());
				em.merge(personaPortal);

			}
			usuarioPortal.setPersona(personaPortal);
			usuarioPortal.setActivo(true);
			usuarioPortal.setFechaAlta(new Date());
			usuarioPortal.setContrasenha(sha.getHash(pass));
			usuarioPortal.setUsuAlta("PORTAL");

			//RVeron, 20140313
			//Para evitar que se generen codigos repetidos
			ReentrantLock lock = new ReentrantLock();
			//Bloqueamos con lock()
			lock.lock();
			
			//JMSamudio 02/04/2014
			int numero_codigo = obtenerUltimoNumero();
			usuarioPortal.setCodigo_usuario_numero(numero_codigo);
			
			
			usuarioPortal.setCodigoUsuario(genCodUsuarioModif(numero_codigo));
			em.persist(usuarioPortal);
			em.flush();
			//RVeron, 20140313
			//liberamos el bloqueo
			lock.unlock();
			

			// Asigna al usuario el rol Portal
			UsuarioRol usuRol = new UsuarioRol();
			usuRol.setUsuario(usuarioPortal);
			usuRol.setRol(rolPortal());
			em.persist(usuRol);
			em.flush();
			enviarEmails();
			statusMessages
					.add("Usuario registrado, en breve recibirá un mail con su contrase\u00F1a");
			result = "persisted";

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Se ha producido un error, consulte con el administrador");
		} finally {
			if (result != null) {
				statusMessages.clear();
				statusMessages
						.add(Severity.INFO,
								SeamResourceBundle.getBundle().getString(
										"msg_operacion_exitosa")
										+ ". Usuario registrado, en breve recibir&aacute; un mail con su contrase\u00F1a");

			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getNacionalidadSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));

		List<Object[]> n = em
				.createNativeQuery(
						"Select  de.id_datos_especificos, de.descripcion From seleccion.referencias  r,"
								+ "seleccion.datos_especificos de Where r.dominio like 'NACIONALIDADES' And  r.valor_num = de.id_datos_generales "
								+ " AND de.activo "
								+ " Order by de.descripcion ").getResultList();
		Iterator<Object[]> it = n.iterator();

		while (it.hasNext()) {
			Object[] fila = it.next();
			if (fila[0] != null)
				si.add(new SelectItem(fila[0], fila[1] != null ? fila[1]
						.toString() : ""));
		}

		return si;
	}

	/***
	 * PARA EL ENVIO DE MAIL
	 ***/
	@SuppressWarnings("unchecked")
	public void enviarEmails() {
		String error = "";

		String email = personaPortal.getEMail();
		String asunto = null;
		asunto = " Bienvenido al Portal Paraguay Concursa ";
		String paisExpedicion = "-";
		if (personaPortal.getPaisByIdPaisExpedicionDoc() != null) {
			paisExpedicion = personaPortal.getPaisByIdPaisExpedicionDoc()
					.getDescripcion();
		}
		String mensaje = "<p> <b> Estimado/a    "
				+ personaPortal.getNombres()
				+ " "
				+ personaPortal.getApellidos()
				+ "</p> "
				+ "</b> <p> Le recordamos que sus claves de acceso al Portal Paraguay Concursa son : </p> "
				+ "<p align=\"justify\">&nbsp; </p>"
				+ " <p><b>Documento de Identidad:  "
				+ personaPortal.getDocumentoIdentidad()
				+ "</b> </p> "
				+ " <p><b>Pa&iacute;s de Expedici&oacute;n del Doc. de Identidad:  "
				+ paisExpedicion
				+ "</b> </p> "
				+ " <p><b>Contrase&ntilde;a: "
				+ pass
				+ "</b> </p> "
				+ "<p align=\"justify\">&nbsp; </p>"
				+ " <p><b> Adem&aacute;s le informamos que el C&oacute;digo de Postulaci&oacute;n que utilizar&aacute; el Portal Paraguay Concursa para identificarlo ser&aacute;:</b>  </p> "
				+ "<p><b> C&oacute;digo de Postulaci&oacute;n: "
				+ usuarioPortal.getCodigoUsuario()
				+ " </b> </p>"
				+ "<p> <b>&#161;Importante&#33;: </b> "
				+ " Le recordamos que al ingresar a su cuenta de usuario debe completar su Curriculum Vitae, en el apartado &#8220;Mis Datos&#8221;."
				+ " El mismo deber&aacute; estar actualizado al momento de postularse a alg&uacute;n Concurso  </p>"
				+ " <p><b> A trav&eacute;s de su cuenta del Portal Paraguay Concursa, podr&aacute; realizar permanentemente las siguientes acciones: </b>  </p>"
				+ " <p>	 -	Actualizar constantemente su Curriculum Vitae  </p>"
				+ " <p>	 -	Modificar su Clave de acceso al sistema </p>"
				+ " <p>	 -	Podr&aacute; postular a los Concursos disponibles en el apartado &#8220;Postule aqu&iacute;&#8221; </p>"
				+ " <p>    -	Visualizar las Postulaciones a la cual se encuentra registrada  </p>"
				+ " <p>	 -	Realizar Reclamos y Sugerencias   </p>"
				+ " <p>	 -	Visualizar las notificaciones a las cuales fue subscripta  </p>"
				+ "<p>Para ello, deber&aacute; ingresar al Portal a trav&eacute;s de este enlace: <a href="
				+ "http://www.paraguayconcursa.gov.py/"
				+ "> Portal Paraguay Concursa</a>, "
				+ "con su claves y de esta forma podr&aacute; ingresar al apartado de su inter&eacute;s  </p>"
				+ "<p>Gracias por haberse registrado al Portal! &Eacute;xitos  <br />  </p>"
				+ " <p> </p>" + " <p> </p>" + " <p> </p>"
				+ "<p align=\"justify\">&nbsp; </p>"
				+ "<strong>Atentamente</strong>"
				+ "<p><strong>Portal Paraguay Concursa</strong></p>";

		try {
			List<String> lista = enviarBCCMail();
			enviarMails(personaPortal.getEMail(), mensaje, asunto, lista);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			error = e.getMessage();
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public Boolean enviarMails(String destinatario, String contenido,
			String asunto, List<String> lBCC) {

		Properties props = new Properties();

		List<Referencias> ref = em
				.createQuery(
						" select r from Referencias r"
								+ " where r.dominio like 'CONFIG_EMAIL' and r.activo= true")
				.getResultList();
		if (ref.isEmpty()) {
			return false;
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

		/*
		 * Ejemplo props.setProperty("mail.transport.protocol", "smtp");
		 * props.setProperty("mail.host", "mail.copaco.com.py");
		 * props.setProperty("mail.user", "menfran@click.com.py");
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
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					destinatario));
			if (lBCC != null) {
				for (String o : lBCC) {
					message.addRecipient(Message.RecipientType.BCC,
							new InternetAddress(o.trim(), usuarioSend));
				}
			}
			message.setFrom(new InternetAddress(user, usuarioSend));
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
	public void findPersona() {

		if (personaPortal.getDocumentoIdentidad() != null
				&& !personaPortal.getDocumentoIdentidad().trim().equals("")) {
			if (idPais == null) {
				statusMessages.add("Debe seleccionar el Pais");
				return;
			}

			List<Persona> personas = em.createQuery(
					"Select p from Persona p"
							+ " where p.documentoIdentidad = '"
							+ personaPortal.getDocumentoIdentidad()
							+ "' and p.paisByIdPaisExpedicionDoc.idPais="
							+ idPais).getResultList();
			if (!personas.isEmpty()) {
				nombre = personas.get(0).getNombres();
				apellido = personas.get(0).getApellidos();
			} else {
				nombre = null;
				apellido = null;
			}

		}
	}

	// METODOS PRIVADOS
	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p = em.createQuery(
				" Select p from Pais p"
						+ " where lower(p.descripcion) like 'paraguay'")
				.getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	@SuppressWarnings("unchecked")
	private String genCodUsuario() {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" select  max(SUBSTRING(x.codigo_usuario, 1, 3)) AS codi ,  SUBSTRING(x.codigo_usuario, 4, 6) AS numero ");
			sql.append(" FROM seguridad.usuario  AS x WHERE x.usu_alta LIKE 'PORTAL' AND  CAST (SUBSTRING(x.codigo_usuario, 4, 6) AS int) = ");
			sql.append(" (SELECT  MAX(CAST (SUBSTRING(p.codigo_usuario, 4, 6) AS INT)) AS num FROM seguridad.usuario p ");
			sql.append(" WHERE p.usu_alta LIKE 'PORTAL' AND  SUBSTRING(p.codigo_usuario, 1, 3) LIKE SUBSTRING(x.codigo_usuario, 1, 3)) ");
			sql.append("GROUP BY numero ");
			sql.append("ORDER BY codi desc ");

			String codLetra = "", codNum = "", ultCod = null, codResult = "";

			List<Object> rsl = em.createNativeQuery(sql.toString())
					.getResultList();
			for (Object obj : (List<Object>) rsl) {
				Object[] record = (Object[]) obj;
				codLetra = (String) record[0];
				codNum = (String) record[1];
				break;
			}
			if (!codLetra.equals(""))
				ultCod = codLetra + codNum;
			if (ultCod != null) {
				LicenseNumber lic = new LicenseNumber(ultCod);
				lic.increment(1);
				System.out.println(lic);
				codResult = lic.toString();
			} else {
				codResult = "AAA001";
			}
			if (codResult.substring(3, 6).equals("000"))
				codResult = codResult.substring(0, 3) + "001";

			return codResult;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * al poner la BD en produccion se comenzaron a generar codigos desde la
	 * combinacion BAE por eso la modificacino para poder realizar con las otras
	 * combinaciones
	 */
	@SuppressWarnings("unchecked")
	public String genCodUsuarioModif(int numero) {
		try {
			int r;
			int num = 0;
			String aux_num = "";
			String cod = "";
			
			
			
//			List<Usuario> u = em.createNativeQuery(
//					"select * from seguridad.usuario WHERE codigo_usuario_numero = (select MAX(codigo_usuario_numero) from seguridad.usuario)")
//					.getResultList();
//
//			num = u.get(0).getCodigo_usuario_numero();
	
//			num = Integer.parseInt(aux_num);
			while ( numero > NCHARS - 1 ) {
				r = numero % NCHARS;
				cod = ACHARS[r] + cod;
				numero = numero / NCHARS;
			}
			cod = ACHARS[numero] + cod;
			statusMessages.add("codigo",cod);
			return cod;
			
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private boolean checkData() {
		if (nombre == null || nombre.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar el nombre");
			return false;
		}
		if (idPais == null) {
			statusMessages.add("Debe seleccionar el Pais");
			return false;
		}
		if (apellido == null || apellido.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar el apellido");
			return false;
		}
		if (email == null || email.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar el email");
			return false;
		}

		if (documentoIdentidad == null || documentoIdentidad.isEmpty()) {
			statusMessages.add(Severity.ERROR,
					"Debe ingresar el documento de identidad");
			return false;
		}
		try {
			if (!validarPersona())
				return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (fechaNac.after(new Date())) {
			statusMessages
					.add(Severity.ERROR,
							"La fecha de nacimiento no puede ser mayor a la fecha actual. Verifique");
			return false;
		}

		if (!verificarEdadEnRango()) {
			return false;
		}
		if (!General.isEmail(email.toLowerCase().trim())) {
			statusMessages.add(Severity.ERROR,
					"Correo electrónico incorrecto. Verifique");
			return false;
		}
		if (!email.trim().equalsIgnoreCase(confirmarEmail.trim())) {
			statusMessages.add(Severity.ERROR,
					"Los correos electrónicos ingresados no coinciden. Verifique");
			return false;
		}
		return true;
	}

	private Boolean verificarEdadEnRango() {
		Query q = em
				.createQuery("select  Referencias from Referencias Referencias where Referencias.dominio = 'ANHOS_MINIMO_USUARIO_PORTAL'");
		List<Referencias> lista = q.getResultList();
		if (lista.size() == 1) {
			int edad = calcularEdad(fechaNac);
			if (edad < lista.get(0).getValorNum()) {
				statusMessages
						.add(Severity.ERROR,
								"No se encuentra dentro del rango de edad permitido para inscribirse. Edad mínima: "
										+ lista.get(0).getValorNum() + " años.");
				return false;
			}
		} else {
			statusMessages
					.add(Severity.ERROR,
							"No se encuentra cargado el dominio: 'ANHOS_MINIMO_USUARIO_PORTAL'. Consulte al Administrador.");
			return false;
		}
		return true;
	}

	private boolean validarPersona() throws Exception {
		/* Validaciones */
		if (idPais == null
				|| !seguridadUtilFormController.validarInput(idPais.toString(),
						TiposDatos.LONG.getValor(), null)) {
			return false;
		}
		
		//**********************************************************************
		//Agregado "0" para cédulas con letras A B C y menores al millón, esto permite comparar con los registros de Identificaciones; Werner.
		documentoIdentidad= documentoIdentidad.toUpperCase();
		
		if ( documentoIdentidad.length() == 7 && (documentoIdentidad.endsWith("A") || documentoIdentidad.endsWith("B") || documentoIdentidad.endsWith("C")) ){
			documentoIdentidad = "0"+documentoIdentidad;
		}
		//**********************************************************************
		
		if (documentoIdentidad == null
				|| !seguridadUtilFormController.validarInput(
						documentoIdentidad.toString(),
						TiposDatos.STRING.getValor(), null)) {
			return false;
		}
		Pais p = em.find(Pais.class, idPais);
		personaDTO = seleccionUtilFormController.buscarPersona(
				documentoIdentidad, p.getDescripcion());

		/**
		 * SI el CU535 retorna: habilitar_btn=nulo
		 * */
		if (idPais.longValue() == General.getIdParaguay().longValue()) {
			if (personaDTO.getHabilitarBtn() == null) {
				statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
				return false;
			} else if (!personaDTO.getHabilitarBtn()
					&& personaDTO.getEstado().equals("EXISTE")) {
				/**
				 * BUSCAR con el id_persona que retorna el CU535 en la tabla
				 * usuario, tal que usu_alta=’PORTAL’
				 * */

				if (existeUsuarioPortal(personaDTO.getPersona().getIdPersona())) {
					statusMessages.add(Severity.ERROR,
							"Ya tiene usuario del Portal");
					return false;
				} else {
					if (!General.sonFechasIguales(fechaNac, personaDTO
							.getPersona().getFechaNacimiento())) {
						statusMessages
								.add(Severity.ERROR,
										"Sus datos no coinciden con los datos de Persona SICCA. Ponerse en contacto con la SFP");
						return false;
					}
				}
			} else if (personaDTO.getHabilitarBtn()
					&& personaDTO.getEstado().equals("NUEVO")) {
				if (!removeCadenasEspeciales(nombre)
						.trim()
						.toLowerCase()
						.equals(removeCadenasEspeciales(
								personaDTO.getPersona().getNombres())
								.toLowerCase())
						|| !removeCadenasEspeciales(apellido)
								.trim()
								.toLowerCase()
								.equals(removeCadenasEspeciales(
										personaDTO.getPersona().getApellidos())
										.toLowerCase())
						|| !General.sonFechasIguales(fechaNac, personaDTO
								.getPersona().getFechaNacimiento())) {
					statusMessages
							.add(Severity.ERROR,
									" Sus datos no coinciden con los de Identificaciones. Ponerse en contacto con la SFP");
					return false;
				}
			}
		} else {
			if (!personaDTO.getHabilitarBtn()
					&& personaDTO.getEstado().equals("EXISTE")) {
				if (existeUsuarioPortal(personaDTO.getPersona().getIdPersona())) {
					statusMessages.add(Severity.ERROR,
							"Ya tiene usuario del Portal");
					return false;
				} else {
					if (!General.sonFechasIguales(fechaNac, personaDTO
							.getPersona().getFechaNacimiento())) {
						statusMessages
								.add(Severity.ERROR,
										"Sus datos no coinciden con los datos de Persona SICCA. Ponerse en contacto con la SFP");
						return false;
					}
				}
			}
		}

		return true;

	}

	@SuppressWarnings("unchecked")
	private boolean existeUsuarioPortal(Long idPersona) {
		List<Usuario> usuarios = em
				.createQuery(
						"Select u from Usuario u "
								+ " where u.persona.idPersona=:idPersona and u.usuAlta='PORTAL'")
				.setParameter("idPersona", idPersona).getResultList();
		return !usuarios.isEmpty();
	}

	/**
	 * Método para calcular la edad de una persona
	 * 
	 * @param fecha
	 * @return
	 */

	public static int calcularEdad(Date fecha) {

		String datetext = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Calendar birth = new GregorianCalendar();
			Calendar today = new GregorianCalendar();
			int age = 0;
			int factor = 0;
			datetext = sdf.format(fecha);
			Date birthDate = sdf.parse(datetext);
			Date currentDate = new Date();
			birth.setTime(birthDate);
			today.setTime(currentDate);
			if (birth.get(Calendar.MONTH) >= today.get(Calendar.MONTH)) {
				if (birth.get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
					if (birth.get(Calendar.DATE) > today.get(Calendar.DATE)) {
						factor = -1; // Aun no celebra su cumpleanhos
					}
				} else {
					factor = -1; // Aun no celebra su cumpleanhos
				}
			}
			age = (today.get(Calendar.YEAR) - birth.get(Calendar.YEAR))
					+ factor;
			return age;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@SuppressWarnings("unchecked")
	private Rol rolPortal() {
		List<Rol> rol = em.createQuery(
				"select r from Rol r"
						+ " where lower(r.descripcion) like 'portal'")
				.getResultList();
		if (!rol.isEmpty())
			return rol.get(0);

		return null;
	}
	
	//JMSamudio 02/04/2014
	public int obtenerUltimoNumero(){
		int num= 0;
		String aux_num = "";
		
		StringBuffer sql = new StringBuffer();
		sql.append("select MAX(codigo_usuario_numero) from seguridad.usuario");
		
		@SuppressWarnings("unchecked")
		List<Object> rsl = em.createNativeQuery(sql.toString()).getResultList();
		if (!rsl.isEmpty()){
			num = (Integer) rsl.get(0);
			return num+1;
		}
		return 0;
		
	}

	// GETTERS Y SETTERS
	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdNacionalidad() {
		return idNacionalidad;
	}

	public void setIdNacionalidad(Long idNacionalidad) {
		this.idNacionalidad = idNacionalidad;
	}

	public Usuario getUsuarioPortal() {
		return usuarioPortal;
	}

	public void setUsuarioPortal(Usuario usuarioPortal) {
		this.usuarioPortal = usuarioPortal;
	}

	public Persona getPersonaPortal() {
		return personaPortal;
	}

	public void setPersonaPortal(Persona personaPortal) {
		this.personaPortal = personaPortal;
	}

	public boolean isNuevaPersona() {
		return nuevaPersona;
	}

	public void setNuevaPersona(boolean nuevaPersona) {
		this.nuevaPersona = nuevaPersona;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConfirmarEmail() {
		return confirmarEmail;
	}

	public void setConfirmarEmail(String confirmarEmail) {
		this.confirmarEmail = confirmarEmail;
	}

	public Date getFechaNac() {
		return fechaNac;
	}

	public void setFechaNac(Date fechaNac) {
		this.fechaNac = fechaNac;
	}

	public String getDocumentoIdentidad() {
		return documentoIdentidad;
	}

	public void setDocumentoIdentidad(String documentoIdentidad) {
		this.documentoIdentidad = documentoIdentidad;
	}

	public String removeCadenasEspeciales(String input) {
		String original = "áàäéèëíìïóòöúùüñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
		String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
		String output = input;
		for (int i = 0; i < original.length(); i++) {
			output = output.replace(original.charAt(i), ascii.charAt(i));
		}
		return output;
	}
	//###########################################################################
		//###########################################################################
		
		@SuppressWarnings("rawtypes")
		public void cambiar(){
			String nuevoCodigo = "";
			String codigo = "";
			int num = 0;
			Persona p;
			Query q = null;
			long id_persona = 0;
			
			//Momentaneamente en comentario... acá
//			String sql = "SELECT id_usuario, codigo_usuario, id_persona, codigo_usuario_numero "
//					+ "FROM seguridad.usuario WHERE char_length(codigo_usuario) > 5 "
//					+ "AND usu_alta <> 'PORTAL' ORDER BY codigo_usuario_numero ASC LIMIT 3";
			
			String sql = "SELECT usuario.id_usuario, usuario.codigo_usuario, usuario.id_persona, usuario.codigo_usuario_numero "
					+ "FROM seguridad.usuario, general.persona, "
					+ "(select count(codigo_usuario) as cantidad, codigo_usuario from seguridad.usuario group by codigo_usuario) duplicados "
					+ "WHERE usuario.codigo_usuario = duplicados.codigo_usuario "
					+ "and persona.id_persona = usuario.id_persona "
					+ "and cantidad > 1 "
					+ "ORDER BY codigo_usuario ASC LIMIT 10";
			//En caso de tener repetidos (impar) con un límite de 10 registros cada 5 minutos; 
			//ejemplo: [(AAA001,AAA001); (AAA002,AAA002); (AAA003,AAA003); (AAA004,AAA004); (AAA005,AAA005)] AAA005
			//el último código usuario quedaría fuera AAA005, y en la próxima consulta no se tomaría ya que no estaría repetido. Werner.
			q = em.createNativeQuery(sql);
			
			@SuppressWarnings("unchecked")
			List<Usuario> lUsuarios = (List<Usuario>) q.getResultList();
			Iterator itr = lUsuarios.iterator();
			
			while (itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				num = Integer.parseInt(String.valueOf(obj[3]));
				codigo =  String.valueOf(obj[3]);// se cambió a la columna 4
				nuevoCodigo = genCodUsuarioModif(num);
				insertarCodigoNuevo(codigo, nuevoCodigo);
				id_persona =  Long.parseLong(String.valueOf(obj[2]));//Integer.parseInt(String.valueOf(obj[2]));
				p = em.find(Persona.class, id_persona);
				enviarEmails(p.getNombreCompleto(), p.getApellidos(), p.getDocumentoIdentidad(), nuevoCodigo, p.getEMail());
//				enviarEmails(p.getNombreCompleto(), p.getApellidos(), p.getDocumentoIdentidad(), nuevoCodigo, "werner@dataworks.com.py");
			} 

		}
		
		private void insertarCodigoNuevo(String codigoUsuario, String codigoNuevo) {		
			String q = "UPDATE seguridad.usuario SET codigo_usuario = '"+codigoNuevo+"' WHERE codigo_usuario_numero = '"+codigoUsuario+"'";//codigo_usuario fue reemplazado por codigo_usuario_numero
			Query query = em.createNativeQuery(q.toString());
			
			query.executeUpdate();
			
		}
		//JMSamudio
		public void enviarEmails(String nombre, String apellido, String documentoIndentidad, String usuarioPortal, String email) {
			String error = "";

			String asunto = null;
			asunto = " Cambio de Código de Postulación - Portal Paraguay Concursa.";
			//asunto = " Bienvenido al Portal Paraguay Concursa ";
			String paisExpedicion = "-";
			
			String mensaje  =  "<p> Estimado/a    <b>"
					+ nombre
					+ " "
//					+ apellido
					+ "</p> "
					+ "</b> <p> Le comunicamos que debido a mantenimientos evolutivos y mejoras continuas implementadas en el Portal Paraguay Concursa, se "
					+ "realizaron cambios en el sistema de generación de códigos de postulación de los usuarios. En adelante su Código de Postulación en el "
					+ "Portal Paraguay Concursa será:  </p> "
					+ "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>"
					+ usuarioPortal+"</b></p>"
//					+ " <p> </p>" /*+ " <p> </p>" + " <p> </p>"*/
//					+ "<p align=\"justify\">&nbsp; </p>"
					+ "<p>Atentamente.</p>"
					+ "<p>&nbsp;</p>"
					+ "<p><b>Administrador Portal Paraguay Concursa</b></p>";
//					+ "<p><b>Portal Paraguay Concursa</b></p>";



			/*String mensaje = "<p> <b> Estimado/a    "
					+ nombre
					+ " "
					+ apellido
					+ "</p> "
					+ "</b> <p> Le recordamos que sus claves de acceso al Portal Paraguay Concursa son : </p> "
					+ "<p align=\"justify\">&nbsp; </p>"
					+ " <p><b>Documento de Identidad:  "
					+ documentoIndentidad
					+ "</b> </p> "
					+ " <p><b>Pa&iacute;s de Expedici&oacute;n del Doc. de Identidad:  "
					+ paisExpedicion
					+ "</b> </p> "
					+ " <p><b>Contrase&ntilde;a: "
//					+ pass
					+ "</b> </p> "
					+ "<p align=\"justify\">&nbsp; </p>"
					+ " <p><b> Adem&aacute;s le informamos que el C&oacute;digo de Postulaci&oacute;n que utilizar&aacute; el Portal Paraguay Concursa para identificarlo ser&aacute;:</b>  </p> "
					+ "<p><b> C&oacute;digo de Postulaci&oacute;n: "
					+ usuarioPortal
					+ " </b> </p>"
					+ "<p> <b>&#161;Importante&#33;: </b> "
					+ " Le recordamos que al ingresar a su cuenta de usuario debe completar su Curriculum Vitae, en el apartado &#8220;Mis Datos&#8221;."
					+ " El mismo deber&aacute; estar actualizado al momento de postularse a alg&uacute;n Concurso  </p>"
					+ " <p><b> A trav&eacute;s de su cuenta del Portal Paraguay Concursa, podr&aacute; realizar permanentemente las siguientes acciones: </b>  </p>"
					+ " <p>	 -	Actualizar constantemente su Curriculum Vitae  </p>"
					+ " <p>	 -	Modificar su Clave de acceso al sistema </p>"
					+ " <p>	 -	Podr&aacute; postular a los Concursos disponibles en el apartado &#8220;Postule aqu&iacute;&#8221; </p>"
					+ " <p>    -	Visualizar las Postulaciones a la cual se encuentra registrada  </p>"
					+ " <p>	 -	Realizar Reclamos y Sugerencias   </p>"
					+ " <p>	 -	Visualizar las notificaciones a las cuales fue subscripta  </p>"
					+ "<p>Para ello, deber&aacute; ingresar al Portal a trav&eacute;s de este enlace: <a href="
					+ "http://www.paraguayconcursa.gov.py/"
					+ "> Portal Paraguay Concursa</a>, "
					+ "con su claves y de esta forma podr&aacute; ingresar al apartado de su inter&eacute;s  </p>"
					+ "<p>Gracias por haberse registrado al Portal! &Eacute;xitos  <br />  </p>"
					+ " <p> </p>" + " <p> </p>" + " <p> </p>"
					+ "<p align=\"justify\">&nbsp; </p>"
					+ "<strong>Atentamente</strong>"
					+ "<p><strong>Portal Paraguay Concursa</strong></p>";*/

			try {
				List<String> lista = enviarBCCMail();
				enviarMails(email, mensaje, asunto, lista);

			} catch (Exception e) {
				System.out.println(e.getMessage());
				error = e.getMessage();
				e.printStackTrace();
			}

		}
}
