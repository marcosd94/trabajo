package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.transaction.SystemException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.transaction.Transaction;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.entity.EmpresaPersona;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalAbiertas;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalReferencial;
import py.com.excelsis.sicca.entity.EvalReferencialCab;
import py.com.excelsis.sicca.entity.EvalReferencialComis;
import py.com.excelsis.sicca.entity.EvalReferencialConvocatoria;
import py.com.excelsis.sicca.entity.EvalReferencialDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.EvalReferencialTipoeval;
import py.com.excelsis.sicca.entity.MatrizRefConf;
import py.com.excelsis.sicca.entity.MatrizRefConfDet;
import py.com.excelsis.sicca.entity.MatrizRefConfEnc;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PromocionConcursoAgr;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.legajo.session.form.VistaPreviaLegajoFC;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.EvalAbiertasList;
import py.com.excelsis.sicca.seleccion.session.EvalCerradasList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.Tab6VistaPreliminarFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import enums.ActividadEnum;
import enums.AusentePresente;
import enums.EvalsReferenciales;
import enums.TipoEvaluacion;
import enums.TiposDatos;


@Scope(ScopeType.CONVERSATION)
@Name("realizarEvalsFormController")
public class RealizarEvalsFormController {
	@In(create = true, required = false)
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true, required = false, value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	@In(create = true)
	Tab6VistaPreliminarFormController tab6VistaPreliminarFormController;
	@In(required = false, create = true)
	EvalAbiertasList lEvalsAbiertasList;
	@In(required = false, create = true)
	EvalCerradasList lEvalsCerradasList;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	Tab7VistaPrePostulacionActualFC tab7VistaPrePostulacionActualFC;
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;
	@In(create = true)
	VistaPreviaLegajoFC vistaPreviaLegajoFC;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	CiudadList ciudadList;
	
	List<EvalAbiertas> lEvalsAbiertas;
	List<EvalAbiertas> lEvalsCerradas;
	List<MatrizRefConfEnc> lMatRefConfEnc;
	List<EvalReferencialComis> lEvaluadores;
	List<MatrizRefConfEnc> lTipoEval;
	private Boolean btnSgteEval = false;
	private Boolean continuarSgteTarea = false;
	private Boolean mostrarModal = false;
	List<String> lTipoEvalPasados;
	SimpleDateFormat sdfHORA = new SimpleDateFormat("HH:mm");
	private SeleccionUtilFormController seleccionUtilFormController;
	private EvalsReferenciales evalsRefsRadio;
	private String observacion;
	private String observacion1;
	private String motivo;
	private Float totalPuntos;
	private float sumatoriaFactores;
	private float puntajeMinimo;
	private Float totalAlcanzado;
	private TipoEvaluacion tipoEvaluacion;
	private static String TIPO_EVAL_COMISION = "comision";
	private static String TIPO_EVAL_EMP_TRECE = "empresa";
	private static String PORC_MIN_AL_FINALIZAR_EVALS = "AL_FINALIZAR";
	private static String PORC_MIN_POR_EVAL = "POR_CADA_EVALUACION";
	private String comisionSeleccion;
	private Long idComisionSeleccion;
	private List<SelectItem> empTereceSelecItem;
	private List<SelectItem> tipoEvalSelecItem;
	private List<SelectItem> integrantesSelectItem;
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelectItem;
	private Long idEmpTerce;
	private Long idTipoEval;
	private String tabActivo = "tab1";
	private Long idTipoEvalCerradas;
	private String textoModalSgteTarea;
	private Long idDatosEspecificosVer;
	private Long idInte;
	private Long idCiudad;
	private String codPostulante = "-";
	private String tipoEvalDesc = "-";
	private DatosEspecificos tipoEvalObj;
	private Boolean bloquearComboTipoEval = false;
	private AusentePresente presente;
	private Long idPostulacion;
	private Long idEvalDocumentalCab;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Boolean isEdit = false;
	private Boolean cambioEstado;
	private Long idDpto;
	private String etiquetaEvaluar = "Evaluar";
	

	
	private Integer nroOrden = 0;
	// Para controlar al operabilidad del combo de empresa tercerizada y la
	// descripcion de la comision de seleccion
	private Boolean apagarComiDesc;
	private Boolean apagarComboEmp;
	private String matriz;
	private String puntajeMax;
	private String porcMinAplicar;
	private String idPorcMinAplicar;
	private String seleccion;
	private String minimoDesc;
	Integer totalPostulantes;
	private Date fechaDesde;
	private Date fechaHasta;
	private String observacion3;
	private String lugar;
	private Date fechaConvo;
	private String direccion;
	private Boolean ver;
	private List<EvalReferencialConvocatoria> lEvalRefConvo;
	private int rowConvoSele = -1;
	private String horaDesde;
	private String horaHasta;
	private Boolean actualizar = false;
	private EvalReferencialTipoeval evalReferencialTipoeval;
	private Boolean mostrarColDisca = false;
	private String conDiscaDesc;
	private Long idPersona;
	private SeguridadUtilFormController seguridadUtilFormController;
	private String direccionFisica = "";
	private EvalReferencial evalReferencialSelected = null;
	

	private Set<EvalReferencialCab> lEvalReferencialCabEditado = null;
	Map<String, Float> diccPuntajesEditado = new HashMap<String, Float>();
	private Boolean presenteBoolean;
	private Boolean hayEvalObliAdelante = false;
	private Boolean esDesierto = false;
	private Long idPersonaPostulante;
	private Long idConcursoPuestoAgr;
	private Long idDatosEspecificos;
	private Boolean esCurricular = false;
	private String evalDocumentalFrom;
	private String evalDocumentalFromCIO;
	private DatosGrupoPuesto datosGrupoPuesto;
	private List<EvalReferencialPostulante> listaEmpatados = new ArrayList<EvalReferencialPostulante>();
	private boolean habilitarVolver;
	private EvalDocumentalCab evaldocumentalcab;
	

	private boolean habilitarGuardar;
	private ReentrantLock lock = new ReentrantLock();
	

	private void validarOee(Concurso concurso) {

		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso
				.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
		if (!seguridadUtilFormController
				.validaEstado(grupoPuestosController.getConcursoPuestoAgr()
						.getIdConcursoPuestoAgr(), "LISTA LARGA")) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	public void imprimirTab7() throws Exception {
		/*
		 * tab7VistaPrePostulacionActualFC.setIdPersonaPostulante(
		 * idPersonaPostulante); tab7VistaPrePostulacionActualFC.init2();
		 * tab7VistaPrePostulacionActualFC.pdf();
		 */
		PersonaPostulante pp = new PersonaPostulante();
		pp = em.find(PersonaPostulante.class, idPersonaPostulante);

		// tab6VistaPreliminarFormController.setIdPostulacion(evalDocumentalCab.getPostulacion().getIdPostulacion());
		tab6VistaPreliminarFormController.setPersona(pp.getPersona());
		tab6VistaPreliminarFormController.pdf();
	}

	public void imprimirLegajo() throws Exception {
		PersonaPostulante p = em.find(PersonaPostulante.class,
				idPersonaPostulante);
		vistaPreviaLegajoFC.setIdPersona(p.getPersona().getIdPersona());
		vistaPreviaLegajoFC.initEvaluacion();
		vistaPreviaLegajoFC.pdf();
	}
	
	public void imprimirCopiaLegajo() throws Exception {
		
		Postulacion pos = em.find(Postulacion.class, idPostulacion);
		//Buscar el documento generado en el momento de la postulacion
		String sql = " select * from general.documentos where nombre_fisico_doc like 'Copia_legajo_al_postularse_%' "
				+ " and id_persona =  "  + pos.getPersonaPostulante().getPersona().getIdPersona() 
				+ " and id_concurso =  " + pos.getConcursoPuestoAgr().getConcurso().getIdConcurso();
		
		Documentos copiaLegajo = (Documentos) em.createNativeQuery(sql, Documentos.class).getSingleResult();
		
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(copiaLegajo.getIdDocumento(),usuarioLogueado.getIdUsuario());
		
		
	}
	
	private Boolean verCopiaLegajo(){
		Boolean retorno = false;
		
		DatosEspecificos de = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr).getConcurso().getDatosEspecificosTipoConc();
		
		em.refresh(de);
		
		if(de.getValorAlf().equals("CII") || de.getValorAlf().equals("CIR") ){
			retorno = true;
		}
		
		return retorno;
	}
	
	
	public EvalAbiertas refreshEvalAbierto(EvalAbiertas eval) {
		if (eval.getDatosEspecificos() != null) {
			DatosEspecificos de = em.find(DatosEspecificos.class, eval
					.getDatosEspecificos().getIdDatosEspecificos());
			eval.setDatosEspecificos(de);
		}
		if (eval.getEvalReferencial() != null) {
			EvalReferencial er = em.find(EvalReferencial.class, eval
					.getEvalReferencial().getIdEvalReferencial());
			eval.setEvalReferencial(er);
		}
		if(eval.getEvalDocumentalCab() != null){
			EvalDocumentalCab edc = em.find(EvalDocumentalCab.class,eval.getEvalDocumentalCab().getIdEvalDocumentalCab());
			
			eval.setEvalDocumentalCab(edc);
		}
		return eval;
	}
	
	
	
	private List<ConcursoPuestoDet> ObtenerPuestosActivos(Long idConcursoPuestoAgr){
		String sql = " select * from seleccion.concurso_puesto_det where  activo = true and id_concurso_puesto_agr = "+idConcursoPuestoAgr;
		
		return em.createNativeQuery(sql, ConcursoPuestoDet.class).getResultList();
	}
	
	private List<PromocionConcursoAgr> ObtenerPuestosActivosPS(Long idConcursoPuestoAgr){
		String sql = " select * from seleccion.promocion_concurso_agr where  activo = true and id_concurso_puesto_agr = "+idConcursoPuestoAgr;
		
		return em.createNativeQuery(sql, PromocionConcursoAgr.class).getResultList();
	}


	/**
	 * Indica si un grupo fue declarado desierto. True si lo es, false en
	 * cualquier otro caso
	 * 
	 * @return
	 */
	private Boolean detectarDesiertoInit() {
		
		// Si se quedó con 0 o 1 postulante
		List<EvalAbiertas> postulantes = postulantesAEvaluar();
		
		if (postulantes.size() == 0 || postulantes.size() == 1) 
			return true;
		
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, this.idConcursoPuestoAgr);
		
