package py.com.excelsis.sicca.session.form;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("usuarioCambioCodigoFC")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class UsuarioCambioCodigoFC{
	
	private static final String SCHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
	private static final char[] ACHARS = SCHARS.toCharArray();
	private static final int NCHARS = ACHARS.length;
	
//	private boolean email = false;
	
	@In
	StatusMessages statusMessages;
	
	@In(value = "entityManager")
	EntityManager em;

	@Transactional
	@Asynchronous
	public QuartzTriggerHandle procesoCambioCodigo(@Expiration Date when,
			@IntervalCron String interval) {
//		String date = new Date().toString();
//		System.out.println("\nEntrando "+date+"\nEntrando "+date+"\nEntrando "+date+"\nEntrando "+date+"\nEntrando "+date+"\nEntrando "+date);
		cambiar();
		return null;
	}
	@SuppressWarnings("rawtypes")
	public void cambiar(){
		String nuevoCodigo = "";
		String codigo = "";
		int num = 0;
		Persona p;
//		Usuario u;//para prueba...
		Query q = null;
		long id_persona = 0;
		
		String sql = "SELECT usuario.id_usuario, usuario.codigo_usuario, usuario.id_persona, usuario.codigo_usuario_numero "
				+ "FROM seguridad.usuario, general.persona, "
				+ "(select count(codigo_usuario) as cantidad, codigo_usuario from seguridad.usuario group by codigo_usuario) duplicados "
				+ "WHERE usuario.codigo_usuario = duplicados.codigo_usuario "
				+ "and persona.id_persona = usuario.id_persona "
				+ "and cantidad > 1 "
				+ "ORDER BY codigo_usuario ASC LIMIT 10";//10,84375 cada 3 min por 24 horas
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
//			para verificar por consola registro nuevo**********************************************************************************
//			System.out.println("\n*******************************************************************************************************************\n");
//			System.out.println("NUEVO REGISTRO\nCódigo VIEJO: "+String.valueOf(obj[1])+" ID_CODIGO_NUM: "+String.valueOf(obj[3])+"\n");
//			***************************************************************************************************************************
			nuevoCodigo = genCodUsuarioModif(num);
			insertarCodigoNuevo(codigo, nuevoCodigo);
			id_persona =  Long.parseLong(String.valueOf(obj[2]));//Integer.parseInt(String.valueOf(obj[2]));
			p = em.find(Persona.class, id_persona);
			
//			para verificar por consola registro nuevo**********************************************************************************
//			u = em.find(Usuario.class, Long.parseLong(obj[0].toString()));//para prueba...
//			System.out.println("NUEVO REGISTRO\nNombre completo: "+p.getNombreCompleto()+" Apellidos: "+p.getApellidos()+" C.I.: "+p.getDocumentoIdentidad()
//					+" Código NUEVO: "+nuevoCodigo+" ID_CODIGO_NUM: "+u.getCodigo_usuario_numero()+" E-mail: "+p.getEMail());
//			System.out.println("\n*******************************************************************************************************************\n");
//			***************************************************************************************************************************
			
			enviarEmails(p.getNombreCompleto(), p.getApellidos(), p.getDocumentoIdentidad(), nuevoCodigo, p.getEMail());
		} 
		
	}
	@SuppressWarnings("unchecked")
	public String genCodUsuarioModif(int numero) {
		try {
			int r;
			String cod = "";
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
	private void insertarCodigoNuevo(String codigoUsuario, String codigoNuevo) {		
		String q = "UPDATE seguridad.usuario SET codigo_usuario = '"+codigoNuevo+"' WHERE codigo_usuario_numero = '"+codigoUsuario+"'";//codigo_usuario fue reemplazado por codigo_usuario_numero
		Query query = em.createNativeQuery(q.toString());
		query.executeUpdate();
	}
	public void enviarEmails(String nombre, String apellido, String documentoIndentidad, String usuarioPortal, String email) {
		String error = "";

		String asunto = null;
		asunto = " Cambio de Código de Postulación - Portal Paraguay Concursa.";
		
		String mensaje  =  "<p> Estimado/a    <b>"
				+ nombre
				+ " "
				+ "</p> "
				+ "</b> <p> Le comunicamos que debido a mantenimientos evolutivos y mejoras continuas implementadas en el Portal Paraguay Concursa, se "
				+ "realizaron cambios en el sistema de generación de códigos de postulación de los usuarios. En adelante su Código de Postulación en el "
				+ "Portal Paraguay Concursa será:  </p> "
				+ "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>"
				+ usuarioPortal+"</b></p>"
				+ "<p>Atentamente.</p>"
				+ "<p>&nbsp;</p>"
				+ "<p><b>Administrador Portal Paraguay Concursa</b></p>";
		try {
			List<String> lista = enviarBCCMail();
			enviarMails(email, mensaje, asunto, lista);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			error = e.getMessage();
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
	/*SCRIPT
	
	UPDATE 
	seleccion.persona_postulante
SET
	e_mail = 'werner@dataworks.com.py';

UPDATE 
	seleccion.persona_postulante 
SET 
	e_mail = 'rveron@dataworks.com.py' 
where id_persona_postulante IN (select id_persona_postulante from seleccion.persona_postulante where e_mail = 'werner@dataworks.com.py' ORDER BY apellidos limit 500);

-------------------------------

UPDATE 
	general.persona
SET
	e_mail = 'werner@dataworks.com.py';

UPDATE 
	general.persona
SET 
	e_mail = 'rveron@dataworks.com.py' 
where id_persona IN (select id_persona from general.persona where e_mail = 'werner@dataworks.com.py' ORDER BY apellidos limit 11000);*/

}
