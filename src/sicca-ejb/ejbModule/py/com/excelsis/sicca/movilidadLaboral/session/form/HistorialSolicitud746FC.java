package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import enums.ActividadEnum;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.CabeceraHistoricoMovilidad;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.HistoricoMovilidad;
import py.com.excelsis.sicca.entity.SolicitudTrasladoCab;
import py.com.excelsis.sicca.movilidadLaboral.session.HistoricoMovilidadList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("historialSolicitud746FC")
public class HistorialSolicitud746FC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	HistoricoMovilidadList historicoMovilidadList;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Long idSolicitud;
	private String from;
	private String estado;
	private String actividad;
	private CabeceraHistoricoMovilidad cabeceraHistorico= new CabeceraHistoricoMovilidad();

	private SolicitudTrasladoCab solicitudTrasladoCab = new SolicitudTrasladoCab();
	private List<HistoricoMovilidad> listaHistorial = new ArrayList<HistoricoMovilidad>();

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idSolicitud != null) {
			if (!sufc.validarInput(idSolicitud.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			solicitudTrasladoCab = em.find(SolicitudTrasladoCab.class, idSolicitud);
			cargarCabecera();
			search();

		}
	}


	public void search() {

		String query = getQuery();
		if (!listaHistorial.isEmpty())
			listaHistorial.clear();
		
		String sqlHistorico = "select * from movilidad.historico_movilidad where historico_movilidad.id_solicitud_traslado_cab = "+idSolicitud;
		Query qHistorial = em.createNativeQuery(sqlHistorico, HistoricoMovilidad.class);
		listaHistorial = qHistorial.getResultList();
		//listaHistorial = historicoMovilidadList.listaResultados();

	}

	public String getQuery() {
		String query = "select historial "
				+ " from HistoricoMovilidad historial ";
		if (idSolicitud != null)
			query += " where historial.idSolicitudTrasladoCab = " + idSolicitud;
		return query;
	}

	public String redirect(String act) {
		
		
		/*
		 * Aca se verifica la actividad para renderearlo
		 */
		actividad = act;
		if (actividad != null && !"".equals(actividad)) {
			if (ActividadEnum.MOV_REGISTRAR_TRASLADADO_POR_CONCURSO.getValor().equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/movilidadLaboral/trasladoConcurso/TrasladoPorConcurso734.seam?idSolicitud="
					+ idSolicitud + "&from=/movilidadLaboral/historialSolicitud/HistorialSolicitud746";
			}

			if (ActividadEnum.MOV_RESPONDER_TRASLADO.getValor().equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/movilidadLaboral/respuestaSolicitudTranslado/RespuestaSolicitudTranslado732Ver.seam?idSolicitud="
					+ idSolicitud + "&from=/movilidadLaboral/historialSolicitud/HistorialSolicitud746";
			}
			if (ActividadEnum.MOV_REVISAR_SOLICITUD_SFP.getValor().equalsIgnoreCase(actividad)) {
				// 2 actividad
				return "/movilidadLaboral/rptaSolicitudSfp/VerRptaSolicitudSfpCU733.seam?idSolicitud="
					+ idSolicitud;
			}
			if (ActividadEnum.MOV_REGISTRAR_TRASLADO_TEMPORAL.getValor().equalsIgnoreCase(actividad)) {
				// 3 actividad
				return "/movilidadLaboral/transladoTmpConSolicitud/TransladoConSolic662.seam?idSolicitud="
					+ idSolicitud+ "&from=/movilidadLaboral/historialSolicitud/HistorialSolicitud746";
			}
			if (ActividadEnum.MOV_REGISTRAR_TRASLADO_SIN_LINEA.getValor().equalsIgnoreCase(actividad)) {
				// 4 actividad
				// 5 actividad
				return "/movilidadLaboral/transladoDefSinLineaConSolic/TransladoSinLineaConSolic673.seam?idSolicitud="
					+ idSolicitud;
			}

			if (ActividadEnum.MOV_REGISTRAR_TRASLADO_CON_LINEA.getValor().equalsIgnoreCase(actividad)) {
				// 5 actividad
				return "/movilidadLaboral/trasladoDefConLineaConSol/VerTrasladoDefConLineaConSolCU674.seam?idSolicitud="
					+ idSolicitud;
			}
			

			return null;
		}
		return null;

	}

	private void cargarDatosNivel(Long idOee) {
		nivelEntidadOeeUtil.setIdConfigCab(idOee);
		nivelEntidadOeeUtil.init2();
	}
	
	@SuppressWarnings("unchecked")
	private void cargarCabecera(){
		cabeceraHistorico= new CabeceraHistoricoMovilidad();
		String sql=" select * from movilidad.cabecera_historico_movilidad where id_solicitud_traslado_cab=:id";
		List<Object> result = em.createNativeQuery(sql).setParameter("id", idSolicitud).getResultList();
		if (!result.isEmpty()) {
			for (Object obj : (List<Object>) result) {
				Object[] record = (Object[]) obj;
				
				if(record[1]!=null)
					cargarDatosNivel(Long.parseLong(record[1].toString()));
				if(record[2]!=null)
					cabeceraHistorico.setFechaAlta((Date)record[2]);
				if(record[4]!=null)
					cabeceraHistorico.setUsuAlta((String)record[4]);
				if(record[6]!=null)
					cabeceraHistorico.setTipoTraslado((String)record[6]);
				if(record[7]!=null)
					cabeceraHistorico.setEstado((String)record[7]);
				if(record[13]!=null)
					cabeceraHistorico.setFuncionario((String)record[13]);
				
			}
		}
	}

	

	

	public Long getIdSolicitud() {
		return idSolicitud;
	}

	public void setIdSolicitud(Long idSolicitud) {
		this.idSolicitud = idSolicitud;
	}

	public SolicitudTrasladoCab getSolicitudTrasladoCab() {
		return solicitudTrasladoCab;
	}

	public void setSolicitudTrasladoCab(SolicitudTrasladoCab solicitudTrasladoCab) {
		this.solicitudTrasladoCab = solicitudTrasladoCab;
	}

	



	public List<HistoricoMovilidad> getListaHistorial() {
		return listaHistorial;
	}

	public void setListaHistorial(List<HistoricoMovilidad> listaHistorial) {
		this.listaHistorial = listaHistorial;
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


	public CabeceraHistoricoMovilidad getCabeceraHistorico() {
		return cabeceraHistorico;
	}


	public void setCabeceraHistorico(CabeceraHistoricoMovilidad cabeceraHistorico) {
		this.cabeceraHistorico = cabeceraHistorico;
	}
	
	

}
