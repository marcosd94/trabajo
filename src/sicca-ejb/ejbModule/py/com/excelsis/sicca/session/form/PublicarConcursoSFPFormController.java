package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.ActividadEnum;

import py.com.excelsis.sicca.dto.AutorizadoSFPDTO;
import py.com.excelsis.sicca.dto.PublicacionSFPDTO;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.CronogramaConcCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PresentAclaracDoc;
import py.com.excelsis.sicca.entity.PublicacionConcurso;
import py.com.excelsis.sicca.entity.PublicacionConcursoCab;
import py.com.excelsis.sicca.entity.PublicacionConcursoRevDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NumeracionConcursoUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("publicarConcursoSFPFormController")
public class PublicarConcursoSFPFormController implements Serializable {

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
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	NumeracionConcursoUtilFormController numeracionConcursoUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	ReportUtilFormController reportUtilFormController;

	private Long idConcursoPuestoAgr;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();

	private ConcursoPuestoAgr concursoPuestoAgr;
	private PublicacionConcursoRevDet publicacionConcursoRevDet;
	private Concurso concurso;
	public Observacion observacion;
	private String obs;
	private String direccionFisica;
	private String codConcurso;
	private Boolean isEdit;
	private String valorRadio;

	private boolean errorObs = false;

	private List<PublicacionSFPDTO> listaGruposDTO = new ArrayList<PublicacionSFPDTO>();
	private List<PublicacionConcursoRevDet> listaRevDet =
		new ArrayList<PublicacionConcursoRevDet>();
	private List<AutorizadoSFPDTO> listasAjustes = new ArrayList<AutorizadoSFPDTO>();
	private List<FechasGrupoPuesto> listasFechasGrupo = new ArrayList<FechasGrupoPuesto>();
	private List<FechasGrupoPuesto> listasFechasConcurso = new ArrayList<FechasGrupoPuesto>();
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		//seguridadUtilFormController.verificarPerteneceOee(null, concursoPuestoAgr.getIdConcursoPuestoAgr(), seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "A PUBLICAR")+"", ActividadEnum.APROBAR_PUBLICACION.getValor());
	}

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
		/* Incidencia 1014 */
		validarOee();
		/**/
		listaGruposDTO = new ArrayList<PublicacionSFPDTO>();
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		direccionFisica =
			"//SICCA//" + anho + "//" + "OEE//"
				+ concurso.getConfiguracionUoCab().getIdConfiguracionUo() + "//"
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
				+ concurso.getIdConcurso() + "//"
				+ concursoPuestoAgrHome.getInstance().getIdConcursoPuestoAgr();
		if (concurso != null) {
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null)
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}
		errorObs = false;
		buscarApublicar();

		recuperarListaRevDet();
		buscarFechaConcurso();
		buscarFechaGrupo();
	}

	public void imprimirPerfilMatriz() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU015_imprimir_perfil_matriz");
		reportUtilFormController.getParametros().put("idConcursoPuestoAgr", idConcursoPuestoAgr);
		reportUtilFormController.imprimirReportePdf();
	}

	@SuppressWarnings("unchecked")
	private void buscarApublicar() {
		String sql =
			"select agr.*  " + "from seleccion.concurso_puesto_agr agr  "
				+ "join seleccion.concurso concurso  "
				+ "on concurso.id_concurso = agr.id_concurso  " + "where agr.estado =  "
				+ "(select ref.valor_num from seleccion.referencias ref  "
				+ "WHERE ref.dominio = 'ESTADOS_GRUPO'  " + "AND ref.desc_abrev = 'A PUBLICAR')  "
				+ "and agr.activo is true " + "and concurso.id_concurso = "
				+ concurso.getIdConcurso();

		List<ConcursoPuestoAgr> lista = new ArrayList<ConcursoPuestoAgr>();
		lista = em.createNativeQuery(sql, ConcursoPuestoAgr.class).getResultList();
		for (ConcursoPuestoAgr f : lista) {
			PublicacionSFPDTO dto = new PublicacionSFPDTO();
			dto.setConcursoPuestoAgr(f);
			dto.setId(f.getIdConcursoPuestoAgr());
			dto.setSeleccionado(false);
			listaGruposDTO.add(dto);
		}
	}

	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {
		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}

	public void marcarTodos() {
		for (int i = 0; i < listaGruposDTO.size(); i++) {
			PublicacionSFPDTO dto = new PublicacionSFPDTO();
			dto = listaGruposDTO.get(i);
			dto.setSeleccionado(true);
			listaGruposDTO.set(i, dto);
		}
	}

	public void desmarcarTodos() {
		for (int i = 0; i < listaGruposDTO.size(); i++) {
			PublicacionSFPDTO dto = new PublicacionSFPDTO();
			dto = listaGruposDTO.get(i);
			dto.setSeleccionado(false);
			listaGruposDTO.set(i, dto);
		}
	}

	@SuppressWarnings("unchecked")
	private List<PublicacionConcursoCab> buscarGrupos(Long id) {
		String cad =
			"select cab.* " + "from seleccion.publicacion_concurso_cab cab "
				+ "where cab.id_concurso_puesto_agr = " + id;
		List<PublicacionConcursoCab> l = new ArrayList<PublicacionConcursoCab>();
		l = em.createNativeQuery(cad, PublicacionConcursoCab.class).getResultList();
		return l;
	}

	@SuppressWarnings("unchecked")
	private Observacion verificarObs(Long id) {
		String cad =
			"select obs.* from seleccion.observacion obs " + "where obs.id_task_instance = " + id;
		List<Observacion> l = new ArrayList<Observacion>();
		l = em.createNativeQuery(cad, Observacion.class).getResultList();
		if (l.size() > 0)
			return l.get(0);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoAgr> buscarListaConcursos() {
		String cad =
			"select agr.* " + "from seleccion.concurso_puesto_agr agr "
				+ "where agr.activo is true " + "and agr.id_concurso = " + concurso.getIdConcurso();
		em.clear();
		return em.createNativeQuery(cad, ConcursoPuestoAgr.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	private Referencias searchReferencia(Integer valor) {
		String sql =
			"select r.*  " + "from seleccion.referencias r  " + "where dominio = 'ESTADOS_GRUPO'  "
				+ "and r.valor_num = " + valor;
		List<Referencias> listaRef = em.createNativeQuery(sql, Referencias.class).getResultList();
		if (listaRef != null && listaRef.size() > 0) {
			return listaRef.get(0);
		}
		return null;
	}

	public String autorizar() {

		try {
			List<ConcursoPuestoAgr> listaAx = new ArrayList<ConcursoPuestoAgr>();
			listaAx = buscarListaConcursos();
			Referencias ref = new Referencias();
			ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "A PUBLICAR");
			Referencias refPub = new Referencias();
			refPub = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "PUBLICADO");
			EstadoDet estadoDet =
				seleccionUtilFormController.buscarEstadoDet("CONCURSO", "PUBLICADO");

			if (ref != null) {

				listasAjustes = new ArrayList<AutorizadoSFPDTO>();
				for (ConcursoPuestoAgr ax : listaAx) {
					if (!ref.getValorNum().equals(ax.getEstado())) {
						AutorizadoSFPDTO sfp = new AutorizadoSFPDTO();
						sfp.setConcursoPuestoAgr(ax);
						sfp.setReferencias(searchReferencia(ax.getEstado()));
						listasAjustes.add(sfp);
					}
				}
				if (listasAjustes.size() > 0)
					return getUrlToPagePrototipoAlternativo();

				for (ConcursoPuestoAgr ax : listaAx) {
					List<PublicacionConcursoCab> listaPublic =
						new ArrayList<PublicacionConcursoCab>();
					listaPublic = buscarGrupos(ax.getIdConcursoPuestoAgr());
					for (PublicacionConcursoCab p : listaPublic) {
						p.setUsuAprobacion(usuarioLogueado.getCodigoUsuario());
						p.setFechaAprobacion(new Date());
						em.merge(p);
						PublicacionConcurso pubCon = new PublicacionConcurso();
						pubCon = p.getPublicacionConcurso();
						pubCon.setUsuAprobacion(usuarioLogueado.getCodigoUsuario());
						pubCon.setFechaAprobacion(new Date());
						em.merge(pubCon);

					}
					ax.setEstado(refPub.getValorNum());
					em.merge(ax);
					/* Incidencia 910 */
					if (ax.getConcursoPuestoDets() != null)
						for (ConcursoPuestoDet o : ax.getConcursoPuestoDets()) {
							o.setEstadoDet(estadoDet);
							em.merge(o);
							o.getPlantaCargoDet().setEstadoDet(estadoDet);
							em.merge(o.getPlantaCargoDet());
						}
					/**/

				}
			}
			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;
			Integer val = buscarMaxNroConcurso() + 1;
			//concurso.setNumero(val);
			concurso.setAnhio(anho);
			concurso.setUsuMod(usuarioLogueado.getCodigoUsuario());
			concurso.setFechaMod(new Date());
			em.merge(concurso);
			// em.flush();
			codConcurso =
				numeracionConcursoUtilFormController.getCodigoConcurso(concurso.getIdConcurso());

			/**
			 * Modificacion para la incidencia 0001382
			 * */
			for (FechasGrupoPuesto fx : listasFechasGrupo) {
				fx.setEstado("A VERIFICAR");
				fx.setFechaPublicacionDesde(fechaActual);
				fx.setHoraPublicacionDesde(fechaActual);
				em.merge(fx);
			}

			for (FechasGrupoPuesto fx : listasFechasConcurso) {
				fx.setEstado("A VERIFICAR");
				fx.setFechaRecepcionDesde(fechaActual);
				fx.setHoraRecepcionDesde(fechaActual);
				em.merge(fx);
			}
			// em.flush();

			for (PublicacionSFPDTO f : listaGruposDTO) {

				long idTaskInstance =
					jbpmUtilFormController.getIdTaskInstanceActual(f.getConcursoPuestoAgr().getIdProcessInstance());

				// Guardar Observacion
				observacion = verificarObs(idTaskInstance);
				if (observacion == null) {
					Observacion observacion = new Observacion();
					observacion.setIdTaskInstance(idTaskInstance);
					observacion.setConcurso(f.getConcursoPuestoAgr().getConcurso());
					Date fecha = new Date();
					observacion.setFechaAlta(fecha);
					observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					if (obs != null && !obs.trim().isEmpty())
						observacion.setObservacion(obs);
					em.persist(observacion);
					// em.flush();
				} else {
					observacion.setFechaMod(new Date());
					observacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
					observacion.setObservacion(obs);
					observacion = em.merge(observacion);
					// em.flush();
				}

				if (siguienteTarea(f.getConcursoPuestoAgr(), "recibir")) {
					em.flush();
				} else {
					return null;
				}
			}

			obs = null;

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	/**
	 * Métodos que obtienen los urls necesarios
	 * 
	 * @return
	 */
	public String getUrlToPagePrototipoAlternativo() {
		return "/seleccion/publicacionConcursoSFP/SolicitarAprobacionSFP.xhtml?fromToPage=seleccion/publicacionConcursoSFP/PublicacionConcursoCabList&idConcursoPuestoAgr="
			+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	@SuppressWarnings("unchecked")
	private Integer buscarMaxNroConcurso() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String sql =
			"select max(c.numero) as nro " + "from seleccion.concurso c " + "where c.anio = "
				+ anho;

		List<Object[]> config = em.createNativeQuery(sql).getResultList();
		if (config == null) {
			return 1;
		}
		Object obj1 = config.get(0);
		if (obj1 == null)
			return 1;
		String v = obj1.toString();
		Integer valor = new Integer(v);
		
		return valor;
	}

	public boolean siguienteTarea(ConcursoPuestoAgr ag, String valor) {
		try {

			// Se pasa a la siguiente tarea
			jbpmUtilFormController.setConcursoPuestoAgr(ag);
			//jbpmUtilFormController.setActividadActual(ActividadEnum.APROBAR_PUBLICACION);
			if (valor.equals("recibir")) {
				jbpmUtilFormController.setActividadSiguiente(ActividadEnum.RECIBIR_POSTULACIONES);
				jbpmUtilFormController.setTransition("next2");
			}
			if (valor.equals("revision")) {
				//jbpmUtilFormController.setActividadSiguiente(ActividadEnum.REVISION_PUBLICACION_OEE);
				jbpmUtilFormController.setTransition("next1");
			}

			jbpmUtilFormController.setActividadPorGrupo(true);
			return jbpmUtilFormController.nextTask();
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Boolean existeFecha(Long id) {
		String sql =
			"select fec.* " + "from seleccion.fechas_grupo_puesto fec "
				+ "where fec.activo is true " + "and fec.id_concurso_puesto_agr = " + id;

		List<FechasGrupoPuesto> listaAux = new ArrayList<FechasGrupoPuesto>();
		listaAux = em.createNativeQuery(sql, FechasGrupoPuesto.class).getResultList();
		if (listaAux.size() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public Boolean existeFechaConcurso() {
		String sql =
			"select fecha.*  " + "from seleccion.fechas_grupo_puesto fecha  "
				+ "where fecha.activo is true and fecha.id_concurso = " + concurso.getIdConcurso();

		List<FechasGrupoPuesto> listaAux = new ArrayList<FechasGrupoPuesto>();
		listaAux = em.createNativeQuery(sql, FechasGrupoPuesto.class).getResultList();
		if (listaAux.size() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public Boolean existeCronograma(Long id) {
		String sql =
			"select conc_cab.* " + "from seleccion.cronograma_conc_cab conc_cab "
				+ "where conc_cab.activo is true " + "and conc_cab.id_concurso_puesto_agr = " + id;

		List<CronogramaConcCab> listaAux = new ArrayList<CronogramaConcCab>();
		listaAux = em.createNativeQuery(sql, CronogramaConcCab.class).getResultList();
		if (listaAux.size() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public Boolean existeCronogramaConcurso() {
		String sql =
			"select conc_cab.*  " + "from seleccion.cronograma_conc_cab conc_cab  "
				+ "where conc_cab.activo is true and conc_cab.id_concurso = "
				+ concurso.getIdConcurso();

		List<CronogramaConcCab> listaAux = new ArrayList<CronogramaConcCab>();
		listaAux = em.createNativeQuery(sql, CronogramaConcCab.class).getResultList();
		if (listaAux.size() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public Boolean existePresentacion(Long id) {
		String sql =
			"select doc.* " + "from seleccion.present_aclarac_doc doc "
				+ "where doc.activo is true " + "and doc.id_concurso_puesto_agr =  " + id;

		List<PresentAclaracDoc> listaAux = new ArrayList<PresentAclaracDoc>();
		listaAux = em.createNativeQuery(sql, PresentAclaracDoc.class).getResultList();
		if (listaAux.size() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public Boolean existePresentacionConcurso() {
		String sql =
			"select doc.*  " + "from seleccion.present_aclarac_doc doc  "
				+ "where doc.activo is true and doc.id_concurso = " + concurso.getIdConcurso();

		List<PresentAclaracDoc> listaAux = new ArrayList<PresentAclaracDoc>();
		listaAux = em.createNativeQuery(sql, PresentAclaracDoc.class).getResultList();
		if (listaAux.size() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	private void recuperarListaRevDet() {
		String sql =
			"select rev_det.* " + "from seleccion.publicacion_concurso_rev_det rev_det "
				+ "join seleccion.publicacion_concurso publicacion "
				+ "on publicacion.id_publicacion_concurso = rev_det.id_publicacion_concurso "
				+ "join seleccion.concurso concurso "
				+ "on concurso.id_concurso = publicacion.id_concurso "
				+ "where rev_det.activo is true " + "and concurso.id_concurso = "
				+ concurso.getIdConcurso();

		listaRevDet = new ArrayList<PublicacionConcursoRevDet>();
		listaRevDet = em.createNativeQuery(sql, PublicacionConcursoRevDet.class).getResultList();
	}

	public void crearNuevoModal() {
		publicacionConcursoRevDet = new PublicacionConcursoRevDet();
		isEdit = false;
		publicacionConcursoRevDet.setFechaObs(new Date());
		publicacionConcursoRevDet.setActivo(true);
		publicacionConcursoRevDet.setEnviadoSfp(false);
		publicacionConcursoRevDet.setAjustadoOee(false);
		publicacionConcursoRevDet.setPublicacionConcurso(buscarPublicacion());
		publicacionConcursoRevDet.setUsuObs(usuarioLogueado.getCodigoUsuario());
	}

	public void cargarModalVer(Long id) {
		publicacionConcursoRevDet = new PublicacionConcursoRevDet();
		publicacionConcursoRevDet = em.find(PublicacionConcursoRevDet.class, id);
		isEdit = true;
		if (publicacionConcursoRevDet.getAjustadoOee())
			valorRadio = "SI";
		if (!publicacionConcursoRevDet.getAjustadoOee())
			valorRadio = "NO";
	}

	public void cargarModal(Long id) {
		publicacionConcursoRevDet = new PublicacionConcursoRevDet();
		publicacionConcursoRevDet = em.find(PublicacionConcursoRevDet.class, id);
		isEdit = false;
	}

	@SuppressWarnings("unchecked")
	private PublicacionConcurso buscarPublicacion() {
		String sql =
			"select pub.*  " + "from seleccion.publicacion_concurso pub "
				+ "where pub.id_concurso = " + concurso.getIdConcurso();
		List<PublicacionConcurso> listaAx = new ArrayList<PublicacionConcurso>();
		listaAx = em.createNativeQuery(sql, PublicacionConcurso.class).getResultList();
		if (listaAx.size() > 0)
			return listaAx.get(0);
		return null;
	}

	public void guardarModal() {
		try {
			if (Utiles.vacio(publicacionConcursoRevDet.getObservacion())) {
				statusMessages.add(Severity.ERROR,"Debe ingresar una Observación");
				return;
			}
			publicacionConcursoRevDet.setPublicacionConcurso(publicacionCoDelGrupo());
			em.persist(publicacionConcursoRevDet);
			em.flush();
			recuperarListaRevDet();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private PublicacionConcurso publicacionCoDelGrupo(){
		try {
			PublicacionConcurso pc=(PublicacionConcurso)em.createQuery("Select p from PublicacionConcurso p where " +
					"p.concurso.idConcurso=:idConcurso").setParameter("idConcurso", concursoPuestoAgr.getConcurso().getIdConcurso()).getSingleResult();
			return pc;
		} catch (NoResultException e) {
			PublicacionConcurso publicacionConcurso= new PublicacionConcurso();
			publicacionConcurso.setUsuSolicitud(usuarioLogueado.getCodigoUsuario());
			publicacionConcurso.setFechaSolicitud(new Date());
			publicacionConcurso.setConcurso(concurso);
			em.persist(publicacionConcurso);
			return publicacionConcurso;
		}
	}

	public void actualizarModal() {
		try {
			if (Utiles.vacio(publicacionConcursoRevDet.getObservacion())) {
				statusMessages.add(Severity.ERROR,"Debe ingresar una Observación");
				return;
			}
			
			em.merge(publicacionConcursoRevDet);
			em.flush();
			recuperarListaRevDet();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Validar el formulario de observacion
	 * 
	 * @return true si valido correctamente. false en otro caso.
	 */
	private Boolean validarObs() {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		String mensaje = "";
		String idCompomente = "";

		if (Utiles.vacio(publicacionConcursoRevDet.getObservacion())) {
			mensaje = "Observación es un campo requerido. Verifique.";
			idCompomente = "formModal:obsField:observacion";
			errorObs = true;
		}

		if (!"".equals(mensaje)) {
			// FACES ERROR
			message.setDetail(mensaje);
			context.addMessage(idCompomente, message);
			return false;
		}

		return true;
	}

	public String onComplete() {
		if (errorObs)
			return "";

		return "Richfaces.hideModalPanel('modal')";
	}

	public String devolver() {
		if (!validar()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe seleccionar por los menos un Grupo de Puestos para enviar al OEE");
			return null;
		}
		Long idRevDet = valorMaximo();
		if (idRevDet == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe registrar una Observación previamente");
			return null;
		}
		try {
			PublicacionConcursoRevDet revDet = new PublicacionConcursoRevDet();
			revDet = em.find(PublicacionConcursoRevDet.class, idRevDet);
			revDet.setEnviadoSfp(true);
			em.merge(revDet);
			PublicacionConcurso pub = new PublicacionConcurso();
			pub = revDet.getPublicacionConcurso();
			pub.setUsuUltRev(usuarioLogueado.getCodigoUsuario());
			pub.setFechaUltRev(new Date());
			em.merge(pub);
			// em.flush();
			Referencias ref = new Referencias();
			ref =
				referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "AJUSTE PUBLICACION");
			EstadoDet estadoDet =
				seleccionUtilFormController.buscarEstadoDet("CONCURSO", "AJUSTE PUBLICACION");
			if (ref != null) {
				for (PublicacionSFPDTO d : listaGruposDTO) {
					if (d.getSeleccionado()) {
						ConcursoPuestoAgr agr = new ConcursoPuestoAgr();
						agr = d.getConcursoPuestoAgr();
						agr.setEstado(ref.getValorNum());
						em.merge(agr);

						// Guardar Observacion. Incidencia 0001028
						if (obs != null && !obs.trim().isEmpty()) {
							Observacion observacion = new Observacion();
							long idTaskInstance =
								jbpmUtilFormController.getIdTaskInstanceActual(agr.getIdProcessInstance());
							observacion.setIdTaskInstance(idTaskInstance);
							observacion.setConcurso(agr.getConcurso());
							Date fecha = new Date();
							observacion.setFechaAlta(fecha);
							observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
							observacion.setObservacion(obs);
							em.persist(observacion);
						}

						/* Incidenica 910 */
						if (agr.getConcursoPuestoDets() != null)
							for (ConcursoPuestoDet o : agr.getConcursoPuestoDets()) {
								o.setEstadoDet(estadoDet);
								em.merge(o);
								o.getPlantaCargoDet().setEstadoDet(estadoDet);
								em.merge(o.getPlantaCargoDet());
							}
						/**/
						if (siguienteTarea(agr, "revision")) {
							em.flush();
						} else {
							return null;
						}
					}
				}
			}

			return "ok";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Long valorMaximo() {
		PublicacionConcurso publicacion = new PublicacionConcurso();
		publicacion = buscarPublicacion();
		if (publicacion != null) {
			String cad =
				"select max(rev_det.id_publicacion_conc_rev_det)  "
					+ "from seleccion.publicacion_concurso_rev_det rev_det "
					+ "where rev_det.activo is true " + "and rev_det.enviado_sfp is false "
					+ "and rev_det.id_publicacion_concurso = "
					+ publicacion.getIdPublicacionConcurso();

			List<Object[]> config = em.createNativeQuery(cad).getResultList();
			if (config == null) {
				return null;
			}
			Object obj1 = config.get(0);
			if (obj1 == null)
				return null;
			String v = obj1.toString();

			return new Long(v);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void buscarFechaGrupo() {
		listasFechasGrupo = new ArrayList<FechasGrupoPuesto>();
		if (listaGruposDTO != null && listaGruposDTO.size() > 0) {
			String grupos = "";
			for (PublicacionSFPDTO publicacionSFPDTO : listaGruposDTO) {
				ConcursoPuestoAgr grupo = publicacionSFPDTO.getConcursoPuestoAgr();
				grupos += "," + grupo.getIdConcursoPuestoAgr();
			}
			grupos = grupos.replaceFirst(",", "");

			String sql =
				" select fechas.* from seleccion.fechas_grupo_puesto fechas "
					+ " where fechas.id_concurso_puesto_agr in (" + grupos
					+ ") and fechas.activo is true";

			listasFechasGrupo = em.createNativeQuery(sql, FechasGrupoPuesto.class).getResultList();
		}
	}

	@SuppressWarnings("unchecked")
	private void buscarFechaConcurso() {
		String sql =
			"select fechas.*  " + "from seleccion.fechas_grupo_puesto fechas "
				+ "where fechas.id_concurso = " + concurso.getIdConcurso()
				+ " and fechas.activo is true";

		listasFechasConcurso = new ArrayList<FechasGrupoPuesto>();
		listasFechasConcurso = em.createNativeQuery(sql, FechasGrupoPuesto.class).getResultList();
	}

	private Boolean validar() {
		for (PublicacionSFPDTO d : listaGruposDTO) {
			if (d.getSeleccionado())
				return true;
		}
		return false;
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

	public Observacion getObservacion() {
		return observacion;
	}

	public void setObservacion(Observacion observacion) {
		this.observacion = observacion;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public List<PublicacionSFPDTO> getListaGruposDTO() {
		return listaGruposDTO;
	}

	public void setListaGruposDTO(List<PublicacionSFPDTO> listaGruposDTO) {
		this.listaGruposDTO = listaGruposDTO;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getCodConcurso() {
		return codConcurso;
	}

	public void setCodConcurso(String codConcurso) {
		this.codConcurso = codConcurso;
	}

	public List<PublicacionConcursoRevDet> getListaRevDet() {
		return listaRevDet;
	}

	public void setListaRevDet(List<PublicacionConcursoRevDet> listaRevDet) {
		this.listaRevDet = listaRevDet;
	}

	public PublicacionConcursoRevDet getPublicacionConcursoRevDet() {
		return publicacionConcursoRevDet;
	}

	public void setPublicacionConcursoRevDet(PublicacionConcursoRevDet publicacionConcursoRevDet) {
		this.publicacionConcursoRevDet = publicacionConcursoRevDet;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public List<AutorizadoSFPDTO> getListasAjustes() {
		return listasAjustes;
	}

	public void setListasAjustes(List<AutorizadoSFPDTO> listasAjustes) {
		this.listasAjustes = listasAjustes;
	}

	public String getValorRadio() {
		return valorRadio;
	}

	public void setValorRadio(String valorRadio) {
		this.valorRadio = valorRadio;
	}

	public List<FechasGrupoPuesto> getListasFechasGrupo() {
		return listasFechasGrupo;
	}

	public void setListasFechasGrupo(List<FechasGrupoPuesto> listasFechasGrupo) {
		this.listasFechasGrupo = listasFechasGrupo;
	}

	public List<FechasGrupoPuesto> getListasFechasConcurso() {
		return listasFechasConcurso;
	}

	public void setListasFechasConcurso(List<FechasGrupoPuesto> listasFechasConcurso) {
		this.listasFechasConcurso = listasFechasConcurso;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

}
