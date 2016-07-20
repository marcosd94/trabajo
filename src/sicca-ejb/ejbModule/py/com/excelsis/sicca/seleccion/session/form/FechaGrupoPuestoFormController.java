package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.ActividadEnum;
import py.com.excelsis.sicca.entity.CalendarioOeeCab;
import py.com.excelsis.sicca.entity.CalendarioSfp;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.MovUsuariosTarea;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.CronogramaConcCabHome;
import py.com.excelsis.sicca.seleccion.session.FechasGrupoPuestoHome;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("fechaGrupoPuestoFormController")
public class FechaGrupoPuestoFormController {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	FechasGrupoPuestoHome fechasGrupoPuestoHome;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false, create = true)
	private Actor actor;
	private String TIPO_PUBLICACION = "PUBLICACION";
	private String TIPO_RECEPCION = "RECEPCION";
	private String TIPO_VIGENCIA = "VIGENCIA";
	private String TIPO_PUBLICACION_PREV = "PUBLICACION_CONCURSO_PREV";

	private Boolean entidadManejada;
	private Date fPublicacionPrev;
	private Date fSugeridaPublicacion;
	private Date fSugeridaRecepcion;
	private Date fcalVigenciaDesde;
	private Date fcalVigenciaHasta;
	private String direccionFisica;

	private String observacion;
	private Date fcalVigenciaHoraDesde;
	private Date fcalVigenciaHoraHasta;
	Long idConfUo = null;
	private String titulo = null;
	private Boolean grupoPuesto = false;
	private String concurso = null;
	SimpleDateFormat sdfHORA = new SimpleDateFormat("HH:mm");
	private Boolean modoConcurso = false;
	private Map<String, Date> diccFechasAnteriores = new HashMap<String, Date>();
	private Integer idEstadoFirmadoHomologa = null;
	private Integer idEstadoFinalizadaCarga = null;
	private SeguridadUtilFormController seguridadUtilFormController;
	private Boolean modo425 = false;
	private String tipo;

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (tipo != null && tipo.trim().equalsIgnoreCase("CIO"))
			seguridadUtilFormController.verificarPerteneceOeeCIO(
					null,
					grupoPuestosController.getIdConcursoPuestoAgr(),
					seguridadUtilFormController.estadoActividades(
							"ESTADOS_GRUPO", "A PRORROGAR") + "",
					ActividadEnum.CIO_PRORROGAR_POSTULACION.getValor());
		else
			seguridadUtilFormController.verificarPerteneceOee(
					null,
					grupoPuestosController.getIdConcursoPuestoAgr(),
					seguridadUtilFormController.estadoActividades(
							"ESTADOS_GRUPO", "A PRORROGAR") + "",
					ActividadEnum.PRORROGAR_POSTULACION.getValor());
	}

	private void validarOee(Concurso Concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		seguridadUtilFormController
				.verificarPerteneceOee(grupoPuestosController
						.getConcursoPuestoAgr().getConcurso()
						.getConfiguracionUoCab().getIdConfiguracionUo());
	}

	/**
	 * Crea un diccionario con los valores anteriores de ciertos campos
	 * modificables por el cu 425
	 */
	private void initDiccionarioAnterior() {
		if (grupoPuestosController.getIdConcursoPuestoAgr() != null) {
			FechasGrupoPuesto fgp = traerActual(fechasGrupoPuestoHome
					.getInstance().getIdFechas());
			diccFechasAnteriores.clear();
			if (fgp != null) {
				diccFechasAnteriores.put("FECHA_RECEPCION_DESDE",
						fgp.getFechaRecepcionDesde());
				diccFechasAnteriores.put("FECHA_RECEPCION_HASTA",
						fgp.getFechaRecepcionHasta());
				diccFechasAnteriores.put("HORA_RECEPCION_DESDE",
						fgp.getHoraRecepcionDesde());
				diccFechasAnteriores.put("HORA_RECEPCION_HASTA",
						fgp.getHoraRecepcionHasta());
				diccFechasAnteriores.put("FECHA_VIGPROC_DESDE",
						fgp.getFechaVigProcDesde());
				diccFechasAnteriores.put("FECHA_VIGPROC_HASTA",
						fgp.getFechaVigProcHasta());
				diccFechasAnteriores.put("HORA_VIGPROC_DESDE",
						fgp.getHoraVigProcDesde());
				diccFechasAnteriores.put("HORA_VIGPROC_HASTA",
						fgp.getHoraVigProcHasta());
				diccFechasAnteriores.put("FECHA_PUB_DESDE",
						fgp.getFechaPublicacionDesde());
				diccFechasAnteriores.put("FECHA_PUB_HASTA",
						fgp.getFechaPublicacionHasta());
				diccFechasAnteriores.put("HORA_PUB_DESDE",
						fgp.getHoraPublicacionDesde());
				diccFechasAnteriores.put("HORA_PUB_HASTA",
						fgp.getHoraPublicacionHasta());

			}
		}
	}

	/**
	 * Inicializa el idConfUo y el titulo
	 */
	private void initIdCongfUo() {
		sdfHORA.setLenient(false);

		if (!modoConcurso) {
			idConfUo = grupoPuestosController.getConcursoPuestoAgr()
					.getConcurso().getConfiguracionUoCab()
					.getIdConfiguracionUo();
			titulo = "Grupo Puesto";
			grupoPuesto = true;
			concurso = grupoPuestosController.getConcursoPuestoAgr()
					.getConcurso().getNombre();
		} else {
			idConfUo = grupoPuestosController.getConcursoPuestoAgr()
					.getConcurso().getConfiguracionUoCab()
					.getIdConfiguracionUo();
			titulo = "Concurso";
			grupoPuesto = false;
			concurso = grupoPuestosController.getConcursoPuestoAgr()
					.getConcurso().getNombre();
		}
	}

	private void initDirteccionFisica() {
		Calendar cal = Calendar.getInstance();
		String separador = File.separator;
		FechasGrupoPuesto laInstancia = fechasGrupoPuestoHome.getInstance();
		if (laInstancia != null) {
			ConcursoPuestoAgr grupo = em
					.find(ConcursoPuestoAgr.class, laInstancia
							.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			direccionFisica = separador
					+ "SICCA"
					+ separador
					+ cal.get(Calendar.YEAR)
					+ separador
					+ "OEE"
					+ separador
					+ grupo.getConcurso().getDatosEspecificosTipoConc()
							.getIdDatosEspecificos() + separador
					+ grupo.getConcurso().getIdConcurso() + separador
					+ grupo.getIdConcursoPuestoAgr();
		}

	}

	private Boolean verificarFechaHabil(Boolean verifDiaHabilFechaPub) {
		FechasGrupoPuesto laEntidad = fechasGrupoPuestoHome.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		System.out.println(laEntidad.getFechaPublicacionDesde());
		if (verifDiaHabilFechaPub) {
			if (!seleccionUtilFormController.fechaTrabajaOee(
					laEntidad.getFechaPublicacionDesde(), idConfUo)) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU269_excepNoEsDiaHabil")
						+ " "
						+ sdf.format(laEntidad.getFechaPublicacionDesde())
						+ " (Fecha de Publicación Desde)");
				return false;
			}
			if (!seleccionUtilFormController.fechaTrabajaOee(
					laEntidad.getFechaPublicacionHasta(), idConfUo)) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU269_excepNoEsDiaHabil")
						+ " "
						+ sdf.format(laEntidad.getFechaPublicacionHasta())
						+ " (Fecha de Publicación Hasta)");
				return false;
			}
		}

		if (!seleccionUtilFormController.fechaTrabajaOee(
				laEntidad.getFechaRecepcionDesde(), idConfUo)) {
			statusMessages.add(
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString(
							"CU269_excepNoEsDiaHabil")
							+ " "
							+ sdf.format(laEntidad.getFechaRecepcionDesde())
							+ " (Fecha de Recepción Desde)");
			return false;
		}
		if (!seleccionUtilFormController.fechaTrabajaOee(
				laEntidad.getFechaRecepcionHasta(), idConfUo)) {
			statusMessages.add(
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString(
							"CU269_excepNoEsDiaHabil")
							+ " "
							+ sdf.format(laEntidad.getFechaRecepcionHasta())
							+ " (Fecha de Recepción Hasta)");
			return false;
		}
		return true;

	}

	private FechasGrupoPuesto traerActual(Long elId) {
		Query q = em.createNativeQuery(
				"SELECT * FROM seleccion.fechas_grupo_puesto WHERE id_fechas = "
						+ elId, FechasGrupoPuesto.class);
		List<FechasGrupoPuesto> lista = q.getResultList();
		if (lista.size() == 1) {
			return lista.get(0);
		}
		return null;
	}

	private Boolean precond425(FechasGrupoPuesto fGPNuevo) {
		try {
			if (!precondiciones(false)) {
				return false;
			} else {
				if (observacion != null && !observacion.trim().isEmpty()) {
					if (observacion.length() > 250) {
						statusMessages
								.add(Severity.ERROR,
										"Longitud máxima para el campo Observación: 250 caracteres.");
						return false;
					}
				}
				Calendar calNuevo = Calendar.getInstance();
				Calendar calActual = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"dd/MM/yyyy HH:mm:ss");
				SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
				SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
				calNuevo.setTime(sdf.parse(sdfFecha.format(fGPNuevo
						.getFechaRecepcionDesde())
						+ " "
						+ sdfHora.format(fGPNuevo.getHoraRecepcionDesde())));
				calActual.setTime(sdf.parse(sdfFecha
						.format(diccFechasAnteriores
								.get("FECHA_RECEPCION_HASTA"))
						+ " "
						+ sdfHora.format(diccFechasAnteriores
								.get("HORA_RECEPCION_HASTA"))));
				System.out.println("Actual " + sdf.format(calActual.getTime())
						+ " , NUEVO: " + sdf.format(calNuevo.getTime()));
				if (calNuevo.getTime().getTime() <= calActual.getTime()
						.getTime()) {
					statusMessages.add(
							Severity.ERROR,
							SeamResourceBundle.getBundle().getString(
									"CU425_errFechaNuevaMenorFechaVieja")
									+ " "
									+ sdf.format(calNuevo.getTime())
									+ " es menor o igual a "
									+ sdf.format(calActual.getTime()));
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static Boolean validarFecha(Date laFecha) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		sdf.setLenient(false);
		try {
			sdf.parse(sdf.format(laFecha));
			Calendar cal = Calendar.getInstance();
			cal.setTime(laFecha);
			if ((cal.get(Calendar.YEAR) + "").length() < 4) {
				throw new Exception("");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private Boolean precondiciones(Boolean verifDiaHabilFechaPub) {

		if (grupoPuestosController.getIdConcursoPuestoAgr() == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU269_reqIdPuesto"));
			return false;
		}
		if (fechasGrupoPuestoHome.getInstance().getFechaPublicacionDesde() == null
				|| fechasGrupoPuestoHome.getInstance()
						.getFechaPublicacionHasta() == null
				|| fechasGrupoPuestoHome.getInstance()
						.getHoraPublicacionDesde() == null
				|| fechasGrupoPuestoHome.getInstance()
						.getHoraPublicacionHasta() == null
				|| fechasGrupoPuestoHome.getInstance().getFechaRecepcionDesde() == null
				|| fechasGrupoPuestoHome.getInstance().getFechaRecepcionHasta() == null
				|| fechasGrupoPuestoHome.getInstance().getHoraRecepcionDesde() == null
				|| fechasGrupoPuestoHome.getInstance().getHoraRecepcionHasta() == null
				|| fechasGrupoPuestoHome.getInstance().getFechaVigProcDesde() == null
				|| fechasGrupoPuestoHome.getInstance().getFechaVigProcHasta() == null
				|| fechasGrupoPuestoHome.getInstance().getHoraVigProcDesde() == null
				|| fechasGrupoPuestoHome.getInstance().getHoraVigProcHasta() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe cargar todos los campos requeridos. Verifique.");
			return false;

		}
		if (!(validarFecha(fechasGrupoPuestoHome.getInstance()
				.getFechaPublicacionDesde())
				&& validarFecha(fechasGrupoPuestoHome.getInstance()
						.getFechaPublicacionHasta())
				&& validarFecha(fechasGrupoPuestoHome.getInstance()
						.getFechaRecepcionDesde()) && validarFecha(fechasGrupoPuestoHome
				.getInstance().getFechaRecepcionHasta()))) {
			statusMessages.add(Severity.ERROR,
					"El formato de alguna fecha no es correcto. Verifique");
			return false;
		}
		if (!modo425 && !restriFechaDesdePub()) {
			return false;
		}
		if (!modo425
				&& fechasGrupoPuestoHome.getInstance().getFechaRecepcionDesde()
						.getTime() < fechasGrupoPuestoHome.getInstance()
						.getFechaPublicacionDesde().getTime()) {
			statusMessages
					.add(Severity.ERROR,
							"La Fecha de Recepción Desde no puede ser menor a la Fecha de Publicación Desde. Verifique");
			return false;
		}

		if (!modo425 && fSugeridaPublicacion == null) {
			fSugeridaPublicacion = sugerenciaDate(fechasGrupoPuestoHome
					.getInstance().getFechaPublicacionDesde(), TIPO_PUBLICACION);
		}
		if (fSugeridaRecepcion == null) {
			fSugeridaRecepcion = sugerenciaDate(fechasGrupoPuestoHome
					.getInstance().getFechaPublicacionDesde(), TIPO_RECEPCION);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (!modo425
				&& fechasGrupoPuestoHome.getInstance()
						.getFechaPublicacionHasta()
						.compareTo(fSugeridaPublicacion) == -1) {
			statusMessages.add(
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString(
							"CU269_excepFechaSugerida")
							+ " (Fecha Publicación Hasta sugerida:"
							+ sdf.format(fSugeridaPublicacion) + " )");
			return false;
		}
		if (fechasGrupoPuestoHome.getInstance().getFechaRecepcionHasta()
				.compareTo(fSugeridaRecepcion) == -1) {
			statusMessages.add(
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString(
							"CU269_excepFechaSugerida")
							+ " (Fecha Recepción Hasta sugerida: "
							+ sdf.format(fSugeridaRecepcion) + ")");
			return false;
		}
		/* Verificar fecha desde menor a fecha hasta */
		try {
			FechasGrupoPuesto fgp = fechasGrupoPuestoHome.getInstance();
			if (!modo425
					&& !Utiles.valFechasPrecedencia(
							fgp.getFechaPublicacionDesde(),
							fgp.getHoraPublicacionDesde(),
							fgp.getFechaPublicacionHasta(),
							fgp.getHoraPublicacionHasta())) {
				statusMessages
						.add(Severity.ERROR,
								"La Fecha de Publicación Desde debe ser menor a la Fecha de Publicación Hasta");
				return false;
			}
			if (!modo425
					&& !Utiles.valFechasPrecedencia(
							fgp.getFechaRecepcionDesde(),
							fgp.getHoraRecepcionDesde(),
							fgp.getFechaRecepcionHasta(),
							fgp.getHoraRecepcionHasta())) {
				statusMessages
						.add(Severity.ERROR,
								"La Fecha de Recepción Desde debe ser menor a la Fecha de Recepción Hasta");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if (!verificarFechaHabil(verifDiaHabilFechaPub)) {
			return false;
		}
		return true;
	}

	private Date sacarHora(Date laFecha) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String laFechaString = sdf.format(laFecha);
		try {
			return sdf.parse(laFechaString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Boolean restriFechaDesdePub() {
		Calendar cal = Calendar.getInstance();
		if (fechasGrupoPuestoHome.getInstance().getFechaPublicacionDesde() == null) {
			return false;
		}
		fPublicacionPrev = sugerenciaDate(new Date(), TIPO_PUBLICACION_PREV);
		if (fPublicacionPrev == null) {
			return false;
		}
		fPublicacionPrev = fechaHabilOee(fPublicacionPrev);
		if (fPublicacionPrev == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU269_excepNoFechaHabil"));
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		System.out.println("DESDE: "
				+ sdf.format(fechasGrupoPuestoHome.getInstance()
						.getFechaPublicacionDesde()));
		Date fecha1 = sacarHora(fechasGrupoPuestoHome.getInstance()
				.getFechaPublicacionDesde());
		Date fecha2 = sacarHora(fPublicacionPrev);
		if (fecha1.compareTo(fecha2) == -1) {
			statusMessages.add(
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString(
							"CU269_excepFechaDesdePub")
							+ sdf.format(fPublicacionPrev.getTime()));
			return false;
		}
		return true;
	}

	private FechasGrupoPuesto calcularFecVigenciaProceso(
			FechasGrupoPuesto laInstancia) {

		fcalVigenciaDesde = calFechaDesdeVigencia(
				laInstancia.getFechaRecepcionHasta(), false);
		fcalVigenciaHasta = sugerenciaDate(fcalVigenciaDesde, TIPO_VIGENCIA);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		System.out.println("Conf-Uo: " + idConfUo);
		System.out.println("Vigencia Habil Desde: "
				+ sdf.format(fcalVigenciaDesde));
		System.out.println("Vigencia Habil Hasta: "
				+ sdf.format(fcalVigenciaHasta));
		calcHoraDesdeyHoraHasta();
		laInstancia.setFechaVigProcDesde(fcalVigenciaDesde);
		laInstancia.setFechaVigProcHasta(fcalVigenciaHasta);
		laInstancia.setHoraVigProcDesde(fcalVigenciaHoraDesde);
		laInstancia.setHoraVigProcHasta(fcalVigenciaHoraHasta);
		return laInstancia;
	}

	public String update() {
		if (precondiciones(true)) {
			try {
				
				Concurso concursoEntity = grupoPuestosController.getConcursoPuestoAgr()
						.getConcurso();
				for(ConcursoPuestoAgr concursopuestoagr : concursoEntity.getConcursoPuestoAgrs()){//AGREGADO RV
					FechasGrupoPuesto laInstancia = fechasGrupoPuestoHome
							.getInstance();
					laInstancia = calcularFecVigenciaProceso(laInstancia);
					
					em.createNativeQuery("update seleccion.fechas_grupo_puesto set "
							+ "fecha_publicacion_desde= ?"
							+ ", fecha_publicacion_hasta= ?"
							+ ", fecha_recepcion_desde= ?"
							+ ", fecha_recepcion_hasta= ?"
							+ ", fecha_vig_proc_desde= ?"
							+ ", fecha_vig_proc_hasta= ?"
							+ ", hora_publicacion_desde= ?"
							+ ", hora_publicacion_hasta= ?"
							+ ", hora_recepcion_desde= ?"
							+ ", hora_recepcion_hasta= ?"
							+ ", hora_vig_proc_desde= ?"
							+ ", hora_vig_proc_hasta= ?"
							+ ", usu_mod= ?"
							+ ", fecha_mod= ?"
							+ " where id_concurso_puesto_agr= ?;")
							.setParameter(1, laInstancia.getFechaPublicacionDesde(), TemporalType.DATE)
							.setParameter(2, laInstancia.getFechaPublicacionHasta(), TemporalType.DATE)
							.setParameter(3, laInstancia.getFechaRecepcionDesde(), TemporalType.DATE)
							.setParameter(4, laInstancia.getFechaRecepcionHasta(), TemporalType.DATE)
							.setParameter(5, laInstancia.getFechaVigProcDesde(), TemporalType.DATE)
							.setParameter(6, laInstancia.getFechaVigProcHasta(), TemporalType.DATE)
							.setParameter(7, laInstancia.getHoraPublicacionDesde(), TemporalType.TIME)
							.setParameter(8, laInstancia.getHoraPublicacionHasta(), TemporalType.TIME)
							.setParameter(9, laInstancia.getHoraRecepcionDesde(), TemporalType.TIME)
							.setParameter(10, laInstancia.getHoraRecepcionHasta(), TemporalType.TIME)
							.setParameter(11, laInstancia.getHoraVigProcDesde(), TemporalType.TIME)
							.setParameter(12, laInstancia.getHoraVigProcHasta(), TemporalType.TIME)
							.setParameter(13, usuarioLogueado.getCodigoUsuario())
							.setParameter(14, new Date(), TemporalType.DATE)
							.setParameter(15, concursopuestoagr.getIdConcursoPuestoAgr()).executeUpdate();
					
					em.createQuery("update ConcursoPuestoAgr c set "
							+ "c.fechaLimite=:fecha"
							+ " where c.idConcursoPuestoAgr=:idGrupo")
							.setParameter("fecha", laInstancia.getFechaRecepcionHasta(), TemporalType.DATE)
							.setParameter("idGrupo", concursopuestoagr.getIdConcursoPuestoAgr()).executeUpdate();

//					if (modoConcurso) {
//						throw new Exception("CURSO INCORRECTO");
//					} else {
//						laInstancia.setConcursoPuestoAgr(new ConcursoPuestoAgr());
//						laInstancia.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
//								concursopuestoagr.getIdConcursoPuestoAgr());
//						laInstancia.setConcurso(null);
//					}
//					laInstancia = calcularFecVigenciaProceso(laInstancia);
//					em.merge(laInstancia);
				}
				em.flush();
				
				fechasGrupoPuestoHome.setInstance(new FechasGrupoPuesto());
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));

				return "persisted";

			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.add(Severity.ERROR, ex.getMessage());
				return null;
			}
		}
		return null;

	}

	private Boolean precondSgteTarea() {
		return true;
	}

	public String nextTask() throws ParseException {
		// Controles particulares
		if (!precondSgteTarea()) {
			return "FAIL";
		}
		ReferenciasUtilFormController referenciasUtilFormController = (ReferenciasUtilFormController) Component
				.getInstance(ReferenciasUtilFormController.class, true);
		SeleccionUtilFormController seleccionUtilFormController = (SeleccionUtilFormController) Component
				.getInstance(SeleccionUtilFormController.class, true);

		FechasGrupoPuesto laInstancia = fechasGrupoPuestoHome.getInstance();
		ConcursoPuestoAgr cpg = em.find(ConcursoPuestoAgr.class, laInstancia
				.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		System.out.println(laInstancia.getIdFechas());
		System.out.println(cpg.getConcurso());
		System.out.println(cpg.getIdProcessInstance());
		laInstancia.setEstado("A VERIFICAR");
		/* Actualiza el estado del Grupo */
		laInstancia.setConcursoPuestoAgr(cpg);
		laInstancia.getConcursoPuestoAgr().setEstado(
				referenciasUtilFormController
						.getReferencia("ESTADOS_GRUPO", "PUBLICADO")
						.getValorNum().intValue());
		laInstancia.getConcursoPuestoAgr().setIdProcessInstance(
				idIdTaskProcLongFromGrupo(laInstancia.getConcursoPuestoAgr()
						.getIdConcursoPuestoAgr()));
		em.merge(laInstancia.getConcursoPuestoAgr());
		/**/
		EstadoDet estadoDet = seleccionUtilFormController.buscarEstadoDet(
				"CONCURSO", "PUBLICADO");
		/* Actualiza el ESTADO de los PUESTOS */
		List<ConcursoPuestoDet> lista = seleccionUtilFormController
				.getConcursoPuestoDet(laInstancia.getConcursoPuestoAgr()
						.getIdConcursoPuestoAgr());
		Iterator<ConcursoPuestoDet> iter = lista.iterator();
		while (iter.hasNext()) {
			ConcursoPuestoDet concursoPuestoDet = iter.next();
			concursoPuestoDet.setEstadoDet(estadoDet);
			concursoPuestoDet.setFechaMod(new Date());
			concursoPuestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			concursoPuestoDet.getPlantaCargoDet().setEstadoDet(estadoDet);
			concursoPuestoDet.getPlantaCargoDet().setUsuMod(
					usuarioLogueado.getCodigoUsuario());
			concursoPuestoDet.getPlantaCargoDet().setFechaMod(new Date());
			concursoPuestoDet.setPlantaCargoDet(em.merge(concursoPuestoDet
					.getPlantaCargoDet()));
			concursoPuestoDet = em.merge(concursoPuestoDet);
		}
		/**/
		laInstancia = em.merge(laInstancia);
		fechasGrupoPuestoHome.setInstance(laInstancia);

		// Se pasa a la siguiente tarea
		jbpmUtilFormController.setConcursoPuestoAgr(grupoPuestosController
				.getConcursoPuestoAgr());
		jbpmUtilFormController
				.setActividadActual(ActividadEnum.PRORROGAR_POSTULACION);
		jbpmUtilFormController
				.setActividadSiguiente(ActividadEnum.RECIBIR_POSTULACIONES);
		// Asignar Actor para el jBPM
		actor.setId(usuarioLogueado.getCodigoUsuario());
		Long idConcurso = idConcurosoFromGrupo(laInstancia
				.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		/*insertarPublicacionPortal(
				laInstancia.getConcursoPuestoAgr().getIdConcursoPuestoAgr(),
				idConcurso,
				genTextoPublicacion(laInstancia.getFechaRecepcionDesde(),
						laInstancia.getHoraRecepcionDesde(),
						laInstancia.getFechaRecepcionHasta(),
						laInstancia.getHoraRecepcionHasta()));*/
		if (jbpmUtilFormController.nextTask()) {
			em.flush();
			return "nextTask";
		}
		return null;
	}

	private Long idIdTaskProcLongFromGrupo(Long idGrupo) {
		Query q = em
				.createNativeQuery("select id_process_instance from seleccion.concurso_puesto_agr cgp where cgp.id_concurso_puesto_agr = "
						+ idGrupo);
		List lista = q.getResultList();
		if (lista.size() > 0) {
			return ((java.math.BigInteger) lista.get(0)).longValue();

		}
		return null;
	}

	private Long idConcurosoFromGrupo(Long idGrupo) {
		Query q = em
				.createNativeQuery("select id_concurso from seleccion.concurso_puesto_agr cgp where cgp.id_concurso_puesto_agr = "
						+ idGrupo);
		List lista = q.getResultList();
		if (lista.size() > 0) {
			return ((java.math.BigInteger) lista.get(0)).longValue();

		}
		return null;
	}

	private String genTextoPublicacion(Date fechaDesde, Date horaDesde,
			Date fechaHasta, Date horaHasta) throws ParseException {
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date fechaDesdeHora = null;
		Date fechaHastaHora = null;
		fechaDesdeHora = sdf.parse(sdfFecha.format(fechaDesde) + " "
				+ sdfHora.format(horaDesde));
		fechaHastaHora = sdf.parse(sdfFecha.format(fechaHasta) + " "
				+ sdfHora.format(horaHasta));
		StringBuffer texto = new StringBuffer();
		String h1O = "<h1>";
		String h1C = "</h1>";

		texto.append(h1O
				+ "Se informa que la nueva fecha para el Plazo de Recepción de Postulaciones es la siguiente: "
				+ h1C);
		texto.append(h1O + "Desde el " + sdf.format(fechaDesdeHora)
				+ " hasta el " + sdf.format(fechaHastaHora) + h1C + ".");

		return texto.toString();
	}

	private void insertarPublicacionPortal(Long idConcursoPuestoAgr,
			Long idConcurso, String texto) {
		PublicacionPortal entity = new PublicacionPortal();
		entity.setFechaAlta(new Date());
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		entity.setActivo(true);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
				idConcursoPuestoAgr);
		entity.setConcurso(new Concurso());
		entity.getConcurso().setIdConcurso(idConcurso);
		entity.setTexto(texto);
		em.persist(entity);

	}

	public String update425() {
		FechasGrupoPuesto fGPNuevo = fechasGrupoPuestoHome.getInstance();

		if (precond425(fGPNuevo)) {
			try {
				Date fechaMod = new Date();
				FechasGrupoPuesto laInstancia = fechasGrupoPuestoHome
						.getInstance();
				laInstancia.setFechaMod(fechaMod);
				laInstancia.setUsuMod(usuarioLogueado.getCodigoUsuario());
				laInstancia.setConcursoPuestoAgr(new ConcursoPuestoAgr());
				laInstancia.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
						grupoPuestosController.getIdConcursoPuestoAgr());
				laInstancia = calcularFecVigenciaProceso(laInstancia);
				laInstancia.setObservacion(observacion);
				laInstancia.setFechaRecepcionDesdeAnt(diccFechasAnteriores
						.get("FECHA_RECEPCION_DESDE"));
				laInstancia.setFechaRecepcionHastaAnt(diccFechasAnteriores
						.get("FECHA_RECEPCION_HASTA"));
				laInstancia.setHoraRecepcionDesdeAnt(diccFechasAnteriores
						.get("HORA_RECEPCION_DESDE"));
				laInstancia.setHoraRecepcionHastaAnt(diccFechasAnteriores
						.get("HORA_RECEPCION_HASTA"));
				laInstancia.setFechaVigHastaAnt(diccFechasAnteriores
						.get("FECHA_VIGPROC_DESDE"));
				laInstancia.setHoraVigProcHastaAnt(diccFechasAnteriores
						.get("HORA_VIGPROC_HASTA"));
				laInstancia.setFechaVigProcDesdeAnt(diccFechasAnteriores
						.get("FECHA_VIGPROC_DESDE"));
				laInstancia.setHoraVigProcDesdeAnt(diccFechasAnteriores
						.get("HORA_VIGPROC_DESDE"));
				/* Incidencia 0001385 */
				// Parte uno
				laInstancia.setFechaPublicacionDesdeAnt(diccFechasAnteriores
						.get("FECHA_PUB_DESDE"));
				laInstancia.setHoraPublicacionDesdeAnt(diccFechasAnteriores
						.get("HORA_PUB_DESDE"));
				laInstancia.setFechaPublicacionHastaAnt(diccFechasAnteriores
						.get("FECHA_PUB_HASTA"));
				laInstancia.setHoraPublicacionHastaAnt(diccFechasAnteriores
						.get("HORA_PUB_HASTA"));
				// Parte dos
				laInstancia.setFechaPublicacionDesde(new Date());
				laInstancia.setHoraPublicacionDesde(new Date());
				laInstancia.setFechaPublicacionHasta(laInstancia
						.getFechaRecepcionHasta());
				laInstancia.setHoraPublicacionHasta(laInstancia
						.getHoraRecepcionHasta());
				/**/

				laInstancia = em.merge(laInstancia);
				fechasGrupoPuestoHome.setInstance(laInstancia);

				return nextTask();

			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_NO_MSG"));
				return null;
			}
		}
		return null;

	}

	public void sugerenciaDate(String tipo, Boolean mostrarErrores) {
		if (tipo.equalsIgnoreCase(TIPO_PUBLICACION)) {

			if (restriFechaDesdePub()) {
				fechasGrupoPuestoHome.getInstance().setFechaPublicacionHasta(
						sugerenciaDate(fechasGrupoPuestoHome.getInstance()
								.getFechaPublicacionDesde(), TIPO_PUBLICACION));
			} else {
				fechasGrupoPuestoHome.getInstance().setFechaPublicacionHasta(
						null);
			}
		} else if (tipo.equalsIgnoreCase(TIPO_RECEPCION)) {
			/* La recepcion */
			if (fechasGrupoPuestoHome.getInstance().getFechaRecepcionDesde() != null) {
				fechasGrupoPuestoHome.getInstance().setFechaRecepcionHasta(
						sugerenciaDate(fechasGrupoPuestoHome.getInstance()
								.getFechaRecepcionDesde(), TIPO_RECEPCION));
				fechasGrupoPuestoHome.getInstance().setFechaVigProcDesde(
						calFechaDesdeVigencia(fechasGrupoPuestoHome
								.getInstance().getFechaRecepcionHasta(),
								mostrarErrores));
				fechasGrupoPuestoHome.getInstance().setFechaVigProcHasta(
						sugerenciaDate(fechasGrupoPuestoHome.getInstance()
								.getFechaVigProcDesde(), TIPO_VIGENCIA));
				fcalVigenciaDesde = fechasGrupoPuestoHome.getInstance()
						.getFechaVigProcDesde();
				fcalVigenciaHasta = fechasGrupoPuestoHome.getInstance()
						.getFechaVigProcHasta();
				calcHoraDesdeyHoraHasta();
				fechasGrupoPuestoHome.getInstance().setHoraVigProcDesde(
						fcalVigenciaHoraDesde);
				fechasGrupoPuestoHome.getInstance().setHoraVigProcHasta(
						fcalVigenciaHoraHasta);
			}

		} else if (tipo.equalsIgnoreCase(TIPO_VIGENCIA)) {
			/* La Vigencia */
			// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			// System.out.println(sdf.format(fcalVigenciaDesde));
			// System.out.println(sdf.format(fcalVigenciaHasta));
			// System.out.println("## "+fechasGrupoPuestoHome.getInstance().getFechaRecepcionHasta());
			fechasGrupoPuestoHome.getInstance().setFechaVigProcDesde(
					calFechaDesdeVigencia(fechasGrupoPuestoHome.getInstance()
							.getFechaRecepcionHasta(), mostrarErrores));
			fechasGrupoPuestoHome.getInstance().setFechaVigProcHasta(
					sugerenciaDate(fechasGrupoPuestoHome.getInstance()
							.getFechaVigProcDesde(), TIPO_VIGENCIA));
			fcalVigenciaDesde = fechasGrupoPuestoHome.getInstance()
					.getFechaVigProcDesde();
			fcalVigenciaHasta = fechasGrupoPuestoHome.getInstance()
					.getFechaVigProcHasta();
			calcHoraDesdeyHoraHasta();
			// System.out.println(sdf.format(fcalVigenciaDesde));
			// System.out.println(sdf.format(fcalVigenciaHasta));
			fechasGrupoPuestoHome.getInstance().setHoraVigProcDesde(
					fcalVigenciaHoraDesde);
			fechasGrupoPuestoHome.getInstance().setHoraVigProcHasta(
					fcalVigenciaHoraHasta);

		}

	}

	public Date sugerenciaDate(Date fechaDesde, String tipo) {
		if (fechaDesde == null) {
			return null;
		}
		SimpleDateFormat sdff = new SimpleDateFormat("dd/MM/yyyy");
		System.out.println(sdff.format(fechaDesde));
		Date respuesta = null;
		String abrev = "";
		if (tipo.equalsIgnoreCase(TIPO_RECEPCION)) {
			abrev = "'RECEPCION_POST_LINEA'";
		} else if (tipo.equalsIgnoreCase(TIPO_PUBLICACION)) {
			abrev = "'PUBLICACION_CONCURSO'";
		} else if (tipo.equalsIgnoreCase(TIPO_VIGENCIA)) {
			abrev = "'VIGENCIA_PROCESO_SELECCION'";
		} else if (tipo.equalsIgnoreCase(TIPO_PUBLICACION_PREV)) {
			abrev = "'PUBLICACION_CONCURSO_PREV'";
		}
		StringBuffer SQL = new StringBuffer();
		SQL.append(" SELECT ");
		SQL.append("     REF");
		SQL.append(" FROM ");
		SQL.append("     Referencias REF ");
		SQL.append(" WHERE ");
		SQL.append("     REF.dominio = 'PLAZO' ");
		SQL.append(" AND REF.descAbrev = " + abrev);
		SQL.append(" AND REF.activo = TRUE ");
		Query q = em.createQuery(SQL.toString());
		List<Referencias> ref = q.getResultList();
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaDesde);
		if (ref.size() == 1 && ref.get(0).getValorNum() != null) {
			cal.add(Calendar.DAY_OF_MONTH, ref.get(0).getValorNum().intValue());
			System.out.println(sdff.format(cal.getTime()));
			if (tipo.equalsIgnoreCase(TIPO_RECEPCION)) {
				fSugeridaRecepcion = fechaHabilOee(cal.getTime());
				respuesta = fSugeridaRecepcion;
			} else if (tipo.equalsIgnoreCase(TIPO_PUBLICACION)) {
				fSugeridaPublicacion = fechaHabilOee(cal.getTime());
				respuesta = fSugeridaPublicacion;
			} else if (tipo.equalsIgnoreCase(TIPO_VIGENCIA)) {
				fcalVigenciaHasta = fechaHabilOee(cal.getTime());
				respuesta = fcalVigenciaHasta;
			} else if (tipo.equalsIgnoreCase(TIPO_PUBLICACION_PREV)) {
				fPublicacionPrev = fechaHabilOee(cal.getTime());
				respuesta = fPublicacionPrev;
			}
		} else {
			if (tipo.equalsIgnoreCase(TIPO_RECEPCION)) {
				respuesta = fSugeridaRecepcion = null;
			} else if (tipo.equalsIgnoreCase(TIPO_PUBLICACION)) {
				respuesta = fSugeridaPublicacion = null;
			} else if (tipo.equalsIgnoreCase(TIPO_VIGENCIA)) {
				respuesta = fcalVigenciaHasta = null;
			} else if (tipo.equalsIgnoreCase(TIPO_PUBLICACION_PREV)) {
				respuesta = fPublicacionPrev = null;
			}
		}
		System.out.println(sdff.format(respuesta));
		return respuesta;

	}

	private void setIdEstadosFirmadoFinalizado() {
		// Id Estado Firmado Homologacion
		Query q = null;
		q = em.createQuery("select Referencias from Referencias Referencias "
				+ "where Referencias.activo is true "
				+ "and Referencias.dominio = 'ESTADOS_GRUPO' and Referencias.descAbrev ='FIRMADO HOMOLOGACION'");
		List<Referencias> lista = q.getResultList();
		if (lista.size() > 0) {
			idEstadoFirmadoHomologa = lista.get(0).getValorNum();
		}
		// Id Estado Finalizada Carga
		q = em.createQuery("select Referencias from Referencias Referencias "
				+ "where Referencias.activo is true "
				+ "and Referencias.dominio = 'ESTADOS_GRUPO' and Referencias.descAbrev ='FINALIZADO CARGA'");
		lista = q.getResultList();
		if (lista.size() > 0) {
			idEstadoFinalizadaCarga = lista.get(0).getValorNum();
		}
	}

	/**
	 * Trae un listado de los grupos que pertenecen al concurso pasado como
	 * parametro
	 * 
	 * @param idConcurso
	 * @return
	 */
	private List<ConcursoPuestoAgr> getGrupoPorConcurso(Long idConcurso) {
		List<ConcursoPuestoAgr> lista;
		/*
		 * Sólo si uno de los estados esta cargado en DB se prodrá proceder con
		 * normalidad el resto del código
		 */
		String listaIn = idEstadoFinalizadaCarga != null ? idEstadoFinalizadaCarga
				.toString() : "";
		listaIn = listaIn
				+ ","
				+ (idEstadoFirmadoHomologa != null ? idEstadoFirmadoHomologa
						.toString() : "");
		if (listaIn.startsWith(",")) {
			listaIn = listaIn.replaceFirst(",", "");
		}
		if (!listaIn.trim().isEmpty()) {
			Query q = em
					.createQuery("select ConcursoPuestoAgr from ConcursoPuestoAgr ConcursoPuestoAgr "
							+ " where ConcursoPuestoAgr.activo is true and ConcursoPuestoAgr.concurso.idConcurso = "
							+ idConcurso
							+ " and (ConcursoPuestoAgr.estado in ("
							+ listaIn
							+ "))");
			lista = q.getResultList();
			Iterator<ConcursoPuestoAgr> iter = lista.iterator();
			// Limpiando, sacar todo lo que este finalizado pero no haya sido
			// homologado
			while (iter.hasNext()) {
				ConcursoPuestoAgr entidad = iter.next();
				if (entidad.getEstado().toString()
						.equalsIgnoreCase(idEstadoFinalizadaCarga.toString())) {
					if (!entidad.getHomologar()) {
						iter.remove();
					}
				}
			}
			return lista;
		}
		return new ArrayList<ConcursoPuestoAgr>();

	}

	public String save() {
		if (precondiciones(true)) {
			try {
				Date fechaAlta = new Date();
				if (modoConcurso) {
					throw new Exception("CURSO INCORRECTO");
				} else {
					Concurso concursoEntity = grupoPuestosController.getConcursoPuestoAgr()
							.getConcurso();
					
					for(ConcursoPuestoAgr concursopuestoagr : concursoEntity.getConcursoPuestoAgrs()){//AGREGADO RV

						FechasGrupoPuesto laInstancia = fechasGrupoPuestoHome
							.getInstance();
						laInstancia = calcularFecVigenciaProceso(laInstancia);
						String a = "A VERIFICAR";
						
						em.createNativeQuery("insert into seleccion.fechas_grupo_puesto ("
								+ "activo, "
								+ "id_concurso_puesto_agr, "
								+ "fecha_alta, "
								+ "fecha_publicacion_desde, "
								+ "fecha_publicacion_hasta, "
								+ "fecha_recepcion_desde, "
								+ "fecha_recepcion_hasta, "
								+ "fecha_vig_proc_desde, "
								+ "fecha_vig_proc_hasta, "
								+ "hora_publicacion_desde, "
								+ "hora_publicacion_hasta, "
								+ "hora_recepcion_desde, "
								+ "hora_recepcion_hasta, "
								+ "hora_vig_proc_desde, "
								+ "hora_vig_proc_hasta, "
								+ "estado,"
								+ "usu_alta) "
								+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
								.setParameter(1, true)
								.setParameter(2, concursopuestoagr.getIdConcursoPuestoAgr())
								.setParameter(3, fechaAlta, TemporalType.DATE)
								.setParameter(4, laInstancia.getFechaPublicacionDesde(), TemporalType.DATE)
								.setParameter(5, laInstancia.getFechaPublicacionHasta(), TemporalType.DATE)
								.setParameter(6, laInstancia.getFechaRecepcionDesde(), TemporalType.DATE)
								.setParameter(7, laInstancia.getFechaRecepcionHasta(), TemporalType.DATE)
								.setParameter(8, laInstancia.getFechaVigProcDesde(), TemporalType.DATE)
								.setParameter(9, laInstancia.getFechaVigProcHasta(), TemporalType.DATE)
								.setParameter(10, laInstancia.getHoraPublicacionDesde(), TemporalType.TIME)
								.setParameter(11, laInstancia.getHoraPublicacionHasta(), TemporalType.TIME)
								.setParameter(12, laInstancia.getHoraRecepcionDesde(), TemporalType.TIME)
								.setParameter(13, laInstancia.getHoraRecepcionHasta(), TemporalType.TIME)
								.setParameter(14, laInstancia.getHoraVigProcDesde(), TemporalType.TIME)
								.setParameter(15, laInstancia.getHoraVigProcHasta(), TemporalType.TIME)
								.setParameter(16, a)
								.setParameter(17, usuarioLogueado.getCodigoUsuario()).executeUpdate();
								
						em.createQuery("update ConcursoPuestoAgr c set "
								+ "c.fechaLimite=:fecha"
								+ " where c.idConcursoPuestoAgr=:idGrupo")
								.setParameter("fecha",  laInstancia.getFechaRecepcionHasta(),TemporalType.DATE)
								.setParameter("idGrupo", concursopuestoagr.getIdConcursoPuestoAgr()).executeUpdate();
					}	
				}

				em.flush();
				fechasGrupoPuestoHome.setInstance(new FechasGrupoPuesto());
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				return "persisted";

			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.add(Severity.ERROR, ex.getMessage());
				return null;
			}
		}
		return null;

	}

	private Date traerHora(CalendarioOeeCab elCalOee, CalendarioSfp elCalSfp,
			Calendar fechaVigencia, String tipo) {
		Integer dia = fechaVigencia.get(Calendar.DAY_OF_WEEK);

		if (elCalOee != null) {
			if (dia == 1) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalOee.getDomHoraDesde();
				} else {
					return elCalOee.getDomHoraHasta();
				}
			}
			if (dia == 2) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalOee.getLunHoraDesde();
				} else {
					return elCalOee.getLunHoraHasta();
				}
			}
			if (dia == 3) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalOee.getMarHoraDesde();
				} else {
					return elCalOee.getMarHoraHasta();
				}
			}
			if (dia == 4) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalOee.getMieHoraDesde();
				} else {
					return elCalOee.getMieHoraHasta();
				}
			}
			if (dia == 5) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalOee.getJueHoraDesde();
				} else {
					return elCalOee.getJueHoraHasta();
				}
			}
			if (dia == 6) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalOee.getVieHoraDesde();
				} else {
					return elCalOee.getVieHoraHasta();
				}
			}
			if (dia == 7) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalOee.getSabHoraDesde();
				} else {
					return elCalOee.getSabHoraHasta();
				}
			}
		} else if (elCalSfp != null) {
			if (dia == 1) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalSfp.getDomHoraDesde();
				} else {
					return elCalSfp.getDomHoraHasta();
				}
			}
			if (dia == 2) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalSfp.getLunHoraDesde();
				} else {
					return elCalSfp.getLunHoraHasta();
				}
			}
			if (dia == 3) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalSfp.getMarHoraDesde();
				} else {
					return elCalSfp.getMarHoraHasta();
				}
			}
			if (dia == 4) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalSfp.getMieHoraDesde();
				} else {
					return elCalSfp.getMieHoraHasta();
				}
			}
			if (dia == 5) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalSfp.getJueHoraDesde();
				} else {
					return elCalSfp.getJueHoraHasta();
				}
			}
			if (dia == 6) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalSfp.getVieHoraDesde();
				} else {
					return elCalSfp.getVieHoraHasta();
				}
			}
			if (dia == 7) {
				if (tipo.equalsIgnoreCase("DESDE")) {
					return elCalSfp.getSabHoraDesde();
				} else {
					return elCalSfp.getSabHoraHasta();
				}
			}
		}
		return null;
	}

	private void calcHoraDesdeyHoraHasta() {
		fcalVigenciaHoraDesde = null;
		;
		fcalVigenciaHoraHasta = null;
		if (fcalVigenciaDesde == null || fcalVigenciaHasta == null) {
			return;
		}
		Calendar calVigenciaDesde = Calendar.getInstance();
		Calendar calVigenciaHasta = Calendar.getInstance();
		calVigenciaDesde.setTime(fcalVigenciaDesde);
		calVigenciaHasta.setTime(fcalVigenciaHasta);
		Integer anhoVigenciaDesde = calVigenciaDesde.get(Calendar.YEAR);
		Integer anhoVigenciaHasta = calVigenciaHasta.get(Calendar.YEAR);
		Query q = em
				.createQuery("select calendarioOeeCab from CalendarioOeeCab calendarioOeeCab "
						+ "where calendarioOeeCab.anho = "
						+ anhoVigenciaDesde
						+ "and  calendarioOeeCab.configuracionUoCab.idConfiguracionUo = "
						+ idConfUo);
		List<CalendarioOeeCab> lCalOee = q.getResultList();
		if (lCalOee.size() == 1) {
			Calendar calFcalVigenciaDesde = Calendar.getInstance();
			calFcalVigenciaDesde.setTime(fcalVigenciaDesde);
			Calendar calFcalVigenciaHasta = Calendar.getInstance();
			calFcalVigenciaHasta.setTime(fcalVigenciaHasta);
			fcalVigenciaHoraDesde = traerHora(lCalOee.get(0), null,
					calFcalVigenciaDesde, "DESDE");
			fcalVigenciaHoraHasta = traerHora(lCalOee.get(0), null,
					calFcalVigenciaHasta, "HASTA");

		} else {
			q = em.createQuery("select calendarioSfp from CalendarioSfp calendarioSfp "
					+ "where calendarioSfp.anho = " + anhoVigenciaDesde);
			List<CalendarioSfp> lCalSfp = q.getResultList();
			if (lCalSfp.size() == 1) {
				Calendar calFcalVigenciaDesde = Calendar.getInstance();
				calFcalVigenciaDesde.setTime(fcalVigenciaDesde);
				Calendar calFcalVigenciaHasta = Calendar.getInstance();
				calFcalVigenciaHasta.setTime(fcalVigenciaHasta);
				fcalVigenciaHoraDesde = traerHora(null, lCalSfp.get(0),
						calFcalVigenciaDesde, "DESDE");
				fcalVigenciaHoraHasta = traerHora(null, lCalSfp.get(0),
						calFcalVigenciaHasta, "HASTA");
			}

		}

	}

	private Date fechaHabilOee(Date laFecha) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Integer tope = 365;
		Integer cuenta = 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(laFecha);
		Boolean stop = false;

		while (cuenta.intValue() < tope.intValue() && !stop) {
			stop = seleccionUtilFormController.fechaTrabajaOee(cal.getTime(),
					idConfUo);
			System.out
					.println("LA FECHA PROBADA: " + sdf.format(cal.getTime()));
			if (stop) {
				break;
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
			cuenta++;
		}
		if (cuenta.intValue() == tope.intValue() && !stop) {
			return null;
		}
		System.out.println("LA FECHA RETORNADA: " + sdf.format(cal.getTime()));
		return cal.getTime();

	}

	private Date calFechaDesdeVigencia(Date fechaRecepcionHastaActual,
			Boolean mostrarErrores) {
		Integer tope = 365;
		Integer cuenta = 0;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (fSugeridaRecepcion == null) {
			fSugeridaRecepcion = sugerenciaDate(fechasGrupoPuestoHome
					.getInstance().getFechaPublicacionDesde(), TIPO_RECEPCION);
		}
		System.out.println(sdf.format(fSugeridaRecepcion));
		if (fechaRecepcionHastaActual.getTime() < fSugeridaRecepcion.getTime()) {
			if (mostrarErrores) {
				statusMessages.add(Severity.ERROR,
						"La fecha introducida es menor a la fecha sugerida: "
								+ sdf.format(fechaRecepcionHastaActual) + " < "
								+ sdf.format(fSugeridaRecepcion));
			}
			return null;
		}
		if (fechasGrupoPuestoHome.getInstance().getFechaRecepcionHasta()
				.getTime() > fSugeridaRecepcion.getTime()) {
			fSugeridaRecepcion = fechasGrupoPuestoHome.getInstance()
					.getFechaRecepcionHasta();
		}
		cal.setTime(fSugeridaRecepcion);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Boolean stop = false;
		while (cuenta.intValue() < tope.intValue() && !stop) {
			stop = seleccionUtilFormController.fechaTrabajaOee(cal.getTime(),
					idConfUo);

			if (stop) {
				break;
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
			cuenta++;
		}
		if ((cuenta.intValue() == tope.intValue()) && !stop) {
			return null;
		}
		return cal.getTime();
	}

	/**
	 * Método que inicializa los datos para el Editado del 425
	 */
	public void initEdit425() {
		modo425 = true;
		if (grupoPuestosController != null) {
			Query q = null;
			List<FechasGrupoPuesto> lista = null;
			validarOee();
			q = em.createQuery("select FechasGrupoPuesto from FechasGrupoPuesto FechasGrupoPuesto "
					+ " where FechasGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = "
					+ grupoPuestosController.getIdConcursoPuestoAgr());
			lista = q.getResultList();
			if (lista != null && lista.size() > 0) {
				FechasGrupoPuesto laInstancia = fechasGrupoPuestoHome
						.getInstance();
				laInstancia = lista.get(0);
				fechasGrupoPuestoHome.setInstance(laInstancia);
				entidadManejada = true;
				fSugeridaRecepcion = fechasGrupoPuestoHome.getInstance()
						.getFechaRecepcionHasta();
				observacion = fechasGrupoPuestoHome.getInstance()
						.getObservacion();
			}
			initDiccionarioAnterior();
			initIdCongfUo();
			initDirteccionFisica();
		}

	}

	/**
	 * Método que inicializa los datos para el Editado
	 */
	public void initEdit() {
		setIdEstadosFirmadoFinalizado();
		if (grupoPuestosController != null) {
			validarOee(grupoPuestosController.getConcursoPuestoAgr()
					.getConcurso());
			Query q = null;
			List<FechasGrupoPuesto> lista = null;
			if (modoConcurso) {
				q = em.createQuery("select FechasGrupoPuesto from FechasGrupoPuesto FechasGrupoPuesto where FechasGrupoPuesto.concurso.idConcurso = "
						+ grupoPuestosController.getConcursoPuestoAgr()
								.getConcurso().getIdConcurso());
			} else {
				q = em.createQuery("select FechasGrupoPuesto from FechasGrupoPuesto FechasGrupoPuesto "
						+ " where FechasGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());
			}
			lista = q.getResultList();
			if (lista != null && lista.size() > 0) {
				FechasGrupoPuesto laInstancia = fechasGrupoPuestoHome
						.getInstance();
				laInstancia = lista.get(0);
				fechasGrupoPuestoHome.setInstance(laInstancia);
				entidadManejada = true;
			} else {
				if (fechasGrupoPuestoHome != null)
					fechasGrupoPuestoHome.setInstance(null);
				entidadManejada = false;
			}
			initIdCongfUo();
		}

	}

	public Boolean getEntidadManejada() {
		return entidadManejada;
	}

	public void setEntidadManejada(Boolean entidadManejada) {
		this.entidadManejada = entidadManejada;
	}

	public String getTIPO_PUBLICACION() {
		return TIPO_PUBLICACION;
	}

	public void setTIPO_PUBLICACION(String tIPO_PUBLICACION) {
		TIPO_PUBLICACION = tIPO_PUBLICACION;
	}

	public String getTIPO_RECEPCION() {
		return TIPO_RECEPCION;
	}

	public void setTIPO_RECEPCION(String tIPO_RECEPCION) {
		TIPO_RECEPCION = tIPO_RECEPCION;
	}

	public String getTIPO_VIGENCIA() {
		return TIPO_VIGENCIA;
	}

	public void setTIPO_VIGENCIA(String tIPO_VIGENCIA) {
		TIPO_VIGENCIA = tIPO_VIGENCIA;
	}

	public String getTIPO_PUBLICACION_PREV() {
		return TIPO_PUBLICACION_PREV;
	}

	public void setTIPO_PUBLICACION_PREV(String tIPO_PUBLICACION_PREV) {
		TIPO_PUBLICACION_PREV = tIPO_PUBLICACION_PREV;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Boolean getGrupoPuesto() {
		return grupoPuesto;
	}

	public void setGrupoPuesto(Boolean grupoPuesto) {
		this.grupoPuesto = grupoPuesto;
	}

	public String getConcurso() {
		return concurso;
	}

	public void setConcurso(String concurso) {
		this.concurso = concurso;
	}

	public String getHoraPublicacionDesde() {

		if (fechasGrupoPuestoHome.getInstance().getHoraPublicacionDesde() != null) {
			return sdfHORA.format(fechasGrupoPuestoHome.getInstance()
					.getHoraPublicacionDesde());
		}

		return null;
	}

	public void setHoraPublicacionDesde(String horaPublicacionDesde) {

		try {
			if (horaPublicacionDesde != null
					&& !horaPublicacionDesde.trim().isEmpty())
				fechasGrupoPuestoHome.getInstance().setHoraPublicacionDesde(
						sdfHORA.parse(horaPublicacionDesde));
		} catch (ParseException e) {
			fechasGrupoPuestoHome.getInstance().setHoraPublicacionDesde(null);
			e.printStackTrace();
		}

	}

	public String getHoraPublicacionHasta() {

		if (fechasGrupoPuestoHome.getInstance().getHoraPublicacionHasta() != null) {
			return sdfHORA.format(fechasGrupoPuestoHome.getInstance()
					.getHoraPublicacionHasta());
		}

		return null;
	}

	public void setHoraPublicacionHasta(String horaPublicacionHasta) {
		try {

			fechasGrupoPuestoHome.getInstance().setHoraPublicacionHasta(
					sdfHORA.parse(horaPublicacionHasta));
		} catch (ParseException e) {
			fechasGrupoPuestoHome.getInstance().setHoraPublicacionHasta(null);
			e.printStackTrace();
		}
	}

	public String getHoraRecepcionDesde() {
		if (fechasGrupoPuestoHome.getInstance().getHoraRecepcionDesde() != null) {
			return sdfHORA.format(fechasGrupoPuestoHome.getInstance()
					.getHoraRecepcionDesde());
		}
		return null;
	}

	public void setHoraRecepcionDesde(String horaRecepcionDesde) {
		try {
			fechasGrupoPuestoHome.getInstance().setHoraRecepcionDesde(
					sdfHORA.parse(horaRecepcionDesde));
		} catch (ParseException e) {
			fechasGrupoPuestoHome.getInstance().setHoraRecepcionDesde(null);
			e.printStackTrace();
		}
	}

	public String getHoraRecepcionHasta() {
		if (fechasGrupoPuestoHome.getInstance().getHoraRecepcionHasta() != null) {
			return sdfHORA.format(fechasGrupoPuestoHome.getInstance()
					.getHoraRecepcionHasta());
		}
		return null;
	}

	public void setHoraRecepcionHasta(String horaRecepcionHasta) {
		try {
			fechasGrupoPuestoHome.getInstance().setHoraRecepcionHasta(
					sdfHORA.parse(horaRecepcionHasta));
		} catch (ParseException e) {
			fechasGrupoPuestoHome.getInstance().setHoraRecepcionHasta(null);
			e.printStackTrace();
		}
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Boolean getModoConcurso() {
		return modoConcurso;
	}

	public void setModoConcurso(Boolean modoConcurso) {
		this.modoConcurso = modoConcurso;
	}

	public String getDireccionFisica() {
		if (direccionFisica == null || direccionFisica.trim().isEmpty()) {
			initDirteccionFisica();
		}
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public FechasGrupoPuestoHome getFechasGrupoPuestoHome() {
		return fechasGrupoPuestoHome;
	}

	public void setFechasGrupoPuestoHome(
			FechasGrupoPuestoHome fechasGrupoPuestoHome) {
		this.fechasGrupoPuestoHome = fechasGrupoPuestoHome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
