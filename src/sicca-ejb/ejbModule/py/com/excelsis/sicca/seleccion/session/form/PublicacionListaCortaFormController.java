package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.spec.ECField;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

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
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalAbiertas;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.HistorialActividadesGrupo;
import py.com.excelsis.sicca.entity.Lista;
import py.com.excelsis.sicca.entity.ListaElegiblesCab;
import py.com.excelsis.sicca.entity.ListaElegiblesDet;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PresentAclaracDoc;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.EvalAbiertasList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.ConceptoPagoFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("publicacionListaCortaFormController")
public class PublicacionListaCortaFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true, required = false,value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;

	private ConcursoPuestoAgr concursoPuestoAgr;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private EvaluarDocPostulantesFormController evaluarDocPostulantesFormController;
	public AdmFecRecPosFC admFecRecPosFC;
	private Concurso concurso;
	private Lista listaARecuperar;
	private String codConcurso;
	private String observacion;
	private String direccionFisica;
	private String horaDesde;
	private String horaHasta;
	private String lugar;
	private Long idDpto;
	private Long idCiudad;
	private String obs;
	private Boolean estaPublicado;
	private Integer cantVacancia;
	private Integer cantDesempatar;
	private String direccion;
	private String tipoMT;
	private Boolean esPrimeraVez;
	private Boolean bloquearTodo = false;
	private String titulo;
	private Boolean conEmpates;
	private String obsPopUp;
	private String existeEnLista;
	private Integer indexPopUp;
	private Integer estadoGrupoDesierto;
	private Date fecha;
	private Boolean estaEnLista;
	private Boolean cambioEstado;
	private Boolean asignarCategoria;
	private String conCedula;
	private String conDetalle;
	private String asunto;
	private String asuntoNoSeleccion;
	private Integer PorcentajeMinimo;
	private SeguridadUtilFormController seguridadUtilFormController;
	public ConceptoPagoFormController conceptoPagoFormController;
	
	private List<EvalReferencialPostulante> listaRefPostulantesSinFiltrar = new ArrayList<EvalReferencialPostulante>();
	private List<EvalReferencialPostulante> listaRefPostulantesFiltrado = new ArrayList<EvalReferencialPostulante>();
	private List<EvalReferencialPostulante> listaCortaPostulantes = new ArrayList<EvalReferencialPostulante>();
	private List<EvalReferencialPostulante> listaElegibles = new ArrayList<EvalReferencialPostulante>();
	private List<Persona> listaNoSeleccionados = new ArrayList<Persona>();
	private List<EvalReferencialPostulante> listaEmpatados = new ArrayList<EvalReferencialPostulante>();
	private List<EvalReferencialPostulante> listaSinEmpatar = new ArrayList<EvalReferencialPostulante>();
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private Long idConcursoPuestoAgr;
	private DatosGrupoPuesto datosGrupoPuesto;

	/**
	 * M�todo que inicializa los datos
	 */
	public void init() {
		
		
		
				
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();

		evaluarDocPostulantesFormController = (EvaluarDocPostulantesFormController) Component
				.getInstance(EvaluarDocPostulantesFormController.class, true);
		if (conceptoPagoFormController == null) {
			conceptoPagoFormController = (py.com.excelsis.sicca.session.form.ConceptoPagoFormController) Component
					.getInstance(ConceptoPagoFormController.class, true);
		}
		validarOee();
		
		bloquearTodo = false;
		esMeritoOTerna();
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
		datosGrupoPuesto = ObtenerDatosGrupoPuesto(concursoPuestoAgr.getIdConcursoPuestoAgr());
		
		
		
		
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
		listaRefPostulantesSinFiltrar = buscarReferencialPostulantes();
		cantVacancia = calcularVacancias();
		
		Integer cantidad = null;
		if (tipoMT.equals("M")) {
			titulo = SeamResourceBundle.getBundle().getString(
					"CU87_title1_merito");
			cantidad = cantVacancia;
		}
		if (tipoMT.equals("T")) {
			titulo = SeamResourceBundle.getBundle().getString(
					"CU87_title1_terna");
			cantidad = vacanciaTerna(cantVacancia);
		}

		listaCortaPostulantes = buscarPostulantesListaCorta();
		listaRefPostulantesFiltrado = buscarListaFiltrada();
		
		

		
		if (tipoMT.equals("M")) {
			
			if (listaRefPostulantesFiltrado.size() < cantidad) {
				declararDesierto();
				reponerCategorias();
				bloquearTodo = true;
				asignarCategoria = false;
				statusMessages
						.add(Severity.ERROR,
								"Este grupo se declara desierto por no contar con Postulantes suficientes.");
				return;
			}
		}
		
		if (tipoMT.equals("T")) {
			if (listaRefPostulantesFiltrado.size() < cantVacancia+1) {
				declararDesierto();
				reponerCategorias();
				bloquearTodo = true;
				asignarCategoria = false;
				statusMessages
						.add(Severity.ERROR,
								"Este grupo se declara desierto por no contar con Postulantes suficientes.");
				return;
			}
		}
			
		
		
		if (esPrimeraVez) {
			if (tipoMT.equals("M")){
				conEmpates = false;
				if (cantidad.intValue() <= listaRefPostulantesSinFiltrar.size()) {
					Boolean verificar = verificarEmpates();
					if (!verificar)
						actualizaSinEmpates();
					if (verificar) {
						cargaListaEmpatados();
						conEmpates = true;
						cargarListaSinEmpatar();
						actualizarFiltradosSinEmpatar();

						Integer cantSinEmpates = listaSinEmpatar.size();
						Integer vacancia = cantVacancia;
						
						cantDesempatar = vacancia - cantSinEmpates;
						titulo = SeamResourceBundle.getBundle().getString(
								"CU87_title_empates");
					}
				
				}
			
			}
			if (tipoMT.equals("T")){
				conEmpates = false;
				if (cantVacancia +1 <= listaRefPostulantesSinFiltrar.size()) {
					Boolean verificar = verificarEmpates();
					if (!verificar)
						actualizaSinEmpates();
					if (verificar) {
						cargaListaEmpatados();
						conEmpates = true;
						cargarListaSinEmpatar();
						actualizarFiltradosSinEmpatar();

						Integer cantSinEmpates = listaSinEmpatar.size();
						Integer vacancia = cantVacancia;
						
						vacancia = vacanciaTerna(vacancia);
						cantDesempatar = vacancia - cantSinEmpates;
						titulo = SeamResourceBundle.getBundle().getString(
								"CU87_title_empates");
					}
				
				}
			
			}
		}
		if (!esPrimeraVez) {
			
			
			/*
			 * SINO ES LA PRIMERA VEZ Y LA CANTIDAD DE SELECCIONADOS ES MENOR A LA CANTIDAD DE VACANCIAS
			 * VUELVE A CONTROLAR SI EXISTE O NO UN EMPATE. 
			 * 
			 */
			
			String sql = "Select count(*) from seleccion.eval_referencial_postulante"
					+ " where activo = true"
					+ " and lista_corta = true"
					+ " and seleccionado = true"
					+ " and id_concurso_puesto_agr = "+ this.concursoPuestoAgr.getIdConcursoPuestoAgr();
			
			BigInteger cantidadSeleccionado = (BigInteger) em.createNativeQuery(sql).getSingleResult();
			
			if(cantidadSeleccionado.intValue() < cantidad.intValue()){
				if (tipoMT.equals("M")){
					conEmpates = false;
					if (cantidad.intValue() <= listaRefPostulantesSinFiltrar.size()) {
						Boolean verificar = verificarEmpates();
						if (!verificar)
							actualizaSinEmpates();
						if (verificar) {
							cargaListaEmpatados();
							conEmpates = true;
							cargarListaSinEmpatar();
							actualizarFiltradosSinEmpatar();

							Integer cantSinEmpates = listaSinEmpatar.size();
							Integer vacancia = cantVacancia;
							
							cantDesempatar = vacancia - cantSinEmpates;
							titulo = SeamResourceBundle.getBundle().getString(
									"CU87_title_empates");
						}
					
					}
									
				
				}
				if (tipoMT.equals("T")){
					conEmpates = false;
					if (cantVacancia +1 <= listaRefPostulantesSinFiltrar.size()) {
						Boolean verificar = verificarEmpates();
						if (!verificar)
							actualizaSinEmpates();
						if (verificar) {
							cargaListaEmpatados();
							conEmpates = true;
							cargarListaSinEmpatar();
							actualizarFiltradosSinEmpatar();

							Integer cantSinEmpates = listaSinEmpatar.size();
							Integer vacancia = cantVacancia;
							
							vacancia = vacanciaTerna(vacancia);
							cantDesempatar = vacancia - cantSinEmpates;
							titulo = SeamResourceBundle.getBundle().getString(
									"CU87_title_empates");
						}
					
					}
				
				}

			}
			
			updateDepartamento();
			updateCiudad();
			//conEmpates = false;
			buscarEnLista();
			if (estaEnLista) {
				SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
				fecha = listaARecuperar.getFechaConv();
				lugar = listaARecuperar.getLugar();
				direccion = listaARecuperar.getDireccion();
				obs = listaARecuperar.getObservacion();
				idCiudad = listaARecuperar.getCiudad().getIdCiudad();
				idDpto = listaARecuperar.getCiudad().getDepartamento()
						.getIdDepartamento();
				updateCiudad();
				horaDesde = sdfHora.format(listaARecuperar.getHoraDesde());
				horaHasta = sdfHora.format(listaARecuperar.getHoraHasta());
			}
			obtenerDireccionFisica();
			estaPublicado = verificarPublicacion();
			conCedula = "S";
			conDetalle = "S";
		}
		String resultante = conceptoPagoFormController
				.esContratadoOpermanente(concursoPuestoAgr
						.getIdConcursoPuestoAgr());
		// Puesto es permanente
		if (resultante != null && resultante.equalsIgnoreCase("PERMANENTE")) {
			// Concurso es adReferendum
			if (concurso.getAdReferendum() != null
					&& concurso.getAdReferendum()) {
				if (!exiteEnGrupoConceptoPago()) {
					statusMessages
							.add(Severity.INFO,
									"El concurso es ad-referendum. Deber� asignar las categor�as reales a cada puesto.");
					asignarCategoria = true;
					bloquearTodo = true;
				} else {
					asignarCategoria = false;
					bloquearTodo = false;
				}
				// Concurso no es adReferendum
			} else {
				asignarCategoria = false;
				bloquearTodo = false;
			}
			// Puesto es contratado
		} else {
			asignarCategoria = false;
			bloquearTodo = false;
		}
		
		
		listaElegibles = buscarListaElegibles();
		
				
	}
	
	public DatosGrupoPuesto ObtenerDatosGrupoPuesto (Long idGrupo){
		
		String sql = "select * from seleccion.datos_grupo_puesto where id_concurso_puesto_agr = "+ idGrupo;
		
		return (DatosGrupoPuesto) em.createNativeQuery(sql,DatosGrupoPuesto.class).getResultList().get(0);
		
	}

	/************************* <MODIFICACION> *********************************/
	/************************* MODIFICADO: 13/12/2013 *************************/
	/************************* AUTOR: RODRIGO VELAZQUEZ ***********************/
	public String getReturnURL() {
		return "/seleccion/evalReferencialPostulante/ElaboracionListaCorta.seam?concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	public String getUrlToListaElegibles() {
		return "/seleccion/evalReferencialPostulante/ListaElegibles.seam?concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	public List<EvalReferencialPostulante> buscarListaElegibles() {
		String sql = "select eval_ref.*  "
				+ "from seleccion.eval_referencial_postulante  eval_ref  "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = eval_ref.id_postulacion "
				+ "where eval_ref.seleccionado is not true ";
		
				if (tipoMT.equals("T"))
					sql  += " and eval_ref.lista_corta is true ";
						
				sql+= "and post.activo is true "
				+ "and post.estado_postulacion like 'ACTIVO' "				
				+ "and eval_ref.activo is true  "
				+ "and eval_ref.id_concurso_puesto_agr = " 
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and eval_ref.puntaje_realizado >= "
				+ this.PorcentajeMinimo;
				//SE EXCLUYEN LOS QUE HAYAN ESTADO AUSENTE EN ALGUNA DE LAS EVALUACIONES
				sql += " and eval_ref.id_postulacion not in ("
				+ "	select id_postulacion from seleccion.eval_referencial where id_postulacion in "
				+ "(select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr =" + concursoPuestoAgr.getIdConcursoPuestoAgr() +" ) and presente = false)";
			
				//EN CASO DE QUE SEA POR ETAPAS SE EXCLUYEN LOS NO APROBADOS POR ETAPAS
				if(this.datosGrupoPuesto.getPorMinPorEvaluacion() != null && this.datosGrupoPuesto.getPorMinPorEvaluacion()){
					sql += " and eval_ref.id_postulacion not in ("
					+ "	select id_postulacion from seleccion.eval_referencial where id_postulacion in "
					+ "(select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr =" + concursoPuestoAgr.getIdConcursoPuestoAgr() +" ) and aprobado = false)";
				}
				
				sql+= " order by eval_ref.puntaje_realizado desc";
		return em.createNativeQuery(sql, EvalReferencialPostulante.class)
				.getResultList();
	}

	private Integer vacanciaTerna(Integer vacancia) {
		//Integer retorno = vacancia * 3;
		Integer retorno = vacancia + 2;
		String sql = "select count(eval_ref.*)  "
				+ "from seleccion.eval_referencial_postulante  eval_ref  "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = eval_ref.id_postulacion  "
				+ "where post.activo is true "
				+ "and post.estado_postulacion like 'ACTIVO' "
				+ "and eval_ref.activo is true and eval_ref.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		BigInteger cant = ((BigInteger) em.createNativeQuery(sql)
				.getSingleResult());
		while (retorno > cant.intValue() && retorno > vacancia + 1)
			--retorno;
		return retorno;
	}

	/************************* </MODIFICACION> ********************************/

	public void reponerCategorias() {
		reponerCategoriasController.reponerCategorias(concursoPuestoAgr,
				"GRUPO", "CONCURSO");
	}

	private Boolean exiteEnGrupoConceptoPago() {
		Referencias referenciaGrupo = new Referencias();
		referenciaGrupo = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA_GRUPO", "RESERVADO");
		Query q = em
				.createQuery("select pago from GrupoConceptoPago pago "
						+ " where pago.activo is true and pago.objCodigo = :cod and pago.concursoPuestoAgr.idConcursoPuestoAgr = :id "
						+ "and pago.estado = :estado");
		q.setParameter("cod", new Integer(111));
		q.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		q.setParameter("estado", referenciaGrupo.getValorNum());
		List<GrupoConceptoPago> lista = new ArrayList<GrupoConceptoPago>();
		lista = q.getResultList();
		if (lista.isEmpty())
			return false;
		return true;
	}
	
	public void initCUEvalDet() {
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);
		
		
	}

	public void init2() {
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
		datosGrupoPuesto = ObtenerDatosGrupoPuesto(concursoPuestoAgr.getIdConcursoPuestoAgr());
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
		listaRefPostulantesSinFiltrar = buscarReferencialPostulantes();
		cantVacancia = calcularVacancias();
		esMeritoOTerna();
		Integer cantidad = null;
		if (tipoMT.equals("M")) {
			titulo = SeamResourceBundle.getBundle().getString(
					"CU87_title1_merito");
			cantidad = cantVacancia;
		}
		if (tipoMT.equals("T")) {
			titulo = SeamResourceBundle.getBundle().getString(
					"CU87_title1_terna");
			cantidad = vacanciaTerna(cantVacancia);
		}

		listaCortaPostulantes = buscarPostulantesListaCorta();
		listaRefPostulantesFiltrado = buscarListaFiltrada();
		if (esPrimeraVez) {
			conEmpates = false;
			if (cantidad.intValue() < listaRefPostulantesSinFiltrar.size()) {
				Boolean verificar = verificarEmpates();
				if (!verificar)
					actualizaSinEmpates();
				if (verificar) {
					cargaListaEmpatados();
					conEmpates = true;
					cargarListaSinEmpatar();
					actualizarFiltradosSinEmpatar();
					titulo = SeamResourceBundle.getBundle().getString(
							"CU87_title_empates");
				}
			}
		}
		if (!esPrimeraVez) {
			updateDepartamento();
			updateCiudad();
			conEmpates = false;
			buscarEnLista();
			if (estaEnLista) {
				SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
				fecha = listaARecuperar.getFechaConv();
				lugar = listaARecuperar.getLugar();
				direccion = listaARecuperar.getDireccion();
				obs = listaARecuperar.getObservacion();
				idCiudad = listaARecuperar.getCiudad().getIdCiudad();
				idDpto = listaARecuperar.getCiudad().getDepartamento()
						.getIdDepartamento();
				updateCiudad();
				horaDesde = sdfHora.format(listaARecuperar.getHoraDesde());
				horaHasta = sdfHora.format(listaARecuperar.getHoraHasta());
			}
			obtenerDireccionFisica();
			estaPublicado = verificarPublicacion();
			
			
			
			
			conCedula = "S";
			conDetalle = "S";
		}
	}

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		seguridadUtilFormController.verificarPerteneceOee(
				null,
				concursoPuestoAgr.getIdConcursoPuestoAgr(),
				seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO",
						"EVALUACION REFERENCIAL") + "",
				ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA.getValor());
	}

	public void print2() {
		init2();
		if (tipoMT.equals("M")) {
			imprimirListaFinalMerito();
		}
		if (tipoMT.equals("T")) {
			//print();
			imprimirListaCortaTerna();
		}
		
	}
	public void imprimirListaFinalTerna() {
		init2();
		try {
			
			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			Connection conn = JpaResourceBean.getConnection();
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("idConcursoPuestoAgr",this.concursoPuestoAgr.getIdConcursoPuestoAgr());
			param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
			param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
			param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());
			
			if(usuarioLogueado != null )
				param.put("usuario", usuarioLogueado.getCodigoUsuario());
			else
				param.put("usuario", "Visitante Portal");
			
			HistorialActividadesGrupo historial = obtenerFechaActividad(concursoPuestoAgr.getIdConcursoPuestoAgr(),
					"REALIZAR ENTREVISTA FINAL MAI/OEE");
			if (!validarTipoConcurso() && (historial==null || historial.getFechaFin()==null))
				param.put("fecha", "Aun no se ha publicado");
			else if(validarTipoConcurso())
			{
				Query l = em.createQuery("select L from Lista L "
						+ "where L.tipo =:tipo and  L.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
				l.setParameter("tipo", "LISTA CORTA");
				l.setParameter("id_concurso_puesto_agr", concursoPuestoAgr.getIdConcursoPuestoAgr());
				List<Lista> auxLista = l.getResultList();
				if(auxLista.isEmpty())
				{
					param.put("fecha", "Aun no se ha publicado");
				}else{
					String aux = auxLista.get(0).getFechaPublicacion().toString().substring(0, 10);
					String aux2 = aux.substring(0, 5);
					aux2 = aux2 + aux.substring(8, 10) + "-";
					aux2 = aux2 + aux.substring(5, 7);
					param.put("fecha",aux2);				
				}

			}
			else{
				Format formato = new SimpleDateFormat("dd-MM-yyyy");
				param.put("fecha", formato.format(historial.getFechaFin()));
			}
			
			
			if(conCedula != null){
				if (conCedula.equals("N"))				
					JasperReportUtils.respondPDF("listaFinalTerna",	param, false, conn,usuarioLogueado);
				else if (conCedula.equals("S"))
					JasperReportUtils.respondPDF("listaFinalTerna_cedula",	param, false, conn,usuarioLogueado);
			}else
				conCedula = esConCedula(this.concursoPuestoAgr.getIdConcursoPuestoAgr());
			
				if (conCedula.equals("N"))				
					JasperReportUtils.respondPDF("listaFinalTerna",	param, false, conn,usuarioLogueado);
				else if (conCedula.equals("S"))
					JasperReportUtils.respondPDF("listaFinalTerna_cedula",	param, false, conn,usuarioLogueado);
				
				//JasperReportUtils.respondPDF("listaFinalTerna_cedula",	param, false, conn,usuarioLogueado);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void imprimirCUEvalDet() {
		initCUEvalDet();
		printCUEvalDet();
	}

	/**
	 * Declara desierto en caso de que no existan postulantes
	 */
	private void declararDesierto() {
		estadoGrupoDesierto = evaluarDocPostulantesFormController
				.obtenerEstadoGrupo("ESTADOS_GRUPO", "GRUPO DESIERTO");
		/* Actualizar el estado del Grupo: */
		cambiarEstadoGrupo(concursoPuestoAgr);
		/* Actualizar el ESTADO de los PUESTOS: */
		cambiarEstadoPuestos(evaluarDocPostulantesFormController
				.traerGrupoPuestoDet(concursoPuestoAgr.getIdConcursoPuestoAgr()));

		// Se pasa a la siguiente tarea
		try {
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA);
			jbpmUtilFormController.setTransition("end");

			if (jbpmUtilFormController.nextTask())
				em.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
		/* Enviar mails a los integrantes de la Comisi�n de Selecci�n */
		evaluarDocPostulantesFormController
				.enviarMailComision(concursoPuestoAgr);
		/* Enviar mail al Postulante (si hubiere) para el Grupo */
		if (admFecRecPosFC == null) {
			admFecRecPosFC = (py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC) Component
					.getInstance(AdmFecRecPosFC.class, true);
		}
		admFecRecPosFC.enviarMail2(concursoPuestoAgr);
		admFecRecPosFC.insertarPublicacionPortal(concursoPuestoAgr
				.getIdConcursoPuestoAgr(), concursoPuestoAgr.getConcurso()
				.getIdConcurso(), admFecRecPosFC.genTextoPublicacion2());

	}

	/**
	 * Cambia el estado de los grupos a desierto en caso de que no existan
	 * postulantes
	 * 
	 * @param grupo
	 */
	private void cambiarEstadoGrupo(ConcursoPuestoAgr grupo) {
		grupo.setEstado(estadoGrupoDesierto);
		grupo.setUsuMod(usuarioLogueado.getCodigoUsuario());
		grupo.setFechaMod(new Date());
		grupo = em.merge(grupo);
	}

	/**
	 * Cambia el estado de los puestos a desierto en caso de que no existan
	 * postulantes
	 * 
	 * @param lista
	 */
	public void cambiarEstadoPuestos(List<ConcursoPuestoDet> lista) {
		EstadoCab estadoCab = evaluarDocPostulantesFormController
				.obtenerEstadosCabecera("VACANTE");
		EstadoDet estadoEval = evaluarDocPostulantesFormController
				.obtenerEstadosVarios("EVALUACION REFERENCIAL", "CONCURSO");
		EstadoDet estadoDet = evaluarDocPostulantesFormController
				.obtenerEstadosVarios("DESIERTO", "CONCURSO");
		for (ConcursoPuestoDet concursoPuesto : lista) {
			// Actualizar el Puesto
			if (concursoPuesto.getPlantaCargoDet().getEstadoDet()
					.getIdEstadoDet().equals(estadoEval.getIdEstadoDet())) {
				concursoPuesto.getPlantaCargoDet().setEstadoCab(estadoCab);
				concursoPuesto.getPlantaCargoDet().setEstadoDet(null);
				concursoPuesto.getPlantaCargoDet().setUsuMod(
						usuarioLogueado.getCodigoUsuario());
				concursoPuesto.getPlantaCargoDet().setFechaMod(new Date());
				concursoPuesto.setPlantaCargoDet(em.merge(concursoPuesto
						.getPlantaCargoDet()));
				concursoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
				concursoPuesto.setFechaMod(new Date());
				concursoPuesto.setActivo(true); //para que aparezca en historial, debe estar activo
				EstadoDet estado = new EstadoDet();
				estado.setIdEstadoDet(estadoDet.getIdEstadoDet());
				concursoPuesto.setEstadoDet(estado);
				concursoPuesto = em.merge(concursoPuesto);
			}
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

	/**
	 * Paso 1 ------ busca postulantes seleccionados sin filtrar por cantidad de
	 * vacancias
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<EvalReferencialPostulante> buscarReferencialPostulantes() {
		String sql = "select eval_ref.*  "
				+ "from seleccion.eval_referencial_postulante  eval_ref "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = eval_ref.id_postulacion "
				+ "where post.activo is true "
				+ " and post.estado_postulacion like 'ACTIVO' "
				+ "and eval_ref.activo is true   "
				+ "and eval_ref.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and eval_ref.puntaje_realizado >= "
				+ this.PorcentajeMinimo;
		
				//SE EXCLUYEN LOS QUE HAYAN ESTADO AUSENTE EN ALGUNA DE LAS EVALUACIONES
				sql += " and eval_ref.id_postulacion not in ("
				+ "	select id_postulacion from seleccion.eval_referencial where id_postulacion in "
				+ "(select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr =" + concursoPuestoAgr.getIdConcursoPuestoAgr() +" ) and presente = false)";
			
				//EN CASO DE QUE SEA POR ETAPAS SE EXCLUYEN LOS NO APROBADOS POR ETAPAS
				if(this.datosGrupoPuesto.getPorMinPorEvaluacion() != null && this.datosGrupoPuesto.getPorMinPorEvaluacion()){
					sql += " and eval_ref.id_postulacion not in ("
					+ "	select id_postulacion from seleccion.eval_referencial where id_postulacion in "
					+ "(select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr =" + concursoPuestoAgr.getIdConcursoPuestoAgr() +" ) and aprobado = false)";
				}
				
				sql += " order by eval_ref.puntaje_realizado desc";
		List<EvalReferencialPostulante> lista = new ArrayList<EvalReferencialPostulante>();
		lista = em.createNativeQuery(sql, EvalReferencialPostulante.class)
				.getResultList();
		for (EvalReferencialPostulante l : lista) {
			if (l.getListaCorta() != null && l.getListaCorta() == true) {
				esPrimeraVez = false;
				return lista;
			}
		}
		esPrimeraVez = true;
		return lista;
	}

	/**
	 * Paso 2 --- Calcula la cantidad de vacancias para el puesto
	 * 
	 * @return
	 */

	private Integer calcularVacancias() {
		
		if(concursoPuestoAgr.getCantidadPuestos() != null)
			return concursoPuestoAgr.getCantidadPuestos();
		else{
			String sql = "SELECT det.* " 
					+ "FROM seleccion.concurso_puesto_det det "
					+ "WHERE id_concurso_puesto_agr = "
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ " AND ACTIVO = TRUE";
			
			List <ConcursoPuestoDet> list = em.createNativeQuery(sql,ConcursoPuestoDet.class).getResultList();
			
			if (list.size() == 0)
				return 0;
			else
				return  new Integer(""+list.size());
			
		}
	}

	/**
	 * Paso 3 --- Verifica si el concurso es por merito o por terna
	 */
	@SuppressWarnings("unchecked")
	private void esMeritoOTerna() {
		String sql = "SELECT datos_gr.* "
				+ "FROM seleccion.datos_grupo_puesto datos_gr "
				+ "WHERE datos_gr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ "AND datos_gr.activo = TRUE";
		List<DatosGrupoPuesto> listaDatos = new ArrayList<DatosGrupoPuesto>();
		listaDatos = em.createNativeQuery(sql, DatosGrupoPuesto.class)
				.getResultList();
		for (DatosGrupoPuesto l : listaDatos) {
			if(l.getPorcMinimo() != null)
				this.PorcentajeMinimo = l.getPorcMinimo();
			if (l.getMerito() && !l.getTerna()) {
				tipoMT = "M";
				
			}
			if (!l.getMerito() && l.getTerna()) {
				tipoMT = "T";
				
			}
			
		}
		
		
	}

	/**
	 * Paso 4 --- busca la lista de postulantes seleccionados filtrados por
	 * cantidad de vacancias
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<EvalReferencialPostulante> buscarListaFiltrada() {
		Integer vacancia = cantVacancia;
		if (tipoMT.equals("T"))
			vacancia = vacanciaTerna(vacancia);

		String sql = "select eval_ref.*  "
				+ "from seleccion.eval_referencial_postulante  eval_ref  "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = eval_ref.id_postulacion  "
				+ "where post.activo is true "
				+ " and post.estado_postulacion like 'ACTIVO' "
				+" and eval_ref.id_postulacion not in (select id_postulacion from seleccion.eval_referencial where presente = false"
				+ "  and id_postulacion in (select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = "+ concursoPuestoAgr.getIdConcursoPuestoAgr()+"))"
				+ "and eval_ref.activo is true and eval_ref.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and eval_ref.puntaje_realizado >= "
				+ this.PorcentajeMinimo
				+ " order by eval_ref.puntaje_realizado desc " + "limit "+ (vacancia);
		
		return em.createNativeQuery(sql, EvalReferencialPostulante.class)
				.getResultList();
	}

	/**
	 * Paso 5 --- verifica que existan empates
	 * 
	 * @return
	 */
	private Boolean verificarEmpates() {
		Integer index = listaRefPostulantesFiltrado.size();
		if (index > 0) {
			index--;
			EvalReferencialPostulante aEmpatar = new EvalReferencialPostulante();
			aEmpatar = listaRefPostulantesFiltrado.get(index);

			for (int i = index + 1; i < listaRefPostulantesSinFiltrar.size(); i++) {
				EvalReferencialPostulante ev = new EvalReferencialPostulante();
				ev = listaRefPostulantesSinFiltrar.get(i);
				if (ev.getPuntajeRealizado().equals(
						aEmpatar.getPuntajeRealizado()))
					return true;
			}
			for (int i = 0; i < index - 1; i++) {
				EvalReferencialPostulante ev = new EvalReferencialPostulante();
				ev = listaRefPostulantesFiltrado.get(i);
				if (ev.getPuntajeRealizado().equals(
						aEmpatar.getPuntajeRealizado()))
					return true;
			}
		}
		return false;
	}

	/**
	 * Paso 6 --- actualiza los registros que no han empatado en el paso
	 * anterior
	 */
	private void actualizaSinEmpates() {
		try {
			if (tipoMT.equals("M")) {
				for (EvalReferencialPostulante filtrado : listaRefPostulantesFiltrado) {
					filtrado.setListaCorta(true);
					filtrado.setSeleccionado(true);
					em.merge(filtrado);
				}

				if (listaRefPostulantesSinFiltrar.size() > cantVacancia) {
					for (int i = cantVacancia; i < listaRefPostulantesSinFiltrar
							.size(); i++) {
						EvalReferencialPostulante eval = new EvalReferencialPostulante();
						eval = listaRefPostulantesSinFiltrar.get(i);
						eval.setListaCorta(true);
						eval.setSeleccionado(false);
						em.merge(eval);
					}
				}

			}

			if (tipoMT.equals("T")) {
				Integer vacancia = vacanciaTerna(cantVacancia);
				for (EvalReferencialPostulante filtrado : listaRefPostulantesFiltrado) {
					filtrado.setListaCorta(true);
					em.merge(filtrado);
				}
				for (int i = vacancia; i < listaRefPostulantesSinFiltrar.size(); i++) {
					EvalReferencialPostulante eval = new EvalReferencialPostulante();
					eval = listaRefPostulantesSinFiltrar.get(i);
					eval.setListaCorta(false);
					em.merge(eval);
				}

			}
			em.flush();
			listaCortaPostulantes = buscarPostulantesListaCorta();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Paso 7 --- recupera la lista corta de postulantes a ser mostrado una vez
	 * culminado las validaciones de ingreso
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<EvalReferencialPostulante> buscarPostulantesListaCorta() {
		String sql = "select eval_ref.*  "
				+ "from seleccion.eval_referencial_postulante  eval_ref  "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = eval_ref.id_postulacion "
				+ "where eval_ref.lista_corta is true  "
				+" and eval_ref.id_postulacion not in (select id_postulacion from seleccion.eval_referencial where presente = false "
				+ "  and id_postulacion in (select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = "+ concursoPuestoAgr.getIdConcursoPuestoAgr()+"))"
				+ "and post.activo is true "
				+ "and post.estado_postulacion like 'ACTIVO' "
				+ "and eval_ref.activo is true  "
				+ "and (eval_ref.seleccionado is null or eval_ref.seleccionado is true) "
				+ "and eval_ref.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and eval_ref.puntaje_realizado >= "
				+ this.PorcentajeMinimo
				+ " order by eval_ref.puntaje_realizado desc";
		return em.createNativeQuery(sql, EvalReferencialPostulante.class)
				.getResultList();
	}

	/**
	 * Paso 8 --- carga la lista de empatados a ser mostrados
	 */
	private void cargaListaEmpatados() {
		Integer index = listaRefPostulantesFiltrado.size();
		index--;
		EvalReferencialPostulante refPost = new EvalReferencialPostulante();
		refPost = listaRefPostulantesFiltrado.get(index);
		listaEmpatados = new ArrayList<EvalReferencialPostulante>();

		for (int i = 0; i < listaRefPostulantesSinFiltrar.size(); i++) {
			EvalReferencialPostulante ev = new EvalReferencialPostulante();
			ev = listaRefPostulantesSinFiltrar.get(i);
			if (ev.getPuntajeRealizado().floatValue() == refPost
					.getPuntajeRealizado().floatValue())
				listaEmpatados.add(ev);
		}

		if (listaEmpatados.size() > 1) {
			/*
			 * for (int i = 0; i < index - 1; i++) { EvalReferencialPostulante
			 * ev = new EvalReferencialPostulante(); ev =
			 * listaRefPostulantesFiltrado.get(i); if
			 * (ev.getPuntajeRealizado().equals(refPost.getPuntajeRealizado()))
			 * listaEmpatados.add(ev); }
			 */
			Integer cantSinEmpates = listaEmpatados.size();
			/*
			 * for (int j = 0; j < listaRefPostulantesFiltrado.size(); j++) {
			 * EvalReferencialPostulante ev = new EvalReferencialPostulante();
			 * ev = listaRefPostulantesFiltrado.get(j); if
			 * (ev.getPuntajeRealizado().equals(refPost.getPuntajeRealizado()))
			 * cantSinEmpates++; }
			 */
			/*
			 * Integer vacancia = cantVacancia; if (tipoMT.equals("T")) vacancia
			 * = vacancia + 2; cantDesempatar = vacancia - cantSinEmpates;
			 */
		} else {
			listaEmpatados = new ArrayList<EvalReferencialPostulante>();
		}

	}

	/**
	 * Paso 9 --- carga la lista de los filtrados que no empataron
	 */

	private void cargarListaSinEmpatar() {
		if (listaEmpatados != null && listaEmpatados.size() > 0) {
			listaSinEmpatar = new ArrayList<EvalReferencialPostulante>();
			EvalReferencialPostulante empate = new EvalReferencialPostulante();
			empate = listaEmpatados.get(0);
			for (EvalReferencialPostulante filtrado : listaRefPostulantesFiltrado) {
				if (empate.getPuntajeRealizado().floatValue() != filtrado
						.getPuntajeRealizado().floatValue())
					listaSinEmpatar.add(filtrado);
			}
		}
	}

	/**
	 * Paso 10 --- actualiza los datos que ingresaron en el filtro pero no
	 * empataron
	 */
	private void actualizarFiltradosSinEmpatar() {
		try {
			for (EvalReferencialPostulante sinEmp : listaSinEmpatar) {
				if (tipoMT.equals("M")) {
					sinEmp.setListaCorta(true);
					sinEmp.setSeleccionado(true);
					em.merge(sinEmp);
				}
				if (tipoMT.equals("T")) {
					sinEmp.setListaCorta(true);
					sinEmp.setSeleccionado(true);
					em.merge(sinEmp);
				}
			}
			em.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Paso 11 --- prepara los datos para el popUp de observacion y una vez
	 * cargado el popUp, actualiza los datos de los empatados
	 * 
	 * @param index
	 */
	public void prepararPopUpObs(Integer index) {
		EvalReferencialPostulante eval = new EvalReferencialPostulante();
		eval = listaEmpatados.get(index);
		obsPopUp = eval.getObsEmpate();
		indexPopUp = index;
	}

	public void updateDatosEmpatados() {
		EvalReferencialPostulante eval = new EvalReferencialPostulante();
		eval = listaEmpatados.get(indexPopUp);
		eval.setObsEmpate(obsPopUp);
		try {
			em.merge(eval);
			em.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
		listaEmpatados.set(indexPopUp, eval);
		obsPopUp = null;
	}

	/**
	 * Paso 12 --- guarda empatados de acuerdo a las restricciones
	 * 
	 * @return
	 */
	public String guardarEmpatados() {
		if (!cumpleCantidadAdesempatar()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU87_msg1_guardar_empate"));
			return null;
		}
		if (!cumpleCargaObservaciones()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU87_msg2_guardar_empate"));
			return null;
		}
		try {
			for (EvalReferencialPostulante emp : listaEmpatados) {
				if (tipoMT.equals("M")) {
					if (emp.getSeleccionado()) {
						emp.setListaCorta(true);
						emp.setSeleccionado(true);
						em.merge(emp);
					}
					if (!emp.getSeleccionado()) {
						emp.setListaCorta(true);
						emp.setSeleccionado(false);
						em.merge(emp);
					}
				}
				if (tipoMT.equals("T")) {
					if (emp.getSeleccionado()) {
						emp.setListaCorta(true);
						em.merge(emp);
					}
					if (!emp.getSeleccionado()) {
						emp.setListaCorta(false);
						em.merge(emp);
					}
				}
			}
			em.flush();
			
			List <EvalReferencialPostulante> Elegibles = this.buscarListaElegibles();
			
			for (EvalReferencialPostulante eval : Elegibles){
			
				if(eval.getSeleccionado() == null ){
					if (tipoMT.equals("M")) {
						eval.setSeleccionado(false);
						em.merge(eval);
					}
				}
				
			}
			
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

		} catch (Exception e) {
			// TODO: handle exception
		}
		conEmpates = false;
		return getUrlToPageLista();
	}

	/**
	 * Verifica que fueron seleccionados tantos registros como se necesitan para
	 * desempatar
	 * 
	 * @return
	 */
	private Boolean cumpleCantidadAdesempatar() {
		Integer cant = 0;
		for (EvalReferencialPostulante emp : listaEmpatados) {
			if (emp.getSeleccionado())
				cant++;
		}
		if (cant == cantDesempatar)
			return true;
		return false;
	}

	/**
	 * Verifica que los registros seleccionados cuenten con una observacion
	 * 
	 * @return
	 */
	private Boolean cumpleCargaObservaciones() {
		for (EvalReferencialPostulante emp : listaEmpatados) {
			if ((emp.getObsEmpate() == null)
					|| emp.getObsEmpate().trim().isEmpty())
				return false;
		}
		return true;
	}

	/**
	 * Metodo que inserta en la tabla Lista
	 * 
	 * @return
	 */
	public String saveConvocatoria() {
		if (faltanDatosObligatorios()) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return null;
		}
		try {
			if (!estaEnLista) {
				Lista lLarga = new Lista();
				lLarga.setCiudad(em.find(Ciudad.class, idCiudad));
				lLarga.setConcursoPuestoAgr(concursoPuestoAgr);
				lLarga.setDireccion(direccion);
				lLarga.setFechaConv(fecha);
				int[] horaD = Utiles.getHora(horaDesde);
				Date fechaDesde = new Date();
				fechaDesde.setHours(horaD[0]);
				fechaDesde.setMinutes(horaD[1]);
				lLarga.setHoraDesde(fechaDesde);
				int[] horaH = Utiles.getHora(horaHasta);
				Date fechaHasta = new Date();
				fechaHasta.setHours(horaH[0]);
				fechaHasta.setMinutes(horaH[1]);
				lLarga.setHoraHasta(fechaHasta);
				lLarga.setLugar(lugar);
				lLarga.setObservacion(obs);
				lLarga.setTipo("LISTA CORTA");
				lLarga.setUsuMod(usuarioLogueado.getCodigoUsuario());
				lLarga.setFechaMod(new Date());
				em.persist(lLarga);
				em.flush();
			}
			if (estaEnLista) {
				listaARecuperar.setCiudad(em.find(Ciudad.class, idCiudad));
				listaARecuperar.setConcursoPuestoAgr(concursoPuestoAgr);
				listaARecuperar.setDireccion(direccion);
				listaARecuperar.setFechaConv(fecha);
				int[] horaD = Utiles.getHora(horaDesde);
				Date fechaDesde = new Date();
				fechaDesde.setHours(horaD[0]);
				fechaDesde.setMinutes(horaD[1]);
				listaARecuperar.setHoraDesde(fechaDesde);
				int[] horaH = Utiles.getHora(horaHasta);
				Date fechaHasta = new Date();
				fechaHasta.setHours(horaH[0]);
				fechaHasta.setMinutes(horaH[1]);
				listaARecuperar.setHoraHasta(fechaHasta);
				listaARecuperar.setLugar(lugar);
				listaARecuperar.setObservacion(obs);
				listaARecuperar.setTipo("LISTA CORTA");
				em.merge(listaARecuperar);
				em.flush();
			}
			limpiarDatosConvocatoria();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return getUrlToPageElaborarListaCorta();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private void limpiarDatosConvocatoria() {
		fecha = null;
		horaDesde = null;
		horaHasta = null;
		lugar = null;
		idDpto = null;
		idCiudad = null;
		direccion = null;
		obs = null;
	}

	private Boolean esTerna() {
		Query q = em
				.createQuery("select DatosGrupoPuesto from DatosGrupoPuesto DatosGrupoPuesto "
						+ " where DatosGrupoPuesto.activo is true "
						+ " and DatosGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<DatosGrupoPuesto> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getTerna();
		}
		return false;
	}

	private String genTextoPublicacion() {
		String texto = new String();
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
		texto = texto + hr + fechaPublicacion;
		if (concursoPuestoAgr.getIdConcursoPuestoAgr() != null)
			texto = texto
					+ br
					+ spanO
					+ "Puede descargar aqu� la: "
					+ spanC
					+ br
					+ spanO
					+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=CU_87&#38;idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ "'>Lista Corta de Pre-Seleccionados</a>";
		texto = texto + spanC;
		
		
		//Codigo para agregar el enlace para publicar la lista de evaluaciones
			texto = texto
				+ br
				+ spanO
				+ "Puede descargar aqu� las: "
				+ spanC
				+ br
				+ spanO
				+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=EvalDet&#38;idConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ "'>Evaluaciones Detalladas</a>";
				texto = texto + spanC;
		
		if (esTerna()) {
			//SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");

			texto = texto
					+ h1O
					+ "Convocatoria a Entrevista Final por la M�xima Autoridad de la Instituci�n: "
					+ h1C;
			if (listaARecuperar.getObservacion() != null)
				texto = texto + spanO + listaARecuperar.getObservacion()
						+ spanC + br;
			if (listaARecuperar.getFechaConv() != null)
				texto = texto + spanO + "Fecha: " + spanC
						+ sdfFecha.format(listaARecuperar.getFechaConv()) + br;
			if (listaARecuperar.getHoraDesde() != null
					&& listaARecuperar.getHoraHasta() != null)
				texto = texto + spanO + "Hora: " + spanC
						+ sdfHora.format(listaARecuperar.getHoraDesde())
						+ " a "
						+ sdfHora.format(listaARecuperar.getHoraHasta()) + br;
			if (listaARecuperar.getLugar() != null)
				texto = texto + spanO + "Lugar: " + spanC
						+ listaARecuperar.getLugar() + br;
			if (listaARecuperar.getCiudad().getDepartamento().getDescripcion() != null)
				texto = texto
						+ spanO
						+ "Departamento: "
						+ spanC
						+ listaARecuperar.getCiudad().getDepartamento()
								.getDescripcion() + br;
			if (listaARecuperar.getCiudad().getDescripcion() != null)
				texto = texto + spanO + "Ciudad: " + spanC
						+ listaARecuperar.getCiudad().getDescripcion() + br;
			if (listaARecuperar.getDireccion() != null)
				texto = texto + spanO + "Direcci�n: " + spanC
						+ listaARecuperar.getDireccion() + br;
		}
		return texto;
	}

	private void publicacionPortal(String texto, Long idConcurso,
			Long idConcursoGrupoPuestoAgr) {
		PublicacionPortal entity = null;

		entity = new PublicacionPortal();
		entity.setActivo(true);
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		entity.setFechaAlta(new Date());
		Concurso c = new Concurso();
		c = em.find(Concurso.class, idConcurso);
		entity.setConcurso(new Concurso());
		entity.setConcurso(c);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		ConcursoPuestoAgr ag = new ConcursoPuestoAgr();
		ag = em.find(ConcursoPuestoAgr.class, idConcursoGrupoPuestoAgr);
		entity.setConcursoPuestoAgr(ag);
		entity.setObservacion(true);
		entity.setTexto(texto);
		em.persist(entity);
	}

	/**
	 * Hace la publicacion en el portal
	 * 
	 * @return
	 */
	public String publicar() {
		
		
		
		if (tipoMT.equals("T")) {
			if (listaARecuperar == null || listaARecuperar.getIdLista() == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU87_msg_convocatoria"));
				return null;
			}
			Referencias ref = new Referencias();
			ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
					"TACHAS_RECLAMOS_MODIF");
			if (ref != null) {
				concursoPuestoAgr.setEstado(ref.getValorNum());
				em.merge(concursoPuestoAgr);
			}
			listaARecuperar.setFechaPublicacion(new Date());
			listaARecuperar.setUsuPublicacion(usuarioLogueado
					.getCodigoUsuario());
			if (conCedula.equals("S"))
				listaARecuperar.setConCedula(true);
			if (conCedula.equals("N"))
				listaARecuperar.setConCedula(false);
			Date fecVencimiento = fechaVencimiento();
			listaARecuperar.setFechaVencElegible(fecVencimiento);
			em.merge(listaARecuperar);
			guardarElegibles();

		}
		if (tipoMT.equals("M")) {
			Referencias ref = new Referencias();
			ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
					"TACHAS_RECLAMOS_MODIF");
			if (ref != null) {
				concursoPuestoAgr.setEstado(ref.getValorNum());
				em.merge(concursoPuestoAgr);
			}
			listaARecuperar = new Lista();
			listaARecuperar.setConcursoPuestoAgr(concursoPuestoAgr);
			listaARecuperar.setUsuMod(usuarioLogueado.getCodigoUsuario());
			listaARecuperar.setFechaMod(new Date());
			listaARecuperar.setTipo("LISTA CORTA");
			listaARecuperar.setFechaPublicacion(new Date());
			listaARecuperar.setUsuPublicacion(usuarioLogueado
					.getCodigoUsuario());
			if (conCedula.equals("S"))
				listaARecuperar.setConCedula(true);
			if (conCedula.equals("N"))
				listaARecuperar.setConCedula(false);
			// Date fecVencimiento = fechaVencimiento();
			// listaARecuperar.setFechaVencElegible(fecVencimiento);
			em.persist(listaARecuperar);
			guardarElegibles();
		}
		actualizarEstados();

		// em.flush();
		Observacion tablaObservacion = new Observacion();
		tablaObservacion.setConcurso(concurso);
		if (observacion != null && observacion.trim().isEmpty())
			tablaObservacion.setObservacion(observacion);

		tablaObservacion.setFechaAlta(new Date());
		tablaObservacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		tablaObservacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
		tablaObservacion.setIdTaskInstance(jbpmUtilFormController
				.getIdTaskInstanceActual(concursoPuestoAgr
						.getIdProcessInstance()));
		em.persist(tablaObservacion);
		// Publicar
		publicacionPortal(genTextoPublicacion(), concursoPuestoAgr
				.getConcurso().getIdConcurso(),
				concursoPuestoAgr.getIdConcursoPuestoAgr());
		// em.flush();

		for (EvalReferencialPostulante listCorta : listaCortaPostulantes)
			enviarEmails(listCorta);
				
		
		for (EvalReferencialPostulante listCort : listaRefPostulantesSinFiltrar){
		
			if (tipoMT.equals("T")) {
				
				if (listCort.getListaCorta() != null
						&& !listCort.getListaCorta())
									
					enviarEmails(listCort);
			}
		
			if (tipoMT.equals("M")) {
				
				if (listCort.getListaCorta() != null
						&& listCort.getListaCorta() && !listCort.getSeleccionado())
									
					enviarEmails(listCort);
			}
		}
		
		this.enviarEmailsNoSeleccionados();
				
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return nextTask();
		
		// return getUrlToPageElaborarListaCorta();
	}

	private void actualizarEstados() {
		PlantaCargoDet cargoDet = new PlantaCargoDet();
		cargoDet = buscarPuesto();
		if (cargoDet != null) {
			EstadoDet estadoDet = new EstadoDet();
			if (tipoMT.equals("T"))
				estadoDet = seleccionUtilFormController.buscarEstadoDet(
						"concurso", ActividadEnum.TACHAS_RECLAMOS_MODIF.getDescripcion());
			if (tipoMT.equals("M"))
				estadoDet = seleccionUtilFormController.buscarEstadoDet(
						"concurso", ActividadEnum.TACHAS_RECLAMOS_MODIF.getDescripcion());
			if (estadoDet != null) {
				cargoDet.setEstadoDet(estadoDet);
				em.merge(cargoDet);
				List<ConcursoPuestoDet> listaPuestosDetActualizar = new ArrayList<ConcursoPuestoDet>();
				listaPuestosDetActualizar = evaluarDocPostulantesFormController
						.traerGrupoPuestoDet(concursoPuestoAgr
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

	private void guardarElegibles() {
		List<EvalReferencialPostulante> listaEleg = buscarListaElegibles();
		Calendar hoy = Calendar.getInstance();
		hoy.add(Calendar.DATE, diasVencimiento());
		ListaElegiblesCab listaElegiblesCab = new ListaElegiblesCab();
		listaElegiblesCab.setVtoValidezLista(hoy.getTime());
		listaElegiblesCab.setConcursoPuestoAgr(concursoPuestoAgr);
		listaElegiblesCab.setFechaAlta(new Date());
		listaElegiblesCab.setCantElegibles(listaEleg.size());
		listaElegiblesCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());

		em.persist(listaElegiblesCab);

		for (EvalReferencialPostulante e : listaEleg) {
			ListaElegiblesDet listaElegiblesDet = new ListaElegiblesDet();
			listaElegiblesDet.setDisponible(true);
			listaElegiblesDet.setFechaAlta(new Date());
			listaElegiblesDet.setListaElegiblesCab(listaElegiblesCab);
			listaElegiblesDet.setPostulacion(e.getPostulacion());
			listaElegiblesDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());

			em.persist(listaElegiblesDet);

		}
	}

	private Integer diasVencimiento() {
		Query q = em
				.createQuery("select ref from Referencias ref "
						+ " where ref.activo is true and ref.dominio = 'LISTA_ELEGIBLES' ");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorNum();
	}

	private List<EvalReferencialPostulante> buscarElegibles() {
		Query q = em
				.createQuery("select eval from EvalReferencialPostulante eval "
						+ " where eval.activo is true and eval.listaCorta is true "
						+ "and eval.postulacion.activo is true and eval.seleccionado is false "
						+ "and eval.concursoPuestoAgr.idConcursoPuestoAgr = :id ");
		q.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());

		return q.getResultList();
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

	/**
	 * Envia los emails a los postulantes
	 * 
	 * @param refPost
	 */
	@SuppressWarnings("unchecked")
	public void enviarEmails(EvalReferencialPostulante refPost) {
		
		em.refresh(refPost);
		
		PersonaPostulante persona = new PersonaPostulante();
		persona = em.find(PersonaPostulante.class, refPost.getPostulacion().getPersonaPostulante().getIdPersonaPostulante());
		
		String dirEmail = persona.getEMail();
		String texto = recuperarTexto(refPost);
		try {
			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,
					null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Envia los emails a los postulantes que no han alcanzado el puntaje minimo
	 * 
	 *
	 *	 */
	@SuppressWarnings("unchecked")
	public void enviarEmailsNoSeleccionados() {
	try {
			
		this.listaNoSeleccionados = this.ObtenerListaNoSeleccionados();
				
		if (listaNoSeleccionados != null && listaNoSeleccionados.size() > 0) {
						
			for (Persona per : listaNoSeleccionados) {
				Persona persona = per;
				String dirEmail = persona.getEMail();
				
				this.asuntoNoSeleccion = "Notificaci�n de no selecci�n - Descalificaci�n ";
				
				String texto = "<p align=\"justify\"> <b> Estimado/a    "
						+ persona.getNombres()
						+ " "
						+ persona.getApellidos()
						+ ":</p> "
						+ "</b> <p align=\"justify\"> Lamentamos comunicarle que ha quedado descalificado del proceso de seleccion, ya que Ud. NO ha alcanzado el puntaje m�nimo necesario para el concurso: "
						+ " </p> "
						+ "<p> <b>"
						+ concurso.getNumero()
						+ "/"
						+ concurso.getAnhio()
						+ " de "
						+ configuracionUoCab.getDescripcionCorta()
						+ " - "
						+ concurso.getNombre()
						+ "</b> </p> "
						+ "<p align=\"center\"> <b>"
						+ concursoPuestoAgr.getDescripcionGrupo()
						+ "</b> </p> "
						+ "<p> Le agradecemos el inter&eacute;s que ha mostrado para este Concurso y deseamos poder contar con usted para nuestros pr&oacute;ximos concursos.</p> "
						+ "<p><b> Atentamente, <br/>"
						+ configuracionUoCab.getDenominacionUnidad() + "</b></p> "
						+ "<p> Para mayor informaci&oacute;n comunicarse con: "
						+ informacion() + " o acceda al Portal Paraguay Concursa haciendo <a href=\"https://www.paraguayconcursa.gov.py/\">click aqu&iacute;</a>.</p> ";
							
						usuarioPortalFormController.enviarMails(dirEmail, texto, asuntoNoSeleccion,	null);
			}
					
		}	
		
	}catch (Exception e) { e.printStackTrace();}
			
	}
	
	/**
	 * Metodo para obtener el listado de las personas que no llegaron a ser seleccionadoss
	 * 
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	private List<Persona> ObtenerListaNoSeleccionados() {
		
		String sql1 = " select per.* from seleccion.postulacion p "
						+ " join seleccion.eval_documental_cab eval_doc  "
						+ " on eval_doc.id_postulacion = p.id_postulacion "
						+ " join seleccion.persona_postulante p_pos "
						+ " on p_pos.id_persona_postulante = p.id_persona_postulante "
						+ " join general.persona per "
						+ " on per.id_persona = p_pos.id_persona "
						+ " left join ( "
						+ " Select per1.* from seleccion.eval_referencial_postulante eval_ref "
						+ " join seleccion.postulacion pos "
						+ " on pos.id_postulacion = eval_ref.id_postulacion "
						+ " join seleccion.persona_postulante p_pos "
						+ " on p_pos.id_persona_postulante = pos.id_persona_postulante "
						+ " join general.persona per1 "
						+ " on per1.id_persona = p_pos.id_persona "
						+ " where eval_ref.id_concurso_puesto_agr = "+concursoPuestoAgr.getIdConcursoPuestoAgr()
						+ " and pos.activo = 'true') aprobados "
						+ " on per.id_persona = aprobados.id_persona "
						+ " where p.id_concurso_puesto_agr = "
						+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and eval_doc.aprobado = 'true' and aprobados.id_persona is null ";

		List<Persona> listaNoSeleccionados = new ArrayList<Persona>();
		listaNoSeleccionados = em.createNativeQuery(sql1, Persona.class).getResultList();
		
		return listaNoSeleccionados;
		

	}
		
	
	

	@SuppressWarnings("unchecked")
	private String informacion() {
		String resultado = "";
		String sql1 = "select pres.* "
				+ "from seleccion.present_aclarac_doc pres "
				+ "where pres.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();

		String sql2 = "select pres.* "
				+ "from seleccion.present_aclarac_doc pres "
				+ "where pres.id_concurso = " + concurso.getIdConcurso();

		List<PresentAclaracDoc> listaPres = new ArrayList<PresentAclaracDoc>();
		listaPres = em.createNativeQuery(sql1, PresentAclaracDoc.class)
				.getResultList();
		if (listaPres.size() == 0) {
			listaPres = em.createNativeQuery(sql2, PresentAclaracDoc.class)
					.getResultList();
		}
		for (PresentAclaracDoc pr : listaPres) {
			resultado = resultado + " - " + pr.getEmail();
		}
		return resultado;

	}

	@SuppressWarnings("unchecked")
	private String listaDocumentos() {
		String sql = "SELECT DE.* "
				+ "FROM seleccion.matriz_doc_config_det DET "
				+ "JOIN seleccion.datos_especificos DE "
				+ "ON DE.id_datos_especificos = DET.id_datos_especificos_tipo_docu "
				+ "JOIN seleccion.matriz_doc_config_enc ENC "
				+ "ON ENC.id_matriz_doc_config_enc = DET.id_matriz_doc_config_enc "
				+ "WHERE ENC.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " AND DET.adjudicacion = true " + "AND DET.activo = true";
		String resultado = "";
		List<DatosEspecificos> listaDatos = new ArrayList<DatosEspecificos>();
		listaDatos = em.createNativeQuery(sql, DatosEspecificos.class)
				.getResultList();
		for (DatosEspecificos dt : listaDatos) {
			resultado = resultado + "<p><b> - " + dt.getDescripcion() + "</b> </p> ";
		}
		return resultado;
	}


	/**
	 * Crea el texto a ser enviado en el email
	 * 
	 * @param referen
	 * @return
	 */
	private String recuperarTexto(EvalReferencialPostulante referen) {
		String text = "";
		PersonaPostulante pers = new PersonaPostulante();
		pers = referen.getPostulacion().getPersonaPostulante();
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
		String fechaConv = "";
		if (listaARecuperar.getFechaConv() != null)
			fechaConv = sdf.format(listaARecuperar.getFechaConv());
		String horaInicio = "-";
		String horaFin = "-";
		SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
		if (listaARecuperar.getHoraDesde() != null){
		
			
			horaInicio = sdfHora.format(listaARecuperar.getHoraDesde()) ;
		}
		
		if (listaARecuperar.getHoraHasta() != null){
					
			horaFin = sdfHora.format(listaARecuperar.getHoraHasta()) ;
		}
		if (tipoMT.equals("T") && referen.getListaCorta()) {
	
			asunto="Notificaci�n";
			text = text
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ pers.getNombres()
					+ " "
					+ pers.getApellidos()
					+ ":</p> "
					+ "</b> <p align=\"justify\"> Para hacer el seguimiento de su postulaci�n al Puesto de: "
					+ concursoPuestoAgr.getDescripcionGrupo()+", "
					
					+ "del Concurso: "
					
					+ concurso.getNumero()
					+ "/"
					+ concurso.getAnhio()
					+ " de "
					+ configuracionUoCab.getDescripcionCorta()
					+ " - "
					+ concurso.getNombre()
					+ " <a href=\" https://www.paraguayconcursa.gov.py/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?"
					+ "idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ "&idConcurso="
					+ concurso.getIdConcurso()
					+ "&from=seleccion%2FbuscarConcurso%2Fevaluados%2Fevaluados&sugestion=&sugestionGrupo=&firstResult=0\""
					+ "> Haga click aqu&iacute;</a>"
					+ "</b> </p> "
					
					+ "<p><b> Atentamente, <br/>"
					+ configuracionUoCab.getDenominacionUnidad() + "</b></p> ";
			return text;
		}
		if (tipoMT.equals("T") && referen.getListaCorta() != null && !referen.getListaCorta()) {
			
			asunto="Notificaci�n";
			text = text
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ pers.getNombres()
					+ " "
					+ pers.getApellidos()
					+ ":</p> "
					+ "</b> <p align=\"justify\"> Para hacer el seguimiento de su postulaci�n al Puesto de: "
					+ concursoPuestoAgr.getDescripcionGrupo()+", "
					
					+ "del Concurso: "
					
					+ concurso.getNumero()
					+ "/"
					+ concurso.getAnhio()
					+ " de "
					+ configuracionUoCab.getDescripcionCorta()
					+ " - "
					+ concurso.getNombre()
					+ " <a href=\" https://www.paraguayconcursa.gov.py/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?"
					+ "idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ "&idConcurso="
					+ concurso.getIdConcurso()
					+ "&from=seleccion%2FbuscarConcurso%2Fevaluados%2Fevaluados&sugestion=&sugestionGrupo=&firstResult=0\""
					+ "> Haga click aqu&iacute;</a>"
					+ "</b> </p> "
					
					+ "<p><b> Atentamente, <br/>"
					+ configuracionUoCab.getDenominacionUnidad() + "</b></p> ";
			return text;
		}
		if (referen.getSeleccionado() != null && tipoMT.equals("M")
				&& referen.getSeleccionado()) {
		
			String _obs="";
			asunto="Notificaci�n";
			if(listaARecuperar.getObservacion()!=null){
				_obs= "Observaci&oacute;n:";
			}else{
				listaARecuperar.setObservacion("");
			}
			text = text
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ pers.getNombres()
					+ " "
					+ pers.getApellidos()
					+ ":</p> "
					+ "</b> <p align=\"justify\"> Para hacer el seguimiento de su postulaci�n al Puesto de: "
					+ concursoPuestoAgr.getDescripcionGrupo()+", "
					
					+ "del Concurso: "
					
					+ concurso.getNumero()
					+ "/"
					+ concurso.getAnhio()
					+ " de "
					+ configuracionUoCab.getDescripcionCorta()
					+ " - "
					+ concurso.getNombre()
					+ " <a href=\" https://www.paraguayconcursa.gov.py/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?"
					+ "idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ "&idConcurso="
					+ concurso.getIdConcurso()
					+ "&from=seleccion%2FbuscarConcurso%2Fevaluados%2Fevaluados&sugestion=&sugestionGrupo=&firstResult=0\""
					+ "> Haga click aqu&iacute;</a>"
					+ "</b> </p> "
					
					+ "<p><b> Atentamente, <br/>"
					+ configuracionUoCab.getDenominacionUnidad() + "</b></p> ";			return text;
		}
		if (referen.getSeleccionado() != null && tipoMT.equals("M")
			&& !referen.getSeleccionado()) {
	
			asunto="Notificaci�n";
			text = text
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ pers.getNombres()
					+ " "
					+ pers.getApellidos()
					+ ":</p> "
					+ "</b> <p align=\"justify\"> Para hacer el seguimiento de su postulaci�n al Puesto de: "
					+ concursoPuestoAgr.getDescripcionGrupo()+", "
					
					+ "del Concurso: "
					
					+ concurso.getNumero()
					+ "/"
					+ concurso.getAnhio()
					+ " de "
					+ configuracionUoCab.getDescripcionCorta()
					+ " - "
					+ concurso.getNombre()
					+ " <a href=\" https://www.paraguayconcursa.gov.py/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?"
					+ "idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ "&idConcurso="
					+ concurso.getIdConcurso()
					+ "&from=seleccion%2FbuscarConcurso%2Fevaluados%2Fevaluados&sugestion=&sugestionGrupo=&firstResult=0\""
					+ "> Haga click aqu&iacute;</a>"
					+ "</b> </p> "
					
					+ "<p><b> Atentamente, <br/>"
					+ configuracionUoCab.getDenominacionUnidad() + "</b></p> ";			
			return text;
		}
		return null;
	}

	/**
	 * pasa a la siguiente tarea
	 * 
	 * @return
	 */
	public String nextTask() {

		try {
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA);
			if (tipoMT.equals("T")) {
				jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.TACHAS_RECLAMOS_MODIF);
				jbpmUtilFormController.setTransition("next");
			} else if (tipoMT.equals("M")) {
				jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.TACHAS_RECLAMOS_MODIF);
				jbpmUtilFormController.setTransition("next");
			} else {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Ocurrio un error al inicializar el proceso. Verifique el estado del grupo.");
				return "null";
			}
			// Se pasa a la siguiente tarea
			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}

		} catch (Exception e) {
			return null;
		}

		return "next";
	}

	/**
	 * Verifica que todos los datos obligatorios hayan sido cargados
	 * 
	 * @return
	 */
	private Boolean faltanDatosObligatorios() {
		if (fecha == null || horaDesde == null || horaDesde.trim().isEmpty()
				|| horaHasta == null || horaHasta.trim().isEmpty()
				|| lugar == null || lugar.trim().isEmpty() || idCiudad == null
				|| direccion == null || direccion.trim().isEmpty()
				|| obs == null || obs.trim().isEmpty())
			return true;
		return false;
	}

	/**
	 * Calcula la fecha de vencimiento, sumando la cantidad de meses obtenido a
	 * la fecha actual
	 * 
	 * @return
	 */
	private Date fechaVencimiento() {
		String sql = "SELECT REF.VALOR_NUM "
				+ "FROM SELECCION.REFERENCIAS REF "
				+ "WHERE DOMINIO = 'LISTA_ELEGIBLES' " + "AND ACTIVO = TRUE";

		Object config = em.createNativeQuery(sql).getSingleResult();
		Integer meses = 0;
		if (config != null)
			meses = new Integer(config.toString());
		Calendar hoy = Calendar.getInstance();
		hoy.add(Calendar.MONTH, meses);
		return hoy.getTime();
	}

	@SuppressWarnings("unchecked")
	private void buscarEnLista() {
		String sql = "select l.* from seleccion.lista l where l.tipo = 'LISTA CORTA' "
				+ "and l.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		List<Lista> listaCorta = new ArrayList<Lista>();
		listaCorta = em.createNativeQuery(sql, Lista.class).getResultList();
		if (listaCorta.size() > 0) {
			listaARecuperar = new Lista();
			listaARecuperar = listaCorta.get(0);
			estaEnLista = true;
			Referencias ref = new Referencias();
			ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
					"ENTREVISTA MAI");
			if (ref != null) {
				if (ref.getValorNum() == concursoPuestoAgr.getEstado())
					cambioEstado = true;
				else
					cambioEstado = false;
			} else
				cambioEstado = false;
		}
		else
			estaEnLista = false;
	}

	/**
	 * Verifica que se haya realizado la publicacion para poder realizar el
	 * siguiente paso
	 * 
	 * @return
	 */
	private Boolean verificarPublicacion() {
		Referencias ref = new Referencias();
		if (tipoMT.equals("T")) {
			ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
					"ENTREVISTA MAI");
		}
		if (tipoMT.equals("M")) {
			ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
					"LISTA CORTA");
		}
		if (ref != null) {
			if (concursoPuestoAgr.getEstado().equals(ref.getValorNum()))
				return true;
		}
		return false;
	}

	/**
	 * Obtiene la direccion fisica a ser usada en la adjuncion de documentos
	 */
	private void obtenerDireccionFisica() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
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
	}

	/**
	 * M�todos que obtienen los urls necesarios
	 * 
	 * @return
	 */
	public String getUrlToPageConvocatoriaFinal() {

		return "/seleccion/evalReferencialPostulante/ListaCortaEdit.xhtml?fromToPage=seleccion/evalReferencialPostulante/ElaboracionListaCorta&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	public String getUrlToPageElaborarListaCorta() {
		return "/seleccion/evalReferencialPostulante/ElaboracionListaCorta.xhtml?fromToPage=seleccion/evalReferencialPostulante/ListaCortaEdit&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	public String getUrlToPageLista() {
		return "/seleccion/evalReferencialPostulante/ElaboracionListaCorta.xhtml?fromToPage=seleccion/evalReferencialPostulante/ElaboracionListaCorta&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
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
		ciudadSelecItem = new ArrayList<SelectItem>();
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
		if (ciudadSelecItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecItem.clear();

		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}

	private String buscarHora(String cod) {

		String[] arrayCodigo = cod.split(":");
		return arrayCodigo[0] + ":" + arrayCodigo[1];
	}

	/**
	 * Es llamado desde el boton Imprimir CU 168
	 */

	public void imprimir() {
		if (conDetalle.equals("S")) {
			EvaluaRefereFC evaluaRefereFC = (EvaluaRefereFC) Component
					.getInstance(EvaluaRefereFC.class, true);
			EvalsReferenciales evalsRefsRadio = EvalsReferenciales.CON_DET;
			evaluaRefereFC.setEvalsRefsRadio(evalsRefsRadio);

			evaluaRefereFC.getNivelEntidadOeeUtil().setIdConfigCab(
					configuracionUoCab.getIdConfiguracionUo());
			evaluaRefereFC.getNivelEntidadOeeUtil().setIdSinEntidad(
					sinEntidad.getIdSinEntidad());
			evaluaRefereFC.getNivelEntidadOeeUtil().setIdSinNivelEntidad(
					nivelEntidad.getIdSinNivelEntidad());
			evaluaRefereFC.getNivelEntidadOeeUtil().setDenominacionUnidad(
					configuracionUoCab.getDenominacionUnidad());
			evaluaRefereFC.getNivelEntidadOeeUtil().setNombreNivelEntidad(
					nivelEntidad.getNenNombre());
			evaluaRefereFC.getNivelEntidadOeeUtil().setNombreSinEntidad(
					sinEntidad.getEntNombre());
			evaluaRefereFC.setIdConcurso(concurso.getIdConcurso());
			evaluaRefereFC.setIdConcursoPuestoAgr(concursoPuestoAgr
					.getIdConcursoPuestoAgr());
			evaluaRefereFC.setPrintCedula(conCedula.equals("N") ? false : true);
			evaluaRefereFC.setlInPersonaConDetalleRep(listaInPostulante());

			evaluaRefereFC.imprimir();

		} else
			print();
	}

	private String listaInPostulante() {
		String lIn = "";
		for (EvalReferencialPostulante o : listaCortaPostulantes) {
			lIn += ","
					+ o.getPostulacion().getPersonaPostulante()
							.getIdPersonaPostulante() + "";
		}
		lIn = lIn.replaceFirst(",", "");
		if (!lIn.isEmpty()) {
			lIn = "(" + lIn + ")";
		}
		return lIn;
	}
	
	public void printCUEvalDet() {
		this.init2();
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("concurso_puesto_agr",this.idConcursoPuestoAgr);
		param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
		param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
		param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());

		HistorialActividadesGrupo historial = obtenerFechaActividad(this.concursoPuestoAgr.getIdConcursoPuestoAgr(), ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA.getDescripcion());
		if (historial==null || historial.getFechaFin()==null)
			param.put("fecha", "Aun no se ha publicado");
		else{
			Format formato = new SimpleDateFormat("dd-MM-yyyy");
			param.put("fecha", formato.format(historial.getFechaFin()));
		}

		JasperReportUtils.respondPDF("ListaEvaluacionesRealizadasPlanilla",	param, false, conn,usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}

	public void print() {

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		if (usuarioLogueado != null)
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
		else
			param.put("usuario", "Portal");
		param.put("concurso", codConcurso + " - " + concurso.getNombre());
		param.put(
				"nivel",
				nivelEntidad.getNenCodigo() + " - "
						+ nivelEntidad.getNenNombre());
		param.put("entidad",
				sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
		param.put("oee", configuracionUoCab.getOrden() + " - "
				+ configuracionUoCab.getDenominacionUnidad());
		param.put("grupo", concursoPuestoAgr.getDescripcionGrupo());
		List<Object[]> lista = consulta();
		if (lista == null) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
			return;
		}
		param.put("columna1",
				SeamResourceBundle.getBundle().getString("CU168_columna1"));
		
		if (conCedula.equals("N"))
			param.put("columna2",
					SeamResourceBundle.getBundle().getString("CU168_columna2"));
		if (conCedula.equals("S"))
			param.put("columna3",
					SeamResourceBundle.getBundle().getString("CU168_columna3"));
		param.put("puntaje_realizado","Puntaje Realizado");
		
		Integer cont = 0;
		try {
			for (Object[] obj : lista) {
				cont++;
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("numero", cont);
				
				if (conCedula.equals("N")) {
					if (obj[0] != null)
						map.put("cod_usuario", obj[0].toString());
				}
				if (conCedula.equals("S")) {
					if (obj[1] != null)
						map.put("ci", obj[1].toString());
				}
				if (obj[2] != null) {
					if (obj[2].toString() != null
							&& obj[2].toString().equals("SI"))
						map.put("lista_corta", "SI");
					if (obj[2].toString() != null
							&& obj[2].toString().equals("NO"))
						map.put("lista_corta", "NO");
				}
				if (obj[3] != null) {
					if (obj[3].toString() != null
							&& obj[3].toString().equals("SI"))
						map.put("seleccionado", "SI");
					if (obj[2].toString() != null
							&& obj[3].toString().equals("NO"))
						map.put("seleccionado", "NO");
				}
				if (obj[2] != null && obj[3] != null) {
					if (obj[2].toString().equals("SI")
							&& obj[3].toString().equals("SI"))
						map.put("sombreado", "SI");
					else
						map.put("sombreado", "NO");
				} else
					map.put("sombreado", "NO");
				
				if (obj[4] != null)
					map.put("puntaje_realizado", obj[4].toString());
				
				listaDatosReporte.add(map);
				
			}
			JasperReportUtils.respondPDF("RPT_CU168_Lista_Corta_Admitidos",	false, listaDatosReporte, param);

			
				conn.close();
			

		} catch (Exception e) {
			e.printStackTrace();

		}

	}


	public void imprimirListaFinalMerito(){
		try {
			
			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			Connection conn = JpaResourceBean.getConnection();
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("idConcursoPuestoAgr",this.concursoPuestoAgr.getIdConcursoPuestoAgr());
			param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
			param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
			param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());
			HistorialActividadesGrupo historial = obtenerFechaActividad(this.concursoPuestoAgr.getIdConcursoPuestoAgr(), ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA.getDescripcion());
			if (!validarTipoConcurso() &&(historial==null || historial.getFechaFin()==null))
				param.put("fecha", "Aun no se ha publicado");
			else if(validarTipoConcurso())
			{
				Query l = em.createQuery("select L from Lista L "
						+ "where L.tipo =:tipo and  L.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
				l.setParameter("tipo", "LISTA LARGA");
				l.setParameter("id_concurso_puesto_agr", concursoPuestoAgr.getIdConcursoPuestoAgr());
				List<Lista> auxLista = l.getResultList();
				if(auxLista.isEmpty())
				{
					param.put("fecha", "Aun no se ha publicado");
				}else{
					String aux = auxLista.get(0).getFechaPublicacion().toString().substring(0, 10);
					String aux2 = aux.substring(0, 5);
					aux2 = aux2 + aux.substring(8, 10) + "-";
					aux2 = aux2 + aux.substring(5, 7);
					param.put("fecha",aux2);				
				}

			}
			else{
				Format formato = new SimpleDateFormat("dd-MM-yyyy");
				param.put("fecha", formato.format(historial.getFechaFin()));
			}
			if(usuarioLogueado != null )
				param.put("usuario", usuarioLogueado.getCodigoUsuario());
			else
				param.put("usuario", "Visitante Portal");
			
			
			
			
			if(conCedula != null){
				
				if (conCedula.equals("N"))				
					JasperReportUtils.respondPDF("listaFinalMerito",	param, false, conn,usuarioLogueado);
				else if (conCedula.equals("S"))
					JasperReportUtils.respondPDF("listaFinalMerito_cedula",	param, false, conn,usuarioLogueado);
			
			}else 
				conCedula = esConCedula(this.concursoPuestoAgr.getIdConcursoPuestoAgr());
			
				if (conCedula.equals("N"))				
					JasperReportUtils.respondPDF("listaFinalMerito",	param, false, conn,usuarioLogueado);
				else if (conCedula.equals("S"))
					JasperReportUtils.respondPDF("listaFinalMerito_cedula",	param, false, conn,usuarioLogueado);
				
				//JasperReportUtils.respondPDF("listaFinalMerito_cedula",	param, false, conn,usuarioLogueado);
			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public void imprimirListaCortaTerna(){
		try {
			
			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			Connection conn = JpaResourceBean.getConnection();
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("idConcursoPuestoAgr",this.concursoPuestoAgr.getIdConcursoPuestoAgr());
			param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
			param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
			param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());
			HistorialActividadesGrupo historial = obtenerFechaActividad(this.concursoPuestoAgr.getIdConcursoPuestoAgr(), ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA.getDescripcion());
			if (!validarTipoConcurso() &&(historial==null || historial.getFechaFin()==null))
				param.put("fecha", "Aun no se ha publicado");
			else if(validarTipoConcurso())
			{
				Query l = em.createQuery("select L from Lista L "
						+ "where L.tipo =:tipo and  L.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
				l.setParameter("tipo", "LISTA LARGA");
				l.setParameter("id_concurso_puesto_agr", concursoPuestoAgr.getIdConcursoPuestoAgr());
				List<Lista> auxLista = l.getResultList();
				if(auxLista.isEmpty())
				{
					param.put("fecha", "Aun no se ha publicado");
				}else{
					String aux = auxLista.get(0).getFechaPublicacion().toString().substring(0, 10);
					String aux2 = aux.substring(0, 5);
					aux2 = aux2 + aux.substring(8, 10) + "-";
					aux2 = aux2 + aux.substring(5, 7);
					param.put("fecha",aux2);				
				}

			}
			else{
				Format formato = new SimpleDateFormat("dd-MM-yyyy");
				param.put("fecha", formato.format(historial.getFechaFin()));
			}
			if(usuarioLogueado != null )
				param.put("usuario", usuarioLogueado.getCodigoUsuario());
			else
				param.put("usuario", "Visitante Portal");
			
			
			
			if(conCedula != null){
			
				if (conCedula.equals("N"))				
					JasperReportUtils.respondPDF("listaCortaTerna",	param, false, conn,usuarioLogueado);
				else if (conCedula.equals("S"))
					JasperReportUtils.respondPDF("listaCortaTerna_cedula",	param, false, conn,usuarioLogueado);
			}else
				
				conCedula = esConCedula(this.concursoPuestoAgr.getIdConcursoPuestoAgr());
			
				if (conCedula.equals("N"))				
					JasperReportUtils.respondPDF("listaCortaTerna",	param, false, conn,usuarioLogueado);
				else if (conCedula.equals("S"))
					JasperReportUtils.respondPDF("listaCortaTerna_cedula",	param, false, conn,usuarioLogueado);
				//JasperReportUtils.respondPDF("listaCortaTerna_cedula",	param, false, conn,usuarioLogueado);
			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
			

	}
	
	

	/**
	 * busca los datos para ser enviados en el reporte
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> consulta() {
		String sql = "SELECT PERSONA_POST.USU_ALTA as cod_usuario, "
				+ "PERSONA_POST.DOCUMENTO_IDENTIDAD as ci, "
				+ "case when EVAL.LISTA_CORTA is true then 'SI' "
				+ "else 'NO' end as lista, case when EVAL.SELECCIONADO is true then 'SI' else 'NO' end AS seleccionado,  "
				+ "EVAL.PUNTAJE_REALIZADO "
				+ "FROM SELECCION.EVAL_REFERENCIAL_POSTULANTE EVAL  "
				+ "JOIN SELECCION.POSTULACION POST ON POST.ID_POSTULACION = EVAL.ID_POSTULACION  "
				+ "JOIN SELECCION.PERSONA_POSTULANTE PERSONA_POST  "
				+ "ON POST.ID_PERSONA_POSTULANTE = PERSONA_POST.ID_PERSONA_POSTULANTE  "
				+ "WHERE POST.ACTIVO = TRUE  " + "AND EVAL.ACTIVO = TRUE  "
				+ "AND  EVAL.ID_CONCURSO_PUESTO_AGR = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " ORDER BY EVAL.PUNTAJE_REALIZADO desc ";
		try {

			List<Object[]> config = em.createNativeQuery(sql).getResultList();
			if (config == null || config.size() == 0) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public void esConCedula() {
		conCedula = "S";
	}
	
	
	//Busca si el reporte seleccionado para la publicacion fue con Cedula o Sin Cedula
	public String esConCedula(Long idConcursoPuestoAgr){
		String sql = "select * from seleccion.lista where tipo like 'LISTA CORTA' and id_concurso_puesto_agr = "+idConcursoPuestoAgr;
		
		List<Lista> lista = em.createNativeQuery(sql,Lista.class).getResultList();
		
		if (lista.size() == 0)
			return "S";
		else if (lista.get(0).getConCedula() == null)
			
				return "S";
			else if (lista.get(0).getConCedula())
				return "S";
			else 
				return "N";
				
	}
	

	public String obtenerCodUsuario(Long id) {
		EvalReferencialPostulante evPost = new EvalReferencialPostulante();
		evPost = em.find(EvalReferencialPostulante.class, id);
		Long idP = evPost.getPostulacion().getIdPostulacion();
		Long idper = em.find(Postulacion.class, idP).getPersonaPostulante()
				.getIdPersonaPostulante();
		return em.find(PersonaPostulante.class, idper).getUsuAlta();
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
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

	public String getCodConcurso() {
		return codConcurso;
	}

	public void setCodConcurso(String codConcurso) {
		this.codConcurso = codConcurso;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getHoraDesde() {
		return horaDesde;
	}

	public void setHoraDesde(String horaDesde) {
		this.horaDesde = horaDesde;
	}

	public String getHoraHasta() {
		return horaHasta;
	}

	public void setHoraHasta(String horaHasta) {
		this.horaHasta = horaHasta;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public Long getIdDpto() {
		return idDpto;
	}

	public void setIdDpto(Long idDpto) {
		this.idDpto = idDpto;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public List<EvalReferencialPostulante> getListaRefPostulantesSinFiltrar() {
		return listaRefPostulantesSinFiltrar;
	}

	public void setListaRefPostulantesSinFiltrar(
			List<EvalReferencialPostulante> listaRefPostulantesSinFiltrar) {
		this.listaRefPostulantesSinFiltrar = listaRefPostulantesSinFiltrar;
	}

	public Integer getCantVacancia() {
		return cantVacancia;
	}

	public void setCantVacancia(Integer cantVacancia) {
		this.cantVacancia = cantVacancia;
	}

	public String getTipoMT() {
		return tipoMT;
	}

	public void setTipoMT(String tipoMT) {
		this.tipoMT = tipoMT;
	}

	public List<EvalReferencialPostulante> getListaRefPostulantesFiltrado() {
		return listaRefPostulantesFiltrado;
	}

	public void setListaRefPostulantesFiltrado(
			List<EvalReferencialPostulante> listaRefPostulantesFiltrado) {
		this.listaRefPostulantesFiltrado = listaRefPostulantesFiltrado;
	}

	public Boolean getEsPrimeraVez() {
		return esPrimeraVez;
	}

	public void setEsPrimeraVez(Boolean esPrimeraVez) {
		this.esPrimeraVez = esPrimeraVez;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public List<EvalReferencialPostulante> getListaCortaPostulantes() {
		return listaCortaPostulantes;
	}

	public void setListaCortaPostulantes(
			List<EvalReferencialPostulante> listaCortaPostulantes) {
		this.listaCortaPostulantes = listaCortaPostulantes;
	}

	public List<EvalReferencialPostulante> getListaEmpatados() {
		return listaEmpatados;
	}

	public void setListaEmpatados(List<EvalReferencialPostulante> listaEmpatados) {
		this.listaEmpatados = listaEmpatados;
	}

	public Integer getCantDesempatar() {
		return cantDesempatar;
	}

	public void setCantDesempatar(Integer cantDesempatar) {
		this.cantDesempatar = cantDesempatar;
	}

	public Boolean getConEmpates() {
		return conEmpates;
	}

	public void setConEmpates(Boolean conEmpates) {
		this.conEmpates = conEmpates;
	}

	public String getObsPopUp() {
		return obsPopUp;
	}

	public void setObsPopUp(String obsPopUp) {
		this.obsPopUp = obsPopUp;
	}

	public Integer getIndexPopUp() {
		return indexPopUp;
	}

	public void setIndexPopUp(Integer indexPopUp) {
		this.indexPopUp = indexPopUp;
	}

	public List<EvalReferencialPostulante> getListaSinEmpatar() {
		return listaSinEmpatar;
	}

	public void setListaSinEmpatar(
			List<EvalReferencialPostulante> listaSinEmpatar) {
		this.listaSinEmpatar = listaSinEmpatar;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public Boolean getEstaPublicado() {
		return estaPublicado;
	}

	public void setEstaPublicado(Boolean estaPublicado) {
		this.estaPublicado = estaPublicado;
	}

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(
			List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public String getExisteEnLista() {
		return existeEnLista;
	}

	public void setExisteEnLista(String existeEnLista) {
		this.existeEnLista = existeEnLista;
	}

	public Boolean getEstaEnLista() {
		return estaEnLista;
	}

	public void setEstaEnLista(Boolean estaEnLista) {
		this.estaEnLista = estaEnLista;
	}

	public Boolean getCambioEstado() {
		return cambioEstado;
	}

	public void setCambioEstado(Boolean cambioEstado) {
		this.cambioEstado = cambioEstado;
	}

	public Lista getListaARecuperar() {
		return listaARecuperar;
	}

	public void setListaARecuperar(Lista listaARecuperar) {
		this.listaARecuperar = listaARecuperar;
	}

	public String getConCedula() {
		return conCedula;
	}

	public void setConCedula(String conCedula) {
		this.conCedula = conCedula;
	}

	public String getConDetalle() {
		return conDetalle;
	}

	public void setConDetalle(String conDetalle) {
		this.conDetalle = conDetalle;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public Boolean getBloquearTodo() {
		return bloquearTodo;
	}

	public void setBloquearTodo(Boolean bloquearTodo) {
		this.bloquearTodo = bloquearTodo;
	}

	public Integer getEstadoGrupoDesierto() {
		return estadoGrupoDesierto;
	}

	public void setEstadoGrupoDesierto(Integer estadoGrupoDesierto) {
		this.estadoGrupoDesierto = estadoGrupoDesierto;
	}

	public Boolean getAsignarCategoria() {
		return asignarCategoria;
	}

	public void setAsignarCategoria(Boolean asignarCategoria) {
		this.asignarCategoria = asignarCategoria;
	}

	public List<EvalReferencialPostulante> getListaElegibles() {
		return listaElegibles;
	}

	public void setListaElegibles(List<EvalReferencialPostulante> listaElegibles) {
		this.listaElegibles = listaElegibles;
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	
	private boolean validarTipoConcurso() {
		
		Referencias ref = new Referencias();
		Referencias ref2 = new Referencias();
		Referencias ref3 = new Referencias();
		Referencias ref4 = new Referencias();
		Referencias ref5 = new Referencias();
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_LARGA");
		ref2 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_CORTA");
		ref3 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_FINALIZADO");
		ref4 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_TERNA_FINAL");
		ref5 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_ADJUDICADOS");
	
		if (concursoPuestoAgr.getEstado().equals(ref.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref2.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref3.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref4.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref5.getValorNum()))
		return true;
		else
		return false;
	}
}
