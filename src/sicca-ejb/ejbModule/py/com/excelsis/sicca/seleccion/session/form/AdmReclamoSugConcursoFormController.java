package py.com.excelsis.sicca.seleccion.session.form;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.ReclamoSugerencia;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.SolicProrrogaDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ReclamoSugerenciaHome;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admReclamoSugConcursoFormController")
public class AdmReclamoSugConcursoFormController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	
	@In(create =  true)
	ReclamoSugerenciaHome reclamoSugerenciaHome;
	
	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	

	private ReclamoSugerencia reclamoSugerencia;
	private Long idConcursoPuestoAgr;
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Long idReclamoSugerencia;
	private String nombrePantalla="perfilMatrizHomologacion_edit";
	private String direccionFisica;
	private String entity;
	private boolean fromRpta;
	private String nomConcurso;
	private Long idGrupo;
	private Persona persona;

	public void init(){
		try {
			reclamoSugerencia= new ReclamoSugerencia();
			Long id = usuarioLogueado.getPersona().getIdPersona();
			persona = em.find(Persona.class, id);
			if(idReclamoSugerencia!=null){
				reclamoSugerencia= em.find(ReclamoSugerencia.class,idReclamoSugerencia);
				reclamoSugerenciaHome.setInstance(reclamoSugerencia);
				if(reclamoSugerencia.getPostulacion()!=null && reclamoSugerencia.getPostulacion().getConcursoPuestoAgr()!=null){
					concursoPuestoAgr= em.find(ConcursoPuestoAgr.class, reclamoSugerencia.getPostulacion().getConcursoPuestoAgr().getIdConcursoPuestoAgr());
					findEntidades();
					idGrupo=concursoPuestoAgr.getIdConcursoPuestoAgr();
					if(concursoPuestoAgr.getConcurso()!=null)
						nomConcurso=concursoPuestoAgr.getConcurso().getNombre();
				}
			}
			if(fromRpta)
				reclamoSugerencia.setFechaRptaSfp(new Date());
			
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void findEntidades(){
		configuracionUoCab=em.find(ConfiguracionUoCab.class, concursoPuestoAgr.getConcurso().getConfiguracionUoCab().getIdConfiguracionUo()) ;
		if(configuracionUoCab.getEntidad()!=null){
			sinEntidad=em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin=em.createQuery("Select n from SinNivelEntidad n " +
					" where n.aniAniopre ="+sinEntidad.getAniAniopre() +" and n.nenCodigo="+sinEntidad.getNenCodigo()).getResultList();
			if(!sin.isEmpty())
				sinNivelEntidad=sin.get(0);
			
		}
		
	}
	public void anexar(){
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio=sdfSoloAnio.format(new Date());
		entity="HomologacionPerfilMatriz";
		direccionFisica="";
		
		
	}
	
	
	public String enviar(){
		try {
			if(reclamoSugerencia.getAsuntoSfp().trim().equals("") || reclamoSugerencia.getDescripcionSfp().trim().equals(""))
			{
				statusMessages.add("Debe ingresar los campos obligatorios");
				return null;
			}
			reclamoSugerencia.setEnviarPostulante(reclamoSugerencia.getEnviarCorreoPostulante());
			if(reclamoSugerencia.getEnviarCorreoPostulante())
				enviarEmails();
			em.merge(reclamoSugerencia);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public void enviarEmails() {
		String dirEmail = persona.getEMail();
		ConcursoPuestoAgr agr = new ConcursoPuestoAgr();
		agr = em.find(ConcursoPuestoAgr.class, idGrupo);
		String asunto = "NOTIF_RECLAMO_SUG_" +nomConcurso.toUpperCase()+ "_"
				+agr!=null?agr.getDescripcionGrupo():"";
		String texto = "";
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		try {
			texto = texto
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ "<p align=\"justify\">Usted ha enviado el siguiente Reclamo/Sugerencia en fecha:"+reclamoSugerencia.getFechaReclamoSugerencia()+ "</p>"
					+ "<p>correspondiente al <b>Concurso :</b>"+nomConcurso
					+ " </p> "
					+ " <p></p>"
					+ "<p>Nro:"
					+ reclamoSugerencia.getNroReclamo()
					+ " </p> "
					+ "<p>Asunto:"
					+ reclamoSugerencia.getAsunto()
					+ " </p>"
					+ "<p>Descripci&oacute;n:"
					+ reclamoSugerencia.getDescripcion()
					+ " </p> "
					+ "<p></p>"
					+ "<p align=\"justify\">Informamos que hemos atendido su reclamo y respondemos lo siguiente: "
					+ "<p>Fecha:"
					+ formatoDeFecha.format(reclamoSugerencia.getFechaRptaSfp())
					+ " </p> "
					+ "<p>Asunto:"
					+ reclamoSugerencia.getAsuntoSfp()
					+ " </p>"
					+ "<p>Descripci&oacute;n:"
					+ reclamoSugerencia.getDescripcionSfp()
					+ " </p> "
					+ "<p></p>"
					+ "<p> Atentos saludos</p> "
					+ "<b>Secretaria de la Funci&oacute;n Pública</p></b>";

			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,null);
			System.out.println("EL MAIL HA SIDO ENVIADO....");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	


//	GETTERS Y SETTERS
	
	

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}


	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public Long getIdReclamoSugerencia() {
		return idReclamoSugerencia;
	}

	public void setIdReclamoSugerencia(Long idReclamoSugerencia) {
		this.idReclamoSugerencia = idReclamoSugerencia;
	}

	public ReclamoSugerencia getReclamoSugerencia() {
		return reclamoSugerencia;
	}

	public void setReclamoSugerencia(ReclamoSugerencia reclamoSugerencia) {
		this.reclamoSugerencia = reclamoSugerencia;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public boolean isFromRpta() {
		return fromRpta;
	}

	public void setFromRpta(boolean fromRpta) {
		this.fromRpta = fromRpta;
	}
	
	

	
	
	
}
