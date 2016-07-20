package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.ReclamoSugerencia;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ReclamoSugerenciaHome;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("reclamoSugerenciaConcursoFormController")
public class ReclamoSugerenciaConcursoFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ReclamoSugerenciaHome reclamoSugerenciaHome;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;

	private Persona persona;
	private ConfiguracionUoCab oee;
	private Concurso concurso;
	private SinEntidad entidad;
	private ReclamoSugerencia reclamoSugerencia;
	private Long idGrupo;
	private Long idConcurso;
	private String tipoReclamo;
	private String asunto;
	private String descripcion;
	private Boolean enviarEmail;
	private String direccionFisica;

	private List<SelectItem> gruposSelecItem;
	private List<SelectItem> concursoSelecItem;

	public void init() {
		persona = new Persona();
		Long id = usuarioLogueado.getPersona().getIdPersona();
		persona = em.find(Persona.class, id);
		cargarComboGrupo();
		updateConcurso();
		obtenerDireccionFisica();
		reclamoSugerencia = new ReclamoSugerencia();
		reclamoSugerencia = reclamoSugerenciaHome.getInstance();
		if (reclamoSugerenciaHome.isIdDefined()) {
			tipoReclamo = reclamoSugerencia.getReclamoSugerencia();
			asunto = reclamoSugerencia.getAsunto();
			descripcion = reclamoSugerencia.getDescripcion();
			enviarEmail = reclamoSugerencia.getEnviarCorreoPostulante();
			Postulacion post = new Postulacion();
			post = reclamoSugerencia.getPostulacion();
			idGrupo = post.getConcursoPuestoAgr().getIdConcursoPuestoAgr();
			updateConcurso();
			concurso = new Concurso();
			concurso = post.getConcursoPuestoAgr().getConcurso();
			idConcurso = concurso.getIdConcurso();
			buscarOee();
		}

	}

	/**
	 * metodo que carga el combo correspondiente a los puestos disponibles para
	 * el usuario logueado
	 */
	@SuppressWarnings("unchecked")
	private void cargarComboGrupo() {
		String cad = "select distinct(agr.*) "
				+ "from seleccion.persona_postulante per_post "
				+ "join general.persona pers  "
				+ "on pers.id_persona = per_post.id_persona "
				+ "join seleccion.postulacion post "
				+ "on post.id_persona_postulante = per_post.id_persona_postulante "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = post.id_concurso_puesto_agr "
				+ "where (post.usu_cancelacion is null or post.usu_cancelacion = '') "
				+ "and post.fecha_cancelacion is null "
				+ "and pers.id_persona = " + persona.getIdPersona();

		List<ConcursoPuestoAgr> lista = new ArrayList<ConcursoPuestoAgr>();
		lista = em.createNativeQuery(cad, ConcursoPuestoAgr.class)
				.getResultList();
		gruposSelecItem = new ArrayList<SelectItem>();
		buildGruposSelectItem(lista);
	}

	private void buildGruposSelectItem(List<ConcursoPuestoAgr> grupoList) {
		if (gruposSelecItem == null)
			gruposSelecItem = new ArrayList<SelectItem>();
		else
			gruposSelecItem.clear();
		gruposSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (ConcursoPuestoAgr agr : grupoList) {
			gruposSelecItem.add(new SelectItem(agr.getIdConcursoPuestoAgr(),
					agr.getDescripcionGrupo()));
		}
	}

	/**
	 * Combo Concurso
	 */
	public void updateConcurso() {
		List<Concurso> concursoList = getConcursoByGrupo();
		concursoSelecItem = new ArrayList<SelectItem>();
		buildConcursoSelectItem(concursoList);
		idConcurso = null;

	}

	@SuppressWarnings("unchecked")
	private List<Concurso> getConcursoByGrupo() {
		if (idGrupo != null) {
			String sql = "select distinct(conc.*) "
					+ "from seleccion.concurso conc "
					+ "join seleccion.concurso_puesto_agr agr "
					+ "on agr.id_concurso = conc.id_concurso "
					+ "where conc.activo is true "
					+ "and agr.id_concurso_puesto_agr = " + idGrupo;
			return em.createNativeQuery(sql, Concurso.class).getResultList();

		}
		return new ArrayList<Concurso>();
	}

	private void buildConcursoSelectItem(List<Concurso> concursoList) {
		if (concursoSelecItem == null)
			concursoSelecItem = new ArrayList<SelectItem>();
		else
			concursoSelecItem.clear();

		concursoSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Concurso c : concursoList) {
			concursoSelecItem.add(new SelectItem(c.getIdConcurso(), c
					.getNombre()));
		}
	}

	public void buscarOee() {
		oee = new ConfiguracionUoCab();
		entidad = new SinEntidad();
		if (idConcurso != null) {
			concurso = em.find(Concurso.class, idConcurso);
			oee = concurso.getConfiguracionUoCab();
			if (oee != null)
				entidad = oee.getEntidad().getSinEntidad();
		}
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
			Long nroReclamo = obtenerNro();
			reclamoSugerencia = new ReclamoSugerencia();
			reclamoSugerencia.setAsunto(asunto);
			reclamoSugerencia.setDescripcion(descripcion);
			reclamoSugerencia.setOrigen("CO");
			reclamoSugerencia.setReclamoSugerencia(tipoReclamo);
			reclamoSugerencia.setEstado("P");
			reclamoSugerencia.setNroReclamo(nroReclamo);
			reclamoSugerencia.setEnviarCorreoPostulante(enviarEmail);
			Postulacion post = new Postulacion();
			post = buscarPostulacionCorrespondiente();
			if (post != null)
				reclamoSugerencia.setPostulacion(post);
			reclamoSugerencia.setFechaReclamoSugerencia(new Date());
			reclamoSugerenciaHome.setInstance(reclamoSugerencia);

			String resultado = reclamoSugerenciaHome.persist();
			if (resultado.equals("persisted")) {
				if (enviarEmail)
					enviarEmails(nroReclamo);
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
			} else {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU400_msg4"));
			}
			return resultado;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void enviarEmails(Long nro) {
		String dirEmail = persona.getEMail();
		ConcursoPuestoAgr agr = new ConcursoPuestoAgr();
		agr = em.find(ConcursoPuestoAgr.class, idGrupo);
		String asunto = "NOTIF_RECLAMO_SUG_" + concurso.getNombre() + "_"
				+ agr.getDescripcionGrupo();
		String texto = "";
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		try {
			texto = texto
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ "<p align=\"justify\">Usted ha enviado el siguiente Reclamo/Sugerencia:</p>"
					+ "<p><b>Nro:"
					+ nro
					+ "</b> </p> "
					+ "<p><b>Fecha:"
					+ formatoDeFecha.format(reclamoSugerenciaHome.getInstance()
							.getFechaReclamoSugerencia())
					+ "</b> </p> "
					+ "<p><b>Asunto:"
					+ reclamoSugerenciaHome.getInstance().getAsunto()
					+ "</b> </p> "
					+ "<p><b>Descripci&oacute;n:"
					+ reclamoSugerenciaHome.getInstance().getDescripcion()
					+ "</b> </p> "
					+ "<p align=\"justify\">La misma sera atendida en la brevedad posible por "
					+ oee.getDenominacionUnidad()
					+ " y/o por la Secretaria de la Funci&oacute;n Pública</p>"
					+ "<p> Atentos saludos</p> ";

			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto, null);
			System.out.println("EL MAIL HA SIDO ENVIADO....");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Boolean faltanDatosObligatorio() {
		if (idConcurso == null || idGrupo == null || tipoReclamo.equals("N")
				|| asunto == null || asunto.trim().isEmpty()
				|| descripcion == null || descripcion.trim().isEmpty())
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	private Postulacion buscarPostulacionCorrespondiente() {
		String sql = "select post.* "
				+ "from seleccion.postulacion post "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = post.id_concurso_puesto_agr "
				+ "join seleccion.persona_postulante pers_post "
				+ "on post.id_persona_postulante = pers_post.id_persona_postulante "
				+ "join general.persona p on p.id_persona = pers_post.id_persona "
				+ "where (post.usu_cancelacion is null or post.usu_cancelacion = '')  "
				+ "and post.fecha_cancelacion is null " + "and p.id_persona = "
				+ persona.getIdPersona() + " and agr.id_concurso_puesto_agr = "
				+ idGrupo;
		List<Postulacion> listaPost = new ArrayList<Postulacion>();
		listaPost = em.createNativeQuery(sql, Postulacion.class)
				.getResultList();
		if (listaPost.size() > 0)
			return listaPost.get(0);
		return null;
	}

	private void obtenerDireccionFisica() {
		String valor_persona = persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona();
		direccionFisica = "//SICCA//USUARIO_PORTAL" + "//" + valor_persona
				+ "//" + "RECLAMOS";
	}

	private Long obtenerNro() {
		String sql = "select max(recl.nro_reclamo) "
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
	
	

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public ConfiguracionUoCab getOee() {
		return oee;
	}

	public void setOee(ConfiguracionUoCab oee) {
		this.oee = oee;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public SinEntidad getEntidad() {
		return entidad;
	}

	public void setEntidad(SinEntidad entidad) {
		this.entidad = entidad;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public List<SelectItem> getGruposSelecItem() {
		return gruposSelecItem;
	}

	public void setGruposSelecItem(List<SelectItem> gruposSelecItem) {
		this.gruposSelecItem = gruposSelecItem;
	}

	public List<SelectItem> getConcursoSelecItem() {
		return concursoSelecItem;
	}

	public void setConcursoSelecItem(List<SelectItem> concursoSelecItem) {
		this.concursoSelecItem = concursoSelecItem;
	}

	public ReclamoSugerencia getReclamoSugerencia() {
		return reclamoSugerencia;
	}

	public void setReclamoSugerencia(ReclamoSugerencia reclamoSugerencia) {
		this.reclamoSugerencia = reclamoSugerencia;
	}

	public String getTipoReclamo() {
		return tipoReclamo;
	}

	public void setTipoReclamo(String tipoReclamo) {
		this.tipoReclamo = tipoReclamo;
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
