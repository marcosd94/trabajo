package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import py.com.excelsis.sicca.entity.BandejaEntrada;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.BandejaEntradaList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("bandejaEntradaFormController")
public class BandejaEntradaFormController implements Serializable {

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
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	BandejaEntradaList bandejaEntradaList;
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
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	private String concursoDesc;
	private String actividadDesc;
	private String grupoDesc;
	private Long idRol;

	private Long idConcursoPuestoAgr;
	private ConcursoPuestoAgr concursoPuestoAgr;
	//Documentos Adjuntos
		private byte[] uploadedFile;
		private String contentType;
		private String fileName;
		
		private String nombrePantalla;
		private UploadItem item;
		private Integer tamArchivo;
		private String tipoDoc;
		private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
		private SinEntidad sinEntidad = new SinEntidad();
		
		//Para la Publicacion de los Concursos para Publicacion
		private String publicDetalle;
		private String tituloDocumentoAdjunto;



	private String actividad;

	private Boolean esAdministradorSFP = false;

	public void init() {

		cargarAdministradorSFP();

		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}

		search();
	}

	public void searchAll() {
		// if (!validar(false))
		// return;

		idRol = null;
		concursoDesc = null;
		actividadDesc = null;
		grupoDesc = null;
		nivelEntidadOeeUtil.limpiar();
		cargarCabecera();

		String query = getQuery();
		bandejaEntradaList.listaResultados(query);
	}

	public void search() {
		// if (!validar(false))
		// return;
		String query = getQuery();
		bandejaEntradaList.listaResultados(query);

	}
	
	public boolean habilitarGestionar(BandejaEntrada bandejaEntrada){
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
	
	
	public String mostrarActividad (Long idConcursoPuestoAgr, String DescripcionActividad){
		String retorno = "";
			if(estaDesierto(idConcursoPuestoAgr))
				retorno = "DESIERTO - ";
			
		
		return retorno + DescripcionActividad;
	}
	
	public Boolean estaDesierto (Long idConcursoPuestoAgr){
		Boolean retorno = false;
		ConcursoPuestoAgr cpa = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		if(cpa.getEstado().intValue() == 35) //Estado GRUPO DESIERTO = 35
			retorno = true;
		return retorno;
	}

	public String getQuery() {
		String query = "select distinct bandejaEntrada "
				+ " from BandejaEntrada bandejaEntrada "
				+ " join bandejaEntrada.configuracionUoCab configuracionUoCab "
				+ " join bandejaEntrada.actividadProceso actividadProceso "
				+ " join actividadProceso.procActividadRols procActividadRol "
				+ " join procActividadRol.rol rol ";

		// JOIN
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			query += " join configuracionUoCab.configuracionUoDets configuracionUoDet ";

		// WHERE
		query += " where 1=1";
		
		
		//PARTE DEL CODIGO QUE SOLO HABILITA LOS REGISTROS QUE TENGAN USUARIO NULL O SEAN DEL USUARIO LOGGEADO
		//query += " and (bandejaEntrada.usuario is null or bandejaEntrada.usuario = '" + usuarioLogueado.getCodigoUsuario() + "') ";

		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
			query += " and bandejaEntrada.idSinNivelEntidad = "
					+ nivelEntidadOeeUtil.getIdSinNivelEntidad() + " ";

		if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
			query += " and bandejaEntrada.idSinEntidad = "
					+ nivelEntidadOeeUtil.getIdSinEntidad() + " ";

		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			query += " and configuracionUoCab.idConfiguracionUo = "
					+ nivelEntidadOeeUtil.getIdConfigCab() + " ";

		if (idRol != null) {
			query += " and rol.idRol = " + idRol + " ";
		} else {
			String roles = getIdRolesUsuario();
			query += " and rol.idRol in (" + roles + ") ";
		}

		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			query += " and configuracionUoDet.idConfiguracionUoDet = "
					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa() + " ";
//		//ZD 22/02/16 - SI TIENE ROL HOMOLOGADOR, LISTAR TODOS
//		if(esAdministradorSFP){
//		//ZD 30/10/15
//		if(usuarioLogueado.getCodigoUsuario() != null)
//			query += " and (bandejaEntrada.codigoUsuario is null or bandejaEntrada.codigoUsuario = '"
//					+ usuarioLogueado.getCodigoUsuario() + "') ";
//		}else{
//			query += " and bandejaEntrada.codigoUsuario = '"
//					+ usuarioLogueado.getCodigoUsuario() + "' ";
//		}
		if (concursoDesc != null && !concursoDesc.isEmpty()) {
			if (!concursoDesc.contains("'") && !concursoDesc.contains(";"))
				query += " and lower(bandejaEntrada.nombreConcurso) like lower('%"
						+ concursoDesc + "%')";
		}
		if (actividadDesc != null && !actividadDesc.isEmpty()) {
			if (!actividadDesc.contains("'") && !actividadDesc.contains(";"))
				query += " and lower(bandejaEntrada.actividad.descripcion) like lower('%"
						+ actividadDesc + "%')";
		}
		if(grupoDesc != null && !grupoDesc.isEmpty())
			if(!grupoDesc.contains("'") && !grupoDesc.contains(";"))
				query += " and lower(bandejaEntrada.descripcionGrupo) like lower('%"
						+ grupoDesc + "%')";
		return query;
	}

	public void cargarCabecera() {

		nivelEntidadOeeUtil.limpiar();

		if (!esAdministradorSFP) {
			grupoPuestosController = (GrupoPuestosController) Component
					.getInstance(GrupoPuestosController.class, true);
			grupoPuestosController.initCabecera();

			SinNivelEntidad sinNivelEntidad = grupoPuestosController
					.getSinNivelEntidad();
			SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
			ConfiguracionUoCab configuracionUoCab = grupoPuestosController
					.getConfiguracionUoCab();

			// Nivel
			nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad
					.getIdSinNivelEntidad());

			// Entidad
			nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

			// OEE
			nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab
					.getIdConfiguracionUo());
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

			ConcursoPuestoAgr concursoPuestoAgr = em.find(
					ConcursoPuestoAgr.class, idConcursoPuestoAgr);
			boolean tareaAsignada = jbpmUtilFormController
					.asignarTaskInstanceActual(
							concursoPuestoAgr.getIdProcessInstance(),
							usuarioLogueado.getCodigoUsuario());
			if (!tareaAsignada) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Ocurrio un error al inicializar la tarea. Consulte con el administrador del sistema.");
				return null;
			}

			if (ActividadEnum.CARGA_GRUPO.getValor()
					.equalsIgnoreCase(actividad)) {
				// 1 actividad
				return "/seleccion/admCargaGrupo/ConcursoPuestoDetList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&nroActividad=1";
			//} else if (ActividadEnum.ENVIAR_HOMOLOGACION.getValor()//MODIFICADO RV
					//.equalsIgnoreCase(actividad)) {
				// 2 actividad
				//return "/seleccion/homologacion/EnvioHomologacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						//+ idConcursoPuestoAgr;
			} else if (ActividadEnum.APROBAR_HOMOLOG_SFP.getValor()
					.equalsIgnoreCase(actividad)) {
				// 3 actividad
				return "/seleccion/perfilMatrizHomologacion/PerfilMatrizHomologacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr+"&actividad=APROBAR_HOMOLOG_SFP";
			}  else if (ActividadEnum.HOMOLOGACION_OEE.getValor()
					.equalsIgnoreCase(actividad)) {
				// 4 actividad
				return "/seleccion/homologacionPerfilMatriz/EnviarHomologacionPerfil.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&actividad=HOMOLOGACION_OEE";
//			} else if (ActividadEnum.ELABORAR_DOC_HOMOLOG.getValor()
//					.equalsIgnoreCase(actividad)) {
//				// 5 actividad
//				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
//																			// por
//																			// concurso
//					return "/seleccion/admDocHomologacion/AdmDocHomologacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
//							+ idConcursoPuestoAgr;
//				}
//				return null;
			} else if (ActividadEnum.FIRMA_RESOL_HOMOLOG.getValor()
					.equalsIgnoreCase(actividad)) {
				// 6 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																			// por
																			// concurso
					return "/seleccion/firmarResolucionHomologacion/FirmarResolucionHomologacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr + "&fromAct=7";
				}
				return null;
			} else if (ActividadEnum.MODIFICAR_DATOS_CONCURSO.getValor()
					.equalsIgnoreCase(actividad)) {
				// 7 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
																			// por
																			// concurso
					return "/seleccion/concurso/modificacionConcurso/ModificarDatosConcursoList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
				}

				return null;
