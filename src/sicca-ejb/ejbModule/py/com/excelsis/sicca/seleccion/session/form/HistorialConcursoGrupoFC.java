package py.com.excelsis.sicca.seleccion.session.form;

import java.sql.Connection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;
import enums.EvalsReferenciales;
import py.com.excelsis.sicca.circuitoCSI.session.form.AdmSorteo511;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.reportes.form.RptDatosModificacionGrupo;
import py.com.excelsis.sicca.reportes.form.RptPostulantesInscrpCancelado;
import py.com.excelsis.sicca.reportes.form.RptRevisionPublicacionGrupo;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.ImprimirDatosGrupoFC;
import py.com.excelsis.sicca.session.form.MatrizDocConfigDetListFormController;
import py.com.excelsis.sicca.session.form.PublicacionConcursoListFormController;
import py.com.excelsis.sicca.session.form.PublicarConcursoSFPFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EvalReferencialTipoeval;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.HistorialActividadesGrupo;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.seleccion.session.HistorialActividadesGrupoList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

//@Scope(ScopeType.CONVERSATION)
@Scope(ScopeType.PAGE)
@Name("historialConcursoGrupoFC")
public class HistorialConcursoGrupoFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	ConcursoPuestoAgrList concursoPuestoAgrList;
	@In(create = true)
	HistorialActividadesGrupoList historialActividadesGrupoList;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	private Long idConcursoPuestoAgr;
	private String concurso;
	private String grupo;
	private DatosGrupoPuesto datosGrupoPuesto;
	private List<ConcursoPuestoAgr> listaGrupos;
	private ConcursoPuestoAgr concursoPuestoAgr;

	private String ubicacionFisica = "";

	private String estadoGrupo;

	private List<HistorialActividadesGrupo> listaHistorialGrupos;
	private String actividad;
	private Long idHistorial;

	private HistorialActividadesGrupo historialActividadesGrupo;

	private List<SelectItem> reporteSelectItem = new ArrayList<SelectItem>();
	private String reporte;

	private Integer nroExpediente;
	private Integer anhoExpediente;
	private Long idTipoConcurso;
	private List<Referencias> referencias;
	
	//RVeron, 20140926
	private boolean verOEETodas;




	public void init() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		referencias = new ArrayList<Referencias>();
		cargarReferencias();
		search();
		
		//RVeron, 20140926
		verOEETodas = isUsuarioHomologador(usuarioLogueado);
					
	}
	
	//RVeron, 20140926
	private boolean isUsuarioHomologador(Usuario usuario){
		Iterator <UsuarioRol> it = usuario.getUsuarioRols().iterator();
		boolean retorno = false;
		
		while (it.hasNext()){
			if(it.next().getRol().getHomologador())
				retorno = true;
		}
				
		return retorno;
		
	}
	

	private void cargarReferencias() {
		String sql = "select ref from Referencias ref "
				+ "where ref.dominio = 'ESTADOS_GRUPO' "
				+ "and ref.valorNum > 9 " + "order by ref.valorNum";
		referencias = em.createQuery(sql).getResultList();
	}

	public void searchAll() {
		limpiar();
		cargarCabecera();
		search();
	}

	public void search() {
		if (!validar(false))
			return;

		buscar();
	}

	public void buscar() {
		nivelEntidadOeeUtil.init();
		concursoPuestoAgrList.setIdConfiguracionUo(nivelEntidadOeeUtil
				.getIdConfigCab());
		concursoPuestoAgrList.setConcurso(concurso);
		concursoPuestoAgrList.setGrupo(grupo);
		concursoPuestoAgrList.setActivo(true);
		concursoPuestoAgrList.setAnho(anhoExpediente);
		concursoPuestoAgrList.setNroExpediente(nroExpediente);
		concursoPuestoAgrList.setIdTipoConcurso(idTipoConcurso);
		listaGrupos = concursoPuestoAgrList.listaResultadosCU463();
	}

	public void cargarCabecera() {
		grupoPuestosController = (GrupoPuestosController) Component
				.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();

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

		nivelEntidadOeeUtil.init();
	}

	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		concurso = null;
		grupo = null;
		idConcursoPuestoAgr = null;
		anhoExpediente = null;
		nroExpediente = null;
		idTipoConcurso = null;
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

		return true;
	}

	/** VER **/
	public void initView() {
		try {

			concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
					grupoPuestosController.getIdConcursoPuestoAgr());
			estadoGrupo = seleccionUtilFormController
					.getEstadoGrupo(grupoPuestosController
							.getIdConcursoPuestoAgr());

			historialActividadesGrupoList
					.setIdConcursoPuestoAgr(grupoPuestosController
							.getIdConcursoPuestoAgr());
			listaHistorialGrupos = historialActividadesGrupoList
					.getResultList();
			
			datosGrupoPuesto  = obtenerDatosGrupoPuestoXIdConcursoPuestoAgr(concursoPuestoAgr.getIdConcursoPuestoAgr());
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Ocurrió un error al cargar la página.");
		}

	}
	
	private DatosGrupoPuesto obtenerDatosGrupoPuestoXIdConcursoPuestoAgr (Long idConcursoPuestoAgr){
		DatosGrupoPuesto dgp = new DatosGrupoPuesto();
		String sql = "select * from seleccion.datos_grupo_puesto where id_concurso_puesto_agr = "+idConcursoPuestoAgr;
		
		dgp = (DatosGrupoPuesto) em.createNativeQuery(sql, DatosGrupoPuesto.class).getSingleResult();
		
		
		return dgp;
		
	}
	
	private List<EvalReferencialTipoeval> obtenerTiposDeEvaluacionesXIdConcursoPuestoAgr (Long idConcursoPuestoAgr){
		
		
		String sql = "select * from seleccion.eval_referencial_tipoeval tipoeval where tipoeval.id_concurso_puesto_agr =  "+idConcursoPuestoAgr;
		
		List<EvalReferencialTipoeval> list = em.createNativeQuery(sql, EvalReferencialTipoeval.class).getResultList();
		
		
		return list;
		
	}

	public String verAnexo() {
		if (actividad != null && !"".equals(actividad)) {
			HistorialActividadesGrupo historialActividadesGrupo = em.find(
					HistorialActividadesGrupo.class, idHistorial);
			idConcursoPuestoAgr = historialActividadesGrupo
					.getConcursoPuestoAgr().getIdConcursoPuestoAgr();

			String url = "/seleccion/adminDocAdjunto/AdmDocAdjunto.seam";
			String nombrePantalla = "";
			String idCU = "";
			String nombreTabla = "";
			String mostrarDoc = "false";
			String isEdit = "false";
			String from = "seleccion/historial/HistorialConcursoGrupoView";

			String pagina = url + "?mostrarDoc=" + mostrarDoc + "&isEdit="
					+ isEdit + "&from=" + from;

			if (ActividadEnum.CARGA_GRUPO.getValor()
					.equalsIgnoreCase(actividad)) {
				// 1 actividad
				nombreTabla = "ConcursoPuestoAgr";
				idCU = idConcursoPuestoAgr.toString();
				nombrePantalla = "adm_carga_grupo_list";
			/*} else if (ActividadEnum.ENVIAR_HOMOLOGACION.getValor()//MODIFICADO RV
					.equalsIgnoreCase(actividad)) {
				// 2 actividad
				Observacion observacion = seleccionUtilFormController
						.getObservacion(historialActividadesGrupo.getId());
				nombreTabla = "Observacion";
				idCU = (observacion != null ? observacion.getIdObservacion()
						.toString() : "0");
				nombrePantalla = "envioGruposHomologacion";*/
			} else if (ActividadEnum.APROBAR_HOMOLOG_SFP.getValor()
					.equalsIgnoreCase(actividad)) {
				// 3 actividad
				HomologacionPerfilMatriz homologacionPerfilMatriz = seleccionUtilFormController
						.getHomologacionPerfilMatriz(idConcursoPuestoAgr);
				nombreTabla = "HomologacionPerfilMatriz";
				idCU = homologacionPerfilMatriz != null ? homologacionPerfilMatriz
						.getIdHomologacion().toString() : null;
				nombrePantalla = "perfilMatrizHomologacion_edit";
			} else if (ActividadEnum.HOMOLOGACION_OEE.getValor()
					.equalsIgnoreCase(actividad)) {
				// 4 actividad
				HomologacionPerfilMatriz homologacionPerfilMatriz = seleccionUtilFormController
						.getHomologacionPerfilMatriz(idConcursoPuestoAgr);
				nombreTabla = "HomologacionPerfilMatriz";
				idCU = homologacionPerfilMatriz != null ? homologacionPerfilMatriz
						.getIdHomologacion().toString() : null;
				nombrePantalla = "enviarHomologacionPerfil_edit";
//			} else if (ActividadEnum.ELABORAR_DOC_HOMOLOG.getValor()
//					.equalsIgnoreCase(actividad)) {
//				// 5 actividad
//				nombreTabla = "Concurso";
//				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
//						.getConcurso().getIdConcurso().toString();
//				nombrePantalla = "admDocHomologacion_edit";
			} else if (ActividadEnum.FIRMA_RESOL_HOMOLOG.getValor()
					.equalsIgnoreCase(actividad)) {
				// 6 actividad
				nombreTabla = "Resolucion";
				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
						.getResolucionHomologacion().getIdResolucion()
						.toString();
				nombrePantalla = "FirmaResolHomolog";
			} else if (ActividadEnum.MODIFICAR_DATOS_CONCURSO.getValor()
					.equalsIgnoreCase(actividad)) {
				// 7 actividad
				nombreTabla = "Concurso";
				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
						.getConcurso().getIdConcurso().toString();
				nombrePantalla = "modificacion_datos_concurso_list";
//			} else if (ActividadEnum.SOLICITAR_PUBLICACION.getValor()
//					.equalsIgnoreCase(actividad)) {
//				// 8 actividad
//				nombreTabla = "Concurso";
//				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
//						.getConcurso().getIdConcurso().toString();
//				nombrePantalla = "publicacion_concursos_list";
//			} else if (ActividadEnum.APROBAR_PUBLICACION.getValor()
//					.equalsIgnoreCase(actividad)) {
//				// 9 actividad
//				nombreTabla = "Concurso";
//				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
//						.getConcurso().getIdConcurso().toString();
//				nombrePantalla = "publicacion_concursos_sfp";
//			} else if (ActividadEnum.REVISION_PUBLICACION_OEE.getValor()
//					.equalsIgnoreCase(actividad)) {
//				// 10 actividad
//				nombreTabla = "Concurso";
//				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
//						.getConcurso().getIdConcurso().toString();
//				nombrePantalla = "verificarPublicacion";
			} else if (ActividadEnum.RECIBIR_POSTULACIONES.getValor()
					.equalsIgnoreCase(actividad)) {
				// 11 actividad
				return null;
			} else if (ActividadEnum.PRORROGAR_POSTULACION.getValor()
					.equalsIgnoreCase(actividad)) {
				// 12 actividad
				FechasGrupoPuesto fechasGrupoPuesto = seleccionUtilFormController
						.getFechasGrupoPuesto(idConcursoPuestoAgr);
				nombreTabla = "FechasGrupoPuesto";
				idCU = fechasGrupoPuesto != null ? fechasGrupoPuesto
						.getIdFechas().toString() : null;
				nombrePantalla = "admProPos425";
			} else if (ActividadEnum.EVALUACION_DOCUMENTAL.getValor()
					.equalsIgnoreCase(actividad)) {
				// 13 actividad
				url = "/seleccion/evalDocumentosPostulantes/EvalDocumentalCabList.seam";
				pagina = url + "?mostrarDoc=" + mostrarDoc + "&isEdit="
						+ isEdit + "&from=" + from;
				nombreTabla = "ConcursoPuestoAgr";
				idCU = idConcursoPuestoAgr.toString();
				nombrePantalla = "evaluar_doc_postulante_list";
			} else if (ActividadEnum.CSI_REALIZAR_SORTEO.getValor()
					.equalsIgnoreCase(actividad)) {
				// 13-CSI actividad
				nombreTabla = "Postulacion";
				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
						.getConcurso().getIdConcurso().toString();
				nombrePantalla = "realizarSorteo";
			} else if(ActividadEnum.EVALUACION_DOCUMENTAL
					.getValor().equalsIgnoreCase(actividad)) {
			} else if (ActividadEnum.ELABORAR_PUBLICACION_LISTA_LARGA
					.getValor().equalsIgnoreCase(actividad)) {
				// 14 actividad
				nombreTabla = "ConcursoPuestoAgr";
				idCU = idConcursoPuestoAgr.toString();
				nombrePantalla = "lista_larga_list";
			} else if (ActividadEnum.REALIZAR_EVALUACIONES.getValor()
					.equalsIgnoreCase(actividad)) {
				// 15 actividad
				EvalReferencialTipoeval evalReferencialTipoeval = seleccionUtilFormController
						.getEvalReferencialTipoeval(idConcursoPuestoAgr);
				nombreTabla = "eval_referencial_tipoeval";
				idCU = evalReferencialTipoeval != null ? evalReferencialTipoeval
						.getIdEvalReferencialTipoeval().toString() : null;
				nombrePantalla = "realizarEvals";
			} else if (ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA
					.getValor().equalsIgnoreCase(actividad)) {
				nombreTabla = "ConcursoPuestoAgr";
				idCU = idConcursoPuestoAgr.toString();
				nombrePantalla = "elaborar_lista_corta";
			} else if (ActividadEnum.REALIZAR_ENTREVISTA_FINAL.getValor()
					.equalsIgnoreCase(actividad)) {
				// 17 actividad
				nombreTabla = "Concurso";
				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
						.getConcurso().getIdConcurso().toString();
				nombrePantalla = "realizarEntrevistaFinal";
			} else if (ActividadEnum.VALIDAR_MATRIZ_DOCUMENTAL_ADJ.getValor()
					.equalsIgnoreCase(actividad)) {
				nombreTabla = "ConcursoPuestoAgr";
				idCU = idConcursoPuestoAgr.toString();
				nombrePantalla = "evaluacion_doc_adjudicados";
			} else if (ActividadEnum.PUBLICAR_ADJUDICADOS.getValor()
					.equalsIgnoreCase(actividad)) {
				// 19 actividad
				nombreTabla = "ConcursoPuestoAgr";
				idCU = idConcursoPuestoAgr.toString();
				nombrePantalla = "publicacion_seleccionados";
//			} else if (ActividadEnum.ELABORAR_RESOLUCION_ADJUDICACION
//					.getValor().equalsIgnoreCase(actividad)) {
//				// 20. actividad
//				nombreTabla = "Concurso";
//				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
//						.getConcurso().getIdConcurso().toString();
//				nombrePantalla = "elaborarResolucionAdjudicacion_edit";
			} else if (ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION.getValor()
					.equalsIgnoreCase(actividad)) {
				// 21 actividad
				nombreTabla = "Resolucion";
				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
						.getResolucionAdjudicacion().getIdResolucion()
						.toString();
				nombrePantalla = "FirmaResolAdjudic";
			} else if (ActividadEnum.ELABORAR_DECRECTO_PRESIDENCIAL.getValor()
					.equalsIgnoreCase(actividad)) {
				// 22 actividad
				nombreTabla = "Concurso";
				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
						.getConcurso().getIdConcurso().toString();
				nombrePantalla = "administrarDecreto_edit";
//			} else if (ActividadEnum.EDITAR_DOC_PRESIDENCIA_REPUBLICA
//					.getValor().equalsIgnoreCase(actividad)) {
//				return null;
			} else if (ActividadEnum.INGRESAR_POSTULANTE.getValor()
					.equalsIgnoreCase(actividad)) {
				// 24 actividad
				nombreTabla = "Concurso";
				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
						.getConcurso().getIdConcurso().toString();
				nombrePantalla = "gestionarIngresosConcursos";
//			} else if (ActividadEnum.ELABORAR_RESOLUCION_NOMBRAMIENTO
//					.getValor().equalsIgnoreCase(actividad)) {
//				// 25 actividad
//				nombreTabla = "Concurso";
//				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
//						.getConcurso().getIdConcurso().toString();
//				nombrePantalla = "resolucionNombramiento_edit";
//			} else if (ActividadEnum.FIRMAR_RESOLUCION_NOMBRAMIENTO.getValor()
//					.equalsIgnoreCase(actividad)) {
//				// 26 actividad
//				nombreTabla = "Resolucion";
//				idCU = historialActividadesGrupo.getConcursoPuestoAgr()
//						.getResolucionNombramiento().getIdResolucion()
//						.toString();
//				nombrePantalla = "FirmaResolNombram";
			} else if (ActividadEnum.REVISION_EMPATES.getValor()
					.equalsIgnoreCase(actividad)) {
				return null;
			}

			pagina += "&nombrePantalla=" + nombrePantalla + "&idCU=" + idCU
					+ "&concursoPuestoAgrIdConcursoPuestoAgr=" + idCU
					+ "&nombreTabla=" + nombreTabla;

			return pagina;
		}
		return null;
	}

	public Boolean mostrarAnexo(Long idHistorial) {
		if (idHistorial != null) {
			HistorialActividadesGrupo historialActividadesGrupo = em.find(
					HistorialActividadesGrupo.class, idHistorial);
			actividad = historialActividadesGrupo.getCodActividad();
			if (ActividadEnum.RECIBIR_POSTULACIONES.getValor()
					.equalsIgnoreCase(actividad)
//					|| ActividadEnum.EDITAR_DOC_PRESIDENCIA_REPUBLICA
//							.getValor().equalsIgnoreCase(actividad)
					|| ActividadEnum.REVISION_EMPATES.getValor()
							.equalsIgnoreCase(actividad)
					|| ActividadEnum.COMPLETAR_CARPETAS.getValor()
							.equalsIgnoreCase(actividad) ){
//					|| ActividadEnum.EVALUACION_DOCUMENTAL.getValor()
//				.equalsIgnoreCase(actividad)){
				return false;
			}
		}
		return true;
	}

	public Boolean mostrarImprimirGrupo(Long idHistorial) {
		if (idHistorial != null) {
			HistorialActividadesGrupo historialActividadesGrupo = em.find(
					HistorialActividadesGrupo.class, idHistorial);
			actividad = historialActividadesGrupo.getCodActividad();
			/*if (ActividadEnum.ENVIAR_HOMOLOGACION.getValor().equalsIgnoreCase(
					actividad)
					|| ActividadEnum.APROBAR_HOMOLOG_SFP.getValor()
							.equalsIgnoreCase(actividad)
					|| ActividadEnum.FIRMA_RESOL_HOMOLOG.getValor()
							.equalsIgnoreCase(actividad)
					|| ActividadEnum.PRORROGAR_POSTULACION.getValor()
							.equalsIgnoreCase(actividad)
					|| ActividadEnum.EVALUACION_DOCUMENTAL.getValor()
							.equalsIgnoreCase(actividad)
					|| ActividadEnum.REALIZAR_ENTREVISTA_FINAL.getValor()
							.equalsIgnoreCase(actividad)
					|| ActividadEnum.VALIDAR_MATRIZ_DOCUMENTAL_ADJ.getValor()
							.equalsIgnoreCase(actividad)
					|| ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION.getValor()
							.equalsIgnoreCase(actividad)
					|| ActividadEnum.EDITAR_DOC_PRESIDENCIA_REPUBLICA
							.getValor().equalsIgnoreCase(actividad)
					|| ActividadEnum.INGRESAR_POSTULANTE.getValor()
							.equalsIgnoreCase(actividad)
					|| ActividadEnum.FIRMAR_RESOLUCION_NOMBRAMIENTO.getValor()
							.equalsIgnoreCase(actividad)
					|| ActividadEnum.REVISION_EMPATES.getValor()
							.equalsIgnoreCase(actividad)) { // No tienen reporte
				return false;
			}*/
		}
		return true;
	}

	public void cargarReporteGrupo(Long idHistorial) {
		if (idHistorial != null) {
			historialActividadesGrupo = em.find(
					HistorialActividadesGrupo.class, idHistorial);
			actividad = historialActividadesGrupo.getCodActividad();
			reporteSelectItem = new ArrayList<SelectItem>();
			if (actividad != null && !"".equals(actividad)) {

				// LAS ACTIVIDADES QUE SE ENCUENTRAN COMENTADAS ES PORQUE NO
				// TIENEN REPORTES
				// LA IDEA ES AGREGAR A MEDIDA QUE VAYAN SURGIENDO LOS REPORTES.
				if (ActividadEnum.CARGA_GRUPO.getValor().equalsIgnoreCase(
						actividad)) {
					// 1 actividad
					reporteSelectItem.add(new SelectItem("DG",
							"Datos del Grupo"));
					reporteSelectItem.add(new SelectItem("MD",
							"Matriz Documental"));
					reporteSelectItem.add(new SelectItem("MR",
							"Matriz Referencial"));
				}
				// else if
				// (ActividadEnum.ENVIAR_HOMOLOGACION.getValor().equalsIgnoreCase(actividad))
				// {
				// // 2 actividad
				// }
				// else if
				// (ActividadEnum.APROBAR_HOMOLOG_SFP.getValor().equalsIgnoreCase(actividad))
				// {
				// // 3 actividad
				// }
				else if (ActividadEnum.HOMOLOGACION_OEE.getValor()
						.equalsIgnoreCase(actividad)) {
					// 4 actividad
					reporteSelectItem.add(new SelectItem("DH",
							"Datos de Homologación"));
//				} else if (ActividadEnum.ELABORAR_DOC_HOMOLOG.getValor()
//						.equalsIgnoreCase(actividad)) {
//					// 5 actividad
//					reporteSelectItem.add(new SelectItem("M", "Memo"));
//					reporteSelectItem.add(new SelectItem("N", "Nota"));
//					reporteSelectItem.add(new SelectItem("R", "Resolución"));
				}
				// else if
				// (ActividadEnum.FIRMA_RESOL_HOMOLOG.getValor().equalsIgnoreCase(actividad))
				// {
				// // 6 actividad
				// }
				else if (ActividadEnum.MODIFICAR_DATOS_CONCURSO.getValor()
						.equalsIgnoreCase(actividad)) {
					// 7 actividad
					reporteSelectItem.add(new SelectItem("M",
							"Modificación Datos del Concurso"));
//				} else if (ActividadEnum.SOLICITAR_PUBLICACION.getValor()
//						.equalsIgnoreCase(actividad)) {
//					// 8 actividad
//					reporteSelectItem.add(new SelectItem("G",
//							"Generar Formato Publicación"));
//				} else if (ActividadEnum.APROBAR_PUBLICACION.getValor()
//						.equalsIgnoreCase(actividad)) {
//					// 9 actividad
//					reporteSelectItem.add(new SelectItem("PM",
//							"Perfil y Matriz"));
//					reporteSelectItem
//							.add(new SelectItem("F",
//									"Fechas, Cronograma, Lugar de Present. y Aclarac."));
//				} else if (ActividadEnum.REVISION_PUBLICACION_OEE.getValor()
//						.equalsIgnoreCase(actividad)) {
//					// 10 actividad
//					reporteSelectItem.add(new SelectItem("RP",
//							"Revisión Publicación"));
				} else if (ActividadEnum.RECIBIR_POSTULACIONES.getValor()
						.equalsIgnoreCase(actividad)) {
					// 11 actividad
					reporteSelectItem.add(new SelectItem("P",
							"Postulantes Inscriptos/Cancelados"));
				} else if (ActividadEnum.COMPLETAR_CARPETAS.getValor()
						.equalsIgnoreCase(actividad)) {
					// 11 actividad
					reporteSelectItem.add(new SelectItem("CC",
							"Postulantes Inscriptos/Cancelados"));
				}
				// else if
				// (ActividadEnum.PRORROGAR_POSTULACION.getValor().equalsIgnoreCase(actividad))
				// {
				// // 12 actividad
				// }
				// else if
				// (ActividadEnum.EVALUACION_DOCUMENTAL.getValor().equalsIgnoreCase(actividad))
				// {
				// // 13 actividad
				// }
				else if (ActividadEnum.ELABORAR_PUBLICACION_LISTA_LARGA
						.getValor().equalsIgnoreCase(actividad)) {
					// 14 actividad
					reporteSelectItem.add(new SelectItem("LL","Lista Larga Admitidos"));
					reporteSelectItem.add(new SelectItem("LNA","Lista No Admitidos"));
					
					
				} else if (ActividadEnum.REALIZAR_EVALUACIONES.getValor()
						.equalsIgnoreCase(actividad)) {
					// 15 actividad
					reporteSelectItem.add(new SelectItem("E","Imprimir Evaluaciones"));
					if (datosGrupoPuesto != null && datosGrupoPuesto.getPorMinPorEvaluacion() != null && datosGrupoPuesto.getPorMinPorEvaluacion()){
						
						
						for(EvalReferencialTipoeval tipoeval : obtenerTiposDeEvaluacionesXIdConcursoPuestoAgr (this.concursoPuestoAgr.getIdConcursoPuestoAgr())){
							reporteSelectItem.add(new SelectItem(""+tipoeval.getDatosEspecificos().getIdDatosEspecificos(),"Admitidos - " +tipoeval.getDatosEspecificos().getDescripcion()));
						}
					}
					
					
				} else if (ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA.getValor().equalsIgnoreCase(actividad)) {
					DatosGrupoPuesto datos = (DatosGrupoPuesto) em.createNativeQuery("select * from seleccion.datos_grupo_puesto where id_concurso_puesto_agr = "+ this.concursoPuestoAgr.getIdConcursoPuestoAgr(),DatosGrupoPuesto.class).getResultList().get(0);
					if(datos.getMerito()){
						reporteSelectItem.add(new SelectItem("LFM_C","Lista Final Mérito con cédula"));
						reporteSelectItem.add(new SelectItem("LCM_SIN_C","Lista Final Mérito con código usuario"));
					}
					
					if(datos.getTerna()){
						reporteSelectItem.add(new SelectItem("LCT_C","Lista Corta Terna con cédula"));
						reporteSelectItem.add(new SelectItem("LCT_SIN_C","Lista Corta Terna con código usuario"));
					}
					
				}
				// else if
				// (ActividadEnum.REALIZAR_ENTREVISTA_FINAL.getValor().equalsIgnoreCase(actividad))
				// {
				// // 17 actividad
				// }
				// else if
				// (ActividadEnum.VALIDAR_MATRIZ_DOCUMENTAL_ADJ.getValor().equalsIgnoreCase(actividad))
				// {
				// }
				else if (ActividadEnum.PUBLICAR_ADJUDICADOS.getValor()
						.equalsIgnoreCase(actividad)) {
					// 19 actividad
					reporteSelectItem.add(new SelectItem("PS",
							"Publicación de Seleccionados"));
//				} else if (ActividadEnum.ELABORAR_RESOLUCION_ADJUDICACION
//						.getValor().equalsIgnoreCase(actividad)) {
//					// 20. actividad
//					reporteSelectItem.add(new SelectItem("BR",
//							"Borrador de Resolución Homolog."));
				}
				// else if
				// (ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION.getValor().equalsIgnoreCase(actividad))
				// {
				// // 21 actividad
				// }
				else if (ActividadEnum.ELABORAR_DECRECTO_PRESIDENCIAL
						.getValor().equalsIgnoreCase(actividad)) {
					// 22 actividad
					reporteSelectItem.add(new SelectItem("BR",
							"Borrador de Resolución Homolog."));
				}
				// else if
				// (ActividadEnum.EDITAR_DOC_PRESIDENCIA_REPUBLICA.getValor().equalsIgnoreCase(actividad))
				// {
				// reporteSelectItem = new ArrayList<SelectItem>();
				// }
				// else if
				// (ActividadEnum.INGRESAR_POSTULANTE.getValor().equalsIgnoreCase(actividad))
				// {
				// // 24 actividad
				// }
//				else if (ActividadEnum.ELABORAR_RESOLUCION_NOMBRAMIENTO
//						.getValor().equalsIgnoreCase(actividad)) {
//					// 25 actividad
//					reporteSelectItem.add(new SelectItem("BR",
//							"Borrador de Resolución Homolog."));
//				}
				// else if
				// (ActividadEnum.FIRMAR_RESOLUCION_NOMBRAMIENTO.getValor().equalsIgnoreCase(actividad))
				// {
				// // 26 actividad
				// }
				// else if
				// (ActividadEnum.REVISION_EMPATES.getValor().equalsIgnoreCase(actividad))
				// {
				// reporteSelectItem = new ArrayList<SelectItem>();
				// }
			}
		}
	}

	/**
	 * Imprime el reporte por grupo
	 * @throws Exception 
	 */
	public void imprimirGrupo() throws Exception {
		ReportUtilFormController reportUtilFormController = (ReportUtilFormController) Component
				.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		actividad = historialActividadesGrupo.getCodActividad();
		reporteSelectItem = new ArrayList<SelectItem>();
		if (actividad != null && !"".equals(actividad)) {

			if (ActividadEnum.CARGA_GRUPO.getValor()
					.equalsIgnoreCase(actividad)) {
				// 1 actividad
				if ("DG".equals(reporte)) {
					// Datos del Grupo
					ImprimirDatosGrupoFC imprimirDatosGrupoFC = (ImprimirDatosGrupoFC) Component
							.getInstance(ImprimirDatosGrupoFC.class, true);
					imprimirDatosGrupoFC.imprimir(historialActividadesGrupo
							.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
				} else if ("MD".equals(reporte)) {
					// Matriz Documental
					MatrizDocConfigDetListFormController matrizDocConfigDetListFormController = (MatrizDocConfigDetListFormController) Component
							.getInstance(
									MatrizDocConfigDetListFormController.class,
									true);
					matrizDocConfigDetListFormController
							.setIdConcursoPuestoAgr(historialActividadesGrupo
									.getConcursoPuestoAgr()
									.getIdConcursoPuestoAgr());
					matrizDocConfigDetListFormController.init();
					matrizDocConfigDetListFormController.print();
				} else if ("MR".equals(reporte)) {
					// Matriz Referencial
					ConfMatrizEvaluacionFormController confMatrizEvaluacionFormController = (ConfMatrizEvaluacionFormController) Component
							.getInstance(
									ConfMatrizEvaluacionFormController.class,
									true);

					GrupoPuestosController grupoPuestoController = (GrupoPuestosController) Component
							.getInstance(GrupoPuestosController.class, true);

					grupoPuestoController
							.setIdConcursoPuestoAgr(historialActividadesGrupo
									.getConcursoPuestoAgr()
									.getIdConcursoPuestoAgr());
					grupoPuestoController.initCabecera();
					confMatrizEvaluacionFormController
							.setGrupoPuestosController(grupoPuestoController);
					confMatrizEvaluacionFormController.pdf();
				}
			} else if (ActividadEnum.HOMOLOGACION_OEE.getValor()
					.equalsIgnoreCase(actividad)) {
				// 4 actividad
				DatosHomologacionGrupoFC datosHomologacionGrupoFC = (DatosHomologacionGrupoFC) Component
						.getInstance(DatosHomologacionGrupoFC.class, true);
				datosHomologacionGrupoFC
						.setIdConcursoPuestoAgr(concursoPuestoAgr
								.getIdConcursoPuestoAgr());
				datosHomologacionGrupoFC.imprimirReporte();

//			} else if (ActividadEnum.ELABORAR_DOC_HOMOLOG.getValor()
//					.equalsIgnoreCase(actividad)) {
//				// 5 actividad
//				if ("M".equals(reporte)) {
//					GenerarMemoHomologacion generarMemoHomologacion = (GenerarMemoHomologacion) Component
//							.getInstance(GenerarMemoHomologacion.class, true);
//
//					if (historialActividadesGrupo.getConcursoPuestoAgr()
//							.getMemoHomologacion() == null)
//						generarMemoHomologacion.setIdMemoHomologacion(new Long(
//								0));
//					else
//						generarMemoHomologacion
//								.setIdMemoHomologacion(historialActividadesGrupo
//										.getConcursoPuestoAgr()
//										.getMemoHomologacion()
//										.getIdMemoHomologacion());
//
//					generarMemoHomologacion.print();
//				} else if ("N".equals(reporte)) {
//					// idNotaHomologacion
//					GenerarNotaHomologacion generarNotaHomologacion = (GenerarNotaHomologacion) Component
//							.getInstance(GenerarNotaHomologacion.class, true);
//
//					if (historialActividadesGrupo.getConcursoPuestoAgr()
//							.getNotaHomologacion() == null)
//						generarNotaHomologacion.setIdNotaHomologacion(new Long(
//								0));
//					else
//						generarNotaHomologacion
//								.setIdNotaHomologacion(historialActividadesGrupo
//										.getConcursoPuestoAgr()
//										.getNotaHomologacion()
//										.getIdNotaHomologacion());
//
//					generarNotaHomologacion.print();
//				} else if ("R".equals(reporte)) {
//					GenBorradorResolucionHomologacion genBorradorResolucionHomologacion = (GenBorradorResolucionHomologacion) Component
//							.getInstance(
//									GenBorradorResolucionHomologacion.class,
//									true);
//
//					if (historialActividadesGrupo.getConcursoPuestoAgr()
//							.getResolucionHomologacion() == null)
//						genBorradorResolucionHomologacion
//								.setIdResolucionHomologacion(new Long(0));
//					else
//						genBorradorResolucionHomologacion
//								.setIdResolucionHomologacion(historialActividadesGrupo
//										.getConcursoPuestoAgr()
//										.getResolucionHomologacion()
//										.getIdResolucion());
//
//					genBorradorResolucionHomologacion.print();
//				}
			} else if (ActividadEnum.MODIFICAR_DATOS_CONCURSO.getValor()
					.equalsIgnoreCase(actividad)) {
				// 7 actividad
				RptDatosModificacionGrupo rptDatosModificacionGrupo = (RptDatosModificacionGrupo) Component
						.getInstance(RptDatosModificacionGrupo.class, true);
				rptDatosModificacionGrupo
						.setIdConcursoPuestoAgr(concursoPuestoAgr
								.getIdConcursoPuestoAgr());
				rptDatosModificacionGrupo.imprimir();
//			} else if (ActividadEnum.SOLICITAR_PUBLICACION.getValor()
//					.equalsIgnoreCase(actividad)) {
//				PublicacionConcursoListFormController publicacionConcursoListFormController = (PublicacionConcursoListFormController) Component
//						.getInstance(
//								PublicacionConcursoListFormController.class,
//								true);
//
//				concursoPuestoAgrHome.setInstance(historialActividadesGrupo
//						.getConcursoPuestoAgr());
//				concursoPuestoAgrHome.setId(historialActividadesGrupo
//						.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
//				publicacionConcursoListFormController
//						.setConcursoPuestoAgrHome(concursoPuestoAgrHome);
//				publicacionConcursoListFormController.init();
//
//				publicacionConcursoListFormController.print();
//			} else if (ActividadEnum.APROBAR_PUBLICACION.getValor()
//					.equalsIgnoreCase(actividad)) {
//				if ("PM".equals(reporte)) {
//					PublicarConcursoSFPFormController publicarConcursoSFPFormController = (PublicarConcursoSFPFormController) Component
//							.getInstance(
//									PublicarConcursoSFPFormController.class,
//									true);
//
//					publicarConcursoSFPFormController
//							.setIdConcursoPuestoAgr(historialActividadesGrupo
//									.getConcursoPuestoAgr()
//									.getIdConcursoPuestoAgr());
//					publicarConcursoSFPFormController.imprimirPerfilMatriz();
//				} else if ("F".equals(reporte)) {
//					// Todavia no esta el reporte
//				}
//			} else if (ActividadEnum.REVISION_PUBLICACION_OEE.getValor()
//					.equalsIgnoreCase(actividad)) {
//				RptRevisionPublicacionGrupo rptRevisionPublicacionGrupo = (RptRevisionPublicacionGrupo) Component
//						.getInstance(RptRevisionPublicacionGrupo.class, true);
//				rptRevisionPublicacionGrupo
//						.setIdConcursoPuestoAgr(concursoPuestoAgr
//								.getIdConcursoPuestoAgr());
//				rptRevisionPublicacionGrupo.imprimirReporte();
//
			} else if (ActividadEnum.RECIBIR_POSTULACIONES.getValor()
					.equalsIgnoreCase(actividad)) {
				RptPostulantesInscrpCancelado rptPostulantesInscrpCancelado = (RptPostulantesInscrpCancelado) Component
						.getInstance(RptPostulantesInscrpCancelado.class, true);
				rptPostulantesInscrpCancelado
						.setIdConcursoPuestoAgr(concursoPuestoAgr
								.getIdConcursoPuestoAgr());
				rptPostulantesInscrpCancelado.imprimir();
			} else if (ActividadEnum.COMPLETAR_CARPETAS.getValor()
					.equalsIgnoreCase(actividad)) {
				RptPostulantesInscrpCancelado rptPostulantesInscrpCancelado = (RptPostulantesInscrpCancelado) Component
						.getInstance(RptPostulantesInscrpCancelado.class, true);
				rptPostulantesInscrpCancelado
						.setIdConcursoPuestoAgr(concursoPuestoAgr
								.getIdConcursoPuestoAgr());
				rptPostulantesInscrpCancelado.imprimir();
			} else if (ActividadEnum.CSI_REALIZAR_SORTEO.getValor()
					.equalsIgnoreCase(actividad)) {
				AdmSorteo511 admSorteo511 = (AdmSorteo511) Component
						.getInstance(AdmSorteo511.class, true);
				admSorteo511.setIdConcursoPuestoAgr(concursoPuestoAgr
						.getIdConcursoPuestoAgr());
				admSorteo511.init2();
				admSorteo511.imprimir();
			}

			else if (ActividadEnum.ELABORAR_PUBLICACION_LISTA_LARGA.getValor()
					.equalsIgnoreCase(actividad)) {
				ListaLargaFormController listaLargaFormController = (ListaLargaFormController) Component
						.getInstance(ListaLargaFormController.class, true);

				concursoPuestoAgrHome.setInstance(historialActividadesGrupo
						.getConcursoPuestoAgr());
				concursoPuestoAgrHome.setId(historialActividadesGrupo
						.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
				listaLargaFormController
						.setConcursoPuestoAgrHome(concursoPuestoAgrHome);
//				try {
//					listaLargaFormController.init();
//					listaLargaFormController.print();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
				listaLargaFormController.init();
				if ("LL".equals(reporte)) {
					listaLargaFormController.print();
				}
				
				if ("LNA".equals(reporte)) {
					listaLargaFormController.printListaNoAdmitidos();
				}
				
				
			} else if (ActividadEnum.REALIZAR_EVALUACIONES.getValor()
					.equalsIgnoreCase(actividad)) {
				
				RealizarEvalsFormController realizarEvalsFormController = (RealizarEvalsFormController) Component
						.getInstance(RealizarEvalsFormController.class, true);
				
			
				realizarEvalsFormController.setIdConcursoPuestoAgr(historialActividadesGrupo.getConcursoPuestoAgr().getIdConcursoPuestoAgr());

				
				if ("E".equals(reporte)) {
					realizarEvalsFormController.imprimirEvaluacionesDetalladas();
				}
				if(datosGrupoPuesto.getPorMinPorEvaluacion() != null && datosGrupoPuesto.getPorMinPorEvaluacion()){
					if(!reporte.equals("E")){
						Long idDatosEspecifico = new Long(reporte);
						if(idDatosEspecifico != null) {
																			
							realizarEvalsFormController.setIdDatosEspecificos(idDatosEspecifico);
							realizarEvalsFormController.imprimirListaAdmitidosXEvaluacion();
							
						}
					}
				}
				
				
				
			} else if (ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA.getValor().equalsIgnoreCase(actividad)) {
				
//				EvaluaRefereFC evaluaRefereFC = (EvaluaRefereFC) Component.getInstance(EvaluaRefereFC.class, true);
//				EvalsReferenciales evalsRefsRadio = EvalsReferenciales.CON_DET;
//				evaluaRefereFC.setEvalsRefsRadio(evalsRefsRadio);

				grupoPuestosController.setIdConcursoPuestoAgr(historialActividadesGrupo.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
				
				grupoPuestosController.initCabecera();
				
//				evaluaRefereFC.getNivelEntidadOeeUtil().setIdConfigCab(grupoPuestosController.getConfiguracionUoCab().getIdConfiguracionUo());
//				
//				evaluaRefereFC.getNivelEntidadOeeUtil().setIdSinEntidad(grupoPuestosController.getSinEntidad().getIdSinEntidad());
//				
//				evaluaRefereFC.getNivelEntidadOeeUtil().setIdSinNivelEntidad(grupoPuestosController.getSinNivelEntidad().getIdSinNivelEntidad());
//				
//				evaluaRefereFC.getNivelEntidadOeeUtil().setDenominacionUnidad(grupoPuestosController.getConfiguracionUoCab().getDenominacionUnidad());
//				
//				evaluaRefereFC.getNivelEntidadOeeUtil().setNombreNivelEntidad(grupoPuestosController.getSinNivelEntidad().getNenNombre());
//				
//				evaluaRefereFC.getNivelEntidadOeeUtil().setNombreSinEntidad(grupoPuestosController.getSinEntidad().getEntNombre());
//				
//				evaluaRefereFC.setIdConcurso(historialActividadesGrupo.getConcursoPuestoAgr().getConcurso().getIdConcurso());
//				
//				evaluaRefereFC.setIdConcursoPuestoAgr(historialActividadesGrupo.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
//				evaluaRefereFC.imprimir();
				
				
				ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
				Connection conn = JpaResourceBean.getConnection();
				
				HashMap<String, Object> param = new HashMap<String, Object>();
				param.put("idConcursoPuestoAgr",this.concursoPuestoAgr.getIdConcursoPuestoAgr());
				param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
				param.put("path_logo", servletContext.getRealPath("/img/"));
				
				SinNivelEntidad nivelEntidad = grupoPuestosController.getSinNivelEntidad();
				SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
				ConfiguracionUoCab configuracionUoCab = grupoPuestosController.getConfiguracionUoCab();
				
				param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
				param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
				param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());
				
				if(usuarioLogueado != null )
					param.put("usuario", usuarioLogueado.getCodigoUsuario());
				else
					param.put("usuario", "Visitante Portal");
				
				HistorialActividadesGrupo historial = obtenerFechaActividad(this.concursoPuestoAgr.getIdConcursoPuestoAgr(), ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA.getDescripcion());
				if (historial==null || historial.getFechaFin()==null)
					param.put("fecha", "Aun no se ha publicado");
				else{
					Format formato = new SimpleDateFormat("dd-MM-yyyy");
					param.put("fecha", formato.format(historial.getFechaFin()));
				}

				if ("LFM_C".equals(reporte)) {
					JasperReportUtils.respondPDF("listaFinalMerito_cedula",	param, false, conn,usuarioLogueado);
				}
				if ("LCM_SIN_C".equals(reporte)) {
					JasperReportUtils.respondPDF("listaFinalMerito",	param, false, conn,usuarioLogueado);
				}
				if ("LCT_C".equals(reporte)) {
					JasperReportUtils.respondPDF("listaCortaTerna_cedula",	param, false, conn,usuarioLogueado);
				}
				if ("LCT_SIN_C".equals(reporte)) {
					JasperReportUtils.respondPDF("listaCortaTerna",	param, false, conn,usuarioLogueado);
				}
				conn.close();
						
			} else if (ActividadEnum.PUBLICAR_ADJUDICADOS.getValor()
					.equalsIgnoreCase(actividad)) {
				// 19 actividad
				ElaborarPublicacionSeleccionadosFormController elaborarPublicacionSeleccionadosFormController = (ElaborarPublicacionSeleccionadosFormController) Component
						.getInstance(
								ElaborarPublicacionSeleccionadosFormController.class,
								true);

				concursoPuestoAgrHome.setInstance(historialActividadesGrupo
						.getConcursoPuestoAgr());
				concursoPuestoAgrHome.setId(historialActividadesGrupo
						.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
				elaborarPublicacionSeleccionadosFormController
						.setConcursoPuestoAgrHome(concursoPuestoAgrHome);
				elaborarPublicacionSeleccionadosFormController.init();
				elaborarPublicacionSeleccionadosFormController.print();
//			} else if (ActividadEnum.ELABORAR_RESOLUCION_ADJUDICACION
//					.getValor().equalsIgnoreCase(actividad)) {
//				// 20. actividad
//				GenBorradorResolucionHomologacion genBorradorResolucionHomologacion = (GenBorradorResolucionHomologacion) Component
//						.getInstance(GenBorradorResolucionHomologacion.class,
//								true);
//
//				if (historialActividadesGrupo.getConcursoPuestoAgr()
//						.getResolucionAdjudicacion() == null)
//					genBorradorResolucionHomologacion
//							.setIdResolucionHomologacion(new Long(0));
//				else
//					genBorradorResolucionHomologacion
//							.setIdResolucionHomologacion(historialActividadesGrupo
//									.getConcursoPuestoAgr()
//									.getResolucionAdjudicacion()
//									.getIdResolucion());
//
//				genBorradorResolucionHomologacion.print();
			} else if (ActividadEnum.ELABORAR_DECRECTO_PRESIDENCIAL.getValor()
					.equalsIgnoreCase(actividad)) {
				// 22 actividad
				GenBorradorResolucionHomologacion genBorradorResolucionHomologacion = (GenBorradorResolucionHomologacion) Component
						.getInstance(GenBorradorResolucionHomologacion.class,
								true);

				if (historialActividadesGrupo.getConcursoPuestoAgr()
						.getDecreto() == null)
					genBorradorResolucionHomologacion
							.setIdResolucionHomologacion(new Long(0));
				else
					genBorradorResolucionHomologacion
							.setIdResolucionHomologacion(historialActividadesGrupo
									.getConcursoPuestoAgr().getDecreto()
									.getIdResolucion());

				genBorradorResolucionHomologacion.print();
//			} else if (ActividadEnum.ELABORAR_RESOLUCION_NOMBRAMIENTO
//					.getValor().equalsIgnoreCase(actividad)) {
//				// 25 actividad
//				GenBorradorResolucionHomologacion genBorradorResolucionHomologacion = (GenBorradorResolucionHomologacion) Component
//						.getInstance(GenBorradorResolucionHomologacion.class,
//								true);
//
//				if (historialActividadesGrupo.getConcursoPuestoAgr()
//						.getResolucionNombramiento() == null)
//					genBorradorResolucionHomologacion
//							.setIdResolucionHomologacion(new Long(0));
//				else
//					genBorradorResolucionHomologacion
//							.setIdResolucionHomologacion(historialActividadesGrupo
//									.getConcursoPuestoAgr()
//									.getResolucionNombramiento()
//									.getIdResolucion());
//
//				genBorradorResolucionHomologacion.print();
			}

			cargarReporteGrupo(historialActividadesGrupo.getId());
			// grupoPuestosController.setIdConcursoPuestoAgr(historialActividadesGrupo.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			// initView();
		}

	}
	
	
	public boolean tieneDocAdjunto(){
		
		String sql  ="select doc.* from general.documentos doc "
				+ " join seleccion.datos_especificos datos on doc.id_datos_especificos_tipo_documento = datos.id_datos_especificos"
				+ " and  datos.valor_alf = 'DOC_PUBLICACION'"
				+ "	and doc.activo = true "
				+ "	and nombre_tabla = 'CONCURSO_DESIERTO'"
				+ " where id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr();
		
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
					+ " where id_concurso_puesto_agr = " + + concursoPuestoAgr.getIdConcursoPuestoAgr();
				
		
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

	public String mostrarCronograma(Integer valor) {
		if(referencias == null){
			referencias = new ArrayList<Referencias>();
			cargarReferencias();
		}
		if (valor != null) {
			for (Referencias r : referencias) {
				if (valor.intValue() == r.getValorNum().intValue())
					return "SI";
			}
		}
		return "NO";
	}

	/**
	 * Imprime el reporte por concurso
	 */
	public void imprimir() {
		ReportUtilFormController reportUtilFormController = (ReportUtilFormController) Component
				.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		/* Setear Parametros */
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfAnho = new SimpleDateFormat("yyyy");
		Concurso concurso = concursoPuestoAgr.getConcurso();
		Query q = em
				.createQuery("select Referencias from Referencias Referencias "
						+ "where Referencias.activo is true and Referencias.dominio ='ESTADOS_GRUPO' "
						+ "and Referencias.valorNum =" + concurso.getEstado());
		List<Referencias> lRef = q.getResultList();
		if (lRef.size() == 1) {
			reportUtilFormController
					.setNombreReporte("RPT_CU464_DATOS_INICIALES_CONCURSO");
			reportUtilFormController.getParametros().put("idConcurso",
					concurso.getIdConcurso());
			reportUtilFormController.getParametros().put(
					"nroAnhoConcurso",
					concurso.getNumero() == null ? "#" : concurso.getNumero()
							+ "/"
							+ (concurso.getAnhio() == null ? "#" : concurso
									.getAnhio()));
			reportUtilFormController.getParametros().put(
					"nrAnhoExpediente",
					(concurso.getNroExpediente() == null ? "#" : concurso
							.getNroExpediente())
							+ "/"
							+ (concurso.getFechaExpediente() == null ? "#"
									: sdfAnho.format(concurso
											.getFechaExpediente())));
			reportUtilFormController.getParametros().put("tipoConcurso",
					concurso.getDatosEspecificosTipoConc().getDescripcion());
			reportUtilFormController.getParametros().put(
					"adReferendum",
					concurso.getAdReferendum() != null
							&& concurso.getAdReferendum() ? "Sí" : "No");
			reportUtilFormController.getParametros().put(
					"pcd",
					concurso.getPcd() != null && concurso.getPcd() ? "Sí"
							: "No");
			reportUtilFormController.getParametros().put("fechaCreacion",
					sdf.format(concurso.getFechaCreacion()));
			reportUtilFormController.getParametros().put("estadoActual",
					lRef.get(0).getDescLarga());
			reportUtilFormController.getParametros().put(
					"observacion",
					concurso.getObservacion() != null
							&& !concurso.getObservacion().isEmpty() ? concurso
							.getObservacion() : "-");

			reportUtilFormController.getParametros().put("nivel",
					grupoPuestosController.getSinNivelEntidad().getNenNombre());
			reportUtilFormController.getParametros().put("entidad",
					grupoPuestosController.getSinEntidad().getEntNombre());
			reportUtilFormController.getParametros().put(
					"oee",
					grupoPuestosController.getConfiguracionUoCab()
							.getDescripcionCorta());
			reportUtilFormController.getParametros().put(
					"concursoCodNom",
					(concurso.getNumero() == null ? "#" : concurso.getNumero())
							+ "/"
							+ (concurso.getAnhio() == null ? "#" : concurso
									.getAnhio())
							+ " - "
							+ concurso.getConfiguracionUoCab()
									.getDescripcionCorta());
			reportUtilFormController.imprimirReportePdf();

		} else {
			statusMessages
					.add(Severity.ERROR,
							"No se puede imprimir el reporte porque no se ha encontrado una descripción para el estado del Concurso");
			return;
		}

	}

	public List<SelectItem> updateTipoConcItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : datosEspecificosByTipoConc())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> datosEspecificosByTipoConc() {
		try {
			List<DatosEspecificos> datosEspecificosLists = em
					.createQuery(
							"Select d from DatosEspecificos d "
									+ " where d.datosGenerales.descripcion like 'TIPOS DE CONCURSO' and d.activo=true order by d.descripcion")
					.getResultList();

			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	private HistorialActividadesGrupo obtenerFechaActividad(Long idGrupo, String descripcionActividad) {
		String sql = "SELECT * FROM seleccion.historial_actividades_grupo hist " 
				+ "WHERE hist.descripcion = '" + descripcionActividad
				+ "' and hist.id_concurso_puesto_agr = "	+ idGrupo.toString()
				+ " and hist.fecha_fin is not null";
		
		HistorialActividadesGrupo resultado;
		try {
			resultado = (HistorialActividadesGrupo) em.createNativeQuery(sql, HistorialActividadesGrupo.class).getSingleResult();
		} catch (Exception e) {
			//El que llama este metodo tiene que verificar el valor de retorno.
			resultado = null;
		}
		return resultado;
	}
	
	/** GETTER'S && SETTER'S **/

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public List<ConcursoPuestoAgr> getListaGrupos() {
		return listaGrupos;
	}

	public void setListaGrupos(List<ConcursoPuestoAgr> listaGrupos) {
		this.listaGrupos = listaGrupos;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getConcurso() {
		return concurso;
	}

	public void setConcurso(String concurso) {
		this.concurso = concurso;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setEstadoGrupo(String estadoGrupo) {
		this.estadoGrupo = estadoGrupo;
	}

	public String getEstadoGrupo() {
		return estadoGrupo;
	}

	public void setListaHistorialGrupos(
			List<HistorialActividadesGrupo> listaHistorialGrupos) {
		this.listaHistorialGrupos = listaHistorialGrupos;
	}

	public List<HistorialActividadesGrupo> getListaHistorialGrupos() {
		return listaHistorialGrupos;
	}

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public void setIdHistorial(Long idHistorial) {
		this.idHistorial = idHistorial;
	}

	public Long getIdHistorial() {
		return idHistorial;
	}

	public void setReporteSelectItem(List<SelectItem> reporteSelectItem) {
		this.reporteSelectItem = reporteSelectItem;
	}

	public List<SelectItem> getReporteSelectItem() {
		return reporteSelectItem;
	}

	public void setReporte(String reporte) {
		this.reporte = reporte;
	}

	public String getReporte() {
		return reporte;
	}

	public Integer getNroExpediente() {
		return nroExpediente;
	}

	public void setNroExpediente(Integer nroExpediente) {
		this.nroExpediente = nroExpediente;
	}

	public Integer getAnhoExpediente() {
		return anhoExpediente;
	}

	public void setAnhoExpediente(Integer anhoExpediente) {
		this.anhoExpediente = anhoExpediente;
	}

	public Long getIdTipoConcurso() {
		return idTipoConcurso;
	}

	public void setIdTipoConcurso(Long idTipoConcurso) {
		this.idTipoConcurso = idTipoConcurso;
	}
	
	//RVeron, 20140926
	public boolean isVerOEETodas() {
		return verOEETodas;
	}

}
