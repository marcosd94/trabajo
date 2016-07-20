package py.com.excelsis.sicca.capacitacion.session.form;

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
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.MovUsuariosTarea;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.BandejaEntradaList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("gestionarBandejaTareaCapacitacionFC")
public class GestionarBandejaTareaCapacitacionFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -784906397449872285L;
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
	List<BandejaCapacitacion> listaBandeja = new ArrayList<BandejaCapacitacion>();

	private String descCapacitacion;
	private String mensaje;
	private String actividad;
	private String tarea;

	private Boolean esAdministradorSFP = false;
	private Boolean mostrarResultado;

	private Long idRol;
	private Long idCapacitacion;

	public void init() {

		if (nivelEntidadOeeUtil == null || (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}

		if (mensaje != null)
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		search();
	}

	public Boolean gestionar(Long idActividad, Long idUnidOrg) {
		Actividad act = new Actividad();
		act = em.find(Actividad.class, idActividad);
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() == null)
			return false;
		Long idUnidOrgUsuario = usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet();
		Long auxIdUnidOrg = idUnidOrg;
		if(idActividad.intValue() == 103){
			while ((auxIdUnidOrg != 0) || (auxIdUnidOrg != null)){
				if(idUnidOrgUsuario.equals(auxIdUnidOrg)){
					return true;
				}
				auxIdUnidOrg = em.find(ConfiguracionUoDet.class, auxIdUnidOrg).getConfiguracionUoDet().getIdConfiguracionUoDet();
			}
		}
		else{
			//if (idUnidOrg.equals(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet()))
			if(idUnidOrgUsuario.equals(auxIdUnidOrg))
				return true;
		}
		if (act.getValor())
			return true;
		return false;
	}

	private MovUsuariosTarea usuarioAMover(Long processInstanceId) {
		String cad =
			"select mov.* " + "from capacitacion.mov_usuarios_tarea mov "
				+ "where mov.id_process_instance = " + processInstanceId
				+ " order by mov.fecha_alta desc limit 1";

		List<MovUsuariosTarea> l = new ArrayList<MovUsuariosTarea>();

		l = em.createNativeQuery(cad, MovUsuariosTarea.class).getResultList();
		if (l != null && l.size() > 0)
			return l.get(0);
		return null;
	}

	public String redirect() {
		if (actividad != null && !"".equals(actividad)) {

			Capacitaciones capacitacion = em.find(Capacitaciones.class, idCapacitacion);
			String tareaAsignada =
				jbpmUtilFormController.tareaActualLiberada(capacitacion.getIdProcessInstance(), usuarioLogueado.getCodigoUsuario());
			if (tareaAsignada.equals("error")) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Ocurrio un error al inicializar la tarea. Consulte con el administrador del sistema.");
				return null;
			}
			if (tareaAsignada.equals("liberado")) {
				MovUsuariosTarea mov = new MovUsuariosTarea();
				mov = usuarioAMover(capacitacion.getIdProcessInstance());
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
				if (ActividadEnum.CAPA_CARGAR_CAPACITACION_ENVIAR_APROB.getValor().equalsIgnoreCase(actividad)) {
					// 1 actividad
					return "/capacitacion/datosCapacitacion/GestionarDatosCapacitacion.seam?capacitacionesIdCapacitacion="
						+ idCapacitacion;
				}

				if (ActividadEnum.CAPA_APROBAR_CAPACITACION.getValor().equalsIgnoreCase(actividad)) {
					// 2 Aprobación de Capacitación
					return "/capacitacion/aprobarCapacitacion/AprobarCapacitacion.seam?capacitacionesIdCapacitacion="
						+ idCapacitacion;
				}

				if (ActividadEnum.CAPA_PUBLICAR_CAPACITACION.getValor().equalsIgnoreCase(actividad)) {
					// 3 Publicación de Capacitación
					return "/capacitacion/publicarCapacitacion/PublicarCapacitacion.seam?capacitacionesIdCapacitacion="
						+ idCapacitacion;
				}
				if (ActividadEnum.CAPA_REVISAR_CAPACITACION.getValor().equalsIgnoreCase(actividad)) {
					// 3 Revision Capacitacion
					return "/capacitacion/revisionCapacitacion/RevisionCapacitacionEdit.seam?capacitacionesIdCapacitacion="
						+ idCapacitacion;
				}
				if (ActividadEnum.CAPA_RECEPCIONAR_POST_INSC.getValor().equalsIgnoreCase(actividad)) {
					// 5 Recepcion de Postulación
					return "/capacitacion/recepcionarPostulante/RecepcionarPostulanteEdit.seam?capacitacionesIdCapacitacion="
						+ idCapacitacion;
				}
				if (ActividadEnum.CAPA_REGISTRAR_COMISION.getValor().equalsIgnoreCase(actividad)) {
					// 7 actividad
					return "/capacitacion/comision/ComisionEdit.seam?capacitacionesIdCapacitacion="
						+ idCapacitacion;
				}
				if (ActividadEnum.CAPA_REPROGRAMAR_CANCELAR_CAPAC.getValor().equalsIgnoreCase(actividad)) {
					// 5 Recepcion de Postulación
					return "/capacitacion/reprogramarCapacitacion/ReprogramarCapacitacion.seam?capacitacionesIdCapacitacion="
						+ idCapacitacion;
				}
				if (ActividadEnum.CAPA_EVALUAR_POSTULANTES.getValor().equalsIgnoreCase(actividad)) {
					// 8 Recepcion de Postulación
					return "/capacitacion/registrarEvalPostulante/porConcurso/EvaluacionPostulanteRegistrar.seam?capacitacionesIdCapacitacion="
						+ idCapacitacion;
				}
				if (ActividadEnum.CAPA_INSCRIPCION_LISTA.getValor().equalsIgnoreCase(actividad)) {
					// 9 Inscripcion Lista
					return "/capacitacion/registrarEvalPostulante/registrarEvalPostulante.seam?idCapacitacion="
						+ idCapacitacion;
				}

				if (ActividadEnum.CAPA_PUBLICAR_SELECCIONADOS.getValor().equalsIgnoreCase(actividad)) {
					// 10 Publicar Seleccionados
					return "/capacitacion/publicarSeleccionados/PublicarSeleccionados.seam?capacitacionesIdCapacitacion="
						+ idCapacitacion;
				}

				return null;
			}

		}
		return null;

	}

	public String redireccionar() {
		if (actividad != null && !"".equals(actividad)) {
			if (ActividadEnum.CAPA_CARGAR_CAPACITACION_ENVIAR_APROB.getValor().equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/capacitacion/datosCapacitacion/VerGestionDatosCapacitacion.seam?capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}

			if (ActividadEnum.CAPA_APROBAR_CAPACITACION.getValor().equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/capacitacion/aprobarCapacitacion/VerAprobarCapacitacion.seam?from=capacitacion/bandeja/gestionarBandeja/BandejaCapacitacionList&capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_REVISAR_CAPACITACION.getValor().equalsIgnoreCase(actividad)) {
				// 3 actividad
				return "/capacitacion/revisionCapacitacion/RevisionCapacitacion.seam?capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_PUBLICAR_CAPACITACION.getValor().equalsIgnoreCase(actividad)) {
				// 3 Publicación de Capacitación
				return "/capacitacion/publicarCapacitacion/VerPublicarCapacitacion.seam?from=capacitacion/bandeja/gestionarBandeja/BandejaCapacitacionList&capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_RECEPCIONAR_POST_INSC.getValor().equalsIgnoreCase(actividad)) {
				// 5 actividad
				return "/capacitacion/recepcionarPostulante/RecepcionarPostulante.seam?from=capacitacion/bandeja/gestionarBandeja/BandejaCapacitacionList&capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_REPROGRAMAR_CANCELAR_CAPAC.getValor().equalsIgnoreCase(actividad)) {
				// 5 Recepcion de Postulación
				return "/capacitacion/reprogramarCapacitacion/VerReprogramarCapacitacion.seam?from=capacitacion/bandeja/gestionarBandeja/BandejaCapacitacionList&capacitacionesIdCapacitacion="
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
					+ idCapacitacion;
			}
			if (ActividadEnum.CAPA_PUBLICAR_SELECCIONADOS.getValor().equalsIgnoreCase(actividad)) {
				// 10 Publicar Seleccionados
				return "/capacitacion/publicarSeleccionados/VerPublicarSeleccionados.seam?from=capacitacion/bandeja/gestionarBandeja/BandejaCapacitacionList&capacitacionesIdCapacitacion="
					+ idCapacitacion;
			}

			return null;
		}
		return null;
	}

	public void search() {

		String query = getQuery();
		if (!listaBandeja.isEmpty())
			listaBandeja.clear();
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
		mostrarResultado = true;
		/*if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null) {
			//mostrarResultado = true;
			query += " join bandejaCapacitacion.configuracionUoDet configuracionUoDet ";
		}*/

		// WHERE
		/*query +=
			" where (bandejaCapacitacion.usuario is null or bandejaCapacitacion.usuario = '"
				+ usuarioLogueado.getCodigoUsuario() + "') ";*/
		query += " where ";
		
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
			query +=
				" bandejaCapacitacion.idSinNivelEntidad = "
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

		if (descCapacitacion != null) {

			query +=
				" and lower(bandejaCapacitacion.nombreCapac) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(descCapacitacion.toLowerCase())
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
		// if (!validar(false))
		// return;

		idRol = null;
		descCapacitacion = null;
		nivelEntidadOeeUtil.limpiar();
		tarea = null;
		cargarCabecera();

		String query = getQuery();
		bandejaCapacitacionList.listaResultados(query);
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
		if (usuarioLogueado.getConfiguracionUoDet() != null && usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet() != null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());

		nivelEntidadOeeUtil.init();
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

	public String getDescCapacitacion() {
		return descCapacitacion;
	}

	public void setDescCapacitacion(String descCapacitacion) {
		this.descCapacitacion = descCapacitacion;
	}

	public Boolean getMostrarResultado() {
		return mostrarResultado;
	}

	public void setMostrarResultado(Boolean mostrarResultado) {
		this.mostrarResultado = mostrarResultado;
	}

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
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
