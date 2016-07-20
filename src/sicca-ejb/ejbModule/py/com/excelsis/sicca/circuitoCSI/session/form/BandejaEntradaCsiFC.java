package py.com.excelsis.sicca.circuitoCSI.session.form;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.circuitoCIO.session.BandejaEntradaCioList;
import py.com.excelsis.sicca.circuitoCSI.session.BandejaEntradaCsiList;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;

@Scope(ScopeType.CONVERSATION)
@Name("bandejaEntradaCsiFC")
public class BandejaEntradaCsiFC {
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
	BandejaEntradaCsiList bandejaEntradaCsiList;
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

	private String concursoDesc;
	private String actividadDesc;
	private Long idRol;

	private Long idConcursoPuestoAgr;
	private String actividad;

	private Boolean esAdministradorSFP = false;

	public void init() {

		cargarAdministradorSFP();

		if (nivelEntidadOeeUtil == null
			|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}

		search();
	}

	public void searchAll() {

		idRol = null;
		concursoDesc = null;
		actividadDesc = null;
		nivelEntidadOeeUtil.limpiar();
		cargarCabecera();

		String query = getQuery();
		bandejaEntradaCsiList.listaResultados(query);
	}

	public void search() {

		String query = getQuery();
		bandejaEntradaCsiList.listaResultados(query);

	}

	public String getQuery() {
		String query =
			"select distinct bandejaEntrada " + " from BandejaEntradaCsi bandejaEntrada "
				+ " join bandejaEntrada.configuracionUoCab configuracionUoCab "
				+ " join bandejaEntrada.actividadProceso actividadProceso "
				+ " join actividadProceso.procActividadRols procActividadRol "
				+ " join procActividadRol.rol rol ";

		// JOIN
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			query += " join configuracionUoCab.configuracionUoDets configuracionUoDet ";

		// WHERE
		query +=
			" where (bandejaEntrada.usuario is null or bandejaEntrada.usuario = '"
				+ usuarioLogueado.getCodigoUsuario() + "') ";

		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
			query +=
				" and bandejaEntrada.idSinNivelEntidad = "
					+ nivelEntidadOeeUtil.getIdSinNivelEntidad() + " ";

		if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
			query +=
				" and bandejaEntrada.idSinEntidad = " + nivelEntidadOeeUtil.getIdSinEntidad() + " ";

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

		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			query +=
				" and configuracionUoDet.idConfiguracionUoDet = "
					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa() + " ";

		if (concursoDesc != null) {
			if (!concursoDesc.contains("'") && !concursoDesc.contains(";"))
				query +=
					" and lower(bandejaEntrada.nombreConcurso) like lower('%" + concursoDesc
						+ "%')";
		}
		if (actividadDesc != null) {
			if (!actividadDesc.contains("'") && !actividadDesc.contains(";"))
				query +=
					" and lower(bandejaEntrada.actividad.descripcion) like lower('%"
						+ actividadDesc + "%')";
		}

		return query;
	}

