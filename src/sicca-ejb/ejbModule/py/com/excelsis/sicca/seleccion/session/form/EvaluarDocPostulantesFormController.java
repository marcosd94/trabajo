package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.mapping.Array;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.ActividadEnum;
import py.com.excelsis.sicca.dto.EvalDocumentalDetDTO;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalDocumentalDet;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.MatrizDocConfigEnc;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Parentesco;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PostulacionAdjuntos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.EvalDocumentalCabHome;
import py.com.excelsis.sicca.seleccion.session.EvalDocumentalCabList;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.Tab6VistaPreliminarFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("evaluarDocPostulantesFormController")
public class EvaluarDocPostulantesFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	EvalDocumentalCabHome evalDocumentalCabHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	EvalDocumentalCabList evalDocumentalCabList;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;
	@In(create = true)
	Tab7VistaPrePostulacionActualFC tab7VistaPrePostulacionActualFC;
	@In(create = true)
	Tab6VistaPreliminarFormController tab6VistaPreliminarFormController;

	ConcursoPuestoAgr concursoPuestoAgr;
	EvalDocumentalCab evalDocumentalCab;
	public AdmFecRecPosFC admFecRecPosFC;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private Observacion obs;
	private Integer estadoGrupoDesierto;
	private Date fechaDesde;
	private Date fechaHasta;
	private String usuPostulacionFiltro;
	private Boolean evaluadoFiltro;
	private Boolean aprobadoFiltro;
	private String observacion;
	private String direccionFisica;
	private Integer cantEvaluados;
	private Boolean cuentaConPostulantes;
	private Boolean todosEvaluados = false;
	private List<MatrizDocConfigEnc> listaMatrizDocCOnfigEnc;
	private List<MatrizDocConfigDet> listaMatrizDocCOnfigDet;
	private List<Postulacion> listaPostulacion;
	private List<PostulacionAdjuntos> listaDocumentosAdjPostulante;
	private List<EvalDocumentalDet> listaEvalDocumentalDet;
	private String mensajeModal;
	private String view451 = "/home.xtml";
	private String mensaje;
	private String aprobo;
	private String codActividad;
	private Boolean mostrarModal = false;
	public Boolean continuaValSgteTarea = false;
	private Boolean isEdit;
	
	private Boolean bloquearTodo = false;
	private Boolean disminuirPuestos = false;
	private Boolean declararDesierto = false;
	
	//RVeron, 20141022
	private ReentrantLock lock = new ReentrantLock();

	private String from;
	private List<EvalDocumentalCab> evalDocumentalCabListado = new ArrayList<EvalDocumentalCab>();
	private SeguridadUtilFormController seguridadUtilFormController;

	private List<Parentesco> listDetails;
	private List<Parentesco> listDetails2;

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
	}

	private void validarEstado() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concursoPuestoAgr == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.validaEstado(
				concursoPuestoAgr.getIdConcursoPuestoAgr(),
				"A EVALUAR DOCUMENTOS")) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	/**
	 * MÈtodo que inicializa los datos
	 */
	public String init() {
		cuentaConPostulantes = true;
		todosEvaluados = false;
		FacesContext fc = FacesContext.getCurrentInstance();
		if(isEdit==null){
			isEdit=fc.getExternalContext().getRequestParameterMap().get("isEdit").equalsIgnoreCase("true");
		}
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		evalDocumentalCab = new EvalDocumentalCab();
		evalDocumentalCab = evalDocumentalCabHome.getInstance();
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		obs = new Observacion();
		estadoGrupoDesierto = obtenerEstadoGrupo("ESTADOS_GRUPO",
				"GRUPO DESIERTO");
		String separador = File.separator;
		direccionFisica = separador
				+ "SICCA"
				+ separador
				+ anho
				+ separador
				+ "OEE"
				+ separador
				+ concurso.getConfiguracionUoCab().getIdConfiguracionUo()
				+ separador
				+ concurso.getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + separador
				+ concurso.getIdConcurso() + separador
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();

		if (concurso != null) {
			validarOee(concurso);
			if(isEdit)
				validarEstado();
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null)
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}

		if (cantidadPostulantes(concursoPuestoAgr.getIdConcursoPuestoAgr()) <= 1) {
			declararDesierto();
			reponerCategorias();
			bloquearTodo = true;
			statusMessages
					.add(Severity.ERROR,
							"Este grupo se declara desierto por no contar con Postulantes suficientes.");
			return "desierto";
		}
		bloquearTodo = false;

		concursoPuestoAgr.setFechaEvalDocDesde(new Date());
		concursoPuestoAgrHome.setInstance(concursoPuestoAgr);
		String res = concursoPuestoAgrHome.update();
		if (res.equals("updated")) {
			buscarMatrizDocConfigEnc();
			buscarMatrizDocConfigDet();
			buscarPostulacion();
			String resp = guardarDatos();
			if (!resp.equals("persisted"))
				return null;

		} else

			return null;

		search();
		buscarCantEvaluados();
		todosFueronEvaluados();
		from = "seleccion/evalDocumentosPostulantes/EvalDocumentalCabList";
		if (mensaje != null && !mensaje.trim().isEmpty()) {
			if (aprobo.equals("SI"))
				mensaje += " "
						+ SeamResourceBundle.getBundle().getString(
								"CU41_msg_aprobado");
			else
				mensaje += " "
						+ SeamResourceBundle.getBundle().getString(
								"CU41_msg_noaprobado");
			statusMessages.add(Severity.INFO, mensaje);
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			mensaje = null;
		}
		return "ok";
	}

	public void reponerCategorias() {
		reponerCategoriasController.reponerCategorias(concursoPuestoAgr,
				"GRUPO", "CONCURSO");
	}

	public String descripParent(Long idParentesco) {
		String q = "select de.descripcion from seleccion.datos_especificos de "
				+ "where de.id_datos_especificos = (select p.id_datos_especificos "
				+ "from general.parentesco p where p.id_parentesco="
				+ idParentesco + ")";
		String descripcion = em.createNativeQuery(q).getSingleResult()
				.toString();
		return descripcion;
	}

	@SuppressWarnings("unchecked")
	public void listarParentesco(Long idPersona) {

		if (idPersona != null) {
			String q = "select * from general.parentesco p where p.activo='true'and  p.id_persona =  "
					+ "(select id_persona from seleccion.persona_postulante, seleccion.datos_especificos de  "
					+ "where id_persona_postulante="
					+ idPersona
					+ " and de.valor_alf like'C' "
					+ "and de.id_datos_especificos = p.id_datos_especificos)";

			String q2 = "select * from general.parentesco p where p.activo='true'and  p.id_persona =  "
					+ "(select id_persona from seleccion.persona_postulante, seleccion.datos_especificos de  "
					+ "where id_persona_postulante="
					+ idPersona
					+ " and de.valor_alf like'A' "
					+ "and de.id_datos_especificos = p.id_datos_especificos)";
			listDetails = new ArrayList<Parentesco>();
			List<Parentesco> listDetailsAux = new ArrayList<Parentesco>();

			listDetailsAux = em.createNativeQuery(q, Parentesco.class).getResultList();
			Parentesco e;
			
			if(listDetailsAux.isEmpty()){
				
			}else
				listDetails = listDetailsAux;
			
			listDetails2 = new ArrayList<Parentesco>();
			listDetailsAux = new ArrayList<Parentesco>();
			
			listDetailsAux = em.createNativeQuery(q2, Parentesco.class).getResultList();
			
			if(listDetailsAux.isEmpty()){
				
			}else
				listDetails2 = listDetailsAux;
			
			
		}

	}

	public void imprimir(EvalDocumentalCab evalDocumentalCab) throws Exception {
		Long id = evalDocumentalCab.getPostulacion().getPersonaPostulante()
				.getIdPersonaPostulante();
		/*
		 * tab7VistaPrePostulacionActualFC.setIdPersonaPostulante(id);
		 * tab7VistaPrePostulacionActualFC.init2();
		 * tab7VistaPrePostulacionActualFC.pdf();
		 */

		tab6VistaPreliminarFormController.setFromCU("88");
		tab6VistaPreliminarFormController.setIdPostulacion(evalDocumentalCab
				.getPostulacion().getIdPostulacion());// MODIFICADO RV
		// tab6VistaPreliminarFormController.setPersona(evalDocumentalCab.getPostulacion().getPersonaPostulante().getPersona());
		tab6VistaPreliminarFormController.pdf();
	}
	
	
	
	

	public Integer cantidadPostulantes(Long idGrupo) {
		String sql = "select post.* from seleccion.postulacion post "
				+ "where post.activo is true "
				+ "and post.usu_cancelacion is null "
				+ "and post.id_concurso_puesto_agr = " + idGrupo;
		List<Postulacion> lista = new ArrayList<Postulacion>();
		lista = em.createNativeQuery(sql, Postulacion.class).getResultList();
		if (lista == null)
			return 0;
		else
			return lista.size();
	}

	public void declararDesierto() {
		/* Actualizar el estado del Grupo: */
		cambiarEstadoGrupo(concursoPuestoAgr);
		/* Actualizar el ESTADO de los PUESTOS: */
		cambiarEstadoPuestos(traerGrupoPuestoDet(concursoPuestoAgr
				.getIdConcursoPuestoAgr()));
		obs.setConcurso(concurso);
		if(Utiles.vacio(observacion))
			observacion = "Se declarÛ desierto.";
		else
			observacion = "Desierto. " + observacion;
		obs.setObservacion(observacion);
		obs.setFechaAlta(new Date());
		obs.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		obs.setUsuMod(usuarioLogueado.getCodigoUsuario());
		obs.setIdTaskInstance(jbpmUtilFormController
				.getIdTaskInstanceActual(concursoPuestoAgr
						.getIdProcessInstance()));
		em.persist(obs);
		// Se pasa a la siguiente tarea
//		try {
//			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
//			jbpmUtilFormController.setActividadActual(ActividadEnum.EVALUACION_DOCUMENTAL);
//			jbpmUtilFormController.setTransition("end");
//
//			if (jbpmUtilFormController.nextTask())
//				em.flush();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		/* Enviar mails a los integrantes de la ComisiÛn de SelecciÛn */
		enviarMailComision(concursoPuestoAgr);
		/* Enviar mail al Postulante (si hubiere) para el Grupo */
		if (admFecRecPosFC == null) {
			admFecRecPosFC = (py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC) Component
					.getInstance(AdmFecRecPosFC.class, true);
		}
		admFecRecPosFC.enviarMail2(concursoPuestoAgr);
		admFecRecPosFC.insertarPublicacionPortal(concursoPuestoAgr
				.getIdConcursoPuestoAgr(), concursoPuestoAgr.getConcurso()
				.getIdConcurso(), admFecRecPosFC.genTextoPublicacion2());
		// continuaValSgteTarea = true;

	}

	public void logicaDisminuirPuestos(Integer cantPostulantes, String elFrom) {
		Integer cantVacancias = cantVacancias();
		mostrarModal = false;
		if (cantVacancias.intValue() >= cantPostulantes.intValue()) {
			Integer cantDisminuir = (cantVacancias.intValue() - cantPostulantes
					.intValue()) + 1;
			mensajeModal = "Debe sacar "
					+ cantDisminuir
					+ " puesto/s del grupo por no contar con la cantidad suficiente de Postulantes.<br/>"
					+ "øDesea sacar los puestos ahora? ";
			viewDisminuirPuestos(concursoPuestoAgr.getIdConcursoPuestoAgr(),
					cantVacancias, cantPostulantes, elFrom);
			mostrarModal = true;
			statusMessages
					.add(Severity.ERROR,
							"Debe sacar "
									+ cantDisminuir
									+ " puesto/s del grupo por no contar con la cantidad suficiente de Postulantes.");
		} else
			view451 = "/home.xtml";

	}

	public Integer cantVacancias() {
		Query q;
		if(concursoPuestoAgr.getConcurso().getPromocion()){
			q = em
					.createQuery("select count(PromocionConcursoAgr) from PromocionConcursoAgr PromocionConcursoAgr "
							+ "where PromocionConcursoAgr.activo is true and PromocionConcursoAgr.concursoPuestoAgr.idConcursoPuestoAgr = "
							+ concursoPuestoAgr.getIdConcursoPuestoAgr());
		}else{
			q = em
					.createQuery("select count(ConcursoPuestoDet) from ConcursoPuestoDet ConcursoPuestoDet "
							+ "where ConcursoPuestoDet.activo is true and ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = "
							+ concursoPuestoAgr.getIdConcursoPuestoAgr());
		}
		
		Object cant = q.getSingleResult();
		System.out.println(cant.getClass());
		
		Integer retorno = ((Long) cant).intValue();
		return retorno;
	}

	public String viewDisminuirPuestos(Long grupo, Integer cantVacancias,
			Integer cantPostulantes, String from) {
		view451 = "/seleccion/admDisPue/admDisPue451.seam?idConcursoPuestoAgr="
				+ grupo + "&cantPostulantes=" + cantPostulantes
				+ "&cantVacancias=" + cantVacancias + "&from=" + from
				+ "&codActividad=" + codActividad;
		return view451;
	}

	private void todosFueronEvaluados() {
		String cad = "select eval_cab.* "
				+ "from seleccion.eval_documental_cab eval_cab "
				+ "join seleccion.concurso_puesto_agr agr  "
				+ "on agr.id_concurso_puesto_agr = eval_cab.id_concurso_puesto_agr  "
				+ "where eval_cab.activo is true "
				+ "and eval_cab.evaluado is true "
				+ "and eval_cab.tipo = 'EVALUACION DOCUMENTAL' "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();

		List<EvalDocumentalCab> lista = new ArrayList<EvalDocumentalCab>();
		lista = em.createNativeQuery(cad, EvalDocumentalCab.class)
				.getResultList();
		if (lista.size() > 0) {
			if (lista.size() == cantEvaluados)
				todosEvaluados = true;
			else
				todosEvaluados = false;
		}

	}

	public void search() {
		String query = getQuery();
		evalDocumentalCabListado = evalDocumentalCabList.listaResultados(query);
	}

	public String getQuery() {
		String query = "select evalDocumentalCab from EvalDocumentalCab evalDocumentalCab "
				+ "join evalDocumentalCab.postulacion postulacion "
				+ "join evalDocumentalCab.concursoPuestoAgr agr "
				+ "where postulacion.activo is true "
				+ "and postulacion.usuCancelacion is null "
				+ "and evalDocumentalCab.tipo = 'EVALUACION DOCUMENTAL' "
				+ "and agr.idConcursoPuestoAgr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		
		if (usuPostulacionFiltro != null && !usuPostulacionFiltro.trim().isEmpty())
			query += " and postulacion.usuPostulacion like '%"+ usuPostulacionFiltro.toUpperCase() + "%'";
		
		if (evaluadoFiltro != null){
			 if (evaluadoFiltro){
				 query += " and evalDocumentalCab.evaluado = true ";
			 }else{
				 query += " and evalDocumentalCab.evaluado is null ";
			 	 aprobadoFiltro = null;
			 }
		}
		
		if (aprobadoFiltro != null){
			 if (aprobadoFiltro)
				 query += " and evalDocumentalCab.aprobado = true ";
			 else
				 query += " and evalDocumentalCab.aprobado = false ";
		}
			
		
		if (fechaDesde != null || fechaHasta != null) {
			SimpleDateFormat formatoDesde = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");
			SimpleDateFormat formatoHasta = new SimpleDateFormat("yyyy-MM-dd 23:59:59.0");
			if( fechaDesde != null )
				query += " and evalDocumentalCab.fechaEvaluacion >= '" + formatoDesde.format(fechaDesde) +"'";
			if( fechaHasta == null ) fechaHasta = new Date();

			query += " and evalDocumentalCab.fechaEvaluacion <= '" + formatoHasta.format(fechaHasta) +"'";
		}
		if (observacion != null && !observacion.trim().isEmpty())
			query += "lower(evalDocumentalCab.observacion) like lower(concat('%', concat("
					+ observacion + ",'%')))";

		return query;

	}

	@SuppressWarnings("unchecked")
	private void buscarCantEvaluados() {
		String cad = "select eval_cab.* "
				+ "from seleccion.eval_documental_cab eval_cab  "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = eval_cab.id_postulacion "
				+ "where eval_cab.activo is true  "
				+ "and eval_cab.evaluado is true  "
				+ "and post.activo is true  "
				+ "and eval_cab.tipo = 'EVALUACION DOCUMENTAL' "
				+ "and post.usu_cancelacion is null "
				+ "and eval_cab.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		List<EvalDocumentalCab> lista = new ArrayList<EvalDocumentalCab>();
		lista = em.createNativeQuery(cad, EvalDocumentalCab.class)
				.getResultList();
		if (lista.size() > 0)
			cantEvaluados = lista.size();
		else
			cantEvaluados = 0;
	}

	public void searchAll() {
		evalDocumentalCabList.setIdConcursoPuestoAgr(concursoPuestoAgrHome
				.getInstance().getIdConcursoPuestoAgr());
		fechaDesde = null;
		fechaHasta = null;
		usuPostulacionFiltro = null;
		evaluadoFiltro= null;
		aprobadoFiltro = null;
		observacion = null;
		String query = getQuery();
		evalDocumentalCabListado = evalDocumentalCabList.listaResultados(query);
		
	}

	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {

		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}

	/**
	 * Recupera la cabera de la matriz de configuracion de documentos
	 */
	@SuppressWarnings("unchecked")
	private void buscarMatrizDocConfigEnc() {
		String sql = "select matriz_enc.* "
				+ "from seleccion.matriz_doc_config_enc matriz_enc "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = matriz_enc.id_concurso_puesto_agr "
				+ "where matriz_enc.activo is true "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();

		listaMatrizDocCOnfigEnc = new ArrayList<MatrizDocConfigEnc>();
		listaMatrizDocCOnfigEnc = em.createNativeQuery(sql,
				MatrizDocConfigEnc.class).getResultList();
	}

	/**
	 * Recupera los detalles de la matriz de configuracion de documentos
	 */
	@SuppressWarnings("unchecked")
	private void buscarMatrizDocConfigDet() {
		String sql = "select * from ("
				+ "select matriz_det.* from "
				+ "seleccion.matriz_doc_config_det matriz_det "
				+ "join seleccion.matriz_doc_config_enc matriz_enc "
				+ "on matriz_det.id_matriz_doc_config_enc = matriz_enc.id_matriz_doc_config_enc "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = matriz_enc.id_concurso_puesto_agr "
				+ "where matriz_enc.activo is true "
				+ "and matriz_det.evaluacion_doc is true "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " union "
				+ " select matriz_det.* from seleccion.matriz_doc_config_det matriz_det  "
				+ " join seleccion.matriz_doc_config_enc matriz_enc on matriz_det.id_matriz_doc_config_enc = matriz_enc.id_matriz_doc_config_enc  "
				+ " join seleccion.matriz_doc_config_grupos matriz_grupos on matriz_grupos.id_matriz_doc_config_grupos = matriz_det.id_matriz_doc_config_grupos "
				+ " and matriz_grupos.evaluacion_doc = true "
				+ " join seleccion.concurso_puesto_agr agr on agr.id_concurso_puesto_agr = matriz_enc.id_concurso_puesto_agr  "
				+ " where matriz_enc.activo is true and matriz_det.agrupado = true "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " ) as detalles order by detalles.nro_orden";

		listaMatrizDocCOnfigDet = new ArrayList<MatrizDocConfigDet>();
		listaMatrizDocCOnfigDet = em.createNativeQuery(sql,
				MatrizDocConfigDet.class).getResultList();
	}

	/**
	 * Busca las postulaciones correspondiente al grupo
	 */
	@SuppressWarnings("unchecked")
	private void buscarPostulacion() {
		String sql = "select post.* "
				+ "from seleccion.postulacion post "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = post.id_concurso_puesto_agr "
				+ "where post.activo is true "
				+ "and post.usu_cancelacion is null "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		listaPostulacion = new ArrayList<Postulacion>();
		listaPostulacion = em.createNativeQuery(sql, Postulacion.class)
				.getResultList();
	}

	private Boolean estaPostulacion(Long id) {
		String sql = "select cab.* from seleccion.eval_documental_cab cab "
				+ "join seleccion.postulacion post on post.id_postulacion = cab.id_postulacion "
				+ "where post.activo is true "
				+ "and post.usu_cancelacion is null "
				+ "and cab.tipo = 'EVALUACION DOCUMENTAL' "
				+ "and cab.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and post.id_postulacion = " + id;
		List<EvalDocumentalCab> lista = new ArrayList<EvalDocumentalCab>();
		lista = em.createNativeQuery(sql, EvalDocumentalCab.class)
				.getResultList();
		if (lista.isEmpty())
			return false;
		return true;
	}

	/**
	 * Guarda datos en la tabla eval_documental_cab y eval_documental_det
	 * 
	 * @return
	 */
	private String guardarDatos() {
		String resultado = "persisted";
		for (Postulacion p : listaPostulacion) {
			if (!estaPostulacion(p.getIdPostulacion())) {
				evalDocumentalCab = new EvalDocumentalCab();
				evalDocumentalCab.setActivo(true);
				evalDocumentalCab.setTipo("EVALUACION DOCUMENTAL");
				evalDocumentalCab.setPostulacion(p);
				evalDocumentalCab.setConcursoPuestoAgr(concursoPuestoAgr);
				evalDocumentalCabHome.setInstance(evalDocumentalCab);
				try {
					resultado = evalDocumentalCabHome.persist();
				} catch (Exception e) {
					e.printStackTrace();
				}

				for (MatrizDocConfigDet det : listaMatrizDocCOnfigDet) {
					EvalDocumentalDet evalDocumentalDet = new EvalDocumentalDet();
					evalDocumentalDet.setActivo(true);
					evalDocumentalDet
							.setEvalDocumentalCab(evalDocumentalCabHome
									.getInstance());
					evalDocumentalDet.setFechaAlta(new Date());
					evalDocumentalDet.setMatrizDocConfigDet(det);
					evalDocumentalDet.setUsuAlta(usuarioLogueado
							.getCodigoUsuario());
					em.persist(evalDocumentalDet);
					em.flush();
				}

				listaDocumentosAdjPostulante = new ArrayList<PostulacionAdjuntos>();
				listaDocumentosAdjPostulante = buscarDocumentosAdjPostulante(p);
				listaEvalDocumentalDet = new ArrayList<EvalDocumentalDet>();
				listaEvalDocumentalDet = buscarDocumentalDet(evalDocumentalCabHome
						.getInstance());
				actualizarDatos();
				evalDocumentalCabHome.clearInstance();
			}
			statusMessages.clear();

		}
		return resultado;
	}

	/**
	 * Actualiza la tabla eval_documental_det teniendo en cuenta el tipo de
	 * documento
	 */
	private void actualizarDatos() {
		for (EvalDocumentalDet d : listaEvalDocumentalDet) {
			Boolean esta = false;
			DatosEspecificos datosEspEval = new DatosEspecificos();
			datosEspEval = d.getMatrizDocConfigDet().getDatosEspecificos();
			for (PostulacionAdjuntos adj : listaDocumentosAdjPostulante) {
				DatosEspecificos datosEspAdj = new DatosEspecificos();
				datosEspAdj = adj.getDocumento().getDatosEspecificos();
				if (datosEspAdj.getIdDatosEspecificos().equals(
						datosEspEval.getIdDatosEspecificos())) {
					esta = true;
					d.setAprobadoConDocumentos(true);
					em.merge(d);
					em.flush();
				}
			}
			if (!esta) {
				d.setAprobadoConDocumentos(false);
				em.merge(d);
				em.flush();
			}
		}

	}

	/**
	 * Recupera los adjuntos del postulante
	 * 
	 * @param postulacion
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<PostulacionAdjuntos> buscarDocumentosAdjPostulante(
			Postulacion postulacion) {
		try {
			String sql = "select adj.* "
					+ "from seleccion.postulacion_adjuntos adj "
					+ "join seleccion.postulacion postulacion "
					+ "on postulacion.id_postulacion = adj.id_postulacion "
					+ "where adj.activo is true "
					+ "and postulacion.id_postulacion = "
					+ postulacion.getIdPostulacion();

			return em.createNativeQuery(sql, PostulacionAdjuntos.class)
					.getResultList();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<EvalDocumentalDet> buscarDocumentalDet(
			EvalDocumentalCab evalDocumentalCab) {
		try {
			String sql = "select documental_det.* "
					+ "from seleccion.eval_documental_det documental_det "
					+ "join seleccion.eval_documental_cab documental_cab "
					+ "on documental_cab.id_eval_documental_cab = documental_det.id_eval_documental_cab "
					+ "where documental_det.activo is true "
					+ "and documental_cab.id_eval_documental_cab = "
					+ evalDocumentalCab.getIdEvalDocumentalCab();

			return em.createNativeQuery(sql, EvalDocumentalDet.class)
					.getResultList();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private Integer countAprobados() {
		Query q = em
				.createQuery("select count(EvalDocumentalCab) from EvalDocumentalCab EvalDocumentalCab "
						+ "where EvalDocumentalCab.aprobado is true and  EvalDocumentalCab.tipo ='EVALUACION DOCUMENTAL' "
						+ "AND EvalDocumentalCab.postulacion.usuCancelacion is null and EvalDocumentalCab.postulacion.fechaCancelacion is null "
						+ "and EvalDocumentalCab.postulacion.activo is true and EvalDocumentalCab.activo is true and  EvalDocumentalCab.postulacion.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ concursoPuestoAgr.getIdConcursoPuestoAgr());
		try {
			Integer count = ((Long) q.getSingleResult()).intValue();
			return count;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public Integer obtenerEstadoGrupo(String dominio, String descAbrev) {
		Query q = em
				.createQuery("Select Referencias from Referencias Referencias "
						+ "where Referencias.activo is true and Referencias.dominio = '"
						+ dominio + "' and Referencias.descAbrev ='"
						+ descAbrev + "'  ");
		List<Referencias> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getValorNum();
		}
		return null;
	}

	public EstadoDet obtenerEstadosVarios(String detDesc, String cabDesc) {
		Query q = em
				.createQuery("Select EstadoDet from EstadoDet EstadoDet "
						+ "where EstadoDet.activo is true and EstadoDet.descripcion = '"
						+ detDesc + "' "
						+ "and EstadoDet.estadoCab.descripcion='" + cabDesc
						+ "' ");
		List<EstadoDet> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public EstadoCab obtenerEstadosCabecera(String cabDesc) {
		Query q = em
				.createQuery("Select EstadoCab from EstadoCab EstadoCab "
						+ "where EstadoCab.activo is true and EstadoCab.descripcion = '"
						+ cabDesc + "' ");
		List<EstadoCab> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public void cambiarEstadoGrupo(ConcursoPuestoAgr grupo) {
		grupo.setEstado(estadoGrupoDesierto);
		grupo.setUsuMod(usuarioLogueado.getCodigoUsuario());
		grupo.setFechaMod(new Date());
		grupo = em.merge(grupo);
	}

	public void cambiarEstadoPuestos(List<ConcursoPuestoDet> lista) {
		EstadoCab estadoCab = obtenerEstadosCabecera("VACANTE");
		for (ConcursoPuestoDet concursoPuesto : lista) {
			// Actualizar el Puesto
			concursoPuesto.getPlantaCargoDet().setEstadoCab(estadoCab);
			concursoPuesto.getPlantaCargoDet().setEstadoDet(null);
			concursoPuesto.getPlantaCargoDet().setUsuMod(
					usuarioLogueado.getCodigoUsuario());
			concursoPuesto.getPlantaCargoDet().setFechaMod(new Date());
			concursoPuesto.setPlantaCargoDet(em.merge(concursoPuesto
					.getPlantaCargoDet()));
			concursoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
			concursoPuesto.setFechaMod(new Date());
			concursoPuesto.setActivo(true);
			EstadoDet estado = new EstadoDet();
			estado.setIdEstadoDet(obtenerEstadosVarios("DESIERTO", "CONCURSO")
					.getIdEstadoDet());
			concursoPuesto.setEstadoDet(estado);
			concursoPuesto.setActivo(true); //tiene que ser true para que aparezca en el historial
			concursoPuesto = em.merge(concursoPuesto);
		}
	}

	public List<ConcursoPuestoDet> traerGrupoPuestoDet(Long idGrupo) {
		Query q = em
				.createQuery("select ConcursoPuestoDet from ConcursoPuestoDet ConcursoPuestoDet"
						+ " where ConcursoPuestoDet.activo is true "
						+ "and ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr =  "
						+ idGrupo);
		return q.getResultList();
	}

	public AdmFecRecPosFC getAdmFecRecPosFC() {
		return admFecRecPosFC;
	}

	public void setAdmFecRecPosFC(AdmFecRecPosFC admFecRecPosFC) {
		this.admFecRecPosFC = admFecRecPosFC;
	}

	public Boolean getContinuaValSgteTarea() {
		return continuaValSgteTarea;
	}

	public void setContinuaValSgteTarea(Boolean continuaValSgteTarea) {
		this.continuaValSgteTarea = continuaValSgteTarea;
	}

	public Boolean getDeclararDesierto(Integer canAprobados, Integer cantVacancias,
			String elFrom) {

		if (canAprobados.intValue() >= 0 && canAprobados.intValue() < 2) {
			/* Declarar DESIERTO el Grupo: */
			/* Actualizar el estado del Grupo: */
			cambiarEstadoGrupo(concursoPuestoAgr, "ESTADOS_GRUPO",
					"GRUPO DESIERTO", true);
			/* Actualizar el ESTADO de los PUESTOS: */
			cambiarEstadoPuestos(traerGrupoPuestoDet(concursoPuestoAgr
					.getIdConcursoPuestoAgr()));
			/* Enviar mails a los integrantes de la ComisiÛn de SelecciÛn */
			enviarMailComision(concursoPuestoAgr);
			/* Enviar mail al Postulante (si hubiere) para el Grupo */
			if (admFecRecPosFC == null) {
				admFecRecPosFC = (py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC) Component
						.getInstance(AdmFecRecPosFC.class, true);
			}
			admFecRecPosFC.enviarMail2(concursoPuestoAgr);
			admFecRecPosFC.insertarPublicacionPortal(
					concursoPuestoAgr.getIdConcursoPuestoAgr(),
					concursoPuestoAgr.getConcurso().getIdConcurso(),
					admFecRecPosFC.genTextoPublicacion2());
			continuaValSgteTarea = true;
			declararDesierto = true;
			em.flush();
		} else if (canAprobados.intValue() <= cantVacancias.intValue()) {
			logicaDisminuirPuestos(canAprobados, elFrom);
			continuaValSgteTarea = false;
		} else {
			continuaValSgteTarea = true;
			declararDesierto = false;
		}
		return continuaValSgteTarea;
	}

	public Boolean logica(Integer canAprobados, Integer cantVacancias,
			String elFrom) {

		if (canAprobados.intValue() > 1
				&& canAprobados.intValue() <= cantVacancias.intValue()) {
			logicaDisminuirPuestos(canAprobados, elFrom);
			continuaValSgteTarea = false;
		} else {
			continuaValSgteTarea = true;
		}
		return continuaValSgteTarea;
	}

	public void enviarMailComision(ConcursoPuestoAgr concursoPuestoAgr) {
	
		if (admFecRecPosFC == null) {
			admFecRecPosFC = (AdmFecRecPosFC) Component.getInstance(
					AdmFecRecPosFC.class, true);
		}
		List<ComisionSeleccionDet> lDestinatarios = admFecRecPosFC
				.traerDestinatarios(concursoPuestoAgr.getIdConcursoPuestoAgr());
		String dirEmail = null;
		for (ComisionSeleccionDet o : lDestinatarios) {
			dirEmail = o.getPersona().getEMail();
			String asunto = "Declarar Desierto el Concurso o hacer Prorroga por ExcepciÛn ñ con AprobaciÛn de la SFP";
			String texto = "";
			try {
				texto = texto
						+ "<p align=\"justify\"> <b> Estimado(a) Integrante de la Comisi&oacute;n de Selecci&oacute;n    "
						+ "<p align=\"justify\">Le comunicamos que el Grupo "
						+ concursoPuestoAgr.getDescripcionGrupo()
						+ " del Concurso "
						+ concursoPuestoAgr.getConcurso().getNombre()
						+ " ha sido declarado Desierto porque no ha reunido la cantidad de Postulantes suficientes."
						+ "</p>"
						+ "<p></p>"
						+ "<p> Atentamente,</p> "
						+ "<b>"
						+ concursoPuestoAgr.getConcurso()
								.getConfiguracionUoCab()
								.getDenominacionUnidad() + "</p></b>";

				usuarioPortalFormController.enviarMails(dirEmail, texto,
						asunto, null);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private Integer cantEvaluacionesCerradas() {
		String sql = "select distinct(cab.*) "
				+ "from seleccion.eval_documental_cab cab "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = cab.id_postulacion "
				+ "where (cab.evaluado is null or cab.evaluado is false) "
				+ "and cab.tipo = 'EVALUACION DOCUMENTAL' "
				+ "and cab.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and post.activo is true";

		List<EvalDocumentalCab> lista = new ArrayList<EvalDocumentalCab>();
		lista = em.createNativeQuery(sql, EvalDocumentalCab.class)
				.getResultList();
		if (lista == null)
			return 0;
		else
			return lista.size();
	}
	
	public boolean tareaCerrada(){
		
		Referencias ref = new Referencias();
		ref = referenciasUtilFormController.getReferencia(
				"ESTADOS_GRUPO", "A EVALUAR DOCUMENTOS");
		
		em.refresh(this.concursoPuestoAgr);
		//ConcursoPuestoAgr aux = em.find(ConcursoPuestoAgr.class, this.concursoPuestoAgr.getIdConcursoPuestoAgr());
		//em.refresh(aux);
		
		
		if (concursoPuestoAgr.getEstado().equals(ref.getValorNum()))
			return false;
		else
			return true;
		
	}

	public String nextTask() {
		//rveron, 20141022. Se agregÛ el lock y se movieron segmentos de cÛdigo para agruparlos en los try-catch
		try {
			lock.lock();
			
			/* verifica que la Actividad no haya sido cerrada por otro usuario mediante el boton Siguiente Tarea */
			if(tareaCerrada())
			{
				statusMessages.add(Severity.INFO,
						"El Grupo ya se encuentra en la Siguiente Actividad. Verifique.");
				this.continuaValSgteTarea = false;
				
				lock.unlock();
				return "next";
			}
			
			/* Existe 0 o 1 postulante */
			if (cantidadPostulantes(concursoPuestoAgr.getIdConcursoPuestoAgr()) <= 1) {
				declararDesierto();
				reponerCategorias();
				bloquearTodo = true;
				statusMessages
						.add(Severity.ERROR,
								"Este grupo se declara desierto por no contar con Postulantes suficientes.");
				lock.unlock();
				return "desierto";
			}
			/* Que se hayan cerrado las evaluaciones de todos los postulantes */
			if (cantEvaluacionesCerradas() > 0) {
				statusMessages.add(Severity.ERROR,
						"Debe cerrar las evaluaciones de todos los Postulantes.");
				lock.unlock();
				return null;
			}

			
		//try {
			Integer cantVacancias = cantVacancias();
			Integer cantidadAprobados = countAprobados();
			/* Cantidad de Postulantes que aprobaron */
			if (cantidadAprobados == null || cantidadAprobados <= 1) {
				declararDesierto();
				reponerCategorias();
				bloquearTodo = true;
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Este grupo se declara desierto por no contar con la cantidad suficiente de Postulantes aprobados.");
				lock.unlock();
				return "desierto";
			}
			if (cantVacancias == null
					|| (cantidadAprobados != null && cantidadAprobados > cantVacancias
							.intValue())) {
				mostrarModal = false;
				continuaValSgteTarea = true;

			} else {
				mostrarModal = true;
				logica(countAprobados(), cantVacancias(),
						"seleccion/evalDocumentosPostulantes/EvalDocumentalCabList");

			}

			if (continuaValSgteTarea) {
				// Original EN CASO DE QUE SEA SGTE TAREA

				if (!Utiles.vacio(observacion)) {
					obs.setConcurso(concurso);
					obs.setObservacion(observacion);
					obs.setFechaAlta(new Date());
					obs.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					obs.setUsuMod(usuarioLogueado.getCodigoUsuario());
					obs.setIdTaskInstance(jbpmUtilFormController
							.getIdTaskInstanceActual(concursoPuestoAgr
									.getIdProcessInstance()));
					em.persist(obs);
				}

				Referencias ref = new Referencias();
				ref = referenciasUtilFormController.getReferencia(
						"ESTADOS_GRUPO", "EVALUACION DOCUMENTAL");
				if (ref != null) {
					concursoPuestoAgr.setEstado(ref.getValorNum());
					// concursoPuestoAgr.setFechaEvalDocHasta(new Date());
					em.merge(concursoPuestoAgr);
					PlantaCargoDet cargoDet = new PlantaCargoDet();
					cargoDet = buscarPuesto();
					if (cargoDet != null) {
						EstadoDet estadoDet = new EstadoDet();
						estadoDet = seleccionUtilFormController
								.buscarEstadoDet("concurso",
										"evaluacion documental");
						if (estadoDet != null) {
							cargoDet.setEstadoDet(estadoDet);
							em.merge(cargoDet);
							List<ConcursoPuestoDet> listaPuestosDetActualizar = new ArrayList<ConcursoPuestoDet>();
							listaPuestosDetActualizar = traerGrupoPuestoDet(concursoPuestoAgr
									.getIdConcursoPuestoAgr());
							if (listaPuestosDetActualizar != null
									&& listaPuestosDetActualizar.size() > 0) {
								for (ConcursoPuestoDet actual : listaPuestosDetActualizar) {
									actual.setEstadoDet(estadoDet);
									em.merge(actual);
								}
							}
						}
					}

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			lock.unlock();
			return null;
		}

		if (continuaValSgteTarea) {

			try {
				
				// Envia email a la lista de postulantes
				List<EvalDocumentalCab> listaEnvio = new ArrayList<EvalDocumentalCab>();
				listaEnvio = buscarTodaslasCab();
				
				for (EvalDocumentalCab envio : listaEnvio) {
					enviarEmails(envio.getPostulacion().getPersonaPostulante(),
							envio.getAprobado(), envio.getObservacion());
				}
				
				// Se pasa a la siguiente tarea
				jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
				jbpmUtilFormController
						.setActividadActual(ActividadEnum.EVALUACION_DOCUMENTAL);
				jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.ELABORAR_PUBLICACION_LISTA_LARGA);
				jbpmUtilFormController.setTransition("next");

				if (jbpmUtilFormController.nextTask())
					em.flush();
				
				lock.unlock();
				return "next";
				
				
			} catch (Exception e) {
				// TODO: handle exception
				lock.unlock();
				return null;
				
			}
			
		} else {
			lock.unlock();
			return "noValida";
		}

	}

	@SuppressWarnings("unchecked")
	private PlantaCargoDet buscarPuesto() {
		String sql = "select distinct(cargo_det.*) "
				+ "from planificacion.planta_cargo_det cargo_det "
				+ "join seleccion.concurso_puesto_det puesto_det "
				+ "on puesto_det.id_planta_cargo_det = cargo_det.id_planta_cargo_det "
				+ "join seleccion.concurso_puesto_agr agr  "
				+ "on agr.id_concurso_puesto_agr = puesto_det.id_concurso_puesto_agr "
				+ "where cargo_det.activo is true and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();

		List<PlantaCargoDet> listaPlanta = new ArrayList<PlantaCargoDet>();
		listaPlanta = em.createNativeQuery(sql, PlantaCargoDet.class)
				.getResultList();
		if (listaPlanta.size() > 0)
			return listaPlanta.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<EvalDocumentalCab> buscarTodaslasCab() {
		String sql = "select doc_cab.* "
				+ "from seleccion.eval_documental_cab doc_cab "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = doc_cab.id_concurso_puesto_agr "
				+ "where doc_cab.activo is true "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		List<EvalDocumentalCab> listaEval = new ArrayList<EvalDocumentalCab>();
		listaEval = em.createNativeQuery(sql, EvalDocumentalCab.class)
				.getResultList();
		return listaEval;
	}

	public void enviarEmails(PersonaPostulante persPost, Boolean aprob, String observacion) {
		String dirEmail = persPost.getEMail();
		String asunto = "Resultado de EvaluaciÛn Documental";
		String _obs = "";
		String valorApr = null;
		String mensaje;
		if (observacion != null && !observacion.trim().isEmpty()) {
			_obs = "<p> Resaltamos la siguiente Observaci&oacute;n al respecto:  </p> "
					+ "<p>"
					+ observacion
					+ " </p>";
		}
		if (aprob){
			valorApr = "s&iacute; cumple";
			
			
			mensaje = "<p> <b> Estimado(a) "
				+ encodingHTML(persPost.getNombres())
				+ " "
				+ encodingHTML(persPost.getApellidos())
				+ " </b>,</p> "
				+ "<p> Le comunicamos que Usted fue admitido en el "+concurso.getDatosEspecificosTipoConc().getDescripcion()+" debido a que "
				+ valorApr
				+ " con los requisitos documentales del "
				+ "Proceso de Evaluaci&oacute;n Documental para el Puesto <b>"
				+ encodingHTML(concursoPuestoAgr.getDescripcionGrupo())
				+ "</b> del <b>"
				+ encodingHTML(concurso.getNombre())
				+ "</b>. </p> "
				+ _obs
				+"<p> En breve nos comunicaremos con Usted para indicarle los pr&oacute;ximos pasos del Concurso: reuni&oacute;n informativa, aplicaciones de evaluaciones, test y entrevistas, en los casos que corresponda seg&uacute;n el perfil. </p>"
				+"<p><b> Atentamente,</b> </p><p><b>"
				+ encodingHTML(concurso.getConfiguracionUoCab()
						.getDenominacionUnidad()) + ".</b> </p>";
		
		}
		else{
			valorApr = "<u>NO CUMPLE</u>";
			
			mensaje = "<p> <b> Estimado(a) "
					+ encodingHTML(persPost.getNombres())
					+ " "
					+ encodingHTML(persPost.getApellidos())
					+ " </b>,</p> "
					+ "<p> Le comunicamos que Usted "
					+ valorApr
					+ " con los requisitos documentales del "
					+ "Proceso de Evaluaci&oacute;n Documental realizado por la Comisi&oacute;n de Selecci&oacute;n para el Puesto <b>"
					+ encodingHTML(concursoPuestoAgr.getDescripcionGrupo())
					+ "</b> del Concurso <b>"
					+ encodingHTML(concurso.getNombre())
					+ "</b>. Por tanto, le informamos que su participaci&oacute;n en el "+concurso.getDatosEspecificosTipoConc().getDescripcion()+" ha llegado hasta esta etapa. Agradecemos su participaci&oacute;n y le invitamos a seguir postulando en otros cargos que ser&aacute;n llamados en diversos Organismos y Entidades del Estado. </p> "
					+ _obs
					+"<p><b> Atentamente,</b> </p><p><b>"
					+ encodingHTML(concurso.getConfiguracionUoCab()
							.getDenominacionUnidad()) + ".</b> </p>";
		}
		
				
		

		try {
			usuarioPortalFormController.enviarMails(dirEmail, mensaje, asunto,
					null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String encodingHTML(String input) {
		String original = "·‡‰ÈËÎÌÏÔÛÚˆ˙˘¸Ò¡¿ƒ…»ÀÕÃœ”“÷⁄Ÿ‹—";
		String[] encoding = { "&aacute;", "&agrave;", "&auml;", "&eacute;",
				"&egrave;", "&euml;", "&iacute;", "&igrave;", "&iuml;",
				"&oacute;", "&ograve;", "&ouml;", "&uacute;", "&ugrave;",
				"&uuml;", "&ntilde;", "&Aacute;", "&Agrave;", "&Auml;",
				"&Eacute;", "&Egrave;", "&Euml;", "&Iacute;", "&Igrave;",
				"&Iuml;", "&Oacute;", "&Ograve;", "&Ouml;", "&Uacute;",
				"&Ugrave;", "&Uuml;", "&Ntilde;" };
		String output = input;
		for (int i = 0; i < original.length(); i++) {
			output = output.replace(original.substring(i, i + 1), encoding[i]);
		}
		return output;
	}

	public void cambiarEstadoGrupo(ConcursoPuestoAgr grupo, String dominio,
			String descAbrev, Boolean activo) {
		grupo.setEstado(obtenerEstadoGrupo(dominio, descAbrev));
		grupo.setUsuMod(usuarioLogueado.getCodigoUsuario());
		grupo.setFechaMod(new Date());
		grupo.setActivo(activo);
		grupo = em.merge(grupo);
	}

	public EstadoDet obtenerEstadoPuesto(String detDesc, String cabDesc) {
		Query q = em
				.createQuery("Select EstadoDet from EstadoDet EstadoDet "
						+ "where EstadoDet.activo is true and EstadoDet.descripcion = '"
						+ detDesc + "' "
						+ "and EstadoDet.estadoCab.descripcion='" + cabDesc
						+ "' ");
		List<EstadoDet> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public List<MatrizDocConfigEnc> getListaMatrizDocCOnfigEnc() {
		return listaMatrizDocCOnfigEnc;
	}

	public void setListaMatrizDocCOnfigEnc(
			List<MatrizDocConfigEnc> listaMatrizDocCOnfigEnc) {
		this.listaMatrizDocCOnfigEnc = listaMatrizDocCOnfigEnc;
	}

	public List<MatrizDocConfigDet> getListaMatrizDocCOnfigDet() {
		return listaMatrizDocCOnfigDet;
	}

	public void setListaMatrizDocCOnfigDet(
			List<MatrizDocConfigDet> listaMatrizDocCOnfigDet) {
		this.listaMatrizDocCOnfigDet = listaMatrizDocCOnfigDet;
	}

	public List<Postulacion> getListaPostulacion() {
		return listaPostulacion;
	}

	public void setListaPostulacion(List<Postulacion> listaPostulacion) {
		this.listaPostulacion = listaPostulacion;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public EvalDocumentalCab getEvalDocumentalCab() {
		return evalDocumentalCab;
	}

	public void setEvalDocumentalCab(EvalDocumentalCab evalDocumentalCab) {
		this.evalDocumentalCab = evalDocumentalCab;
	}

	public List<PostulacionAdjuntos> getListaDocumentosAdjPostulante() {
		return listaDocumentosAdjPostulante;
	}

	public void setListaDocumentosAdjPostulante(
			List<PostulacionAdjuntos> listaDocumentosAdjPostulante) {
		this.listaDocumentosAdjPostulante = listaDocumentosAdjPostulante;
	}

	public List<EvalDocumentalDet> getListaEvalDocumentalDet() {
		return listaEvalDocumentalDet;
	}

	public void setListaEvalDocumentalDet(
			List<EvalDocumentalDet> listaEvalDocumentalDet) {
		this.listaEvalDocumentalDet = listaEvalDocumentalDet;
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

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
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

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Integer getCantEvaluados() {
		return cantEvaluados;
	}

	public void setCantEvaluados(Integer cantEvaluados) {
		this.cantEvaluados = cantEvaluados;
	}

	public Boolean getCuentaConPostulantes() {
		return cuentaConPostulantes;
	}

	public void setCuentaConPostulantes(Boolean cuentaConPostulantes) {
		this.cuentaConPostulantes = cuentaConPostulantes;
	}

	public Boolean getTodosEvaluados() {
		return todosEvaluados;
	}

	public void setTodosEvaluados(Boolean todosEvaluados) {
		this.todosEvaluados = todosEvaluados;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public Observacion getObs() {
		return obs;
	}

	public void setObs(Observacion obs) {
		this.obs = obs;
	}

	public String getMensajeModal() {
		return mensajeModal;
	}

	public void setMensajeModal(String mensajeModal) {
		this.mensajeModal = mensajeModal;
	}

	public String getView451() {
		return view451;
	}

	public void setView451(String view451) {
		this.view451 = view451;
	}

	public Boolean getMostrarModal() {
		return mostrarModal;
	}

	public void setMostrarModal(Boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Integer getEstadoGrupoDesierto() {
		return estadoGrupoDesierto;
	}

	public void setEstadoGrupoDesierto(Integer estadoGrupoDesierto) {
		this.estadoGrupoDesierto = estadoGrupoDesierto;
	}

	public Boolean getBloquearTodo() {
		return bloquearTodo;
	}

	public void setBloquearTodo(Boolean bloquearTodo) {
		this.bloquearTodo = bloquearTodo;
	}

	public Boolean getDisminuirPuestos() {
		return disminuirPuestos;
	}

	public void setDisminuirPuestos(Boolean disminuirPuestos) {
		this.disminuirPuestos = disminuirPuestos;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getAprobo() {
		return aprobo;
	}

	public void setAprobo(String aprobo) {
		this.aprobo = aprobo;
	}

	public Boolean getDeclararDesierto() {
		return declararDesierto;
	}

	public void setDeclararDesierto(Boolean declararDesierto) {
		this.declararDesierto = declararDesierto;
	}

	public String getCodActividad() {
		return codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	public List<Parentesco> getListDetails2() {
		return listDetails2;
	}

	public void setListDetails2(List<Parentesco> listDetails2) {
		this.listDetails2 = listDetails2;
	}

	public List<Parentesco> getListDetails() {
		return listDetails;
	}

	public void setListDetails(List<Parentesco> listDetails) {
		this.listDetails = listDetails;
	}
	public String getUsuPostulacionFiltro() {
		return usuPostulacionFiltro;
	}

	public void setUsuPostulacionFiltro(String usuPostulacion) {
		this.usuPostulacionFiltro = usuPostulacion;
	}
	public Boolean getEvaluadoFiltro() {
		return evaluadoFiltro;
	}

	public void setEvaluadoFiltro(Boolean evaluado) {
		this.evaluadoFiltro = evaluado;
	}
	
	public Boolean getAprobadoFiltro() {
		return aprobadoFiltro;
	}

	public void setAprobadoFiltro(Boolean aprobadoFiltro) {
		this.aprobadoFiltro = aprobadoFiltro;
	}

	
	public List<SelectItem> estaEvaluado() {
		List<SelectItem> listaItem = new ArrayList<SelectItem>();
		listaItem.add(new SelectItem(true, "Si"));
		listaItem.add(new SelectItem(false, "No"));
		return listaItem;
	}
	
	public List<SelectItem> estaAprobado() {
		List<SelectItem> listaItem = new ArrayList<SelectItem>();
		listaItem.add(new SelectItem(true, "Si"));
		listaItem.add(new SelectItem(false, "No"));
		return listaItem;
	}
	
	public boolean deshabilitarEvaluarPostulante (EvalDocumentalCab evalDocumentalCab){
		boolean retorno = true;
		
		if(evalDocumentalCab.getAprobado() == null)
			retorno = false;
		else {
			if(evalDocumentalCab.getIncluido()==null || !evalDocumentalCab.getIncluido())
				retorno = false;
			else
				if(evalDocumentalCab.getUsuMod().equals(usuarioLogueado.getCodigoUsuario()))
					retorno = false;
		}
				
				
		return retorno;
		
	}

	public String evaluar(){
		if(this.isEdit==null || !this.isEdit) {
			return "Ver evaluaciÛn";
		}
		else
			return "Evaluar postulante";
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}
	
	public String volverHistorial() {
		return "/seleccion/historial/HistorialConcursoGrupoView.seam?idConcursoPuestoAgr="
				+ this.concursoPuestoAgr.getIdConcursoPuestoAgr().toString();
	}
}
