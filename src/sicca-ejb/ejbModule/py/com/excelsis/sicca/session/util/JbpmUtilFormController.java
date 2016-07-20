package py.com.excelsis.sicca.session.util;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.DelegationException;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import py.com.excelsis.sicca.entity.Actividad;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.JbpmTaskinstance;
import py.com.excelsis.sicca.entity.SolicitudTrasladoCab;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;
import enums.ProcesoEnum;

@Scope(ScopeType.CONVERSATION)
@Name("jbpmUtilFormController")
public class JbpmUtilFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4991047933512556323L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private ConcursoPuestoAgr concursoPuestoAgr;
	private Excepcion excepcion;
	private Capacitaciones capacitacion;
	private EvaluacionDesempeno evaluacionDesempeno;
	private ActividadEnum actividadActual;
	private ActividadEnum actividadSiguiente;
	private String transition;
	private ProcesoEnum procesoEnum;
	private SolicitudTrasladoCab solicitudTrasladoCab;

	private Boolean actividadPorGrupo = false;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	public void init() {
		actividadPorGrupo = false;
	}

	@SuppressWarnings("unchecked")
	public Long getIdTaskInstanceActual(Long processInstanceId) {
		JbpmConfiguration jbpmConf = JbpmConfiguration.getInstance();
		JbpmContext jbpmCtx = jbpmConf.createJbpmContext();
		long id = 0;
		try {
			ProcessInstance procInst = jbpmCtx
					.getProcessInstance(processInstanceId);
			if (procInst != null && procInst.getTaskMgmtInstance() != null) {
				for (Object o : procInst.getTaskMgmtInstance()
						.getTaskInstances()) {
					TaskInstance t = (TaskInstance) o;
					if (t.getId() > id)
						id = t.getId();
				}
			}
		} catch (Exception e) {

		} finally {
			jbpmCtx.close();
		}

		return id;
	}

	@SuppressWarnings("unchecked")
	public boolean asignarTaskInstanceActual(Long processInstanceId,
			String usuario) {
		JbpmConfiguration jbpmConf = JbpmConfiguration.getInstance();
		JbpmContext jbpmCtx = jbpmConf.createJbpmContext();
		long id = 0;
		boolean ok = true;
		try {
			id = getIdTaskInstanceActual(processInstanceId);
			TaskInstance t = jbpmCtx.getTaskInstance(id);
			if (t.getActorId() == null) // No se encuentra asignado aun
				t.start(usuario);
		} catch (Exception e) {
			ok = false;
			e.printStackTrace();
		} finally {
			jbpmCtx.close();
		}

		return ok;
	}

	public String getActorIdActual(Long processInstanceId) {
		JbpmConfiguration jbpmConf = JbpmConfiguration.getInstance();
		JbpmContext jbpmCtx = jbpmConf.createJbpmContext();
		long id = 0;

		try {
			id = getIdTaskInstanceActual(processInstanceId);
			TaskInstance t = jbpmCtx.getTaskInstance(id);
			return t.getActorId();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jbpmCtx.close();
		}

		return null;
	}

	public Date getStartIdActual(Long processInstanceId) {
		JbpmConfiguration jbpmConf = JbpmConfiguration.getInstance();
		JbpmContext jbpmCtx = jbpmConf.createJbpmContext();
		long id = 0;

		try {
			id = getIdTaskInstanceActual(processInstanceId);
			TaskInstance t = jbpmCtx.getTaskInstance(id);
			return t.getStart();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jbpmCtx.close();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public boolean updateActorIdActual(Long processInstanceId) {
		JbpmConfiguration jbpmConf = JbpmConfiguration.getInstance();
		JbpmContext jbpmCtx = jbpmConf.createJbpmContext();
		long id = 0;
		boolean ok = true;
		try {
			id = getIdTaskInstanceActual(processInstanceId);
			TaskInstance t = jbpmCtx.getTaskInstance(id);
			t.start(null);
		} catch (Exception e) {
			ok = false;
			e.printStackTrace();
		} finally {
			jbpmCtx.close();
		}

		return ok;
	}

	@SuppressWarnings("unchecked")
	public String tareaActualLiberada(Long processInstanceId, String usuario) {
		JbpmConfiguration jbpmConf = JbpmConfiguration.getInstance();
		JbpmContext jbpmCtx = jbpmConf.createJbpmContext();
		long id = 0;
		String resultado = "sinEfecto";
		try {
			id = getIdTaskInstanceActual(processInstanceId);
			TaskInstance t = jbpmCtx.getTaskInstance(id);
			if (t.getActorId() == null && t.getStart() != null) { // fue
																	// liberado
				t.setActorId(usuario);

				resultado = "liberado";
			}
			if (t.getActorId() == null && t.getStart() == null) {// aun no fue
																	// asignado
				t.start(usuario);
				resultado = "asignado";
			}
		} catch (Exception e) {
			resultado = "error";
			e.printStackTrace();
		} finally {
			jbpmCtx.close();
		}

		return resultado;
	}

	public boolean nextTask() {
		try {
			// Traer roles para la siguiente tarea
			if (actividadSiguiente != null) {
				if (procesoEnum == null) {
					procesoEnum = ProcesoEnum.CONCURSO; // Por defecto concurso CPO
				}

				roles = seleccionUtilFormController.getRolesTarea(
						actividadSiguiente.getValor(), procesoEnum.getValor());
				if (roles == null || roles.length() == 0) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"No se encontraron roles asignados para la tarea: "
									+ actividadSiguiente.getDescripcion());
					
					return false;
				}
			}

			// Se verifica si la actividad es por grupo o por concurso
			if ("GRUPO".equalsIgnoreCase(getTipoActividad(actividadActual))
					|| actividadPorGrupo) {
				// Actividades por grupo
				if (!nextTaskGroup(concursoPuestoAgr)) {
					statusMessages.clear();
					statusMessages
							.add(Severity.FATAL,
									"Ocurrio un error al inicializar el proceso. Consulte con el administrador del sistema.");
					
					return false;
				}
			} else if ("CONCURSO"
					.equalsIgnoreCase(getTipoActividad(actividadActual))) {
				// Actividades por concurso
				List<ConcursoPuestoAgr> lista = seleccionUtilFormController
						.buscarGruposConcurso(concursoPuestoAgr.getConcurso()
								.getIdConcurso(), actividadActual.getValor());
				for (ConcursoPuestoAgr grupo : lista) { // Se pasan todos los grupos
														// del concurso
					if (!nextTaskGroup(grupo)) {
						statusMessages.clear();
						statusMessages
								.add(Severity.FATAL,
										"Ocurrio un error al inicializar el proceso. Consulte con el administrador del sistema.");
						
						return false;
					}
				}
			} else if ("EXCEPCION"
					.equalsIgnoreCase(getTipoActividad(actividadActual))) {
				// Es una Excepcion
				if (!nextTaskGroup(excepcion)) {
					statusMessages.clear();
					statusMessages
							.add(Severity.FATAL,
									"Ocurrio un error al inicializar el proceso. Consulte con el administrador del sistema.");
					
					return false;
				}
			} else if ("CAPACITACION"
					.equalsIgnoreCase(getTipoActividad(actividadActual))) {
				
				// Es una Capacitacion
				if (!nextTaskGroup(capacitacion.getIdProcessInstance())) {
					statusMessages.clear();
					statusMessages
							.add(Severity.FATAL,
									"Ocurrio un error al inicializar el proceso. Consulte con el administrador del sistema.");
					
					return false;
				}
				
			} 
			
			else if ("EVALUACION_DESEMPENO"
					.equalsIgnoreCase(getTipoActividad(actividadActual))) {
				// Es una Evaluacion
				if (!nextTaskGroup(evaluacionDesempeno.getIdProcessInstance())) {
					statusMessages.clear();
					statusMessages
							.add(Severity.FATAL,
									"Ocurrio un error al inicializar el proceso. Consulte con el administrador del sistema.");
					
					return false;
				}
			}else if ("TRASLADO SICCA"
					.equalsIgnoreCase(getTipoActividad(actividadActual))) {
				// Es Movilidad
				if (!nextTaskGroup(solicitudTrasladoCab.getIdProcessInstance())) {
					statusMessages.clear();
					statusMessages
							.add(Severity.FATAL,
									"Ocurrio un error al inicializar el proceso. Consulte con el administrador del sistema.");
					
					return false;
				}
			}
			
			else {
				statusMessages.clear();
				statusMessages
						.add(Severity.FATAL,
								"Error de tipo de actividad en el proceso. Consulte con el administrador del sistema.");
				
				return false;
			}

			statusMessages.clear();
			statusMessages.add(Severity.INFO,
					"Tarea: " + actividadActual.getDescripcion() + " Finalizada.");
			

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	public boolean nextTaskGroup(Long idProcessInstance) {
		// Se pasa a la siguiente tarea para un grupo en particular
		try {
			BusinessProcess bp = BusinessProcess.instance();
			bp.resumeProcess(idProcessInstance);
			Long idTaskInstance = jbpmUtilFormController
					.getIdTaskInstanceActual(idProcessInstance);
			bp.resumeTask(idTaskInstance);
			if (bp != null && bp.getProcessId() != null) {

				try {
					if (transition == null || "".equals(transition))
						bp.endTask("next");
					else
						bp.endTask(transition);
				} catch (DelegationException de) {

				}

				JbpmTaskinstance jbpmTaskinstance = em.find(
						JbpmTaskinstance.class, idTaskInstance);
				jbpmTaskinstance
						.setUserend(usuarioLogueado != null ? usuarioLogueado
								.getCodigoUsuario() : "SYSTEM");
				em.merge(jbpmTaskinstance);

				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages
					.add(Severity.FATAL,
							"Ocurrio un error al finalizar la tarea. Consulte con el administrador del sistema.");
		}
		return false;
	}

	public boolean nextTaskGroup(ConcursoPuestoAgr concursoPuestoAgr) {
		// Se pasa a la siguiente tarea para un grupo en particular
		try {
			BusinessProcess bp = BusinessProcess.instance();
			bp.resumeProcess(concursoPuestoAgr.getIdProcessInstance());
			Long idTaskInstance = jbpmUtilFormController
					.getIdTaskInstanceActual(concursoPuestoAgr
							.getIdProcessInstance());
			bp.resumeTask(idTaskInstance);
			if (bp != null && bp.getProcessId() != null) {

				try {
					if (transition == null || "".equals(transition))
						bp.endTask("next");
					else
						bp.endTask(transition);
				} catch (DelegationException de) {

				}

				JbpmTaskinstance jbpmTaskinstance = em.find(
						JbpmTaskinstance.class, idTaskInstance);
				jbpmTaskinstance
						.setUserend(usuarioLogueado != null ? usuarioLogueado
								.getCodigoUsuario() : "SYSTEM");
				em.merge(jbpmTaskinstance);

				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages
					.add(Severity.FATAL,
							"Ocurrio un error al finalizar la tarea. Consulte con el administrador del sistema.");
		}
		return false;
	}

	public String getTipoActividad(ActividadEnum actividadEnum) {
		try {
			String cad = " " + " select actividad from Actividad actividad "
					+ " where actividad.codActividad = :codActividad "
					+ " 	and actividad.activo = :activo";

			Query q = em.createQuery(cad);
			/*************************<MODIFICACION>*********************************/
			/*************************MODIFICADO: 11/01/2013*************************/
			/*************************AUTOR: RODRIGO VELAZQUEZ***********************/
			if(actividadEnum.getValor().equals("ENVIAR_HOMOLOGACION"))
				q.setParameter("codActividad", "APROBAR_HOMOLOG_SFP");
			else if (actividadEnum.getValor().equals("ELABORAR_DOC_HOMOLOG"))
				q.setParameter("codActividad", "FIRMA_RESOL_HOMOLOG");
			else if (actividadEnum.getValor().equals("MODIFICAR_DATOS_CONCURSO"))
				q.setParameter("codActividad", "RECIBIR_POSTULACIONES");
			else if (actividadEnum.getValor().equals("PUBLICAR_ADJUDICADOS"))
				q.setParameter("codActividad", "FIRMAR_RESOLUCION_ADJUDICACION");
			else if (actividadEnum.getValor().equals("ELABORAR_DECRECTO_PRESIDENCIAL"))
				q.setParameter("codActividad", "INGRESAR_POSTULANTE");
			else
				q.setParameter("codActividad", actividadEnum.getValor());
			/*************************</MODIFICACION>********************************/
			
			q.setParameter("activo", new Boolean(true));

			List<Actividad> lista = q.getResultList();
			Actividad actividad = lista.get(0);
			return actividad.getTipo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Inicializa el proceso para un processDefinition
	 * 
	 * @param processDefinition
	 *            : nombre del proceso que se desea iniciar
	 * @return
	 */
	public Long initProcess(String processDefinition) {
		// Asignar roles a la siguiente tarea
		roles = seleccionUtilFormController.getRolesTarea(
				actividadSiguiente.getValor(), procesoEnum.getValor());
		if (roles == null || roles.length() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"No se encontraron roles asignados para la tarea: "
							+ actividadSiguiente.getDescripcion());
			return null;
		}

		try {
			if (transition != null) {
				CustomBusinessProcess bp = CustomBusinessProcess.instance();
				bp.createProcessAndTransition(processDefinition, transition);

				return bp.getProcessId();
			} else {
				BusinessProcess bp = BusinessProcess.instance();
				bp.createProcess(processDefinition);

				return bp.getProcessId();
			}

		} catch (DelegationException de) {
			de.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages
					.add(Severity.FATAL,
							"Ocurrio un error al iniciar el proceso. Consulte con el administrador del sistema.");
		}
		return null;
	}

	private boolean nextTaskGroup(Excepcion excepcion) {
		// Se pasa a la siguiente tarea para un grupo en particular
		try {
			BusinessProcess bp = BusinessProcess.instance();
			bp.resumeProcess(excepcion.getIdProcessInstance());
			Long idTaskInstance = jbpmUtilFormController
					.getIdTaskInstanceActual(excepcion.getIdProcessInstance());
			bp.resumeTask(idTaskInstance);
			if (bp != null && bp.getProcessId() != null) {
				try {
					if (transition == null || "".equals(transition))
						bp.endTask("next");
					else
						bp.endTask(transition);
				} catch (DelegationException de) {
					// de.printStackTrace();
				}
				JbpmTaskinstance jbpmTaskinstance = em.find(
						JbpmTaskinstance.class, idTaskInstance);
				jbpmTaskinstance
						.setUserend(usuarioLogueado != null ? usuarioLogueado
								.getCodigoUsuario() : "SYSTEM");
				em.merge(jbpmTaskinstance);

				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages
					.add(Severity.FATAL,
							"Ocurrio un error al finalizar la tarea. Consulte con el administrador del sistema.");
		}
		return false;
	}

	/**
	 * Asigna los roles necesarios a la tarea
	 */
	public String asignarRolesTarea() {
		if (procesoEnum == null) {
			procesoEnum = ProcesoEnum.CONCURSO; // Por defecto concurso CPO
		}

		roles = seleccionUtilFormController.getRolesTarea(
				actividadSiguiente.getValor(), procesoEnum.getValor());
		if (roles == null || roles.length() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"No se encontraron roles asignados para la tarea: "
							+ actividadSiguiente.getDescripcion());
		}
		return roles;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setActividadActual(ActividadEnum actividadActual) {
		this.actividadActual = actividadActual;
	}

	public ActividadEnum getActividadActual() {
		return actividadActual;
	}

	public void setActividadSiguiente(ActividadEnum actividadSiguiente) {
		this.actividadSiguiente = actividadSiguiente;
	}

	public ActividadEnum getActividadSiguiente() {
		return actividadSiguiente;
	}

	public void setTransition(String transition) {
		this.transition = transition;
	}

	public String getTransition() {
		return transition;
	}

	/**
	 * Fuerza a que la tarea sea tratada como grupo y no por concurso
	 * 
	 * @return
	 */
	public Boolean getActividadPorGrupo() {
		return actividadPorGrupo;
	}

	public void setActividadPorGrupo(Boolean actividadPorGrupo) {
		this.actividadPorGrupo = actividadPorGrupo;
	}

	public void setProcesoEnum(ProcesoEnum procesoEnum) {
		this.procesoEnum = procesoEnum;
	}

	public ProcesoEnum getProcesoEnum() {
		return procesoEnum;
	}

	public Excepcion getExcepcion() {
		return excepcion;
	}

	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}

	public Capacitaciones getCapacitacion() {
		return capacitacion;
	}

	public void setCapacitacion(Capacitaciones capacitacion) {
		this.capacitacion = capacitacion;
	}

	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}

	public void setEvaluacionDesempeno(EvaluacionDesempeno evaluacionDesempeno) {
		this.evaluacionDesempeno = evaluacionDesempeno;
	}

	public SolicitudTrasladoCab getSolicitudTrasladoCab() {
		return solicitudTrasladoCab;
	}

	public void setSolicitudTrasladoCab(SolicitudTrasladoCab solicitudTrasladoCab) {
		this.solicitudTrasladoCab = solicitudTrasladoCab;
	}
	
	

}