		if(concursoPuestoAgr.getConcurso().getPromocion() != null && concursoPuestoAgr.getConcurso().getPromocion()){
			List <PromocionConcursoAgr> puestos = ObtenerPuestosActivosPS(this.idConcursoPuestoAgr);
			if (puestos.size() == 0) 
				return true;
			
			if(this.datosGrupoPuesto.getMerito()!= null && this.datosGrupoPuesto.getMerito()){
				if(postulantes.size() < puestos.size()+1 )
					return true;
				
			}else if (this.datosGrupoPuesto.getTerna()!= null && this.datosGrupoPuesto.getTerna()){
				if(postulantes.size() < puestos.size()+1 )
					return true;
				
			}
		}else{
			List <ConcursoPuestoDet> puestos = ObtenerPuestosActivos(this.idConcursoPuestoAgr);
			if (puestos.size() == 0) 
				return true;
			
			if(this.datosGrupoPuesto.getMerito()!= null && this.datosGrupoPuesto.getMerito()){
				if(postulantes.size() < puestos.size()+1 )
					return true;
				
			}else if (this.datosGrupoPuesto.getTerna()!= null && this.datosGrupoPuesto.getTerna()){
				if(postulantes.size() < puestos.size()+1 )
					return true;
				
			}
		}
					
		
		return false;
	}

	private void declararGrupoDesierto() {
		EvaluarDocPostulantesFormController evaluarDocPostulantesFormController = (EvaluarDocPostulantesFormController) Component
				.getInstance(EvaluarDocPostulantesFormController.class,
						true);
		evaluarDocPostulantesFormController.setConcursoPuestoAgr(grupoPuestosController.getConcursoPuestoAgr());
		evaluarDocPostulantesFormController.setCodActividad("REALIZAR_EVALUACIONES");
		
		
		if (detectarDesiertoInit()) {
			//Cantidad de postulantes a Evaluar
			List<EvalAbiertas> postulantes = postulantesAEvaluar();
					
			
			evaluarDocPostulantesFormController.getDeclararDesierto(postulantes.size(),evaluarDocPostulantesFormController.cantVacancias(),"seleccion/realizarEval/realizarEvals");
			esDesierto = evaluarDocPostulantesFormController.getDeclararDesierto();			
			if(esDesierto){
				ConcursoPuestoAgr grupo = em.find(ConcursoPuestoAgr.class,grupoPuestosController.getIdConcursoPuestoAgr());
				
				reponerCategorias(grupo);
				
				Observacion tablaObservacion = new Observacion();
				tablaObservacion.setConcurso(grupo.getConcurso());
				if (observacion != null && observacion.trim().isEmpty())
					tablaObservacion.setObservacion(observacion);
				else
					tablaObservacion.setObservacion("El grupo ha sido declarado desierto por no contar con la cantidad suficiente de postulantes");
				tablaObservacion.setFechaAlta(new Date());
				tablaObservacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				tablaObservacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
	
				tablaObservacion.setIdTaskInstance(jbpmUtilFormController.getIdTaskInstanceActual(grupo.getIdProcessInstance()));
	
				em.persist(tablaObservacion);
				em.flush();
				
				// Si el grupo esta inactivo se ha declarado desierto 
				
					esDesierto = true;
					statusMessages
							.add(Severity.ERROR,
									"El grupo ha sido declarado desierto por no contar con la cantidad suficiente de postulantes");
					return;
				
			
			}
			
		}else{
			
			if(this.terminoEvaluacion()){
				
				Integer cantPostulantes = obtenerCantidadPostulantes(grupoPuestosController.getIdConcursoPuestoAgr());
							
				evaluarDocPostulantesFormController.logicaDisminuirPuestos(cantPostulantes, "seleccion/realizarEval/realizarEvals");
			}
			
			
		}
	
	}
	
		
	
	//PERMITE OBTENER LA CANTIDAD DE POSTULANTES QUE HAYAN ESTADO PRESENTE EN TODAS LAS EVALUACIONES 
	private Integer obtenerCantidadPostulantes(Long idConcursoPuestoAgr){
		Integer retorno = 0;
		
		String sql = "select count (distinct id_postulacion ) from seleccion.eval_referencial  "
				+ " where id_postulacion in (select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = "
				+idConcursoPuestoAgr+ " ) "
				+ " and id_postulacion not in (select id_postulacion from seleccion.eval_referencial where presente is false "
				+ "  and id_postulacion in (select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = "
				+idConcursoPuestoAgr+ " ))";
		
		Integer aux = ((BigInteger) em.createNativeQuery(sql).getSingleResult()).intValue();
		
		if(aux != null &&  aux > 0)
			retorno = aux;
		
		return retorno;
	}

	/**
	 * Incidencia 0001637 Llamar al CU604 (le envía como parámetros el
	 * id_concurso_puesto_agr, y las cadenas ‘GRUPO’, ‘CONCURSO’)
	 */
	public void reponerCategorias(ConcursoPuestoAgr agr) {
		reponerCategoriasController.reponerCategorias(agr, "GRUPO", "CONCURSO");
	}

	/**
	 * Este método calcula las ultimas evaluaciones hechas y cerradas
	 */
	private List<EvalReferencialTipoeval> calEvlasHechas(Long idGrupo) {
		Query q = em
				.createQuery("Select EvalReferencialTipoeval  from EvalReferencialTipoeval EvalReferencialTipoeval "
						+ " where EvalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idGrupo
						+ " and fechaCierreEvaluacion is not null  order by idEvalReferencialTipoeval desc");
		List<EvalReferencialTipoeval> lista = q.getResultList();

		return lista;
	}

	private void initDireccionFisica() {
		Calendar c = Calendar.getInstance();
		String fSeparator = File.separator;
		direccionFisica = fSeparator
				+ "SICCA"
				+ fSeparator
				+ c.get(Calendar.YEAR)
				+ fSeparator
				+ "OEE"
				+ fSeparator
				+ grupoPuestosController.getConcursoPuestoAgr().getConcurso()
						.getConfiguracionUoCab().getIdConfiguracionUo()
				+ fSeparator
				+ grupoPuestosController.getConcursoPuestoAgr().getConcurso()
						.getDatosEspecificosTipoConc().getIdDatosEspecificos()
				+ fSeparator
				+ grupoPuestosController.getConcursoPuestoAgr().getConcurso()
						.getIdConcurso()
				+ fSeparator
				+ grupoPuestosController.getConcursoPuestoAgr()
						.getIdConcursoPuestoAgr();
	}

	private Boolean paramLimpios() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		Map<String, List<String>> diccParametro = new HashMap<String, List<String>>();
		diccParametro.put(TiposDatos.LONG.getValor(), new ArrayList<String>());
		if (grupoPuestosController.getIdConcursoPuestoAgr() != null)
			diccParametro.get(TiposDatos.LONG.getValor()).add(
					grupoPuestosController.getIdConcursoPuestoAgr().toString());
		if (grupoPuestosController.getIdPuesto() != null)
			diccParametro.get(TiposDatos.LONG.getValor()).add(
					grupoPuestosController.getIdPuesto().toString());
		return sufc.isParametrosLimpios(diccParametro);
	}

	public void init() throws Exception {
		/* Seguridad */
		if (!paramLimpios()) {
			return;
		}
		initDireccionFisica();
		// Se carga la lista de evaluaciones abiertas
		idConcursoPuestoAgr = grupoPuestosController.getConcursoPuestoAgr()
				.getIdConcursoPuestoAgr();
		validarOee(grupoPuestosController.getConcursoPuestoAgr().getConcurso());
		initAbiertas();
		initCerradas();
		cargarCabMatrizConfigurada();
		updateTipoEval();
		
		lTipoEval = getTipoEval();
		lTipoEvalPasados = new ArrayList<String>();
		totalPostulantes = null;
		siguienteEvaluacion();
		// Filtrar el listado de evaluaciones abiertas
		aplicarFiltradoListaAbierta();
		verificarConcursoDisca();
		obtenerDatosGrupoPuesto();
		declararGrupoDesierto();
		
		lEvalsAbiertas = quicksort(lEvalsAbiertas);
		lEvalsCerradas = quicksort(lEvalsCerradas);
		
		evalDocumentalFrom = "/seleccion/realizarEval/realizarEvals.seam?idConcursoPuesto="
					+ idConcursoPuestoAgr;
		
		evalDocumentalFromCIO = "/seleccion/realizarEval/CIO/realizarEvals.seam?idConcursoPuesto="
				+ idConcursoPuestoAgr;

	}
	//static
	public  List<EvalAbiertas> quicksort(List<EvalAbiertas> lEvalAbiertas) {
		if (lEvalAbiertas.size() <= 1)
			return lEvalAbiertas;
		int pivot = lEvalAbiertas.size() / 2;
		List<EvalAbiertas> lesser = new ArrayList<EvalAbiertas>();
		List<EvalAbiertas> greater = new ArrayList<EvalAbiertas>();
		int sameAsPivot = 0;
		for (EvalAbiertas evalAbiertas : lEvalAbiertas) {
			if (evalAbiertas
					.getPostulacion()
					.getPersonaPostulante()
					.getUsuAlta()
					.compareTo(
							lEvalAbiertas.get(pivot).getPostulacion()
									.getPersonaPostulante().getUsuAlta()) > 0)
				greater.add(evalAbiertas);
			else if (evalAbiertas
					.getPostulacion()
					.getPersonaPostulante()
					.getUsuAlta()
					.compareTo(
							lEvalAbiertas.get(pivot).getPostulacion()
									.getPersonaPostulante().getUsuAlta()) < 0)
				lesser.add(evalAbiertas);
			else
				sameAsPivot++;
		}
		lesser = quicksort(lesser);
		for (int i = 0; i < sameAsPivot; i++)
			lesser.add(lEvalAbiertas.get(pivot));
		greater = quicksort(greater);
		List<EvalAbiertas> sorted = new ArrayList<EvalAbiertas>();
		for (EvalAbiertas evalAbiertas : lesser)
			sorted.add(evalAbiertas);
		for (EvalAbiertas evalAbiertas : greater)
			sorted.add(evalAbiertas);
		return sorted;
	}
	
	private void obtenerDatosGrupoPuesto() {
		Query q = em
				.createQuery("select DatosGrupoPuesto from DatosGrupoPuesto DatosGrupoPuesto "
						+ "where DatosGrupoPuesto.activo is true "
						+ "and DatosGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getConcursoPuestoAgr()
								.getIdConcursoPuestoAgr()
						);
		this.datosGrupoPuesto = (DatosGrupoPuesto)q.getSingleResult();
		

	}

	private Boolean verificarConcursoDisca() {

		Query q = em
				.createQuery("select DatosGrupoPuesto from DatosGrupoPuesto DatosGrupoPuesto "
						+ "where DatosGrupoPuesto.activo is true "
						+ "and DatosGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getConcursoPuestoAgr()
								.getIdConcursoPuestoAgr()
						+ ""
						+ " and DatosGrupoPuesto.preferenciaPersDiscapacidad is true");
		List<DatosGrupoPuesto> lista = q.getResultList();
		if (grupoPuestosController.getConcursoPuestoAgr().getConcurso()
				.getPcd()
				&& lista.size() > 0) {
			mostrarColDisca = true;
		} else {
			mostrarColDisca = false;
		}
		return mostrarColDisca;
	}

	public void imprimirCv() {
		tab6VistaPreliminarFormController.setFromCU("88");
		tab6VistaPreliminarFormController.setIdPostulacion(idPostulacion);
		tab6VistaPreliminarFormController.pdf();
	}

	
	
	
	//AL CONTAR LA CANTIDAD DE APROBADOS DEBE VERIFICAR QUE SOLO CUENTE A LOS QUE PASARON EL MINIMO Y QUE HAYAN PARTICIPADO EN TODAS 
	//LAS ETAPAS. 
	
	
	private Integer countAprobados() {

		Query q = em
				.createQuery("select count(EvalReferencialPostulante) from EvalReferencialPostulante EvalReferencialPostulante "
						+ "where EvalReferencialPostulante.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
						+ "and EvalReferencialPostulante.activo is true  "
						+ "and EvalReferencialPostulante.postulacion.activo is true "
						+ "and EvalReferencialPostulante.puntajeRealizado >= :minimo "
						+ "and EvalReferencialPostulante.postulacion not in "
						+ "(select EvalReferencial.postulacion from EvalReferencial EvalReferencial "
						+ " join EvalReferencial.postulacion postulacion "
						+ "	where postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
						+ " and EvalReferencial.presente = false )"
						);
		
		
		
		q.setParameter("idGrupo",
				grupoPuestosController.getIdConcursoPuestoAgr());
		q.setParameter("minimo",new Float (this.datosGrupoPuesto.getPorcMinimo()));

		
		
		
		
		Integer count = ((Long) q.getSingleResult()).intValue();
		// Realizar los cambios propios a la última evaluación

		return count;
	}

	public Boolean getDeclararDesierto(
			EvaluarDocPostulantesFormController evaluarDocPostulantesFormController) {
		
		evaluarDocPostulantesFormController
				.setCodActividad("REALIZAR_EVALUACIONES");
		evaluarDocPostulantesFormController.getDeclararDesierto(countAprobados(),
				evaluarDocPostulantesFormController.cantVacancias(),
				"seleccion/realizarEval/realizarEvals");

		
		if (evaluarDocPostulantesFormController.getDeclararDesierto()) {
			statusMessages
					.add(Severity.ERROR,
							"El grupo ha sido declarado desierto porque no existen suficientes postulantes que hayan alcanzado el porcentaje mínimo.");
			esDesierto = true;
			return false;
		}

		if (evaluarDocPostulantesFormController.getContinuaValSgteTarea())
			return true;
		return false;
	}

	public void imprimir() {
		// Pasos previos, la configuracion del componente que realiza la logica
		// principal.
		if(evalsRefsRadio.getId().intValue() != EvalsReferenciales.CON_MATRIZ.getId().intValue()){
			EvaluaRefereFC evaluaRefereFC = (EvaluaRefereFC) Component.getInstance(
					EvaluaRefereFC.class, true);
			evaluaRefereFC.setEvalsRefsRadio(evalsRefsRadio);
			grupoPuestosController.initCabecera();
			evaluaRefereFC.getNivelEntidadOeeUtil().setIdConfigCab(
					grupoPuestosController.getConfiguracionUoCab()
							.getIdConfiguracionUo());
			evaluaRefereFC.getNivelEntidadOeeUtil().setIdSinEntidad(
					grupoPuestosController.getSinEntidad().getIdSinEntidad());
			evaluaRefereFC.getNivelEntidadOeeUtil().setIdSinNivelEntidad(
					grupoPuestosController.getSinNivelEntidad()
							.getIdSinNivelEntidad());
			evaluaRefereFC.getNivelEntidadOeeUtil().setDenominacionUnidad(
					grupoPuestosController.getConfiguracionUoCab()
							.getDenominacionUnidad());
			evaluaRefereFC.getNivelEntidadOeeUtil().setNombreNivelEntidad(
					grupoPuestosController.getSinNivelEntidad().getNenNombre());
			evaluaRefereFC.getNivelEntidadOeeUtil().setNombreSinEntidad(
					grupoPuestosController.getSinEntidad().getEntNombre());
			evaluaRefereFC.setIdConcurso(grupoPuestosController
					.getConcursoPuestoAgr().getConcurso().getIdConcurso());
			evaluaRefereFC.setIdConcursoPuestoAgr(grupoPuestosController
					.getIdConcursoPuestoAgr());
			evaluaRefereFC.setIdTipoEval(idTipoEvalCerradas);
			evaluaRefereFC.imprimir();
		} else {
			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			Connection conn = JpaResourceBean.getConnection();
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("concurso_puesto_agr",this.idConcursoPuestoAgr);
			param.put("desde", new Long(1));
			if(idTipoEval==null)
				param.put("etapa", new Long(8001));
			else
				param.put("etapa", idTipoEval);
			JasperReportUtils.respondPDF("ListaEvaluacionesRealizadasPlanillaHastaAhora",	param, false, conn,
					usuarioLogueado);
			try {
				conn.close();
				return;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void imprimirEvaluacionesDetalladas() {

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("concurso_puesto_agr",this.idConcursoPuestoAgr);
		
		JasperReportUtils.respondPDF("ListaEvaluacionesRealizadasPlanilla",	param, false, conn,usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}

	private void cargarEvaluadores() {
		Long idDatosEspecificos = null;
		if (idDatosEspecificosVer != null) {
			idDatosEspecificos = idDatosEspecificosVer;
		} else {
			MatrizRefConfEnc elFactor = em.find(MatrizRefConfEnc.class,
					idTipoEval);
			tipoEvalObj = elFactor.getDatosEspecificos();
			idDatosEspecificos = tipoEvalObj.getIdDatosEspecificos();
		}
		Query q = em
				.createQuery("select evalReferencialComis from EvalReferencialComis evalReferencialComis where "
						+ " evalReferencialComis.evalReferencial.postulacion.idPostulacion = "
						+ idPostulacion
						+ "and evalReferencial.evalReferencialTipoeval.datosEspecificos.idDatosEspecificos = "
						+ idDatosEspecificos);
		lEvaluadores = q.getResultList();
		
	}

	private List<EvalReferencialDet> lEvalReferencialDet(Long idCab) {
		Query q = em
				.createQuery("select EvalReferencialDet from EvalReferencialDet EvalReferencialDet "
						+ "where EvalReferencialDet.evalReferencialCab.idEvalReferencialCab = "
						+ idCab);
		List<EvalReferencialDet> lista = q.getResultList();
		return lista;

	}
	
	

	private EvalReferencial cargarVerEvaluacion() {
		Long idDatosEspecificos = null;
		if (idDatosEspecificosVer != null) {
			idDatosEspecificos = idDatosEspecificosVer;
			
		} else {
			idDatosEspecificos = tipoEvalObj.getIdDatosEspecificos();
			
		}

		Query q = em
				.createQuery("select evalReferencialCab from EvalReferencialCab evalReferencialCab where"
						+ " evalReferencialCab.evalReferencial.postulacion.idPostulacion = "
						+ idPostulacion
						+ " and matrizRefConfEnc.datosEspecificos.idDatosEspecificos = "
						+ idDatosEspecificos);

		List<EvalReferencialCab> lista = q.getResultList();
		if (lista.size() > 0) {
			evalReferencialSelected = em.find(EvalReferencial.class,
					lista.get(0).getEvalReferencial().getIdEvalReferencial());
			lEvalReferencialCabEditado = evalReferencialSelected
					.getEvalReferencialCabs();
			presente = (evalReferencialSelected.isPresente() ? AusentePresente.PRESENTE
					: AusentePresente.AUSENTE);
			totalPuntos = new Float(
					evalReferencialSelected.getPuntajeRealizado()).floatValue();
			totalAlcanzado = new Float(
					evalReferencialSelected.getPorcRealizado()).floatValue();
			observacion1 = evalReferencialSelected.getObservacion();

			String listaIn = "";
			/* Para obtener los puntajes */
			Map<String, Map<String, Float>> mapaCachePuntajes = new HashMap<String, Map<String, Float>>();
			String elKey = null;
			String elKey2 = null;
			for (EvalReferencialCab o : lista) {
				elKey = o.getMatrizRefConfEnc().getIdMatrizRefConfEnc()
						.toString();
				if (!mapaCachePuntajes.containsKey(elKey)) {
					mapaCachePuntajes.put(elKey, new HashMap<String, Float>());
				}
				for (EvalReferencialDet p : lEvalReferencialDet(o
						.getIdEvalReferencialCab())) {
					elKey2 = p.getMatrizRefConfDet().getIdMatrizRefConfDet()
							.toString();
					mapaCachePuntajes.get(elKey).put(elKey2,
							p.getPuntajeRealizado());
				}

				listaIn += ","
						+ o.getMatrizRefConfEnc().getIdMatrizRefConfEnc();
			}
			/**/
			/* Para obtener los elementos evaluados */
			listaIn = listaIn.replaceFirst(",", "");
			q = em.createQuery("select matrizRefConfEnc from MatrizRefConfEnc matrizRefConfEnc,MatrizRefConf matrizRefConf where "
					+ " matrizRefConfEnc.matrizRefConf = matrizRefConf  "
					+ " and matrizRefConf.concursoPuestoAgr.idConcursoPuestoAgr = "
					+ grupoPuestosController.getIdConcursoPuestoAgr()
					+ " and matrizRefConfEnc.activo = true "
					+ " and matrizRefConfEnc.matrizRefConf.activo = true "
					+ " and matrizRefConfEnc.idMatrizRefConfEnc IN("
					+ listaIn
					+ ")  and matrizRefConf.tipo ='GRUPO' "
					+ " order by matrizRefConfEnc.nroOrden asc ");

			lMatRefConfEnc = q.getResultList();
			/* Cargando los puntajes */
			for (MatrizRefConfEnc o : lMatRefConfEnc) {
				elKey = o.getIdMatrizRefConfEnc().toString();
				for (MatrizRefConfDet p : o.getMatrizRefConfDets()) {
					elKey2 = p.getIdMatrizRefConfDet().toString();
					if (mapaCachePuntajes.get(elKey) != null
							&& mapaCachePuntajes.get(elKey).get(elKey2) != null)
						p.setPuntaje(mapaCachePuntajes.get(elKey).get(elKey2)
								.floatValue());
				}
			}
			/* Cargar los evaluadores */
			cargarEvaluadores();
		} 

		cargarCabMatrizConfigurada();
		if (evalReferencialSelected == null)
			sumarPuntos(true);
		else {
			sumarPuntos(false);
		}

		return evalReferencialSelected;
	}
	
	
	
	/**
	 * @return
	 */
	private EvalReferencial generarEvalReferencial() {
		Long idDatosEspecificos = null;
		if (idDatosEspecificosVer != null) {
			idDatosEspecificos = idDatosEspecificosVer;
			
		} else {
			idDatosEspecificos = tipoEvalObj.getIdDatosEspecificos();
			
		}
		Query q = em
				.createQuery("select evalReferencialCab from EvalReferencialCab evalReferencialCab where"
						+ " evalReferencialCab.evalReferencial.postulacion.idPostulacion = "
						+ idPostulacion
						+ " and matrizRefConfEnc.datosEspecificos.idDatosEspecificos = "
						+ idDatosEspecificos);

		List<EvalReferencialCab> lista = q.getResultList();
		
		
		if (lista.size() == 1) {
			evalReferencialSelected = em.find(EvalReferencial.class,
					lista.get(0).getEvalReferencial().getIdEvalReferencial());
			
			if(!evalReferencialSelected.isSiendoEvaluado()){
						
				evalReferencialSelected.setSiendoEvaluado(true);
				em.merge(evalReferencialSelected);
				em.flush();
				
			}
			
			lEvalReferencialCabEditado = evalReferencialSelected.getEvalReferencialCabs();
			
			presente = (evalReferencialSelected.isPresente() ? AusentePresente.PRESENTE
					: AusentePresente.AUSENTE);
			totalPuntos = new Float(
					evalReferencialSelected.getPuntajeRealizado()).floatValue();
			totalAlcanzado = new Float(
					evalReferencialSelected.getPorcRealizado()).floatValue();
			observacion1 = evalReferencialSelected.getObservacion();

			String listaIn = "";
			/* Para obtener los puntajes */
			Map<String, Map<String, Float>> mapaCachePuntajes = new HashMap<String, Map<String, Float>>();
			String elKey = null;
			String elKey2 = null;
			for (EvalReferencialCab o : lista) {
				elKey = o.getMatrizRefConfEnc().getIdMatrizRefConfEnc()
						.toString();
				if (!mapaCachePuntajes.containsKey(elKey)) {
					mapaCachePuntajes.put(elKey, new HashMap<String, Float>());
				}
				for (EvalReferencialDet p : lEvalReferencialDet(o
						.getIdEvalReferencialCab())) {
					elKey2 = p.getMatrizRefConfDet().getIdMatrizRefConfDet()
							.toString();
					mapaCachePuntajes.get(elKey).put(elKey2,
							p.getPuntajeRealizado());
				}

				listaIn += ","
						+ o.getMatrizRefConfEnc().getIdMatrizRefConfEnc();
			}
			/**/
			/* Para obtener los elementos evaluados */
			listaIn = listaIn.replaceFirst(",", "");
			q = em.createQuery("select matrizRefConfEnc from MatrizRefConfEnc matrizRefConfEnc,MatrizRefConf matrizRefConf where "
					+ " matrizRefConfEnc.matrizRefConf = matrizRefConf  "
					+ " and matrizRefConf.concursoPuestoAgr.idConcursoPuestoAgr = "
					+ grupoPuestosController.getIdConcursoPuestoAgr()
					+ " and matrizRefConfEnc.idMatrizRefConfEnc IN("
					+ listaIn
					+ ")  and matrizRefConf.tipo ='GRUPO' "
					+ " order by matrizRefConfEnc.nroOrden asc ");

			lMatRefConfEnc = q.getResultList();
			/* Cargando los puntajes */
			for (MatrizRefConfEnc o : lMatRefConfEnc) {
				elKey = o.getIdMatrizRefConfEnc().toString();
				for (MatrizRefConfDet p : o.getMatrizRefConfDets()) {
					elKey2 = p.getIdMatrizRefConfDet().toString();
					if (mapaCachePuntajes.get(elKey) != null
							&& mapaCachePuntajes.get(elKey).get(elKey2) != null)
						p.setPuntaje(mapaCachePuntajes.get(elKey).get(elKey2)
								.floatValue());
				}
			}
			/* Cargar los evaluadores */
			cargarEvaluadores();
		} else {
			
			evalReferencialSelected = exiteEvalReferencial(idPostulacion, idDatosEspecificos);
			
			if (evalReferencialSelected == null) {
				evalReferencialSelected = new EvalReferencial();
				evalReferencialSelected.setNroOrden(this.nroOrden);
				evalReferencialSelected.setPostulacion(em.find(Postulacion.class, this.idPostulacion));
				evalReferencialSelected.setPresente(false);
				evalReferencialSelected.setPuntajeRealizado(0);
				evalReferencialSelected.setPorcRealizado(0);
				evalReferencialSelected.setFechaEvaluacion(new Date());
				evalReferencialSelected.setActivo(true);
				evalReferencialSelected.setUsuAlta(this.usuarioLogueado.getCodigoUsuario());
				evalReferencialSelected.setFechaAlta(new Date());
				evalReferencialSelected.setSiendoEvaluado(true);

				evalReferencialSelected.setEvalReferencialTipoeval(this.traerEncabezado());
				em.persist(evalReferencialSelected);
				em.flush();
				
				
			}
			
			cargarEvaluarPuntaje();
			cargarComisionSeleccion();
			updateEmpTerce();
			updateIntegrantes();
			inicializarTotales();
			cargarCabMatrizConfigurada();
			
			
		}

		cargarCabMatrizConfigurada();
		if (evalReferencialSelected == null)
			sumarPuntos(true);
		else {
			sumarPuntos(false);
		}
		
	

		return evalReferencialSelected;
	}
	

	public String formatObligatorio(Boolean valor) {
		if (valor) {
			return "Sí";

		} else {
			return "No";
		}
	}

	private void cambiarEstadoPuestos(List<ConcursoPuestoDet> lista,
			EstadoDet estadoDet) {
		for (ConcursoPuestoDet concursoPuesto : lista) {
			// Actualizar el Puesto
			concursoPuesto.getPlantaCargoDet().setEstadoDet(estadoDet);
			concursoPuesto.getPlantaCargoDet().setUsuMod(
					usuarioLogueado.getCodigoUsuario());
			concursoPuesto.getPlantaCargoDet().setFechaMod(new Date());
			concursoPuesto.setPlantaCargoDet(em.merge(concursoPuesto
					.getPlantaCargoDet()));
			concursoPuesto.setEstadoDet(estadoDet);
			concursoPuesto.setFechaMod(new Date());
			concursoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(concursoPuesto);
		}
	}

	/**
	 * Detecta si hay evaluaciones obligatorias más allá de la evaluación actual
	 * 
	 * @return
	 */
	private Boolean hayEvalsObliAdelante() {
		textoModalSgteTarea = "";
		// Evaluacion actual
		MatrizRefConfEnc elFactor = em.find(MatrizRefConfEnc.class, idTipoEval);
		for (MatrizRefConfEnc o : lTipoEval) {
			if (o.getNroOrden().intValue() > elFactor.getNroOrden().intValue()) {
				if (o.getObligatorioSN()) {
					textoModalSgteTarea = "Hay una Evaluación Obligatoria pendiente más adelante. No se puede pasar de actividad.";
					return true;
				} else {
					textoModalSgteTarea += "<li type='disc'>"
							+ o.getDatosEspecificos().getDescripcion()
							+ "</li>";
				}
			}
		}
		if (!textoModalSgteTarea.trim().isEmpty()) {
			textoModalSgteTarea = "<p>Hay evaluación/es No obligatoria/s pendiente/s. </p>"
					+ " <ul>"
					+ textoModalSgteTarea
					+ "</ul> <p>¿Desea seguir evaluando? </p>";
		} else {
			textoModalSgteTarea = "La evaluación: "
					+ tipoEvalObj.getDescripcion()
					+ " (NO obligatoria) no fue aplicada a ningun postulante. ¿Desea seguir evaluando?";
		}
		return false;
	}

	private MatrizRefConfEnc calcIdMatrizRefConfEncFromIdDatosEspecificos(
			Long idDatosEspecificos) {
		for (MatrizRefConfEnc o : lTipoEval) {
			if (o.getDatosEspecificos().getIdDatosEspecificos().intValue() == idDatosEspecificos
					.intValue()) {
				return o;
			}
		}
		// No se debería dar.
		return null;
	}

	/**
	 * Calcula la posición del cursor de evaluaciones teniendo en cuenta las
	 * evalauciones ya realizadas
	 */
	private void actualizarTipoEvalPasados() {

		Query q = em
				.createQuery("Select EvalReferencialTipoeval  from EvalReferencialTipoeval EvalReferencialTipoeval "
						+ " where EvalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr()
						+ " order by idEvalReferencialTipoeval asc");
		List<EvalReferencialTipoeval> lista = q.getResultList();
		Boolean encontrado = false;
		Integer maxNroOrden = Integer.MIN_VALUE;
		List<String> lTipoEvalPasadosAux = new ArrayList<String>();
		for (MatrizRefConfEnc o : lTipoEval) {
			for (EvalReferencialTipoeval p : lista) {
				if (o.getDatosEspecificos().getIdDatosEspecificos().intValue() == p
						.getDatosEspecificos().getIdDatosEspecificos()
						.intValue()) {
					encontrado = true;
					// Se calcula el mayor numero de orden evaluado
					if (maxNroOrden.intValue() < o.getNroOrden().intValue()) {
						maxNroOrden = o.getNroOrden();
					}
					break;
				} else {
					encontrado = false;
				}
			}
			if (!encontrado) {

				String elKey = o.getIdMatrizRefConfEnc().toString() + "#"
						+ o.getNroOrden();
				if (!lTipoEvalPasadosAux.contains(elKey)) {
					lTipoEvalPasadosAux.add(elKey);
				}
			}

		}
		/************* Limpieza ************/
		Iterator<String> iter = lTipoEvalPasadosAux.iterator();
		while (iter.hasNext()) {
			String elem = iter.next();
			String compos[] = elem.split("#");
			if (Integer.parseInt(compos[1]) >= maxNroOrden.intValue()) {
				iter.remove();
			}
		}
		for (String o : lTipoEvalPasadosAux) {
			String compos[] = o.split("#");
			if (!lTipoEvalPasados.contains(compos[0])) {
				lTipoEvalPasados.add(compos[0]);
			}
		}
		/***********/
	}

	
	public Boolean precondRendered() {

		// Verificar si ya esta todo evaluado
		List<EvalReferencialTipoeval> lEvaluacioneHechas;
		lEvaluacioneHechas = calEvlasHechas(grupoPuestosController
				.getIdConcursoPuestoAgr());
		// MatrizRefConfEnc ultimaEvalARealizar = ultEvalRealizar();
		if (lEvaluacioneHechas.size() == 0) {
//			statusMessages
//					.add(Severity.ERROR,
//							"Deben estar cerradas todas las evaluaciones OBLIGATORIAS antes de poder pasar a la SIGUIENTE TAREA");
			return false;
		}
		if (terminoEvaluacion()) {
			return true;
		}

		MatrizRefConfEnc elFactor = em.find(MatrizRefConfEnc.class, idTipoEval);
		Boolean hayEvaluados = hayEvaluados(elFactor);
		if (continuarSgteTarea && !hayEvaluados) {
			return true;
		}
		/*
		 * Si no es obligatoria la evaluacion y si todavia no se ha evaluado a
		 * nadie se debe mostrar la ventana de modal
		 */

		if (!hayEvaluados && !elFactor.getObligatorioSN()) {
			// verificar si hay evaluaciones obligatorias más adelante
			hayEvalObliAdelante = hayEvalsObliAdelante();
			mostrarModal = true;
			return false;
		}

		if (!sePuedeCerrarEval()) {
//			statusMessages.add(Severity.ERROR,
//					"No se puede continuar, hay evaluaciones pendientes: "
//							+ tipoEvalObj.getDescripcion());
			return false;
		} else {
//			statusMessages
//					.add(Severity.ERROR,
//							"Todos los postulantes han sido evaluados. Sin embargo, debe cerrar la evaluación: "
//									+ tipoEvalObj.getDescripcion()
//									+ " para continuar.");
			return false;
		}

	}	
	
	public Boolean precondNextTask() {

		// Verificar si ya esta todo evaluado
		List<EvalReferencialTipoeval> lEvaluacioneHechas;
		lEvaluacioneHechas = calEvlasHechas(grupoPuestosController
				.getIdConcursoPuestoAgr());
		// MatrizRefConfEnc ultimaEvalARealizar = ultEvalRealizar();
		
		/*if(tareaCerrada())
		{
			statusMessages.add(Severity.INFO,
					"El Grupo ya se encuentra en la Siguiente Actividad. Verifique.");
			this.continuarSgteTarea = false;
			return false;
		}*/
		
		
		if (lEvaluacioneHechas.size() == 0) {
			statusMessages
					.add(Severity.ERROR,
							"Deben estar cerradas todas las evaluaciones OBLIGATORIAS antes de poder pasar a la SIGUIENTE TAREA");
			return false;
		}
		if (terminoEvaluacion()) {
			return true;
		}

		MatrizRefConfEnc elFactor = em.find(MatrizRefConfEnc.class, idTipoEval);
		Boolean hayEvaluados = hayEvaluados(elFactor);
		if (continuarSgteTarea && !hayEvaluados) {
			return true;
		}
		/*
		 * Si no es obligatoria la evaluacion y si todavia no se ha evaluado a
		 * nadie se debe mostrar la ventana de modal
		 */

		if (!hayEvaluados && !elFactor.getObligatorioSN()) {
			// verificar si hay evaluaciones obligatorias más adelante
			hayEvalObliAdelante = hayEvalsObliAdelante();
			mostrarModal = true;
			return false;
		}

		if (!sePuedeCerrarEval()) {
			statusMessages.add(Severity.ERROR,
					"No se puede continuar, hay evaluaciones pendientes: "
							+ tipoEvalObj.getDescripcion());
			return false;
		} else {
			statusMessages
					.add(Severity.ERROR,
							"Todos los postulantes han sido evaluados. Sin embargo, debe cerrar la evaluación: "
									+ tipoEvalObj.getDescripcion()
									+ " para continuar.");
			return false;
		}

	}
	
	public boolean tareaCerrada(){
		
		Referencias ref = new Referencias();
		ref = referenciasUtilFormController.getReferencia(
				"ESTADOS_GRUPO", "LISTA LARGA");
		
		ConcursoPuestoAgr aux = em.find(ConcursoPuestoAgr.class, this.idConcursoPuestoAgr);
		
		em.refresh(aux);
		
		if (aux.getEstado().equals(ref.getValorNum()))
			return false;
		else
			return true;
		
	}

	/**
	 * Detecta repetidos en EvalReferencialPostulante utilizando el idGrupo y el
	 * idPostulacion
	 * 
	 * @param idGrupo
	 * @param idPostulacion
	 * @return
	 */
	private Boolean detectarRepetidoAprobados(Long idGrupo, Long idPostulacion) {
		Query q = em
				.createQuery("select EvalReferencialPostulante from EvalReferencialPostulante EvalReferencialPostulante "
						+ " where EvalReferencialPostulante.postulacion.idPostulacion =:idPostulacion "
						+ " and EvalReferencialPostulante.concursoPuestoAgr.idConcursoPuestoAgr =:idGrupo ");
		q.setParameter("idGrupo", idGrupo);
		q.setParameter("idPostulacion", idPostulacion);
		List<EvalReferencial> lista = q.getResultList();
		if (lista.size() > 0) {
			return true;
		}
		return false;
	}

	private List<EvalReferencial> lERultimaEval(Long idER) {
		Query q = em
				.createQuery("select EvalReferencial from EvalReferencial EvalReferencial "
						+ " where EvalReferencial.evalReferencialTipoeval.idEvalReferencialTipoeval = :idER ");
		q.setParameter("idER", idER);

		return q.getResultList();
	}

	/**
	 * Si guardar es true, persiste los datos. Siempre devuelve la cantidad de
	 * postulantes que aprobaron y solo guarda los postulantes que han aprobado
	 * 
	 * @param guardar
	 * @return
	 */
	private Integer getCantidadPostulantesAprobados(Boolean guardar) {
		/*
		 * Hacer los cambios correspondientes a la última evaluación.
		 */
		// Traer la ultima evaluacion cerrada
		List<EvalReferencialTipoeval> lEvaluacioneHechas;
		lEvaluacioneHechas = calEvlasHechas(grupoPuestosController
				.getIdConcursoPuestoAgr());
		int cantAprobados = 0;
		
			
		
			EvalReferencialTipoeval ultEvalRealizada = lEvaluacioneHechas.get(0);
			
			
//SOLO ESTABA GUARDANDO EL PUNTAJE DE LA ULTIMA EVALUACION, ESO SE CAMBIO Y AHORA GUARDA EL PROMEDIO DE TODAS LAS EVALUACIONES EN LAS QUE HAYA PARTICIPADO EL POSTULANTE
			// EDCM
			if (idPorcMinAplicar.equalsIgnoreCase(PORC_MIN_POR_EVAL)) { 
//				for (EvalReferencial o : lERultimaEval(ultEvalRealizada.getIdEvalReferencialTipoeval())) {
//					if (o.getAprobado()	&& refreshPostulacion(o.getPostulacion().getIdPostulacion()).isActivo()) {
//						cantAprobados++;
//						if (guardar) {
//							if (!detectarRepetidoAprobados(
//									grupoPuestosController.getIdConcursoPuestoAgr(),
//									o.getPostulacion().getIdPostulacion())) {
//								EvalReferencialPostulante erp = new EvalReferencialPostulante();
//								erp.setActivo(true);
//								erp.setFechaAlta(new Date());
//								erp.setUsuAlta(usuarioLogueado.getCodigoUsuario());
//								erp.setPuntajeRealizado(o.getPuntajeRealizado());
//								erp.setPostulacion(o.getPostulacion());
//								erp.setConcursoPuestoAgr(new ConcursoPuestoAgr());
//								erp.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
//										grupoPuestosController
//												.getIdConcursoPuestoAgr());
//								erp.setFechaEvaluacion(new Date());
//								em.persist(erp);
//							}
//						}
//					}
//				}
				
				
				//NUEVO BLOQUE DE CODIGO DONDE SE CALCULA EL PROMEDIO DE LAS EVALUACIONES - EDCM
				// Calcular el promedio que ha obtenido cada postulante en todas las
				// evaluaciones en las que ha participado
				Map<String, Float> diccPostulantePromedio = calcularPromedios(evalTipoEvalPorGrupo(grupoPuestosController
						.getIdConcursoPuestoAgr()));
				// Float puntajeMaximo =
				// diccPostulantePromedio.get("PUNTAJE_MAXIMO");
				// diccPostulantePromedio.remove("PUNTAJE_MAXIMO");
				for (String o : diccPostulantePromedio.keySet()) {
					// Verificar si aprueba
					//if (diccPostulantePromedio.get(o).floatValue() >= porMinFinEvaluacion().floatValue() && refreshPostulacion(new Long(o)).isActivo()) {
					if (refreshPostulacion(new Long(o)).isActivo()) {
						cantAprobados++;
						if (guardar) {
							if (!detectarRepetidoAprobados(
									grupoPuestosController.getIdConcursoPuestoAgr(),
									new Long(o))) {
								EvalReferencialPostulante erp = new EvalReferencialPostulante();
								erp.setActivo(true);
								erp.setFechaAlta(new Date());
								erp.setUsuAlta(usuarioLogueado.getCodigoUsuario());
								erp.setPuntajeRealizado(diccPostulantePromedio.get(
										o).floatValue());
								erp.setPostulacion(new Postulacion());
								erp.getPostulacion().setIdPostulacion(new Long(o));
								erp.setConcursoPuestoAgr(new ConcursoPuestoAgr());
								erp.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
										grupoPuestosController
												.getIdConcursoPuestoAgr());
								erp.setFechaEvaluacion(new Date());
								em.persist(erp);
							}
						}
					}
				}
	
			} else {
				// Calcular el promedio que ha obtenido cada postulante en todas las
				// evaluaciones en las que ha participado
				Map<String, Float> diccPostulantePromedio = calcularPromedios(evalTipoEvalPorGrupo(grupoPuestosController
						.getIdConcursoPuestoAgr()));
				// Float puntajeMaximo =
				// diccPostulantePromedio.get("PUNTAJE_MAXIMO");
				// diccPostulantePromedio.remove("PUNTAJE_MAXIMO");
				for (String o : diccPostulantePromedio.keySet()) {
					// Verificar si aprueba
					//if (diccPostulantePromedio.get(o).floatValue() >= porMinFinEvaluacion().floatValue() && refreshPostulacion(new Long(o)).isActivo()) {
					if (refreshPostulacion(new Long(o)).isActivo()) {
						cantAprobados++;
						if (guardar) {
							if (!detectarRepetidoAprobados(
									grupoPuestosController.getIdConcursoPuestoAgr(),
									new Long(o))) {
								EvalReferencialPostulante erp = new EvalReferencialPostulante();
								erp.setActivo(true);
								erp.setFechaAlta(new Date());
								erp.setUsuAlta(usuarioLogueado.getCodigoUsuario());
								erp.setPuntajeRealizado(diccPostulantePromedio.get(
										o).floatValue());
								erp.setPostulacion(new Postulacion());
								erp.getPostulacion().setIdPostulacion(new Long(o));
								erp.setConcursoPuestoAgr(new ConcursoPuestoAgr());
								erp.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
										grupoPuestosController
												.getIdConcursoPuestoAgr());
								erp.setFechaEvaluacion(new Date());
								em.persist(erp);
							}
						}
					}
				}
			}
			if (guardar)
				em.flush();
		
		

		return cantAprobados;

	}

	public String nextTask(String sgteActividad) throws IllegalStateException,
			SecurityException, SystemException {
		
		
		try {
			//rveron, 20141022. Se agrega lock para y se movió segmento de código para que quede dentro del try-catch
			lock.lock();
			
			
			if(tareaCerrada())
			{
				statusMessages.add(Severity.INFO,
						"El Grupo ya se encuentra en la Siguiente Actividad. Verifique.");
				//this.continuarSgteTarea = false;
				lock.unlock();
				return "nextTask";
			}
			
			
			// Controla que todos los postulantes hayan sido evaluados
			if (!precondNextTask()) {
				lock.unlock();
				return null;
			}

			
			// Controles particulares
			EvaluarDocPostulantesFormController evaluarDocPostulantesFormController = (EvaluarDocPostulantesFormController) 
																		Component.getInstance(EvaluarDocPostulantesFormController.class,true);
			
			evaluarDocPostulantesFormController.setConcursoPuestoAgr(grupoPuestosController.getConcursoPuestoAgr());
			
			Integer cantidadAprob = getCantidadPostulantesAprobados(true);
			
			if (getDeclararDesierto(evaluarDocPostulantesFormController)) {
				// Realizar los cambios propios a la última evaluación

				// El estado del Grupo:
				evaluarDocPostulantesFormController.cambiarEstadoGrupo(grupoPuestosController.getConcursoPuestoAgr(),
																				"ESTADOS_GRUPO", "EVALUACION REFERENCIAL", true);
				// El estado del Puesto:
				cambiarEstadoPuestos(evaluarDocPostulantesFormController.traerGrupoPuestoDet(grupoPuestosController.getIdConcursoPuestoAgr()),
										evaluarDocPostulantesFormController.obtenerEstadoPuesto("EVALUACION REFERENCIAL","CONCURSO"));
				// Se actualiza la tabla SEL_CONCURSO_PUESTO_AGR:
				grupoPuestosController.getConcursoPuestoAgr().setFechaEvalRefHasta(new Date());
				grupoPuestosController.setConcursoPuestoAgr(em.merge(grupoPuestosController.getConcursoPuestoAgr()));
				grupoPuestosController.getConcursoPuestoAgr().setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "EVALUACION REFERENCIAL"));
				publicar();// MODIFICADO RV
				// Se pasa a la siguiente tarea
				jbpmUtilFormController.setConcursoPuestoAgr(grupoPuestosController.getConcursoPuestoAgr());

				if (sgteActividad.equalsIgnoreCase("ELABORAR_PUBLICACION_LISTA_CORTA")) {
					jbpmUtilFormController.setActividadActual(ActividadEnum.REALIZAR_EVALUACIONES);
					jbpmUtilFormController.setActividadSiguiente(ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA);
				} else if (sgteActividad.equalsIgnoreCase("ELABORAR_PUBLICACION_LISTA_CORTA_S")) {
					jbpmUtilFormController.setActividadActual(ActividadEnum.CSI_REALIZAR_EVALUACIONES);
					jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CSI_ELABORAR_PUBLICACION_LISTA_CORTA);
				} else if (sgteActividad.equalsIgnoreCase("ELABORAR_PUBLICACION_LISTA_CORTA_I")) {
					jbpmUtilFormController.setActividadActual(ActividadEnum.CIO_REALIZAR_EVALUACIONES);
					jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CIO_ELABORAR_PUBLICACION_LISTA_CORTA);
				}
				jbpmUtilFormController.setTransition("next");
				if (jbpmUtilFormController.nextTask()) {
					em.flush();
				}
				lock.unlock();
				return "nextTask";
			} else {
				lock.unlock();
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
			lock.unlock();
			if (!Transaction.instance().isMarkedRollback()) {
				Transaction.instance().rollback();
			}
		}
		lock.unlock();
		return "";
	}

	public String obtenerCiudad(Long idCiudad) {
		Query q = em
				.createQuery("select Ciudad from Ciudad Ciudad where Ciudad.idCiudad =  "
						+ idCiudad);
		List<Ciudad> lista = q.getResultList();
		if (lista.size() == 1) {
			return lista.get(0).getDescripcion();
		} else {
			return "-";
		}
	}

	public String formatAprobado(EvalReferencial evalRef) {
		if (evalRef == null) {
			return "";
		}else{
			if(!tieneDetalles(evalRef.getIdEvalReferencial()))
				return "NO EVALUADO";
		}
		if (idPorcMinAplicar.equals(PORC_MIN_POR_EVAL)) {
			if ((evalRef.getAprobado() != null) && evalRef.getAprobado()) {
				return "APROBADO";

			} else if ((evalRef.getAprobado() == null)
					|| !evalRef.getAprobado()) {
				return "NO APROBADO";
			}
		}

		if (idPorcMinAplicar.equals(PORC_MIN_AL_FINALIZAR_EVALS)) {
			return "EVALUADO";
		} else
			return "-";

	}
	//Debe cargar por lo menos un puntaje para el Factor:
	private boolean tieneDetalles(Long id_eval_referencial){
		boolean retorno = true;
		String sql = "select * from seleccion.eval_referencial_cab where id_eval_referencial = "
				+ id_eval_referencial;
		List <EvalReferencialCab> list =  em.createNativeQuery(sql, EvalReferencialCab.class).getResultList();
		
		if(list.size() == 0)
			retorno = false;
		return retorno;
	}

	private void cargarListaConvoDet() {//MODIFICADO RV
		
			Query q = em
					.createQuery("select EvalReferencialConvocatoria from EvalReferencialConvocatoria EvalReferencialConvocatoria "
							+ " where EvalReferencialConvocatoria.concursoPuestoAgr.idConcursoPuestoAgr =  "
							+ idConcursoPuestoAgr);
			lEvalRefConvo = q.getResultList();
			rowConvoSele = 0;
		
	}

	private EvalReferencialTipoeval traerEvalRefTipoEval(Long idDatosEspe) {
		if (idDatosEspe == null) {
			return null;
		} else {
			Query q = em
					.createQuery("select EvalReferencialTipoeval from EvalReferencialTipoeval EvalReferencialTipoeval "
							+ " where EvalReferencialTipoeval.datosEspecificos.idDatosEspecificos =  "
							+ idDatosEspe
							+ " and  EvalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
							+ grupoPuestosController.getIdConcursoPuestoAgr());
			List<EvalReferencialTipoeval> lista = q.getResultList();
			if (lista.size() == 1) {
				return lista.get(0);
			} else {
				return null;
			}
		}
	}
	public void initDatosConvo() {
		validarOee(grupoPuestosController.getConcursoPuestoAgr().getConcurso());
		
		evalReferencialTipoeval = traerEvalRefTipoEval(idTipoEvalCerradas);
		cargarListaConvoDet();
		updateDepartamento();
		updateCiudad();
		rowConvoSele = -1;
	}

	private Boolean precondSaveConvo() {
		
//		idTipoEvalCerradas == null ||
		if ( fechaConvo == null
				|| horaDesde == null || idCiudad == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU88_todosSonObligatorios"));
			return false;
		}
		Date horaDesdeDate = null;
		Date horaHastaDate = null;
		try {
			sdfHORA.setLenient(false);
			horaDesdeDate = sdfHORA.parse(horaDesde);
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, "Hora Desde no válida");
			return false;
		}
		try {
			sdfHORA.setLenient(false);
			if(horaHasta != null && !horaHasta.trim().equals(""))
				horaHastaDate = sdfHORA.parse(horaHasta);
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, "Hora Hasta no válida");
			return false;
		}
		if (horaHastaDate != null && horaDesdeDate.getTime() > horaHastaDate.getTime()) {
			statusMessages.add(Severity.ERROR,
					"La Hora Desde no puede ser mayor a la Hora Hasta");
			return false;
		}
		if (lugar == null || (lugar != null && lugar.trim().isEmpty())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU88_todosSonObligatorios"));
			return false;
		}
		if (lugar.length() > 150) {
			statusMessages
					.add(Severity.ERROR,
							"Superada la cantidad máxima de caracteres (150) para el campo Lugar");
			return false;
		}

		if (direccion == null
				|| (direccion != null && direccion.trim().isEmpty())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU88_todosSonObligatorios"));
			return false;
		}
		if (direccion.length() > 150) {
			statusMessages
					.add(Severity.ERROR,
							"Superada la cantidad máxima de caracteres (150) para el campo Dirección");
			return false;
		}
