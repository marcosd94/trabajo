package py.com.excelsis.sicca.capacitacion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;

import py.com.excelsis.sicca.capacitacion.session.BandejaCapacitacionList;
import py.com.excelsis.sicca.entity.BandejaCapacitacion;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.MovUsuariosTarea;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admBandejaTareaCapacitacionFC")
public class AdmBandejaTareaCapacitacionFC {

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
	BandejaCapacitacionList bandejaCapacitacionList;
	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private String capacitacion;
	private String actividad;
	private String motivo;
	private String tarea;

	private Boolean esAdministradorSFP = false;

	private Long idRol;
	private Long idCapacitacion;
	private Long processInstanceIdActual;

	private List<MovUsuariosTarea> listaUsuariosMover;
	private List<BandejaCapacitacion> listaBandeja;

	public void init() {
		listaBandeja = new ArrayList<BandejaCapacitacion>();
		cargarAdministradorSFP();

		if (nivelEntidadOeeUtil == null
			|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}

		search();
	}

	public void search() {

		String query = getQuery();
		listaBandeja = bandejaCapacitacionList.listaResultados(query);

	}

	public String getQuery() {
		String query =
			"select distinct bandejaCapacitacion "
				+ " from BandejaCapacitacion bandejaCapacitacion "
				+ " join bandejaCapacitacion.configuracionUoCab configuracionUoCab "
				+ " join bandejaCapacitacion.actividadProceso actividadProceso "
				+ " join actividadProceso.procActividadRols procActividadRol "
				+ " join procActividadRol.rol rol ";

		// JOIN
		/*if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)

			query += " join bandejaCapacitacion.configuracionUoDet configuracionUoDet ";*/

		// WHERE
		query +=
			" where (bandejaCapacitacion.usuario is not null and bandejaCapacitacion.fechaInicio is not null) ";

		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
			query +=
				" and bandejaCapacitacion.idSinNivelEntidad = "
					+ nivelEntidadOeeUtil.getIdSinNivelEntidad() + " ";

		if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
			query +=
				" and bandejaCapacitacion.idSinEntidad = " + nivelEntidadOeeUtil.getIdSinEntidad()
					+ " ";

		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			query +=
				" and configuracionUoCab.idConfiguracionUo = "
					+ nivelEntidadOeeUtil.getIdConfigCab() + " ";

		if (idRol != null) {
			query += " and rol.idRol = " + idRol + " ";
		} else {
			String roles = getIdRolesUsuario();
			query += " and rol.idRol in (" + roles + ") ";
		}

		/*if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			query +=
				" and configuracionUoDet.idConfiguracionUoDet = "
					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa() + " ";*/

		if (capacitacion != null) {

			query +=
				" and lower(bandejaCapacitacion.nombreCapac) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(capacitacion.toLowerCase())
					+ "','%')))";
		}
		if (tarea != null) {
			query +=
				" and lower(bandejaCapacitacion.actividadDesc) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(tarea.toLowerCase())
					+ "','%')))";

		}
		return query;
	}

	public void searchAll() {

		idRol = null;
		capacitacion = null;
		tarea = null;
		nivelEntidadOeeUtil.limpiar();
		cargarCabecera();

		String query = getQuery();
		listaBandeja = bandejaCapacitacionList.listaResultados(query);
	}

	private String getIdRolesUsuario() {
		String id = "";
		for (Rol r : seguridadUtilFormController.obtenerRolesUsuario()) {
			id += "," + r.getId();
		}
		id = id.replaceFirst(",", "");
		return id;
	}

	/**
	 * Verifica si un usuario puede ver todas las OEE o no
	 * 
	 * @return
	 */
	private void cargarAdministradorSFP() {
		for (Rol r : seguridadUtilFormController.obtenerRolesUsuario()) {
			if (r.getHomologador() != null && r.getHomologador()) {
				esAdministradorSFP = true;
				return;
			}
		}

		esAdministradorSFP = false;
	}

	public void cargarCabecera() {

		nivelEntidadOeeUtil.limpiar();
		Long idOee = usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo();
		ConfiguracionUoCab cab = new ConfiguracionUoCab();
		cab = em.find(ConfiguracionUoCab.class, idOee);
		Long idEnt = cab.getEntidad().getIdEntidad();
		Entidad enti = new Entidad();
		enti = em.find(Entidad.class, idEnt);
		Long idSinEnt = enti.getSinEntidad().getIdSinEntidad();
		SinEntidad sinEnt = new SinEntidad();
		sinEnt = em.find(SinEntidad.class, idSinEnt);

		Long idSinNivelEntidad = nivelEntidadOeeUtil.getIdSinNivelEntidad(sinEnt.getNenCodigo());

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(idSinEnt);

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(idOee);

		// Unidad Organizativa
		if (usuarioLogueado.getConfiguracionUoDet() != null
			&& usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet() != null)

			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());

