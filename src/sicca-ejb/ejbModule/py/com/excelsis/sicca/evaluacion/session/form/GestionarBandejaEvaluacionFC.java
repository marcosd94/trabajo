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
import py.com.excelsis.sicca.capacitacion.session.BandejaCapacitacionList;
import py.com.excelsis.sicca.entity.Actividad;
import py.com.excelsis.sicca.entity.BandejaCapacitacion;
import py.com.excelsis.sicca.entity.BandejaEvaluacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.MovUsuariosTarea;
import py.com.excelsis.sicca.entity.ProcActividadRol;
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
@Name("gestionarBandejaEvaluacionFC")
public class GestionarBandejaEvaluacionFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1378244926693220532L;
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
	
	private String evaluacion;
	private String mensaje;
	private String actividad;
	private String tarea;

	private Boolean esAdministradorSFP = false;
	private Boolean mostrarResultado;

	private Long idRol;
	private Long idEvaluacion;

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
		if (usuarioLogueado.getConfiguracionUoDet() == null)
//			mostrarResultado = false; sin UO
			mostrarResultado = true;
		else
			mostrarResultado = true;

		// WHERE
		query += " where (bandejaEvaluacion.usuario is null or bandejaEvaluacion.usuario = '"
				+ usuarioLogueado.getCodigoUsuario() + "') ";
		
		//agregado; Werner, si pertenece a SFP o la OEE
		String rolesProc = getIdRolesUsuario();
		List<ProcActividadRol> procActividadRol = new ArrayList<ProcActividadRol>();
		String q = "select * from proceso.proc_actividad_rol where id_rol IN ( "+rolesProc+") and id_actividad_proceso IN (42,43)";//procesos correspondientes a la SFP, Aprobar Evaluación; Adjuntar Resolución
		procActividadRol = em.createNativeQuery(q, ProcActividadRol.class).getResultList();
		List<ProcActividadRol> procActividadRol2 = new ArrayList<ProcActividadRol>();
		String q2 = "select * from proceso.proc_actividad_rol where id_rol IN ( "+rolesProc+") and id_actividad_proceso IN (40,41,42,43,44)";//en el caso que tenga SFP y OEE
		procActividadRol2 = em.createNativeQuery(q2, ProcActividadRol.class).getResultList();
		//************
		if (procActividadRol.isEmpty()) {//agregado
			if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
				query += " and bandejaEvaluacion.idSinNivelEntidad = "
						+ nivelEntidadOeeUtil.getIdSinNivelEntidad() + " ";

			if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
				query += " and bandejaEvaluacion.idSinEntidad = "
						+ nivelEntidadOeeUtil.getIdSinEntidad() + " ";
				
			if (nivelEntidadOeeUtil.getIdConfigCab() != null)
				query += " and bandejaEvaluacion.idConfiguracionUo = "
						+ nivelEntidadOeeUtil.getIdConfigCab() + " ";
		}else{
			if (procActividadRol2.size() >= 5) {//aún faltaN ajustes en caso de hacer variantes en los permisos; varios roles con permisos similares pueden sumar más que la condición...
				List<BandejaEvaluacion> bandejaAux = new ArrayList<BandejaEvaluacion>();
				String q3 = "select * from eval_desempeno.bandeja_evaluacion b where b.id_configuracion_uo = "+nivelEntidadOeeUtil.getIdConfigCab()+" "
						+ " or b.id_configuracion_uo IN (select c.id_configuracion_uo from eval_desempeno.bandeja_evaluacion c where c.id_actividad_proceso NOT IN (40,41,44))";
				bandejaAux = em.createNativeQuery(q3, BandejaEvaluacion.class).getResultList();
				String param="";
				for (int i = 0; i < bandejaAux.size(); i++) {
					if (i == 0) {
						param=""+bandejaAux.get(i).getIdEvaluacionDesempeno();
					}else{
						param= param+","+bandejaAux.get(i).getIdEvaluacionDesempeno();
					}
				}
				if (param.contentEquals(""))
					query += " and bandejaEvaluacion.actividadProceso IN (42,43) ";//para que no traiga tareas que no correspondan a su OEE, en el caso de no tener registros disponibles y tenga roles SFP y OEE
				else
					query += " and bandejaEvaluacion.idEvaluacionDesempeno IN ("+param+") ";//"admin" y que pueda manejar otras tareas pero solo de su OEE...
			}else{
				query += " and bandejaEvaluacion.actividadProceso IN (42,43) ";//exclusivo para SFP
			}
		}
		
		if (idRol != null) {
			query += " and rol.idRol = " + idRol + " ";
		} else {
			String roles = getIdRolesUsuario();
			query += " and rol.idRol in (" + roles + ") ";
		}

