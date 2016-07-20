package py.com.excelsis.sicca.capacitacion.session.form;

import java.awt.image.ComponentSampleModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EvaluacionInscPost;
import py.com.excelsis.sicca.entity.EvaluacionPart;
import py.com.excelsis.sicca.entity.EvaluacionTipo;
import py.com.excelsis.sicca.entity.ListaDet;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.VerCursosEvaluacionPostulantes;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.CONVERSATION)
@Name("registrarEvalPostulanteFC")
public class RegistrarEvalPostulanteFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private Long idPaisExpe;
	private String docIdentidad;
	private Long idCapacitacion;
	private Capacitaciones capacitacion;
	private String titulo;
	private String tipoCapacitacion;
	private String idMostrar;
	private List<EvaluacionInscPost> lista;
	private List<VerCursosEvaluacionPostulantes> lCapacitaciones;
	private Boolean mostrarModal = false;
	private static final String SELECCIONADOS = "S";
	private static final String NO_SELECCIONADOS = "N";
	private static final String LISTA_ESPERA = "L";
	private String from;

	public void init() {

		mostrarModal = false;
		cargarCabecera();
		//ConfiguracionUoDet uoCap = em.find(ConfiguracionUoDet.class, capacitacion.getConfiguracionUoDet().getIdConfiguracionUoDet());
		/*em.refresh(capacitacion);
		ConfiguracionUoDet uoCap = capacitacion.getConfiguracionUoDet();
		ConfiguracionUoDet uoUser = usuarioLogueado.getConfiguracionUoDet();
		if (!(uoCap.getIdConfiguracionUoDet().equals(uoUser.getIdConfiguracionUoDet()))) {
		//if (!(capacitacion.getConfiguracionUoDet().getIdConfiguracionUoDet().longValue() == usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet().longValue())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}*/

		SeguridadUtilFormController seguridadUtilFormController =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		seguridadUtilFormController.validarCapacitacion(idCapacitacion, seguridadUtilFormController.estadoActividades("ESTADOS_CAPACITACION", "INSCRIPCION/LISTA"), ActividadEnum.CAPA_INSCRIPCION_LISTA.getValor());

		primeraVez();
		search();
	}

	public void initVer() {
		mostrarModal = false;
		cargarCabecera();
		/*if (!(capacitacion.getConfiguracionUoDet().getIdConfiguracionUoDet().longValue() == usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet().longValue())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}*/

		primeraVez();
		search();
	}

	public String finalizo(Boolean finalizo) {
		if (finalizo) {
			return "Sí";
		} else
			return "No";
	}

	public void cargarCapacitaciones(Long idPersona) {
		Query q =
			em.createQuery("select curso from VerCursosEvaluacionPostulantes curso "
				+ "where curso.idPersona  = :idPersona ");
		q.setParameter("idPersona", idPersona);
		lCapacitaciones = q.getResultList();
		mostrarModal = true;
	}

	public void imprimir() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU530_imprimir_eval_postulante_526");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}

	private void cargarParametros() {
		try {
			ConfiguracionUoCab uoCab =
				em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			String elWhere = " ";
			Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel", diccDscNEO.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad", diccDscNEO.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oee", diccDscNEO.get("OEE"));
			reportUtilFormController.getParametros().put("unidadOrga", diccDscNEO.get("UND_ORG"));
			reportUtilFormController.getParametros().put("nombreCapa", capacitacion.getNombre());
			reportUtilFormController.getParametros().put("tipoCapa", capacitacion.getDatosEspecificosTipoCap().getDescripcion());
			reportUtilFormController.getParametros().put("oeeUsuarioLogueado", uoCab.getDenominacionUnidad());

			elWhere = "  AND ET.ID_CAPACITACION = " + idCapacitacion;

			if (idPaisExpe == null)
				reportUtilFormController.getParametros().put("pais", "TODOS");
			else {
				if (!seguridadUtilFormController.validarInput(idPaisExpe.toString(), TiposDatos.INTEGER.getValor(), null)) {
					elWhere += "  AND P.id_pais_expedicion_doc = " + (new Date()).getTime();
				} else {
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
				if (!seguridadUtilFormController.validarInput(idMostrar, TiposDatos.STRING.getValor(), 1)) {
					elWhere += "  AND EI.RESULTADO = '" + (new Date()).getTime() + "'";
				} else {
					elWhere += "  AND EI.RESULTADO = '" + idMostrar + "'";
					reportUtilFormController.getParametros().put("mostrar", idMostrar.equals("N")
						? "NO SELECCIONADOS" : idMostrar.equals("S") ? "SELECCIONADOS"
							: "LISTA DE ESPERA");
				}

			}
			// reportUtilFormController.getParametros().put("mostrar", "SELECCIONADOS");
			reportUtilFormController.getParametros().put("usuAlta", usuarioLogueado.getCodigoUsuario());
			reportUtilFormController.getParametros().put("elWhere", elWhere);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		List<PostulacionCap> lista = q.getResultList();
		if (lista.size() > 0) {
			for (PostulacionCap o : lista) {
				q =
					em.createQuery("select EvaluacionInscPost from EvaluacionInscPost EvaluacionInscPost"
						+ " where EvaluacionInscPost.postulacionCap.idPostulacion = "
						+ o.getIdPostulacion());
				if (q.getResultList().size() == 0) {
					// Insertar
					EvaluacionInscPost evaluacionInscPost = new EvaluacionInscPost();
					evaluacionInscPost.setActivo(true);
					evaluacionInscPost.setEvaluacionTipo(new EvaluacionTipo());
					evaluacionInscPost.getEvaluacionTipo().setIdEval(idEvalTipo);
					evaluacionInscPost.setFechaAlta(new Date());
					evaluacionInscPost.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					evaluacionInscPost.setPostulacionCap(new PostulacionCap());
					evaluacionInscPost.getPostulacionCap().setIdPostulacion(o.getIdPostulacion());
					em.persist(evaluacionInscPost);
				}
				// sino, ya está presente
			}
		}
	}

	/**
	 * Chequea la configuración inicial de la actividad
	 */
	private void primeraVez() {
		if (capacitacion == null) {
			return;
		}
		Query q =
			em.createQuery("select EvaluacionTipo from EvaluacionTipo EvaluacionTipo "
				+ " where EvaluacionTipo.capacitaciones.idCapacitacion = " + idCapacitacion
				+ " and EvaluacionTipo.tipo ='EVAL_INSCRIP' ");
		List<EvaluacionTipo> lista = q.getResultList();
		if (lista.size() == 0) {
			// Primera vez
			EvaluacionTipo evaluacionTipo = new EvaluacionTipo();
			evaluacionTipo.setTipo("EVAL_INSCRIP");
			evaluacionTipo.setActivo(true);
			evaluacionTipo.setCapacitaciones(new Capacitaciones());
			evaluacionTipo.getCapacitaciones().setIdCapacitacion(idCapacitacion);
			evaluacionTipo.setFechaAlta(new Date());
			evaluacionTipo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			evaluacionTipo.setFechaInicio(new Date());
			evaluacionTipo.setUsuInicio(usuarioLogueado.getCodigoUsuario());
			em.persist(evaluacionTipo);
			// Insertar los postulantes
			insertarPostulantes(evaluacionTipo.getIdEval());
			em.flush();
		} else {
			if (lista.size() == 1 && lista.get(0).getUsuInicio() == null
				&& lista.get(0).getFechaInicio() == null) {
				// Primera Vez
				// actualizar EvaluacionTipo
				EvaluacionTipo evaluacionTipo = lista.get(0);
				evaluacionTipo.setFechaInicio(new Date());
				evaluacionTipo.setUsuInicio(usuarioLogueado.getCodigoUsuario());
				evaluacionTipo = em.merge(evaluacionTipo);
				// Insertar los postulantes
				insertarPostulantes(evaluacionTipo.getIdEval());
				em.flush();
			}
		}

	}

	public String traerResultado(String res) {
		if (res == null) {
			return "";
		} else if (res.equalsIgnoreCase("S"))
			return "Seleccionado";
		else if (res.equalsIgnoreCase("N"))
			return "No Seleccionado";
		else if (res.equalsIgnoreCase("L"))
			return "Lista de Espera";

		return "";
	}

	public String modalidad(String mod) {
		if (mod == null) {
			return "";
		}
		if (mod.equalsIgnoreCase("S")) {
			return "SEMIPRESENCIAL";
		} else if (mod.equalsIgnoreCase("V")) {
			return "VIRTUAL";
		} else if (mod.equalsIgnoreCase("P")) {
			return "PRESENCIAL";
		}
		return "";
	}

	public String fechaInicioFin(Date fecha1, Date fecha2) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String respuesta = "";
		if (fecha1 != null)
			respuesta = sdf.format(fecha1);
		else {
			return "";
		}
		if (fecha2 != null)
			respuesta = respuesta + " - " + sdf.format(fecha2);

		return respuesta;
	}

	private void cargarCabecera() {
		capacitacion = em.find(Capacitaciones.class, idCapacitacion);
		em.refresh(capacitacion);
		if (nivelEntidadOeeUtil == null) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		}
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitacion.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdConfigCab(capacitacion.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		if (capacitacion != null) {
			titulo = capacitacion.getNombre();
			tipoCapacitacion = capacitacion.getDatosEspecificosTipoCap().getDescripcion();
		}
	}

	public void search() {
		mostrarModal = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select EvaluacionInscPost from EvaluacionInscPost EvaluacionInscPost ");
		StringBuffer elWhere = new StringBuffer();
		elWhere.append(" where EvaluacionInscPost.evaluacionTipo.capacitaciones.idCapacitacion = :idCapacitacion");
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
		sql.append(elWhere.toString());
		Query q = em.createQuery(sql.toString());
		q.setParameter("idCapacitacion", idCapacitacion);
		if (docIdentidad != null && !docIdentidad.trim().isEmpty()) {
			q.setParameter("docIdentidad", docIdentidad);
		}
		if (idMostrar != null) {
			q.setParameter("resultado", idMostrar);
		}
		if (idPaisExpe != null) {
			q.setParameter("idPais", idPaisExpe);
		}
		lista = q.getResultList();
	}

	public void limpiar() {
		idMostrar = null;
		docIdentidad = null;
		idPaisExpe = null;
	}

	public EvaluacionInscPost actualizarPostulacion(EvaluacionInscPost evaluacionInscPost) {
		if (evaluacionInscPost.getPostulacionCap().getPersona() == null) {
			PostulacionCap postulacionCap =
				em.find(PostulacionCap.class, evaluacionInscPost.getPostulacionCap().getIdPostulacion());
			Persona persona = em.find(Persona.class, postulacionCap.getPersona().getIdPersona());
			evaluacionInscPost.setPostulacionCap(postulacionCap);
			evaluacionInscPost.getPostulacionCap().setPersona(persona);
		}

		return evaluacionInscPost;
	}

	public String save() {
		if (precondSave()) {
			for (EvaluacionInscPost o : lista) {
				o.setFechaEval(new Date());
				o.setUsuEval(usuarioLogueado.getCodigoUsuario());
				em.merge(o);
			}
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "OK";
		}
		return null;
	}

	private Boolean precondSave() {
		if (lista == null || lista.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "No hay registros que guardar");
			return false;
		}
		for (EvaluacionInscPost o : lista) {
			if (o.getResultado() == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Debe evaluar a todos los postulantes");
				return false;
			}
		}
		int seleccionados = 0;
		Capacitaciones capacitacionActual = em.find(Capacitaciones.class, idCapacitacion);
		for (EvaluacionInscPost o : lista) {
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

	private Boolean precondNextTask() {
		if (lista == null || lista.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "No hay registros para pasar a la siguiente tarea");
			return false;
		}
		for (EvaluacionInscPost o : lista) {
			if (o.getResultado() == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Debe evaluar a todos los postulantes ");
				return false;
			}
		}
		int seleccionados = 0;
		Capacitaciones capacitacionActual = em.find(Capacitaciones.class, idCapacitacion);
		for (EvaluacionInscPost o : lista) {
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

	public String nextTask() {
		if (precondNextTask()) {
			// Siguiente tarea
			Capacitaciones capa = em.find(Capacitaciones.class, idCapacitacion);
			SeleccionUtilFormController seleccionUtilFormController =
				(SeleccionUtilFormController) Component.getInstance(SeleccionUtilFormController.class, true);
			Integer idEstadoRefPubSel =
				seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_CAPACITACION", "PUBLICAR SELECCIONADOS");
			// a. Si todos fueron evaluados

			// ii.
			capa.setEstado(idEstadoRefPubSel);
			capa.setUsuMod(usuarioLogueado.getCodigoUsuario());
			capa.setFechaMod(new Date());
			capa = em.merge(capa);

			Query q =
				em.createQuery("select EvaluacionTipo from EvaluacionTipo EvaluacionTipo "
					+ " where EvaluacionTipo.capacitaciones.idCapacitacion =  " + idCapacitacion
					+ " and EvaluacionTipo.tipo = 'EVAL_INSCRIP'");
			EvaluacionTipo evaluacionTipo = (EvaluacionTipo) q.getSingleResult();
			evaluacionTipo.setUsuCierrre(usuarioLogueado.getCodigoUsuario());
			evaluacionTipo.setFechaCierre(new Date());
			em.merge(evaluacionTipo);

			// i.
			jbpmUtilFormController =
				(JbpmUtilFormController) Component.getInstance(JbpmUtilFormController.class, true);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
			jbpmUtilFormController.setCapacitacion(capa);
			jbpmUtilFormController.setActividadActual(ActividadEnum.CAPA_INSCRIPCION_LISTA);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CAPA_PUBLICAR_SELECCIONADOS);
			jbpmUtilFormController.setTransition("insLis_TO_pubSel");
			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}

			// iii.
			enviarMails();
			return "nextTask";
		}
		return null;
	}

	private String listaCorreo(Long idCapa) {
		String sql =
			"SELECT  CAPACITACION.FNC_EMAIL_CONSULTA(ID_CAPACITACION) AS EMAIL "
				+ "FROM  CAPACITACION.CAPACITACIONES " + "WHERE ID_CAPACITACION = " + idCapa;
		Object resultado = (Object) em.createNativeQuery(sql).getSingleResult();
		String listaCorreos = resultado.toString();
		return listaCorreos;
	}

	private void enviarMails() {
		UsuarioPortalFormController usuarioPortalFormController =

			(UsuarioPortalFormController) Component.getInstance(UsuarioPortalFormController.class, true);

		String dirEmail = null;
		String texto = null;
		String asunto = null;
		String textoHeader = null;
		String disculpaTexto =
			"<p align=\"justify\">Le agradecemos el inter&eacute;s que ha mostrado, y deseamos poder contar con usted para nuestros pr&oacute;ximos cursos</p>";
		String textoFooter =
			"<b>Atentamente,<br/> " + nivelEntidadOeeUtil.getDenominacionUnidad() + "<br/></b>";
		String lCorrero = listaCorreo(idCapacitacion);
		lCorrero = (lCorrero != null && !(lCorrero.trim().isEmpty())) ? lCorrero.trim() : "-";
		textoFooter += "Para mayor informaci&oacute;n comunicarse con: " + lCorrero + "</b>";
		Capacitaciones capa = null;
		for (EvaluacionInscPost o : lista) {
			dirEmail = o.getPostulacionCap().getPersona().getEMail();
			capa = o.getPostulacionCap().getCapacitacion();
			textoHeader =
				" <p align=\"justify\"> <b> Estimado(a)  "
					+ o.getPostulacionCap().getPersona().getNombres() + " "
					+ o.getPostulacionCap().getPersona().getApellidos() + ": </b></p>";
			if (o.getResultado().equalsIgnoreCase(SELECCIONADOS)) {
				asunto =
					"Portal Paraguay Concursa – " + capa.getNombre() + " – Lista de Seleccionados";
				texto =
					"<p>Comunicamos a usted, que ha sido seleccionado/a para:</p> <p align=\"center\"> "
						+ capa.getDatosEspecificosTipoCap().getDescripcion()
						+ "</p><p align=\"center\"> " + capa.getNombre() + "</p>";
			} else if (o.getResultado().equalsIgnoreCase(NO_SELECCIONADOS)) {
				asunto =
					"Portal Paraguay Concursa – "
						+ o.getPostulacionCap().getCapacitacion().getNombre()
						+ " – Lista de No Seleccionados";
				texto =
					"<p>Comunicamos a usted, que no ha sido seleccionado/a para:</p> <p align=\"center\"> "
						+ capa.getDatosEspecificosTipoCap().getDescripcion()
						+ "</p><p align=\"center\"> " + capa.getNombre() + "</p>" + disculpaTexto;

			} else {
				texto =
					"<p>Comunicamos a usted, que forma parte de la Lista de Espera para: </p> <p align=\"center\"> "
						+ capa.getDatosEspecificosTipoCap().getDescripcion()
						+ "</p><p align=\"center\"> " + capa.getNombre() + "</p>" + disculpaTexto;
				asunto =
					"Portal Paraguay Concursa – "
						+ o.getPostulacionCap().getCapacitacion().getNombre()
						+ " – Lista de Espera";
			}
			try {
				texto = textoHeader + texto + textoFooter;
				usuarioPortalFormController.enviarMails(dirEmail, texto, asunto, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

	public List<EvaluacionInscPost> getLista() {
		return lista;
	}

	public void setLista(List<EvaluacionInscPost> lista) {
		this.lista = lista;
	}

	

	public List<VerCursosEvaluacionPostulantes> getlCapacitaciones() {
		return lCapacitaciones;
	}

	public void setlCapacitaciones(
			List<VerCursosEvaluacionPostulantes> lCapacitaciones) {
		this.lCapacitaciones = lCapacitaciones;
	}

	public Boolean getMostrarModal() {
		return mostrarModal;
	}

	public void setMostrarModal(Boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
