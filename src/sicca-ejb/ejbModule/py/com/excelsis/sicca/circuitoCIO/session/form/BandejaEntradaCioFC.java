package py.com.excelsis.sicca.circuitoCIO.session.form;

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
import py.com.excelsis.sicca.entity.BandejaEntrada;
import py.com.excelsis.sicca.entity.BandejaEntradaCio;
import py.com.excelsis.sicca.entity.Concurso;
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
@Name("bandejaEntradaCioFC")
public class BandejaEntradaCioFC {
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
	BandejaEntradaCioList bandejaEntradaCioList;
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
		bandejaEntradaCioList.listaResultados(query);
	}
	
	public boolean habilitarGestionar(BandejaEntradaCio bandejaEntrada){
		boolean retorno = false;
		if(bandejaEntrada.getUsuario() == null || bandejaEntrada.getUsuario().equals(this.usuarioLogueado.getCodigoUsuario()))
			retorno = true;
		else {
			if (bandejaEntrada.getCodActividad().equals("EVALUACION_DOCUMENTAL") || bandejaEntrada.getCodActividad().equals("REALIZAR_EVALUACIONES") ){
				retorno= true;
			}
						
		}
		
		
		
		return retorno;
	}

	public void search() {

		String query = getQuery();
		bandejaEntradaCioList.listaResultados(query);

	}

	public String getQuery() {
		String query =
			"select distinct bandejaEntrada " + " from BandejaEntradaCio bandejaEntrada "
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

			ConcursoPuestoAgr concursoPuestoAgr =	em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
			Concurso concurso = concursoPuestoAgr.getConcurso();
			boolean tareaAsignada =	jbpmUtilFormController.asignarTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance(), usuarioLogueado.getCodigoUsuario());
			if (!tareaAsignada) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Ocurrio un error al inicializar la tarea. Consulte con el administrador del sistema.");
				return null;
			}
			
			
			// SI ES UN CONCURSO DE PROMOCION SALARIAL
			if(concurso.getDatosEspecificosTipoConc().getDescripcion().equals("CONCURSO INTERNO DE PROMOCION SALARIAL")){
				if (ActividadEnum.CIO_CARGA_GRUPO.getValor().equalsIgnoreCase(actividad)) {
					// 1 actividad
					return "/circuitoCIO/cargaGrupos/PromocionConcursoAgrList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&nroActividad=1";
					
				} else if (ActividadEnum.CIO_ENVIAR_HOMOLOGACION.getValor().equalsIgnoreCase(actividad)) {
					// 2 actividad
					return "/seleccion/homologacion/CIO/EnvioHomologacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr+"&tipo='CIO'";
				} else if (ActividadEnum.CIO_APROBAR_HOMOLOG_SFP.getValor().equalsIgnoreCase(actividad)) {
					// 3 actividad
					return "/circuitoCIO/perfilMatrizhomolog619/PerfilMatrizHomologacion619.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr+ "&codActividad=APROBAR_HOMOLOG_SFP" ;
				} else if (ActividadEnum.CIO_HOMOLOGACION_OEE.getValor().equalsIgnoreCase(actividad)) {
					// 4 actividad
					return "/circuitoCIO/enviarHomologacion620/EnviarHomologacionPerfilMatriz620.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr+ "&codActividad=HOMOLOGACION_OEE";
				} else if (ActividadEnum.CIO_ELABORAR_DOC_HOMOLOG.getValor().equalsIgnoreCase(actividad)) {
					// 5 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/admDocumentoHomologacion621/DocumentoHomologacion621.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
					return null;
				} else if (ActividadEnum.CIO_FIRMA_RESOL_HOMOLOG.getValor().equalsIgnoreCase(actividad)) {
					// 6 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/firmarResolucionHomolog622/FirmarResolucionHomolog622.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr + "&fromAct=7";
					}
					return null;
				} else if (ActividadEnum.CIO_MODIFICAR_DATOS_CONCURSO.getValor().equalsIgnoreCase(actividad)) {
					// 7 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/modificarDatosConcurso/ModificarDatosConcursoList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_SOLICITAR_PUBLICACION.getValor().equalsIgnoreCase(actividad)) {
					// 8 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/publicarConcursoPortal/PublicacionConcursoCabList.seam?idConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
					return null;
				} else if (ActividadEnum.CIO_APROBAR_PUBLICACION.getValor().equalsIgnoreCase(actividad)) {
					// 9 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/publicarConcursoPortalSFP/PublicacionConcursoCabList.seam?idConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
					return null;
				} else if (ActividadEnum.CIO_REVISION_PUBLICACION_OEE.getValor().equalsIgnoreCase(actividad)) {
					// 10 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/verificarPublicacionConcurso626/VerificarPublicacionConcurso626.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_RECIBIR_POSTULACIONES.getValor().equalsIgnoreCase(actividad)) {
					// 11 actividad
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Este tipo de Concurso no tiene Postulación por Carpeta.");
					return ".seam?codActividad=RECIBIR_POSTULACIONES&idGrupo=" + idConcursoPuestoAgr;
				} else if (ActividadEnum.CIO_PRORROGAR_POSTULACION.getValor().equalsIgnoreCase(actividad)) {
					// 12 actividad
					return "/seleccion/admProPos/CIO/admProPos425.seam?puesto_agr="
						+ idConcursoPuestoAgr+"&tipo=CIO";
	
				} else if (ActividadEnum.CIO_EVALUACION_DOCUMENTAL.getValor().equalsIgnoreCase(actividad)) {
					// 13 actividad
					return "/circuitoCIO/evaluacionDocumentalPostulante/EvalDocumentalCabList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&codActividad=" + actividad+ "&isEdit=true";
				} else if (ActividadEnum.CIO_ELABORAR_PUBLICACION_LISTA_LARGA.getValor().equalsIgnoreCase(actividad)) {
					// 14 actividad
					return "/circuitoCIO/listaLarga/ListaLargaList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
				} else if (ActividadEnum.CIO_REALIZAR_EVALUACIONES.getValor().equalsIgnoreCase(actividad)) {
					// 15 actividad
					return "/seleccion/realizarEval/CIO/realizarEvals.seam?idConcursoPuesto="
						+ idConcursoPuestoAgr;
				} else if (ActividadEnum.CIO_ELABORAR_PUBLICACION_LISTA_CORTA.getValor().equalsIgnoreCase(actividad)) {
					// 16 actividad
	
					return "/circuitoCIO/listaCorta/ElaboracionListaCorta.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
					
				}else if (ActividadEnum.TACHAS_RECLAMOS_MODIF
						.getValor().equalsIgnoreCase(actividad)) {
					// nueva actividad de tachas, reclamos y modificaciones
					return "/circuitoCIO/tachasReclamosModif/tachasReclamosModifList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
	
				} else if (ActividadEnum.CIO_REALIZAR_ENTREVISTA_FINAL.getValor().equalsIgnoreCase(actividad)) {
					// 17 actividad
					return "/seleccion/entrevistaFinal/CIO/RealizarEntrevistaFinal.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
				} else if (ActividadEnum.CIO_VALIDAR_MATRIZ_DOCUMENTAL_ADJ.getValor().equalsIgnoreCase(actividad)) {
					// 18 actividad
					return "/circuitoCIO/evaluacionDocumentosAdjudicados/EvaluarDocAdjudicado.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
	
				} else if (ActividadEnum.CIO_PUBLICAR_ADJUDICADOS.getValor().equalsIgnoreCase(actividad)) {
					// 19 actividad
	
					return "/circuitoCIO/publicacionSeleccionados/ListaSeleccionadosList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&codActividad=" + actividad;
				} else if (ActividadEnum.CIO_ELABORAR_RESOLUCION_ADJUDICACION.getValor().equalsIgnoreCase(actividad)) {
					// 20. actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/circuitoCIO/elaboracionResoAdjudicacion636/ElaboracionResoAdjudicacion636.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
					return null;
				}
				else if (ActividadEnum.ASIGNAR_PROMOCION_SALARIAL.getValor().equalsIgnoreCase(actividad)) {
					// 21 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
//						return "/circuitoCIO/firmarResolucionHomolog622/FirmarResolucionHomolog622.seam?concursoPuestoAgrIdConcursoPuestoAgr="
//							+ idConcursoPuestoAgr + "&fromAct=21";
						
						return "/promocionCIO/gestionarIngresosConcursos/gestionarIngresosConcursos.seam?idConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
					}
	
					return null;
				}		
				
				else if (ActividadEnum.CIO_FIRMAR_RESOLUCION_ADJUDICACION.getValor().equalsIgnoreCase(actividad)) {
					// 21 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
//						return "/circuitoCIO/firmarResolucionHomolog622/FirmarResolucionHomolog622.seam?concursoPuestoAgrIdConcursoPuestoAgr="
//							+ idConcursoPuestoAgr + "&fromAct=21";
						
						return "/promocionCIO/gestionarIngresosConcursos/gestionarIngresosConcursos.seam?idConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
					}
	
					return null;
				}
	
				else if (ActividadEnum.CIO_ELABORAR_DECRECTO_PRESIDENCIAL.getValor().equalsIgnoreCase(actividad)) {
					// 22 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/circuitoCIO/admDecreto638/AdministrarDecreto638.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_EDITAR_DOC_PRESIDENCIA_REPUBLICA.getValor().equalsIgnoreCase(actividad)) {
					// 23 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/circuitoCIO/editarDocPresidencia639/EditarDocPresidencia639.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_INGRESAR_POSTULANTE.getValor().equalsIgnoreCase(actividad)) {
					// 24 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/promocionCIO/gestionarIngresosConcursos/gestionarIngresosConcursos.seam?idConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_ELABORAR_RESOLUCION_NOMBRAMIENTO.getValor().equalsIgnoreCase(actividad)) {
					// 25 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/circuitoCIO/elaboResNombamiento640/ElaboResNombamiento640.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_FIRMAR_RESOLUCION_NOMBRAMIENTO.getValor().equalsIgnoreCase(actividad)) {
					// 26 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/circuitoCIO/firmarResolucionHomolog622/FirmarResolucionHomolog622.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr + "&fromAct=26";
					}
	
					return null;
				} else if (ActividadEnum.CIO_REVISION_EMPATES.getValor().equalsIgnoreCase(actividad)) {
					// 27 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return ".seam?concursoPuestoAgrIdConcursoPuestoAgr=" + idConcursoPuestoAgr;
					}
	
					return null;
				}
			//SI NO ES UN CONCURSO DE PROMOCION SALARIAL. 
			}else{
				if (ActividadEnum.CIO_CARGA_GRUPO.getValor().equalsIgnoreCase(actividad)) {
					// 1 actividad
					return "/circuitoCIO/cargaGrupos/ConcursoPuestoDetList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&nroActividad=1";
				} else if (ActividadEnum.CIO_ENVIAR_HOMOLOGACION.getValor().equalsIgnoreCase(actividad)) {
					// 2 actividad
					return "/seleccion/homologacion/CIO/EnvioHomologacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr+"&tipo='CIO'";
				} else if (ActividadEnum.CIO_APROBAR_HOMOLOG_SFP.getValor().equalsIgnoreCase(actividad)) {
					// 3 actividad
					return "/circuitoCIO/perfilMatrizhomolog619/PerfilMatrizHomologacion619.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr+ "&codActividad=APROBAR_HOMOLOG_SFP" ;
				} else if (ActividadEnum.CIO_HOMOLOGACION_OEE.getValor().equalsIgnoreCase(actividad)) {
					// 4 actividad
					return "/circuitoCIO/enviarHomologacion620/EnviarHomologacionPerfilMatriz620.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr+ "&codActividad=HOMOLOGACION_OEE";
				} else if (ActividadEnum.CIO_ELABORAR_DOC_HOMOLOG.getValor().equalsIgnoreCase(actividad)) {
					// 5 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/admDocumentoHomologacion621/DocumentoHomologacion621.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
					return null;
				} else if (ActividadEnum.CIO_FIRMA_RESOL_HOMOLOG.getValor().equalsIgnoreCase(actividad)) {
					// 6 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/firmarResolucionHomolog622/FirmarResolucionHomolog622.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr + "&fromAct=7";
					}
					return null;
				} else if (ActividadEnum.CIO_MODIFICAR_DATOS_CONCURSO.getValor().equalsIgnoreCase(actividad)) {
					// 7 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/modificarDatosConcurso/ModificarDatosConcursoList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_SOLICITAR_PUBLICACION.getValor().equalsIgnoreCase(actividad)) {
					// 8 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/publicarConcursoPortal/PublicacionConcursoCabList.seam?idConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
					return null;
				} else if (ActividadEnum.CIO_APROBAR_PUBLICACION.getValor().equalsIgnoreCase(actividad)) {
					// 9 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/publicarConcursoPortalSFP/PublicacionConcursoCabList.seam?idConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
					return null;
				} else if (ActividadEnum.CIO_REVISION_PUBLICACION_OEE.getValor().equalsIgnoreCase(actividad)) {
					// 10 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																				// por
																				// concurso
						return "/circuitoCIO/verificarPublicacionConcurso626/VerificarPublicacionConcurso626.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_RECIBIR_POSTULACIONES.getValor().equalsIgnoreCase(actividad)) {
					// 11 actividad
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Este tipo de Concurso no tiene Postulación por Carpeta.");
					return ".seam?codActividad=RECIBIR_POSTULACIONES&idGrupo=" + idConcursoPuestoAgr;
				} else if (ActividadEnum.CIO_PRORROGAR_POSTULACION.getValor().equalsIgnoreCase(actividad)) {
					// 12 actividad
					return "/seleccion/admProPos/CIO/admProPos425.seam?puesto_agr="
						+ idConcursoPuestoAgr+"&tipo=CIO";
	
				} else if (ActividadEnum.CIO_EVALUACION_DOCUMENTAL.getValor().equalsIgnoreCase(actividad)) {
					// 13 actividad
					return "/circuitoCIO/evaluacionDocumentalPostulante/EvalDocumentalCabList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&codActividad=" + actividad+ "&isEdit=true";
				} else if (ActividadEnum.CIO_ELABORAR_PUBLICACION_LISTA_LARGA.getValor().equalsIgnoreCase(actividad)) {
					// 14 actividad
					return "/circuitoCIO/listaLarga/ListaLargaList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
				} else if (ActividadEnum.CIO_REALIZAR_EVALUACIONES.getValor().equalsIgnoreCase(actividad)) {
					// 15 actividad
					return "/seleccion/realizarEval/CIO/realizarEvals.seam?idConcursoPuesto="
						+ idConcursoPuestoAgr;
				} else if (ActividadEnum.CIO_ELABORAR_PUBLICACION_LISTA_CORTA.getValor().equalsIgnoreCase(actividad)) {
					// 16 actividad
	
					return "/circuitoCIO/listaCorta/ElaboracionListaCorta.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
					
					
				}else if (ActividadEnum.TACHAS_RECLAMOS_MODIF
						.getValor().equalsIgnoreCase(actividad)) {
					// nueva actividad de tachas, reclamos y modificaciones
					return "/circuitoCIO/tachasReclamosModif/tachasReclamosModifList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
	
				} else if (ActividadEnum.CIO_REALIZAR_ENTREVISTA_FINAL.getValor().equalsIgnoreCase(actividad)) {
					// 17 actividad
					return "/seleccion/entrevistaFinal/CIO/RealizarEntrevistaFinal.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
				} else if (ActividadEnum.CIO_VALIDAR_MATRIZ_DOCUMENTAL_ADJ.getValor().equalsIgnoreCase(actividad)) {
					// 18 actividad
					return "/circuitoCIO/evaluacionDocumentosAdjudicados/EvaluarDocAdjudicado.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
	
				} else if (ActividadEnum.CIO_PUBLICAR_ADJUDICADOS.getValor().equalsIgnoreCase(actividad)) {
					// 19 actividad
	
					return "/circuitoCIO/publicacionSeleccionados/ListaSeleccionadosList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&codActividad=" + actividad;
				} else if (ActividadEnum.CIO_ELABORAR_RESOLUCION_ADJUDICACION.getValor().equalsIgnoreCase(actividad)) {
					// 20. actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/circuitoCIO/elaboracionResoAdjudicacion636/ElaboracionResoAdjudicacion636.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
					return null;
				} else if (ActividadEnum.CIO_FIRMAR_RESOLUCION_ADJUDICACION.getValor().equalsIgnoreCase(actividad)) {
					// 21 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/circuitoCIO/firmarResolucionHomolog622/FirmarResolucionHomolog622.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr + "&fromAct=21";
					}
	
					return null;
				}
	
				else if (ActividadEnum.CIO_ELABORAR_DECRECTO_PRESIDENCIAL.getValor().equalsIgnoreCase(actividad)) {
					// 22 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/circuitoCIO/admDecreto638/AdministrarDecreto638.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_EDITAR_DOC_PRESIDENCIA_REPUBLICA.getValor().equalsIgnoreCase(actividad)) {
					// 23 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/circuitoCIO/editarDocPresidencia639/EditarDocPresidencia639.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_INGRESAR_POSTULANTE.getValor().equalsIgnoreCase(actividad)) {
					// 24 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/seleccion/gestionarIngresoConcursoInterno/gestionarIngresoConcursoInterno543.seam?idConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_ELABORAR_RESOLUCION_NOMBRAMIENTO.getValor().equalsIgnoreCase(actividad)) {
					// 25 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/circuitoCIO/elaboResNombamiento640/ElaboResNombamiento640.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
					}
	
					return null;
				} else if (ActividadEnum.CIO_FIRMAR_RESOLUCION_NOMBRAMIENTO.getValor().equalsIgnoreCase(actividad)) {
					// 26 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return "/circuitoCIO/firmarResolucionHomolog622/FirmarResolucionHomolog622.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr + "&fromAct=26";
					}
	
					return null;
				} else if (ActividadEnum.CIO_REVISION_EMPATES.getValor().equalsIgnoreCase(actividad)) {
					// 27 actividad
					if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
						return ".seam?concursoPuestoAgrIdConcursoPuestoAgr=" + idConcursoPuestoAgr;
					}
	
					return null;
				}
			}


	
			}//principal
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

	private Boolean validar(Boolean controlarCab) {
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
	}

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
