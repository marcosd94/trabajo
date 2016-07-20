package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.drools.lang.DRLParser.neg_operator_key_return;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.ActividadEnum;
import enums.ProcesoEnum;

import py.com.excelsis.sicca.dto.AutorizadoSFPDTO;
import py.com.excelsis.sicca.dto.GruposPublicacionDTO;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.FechasGrupo;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.JbpmTaskinstance;
import py.com.excelsis.sicca.entity.MatrizRefConfDet;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PublicacionConcurso;
import py.com.excelsis.sicca.entity.PublicacionConcursoCab;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.PublicacionConcursoHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;

@Scope(ScopeType.CONVERSATION)
@Name("publicacionConcursoListFormController")
public class PublicacionConcursoListFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	PublicacionConcursoHome publicacionConcursoHome;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;
	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();

	private ConcursoPuestoAgr concursoPuestoAgr;
	private Concurso concurso;
	public Observacion observacion;
	private String obs;
	private String direccionFisica;
	private Boolean habilitarBoton;

	private List<GruposPublicacionDTO> listaGruposDTO = new ArrayList<GruposPublicacionDTO>();
	private List<AutorizadoSFPDTO> listaAutorizacionSFP = new ArrayList<AutorizadoSFPDTO>();
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

//		seguridadUtilFormController.verificarPerteneceOee(//MODIFICADO RV
//				null,
//				concursoPuestoAgr.getIdConcursoPuestoAgr(),
//				seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO",
//						"MODIFICADO DATOS") + "",
//				ActividadEnum.SOLICITAR_PUBLICACION.getValor());
	}

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		habilitarBoton = false;
		obs = null;
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
		listaGruposDTO = new ArrayList<GruposPublicacionDTO>();
		if (concurso != null) {
			validarOee();
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null)
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}
		// buscarApublicar();
		buscarSolicitados();
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String separador = File.separator;
		direccionFisica = separador
				+ "SICCA"
				+ separador
				+ anho
				+ separador
				+ "OEE"
				+ separador
				+ configuracionUoCab.getIdConfiguracionUo()
				+ separador
				+ concurso.getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + separador
				+ concurso.getIdConcurso() + separador
				+ concursoPuestoAgrHome.getInstance().getIdConcursoPuestoAgr();
		habilitarBoton = verificarValidez();
	}

	// @SuppressWarnings("unchecked")
	// private void buscarApublicar() {
	// String sql =
	// "select fechas.*  from seleccion.fechas_grupo_puesto fechas "
	// + "join seleccion.concurso_puesto_agr agr "
	// + "on agr.id_concurso_puesto_agr = fechas.id_concurso_puesto_agr "
	// + "join seleccion.concurso concurso "
	// + "on concurso.id_concurso = agr.id_concurso " + "where agr.estado = "
	// + "(select ref.valor_num from seleccion.referencias ref " + "WHERE "
	// + "ref.dominio = 'ESTADOS_GRUPO' " + "AND " +
	// "ref.desc_abrev = 'A PUBLICAR') "
	// + " and fechas.activo is true " + "and concurso.id_concurso = "
	// + concurso.getIdConcurso();
	//
	// List<FechasGrupoPuesto> lista = new ArrayList<FechasGrupoPuesto>();
	// lista = em.createNativeQuery(sql,
	// FechasGrupoPuesto.class).getResultList();
	// for (FechasGrupoPuesto f : lista) {
	// GruposPublicacionDTO dto = new GruposPublicacionDTO();
	// dto.setFechasGrupoPuesto(f);
	// dto.setId(f.getIdFechas());
	// dto.setSeleccionado(true);
	//
	// listaGruposDTO.add(dto);
	// }
	// }

	@SuppressWarnings("unchecked")
	private void buscarSolicitados() {
		String sql = "select fechas.*  from seleccion.fechas_grupo fechas "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = fechas.id_concurso_puesto_agr "
				+ "join seleccion.concurso concurso "
				+ "on concurso.id_concurso = agr.id_concurso "
				+ "where agr.estado = "
				+ "(select ref.valor_num from seleccion.referencias ref "
				+ "WHERE " + "ref.dominio = 'ESTADOS_GRUPO' " + "AND "
				+ "ref.desc_abrev = 'MODIFICADO DATOS') "
				+ "and concurso.id_concurso = " + concurso.getIdConcurso();

		List<FechasGrupo> lista = new ArrayList<FechasGrupo>();
		lista = em.createNativeQuery(sql, FechasGrupo.class).getResultList();
		for (FechasGrupo f : lista) {
			GruposPublicacionDTO dto = new GruposPublicacionDTO();
			dto.setFechasGrupo(f);
			dto.setId(f.getIdFechas());
			dto.setSeleccionado(false);
			dto.setEstado("MODIFICADO DATOS");
			listaGruposDTO.add(dto);
		}
	}

	@SuppressWarnings("unchecked")
	private Integer buscarEstadoGrupo(String valor) {
		String cad = "select ref.* from seleccion.referencias ref  "
				+ "WHERE ref.dominio = 'ESTADOS_GRUPO'  "
				+ "AND ref.desc_abrev = '" + valor + "'";
		List<Referencias> l = new ArrayList<Referencias>();
		l = em.createNativeQuery(cad, Referencias.class).getResultList();
		if (l.size() > 0)
			return l.get(0).getValorNum();
		else
			return null;
	}

	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {

		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}

	@SuppressWarnings("unchecked")
	private Observacion verificarObs(Long id) {
		String cad = "select obs.* from seleccion.observacion obs "
				+ "where obs.id_task_instance = " + id;
		List<Observacion> l = new ArrayList<Observacion>();
		l = em.createNativeQuery(cad, Observacion.class).getResultList();
		if (l.size() > 0)
			return l.get(0);
		else
			return null;

	}

	@SuppressWarnings("unchecked")
	private PublicacionConcurso buscarPublicacionConcurso(ConcursoPuestoAgr x) {
		String cad = "select publ.* "
				+ "from seleccion.publicacion_concurso publ "
				+ "where publ.id_concurso = " + x.getConcurso().getIdConcurso();
		List<PublicacionConcurso> listaPubl = new ArrayList<PublicacionConcurso>();
		listaPubl = em.createNativeQuery(cad, PublicacionConcurso.class)
				.getResultList();
		if (listaPubl.size() > 0)
			return listaPubl.get(0);
		return null;
	}

	private Boolean verificarValidez() {
		Integer estado = buscarEstadoGrupo("A PUBLICAR");
		for (GruposPublicacionDTO f : listaGruposDTO) {
			Integer est = f.getFechasGrupo().getConcursoPuestoAgr().getEstado();
			if (!est.equals(estado))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public String validarAutorizacionSfp() {

		Integer estado = buscarEstadoGrupo("A PUBLICAR");

		String sql = "SELECT GRUPO.* "
				+ "FROM SELECCION.CONCURSO_PUESTO_AGR GRUPO "
				+ "WHERE GRUPO.ACTIVO = TRUE "
				+ "AND GRUPO.ESTADO IN (SELECT REF.VALOR_NUM "
				+ "FROM SELECCION.REFERENCIAS REF "
				+ "WHERE REF.DOMINIO = 'ESTADOS_GRUPO' "
				+ "AND DESC_LARGA IN ('FINALIZADO CARGA', "
				+ "'ENVIADO A HOMOLOGACION', " + "'INICIADO CIRCUITO', "
				+ "'HOMOLOGADO', " + "'CON DOCUMENTOS DE HOMOLOGACION', "
				+ "'PENDIENTE REVISION', "
				+ "'FIRMADO HOMOLOGACION', 'CREADO')) "
				+ " AND GRUPO.ID_CONCURSO = "
				+ concursoPuestoAgr.getConcurso().getIdConcurso();

		List<ConcursoPuestoAgr> listaAgr = new ArrayList<ConcursoPuestoAgr>();
		listaAgr = em.createNativeQuery(sql, ConcursoPuestoAgr.class)
				.getResultList();
		if (listaAgr.size() == 0) {

			for (GruposPublicacionDTO f : listaGruposDTO) {

				ConcursoPuestoAgr agr = f.getFechasGrupo()
						.getConcursoPuestoAgr();

				List<PublicacionConcursoCab> listaPublic = new ArrayList<PublicacionConcursoCab>();

				listaPublic = buscarGrupos(agr.getIdConcursoPuestoAgr());
				if (listaPublic.size() > 0) {
					for (PublicacionConcursoCab p : listaPublic) {

						p.setUsuSolicitud(usuarioLogueado.getCodigoUsuario());
						p.setFechaSolicitud(new Date());
						if (p.getPublicacionConcurso() == null) {
							PublicacionConcurso publicacionConcurso = new PublicacionConcurso();
							publicacionConcurso = buscarPublicacionConcurso(p
									.getConcursoPuestoAgr());
							if (publicacionConcurso == null) {
								publicacionConcurso = new PublicacionConcurso();
								publicacionConcurso.setConcurso(p
										.getConcursoPuestoAgr().getConcurso());
								publicacionConcurso
										.setUsuSolicitud(usuarioLogueado
												.getCodigoUsuario());
								publicacionConcurso
										.setFechaSolicitud(new Date());
								publicacionConcursoHome
										.setInstance(publicacionConcurso);
								String result = publicacionConcursoHome
										.persist();
								if (result.equals("persisted")) {
									publicacionConcurso = publicacionConcursoHome
											.getInstance();
								}
							}
							p.setPublicacionConcurso(publicacionConcurso);
						}

						em.merge(p);
						// em.flush();
					}

				} else {
					PublicacionConcursoCab cab = new PublicacionConcursoCab();
					cab.setConcursoPuestoAgr(agr);
					cab.setFechaSolicitud(new Date());
					cab.setUsuSolicitud(usuarioLogueado.getCodigoUsuario());
					em.persist(cab);
					// em.flush();
					/* Incidencia 749 */
					PublicacionConcurso publicacionConcurso = new PublicacionConcurso();
					publicacionConcurso = buscarPublicacionConcurso(cab
							.getConcursoPuestoAgr());
					if (publicacionConcurso == null) {
						publicacionConcurso = new PublicacionConcurso();
						publicacionConcurso.setConcurso(cab
								.getConcursoPuestoAgr().getConcurso());
						publicacionConcurso.setUsuSolicitud(usuarioLogueado
								.getCodigoUsuario());
						publicacionConcurso.setFechaSolicitud(new Date());
						publicacionConcursoHome
								.setInstance(publicacionConcurso);

						String result = publicacionConcursoHome.persist();
						// em.flush();
						if (result.equals("persisted")) {
							publicacionConcurso = publicacionConcursoHome
									.getInstance();
						}
					}
					cab.setPublicacionConcurso(publicacionConcurso);
					em.merge(cab);
					// em.flush();
					/* End incidencia 749 */

				}

				agr.setEstado(estado);
				em.merge(agr);

				// Actualizar el estado de ConcursoPuestoDet y PlantaCargoDet
				EstadoDet estadoDet = seleccionUtilFormController
						.buscarEstadoDet("CONCURSO", "A PUBLICAR");
				if (agr.getConcursoPuestoDets() != null) {
					for (ConcursoPuestoDet concursoPuestoDet : agr
							.getConcursoPuestoDets()) {
						concursoPuestoDet.setEstadoDet(estadoDet);
						em.merge(concursoPuestoDet);

						PlantaCargoDet plantaCargoDet = concursoPuestoDet
								.getPlantaCargoDet();
						plantaCargoDet.setEstadoDet(estadoDet);
						em.merge(plantaCargoDet);
					}
				}

				// em.flush();

			}

			guardarDatosAutorizacion();

			return siguienteTarea();
		} else {
			listaAutorizacionSFP = new ArrayList<AutorizadoSFPDTO>();
			for (ConcursoPuestoAgr ag : listaAgr) {
				AutorizadoSFPDTO dt = new AutorizadoSFPDTO();
				dt.setConcursoPuestoAgr(ag);
				dt.setReferencias(buscarReferencia(ag));
				listaAutorizacionSFP.add(dt);
			}

			return getUrlToPageSolicitarAprobacion();
		}

	}

	/**
	 * Métodos que obtienen los urls necesarios
	 * 
	 * @return
	 */
	public String getUrlToPageSolicitarAprobacion() {
		return "/seleccion/publicacionConcursoCab/SolicitarAprobacionSFP.xhtml?fromToPage=seleccion/publicacionConcursoCab/PublicacionConcursoCabList&idConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	public String getUrlToPagePublicacionConcurso() {
		return "/seleccion/publicacionConcursoCab/PublicacionConcursoCabList.xhtml?fromToPage=seleccion/publicacionConcursoCab/SolicitarAprobacionSFP&idConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	@SuppressWarnings("unchecked")
	private Referencias buscarReferencia(ConcursoPuestoAgr conc) {
		String sql = "SELECT REF.* " + "FROM SELECCION.REFERENCIAS REF "
				+ "WHERE REF.DOMINIO = 'ESTADOS_GRUPO' "
				+ "AND DESC_LARGA IN ('FINALIZADO CARGA', "
				+ "'ENVIADO A HOMOLOGACION',  " + "'INICIADO CIRCUITO',  "
				+ "'HOMOLOGADO',  " + "'CON DOCUMENTOS DE HOMOLOGACION', "
				+ "'PENDIENTE REVISION',  "
				+ "'FIRMADO HOMOLOGACION', 'CREADO') " + "AND REF.VALOR_NUM = "
				+ conc.getEstado();
		List<Referencias> ref = new ArrayList<Referencias>();
		ref = em.createNativeQuery(sql, Referencias.class).getResultList();
		if (ref.size() > 0)
			return ref.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoDet> buscarListaConcursoPuestoDet(
			ConcursoPuestoAgr actual) {
		String sql = "select det.* "
				+ "from seleccion.concurso_puesto_det det "
				+ "where det.activo is true "
				+ "and det.id_concurso_puesto_agr = "
				+ actual.getIdConcursoPuestoAgr();
		return em.createNativeQuery(sql, ConcursoPuestoDet.class)
				.getResultList();
	}

	/**
	 * Busca el id correspondiente a un estado
	 * 
	 * @param estado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private EstadoCab buscarEstado(String estado) {
		String cad = "select cab.* from planificacion.estado_cab cab "
				+ "where lower(cab.descripcion)='" + estado + "'";
		List<EstadoCab> lista = new ArrayList<EstadoCab>();
		lista = em.createNativeQuery(cad, EstadoCab.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	public String autorizar() {
		EstadoCab estadoCab = buscarEstado("vacante");
		EstadoDet estadoDetCancelado = seleccionUtilFormController
		.buscarEstadoDet("CONCURSO", "CANCELADO");
		try {
			
			List<ConcursoPuestoDet> listaPuestoDet = new ArrayList<ConcursoPuestoDet>();
			for (AutorizadoSFPDTO aut : listaAutorizacionSFP) {
				ConcursoPuestoAgr puestoAgr = new ConcursoPuestoAgr();
				puestoAgr = aut.getConcursoPuestoAgr();
				
				listaPuestoDet = buscarListaConcursoPuestoDet(puestoAgr);
				for (ConcursoPuestoDet dt : listaPuestoDet) {
					PlantaCargoDet plantaCargoDet = new PlantaCargoDet();
					plantaCargoDet = dt.getPlantaCargoDet();
					plantaCargoDet.setEstadoCab(estadoCab);
					plantaCargoDet.setEstadoDet(null);
					plantaCargoDet.setFechaMod(new Date());
					plantaCargoDet
							.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(plantaCargoDet);
					dt.setActivo(false);
					dt.setFechaMod(new Date());
					dt.setEstadoDet(estadoDetCancelado);
					dt.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(dt);
					HistoricosEstado historico = new HistoricosEstado();
					historico.setEstadoCab(estadoCab);
					historico.setPlantaCargoDet(plantaCargoDet);
					historico.setFechaMod(new Date());
					historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.persist(historico);
					
					// em.flush();
				}
				
				reponerCategoriasController.reponerCategorias(puestoAgr, "GRUPO", "CANCELADO");
				em.flush();
				cancelarGrupo(puestoAgr);
			}
			Integer estado = buscarEstadoGrupo("A PUBLICAR");
			for (GruposPublicacionDTO f : listaGruposDTO) {

				ConcursoPuestoAgr agr = new ConcursoPuestoAgr();
				agr = f.getFechasGrupo().getConcursoPuestoAgr();
				agr.setEstado(estado);
				agr.setFechaMod(new Date());
				agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(agr);
				PublicacionConcurso publicacionConcurso = new PublicacionConcurso();
				publicacionConcurso = buscarPublicacionConcurso(agr);
				if (publicacionConcurso != null) {
					publicacionConcurso.setFechaSolicitud(new Date());
					publicacionConcurso.setUsuSolicitud(usuarioLogueado
							.getCodigoUsuario());
					em.merge(publicacionConcurso);
				}
			}
			em.flush();

			String res = guardarDatosAutorizacion();
			if (res.equals("ok")) {
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				return siguienteTarea();
			}
			return null;
		} catch (Exception e) {
			return null;
		}

	}

	private void cancelarGrupo(ConcursoPuestoAgr ag) {
		Integer estadoGrupoCancelado = referenciasUtilFormController.getReferencia(
				"ESTADOS_GRUPO", "GRUPO CANCELADO").getValorNum();
		
		ag.setEstado(estadoGrupoCancelado);
		ag.setFechaMod(new Date());
		ag.setUsuMod(usuarioLogueado.getCodigoUsuario());
		ag.setActivo(false);
		em.merge(ag);
		// Pasar grupo a la siguiente tarea
		jbpmUtilFormController.setConcursoPuestoAgr(ag);
//		jbpmUtilFormController
//				.setActividadActual(ActividadEnum.SOLICITAR_PUBLICACION);//MODIFICADO RV
		jbpmUtilFormController.setTransition("end");
		jbpmUtilFormController.setActividadPorGrupo(true);
		if (jbpmUtilFormController.nextTask()) {
			em.flush();
		}
		return;
	}

	/** Metodo creado por incidencia 1117 **/
	private List<ConcursoPuestoDet> concursoPuestoDetACancelar(Long id) {
		String cad = "select det.* "
				+ "from seleccion.concurso_puesto_det det "
				+ "where det.id_concurso_puesto_agr = " + id;
		return em.createNativeQuery(cad, ConcursoPuestoDet.class)
				.getResultList();
	}

	private String guardarDatosAutorizacion() {
		try {
			if (obs != null && !obs.trim().isEmpty()) {
				for (GruposPublicacionDTO f : listaGruposDTO) {
					ConcursoPuestoAgr puestoAg = new ConcursoPuestoAgr();
					puestoAg = f.getFechasGrupo().getConcursoPuestoAgr();
					long idTaskInstance = jbpmUtilFormController
							.getIdTaskInstanceActual(puestoAg
									.getIdProcessInstance());

					// Guardar Observacion
					observacion = verificarObs(idTaskInstance);
					if (observacion == null) {
						observacion = new Observacion();
						observacion.setIdTaskInstance(idTaskInstance);
						observacion.setConcurso(puestoAg.getConcurso());
						Date fecha = new Date();
						observacion.setFechaAlta(fecha);
						observacion.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
						observacion.setObservacion(obs);

						em.persist(observacion);
						em.flush();
					} else {
						observacion.setFechaMod(new Date());
						observacion.setUsuMod(usuarioLogueado
								.getCodigoUsuario());
						observacion.setObservacion(obs);
						em.merge(observacion);
						em.flush();
					}
				}
				obs = null;
			}

			return "ok";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	public String siguienteTarea() {
		try {

			// Se pasa a la siguiente tarea
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
//			jbpmUtilFormController
//					.setActividadActual(ActividadEnum.SOLICITAR_PUBLICACION);//MODIFICADO RV
//			jbpmUtilFormController
//					.setActividadSiguiente(ActividadEnum.APROBAR_PUBLICACION);
			jbpmUtilFormController.setActividadPorGrupo(false);
			jbpmUtilFormController.setTransition(null);
			if (jbpmUtilFormController.nextTask()) {
				em.flush();

			}

			return "ok";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private List<PublicacionConcursoCab> buscarGrupos(Long id) {
		String cad = "select cab.* "
				+ "from seleccion.publicacion_concurso_cab cab "
				+ "where cab.id_concurso_puesto_agr = " + id;
		List<PublicacionConcursoCab> l = new ArrayList<PublicacionConcursoCab>();
		l = em.createNativeQuery(cad, PublicacionConcursoCab.class)
				.getResultList();
		return l;
	}

	public void print() {
		List<ConcursoPuestoAgr> listaGruposAImprimir = new ArrayList<ConcursoPuestoAgr>();
		Boolean marcado = false;
		for (GruposPublicacionDTO f : listaGruposDTO) {

			marcado = true;
			listaGruposAImprimir.add(f.getFechasGrupo().getConcursoPuestoAgr());

		}
		if (!marcado) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen seleccionados para imprimir",
							"Seleccione los grupos a imprimir"));
			return;
		}
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		/*
		 * param.put("path_logo", servletContext.getRealPath("/img/"));
		 */
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		if (configuracionUoCab.getDenominacionUnidad() != null)
			param.put("OEE", configuracionUoCab.getDenominacionUnidad());
		if (concurso.getNombre() != null)
			param.put("concurso", concurso.getNombre());
		if (concurso.getObservacion() != null)
			param.put("concurso_obs", concurso.getObservacion());
		if (configuracionUoCab.getDescripcionCorta() != null)
			param.put("OEE_descripcion_corta",
					configuracionUoCab.getDescripcionCorta());
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		Boolean tieneImprimir = false;
		for (ConcursoPuestoAgr grupo : listaGruposAImprimir) {

			List<Object[]> lista = consulta1(grupo.getIdConcursoPuestoAgr());

			if (lista != null) {
				tieneImprimir = true;
				for (Object[] obj : lista) {

					HashMap<String, Object> map = new HashMap<String, Object>();
					Long id = null;
					if (obj[0] != null) {
						id = new Long(obj[0].toString());
						map.put("id", id);
					}
					if (obj[1] != null)
						map.put("grupo", obj[1].toString());
					if (obj[2] != null)
						map.put("monto", new Integer(obj[2].toString()));
					if (obj[3] != null && obj[4] != null) {
						String fec1 = formatoDeFecha.format(obj[3]);
						String fec2 = formatoDeFecha.format(obj[4]);

						String resultado = CalculoDias(new Date(fec1),
								new Date(fec2), id);
						if ("1".equals(resultado))
							resultado += " semana";
						else if (!"".equals(resultado))
							resultado += " semanas";

						map.put("duracion_llamado", resultado);
					}
					if (obj[5] != null && obj[6] != null) {

						String valor = formatoDeFecha.format(obj[5])
								+ obj[6].toString();
						map.put("fecha_recepcion", valor);
					}
					String lugar = "";
					if (obj[7] != null) {
						String l = obj[7].toString();
						if (!l.trim().isEmpty())
							lugar = lugar + obj[7].toString();
					}
					if (obj[8] != null) {
						String l = obj[8].toString();
						if (!l.trim().isEmpty())
							lugar = lugar + " - " + obj[8].toString();
					}
					if (obj[9] != null) {
						String l = obj[9].toString();
						if (!l.trim().isEmpty())
							lugar = lugar + " - " + obj[9].toString();
					}
					if (obj[10] != null) {
						String l = obj[10].toString();
						if (!l.trim().isEmpty())
							lugar = lugar + " - " + obj[10].toString();
					}
					map.put("lugar_entrega", lugar);
					if (obj[11] != null)
						map.put("id_uo_det", new Long(obj[11].toString()));
					if (obj[12] != null)
						map.put("denominacion_unidad", obj[12].toString());

					map.put("cantidad",
							calculoCantidad(grupo.getIdConcursoPuestoAgr()));
					map.put("requisitos",
							buscarRequisitos(grupo.getIdConcursoPuestoAgr()));
					if (id != null)
						map.put("departamento", listaDptos(id));
					listaDatosReporte.add(map);
				}
			}
		}
		if (!tieneImprimir) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
			return;

		}

		JasperReportUtils.respondPDF("RPT_CU285_Formato_Publicacion", false,
				listaDatosReporte, param);

		/*
		 * try { Connection conn = JpaResourceBean.getConnection();
		 * JasperReportUtils.respondPDF("RPT_CU285_Formato_Publicacion", param,
		 * false, conn, new Usuario()); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */
	}

	private String CalculoDias(Date desde, Date hasta, Long idUo) {
		GregorianCalendar calDesde = new GregorianCalendar();
		GregorianCalendar calHasta = new GregorianCalendar();
		calDesde.setTime(desde);
		calHasta.setTime(hasta);
		Integer cantDias = 0;
		while (calDesde.before(calHasta)) {
			if (seleccionUtilFormController.fechaTrabajaOee(calDesde.getTime(),
					idUo))
				cantDias++;
			calDesde.add(Calendar.DAY_OF_MONTH, 1);
		}
		if (seleccionUtilFormController.fechaTrabajaOee(calHasta.getTime(),
				idUo))
			cantDias++;
		return cantDias + "";
	}

	@SuppressWarnings("unchecked")
	private String calculoCantidad(Long idGrupo) {
		String resultado = null;
		String sql = "select count(puesto_det.*) "
				+ "from seleccion.concurso_puesto_agr agr "
				+ "join seleccion.concurso_puesto_det puesto_det "
				+ "on puesto_det.id_concurso_puesto_agr = agr.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = " + idGrupo;
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null) {
			return 0 + "";
		}

		return resultado = config.toString();

	}

	/*
	 * private String calculoSemanas(Integer cantidad) { Float res = new
	 * Float(0); Float val = new Float(7); if (cantidad < 7) return "1 semana";
	 * res = cantidad / val; String resultante = res.toString(); String[]
	 * valores = resultante.split(","); return valores[0] + " semanas"; }
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> consulta1(Long idGrupo) {
		try {
			String sql = "select distinct(agr.id_concurso_puesto_agr) as id,  "
					+ "agr.descripcion_grupo as grupo, grup_pago.monto as monto, "

					+ "case WHEN fechas1.fecha_recepcion_hasta is null THEN fechas2.fecha_recepcion_hasta "
					+ "ELSE fechas1.fecha_recepcion_hasta END as recepcion_hasta, "

					+ "case WHEN fechas1.fecha_recepcion_desde is null THEN fechas2.fecha_recepcion_desde "
					+ "ELSE fechas1.fecha_recepcion_desde END as recepcion_desde, "

					+ "case WHEN fechas1.fecha_recepcion_hasta is null THEN fechas2.fecha_recepcion_hasta "
					+ "ELSE fechas1.fecha_recepcion_hasta END as fecha_recepcion, "

					+ "case WHEN fechas1.hora_recepcion_hasta is null THEN fechas2.hora_recepcion_hasta "
					+ "ELSE fechas1.hora_recepcion_hasta END as hora_hasta,  "

					+ "case WHEN aclarac_doc1.descripcion is null THEN aclarac_doc2.descripcion "
					+ "ELSE aclarac_doc1.descripcion END as descripcion, "

					+ "case WHEN aclarac_doc1.direccion is null THEN aclarac_doc2.direccion "
					+ " ELSE aclarac_doc1.direccion END as direccion, "

					+ "case WHEN aclarac_doc1.telefono is null THEN aclarac_doc2.telefono "
					+ "ELSE aclarac_doc1.telefono END as telefono, "

					+ "case WHEN aclarac_doc1.email is null THEN aclarac_doc2.email "
					+ "ELSE aclarac_doc1.email END as email, "

					+ "uo_det.id_configuracion_uo_det as id_uo_det, uo_det.denominacion_unidad "

					+ "from seleccion.concurso_puesto_agr agr "

					+ "left join seleccion.fechas_grupo_puesto fechas1 "
					+ "on (fechas1.id_concurso_puesto_agr = agr.id_concurso_puesto_agr) "

					+ "left join seleccion.fechas_grupo_puesto fechas2 "
					+ "on (fechas2.id_concurso = agr.id_concurso)  "

					+ "left join seleccion.grupo_concepto_pago grup_pago  "
					+ "on grup_pago.id_concurso_puesto_agr = agr.id_concurso_puesto_agr  "
					+ "left join seleccion.concurso_puesto_det puesto_det  "
					+ "on puesto_det.id_concurso_puesto_agr = agr.id_concurso_puesto_agr  "

					+ "left join seleccion.present_aclarac_doc aclarac_doc1 "
					+ "on aclarac_doc1.id_concurso_puesto_agr = agr.id_concurso_puesto_agr "

					+ "left join seleccion.present_aclarac_doc aclarac_doc2 "
					+ "on aclarac_doc2.id_concurso = agr.id_concurso  "

					+ "left join planificacion.planta_cargo_det cargo_det  "
					+ "on (cargo_det.id_planta_cargo_det = puesto_det.id_planta_cargo_det and puesto_det.id_concurso_puesto_agr = agr.id_concurso_puesto_agr)  "
					+ "left join planificacion.configuracion_uo_det uo_det  "
					+ "on uo_det.id_configuracion_uo_det = cargo_det.id_configuracion_uo_det  "
					+ "left join planificacion.oficina oficina  "
					+ "on oficina.id_oficina = cargo_det.id_oficina  "
					+ "left join general.ciudad ciudad  "
					+ "on ciudad.id_ciudad = oficina.id_ciudad "
					+ "left join general.departamento dpto   "
					+ "on dpto.id_departamento = ciudad.id_departamento "
					+ "where grup_pago.obj_codigo = 111 "
					+ "and puesto_det.activo = true and agr.id_concurso_puesto_agr = "
					+ idGrupo;

			List<Object[]> config = em.createNativeQuery(sql).getResultList();
			if (config == null || config.size() == 0) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private String listaDptos(Long id) {
		String cad = "select distinct(agr.id_concurso_puesto_agr) as id, dpto.descripcion as departamento "
				+ "from seleccion.concurso_puesto_agr agr  "
				+ "LEFT join seleccion.fechas_grupo_puesto fechas  "
				+ "on fechas.id_concurso_puesto_agr = agr.id_concurso_puesto_agr  "
				+ "join seleccion.grupo_concepto_pago grup_pago  "
				+ "on grup_pago.id_concurso_puesto_agr = agr.id_concurso_puesto_agr  "
				+ "join seleccion.concurso_puesto_det puesto_det  "
				+ "on puesto_det.id_concurso_puesto_agr = agr.id_concurso_puesto_agr "
				+ "LEFT join seleccion.present_aclarac_doc aclarac_doc  "
				+ "on aclarac_doc.id_concurso_puesto_agr = agr.id_concurso_puesto_agr  "
				+ "join planificacion.planta_cargo_det cargo_det  "
				+ "on cargo_det.id_planta_cargo_det = puesto_det.id_planta_cargo_det  "
				+ "join planificacion.configuracion_uo_det uo_det  "
				+ "on uo_det.id_configuracion_uo_det = cargo_det.id_configuracion_uo_det  "
				+ "join planificacion.oficina oficina  "
				+ "on oficina.id_oficina = cargo_det.id_oficina  "
				+ "join general.ciudad ciudad  "
				+ "on ciudad.id_ciudad = oficina.id_ciudad  "
				+ "join general.departamento dpto   "
				+ "on dpto.id_departamento = ciudad.id_departamento "
				+ "where grup_pago.obj_codigo = 111 "
				+ "and puesto_det.activo = true "
				+ "and agr.id_concurso_puesto_agr = " + id;

		List<Object[]> lista = em.createNativeQuery(cad).getResultList();
		if (lista == null || lista.size() == 0) {
			return "";
		}

		String result = "";
		for (Object[] obj : lista) {
			if (obj[1] != null)
				result = result + " - " + obj[1].toString();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private String buscarRequisitos(Long idGrupo) {
		String resultado = "";
		String sql = "SELECT MR_DET.* "
				+ "FROM SELECCION.MATRIZ_REF_CONF_DET MR_DET "
				+ "JOIN SELECCION.MATRIZ_REF_CONF_ENC MR_ENC "
				+ "ON MR_DET.ID_MATRIZ_REF_CONF_ENC = MR_ENC.ID_MATRIZ_REF_CONF_ENC "
				+ "JOIN SELECCION.MATRIZ_REF_CONF MR "
				+ "ON MR.ID_MATRIZ_REF_CONF = MR_ENC.ID_MATRIZ_REF_CONF "
				+ "WHERE MR_DET.ACTIVO= TRUE " + " and MR.TIPO = 'GRUPO' "
				+ " AND MR.ID_CONCURSO_PUESTO_AGR = " + idGrupo;

		List<MatrizRefConfDet> listaMat = new ArrayList<MatrizRefConfDet>();
		listaMat = em.createNativeQuery(sql, MatrizRefConfDet.class)
				.getResultList();

		Integer cont = 1;
		for (MatrizRefConfDet m : listaMat) {
			resultado = resultado + " " + cont + "- " + m.getDescripcion();
			cont++;
		}

		return resultado;
	}

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
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

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public List<GruposPublicacionDTO> getListaGruposDTO() {
		return listaGruposDTO;
	}

	public void setListaGruposDTO(List<GruposPublicacionDTO> listaGruposDTO) {
		this.listaGruposDTO = listaGruposDTO;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public Observacion getObservacion() {
		return observacion;
	}

	public void setObservacion(Observacion observacion) {
		this.observacion = observacion;
	}

	public List<AutorizadoSFPDTO> getListaAutorizacionSFP() {
		return listaAutorizacionSFP;
	}

	public void setListaAutorizacionSFP(
			List<AutorizadoSFPDTO> listaAutorizacionSFP) {
		this.listaAutorizacionSFP = listaAutorizacionSFP;
	}

	public Boolean getHabilitarBoton() {
		return habilitarBoton;
	}

	public void setHabilitarBoton(Boolean habilitarBoton) {
		this.habilitarBoton = habilitarBoton;
	}

	public ConcursoPuestoAgrHome getConcursoPuestoAgrHome() {
		return concursoPuestoAgrHome;
	}

	public void setConcursoPuestoAgrHome(
			ConcursoPuestoAgrHome concursoPuestoAgrHome) {
		this.concursoPuestoAgrHome = concursoPuestoAgrHome;
	}

}