//		if (observacion3 == null
//				|| (observacion3 != null && observacion3.trim().isEmpty())) {
//			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
//					.getString("CU88_todosSonObligatorios"));
//			return false;
//		}
		if (observacion3.length() > 500) {
			statusMessages
					.add(Severity.ERROR,
							"Superada la cantidad máxima de caracteres (500) para el campo Observación");
			return false;
		}
		return true;
	}

	private void enviarMail() {
		if (usuarioPortalFormController == null) {
			usuarioPortalFormController = (UsuarioPortalFormController) Component
					.getInstance(UsuarioPortalFormController.class, true);
		}
		ConfiguracionUoCab uo = em.find(ConfiguracionUoCab.class,
				grupoPuestosController.getConcursoPuestoAgr().getConcurso()
						.getConfiguracionUoCab().getIdConfiguracionUo());
		
		String asunto = "Resultado de "
				+ tipoEvalDesc
				+ " "
				+ grupoPuestosController.getConcursoPuestoAgr().getConcurso()
						.getNombre()
				+ " "

				+ grupoPuestosController.getConcursoPuestoAgr()
						.getDescripcionGrupo();
		String texto = "<p align=\"justify\"> <b> Estimado(a),    ";
		
		
		for (EvalAbiertas o : lEvalsAbiertas) {
			try {
				texto = "<b><p align=\"justify\">Le comunicamos  que "
						+ (o.getEvalReferencial().getAprobado() ? "" : "no")
						+ " ha aprobado la Evaluaci&oacute;n "
						+ tipoEvalDesc
						+ " para el Puesto "
						+ grupoPuestosController.getConcursoPuestoAgr()
								.getDescripcionGrupo()
						+ " del Concurso "
						+ grupoPuestosController.getConcursoPuestoAgr()
								.getConcurso().getNombre() + ".</p></b>";
				texto += "<b><p align=\"justify\">Resaltamos la siguiente informaci&oacute;n al respecto:</p></b>";
				texto += "<b><p align=\"justify\"> "
						+ o.getEvalReferencial().getObservacion() + "</p></b>";
				texto += "<b><p align=\"justify\"> Por favor acceder al <a href='http://www.paraguayconcursa.gov.py/'>Portal</a>  para mayor informaci&oacute;n al respecto.</p></b> ";
				texto += "<b><p align=\"justify\"> Atentos saludos,</p></b> ";
				texto += "<b><p align=\"justify\"> "
						+ uo.getDenominacionUnidad() + "</p></b> ";
				usuarioPortalFormController
						.enviarMails(o.getPostulacion().getPersonaPostulante()
								.getEMail(), texto, asunto, null);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	

	private void publicacionPortal(String texto, Long idConcurso,
			Long idConcursoGrupoPuestoAgr) {
		PublicacionPortal entity = new PublicacionPortal();
		entity.setActivo(true);
		entity.setFechaAlta(new Date());
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		entity.setTexto(texto);
		entity.setConcurso(new Concurso());
		entity.getConcurso().setIdConcurso(idConcurso);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
				idConcursoGrupoPuestoAgr);
		em.persist(entity);
		em.flush();
	}

	public String publicar() {
		try {
			
			String texto = "";
			String textoMail = "";
			
			if (lEvalRefConvo != null && lEvalRefConvo.size() > 0) {

				for (EvalReferencialConvocatoria o : lEvalRefConvo) {
					if(o.getFechaPublicacion() == null){	
						if (o.getIdEvalReferencialConvocatoria() == null) {
							o.setFechaPublicacion(new Date());
							o.setConcursoPuestoAgr(grupoPuestosController.getConcursoPuestoAgr());
							em.persist(o);
							texto = genTextoPublicacion(o);
							
							publicacionPortal(texto,
									grupoPuestosController.getConcursoPuestoAgr()
											.getConcurso().getIdConcurso(),
									grupoPuestosController.getIdConcursoPuestoAgr());
							
							textoMail += genTextoMail(o);
							String asuntoMail = "Publicaciones del Portal";
							enviarMailsDeConvocatoria (asuntoMail,textoMail);
							
							
						} else {//Verificar si realmente esta siendo utilizado la funcionalidad de Edicion
							o.setFechaMod(new Date());
							o.setUsuMod(usuarioLogueado.getCodigoUsuario());
							o = em.merge(o);
						}
						
						
											
					}
				}
				
				
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
			}
			
			
			limpiarVariables();
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return "FAIL";
		}
		return "OK";
	}
	
	
	private String genTextoMail(EvalReferencialConvocatoria o){
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		
		SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
		
		String texto = "<p align=\"justify\"> Estimado/a Señor/a "  +  "</p>";
				
				texto += "<p align=\"justify\"> Le comunicamos que como postulante para el cargo de  "+grupoPuestosController.getConcursoPuestoAgr().getDescripcionGrupo()  +
						" del Concurso convocado por el/la "+grupoPuestosController.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getDenominacionUnidad()+ "</p>";
		
				texto +="<p align=\"justify\"> Deberá presentarse para las evaluaciones establecidas de acuerdo al siguiente cronograma: "  +  "</p>";
				
				texto += "<p align=\"justify\"> Institución: "+grupoPuestosController.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getDenominacionUnidad() +  "</p>" ;
				
				texto +="<p align=\"justify\"> Cargo: "+grupoPuestosController.getConcursoPuestoAgr().getDescripcionGrupo()  +  "</p>";
				texto += "<p align=\"justify\"> Convocatoria para: "+ o.getMotivo() +  "</p>" ;
				
				texto += "<p align=\"justify\"> Fecha:"+ sdfFecha.format(o.getFechaConvocatoria())   + "</p> ";
				texto += "<p align=\"justify\"> Hora : "+sdfHora.format(o.getHoraDesde());
				
				if(o.getHoraHasta() != null )
					texto += "Hs. A "+ sdfHora.format(o.getHoraHasta())  + " Hs.</p></b> ";
				else
					texto += "Hs.</p></b> ";
				
				texto += "<p align=\"justify\"> Lugar : "+ o.getLugar() +  "</p> " ;
				texto += "<p align=\"justify\"> Dirección : "+ o.getDireccion() +  "</p> ";
				if(o.getIdCiudad() != null){
					Ciudad ciudad = em.find(Ciudad.class, o.getIdCiudad());
					Departamento depto = em.find(Departamento.class, ciudad.getDepartamento().getIdDepartamento());
					texto += "<p align=\"justify\"> Ciudad : "+ciudad.getDescripcion()  +" - " +depto.getDescripcion() +"</p>";
				}
				if(o.getObservacion() != null && !o.getObservacion().trim().equals(""))
					texto += "<p align=\"justify\"> Observación : "+ o.getObservacion() + "</p>" ;
		
		
		return texto;
	}
	
	
	public void enviarMailsDeConvocatoria(String asunto,String texto) {
		if (usuarioPortalFormController == null) {
			usuarioPortalFormController = (UsuarioPortalFormController) Component
					.getInstance(UsuarioPortalFormController.class, true);
		}
		ConfiguracionUoCab uo = em.find(ConfiguracionUoCab.class,
				grupoPuestosController.getConcursoPuestoAgr().getConcurso()
						.getConfiguracionUoCab().getIdConfiguracionUo());
				
		List<EvalAbiertas> lista = lEvalsAbiertas;
				
		
		for (EvalAbiertas o : lista) {
			try {
		
				PersonaPostulante postulante = em.find(PersonaPostulante.class , o.getPostulacion().getPersonaPostulante().getIdPersonaPostulante());
				
				usuarioPortalFormController.enviarMails(postulante.getEMail(), texto, asunto, null);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		


	private String genTextoPublicacion(EvalReferencialConvocatoria convocatoria) {
		String hr = "<hr>";
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdfFecha.format(new Date()).toString();
		String h1O = "<h1>";
		String h1C = "</h1>";
		String br = "</br>";
		String spanO = "<span>";
		String spanC = "</span>";
		SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
		String texto;
		texto = hr + fechaPublicacion
				+ h1O
//				+ "Convocatoria a "
				
//				+ convocatoria.getEvalReferencialTipoeval()
//						.getDatosEspecificos().getDescripcion()
						
						+ h1C;
		
		texto = texto + "<p align=\"center\">" + h1O+ convocatoria.getMotivo() + h1C + "</p>";
		if(convocatoria.getObservacion() != null)
			texto = texto + spanO + "Observación: " + spanC	+ convocatoria.getObservacion() + br;
		
		texto = texto + spanO + "Fecha: " + spanC
				+ sdfFecha.format(convocatoria.getFechaConvocatoria()) + br;
		texto = texto + spanO + "Hora: " + spanC
				+ sdfHora.format(convocatoria.getHoraDesde());
		if(convocatoria.getHoraHasta() != null)
			texto = texto + " a "	+ sdfHora.format(convocatoria.getHoraHasta()) + br;
		texto = texto + spanO + "Lugar: " + spanC + convocatoria.getLugar()
				+ br;
		if (convocatoria.getIdCiudad() != null) {
			Ciudad ciudad = em.find(Ciudad.class, convocatoria.getIdCiudad());
			Departamento depto = em.find(Departamento.class, ciudad.getDepartamento().getIdDepartamento());
			texto = texto + spanO + "Ciudad: " + spanC	+ ciudad.getDescripcion() + "-" +depto.getDescripcion() + br;
		} else {
			texto = texto + spanO + "Ciudad: " + spanC + "-" + br;
		}
		texto = texto + spanO + "Dirección: " + spanC	+ convocatoria.getDireccion() + br;

		return texto;
	}
	
	
	private String genTextoPublicacionAdmitidosXEvaluacion(EvalReferencialTipoeval tipoeval) {
		String texto = "";
		if(tipoeval != null) {
			String hr = "<hr>";
			SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
			String fechaPublicacion = sdfFecha.format(new Date()).toString();
			String h1O = "<h1>";
			String h1C = "</h1>";
			String h2O = "<h2>";
			String h2C = "</h2>";
			String spanO = "<span>";
			String spanC = "</span>";
			String br = "</br>";
			
			
			texto = texto
					+ hr
					+ fechaPublicacion + br
					+ spanO
					+ "Puede descargar aquí: "
					+ spanC
					+ br
					+ spanO
					+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=reporteEvaluacionesXEtapa&#38;idConcursoPuestoAgr="
					+ tipoeval.getConcursoPuestoAgr().getIdConcursoPuestoAgr()
					+"&idDatosEspecificos="+tipoeval.getDatosEspecificos().getIdDatosEspecificos()
					+ "'>Evaluaciones Detalladas - "+tipoeval.getDatosEspecificos().getDescripcion()+"</a>"
					+ spanC
					+ br
					+ spanO
					+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=ListaAdmitidosXEvaluacion&#38;idConcursoPuestoAgr="
					+ tipoeval.getConcursoPuestoAgr().getIdConcursoPuestoAgr()
					+"&idDatosEspecificos="+tipoeval.getDatosEspecificos().getIdDatosEspecificos()
					+ "'>Lista de Admitidos </a>";
					texto = texto + spanC;
					
			
					
		}
		return texto;
	}
	
	private String genTextoPublicacionNoAprobadosXEvaluacion(EvalReferencialTipoeval tipoeval) {
		String texto = new String();
		String h1O = "<h1>";
		String h1C = "</h1>";
		String h2O = "<h2>";
		String h2C = "</h2>";
		String spanO = "<span>";
		String spanC = "</span>";
		String br = "</br>";
		
		
		texto = texto
				+ br
				+ spanO
				+ "Puede descargar aquí la: "
				+ spanC
				+ br
				+ spanO
				+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=ListaNoAprobadosXEvaluacion&#38;idConcursoPuestoAgr="
				+ tipoeval.getConcursoPuestoAgr().getIdConcursoPuestoAgr()
				+"&idDatosEspecificos="+tipoeval.getDatosEspecificos().getIdDatosEspecificos()
				+ "'>Lista No Aprobados - "+tipoeval.getDatosEspecificos().getDescripcion()+"</a>";
				texto = texto + spanC;

		return texto;
	}
	
	
	public void imprimirListaAdmitidosXEvaluacion() {

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		
		Concurso concurso = concursoPuestoAgr.getConcurso();
		String codConcurso = "";
		
		if (concurso != null) {
			codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null) {
				codConcurso = codConcurso + " DE "
						+ configuracionUoCab.getDescripcionCorta();
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			}
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("idConcursoPuestoAgr",this.idConcursoPuestoAgr);
		param.put("idDatosEspecificos",this.idDatosEspecificos);
		param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
		param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
		param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());
		
		JasperReportUtils.respondPDF("ListaAdmitidosXEvaluacion",	param, false, conn,usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}
	
	public void imprimirReporteEvaluacionesXEtapa() {

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		
		Concurso concurso = concursoPuestoAgr.getConcurso();
		String codConcurso = "";
		
		if (concurso != null) {
			codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null) {
				codConcurso = codConcurso + " DE "
						+ configuracionUoCab.getDescripcionCorta();
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			}
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("concurso_puesto_agr",this.idConcursoPuestoAgr);
		param.put("desde",this.idDatosEspecificos);
		param.put("conNumero",true);
//		param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
//		param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
//		param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());
		
		
		JasperReportUtils.respondPDF("reporteEvaluacionesDetalladaXEtapa",	param, false, conn,usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}
	
	/**
	 * Busca el nivel correspondiente a la entidad
	 * 
	 * @param nenCodigo
	 * @return
	 */
	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {
		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}
	
	public void imprimirListaNoAprobadosXEvaluacion() {

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		
		Concurso concurso = concursoPuestoAgr.getConcurso();
		String codConcurso = "";
		
		if (concurso != null) {
			codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null) {
				codConcurso = codConcurso + " DE "
						+ configuracionUoCab.getDescripcionCorta();
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			}
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("idConcursoPuestoAgr",this.idConcursoPuestoAgr);
		param.put("idDatosEspecificos",this.idDatosEspecificos);
		param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
		param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
		param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());
		
		JasperReportUtils.respondPDF("ListaNoAdmitidosXEvaluacion",	param, false, conn,usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}
	
	
	private List<EvalAbiertas> filtro1(List<EvalAbiertas> lEvalsAbiertas) {
		String elKey = null;

		/*
		 * Esta lista se carga con todos los postulantes diferentes con su
		 * ultima evaluacion recibida.
		 * 
		 * Esto sirve para dos cosas:
		 * 
		 * 1) No dejar vacia la lista de evaluaciones abiertas cuando haya una
		 * nueva evaluacion por realizar
		 * 
		 * 2) Filtrar la lista de evaluaciones abiertas para el caso de porc.
		 * Minimo = Por cada Evaluación
		 */
		Map<String, EvalAbiertas> diccPostulantes = new HashMap<String, EvalAbiertas>();

		// Se borrar los elementos seleccionados
		for (EvalAbiertas o : lEvalsAbiertas) {
			elKey = o.getPostulacion().getIdPostulacion().toString();
			if (!diccPostulantes.containsKey(elKey)) {
				diccPostulantes.put(elKey, o);
			} else {
				/*
				 * En este caso ya se tiene cargado al postulante,
				 * 
				 * pero hay que asegurarse de que sea la última evaluación.
				 * 
				 * Si lo es, entra en el diccionario y se descarta el valor
				 * anterior
				 */
				if (o.getEvalReferencial().getFechaEvaluacion().getTime() > diccPostulantes
						.get(elKey).getEvalReferencial().getFechaEvaluacion()
						.getTime()) {
					diccPostulantes.put(elKey, o);
				}
			}

		}
		lEvalsAbiertas.clear();
		for (String o : diccPostulantes.keySet()) {
			lEvalsAbiertas.add(diccPostulantes.get(o));
		}
		return lEvalsAbiertas;
	}

	private void inicializacionAbiertas() {
		if (tipoEvalObj != null)
			// Se inicializan los que todavia no fueron evaluados en la
			// evaluacion actual
			for (EvalAbiertas o : lEvalsAbiertas) {
				System.out.println(tipoEvalObj.getIdDatosEspecificos()
						.intValue());
				if (o.getEvalReferencialTipoeval() != null
						&& o.getEvalReferencialTipoeval().getDatosEspecificos()
								.getIdDatosEspecificos().intValue() != tipoEvalObj
								.getIdDatosEspecificos().intValue()) {
					System.out.println(o.getEvalReferencialTipoeval()
							.getDatosEspecificos().getIdDatosEspecificos()
							.intValue());
					o.setDatosEspecificos(null);
					o.setEvalReferencial(null);
				}
			}
	}

	private List<EvalAbiertas> sacarAplazados(List<EvalAbiertas> lEvalsAbiertas) {
		/*
		 * Filtrado de Porc. Máximo a aplicar es por cada evaluación se debe
		 * sacar los que no aprobaron en la ultima evaluacion que recibieron
		 */
		if (tipoEvalObj == null){
			
			//se busca el ultimo tipo de evaluacion que se haya realizado					
			String sql = " select * from seleccion.eval_referencial_tipoeval tipoeval where tipoeval.fecha_cierre_evaluacion is not null "
					+ " and id_concurso_puesto_agr =  "+grupoPuestosController.getIdConcursoPuestoAgr() 
					+ " order by tipoeval.fecha_cierre_evaluacion desc ";
			
			List<EvalReferencialTipoeval> listTipoeval = em.createNativeQuery(sql, EvalReferencialTipoeval.class).getResultList();
				
			DatosEspecificos aux;
			if(listTipoeval.size()!= 0 && listTipoeval.get(0) != null){
			
				aux =  listTipoeval.get(0).getDatosEspecificos();
				int indice = -1;
				for (MatrizRefConfEnc o : lTipoEval) {
					if (o.getDatosEspecificos().getIdDatosEspecificos().intValue() == aux.getIdDatosEspecificos().intValue()) {
						break;
					} else {
						indice++;
					}
				}
				
				MatrizRefConfEnc ultEval = lTipoEval.get(indice+1);
				
				if (ultEval != null) {
					// Condición para aplicar la eliminación por evaluación
					if (idPorcMinAplicar.equalsIgnoreCase(PORC_MIN_POR_EVAL)) {
						// Verificar el postulante ha pasado todo
						Iterator<EvalAbiertas> iterEvalAbiertas = lEvalsAbiertas
								.iterator();
						while (iterEvalAbiertas.hasNext()) {
							EvalAbiertas elem = iterEvalAbiertas.next();
							// Si el postulante no aprobo, se lo elimina de la lista,
							// sino continua
							if (elem.getPostulacion() != null) {
								
								if (!verifAproboTodasLasEvaluaciones(elem.getPostulacion())) {
														
									iterEvalAbiertas.remove();
								}
							}
						}
					}
				}
				
				return lEvalsAbiertas;
				
			}else			
				return lEvalsAbiertas;
		}
		
			
		MatrizRefConfEnc ultEval = calcEvalAnterior(tipoEvalObj);
		if (ultEval != null) {
			// Condición para aplicar la eliminación por evaluación
			if (idPorcMinAplicar.equalsIgnoreCase(PORC_MIN_POR_EVAL)) {
				// Verificar el postulante ha pasado todo
				Iterator<EvalAbiertas> iterEvalAbiertas = lEvalsAbiertas
						.iterator();
				while (iterEvalAbiertas.hasNext()) {
					EvalAbiertas elem = iterEvalAbiertas.next();
					// Si el postulante no aprobo, se lo elimina de la lista,
					// sino continua
					if (elem.getPostulacion() != null) {
						
						if (!verifAproboUltEval(elem.getPostulacion())) {
												
							iterEvalAbiertas.remove();
						}
					}
				}
			}else{
				if(idPorcMinAplicar.equalsIgnoreCase(PORC_MIN_AL_FINALIZAR_EVALS)){
					// Verificar el postulante ha pasado todo
					Iterator<EvalAbiertas> iterEvalAbiertas = lEvalsAbiertas
							.iterator();
					while (iterEvalAbiertas.hasNext()) {
						EvalAbiertas elem = iterEvalAbiertas.next();
						// Si el postulante no aprobo, se lo elimina de la lista,
						// sino continua
						if (elem.getPostulacion() != null) {
							
							if (!verifEstuvoPresente(elem.getPostulacion())) {
													
								iterEvalAbiertas.remove();
							}
						}
					}
				}
			}
		}
		return lEvalsAbiertas;
	}

	private void aplicarFiltradoListaAbierta() {

		if (terminoEvaluacion()) {
			/* Ya fueron realizadas todas las evaluaciones */
			lEvalsAbiertas.clear();
			totalPostulantes = 0;
			return;
		}

		/**************** Limpeza ****************/
		lEvalsAbiertas = filtro1(lEvalsAbiertas);

		inicializacionAbiertas();

		lEvalsAbiertas = sacarAplazados(lEvalsAbiertas);
		/********** Fin Limpieza y filtrados *******************/
		// Recalcular la cantidad maxima de postulantes
		totalPostulantes = lEvalsAbiertas.size();
	}

	/**
	 * Retorna la última evaluación que recibió el postulante
	 * 
	 * @param p
	 *            : El postulante
	 * @return
	 */
	private Boolean verifAproboUltEval(Postulacion p) {
		// Traer la última evaluacion que recibió el postulante
		Query q = em
				.createQuery("select EvalReferencial from EvalReferencial EvalReferencial "
						+ " where EvalReferencial.postulacion.idPostulacion = "
						+ p.getIdPostulacion()
						+ " and EvalReferencial.evalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr()
						+ " order by EvalReferencial.idEvalReferencial desc ");
		List<EvalReferencial> lista = q.getResultList();
		if (lista.size() > 0) {
			EvalReferencial ultEvalRecibida = lista.get(0);
			if(ultEvalRecibida != null){
							
				if (ultEvalRecibida.getAprobado() != null && ultEvalRecibida.getAprobado()) {
					return true;
				} else 
					if (tipoEvalObj != null && ultEvalRecibida.getEvalReferencialTipoeval().getDatosEspecificos().getIdDatosEspecificos().intValue() != tipoEvalObj.getIdDatosEspecificos().intValue()) {
					// Se aplazó en una evaluacion anterior. Esto evita que se lo
					// saque de la lista de postulantes si se apalzó en la
					// evaluacion actual
					return false;
				} else {
					return true;
				}
			}
		}
		// Todavia no fue evaluado
		return true;
	}
	
	
	private Boolean verifAproboTodasLasEvaluaciones(Postulacion p) {
		// Traer la última evaluacion que recibió el postulante
		Query q = em
				.createQuery("select EvalReferencial from EvalReferencial EvalReferencial "
						+ " where EvalReferencial.postulacion.idPostulacion = "
						+ p.getIdPostulacion()
						+ " and EvalReferencial.evalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr()
						+ " order by EvalReferencial.idEvalReferencial desc ");
		List<EvalReferencial> lista = q.getResultList();
		if (lista.size() > 0) {
			EvalReferencial ultEvalRecibida = lista.get(0);
			if(ultEvalRecibida != null){
							
				if (ultEvalRecibida.getAprobado() != null && ultEvalRecibida.getAprobado()) {
					return true;
				} else 
					
					return false;
				} 
			}
		 return true;
		}
		
	
	/**
	 * Retorna true si estuvo presente en la ultima evaluacion
	 * 
	 * @param p
	 *            : El postulante
	 * @return
	 */
	private Boolean verifEstuvoPresente(Postulacion p) {
		// Traer la última evaluacion que recibió el postulante
		Query q = em
				.createQuery("select EvalReferencial from EvalReferencial EvalReferencial "
						+ " where EvalReferencial.postulacion.idPostulacion = "
						+ p.getIdPostulacion()
						+ " and EvalReferencial.evalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr()
						+ " order by EvalReferencial.idEvalReferencial desc ");
		List<EvalReferencial> lista = q.getResultList();
		if (lista.size() > 0) {
			EvalReferencial ultEvalRecibida = lista.get(0);
			if(ultEvalRecibida != null){
							
				if (ultEvalRecibida.isPresente()) {
					return true;
				} else if (ultEvalRecibida.getEvalReferencialTipoeval().getDatosEspecificos().getIdDatosEspecificos().intValue() != tipoEvalObj.getIdDatosEspecificos().intValue()) {
					// Se aplazó en una evaluacion anterior. Esto evita que se lo
					// saque de la lista de postulantes si se apalzó en la
					// evaluacion actual
					return false;
				} else {
					return true;
				}
			}
		}
		// Todavia no fue evaluado
		return true;
	}

	private MatrizRefConfEnc calcEvalAnterior(DatosEspecificos de) {
		int indice = -1;
		for (MatrizRefConfEnc o : lTipoEval) {
			if (o.getDatosEspecificos().getIdDatosEspecificos().intValue() == de
					.getIdDatosEspecificos().intValue()) {
				break;
			} else {
				indice++;
			}
		}
		if (indice == -1) {
			return null;
		} else {
			
			return lTipoEval.get(indice);
		}

	}

	private void initAbiertas() {
		lEvalsAbiertasList = (EvalAbiertasList) Component.getInstance(
				EvalAbiertasList.class, true);
		lEvalsAbiertas = lEvalsAbiertasList
				.cargarEvalAbiertas(grupoPuestosController
						.getIdConcursoPuestoAgr());

		System.out.println(lEvalsAbiertas.size());
	}

	private List<EvalAbiertas> postulantesAEvaluar() {
		lEvalsAbiertasList = (EvalAbiertasList) Component.getInstance(
				EvalAbiertasList.class, true);
		List<EvalAbiertas> lEvalsAbiertas = lEvalsAbiertasList
				.cargarEvalAbiertas(grupoPuestosController
						.getIdConcursoPuestoAgr());

		lEvalsAbiertas = filtro1(lEvalsAbiertas);
		lEvalsAbiertas = sacarAplazados(lEvalsAbiertas);

		return lEvalsAbiertas;
	}

	private void initCerradas() {
		lEvalsCerradasList = (EvalCerradasList) Component.getInstance(
				EvalCerradasList.class, true);

		lEvalsCerradas = lEvalsCerradasList
				.cargarEvalCerradas(grupoPuestosController
						.getIdConcursoPuestoAgr());
		
	}

	private Boolean precondSearch() {
		if (fechaDesde != null && fechaHasta == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU88_cargarFechaDesdeYHasta"));
			return false;
		} else if (fechaDesde == null && fechaHasta != null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU88_cargarFechaDesdeYHasta"));
			return false;
		}
		return true;
	}

	public void search() {
		this.tabActivo = "tab2";
		if (precondSearch()) {
			if (lEvalsCerradasList == null) {
				lEvalsCerradasList = (EvalCerradasList) Component.getInstance(
						EvalCerradasList.class, true);
			}
			lEvalsCerradas = lEvalsCerradasList.cargarEvalCerradas(
					idTipoEvalCerradas, fechaDesde, fechaHasta,
					grupoPuestosController.getIdConcursoPuestoAgr());
			
		}
	}

	public void searchAll() {
		fechaDesde = null;
		fechaHasta = null;
		idTipoEvalCerradas = null;
		search();
		;
	}

	private List<EvalReferencialTipoeval> evalTipoEvalPorGrupo(Long idGrupo) {
		Query q = em
				.createQuery("Select EvalReferencialTipoeval  from EvalReferencialTipoeval EvalReferencialTipoeval "
						+ " where EvalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idGrupo + "order by idEvalReferencialTipoeval desc");
		List<EvalReferencialTipoeval> lista = q.getResultList();
		return lista;

	}

	private Float calcPuntajeMayor(List<String> lista) {
		String inSet = "";
		for (String o : lista) {
			inSet += "," + o;
		}
		inSet = inSet.replaceFirst(",", "");
		Query q = em
				.createQuery("select sum(EvalReferencial.puntajeRealizado) from EvalReferencial EvalReferencial "
						+ "where EvalReferencial.evalReferencialTipoeval.idEvalReferencialTipoeval in ("
						+ inSet + ")");
		java.lang.Double puntajeMaximo = (java.lang.Double) q.getSingleResult();
		System.out.println(puntajeMaximo.getClass());
		return puntajeMaximo.floatValue();
	}

	private Set<EvalReferencial> caragarEvalReferencial(Long idEvalRefTipoEval) {
		Query q = em
				.createQuery("select EvalReferencial from EvalReferencial EvalReferencial "
						+ "where EvalReferencial.evalReferencialTipoeval.idEvalReferencialTipoeval = "
						+ idEvalRefTipoEval);
		Set<EvalReferencial> set = new HashSet<EvalReferencial>(
				q.getResultList());

		return set;
	}

	private Postulacion refreshPostulacion(Long idPostulacion) {
		Postulacion postulacion = em.find(Postulacion.class, idPostulacion);

		return postulacion;
	}

	private Map<String, Float> calcularPromedios(
			List<EvalReferencialTipoeval> lista) {
		Map<String, Float> diccPostulantePromedio = new HashMap<String, Float>();
		// Se guardan los Id de las evaluaciones realizadas
		List<String> listaIn = new ArrayList<String>();
		Set<EvalReferencial> lEvalReferencial = null;
		String elKey = null;
		for (EvalReferencialTipoeval o : lista) {
			listaIn.add(o.getIdEvalReferencialTipoeval().toString());
			if (o.getEvalReferencials().size() == 0) {
				System.out.println("¡¡¡¡¡LAZY LOADER ERROR!!!!!");
				lEvalReferencial = caragarEvalReferencial(o
						.getIdEvalReferencialTipoeval());
			} else {
				lEvalReferencial = o.getEvalReferencials();
			}
			// Calcular los promedios
			for (EvalReferencial p : lEvalReferencial) {
				elKey = p.getPostulacion().getIdPostulacion().toString();
				if (!diccPostulantePromedio.containsKey(elKey)) {
					diccPostulantePromedio.put(elKey, new Float(0));
				}
				diccPostulantePromedio.put(
						elKey,
						diccPostulantePromedio.get(elKey).floatValue()
								+ p.getPuntajeRealizado());

			}
		}
		// Calculo de promedio
		int cantEvaluaciones = lista.size();// Todos deberian tener la misma
											// cantidad de evaluaciones
		for (String o : diccPostulantePromedio.keySet()) {
			diccPostulantePromedio.put(o, diccPostulantePromedio.get(o)
					.floatValue());
			// cantEvaluaciones);
		}
		// Float puntajeMaximo = calcPuntajeMayor(listaIn);
		// diccPostulantePromedio.put("PUNTAJE_MAXIMO",
		// puntajeMaximo.floatValue());
		return diccPostulantePromedio;
	}

	/**
	 * Devuelve la última evaluación a realizar, teniendo en cuenta las
	 * evaluaciones salteadas (por ser opcionales).
	 * 
	 * @return
	 */
	private MatrizRefConfEnc ultEvalRealizar() {
		for (int i = lTipoEval.size() - 1; i > 0; i--) {
			// lTipoEvalPasados
			if (!lTipoEvalPasados.contains(lTipoEval.get(i)
					.getIdMatrizRefConfEnc().toString())) {
				System.out.println("ID: "
						+ lTipoEval.get(i).getDatosEspecificos()
								.getIdDatosEspecificos()
						+ ", "
						+ lTipoEval.get(i).getDatosEspecificos()
								.getDescripcion());
				return lTipoEval.get(i);
			}
		}
		return null;

	}

	/**
	 * Indica si ya se ha realizado la última evaluación de la lista de
	 * evaluaciones
	 * 
	 * @return
	 */
	private Boolean esUltimaEval() {
		List<EvalReferencialTipoeval> lEvaluacioneHechas;
		lEvaluacioneHechas = calEvlasHechas(grupoPuestosController
				.getIdConcursoPuestoAgr());
		MatrizRefConfEnc ultimaEvalARealizar = ultEvalRealizar();
		if (lEvaluacioneHechas.size() == 0) {
			/*
			 * En este caso no hay evaluaciones hechas
			 * 
			 * Pero puede darse dos casos:
			 * 
			 * 1) La evaluacion a realizar es obligatoria: En este caso
			 * devolvemos false
			 * 
			 * 2) La evaluacion a realizar no es obligatoria: En este caso
			 * devolvemos true
			 */
			if (ultimaEvalARealizar.getObligatorioSN()) {
				return false;
			} else {
				return true;
			}
		}
		EvalReferencialTipoeval ultimaEvalRealizada = lEvaluacioneHechas
				.get(lEvaluacioneHechas.size() - 1);

		if (ultimaEvalARealizar
				.getDatosEspecificos()
				.getIdDatosEspecificos()
				.toString()
				.equalsIgnoreCase(
						ultimaEvalRealizada.getDatosEspecificos()
								.getIdDatosEspecificos().toString())) {
			return true;
		}
		return false;
	}

	/**
	 * Indica si la evaluacion actual ya ha sido aplicada a cualquiera de los
	 * postulantes presentes
	 * 
	 * @return
	 */
	private Boolean hayEvaluados(MatrizRefConfEnc elFactor) {

		Long idDatosEspecificos = elFactor.getDatosEspecificos().getIdDatosEspecificos();
		
		
		Query q = em
				.createQuery("select evalReferencial from EvalReferencial evalReferencial where"
						+ " evalReferencial.evalReferencialTipoeval.datosEspecificos.idDatosEspecificos = "
						+ idDatosEspecificos);

		List <EvalReferencial> lista = q.getResultList();
		if (lista.size() > 0) {
			return true;
		}
		return false;
	}

	private Boolean terminoEvaluacion() {
		if (tipoEvalObj == null) {
			return true;
		}
		return false;

	}

	private Boolean sePuedeCerrarEval() {

		Integer countEvaluados = new Integer(cantPostulantesEvals());
		if ((totalPostulantes.intValue() - countEvaluados.intValue()) <= 0) {
			return true;
		}

		return false;
	}

	public String cerrarEvaluacion() {
		if (terminoEvaluacion()) {
			statusMessages.add(Severity.ERROR,
					"No hay evaluaciones que cerrar.");
			return "FAIL";
		}
		
		
		
		Integer countEvaluados = new Integer(cantPostulantesEvals());
		int pendientes = totalPostulantes.intValue()
				- countEvaluados.intValue();

		if (sePuedeCerrarEval()) {
			/* Se puede cerrar */
			
			//List <EvalReferencialTipoeval> list = traerEncabezado();//Probar si s puede cerrar la lista de evaluaciones y no solo uno
			
			
			//SE VERFICAR SI ES NECESARIO DISMINUIR PUESTOS ANTES DE CERRAR LA EVALUACION
			
							
				Integer cantPostulantes = obtenerCantidadPostulantes(grupoPuestosController.getIdConcursoPuestoAgr());
				
				if(disminuirPuestos(cantPostulantes,grupoPuestosController.getIdConcursoPuestoAgr() ))
					return "FAIL";
				
			// FIN BLOQUE DISMINUIR PUESTOS
			
			
			EvalReferencialTipoeval evalReferencialTipoeval = traerEncabezado();
			//for(EvalReferencialTipoeval evalReferencialTipoeval : list){
				if (evalReferencialTipoeval == null) {
					statusMessages.add(Severity.ERROR,
							"No se puede cerrar la Evaluación");
					return "FAIL";
				}
				if (evalReferencialTipoeval.getFechaCierreEvaluacion() != null) {
					statusMessages.add(Severity.ERROR,
							"La evaluación ya ha sido cerrada");
					return "FAIL";
				}
				evalReferencialTipoeval.setFechaCierreEvaluacion(new Date());
				evalReferencialTipoeval.setUsuCierreEvaluacion(usuarioLogueado
						.getCodigoUsuario());
				evalReferencialTipoeval.setObservacion(observacion);
				em.persist(evalReferencialTipoeval);
				em.flush();
			//}
			
			if(datosGrupoPuesto.getPorMinPorEvaluacion() != null && datosGrupoPuesto.getPorMinPorEvaluacion()){
				enviarMail();
				//SOLO SE PUBLICARA LA LISTA DE ADMITIDOS POR ETAPA... NO SE PUBLICARA LA LISTA DE NO APROBADOS
				this.publicacionPortal(this.genTextoPublicacionAdmitidosXEvaluacion(evalReferencialTipoeval), 
												evalReferencialTipoeval.getConcursoPuestoAgr().getConcurso().getIdConcurso(),
														evalReferencialTipoeval.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
				
				
				
				
			}	
			
			siguienteEvaluacion();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
			

		} else {
			if (idTipoEval == null) {
				statusMessages.add(Severity.ERROR,
						"No existe evaluación que cerrar.");
				return "FAIL";
			} else {
				/* no se puede cerrar */
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU88_noPuedeCerrar")
						+ " "
						+ pendientes + " postulante/s.");
				return "FAIL";
			}

		}
		
		String desbloqueado = desbloquearEvaluaciones(grupoPuestosController.getIdConcursoPuestoAgr());
		if(desbloqueado.equals("ERROR")){
			statusMessages.add(Severity.INFO, "Existen Evaluaciones Bloqueadas por otros usuarios.. verifique.");
			
		}
		
			
		return "OK";
	}
	
	public String desbloquearEvaluaciones(Long idConcursoPuestoAgr){
		String retorno = "OK";
		try {
			String sql = " select * from  seleccion.eval_documental_cab  "
					+ " where incluido = true and aprobado = true"
					+ " and id_postulacion in (select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = "+idConcursoPuestoAgr+")";
			
			List<EvalDocumentalCab> lista = em.createNativeQuery(sql,EvalDocumentalCab.class).getResultList();
			
			for (EvalDocumentalCab cab : lista){
				cab.setIncluido(false);
				em.merge(cab);
			
			}
			em.flush();
				
			
			
		}
		catch (Exception e){
			retorno = "ERROR";
		}
		
		
		return retorno;
	}
	
	
	public Boolean disminuirPuestos(Integer cantPostulantes, Long idConcursoPuestoAgr) {
		Integer cantVacancias = cantVacancias(idConcursoPuestoAgr);
		Boolean retorno = false;
		if (cantVacancias.intValue() >= cantPostulantes.intValue()) {
			Integer cantDisminuir = (cantVacancias.intValue() - cantPostulantes
					.intValue()) + 1;
						
			EvaluarDocPostulantesFormController evaluarDocPostulantesFormController = (EvaluarDocPostulantesFormController) Component
					.getInstance(EvaluarDocPostulantesFormController.class,
							true);
				evaluarDocPostulantesFormController.setConcursoPuestoAgr(grupoPuestosController.getConcursoPuestoAgr());
				evaluarDocPostulantesFormController.setCodActividad("REALIZAR_EVALUACIONES");
				
				evaluarDocPostulantesFormController.logicaDisminuirPuestos(cantPostulantes, "seleccion/realizarEval/realizarEvals");
			retorno = true;
		} 
		return retorno;
	}
	
	public Integer cantVacancias(Long idConcursoPuestoAgr) {
		Query q = em
				.createQuery("select count(ConcursoPuestoDet) from ConcursoPuestoDet ConcursoPuestoDet "
						+ "where ConcursoPuestoDet.activo is true and ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idConcursoPuestoAgr);
		Object cant = q.getSingleResult();
		
		return ((Long) cant).intValue();
	}

	/**
	 * Metodo que pasa de un tipo de evaluación a otro en un sólo sentido (desde
	 * el de menor orden al de mayor orden)
	 */
	public void siguienteEvaluacion() {
		/**/
		actualizarTipoEvalPasados();
		/**/
		Query q = null;
		List<EvalReferencialTipoeval> lEvalTipoEval;
		List<EvalReferencial> lEvalReferencial;
		int contador = 0;
		for (MatrizRefConfEnc elFactor : lTipoEval) {
			contador++;
	
			if (lTipoEvalPasados
					.contains(elFactor.getIdMatrizRefConfEnc() + "")) {
				if (contador == lTipoEval.size()) {
					idTipoEval = null;
				}
				continue;
			}
			/*
			 * Verificar si esta cerrado. Si esta cerrado pone el cursor en el
			 * siguiente
			 */
			q = em.createQuery("select evalReferencialTipoeval from EvalReferencialTipoeval evalReferencialTipoeval where "
					+ " evalReferencialTipoeval.datosEspecificos.idDatosEspecificos = "
					+ elFactor.getDatosEspecificos().getIdDatosEspecificos()
					+ " AND"
					+ " evalReferencialTipoeval.fechaCierreEvaluacion is not null and evalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
					+ grupoPuestosController.getIdConcursoPuestoAgr());
			lEvalTipoEval = q.getResultList();
			if (lEvalTipoEval.size() == 0 && elFactor.getObligatorioSN()) {
				/*
				 * No esta cerrado pero es obligatorio. Se pone el cursor y se
				 * bloquea el combo
				 */
				idTipoEval = elFactor.getIdMatrizRefConfEnc();
				bloquearComboTipoEval = true;
				break;
			} else if (lEvalTipoEval.size() == 0
					&& !elFactor.getObligatorioSN()) {
				/*
				 * No esta cerrado y no es obligatorio. Se pone el cursor y no
				 * se bloquea el combo
				 * 
				 * si es que nadie fue evaluado aun. Caso contrario se bloquea
				 * el combo
				 */
				q = em.createQuery("select evalReferencialTipoeval from EvalReferencialTipoeval evalReferencialTipoeval where "
						+ " evalReferencialTipoeval.datosEspecificos.idDatosEspecificos = "
						+ elFactor.getDatosEspecificos()
								.getIdDatosEspecificos()
						+ " AND"
						+ " evalReferencialTipoeval.fechaCierreEvaluacion is null and evalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());
				lEvalReferencial = q.getResultList();
				if (lEvalReferencial.size() > 0) {
					bloquearComboTipoEval = true;
				} else {
					if (btnSgteEval) {
						// Se nos pide que pasemos de evaluacion
						idTipoEval = elFactor.getIdMatrizRefConfEnc();
						// Se almacena la evaluación que se quiere saltar
						if (!lTipoEvalPasados.contains(idTipoEval.toString())) {
							lTipoEvalPasados.add(idTipoEval.toString());
						}
						continue;
					}

					bloquearComboTipoEval = false;
				}
				idTipoEval = elFactor.getIdMatrizRefConfEnc();
				break;
			}
		}
		// Seteando las descripciones
		if (idTipoEval != null) {
			// Verificar si ya no fue cerrado
			MatrizRefConfEnc elFactor = em.find(MatrizRefConfEnc.class,
					idTipoEval);
			q = em.createQuery("select evalReferencialTipoeval from EvalReferencialTipoeval evalReferencialTipoeval where "
					+ " evalReferencialTipoeval.datosEspecificos.idDatosEspecificos = "
					+ elFactor.getDatosEspecificos().getIdDatosEspecificos()
					+ " AND"
					+ " evalReferencialTipoeval.fechaCierreEvaluacion is not null and evalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
					+ grupoPuestosController.getIdConcursoPuestoAgr());
			if (q.getResultList().size() == 1) {
				limpiezaSinEval();
			} else {
				tipoEvalDesc = elFactor.getDatosEspecificos().getDescripcion()
						+ (elFactor.getObligatorioSN() ? " (Obligatorio)"
								: " (Opcional)");
				idDatosEspecificosVer = elFactor.getDatosEspecificos().getIdDatosEspecificos();//MODIFICADO RV
				if (elFactor.getDatosEspecificos().getDescripcion()
						.equals("EVALUACION CURRICULAR")) {
					esCurricular = true;
					presente = AusentePresente.PRESENTE;
				} else {
					esCurricular = false;
				}
				tipoEvalObj = elFactor.getDatosEspecificos();
			}
		} else {
			limpiezaSinEval();
		}
		
		btnSgteEval = false;

	}

	private void limpiezaSinEval() {
		tipoEvalDesc = "-";
		tipoEvalObj = null;
		aplicarFiltradoListaAbierta();
	}

	/**
	 * Cantidad de Postulantes evaluados en esta evaluación en la última
	 * evaluacion iterada
	 */
	public String cantPostulantesEvals() {
		if (idTipoEval == null) {
			return "0";
		}
		MatrizRefConfEnc elFactor = em.find(MatrizRefConfEnc.class, idTipoEval);
		Query q = em
				.createQuery("select evalReferencial from EvalReferencial evalReferencial where "
						+ "evalReferencial.evalReferencialTipoeval.datosEspecificos.idDatosEspecificos = "
						+ elFactor.getDatosEspecificos()
								.getIdDatosEspecificos()
						+ " and evalReferencial.evalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr()
						+ " and evalReferencial.postulacion.usuCancelacion is null");

		List <EvalReferencial> cuenta = q.getResultList();
		long cantidadEvaluados = 0;
		for(EvalReferencial evalRef : cuenta){
			if(tieneDetalles(evalRef.getIdEvalReferencial()))
				cantidadEvaluados+=1; 
		}
		
		
		return ""+cantidadEvaluados;

	}	

	

	private void cargarCabMatrizConfigurada() {
		/**/
		Query q = em
				.createQuery("select matrizRefConf from MatrizRefConf matrizRefConf where matrizRefConf.tipo ='GRUPO' and "
						+ "matrizRefConf.concursoPuestoAgr.idConcursoPuestoAgr ="
						+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<MatrizRefConf> lista = q.getResultList();
		if (lista.size() == 1) {
			MatrizRefConf matrizRefConf = lista.get(0);
			matriz = matrizRefConf.getDescripcion() == null ? "-"
					: matrizRefConf.getDescripcion().trim();
			puntajeMax = matrizRefConf.getPuntajeMaximo() + "";
		} else {
			matriz = "-";
			puntajeMax = "-";
		}
		/**/
		q = em.createQuery("select datosGrupoPuesto from DatosGrupoPuesto datosGrupoPuesto where "
				+ "datosGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr =  "
				+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DatosGrupoPuesto> lista2 = q.getResultList();
		if (lista2.size() == 1) {
			DecimalFormat fNumero = new DecimalFormat("#0.00");
			DatosGrupoPuesto datosGrupoPuesto = lista2.get(0);
			if (datosGrupoPuesto.getPorMinPorEvaluacion() != null
					&& datosGrupoPuesto.getPorMinPorEvaluacion()) {
				porcMinAplicar = SeamResourceBundle.getBundle().getString(
						"CU88_porCadaEvaluacion");
				idPorcMinAplicar = PORC_MIN_POR_EVAL;
			}

			if (datosGrupoPuesto.getPorMinFinEvaluacion() != null
					&& datosGrupoPuesto.getPorMinFinEvaluacion()) {
				porcMinAplicar = SeamResourceBundle.getBundle().getString(
						"CU88_alFinalizarEvals");
				idPorcMinAplicar = PORC_MIN_AL_FINALIZAR_EVALS;
			}

			if (datosGrupoPuesto.getTerna() != null	&& datosGrupoPuesto.getTerna())
				seleccion = SeamResourceBundle.getBundle().getString(
						"CU88_porTerna");
			if (datosGrupoPuesto.getMerito() != null
					&& datosGrupoPuesto.getMerito())
				seleccion = SeamResourceBundle.getBundle().getString(
						"CU88_porPuntaje");
			if (datosGrupoPuesto.getPorcMinimo() != null) {
				minimoDesc = fNumero.format(datosGrupoPuesto.getPorcMinimo()
						.doubleValue());
			}
		}

	}

	public void sumarPuntos(Boolean init) {
		if (init) {
			return;
		}
		if (esCurricular) {
			presente = AusentePresente.PRESENTE;
		}
		if (precondEvaluacion()) {
			if (presente.getValor()) {
				try {
					float sumatoriaItems = 0;
					sumatoriaFactores = 0;
					
					
					for (MatrizRefConfEnc o : lMatRefConfEnc) {
						sumatoriaFactores += o.getPuntajeMaximo();
						for (MatrizRefConfDet p : o.getMatrizRefConfDets()) {
							if (p.getPuntaje() != null)
								sumatoriaItems += p.getPuntaje();
						}
					}

					totalPuntos = new Float(
							new Float(sumatoriaItems).floatValue());
					totalAlcanzado = new Float(
							(new Float(
									(sumatoriaItems / sumatoriaFactores) * 100))
									.floatValue());

				} catch (Exception e) {
					limpiarTotales();
					e.printStackTrace();
				}
			} else {
				limpiarTotales();
			}
		}
	}

	private void limpiarTotales() {
		totalPuntos = new Float("0");
		totalAlcanzado = new Float("0");
		sumatoriaFactores = 0;

	}

	public void editarDatosConvDet(Integer idRow) {
		if (lEvalRefConvo != null && lEvalRefConvo.size() > idRow.intValue()) {
			rowConvoSele = idRow.intValue();
			EvalReferencialConvocatoria editadoEvalRefConv = lEvalRefConvo
					.get(idRow.intValue());
			direccion = editadoEvalRefConv.getDireccion();
			// idTipoEvalCerradas =
			// editadoEvalRefConv.getEvalReferencialTipoeval().getIdEvalReferencialTipoeval();
			fechaConvo = editadoEvalRefConv.getFechaConvocatoria();
			idCiudad = editadoEvalRefConv.getIdCiudad();
			lugar = editadoEvalRefConv.getLugar();
			observacion3 = editadoEvalRefConv.getObservacion();
			motivo = editadoEvalRefConv.getMotivo();
			if (editadoEvalRefConv.getHoraDesde() != null)
				horaDesde = sdfHORA.format(editadoEvalRefConv.getHoraDesde());
			if (editadoEvalRefConv.getHoraHasta() != null)
				horaHasta = sdfHORA.format(editadoEvalRefConv.getHoraHasta());
			actualizar = true;
		}
	}
	
	public boolean habilitarEnlaces(Integer idRow) {
		boolean retorno = true;
		if (lEvalRefConvo != null && lEvalRefConvo.size() > idRow.intValue()) {
			rowConvoSele = idRow.intValue();
			EvalReferencialConvocatoria editadoEvalRefConv = lEvalRefConvo.get(idRow.intValue());
			
			if( editadoEvalRefConv.getFechaPublicacion() != null)
				retorno = false;
		}
		return retorno;
	}

	
	public void updateDatosConvDet() {
		if (lEvalRefConvo.size() > rowConvoSele) {
			try {
				EvalReferencialConvocatoria entity = lEvalRefConvo
						.get(rowConvoSele);
				entity.setDireccion(direccion);
				entity.setLugar(lugar);
				entity.setFechaConvocatoria(fechaConvo);
				entity.setHoraDesde(sdfHORA.parse(horaDesde));
				if(horaHasta != null && !horaHasta.trim().equals(""))
					entity.setHoraHasta(sdfHORA.parse(horaHasta));
				if(observacion3 != null)
					entity.setObservacion(observacion3);
				entity.setIdCiudad(idCiudad);
				limpiarVariables();

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public String formatearHora(Date laFecha) {
		String retorno = "";
		if(laFecha != null)
			retorno = sdfHORA.format(laFecha);
		return retorno;
	}

	public void eliminarDatosConvDet(Integer idRow) {

		if (lEvalRefConvo != null && lEvalRefConvo.size() > idRow.intValue()) {
			EvalReferencialConvocatoria entity = lEvalRefConvo.get(idRow
					.intValue());
			if (entity.getIdEvalReferencialConvocatoria() != null) {
				entity = em.find(EvalReferencialConvocatoria.class,
						entity.getIdEvalReferencialConvocatoria());
				em.remove(entity);
			}
			lEvalRefConvo.remove(idRow.intValue());
			limpiarVariables();
		}
	}

	public String esPersonDisca(Long idPersona) {
		RegistrarPostulacionFormController registrarPostulacionFormController = (RegistrarPostulacionFormController) Component
				.getInstance(RegistrarPostulacionFormController.class, true);
		if (registrarPostulacionFormController
				.verifCompletoFichaDisca(idPersona)) {
			return "Sí";

		} else
			return "No";
	}

	private Boolean precondAgregarEvaluadores() {
		if (idInte == null) {
			return false;
		}
		if (idComisionSeleccion == null && idEmpTerce == null) {
			return false;
		}
		return true;
	}

	public void eliminarEvaluador(Integer indice) {
		if (lEvaluadores != null && lEvaluadores.size() > indice.intValue()) {
			lEvaluadores.remove(indice.intValue());
		}
	}

	

	public void agregarDetConvocatoria() throws ParseException {
		if (precondSaveConvo()) {
			if (lEvalRefConvo == null) {
				lEvalRefConvo = new ArrayList<EvalReferencialConvocatoria>();
			}
			
			
			EvalReferencialConvocatoria nuevaConvocatoria = new EvalReferencialConvocatoria();
			
			nuevaConvocatoria.setActivo(true);
			nuevaConvocatoria.setDireccion(direccion);
			nuevaConvocatoria.setEvalReferencialTipoeval(evalReferencialTipoeval);
			nuevaConvocatoria.setFechaAlta(new Date());
			nuevaConvocatoria.setFechaConvocatoria(fechaConvo);
			nuevaConvocatoria.setHoraDesde(sdfHORA.parse(horaDesde));
			if(horaHasta != null && !horaHasta.trim().equals(""))
				nuevaConvocatoria.setHoraHasta(sdfHORA.parse(horaHasta));
			nuevaConvocatoria.setIdCiudad(idCiudad);
			nuevaConvocatoria.setLugar(lugar);
			if(observacion3 != null && !observacion3.trim().equals(""))
				nuevaConvocatoria.setObservacion(observacion3);
			nuevaConvocatoria.setMotivo(motivo);			
			nuevaConvocatoria.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			
			lEvalRefConvo.add(nuevaConvocatoria);
			limpiarVariables();
		}
	}

	/**
	 * Indica si lo que se quiere agregar a la lista de evaluadores no esta
	 * presente.
	 * 
	 * @return true si el elemento que se intenta introducir ya se encuentra en
	 *         la lista. False en cualquier otro caso
	 */
	private Boolean validarRepetido(ComisionSeleccionDet comi,
			EmpresaPersona empTerce) {
		if (comi != null) {
			for (EvalReferencialComis o : lEvaluadores) {
				if (o.getComisionSeleccionDet() != null) {
					if (o.getComisionSeleccionDet()
							.getIdComisionSelDet()
							.toString()
							.equalsIgnoreCase(
									comi.getIdComisionSelDet().toString())) {
						return true;
					}
				}
			}
		} else if (empTerce != null) {
			for (EvalReferencialComis o : lEvaluadores) {
				if (o.getEmpresaPersona() != null) {
					if (o.getEmpresaPersona()
							.getEmprTercerizada()
							.getIdEmpresaTercerizada()
							.toString()
							.equalsIgnoreCase(
									empTerce.getEmprTercerizada()
											.getIdEmpresaTercerizada()
											.toString())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void agregarEvaluadores() {

		if (precondAgregarEvaluadores()) {
			if (lEvaluadores == null) {
				lEvaluadores = new ArrayList<EvalReferencialComis>();
			}
			Query q = null;
			if (idComisionSeleccion != null && idEmpTerce == null) {
				q = em.createQuery("select comisionSeleccionDet  from ComisionSeleccionDet comisionSeleccionDet,"
						+ "ComisionSeleccionCab comisionSeleccionCab,ComisionGrupo comisionGrupo "
						+ " WHERE comisionSeleccionDet.comisionSeleccionCab=comisionSeleccionCab AND"
						+ " comisionSeleccionDet.persona.idPersona = "
						+ idInte
						+ " AND comisionGrupo.comisionSeleccionCab =comisionSeleccionCab "
						+ " AND comisionGrupo.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());
				List<ComisionSeleccionDet> lista = q.getResultList();
				if (lista.size() == 1) {
					if (!validarRepetido(lista.get(0), null)) {
						lEvaluadores.add(new EvalReferencialComis());
						int ultIndice = lEvaluadores.size() - 1;
						lEvaluadores.get(ultIndice).setComisionSeleccionDet(
								lista.get(0));
						lEvaluadores.get(ultIndice).setActivo(true);
						lEvaluadores.get(ultIndice).setUsuAlta(
								usuarioLogueado.getCodigoUsuario());
						lEvaluadores.get(ultIndice).setFechaAlta(new Date());
					} else {
						statusMessages
								.add(Severity.ERROR,
										"El registro ya se encuentra en la Lista de Evaluadores");
					}

				} else {
					if (lista.size() == 0) {
						statusMessages.add(Severity.ERROR, SeamResourceBundle
								.getBundle().getString("CU88_noHayRegistros"));
					} else {
						statusMessages.add(Severity.ERROR, SeamResourceBundle
								.getBundle().getString("CU88_masUnoComision")
								+ comisionSeleccion);
					}

				}
			} else if (idComisionSeleccion == null && idEmpTerce != null) {
				q = em.createQuery("select empresaPersona  from EmpresaPersona empresaPersona, EmprTercerizada emprTercerizada "
						+ "WHERE empresaPersona.emprTercerizada = emprTercerizada"
						+ " AND empresaPersona.persona.idPersona = " + idInte);
				List<EmpresaPersona> lista = q.getResultList();
				if (lista.size() == 1) {
					if (!validarRepetido(null, lista.get(0))) {
						lEvaluadores.add(new EvalReferencialComis());
						int ultIndice = lEvaluadores.size() - 1;
						lEvaluadores.get(ultIndice).setEmpresaPersona(
								lista.get(0));
						lEvaluadores.get(ultIndice).setActivo(true);
						lEvaluadores.get(ultIndice).setUsuAlta(
								usuarioLogueado.getCodigoUsuario());
						lEvaluadores.get(ultIndice).setFechaAlta(new Date());
					} else {
						statusMessages
								.add(Severity.ERROR,
										"El registro ya se encuentra en la Lista de Evaluadores");
					}

				} else {
					if (lista.size() == 0) {
						statusMessages.add(Severity.ERROR, SeamResourceBundle
								.getBundle().getString("CU88_noHayRegistros"));
					} else {
						statusMessages.add(
								Severity.ERROR,
								SeamResourceBundle.getBundle().getString(
										"CU88_masUnoPersonaEmpresa"));
					}
				}
			}

		}
	}

	public void agregarTodosLosEvaluadores() {
		Query query = em
				.createQuery("select comisionSeleccionCab from ComisionSeleccionCab comisionSeleccionCab, ComisionGrupo comisionGrupo "
						+ "WHERE comisionGrupo.comisionSeleccionCab = comisionSeleccionCab AND"
						+ " comisionGrupo.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());

		List<ComisionSeleccionCab> listaComisionCab = query.getResultList();
		
		if (listaComisionCab.size() == 1) {
		
			idComisionSeleccion = listaComisionCab.get(0).getIdComisionSel();
		
		}
		
		
		if (idComisionSeleccion != null) {
			if (lEvaluadores == null) {
				lEvaluadores = new ArrayList<EvalReferencialComis>();
			}
			Query q = null;
			if (idComisionSeleccion != null && idEmpTerce == null) {
				q = em.createQuery("select comisionSeleccionDet  from ComisionSeleccionDet comisionSeleccionDet,"
						+ "ComisionSeleccionCab comisionSeleccionCab,ComisionGrupo comisionGrupo "
						+ " WHERE comisionSeleccionDet.comisionSeleccionCab=comisionSeleccionCab "
						+ " AND comisionGrupo.comisionSeleccionCab =comisionSeleccionCab "
						+ " AND comisionGrupo.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());
				List<ComisionSeleccionDet> lista = q.getResultList();
				if (lista.size() >= 1) {
					for (int i = 0 ; i < lista.size(); i++ ) {
						lEvaluadores.add(new EvalReferencialComis());
						int ultIndice = lEvaluadores.size() - 1;
						lEvaluadores.get(ultIndice).setComisionSeleccionDet(lista.get(i));
						lEvaluadores.get(ultIndice).setActivo(true);
						lEvaluadores.get(ultIndice).setUsuAlta(
								usuarioLogueado.getCodigoUsuario());
						lEvaluadores.get(ultIndice).setFechaAlta(new Date());
					}

				} else {
					if (lista.size() == 0) {
						statusMessages.add(Severity.ERROR, SeamResourceBundle
								.getBundle().getString("CU88_noHayRegistros"));
					} else {
						statusMessages.add(Severity.ERROR, SeamResourceBundle
								.getBundle().getString("CU88_masUnoComision")
								+ comisionSeleccion);
					}

				}
			} 
//				else if (idComisionSeleccion == null && idEmpTerce != null) {
//				q = em.createQuery("select empresaPersona  from EmpresaPersona empresaPersona, EmprTercerizada emprTercerizada "
//						+ "WHERE empresaPersona.emprTercerizada = emprTercerizada"
//						+ " AND empresaPersona.persona.idPersona = " + idInte);
//				List<EmpresaPersona> lista = q.getResultList();
//				if (lista.size() == 1) {
//					if (!validarRepetido(null, lista.get(0))) {
//						lEvaluadores.add(new EvalReferencialComis());
//						int ultIndice = lEvaluadores.size() - 1;
//						lEvaluadores.get(ultIndice).setEmpresaPersona(
//								lista.get(0));
//						lEvaluadores.get(ultIndice).setActivo(true);
//						lEvaluadores.get(ultIndice).setUsuAlta(
//								usuarioLogueado.getCodigoUsuario());
//						lEvaluadores.get(ultIndice).setFechaAlta(new Date());
//					} else {
//						statusMessages
//								.add(Severity.ERROR,
//										"El registro ya se encuentra en la Lista de Evaluadores");
//					}
//
//				} else {
//					if (lista.size() == 0) {
//						statusMessages.add(Severity.ERROR, SeamResourceBundle
//								.getBundle().getString("CU88_noHayRegistros"));
//					} else {
//						statusMessages.add(
//								Severity.ERROR,
//								SeamResourceBundle.getBundle().getString(
//										"CU88_masUnoPersonaEmpresa"));
//					}
//				}
//			}

		}
	}
	
	
	public void initEvaluarPuntaje() {
		lock.lock();
//		initAbiertas();
		
		observacion1 = null;
		limpiarTotales();
		validarOee(em.find(ConcursoPuestoAgr.class,
				grupoPuestosController.getIdConcursoPuestoAgr()).getConcurso());
		limpiar();
		if (idPostulacion != null) {
			Postulacion postulacion = em.find(Postulacion.class, idPostulacion);
			if (postulacion != null) {
				validarOee(postulacion.getConcursoPuestoAgr().getConcurso());
			}
		}
		//General el evalReferencial a ser utilizado para marcar que esta siendo evaluado el postulante.. 
		generarEvalReferencial();
		
		agregarTodosLosEvaluadores();
		
		evalDocumentalFrom = "/seleccion/realizarEval/evalPuntajePostulante.seam?"
				+ "idDatosEspecificosVer="+idDatosEspecificosVer
				+ "&idConcursoPuesto="+idConcursoPuestoAgr
				+ "&tabActivo="+tabActivo
				+ "&idPostulacion="+idPostulacion
				+ "&codPostulante="+codPostulante
				+ "&idPersona="+idPersona
				+ "&ver="+ver;
		
		Query q = em.createQuery("select evaldocumentalcab  from EvalDocumentalCab evaldocumentalcab "
				+ "WHERE evaldocumentalcab.concursoPuestoAgr.idConcursoPuestoAgr = " + idConcursoPuestoAgr
				+ " AND evaldocumentalcab.postulacion.idPostulacion = " + idPostulacion);
		
		
		evaldocumentalcab = (EvalDocumentalCab)q.getSingleResult();
		
		
		//EvalDocumentalCab edc = em.find(EvalDocumentalCab.class, idEvalDocumentalCab); 
		
		em.refresh(evaldocumentalcab);
		
		if(evaldocumentalcab.getIncluido() == null || !evaldocumentalcab.getIncluido()){//INDICA QUE YA HA SIDO SELECCIONADO POR OTRO EVALUADOR
			
			
				evaldocumentalcab.setIncluido(true);
				evaldocumentalcab.setUsuMod(this.usuarioLogueado.getCodigoUsuario());
				evaldocumentalcab.setFechaMod(new Date());
				em.merge(evaldocumentalcab);
				em.flush();
				habilitarGuardar = true;
				habilitarVolver = false;
			
			
			statusMessages.clear();
			
		}else{
		
			if(evaldocumentalcab.getUsuMod().equals(usuarioLogueado.getCodigoUsuario())){
							
				habilitarGuardar = true;
				habilitarVolver = false;
				statusMessages.clear();
				
			}else{
				
				habilitarGuardar= false;
				statusMessages.clear();
				statusMessages.add(Severity.WARN, "El Postulante esta siendo evaluado por el Usuario: "+ evaldocumentalcab.getUsuMod());
				
			}
			
			
		}	
		
		
		// Siempre se debe verificar si ya no está cargada la evaluación para
		// esa postulación
		
		evalReferencialSelected = cargarVerEvaluacion();
 
		
		
		if (!ver) {
			// No estamos en formato ver. Por lo tanto, se debe preparar el
			// ambiente para el
			// caso en el que sea un editado de una evaluación o la creación de
			// la primera evaluación
			if (evalReferencialSelected == null) {
				cargarEvaluarPuntaje();
				cargarComisionSeleccion();
				updateEmpTerce();
				updateIntegrantes();
				inicializarTotales();
				cargarCabMatrizConfigurada();
			}
		}
		if (idPersona != null
				&& !esPersonDisca(idPersona).equalsIgnoreCase("No")) {
			conDiscaDesc = "*** Con Discapacidad ***";
		} else {
			conDiscaDesc = "";
		}
		lock.unlock();
	}

	public boolean isHabilitarGuardar() {
		return habilitarGuardar;
	}

	public void setHabilitarGuardar(boolean habilitarGuardar) {
		this.habilitarGuardar = habilitarGuardar;
	}

	private void inicializarTotales() {
		totalAlcanzado = new Float("0");
		totalPuntos = new Float("0");

	}

	private void cargarEvaluarPuntaje() {

		Query q = em
				.createQuery("select matrizRefConfEnc from MatrizRefConfEnc matrizRefConfEnc,MatrizRefConf matrizRefConf where "
						+ " matrizRefConfEnc.matrizRefConf = matrizRefConf  "
						+ " and matrizRefConf.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idConcursoPuestoAgr
						+ " and matrizRefConf.tipo ='GRUPO'"
						+ " and matrizRefConfEnc.activo = true "
						+ " and matrizRefConfEnc.matrizRefConf.activo = true "
						+ " and matrizRefConfEnc.datosEspecificos.idDatosEspecificos = "
						+ idDatosEspecificosVer
						+ " order by matrizRefConfEnc.nroOrden asc ");
		lMatRefConfEnc = q.getResultList();
		// Inicializar los puntajes
		if (lMatRefConfEnc.size() > 0) {
			for (MatrizRefConfEnc o : lMatRefConfEnc) {
				for (MatrizRefConfDet p : o.getMatrizRefConfDets()) {
					p.setPuntaje(null);
				}
			}
		}
	}

	private void setearPorAusencia() {
		if (!presente.getValor()) {
			totalAlcanzado = new Float("0");
			totalPuntos = new Float("0");
		}
	}

	private Integer porMinFinEvaluacion() {
		Query q = em
				.createQuery("select datosGrupoPuesto from DatosGrupoPuesto datosGrupoPuesto "
						+ " where datosGrupoPuesto.concursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());

		DatosGrupoPuesto dgp = (DatosGrupoPuesto) q.getSingleResult();

		return dgp.getPorcMinimo();
	}

	private Boolean calcAprobado() {
		if (!presente.getValor()) {
			return null;
		} else {
			Query q = em
					.createQuery("select datosGrupoPuesto from DatosGrupoPuesto datosGrupoPuesto "
							+ " where datosGrupoPuesto.concursoPuestoAgr = "
							+ grupoPuestosController.getIdConcursoPuestoAgr());

			DatosGrupoPuesto dgp = (DatosGrupoPuesto) q.getSingleResult();

			Integer porcAlcanzado = new Integer(totalAlcanzado.intValue() + "");
			if ((dgp.getPorcMinimo().compareTo(porcAlcanzado) == 0)
					|| (dgp.getPorcMinimo().compareTo(porcAlcanzado) < 0)) {
				return true;
			} else {
				return false;
			}

		}
	}

	private EvalReferencialTipoeval traerEncabezado() {
		MatrizRefConfEnc matrizRefConfEnc = em.find(MatrizRefConfEnc.class,
				idTipoEval);
		Query q = em
				.createQuery("select evalReferencialTipoeval from EvalReferencialTipoeval evalReferencialTipoeval "
						+ "where evalReferencialTipoeval.datosEspecificos.idDatosEspecificos = "
						+ matrizRefConfEnc.getDatosEspecificos()
								.getIdDatosEspecificos()
						+ " and evalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<EvalReferencialTipoeval> lista = q.getResultList();
		if (lista.size() >= 1) {
			return lista.get(0);
		}else{
			
			EvalReferencialTipoeval evaltipoEval = new EvalReferencialTipoeval();
			evaltipoEval.setConcursoPuestoAgr(em.find(ConcursoPuestoAgr.class, this.idConcursoPuestoAgr));
			evaltipoEval.setDatosEspecificos(em.find(DatosEspecificos.class, this.idDatosEspecificosVer));
			evaltipoEval.setActivo(true);
			evaltipoEval.setUsuAlta(this.usuarioLogueado.getCodigoUsuario());
			evaltipoEval.setFechaAlta(new Date());
			em.persist(evaltipoEval);
			em.flush();
			
			return evaltipoEval;
		}
		
		
	}

	private EvalReferencialTipoeval crearEvalReferencialTipoeval() {
		MatrizRefConfEnc matrizRefConfEnc = em.find(MatrizRefConfEnc.class,
				idTipoEval);
		EvalReferencialTipoeval evalReferencialTipoeval = new EvalReferencialTipoeval();
		evalReferencialTipoeval.setActivo(true);
		evalReferencialTipoeval.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		evalReferencialTipoeval.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
				grupoPuestosController.getIdConcursoPuestoAgr());
		evalReferencialTipoeval.setDatosEspecificos(new DatosEspecificos());
		evalReferencialTipoeval.getDatosEspecificos().setIdDatosEspecificos(
				matrizRefConfEnc.getDatosEspecificos().getIdDatosEspecificos());
		evalReferencialTipoeval.setFechaAlta(new Date());
		evalReferencialTipoeval.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		em.persist(evalReferencialTipoeval);
		return evalReferencialTipoeval;
	}

	private EvalReferencial guardarActualizarEvaluacion() throws Exception {
		Date laFecha = new Date();
		EvalReferencial evalRef = null;
		Boolean aprobado = null;
		try {
			EvalReferencialTipoeval evalRefeTipoEval;
			if (lMatRefConfEnc.size() > 0) {
				// En en caso que este ausente el postulante
				sumarPuntos(false);
				setearPorAusencia();
				if (evalReferencialSelected == null)
					// Crear Cabecera. Se crea una cabecera por cada postulante
					evalRef = new EvalReferencial();
				else {
					// Es un editado, no una creación
					evalRef = evalReferencialSelected;
				}
				evalRef.setActivo(true);
				aprobado = calcAprobado();
				evalRef.setAprobado(aprobado);
				/* Se crea uno por cada Tipo de Evaluacion */
				evalRefeTipoEval = traerEncabezado();
				evalRefeTipoEval = evalReferencialSelected.getEvalReferencialTipoeval();
				
				if (evalRef.getEvalReferencialTipoeval() == null) {
					evalRefeTipoEval = crearEvalReferencialTipoeval();
				}else
					evalRefeTipoEval = evalReferencialSelected.getEvalReferencialTipoeval();
				
				evalRef.setEvalReferencialTipoeval(evalRefeTipoEval);
				if (evalReferencialSelected == null) {
					evalRef.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					evalRef.setFechaAlta(laFecha);
				} else {
					evalRef.setFechaMod(new Date());
					evalRef.setUsuMod(usuarioLogueado.getCodigoUsuario());
				}
				if (!presente.getValor()) {
					evalRef.setAprobado(false);
				}
				evalRef.setFechaEvaluacion(laFecha);
				evalRef.setNroOrden(++nroOrden);
				evalRef.setObservacion(observacion1);
				evalRef.setPorcRealizado(totalAlcanzado);
				evalRef.setPostulacion(new Postulacion());
				evalRef.getPostulacion().setIdPostulacion(idPostulacion);
				evalRef.setPresente(presente.getValor());
				evalRef.setPuntajeRealizado(totalPuntos);
				evalRef.setSiendoEvaluado(false);
				
				if (evalReferencialSelected == null) {
					//em.persist(evalRef);edcm
				} else {
					evalRef = em.merge(evalRef);
				}
			}

			Float sumatoriaItems = new Float("0");
			Iterator iterReferencialCab = null;
			Iterator iterReferencialDet = null;
			//if (evalReferencialSelected == null) {//edcm
			if(!existenEvalReferencialCab(evalRef)){
				iterReferencialCab = lMatRefConfEnc.iterator();
			} else {
				
				
				
				if(evalRef.getEvalReferencialCabs().iterator().hasNext())
					iterReferencialCab = evalRef.getEvalReferencialCabs().iterator();//edcm
				else{
					
					evalRef= em.find(EvalReferencial.class, evalRef.getIdEvalReferencial());
					String sql = "select * from seleccion.eval_referencial_cab where id_eval_referencial = "+ evalRef.getIdEvalReferencial();
					List <EvalReferencialCab> list = em.createNativeQuery(sql,EvalReferencialCab.class).getResultList();
					
					Set<EvalReferencialCab> evalReferencialCabs = new HashSet<EvalReferencialCab>(0);
					for (EvalReferencialCab cab : list)
						
						evalReferencialCabs.add(cab);
					
					iterReferencialCab = evalReferencialCabs.iterator();
				}
					
				
				
			}
			while (iterReferencialCab.hasNext()) {
				EvalReferencialCab elFactor = null;
				MatrizRefConfEnc o = null;
				// Crear o Editar el Factor. Seleccionar el tipo de elemento a
				// utilizar
				//if (evalReferencialSelected == null) {//edcm
				if(!existenEvalReferencialCab(evalRef)){
					o = (MatrizRefConfEnc) iterReferencialCab.next();
					elFactor = new EvalReferencialCab();
				} else {
					
					elFactor = (EvalReferencialCab) iterReferencialCab.next();
				}
				// EvalReferencialCab elFactor = new EvalReferencialCab();
				elFactor.setActivo(true);
				elFactor.setEvalReferencial(new EvalReferencial());
				elFactor.getEvalReferencial().setIdEvalReferencial(
						evalRef.getIdEvalReferencial());

				// Crear o editar el factor
				//if (evalReferencialSelected == null) {//edcm
				if(elFactor.getIdEvalReferencialCab() == null){
					elFactor.setFechaAlta(laFecha);
					elFactor.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					elFactor.setMatrizRefConfEnc(o);
					em.persist(elFactor);
				} else {
					elFactor.setFechaMod(new Date());
					elFactor.setUsuMod(usuarioLogueado.getCodigoUsuario());
					elFactor = em.merge(elFactor);
				}
				// Seleccionar el iterador para trabajar con los detalles
				//if (evalReferencialSelected == null) {//edcm
				if(!existeEvalReferencialDet(elFactor)){
					//RVeron. Usamos el procedimiento que trae solo los ítems activos.
					//iterReferencialDet = o.getMatrizRefConfDets().iterator();
					iterReferencialDet = o.ObtenerMatrizRefConfDetsActivos().iterator();
				} else {
					
					if(elFactor.getEvalReferencialDets().iterator().hasNext())
						iterReferencialDet = elFactor.getEvalReferencialDets().iterator();
					else{
						String sql = "select * from seleccion.eval_referencial_det where id_eval_referencial_cab = "+ elFactor.getIdEvalReferencialCab();
						
						List <EvalReferencialDet> list = em.createNativeQuery(sql,EvalReferencialDet.class).getResultList();
						
						Set<EvalReferencialDet> evalReferencialDets = new HashSet<EvalReferencialDet>(0);
						for (EvalReferencialDet cab : list)
							
							evalReferencialDets.add(cab);
						
						iterReferencialDet = evalReferencialDets.iterator();
					}
					
					
					
				}
				// Iterar
				while (iterReferencialDet.hasNext()) { 
					EvalReferencialDet elItem = null;
					MatrizRefConfDet p = null;

					// Crear o Editar el Item de detalle. Seleccionar el tipo de
					// elemento a utilizar
					//if (evalReferencialSelected == null) {edcm
					if(!existeEvalReferencialDet(elFactor)){
						p = (MatrizRefConfDet) iterReferencialDet.next();
						elItem = new EvalReferencialDet();
					} else {
						elItem = (EvalReferencialDet) iterReferencialDet.next();
					}

					//if (evalReferencialSelected == null) {//edcm
					if(!existeEvalReferencialDet(elFactor)){
						if (!presente.getValor()) {
							p.setPuntaje(new Float("0"));
						}
						elItem.setActivo(true);
						if (p.getPuntaje() != null) {
							elItem.setPuntajeRealizado(p.getPuntaje());
							sumatoriaItems += p.getPuntaje();
						} else {
							elItem.setPuntajeRealizado(0);
						}
						elItem.setEvalReferencialCab(new EvalReferencialCab());
						elItem.getEvalReferencialCab().setIdEvalReferencialCab(
								elFactor.getIdEvalReferencialCab());
						elItem.setFechaAlta(laFecha);
						elItem.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						elItem.setMatrizRefConfDet(new MatrizRefConfDet());
						elItem.getMatrizRefConfDet().setIdMatrizRefConfDet(
								p.getIdMatrizRefConfDet());
						em.persist(elItem);
					} else {
						if (!presente.getValor()) {
							elItem.setPuntajeRealizado(new Long("0"));
						} else {
							elItem.setPuntajeRealizado(diccPuntajesEditado
									.get(elItem.getMatrizRefConfDet()
											.getIdMatrizRefConfDet().toString()));
							sumatoriaItems += diccPuntajesEditado.get(elItem
									.getMatrizRefConfDet()
									.getIdMatrizRefConfDet().toString());
						}

						elItem.setFechaMod(laFecha);
						elItem.setUsuMod(usuarioLogueado.getCodigoUsuario());
						elItem = em.merge(elItem);
					}
				}
				// Se actualiza la sumatoria de sus items
				elFactor.setPuntajeRealizado(sumatoriaItems);
				elFactor = em.merge(elFactor);
				sumatoriaItems = new Float("0");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			nroOrden--;
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_NO_MSG"));
			Transaction.instance().rollback();
			throw new Exception(
					"ERROR EN realizarEvalsFormController.guardarMatrizEvaluacion()");

		}
		return evalRef;
	}
	
	

	public Long getIdPostulacion() {
		return idPostulacion;
	}

	public void setIdPostulacion(Long idPostulacion) {
		this.idPostulacion = idPostulacion;
	}
	
	private Boolean sinValoresNulos(MatrizRefConfDet p) {
		
		if (p.getPuntaje() == null) {
			statusMessages.add(
					Severity.ERROR,"Existen Valores Nulos. Verifique. ");
			return false;
		
		}else
			return true;
	}	

	private Boolean controlarLimitesPuntaje(MatrizRefConfDet p) {
		// Controlar que se encuentre entre el puntaje minimo y maximo
		if (sinValoresNulos(p)) {
			
			if(p.getPuntaje().floatValue() < 0){
				statusMessages.add(
						Severity.ERROR,
						SeamResourceBundle.getBundle().getString(
								"CU88_noPuntajeMenorCero")
								+ " " + p.getDescripcion());
			}
			if (p.getPuntaje().floatValue() < p.getPuntajeMinimo()) {
				statusMessages.add(
						Severity.ERROR,
						SeamResourceBundle.getBundle().getString(
								"CU88_noInferiorPuntajeMinimo")
								+ " " + p.getDescripcion());
				return false;
			}
			
			if (p.getPuntaje().floatValue() > p.getPuntajeMaximo()) {
				statusMessages.add(
						Severity.ERROR,
						SeamResourceBundle.getBundle().getString(
								"CU88_noInferiorPuntajeMaximo")
								+ " " + p.getDescripcion());
				return false;
			}
			
		}else{
			//TIENE VALORES NULOS
			return false;
		}
				
		return true;
	}

	public String refresTipoEval(EvalReferencialTipoeval erte) {
		if (erte != null) {
			if (erte.getDatosEspecificos() != null
					&& erte.getDatosEspecificos().getDescripcion() == null) {
				DatosEspecificos de = em.find(DatosEspecificos.class, erte
						.getDatosEspecificos().getIdDatosEspecificos());
				erte.setDatosEspecificos(de);
				return erte.getDatosEspecificos().getDescripcion();
			} else if (erte.getDatosEspecificos() != null
					&& erte.getDatosEspecificos().getDescripcion() != null) {
				return erte.getDatosEspecificos().getDescripcion();
			}
		}
		return "";
	}
	
	private Boolean precondEvaluacion() {
		/*
		 * Este Mapa almacena los puntajes cargados en la matriz de
		 * 
		 * evaluacion para que puedan ser utilizado a la hora de realizar un
		 * editado de
		 * 
		 * una evaluacion anterior
		 */
		diccPuntajesEditado.clear();
		Integer cantVeces = 0;
		if (esCurricular) {
			presente = AusentePresente.PRESENTE;
		}
		if (presente == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU88_elegirPresenciaAusencia")
					+ ": "
					+ codPostulante);
			return false;
		}
		String elKey = null;
		if (observacion1 != null) {
			if (!observacion1.trim().isEmpty()) {
				if (observacion1.length() > 250) {
					statusMessages
							.add(Severity.ERROR,
									"Superada la cantidad máxima de caracteres (250) para el campo Observación");
					return false;
				}
			}
		}
		if (!presente.getValor()) {
			// setear los puntajes
			return true;
		}

		for (MatrizRefConfEnc o : lMatRefConfEnc) {
			cantVeces = -1; //iniciamos con -1, asi cuando cuente un ingreso de puntaje será Cero
			
			for (MatrizRefConfDet p : o.getMatrizRefConfDets()) {
				if (!p.isActivo())
					continue;
				/*
				 * Se cargar el diccionario de puntajes. Siempre
				 * 
				 * sobreescribe los valores. Siempre está actualizado
				 */
				elKey = p.getIdMatrizRefConfDet().toString();
				diccPuntajesEditado.put(elKey, p.getPuntaje());
				puntajeMinimo = p.getPuntajeMinimo();
				// Recorrer la lista buscando que no se hayan cargado puntajes
				// cuando no es permitido
				if (!o.getSumItemsSN()) {
					if (p.getPuntaje() != null) {
						if(p.getPuntaje().intValue() > puntajeMinimo){
						
							cantVeces++;
							if (cantVeces > 0 ) {//si es mayor a Cero es que se cargaron dos puntajes, cuando solo debería haber uno.. 
								statusMessages
										.add(Severity.ERROR,
												SeamResourceBundle
														.getBundle()
														.getString(
																"CU88_noPuedecargarMasDeUnPuntajePara")
														+ " "
														+ p.getMatrizRefConfEnc()
																.getDescripcion());
								return false;
							}
							if ( cantVeces.intValue() == -1 ) {
								statusMessages.add(Severity.ERROR,
										"Debe cargar por lo menos un puntaje para el Factor: "
												+ " " + o.getDescripcion());
								return false;
							}
							
						}
						if ( cantVeces.intValue() == 0 && !controlarLimitesPuntaje(p)) {
							return false;
						}
					}
				} else {
					if (!controlarLimitesPuntaje(p)) {
						return false;
					}
				}
			}
						
			
		}
		return true;
	}

	private void guardarEvaluadores(EvalReferencial matRef) {
		for (EvalReferencialComis o : lEvaluadores) {
			System.out.println("-- " + matRef.getIdEvalReferencial());
			if (o.getIdEvalReferencialComis() == null) {
				o.setEvalReferencial(matRef);
				em.persist(o);
			}
		}
	}

	/**
	 * Guarda o acutaliza evaluaciones a postulantes
	 * 
	 * @return
	 * @throws Exception
	 */
	public String saveEvaluacion() throws Exception {

		if (lEvaluadores == null
				|| (lEvaluadores != null && lEvaluadores.size() == 0)) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU88_noHayEvaluadores"));
			return "FAIL";
		}
		if (precondEvaluacion()) {
			try {
				
				this.evaldocumentalcab.setIncluido(false);
				em.merge(evaldocumentalcab);
				em.flush();
				
				EvalReferencial evalRef = guardarActualizarEvaluacion();
				guardarEvaluadores(evalRef);
				em.flush();
				
				
				limpiar();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
			} catch (Exception e) {
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_NO_MSG"));
				e.printStackTrace();
				return null;
			}
		} else
			return null;

		return "OK";
	}
	
	


	private void limpiar() {
		if (lEvalReferencialCabEditado != null)
			lEvaluadores.clear();
		if (lEvaluadores != null)
			lEvaluadores.clear();
		if (lMatRefConfEnc != null)
			lMatRefConfEnc.clear();
		presente = null;
		totalPuntos = null;
		totalAlcanzado = null;

	}

	public void cargarComisionSeleccion() {
		if (tipoEvaluacion != null
				&& tipoEvaluacion.getId().equalsIgnoreCase(TIPO_EVAL_COMISION)) {
			Query q = em
					.createQuery("select comisionSeleccionCab from ComisionSeleccionCab comisionSeleccionCab, ComisionGrupo comisionGrupo "
							+ "WHERE comisionGrupo.comisionSeleccionCab = comisionSeleccionCab AND"
							+ " comisionGrupo.concursoPuestoAgr.idConcursoPuestoAgr = "
							+ grupoPuestosController.getIdConcursoPuestoAgr());

			List<ComisionSeleccionCab> lista = q.getResultList();
			idInte = null;
			idEmpTerce = null;
			updateIntegrantes();
			if (lista.size() == 1) {
				comisionSeleccion = lista.get(0).getDescripcion();
				idComisionSeleccion = lista.get(0).getIdComisionSel();
				apagarComiDesc = false;
				apagarComboEmp = true;

			} else {
				comisionSeleccion = "-";
				idComisionSeleccion = null;
				apagarComboEmp = true;
				apagarComiDesc = true;
			}
		} else if (tipoEvaluacion != null
				&& tipoEvaluacion.getId().equalsIgnoreCase(TIPO_EVAL_EMP_TRECE)) {
			comisionSeleccion = "-";
			idComisionSeleccion = null;
			apagarComboEmp = false;
			apagarComiDesc = true;
			idInte = null;
			updateIntegrantes();
		} else {
			comisionSeleccion = "-";
			idComisionSeleccion = null;
			apagarComboEmp = true;
			apagarComiDesc = true;
			idInte = null;
			idEmpTerce = null;
		}
	}

	private List<EmprTercerizada> getEmpresaTerciarizada() {
		Query q = em
				.createQuery("select empTercer from EmprTercerizada empTercer order by empTercer.nombre");
		return q.getResultList();
	}

	public void updateEmpTerce() {
		List<EmprTercerizada> lista = getEmpresaTerciarizada();
		empTereceSelecItem = new ArrayList<SelectItem>();
		buildEmpTerceSelectItem(lista);
		idEmpTerce = null;
		idInte = null;
	}

	private void buildEmpTerceSelectItem(List<EmprTercerizada> lista) {
		if (empTereceSelecItem == null)
			empTereceSelecItem = new ArrayList<SelectItem>();
		else
			empTereceSelecItem.clear();

		empTereceSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (EmprTercerizada o : lista) {
			empTereceSelecItem.add(new SelectItem(o.getIdEmpresaTercerizada(),
					o.getNombre()));
		}
	}

	private List<MatrizRefConfEnc> getTipoEval() {
		Query q = em
				.createQuery("select distinct matrizRefConfEnc from MatrizRefConf matrizRefConf, MatrizRefConfEnc matrizRefConfEnc "
						+ " where matrizRefConfEnc.matrizRefConf = matrizRefConf "
						+ " AND matrizRefConf.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr()
						+ " order by matrizRefConfEnc.nroOrden asc");
		List<MatrizRefConfEnc> lista = q.getResultList();
		LinkedHashMap<String, MatrizRefConfEnc> mapaCache = new LinkedHashMap<String, MatrizRefConfEnc>();
		String elKey = null;
		for (MatrizRefConfEnc o : lista) {
			elKey = o.getDatosEspecificos().getIdDatosEspecificos().toString();
			if (!mapaCache.containsKey(elKey)) {
				mapaCache.put(elKey, o);
			} else {
				if (o.getObligatorioSN()) {
					mapaCache.put(elKey, o);
				}
			}
		}
		List<MatrizRefConfEnc> lResultado = new ArrayList<MatrizRefConfEnc>();

		for (String o : mapaCache.keySet()) {
			
			lResultado.add(mapaCache.get(o));
		}
		return lResultado;
	}

	private void limpiarVariables() {
		lugar = null;
		direccion = null;
		idCiudad = null;
		idDpto = null;
		fechaConvo = null;
		horaDesde = null;
		horaHasta = null;
		observacion3 = null;
		rowConvoSele = -1;
		motivo = null;
		actualizar = false;
	}

	public void updateTipoEval() {
		List<MatrizRefConfEnc> lista = getTipoEval();
		tipoEvalSelecItem = new ArrayList<SelectItem>();
		buildTipoEvalSelectItem(lista);
	}

	private void buildTipoEvalSelectItem(List<MatrizRefConfEnc> lista) {
		if (tipoEvalSelecItem == null)
			tipoEvalSelecItem = new ArrayList<SelectItem>();
		else
			tipoEvalSelecItem.clear();
		if (lista.size() > 0) {
			tipoEvalSelecItem.add(new SelectItem(null, SeamResourceBundle
					.getBundle().getString("opt_select_one")));
		}
		for (MatrizRefConfEnc o : lista) {
			tipoEvalSelecItem.add(new SelectItem(o.getDatosEspecificos()
					.getIdDatosEspecificos(), o.getDatosEspecificos()
					.getDescripcion()));
		}
		if (this.idTipoEvalCerradas == null 
				&& tipoEvalSelecItem != null
				&& tipoEvalSelecItem.size() > 0
				&& tipoEvalSelecItem.get(0) != null) {
			tipoEvalSelecItem.get(0).getValue();
		}
	}

	private List<Persona> getIntegrantes() {
		Query q = null;
		if (tipoEvaluacion != null
				&& tipoEvaluacion.getId().equalsIgnoreCase(TIPO_EVAL_COMISION)) {
			seleccionUtilFormController = (SeleccionUtilFormController) Component
					.getInstance(SeleccionUtilFormController.class, true);

			return seleccionUtilFormController
					.getComisionSelecion(grupoPuestosController
							.getIdConcursoPuestoAgr());

		} else if (tipoEvaluacion != null
				&& tipoEvaluacion.getId().equalsIgnoreCase(TIPO_EVAL_EMP_TRECE)
				&& idEmpTerce != null) {
			q = em.createQuery("select persona from Persona persona, EmpresaPersona empresaPersona"
					+ " WHERE empresaPersona.persona = persona and empresaPersona.emprTercerizada.idEmpresaTercerizada ="
					+ idEmpTerce
					+ "and persona.activo is true order by persona.nombres asc, persona.apellidos asc");

		}
		if (q == null) {
			return new ArrayList<Persona>();
		}
		List<Persona> lista = q.getResultList();
		return lista;
	}

	public void updateIntegrantes() {
		List<Persona> lista = getIntegrantes();
		integrantesSelectItem = new ArrayList<SelectItem>();
		buildIntegrantesSelectItem(lista);
		idInte = null;
	}

	private void buildIntegrantesSelectItem(List<Persona> lista) {
		if (integrantesSelectItem == null)
			integrantesSelectItem = new ArrayList<SelectItem>();
		else
			integrantesSelectItem.clear();

		integrantesSelectItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Persona o : lista) {
			integrantesSelectItem.add(new SelectItem(o.getIdPersona(), o
					.getNombres() + " " + o.getApellidos()));
		}
	}

	private List<Ciudad> getCiudades() {
		Query q = em
				.createQuery("select ciudad from Ciudad ciudad "
						+ "WHERE ciudad.departamento.pais.descripcion ='PARAGUAY' ORDER BY ciudad.descripcion ");

		return q.getResultList();
	}
	
	
	/**
	 * Recupera los departamentos de Paraguay
	 */
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
	}

	private void buildDepartamentoSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(),
					dep.getDescripcion()));
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Departamento> getDepartamentosByPais() {
		String sql = "select dpto.* " + "from general.departamento dpto "
				+ "join general.pais p " + "on p.id_pais = dpto.id_pais "
				+ "where dpto.activo is true "
				+ "and lower(p.descripcion)='paraguay' "
				+ "order by dpto.descripcion";
		List<Departamento> listaDpto = new ArrayList<Departamento>();
		listaDpto = em.createNativeQuery(sql, Departamento.class)
				.getResultList();
		if (listaDpto.size() > 0)
			return listaDpto;

		return new ArrayList<Departamento>();
	}

	/**
	 * Carga el combo de ciudad de acuerdo al departamento seleccionado
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelectItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDpto != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDpto);
			ciudadList.setMaxResults(null);
			return ciudadList.getResultList();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildCiudadSelectItem(List<Ciudad> ciudadList) {
		if (ciudadSelectItem == null)
			ciudadSelectItem = new ArrayList<SelectItem>();
		else
			ciudadSelectItem.clear();

		ciudadSelectItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelectItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}

	public String traerComiEmpTerce(ComisionSeleccionDet comi,
			EmpresaPersona empTerce) {
		String respuesta = null;
		if (comi != null) {
			respuesta = comi.getComisionSeleccionCab().getDescripcion();
		} else if (empTerce != null) {
			respuesta = empTerce.getEmprTercerizada().getNombre();
		}
		if (respuesta == null) {
			respuesta = "-";
		}
		return respuesta;
	}

	public String traerPersona(ComisionSeleccionDet comi,
			EmpresaPersona empTerce) {
		String respuesta = null;
		if (comi != null) {
			respuesta = comi.getPersona().getNombres() + " "
					+ comi.getPersona().getApellidos();
		} else if (empTerce != null) {
			respuesta = empTerce.getPersona().getNombres() + " "
					+ empTerce.getPersona().getApellidos();
		}
		if (respuesta == null) {
			respuesta = "-";
		}
		return respuesta;
	}
	
	public void imprimirEvaluacionActual(){
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("concurso_puesto_agr",this.idConcursoPuestoAgr);
		param.put("desde", lEvalsAbiertas.get(0).getDatosEspecificos().getIdDatosEspecificos());
		//param.put("desde", tipoEvalObj.getIdDatosEspecificos());
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		JasperReportUtils.respondPDF("ListaEvaluacionesAbiertas",	param, false, conn,
				usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
	//METODOS PARA TACHAS_RECLAMOS_MODIF
	private void cargarEvaluadoresTRM() {
		if(lEvaluadores != null && lEvaluadores.size() > 0)
			lEvaluadores.clear();
		
		Long idDatosEspecificos = null;
		if (idDatosEspecificosVer != null) {
			idDatosEspecificos = idDatosEspecificosVer;
		} else {
			MatrizRefConfEnc elFactor = em.find(MatrizRefConfEnc.class,
					idTipoEval);
			tipoEvalObj = elFactor.getDatosEspecificos();
			idDatosEspecificos = tipoEvalObj.getIdDatosEspecificos();
		}
		Query q = em
				.createQuery("select evalReferencialComis from EvalReferencialComis evalReferencialComis where "
						+ " evalReferencialComis.evalReferencial.postulacion.idPostulacion = "
						+ idPostulacion
						+" and evalReferencialComis.usuAlta = 'SYSTEM'"
						+ "and evalReferencial.evalReferencialTipoeval.datosEspecificos.idDatosEspecificos = "
						+ idDatosEspecificos);
		lEvaluadores = q.getResultList();
		
	}
	
	
	private EvalReferencial cargarVerEvaluacionTRM() {
		Long idDatosEspecificos = null;
		if (idDatosEspecificosVer != null) {
			idDatosEspecificos = idDatosEspecificosVer;
			if(idDatosEspecificosVer == 1){
				esCurricular = true;
				presente = AusentePresente.PRESENTE;
			}
			else 
				esCurricular = false;
			
		} else {
			idDatosEspecificos = tipoEvalObj.getIdDatosEspecificos();
			
		}

		Query q = em
				.createQuery("select evalReferencialCab from EvalReferencialCab evalReferencialCab where"
						+ "  evalReferencialCab.usuAlta = 'SYSTEM'"
						+ " and evalReferencialCab.evalReferencial.postulacion.idPostulacion = "
						+ idPostulacion
						+ " and matrizRefConfEnc.datosEspecificos.idDatosEspecificos = "
						+ idDatosEspecificos
						
						);

		List<EvalReferencialCab> lista = q.getResultList();
		if (lista.size() > 0) {
			evalReferencialSelected = em.find(EvalReferencial.class,
					lista.get(0).getEvalReferencial().getIdEvalReferencial());
			
			lEvalReferencialCabEditado = evalReferencialSelected.getEvalReferencialCabs();
			presente = (evalReferencialSelected.isPresente() ? AusentePresente.PRESENTE
					: AusentePresente.AUSENTE);
			totalPuntos = new Float(
					evalReferencialSelected.getPuntajeRealizado()).floatValue();
			totalAlcanzado = new Float(
					evalReferencialSelected.getPorcRealizado()).floatValue();
			observacion1 = evalReferencialSelected.getObservacion();

			String listaIn = "";
			/* Para obtener los puntajes */
			Map<String, Map<String, Float>> mapaCachePuntajes = new HashMap<String, Map<String, Float>>();
			String elKey = null;
			String elKey2 = null;
			for (EvalReferencialCab o : lista) {
				elKey = o.getMatrizRefConfEnc().getIdMatrizRefConfEnc()
						.toString();
				if (!mapaCachePuntajes.containsKey(elKey)) {
					mapaCachePuntajes.put(elKey, new HashMap<String, Float>());
				}
				for (EvalReferencialDet p : lEvalReferencialDet(o
						.getIdEvalReferencialCab())) {
					elKey2 = p.getMatrizRefConfDet().getIdMatrizRefConfDet()
							.toString();
					mapaCachePuntajes.get(elKey).put(elKey2,
							p.getPuntajeRealizado());
				}

				listaIn += ","
						+ o.getMatrizRefConfEnc().getIdMatrizRefConfEnc();
			}
			/**/
			/* Para obtener los elementos evaluados */
			listaIn = listaIn.replaceFirst(",", "");
			q = em.createQuery("select matrizRefConfEnc from MatrizRefConfEnc matrizRefConfEnc,MatrizRefConf matrizRefConf where "
					+ " matrizRefConfEnc.matrizRefConf = matrizRefConf  "
					+ " and matrizRefConf.concursoPuestoAgr.idConcursoPuestoAgr = "
					+ this.idConcursoPuestoAgr
					+ " and matrizRefConfEnc.activo = true "
					+ " and matrizRefConfEnc.matrizRefConf.activo = true "
					+ " and matrizRefConfEnc.idMatrizRefConfEnc IN("
					+ listaIn
					+ ")  and matrizRefConf.tipo ='GRUPO' "
					+ " order by matrizRefConfEnc.nroOrden asc ");

			lMatRefConfEnc = q.getResultList();
			/* Cargando los puntajes */
			for (MatrizRefConfEnc o : lMatRefConfEnc) {
				elKey = o.getIdMatrizRefConfEnc().toString();
				for (MatrizRefConfDet p : o.getMatrizRefConfDets()) {
					elKey2 = p.getIdMatrizRefConfDet().toString();
					if (mapaCachePuntajes.get(elKey) != null
							&& mapaCachePuntajes.get(elKey).get(elKey2) != null)
						p.setPuntaje(mapaCachePuntajes.get(elKey).get(elKey2)
								.floatValue());
				}
			}
			/* Cargar los evaluadores */
			cargarEvaluadoresTRM();
		}else{
			this.presente = null;
			if (lMatRefConfEnc != null)
				lMatRefConfEnc.clear();
			this.sumatoriaFactores = 0;
			this.totalPuntos = new Float (0);
			this.totalAlcanzado = new Float (0);
		}
		

		cargarCabMatrizConfiguradaTRM();
		if (evalReferencialSelected == null)
			sumarPuntos(true);
		else {
			sumarPuntos(false);
		}

		return evalReferencialSelected;
	}
	
	
	
	
	private EvalReferencial generarEvalReferencialTRM() {
		Long idDatosEspecificos = null;
		if (idDatosEspecificosVer != null) {
			idDatosEspecificos = idDatosEspecificosVer;
			
		} else {
			idDatosEspecificos = tipoEvalObj.getIdDatosEspecificos();
			
		}
		Query q = em
				.createQuery("select evalReferencialCab from EvalReferencialCab evalReferencialCab where"
						+ " evalReferencialCab.usuAlta = 'SYSTEM' "
						+ " and  evalReferencialCab.evalReferencial.postulacion.idPostulacion = "
						+ idPostulacion
						+ " and matrizRefConfEnc.datosEspecificos.idDatosEspecificos = "
						+ idDatosEspecificos
						
						);

		List<EvalReferencialCab> lista = q.getResultList();
		
		
		if (lista.size() > 1) {
			evalReferencialSelected = em.find(EvalReferencial.class,
					lista.get(0).getEvalReferencial().getIdEvalReferencial());
			
			if(!evalReferencialSelected.isSiendoEvaluado()){
						
				evalReferencialSelected.setSiendoEvaluado(true);
				em.merge(evalReferencialSelected);
				em.flush();
				
			}
			
			lEvalReferencialCabEditado = evalReferencialSelected.getEvalReferencialCabs();
			
			presente = (evalReferencialSelected.isPresente() ? AusentePresente.PRESENTE
					: AusentePresente.AUSENTE);
			totalPuntos = new Float(
					evalReferencialSelected.getPuntajeRealizado()).floatValue();
			totalAlcanzado = new Float(
					evalReferencialSelected.getPorcRealizado()).floatValue();
			observacion1 = evalReferencialSelected.getObservacion();

			String listaIn = "";
			/* Para obtener los puntajes */
			Map<String, Map<String, Float>> mapaCachePuntajes = new HashMap<String, Map<String, Float>>();
			String elKey = null;
			String elKey2 = null;
			for (EvalReferencialCab o : lista) {
				elKey = o.getMatrizRefConfEnc().getIdMatrizRefConfEnc()
						.toString();
				if (!mapaCachePuntajes.containsKey(elKey)) {
					mapaCachePuntajes.put(elKey, new HashMap<String, Float>());
				}
				for (EvalReferencialDet p : lEvalReferencialDet(o
						.getIdEvalReferencialCab())) {
					elKey2 = p.getMatrizRefConfDet().getIdMatrizRefConfDet()
							.toString();
					mapaCachePuntajes.get(elKey).put(elKey2,
							p.getPuntajeRealizado());
				}

				listaIn += ","
						+ o.getMatrizRefConfEnc().getIdMatrizRefConfEnc();
			}
			/**/
			/* Para obtener los elementos evaluados */
			listaIn = listaIn.replaceFirst(",", "");
			q = em.createQuery("select matrizRefConfEnc from MatrizRefConfEnc matrizRefConfEnc,MatrizRefConf matrizRefConf where "
					+ " matrizRefConfEnc.matrizRefConf = matrizRefConf  "
					+ " and matrizRefConf.concursoPuestoAgr.idConcursoPuestoAgr = "
					+ this.idConcursoPuestoAgr
					+ " and matrizRefConfEnc.idMatrizRefConfEnc IN("
					+ listaIn
					+ ")  and matrizRefConf.tipo ='GRUPO' "
					+ " order by matrizRefConfEnc.nroOrden asc ");

			lMatRefConfEnc = q.getResultList();
			/* Cargando los puntajes */
			for (MatrizRefConfEnc o : lMatRefConfEnc) {
				elKey = o.getIdMatrizRefConfEnc().toString();
				for (MatrizRefConfDet p : o.getMatrizRefConfDets()) {
					elKey2 = p.getIdMatrizRefConfDet().toString();
					if (mapaCachePuntajes.get(elKey) != null
							&& mapaCachePuntajes.get(elKey).get(elKey2) != null)
						p.setPuntaje(mapaCachePuntajes.get(elKey).get(elKey2)
								.floatValue());
				}
			}
			/* Cargar los evaluadores */
			cargarEvaluadores();
		} else {
			
			evalReferencialSelected = exiteEvalReferencial(idPostulacion, idDatosEspecificos);
			
			if (evalReferencialSelected == null) {
				evalReferencialSelected = new EvalReferencial();
				evalReferencialSelected.setNroOrden(this.nroOrden);
				evalReferencialSelected.setPostulacion(em.find(Postulacion.class, this.idPostulacion));
				evalReferencialSelected.setPresente(false);
				evalReferencialSelected.setPuntajeRealizado(0);
				evalReferencialSelected.setPorcRealizado(0);
				evalReferencialSelected.setFechaEvaluacion(new Date());
				evalReferencialSelected.setActivo(true);
				evalReferencialSelected.setUsuAlta(this.usuarioLogueado.getCodigoUsuario());
				evalReferencialSelected.setFechaAlta(new Date());
				evalReferencialSelected.setSiendoEvaluado(true);

				evalReferencialSelected.setEvalReferencialTipoeval(this.traerEncabezado());
				em.persist(evalReferencialSelected);
				em.flush();
				
				
			}
			
			cargarEvaluarPuntaje();
			cargarComisionSeleccion();
			updateEmpTerce();
			updateIntegrantes();
			inicializarTotales();
			cargarCabMatrizConfiguradaTRM();
			
			
		}

		cargarCabMatrizConfiguradaTRM();
		if (evalReferencialSelected == null)
			sumarPuntos(true);
		else {
			sumarPuntos(false);
		}
		
	

		return evalReferencialSelected;
	}
	
	
	private void cargarCabMatrizConfiguradaTRM() {
		/**/
		Query q = em
				.createQuery("select matrizRefConf from MatrizRefConf matrizRefConf where matrizRefConf.tipo ='GRUPO' and "
						+ "matrizRefConf.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ this.idConcursoPuestoAgr);
		List<MatrizRefConf> lista = q.getResultList();
		if (lista.size() == 1) {
			MatrizRefConf matrizRefConf = lista.get(0);
			matriz = matrizRefConf.getDescripcion() == null ? "-"
					: matrizRefConf.getDescripcion().trim();
			puntajeMax = matrizRefConf.getPuntajeMaximo() + "";
		} else {
			matriz = "-";
			puntajeMax = "-";
		}
		/**/
		q = em.createQuery("select datosGrupoPuesto from DatosGrupoPuesto datosGrupoPuesto where "
				+ "datosGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr =  "
				+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DatosGrupoPuesto> lista2 = q.getResultList();
		if (lista2.size() == 1) {
			DecimalFormat fNumero = new DecimalFormat("#0.00");
			DatosGrupoPuesto datosGrupoPuesto = lista2.get(0);
			if (datosGrupoPuesto.getPorMinPorEvaluacion() != null
					&& datosGrupoPuesto.getPorMinPorEvaluacion()) {
				porcMinAplicar = SeamResourceBundle.getBundle().getString(
						"CU88_porCadaEvaluacion");
				idPorcMinAplicar = PORC_MIN_POR_EVAL;
			}

			if (datosGrupoPuesto.getPorMinFinEvaluacion() != null
					&& datosGrupoPuesto.getPorMinFinEvaluacion()) {
				porcMinAplicar = SeamResourceBundle.getBundle().getString(
						"CU88_alFinalizarEvals");
				idPorcMinAplicar = PORC_MIN_AL_FINALIZAR_EVALS;
			}

			if (datosGrupoPuesto.getTerna() != null	&& datosGrupoPuesto.getTerna())
				seleccion = SeamResourceBundle.getBundle().getString(
						"CU88_porTerna");
			if (datosGrupoPuesto.getMerito() != null
					&& datosGrupoPuesto.getMerito())
				seleccion = SeamResourceBundle.getBundle().getString(
						"CU88_porPuntaje");
			if (datosGrupoPuesto.getPorcMinimo() != null) {
				minimoDesc = fNumero.format(datosGrupoPuesto.getPorcMinimo()
						.doubleValue());
			}
		}

	}
	
	
	public void initEvaluarPuntajeTRM() {
		lock.lock();
//		initAbiertas();
		if(!this.evaluacionesModificado(this.idPostulacion)){
			String sqlFuncion = "select seleccion.replicar_evaluaciones("+ idPostulacion +")";
			String retornoFuncion = (String) em.createNativeQuery(sqlFuncion).getSingleResult();
			
			if(retornoFuncion.equals("ok")){
				statusMessages.clear();
				statusMessages.add(Severity.INFO,"Función ejecutada correctamente ");
			}else if (retornoFuncion.equals("error")){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"Función ejecutada con errores ");
			}
				
		}
		
		
		this.grupoPuestosController.setConcursoPuestoAgr(em.find(ConcursoPuestoAgr.class, this.idConcursoPuestoAgr));
		observacion1 = null;
		limpiarTotales();
		
		updateTipoEvalTRM();
		
		lTipoEval = getTipoEvalTRM();
		
		lTipoEvalPasados = new ArrayList<String>();
		totalPostulantes = null;
		
		obtenerDatosGrupoPuesto();
		
		limpiar();
		if (idPostulacion != null) {
			Postulacion postulacion = em.find(Postulacion.class, idPostulacion);
			
		}
		//agregarTodosLosEvaluadores();
		
		evalDocumentalFrom = "/seleccion/tachasReclamosModif/evalPuntajePostulante.seam?"
				+ "idDatosEspecificosVer="+idDatosEspecificosVer
				+ "&idConcursoPuesto="+idConcursoPuestoAgr
				+ "&tabActivo="+tabActivo
				+ "&idPostulacion="+idPostulacion
				+ "&codPostulante="+codPostulante
				+ "&idPersona="+idPersona
				+ "&ver="+ver;
		

				habilitarGuardar = true;
				habilitarVolver = false;
			
			
			
			
			
		evalReferencialSelected = cargarVerEvaluacionTRM();
 
		lock.unlock();
	}
	
	private List<MatrizRefConfEnc> getTipoEvalTRM() {
		Query q = em
				.createQuery("select distinct matrizRefConfEnc from MatrizRefConf matrizRefConf, MatrizRefConfEnc matrizRefConfEnc "
						+ " where matrizRefConfEnc.matrizRefConf = matrizRefConf "
						+ " AND matrizRefConf.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ this.idConcursoPuestoAgr
						+ " order by matrizRefConfEnc.nroOrden asc");
		List<MatrizRefConfEnc> lista = q.getResultList();
		LinkedHashMap<String, MatrizRefConfEnc> mapaCache = new LinkedHashMap<String, MatrizRefConfEnc>();
		String elKey = null;
		for (MatrizRefConfEnc o : lista) {
			elKey = o.getDatosEspecificos().getIdDatosEspecificos().toString();
			if (!mapaCache.containsKey(elKey)) {
				mapaCache.put(elKey, o);
			} else {
				if (o.getObligatorioSN()) {
					mapaCache.put(elKey, o);
				}
			}
		}
		List<MatrizRefConfEnc> lResultado = new ArrayList<MatrizRefConfEnc>();

		for (String o : mapaCache.keySet()) {
			
			lResultado.add(mapaCache.get(o));
		}
		return lResultado;
	}
	
	
	public boolean evaluacionesModificado (Long idPostulacion){
		boolean retorno = false;
		
		String sql = "select count (*) from seleccion.eval_referencial_postulante where usu_alta = 'SYSTEM' and activo = true and id_postulacion = "+idPostulacion;
		
		int cantidad = ((BigInteger) em.createNativeQuery(sql).getSingleResult()).intValue();
		
		if(cantidad >= 1)
			retorno = true;
		
		return retorno;
	}
	
		public String saveEvaluacionTRM() throws Exception {
	
			if (lEvaluadores == null
					|| (lEvaluadores != null && lEvaluadores.size() == 0)) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
						.getString("CU88_noHayEvaluadores"));
				return "FAIL";
			}
			if (precondEvaluacion()) {
				try {
					
									
					EvalReferencial evalRef = guardarActualizarEvaluacionTRM();
					//guardarEvaluadores(evalRef);  
					 
					if(actualizarEvalReferencialPostulanteReplica(idPostulacion).equals("ok"))
						em.flush();
					else
						return null;
					
					limpiar();
					statusMessages.clear();
					statusMessages.add(Severity.INFO, SeamResourceBundle
							.getBundle().getString("GENERICO_MSG"));
				} catch (Exception e) {
					statusMessages.clear();
					statusMessages.add(Severity.INFO, SeamResourceBundle
							.getBundle().getString("GENERICO_NO_MSG"));
					e.printStackTrace();
					return null;
				}
			} else
				return null;
	
			return "OK";
		}
	
	
		private String actualizarEvalReferencialPostulanteReplica (Long idPostulacion){
			String retorno = "ok";
				
			try {
				
				EvalReferencialPostulante eval = new EvalReferencialPostulante();
				
				String sqlBuscarEval = "select * from seleccion.eval_referencial_postulante where usu_alta = 'SYSTEM'  and id_postulacion = "+idPostulacion;
				
				eval = (EvalReferencialPostulante) em.createNativeQuery(sqlBuscarEval, EvalReferencialPostulante.class).getResultList().get(0);
				
				String sqlSumar = "select sum (puntaje_realizado) from seleccion.eval_referencial where usu_alta = 'SYSTEM'  and id_postulacion = " + idPostulacion;
				
				Float puntajeRealizado = ((Float) em.createNativeQuery(sqlSumar).getSingleResult()); 
				
				eval.setPuntajeRealizado(puntajeRealizado);
				eval.setUsuMod(this.usuarioLogueado.getCodigoUsuario());
				eval.setFechaMod(new Date());
				em.merge(eval);
				
			} catch (Exception e) {
				retorno = "Error : "+e;
			}
				
						
			return retorno;
		}



		private EvalReferencial guardarActualizarEvaluacionTRM() throws Exception {
			Date laFecha = new Date();
			EvalReferencial evalRef = null;
			Boolean aprobado = null;
			try {
				EvalReferencialTipoeval evalRefeTipoEval;
				if (lMatRefConfEnc.size() > 0) {
					// En en caso que este ausente el postulante
					sumarPuntos(false);
					setearPorAusencia();
					if (evalReferencialSelected == null)
						// Crear Cabecera. Se crea una cabecera por cada postulante
						evalRef = new EvalReferencial();
					else {
						// Es un editado, no una creación
						evalRef = evalReferencialSelected;
					}
					evalRef.setActivo(true);
					aprobado = calcAprobadoTRM();
					evalRef.setAprobado(aprobado);
					/* Se crea uno por cada Tipo de Evaluacion */
					//evalRefeTipoEval = traerEncabezado();
					evalRefeTipoEval = evalReferencialSelected.getEvalReferencialTipoeval();
					
					if (evalRef.getEvalReferencialTipoeval() == null) {
						evalRefeTipoEval = crearEvalReferencialTipoeval();
					}else
						evalRefeTipoEval = evalReferencialSelected.getEvalReferencialTipoeval();
					
					evalRef.setEvalReferencialTipoeval(evalRefeTipoEval);
					if (evalReferencialSelected == null) {
						evalRef.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						evalRef.setFechaAlta(laFecha);
					} else {
						evalRef.setFechaMod(new Date());
						evalRef.setUsuMod(usuarioLogueado.getCodigoUsuario());
					}
					if (!presente.getValor()) {
						evalRef.setAprobado(false);
					}
					evalRef.setFechaEvaluacion(laFecha);
					evalRef.setNroOrden(++nroOrden);
					evalRef.setObservacion(observacion1);
					evalRef.setPorcRealizado(totalAlcanzado);
					evalRef.setPostulacion(new Postulacion());
					evalRef.getPostulacion().setIdPostulacion(idPostulacion);
					evalRef.setPresente(presente.getValor());
					evalRef.setPuntajeRealizado(totalPuntos);
					evalRef.setSiendoEvaluado(false);
					
					if (evalReferencialSelected == null) {
						//em.persist(evalRef);edcm
					} else {
						evalRef = em.merge(evalRef);
					}
				}
		
				Float sumatoriaItems = new Float("0");
				Iterator iterReferencialCab = null;
				Iterator iterReferencialDet = null;
				//if (evalReferencialSelected == null) {//edcm
				if(!existenEvalReferencialCab(evalRef)){
					iterReferencialCab = lMatRefConfEnc.iterator();
				} else {
					
					
					
					if(evalRef.getEvalReferencialCabs().iterator().hasNext())
						iterReferencialCab = evalRef.getEvalReferencialCabs().iterator();//edcm
					else{
						
						evalRef= em.find(EvalReferencial.class, evalRef.getIdEvalReferencial());
						String sql = "select * from seleccion.eval_referencial_cab where id_eval_referencial = "+ evalRef.getIdEvalReferencial();
						List <EvalReferencialCab> list = em.createNativeQuery(sql,EvalReferencialCab.class).getResultList();
						
						Set<EvalReferencialCab> evalReferencialCabs = new HashSet<EvalReferencialCab>(0);
						for (EvalReferencialCab cab : list)
							
							evalReferencialCabs.add(cab);
						
						iterReferencialCab = evalReferencialCabs.iterator();
					}
						
					
					
				}
				while (iterReferencialCab.hasNext()) {
					EvalReferencialCab elFactor = null;
					MatrizRefConfEnc o = null;
					// Crear o Editar el Factor. Seleccionar el tipo de elemento a
					// utilizar
					//if (evalReferencialSelected == null) {//edcm
					if(!existenEvalReferencialCab(evalRef)){
						o = (MatrizRefConfEnc) iterReferencialCab.next();
						elFactor = new EvalReferencialCab();
					} else {
						
						elFactor = (EvalReferencialCab) iterReferencialCab.next();
					}
					// EvalReferencialCab elFactor = new EvalReferencialCab();
					elFactor.setActivo(true);
					elFactor.setEvalReferencial(new EvalReferencial());
					elFactor.getEvalReferencial().setIdEvalReferencial(
							evalRef.getIdEvalReferencial());
		
					// Crear o editar el factor
					//if (evalReferencialSelected == null) {//edcm
					if(elFactor.getIdEvalReferencialCab() == null){
						elFactor.setFechaAlta(laFecha);
						elFactor.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						elFactor.setMatrizRefConfEnc(o);
						em.persist(elFactor);
					} else {
						elFactor.setFechaMod(new Date());
						elFactor.setUsuMod(usuarioLogueado.getCodigoUsuario());
						elFactor = em.merge(elFactor);
					}
					// Seleccionar el iterador para trabajar con los detalles
					//if (evalReferencialSelected == null) {//edcm
					if(!existeEvalReferencialDet(elFactor)){
						//RVeron. Usamos el procedimiento que trae solo los ítems activos.
						//iterReferencialDet = o.getMatrizRefConfDets().iterator();
						iterReferencialDet = o.ObtenerMatrizRefConfDetsActivos().iterator();
					} else {
						
						if(elFactor.getEvalReferencialDets().iterator().hasNext())
							iterReferencialDet = elFactor.getEvalReferencialDets().iterator();
						else{
							String sql = "select * from seleccion.eval_referencial_det where id_eval_referencial_cab = "+ elFactor.getIdEvalReferencialCab();
							
							List <EvalReferencialDet> list = em.createNativeQuery(sql,EvalReferencialDet.class).getResultList();
							
							Set<EvalReferencialDet> evalReferencialDets = new HashSet<EvalReferencialDet>(0);
							for (EvalReferencialDet cab : list)
								
								evalReferencialDets.add(cab);
							
							iterReferencialDet = evalReferencialDets.iterator();
						}
						
						
						
					}
					// Iterar
					while (iterReferencialDet.hasNext()) { 
						EvalReferencialDet elItem = null;
						MatrizRefConfDet p = null;
		
						// Crear o Editar el Item de detalle. Seleccionar el tipo de
						// elemento a utilizar
						//if (evalReferencialSelected == null) {edcm
						if(!existeEvalReferencialDet(elFactor)){
							p = (MatrizRefConfDet) iterReferencialDet.next();
							elItem = new EvalReferencialDet();
						} else {
							elItem = (EvalReferencialDet) iterReferencialDet.next();
						}
		
						//if (evalReferencialSelected == null) {//edcm
						if(!existeEvalReferencialDet(elFactor)){
							if (!presente.getValor()) {
								p.setPuntaje(new Float("0"));
							}
							elItem.setActivo(true);
							if (p.getPuntaje() != null) {
								elItem.setPuntajeRealizado(p.getPuntaje());
								sumatoriaItems += p.getPuntaje();
							} else {
								elItem.setPuntajeRealizado(0);
							}
							elItem.setEvalReferencialCab(new EvalReferencialCab());
							elItem.getEvalReferencialCab().setIdEvalReferencialCab(
									elFactor.getIdEvalReferencialCab());
							elItem.setFechaAlta(laFecha);
							elItem.setUsuAlta(usuarioLogueado.getCodigoUsuario());
							elItem.setMatrizRefConfDet(new MatrizRefConfDet());
							elItem.getMatrizRefConfDet().setIdMatrizRefConfDet(
									p.getIdMatrizRefConfDet());
							em.persist(elItem);
						} else {
							if (!presente.getValor()) {
								elItem.setPuntajeRealizado(new Long("0"));
							} else {
								elItem.setPuntajeRealizado(diccPuntajesEditado.get(elItem.getMatrizRefConfDet().getIdMatrizRefConfDet().toString()));
								sumatoriaItems += diccPuntajesEditado.get(elItem.getMatrizRefConfDet().getIdMatrizRefConfDet().toString());
							}
		
							elItem.setFechaMod(laFecha);
							elItem.setUsuMod(usuarioLogueado.getCodigoUsuario());
							elItem = em.merge(elItem);
						}
					}
					// Se actualiza la sumatoria de sus items 
					elFactor.setPuntajeRealizado(sumatoriaItems);
					elFactor = em.merge(elFactor);
					sumatoriaItems = new Float("0");
				}
				
				em.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
				nroOrden--;
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
						.getString("GENERICO_NO_MSG"));
				Transaction.instance().rollback();
				throw new Exception(
						"ERROR EN realizarEvalsFormController.guardarMatrizEvaluacion()");
		
			}
			return evalRef;
		}




	private Boolean calcAprobadoTRM() {
		if (!presente.getValor()) {
			return null;
		} else {
			Query q = em
					.createQuery("select datosGrupoPuesto from DatosGrupoPuesto datosGrupoPuesto "
							+ " where datosGrupoPuesto.concursoPuestoAgr = "
							+ idConcursoPuestoAgr);
	
			DatosGrupoPuesto dgp = (DatosGrupoPuesto) q.getSingleResult();
	
			Integer porcAlcanzado = new Integer(totalAlcanzado.intValue() + "");
			if ((dgp.getPorcMinimo().compareTo(porcAlcanzado) == 0)
					|| (dgp.getPorcMinimo().compareTo(porcAlcanzado) < 0)) {
				return true;
			} else {
				return false;
			}
	
		}
	}
private EvalReferencial exiteEvalReferencial(Long idPostulacion,Long idDatosEspecificos){
	String sql =  "select * from seleccion.eval_referencial eval_ref "
			+ "join seleccion.eval_referencial_tipoeval tipoeval on eval_ref.id_eval_referencial_tipoeval = tipoeval.id_eval_referencial_tipoeval "
			+ "where id_postulacion = "+idPostulacion
			+" and tipoeval.id_datos_especificos_tipo_eval =  "+idDatosEspecificos;
	
	List<EvalReferencial> list = em.createNativeQuery(sql,EvalReferencial.class).getResultList(); 
	
	if (list.size() > 0)
		return  list.get(0);
	else
		return null;
}

private boolean existeEvalReferencialDet (EvalReferencialCab evalReferencialCab){
	String sql = "select * from seleccion.eval_referencial_det where id_eval_referencial_cab = "+ evalReferencialCab.getIdEvalReferencialCab();
	
	return em.createNativeQuery(sql,EvalReferencialDet.class).getResultList().size() > 0;
}

private boolean existenEvalReferencialCab (EvalReferencial evalReferencial){
	
	if ( evalReferencial != null && evalReferencial.getIdEvalReferencial() != null){
				
		String sql = "select * from seleccion.eval_referencial_cab where id_eval_referencial = "+ evalReferencial.getIdEvalReferencial();
		List<EvalReferencialCab> lista = em.createNativeQuery(sql,EvalReferencialCab.class).getResultList();
		
		return lista.size() > 0;
	}
	else
		
		return false;
	
	
}
	
	public void cargarEvaluacion(){
		evalReferencialSelected = cargarVerEvaluacionTRM();
			
	}
	
	public void updateTipoEvalTRM() {
		List<MatrizRefConfEnc> lista = getTipoEvalTRM();
		tipoEvalSelecItem = new ArrayList<SelectItem>();
		buildTipoEvalSelectItemTRM(lista);
	}
	
	
	private void buildTipoEvalSelectItemTRM(List<MatrizRefConfEnc> lista) {
		if (tipoEvalSelecItem == null)
			tipoEvalSelecItem = new ArrayList<SelectItem>();
		else
			tipoEvalSelecItem.clear();
		
		for (MatrizRefConfEnc o : lista) {
			tipoEvalSelecItem.add(new SelectItem(o.getDatosEspecificos()
					.getIdDatosEspecificos(), o.getDatosEspecificos()
					.getDescripcion()));
		}
		if (this.idTipoEvalCerradas == null 
				&& tipoEvalSelecItem != null
				&& tipoEvalSelecItem.size() > 0
				&& tipoEvalSelecItem.get(0) != null) {
			tipoEvalSelecItem.get(0).getValue();
		}
	}
	
	
	/**
	 * Distingue entre una nueva evaluacion o una que está en proceso
	 * 
	 * @param col
	 *            La columna que sera devuelta si es una evaluación que está en
	 *            proceso
	 * @param idDatosEspecificos
	 *            El id de la evaluación en proceso
	 * @return
	 */
	public Object initColsAbiertas(Object col, Long idDatosEspecificos) {
		if (idDatosEspecificos.intValue() == idTipoEval.intValue()) {
			return col;
		}
		return null;
	}

	public EvalAbiertasList getlEvalsAbiertasList() {
		return lEvalsAbiertasList;
	}

	public void setlEvalsAbiertasList(EvalAbiertasList lEvalsAbiertasList) {
		this.lEvalsAbiertasList = lEvalsAbiertasList;
	}

	public List<EvalAbiertas> getlEvalsAbiertas() {
		return lEvalsAbiertas;
	}

	public void setlEvalsAbiertas(List<EvalAbiertas> lEvalsAbiertas) {
		this.lEvalsAbiertas = lEvalsAbiertas;
	}

	public List<MatrizRefConfEnc> getlMatRefConfEnc() {
		return lMatRefConfEnc;
	}

	public void setlMatRefConfEnc(List<MatrizRefConfEnc> lMatRefConfEnc) {
		this.lMatRefConfEnc = lMatRefConfEnc;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	public Float getTotalPuntos() {
		DecimalFormat myFormatter = new DecimalFormat("##,##");
		if (totalPuntos != null)
			return new Float( myFormatter.format(totalPuntos));
		else
			return new Float(0);
		
	}

	public String getTotalPuntosFormateado() {
		DecimalFormat myFormatter = new DecimalFormat("##.##");
		
		if (totalPuntos != null)
			return  myFormatter.format(totalPuntos);
		else
			return  "";
		
	}

	public void setTotalPuntos(Float totalPuntos) {
		this.totalPuntos = totalPuntos;
	}

	public String getTotalAlcanzadoFormateado() {
		DecimalFormat myFormatter = new DecimalFormat("##.##");
		if (totalAlcanzado != null)
			return myFormatter.format(totalAlcanzado);
		return "";
	}

	public Float getTotalAlcanzado() {
		return totalAlcanzado;
	}

	public void setTotalAlcanzado(Float totalAlcanzado) {
		this.totalAlcanzado = totalAlcanzado;
	}

	public TipoEvaluacion getTipoEvaluacion() {
		return tipoEvaluacion;
	}

	public void setTipoEvaluacion(TipoEvaluacion tipoEvaluacion) {
		this.tipoEvaluacion = tipoEvaluacion;
	}

	public String getComisionSeleccion() {
		return comisionSeleccion;
	}

	public void setComisionSeleccion(String comisionSeleccion) {
		this.comisionSeleccion = comisionSeleccion;
	}

	public List<SelectItem> getEmpTereceSelecItem() {
		if (empTereceSelecItem == null) {
			updateEmpTerce();
		}
		return empTereceSelecItem;
	}

	public void setEmpTereceSelecItem(List<SelectItem> empTereceSelecItem) {
		this.empTereceSelecItem = empTereceSelecItem;
	}

	public List<SelectItem> getIntegrantesSelectItem() {
		if (integrantesSelectItem == null) {
			updateIntegrantes();
		}
		return integrantesSelectItem;
	}

	public void setIntegrantesSelectItem(List<SelectItem> integrantesSelectItem) {
		this.integrantesSelectItem = integrantesSelectItem;
	}

	public Long getIdEmpTerce() {
		return idEmpTerce;
	}

	public void setIdEmpTerce(Long idEmpTerce) {
		this.idEmpTerce = idEmpTerce;
	}

	public Long getIdInte() {
		return idInte;
	}

	public void setIdInte(Long idInte) {
		this.idInte = idInte;
	}

	public List<SelectItem> getCiudadSelectItem() {
		return ciudadSelectItem;
	}

	public void setCiudadSelectItem(List<SelectItem> ciudadSelectItem) {
		this.ciudadSelectItem = ciudadSelectItem;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public String getCodPostulante() {
		return codPostulante;
	}

	public void setCodPostulante(String codPostulante) {
		this.codPostulante = codPostulante;
	}

	public String getTipoEvalDesc() {
		return tipoEvalDesc;
	}

	public void setTipoEvalDesc(String tipoEvalDesc) {
		this.tipoEvalDesc = tipoEvalDesc;
	}

	public AusentePresente getPresente() {
		return presente;
	}

	public void setPresente(AusentePresente presente) {
		this.presente = presente;
	}

	public List<EvalReferencialComis> getlEvaluadores() {
		return lEvaluadores;
	}

	public void setlEvaluadores(List<EvalReferencialComis> lEvaluadores) {
		this.lEvaluadores = lEvaluadores;
	}

	public Boolean getApagarComiDesc() {
		return apagarComiDesc;
	}

	public void setApagarComiDesc(Boolean apagarComiDesc) {
		this.apagarComiDesc = apagarComiDesc;
	}

	public Boolean getApagarComboEmp() {
		return apagarComboEmp;
	}

	public void setApagarComboEmp(Boolean apagarComboEmp) {
		this.apagarComboEmp = apagarComboEmp;
	}

	public String getMatriz() {
		return matriz;
	}

	public void setMatriz(String matriz) {
		this.matriz = matriz;
	}

	public String getPuntajeMax() {
		return puntajeMax;
	}

	public void setPuntajeMax(String puntajeMax) {
		this.puntajeMax = puntajeMax;
	}

	public String getPorcMinAplicar() {
		return porcMinAplicar;
	}

	public void setPorcMinAplicar(String porcMinAplicar) {
		this.porcMinAplicar = porcMinAplicar;
	}

	public String getSeleccion() {
		return seleccion;
	}

	public void setSeleccion(String seleccion) {
		this.seleccion = seleccion;
	}

	public String getMinimoDesc() {
		return minimoDesc;
	}

	public void setMinimoDesc(String minimoDesc) {
		this.minimoDesc = minimoDesc;
	}

	public List<SelectItem> getTipoEvalSelecItem() {
		return tipoEvalSelecItem;
	}

	public void setTipoEvalSelecItem(List<SelectItem> tipoEvalSelecItem) {
		this.tipoEvalSelecItem = tipoEvalSelecItem;
	}

	public Long getIdTipoEval() {
		return idTipoEval;
	}

	public void setIdTipoEval(Long idTipoEval) {
		this.idTipoEval = idTipoEval;
	}

	public Boolean getBloquearComboTipoEval() {
		return bloquearComboTipoEval;
	}

	public void setBloquearComboTipoEval(Boolean bloquearComboTipoEval) {
		this.bloquearComboTipoEval = bloquearComboTipoEval;
	}

	public String getObservacion1() {
		return observacion1;
	}

	public void setObservacion1(String observacion1) {
		this.observacion1 = observacion1;
	}

	public EvalCerradasList getlEvalsCerradasList() {
		return lEvalsCerradasList;
	}

	public void setlEvalsCerradasList(EvalCerradasList lEvalsCerradasList) {
		this.lEvalsCerradasList = lEvalsCerradasList;
	}

	public List<EvalAbiertas> getlEvalsCerradas() {
		return lEvalsCerradas;
	}

	public void setlEvalsCerradas(List<EvalAbiertas> lEvalsCerradas) {
		this.lEvalsCerradas = lEvalsCerradas;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Long getIdTipoEvalCerradas() {
		return idTipoEvalCerradas;
	}

	public void setIdTipoEvalCerradas(Long idTipoEvalCerradas) {
		this.idTipoEvalCerradas = idTipoEvalCerradas;
	}

	public String getObservacion3() {
		return observacion3;
	}

	public void setObservacion3(String observacion3) {
		this.observacion3 = observacion3;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public Date getFechaConvo() {
		return fechaConvo;
	}

	public void setFechaConvo(Date fechaConvo) {
		this.fechaConvo = fechaConvo;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	// public String getHoraDesde() {
	// return horaDesde;
	// }
	//
	// public void setHoraDesde(String horaDesde) {
	// this.horaDesde = horaDesde;
	// }
	//
	// public String getMinutoDesde() {
	// return minutoDesde;
	// }
	//
	// public void setMinutoDesde(String minutoDesde) {
	// this.minutoDesde = minutoDesde;
	// }
	//
	// public String getHoraHasta() {
	// return horaHasta;
	// }
	//
	// public void setHoraHasta(String horaHasta) {
	// this.horaHasta = horaHasta;
	// }

	// public String getMinutoHasta() {
	// return minutoHasta;
	// }
	//
	// public void setMinutoHasta(String minutoHasta) {
	// this.minutoHasta = minutoHasta;
	// }

	public GrupoPuestosController getGrupoPuestosController() {
		return grupoPuestosController;
	}

	public void setGrupoPuestosController(
			GrupoPuestosController grupoPuestosController) {
		this.grupoPuestosController = grupoPuestosController;
	}

	public Boolean getVer() {
		return ver;
	}

	public void setVer(Boolean ver) {
		this.ver = ver;
	}

	public List<EvalReferencialConvocatoria> getlEvalRefConvo() {
		return lEvalRefConvo;
	}

	public void setlEvalRefConvo(List<EvalReferencialConvocatoria> lEvalRefConvo) {
		this.lEvalRefConvo = lEvalRefConvo;
	}

	public String getHoraDesde() {
		if (rowConvoSele != -1 && lEvalRefConvo != null
				&& lEvalRefConvo.size() > rowConvoSele)
			if (lEvalRefConvo.get(rowConvoSele) != null
					&& lEvalRefConvo.get(rowConvoSele).getHoraDesde() != null) {
				return sdfHORA.format(lEvalRefConvo.get(rowConvoSele)
						.getHoraDesde());
			}
		return null;
	}

	public void setHoraDesde(String horaDesde) {
		this.horaDesde = horaDesde;
	}

	public String getHoraHasta() {
		if (rowConvoSele != -1 && lEvalRefConvo != null
				&& lEvalRefConvo.size() > rowConvoSele)
			if (lEvalRefConvo.get(rowConvoSele) != null
					&& lEvalRefConvo.get(rowConvoSele).getHoraHasta() != null) {
				return sdfHORA.format(lEvalRefConvo.get(rowConvoSele)
						.getHoraHasta());
			}
		return null;
	}
	
	public void ponerTipoEval(Long elId) {
		setIdTipoEvalCerradas(elId);

	}

	public void setHoraHasta(String horaHasta) {
		this.horaHasta = horaHasta;
	}

	public Boolean getActualizar() {
		return actualizar;
	}

	public void setActualizar(Boolean actualizar) {
		this.actualizar = actualizar;
	}

	public EvalsReferenciales getEvalsRefsRadio() {
		return evalsRefsRadio;
	}

	public void setEvalsRefsRadio(EvalsReferenciales evalsRefsRadio) {
		this.evalsRefsRadio = evalsRefsRadio;
	}

	public Boolean getMostrarColDisca() {
		return mostrarColDisca;
	}

	public void setMostrarColDisca(Boolean mostrarColDisca) {
		this.mostrarColDisca = mostrarColDisca;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public String getConDiscaDesc() {
		return conDiscaDesc;
	}

	public void setConDiscaDesc(String conDiscaDesc) {
		this.conDiscaDesc = conDiscaDesc;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public Set<EvalReferencialCab> getlEvalReferencialCabEditado() {
		return lEvalReferencialCabEditado;
	}

	public void setlEvalReferencialCabEditado(
			Set<EvalReferencialCab> lEvalReferencialCabEditado) {
		this.lEvalReferencialCabEditado = lEvalReferencialCabEditado;
	}

	public float getSumatoriaFactores() {
		return sumatoriaFactores;
	}

	public void setSumatoriaFactores(float sumatoriaFactores) {
		this.sumatoriaFactores = sumatoriaFactores;
	}

	public Boolean getMostrarModal() {
		return mostrarModal;
	}

	public void setMostrarModal(Boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}

	public Boolean getContinuarSgteTarea() {
		return continuarSgteTarea;
	}

	public void setContinuarSgteTarea(Boolean continuarSgteTarea) {
		this.continuarSgteTarea = continuarSgteTarea;
	}

	public String getTextoModalSgteTarea() {
		return textoModalSgteTarea;
	}

	public void setTextoModalSgteTarea(String textoModalSgteTarea) {
		this.textoModalSgteTarea = textoModalSgteTarea;
	}

	public Boolean getHayEvalObliAdelante() {
		return hayEvalObliAdelante;
	}

	public void setHayEvalObliAdelante(Boolean hayEvalObliAdelante) {
		this.hayEvalObliAdelante = hayEvalObliAdelante;
	}

	public Long getIdDatosEspecificosVer() {
		return idDatosEspecificosVer;
	}

	public void setIdDatosEspecificosVer(Long idDatosEspecificosVer) {
		this.idDatosEspecificosVer = idDatosEspecificosVer;
	}

	public Boolean getBtnSgteEval() {
		return btnSgteEval;
	}

	public void setBtnSgteEval(Boolean btnSgteEval) {
		this.btnSgteEval = btnSgteEval;
	}

	public Boolean getPresenteBoolean() {
		return presenteBoolean;
	}

	public void setPresenteBoolean(Boolean presenteBoolean) {
		if (presenteBoolean != null) {
			if (presenteBoolean) {
				presente = AusentePresente.PRESENTE;
			} else {
				presente = AusentePresente.AUSENTE;
			}
		}
		this.presenteBoolean = presenteBoolean;
	}

	public String getTabActivo() {
		return tabActivo;
	}

	public void setTabActivo(String tabActivo) {
		this.tabActivo = tabActivo;
	}

	public void activarTab(String activar) {
		this.tabActivo = activar;

	}

	public Boolean getEsDesierto() {
		return esDesierto;
	}

	public void setEsDesierto(Boolean esDesierto) {
		this.esDesierto = esDesierto;
	}

	public Long getIdPersonaPostulante() {
		return idPersonaPostulante;
	}

	public void setIdPersonaPostulante(Long idPersonaPostulante) {
		this.idPersonaPostulante = idPersonaPostulante;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}
	
	public EvalDocumentalCab getEvaldocumentalcab() {
		return evaldocumentalcab;
	}

	public void setEvaldocumentalcab(EvalDocumentalCab evaldocumentalcab) {
		this.evaldocumentalcab = evaldocumentalcab;
	}
	
	public String getEvalDocumentalFrom() {
		return evalDocumentalFrom;
	}

	public void setEvalDocumentalFrom(String evalDocumentalFrom) {
		this.evalDocumentalFrom = evalDocumentalFrom;
	}
	public List<EvalReferencialPostulante> getListaEmpatados() {
		return listaEmpatados;
	}

	public void setListaEmpatados(List<EvalReferencialPostulante> listaEmpatados) {
		this.listaEmpatados = listaEmpatados;
	}
	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	
	public boolean isHabilitarVolver() {
		return habilitarVolver;
	}

	public void setHabilitarVolver(boolean habilitarVolver) {
		this.habilitarVolver = habilitarVolver;
	}
	
	
	public void habilitarRegistro(){
		if(habilitarGuardar){
			this.evaldocumentalcab.setIncluido(false);
			em.merge(evaldocumentalcab);
			em.flush();
			this.evalReferencialSelected.setSiendoEvaluado(false);
			em.merge(evalReferencialSelected);
			em.flush();
		
		}
		
	}
	
	public boolean deshabilitarEvaluarPostulante (EvalAbiertas evalAbiertas){
		boolean retorno = true;
		etiquetaEvaluar = "Evaluado por " + evalAbiertas.getEvalDocumentalCab().getUsuMod();
		
		if(evalAbiertas == null)
			retorno = false;
		else{
			
			if(evalAbiertas.getEvalDocumentalCab() == null){
				
				retorno = false;
				etiquetaEvaluar = "Evaluar";
			}else {
				if (evalAbiertas.getEvalDocumentalCab().getIncluido() == null){
					retorno = false;
					etiquetaEvaluar = "Evaluar";
				}else {
					if(!evalAbiertas.getEvalDocumentalCab().getIncluido()){
						retorno = false;
						etiquetaEvaluar = "Evaluar";
					}else
						if(evalAbiertas.getEvalDocumentalCab().getUsuMod().equals(usuarioLogueado.getCodigoUsuario())){
							retorno = false;
							etiquetaEvaluar = "Evaluar";
						}
							
				}
			}
				
		}		
	return retorno;
		
	}
	
	public Long getIdEvalDocumentalCab() {
		return idEvalDocumentalCab;
	}

	public void setIdEvalDocumentalCab(Long idEvalDocumentalCab) {
		this.idEvalDocumentalCab = idEvalDocumentalCab;
	}
	
	public Long getIdDatosEspecificos() {
		return idDatosEspecificos;
	}

	public void setIdDatosEspecificos(Long idDatosEspecificos) {
		this.idDatosEspecificos = idDatosEspecificos;
	}
	
	public DatosEspecificos getTipoEvalObj() {
		return tipoEvalObj;
	}

	public void setTipoEvalObj(DatosEspecificos tipoEvalObj) {
		this.tipoEvalObj = tipoEvalObj;
	}
	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public String getEvalDocumentalFromCIO() {
		return evalDocumentalFromCIO;
	}

	public void setEvalDocumentalFromCIO(String evalDocumentalFromCIO) {
		this.evalDocumentalFromCIO = evalDocumentalFromCIO;
	}
	
	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(
			List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}
	
	public Boolean getCambioEstado() {
		return cambioEstado;
	}

	public void setCambioEstado(Boolean cambioEstado) {
		this.cambioEstado = cambioEstado;
	}
	
	public Long getIdDpto() {
		return idDpto;
	}

	public void setIdDpto(Long idDpto) {
		this.idDpto = idDpto;
	}

	public Boolean getEsCurricular() {
		return esCurricular;
	}

	public void setEsCurricular(Boolean esCurricular) {
		this.esCurricular = esCurricular;
	}

	public String getEtiquetaEvaluar() {
		return etiquetaEvaluar;
	}

	public void setEtiquetaEvaluar(String etiquetaEvaluar) {
		this.etiquetaEvaluar = etiquetaEvaluar;
	}
	
	
	
	


}
