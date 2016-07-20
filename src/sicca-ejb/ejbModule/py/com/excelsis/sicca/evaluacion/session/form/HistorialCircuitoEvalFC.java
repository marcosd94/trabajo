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
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;
import enums.TiposDatos;

import py.com.excelsis.sicca.capacitacion.session.HistorialCircuitoCapList;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.HistorialCircuitoCap;
import py.com.excelsis.sicca.entity.HistoricoEvaluacion;
import py.com.excelsis.sicca.entity.MovUsuariosTarea;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.evaluacion.session.HistoricoEvaluacionList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("historialCircuitoEvalFC")
public class HistorialCircuitoEvalFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	HistoricoEvaluacionList historicoEvaluacionList;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Long idEvaluacion;
	private String from;
	private String estado;
	private String actividad;

	private EvaluacionDesempeno evaluacionDesempeno = new EvaluacionDesempeno();
	private List<HistoricoEvaluacion> listaHistorial = new ArrayList<HistoricoEvaluacion>();

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idEvaluacion != null) {
			if (!sufc.validarInput(idEvaluacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			evaluacionDesempeno = em.find(EvaluacionDesempeno.class,
					idEvaluacion);
			buscarEstadoCapacitacion();
			cargarDatosNivel();
			search();

		}
	}

	public void search() {

		String query = getQuery();
		if (!listaHistorial.isEmpty())
			listaHistorial.clear();

		listaHistorial = historicoEvaluacionList.listaResultados(query);

	}

	public String getQuery() {
		String query = "select distinct historial "
				+ " from HistoricoEvaluacion historial ";
		if (idEvaluacion != null)
			query += " where historial.idEvaluacion = " + idEvaluacion;
		return query;
	}

	private void cargarDatosNivel() {
//		if (nivelEntidadOeeUtil == null
//				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
//						.getNombreNivelEntidad() == null)) {
//			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
//					NivelEntidadOeeUtil.class, true);
//			if (evaluacionDesempeno.getConfiguracionUoDet() != null)
//				nivelEntidadOeeUtil.setIdUnidadOrganizativa(evaluacionDesempeno
//						.getConfiguracionUoDet().getIdConfiguracionUoDet());
//		}
		cargarCabecera();
	}

	public void cargarCabecera() {
		ConfiguracionUoCab uoCab = new ConfiguracionUoCab();
		uoCab = em.find(ConfiguracionUoCab.class, evaluacionDesempeno
				.getConfiguracionUoCab()
				.getIdConfiguracionUo());
		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil.getIdSinNivelEntidad(uoCab
				.getEntidad().getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(uoCab.getEntidad().getSinEntidad()
				.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(uoCab.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

	private void buscarEstadoCapacitacion() {
		String sql = "select r.* " + "from seleccion.referencias r  "
				+ "where dominio = 'ESTADOS_EVALUACION_DESEMPENO'  "
				+ "and activo is true " + "and r.valor_num = "
				+ evaluacionDesempeno.getEstado();
		try {
			Referencias ref = new Referencias();
			ref = (Referencias) em.createNativeQuery(sql, Referencias.class)
					.getSingleResult();
			if (ref != null)
				estado = ref.getDescAbrev();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String redirect(String act) {
		/*
		 * Aca se verifica la actividad para renderearlo
		 */
		actividad = act;
		if (actividad != null && !"".equals(actividad)) {

			if (actividad != null && !"".equals(actividad)) {
				
				//Actividad 1
				if (ActividadEnum.EVAL_DESEM_ASIGNAR_PLANTILLA.getValor().equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/metodologiaGrupo/VerAsigMetodologiaGrupo.seam?evaluacionDesempenoIdEvaluacionDesempeno="
					+ idEvaluacion+"&from=evaluacionDesempenho/historialCircuito/HistorialCircuitoList";
				}
				//Actividad 2
				if (ActividadEnum.EVAL_DESEM_GENERAR_RESOLUCION.getValor().equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/gestionarResolucion/GestionarResolucionEvalView.seam?evaluacionDesempenoIdEvaluacionDesempeno="
					+ idEvaluacion+"&from=evaluacionDesempenho/historialCircuito/HistorialCircuitoList";
				}
				// Actividad 3
				if (ActividadEnum.EVAL_DESEM_APROBAR_EVALUACION.getValor()
						.equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/aprobarEvaluacionDesempenho/VerAprobacionEvaluacionDesempenho.seam?evaluacionDesempenoIdEvaluacionDesempeno="
					+ idEvaluacion+"&from=evaluacionDesempenho/historialCircuito/HistorialCircuitoList";
				}

				// Actividad 4.1
				if (ActividadEnum.EVAL_DESEM_REVISAR_EVALUACION.getValor()
						.equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/revisarEvalDesempeno/VerRevisarEvalDesempeno564.seam?evaluacionDesempenoIdEvaluacionDesempeno="
					+ idEvaluacion+"&from=evaluacionDesempenho/historialCircuito/HistorialCircuitoList";

				}

				// Actividad 4.2
				if (ActividadEnum.EVAL_DESEM_ADJUNTAR_RESOLUCION_FIRMADA
						.getValor().equalsIgnoreCase(actividad)) {
					return "/evaluacionDesempenho/adjuntosResolucion/VerAjuntosResolucion.seam?evaluacionDesempenoIdEvaluacionDesempeno="
					+ idEvaluacion+"&from=evaluacionDesempenho/historialCircuito/HistorialCircuitoList";
				}
			}
		}
		return null;
	}

	public Long getIdEvaluacion() {
		return idEvaluacion;
	}

	public void setIdEvaluacion(Long idEvaluacion) {
		this.idEvaluacion = idEvaluacion;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}

	public void setEvaluacionDesempeno(EvaluacionDesempeno evaluacionDesempeno) {
		this.evaluacionDesempeno = evaluacionDesempeno;
	}

	public List<HistoricoEvaluacion> getListaHistorial() {
		return listaHistorial;
	}

	public void setListaHistorial(List<HistoricoEvaluacion> listaHistorial) {
		this.listaHistorial = listaHistorial;
	}

}