		nivelEntidadOeeUtil.init();
	}

	public void usuarioAMover(Integer row) {
		BandejaCapacitacion bandeja = new BandejaCapacitacion();
		bandeja = listaBandeja.get(row);
		Long id = bandeja.getIdCapacitacion();
		processInstanceIdActual = bandeja.getIdProcessInstance();
		String cad =
			"select mov.* " + "from capacitacion.mov_usuarios_tarea mov "
				+ "where mov.id_process_instance = " + processInstanceIdActual;
		listaUsuariosMover = new ArrayList<MovUsuariosTarea>();
		listaUsuariosMover = em.createNativeQuery(cad, MovUsuariosTarea.class).getResultList();

	}

	public String guardarMotivo() {
		if (motivo == null || motivo.trim().isEmpty()) {
			motivo = null;
			statusMessages.clear();
			statusMessages.add(Severity.WARN, "La tarea no fue liberada, debe ingresar el motivo.");

			return null;
		}
		try {
			MovUsuariosTarea movUsuariosTarea = new MovUsuariosTarea();

			movUsuariosTarea.setUsuOrigen(jbpmUtilFormController.getActorIdActual(processInstanceIdActual));
			movUsuariosTarea.setFechaOrigen(jbpmUtilFormController.getStartIdActual(processInstanceIdActual));
			movUsuariosTarea.setMotivo(motivo);
			movUsuariosTarea.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			movUsuariosTarea.setFechaAlta(new Date());
			movUsuariosTarea.setIdProcessInstance(processInstanceIdActual);
			em.persist(movUsuariosTarea);
			jbpmUtilFormController.updateActorIdActual(processInstanceIdActual);
			em.flush();
			processInstanceIdActual = null;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

		} catch (Exception e) {
			// TODO: handle exception
		}
		return "ok";
	}

	public String redirect() {
		if (actividad != null && !"".equals(actividad)) {
			if (ActividadEnum.CAPA_CARGAR_CAPACITACION_ENVIAR_APROB.getValor().equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/capacitacion/datosCapacitacion/VerGestionDatosCapacitacion.seam?capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}

			if (ActividadEnum.CAPA_APROBAR_CAPACITACION.getValor().equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/capacitacion/aprobarCapacitacion/VerAprobarCapacitacion.seam?from=capacitacion/bandeja/admBandeja/BandejaCapacitacionList&capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_REVISAR_CAPACITACION.getValor().equalsIgnoreCase(actividad)) {
				// 3 actividad
				return "/capacitacion/revisionCapacitacion/RevisionCapacitacion.seam?capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_PUBLICAR_CAPACITACION.getValor().equalsIgnoreCase(actividad)) {
				// 3 Publicación de Capacitación
				return "/capacitacion/publicarCapacitacion/VerPublicarCapacitacion.seam?from=capacitacion/bandeja/admBandeja/BandejaCapacitacionList&capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_RECEPCIONAR_POST_INSC.getValor().equalsIgnoreCase(actividad)) {
				// 5 actividad
				return "/capacitacion/recepcionarPostulante/RecepcionarPostulante.seam?from=capacitacion/bandeja/admBandeja/BandejaCapacitacionList&capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_REPROGRAMAR_CANCELAR_CAPAC.getValor().equalsIgnoreCase(actividad)) {
				// 5 Recepcion de Postulación
				return "/capacitacion/reprogramarCapacitacion/VerReprogramarCapacitacion.seam?from=capacitacion/bandeja/admBandeja/BandejaCapacitacionList&capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_REGISTRAR_COMISION.getValor().equalsIgnoreCase(actividad)) {
				// 7 actividad
				return "/capacitacion/comision/Comision.seam?capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_EVALUAR_POSTULANTES.getValor().equalsIgnoreCase(actividad)) {
				// 8 Recepcion de Postulación
				return "/capacitacion/registrarEvalPostulante/porConcurso/EvaluacionPostulanteRegistrarView.seam?capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_INSCRIPCION_LISTA.getValor().equalsIgnoreCase(actividad)) {
				// 9 Inscripcion Lista
				return "/capacitacion/registrarEvalPostulante/registrarEvalPostulanteVer.seam?idCapacitacion="
					+ idCapacitacion+"&from=capacitacion/bandeja/admBandeja/BandejaCapacitacionList";
			}
			if (ActividadEnum.CAPA_PUBLICAR_SELECCIONADOS.getValor().equalsIgnoreCase(actividad)) {
				// 10 Publicar Seleccionados
				return "/capacitacion/publicarSeleccionados/VerPublicarSeleccionados.seam?from=capacitacion/bandeja/admBandeja/BandejaCapacitacionList&capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}

			return null;
		}
		return null;
	}

	public String getCapacitacion() {
		return capacitacion;
	}

	public void setCapacitacion(String capacitacion) {
		this.capacitacion = capacitacion;
	}

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public Boolean getEsAdministradorSFP() {
		return esAdministradorSFP;
	}

	public void setEsAdministradorSFP(Boolean esAdministradorSFP) {
		this.esAdministradorSFP = esAdministradorSFP;
	}

	public Long getIdRol() {
		return idRol;
	}

	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public List<MovUsuariosTarea> getListaUsuariosMover() {
		return listaUsuariosMover;
	}

	public void setListaUsuariosMover(List<MovUsuariosTarea> listaUsuariosMover) {
		this.listaUsuariosMover = listaUsuariosMover;
	}

	public Long getProcessInstanceIdActual() {
		return processInstanceIdActual;
	}

	public void setProcessInstanceIdActual(Long processInstanceIdActual) {
		this.processInstanceIdActual = processInstanceIdActual;
	}

	public List<BandejaCapacitacion> getListaBandeja() {
		return listaBandeja;
	}

	public void setListaBandeja(List<BandejaCapacitacion> listaBandeja) {
		this.listaBandeja = listaBandeja;
	}

	public String getTarea() {
		return tarea;
	}

	public void setTarea(String tarea) {
		this.tarea = tarea;
	}

}
