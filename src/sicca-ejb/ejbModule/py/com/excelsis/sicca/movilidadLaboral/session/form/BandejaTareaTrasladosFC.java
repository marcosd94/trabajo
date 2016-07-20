package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;

import enums.ActividadEnum;
import py.com.excelsis.sicca.entity.Actividad;
import py.com.excelsis.sicca.entity.BandejaTraslado;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SolicitudTrasladoCab;
import py.com.excelsis.sicca.movilidadLaboral.session.BandejaTrasladoList;
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
@Name("bandejaTareaTrasladosFC")
public class BandejaTareaTrasladosFC {
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
	BandejaTrasladoList bandejaTrasladoList;
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

	private List<BandejaTraslado> listaBandeja = new ArrayList<BandejaTraslado>();
	private List<SelectItem> tipoSolicitud = new ArrayList<SelectItem>();
	private NivelEntidadOeeUtil nivelEntidadOeeOrigen = new NivelEntidadOeeUtil();
	private NivelEntidadOeeUtil nivelEntidadOeeDestino = new NivelEntidadOeeUtil();
	private String actividad;
	private String tarea;

	private Boolean esAdministradorSFP = false;
	private Long idRol;
	private Long idTipoSol;
	private Long idSolicitudMov;

	public void init() {

		if (nivelEntidadOeeUtil == null
			|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		cargarOees();
		cargarSelectItems();
		search();
	}

	private void cargarOees() {
		nivelEntidadOeeOrigen.setEm(em);
		nivelEntidadOeeDestino.setEm(em);
		nivelEntidadOeeOrigen.setVarDestinoConfigUo("configuracionUoCabIdConfiguracionUo1");

		nivelEntidadOeeOrigen.setIdSinNivelEntidad(nivelEntidadOeeUtil.getIdSinNivelEntidad());
		nivelEntidadOeeOrigen.setIdSinEntidad(nivelEntidadOeeUtil.getIdSinEntidad());
		nivelEntidadOeeOrigen.init();

		nivelEntidadOeeDestino.setVarDestinoConfigUo("configuracionUoCabIdConfiguracionUo2");

		nivelEntidadOeeDestino.setIdSinNivelEntidad(nivelEntidadOeeUtil.getIdSinNivelEntidad());
		nivelEntidadOeeDestino.setIdSinEntidad(nivelEntidadOeeUtil.getIdSinEntidad());
		nivelEntidadOeeDestino.init();

	}

	private void cargarSelectItems() {
		String sql =
			"SELECT espec.* "
				+ "FROM seleccion.datos_generales gral "
				+ "INNER JOIN seleccion.datos_especificos espec ON gral.id_datos_generales = espec.id_datos_generales "
				+ "WHERE gral.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD' AND espec.descripcion LIKE 'TRASLADO%'";
		List<DatosEspecificos> l =
			em.createNativeQuery(sql, DatosEspecificos.class).getResultList();
		tipoSolicitud = new Vector<SelectItem>();
		tipoSolicitud.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : l)
			tipoSolicitud.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
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

	public void search() {

		String query = getQuery();
		if (!listaBandeja.isEmpty())
			listaBandeja.clear();
		listaBandeja = bandejaTrasladoList.listaResultados(query);
	}

	public void searchAll() {

		idRol = null;
		idTipoSol = null;
		nivelEntidadOeeUtil.limpiar();
		nivelEntidadOeeOrigen.limpiar();
		nivelEntidadOeeDestino.limpiar();
		tarea = null;
		cargarCabecera();

		String query = getQuery();
		bandejaTrasladoList.listaResultados(query);
	}

	public String getQuery() {
		String query =
			"select distinct bandejaTraslado " + " from BandejaTraslado bandejaTraslado "
				+ " join bandejaTraslado.actividadProceso actividadProceso "
				+ " join actividadProceso.procActividadRols procActividadRol "
				+ " join procActividadRol.rol rol ";

		// WHERE
		query +=
			" where (bandejaTraslado.usuario is null or bandejaTraslado.usuario = '"
				+ usuarioLogueado.getCodigoUsuario() + "') ";
		
		//if(!usuarioLogueado.getConfiguracionUoCab().getDenominacionUnidad().equalsIgnoreCase("SECRETARÍA DE LA FUNCIÓN PÚBLICA")){

			if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
				query +=
					" and bandejaTraslado.idSinNivelEntidad = "
							+ nivelEntidadOeeUtil.getIdSinNivelEntidad() + " ";

			if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
				query +=
					" and bandejaTraslado.idSinEntidad = " + nivelEntidadOeeUtil.getIdSinEntidad()
						+ " ";
			/*if (nivelEntidadOeeOrigen.getIdConfigCab() != null)
				query +=
					" and bandejaTraslado.idOeeOrigen = " + nivelEntidadOeeOrigen.getIdConfigCab()
						+ " ";

			if (nivelEntidadOeeDestino.getIdConfigCab() != null)
				query +=
					" and bandejaTraslado.idOeeDestino = " + nivelEntidadOeeDestino.getIdConfigCab()
						+ " ";*/
			if (usuarioLogueado.getConfiguracionUoCab() != null)
				query +=
					" and (bandejaTraslado.idOeeOrigen = " + usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo()
						+ 
					" or bandejaTraslado.idOeeDestino = " + usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo()
						+ ") ";
		//}
		
		if (idRol != null) {
			query += " and rol.idRol = " + idRol + " ";
		} else {
			String roles = getIdRolesUsuario();
			query += " and rol.idRol in (" + roles + ") ";
		}
		if (idTipoSol != null)
			query +=
				" and bandejaTraslado.tipoTraslado = '"
					+ em.find(DatosEspecificos.class, idTipoSol).getDescripcion() + "' ";
		if (tarea != null && !tarea.trim().isEmpty())
			query +=
				" and lower(bandejaTraslado.actividad) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(tarea.toLowerCase())
					+ "','%')))";

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

	public String redirect() {
		if (actividad != null && !"".equals(actividad)) {
			if (ActividadEnum.MOV_REGISTRAR_TRASLADADO_POR_CONCURSO.getValor().equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/movilidadLaboral/trasladoConcurso/TrasladoPorConcurso734Edit.seam?idSolicitud="
					+ idSolicitudMov + "&from=/movilidadLaboral/bandeja/BandejaTareaTraslados";
			}

			if (ActividadEnum.MOV_RESPONDER_TRASLADO.getValor().equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/movilidadLaboral/respuestaSolicitudTranslado/RespuestaSolicitudTranslado732.seam?idSolicitud="
					+ idSolicitudMov + "&from=/movilidadLaboral/bandeja/BandejaTareaTraslados";
			}
			if (ActividadEnum.MOV_REVISAR_SOLICITUD_SFP.getValor().equalsIgnoreCase(actividad)) {
				// 2 actividad
				return "/movilidadLaboral/rptaSolicitudSfp/RptaSolicitudSfpCU733.seam?idSolicitud="
					+ idSolicitudMov;
			}
			if (ActividadEnum.MOV_REGISTRAR_TRASLADO_TEMPORAL.getValor().equalsIgnoreCase(actividad)) {
				// 3 actividad
				return "/movilidadLaboral/transladoTmpConSolicitud/TransladoConSolic662Edit.seam?idSolicitud="
					+ idSolicitudMov + "&from=/movilidadLaboral/bandeja/BandejaTareaTraslados";
			}
			if (ActividadEnum.MOV_REGISTRAR_TRASLADO_SIN_LINEA.getValor().equalsIgnoreCase(actividad)) {
				// 4 actividad
				// 5 actividad
				return "/movilidadLaboral/transladoDefSinLineaConSolic/TransladoSinLineaConSolic673Edit.seam?idSolicitud="
					+ idSolicitudMov;
			}

			if (ActividadEnum.MOV_REGISTRAR_TRASLADO_CON_LINEA.getValor().equalsIgnoreCase(actividad)) {
				// 5 actividad
				return "/movilidadLaboral/trasladoDefConLineaConSol/TrasladoDefConLineaConSolCU674.seam?idSolicitud="
					+ idSolicitudMov;
			}
		}
		return null;
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

	public Long getIdRol() {
		return idRol;
	}

	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}

	public List<BandejaTraslado> getListaBandeja() {
		return listaBandeja;
	}

	public void setListaBandeja(List<BandejaTraslado> listaBandeja) {
		this.listaBandeja = listaBandeja;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeOrigen() {
		return nivelEntidadOeeOrigen;
	}

	public void setNivelEntidadOeeOrigen(NivelEntidadOeeUtil nivelEntidadOeeOrigen) {
		this.nivelEntidadOeeOrigen = nivelEntidadOeeOrigen;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeDestino() {
		return nivelEntidadOeeDestino;
	}

	public void setNivelEntidadOeeDestino(NivelEntidadOeeUtil nivelEntidadOeeDestino) {
		this.nivelEntidadOeeDestino = nivelEntidadOeeDestino;
	}

	public List<SelectItem> getTipoSolicitud() {
		return tipoSolicitud;
	}

	public void setTipoSolicitud(List<SelectItem> tipoSolicitud) {
		this.tipoSolicitud = tipoSolicitud;
	}

	public Long getIdTipoSol() {
		return idTipoSol;
	}

	public void setIdTipoSol(Long idTipoSol) {
		this.idTipoSol = idTipoSol;
	}

	public Long getIdSolicitudMov() {
		return idSolicitudMov;
	}

	public void setIdSolicitudMov(Long idSolicitudMov) {
		this.idSolicitudMov = idSolicitudMov;
	}
	
	public Boolean gestionar(String actividad, Long idSolicitudTrasladoCab) {
		
		Actividad act = new Actividad();
		SolicitudTrasladoCab solicitudTraslado = new SolicitudTrasladoCab();

		solicitudTraslado = em.find(SolicitudTrasladoCab.class, idSolicitudTrasladoCab);
		
		if (ActividadEnum.MOV_REGISTRAR_TRASLADADO_POR_CONCURSO.getDescripcion().equalsIgnoreCase(actividad)) {
			return true;
		}
		if (ActividadEnum.MOV_RESPONDER_TRASLADO.getDescripcion().equalsIgnoreCase(actividad)){
			if(solicitudTraslado.getOeeOrigen().getDenominacionUnidad().equals(usuarioLogueado.getConfiguracionUoCab().getDenominacionUnidad())) {
				return true;
			}
		}
		if (ActividadEnum.MOV_REVISAR_SOLICITUD_SFP.getDescripcion().equalsIgnoreCase(actividad)){
			if(usuarioLogueado.getConfiguracionUoCab().getDenominacionUnidad().equals("SECRETARÍA DE LA FUNCIÓN PÚBLICA")) {
				return true;
			}
		}
		if (ActividadEnum.MOV_REGISTRAR_TRASLADO_TEMPORAL.getDescripcion().equalsIgnoreCase(actividad)){ 
			if(solicitudTraslado.getOeeDestino().getDenominacionUnidad().equals(usuarioLogueado.getConfiguracionUoCab().getDenominacionUnidad())) {
				return true;
			}
		}
		if (ActividadEnum.MOV_REGISTRAR_TRASLADO_SIN_LINEA.getDescripcion().equalsIgnoreCase(actividad)){
			if(solicitudTraslado.getOeeDestino().getDenominacionUnidad().equals(usuarioLogueado.getConfiguracionUoCab().getDenominacionUnidad())) {
				return true;
			}
		}
		if (ActividadEnum.MOV_REGISTRAR_TRASLADO_CON_LINEA.getDescripcion().equalsIgnoreCase(actividad)){ 
			if(solicitudTraslado.getOeeDestino().getDenominacionUnidad().equals(usuarioLogueado.getConfiguracionUoCab().getDenominacionUnidad())) {
				return true;
			}
		}
		return false;
	}

}
