package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import enums.ActividadEnum;
import enums.TiposDatos;

import py.com.excelsis.sicca.capacitacion.session.HistorialCircuitoCapList;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.HistorialCircuitoCap;
import py.com.excelsis.sicca.entity.ObsCapEval;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("historialCircuitoCapFC")
public class HistorialCircuitoCapFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	HistorialCircuitoCapList historialCircuitoCapList;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Long idCapacitacion;
	private String from;
	private String estado;
	private String actividad;

	private Capacitaciones capacitacion = new Capacitaciones();
	private List<HistorialCircuitoCap> listaHistorial = new ArrayList<HistorialCircuitoCap>();

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idCapacitacion != null) {
			if (!sufc.validarInput(idCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			capacitacion = em.find(Capacitaciones.class, idCapacitacion);
			buscarEstadoCapacitacion();
			cargarDatosNivel();
			search();

		}
	}

	private void buscarEstadoCapacitacion() {
		String sql = "select r.* " + "from seleccion.referencias r  "
				+ "where dominio = 'ESTADOS_CAPACITACION'  "
				+ "and activo is true " + "and r.valor_num = "
				+ capacitacion.getEstado();
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

	public void search() {

		String query = getQuery();
		if (!listaHistorial.isEmpty())
			listaHistorial.clear();

		listaHistorial = historialCircuitoCapList.listaResultados(query);

	}

	public String getQuery() {
		String query = "select distinct historial "
				+ " from HistorialCircuitoCap historial join historial.capacitaciones cap";
		if (idCapacitacion != null)
			query += " where cap.idCapacitacion = " + idCapacitacion;
		return query;
	}

	public String redirect(String act) {
		/*
		 * Aca se verifica la actividad para renderearlo
		 */
		actividad = act;
		if (actividad != null && !"".equals(actividad)) {
			if (ActividadEnum.CAPA_CARGAR_CAPACITACION_ENVIAR_APROB.getValor()
					.equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/capacitacion/datosCapacitacion/VerGestionDatosCapacitacion.seam?capacitacionesIdCapacitacion="
				+ idCapacitacion+"&from=capacitacion/historialCircuito/HistorialCircuitoCapList";
			}
			
		
			if (ActividadEnum.CAPA_APROBAR_CAPACITACION.getValor()
					.equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/capacitacion/aprobarCapacitacion/VerAprobarCapacitacion.seam?from=capacitacion/historialCircuito/HistorialCircuitoCapList&capacitacionesIdCapacitacion="
				+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_REVISAR_CAPACITACION.getValor()
					.equalsIgnoreCase(actividad)) {
				// 3 actividad
				return "/capacitacion/revisionCapacitacion/RevisionCapacitacion.seam?capacitacionesIdCapacitacion="
				+ idCapacitacion+"&from=capacitacion/historialCircuito/HistorialCircuitoCapList";
			}
			if (ActividadEnum.CAPA_PUBLICAR_CAPACITACION.getValor()
					.equalsIgnoreCase(actividad)) {
				// 3 Publicación de Capacitación
				return "/capacitacion/publicarCapacitacion/VerPublicarCapacitacion.seam?from=capacitacion/historialCircuito/HistorialCircuitoCapList&capacitacionesIdCapacitacion="
				+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_RECEPCIONAR_POST_INSC.getValor()
					.equalsIgnoreCase(actividad)) {
				// 5 actividad
				return "/capacitacion/recepcionarPostulante/RecepcionarPostulante.seam?from=capacitacion/historialCircuito/HistorialCircuitoCapList&capacitacionesIdCapacitacion="
				+ idCapacitacion+"&from=capacitacion/historialCircuito/HistorialCircuitoCapList";
			}
			if (ActividadEnum.CAPA_REPROGRAMAR_CANCELAR_CAPAC.getValor()
					.equalsIgnoreCase(actividad)) {
				// 5 Recepcion de Postulación
				return "/capacitacion/reprogramarCapacitacion/VerReprogramarCapacitacion.seam?from=capacitacion/historialCircuito/HistorialCircuitoCapList&capacitacionesIdCapacitacion="
						+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_REGISTRAR_COMISION.getValor()
					.equalsIgnoreCase(actividad)) {
				// 7 actividad
				return "/capacitacion/comision/Comision.seam?capacitacionesIdCapacitacion="
				+ idCapacitacion+"&from=capacitacion/historialCircuito/HistorialCircuitoCapList";
			}
			if (ActividadEnum.CAPA_EVALUAR_POSTULANTES.getValor()
					.equalsIgnoreCase(actividad)) {
				// 8 Recepcion de Postulación
				return "/capacitacion/registrarEvalPostulante/porConcurso/EvaluacionPostulanteRegistrarView.seam?capacitacionesIdCapacitacion="
						+ idCapacitacion+"&from=capacitacion/historialCircuito/HistorialCircuitoCapList";
			}
			if (ActividadEnum.CAPA_INSCRIPCION_LISTA.getValor()
					.equalsIgnoreCase(actividad)) {
				// 9 Inscripcion Lista
				return "/capacitacion/registrarEvalPostulante/registrarEvalPostulanteVer.seam?idCapacitacion="
						+ idCapacitacion+"&from=capacitacion/historialCircuito/HistorialCircuitoCapList";
			}
			if (ActividadEnum.CAPA_PUBLICAR_SELECCIONADOS.getValor()
					.equalsIgnoreCase(actividad)) {
				// 10 Publicar Seleccionados
				return "/capacitacion/publicarSeleccionados/VerPublicarSeleccionados.seam?from=capacitacion/historialCircuito/HistorialCircuitoCapList&capacitacionesIdCapacitacion="
						+ idCapacitacion;
			}
			

			return null;
		}
		return null;

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

	public Capacitaciones getCapacitacion() {
		return capacitacion;
	}

	public void setCapacitacion(Capacitaciones capacitacion) {
		this.capacitacion = capacitacion;
	}

	public List<HistorialCircuitoCap> getListaHistorial() {
		return listaHistorial;
	}

	public void setListaHistorial(List<HistorialCircuitoCap> listaHistorial) {
		this.listaHistorial = listaHistorial;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
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
	
	

}