	public void cargarCabecera() {

		nivelEntidadOeeUtil.limpiar();

		if (!esAdministradorSFP) {
			grupoPuestosController =
				(GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);
			grupoPuestosController.initCabecera();

			SinNivelEntidad sinNivelEntidad = grupoPuestosController.getSinNivelEntidad();
			SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
			ConfiguracionUoCab configuracionUoCab = grupoPuestosController.getConfiguracionUoCab();

			// Nivel
			nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad.getIdSinNivelEntidad());

			// Entidad
			nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

			// OEE
			nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab.getIdConfiguracionUo());
		}

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

	public String redirect() {
		if (actividad != null && !"".equals(actividad)) {

			ConcursoPuestoAgr concursoPuestoAgr =
				em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
			boolean tareaAsignada =
				jbpmUtilFormController.asignarTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance(), usuarioLogueado.getCodigoUsuario());
			if (!tareaAsignada) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Ocurrio un error al inicializar la tarea. Consulte con el administrador del sistema.");
				return null;
			}

			if (ActividadEnum.CSI_CARGA_GRUPO.getValor().equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/circuitoCSI/cargaDeGrupos/ConcursoPuestoDetList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
					+ idConcursoPuestoAgr + "&nroActividad=1";
			} else if (ActividadEnum.CSI_ENVIAR_HOMOLOGACION.getValor().equalsIgnoreCase(actividad)) {
				// 2 actividad
				return "/seleccion/homologacion/CSI/EnvioHomologacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
					+ idConcursoPuestoAgr+"&tipo=CSI";
			} else if (ActividadEnum.CSI_APROBAR_HOMOLOG_SFP.getValor().equalsIgnoreCase(actividad)) {
				// 3 actividad
				return "/circuitoCSI/perfilMatrizHomolog687/PerfilMatrizHomolog687.seam?concursoPuestoAgrIdConcursoPuestoAgr="
					+ idConcursoPuestoAgr;
			} else if (ActividadEnum.CSI_HOMOLOGACION_OEE.getValor().equalsIgnoreCase(actividad)) {
				// 4 actividad
				return "/circuitoCSI/enviarHomologPerfMatr688/EnviarHomologPerfMatr688.seam?concursoPuestoAgrIdConcursoPuestoAgr="
					+ idConcursoPuestoAgr;
			} else if (ActividadEnum.CSI_ELABORAR_DOC_HOMOLOG.getValor().equalsIgnoreCase(actividad)) {
				// 5 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																			// por
																			// concurso
					return "/circuitoCSI/admDocHomologacion689/AdmDocHomologacion689.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
				}
				return null;
			} else if (ActividadEnum.CSI_FIRMA_RESOL_HOMOLOG.getValor().equalsIgnoreCase(actividad)) {
				// 6 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																			// por
																			// concurso
					return "/circuitoCSI/firmarResolucionHomolog690/FirmarResolucionHomolog690.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&fromAct=7";
				}
				return null;
			} else if (ActividadEnum.CSI_MODIFICAR_DATOS_CONCURSO.getValor().equalsIgnoreCase(actividad)) {
				// 7 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																			// por
																			// concurso
					return "/circuitoCSI/modificacionConcurso/ModificarDatosConcursoList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
				}

				return null;
			} else if (ActividadEnum.CSI_SOLICITAR_PUBLICACION.getValor().equalsIgnoreCase(actividad)) {
				// 8 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																			// por
																			// concurso
					return "/circuitoCSI/publicacionConcursoCU692/PublicacionConcursoCabList.seam?idConcursoPuestoAgr=" + idConcursoPuestoAgr;
				}
				return null;
			} else if (ActividadEnum.CSI_APROBAR_PUBLICACION.getValor().equalsIgnoreCase(actividad)) {
				// 9 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																			// por
																			// concurso
					return "/circuitoCSI/publicacionConcursoSFPCU693/PublicacionConcursoCabList.seam?idConcursoPuestoAgr=" + idConcursoPuestoAgr;
				}
				return null;
			} else if (ActividadEnum.CSI_REVISION_PUBLICACION_OEE.getValor().equalsIgnoreCase(actividad)) {
				// 10 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																			// por
																			// concurso
					return "/circuitoCSI/verificarPublicacion694/VerificarPublicacion694.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
				}

				return null;
			} else if (ActividadEnum.RECIBIR_POSTULACIONES.getValor().equalsIgnoreCase(actividad)) {
				// 11 actividad
				return "/cargarCarpeta/CSI/cargarCarpeta.seam?codActividad=RECIBIR_POSTULACIONES&idGrupo="
					+ idConcursoPuestoAgr;
			} else if (ActividadEnum.CSI_PRORROGAR_POSTULACION.getValor().equalsIgnoreCase(actividad)) {
				// 12 actividad
				return "/seleccion/admProPos/CSI/admProPos697.seam?puesto_agr=" + idConcursoPuestoAgr;

			} else if (ActividadEnum.COMPLETAR_CARPETAS.getValor().equalsIgnoreCase(actividad)) {
				// 14 Completar Carpetas
				return "/cargarCarpeta/CSI/cargarCarpeta.seam?codActividad=COMPLETAR_CARPETAS&idGrupo="
					+ idConcursoPuestoAgr;
			} else if (ActividadEnum.CSI_REALIZAR_SORTEO.getValor().equalsIgnoreCase(actividad)) {
				// 13 actividad
				return "/circuitoCSI/admSorteo/AdmSorteo511.seam?&concursoPuestoAgrIdConcursoPuestoAgr="
					+ idConcursoPuestoAgr;

			} else if (ActividadEnum.CSI_REALIZAR_EVALUACIONES.getValor().equalsIgnoreCase(actividad)) {
				// 15 actividad
				return "/seleccion/realizarEval/CSI/realizarEvals.seam?idConcursoPuesto="
					+ idConcursoPuestoAgr;
			} else if (ActividadEnum.CSI_ELABORAR_PUBLICACION_LISTA_CORTA.getValor().equalsIgnoreCase(actividad)) {
				// 16 actividad

				return "/circuitoCSI/listaCortaCU699/ElaboracionListaCortaCU699.seam?concursoPuestoAgrIdConcursoPuestoAgr=" + idConcursoPuestoAgr;

			} else if (ActividadEnum.CSI_VALIDAR_MATRIZ_DOCUMENTAL_ADJ.getValor().equalsIgnoreCase(actividad)) {
				// 18 actividad
				return "/circuitoCSI/evaluacionMatrizDocumental/EvaluarDocAdjudicadoCU699.seam?concursoPuestoAgrIdConcursoPuestoAgr=" + idConcursoPuestoAgr;

			} else if (ActividadEnum.CSI_PUBLICAR_ADJUDICADOS.getValor().equalsIgnoreCase(actividad)) {
				// 19 actividad

				return "/circuitoCSI/publicacionSeleccionadosCU701/ListaSeleccionadosListCU701.seam?concursoPuestoAgrIdConcursoPuestoAgr=" + idConcursoPuestoAgr
				+ "&codActividad=" + actividad;
			} else if (ActividadEnum.CSI_ELABORAR_RESOLUCION_ADJUDICACION.getValor().equalsIgnoreCase(actividad)) {
				// 20. actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return "/circuitoCSI/resolucionAdjudicacion702/ResolucionAdjudacion702.seam?concursoPuestoAgrIdConcursoPuestoAgr=" + idConcursoPuestoAgr;
				}
				return null;
			} else if (ActividadEnum.CSI_FIRMAR_RESOLUCION_ADJUDICACION.getValor().equalsIgnoreCase(actividad)) {
				// 21 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return "/circuitoCSI/firmarResolucionHomolog690/FirmarResolucionHomolog690.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&fromAct=20";
				}

				return null;
			}

			else if (ActividadEnum.CSI_ELABORAR_DECRECTO_PRESIDENCIAL.getValor().equalsIgnoreCase(actividad)) {
				// 22 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return "/circuitoCSI/genDecretoPresidencial703/GenDecretoPresidencial703.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
				}

				return null;
			} else if (ActividadEnum.CSI_EDITAR_DOC_PRESIDENCIA_REPUBLICA.getValor().equalsIgnoreCase(actividad)) {
				// 23 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return "/circuitoCSI/editarDocPresidencia704/EditarDocPresidencia704.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
				}

				return null;
			} else if (ActividadEnum.CSI_INGRESAR_POSTULANTE.getValor().equalsIgnoreCase(actividad)) {
				// 24 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return "/seleccion/gestionarIngresosConcursos/gestionarIngresosConcursos.seam?idConcursoPuestoAgr="
					+ idConcursoPuestoAgr;
				}

				return null;
			} else if (ActividadEnum.CSI_ELABORAR_RESOLUCION_NOMBRAMIENTO.getValor().equalsIgnoreCase(actividad)) {
				// 25 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return "/circuitoCSI/elaborarResolucionNombramiento705/ElaborarResolucionNombramiento705.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
				}

				return null;
			} else if (ActividadEnum.CSI_FIRMAR_RESOLUCION_NOMBRAMIENTO.getValor().equalsIgnoreCase(actividad)) {
				// 26 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return "/circuitoCSI/firmarResolucionHomolog690/FirmarResolucionHomolog690.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&fromAct=26";
				}

				return null;
			} else if (ActividadEnum.CSI_REVISION_EMPATES.getValor().equalsIgnoreCase(actividad)) {
				// 27 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return ".seam?concursoPuestoAgrIdConcursoPuestoAgr=" + idConcursoPuestoAgr;
				}

				return null;
			}

		}
		return null;
	}

	private boolean asignarTareasConcurso(ConcursoPuestoAgr concursoPuestoAgr, String nombreActividad) {
		List<ConcursoPuestoAgr> lista =
			seleccionUtilFormController.buscarGruposConcurso(concursoPuestoAgr.getConcurso().getIdConcurso(), nombreActividad);
		for (ConcursoPuestoAgr grupo : lista) {
			if (grupo.getIdConcursoPuestoAgr().longValue() != concursoPuestoAgr.getIdConcursoPuestoAgr().longValue()) {
				boolean tareaAsignada =
					jbpmUtilFormController.asignarTaskInstanceActual(grupo.getIdProcessInstance(), usuarioLogueado.getCodigoUsuario());
				if (!tareaAsignada) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Ocurrio un error al inicializar la tarea. Consulte con el administrador del sistema.");
					return false;
				}
			}
		}

		return true;
	}

/*	private Boolean validar(Boolean controlarCab) {
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Nivel es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Entidad es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, "OEE es un campo requerido. Verifique.");
			return false;
		}

		return true;
	}*/

	public String getConcursoDesc() {
		return concursoDesc;
	}

	public void setConcursoDesc(String concursoDesc) {
		this.concursoDesc = concursoDesc;
	}

	public Long getIdRol() {
		return idRol;
	}

	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public void setEsAdministradorSFP(Boolean esAdministradorSFP) {
		this.esAdministradorSFP = esAdministradorSFP;
	}

	public Boolean getEsAdministradorSFP() {
		return esAdministradorSFP;
	}

	public String getActividadDesc() {
		return actividadDesc;
	}

	public void setActividadDesc(String actividadDesc) {
		this.actividadDesc = actividadDesc;
	}
}
