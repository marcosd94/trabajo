package py.com.excelsis.sicca.seleccion.session;

import java.io.File;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import enums.ActividadEnum;

import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.juridicos.reportes.form.ComprobantePostulacionCarpetaRC;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import py.com.excelsis.sicca.util.ValidadorController;

@Scope(ScopeType.CONVERSATION)
@Name("cargarCarpeta508FC")
public class CargarCarpeta508FC {

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
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;
	@In(required = false, create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(required = false, create = true)
	ReponerCategoriasController reponerCategoriasController;

	@In(required = false, create = true)
	ValidadorController validadorController;

	private String nombre;
	private String apellido;
	private Long idPaisEpeDoc;
	private String ci;
	private String codActividad;
	private List<DTO508> lista;
	private Pais paisExpedi;
	private Long idGrupo;
	private ConcursoPuestoAgr grupo;
	private Boolean habilitarBtnNewPerson = false;
	private Boolean mostrarModalConfirmacion = false;
	private Long idPersonaAPostular;
	private String mensajeConfirmPostula;
	private String from = "";
	private Boolean isActividad13 = false;
	private String nombrePantalla = "";
	private Boolean habBtnDisminuirP = false;
	private String obsActividad;
	private String direccionFisica;
	private String viewDisminuirPuesto = "";
	private String msjModalPersonDisca = "";
	private Boolean pasarSgteActividad = false;
	private String mensajeModalDismiPue = null;
	private String obsModal;
	private Long idPersonaModalObs;
	private String msjModalPersonMatch = "";
	private Persona personMatch;
	private Boolean primeraVez = true;
	private boolean csi = false;

	public String init() {
		if (!validarActividad11()) {
			statusMessages.add(Severity.ERROR, "No es posible gestionar la actividad debido a que aun no ha iniciado la fecha de recepción de postulación");
			org.jboss.seam.faces.Redirect.instance().setViewId("/seleccion/bandejaEntrada/BandejaEntradaList.xhtml");
			return "FAIL";
		}
		cargarCabecera();
		esActividad13();
		cargarDireccionFisica();
		if (!isActividad13) {
			primeraVez = false;
			search11("TODOS");
		}
		return "OK";
	}

	private boolean validarActividad11() {
		if (codActividad.equals(ActividadEnum.RECIBIR_POSTULACIONES.getValor())) {
			Query q =
				em.createQuery("select FechasGrupoPuesto from FechasGrupoPuesto FechasGrupoPuesto "
					+ " where FechasGrupoPuesto.activo is true and FechasGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo"
					+ " and FechasGrupoPuesto.fechaRecepcionDesde <= :fechaSistema");

			q.setParameter("idGrupo", idGrupo);
			q.setParameter("fechaSistema", new Date(), TemporalType.DATE);
			List lista = q.getResultList();
			if (lista.size() == 1) {
				return true;
			} else
				return false;
		}
		return true;
	}

	public Boolean disminuirPuestos() {
		Integer cantPosActivos = seleccionUtilFormController.cantPostulantesActivos(idGrupo);
		// Cantidad de Puestos Activos
		Integer cantPuestosActivos = seleccionUtilFormController.cantPuestosActivos(idGrupo);
		Integer cantDisminuir =
			seleccionUtilFormController.diminuirPuesto(cantPosActivos, cantPuestosActivos);
		if (cantDisminuir != null) {
			mensajeModalDismiPue =
				"Debe sacar "
					+ cantDisminuir
					+ " puesto/s del grupo por no contar con la cantidad suficiente de postulantes. "
					+ " ¿Desea sacar los puestos ahora?";
			viewDisminuirPuesto =
				"/seleccion/admDisPue/admDisPue451.seam?idConcursoPuestoAgr=" + grupo
					+ "&cantPostulantes=" + cantPosActivos + "&cantVacancias=" + cantPuestosActivos
					+ "&from=/cargarCarpeta/cargarCarpeta&codActividad="
					+ ActividadEnum.COMPLETAR_CARPETAS.getValor();// se agrego
																	// en la
																	// incidencia
																	// 0001635
			return true;
		}
		return false;

	}

	private void cargarDireccionFisica() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		direccionFisica =
			"SICCA" + File.separator + sdf.format(new Date()) + File.separator + "OEE"
				+ File.separator
				+ grupo.getConcurso().getConfiguracionUoCab().getIdConfiguracionUo()
				+ File.separator
				+ grupo.getConcurso().getDatosEspecificosTipoConc().getIdDatosEspecificos()
				+ File.separator + grupo.getConcurso().getIdConcurso() + File.separator
				+ grupo.getIdConcursoPuestoAgr();
	}

