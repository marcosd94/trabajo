package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

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
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("admEnvioHomologacionFormController")
public class AdmEnvioHomologacionFormController implements Serializable {

	private static final long serialVersionUID = 1661926467489160485L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	public Observacion observacion;
	private boolean homologacionEnviada = false;

	private String entity = "Observacion";
	private String idEntity = "";
	private String nombrePantalla = "envioGruposHomologacion";
	private String ubicacionFisica = "";
	private String tipo;
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		/*if (tipo == null)
			seguridadUtilFormController.verificarPerteneceOee(
					null,
					grupoPuestosController.getIdConcursoPuestoAgr(),
					seguridadUtilFormController.estadoActividades(
							"ESTADOS_GRUPO", "FINALIZADO CARGA") + "",
					ActividadEnum.ENVIAR_HOMOLOGACION.getValor());*/
		if (tipo != null && tipo.equalsIgnoreCase("CIO"))
			seguridadUtilFormController.verificarPerteneceOeeCIO(
					null,
					grupoPuestosController.getIdConcursoPuestoAgr(),
					seguridadUtilFormController.estadoActividades(
							"ESTADOS_GRUPO", "FINALIZADO CARGA") + "",
					ActividadEnum.CIO_APROBAR_HOMOLOG_SFP.getValor());
		
		if (tipo != null && tipo.equalsIgnoreCase("CSI"))
			seguridadUtilFormController.verificarPerteneceOeeCSI(
					null,
					grupoPuestosController.getIdConcursoPuestoAgr(),
					seguridadUtilFormController.estadoActividades(
							"ESTADOS_GRUPO", "FINALIZADO CARGA") + "",
					ActividadEnum.APROBAR_HOMOLOG_SFP.getValor());
		
		if (!seguridadUtilFormController.validaEstado(grupoPuestosController
				.getConcursoPuestoAgr().getIdConcursoPuestoAgr(), "FINALIZADO CARGA")) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	public void init() {
		cargar();
	}

	public void limpiar() {
		observacion = null;
		homologacionEnviada = false;
	}

