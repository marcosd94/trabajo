package py.com.excelsis.sicca.capacitacion.session.form;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.print.DocFlavor.STRING;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;

import enums.TiposDatos;

import py.com.excelsis.sicca.capacitacion.session.PostulacionCapHome;
import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.EvaluacionInscPost;
import py.com.excelsis.sicca.entity.EvaluacionTipo;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.IdiomasPersona;
import py.com.excelsis.sicca.entity.ListaDet;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.PostulacionCapAdj;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.PersonaPostulanteHome;
import py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;

@Scope(ScopeType.CONVERSATION)
@Name("imprimirRegistrarPostulacionFC")
public class ImprimirRegistrarPostulacionFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	PersonaPostulanteHome personaPostulanteHome;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;

	private Capacitaciones capacitaciones = new Capacitaciones();
	private Persona persona = new Persona();
	private PostulacionCap postulacionCap = new PostulacionCap();
	private EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
	public ValidadorController validadorController;
	private ValidadorDTO validadorDTO;
	private Long idCapacitacion;
	private String codigo;
	private String mensaje;
	private String confirmar;

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idCapacitacion != null) {
			if (!sufc.validarInput(idCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
		}
		persona = usuarioLogueado.getPersona();
		codigo = usuarioLogueado.getCodigoUsuario();
		if (idCapacitacion != null)
			capacitaciones = em.find(Capacitaciones.class, idCapacitacion);
	}

	public void initConfirmacion() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idCapacitacion != null) {
			if (!sufc.validarInput(idCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
		}
		persona = usuarioLogueado.getPersona();
		codigo = usuarioLogueado.getCodigoUsuario();
		if (idCapacitacion != null)
			capacitaciones = em.find(Capacitaciones.class, idCapacitacion);
		statusMessages.clear();
		if (mensaje != null && !mensaje.trim().isEmpty())
			statusMessages.add(Severity.WARN, mensaje);
		else
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

		mensaje = null;
	}

	public String confirmar() throws Exception {
		if (validadorController == null)
			validadorController = (py.com.excelsis.sicca.util.ValidadorController) Component
					.getInstance(ValidadorController.class, true);
		if (!valido()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, mensaje);
			mensaje = null;
			return null;
		}
		try {
			guardarPersonaPostulante();
			actualizarPostulacion();
			guardarEstudiosRealizados();
			guardarIdiomas();
			guardarExperienciaLaboral();
			enviarEmail();
			em.flush();
			//enviarEmail();
			verificacionTipo();

			String url = "/capacitacion/registrarImprimirPostulacion/RegistrarPostulacionConfirmacion.seam?capacitacionesIdCapacitacion="
					+ idCapacitacion;
			if (mensaje != null)
				url += "&mensaje=" + mensaje;
			return url;
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_NO_MSG"));
			Transaction.instance().rollback();
			return null;
		}

	}

	private Boolean valido() {
		validadorDTO = validadorController.validarCfg("PERSONA", "CAPACIT",
				persona, null, null, capacitaciones.getConfiguracionUoCab());
		if (validadorDTO != null && !validadorDTO.isValido()) {
			mensaje = validadorDTO.getMensaje();
			return false;
		}
		if (!capacitaciones.getDestinadoA().equalsIgnoreCase("F")) {
			List<EmpleadoPuesto> listaPuesto = buscarFuncionarios();
			if (!listaPuesto.isEmpty()) {
				List<EmpleadoPuesto> listaPertenecientes = perteneceOee();
				if (listaPertenecientes.isEmpty()) {
					mensaje = "Usted permanece en la lista de Espera ya que no es funcionario del OEE especificado";
					empleadoPuesto = listaPuesto.get(0);
					return true;
				}
			} else {
				mensaje = "Usted no es funcionario Público y la Capacitación a la que se está intentando postular es Cerrada";
				return false;
			}
		}
		if (estaListaNegra()) {
			mensaje = "Usted se encuentra en la Lista Negra de participantes. Consulte con la SFP";
			return false;
		}
		return true;
	}

	private List<EmpleadoPuesto> buscarFuncionarios() {
		String sql = "SELECT E.* " + "FROM GENERAL.EMPLEADO_PUESTO E "
				+ "JOIN PLANIFICACION.PLANTA_CARGO_DET P "
				+ "ON P.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_DET UO "
				+ "ON UO.ID_CONFIGURACION_UO_DET = P.ID_CONFIGURACION_UO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "
				+ "ON OEE.ID_CONFIGURACION_UO = UO.ID_CONFIGURACION_UO "
				+ "WHERE E.ACTIVO = TRUE " + "AND E.ACTUAL = TRUE "
				+ "AND ID_PERSONA = " + persona.getIdPersona();

		return em.createNativeQuery(sql, EmpleadoPuesto.class).getResultList();
	}

	private List<EmpleadoPuesto> perteneceOee() {
		String sql = "SELECT E.* "
				+ "FROM GENERAL.EMPLEADO_PUESTO E "
				+ "JOIN PLANIFICACION.PLANTA_CARGO_DET P "
				+ "ON P.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_DET UO "
				+ "ON UO.ID_CONFIGURACION_UO_DET = P.ID_CONFIGURACION_UO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "
				+ "ON OEE.ID_CONFIGURACION_UO = UO.ID_CONFIGURACION_UO "
				+ "WHERE E.ACTIVO = TRUE "
				+ "AND E.ACTUAL = TRUE "
				+ "AND ID_PERSONA = "
				+ persona.getIdPersona()
				+ " AND OEE.ID_CONFIGURACION_UO IN (SELECT CC.ID_CONFIGURACION_UO "
				+ "FROM CAPACITACION.CAPACITACION_CERRADA CC "
				+ "WHERE CC.ACTIVO = TRUE " + "AND CC.ID_CAPACITACION = "
				+ capacitaciones.getIdCapacitacion() + ")";

		return em.createNativeQuery(sql, EmpleadoPuesto.class).getResultList();
	}

	private Boolean estaListaNegra() {
		String sql = "SELECT DET.* " + "FROM CAPACITACION.LISTA_DET DET "
				+ "JOIN CAPACITACION.POSTULACION_CAP P "
				+ "ON P.ID_POSTULACION = DET.ID_POSTULACION "
				+ "JOIN SELECCION.DATOS_ESPECIFICOS DE "
				+ "ON DE.ID_DATOS_ESPECIFICOS = DET.ID_DATOS_ESPECIFICOS_DESV "
				+ "WHERE DET.TIPO = 'D' " + "AND DET.ACTIVO = TRUE "
				+ "AND DE.VALOR_ALF = 'B' " + "AND DET.MOTIVO_HABILIT IS NULL "
				+ "AND P.ID_PERSONA = " + persona.getIdPersona();
		List<ListaDet> listaDet = new ArrayList<ListaDet>();
		listaDet = em.createNativeQuery(sql, ListaDet.class).getResultList();
		if (listaDet.isEmpty())
			return false;
		return true;
	}

	/**
	 * Paso1 Obligatorio
	 */
	private void guardarPersonaPostulante() {
		PersonaPostulante personaPostulante = new PersonaPostulante();
		personaPostulante.setActivo(persona.getActivo());
		personaPostulante.setApellidos(persona.getApellidos());
		personaPostulante.setBarrio(persona.getBarrio());
		personaPostulante.setCallePrincipal(persona.getCallePrincipal());
		personaPostulante.setCiudadDirecc(persona.getCiudadByIdCiudadDirecc());
		personaPostulante.setCiudadNac(persona.getCiudadByIdCiudadNac());
		personaPostulante.setDatosEspecificos(persona.getDatosEspecificos());
		personaPostulante.setDepartamentoNro(persona.getDepartamentoNro());
		personaPostulante.setDireccionLaboral(persona.getDireccionLaboral());
		personaPostulante
				.setDocumentoIdentidad(persona.getDocumentoIdentidad());
		personaPostulante.setEMail(persona.getEMail());
		personaPostulante.setEstadoCivil(persona.getEstadoCivil());
		personaPostulante.setFechaNacimiento(persona.getFechaNacimiento());
		personaPostulante.setNombres(persona.getNombres());
		personaPostulante.setObservacion(persona.getObservacion());
		personaPostulante.setOtrasDirecciones(persona.getOtrasDirecciones());
		personaPostulante.setPaisExpedicionDoc(persona
				.getPaisByIdPaisExpedicionDoc());
		personaPostulante.setPersona(persona);
		personaPostulante.setPiso(persona.getPiso());
		personaPostulante.setPrimeraLateral(persona.getPrimeraLateral());
		personaPostulante.setSegundaLateral(persona.getSegundaLateral());
		personaPostulante.setSexo(persona.getSexo());
		personaPostulante.setTelefonos(persona.getTelefonos());

		personaPostulanteHome.setInstance(personaPostulante);
		personaPostulanteHome.persist();
	}

	/**
	 * Paso2 Obligatorio
	 */
	private void actualizarPostulacion() {
		String cad = "SELECT P.* FROM CAPACITACION.POSTULACION_CAP P "
				+ "WHERE P.ID_CAPACITACION IS NULL "
				+ "AND P.ACTIVO IS TRUE AND P.FICHA IS TRUE "
				+ "AND P.ID_PERSONA = " + persona.getIdPersona();
		postulacionCap = new PostulacionCap();
		postulacionCap = (PostulacionCap) em.createNativeQuery(cad,
				PostulacionCap.class).getSingleResult();
		if (postulacionCap != null) {
			postulacionCap.setFicha(false);
			postulacionCap.setPersonaPostulante(personaPostulanteHome
					.getInstance());
			postulacionCap.setCapacitacion(capacitaciones);
			postulacionCap.setUsuPost(usuarioLogueado.getCodigoUsuario());
			postulacionCap.setFechaPost(new Date());
			postulacionCap.setTipo("PO");
			List<EmpleadoPuesto> listaPuesto = buscarFuncionarios();
			if (capacitaciones.getDestinadoA().equalsIgnoreCase("C")) {

				if (!listaPuesto.isEmpty())
					postulacionCap.setEmpleadoPuesto(listaPuesto.get(0));
			} //else {
			if(capacitaciones.getDestinadoA().equalsIgnoreCase("A")){
				if (listaPuesto.size() == 1)
					postulacionCap.setEmpleadoPuesto(listaPuesto.get(0));
				else {
					EmpleadoPuesto emp = estaOee(listaPuesto);
					if (emp != null)
						postulacionCap.setEmpleadoPuesto(emp);
					else
						postulacionCap.setEmpleadoPuesto(listaPuesto.get(0));
				}
			}
			em.merge(postulacionCap);
		}
	}

	private EmpleadoPuesto estaOee(List<EmpleadoPuesto> lista) {
		for (EmpleadoPuesto e : lista) {
			Long id = e.getPlantaCargoDet().getConfiguracionUoDet()
					.getConfiguracionUoCab().getIdConfiguracionUo();
			if (id.equals(capacitaciones.getConfiguracionUoCab()
					.getIdConfiguracionUo()))
				return e;
		}
		return null;
	}

	/**
	 * Paso3 se ejecuta en caso de que existan registros
	 */
	private void guardarEstudiosRealizados() {
		String sql = "SELECT E.* " + "FROM SELECCION.ESTUDIOS_REALIZADOS E "
				+ "WHERE E.ACTIVO IS TRUE " + "AND E.ID_PERSONA = "
				+ persona.getIdPersona();
		List<EstudiosRealizados> listaEstudios = new ArrayList<EstudiosRealizados>();
		listaEstudios = em.createNativeQuery(sql, EstudiosRealizados.class)
				.getResultList();
		for (EstudiosRealizados e : listaEstudios) {
			EstudiosRealizados actual = new EstudiosRealizados();
			actual.setActivo(e.isActivo());
			actual.setDocumentos(e.getDocumentos());
			actual.setDuracionHoras(e.getDuracionHoras());
			actual.setEspecialidades(e.getEspecialidades());
			actual.setFechaAlta(new Date());
			actual.setFechaFin(e.getFechaFin());
			actual.setFechaInicio(e.getFechaInicio());
			actual.setFinalizo(e.getFinalizo());
			actual.setInstitucionEducativa(e.getInstitucionEducativa());
			actual.setOtraInstit(e.getOtraInstit());
			actual.setOtroTituloObt(e.getOtroTituloObt());
			actual.setPais(e.getPais());
			actual.setPersonaPostulante(personaPostulanteHome.getInstance());
			actual.setTipoDuracion(e.getTipoDuracion());
			actual.setTituloAcademico(e.getTituloAcademico());
			actual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(actual);
		}
	}

	/**
	 * Paso4 se ejecuta en caso de que existan registros
	 */
	private void guardarIdiomas() {
		String sql = "SELECT I.* FROM SELECCION.IDIOMAS_PERSONA I "
				+ "WHERE  I.ACTIVO IS TRUE  " + "AND I.ID_PERSONA = "
				+ persona.getIdPersona();
		List<IdiomasPersona> listaIdiomas = new ArrayList<IdiomasPersona>();
		listaIdiomas = em.createNativeQuery(sql, IdiomasPersona.class)
				.getResultList();
		for (IdiomasPersona i : listaIdiomas) {
			IdiomasPersona idiomasPersona = new IdiomasPersona();
			idiomasPersona.setActivo(i.isActivo());
			idiomasPersona.setDatosEspecificos(i.getDatosEspecificos());
			idiomasPersona.setEscribe(i.getEscribe());
			idiomasPersona.setFechaAlta(new Date());
			idiomasPersona.setHabla(i.getHabla());
			idiomasPersona.setLee(i.getLee());
			idiomasPersona.setPersonaPostulante(personaPostulanteHome
					.getInstance());
			idiomasPersona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(idiomasPersona);
		}
	}

	/**
	 * Paso5 se ejecuta en caso de que existan registros
	 */
	private void guardarExperienciaLaboral() {
		String sql = "SELECT E.* " + "FROM SELECCION.EXPERIENCIA_LABORAL E "
				+ "WHERE  E.ACTIVO IS TRUE  " + "AND E.ID_PERSONA = "
				+ persona.getIdPersona();
		List<ExperienciaLaboral> listaExperiencias = new ArrayList<ExperienciaLaboral>();
		listaExperiencias = em.createNativeQuery(sql, ExperienciaLaboral.class)
				.getResultList();
		for (ExperienciaLaboral exp : listaExperiencias) {
			ExperienciaLaboral experiencia = new ExperienciaLaboral();
			experiencia.setActivo(exp.getActivo());
			experiencia.setDatosEspecificosSector(exp
					.getDatosEspecificosSector());
			experiencia.setDatosEspecificosTipoVinculo(exp
					.getDatosEspecificosTipoVinculo());
			experiencia.setDocumentos(exp.getDocumentos());
			experiencia.setEmpresa(exp.getEmpresa());
			experiencia.setFechaAlta(new Date());
			experiencia.setFechaDesde(exp.getFechaDesde());
			experiencia.setFechaHasta(exp.getFechaHasta());
			experiencia.setFuncionesRealizadas(exp.getFuncionesRealizadas());
			experiencia.setOtroSector(exp.getOtroSector());
			experiencia.setOtroVincu(exp.getOtroVincu());
			experiencia.setPais(exp.getPais());
			experiencia.setPersonaPostulante(personaPostulanteHome
					.getInstance());
			experiencia.setPosicion(exp.getPosicion());
			experiencia.setPuestoCargo(exp.getPuestoCargo());
			experiencia.setReferenciasLaborales(exp.getReferenciasLaborales());
			experiencia.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(experiencia);
		}
	}

	private void verificacionTipo() {
		if (capacitaciones.getTipoSeleccion().equals("I")) {
			if (sobrepasaCupo()) {
				buscarCabecera();
				statusMessages.add(Severity.WARN,
						"Formará parte de la Lista de Espera");
			}
		}
	}

	private Boolean sobrepasaCupo() {
		String sql = "SELECT P.* " + "FROM CAPACITACION.POSTULACION_CAP P "
				+ "WHERE P.ACTIVO IS TRUE AND P.FICHA IS FALSE AND P.ID_CAPACITACION = "
				+ capacitaciones.getIdCapacitacion();
		List<PostulacionCap> listaPostulaciones = new ArrayList<PostulacionCap>();
		listaPostulaciones = em.createNativeQuery(sql, PostulacionCap.class)
				.getResultList();
		if (listaPostulaciones != null && listaPostulaciones.size() > 0) {
			if (listaPostulaciones.size() > capacitaciones.getCupoMaximo()
					.intValue())
				return true;
		}
		return false;
	}

	private void buscarCabecera() {
		String sql = "SELECT E.* " + "FROM CAPACITACION.EVALUACION_TIPO E "
				+ "WHERE E.ACTIVO IS TRUE " + "AND E.TIPO = 'EVAL_INSCRIP' "
				+ "AND E.ID_CAPACITACION = "
				+ capacitaciones.getIdCapacitacion();
		EvaluacionTipo evaluacionTipo = new EvaluacionTipo();
		try{
			evaluacionTipo = (EvaluacionTipo) em.createNativeQuery(sql,
					EvaluacionTipo.class).getSingleResult();
		}catch(Exception e) {
			evaluacionTipo = new EvaluacionTipo();
			evaluacionTipo.setActivo(true);
			evaluacionTipo.setCapacitaciones(capacitaciones);
			evaluacionTipo.setFechaAlta(new Date());
			evaluacionTipo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			evaluacionTipo.setTipo("EVAL_INSCRIP");
			em.persist(evaluacionTipo);
		}
		
		/*if (evaluacionTipo == null || evaluacionTipo.getIdEval() == null) {
			evaluacionTipo = new EvaluacionTipo();
			evaluacionTipo.setActivo(true);
			evaluacionTipo.setCapacitaciones(capacitaciones);
			evaluacionTipo.setFechaAlta(new Date());
			evaluacionTipo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			evaluacionTipo.setTipo("EVAL_INSCRIP");
			em.persist(evaluacionTipo);
		}*/
		EvaluacionInscPost evaluacionInscPost = new EvaluacionInscPost();
		evaluacionInscPost.setActivo(true);
		evaluacionInscPost.setEvaluacionTipo(evaluacionTipo);
		evaluacionInscPost.setFechaAlta(new Date());
		evaluacionInscPost.setPostulacionCap(postulacionCap);
		evaluacionInscPost.setObservacion("Asignado por el sistema");
		evaluacionInscPost.setResultado("L");
		evaluacionInscPost.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		em.persist(evaluacionInscPost);
		em.flush();
	}

	public void enviarEmail() {
		String sql = "SELECT  CAPACITACION.FNC_EMAIL_CONSULTA(ID_CAPACITACION) AS EMAIL "
				+ "FROM  CAPACITACION.CAPACITACIONES "
				+ "WHERE ID_CAPACITACION = "
				+ capacitaciones.getIdCapacitacion();
		Object resultado = (Object) em.createNativeQuery(sql).getSingleResult();
		String listaCorreos = resultado.toString();
		String dirEmail = "";
		dirEmail = persona.getEMail();
		String asunto = "Portal Paraguay Concursa - Confirmación de Postulación Capacitación/Beca";
		String texto = "";
		String configuracionCab = "";
		ConfiguracionUoCab auxiliarConfUoCab = null;
		if (capacitaciones.getConfiguracionUoCab() != null)
			auxiliarConfUoCab = capacitaciones.getConfiguracionUoCab();
			configuracionCab = auxiliarConfUoCab.getDenominacionUnidad();
		try {
			texto = texto
					+ "<p align=\"justify\"> <b> Estimado/a "
					+ persona.getNombreCompleto()
					+ "</b></p>"
					+ "<p align=\"justify\">Se ha registrado con &eacute;xito la postulaci&oacute;n de la Capacitaci&oacute;n/Beca: </p>"
					+ "<p align=\"center\"> <b>"
					+ capacitaciones.getDatosEspecificosTipoCap()
							.getDescripcion()
					+ "</b></p>"
					+ "<p align=\"center\"> <b>"
					+ capacitaciones.getNombre()
					+ "</b></p>"
					+ "<p align=\"justify\"> Adem&aacute;s le recordamos que el C&oacute;digo de Postulaci&oacute;n que utilizar&aacute; el Portal Paraguay Concursa es: </p>"
					+ "<p align=\"center\">"
					+ usuarioLogueado.getCodigoUsuario()
					+ "</p>"

					+ "<p></p>"
					+ "<p align=\"justify\"><b> ¡Importante!: </b> Si desea consultar sus postulaciones o cancelar el mismo, podr&aacute; acceder a su cuenta en el Portal y en el apartado <b>Capacitaciones</b> &iacute;tem: <b>Mis Postulaciones</b>, encontrar&aacute; todas las postulaciones activas, a las que se encuentra inscripta. Desde aqu&iacute; podr&aacute; Cancelar su Postulaci&oacute;n. </p>"
					+ "<p></p>"
					+ "<p> Atentamente,</p> "
					+ "<b>"
					+ configuracionCab
					+ "</p></b>"

					+ "<p align=\"justify\"><b>Para mayor informaci&oacute;n comuniquese con: </b> "
					+ listaCorreos + "</p>";

			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,
					null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pdf() throws Exception {
		Connection conn = JpaResourceBean.getConnection();
		try {

			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			if (capacitaciones.getConfiguracionUoCab() != null)
				param.put("configuracionUoCab", capacitaciones
						.getConfiguracionUoCab().getDenominacionUnidad());
			param.put("codPostulante", usuarioLogueado.getCodigoUsuario());
			param.put("postulante",
					persona.getNombres() + " " + persona.getApellidos());
			param.put("ci", persona.getDocumentoIdentidad());
			param.put("pais", persona.getPaisByIdPaisExpedicionDoc()
					.getDescripcion());
			param.put("tipoCapacitacion", capacitaciones
					.getDatosEspecificosTipoCap().getDescripcion());
			param.put("nombre", capacitaciones.getNombre());
			param.put("fecha", postulacionCap.getFechaPost());
			JasperReportUtils.respondPDF(
					"ImprimirPostulacionCapacitacionCU482", param, false, conn,
					usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}

	}

	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}

	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public PostulacionCap getPostulacionCap() {
		return postulacionCap;
	}

	public void setPostulacionCap(PostulacionCap postulacionCap) {
		this.postulacionCap = postulacionCap;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public ValidadorDTO getValidadorDTO() {
		return validadorDTO;
	}

	public void setValidadorDTO(ValidadorDTO validadorDTO) {
		this.validadorDTO = validadorDTO;
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getConfirmar() {
		return confirmar;
	}

	public void setConfirmar(String confirmar) {
		this.confirmar = confirmar;
	}

}
