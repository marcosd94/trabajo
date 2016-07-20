package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.MatrizRefConf;
import py.com.excelsis.sicca.entity.MatrizRefConfDet;
import py.com.excelsis.sicca.entity.MatrizRefConfEnc;
import py.com.excelsis.sicca.entity.MatrizReferencial;
import py.com.excelsis.sicca.entity.MatrizReferencialDet;
import py.com.excelsis.sicca.entity.MatrizReferencialEnc;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.MatrizRefConfDTO;
import py.com.excelsis.sicca.util.MatrizRefDTO;
import py.com.excelsis.sicca.util.MatrizReferencialDTO;
import sun.net.idn.Punycode;

@Scope(ScopeType.PAGE)
@Name("confMatrizEvalFormController")
public class ConfMatrizEvaluacionFormController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2096131460149980428L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private Long id_concurso_puesto_agr;
	private List<MatrizRefDTO> listaMatrizRefDTO;
	private List<MatrizReferencial> listaMatrizRefercial;
	private List<MatrizReferencialDTO> listaSeleccionadosMatrizRefDTO;

	/* Permite tener la funcionalidad de inactivo/activo y todos */
	private Map<String, List<MatrizReferencialDTO>> mapSeleActivo;
	/* Permite tener la funcionalidad de inactivo/activo y todos */
	private Map<String, List<MatrizReferencialDTO>> mapSeleInactivo;
	private Map<String, MatrizReferencialDTO> mapCacheCabecera = new HashMap();
	/*
	 * Permite tener la funcionalidad de inactivo/activo y todos (para el
	 * editado)
	 */
	private Map<String, List<MatrizRefConfDTO>> mapSeleActivoEdit;
	/*
	 * Permite tener la funcionalidad de inactivo/activo y todos (para el
	 * editado)
	 */
	private Map<String, List<MatrizRefConfDTO>> mapSeleInactivoEdit;
	private Map<String, MatrizRefConfDTO> mapCacheCabeceraEdit = new HashMap();
	private List<MatrizRefConfDTO> listaEditarMatrizRefConfDTO;
	private Float montoMinFactor;
	private Float montoMaxFactor;
	private Boolean mostrarFormEditFactorItem = false;
	private Integer indiceSeleccionado;
	private Long idCabRefConf;
	private Long idCabRefConfEnc;
	private List<SelectItem> tipoEvalSelecItem;
	private java.math.BigInteger elemComboSel;
	private String usurioAlta;
	private String usurioMod;
	private Date laFechaAlta;
	private Date laFechaMod;
	private Boolean filtroActivo = true;
	private Boolean filtroTodos = false;
	private static String TIPO_CONCURSO_PUESTO = "GRUPO";
	private static String TIPO_HOMOLOGACION = "HOMOLOGACION";
	private String tipoOperacion = null;
	private Long idHomologacion;
	private Integer idMatRefSel;
	private Boolean habilitar = true;
	private Map<String, Boolean> cacheMostrarLinkSelect = new HashMap<String, Boolean>();
	private boolean habModoLectura = false;
	private String descripcion;
	
	private String criteriosEvaluacion;
	private String otrosDatos;
	private Long idMatrizRefConf;
	private Long idMatrizRefConfEnc;
	
	// Indica si se ingreso para editar la lista de factores e items.
	private Boolean modoEditado = false;
	private SeguridadUtilFormController seguridadUtilFormController;

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

	public void init2() {
		cacheMostrarLinkSelect.clear();
	}

	/*
	 * Esta varible facilita la tarea de controlar que no se queden cabeceras
	 * sin items. En resumen, guarda las ubicaciones de
	 * listaSeleccionadosMatrizRefDTO de cada cabecera
	 */
	private Map<String, List<String>> mapaEncabezados = new TreeMap<String, List<String>>();

	public String formatObligatorio(Boolean obligatorio) {
		String respuesta = (obligatorio ? "Si" : "No");
		return respuesta;
	}
	
	

	public void pdf() {
		try {
			Connection conn = JpaResourceBean.getConnection();			
			
			JasperReportUtils.respondPDF("RPT_CU85_matriz_seleccion",obtenerMapaParametros(), false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap<String, Object> obtenerMapaParametros() {
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT 	MR_ENC.DESCRIPCION AS FactoresEvaluacion,	MR_DET.DESCRIPCION AS Items,	MR_ENC.PUNTAJE_MAXIMO AS PuntajeMaximo,	MR_DET.PUNTAJE_MAXIMO AS GraduacionPuntajes ,	MR.PUNTAJE_MAXIMO  AS TotalPuntos, ");
		//*****************nuevo campo para el reporte RPT_CU85_matriz_seleccion; Werner.**************************
		sql.append(" MR_DET.criterios_evaluacion as criterios_evaluacion, ");
		//*********************************************************************************************************
		sql.append(" MR.otros_datos as otros_datos ");
		sql.append(" FROM	SELECCION.MATRIZ_REF_CONF_DET MR_DET JOIN	SELECCION.MATRIZ_REF_CONF_ENC MR_ENC ON 	MR_DET.ID_MATRIZ_REF_CONF_ENC = MR_ENC.ID_MATRIZ_REF_CONF_ENC JOIN 	SELECCION.MATRIZ_REF_CONF MR ON 	MR.ID_MATRIZ_REF_CONF = MR_ENC.ID_MATRIZ_REF_CONF ");
		sql.append(" WHERE ");
		sql.append(" MR_DET.activo is true and MR_ENC.activo is true and MR.activo is true and ");
		sql.append(" MR.TIPO ='GRUPO' and ");
		sql.append(" MR.ID_CONCURSO_PUESTO_AGR =  "
				+ grupoPuestosController.getIdConcursoPuestoAgr());
		sql.append(" ORDER BY	MR_ENC.nro_orden ");
		param.put("sql", sql.toString());
		grupoPuestosController.initCabecera();
		param.put("nivel", grupoPuestosController.getSinNivelEntidad()
				.getNenCodigo()
				+ "-"
				+ grupoPuestosController.getSinNivelEntidad().getNenNombre());
		param.put("oee", grupoPuestosController.getConfiguracionUoCab()
				.getOrden()
				+ "-"
				+ grupoPuestosController.getConfiguracionUoCab()
						.getDenominacionUnidad());

		param.put("entidad", grupoPuestosController.getSinEntidad()
				.getEntCodigo()
				+ " - "
				+ grupoPuestosController.getSinEntidad().getEntNomabre());
		param.put("grupoPuestos", grupoPuestosController.getConcursoPuestoAgr()
				.getDescripcionGrupo());
		param.put("concurso", grupoPuestosController.getConcursoPuestoAgr()
				.getConcurso().getNombre());

		return param;

	}

	public void search() {
		cargarLista(elemComboSel);
	}

	public void searchAll() {
		elemComboSel = null;
		search();
		buildTipoEvaluacionSelectItem();
	}

	private void buildTipoEvaluacionSelectItem() {
		String sql = "SELECT DE.ID_DATOS_ESPECIFICOS, DE.DESCRIPCION FROM"
				+ " SELECCION.DATOS_ESPECIFICOS  DE JOIN SELECCION.DATOS_GENERALES    DG ON"
				+ " DG.ID_DATOS_GENERALES  = DE.ID_DATOS_GENERALES "
				+ " WHERE DG.DESCRIPCION  = 'TIPOS DE EVALUACION' AND DE.ACTIVO = TRUE ORDER BY 1";
		List<Object> elResultList = em.createNativeQuery(sql).getResultList();
		if (elResultList.size() > 0) {
			if (tipoEvalSelecItem == null)
				tipoEvalSelecItem = new ArrayList<SelectItem>();
			else
				tipoEvalSelecItem.clear();
			tipoEvalSelecItem.add(new SelectItem(null, SeamResourceBundle
					.getBundle().getString("opt_select_one")));
			for (Object r : elResultList) {
				System.out.println("La clase: "
						+ (((Object[]) r)[0]).getClass());
				tipoEvalSelecItem.add(new SelectItem(
						(java.math.BigInteger) ((Object[]) r)[0],
						(String) ((Object[]) r)[1]));
			}
		}
	}

	public Boolean mostrarLinkSelect(Long idMatrizReferencial) {
		if (cacheMostrarLinkSelect.get(idMatrizReferencial.toString()) != null)
			return cacheMostrarLinkSelect.get(idMatrizReferencial.toString());
		else
			return false;
	}

	/**
	 * Verifica si una matriz referencial estï¿½ completa.
	 * 
	 * @param matrizReferencial
	 * @return
	 */

	public Boolean completarMatriz(MatrizReferencial matrizReferencial) {

		if (cacheMostrarLinkSelect.get(matrizReferencial
				.getIdMatrizReferencial().toString()) != null) {
			return cacheMostrarLinkSelect.get(matrizReferencial
					.getIdMatrizReferencial().toString());
		}
		Boolean respuesta = true;
		float sumPuntMaxEnc = 0;
		float sumPuntMaxItems = 0;
		/*
		 * Tabla SEL_MATRIZ_REFERENCIAL_ENC: verificar que la sumatoria de la
		 * columna puntaje_maximo de cï¿½mo resultado el valor de la columna
		 * puntaje_maximo de la tabla SEL_MATRIZ_REFERENCIAL.
		 */
		for (MatrizReferencialEnc o : matrizReferencial
				.getMatrizReferencialEncs()) {
			if (o.getActivo())
			sumPuntMaxEnc += o.getPuntajeMaximo().floatValue();
			sumPuntMaxItems = 0;
			if (o.getSumItemsSN()) {
				/*
				 * Por cada factor de evaluaciï¿½n (que se encuentra en la tabla
				 * SEL_MATRIZ_REFERENCIAL_ENC) verificar en sus ï¿½tems de
				 * factor: Tabla SEL_MATRIZ_REFERENCIAL_DET: verificar que la
				 * sumatoria de la columna puntaje_maximo de cï¿½mo resultado el
				 * valor de la columna puntaje mï¿½ximo de la tabla
				 * SEL_MATRIZ_REFERENCIAL_ENC (SI SEL_MATRIZ_REFERENCIAL_ENC.
				 * sum_items_s_n = TRUE)
				 */
				for (MatrizReferencialDet p : o.getMatrizReferencialDets()) {
					if (o.getActivo() && p.getActivo()) {
						sumPuntMaxItems += p.getPuntajeMaximo().floatValue();
					}
				}
				if(o.getActivo()){
					if (!(sumPuntMaxItems + "").equals(o.getPuntajeMaximo() + "")) {
						respuesta = false;
					}
				}
			}
		}
		if (!(sumPuntMaxEnc + "").equals(matrizReferencial.getPuntajeMaximo()
				.floatValue() + "")) {
			respuesta = false;
		}
		/* Controlar que todo este cargado */
		String query = " select * from seleccion.matriz_referencial_det this_  "
				+ " inner join seleccion.matriz_referencial_enc encab1_ on this_.id_matriz_referencial_enc=encab1_.id_matriz_referencial_enc "
				+ "  inner join seleccion.matriz_referencial cab2_ on encab1_.id_matriz_referencial=cab2_.id_matriz_referencial"
				+ " where cab2_.id_matriz_referencial= "
				+ matrizReferencial.getIdMatrizReferencial();
		List lista = em.createNativeQuery(query).getResultList();
		if (lista.size() == 0) {
			respuesta = false;
		}
		/* Se agrega al cache el id de la matriz referencial */
		cacheMostrarLinkSelect.put(matrizReferencial.getIdMatrizReferencial()
				.toString(), respuesta);
		return respuesta;
	}

	public Boolean precondSave() {
		// Controlar la restricion 4 del ETC
		Boolean control = true;
		boolean error = false;
		Integer indice = 0;
		Integer indiceAux = 0;
		boolean bandera= true;
		Float puntaje = null;
		if (!modoEditado) {
			while (control
					&& listaSeleccionadosMatrizRefDTO != null
					&& listaSeleccionadosMatrizRefDTO.size() > 0
					&& indice.intValue() < listaSeleccionadosMatrizRefDTO
							.size()) {
				for (MatrizReferencialDTO o : listaSeleccionadosMatrizRefDTO) {
					if (o.getMostrarAcciones()) {
						/*control = valSumatoriaAtomico(indice, o
								.getMatrizReferencialDet().getPuntajeMaximo());
						if (!control) {
							break;
						}*/
						control = valSumatoriaAtomico(indice, o.getMatrizReferencialDet().getPuntajeMaximo());
						if (!control) {
							break;
						}
						//varifica que no sea nulo el campo acausa del "LAZY" puede que sea nulo!
						if (o.getMatrizReferencialDet().getMatrizReferencialEnc()
								.getSumItemsSN()==null) {
							Session session = jpaResourceBean.getSession();
							Criteria crit3 = session.createCriteria(MatrizReferencialEnc.class);
							crit3.add(Restrictions.eq("idMatrizRefConfEnc", listaSeleccionadosMatrizRefDTO
									.get(indice.intValue()).getMatrizReferencialDet()
									.getMatrizReferencialEnc().getIdMatrizReferencialEnc()));
							List<MatrizReferencialEnc> lista3 = new ArrayList<MatrizReferencialEnc>();
							lista3 = crit3.list();
							//obtiene resultado de la bd y lo almacena en la variable
							o.getMatrizReferencialDet().setMatrizReferencialEnc(lista3.get(0));
						}
						/*
						 * verifica si existe un valor maximo distinto al del MatrizReferencialEnc
						 *  de los que no son sumables entonces hay error y se almacena el indice
						 *  sino, setea la bandera en false porque encontro un valor que cumple la 
						 *  condicion y setea el error a false.
						 *  La bandera hace que no se controle de vuelta el if mas abajo si es que se
						 *  encontro un valor que cumpla
						 */
						if(!o.getMatrizReferencialDet().getMatrizReferencialEnc()
								.getSumItemsSN() && o.getMatrizReferencialDet().getActivo()){
							if(o.getMatrizReferencialDet().getMatrizReferencialEnc().getPuntajeMaximo().floatValue() != 
									o.getMatrizReferencialDet().getPuntajeMaximo().floatValue() && bandera	){
								error = true;
								indiceAux = indice;
								puntaje = o.getMatrizReferencialDet().getMatrizReferencialEnc().getPuntajeMaximo();
							}else{
								error = false;
								bandera = false;
							}
						}	
					}
					indice++;
				}
			}
			//envia mensaje de error por no encontrar valores iguales al maximo de entre los no sumables
			if(error){
				statusMessages.add(
						Severity.WARN,
						"Algun valor de items del factor con Nº. Orden "
								+ listaSeleccionadosMatrizRefDTO
										.get(indiceAux.intValue())
										.getMatrizReferencialDet()
										.getMatrizReferencialEnc()
										.getNroOrden()
								+ " debe ser igual a "
								+ puntaje);
				return false;
			}
		} else {
			
			while (control && listaEditarMatrizRefConfDTO != null
					&& listaEditarMatrizRefConfDTO.size() > 0
					&& indice.intValue() < listaEditarMatrizRefConfDTO.size()) {
				for (MatrizRefConfDTO o : listaEditarMatrizRefConfDTO) {
					//solo controla los que poseen acciones
					if (o.getMostrarAcciones()) {
						control = valSumatoriaAtomico(indice, o
							.getMatrizRefConfDet().getPuntajeMaximo());
						if (!control) {
							break;
						}
						//varifica que no sea nulo el campo acausa del "LAZY" puede que sea nulo!
						if (o.getMatrizRefConfDet().getMatrizRefConfEnc()
								.getSumItemsSN()==null) {
							Session session = jpaResourceBean.getSession();
							Criteria crit3 = session.createCriteria(MatrizRefConfEnc.class);
							crit3.add(Restrictions.eq("idMatrizRefConfEnc", listaEditarMatrizRefConfDTO
									.get(indice.intValue()).getMatrizRefConfDet()
									.getMatrizRefConfEnc().getIdMatrizRefConfEnc()));
							List<MatrizRefConfEnc> lista3 = new ArrayList<MatrizRefConfEnc>();
							lista3 = crit3.list();
							//obtiene resultado de la bd y lo almacena en la variable
							o.getMatrizRefConfDet().setMatrizRefConfEnc(lista3.get(0));
						}
						/*
						 * verifica si existe un valor maximo distinto al del matrefenc
						 *  de los que no son sumables entonces hay error y se almacena el indice
						 *  sino, setea la bandera en false porque encontro un valor que cumple la 
						 *  condicion y setea el error a false.
						 *  La bandera hace que no se controle de vuelta el if mas abajo si es que se
						 *  encontro un valor que cumpla
						 */
						if(!o.getMatrizRefConfDet().getMatrizRefConfEnc()
								.getSumItemsSN() && o.getMatrizRefConfDet().isActivo()){
							if(o.getMatrizRefConfDet().getMatrizRefConfEnc().getPuntajeMaximo() != 
									o.getMatrizRefConfDet().getPuntajeMaximo() && bandera	){
								error = true;
								indiceAux = indice;
								puntaje = o.getMatrizRefConfDet().getMatrizRefConfEnc().getPuntajeMaximo();
							}else{
								error = false;
								bandera = false;
							}
						}
						
					}
					indice++;
				}
			}
			//envia mensaje de error por no encontrar valores iguales al maximo de entre los no sumables
			if(error){
				statusMessages.add(
						Severity.WARN,
						"Algun valor de items del factor con Nº. Orden "
								+ listaEditarMatrizRefConfDTO
										.get(indiceAux.intValue())
										.getMatrizRefConfDet()
										.getMatrizRefConfEnc()
										.getNroOrden()
								+ " debe ser igual a "
								+ puntaje);
				return false;
			}
		}
		
		return control;
	}

	
	@Transactional
	public String save(Long idCampo, String tipo) {
		if (!precondSave()) {
			return "FAIL";
		}
		try {
			Map<String, String> lMatriz = new HashMap<String, String>();
			Map<String, String> lMatrizEnc = new HashMap<String, String>();
			String idMatRef = "";
			String idMatRefEnc = "";
			Date fechaAlta = new Date();
			for (MatrizReferencialDTO o : listaSeleccionadosMatrizRefDTO) {
				// Se hace asi para asegurar no meter doble
				if (o.getMatrizReferencialDet() != null) {
					idMatRef = o.getMatrizReferencialDet()
							.getMatrizReferencialEnc().getMatrizReferencial()
							.getIdMatrizReferencial().toString();
					idMatRefEnc = o.getMatrizReferencialDet()
							.getMatrizReferencialEnc()
							.getIdMatrizReferencialEnc().toString();
				} else {
					idMatRef = o.getMatrizReferencialEnc()
							.getMatrizReferencial().getIdMatrizReferencial()
							.toString();
					idMatRefEnc = o.getMatrizReferencialEnc()
							.getIdMatrizReferencialEnc().toString();
				}
				// Cabecera
				if (!lMatriz.keySet().contains(idMatRef)) {
					MatrizReferencial matRef = null;
					if (o.getMatrizReferencialDet() != null) {
						matRef = o.getMatrizReferencialDet()
								.getMatrizReferencialEnc()
								.getMatrizReferencial();
					} else {
						matRef = o.getMatrizReferencialEnc()
								.getMatrizReferencial();
					}
					MatrizRefConf matRefConf = new MatrizRefConf();
					matRefConf.setOtrosDatos(otrosDatos);
					matRefConf.setActivo(true);
					matRefConf.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					matRefConf.setFechaAlta(fechaAlta);
					if (tipo.equalsIgnoreCase(TIPO_CONCURSO_PUESTO)) {
						matRefConf
								.setConcursoPuestoAgr(new ConcursoPuestoAgr());
						matRefConf.getConcursoPuestoAgr()
								.setIdConcursoPuestoAgr(idCampo);
					} else if (tipo.equalsIgnoreCase(TIPO_HOMOLOGACION)) {
						matRefConf
								.setHomologacionPerfilMatriz(new HomologacionPerfilMatriz());
						matRefConf.getHomologacionPerfilMatriz()
								.setIdHomologacion(idCampo);
					}

					matRefConf
							.setDatosEspecificos(matRef.getDatosEspecificos());
					matRefConf.setDescripcion(matRef.getDescripcion());
					matRefConf.setPuntajeMaximo(matRef.getPuntajeMaximo());
					matRefConf.setTipo(tipo);
					em.persist(matRefConf);
					// Guarda la nueva cabecera
					lMatriz.put(idMatRef, matRefConf.getIdMatrizRefConf() + "");
				}
				// Detalle
				if (!lMatrizEnc.keySet().contains(idMatRefEnc)) {
					MatrizReferencialEnc matRefEnc = null;
					if (o.getMatrizReferencialDet() != null) {

						Long idmatRefEncSel = o.getMatrizReferencialDet()
								.getMatrizReferencialEnc()
								.getIdMatrizReferencialEnc();
						matRefEnc = em.find(MatrizReferencialEnc.class,
								idmatRefEncSel);
					} else {
						Long idmatRefEncSel = o.getMatrizReferencialEnc()
								.getIdMatrizReferencialEnc();
						matRefEnc = em.find(MatrizReferencialEnc.class,
								idmatRefEncSel);

					}
					MatrizRefConfEnc matRefConfEnc = new MatrizRefConfEnc();
					matRefConfEnc.setActivo(true);
					matRefConfEnc.setDatosEspecificos(matRefEnc
							.getDatosEspecificos());
					matRefConfEnc.setDescripcion(matRefEnc.getDescripcion());
					matRefConfEnc.setFechaAlta(fechaAlta);
					matRefConfEnc
							.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					matRefConfEnc.setMatrizRefConf(new MatrizRefConf());
					matRefConfEnc.getMatrizRefConf().setIdMatrizRefConf(
							new Long(lMatriz.get(matRefEnc
									.getMatrizReferencial()
									.getIdMatrizReferencial().toString())));
					matRefConfEnc.setNroOrden(matRefEnc.getNroOrden());
					matRefConfEnc
							.setObligatorioSN(matRefEnc.getObligatorioSN());
					matRefConfEnc
							.setPuntajeMaximo(matRefEnc.getPuntajeMaximo());
					matRefConfEnc.setSumItemsSN(matRefEnc.getSumItemsSN());
					matRefConfEnc
							.setUsuAlta(usuarioLogueado.getCodigoUsuario());

					em.persist(matRefConfEnc);
					// Guarda la nueva Cabecera
					lMatrizEnc.put(idMatRefEnc,
							matRefConfEnc.getIdMatrizRefConfEnc() + "");
				}
				if (o.getMatrizReferencialDet() != null) {
					// SubDetalle
					MatrizReferencialDet matDet = null;
					matDet = o.getMatrizReferencialDet();
					MatrizRefConfDet matRefConfDet = new MatrizRefConfDet();
					matRefConfDet.setActivo(matDet.getActivo());
					matRefConfDet.setDescripcion(matDet.getDescripcion());
					matRefConfDet.setFechaAlta(fechaAlta);
					matRefConfDet.setMatrizRefConfEnc(new MatrizRefConfEnc());
					matRefConfDet.getMatrizRefConfEnc().setIdMatrizRefConfEnc(
							new Long(lMatrizEnc.get(idMatRefEnc)));
					matRefConfDet.setPuntajeMaximo(matDet.getPuntajeMaximo());
					matRefConfDet.setPuntajeMinimo(matDet.getPuntajeMinimo());
					matRefConfDet.setCriteriosEvaluacion(matDet.getCriteriosEvaluacion());
					matRefConfDet
							.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(matRefConfDet);
				}
			}
			em.flush();

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "persisted";

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public String updateAll() {
		//persistir los elementos de la lista: listaEditarMatrizRefConfDTO
		//listaEditarMatrizRefConfDTO
		if (!precondSave()) {
			return "FAIL";
		}
		try {
			Date fechaMod = new Date();
			// Se organiza lo que se guarda. Se une inactivos con activos
			//listaEditarMatrizRefConfDTO.clear();
			//copiarTodos(null, mergeActivoInactivoEdit());
			Long idCab = null;
			for (MatrizRefConfDTO o : listaEditarMatrizRefConfDTO) {
				//if (o.getMostrarAcciones()) {
					// Este es un detalle. Entonces se realiza la actualizacion
					// correspondiente
				
					idCab = o.getMatrizRefConfDet().getMatrizRefConfEnc().getIdMatrizRefConfEnc();
					
					o.getMatrizRefConfDet().setFechaMod(fechaMod);
					o.getMatrizRefConfDet().setUsuMod(
							usuarioLogueado.getCodigoUsuario());
					o.setMatrizRefConfDet(em.merge(o.getMatrizRefConfDet()));
					//}
			}
			em.flush();
			if(idCab != null){
				MatrizRefConfEnc m = em.find(MatrizRefConfEnc.class, idCab);
				m.getMatrizRefConf().setOtrosDatos(otrosDatos);
				em.merge(m);
			}
			em.flush();

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "updated";

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	
	public String update() {
		if (!precondSave()) {
			return "FAIL";
		}
		try {
			Date fechaMod = new Date();
			// Se organiza lo que se guarda. Se une inactivos con activos
			listaEditarMatrizRefConfDTO.clear();
			copiarTodos(null, mergeActivoInactivoEdit());
			for (MatrizRefConfDTO o : listaEditarMatrizRefConfDTO) {
				if (o.getMostrarAcciones()) {
					// Este es un detalle. Entonces se realiza la actualizacion
					// correspondiente
					o.getMatrizRefConfDet().setFechaMod(fechaMod);
					o.getMatrizRefConfDet().setUsuMod(
							usuarioLogueado.getCodigoUsuario());
					o.setMatrizRefConfDet(em.merge(o.getMatrizRefConfDet()));
				}
			}
			em.flush();

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "updated";

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	
	public String eliminarPlantilla(){
		if (!precondSave()) {
			return "FAIL";
		}
		try {
			Date fechaMod = new Date();
			MatrizRefConf matrizRefConf = em.find(MatrizRefConf.class, idMatrizRefConf);
			matrizRefConf.setActivo(false);
			matrizRefConf.setFechaMod(fechaMod);
			matrizRefConf.setUsuMod(
					usuarioLogueado.getCodigoUsuario());
			listaMatrizRefDTO.clear();
			em.merge(matrizRefConf);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "OK";
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
	}
	
	public void cancelarEditFactorItems() {
		limpiarVarFactorItemForm();
		// Secuencia
		mostrarFormEditFactorItem = false;
	}

	private void limpiarVarFactorItemForm() {
		montoMaxFactor = null;
		montoMinFactor = null;
		indiceSeleccionado = 0;
	}

	private Float calcSumatariaPuntMax(Integer indiceSeleccionado,
			Float montoMaxFactor) {
		Float vViejoMax = null;
		String idEncabezado = null;
		String idEncabezadoActual = "";
		float sumPuntMax = 0;
		// Se diferencia el modo editado del modo de creacion puesto que los
		// datos son de lugares diferentes.
		if (!modoEditado) {
			if (!listaSeleccionadosMatrizRefDTO
					.get(indiceSeleccionado.intValue())
					.getMatrizReferencialDet().getMatrizReferencialEnc()
					.getSumItemsSN()) {
				sumPuntMax = montoMaxFactor.floatValue();
				return sumPuntMax;
			}
		} else {
			MatrizRefConfEnc matrizRefConfEnc = em.find(
					MatrizRefConfEnc.class,
					listaEditarMatrizRefConfDTO
							.get(indiceSeleccionado.intValue())
							.getMatrizRefConfDet().getMatrizRefConfEnc()
							.getIdMatrizRefConfEnc());
			if (!matrizRefConfEnc.getSumItemsSN()) {
				sumPuntMax = montoMaxFactor.floatValue();
				return sumPuntMax;
			}
		}
		if (!modoEditado) {
			vViejoMax = listaSeleccionadosMatrizRefDTO
					.get(indiceSeleccionado.intValue())
					.getMatrizReferencialDet().getPuntajeMaximo();
			idEncabezado = listaSeleccionadosMatrizRefDTO
					.get(indiceSeleccionado.intValue())
					.getMatrizReferencialDet().getMatrizReferencialEnc()
					.getIdMatrizReferencialEnc().toString();
			// Realizar la sumatoria
			for (MatrizReferencialDTO o : listaSeleccionadosMatrizRefDTO) {
				// Las cabeceras no llevan Dets!
				if (o.getMatrizReferencialDet() != null
						&& o.getMatrizReferencialDet().getActivo()) {
					idEncabezadoActual = o.getMatrizReferencialDet()
							.getMatrizReferencialEnc()
							.getIdMatrizReferencialEnc().toString();
					if (idEncabezado.equalsIgnoreCase(idEncabezadoActual)) {
						sumPuntMax += o.getMatrizReferencialDet()
								.getPuntajeMaximo().floatValue();
					}

				}
			}
		} else {
			vViejoMax = listaEditarMatrizRefConfDTO
					.get(indiceSeleccionado.intValue()).getMatrizRefConfDet()
					.getPuntajeMaximo();
			idEncabezado = listaEditarMatrizRefConfDTO
					.get(indiceSeleccionado.intValue()).getMatrizRefConfDet()
					.getMatrizRefConfEnc().getIdMatrizRefConfEnc()
					+ "";
			// Realizar la sumatoria
			for (MatrizRefConfDTO o : listaEditarMatrizRefConfDTO) {
				// Las cabeceras no llevan Dets!
				if (o.getMostrarAcciones()
						&& o.getMatrizRefConfDet().isActivo()) {
					idEncabezadoActual = o.getMatrizRefConfDet()
							.getMatrizRefConfEnc().getIdMatrizRefConfEnc()
							+ "";
					if (idEncabezado.equalsIgnoreCase(idEncabezadoActual)) {
						sumPuntMax += o.getMatrizRefConfDet()
								.getPuntajeMaximo();
					}

				}
			}
		}

		// Restar el valor viejo y sumar el nuevo
		sumPuntMax = sumPuntMax - vViejoMax.floatValue();
		sumPuntMax = sumPuntMax + montoMaxFactor.floatValue();
		return sumPuntMax;
	}

	private Boolean valSumatoriaParcial(Integer indiceSeleccionado,
			Float montoMaxFactor) {
		// Controlar la restricion 4 del ETC
		// Obtener la sumatoria
		Float sumPuntMax = calcSumatariaPuntMax(indiceSeleccionado,
				montoMaxFactor);
		Float puntajeMaxEsperado = null;
		if (!modoEditado) {
			puntajeMaxEsperado = listaSeleccionadosMatrizRefDTO
					.get(indiceSeleccionado.intValue())
					.getMatrizReferencialDet().getMatrizReferencialEnc()
					.getPuntajeMaximo();

			if (!listaSeleccionadosMatrizRefDTO
					.get(indiceSeleccionado.intValue())
					.getMatrizReferencialDet().getMatrizReferencialEnc()
					.getSumItemsSN()) {
				if (sumPuntMax.compareTo(puntajeMaxEsperado) > 0) {
					statusMessages.add(
							Severity.WARN,
							"La sumatoria de items del factor con Nº. Orden "
									+ listaSeleccionadosMatrizRefDTO
											.get(indiceSeleccionado.intValue())
											.getMatrizReferencialDet()
											.getMatrizReferencialEnc()
											.getNroOrden()
									+ " debe ser menor o igual a "
									+ puntajeMaxEsperado + ". Valor actual: "
									+ sumPuntMax + ". Verifique.");
					return false;
				}
			}

		} else {
			if (listaEditarMatrizRefConfDTO.get(indiceSeleccionado.intValue())
					.getMatrizRefConfDet().getMatrizRefConfEnc()
					.getSumItemsSN()==null) {
				Session session = jpaResourceBean.getSession();
				Criteria crit3 = session.createCriteria(MatrizRefConfEnc.class);
				crit3.add(Restrictions.eq("idMatrizRefConfEnc", listaEditarMatrizRefConfDTO
						.get(indiceSeleccionado.intValue()).getMatrizRefConfDet()
						.getMatrizRefConfEnc().getIdMatrizRefConfEnc()));
				List<MatrizRefConfEnc> lista3 = new ArrayList<MatrizRefConfEnc>();
				lista3 = crit3.list();
				listaEditarMatrizRefConfDTO.get(indiceSeleccionado.intValue())
				.getMatrizRefConfDet().setMatrizRefConfEnc(lista3.get(0));
			}
			
			if (!listaEditarMatrizRefConfDTO.get(indiceSeleccionado.intValue())
					.getMatrizRefConfDet().getMatrizRefConfEnc()
					.getSumItemsSN()) {
				puntajeMaxEsperado = listaEditarMatrizRefConfDTO
						.get(indiceSeleccionado.intValue()).getMatrizRefConfDet()
						.getMatrizRefConfEnc().getPuntajeMaximo();
				if (sumPuntMax.compareTo(puntajeMaxEsperado) > 0) {
					statusMessages.add(
							Severity.WARN,
							"La sumatoria de items del factor con Nº. Orden "
									+ listaEditarMatrizRefConfDTO
											.get(indiceSeleccionado.intValue())
											.getMatrizRefConfDet()
											.getMatrizRefConfEnc()
											.getNroOrden()
									+ " debe ser menor o igual a "
									+ puntajeMaxEsperado + ". Valor actual: "
									+ sumPuntMax + ". Verifique.");
					return false;
				}
			}

		}
		return true;
	}

	private Boolean valSumatoriaAtomico(Integer indiceSeleccionado,
			Float montoMaxFactor) {
		// Controlar la restricion 4 del ETC
		// Obtener la sumatoria
		Float sumPuntMax = calcSumatariaPuntMax(indiceSeleccionado,
				montoMaxFactor);
		
		Float puntajeMaxEsperado = null;
		if (!modoEditado) {
			puntajeMaxEsperado = listaSeleccionadosMatrizRefDTO
					.get(indiceSeleccionado.intValue())
					.getMatrizReferencialDet().getMatrizReferencialEnc()
					.getPuntajeMaximo();
			if (listaSeleccionadosMatrizRefDTO
					.get(indiceSeleccionado.intValue())
					.getMatrizReferencialDet().getMatrizReferencialEnc()
					.getSumItemsSN()) {
				String laCabecera = listaSeleccionadosMatrizRefDTO.get(
						indiceSeleccionado.intValue()).toString();

				// Se verifica que los valores sean iguales
				if (sumPuntMax.compareTo(puntajeMaxEsperado) != 0) {
					statusMessages.add(
							Severity.WARN,
							"La sumatoria de items del factor con Nº. Orden "
									+ listaSeleccionadosMatrizRefDTO
											.get(indiceSeleccionado.intValue())
											.getMatrizReferencialDet()
											.getMatrizReferencialEnc()
											.getNroOrden()
									+ " debe ser igual a " + puntajeMaxEsperado
									+ ". Sumatoria actual: " + sumPuntMax
									+ ". Verifique.");
					return false;
				}
			} else {
				if (sumPuntMax.compareTo(puntajeMaxEsperado) > 0) {
					statusMessages.add(
							Severity.WARN,
							"La sumatoria de items del factor con Nº. Orden "
									+ listaSeleccionadosMatrizRefDTO
											.get(indiceSeleccionado.intValue())
											.getMatrizReferencialDet()
											.getMatrizReferencialEnc()
											.getNroOrden()
									+ " debe ser menor o igual a "
									+ puntajeMaxEsperado + ". Valor actual: "
									+ sumPuntMax + ". Verifique.");
					return false;
				}
			}
		} else {
			if (listaEditarMatrizRefConfDTO.get(indiceSeleccionado.intValue())
					.getMatrizRefConfDet().getMatrizRefConfEnc()
					.getSumItemsSN()==null) {
				Session session = jpaResourceBean.getSession();
				Criteria crit3 = session.createCriteria(MatrizRefConfEnc.class);
				crit3.add(Restrictions.eq("idMatrizRefConfEnc", listaEditarMatrizRefConfDTO
						.get(indiceSeleccionado.intValue()).getMatrizRefConfDet()
						.getMatrizRefConfEnc().getIdMatrizRefConfEnc()));
				List<MatrizRefConfEnc> lista3 = new ArrayList<MatrizRefConfEnc>();
				lista3 = crit3.list();
				listaEditarMatrizRefConfDTO.get(indiceSeleccionado.intValue())
				.getMatrizRefConfDet().setMatrizRefConfEnc(lista3.get(0));
			}
			puntajeMaxEsperado = listaEditarMatrizRefConfDTO
					.get(indiceSeleccionado.intValue()).getMatrizRefConfDet()
					.getMatrizRefConfEnc().getPuntajeMaximo();
			if (listaEditarMatrizRefConfDTO.get(indiceSeleccionado.intValue())
					.getMatrizRefConfDet().getMatrizRefConfEnc()
					.getSumItemsSN()) {
				String laCabecera = listaEditarMatrizRefConfDTO.get(
						indiceSeleccionado.intValue()).toString();

				// Se verifica que los valores sean iguales
				if (sumPuntMax.compareTo(puntajeMaxEsperado) != 0) {
					statusMessages.add(
							Severity.WARN,
							"La sumatoria de items del factor con Nº. Orden "
									+ listaEditarMatrizRefConfDTO
											.get(indiceSeleccionado.intValue())
											.getMatrizRefConfDet()
											.getMatrizRefConfEnc()
											.getNroOrden()
									+ " debe ser igual a " + puntajeMaxEsperado
									+ ". Sumatoria actual: " + sumPuntMax
									+ ". Verifique.");
					return false;
				}
			} else {
				if (sumPuntMax.compareTo(puntajeMaxEsperado) > 0) {
					statusMessages.add(
							Severity.WARN,
							"La sumatoria de items del factor con Nº. Orden "
									+ listaEditarMatrizRefConfDTO
											.get(indiceSeleccionado.intValue())
											.getMatrizRefConfDet()
											.getMatrizRefConfEnc()
											.getNroOrden()
									+ " debe ser menor o igual a "
									+ puntajeMaxEsperado + ". Valor actual: "
									+ sumPuntMax + ". Verifique.");
					return false;
				}
			}

		}
		return true;
	}

	private Boolean precondicionesSaveFactorItem() {
		if (montoMinFactor.compareTo(new Float("0")) < 0) {
			statusMessages.add(Severity.WARN, SeamResourceBundle.getBundle()
					.getString("CU85_noNegativo"));
			return false;

		}
		if (montoMaxFactor.compareTo(new Float("0")) < 0) {
			statusMessages.add(Severity.WARN, SeamResourceBundle.getBundle()
					.getString("CU85_noNegativo"));
			return false;

		}
		if (montoMinFactor.compareTo(montoMaxFactor) != -1) {
			statusMessages.add(Severity.WARN, SeamResourceBundle.getBundle()
					.getString("CU85_puntMinMayorMin"));
			return false;
		}

		return valSumatoriaParcial(indiceSeleccionado, montoMaxFactor);
	}

	public void saveFactorItem() {
		// Cummple las precondiciones
		if (precondicionesSaveFactorItem()) {
			if (!modoEditado) {
				// Actualizar la tabla
				listaSeleccionadosMatrizRefDTO
						.get(indiceSeleccionado.intValue())
						.getMatrizReferencialDet()
						.setPuntajeMaximo(montoMaxFactor);
				listaSeleccionadosMatrizRefDTO
						.get(indiceSeleccionado.intValue())
						.getMatrizReferencialDet()
						.setPuntajeMinimo(montoMinFactor);
				listaSeleccionadosMatrizRefDTO
						.get(indiceSeleccionado.intValue())
						.getMatrizReferencialDet()
						.setCriteriosEvaluacion(criteriosEvaluacion);
			} else {
				// Actualizar la tabla
				listaEditarMatrizRefConfDTO.get(indiceSeleccionado.intValue())
						.getMatrizRefConfDet().setPuntajeMaximo(montoMaxFactor);
				listaEditarMatrizRefConfDTO.get(indiceSeleccionado.intValue())
						.getMatrizRefConfDet().setPuntajeMinimo(montoMinFactor);
				listaEditarMatrizRefConfDTO.get(indiceSeleccionado.intValue())
						.getMatrizRefConfDet().setCriteriosEvaluacion(criteriosEvaluacion);;
			}

			// Se limpia
			limpiarVarFactorItemForm();
			// Secuencia
			mostrarFormEditFactorItem = false;
			em.flush();
		}
	}

	public void cargarEditarFactorItem(Integer laFila) {
		indiceSeleccionado = laFila;
		if (!modoEditado) {
			descripcion = listaSeleccionadosMatrizRefDTO
					.get(laFila.intValue()).getMatrizReferencialDet()
					.getDescripcion();
			montoMaxFactor = listaSeleccionadosMatrizRefDTO
					.get(laFila.intValue()).getMatrizReferencialDet()
					.getPuntajeMaximo();
			montoMinFactor = listaSeleccionadosMatrizRefDTO
					.get(laFila.intValue()).getMatrizReferencialDet()
					.getPuntajeMinimo();
			criteriosEvaluacion = listaSeleccionadosMatrizRefDTO
					.get(laFila.intValue()).getMatrizReferencialDet()
					.getCriteriosEvaluacion();
		} else {
			descripcion = listaEditarMatrizRefConfDTO.get(laFila.intValue())
					.getMatrizRefConfDet().getDescripcion();
			montoMaxFactor = listaEditarMatrizRefConfDTO.get(laFila.intValue())
					.getMatrizRefConfDet().getPuntajeMaximo();
			montoMinFactor = listaEditarMatrizRefConfDTO.get(laFila.intValue())
					.getMatrizRefConfDet().getPuntajeMinimo();
			criteriosEvaluacion = listaEditarMatrizRefConfDTO
					.get(laFila.intValue()).getMatrizRefConfDet()
					.getCriteriosEvaluacion();
		}

		mostrarFormEditFactorItem = true;
	}

	private void actualizarMapa(String elEncab, String eliminar) {
		// Se actualiza el mapa de encabezados
		Integer elIndice = mapaEncabezados.get(elEncab).indexOf(eliminar);
		mapaEncabezados.get(elEncab).remove(elIndice.intValue());

	}

	private void actualizarMapaActivos(String elEncab, Integer laFila) {
		if (!modoEditado) {
			if (mapSeleActivo.get(elEncab) != null
					&& mapSeleActivo.get(elEncab).size() > 0) {
				Iterator<MatrizReferencialDTO> iter = mapSeleActivo
						.get(elEncab).iterator();
				String eliminar = listaSeleccionadosMatrizRefDTO.get(laFila)
						.getMatrizReferencialDet().getIdMatrizReferencialDet()
						.toString();
				while (iter.hasNext()) {
					MatrizReferencialDTO actual = iter.next();
					if (actual.getMatrizReferencialDet() != null
							&& actual.getMatrizReferencialDet()
									.getIdMatrizReferencialDet().toString()
									.equalsIgnoreCase(eliminar)) {
						if (!mapSeleInactivo.containsKey(elEncab)) {
							mapSeleInactivo.put(elEncab,
									new ArrayList<MatrizReferencialDTO>());
						}
						mapSeleInactivo.get(elEncab).add(actual);
						iter.remove();
					}
				}
			}
		} else {
			if (mapSeleActivoEdit.get(elEncab) != null
					&& mapSeleActivoEdit.get(elEncab).size() > 0) {
				Iterator<MatrizRefConfDTO> iter = mapSeleActivoEdit
						.get(elEncab).iterator();
				String eliminar = listaEditarMatrizRefConfDTO.get(laFila)
						.getMatrizRefConfDet().getIdMatrizRefConfDet()
						+ "";
				while (iter.hasNext()) {
					MatrizRefConfDTO actual = iter.next();
					if (actual.getMostrarAcciones()
							&& (actual.getMatrizRefConfDet()
									.getIdMatrizRefConfDet() + "")
									.equalsIgnoreCase(eliminar)) {
						if (!mapSeleInactivoEdit.containsKey(elEncab)) {
							mapSeleInactivoEdit.put(elEncab,
									new ArrayList<MatrizRefConfDTO>());
						}
						mapSeleInactivoEdit.get(elEncab).add(actual);
						iter.remove();
					}
				}
			}
		}
	}

	private void actualizarMapaInactivos(String elEncab, Integer laFila) {

		if (!modoEditado) {
			if (mapSeleInactivo.get(elEncab) != null
					&& mapSeleInactivo.get(elEncab).size() > 0) {
				Iterator<MatrizReferencialDTO> iter = mapSeleInactivo.get(
						elEncab).iterator();
				String eliminar = listaSeleccionadosMatrizRefDTO.get(laFila)
						.getMatrizReferencialDet().getIdMatrizReferencialDet()
						.toString();
				while (iter.hasNext()) {
					MatrizReferencialDTO actual = iter.next();
					if (actual.getMatrizReferencialDet() != null
							&& actual.getMatrizReferencialDet()
									.getIdMatrizReferencialDet().toString()
									.equalsIgnoreCase(eliminar)) {
						if (!mapSeleActivo.containsKey(elEncab)) {
							mapSeleActivo.put(elEncab,
									new ArrayList<MatrizReferencialDTO>());
						}
						mapSeleActivo.get(elEncab).add(actual);
						iter.remove();
					}
				}
				// Verificar si no se quedo si items la cabecera para eliminarla
				if (mapSeleInactivo.get(elEncab).size() == 0) {
					mapSeleInactivo.remove(elEncab);
				}
			}
		} else {
			if (mapSeleInactivoEdit.get(elEncab) != null
					&& mapSeleInactivoEdit.get(elEncab).size() > 0) {
				Iterator<MatrizRefConfDTO> iter = mapSeleInactivoEdit.get(
						elEncab).iterator();
				String eliminar = listaEditarMatrizRefConfDTO.get(laFila)
						.getMatrizRefConfDet().getIdMatrizRefConfDet()
						+ "";
				while (iter.hasNext()) {
					MatrizRefConfDTO actual = iter.next();
					if (actual.getMostrarAcciones()
							&& (actual.getMatrizRefConfDet()
									.getIdMatrizRefConfDet() + "")
									.equalsIgnoreCase(eliminar)) {
						if (!mapSeleActivoEdit.containsKey(elEncab)) {
							mapSeleActivoEdit.put(elEncab,
									new ArrayList<MatrizRefConfDTO>());
						}
						mapSeleActivoEdit.get(elEncab).add(actual);
						iter.remove();
					}
				}
				// Verificar si no se quedo si items la cabecera para eliminarla
				if (mapSeleInactivoEdit.get(elEncab).size() == 0) {
					mapSeleInactivoEdit.remove(elEncab);
				}
			}
		}

	}

	public void eliminarFactorItem2(Integer laFila) {
		if (!modoEditado) {
			if (listaSeleccionadosMatrizRefDTO != null
					&& listaSeleccionadosMatrizRefDTO.size() > laFila) {
				String elEncab = listaSeleccionadosMatrizRefDTO.get(laFila)
						.getMatrizReferencialDet().getMatrizReferencialEnc()
						.getIdMatrizReferencialEnc().toString();
				String eliminar = listaSeleccionadosMatrizRefDTO.get(laFila)
						.getMatrizReferencialDet().getIdMatrizReferencialDet()
						.toString();
				Boolean activo = listaSeleccionadosMatrizRefDTO.get(laFila)
						.getMatrizReferencialDet().getActivo();
				if (filtroActivo || filtroTodos) {
					if (activo) {
						actualizarMapaInactivos(elEncab, laFila);
						cargarListaSel();
					} else if (mapaEncabezados.get(elEncab).size() > 1) {
						actualizarMapa(elEncab, eliminar);
						actualizarMapaActivos(elEncab, laFila);
						cargarListaSel();

					} else {
						listaSeleccionadosMatrizRefDTO.get(laFila)
								.getMatrizReferencialDet().setActivo(true);
						statusMessages.add(Severity.WARN, SeamResourceBundle
								.getBundle().getString("CU85_cabSinHijo"));
					}
				} else {
					actualizarMapaInactivos(elEncab, laFila);
					cargarListaSel();
				}
			}
		} else {
			if (listaEditarMatrizRefConfDTO != null
					&& listaEditarMatrizRefConfDTO.size() > laFila) {
				String elEncab = listaEditarMatrizRefConfDTO.get(laFila)
						.getMatrizRefConfDet().getMatrizRefConfEnc()
						.getIdMatrizRefConfEnc()
						+ "";
				String eliminar = listaEditarMatrizRefConfDTO.get(laFila)
						.getMatrizRefConfDet().getIdMatrizRefConfDet()
						+ "";
				Boolean activo = listaEditarMatrizRefConfDTO.get(laFila)
						.getMatrizRefConfDet().isActivo();
				if (filtroActivo || filtroTodos) {
					if (activo) {
						actualizarMapaInactivos(elEncab, laFila);
						cargarListaSeleEdit();
					} else if (mapaEncabezados.get(elEncab).size() > 1) {
						actualizarMapa(elEncab, eliminar);
						actualizarMapaActivos(elEncab, laFila);
						cargarListaSeleEdit();

					} else {
						listaEditarMatrizRefConfDTO.get(laFila)
								.getMatrizRefConfDet().setActivo(true);
						statusMessages.add(Severity.WARN, SeamResourceBundle
								.getBundle().getString("CU85_cabSinHijo"));
					}
				} else {
					actualizarMapaInactivos(elEncab, laFila);
					cargarListaSeleEdit();
				}
			}
		}
	}

	public void initCrear() {
		/* Incidencia 1014 */
		validarOee(grupoPuestosController.getConcursoPuestoAgr().getConcurso());
		/**/
		if (!modoEditado) {
			Session session = jpaResourceBean.getSession();
			Criteria crit = session.createCriteria(MatrizReferencial.class);
			Criteria critMatRefConfEnc = crit.createCriteria(
					"datosEspecificos", "datosEspe");
			crit.add(Restrictions.eq("datosEspe.idDatosEspecificos",
					grupoPuestosController.getConcursoPuestoAgr().getConcurso()
							.getDatosEspecificosTipoConc()
							.getIdDatosEspecificos()));
			crit.add(Restrictions.eq("activo", true));
			crit.add(Restrictions.eq("datosEspe.activo", true));

			listaMatrizRefercial = crit.list();
		} else {
			cargarListaSelEditar(idCabRefConf);
			
		}

	}

	public void cargarListaSelEditar(Long idCabecera) {
		id_concurso_puesto_agr = grupoPuestosController
				.getIdConcursoPuestoAgr();
		// Debe traer todo, pero solo el que selecciono debe permitir editar
		inicializarVars();
		Session session = jpaResourceBean.getSession();
		Criteria crit = session.createCriteria(MatrizRefConfDet.class);
		Criteria critMatrizRefConfEnc = crit.createCriteria("matrizRefConfEnc",
				"encab");
		Criteria critMatrizRefConf = critMatrizRefConfEnc.createCriteria(
				"matrizRefConf", "cab");
		// La restriccion
		if (tipoOperacion.equalsIgnoreCase(TIPO_CONCURSO_PUESTO)) {
			crit.add(Restrictions.eq("cab.idMatrizRefConf", idCabecera));
		} else {
			Criteria critHomologo = critMatrizRefConf.createCriteria(
					"homologacionPerfilMatriz", "homologo");
			crit.add(Restrictions.eq("homologo.idHomologacion", idCabecera));
		}
		// Solo los activos

		List<MatrizRefConfDet> lista = new ArrayList<MatrizRefConfDet>();

		critMatrizRefConfEnc.addOrder(Order.asc("nroOrden"));
		//critMatrizRefConfEnc.addOrder(Order.asc("idMatrizRefConfEnc"));
		//crit.addOrder(Order.asc("idMatrizRefConfDet"));
		try {
			lista = crit.list();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Integer ultIndice = 0;
		
		if (lista.size() > 0) {
			mapCacheCabeceraEdit.clear();
			String laCab = "";
			Integer orden = 0;
			
			Criteria crit2 = session.createCriteria(MatrizRefConf.class);
			Criteria critConcurso2 = crit2.createCriteria("concursoPuestoAgr", "concurso");
			crit2.add(Restrictions.eq("concurso.idConcursoPuestoAgr", id_concurso_puesto_agr));
			crit2.add(Restrictions.eq("activo", true));
			List<MatrizRefConf> lista2 = new ArrayList<MatrizRefConf>();
			lista2 = crit2.list();
			
			otrosDatos = lista2.get(0).getOtrosDatos();
			
			for (MatrizRefConfDet o : lista) {
				laCab = o.getMatrizRefConfEnc().getIdMatrizRefConfEnc() + "";
				if (!mapaEncabezados.containsKey(laCab)) {
					mapaEncabezados.put(laCab, new ArrayList<String>());
					listaEditarMatrizRefConfDTO.add(new MatrizRefConfDTO(
							orden++));
					ultIndice = listaEditarMatrizRefConfDTO.size() - 1;
					// Encabezado
					listaEditarMatrizRefConfDTO.get(ultIndice)
							.setMostrarAcciones(false);
					
					Criteria crit3 = session.createCriteria(MatrizRefConfEnc.class);
					Criteria critMatRefConf2 = crit3.createCriteria("matrizRefConf", "cab");
					crit3.add(Restrictions.eq("cab.activo", true));
					crit3.add(Restrictions.eq("cab.tipo", "GRUPO"));
					crit3.add(Restrictions.eq("cab.idMatrizRefConf",lista2.get(0).getIdMatrizRefConf()));
					crit3.add(Restrictions.eq("idMatrizRefConfEnc",o.getMatrizRefConfEnc()
							.getIdMatrizRefConfEnc()));
					List<MatrizRefConfEnc> lista3 = new ArrayList<MatrizRefConfEnc>();
					lista3 = crit3.list();
					o.setMatrizRefConfEnc(lista3.get(0));
					listaEditarMatrizRefConfDTO.get(ultIndice)
							.setMatrizRefConfDet(o);
					
					
					// Cache de cabecera
					
					mapCacheCabeceraEdit.put(laCab,
							listaEditarMatrizRefConfDTO.get(ultIndice));
					
					
				}
				listaEditarMatrizRefConfDTO.add(new MatrizRefConfDTO(orden++));
				ultIndice = listaEditarMatrizRefConfDTO.size() - 1;
				// Items
				usurioAlta = o.getUsuAlta();
				usurioMod = o.getUsuMod();
				laFechaAlta = o.getFechaAlta();
				laFechaMod = o.getFechaMod();

				// if ((laCab).equalsIgnoreCase(idCabRefConfEnc.toString())) {
				// listaEditarMatrizRefConfDTO.get(ultIndice).setMostrarAcciones2(true);
				// } else {
				// listaEditarMatrizRefConfDTO.get(ultIndice).setMostrarAcciones2(false);
				// }
				listaEditarMatrizRefConfDTO.get(ultIndice).setMostrarAcciones(
						true);
				listaEditarMatrizRefConfDTO.get(ultIndice).setMatrizRefConfDet(
						o);
				if (o.isActivo())
					mapaEncabezados.get(laCab).add(
							o.getIdMatrizRefConfDet() + "");
			}
			copiarAActivos();
			/*Collections.sort(listaEditarMatrizRefConfDTO,
					new Comparator<MatrizRefConfDTO>() {

						public int compare(MatrizRefConfDTO o1,
								MatrizRefConfDTO o2) {
							return (o1.getOrden() > o2.getOrden() ? -1 : (o1
									.getOrden() == o2.getOrden() ? 0 : 1));
						}

					});*/

		}

	}

	public void cargarListaSelCrear() throws CloneNotSupportedException {
		inicializarVars();
		Query q = em
				.createQuery("select MatrizReferencialDet from MatrizReferencialDet MatrizReferencialDet "
						+ " where MatrizReferencialDet.matrizReferencialEnc.matrizReferencial.idMatrizReferencial = "
						+ listaMatrizRefercial.get(indiceSeleccionado)
								.getIdMatrizReferencial()
						+ " and MatrizReferencialDet.activo is true and MatrizReferencialDet.matrizReferencialEnc.activo is true "
						+ "and MatrizReferencialDet.matrizReferencialEnc.matrizReferencial.activo is true "
						+ " order by MatrizReferencialDet.matrizReferencialEnc.idMatrizReferencialEnc asc,"
						+ " MatrizReferencialDet.matrizReferencialEnc.nroOrden asc, "
						+ " MatrizReferencialDet.idMatrizReferencialDet asc ");
		List<MatrizReferencialDet> lista = null;
		try {
			lista = q.getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Integer ultIndice = 0;

		if (lista.size() > 0) {
			mapCacheCabecera.clear();
			String laCab = "";
			Integer orden = 0;
			for (MatrizReferencialDet elDet : lista) {
				MatrizReferencialDet o = (MatrizReferencialDet) elDet.clone();
				System.out.println(o.getIdMatrizReferencialDet() + " -- "
						+ o.getPuntajeMaximo());
				laCab = o.getMatrizReferencialEnc().getIdMatrizReferencialEnc()
						.toString();
				if (!mapaEncabezados.containsKey(laCab)) {
					mapaEncabezados.put(laCab, new ArrayList<String>());
					listaSeleccionadosMatrizRefDTO
							.add(new MatrizReferencialDTO(orden++));
					ultIndice = listaSeleccionadosMatrizRefDTO.size() - 1;
					// Encabezado
					listaSeleccionadosMatrizRefDTO.get(ultIndice)
							.setMostrarAcciones(false);
					listaSeleccionadosMatrizRefDTO.get(ultIndice)
							.setMatrizReferencialEnc(
									o.getMatrizReferencialEnc());
					// Cache de cabecera
					mapCacheCabecera.put(laCab,
							listaSeleccionadosMatrizRefDTO.get(ultIndice));
				}
				listaSeleccionadosMatrizRefDTO.add(new MatrizReferencialDTO(
						orden++));
				ultIndice = listaSeleccionadosMatrizRefDTO.size() - 1;
				// Items
				listaSeleccionadosMatrizRefDTO.get(ultIndice)
						.setMostrarAcciones(true);
				listaSeleccionadosMatrizRefDTO.get(ultIndice)
						.setMatrizReferencial(
								o.getMatrizReferencialEnc()
										.getMatrizReferencial());
				o.setActivo(true);
				listaSeleccionadosMatrizRefDTO.get(ultIndice)
						.setMatrizReferencialDet(o);
				mapaEncabezados.get(laCab).add(
						o.getIdMatrizReferencialDet().toString());

			}
			//precondSave();
			copiarAActivos();
		}
	}

	public void cargarListaSel(String op) throws CloneNotSupportedException {
		inicializarVars();
		String query = "select MatrizReferencialDet from MatrizReferencialDet MatrizReferencialDet "
				+ " where MatrizReferencialDet.matrizReferencialEnc.matrizReferencial.idMatrizReferencial = "
				+ listaMatrizRefercial.get(indiceSeleccionado)
						.getIdMatrizReferencial();

		if (op.equals("I"))
			query += " and MatrizReferencialDet.activo is false and MatrizReferencialDet.matrizReferencialEnc.activo is false "
					+ "and MatrizReferencialDet.matrizReferencialEnc.matrizReferencial.activo is false ";
					
		if (op.equals("A"))
			query += " and MatrizReferencialDet.activo is true and MatrizReferencialDet.matrizReferencialEnc.activo is true "
					+ "and MatrizReferencialDet.matrizReferencialEnc.matrizReferencial.activo is true ";
					
		
			query += " order by MatrizReferencialDet.matrizReferencialEnc.idMatrizReferencialEnc asc,"
					+ " MatrizReferencialDet.matrizReferencialEnc.nroOrden asc, "
					+ " MatrizReferencialDet.idMatrizReferencialDet asc ";

		Query q = em.createQuery(query);
		List<MatrizReferencialDet> lista = null;
		try {
			lista = q.getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Integer ultIndice = 0;

		if (lista.size() > 0) {
			mapCacheCabecera.clear();
			String laCab = "";
			Integer orden = 0;
			for (MatrizReferencialDet elDet : lista) {
				MatrizReferencialDet o = (MatrizReferencialDet) elDet.clone();
				System.out.println(o.getIdMatrizReferencialDet() + " -- "
						+ o.getPuntajeMaximo());
				laCab = o.getMatrizReferencialEnc().getIdMatrizReferencialEnc()
						.toString();
				if (!mapaEncabezados.containsKey(laCab)) {
					mapaEncabezados.put(laCab, new ArrayList<String>());
					listaSeleccionadosMatrizRefDTO
							.add(new MatrizReferencialDTO(orden++));
					ultIndice = listaSeleccionadosMatrizRefDTO.size() - 1;
					// Encabezado
					listaSeleccionadosMatrizRefDTO.get(ultIndice)
							.setMostrarAcciones(false);
					listaSeleccionadosMatrizRefDTO.get(ultIndice)
							.setMatrizReferencialEnc(
									o.getMatrizReferencialEnc());
					// Cache de cabecera
					mapCacheCabecera.put(laCab,
							listaSeleccionadosMatrizRefDTO.get(ultIndice));
				}
				listaSeleccionadosMatrizRefDTO.add(new MatrizReferencialDTO(
						orden++));
				ultIndice = listaSeleccionadosMatrizRefDTO.size() - 1;
				// Items
				listaSeleccionadosMatrizRefDTO.get(ultIndice)
						.setMostrarAcciones(true);
				listaSeleccionadosMatrizRefDTO.get(ultIndice)
						.setMatrizReferencial(
								o.getMatrizReferencialEnc()
										.getMatrizReferencial());
				o.setActivo(true);
				listaSeleccionadosMatrizRefDTO.get(ultIndice)
						.setMatrizReferencialDet(o);
				mapaEncabezados.get(laCab).add(
						o.getIdMatrizReferencialDet().toString());

			}
			// precondSave();
			// copiarAActivos();
		}
	}

	public void cargarListaSelCrear(Integer elIndice)
			throws CloneNotSupportedException {
		indiceSeleccionado = elIndice;
		cargarListaSelCrear();
	}

	private Boolean findCabecera(String laCab) {
		if (!modoEditado) {
			for (MatrizReferencialDTO o : listaSeleccionadosMatrizRefDTO) {
				if (o.getMatrizReferencialEnc() != null
						&& o.getMatrizReferencialEnc()
								.getIdMatrizReferencialEnc().toString()
								.equalsIgnoreCase(laCab)) {
					return true;
				}
			}
			return false;
		} else {
			for (MatrizRefConfDTO o : listaEditarMatrizRefConfDTO) {
				if (!o.getMostrarAcciones()
						&& (o.getMatrizRefConfDet().getMatrizRefConfEnc()
								.getIdMatrizRefConfEnc() + "")
								.equalsIgnoreCase(laCab)) {
					return true;
				}
			}
		}
		return false;
	}

	private void copiarTodos(
			Map<String, List<MatrizReferencialDTO>> elMapaMerge,
			Map<String, List<MatrizRefConfDTO>> elMapaMergeEdit) {
		List<String> lCabPresente = new ArrayList<String>();
		if (elMapaMerge != null) {
			if (elMapaMerge != null)
				for (String o : elMapaMerge.keySet()) {
					if (!findCabecera(o)) {
						listaSeleccionadosMatrizRefDTO.add(mapCacheCabecera
								.get(o));
						lCabPresente.add(o);
					}
					for (MatrizReferencialDTO p : elMapaMerge.get(o)) {
						if (!(p.getMatrizReferencialEnc() != null && lCabPresente
								.contains(p.getMatrizReferencialEnc()
										.getIdMatrizReferencialEnc().toString()))) {
							listaSeleccionadosMatrizRefDTO.add(p);
						}
					}
				}
		} else {
			if (elMapaMergeEdit != null)
				for (String o : elMapaMergeEdit.keySet()) {
					if (!findCabecera(o)) {
						listaEditarMatrizRefConfDTO.add(mapCacheCabeceraEdit
								.get(o));
						lCabPresente.add(o);
					}
					for (MatrizRefConfDTO p : elMapaMergeEdit.get(o)) {
						if (!(!p.getMostrarAcciones() && lCabPresente
								.contains(p.getMatrizRefConfDet()
										.getMatrizRefConfEnc()
										.getIdMatrizRefConfEnc()
										+ ""))) {
							listaEditarMatrizRefConfDTO.add(p);
						}
					}
				}
		}
	}

	private void copiarActivos() {
		List<String> lCabPresente = new ArrayList<String>();
		if (!modoEditado) {
			if (mapSeleActivo != null)
				for (String o : mapSeleActivo.keySet()) {
					if (!findCabecera(o)) {
						listaSeleccionadosMatrizRefDTO.add(mapCacheCabecera
								.get(o));
						lCabPresente.add(o);
					}
					for (MatrizReferencialDTO p : mapSeleActivo.get(o)) {
						if (!(p.getMatrizReferencialEnc() != null && lCabPresente
								.contains(p.getMatrizReferencialEnc()
										.getIdMatrizReferencialEnc().toString()))) {
							listaSeleccionadosMatrizRefDTO.add(p);
						}
					}
				}
		} else {
			if (mapSeleActivoEdit != null)
				for (String o : mapSeleActivoEdit.keySet()) {
					if (!findCabecera(o)) {
						listaEditarMatrizRefConfDTO.add(mapCacheCabeceraEdit
								.get(o));
						lCabPresente.add(o);
					}
					for (MatrizRefConfDTO p : mapSeleActivoEdit.get(o)) {
						if (!(!p.getMostrarAcciones() && lCabPresente
								.contains(p.getMatrizRefConfDet()
										.getMatrizRefConfEnc()
										.getIdMatrizRefConfEnc()
										+ ""))) {
							listaEditarMatrizRefConfDTO.add(p);
						}
					}
				}

		}
	}

	private void copiarInactivo() {
		List<String> lCabPresente = new ArrayList<String>();
		if (!modoEditado) {
			if (mapSeleInactivo != null)
				for (String o : mapSeleInactivo.keySet()) {
					if (!findCabecera(o)) {
						listaSeleccionadosMatrizRefDTO.add(mapCacheCabecera
								.get(o));
						lCabPresente.add(o);
					}
					for (MatrizReferencialDTO p : mapSeleInactivo.get(o)) {
						if (!(p.getMatrizReferencialEnc() != null && lCabPresente
								.contains(p.getMatrizReferencialEnc()
										.getIdMatrizReferencialEnc().toString()))) {
							listaSeleccionadosMatrizRefDTO.add(p);
						}
					}
				}
		} else {
			if (mapSeleInactivoEdit != null)
				for (String o : mapSeleInactivoEdit.keySet()) {
					if (!findCabecera(o)) {
						listaEditarMatrizRefConfDTO.add(mapCacheCabeceraEdit
								.get(o));
						lCabPresente.add(o);
					}
					for (MatrizRefConfDTO p : mapSeleInactivoEdit.get(o)) {
						if (!(!p.getMostrarAcciones() && lCabPresente
								.contains(p.getMatrizRefConfDet()
										.getMatrizRefConfEnc()
										.getIdMatrizRefConfEnc()
										+ ""))) {
							listaEditarMatrizRefConfDTO.add(p);
						}
					}
				}
		}
	}

	/**
	 * Metodo que mezcla los mapas de activos e Inactivos para permitir la
	 * funcionalidad de mostrar todos
	 * 
	 * @return
	 */

	public Map<String, List<MatrizReferencialDTO>> mergeActivoInactivo() {

		Map<String, List<MatrizReferencialDTO>> mapResultado = new HashMap<String, List<MatrizReferencialDTO>>();
		if (mapSeleActivo == null) {
			mapSeleActivo = new HashMap<String, List<MatrizReferencialDTO>>();
		}
		if (mapSeleInactivo == null) {
			mapSeleInactivo = new HashMap<String, List<MatrizReferencialDTO>>();
		}
		for (String o : mapSeleActivo.keySet()) {
			mapResultado.put(o, new ArrayList<MatrizReferencialDTO>());
			if (mapSeleActivo.get(o) != null) {
				mapResultado.get(o).addAll(mapSeleActivo.get(o));
			}
			if (mapSeleInactivo.get(o) != null) {
				mapResultado.get(o).addAll(mapSeleInactivo.get(o));
			}
		}
		return mapResultado;
	}

	/**
	 * Metodo que mezcla los mapas de activos e Inactivos para permitir la
	 * funcionalidad de mostrar todos(Para el caso de editado)
	 * 
	 * @return
	 */

	public Map<String, List<MatrizRefConfDTO>> mergeActivoInactivoEdit() {

		Map<String, List<MatrizRefConfDTO>> mapResultado = new HashMap<String, List<MatrizRefConfDTO>>();
		if (mapSeleActivoEdit == null) {
			mapSeleActivoEdit = new HashMap<String, List<MatrizRefConfDTO>>();
		}
		if (mapSeleInactivoEdit == null) {
			mapSeleInactivoEdit = new HashMap<String, List<MatrizRefConfDTO>>();
		}
		for (String o : mapSeleActivoEdit.keySet()) {
			mapResultado.put(o, new ArrayList<MatrizRefConfDTO>());
			if (mapSeleActivoEdit.get(o) != null) {
				mapResultado.get(o).addAll(mapSeleActivoEdit.get(o));
			}
			if (mapSeleInactivoEdit.get(o) != null) {
				mapResultado.get(o).addAll(mapSeleInactivoEdit.get(o));
			}
		}
		return mapResultado;
	}

	public List<MatrizReferencialDTO> cargarListaSel() {

		if (listaSeleccionadosMatrizRefDTO == null) {
			return null;
		}
		try {
			if (filtroTodos) {
				listaSeleccionadosMatrizRefDTO.clear();

				cargarListaSel("T");

				copiarTodos(mergeActivoInactivo(), null);
				ajustarMapa();
			} else if (!filtroActivo) {
				listaSeleccionadosMatrizRefDTO.clear();

				cargarListaSel("I");

				copiarInactivo();
			} else if (filtroActivo) {
				listaSeleccionadosMatrizRefDTO.clear();
				cargarListaSel("A");
				copiarActivos();
				ajustarMapa();
			}
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.sort(listaSeleccionadosMatrizRefDTO);
		return listaSeleccionadosMatrizRefDTO;
	}

	public List<MatrizRefConfDTO> cargarListaSeleEdit() {
		if (listaEditarMatrizRefConfDTO == null) {
			return null;
		}
		if (filtroTodos) {
			listaEditarMatrizRefConfDTO.clear();
			copiarTodos(null, mergeActivoInactivoEdit());
			ajustarMapa();
		} else if (!filtroActivo) {
			listaEditarMatrizRefConfDTO.clear();
			copiarInactivo();
		} else if (filtroActivo) {
			listaEditarMatrizRefConfDTO.clear();
			copiarActivos();
			ajustarMapa();
		}
		Collections.sort(listaEditarMatrizRefConfDTO);
		return listaEditarMatrizRefConfDTO;
	}

	private void ajustarMapa() {
		// Se limpia todo primero
		for (String o : mapaEncabezados.keySet()) {
			mapaEncabezados.get(o).clear();
		}
		if (!modoEditado) {
			// Se genera de vuelta
			for (MatrizReferencialDTO o : listaSeleccionadosMatrizRefDTO) {
				if (o.getMatrizReferencialDet() != null) {
					mapaEncabezados.get(
							o.getMatrizReferencialDet()
									.getMatrizReferencialEnc()
									.getIdMatrizReferencialEnc().toString())
							.add(o.getMatrizReferencialDet()
									.getIdMatrizReferencialDet().toString());
				}
			}
			// Ineficiente pero eficaz para el tiempo que se dispone.
		} else {
			// Se genera de vuelta
			for (MatrizRefConfDTO o : listaEditarMatrizRefConfDTO) {
				if (o.getMostrarAcciones()
						&& o.getMatrizRefConfDet().isActivo()) {
					mapaEncabezados.get(
							o.getMatrizRefConfDet().getMatrizRefConfEnc()
									.getIdMatrizRefConfEnc()
									+ "").add(
							o.getMatrizRefConfDet().getIdMatrizRefConfDet()
									+ "");
				}
			}
			// Ineficiente pero eficaz para el tiempo que se dispone.
		}
	}

	public void mostrarActivos() {
		filtroActivo = true;
		filtroTodos = false;
		if (!modoEditado) {
			cargarListaSel();
		} else {
			cargarListaSeleEdit();
		}

	}

	public void mostrarTodos() {
		filtroTodos = true;
		if (!modoEditado) {
			cargarListaSel();
		} else {
			cargarListaSeleEdit();
		}
	}

	public void mostrarInactivos() {
		filtroActivo = false;
		filtroTodos = false;
		if (!modoEditado) {
			cargarListaSel();
		} else {
			cargarListaSeleEdit();
		}
	}

	private void inicializarVars() {
		if (mapaEncabezados == null) {
			mapaEncabezados = new TreeMap<String, List<String>>();
		} else {
			mapaEncabezados.clear();
		}
		if (mapCacheCabecera == null) {
			mapCacheCabecera = new TreeMap<String, MatrizReferencialDTO>();
		} else {
			mapCacheCabecera.clear();
		}

		if (mapSeleActivo == null) {
			mapSeleActivo = new TreeMap<String, List<MatrizReferencialDTO>>();
		} else {
			mapSeleActivo.clear();
		}
		if (mapSeleInactivo == null) {
			mapSeleInactivo = new TreeMap<String, List<MatrizReferencialDTO>>();
		} else {
			mapSeleInactivo.clear();
		}
		if (mapCacheCabeceraEdit == null) {
			mapCacheCabeceraEdit = new TreeMap<String, MatrizRefConfDTO>();
		} else {
			mapCacheCabeceraEdit.clear();
		}

		if (mapSeleActivoEdit == null) {
			mapSeleActivoEdit = new TreeMap<String, List<MatrizRefConfDTO>>();
		} else {
			mapSeleActivoEdit.clear();
		}
		if (mapSeleInactivoEdit == null) {
			mapSeleInactivoEdit = new TreeMap<String, List<MatrizRefConfDTO>>();
		} else {
			mapSeleInactivoEdit.clear();
		}
		if (listaSeleccionadosMatrizRefDTO != null) {
			listaSeleccionadosMatrizRefDTO.clear();
		} else {
			listaSeleccionadosMatrizRefDTO = new ArrayList<MatrizReferencialDTO>();
		}
		if (listaEditarMatrizRefConfDTO != null) {
			listaEditarMatrizRefConfDTO.clear();
		} else {
			listaEditarMatrizRefConfDTO = new ArrayList<MatrizRefConfDTO>();
		}
	}

	private void copiarAActivos() {
		if (!modoEditado) {
			// Copia a activos
			if (mapSeleActivo == null) {
				mapSeleActivo = new TreeMap<String, List<MatrizReferencialDTO>>();
			}
			if (mapSeleInactivo == null) {
				mapSeleInactivo = new TreeMap<String, List<MatrizReferencialDTO>>();
			}
			mapSeleActivo.clear();
			for (MatrizReferencialDTO o : listaSeleccionadosMatrizRefDTO) {
				String laCab = o.getMatrizReferencialDet() != null ? o
						.getMatrizReferencialDet().getMatrizReferencialEnc()
						.getIdMatrizReferencialEnc().toString() : o
						.getMatrizReferencialEnc().getIdMatrizReferencialEnc()
						.toString();
				if (!mapSeleActivo.containsKey(laCab)) {
					mapSeleActivo.put(laCab,
							new ArrayList<MatrizReferencialDTO>());
				}
				mapSeleActivo.get(laCab).add(o);
			}

		} else {
			// Copia a activos
			if (mapSeleActivoEdit == null) {
				mapSeleActivoEdit = new TreeMap<String, List<MatrizRefConfDTO>>();
			}
			if (mapSeleInactivoEdit == null) {
				mapSeleInactivoEdit = new TreeMap<String, List<MatrizRefConfDTO>>();
			}
			mapSeleActivoEdit.clear();
			for (MatrizRefConfDTO o : listaEditarMatrizRefConfDTO) {
				String laCab = o.getMatrizRefConfDet().getMatrizRefConfEnc()
						.getIdMatrizRefConfEnc()
						+ "";
				if (o.getMatrizRefConfDet().isActivo()) {
					if (!mapSeleActivoEdit.containsKey(laCab)) {
						mapSeleActivoEdit.put(laCab,
								new ArrayList<MatrizRefConfDTO>());
					}
					mapSeleActivoEdit.get(laCab).add(o);
				} else {
					if (!mapSeleInactivoEdit.containsKey(laCab)) {
						mapSeleInactivoEdit.put(laCab,
								new ArrayList<MatrizRefConfDTO>());
					}
					mapSeleInactivoEdit.get(laCab).add(o);
				}
			}
		}
	}

	@Transactional
	public String guardar() {
		if (!precondSave()) {
			return "FAIL";
		}
		Long id = null;
		String tipo = null;
		String respuesta = null;
		if (tipoOperacion == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_PARAM_INCORR"));
		} else {
			if (tipoOperacion.equalsIgnoreCase(TIPO_CONCURSO_PUESTO)) {
				id = grupoPuestosController.getIdConcursoPuestoAgr();
				tipo = TIPO_CONCURSO_PUESTO;
			} else if (tipoOperacion.equalsIgnoreCase(TIPO_HOMOLOGACION)) {
				id = idHomologacion;
				tipo = TIPO_HOMOLOGACION;
			}
			if (id == null || tipo == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_PARAM_INCORR"));
			} else {
				// Se organiza lo que se guarda. Se une inactivos con activos
				//listaSeleccionadosMatrizRefDTO.clear();
				//copiarTodos(mergeActivoInactivo(), null);
				save(id, tipo);
				respuesta = "OK";
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
			}
		}
		return respuesta;
	}

	/**
	 * Mï¿½todo que inicializa los datos para el Listado
	 */
	public void init() {
		if (grupoPuestosController != null
				&& grupoPuestosController.getIdConcursoPuestoAgr() != null){
			id_concurso_puesto_agr = grupoPuestosController.getIdConcursoPuestoAgr();
		}
		/* Incidencia 1014 */
		validarOee(grupoPuestosController.getConcursoPuestoAgr().getConcurso());
		/**/
		buildTipoEvaluacionSelectItem();
		cargarLista(null);
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				id_concurso_puesto_agr);
		
		if(listaMatrizRefDTO != null 
				&& listaMatrizRefDTO.size() > 0 
					&& listaMatrizRefDTO.get(0) != null 
						&& listaMatrizRefDTO.get(0).getMatrizRefConfEnc() != null){
			idMatrizRefConfEnc = listaMatrizRefDTO.get(0).getMatrizRefConfEnc().getIdMatrizRefConfEnc();
		}
		
		if(listaMatrizRefDTO != null 
				&& listaMatrizRefDTO.size() > 0 
					&& listaMatrizRefDTO.get(0) != null 
						&& listaMatrizRefDTO.get(0).getMatrizRefConfEnc() != null
							&& listaMatrizRefDTO.get(0).getMatrizRefConf() != null){
			idMatrizRefConf = listaMatrizRefDTO.get(0).getMatrizRefConf().getIdMatrizRefConf();
		}
		
		if(listaMatrizRefDTO != null 
				&& listaMatrizRefDTO.size() > 0 
					&& listaMatrizRefDTO.get(0) != null 
						&& listaMatrizRefDTO.get(0).getMatrizRefConfEnc() != null
							&& listaMatrizRefDTO.get(0).getMatrizRefConf() != null
								&& listaMatrizRefDTO.get(0).getMatrizRefConf().getOtrosDatos() != null){
			otrosDatos = listaMatrizRefDTO.get(0).getMatrizRefConf().getOtrosDatos();
		}else
		{
			otrosDatos = null;
		}
		/**
		 * Incidencia 0002197
		 * */
		habilitarPantalla(concursoPuestoAgr);
	}

	private void habilitarPantalla(ConcursoPuestoAgr concursoPuestoAgr) {
		habilitar = false;
		habModoLectura = false;
		if (concursoPuestoAgr.getHomologar() != null
				&& !concursoPuestoAgr.getHomologar()
				&& concursoPuestoAgr.getHomologacionPerfilMatriz() != null)
			habilitar = false;
		else {
			Referencias refIniciado = new Referencias();
			refIniciado = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "INICIADO CIRCUITO");
			Referencias refPendiente = new Referencias();
			refPendiente = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "PENDIENTE REVISION");
			Referencias refAjuste = new Referencias();
			refAjuste = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "AJUSTE PUBLICACION");
			if (concursoPuestoAgr.getEstado().intValue() == refIniciado
					.getValorNum().intValue()
					|| concursoPuestoAgr.getEstado().intValue() == refPendiente
							.getValorNum().intValue()) {
				habilitar = true;
				List<MatrizRefConf> mat = em
						.createQuery(
								"Select e from MatrizRefConf e "
										+ " where e.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo "
										+ " and e.activo=true ")
						.setParameter("idGrupo",
								concursoPuestoAgr.getIdConcursoPuestoAgr())
						.getResultList();
				if (mat.isEmpty())
					habModoLectura = true;
				else
					habModoLectura = false;
			}
			if (concursoPuestoAgr.getEstado().intValue() == refAjuste
					.getValorNum().intValue()) {
				habilitar = false;
				habModoLectura = false;
			}
		}

	}

	public void crear() {

		System.out.println("EN LA CREACION");
	}

	/**
	 * Carga la lista Principal con los items y subitems
	 * 
	 * @param sql1
	 * @param sql2
	 */
	@SuppressWarnings("unchecked")
	private void cargarLista(BigInteger idDatosEspe) {
		id_concurso_puesto_agr = grupoPuestosController
				.getIdConcursoPuestoAgr();
		Session session = jpaResourceBean.getSession();
		Criteria crit = session.createCriteria(MatrizRefConfDet.class);
		
		Criteria critMatRefConfEnc = crit.createCriteria("matrizRefConfEnc","encab");
		Criteria critMatRefConf = critMatRefConfEnc.createCriteria(	"matrizRefConf", "cab");
		Criteria critConcurso = critMatRefConf.createCriteria("concursoPuestoAgr", "concurso");
		Criteria critDatosEspe = critMatRefConfEnc.createCriteria("datosEspecificos", "datosEspe");
		
		Criteria crit2 = session.createCriteria(MatrizRefConf.class);
		Criteria critConcurso2 = crit2.createCriteria("concursoPuestoAgr", "concurso");
		crit2.add(Restrictions.eq("concurso.idConcursoPuestoAgr", id_concurso_puesto_agr));
		crit2.add(Restrictions.eq("activo", true));
		List<MatrizRefConf> lista2 = new ArrayList<MatrizRefConf>();
		lista2 = crit2.list();
		if (idDatosEspe != null) {
			crit.add(Restrictions.eq("datosEspe.idDatosEspecificos",
					idDatosEspe.longValue()));
		}
		// Solo los activos
		crit.add(Restrictions.eq("activo", true));
		crit.add(Restrictions.eq("encab.activo", true));
		crit.add(Restrictions.eq("cab.activo", true));
		crit.add(Restrictions.eq("cab.tipo", "GRUPO"));
		// La restriccion
		crit.add(Restrictions.eq("concurso.idConcursoPuestoAgr",id_concurso_puesto_agr));
		List<MatrizRefConfDet> lista = new ArrayList<MatrizRefConfDet>();
		crit.addOrder(Order.asc("encab.nroOrden"));
		// critMatRefConfEnc.addOrder(Order.asc("idMatrizRefConfEnc"));

		lista = crit.list();
		Map<String, String> mapaEncabezados = new TreeMap<String, String>();
		Integer ultIndice = 0;
		if (listaMatrizRefDTO == null) {
			listaMatrizRefDTO = new ArrayList<MatrizRefDTO>();
		} else {
			listaMatrizRefDTO.clear();
		}
		if (lista.size() > 0) {
			for (MatrizRefConfDet o : lista) {
				if (!mapaEncabezados.containsKey(o.getMatrizRefConfEnc()
						.getIdMatrizRefConfEnc() + "")) {
					mapaEncabezados.put(o.getMatrizRefConfEnc()
							.getIdMatrizRefConfEnc() + "", "Listo");
					listaMatrizRefDTO.add(new MatrizRefDTO());
					ultIndice = listaMatrizRefDTO.size() - 1;
					// Encabezado
					listaMatrizRefDTO.get(ultIndice).setMostrarAcciones(true);
					//listaMatrizRefDTO.get(ultIndice).setMatrizRefConfDet(o);
					
					Criteria crit3 = session.createCriteria(MatrizRefConfEnc.class);
					Criteria critMatRefConf2 = crit3.createCriteria("matrizRefConf", "cab");
					crit3.add(Restrictions.eq("cab.activo", true));
					crit3.add(Restrictions.eq("cab.tipo", "GRUPO"));
					crit3.add(Restrictions.eq("cab.idMatrizRefConf",lista2.get(0).getIdMatrizRefConf()));
					crit3.add(Restrictions.eq("idMatrizRefConfEnc",o.getMatrizRefConfEnc()
							.getIdMatrizRefConfEnc()));
					List<MatrizRefConfEnc> lista3 = new ArrayList<MatrizRefConfEnc>();
					lista3 = crit3.list();
					
					listaMatrizRefDTO.get(ultIndice).setMatrizRefConfEnc((MatrizRefConfEnc)
							lista3.get(0));
					//Otros datos
					listaMatrizRefDTO.get(ultIndice).setMatrizRefConf((MatrizRefConf) lista2.get(0));
				}
				listaMatrizRefDTO.add(new MatrizRefDTO());
				ultIndice = listaMatrizRefDTO.size() - 1;
				// Items
				listaMatrizRefDTO.get(ultIndice).setMostrarAcciones(false);
				listaMatrizRefDTO.get(ultIndice).setMatrizRefConfDet(o);
			}
			/*
			 * Collections.sort(listaMatrizRefDTO, new
			 * Comparator<MatrizRefDTO>(){
			 * 
			 * public int compare(MatrizRefDTO o1, MatrizRefDTO o2) {
			 * if(o1.getMatrizRefConfEnc().getNroOrden() == null ||
			 * o2.getMatrizRefConfEnc().getNroOrden() == null) return 1;
			 * if(o1.getMatrizRefConfEnc
			 * ().getNroOrden()>o2.getMatrizRefConfEnc().getNroOrden()) return
			 * -1;
			 * if(o1.getMatrizRefConfEnc().getNroOrden()==o2.getMatrizRefConfEnc
			 * ().getNroOrden()) return 0; return 1; }
			 * 
			 * });
			 */
		}

	}

	public void copiar(MatrizRefConfDet matriz,int posicion){
		/*
		 * MatrizRefConfDet(Long idMatrizRefConfDet,
		 * MatrizRefConfEnc matrizRefConfEnc, String descripcion,
				float puntajeMinimo, float puntajeMaximo, boolean activo,
				String usuAlta, Date fechaAlta, String usuMod, Date fechaMod)
		 */
		listaMatrizRefDTO.get(posicion).setMatrizRefConfEnc(
				matriz.getMatrizRefConfEnc());
		
		/*
		 * MatrizRefConfDet copy = new MatrizRefConfEnc(matriz.getIdMatrizRefConfDet());
		 */

	}
	
	
	public String getDescripcion(){
		return this.descripcion;
	}
	
	public void setDescripcion(String descripcion){
		this.descripcion = descripcion;
	}
	
	public Float getMontoMinFactor() {
		return montoMinFactor;
	}

	public void setMontoMinFactor(Float montoMinFactor) {
		this.montoMinFactor = montoMinFactor;
	}

	public Float getMontoMaxFactor() {
		return montoMaxFactor;
	}

	public void setMontoMaxFactor(Float montoMaxFactor) {
		this.montoMaxFactor = montoMaxFactor;
	}

	public Boolean getMostrarFormEditFactorItem() {
		return mostrarFormEditFactorItem;
	}

	public void setMostrarFormEditFactorItem(Boolean mostrarFormEditFactorItem) {
		this.mostrarFormEditFactorItem = mostrarFormEditFactorItem;
	}

	public List<MatrizReferencial> getListaMatrizRefercial() {
		return listaMatrizRefercial;
	}

	public void setListaMatrizRefercial(
			List<MatrizReferencial> listaMatrizRefercial) {
		this.listaMatrizRefercial = listaMatrizRefercial;
	}

	public List<MatrizReferencialDTO> getListaSeleccionadosMatrizRefDTO() {
		return listaSeleccionadosMatrizRefDTO;
	}

	public void setListaSeleccionadosMatrizRefDTO(
			List<MatrizReferencialDTO> listaSeleccionadosMatrizRefDTO) {
		this.listaSeleccionadosMatrizRefDTO = listaSeleccionadosMatrizRefDTO;
	}

	public List<MatrizRefDTO> getListaMatrizRefDTO() {
		return listaMatrizRefDTO;
	}

	public void setListaMatrizRefDTO(List<MatrizRefDTO> listaMatrizRefDTO) {
		this.listaMatrizRefDTO = listaMatrizRefDTO;
	}

	public Long getId_concurso_puesto_agr() {
		return id_concurso_puesto_agr;
	}

	public void setId_concurso_puesto_agr(Long id_concurso_puesto_agr) {
		this.id_concurso_puesto_agr = id_concurso_puesto_agr;
	}

	public Boolean getModoEditado() {
		return modoEditado;
	}

	public void setModoEditado(Boolean modoEditado) {
		this.modoEditado = modoEditado;
	}

	public List<MatrizRefConfDTO> getListaEditarMatrizRefConfDTO() {
		return listaEditarMatrizRefConfDTO;
	}

	public void setListaEditarMatrizRefConfDTO(
			List<MatrizRefConfDTO> listaEditarMatrizRefConfDTO) {
		this.listaEditarMatrizRefConfDTO = listaEditarMatrizRefConfDTO;
	}

	public Long getIdCabRefConf() {
		return idCabRefConf;
	}

	public void setIdCabRefConf(Long idCabRefConf) {
		this.idCabRefConf = idCabRefConf;
	}

	public Long getIdCabRefConfEnc() {
		return idCabRefConfEnc;
	}

	public void setIdCabRefConfEnc(Long idCabRefConfEnc) {
		this.idCabRefConfEnc = idCabRefConfEnc;
	}

	public Integer getIndiceSeleccionado() {
		return indiceSeleccionado;
	}

	public void setIndiceSeleccionado(Integer indiceSeleccionado) {
		this.indiceSeleccionado = indiceSeleccionado;
	}

	public List<SelectItem> getTipoEvalSelecItem() {
		return tipoEvalSelecItem;
	}

	public void setTipoEvalSelecItem(List<SelectItem> tipoEvalSelecItem) {
		this.tipoEvalSelecItem = tipoEvalSelecItem;
	}

	public java.math.BigInteger getElemComboSel() {
		return elemComboSel;
	}

	public void setElemComboSel(java.math.BigInteger elemComboSel) {
		this.elemComboSel = elemComboSel;
	}

	public String getUsurioAlta() {
		return usurioAlta;
	}

	public void setUsurioAlta(String usurioAlta) {
		this.usurioAlta = usurioAlta;
	}

	public String getUsurioMod() {
		return usurioMod;
	}

	public void setUsurioMod(String usurioMod) {
		this.usurioMod = usurioMod;
	}

	public Date getLaFechaAlta() {
		return laFechaAlta;
	}

	public void setLaFechaAlta(Date laFechaAlta) {
		this.laFechaAlta = laFechaAlta;
	}

	public Date getLaFechaMod() {
		return laFechaMod;
	}

	public void setLaFechaMod(Date laFechaMod) {
		this.laFechaMod = laFechaMod;
	}

	public Boolean getFiltroActivo() {
		return filtroActivo;
	}

	public void setFiltroActivo(Boolean filtroActivo) {
		this.filtroActivo = filtroActivo;
	}

	public Boolean getFiltroTodos() {
		return filtroTodos;
	}

	public void setFiltroTodos(Boolean filtroTodos) {
		this.filtroTodos = filtroTodos;
	}

	public String getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	public Long getIdHomologacion() {
		return idHomologacion;
	}

	public void setIdHomologacion(Long idHomologacion) {
		this.idHomologacion = idHomologacion;
	}

	public Integer getIdMatRefSel() {
		return idMatRefSel;
	}

	public void setIdMatRefSel(Integer idMatRefSel) {
		this.idMatRefSel = idMatRefSel;
	}

	public Map<String, Boolean> getCacheMostrarLinkSelect() {
		return cacheMostrarLinkSelect;
	}

	public void setCacheMostrarLinkSelect(
			Map<String, Boolean> cacheMostrarLinkSelect) {
		this.cacheMostrarLinkSelect = cacheMostrarLinkSelect;
	}

	public GrupoPuestosController getGrupoPuestosController() {
		return grupoPuestosController;
	}

	public void setGrupoPuestosController(
			GrupoPuestosController grupoPuestosController) {
		this.grupoPuestosController = grupoPuestosController;
	}

	public boolean isHabModoLectura() {
		return habModoLectura;
	}

	public void setHabModoLectura(boolean habModoLectura) {
		this.habModoLectura = habModoLectura;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}

	public String getCriteriosEvaluacion() {
		return criteriosEvaluacion;
	}

	public void setCriteriosEvaluacion(String criteriosEvaluacion) {
		this.criteriosEvaluacion = criteriosEvaluacion;
	}
	
	public String getOtrosDatos() {
		return otrosDatos;
	}

	public void setOtrosDatos(String otrosDatos) {
		this.otrosDatos = otrosDatos;
	}
	
	public Long getIdMatrizRefConf() {
		return idMatrizRefConf;
	}

	public void setIdMatrizRefConf(Long idMatrizRefConf) {
		this.idMatrizRefConf = idMatrizRefConf;
	}

	public Long getIdMatrizRefConfEnc() {
		return idMatrizRefConfEnc;
	}

	public void setIdMatrizRefConfEnc(Long idMatrizRefConfEnc) {
		this.idMatrizRefConfEnc = idMatrizRefConfEnc;
	}

}