	public void cargar() {
		ConcursoPuestoAgr concursoPuestoAgr = grupoPuestosController
				.getConcursoPuestoAgr();
		validarOee();
		long idTaskInstance = jbpmUtilFormController
				.getIdTaskInstanceActual(concursoPuestoAgr
						.getIdProcessInstance());
		observacion = cargarObservacion(idTaskInstance);

		if (observacion == null) {
			observacion = new Observacion();
			homologacionEnviada = false;
		} else {
			homologacionEnviada = true;
		}

		Calendar c = Calendar.getInstance();
		ubicacionFisica = "\\SICCA\\"
				+ c.get(Calendar.YEAR)
				+ "\\OEE\\"
				+ concursoPuestoAgr.getConcurso().getConfiguracionUoCab()
						.getIdConfiguracionUo()
				+ "\\"
				+ concursoPuestoAgr.getConcurso().getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + "\\"
				+ concursoPuestoAgr.getConcurso().getIdConcurso() + "\\"
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	public Observacion cargarObservacion(long idTaskInstance) {
		try {
			String cad = " select o.* " + " from seleccion.observacion o "
					+ " where id_task_instance = " + idTaskInstance + " ";

			List<Observacion> lista = em.createNativeQuery(cad,
					Observacion.class).getResultList();
			if (lista.size() > 0)
				return lista.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public void save() {
		if (observacion.getIdObservacion() == null) {
			// Nuevo
			try {
				ConcursoPuestoAgr concursoPuestoAgr = grupoPuestosController
						.getConcursoPuestoAgr();
				long idTaskInstance = jbpmUtilFormController
						.getIdTaskInstanceActual(concursoPuestoAgr
								.getIdProcessInstance());

				// Guardar Observacion
				observacion.setIdTaskInstance(idTaskInstance);
				observacion.setConcurso(concursoPuestoAgr.getConcurso());
				Date fecha = new Date();
				observacion.setFechaAlta(fecha);
				observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(observacion);

				// Guardar HOMOLOGACION_PERFIL_MATRIZ
				HomologacionPerfilMatriz homologacionPerfilMatriz = new HomologacionPerfilMatriz();
				homologacionPerfilMatriz
						.setConcursoPuestoAgr(concursoPuestoAgr);
				homologacionPerfilMatriz.setFechaAlta(fecha);
				homologacionPerfilMatriz.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());
				homologacionPerfilMatriz.setFechaPresentacion(fecha);

				ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr
						.getConcursoPuestoDets().get(0);
				homologacionPerfilMatriz.setCpt(concursoPuestoDet
						.getPlantaCargoDet().getCpt());
				em.persist(homologacionPerfilMatriz);

				em.flush();
			} catch (Exception e) {

			}
		}
		homologacionEnviada = true;
		statusMessages.clear();
		statusMessages.add(Severity.INFO, "Solicitud de Homologación Enviada");
	}

	public String nextTask(String sgteActividad) {
		try {

			if (observacion.getIdObservacion() == null) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Debe enviar la Solicitud de Homologación antes de pasar a la siguiente tarea. Verifique.");
				return null;
			}

			ConcursoPuestoAgr concursoPuestoAgr = grupoPuestosController
					.getConcursoPuestoAgr();

			// Se actualiza el estado
			Referencias referencias = referenciasUtilFormController
					.getReferencia("ESTADOS_GRUPO", "ENVIADO A HOMOLOGACION");
			concursoPuestoAgr.setEstado(referencias.getValorNum());
			em.merge(concursoPuestoAgr);

			// Se actualiza los estados de las plantas
			EstadoDet estadoDet = seleccionUtilFormController.buscarEstadoDet(
					"CONCURSO", "ENVIADO A HOMOLOGACION");
			List<ConcursoPuestoDet> lista = concursoPuestoAgr
					.getConcursoPuestoDets();
			if (lista != null && lista.size() > 0) {
				for (ConcursoPuestoDet concursoPuestoDet : lista) {
					PlantaCargoDet plantaCargoDet = concursoPuestoDet
							.getPlantaCargoDet();
					plantaCargoDet.setEstadoDet(estadoDet);
					em.merge(plantaCargoDet);
					/* incidencia 779 */
					concursoPuestoDet.setEstadoDet(estadoDet);
					em.merge(concursoPuestoDet);
					/*******/
				}
			}

			// Se pasa a la siguiente tarea
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
			/*if (sgteActividad.equalsIgnoreCase("APROBAR_HOMOLOG_SFP")) {
				jbpmUtilFormController
						.setActividadActual(ActividadEnum.ENVIAR_HOMOLOGACION);
				jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.APROBAR_HOMOLOG_SFP);
			} else*/ if (sgteActividad.equalsIgnoreCase("APROBAR_HOMOLOG_SFP_S")) {
				jbpmUtilFormController
						.setActividadActual(ActividadEnum.APROBAR_HOMOLOG_SFP);
				jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.CSI_APROBAR_HOMOLOG_SFP);
			} else if (sgteActividad.equalsIgnoreCase("APROBAR_HOMOLOG_SFP_I")) {
				jbpmUtilFormController
						.setActividadActual(ActividadEnum.APROBAR_HOMOLOG_SFP);
				jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.CIO_APROBAR_HOMOLOG_SFP);
			}

			jbpmUtilFormController.setTransition("next");

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}

			return "nextTask";

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Observacion getObservacion() {
		return observacion;
	}

	public void setObservacion(Observacion observacion) {
		this.observacion = observacion;
	}

	public boolean isHomologacionEnviada() {
		return homologacionEnviada;
	}

	public void setHomologacionEnviada(boolean homologacionEnviada) {
		this.homologacionEnviada = homologacionEnviada;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(String idEntity) {
		this.idEntity = idEntity;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
