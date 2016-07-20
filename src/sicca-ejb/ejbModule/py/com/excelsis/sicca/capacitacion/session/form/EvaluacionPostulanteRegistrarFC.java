package py.com.excelsis.sicca.capacitacion.session.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.SystemException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.persistence.ManagedPersistenceContext;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EvaluacionInscPost;
import py.com.excelsis.sicca.entity.EvaluacionPart;
import py.com.excelsis.sicca.entity.EvaluacionTipo;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("evaluacionPostulanteRegistrarFC")
public class EvaluacionPostulanteRegistrarFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(required = false)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	@In(required = false)
	SeguridadUtilFormController seguridadUtilFormController;
	private String from;

	private Long idPaisExpe;
	private String docIdentidad;
	private Long idCapacitacion;
	private Capacitaciones capacitacion;
	private String titulo;
	private String tipoCapacitacion;
	private String idMostrar;
	private List<EvaluacionInscPost> inscPostLista = new ArrayList<EvaluacionInscPost>();
	private Long idEval;

	public void init() {
		
		SeguridadUtilFormController seguridadUtilFormController =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		seguridadUtilFormController.validarCapacitacion(idCapacitacion, seguridadUtilFormController.estadoActividades("ESTADOS_CAPACITACION", "REALIZAR EVALUACION"), ActividadEnum.CAPA_EVALUAR_POSTULANTES.getValor());
		cargarCabecera();
		primeraVez();
		search();
		 
	}
	public void initVer() {
		
		cargarCabecera();
		primeraVez();
		search();
		 
	}

	/**
	 * Inserta los postulante que aun no estan presentes en EvaluacionInscPost
	 */
	private void insertarPostulantes(Long idEvalTipo) {
		Query q =
			em.createQuery("select PostulacionCap from PostulacionCap PostulacionCap"
				+ " where PostulacionCap.activo is true "
				+ " and PostulacionCap.capacitacion.idCapacitacion =:idCapacitacion ");

		q.setParameter("idCapacitacion", idCapacitacion);
		List<PostulacionCap> listaPostulantes = q.getResultList();
		if (listaPostulantes.size() > 0) {
			ManagedPersistenceContext persistenceContext =
				(ManagedPersistenceContext) Contexts.getConversationContext().get("entityManager");
			for (PostulacionCap o : listaPostulantes) {
				try {
					em = persistenceContext.getEntityManager();
					EvaluacionInscPost evaluacionInscPost = new EvaluacionInscPost();
					evaluacionInscPost.setActivo(true);
					evaluacionInscPost.setEvaluacionTipo(em.find(EvaluacionTipo.class, idEvalTipo));
					evaluacionInscPost.setFechaAlta(new Date());
					evaluacionInscPost.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					evaluacionInscPost.setPostulacionCap(em.find(PostulacionCap.class, o.getIdPostulacion()));
					em.persist(evaluacionInscPost);
					em.flush();
				} catch (NamingException e) {

					e.printStackTrace();
				} catch (SystemException e) {

					e.printStackTrace();
				}

			}

		}

	}

	/**
	 * Chequea la configuración inicial de la actividad
	 */
	@SuppressWarnings("unchecked")
	private void primeraVez() {
		if (capacitacion == null) {
			return;
		}
		Query q =
			em.createQuery("select EvaluacionTipo from EvaluacionTipo EvaluacionTipo "
				+ "where EvaluacionTipo.capacitaciones.idCapacitacion = " + idCapacitacion
				+ "and EvaluacionTipo.tipo ='EVAL_POST' ");
		List<EvaluacionTipo> lista = q.getResultList();
		if (lista.size() == 0) {
			// Primera vez
			EvaluacionTipo evaluacionTipo = new EvaluacionTipo();
			evaluacionTipo.setTipo("EVAL_POST");
			evaluacionTipo.setActivo(true);
			evaluacionTipo.setCapacitaciones(em.find(Capacitaciones.class, idCapacitacion));
			evaluacionTipo.setFechaAlta(new Date());
			evaluacionTipo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			evaluacionTipo.setFechaInicio(new Date());
			evaluacionTipo.setUsuInicio(usuarioLogueado.getCodigoUsuario());
			em.persist(evaluacionTipo);
			// Insertar los postulantes
			idEval = evaluacionTipo.getIdEval();
			insertarPostulantes(evaluacionTipo.getIdEval());
			em.flush();
		} else
			idEval = lista.get(0).getIdEval();

	}

	public String finalizarEvaluacion() {
		try {
			SeguridadUtilFormController seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			//if (!tieneRespuesta()) {
			if (!precondFinalizar()) {
				//statusMessages.add(Severity.ERROR, "Debe evaluar a todos los postulantes");
				return null;
			}
			/**
			 * SE ACTUALIZAN LAs TABLAS
			 **/

			capacitacion.setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_CAPACITACION", "PUBLICAR SELECCIONADOS"));
			capacitacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			capacitacion.setFechaMod(new Date());
			em.merge(capacitacion);

			EvaluacionTipo tipo = em.find(EvaluacionTipo.class, idEval);
			tipo.setUsuCierrre(usuarioLogueado.getCodigoUsuario());
			tipo.setFechaCierre(new Date());
			em.merge(tipo);

			/**
			 * SE PASA A SGT. TAREA
			 */
			jbpmUtilFormController.setActividadActual(ActividadEnum.CAPA_EVALUAR_POSTULANTES);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CAPA_PUBLICAR_SELECCIONADOS);
			jbpmUtilFormController.setTransition("evalPos_TO_pubSel");
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
			jbpmUtilFormController.setCapacitacion(capacitacion);

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			} else
				return null;

			/**
			 * SE ENVIA EL MAIL A LOS POSTULANTES
			 **/
			enviarMails();

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "next";

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, "Error inesperado :" + e.getMessage());
			return null;

		}
	}

	public boolean tieneRespuesta() {
		for (EvaluacionInscPost o : inscPostLista) {
			if (o.getResultado() == null || o.getResultado().trim().equals(""))
				return false;
		}
		return true;
	}
	
	private Boolean precondFinalizar() {
		if (inscPostLista == null || inscPostLista.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "No hay registros que guardar");
			return false;
		}
		for (EvaluacionInscPost o : inscPostLista) {
			if (o.getResultado() == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Debe evaluar a todos los postulantes");
				return false;
			}
		}
		int seleccionados = 0;
		Capacitaciones capacitacionActual = em.find(Capacitaciones.class, idCapacitacion);
		for (EvaluacionInscPost o : inscPostLista) {
			if(o.getResultado().equals("S")){
				seleccionados++;
			}
			if(seleccionados > capacitacionActual.getCupoMaximo().intValue()){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Cantidad de Seleccionados supera el cupo máximo de la capacitación.");
				return false;
			}
		}
		if(seleccionados < capacitacionActual.getCupoMinimo().intValue()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Cantidad de Seleccionados no alcanza el cupo mínimo de la capacitación.");
			return false;
		}
		return true;
	}

	private void cargarCabecera() {
		capacitacion = em.find(Capacitaciones.class, idCapacitacion);
		if (capacitacion != null) {
			titulo = capacitacion.getNombre();
			tipoCapacitacion = capacitacion.getDatosEspecificosTipoCap().getDescripcion();
		}
		// Nivel
		ConfiguracionUoCab oee =
			em.find(ConfiguracionUoCab.class, capacitacion.getConfiguracionUoCab().getIdConfiguracionUo());
		Entidad enti = em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		SinEntidad sinEn = em.find(SinEntidad.class, enti.getSinEntidad().getIdSinEntidad());
		Long idSinNivelEntidad = nivelEntidadOeeUtil.getIdSinNivelEntidad(sinEn.getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(enti.getSinEntidad().getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(oee.getIdConfiguracionUo());

		if (capacitacion.getConfiguracionUoDet() != null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitacion.getConfiguracionUoDet().getIdConfiguracionUoDet());

		nivelEntidadOeeUtil.init();

	}

	public void imprimir() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU530_imprimir_eval_postulante_486");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();

	}

	private void cargarParametros() {
		try {
			ConfiguracionUoCab uo = em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			String elWhere = " ";
			Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel", diccDscNEO.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad", diccDscNEO.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oee", diccDscNEO.get("OEE"));
			reportUtilFormController.getParametros().put("unidadOrga", diccDscNEO.get("UND_ORG"));
			reportUtilFormController.getParametros().put("nombreCapa", capacitacion.getNombre());
			reportUtilFormController.getParametros().put("tipoCapa", capacitacion.getDatosEspecificosTipoCap().getDescripcion());
			reportUtilFormController.getParametros().put("oeeUsuarioLogueado", uo.getDenominacionUnidad());

			elWhere = "  AND ET.ID_CAPACITACION = " + idCapacitacion;
			if (idPaisExpe == null)
				reportUtilFormController.getParametros().put("pais", "TODOS");
			else {				
				if (!seguridadUtilFormController.validarInput(idPaisExpe.toString(), TiposDatos.INTEGER.getValor(), null)){
					elWhere += "  AND P.id_pais_expedicion_doc = " + (new Date()).getTime();
				}else{
					reportUtilFormController.getParametros().put("pais", em.find(Pais.class, idPaisExpe).getDescripcion());
					elWhere += "  AND P.id_pais_expedicion_doc = " + idPaisExpe;	
				}
			}
			if (docIdentidad == null || docIdentidad.trim().isEmpty())
				reportUtilFormController.getParametros().put("cedula", "TODOS");
			else {
				if (!seguridadUtilFormController.validarInput(docIdentidad, TiposDatos.STRING.getValor(), null)) {
					reportUtilFormController.getParametros().put("cedula", docIdentidad);
					elWhere += "  AND P.documento_identidad = '" + (new Date()).getTime() + "'";
				} else {
					reportUtilFormController.getParametros().put("cedula", docIdentidad);
					elWhere +=
						"  AND P.documento_identidad = '"
							+ seguridadUtilFormController.caracterInvalido(docIdentidad) + "'";
				}
			}

			if (idMostrar == null)
				reportUtilFormController.getParametros().put("mostrar", "TODOS");
			else {
				if (!seguridadUtilFormController.validarInput(idMostrar, TiposDatos.STRING.getValor(), 1)){
					elWhere += "  AND EI.RESULTADO = '"+(new Date()).getTime()+"'";
				}
				else{
					elWhere += "  AND EI.RESULTADO = '" + idMostrar + "'";
					reportUtilFormController.getParametros().put("mostrar", idMostrar.equals("N")
						? "NO SELECCIONADOS" : idMostrar.equals("S") ? "SELECCIONADOS"
							: "LISTA DE ESPERA");	
				}
				
			}
			reportUtilFormController.getParametros().put("usuAlta", usuarioLogueado.getCodigoUsuario());
			reportUtilFormController.getParametros().put("elWhere", elWhere);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void search() {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		StringBuffer sql = new StringBuffer();
		sql.append("select EvaluacionInscPost from EvaluacionInscPost EvaluacionInscPost ");
		StringBuffer elWhere = new StringBuffer();
		elWhere.append(" where EvaluacionInscPost.evaluacionTipo.capacitaciones.idCapacitacion = :idCapacitacion");
		elWhere.append(" and EvaluacionInscPost.evaluacionTipo.tipo ='EVAL_POST'");
		elWhere.append(" and  EvaluacionInscPost.activo is true ");
		if (docIdentidad != null && !docIdentidad.trim().isEmpty()) {
			elWhere.append(" and  EvaluacionInscPost.postulacionCap.personaPostulante.documentoIdentidad = :docIdentidad");
		}
		if (idMostrar != null) {
			elWhere.append(" and  EvaluacionInscPost.resultado = :resultado");
		}
		if (idPaisExpe != null) {
			elWhere.append(" and  EvaluacionInscPost.postulacionCap.personaPostulante.paisExpedicionDoc.idPais = :idPais");
		}
		elWhere.append("  order by EvaluacionInscPost.idEvalIp  ");
		sql.append(elWhere.toString());
		Query q = em.createQuery(sql.toString());
		q.setParameter("idCapacitacion", idCapacitacion);
		if (docIdentidad != null && !docIdentidad.trim().isEmpty()) {
			q.setParameter("docIdentidad", sufc.caracterInvalido(docIdentidad));
		}
		if (idMostrar != null) {
			q.setParameter("resultado", idMostrar);
		}
		if (idPaisExpe != null) {
			q.setParameter("idPais", idPaisExpe);
		}

		inscPostLista = q.getResultList();
	}

	public void searchAll() {
		idPaisExpe = null;
		idMostrar = null;
		docIdentidad = null;
		search();
	}

	private void enviarMails() {
		try {
			String sql =
				"SELECT  CAPACITACION.FNC_EMAIL_CONSULTA(ID_CAPACITACION) AS EMAIL "
					+ "FROM  CAPACITACION.CAPACITACIONES " + "WHERE ID_CAPACITACION = "
					+ capacitacion.getIdCapacitacion();
			Object resultado = (Object) em.createNativeQuery(sql).getSingleResult();
			String listaCorreos = resultado.toString();

			for (EvaluacionInscPost post : inscPostLista) {
				String asunto = "Portal Paraguay Concursa -" + capacitacion.getNombre() + "-";
				String contenido = "";
				if (post.getResultado() != null) {
					if (post.getResultado().equals("S")) {
						asunto += " Lista de Seleccionados ";
						contenido =
							contenidoSeleccionado(post.getPostulacionCap().getPersona().getNombreCompleto(), listaCorreos);
						seleccionUtilFormController.enviarMails(post.getPostulacionCap().getPersona().getEMail(), contenido, asunto);
					}
					if (post.getResultado().equals("N")) {
						asunto += " Lista de No Seleccionados ";
						contenido =
							contenidoNoSeleccionado(post.getPostulacionCap().getPersona().getNombreCompleto(), listaCorreos);
						seleccionUtilFormController.enviarMails(post.getPostulacionCap().getPersona().getEMail(), contenido, asunto);
					}
					if (post.getResultado().equals("L")) {
						asunto += " Lista de Espera ";
						contenido =
							contenidoListaEspera(post.getPostulacionCap().getPersona().getNombreCompleto(), listaCorreos);
						seleccionUtilFormController.enviarMails(post.getPostulacionCap().getPersona().getEMail(), contenido, asunto);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, "Error al enviar mail: " + e.getMessage());
		}

	}

	private String contenidoSeleccionado(String nombre, String mails) {
		String texto =
			"<p align=\"justify\"> <b> Estimado(a) " + nombre + " </b>" + "<p></p> "
				+ " Comunicamos a usted, que ha sido seleccionado/a para: " + "<p></p> "
				+ "<p align=\"center\"> <b>"
				+ capacitacion.getDatosEspecificosTipoCap().getDescripcion() + "</b></p>"
				+ "<p align=\"center\"> <b>" + capacitacion.getNombre() + "</b></p>" + "<p></p> "
				+ "<p> Atentamente,</p> " + "<b><p>"
				+ nivelEntidadOeeUtil.getDenominacionUnidad().toUpperCase() + "</p></b>"
				+ "<b><p>Para mayor información comunicarse con: " + mails + " </p></b>";

		return texto;
	}

	private String contenidoNoSeleccionado(String nombre, String mails) {
		String texto =
			"<p align=\"justify\"> <b> Estimado(a) "
				+ nombre
				+ " </b>"
				+ "<p></p> "
				+ " Lamentamos comunicarle que no ha sido seleccionado/a para: "
				+ "<p></p> "
				+ "<p align=\"center\"> <b>"
				+ capacitacion.getDatosEspecificosTipoCap().getDescripcion()
				+ "</b></p>"
				+ "<p align=\"center\"> <b>"
				+ capacitacion.getNombre()
				+ "</b></p>"
				+ " Le agradecemos el interés que ha mostrado, y deseamos poder contar con usted para nuestros próximos cursos."
				+ "<p></p> " + "<p> Atentamente,</p> " + "<b><p>"
				+ nivelEntidadOeeUtil.getDenominacionUnidad().toUpperCase() + "</p></b>"
				+ "<b><p>Para mayor información comunicarse con: " + mails + "  </p></b>";

		return texto;
	}

	private String contenidoListaEspera(String nombre, String mails) {
		String texto =
			"<p align=\"justify\"> <b> Estimado(a) "
				+ nombre
				+ " </b>"
				+ "<p></p> "
				+ " Comunicamos a usted, que forma parte de la Lista de Espera para: "
				+ "<p></p> "
				+ "<p align=\"center\"> <b>"
				+ capacitacion.getDatosEspecificosTipoCap().getDescripcion()
				+ "</b></p>"
				+ "<p align=\"center\"> <b>"
				+ capacitacion.getNombre()
				+ "</b></p>"
				+ " Le agradecemos el interés que ha mostrado, y deseamos poder contar con usted para nuestros próximos cursos."
				+ "<p></p> " + "<p> Atentamente,</p> " + "<b><p>"
				+ nivelEntidadOeeUtil.getDenominacionUnidad().toUpperCase() + "</p></b>"
				+ "<b><p>Para mayor información comunicarse con: " + mails + " </p></b>";

		return texto;
	}

	public Long getIdPaisExpe() {
		return idPaisExpe;
	}

	public void setIdPaisExpe(Long idPaisExpe) {
		this.idPaisExpe = idPaisExpe;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTipoCapacitacion() {
		return tipoCapacitacion;
	}

	public void setTipoCapacitacion(String tipoCapacitacion) {
		this.tipoCapacitacion = tipoCapacitacion;
	}

	public String getIdMostrar() {
		return idMostrar;
	}

	public void setIdMostrar(String idMostrar) {
		this.idMostrar = idMostrar;
	}

	public List<EvaluacionInscPost> getInscPostLista() {
		return inscPostLista;
	}

	public void setInscPostLista(List<EvaluacionInscPost> inscPostLista) {
		this.inscPostLista = inscPostLista;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}

}