//		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
//			query += " and bandejaEvaluacion.idConfiguracionUoDet = "
//					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa() + " ";

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

	private String getIdRolesUsuario() {
		String id = "";
		for (Rol r : seguridadUtilFormController.obtenerRolesUsuario()) {
			id += "," + r.getId();
		}
		id = id.replaceFirst(",", "");
		return id;
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
		if (usuarioLogueado.getConfiguracionUoDet() != null
				&& usuarioLogueado.getConfiguracionUoDet()
						.getIdConfiguracionUoDet() != null
				&& !seguridadUtilFormController.esRolHomologar(usuarioLogueado
						.getIdUsuario()))

			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
					.getConfiguracionUoDet().getIdConfiguracionUoDet());

		nivelEntidadOeeUtil.init();
	}

	public String redirect() {
		if (actividad != null && !"".equals(actividad)) {
			EvaluacionDesempeno evaluacion = em.find(EvaluacionDesempeno.class,
					idEvaluacion);
			String tareaAsignada = jbpmUtilFormController.tareaActualLiberada(
					evaluacion.getIdProcessInstance(),
					usuarioLogueado.getCodigoUsuario());
			if (tareaAsignada.equals("error")) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Ocurrio un error al inicializar la tarea. Consulte con el administrador del sistema.");
				return null;
			}
			if (tareaAsignada.equals("liberado")) {
				MovUsuariosTarea mov = new MovUsuariosTarea();
				mov = usuarioAMover(evaluacion.getIdProcessInstance());
				if (mov != null && mov.getIdMov() != null) {
					mov.setUsuDest(usuarioLogueado.getCodigoUsuario());
					mov.setFechaDest(new Date());
					em.merge(mov);
					em.flush();
				}
			}
			/*
			 * Aca se verifica la actividad para renderearlo
			 */
			if (actividad != null && !"".equals(actividad)) {
				// Actividad 1
				if (ActividadEnum.EVAL_DESEM_ASIGNAR_PLANTILLA.getValor()
						.equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/metodologiaGrupo/AsignarMetodologiaGrupo.seam?evaluacionDesempenoIdEvaluacionDesempeno="
							+ idEvaluacion;
				}
				// Actividad 2
				if (ActividadEnum.EVAL_DESEM_GENERAR_RESOLUCION.getValor()
						.equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/gestionarResolucion/GestionarResolucionEvalEdit.seam?evaluacionDesempenoIdEvaluacionDesempeno="
							+ idEvaluacion;
				}
				// Actividad 3
				if (ActividadEnum.EVAL_DESEM_APROBAR_EVALUACION.getValor()
						.equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/aprobarEvaluacionDesempenho/AprobarEvaluacionDesempenho.seam?evaluacionDesempenoIdEvaluacionDesempeno="
							+ idEvaluacion;
				}

				// Actividad 4.1
				if (ActividadEnum.EVAL_DESEM_REVISAR_EVALUACION.getValor()
						.equalsIgnoreCase(actividad)) {

				}

				// Actividad 4.2
				if (ActividadEnum.EVAL_DESEM_ADJUNTAR_RESOLUCION_FIRMADA
						.getValor().equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/adjuntosResolucion/AdjuntarResolucion.seam?evaluacionDesempenoIdEvaluacionDesempeno="
							+ idEvaluacion;
				}
				// Actividad
				if (ActividadEnum.EVAL_DESEM_REVISAR_EVALUACION.getValor()
						.equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/revisarEvalDesempeno/revisarEvalDesempeno564.seam?evaluacionDesempenoIdEvaluacionDesempeno="
							+ idEvaluacion;
				}
			}
		}
		return null;
	}

	public Boolean gestionar(Long idActividad, Long idUOCab) {
		if (idUOCab == null || idActividad == null)
			return false;
		Actividad act = new Actividad();
		act = em.find(Actividad.class, idActividad);
		if (nivelEntidadOeeUtil.getIdConfigCab() == null)
			return true;
		if (idUOCab.equals(nivelEntidadOeeUtil.getIdConfigCab()))
			return true;
		if (act.getValor() != null && act.getValor())
			return true;
		return false;
	}

	private MovUsuariosTarea usuarioAMover(Long processInstanceId) {
		String cad = "select mov.* "
				+ "from capacitacion.mov_usuarios_tarea mov "
				+ "where mov.id_process_instance = " + processInstanceId
				+ " order by mov.fecha_alta desc limit 1";

		List<MovUsuariosTarea> l = new ArrayList<MovUsuariosTarea>();

		l = em.createNativeQuery(cad, MovUsuariosTarea.class).getResultList();
		if (l != null && l.size() > 0)
			return l.get(0);
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

	public BandejaEvaluacionList getBandejaEvaluacionList() {
		return bandejaEvaluacionList;
	}

	public void setBandejaEvaluacionList(
			BandejaEvaluacionList bandejaEvaluacionList) {
		this.bandejaEvaluacionList = bandejaEvaluacionList;
	}

	public Long getIdEvaluacion() {
		return idEvaluacion;
	}

	public void setIdEvaluacion(Long idEvaluacion) {
		this.idEvaluacion = idEvaluacion;
	}

}
