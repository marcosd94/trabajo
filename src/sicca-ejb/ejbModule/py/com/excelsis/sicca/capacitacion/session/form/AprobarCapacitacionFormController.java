package py.com.excelsis.sicca.capacitacion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.BandejaCapacitacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.HistoricoCapacitacionDoc;
import py.com.excelsis.sicca.entity.MovUsuariosTarea;
import py.com.excelsis.sicca.entity.ObsCapEval;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RevisionCapacitacion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("aprobarCapacitacionFormController")
public class AprobarCapacitacionFormController {

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
	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	private Capacitaciones capacitacion = new Capacitaciones();
	private BandejaCapacitacion bandejaCapacitacion = new BandejaCapacitacion();
	private ObsCapEval obsCapEval = new ObsCapEval();

	private List<RevisionCapacitacion> listaRevisionCapacitacion;
	private List<HistoricoCapacitacionDoc> listaHistoricos;

	private Long idCapacitacion;
	private String obs;

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idCapacitacion != null) {
			if (!sufc.validarInput(idCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
		}
		capacitacion = em.find(Capacitaciones.class, idCapacitacion);
		seguridadUtilFormController.validarCapacitacion(idCapacitacion,
				capacitacion.getEstado(),
				ActividadEnum.CAPA_APROBAR_CAPACITACION.getValor());
		cargarDatosNivel();
		buscarBandeja();
		buscarObservacionTareaAnterior();
		buscarRevisionCapacitacion();

	}

	public void initVer() {

		capacitacion = em.find(Capacitaciones.class, idCapacitacion);

		cargarDatosNivel();
		buscarBandeja();
		buscarObservacionTareaAnterior();
		buscarRevisionCapacitacion();

	}

	private void buscarBandeja() {
		String sql = "select * "
				+ "from capacitacion.bandeja_capacitacion bandeja "
				+ "where id_capacitacion = " + idCapacitacion;
		try {
			bandejaCapacitacion = (BandejaCapacitacion) em.createNativeQuery(sql,
					BandejaCapacitacion.class).getSingleResult();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	private void buscarObservacionTareaAnterior() {
		String sql = "SELECT OBS.* "
				+ "FROM CAPACITACION.CAPACITACIONES CAP "
				+ "JOIN JBPM.JBPM_TASKINSTANCE TASK "
				+ "ON TASK.PROCINST_ = CAP.ID_PROCESS_INSTANCE "
				+ "JOIN PROCESO.ACTIVIDAD ACT "
				+ "ON ACT.COD_ACTIVIDAD = TASK.NAME_	"
				+ "JOIN CAPACITACION.OBS_CAP_EVAL OBS "
				+ "ON OBS.ID_CAPACITACION = CAP.ID_CAPACITACION "
				+ "WHERE ACT.COD_ACTIVIDAD = 'CARGAR_CAPACITACION_ENVIAR_APROB' "
				+ "AND OBS.ID_CAPACITACION = " + idCapacitacion;
		try {
			obsCapEval = (ObsCapEval) em.createNativeQuery(sql,
					ObsCapEval.class).getSingleResult();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void buscarRevisionCapacitacion() {
		String sql = "select rev.*  "
				+ "from capacitacion.revision_capacitacion rev "
				+ "where rev.id_capacitacion = " + idCapacitacion;
		listaRevisionCapacitacion = new ArrayList<RevisionCapacitacion>();
		listaRevisionCapacitacion = em.createNativeQuery(sql,
				RevisionCapacitacion.class).getResultList();
	}

	public void buscarHistoricos() {
		String sql = "select doc.* "
				+ "from capacitacion.historico_capacitacion_doc doc "
				+ "where doc.id_capacitacion = " + idCapacitacion;
		listaHistoricos = new ArrayList<HistoricoCapacitacionDoc>();
		listaHistoricos = em.createNativeQuery(sql,
				HistoricoCapacitacionDoc.class).getResultList();

	}

	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			if (capacitacion.getConfiguracionUoDet() != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitacion
						.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
	}

	public void cargarCabecera() {

		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil
				.getIdSinNivelEntidad(capacitacion.getConfiguracionUoCab()
						.getEntidad().getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(capacitacion
				.getConfiguracionUoCab().getEntidad().getSinEntidad()
				.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(capacitacion.getConfiguracionUoCab()
				.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

	public void abrirDoc() {

		AdmDocAdjuntoFormController.abrirDocumentoFromCU(capacitacion
				.getDocumentos().getIdDocumento(), usuarioLogueado
				.getIdUsuario());
	}

	public void abrirDoc(int index) {

		HistoricoCapacitacionDoc h = listaHistoricos.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(h.getDocumento()
				.getIdDocumento(), usuarioLogueado.getIdUsuario());

	}

	public String aprobar() {
		try {
			/**
			 * SE ACTUALIZA LA TABLA
			 * **/
			Referencias referencias = referenciasUtilFormController
					.getReferencia("ESTADOS_CAPACITACION",
							"CAPACITACION APROBADA");
			capacitacion.setEstado(referencias.getValorNum());
			capacitacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			capacitacion.setFechaMod(new Date());
			em.merge(capacitacion);

			/**
			 * SE PASA A SGT. TAREA
			 * */
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CAPA_APROBAR_CAPACITACION);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.CAPA_PUBLICAR_CAPACITACION);
			jbpmUtilFormController.setTransition("aprCap_TO_pubCap");
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
			jbpmUtilFormController.setCapacitacion(capacitacion);

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(
					Severity.ERROR,
					"No se ha podido evaluar, ha ocurrido un error "
							+ e.getMessage());
			return null;
		}
	}

	private Boolean chkDatos() {
		if (obs == null || obs.trim().isEmpty()) {
			statusMessages.add(Severity.ERROR,
					"Debe completar el campo Observación p/ Revisión");
			return false;
		}
		return true;
	}

	public String enviarARevision() {
		try {
			if (!chkDatos())
				return null;
			/**
			 * SE ACTUALIZA LA TABLA
			 * **/
			Referencias referencias = referenciasUtilFormController
					.getReferencia("ESTADOS_CAPACITACION",
							"REVISION DE CAPACITACION");
			capacitacion.setEstado(referencias.getValorNum());
			capacitacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			capacitacion.setFechaMod(new Date());
			em.merge(capacitacion);

			/**
			 * SE GUARDA EN LA TABLA REVISION CAPACITACION
			 * **/
			RevisionCapacitacion revisionCapacitacion = new RevisionCapacitacion();
			revisionCapacitacion.setCapacitaciones(capacitacion);
			revisionCapacitacion.setFechaObs(new Date());
			revisionCapacitacion.setUsuObs(usuarioLogueado.getCodigoUsuario());
			revisionCapacitacion.setObservacion(obs);

			em.persist(revisionCapacitacion);

			/**
			 * SE PASA A SGT. TAREA
			 * */
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CAPA_APROBAR_CAPACITACION);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.CAPA_REVISAR_CAPACITACION);
			jbpmUtilFormController.setTransition("aprCap_TO_revCap");
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
			jbpmUtilFormController.setCapacitacion(capacitacion);

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}
		
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(
					Severity.ERROR,
					"No se ha podido evaluar, ha ocurrido un error "
							+ e.getMessage());
			return null;
		}
	}

	

	public Capacitaciones getCapacitacion() {
		return capacitacion;
	}

	public void setCapacitacion(Capacitaciones capacitacion) {
		this.capacitacion = capacitacion;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public BandejaCapacitacion getBandejaCapacitacion() {
		return bandejaCapacitacion;
	}

	public void setBandejaCapacitacion(BandejaCapacitacion bandejaCapacitacion) {
		this.bandejaCapacitacion = bandejaCapacitacion;
	}

	public ObsCapEval getObsCapEval() {
		return obsCapEval;
	}

	public void setObsCapEval(ObsCapEval obsCapEval) {
		this.obsCapEval = obsCapEval;
	}

	public List<RevisionCapacitacion> getListaRevisionCapacitacion() {
		return listaRevisionCapacitacion;
	}

	public void setListaRevisionCapacitacion(
			List<RevisionCapacitacion> listaRevisionCapacitacion) {
		this.listaRevisionCapacitacion = listaRevisionCapacitacion;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public List<HistoricoCapacitacionDoc> getListaHistoricos() {
		return listaHistoricos;
	}

	public void setListaHistoricos(
			List<HistoricoCapacitacionDoc> listaHistoricos) {
		this.listaHistoricos = listaHistoricos;
	}

}