//			} else if (ActividadEnum.SOLICITAR_PUBLICACION.getValor()
//					.equalsIgnoreCase(actividad)) {
//				// 8 actividad
//				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
//																			// por
//																			// concurso
//					return "/seleccion/publicacionConcursoCab/PublicacionConcursoCabList.seam?idConcursoPuestoAgr="
//							+ idConcursoPuestoAgr;
//				}
//				return null;
//			} else if (ActividadEnum.APROBAR_PUBLICACION.getValor()
//					.equalsIgnoreCase(actividad)) {
//				// 9 actividad
//				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
//																			// por
//																			// concurso
//					return "/seleccion/publicacionConcursoSFP/PublicacionConcursoCabList.seam?idConcursoPuestoAgr="
//							+ idConcursoPuestoAgr;
//				}
//				return null;
//			} else if (ActividadEnum.REVISION_PUBLICACION_OEE.getValor()
//					.equalsIgnoreCase(actividad)) {
//				// 10 actividad
//				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) { // Tarea
//																			// por
//																			// concurso
//					return "/seleccion/verificarPublicacion/VerificarPublicacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
//							+ idConcursoPuestoAgr;
//				}
//
//				return null;
			} else if (ActividadEnum.RECIBIR_POSTULACIONES.getValor()
					.equalsIgnoreCase(actividad)) {
				// 11 actividad
				return "/cargarCarpeta/cargarCarpeta.seam?codActividad=RECIBIR_POSTULACIONES&idGrupo="
						+ idConcursoPuestoAgr;
			} else if (ActividadEnum.PRORROGAR_POSTULACION.getValor()
					.equalsIgnoreCase(actividad)) {
				// 12 actividad
				return "/seleccion/admProPos/admProPos425.seam?puesto_agr="
						+ idConcursoPuestoAgr;
			} else if (ActividadEnum.COMPLETAR_CARPETAS.getValor()
					.equalsIgnoreCase(actividad)) {
				// 13 Completar Carpetas
				return "/cargarCarpeta/cargarCarpeta.seam?codActividad=COMPLETAR_CARPETAS&idGrupo="
						+ idConcursoPuestoAgr;
			} else if (ActividadEnum.EVALUACION_DOCUMENTAL.getValor()
					.equalsIgnoreCase(actividad)) {
				// 13 actividad
				return "/seleccion/evalDocumentosPostulantes/EvalDocumentalCabList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&codActividad=" + actividad + "&isEdit=true";
			} else if (ActividadEnum.ELABORAR_PUBLICACION_LISTA_LARGA
					.getValor().equalsIgnoreCase(actividad)) {
				// 14 actividad
				return "/seleccion/listaLarga/ListaLargaList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;
			} else if (ActividadEnum.REALIZAR_EVALUACIONES.getValor()
					.equalsIgnoreCase(actividad)) {
				// 15 actividad
				return "/seleccion/realizarEval/realizarEvals.seam?idConcursoPuesto="
						+ idConcursoPuestoAgr;
			} else if (ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA
					.getValor().equalsIgnoreCase(actividad)) {
				// 16 actividad

				return "/seleccion/evalReferencialPostulante/ElaboracionListaCorta.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;

			}else if (ActividadEnum.TACHAS_RECLAMOS_MODIF
					.getValor().equalsIgnoreCase(actividad)) {
				// nueva actividad de tachas, reclamos y modificaciones
				return "/seleccion/tachasReclamosModif/tachasReclamosModifList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr; 
			
			
			}else if (ActividadEnum.REALIZAR_ENTREVISTA_FINAL.getValor()
					.equalsIgnoreCase(actividad)) {
				// 17 actividad
				/*return "/seleccion/entrevistaFinal/RealizarEntrevistaFinal.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;*/
				return "/seleccion/entrevistaFinal/TernasEntrevistaFinal.seam?concursoPuestoAgrIdConcursoPuestoAgr="
				+ idConcursoPuestoAgr;
			} else if (ActividadEnum.VALIDAR_MATRIZ_DOCUMENTAL_ADJ.getValor()
					.equalsIgnoreCase(actividad)) {
				// 18 actividad
				return "/seleccion/evaluacionDocumentalAdjudicados/EvaluarDocAdjudicado.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr;

			} else if (ActividadEnum.PUBLICAR_ADJUDICADOS.getValor()
					.equalsIgnoreCase(actividad)) {
				// 19 actividad

				return "/seleccion/elaborarPublicacionSeleccionados/ListaSeleccionadosList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
						+ idConcursoPuestoAgr + "&codActividad=" + actividad;
//			} else if (ActividadEnum.ELABORAR_RESOLUCION_ADJUDICACION
//					.getValor().equalsIgnoreCase(actividad)) {
//				// 20. actividad
//				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
//					return "/seleccion/resolucionAdjudicacion/ElaborarResolucionAdjudicacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
//							+ idConcursoPuestoAgr;
//				}
//				return null;
			} else if (ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION.getValor()
					.equalsIgnoreCase(actividad)) {
				// 21 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					//return "/seleccion/firmarResolucionHomologacion/FirmarResolucionHomologacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							//+ idConcursoPuestoAgr + "&fromAct=21";
					return "/seleccion/firmarResolucionHomologacion/FirmarResolucionAdjudicacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr + "&fromAct=21";
				}

				return null;
			}

			else if (ActividadEnum.ELABORAR_DECRECTO_PRESIDENCIAL.getValor()
					.equalsIgnoreCase(actividad)) {
				// 22 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return "/seleccion/administrarDecreto/AdministrarDecreto.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
				}

				return null;
