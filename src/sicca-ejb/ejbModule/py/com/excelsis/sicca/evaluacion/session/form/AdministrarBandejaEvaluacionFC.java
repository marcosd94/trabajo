package py.com.excelsis.sicca.evaluacion.session.form;

import java.io.Serializable;
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

import py.com.excelsis.sicca.entity.BandejaCapacitacion;
import py.com.excelsis.sicca.entity.BandejaEvaluacion;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.MovUsuariosTarea;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.evaluacion.session.BandejaEvaluacionList;
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
@Name("administrarBandejaEvaluacionFC")
public class AdministrarBandejaEvaluacionFC implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4600554692134096852L;
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
	BandejaEvaluacionList bandejaEvaluacionList;
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
	List<BandejaEvaluacion> listaBandeja = new ArrayList<BandejaEvaluacion>();
	private List<MovUsuariosTarea> listaUsuariosMover;

	private String evaluacion;
	private String mensaje;
	private String actividad;
	private String tarea;
	private String motivo;

	private Boolean esAdministradorSFP = false;
	private Boolean mostrarResultado;

	private Long idRol;
	private Long idEvaluacion;
	private Long processInstanceIdActual;

	public void init() {

		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}

		if (mensaje != null)
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		search();
	}

	public void search() {

		String query = getQuery();
		if (!listaBandeja.isEmpty())
			listaBandeja.clear();
		listaBandeja = bandejaEvaluacionList.listaResultados(query);

	}

	public void searchAll() {
		// if (!validar(false))
		// return;

		idRol = null;
		evaluacion = null;
		nivelEntidadOeeUtil.limpiar();
		tarea = null;
		cargarCabecera();

		String query = getQuery();
		bandejaEvaluacionList.listaResultados(query);
		
	}

	public String getQuery() {
		String query = "select distinct bandejaEvaluacion "
				+ " from BandejaEvaluacion bandejaEvaluacion "
				+ " join bandejaEvaluacion.actividadProceso actividadProceso "
				+ " join actividadProceso.procActividadRols procActividadRol "
				+ " join procActividadRol.rol rol ";

		// JOIN
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			mostrarResultado = true;

		else
//			sin importar la UO, se muestra el resultado; Werner.
//			mostrarResultado = false;
		mostrarResultado = true;
		// WHERE
		query += " where (bandejaEvaluacion.usuario is not null and bandejaEvaluacion.fechaInicio is not null) ";

		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
			query += " and bandejaEvaluacion.idSinNivelEntidad = "
					+ nivelEntidadOeeUtil.getIdSinNivelEntidad() + " ";

		if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
			query += " and bandejaEvaluacion.idSinEntidad = "
					+ nivelEntidadOeeUtil.getIdSinEntidad() + " ";

		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			query += " and bandejaEvaluacion.idConfiguracionUo = "
					+ nivelEntidadOeeUtil.getIdConfigCab() + " ";

		if (idRol != null) {
			query += " and rol.idRol = " + idRol + " ";
		} else {
			String roles = getIdRolesUsuario();
			query += " and rol.idRol in (" + roles + ") ";
		}

		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			query += " and bandejaEvaluacion.idConfiguracionUoDet = "
					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa() + " ";

		if (evaluacion != null) {

			query += " and lower(bandejaEvaluacion.tituloEval) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(evaluacion
							.toLowerCase()) + "','%')))";
		}

		if (tarea != null) {

			query += " and lower(bandejaEvaluacion.actividad) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(tarea
							.toLowerCase()) + "','%')))";
		}

		return query;
	}
	
	public void cargarCabecera() {

		nivelEntidadOeeUtil.limpiar();
		Long idOee = usuarioLogueado.getConfiguracionUoCab()
				.getIdConfiguracionUo();
		ConfiguracionUoCab cab = new ConfiguracionUoCab();
		cab = em.find(ConfiguracionUoCab.class, idOee);
		Long idEnt = cab.getEntidad().getIdEntidad();
		Entidad enti = new Entidad();
		enti = em.find(Entidad.class, idEnt);
		Long idSinEnt = enti.getSinEntidad().getIdSinEntidad();
		SinEntidad sinEnt = new SinEntidad();
		sinEnt = em.find(SinEntidad.class, idSinEnt);

		Long idSinNivelEntidad = nivelEntidadOeeUtil
				.getIdSinNivelEntidad(sinEnt.getNenCodigo());

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(idSinEnt);

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(idOee);

		// Unidad Organizativa
	/*	if (usuarioLogueado.getConfiguracionUoDet() != null
				&& usuarioLogueado.getConfiguracionUoDet()
						.getIdConfiguracionUoDet() != null)

			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
					.getConfiguracionUoDet().getIdConfiguracionUoDet());
*/
		nivelEntidadOeeUtil.init();
	}

	private String getIdRolesUsuario() {
		String id = "";
		for (Rol r : seguridadUtilFormController.obtenerRolesUsuario()) {
			id += "," + r.getId();
		}
		id = id.replaceFirst(",", "");
		return id;
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
			motivo = null;
			processInstanceIdActual = null;
			search();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

		} catch (Exception e) {
			// TODO: handle exception
		}
		return "ok";
	}
	
	public void usuarioAMover(Integer row) {
		BandejaEvaluacion bandeja = new BandejaEvaluacion();
		bandeja = listaBandeja.get(row);
		Long id = bandeja.getIdEvaluacionDesempeno();
		processInstanceIdActual = bandeja.getIdProcessInstance();
		String cad =
			"select mov.* " + "from capacitacion.mov_usuarios_tarea mov "
				+ "where mov.id_process_instance = " + processInstanceIdActual;
		listaUsuariosMover = new ArrayList<MovUsuariosTarea>();
		listaUsuariosMover = em.createNativeQuery(cad, MovUsuariosTarea.class).getResultList();

	}
	
	public String redirect() {
		if (actividad != null && !"".equals(actividad)) {
			/*
			 * Aca se verifica la actividad para renderearlo
			 */
			if (actividad != null && !"".equals(actividad)) {
				//Actividad 1
				if (ActividadEnum.EVAL_DESEM_ASIGNAR_PLANTILLA.getValor().equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/metodologiaGrupo/VerAsigMetodologiaGrupo.seam?evaluacionDesempenoIdEvaluacionDesempeno="
					+ idEvaluacion;
				}
				//Actividad 2
				if (ActividadEnum.EVAL_DESEM_GENERAR_RESOLUCION.getValor().equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/gestionarResolucion/GestionarResolucionEvalView.seam?evaluacionDesempenoIdEvaluacionDesempeno="
					+ idEvaluacion+"&from=evaluacionDesempenho/bandeja/administrar/BandejaEvaluacionList";
				}
				//Actividad 3
				if (ActividadEnum.EVAL_DESEM_APROBAR_EVALUACION.getValor().equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/aprobarEvaluacionDesempenho/VerAprobacionEvaluacionDesempenho.seam?evaluacionDesempenoIdEvaluacionDesempeno="
					+ idEvaluacion;
				}
				
				//Actividad 4.1
				if (ActividadEnum.EVAL_DESEM_REVISAR_EVALUACION.getValor().equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/revisarEvalDesempeno/VerRevisarEvalDesempeno564.seam?evaluacionDesempenoIdEvaluacionDesempeno="
					+ idEvaluacion;
				}
				
				//Actividad 4.2
				if (ActividadEnum.EVAL_DESEM_ADJUNTAR_RESOLUCION_FIRMADA.getValor().equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/adjuntosResolucion/VerAjuntosResolucion.seam?evaluacionDesempenoIdEvaluacionDesempeno="
					+ idEvaluacion;
				}
				 
			}
		}
		
		return null;
		
	}


	public String getEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(String evaluacion) {
		this.evaluacion = evaluacion;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public String getTarea() {
		return tarea;
	}

	public void setTarea(String tarea) {
		this.tarea = tarea;
	}

	public Boolean getEsAdministradorSFP() {
		return esAdministradorSFP;
	}

	public void setEsAdministradorSFP(Boolean esAdministradorSFP) {
		this.esAdministradorSFP = esAdministradorSFP;
	}

	public Boolean getMostrarResultado() {
		return mostrarResultado;
	}

	public void setMostrarResultado(Boolean mostrarResultado) {
		this.mostrarResultado = mostrarResultado;
	}

	public Long getIdRol() {
		return idRol;
	}

	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}

	public Long getIdEvaluacion() {
		return idEvaluacion;
	}

	public void setIdEvaluacion(Long idEvaluacion) {
		this.idEvaluacion = idEvaluacion;
	}

	public List<MovUsuariosTarea> getListaUsuariosMover() {
		return listaUsuariosMover;
	}

	public void setListaUsuariosMover(List<MovUsuariosTarea> listaUsuariosMover) {
		this.listaUsuariosMover = listaUsuariosMover;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Long getProcessInstanceIdActual() {
		return processInstanceIdActual;
	}

	public void setProcessInstanceIdActual(Long processInstanceIdActual) {
		this.processInstanceIdActual = processInstanceIdActual;
	}

	
	
}
