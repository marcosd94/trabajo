package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.ReclamoSugerencia;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ReclamoSugerenciaHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("reclamoSugerenciaComiteFormController")
public class ReclamoSugerenciaComiteFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	ReclamoSugerenciaHome reclamoSugerenciaHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;

	private ConfiguracionUoCab configuracionUoCab;
	private SinNivelEntidad sinNivelEntidad = new SinNivelEntidad();
	private Persona persona;
	private ReclamoSugerencia reclamoSugerencia;

	private String asuntoRpta;
	private String textoRpta;
	private String direccionFisica;
	private Boolean enviarEmail;

	public void init() {

		reclamoSugerencia = new ReclamoSugerencia();
		reclamoSugerencia = reclamoSugerenciaHome.getInstance();
		obtenerDatosUsuarioLogueado();
		obtenerDireccionFisica();
		if(reclamoSugerencia.getEstado().equals("R")){
			asuntoRpta = reclamoSugerencia.getAsuntoRespuesta();
			textoRpta = reclamoSugerencia.getDescripcionRespuesta();
			enviarEmail = reclamoSugerencia.getEnviarCorreoPostulante();
		}
	}

	private void obtenerDatosUsuarioLogueado() {
		configuracionUoCab = em.find(ConfiguracionUoCab.class, usuarioLogueado
				.getConfiguracionUoCab().getIdConfiguracionUo());
		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
				configuracionUoCab.getEntidad().getSinEntidad().getNenCodigo());
		sinNivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		persona = new Persona();
		Long id = usuarioLogueado.getPersona().getIdPersona();
		persona = em.find(Persona.class, id);
	}

	private void obtenerDireccionFisica() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		direccionFisica = "//SICCA//"
				+ anho
				+ "//"
				+ "OEE//"
				+ configuracionUoCab.getIdConfiguracionUo()
				+ "//"
				+ reclamoSugerencia.getPostulacion().getConcursoPuestoAgr()
						.getConcurso().getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + "//CS";
	}

	@SuppressWarnings("unchecked")
	public String obtenerEstadoActualConcurso() {
		if (reclamoSugerencia != null
				&& reclamoSugerencia.getIdReclamoSugerencia() != null)
			return obtenerEstadoConcurso(reclamoSugerencia);
		return null;
	}

	@SuppressWarnings("unchecked")
	public String obtenerEstadoConcurso(ReclamoSugerencia reclamo) {
		String sql = "select r.*  "
				+ "from seleccion.referencias r  "
				+ "where dominio = 'ESTADOS_CONCURSO' "
				+ "and r.valor_num = "
				+ reclamo.getPostulacion().getConcursoPuestoAgr().getConcurso()
						.getEstado();
		List<Referencias> listaRef = new ArrayList<Referencias>();
		listaRef = em.createNativeQuery(sql, Referencias.class).getResultList();
		if (listaRef.size() > 0)
			return listaRef.get(0).getDescAbrev();
		return "";
	}

	private Boolean faltanDatosObligatorio() {
		if (asuntoRpta == null || asuntoRpta.trim().isEmpty()
				|| textoRpta == null || textoRpta.trim().isEmpty())
			return true;
		return false;
	}

	private Long obtenerNro() {
		String sql = "select max(recl.nro_respuesta) "
				+ "from seleccion.reclamo_sugerencia recl";
		Long numero;
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null) {
			numero = new Long(0);
		} else {
			numero = new Long(config.toString());
		}
		numero = numero + 1;
		return numero;

	}

	/**
	 * Metodo que guarda los datos en la bd
	 * 
	 * @return
	 */
	public String save() {

		if (faltanDatosObligatorio()) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return null;
		}
		try {

			Long nroRpta = obtenerNro();
			reclamoSugerencia.setNroRespuesta(nroRpta);
			reclamoSugerencia.setAsuntoRespuesta(asuntoRpta);
			reclamoSugerencia.setDescripcionRespuesta(textoRpta);
			reclamoSugerencia.setEstado("R");
			reclamoSugerencia.setOrigen("CS");
			reclamoSugerencia.setEnviarCorreoPostulante(enviarEmail);
			reclamoSugerencia.setFechaRespuesta(new Date());
			reclamoSugerencia.setFechaMod(new Date());
			reclamoSugerencia.setComisionSeleccionDet(buscarComision());
			reclamoSugerencia.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			reclamoSugerencia.setUsuRespuesta(usuarioLogueado
					.getCodigoUsuario());
			
			em.merge(reclamoSugerencia);
			em.flush();

			if (enviarEmail)
				enviarEmails(reclamoSugerencia);

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "Ok";
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU400_msg4"));
			return null;
		}
	}
	

	@SuppressWarnings("unchecked")
	public void enviarEmails(ReclamoSugerencia sug) {
		String dirEmail = persona.getEMail();
		ConcursoPuestoAgr agr = new ConcursoPuestoAgr();
		agr = sug.getPostulacion().getConcursoPuestoAgr();
		String asunto = "NOTIF_RECLAMO_SUG_" + agr.getConcurso().getNombre()
				+ "_" + agr.getDescripcionGrupo();
		String texto = "";
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		try {
			texto = texto
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ "<p align=\"justify\">Su Reclamo/Sugerencia ha sido respondida:</p>"
					+ "<p><b>Nro Reclamo:" + sug.getNroReclamo() + "</b> </p> "
					+ "<p><b>Fecha Reclamo:"
					+ formatoDeFecha.format(sug.getFechaReclamoSugerencia())
					+ "</b> </p> " + "<p><b>Asunto:" + sug.getAsunto()
					+ "</b> </p> " + "<p><b>Descripci&oacute;n:"
					+ sug.getDescripcion() + "</b> </p> "
					+ "<p><b>Nro Respuesta:" + sug.getNroRespuesta()
					+ "</b> </p> " + "<p><b>Fecha Respuesta:"
					+ formatoDeFecha.format(sug.getFechaRespuesta())
					+ "</b> </p> " + "<p><b>Asunto:" + sug.getAsuntoRespuesta()
					+ "</b> </p> " + "<p><b>Descripci&oacute;n:"
					+ sug.getDescripcionRespuesta() + "</b> </p> "

					+ "<p> Atentos saludos</p> ";

			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,
					null);
			System.out.println("EL MAIL HA SIDO ENVIADO....");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	@SuppressWarnings("unchecked")
	private ComisionSeleccionDet buscarComision() {
		String cad = "select com_det.* "
				+ "from seleccion.comision_seleccion_det com_det "
				+ "join general.persona pers "
				+ "on pers.id_persona = com_det.id_persona "
				+ "join planificacion.configuracion_uo_cab cab "
				+ "on cab.id_configuracion_uo = com_det.id_configuracion_uo "
				+ "where com_det.activo is true " + "and pers.id_persona = "
				+ persona.getIdPersona() + " and cab.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo();
		List<ComisionSeleccionDet> listaCom = new ArrayList<ComisionSeleccionDet>();
		listaCom = em.createNativeQuery(cad, ComisionSeleccionDet.class)
				.getResultList();
		if (listaCom.size() > 0)
			return listaCom.get(0);
		return null;
	}

	public ReclamoSugerencia getReclamoSugerencia() {
		return reclamoSugerencia;
	}

	public void setReclamoSugerencia(ReclamoSugerencia reclamoSugerencia) {
		this.reclamoSugerencia = reclamoSugerencia;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getAsuntoRpta() {
		return asuntoRpta;
	}

	public void setAsuntoRpta(String asuntoRpta) {
		this.asuntoRpta = asuntoRpta;
	}

	public String getTextoRpta() {
		return textoRpta;
	}

	public void setTextoRpta(String textoRpta) {
		this.textoRpta = textoRpta;
	}

	public Boolean getEnviarEmail() {
		return enviarEmail;
	}

	public void setEnviarEmail(Boolean enviarEmail) {
		this.enviarEmail = enviarEmail;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

}