//			} else if (ActividadEnum.EDITAR_DOC_PRESIDENCIA_REPUBLICA
//					.getValor().equalsIgnoreCase(actividad)) {
//				// 23 actividad
//				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
//					return "/seleccion/documentoPresidencia/EditarDocumentoPresidencia.seam?concursoPuestoAgrIdConcursoPuestoAgr="
//							+ idConcursoPuestoAgr;
//				}
//
//				return null;
			} else if (ActividadEnum.INGRESAR_POSTULANTE.getValor()
					.equalsIgnoreCase(actividad)) {
				// 24 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return "/seleccion/gestionarIngresosConcursos/gestionarIngresosConcursos.seam?idConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
				}

				return null;
//			} else if (ActividadEnum.ELABORAR_RESOLUCION_NOMBRAMIENTO
//					.getValor().equalsIgnoreCase(actividad)) {
//				// 25 actividad
//				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
//					return "/seleccion/resolucionNombramiento/ResolucionNombramiento.seam?concursoPuestoAgrIdConcursoPuestoAgr="
//							+ idConcursoPuestoAgr;
//				}
//
//				return null;
//			} else if (ActividadEnum.FIRMAR_RESOLUCION_NOMBRAMIENTO.getValor()
//					.equalsIgnoreCase(actividad)) {
//				// 26 actividad
//				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
//					return "/seleccion/firmarResolucionHomologacion/FirmarResolucionHomologacion.seam?concursoPuestoAgrIdConcursoPuestoAgr="
//							+ idConcursoPuestoAgr + "&fromAct=26";
//				}
//
//				return null;
			} else if (ActividadEnum.REVISION_EMPATES.getValor()
					.equalsIgnoreCase(actividad)) {
				// 27 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return "/seleccion/cancelacionPostulante/RevisionEmpate.seam?concursoPuestoAgrIdConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
				}

				return null;
			}

			else if (ActividadEnum.GESTION_INGRESOS.getValor()
					.equalsIgnoreCase(actividad)) {
				// 28 actividad
				if (asignarTareasConcurso(concursoPuestoAgr, actividad)) {
					return "/seleccion/gestionarIngresosConcursos/gestionarIngresosConcursos.seam?idConcursoPuestoAgr="
							+ idConcursoPuestoAgr;
				}

				return null;
			}
		}
		return null;
	}

	private boolean asignarTareasConcurso(ConcursoPuestoAgr concursoPuestoAgr,
			String nombreActividad) {
		List<ConcursoPuestoAgr> lista = seleccionUtilFormController
				.buscarGruposConcurso(concursoPuestoAgr.getConcurso()
						.getIdConcurso(), nombreActividad);
		for (ConcursoPuestoAgr grupo : lista) {
			if (grupo.getIdConcursoPuestoAgr().longValue() != concursoPuestoAgr
					.getIdConcursoPuestoAgr().longValue()) {
				boolean tareaAsignada = jbpmUtilFormController
						.asignarTaskInstanceActual(
								grupo.getIdProcessInstance(),
								usuarioLogueado.getCodigoUsuario());
				if (!tareaAsignada) {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"Ocurrio un error al inicializar la tarea. Consulte con el administrador del sistema.");
					return false;
				}
			}
		}

		return true;
	}

	private Boolean validar(Boolean controlarCab) {
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR,
					"Nivel es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR,
					"Entidad es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR,
					"OEE es un campo requerido. Verifique.");
			return false;
		}

		// if (fechaInhabilitacionDesde != null && fechaInhabilitacionHasta !=
		// null && fechaInhabilitacionDesde.after(fechaInhabilitacionHasta)) {
		// statusMessages.add(Severity.ERROR,
		// "El rango de fechas es inv\u00E1lido, la Fecha Desde no debe superar la Fecha Hasta.");
		// return;
		// }
		//

		return true;
	}
	
	
	
	public void initAgregarDocumento() throws Exception {
		
		this.concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, this.idConcursoPuestoAgr);
				
		sinEntidad = concursoPuestoAgr.getConcurso().getConfiguracionUoCab().getEntidad().getSinEntidad();
		if (sinEntidad != null)
			nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
	
		//Verificar sin Entidad y 	
		
	}
	
	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {

		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}
	
	public boolean tieneDocAdjunto(Long idConcursoPuestoAgr){
		
		String sql  ="select doc.* from general.documentos doc "
				+ " join seleccion.datos_especificos datos on doc.id_datos_especificos_tipo_documento = datos.id_datos_especificos"
				+ " and  datos.valor_alf = 'DOC_PUBLICACION'"
				+ "	and doc.activo = true "
				+ "	and nombre_tabla = 'CONCURSO_DESIERTO'"
				+ " where id_concurso_puesto_agr = " + idConcursoPuestoAgr;
		
		List <Documentos> docs = em.createNativeQuery(sql,Documentos.class).getResultList();
		
		return docs.isEmpty();
		
	}
	
	public boolean tieneDocAdjunto(){
		
		String sql  ="select doc.* from general.documentos doc "
				+ " join seleccion.datos_especificos datos on doc.id_datos_especificos_tipo_documento = datos.id_datos_especificos"
				+ " and  datos.valor_alf = 'DOC_PUBLICACION'"
				+ "	and doc.activo = true "
				+ "	and nombre_tabla = 'CONCURSO_DESIERTO'"
				+ " where id_concurso_puesto_agr = " + idConcursoPuestoAgr;
		
		List <Documentos> docs = em.createNativeQuery(sql,Documentos.class).getResultList();
		
		return docs.isEmpty();
		
	}
	
	public void imprimirDocumento() {
		
		
		try{
		
			String sql  ="select doc.* from general.documentos doc "
					+ " join seleccion.datos_especificos datos on doc.id_datos_especificos_tipo_documento = datos.id_datos_especificos"
					+ " and  datos.valor_alf = 'DOC_PUBLICACION'"
					+ "	and doc.activo = true"
					+ "	and nombre_tabla = 'CONCURSO_DESIERTO'"
					+ " where id_concurso_puesto_agr = " + this.idConcursoPuestoAgr;
				
		
		List <Documentos> docs = em.createNativeQuery(sql,Documentos.class).getResultList();
		
		Long id = docs.get(0).getIdDocumento();
		
		String usuario = "Invitado";
		
		if (usuarioLogueado != null )
			usuario = usuarioLogueado.getCodigoUsuario();
		
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(id,usuario);
		
		}catch(Exception e){
			e.printStackTrace();
			
		}
					
	}
	
		
	public void btnAgregarDocumento() throws Exception{
		
		
		try {
			
						
			Long id= null;
			
			if(fileName != null && !fileName.equals("")){
				if(tituloDocumentoAdjunto.equals("")){
					tituloDocumentoAdjunto = "Documento Adjunto";
					
				}
				
				id = this.guardarAdjuntoPublicacion(fileName, tamArchivo, contentType, uploadedFile);
			
				 //documentosAdjuntoPublicacion = em.find(Documentos.class,id);
				 
				 
			}
			//ZD 16/03/16 -- Se incluye mensajes de las validaciones
			if(id == -9){
				statusMessages.add(Severity.ERROR,"Ingrese los datos obligatorios");
				return;
			}else if (id == -7){
				statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
				return;
			}
			
			String texto = this.genTextoPublicacion();
			PublicacionPortal pp = obtenerPublicacionPortal(this.concursoPuestoAgr.getIdConcursoPuestoAgr(), this.concursoPuestoAgr.getConcurso().getIdConcurso(),texto );
			
							
			if(id != null ){
									
				Documentos docs = em.find(Documentos.class, id);
				
				docs.setIdTabla(pp.getIdPublicacionPortal());
				em.merge(docs);
				em.flush();
				
				
				 texto = pp.getTexto(); 
				
								
				texto = texto + "<dd><h3><a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=ImprimirDocAdjunto&#38;idPublicacionPortal="
						+pp.getIdPublicacionPortal()
						+ "'>"
						+ tituloDocumentoAdjunto
						+ "</a></h3></dd>";
				
				pp.setTexto(texto);
				em.merge(pp);
				em.flush();
				
				
			}
			
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
				
								
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
					
		
	
}
	
	
	
	public void finalizarConcursoDesierto(){
		ConcursoPuestoAgr cpa  = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		if(cpa != null){
			cpa.setEstado(38);// DESIERTO CON DOCUMENTO
			em.merge(cpa);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Concurso Desierto Finalizado");
			
			idConcursoPuestoAgr = null;
		}else{
			statusMessages.clear();
			statusMessages.add(Severity.INFO, "No se actualizo el registro");
		}
		
		
		
	}
	
	
	public PublicacionPortal obtenerPublicacionPortal(Long idConcursoPuestoAgr, Long idConcurso, String texto) {
		
		PublicacionPortal entity;
		
		String sql  ="select doc.* from general.documentos doc "
				+ " join seleccion.datos_especificos datos on doc.id_datos_especificos_tipo_documento = datos.id_datos_especificos"
				+ " and  datos.valor_alf = 'DOC_PUBLICACION'"
				+ "	and doc.activo = true"
				+ "	and nombre_tabla = 'CONCURSO_DESIERTO'"
				+ " where id_concurso_puesto_agr = " + this.idConcursoPuestoAgr;
		
	
		List<Documentos> listDocs = em.createNativeQuery(sql, Documentos.class).getResultList();
		
		if(listDocs.size() > 0 && listDocs.get(0) != null &&listDocs.get(0).getIdTabla()!= null){
			Documentos doc = listDocs.get(0);
			entity = em.find(PublicacionPortal.class,doc.getIdTabla()) ;
			entity.setTexto(texto);
			em.merge(entity);
			doc.setActivo(false);
			em.merge(doc);
		}else{
			entity = new PublicacionPortal();
			entity.setFechaAlta(new Date());
			entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			entity.setActivo(true);
			entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
			entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(idConcursoPuestoAgr);
			entity.setConcurso(new Concurso());
			entity.getConcurso().setIdConcurso(idConcurso);
			entity.setTexto(texto);
			em.persist(entity);
		}
		
		entity.setTexto(texto);
		em.persist(entity);
		em.flush();
		
		return entity;

	}
	private Long guardarAdjuntoPublicacion(String fileName, int tamArchivo,String contentType, byte[] uploadedFile) {
		try {
			
			String sql = "select * from seleccion.datos_especificos where valor_alf = 'DOC_PUBLICACION'";
			
			 List <DatosEspecificos> list = em.createNativeQuery(sql,DatosEspecificos.class).getResultList();
			 
			 Long idTipoDoc = list.get(0).getIdDatosEspecificos();
			
			UploadItem fileItem = seleccionUtilFormController.crearUploadItem(fileName, tamArchivo, contentType, uploadedFile);
			Long idDocuGenerado;
			
			String nombreTabla = "CONCURSO_DESIERTO";
			
			String direccionFisica = File.separator
					+ "CONCURSO_DESIERTO" + File.separator +concursoPuestoAgr.getConcurso().getIdConcurso() + File.separator + concursoPuestoAgr.getIdConcursoPuestoAgr();
			idDocuGenerado = admDocAdjuntoFormController.guardarDocParaPublicacion(fileItem,direccionFisica, nombrePantalla, idTipoDoc,usuarioLogueado.getCodigoUsuario() ,nombreTabla,concursoPuestoAgr.getConcurso().getIdConcurso(),concursoPuestoAgr.getIdConcursoPuestoAgr());
			
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

private String genTextoPublicacion() {
	String texto = new String();
	SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
	String fechaPublicacion = sdfFecha.format(new Date()).toString();
	String hr = "<hr>";
	String h1O = "<h1>";
	String h1C = "</h1>";
	String h5O = "<h5>";
	String h5C = "</h5>";
	String h3O = "<h3>";
	String h3C = "</h3>";
	String h4O = "<h4>";
	String h4C = "</h4>";
	String spanO = "<span>";
	String spanC = "</span>";
	String boldO = "<b>";
	String boldC = "</b>";
	String tabO = "<dd>";
	String tabC = "</dd>";
	String fontO = "<font size = 3>";
	String fontC = "</font>";
	String br = "</br>";
	
//		if(publicDetalle != null && !publicDetalle.equals(""))
//			texto += tabO + h3O + spanO + publicDetalle+ spanC+ h3C + tabC;
		
				
		if(publicDetalle != null && !publicDetalle.equals(""))
			texto += h3O + spanO + publicDetalle+ spanC+ h3C;
		
		
		
	return hr + fechaPublicacion + texto;
}

	public String getConcursoDesc() {
		return concursoDesc;
	}

	public void setConcursoDesc(String concursoDesc) {
		this.concursoDesc = concursoDesc;
	}

	public String getGrupoDesc() {
		return grupoDesc;
	}

	public void setGrupoDesc(String grupoDesc) {
		this.grupoDesc = grupoDesc;
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

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public String getPublicDetalle() {
		return publicDetalle;
	}

	public void setPublicDetalle(String publicDetalle) {
		this.publicDetalle = publicDetalle;
	}
	
	public String getTituloDocumentoAdjunto() {
		return tituloDocumentoAdjunto;
	}

	public void setTituloDocumentoAdjunto(String tituloDocumentoAdjunto) {
		this.tituloDocumentoAdjunto = tituloDocumentoAdjunto;
	}

	public byte[] getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(byte[] uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public UploadItem getItem() {
		return item;
	}

	public void setItem(UploadItem item) {
		this.item = item;
	}

	public Integer getTamArchivo() {
		return tamArchivo;
	}

	public void setTamArchivo(Integer tamArchivo) {
		this.tamArchivo = tamArchivo;
	}

	public String getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

}