	private Long traerIdParaguay() {
		Query q =
			em.createQuery("select Pais from Pais Pais where Pais.descripcion = 'PARAGUAY' and Pais.activo is true ");
		List<Pais> lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0).getIdPais();
		return null;
	}

	private void esActividad13() {
		if (codActividad.equalsIgnoreCase(ActividadEnum.COMPLETAR_CARPETAS.getValor())) {
			isActividad13 = true;
		}
	}

	public Persona updatePersona(Persona persona) {
		if (persona.getNombres() == null || persona.getNombres().trim().isEmpty()) {

			persona = em.find(Persona.class, persona.getIdPersona());
			if (persona.getPaisByIdPaisExpedicionDoc().getIdPais() != null) {
				persona.setPaisByIdPaisExpedicionDoc(em.find(Pais.class, persona.getPaisByIdPaisExpedicionDoc().getIdPais()));
			}
		}
		return persona;
	}

	public Usuario usuarioPostulante(Long idUsuario) {
		Query q =
			em.createQuery("select Usuario from Usuario Usuario "
				+ "  where usuario.activo is true                "
				+ "  and usuario.usuAlta ='PORTAL'              "
				+ "  and usuario.persona.idPersona = :idUsuario          ");
		q.setParameter("idUsuario", idUsuario);
		Usuario usuario = (Usuario) q.getSingleResult();
		return usuario;
	}

	public void postular() {
		try {
			if (idPersonaAPostular == null) {
				statusMessages.add(Severity.INFO, "Debe seleccionar un Postulante");
				return;
			}
			Postulacion postulacion = new Postulacion();
			postulacion.setActivo(true);
			postulacion.setPersona(new Persona());
			postulacion.getPersona().setIdPersona(idPersonaAPostular);
			postulacion.setConcursoPuestoAgr(new ConcursoPuestoAgr());
			postulacion.getConcursoPuestoAgr().setIdConcursoPuestoAgr(idGrupo);
			postulacion.setUsuPostulacion(usuarioPostulante(idPersonaAPostular).getCodigoUsuario());
			postulacion.setFechaPostulacion(new Date());
			postulacion.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
			postulacion.setFechaAltaOee(new Date());
			postulacion.setTipo("CARPETA");
			em.persist(postulacion);
			em.flush();
			enviarMailPostulacion(idPersonaAPostular);
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			limpiarPostulante();
			reporte514(postulacion.getIdPostulacion());
			search("TODOS");
		} catch (Exception e) {
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			e.printStackTrace();
		} finally {
			limpiarPostulante();
		}
	}

	private void reporte514(Long idPostulacion) throws SQLException {
		ComprobantePostulacionCarpetaRC comprobantePostulacionCarpetaRC =
			(ComprobantePostulacionCarpetaRC) Component.getInstance(ComprobantePostulacionCarpetaRC.class, true);
		comprobantePostulacionCarpetaRC.print(idPostulacion);
	}

	private Boolean validacionCeroPostulantesActivos() {
		// PASO 1: Contar cantidad de postulantes del grupo:
		Query q =
			em.createQuery("select count(Postulacion) from Postulacion Postulacion"
				+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
				+ " and Postulacion.activo is true");
		q.setParameter("idGrupo", idGrupo);
		Integer cantPosGrupo = ((Long) q.getSingleResult()).intValue();
		// PASO 2: Contar cantidad de postulantes del grupo que cancelaron:
		q =
			em.createQuery("select count(Postulacion) from Postulacion Postulacion"
				+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
				+ " and Postulacion.activo is true and Postulacion.usuCancelacion is not null "
				+ " and Postulacion.fechaCancelacion is not null");
		q.setParameter("idGrupo", idGrupo);
		Integer cantPosCancelaron = ((Long) q.getSingleResult()).intValue();
		// PASO 3: Hay 0 Postulantes activos?:
		if (cantPosCancelaron.intValue() == cantPosGrupo.intValue()) {
			statusMessages.add(Severity.ERROR, "El grupo se declara desierto por no contar con Postulantes");

			// PASO 4: Actualización de estados:
			grupo.setEstado(seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_GRUPO", "GRUPO DESIERTO"));
			grupo.setUsuMod(usuarioLogueado.getCodigoUsuario());
			grupo.setFechaMod(new Date());
			grupo = em.merge(grupo);
			List<PlantaCargoDet> lPuestos = seleccionUtilFormController.getPlantaCargoDet(idGrupo);
			for (PlantaCargoDet o : lPuestos) {
				o.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("VACANTE"));
				o.setEstadoDet(seleccionUtilFormController.buscarEstadoDet("CONCURSO", "DESIERTO"));
				o.setActivo(false);
				o.setUsuMod(usuarioLogueado.getCodigoUsuario());
				o.setFechaMod(new Date());
				em.merge(o);
			}
			/**
			 * Incidencia 0001635 Llamar al CU604 (le envía como parámetros el id_concurso_puesto_agr, y las cadenas ‘GRUPO’, ‘CONCURSO’)
			 */
			reponerCategoriasController.reponerCategorias(grupo, "GRUPO", "CONCURSO");
			/**
			 * fin
			 */
			// PASO 5: Envío de mails a los Integrantes de la Comisión de
			// Selección:
			enviarMailIntegrantes();
			return false;
		}
		return true;
	}

	public void enviarMailIntegrantes() {
		List<Persona> lComision = seleccionUtilFormController.getComisionSelecion(idGrupo);
		for (Persona o : lComision) {
			enviarMailComision(grupo, o);
		}
	}

	public void seleccionarIngresarObs(Long idpp) {
		idPersonaModalObs = idpp;
	}

	private Boolean precondNextTask() {
		if (validacionCeroPostulantesActivos()) {
			if (disminuirPuestos()) {
				return false;
			}
			if (!completarCarpetas(grupo)) {
				return false;
			}
		} else

			return false;
		return true;
	}

	public String guardarObsModal() {
		if (idPersonaModalObs == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un Postulante");
			return null;
		}
		if (obsModal == null || obsModal.trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe cargar una Oservación");
			return null;
		} else {
			if (obsModal.length() > 50) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
					+ ": Observación(50)");
				return null;
			}
		}
		try {
			Query q =
				em.createQuery("select Postulacion.personaPostulante from Postulacion Postulacion "
					+ " where Postulacion.personaPostulante.persona.idPersona = :idPersona "
					+ " and Postulacion.personaPostulante.activo is true "
					+ " and Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
			q.setParameter("idPersona", idPersonaModalObs);
			q.setParameter("idGrupo", idGrupo);
			PersonaPostulante pp = (PersonaPostulante) q.getSingleResult();

			pp.setObservacion(obsModal);
			pp.setUsuMod(usuarioLogueado.getCodigoUsuario());
			pp.setFechaMod(new Date());
			pp = em.merge(pp);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			limpiarObsModal();
		} catch (Exception e) {
			limpiarObsModal();
			e.printStackTrace();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			return null;
		}
		return "";
	}

	public void guardarObsActividad() {
		if (obsActividad != null && !obsActividad.trim().isEmpty()) {
			if (obsModal.length() > 250) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
					+ ": Observación(250)");
				return;
			}
			try {
				Observacion observacion = new Observacion();
				observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				observacion.setFechaAlta(new Date());
				observacion.setConcurso(grupo.getConcurso());
				observacion.setObservacion(obsActividad);
				observacion.setIdTaskInstance(grupo.getIdProcessInstance());
				em.persist(observacion);
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				e.printStackTrace();
			}
		} else {
			statusMessages.add(Severity.ERROR, "Debe cargar una Obs. de Actividad");
		}
	}

	private List<Postulacion> lPostulantesCarpeta(Long idGrupo) {
		Query q =
			em.createQuery("select Postulacion from Postulacion Postulacion "
				+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = " + idGrupo
				+ " and Postulacion.tipo = 'CARPETA'" + " and Postulacion.usuCancelacion is null "
				+ " and Postulacion.fechaCancelacion is null and Postulacion.activo is true ");

		return q.getResultList();
	}

	private List<EstudiosRealizados> lEstudiosRealizados(Long idPP) {
		Query q =
			em.createQuery("select EstudiosRealizados from EstudiosRealizados EstudiosRealizados "
				+ " where  EstudiosRealizados.personaPostulante.idPersonaPostulante = :idPP and EstudiosRealizados.activo is true ");
		q.setParameter("idPP", idPP);
		return q.getResultList();

	}

	private List<ExperienciaLaboral> lExperienciaLaboral(Long idPP) {
		Query q =
			em.createQuery("select ExperienciaLaboral from ExperienciaLaboral ExperienciaLaboral "
				+ " where  ExperienciaLaboral.personaPostulante.idPersonaPostulante = :idPP and ExperienciaLaboral.activo is true");
		q.setParameter("idPP", idPP);
		return q.getResultList();

	}

	private List<PersonaDiscapacidad> lPersonaDiscapacidad(Long idPP) {
		Query q =
			em.createQuery("select PersonaDiscapacidad from PersonaDiscapacidad PersonaDiscapacidad "
				+ " where  PersonaDiscapacidad.personaPostulante.idPersonaPostulante = :idPP and PersonaDiscapacidad.activo is true");
		q.setParameter("idPP", idPP);
		return q.getResultList();

	}

	public Boolean completarCarpetas(ConcursoPuestoAgr grupo) {
		List<Postulacion> lista = lPostulantesCarpeta(grupo.getIdConcursoPuestoAgr());
		for (Postulacion o : lista) {
			PersonaPostulante pp = o.getPersonaPostulante();
			Long id = pp.getIdPersonaPostulante();
			if (pp == null) {
				statusMessages.add(Severity.ERROR, "Debe completar la Ficha del postulate "
					+ o.getPersona().getNombres() + " " + o.getPersona().getApellidos());
				return false;
			}

			if (grupo.getConcurso().getPcd()) {
				String faltaCompletar = "";
				if (pp.getEstudiosRealizadoses().size() == 0) {
					faltaCompletar = "Ficha de Estudios Realizados";
				}
				if (pp.getExperienciaLaborals().size() == 0) {
					faltaCompletar += ", Ficha de Experiencia Laboral";
				}
				if (pp.getPersonaDiscapacidads().size() == 0) {
					faltaCompletar += ", Ficha de Persona Discapacidad";
				}
				if (!faltaCompletar.isEmpty()) {
					statusMessages.add(Severity.ERROR, "No puede pasar a la Siguiente Tarea porque "
						+ " queda por completar la/s ficha/s: "
						+ faltaCompletar
						+ " del postulante "
						+ o.getPersonaPostulante().getPersona().getNombres()
						+ " " + o.getPersonaPostulante().getPersona().getApellidos());
					return false;
				}
			} else {
				// No es concurso PCD
				String faltaCompletar = "";
				if (lEstudiosRealizados(pp.getIdPersonaPostulante()).size() == 0) {
					faltaCompletar += "Ficha de Estudios Realizados";
				}
				if (lExperienciaLaboral(pp.getIdPersonaPostulante()).size() == 0) {
					faltaCompletar += ", Ficha de Experiencia Laboral";
				}

				if (faltaCompletar.startsWith(",")) {
					faltaCompletar = faltaCompletar.replaceFirst(",", "");
				}

				if (!faltaCompletar.isEmpty()) {
					statusMessages.add(Severity.ERROR, "No puede pasar a la Siguiente Tarea porque "
						+ " queda por completar la/s ficha/s: "
						+ faltaCompletar
						+ " del postulante "
						+ o.getPersonaPostulante().getPersona().getNombres()
						+ " " + o.getPersonaPostulante().getPersona().getApellidos());
					return false;
				} else if (faltaCompletar.isEmpty()) {
					if (preferenciaDiscapa(idGrupo)) {
						if (pasarSgteActividad) {
							// El usuario respodió aceptar
							pasarSgteActividad = false;
							return true;
						}
						if (lPersonaDiscapacidad(pp.getIdPersonaPostulante()).size() == 0) {
							msjModalPersonDisca =
								"El grupo tiene Preferencia para Personas con Discapacidad, y hay Postulantes ("
									+ o.getPersonaPostulante().getPersona().getNombres() + " "
									+ o.getPersonaPostulante().getPersona().getApellidos()
									+ ") que no tienen completa la Ficha de Discapacidad. "
									+ " ¿Desea pasar a la Siguiente Actividad de todas maneras?";
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private Boolean preferenciaDiscapa(Long idGrupo) {
		Query q =
			em.createQuery("select DatosGrupoPuesto from DatosGrupoPuesto DatosGrupoPuesto "
				+ " where DatosGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
				+ " and DatosGrupoPuesto.activo is true");
		q.setParameter("idGrupo", idGrupo);
		List<DatosGrupoPuesto> lista = q.getResultList();
		if (lista.size() > 0) {
			if (lista.get(0).getPreferenciaPersDiscapacidad() != null
				&& lista.get(0).getPreferenciaPersDiscapacidad()) {
				return true;
			}
		}
		return false;

	}

	public String nextTask(String circuto) {
		if (!precondNextTask()) {
			return null;
		}
		try {
			// Actualizar, el estado del Grupo:
			grupo.setEstado(seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_GRUPO", "A EVALUAR DOCUMENTOS"));
			grupo.setUsuMod(usuarioLogueado.getCodigoUsuario());
			grupo.setFechaMod(new Date());
			grupo = em.merge(grupo);

			// Actualizar, el estado del Puesto en Planificación y el estado del
			// Puesto en Selección
			EstadoDet estadoDet =
				seleccionUtilFormController.buscarEstadoDet("CONCURSO", "A EVALUAR DOCUMENTOS");
			List<ConcursoPuestoDet> lConcursoPuestoDet =
				seleccionUtilFormController.getConcursoPuestoDet(idGrupo);
			for (ConcursoPuestoDet o : lConcursoPuestoDet) {
				o.setEstadoDet(estadoDet);
				o.setFechaMod(new Date());
				o.setUsuMod(usuarioLogueado.getCodigoUsuario());
				o.getPlantaCargoDet().setEstadoDet(estadoDet);
				o.getPlantaCargoDet().setUsuMod(usuarioLogueado.getCodigoUsuario());
				o.getPlantaCargoDet().setFechaMod(new Date());
				o.setPlantaCargoDet(em.merge(o.getPlantaCargoDet()));
				em.merge(o);
			}
			// Siguiente tarea
			jbpmUtilFormController =
				(JbpmUtilFormController) Component.getInstance(JbpmUtilFormController.class, true);
			jbpmUtilFormController.setConcursoPuestoAgr(grupo);
			if (circuto.equalsIgnoreCase("CPO")) {
				jbpmUtilFormController.setActividadActual(ActividadEnum.COMPLETAR_CARPETAS);
				jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EVALUACION_DOCUMENTAL);
				jbpmUtilFormController.setTransition("comCar_TO_EvaDoc");
			} else if (circuto.equalsIgnoreCase("CSI")) {
				jbpmUtilFormController.setActividadActual(ActividadEnum.CSI_COMPLETAR_CARPETAS);
				jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CSI_REALIZAR_EVALUACIONES);
				jbpmUtilFormController.setTransition("comCar_TO_reaEva");
			}

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			return null;
		}
	}

	public void limpiarPostulante() {
		idPersonaAPostular = null;
		mensajeConfirmPostula = null;

	}

	public String limpiarObsModal() {
		obsModal = null;
		idPersonaModalObs = null;
		return "";
	}

	public void limpiarDismiPue() {
		mensajeModalDismiPue = null;
	}

	public String generarLinkDisminuir() {
		String respuesta =
			"/seleccion/admDisPue/admDisPue451.seam?codActividad=COMPLETAR_CARPETAS&idConcursoPuestoAgr="
				+ idGrupo + "&from=cargarCarpeta/cargarCarpeta";
		return respuesta;
	}

	public void confirmPostulacion(Long idPersona) {
		Persona persona = em.find(Persona.class, idPersona);
		mensajeConfirmPostula =
			"Usted está postulando a "
				+ persona.getNombres()
				+ " "
				+ persona.getApellidos()
				+ " al concurso "
				+ grupo.getConcurso().getNumero()
				+ "/"
				+ grupo.getConcurso().getAnhio()
				+ " del "
				+ grupo.getConcurso().getConfiguracionUoCab().getDenominacionUnidad()
				+ ", para el puesto de "
				+ grupo.getDescripcionGrupo()
				+ ". Haga click en 'Aceptar' para confirmar, o 'Cancelar' para volver a la pantalla anterior.";
		idPersonaAPostular = idPersona;
		mostrarModalConfirmacion = true;
	}

	private String enviarMailComision(ConcursoPuestoAgr grupo, Persona persona) {
		if (persona.getEMail() != null && General.isEmail(persona.getEMail())) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			VelocityEngine ve = new VelocityEngine();
			java.util.Properties p = new java.util.Properties();
			p.setProperty("resource.loader", "class");
			p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init(p);

			VelocityContext context = new VelocityContext();
			context.put("descripcion_grupo", grupo.getDescripcionGrupo());
			context.put("nombreConcurso", seleccionUtilFormController.nombreConcuro(grupo.getConcurso()));
			context.put("fecha", sdf.format(new Date()));
			Template t = ve.getTemplate("/templates/emailContestDesierto_508.vm");
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			writer.toString();
			seleccionUtilFormController.enviarMails(persona.getEMail(), writer.toString(), ":  NOTIF_GRUPO_DESIERTO_"
				+ grupo.getConcurso().getNombre(), null);
		}
		return "";
	}

	private void enviarMailNotifSFP(Persona persona, Concurso concurso) {

		if (persona.getEMail() != null && General.isEmail(persona.getEMail())) {
			VelocityEngine ve = new VelocityEngine();
			java.util.Properties p = new java.util.Properties();
			p.setProperty("resource.loader", "class");
			p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init(p);
			VelocityContext context = new VelocityContext();
			context.put("nro_documento_identidad", persona.getDocumentoIdentidad());
			context.put("pais_expedicion_documento", persona.getPaisByIdPaisExpedicionDoc().getDescripcion());
			context.put("CONCURSO_NUMERO", concurso.getNumero());
			context.put("CONCURSO_ANIO", concurso.getAnhio());
			context.put("CONFIGURACION_UO_CAB_DESCRIPCION_CORTE", concurso.getConfiguracionUoCab().getDescripcionCorta());
			context.put("CONFIGURACION_UO_CAB_DENOMINACION_UNIDAD", concurso.getConfiguracionUoCab().getDenominacionUnidad());
			Template t = ve.getTemplate("/templates/emailPersonNotMatch_508.vm");
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			writer.toString();
			seleccionUtilFormController.enviarMails(persona.getEMail(), writer.toString(), "NOTIF_DATOS_PERSONA_NO_COINCIDEN", null);
		}
	}

	private void enviarMailPostulacion(Long idPersona) {
		Persona persona = em.find(Persona.class, idPersona);

		if (persona.getEMail() != null && General.isEmail(persona.getEMail())) {
			VelocityEngine ve = new VelocityEngine();
			java.util.Properties p = new java.util.Properties();
			p.setProperty("resource.loader", "class");
			p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init(p);
			VelocityContext context = new VelocityContext();
			context.put("nombreUsuario", persona.getNombres() + " " + persona.getApellidos());
			context.put("concursoNro", grupo.getConcurso().getNumero());
			context.put("concursoAnio", grupo.getConcurso().getAnhio());
			context.put("uoDescCorta", grupo.getConcurso().getConfiguracionUoCab().getDescripcionCorta());
			context.put("descConcursoPuesroAgr", grupo.getDescripcionGrupo());
			context.put("usuarioCodigo", usuarioEmailPostulacion(persona.getIdPersona()));
			Template t = ve.getTemplate("/templates/emailConfirmPost_508.vm");
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			writer.toString();
			seleccionUtilFormController.enviarMails(persona.getEMail(), writer.toString(), "Confirmación de Postulación - Portal Paraguay Concursa", null);
		}
	}

	private String usuarioEmailPostulacion(Long id) {
		Query q =
			em.createQuery("select usuario from Usuario usuario "
				+ " where usuario.persona.idPersona = :id " + " and usuario.usuAlta ='PORTAL' ");
		q.setParameter("id", id);
		List<Usuario> lista = q.getResultList();
		if (lista.isEmpty())
			return "";
		else
			return lista.get(0).getCodigoUsuario();
	}

	private void cargarCabecera() {
		if (nivelEntidadOeeUtil == null) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		}
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		grupo = em.find(ConcursoPuestoAgr.class, idGrupo);
		if (codActividad != null || !codActividad.isEmpty()) {
			if (codActividad.equalsIgnoreCase(ActividadEnum.RECIBIR_POSTULACIONES.getValor())) {
				if (isCsi()) {
					nombrePantalla = "Administrar Carga De Carpetas Concurso Simplificado";
				} else {
					nombrePantalla = "Administrar Carga de Carpetas";
				}

			} else if (codActividad.equalsIgnoreCase(ActividadEnum.COMPLETAR_CARPETAS.getValor())) {
				if (isCsi()) {
					nombrePantalla = "Completar Carpetas";
				} else {
					nombrePantalla = "Completar Carpetas Concurso Simplificado";
				}
			} else {
				statusMessages.add(Severity.ERROR, "Actividad incorrecta");
			}
		}
		if (idPaisEpeDoc == null)
			idPaisEpeDoc = traerIdParaguay();
	}

	private Boolean precondSearch(String tipo) {
		if (codActividad == null || codActividad.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Es necesario el código de actividad");
			return false;
		}
		if (codActividad.equalsIgnoreCase(ActividadEnum.RECIBIR_POSTULACIONES.getValor())) {
			if (tipo != null && !tipo.equalsIgnoreCase("TODOS"))
				if (idPaisEpeDoc == null || (ci == null || ci.isEmpty())) {
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CARGAR_REQUERIDOS"));
					return false;
				}
		}
		if (tipo != null && tipo.equalsIgnoreCase("TODOS")) {
			limpiar();
		}
		return true;
	}

	public void search(String tipo) {
		if (!precondSearch(tipo)) {
			return;
		}
		if (idPaisEpeDoc != null)
			paisExpedi = em.find(Pais.class, idPaisEpeDoc);
		if (codActividad.equalsIgnoreCase(ActividadEnum.RECIBIR_POSTULACIONES.getValor())) {
			search11(tipo);
		} else if (codActividad.equalsIgnoreCase(ActividadEnum.COMPLETAR_CARPETAS.getValor())) {

			search13();
		} else {
			statusMessages.add(Severity.ERROR, "Actividad incorrecta");
		}
	}

	private void unirListasDTO508(List<DTO508> lista1, List<DTO508> lista2) {

		if (lista1 != null)
			lista.addAll(lista1);
		if (lista2 != null)
			lista.addAll(lista2);
		// if (lista.size() > 0)
		// Collections.sort(lista);
	}

	private String notInPersona(List<DTO508> lista) {
		String respuesta = "";
		if (lista != null)
			for (DTO508 o : lista) {
				respuesta += "," + o.getPersona().getIdPersona();
			}
		respuesta = respuesta.replaceFirst(",", "");
		return respuesta;
	}

	private Boolean permanenteOContratado(Long idGrupo) {
		Query q =
			em.createQuery("select ConcursoPuestoDet from ConcursoPuestoDet ConcursoPuestoDet "
				+ " where ConcursoPuestoDet.activo is true and ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", idGrupo);
		List<ConcursoPuestoDet> lista = q.getResultList();
		if (lista.size() > 0) {
			PlantaCargoDet pcd = lista.get(0).getPlantaCargoDet();
			return ((pcd != null && pcd.getPermanente()) || (pcd.getContratado() != null && pcd.getContratado()));
		}
		return true;
	}

	private Boolean valTipoConcurso() {
		if (permanenteOContratado(idGrupo)) {
			if (idPaisEpeDoc == null) {
				idPaisEpeDoc = traerIdParaguay();
			}
			Pais pais = em.find(Pais.class, idPaisEpeDoc);
			if (!pais.getDescripcion().equalsIgnoreCase("PARAGUAY")) {
				statusMessages.add(Severity.ERROR, "Usted no puede postular. Este concurso es sólo para paraguayos/as");
				return false;
			} else {
				return true;
			}
		}
		return true;
	}

	private Boolean valPostulante(Persona persona) {
		ValidadorDTO validadorDTO =
			validadorController.validarCfg("ESPECIFICO", "INHAB", persona, null, null, grupo.getConcurso().getConfiguracionUoCab());
		if (!validadorDTO.isValido()) {
			statusMessages.add(Severity.ERROR, "Usted no puede postular porque está Inhabilitado/a. Consulte con la SFP");
			return false;
		} else {
			validadorDTO =
				validadorController.validarCfg("ESPECIFICO", "JUBILADO", persona, null, null, null);
			if (!validadorDTO.isValido()) {
				statusMessages.add(Severity.ERROR, "Usted no puede postular porque está Jubilado/a");
				return false;
			} else {
				validadorDTO =
					validadorController.validarCfg("ESPECIFICO", "E_MINIMA", persona, null, null, null);
				if (!validadorDTO.isValido()) {
					statusMessages.add(Severity.ERROR, "Usted no cuenta con la mayoría de edad para Concursar al Puesto");
					return false;
				}
			}
		}
		return true;
	}

	public void search11(String tipo) {

		if (lista == null) {
			lista = new ArrayList<DTO508>();
		} else {
			lista.clear();
		}
		/* Incidencia 1724 */
		if (!valTipoConcurso())
			return;
		/*****************/
		/* Desde boton todos */
		if (tipo.equalsIgnoreCase("TODOS")) {
			List<DTO508> lista1 = findPostulacion(grupo.getIdConcursoPuestoAgr(), null);
			List<DTO508> lista2 = findUsuario(grupo.getIdConcursoPuestoAgr(), notInPersona(lista1));
			unirListasDTO508(lista1, lista2);
		} else {
			/* Desde el botón buscar */
			PersonaDTO personaDTO =
				seleccionUtilFormController.buscarPersona(ci, paisExpedi.getDescripcion());
			if (personaDTO.getHabilitarBtn() == null) {
				statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			} else if (personaDTO.getHabilitarBtn()) {
				statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
				habilitarBtnNewPerson = true;
			} else {
				/* Incidencia 1724 */
				if (!valPostulante(personaDTO.getPersona())) {
					return;
				}
				/*****************/
				/* BUSCAR en tabla POSTULACION */
				lista = findPostulacion2(idGrupo, personaDTO.getPersona().getIdPersona());
			}
		}
	}

	private Postulacion findPostulacionCarpeta(Long idGrupo, Long idPersona) {
		List<Postulacion> lista = null;
		Query q =
			em.createQuery("select Postulacion from Postulacion Postulacion "
				+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
				+ " and Postulacion.tipo ='CARPETA' " + "and Postulacion.activo is true "
				+ " and Postulacion.persona.idPersona = :idPersona");
		q.setParameter("idGrupo", idGrupo);
		q.setParameter("idPersona", idPersona);
		lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	private Postulacion findPostulacionOnline(Long idPersona) {
		List<Postulacion> lista = null;
		Query q =
			em.createQuery("select Postulacion from Postulacion Postulacion "
				+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
				+ " and Postulacion.activo is true "
				+ " and Postulacion.personaPostulante.persona.idPersona = :idPersona");
		q.setParameter("idGrupo", idGrupo);
		q.setParameter("idPersona", idPersona);
		lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	public void limpiarDatosPersonMatch() {
		personMatch = null;
		msjModalPersonMatch = null;
	}

	private List<DTO508> findPostulacion2(Long idGrupo, Long idPersona) {
		limpiarDatosPersonMatch();
		List<DTO508> lista = new ArrayList<DTO508>();
		int index = -1;
		Postulacion postulacion = null;
		postulacion = findPostulacionCarpeta(idGrupo, idPersona);
		if (postulacion == null) {
			postulacion = findPostulacionOnline(idPersona);
		}
		// Buscar por postulación
		if (postulacion != null) {
			Boolean canceloPostulacion =
				(postulacion.getUsuCancelacion() != null && postulacion.getFechaCancelacion() != null);
			if ((postulacion.getTipo() == null || postulacion.getTipo().isEmpty())
				&& !canceloPostulacion) {
				lista.add(new DTO508());
				index = lista.size() - 1;
				lista.get(index).setPersona(postulacion.getPersonaPostulante().getPersona());
				statusMessages.add(Severity.INFO, "Persona postulada en forma on-line a este Concurso");
				return lista;
			} else if ((postulacion.getTipo() == null || postulacion.getTipo().isEmpty())
				&& canceloPostulacion) {
				lista.add(new DTO508());
				index = lista.size() - 1;
				lista.get(index).setPersona(postulacion.getPersonaPostulante().getPersona());
				lista.get(index).setHabLinkVer(true);
				// lista.get(index).setHabLinkEliminar(true);
				lista.get(index).setHabLinkPostular(true);
				lista.get(index).setHabLinkDatosDetallados(true);
				return lista;
			} else if ((postulacion.getTipo().equals("CARPETA")) && !canceloPostulacion) {
				lista.add(new DTO508());
				index = lista.size() - 1;
				lista.get(index).setPersona(postulacion.getPersona());
				lista.get(index).setHabLinkVer(true);
				lista.get(index).setHabLinkDatosDetallados(true);
				return lista;
			} else if ((postulacion.getTipo().equals("CARPETA")) && canceloPostulacion) {
				lista.add(new DTO508());
				index = lista.size() - 1;
				lista.get(index).setPersona(postulacion.getPersona());
				statusMessages.add(Severity.ERROR, "Postulación vía carpeta cancelada");
				return lista;
			}
		}
		// Buscar por usuario
		Query q =
			em.createQuery("select Usuario from Usuario Usuario where Usuario.persona.idPersona =:idPersona and Usuario.activo is true and Usuario.usuAlta = 'PORTAL'");
		q.setParameter("idPersona", idPersona);

		List<Usuario> lUsuario = q.getResultList();
		Usuario usuario = null;
		if (lUsuario.size() > 0) {
			usuario = lUsuario.get(0);
		}
		if (usuario == null) {
			statusMessages.add(Severity.ERROR, "La Persona no existe como usuario. Verificar con la SFP");
			return null;
		}
		if (usuario.getTipo() == null || usuario.getTipo().isEmpty()) {
			if (paisExpedi.getDescripcion().equals("PARAGUAY")) {
				lista.add(new DTO508());
				index = lista.size() - 1;
				lista.get(index).setPersona(usuario.getPersona());
				lista.get(index).setHabLinkVer(true);
				// lista.get(index).setHabLinkEliminar(true);
				lista.get(index).setHabLinkPostular(true);
				lista.get(index).setHabLinkDatosDetallados(true);
			} else {
				msjModalPersonMatch =
					usuario.getPersona().getNombres() + " " + usuario.getPersona().getApellidos()
						+ ", ¿Estos datos coinciden con los de la carpeta? ";
				personMatch = usuario.getPersona();
				return null;
			}
		} else if (usuario.getTipo().equalsIgnoreCase("CARPETA")) {
			lista.add(new DTO508());
			index = lista.size() - 1;
			lista.get(index).setPersona(usuario.getPersona());
			lista.get(index).setHabLinkVer(true);
			// lista.get(index).setHabLinkEliminar(true);
			lista.get(index).setHabLinkPostular(true);
			lista.get(index).setHabLinkDatosDetallados(true);
		}
		return lista;
	}

	private List<DTO508> findUsuario(Long idGrupo, String notIn) {
		List<DTO508> lista = null;
		int index = -1;
		String elWhere = "";
		if (notIn != null && !notIn.trim().isEmpty()) {
			elWhere = " and Usuario.persona.idPersona not in (" + notIn + ") ";
		}
		Query q =
			em.createQuery("select Usuario.persona from Usuario Usuario "
				+ " where Usuario.activo is true and Usuario.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo"
				+ " and Usuario.tipo ='CARPETA'" + " and Usuario.activo is true " + elWhere);

		q.setParameter("idGrupo", idGrupo);
		List<Persona> lPersonas = q.getResultList();
		for (Persona o : lPersonas) {
			lista.add(new DTO508());
			index = lista.size() - 1;
			lista.get(index).setPersona(o);
			lista.get(index).setHabLinkVer(true);
			lista.get(index).setHabLinkDatosDetallados(true);
			lista.get(index).setHabLinkPostular(true);
		}
		return lista;
	}

	private List<DTO508> findPostulacion(Long idGrupo, Persona persona) {
		List<DTO508> lista = new ArrayList<DTO508>();
		int index = -1;
		String elWhere = " ";
		String elFrom = " ";
		if (ci != null && !ci.isEmpty() && persona != null) {
			elFrom = " LEFT JOIN FETCH Postulacion.personaPostulante ";
			elWhere +=
				" and (Postulacion.personaPostulante.persona.idPersona = :idPersona or Postulacion.persona.idPersona = :idPersona ) ";
		}
		if (nombre != null && !nombre.isEmpty()) {
			elWhere +=
				" and (upper(Postulacion.personaPostulante.persona.nombres) like :nombres or upper(Postulacion.persona.nombres) like :nombres ) ";
		}
		if (apellido != null && !apellido.isEmpty()) {
			elWhere +=
				" and (upper(Postulacion.personaPostulante.persona.apellidos) like :apellidos or upper(Postulacion.persona.apellidos) like :apellidos ) ";
		}
		Query q =
			em.createQuery("select Postulacion from Postulacion Postulacion " + elFrom
				+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
				+ " and Postulacion.tipo = 'CARPETA' and Postulacion.activo is true " + elWhere);
		q.setParameter("idGrupo", idGrupo);
		if (ci != null && !ci.isEmpty() && persona != null) {
			q.setParameter("idPersona", persona.getIdPersona());
		}
		if (nombre != null && !nombre.isEmpty()) {
			q.setParameter("nombres", "%" + nombre.toUpperCase() + "%");
		}
		if (apellido != null && !apellido.isEmpty()) {
			q.setParameter("apellidos", "%" + apellido.toUpperCase() + "%");
		}
		List<Postulacion> lPostulacion = q.getResultList();
		for (Postulacion o : lPostulacion) {
			// Si el Postulante NO CANCELO la postulación:
			if (o.getUsuCancelacion() == null && o.getFechaCancelacion() == null) {
				if (o.getPersonaPostulante() == null) {
					lista.add(new DTO508());
					index = lista.size() - 1;
					lista.get(index).setPersona(o.getPersona());
				} else {
					lista.add(new DTO508());
					index = lista.size() - 1;
					lista.get(index).setPersona(o.getPersonaPostulante().getPersona());
					lista.get(index).setHabLinkIngresarObs(true);
				}
				lista.get(index).setHabLinkVer(true);
				lista.get(index).setHabLinkDatosDetallados(true);
			} else if (o.getUsuCancelacion() != null && o.getFechaCancelacion() != null) {
				// Si el Postulante CANCELO la postulación:
				if (o.getPersonaPostulante() == null) {
					lista.add(new DTO508());
					index = lista.size() - 1;
					lista.get(index).setPersona(o.getPersona());
				} else {
					lista.add(new DTO508());
					index = lista.size() - 1;
					lista.get(index).setPersona(o.getPersonaPostulante().getPersona());
					lista.get(index).setHabLinkIngresarObs(true);
				}
				lista.get(index).setHabLinkVer(true);
				lista.get(index).setEstado("Postulación cancelada");
			}
		}
		return lista;

	}

	public void search13() {
		if (lista != null)
			lista.clear();
		if (ci == null || (ci.trim().isEmpty())) {
			lista = findPostulacion(grupo.getIdConcursoPuestoAgr(), null);
			return;
		}
		PersonaDTO personaDTO =
			seleccionUtilFormController.buscarPersona(ci, paisExpedi.getDescripcion());
		if (personaDTO.getHabilitarBtn() == null) {
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else if (personaDTO.getHabilitarBtn()) {
			statusMessages.add(Severity.ERROR, "Persona no encontrada en esta postulación");
		} else {
			/* BUSCAR en tabla POSTULACION */
			lista = findPostulacion(grupo.getIdConcursoPuestoAgr(), personaDTO.getPersona());
		}
	}

	public void siAddPersonMatch() {
		if (lista == null) {
			lista = new ArrayList<DTO508>();
		} else
			lista.clear();
		lista.add(new DTO508());
		int index = lista.size() - 1;
		lista.get(index).setPersona(personMatch);
		lista.get(index).setHabLinkVer(true);
		// lista.get(index).setHabLinkEliminar(true);
		lista.get(index).setHabLinkPostular(true);
		lista.get(index).setHabLinkDatosDetallados(true);
		limpiarDatosPersonMatch();
	}

	public void noAddPersonMatch() {
		if (lista == null) {
			lista = new ArrayList<DTO508>();
		} else
			lista.clear();
		enviarMailNotifSFP(personMatch, grupo.getConcurso());
		lista.add(new DTO508());
		int index = lista.size() - 1;
		lista.get(index).setPersona(personMatch);
		lista.get(index).setHabLinkVer(true);
		lista.get(index).setHabLinkPostular(true);
		lista.get(index).setHabLinkDatosDetallados(true);
		limpiarDatosPersonMatch();
	}

	public void limpiar() {
		ci = null;
		nombre = null;
		apellido = null;
		idPaisEpeDoc = null;
		habilitarBtnNewPerson = false;
		idPersonaAPostular = null;
		mostrarModalConfirmacion = false;
		idPersonaModalObs = null;
		mensajeConfirmPostula = null;
		if (lista != null)
			lista.clear();

	}

	public String getUrlCU513() {
		String url =
			"/cargarCarpeta/admPersona/AdmPersonaPorCarpeta.xhtml?documentoIdentidad=" + ci
				+ "&idPaisExpedicionDoc=" + idPaisEpeDoc;
		ci = null;
		habilitarBtnNewPerson = false;
		return url;
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

	public Long getIdPaisEpeDoc() {
		return idPaisEpeDoc;
	}

	public void setIdPaisEpeDoc(Long idPaisEpeDoc) {
		this.idPaisEpeDoc = idPaisEpeDoc;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public String getCodActividad() {
		return codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	public List<DTO508> getLista() {
		return lista;
	}

	public void setLista(List<DTO508> lista) {
		this.lista = lista;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public Boolean getHabilitarBtnNewPerson() {
		return habilitarBtnNewPerson;
	}

	public void setHabilitarBtnNewPerson(Boolean habilitarBtnNewPerson) {
		this.habilitarBtnNewPerson = habilitarBtnNewPerson;
	}

	public ConcursoPuestoAgr getGrupo() {
		return grupo;
	}

	public void setGrupo(ConcursoPuestoAgr grupo) {
		this.grupo = grupo;
	}

	public Boolean getMostrarModalConfirmacion() {
		return mostrarModalConfirmacion;
	}

	public void setMostrarModalConfirmacion(Boolean mostrarModalConfirmacion) {
		this.mostrarModalConfirmacion = mostrarModalConfirmacion;
	}

	public Long getIdPersonaAPostular() {
		return idPersonaAPostular;
	}

	public void setIdPersonaAPostular(Long idPersonaAPostular) {
		this.idPersonaAPostular = idPersonaAPostular;
	}

	public String getMensajeConfirmPostula() {
		return mensajeConfirmPostula;
	}

	public void setMensajeConfirmPostula(String mensajeConfirmPostula) {
		this.mensajeConfirmPostula = mensajeConfirmPostula;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Boolean getIsActividad13() {
		return isActividad13;
	}

	public void setIsActividad13(Boolean isActividad13) {
		this.isActividad13 = isActividad13;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public Boolean getHabBtnDisminuirP() {
		return habBtnDisminuirP;
	}

	public void setHabBtnDisminuirP(Boolean habBtnDisminuirP) {
		this.habBtnDisminuirP = habBtnDisminuirP;
	}

	public String getObsActividad() {
		return obsActividad;
	}

	public void setObsActividad(String obsActividad) {
		this.obsActividad = obsActividad;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getMensajeModalDismiPue() {
		return mensajeModalDismiPue;
	}

	public void setMensajeModalDismiPue(String mensajeModalDismiPue) {
		this.mensajeModalDismiPue = mensajeModalDismiPue;
	}

	public String getViewDisminuirPuesto() {
		return viewDisminuirPuesto;
	}

	public void setViewDisminuirPuesto(String viewDisminuirPuesto) {
		this.viewDisminuirPuesto = viewDisminuirPuesto;
	}

	public String getMsjModalPersonDisca() {
		return msjModalPersonDisca;
	}

	public void setMsjModalPersonDisca(String msjModalPersonDisca) {
		this.msjModalPersonDisca = msjModalPersonDisca;
	}

	public Boolean getPasarSgteActividad() {
		return pasarSgteActividad;
	}

	public void setPasarSgteActividad(Boolean pasarSgteActividad) {
		this.pasarSgteActividad = pasarSgteActividad;
	}

	public String getObsModal() {
		return obsModal;
	}

	public void setObsModal(String obsModal) {
		this.obsModal = obsModal;
	}

	public Long getIdPersonaModalObs() {
		return idPersonaModalObs;
	}

	public void setIdPersonaModalObs(Long idPersonaModalObs) {
		this.idPersonaModalObs = idPersonaModalObs;
	}

	public String getMsjModalPersonMatch() {
		return msjModalPersonMatch;
	}

	public void setMsjModalPersonMatch(String msjModalPersonMatch) {
		this.msjModalPersonMatch = msjModalPersonMatch;
	}

	public boolean isCsi() {
		return csi;
	}

	public void setCsi(boolean csi) {
		this.csi = csi;
	}

}
