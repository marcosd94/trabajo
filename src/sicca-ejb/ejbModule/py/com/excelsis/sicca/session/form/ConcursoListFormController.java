package py.com.excelsis.sicca.session.form;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesContext;
import org.jboss.seam.international.StatusMessages;

import enums.ModalidadOcupacion;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.form.ElaborarPublicacionSeleccionadosFormController;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.CptList;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.CU422DTO;
import py.com.excelsis.sicca.util.DiaHabilController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.PAGE)
@Synchronized(timeout=10000)//MODIFICADO RV
@Name("concursoListFormController")
public class ConcursoListFormController extends
		CustomEntityQuery<FechasGrupoPuesto> {
	private static final long serialVersionUID = 2333559688607747170L;

	private static final String EJBQL = " SELECT fechasGrupoPuesto from FechasGrupoPuesto fechasGrupoPuesto  ";

	private static final String CAMPOS_BASE = " fechasGrupoPuesto ";

	private static final String FROM_BASE = "FechasGrupoPuesto fechasGrupoPuesto "
			+ "JOIN fechasGrupoPuesto.concursoPuestoAgr concursoPuestoAgr "
			+ "JOIN concursoPuestoAgr.concurso concurso "
			+ "JOIN concurso.configuracionUoCab configuracionUoCab "
			+ "JOIN concurso.datosEspecificosTipoConc datosEspecificos ";

	private static final String WHERE_BASE = " concursoPuestoAgr.activo = true and concurso.activo = true";

	private static final String ORDER_BASE = " fechasGrupoPuestos.fechaPublicacionDesde,concurso.configuracionUoCab.denominacionUnidad ";

	private Concurso concurso = new Concurso();
	private FechasGrupoPuesto fechasGrupoPuesto = new FechasGrupoPuesto();

	@In(create = true)
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
	CiudadList ciudadList;
	@In(create = true)
	BarrioList barrioList;
	@In(required = false, create = true)
	DiaHabilController diaHabilController;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	ReportUtilFormController reportUtilFormController;
	private ModalidadOcupacion modalidadOcupacion;
	List<String> listasugestion = new ArrayList<String>();
	private String sugestion;
	private String sugestionGrupo;
	
	// Para no hacer un query y obtener el id de la entidad
	private Map<String, Long> mapaSuggsIds;
	private Date fechaSistema;
	private String FROM_DEFAULT_POSTULA = null;
	private String WHERE_DEFAULT_ADJUDICADOS = null;
	private String WHERE_DEFAULT_EVALUADOS = null;
	private String WHERE_DEFAULT_PROC_POSTU = null;
	private String WHERE_DEFAULT_CONCURSOS = null;

	// Sirve para no hacer un join en el calculo de inicio y fin postulacion 
	private Long idConcursoPuestoAgr;
	private Long idDpto;
	private Long idCiudad;
	private Long idBarrio;
	private Long idTipoConcurso;
	private Long idTipoPuesto;
//	private Long idEntidad;
	private Long idConfiguracionUo;
	private String entidadSugestion;
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private List<SelectItem> barrioSelecItem;
	private List<SelectItem> tipoPuestoSelecItem;
	private List<SelectItem> tipoConcursoSelecItem;
	List<FechasGrupoPuesto> lista;
	List<CU422DTO> lHistorial;
	private Long idGrupoSele;
	Integer valorNum;
	String paginaActual;

	public void initGeneral() {
//		updateDepartamento();
//		updateCiudad();
//		updateBarrio();
//		updateTipoConcurso();
//		updateTipoPuesto();
		
		
	}

	public void init() {
		fechaSistema = new Date();
		
		if(paginaActual == null)
			paginaActual = "ConcursoList";
		else{ 
			if (!paginaActual.equals("ConcursoList")){
				paginaActual = "ConcursoList";
				this.setFirstResult(0);
				this.sugestion = "";
				this.sugestionGrupo = "";
			}
		}
		
		WHERE_DEFAULT_CONCURSOS = WHERE_BASE
				+ "  "
				+ " AND fechasGrupoPuesto.activo is true and fechasGrupoPuesto.concursoPuestoAgr.activo is true "
				+ " AND (concursoPuestoAgr.estado IN(select Referencias.valorNum from Referencias Referencias "
				+ " where"
				+ " Referencias.dominio = 'ESTADOS_GRUPO' and   (Referencias.descAbrev = 'RECIBIR POSTULACION' or Referencias.descAbrev = 'PUBLICADO' or Referencias.descAbrev = 'PUBLICACION'))) "
				+ " AND ((fechasGrupoPuesto.fechaPublicacionDesde <= :fechaSistema and fechasGrupoPuesto.horaPublicacionDesde <= :horaSistema ) "
				+ "				OR (fechasGrupoPuesto.fechaPublicacionDesde < :fechaSistema and fechasGrupoPuesto.horaPublicacionDesde >= :horaSistema))   "
				+ " AND (fechasGrupoPuesto.fechaPublicacionHasta >= :fechaSistema )   ";

		
//		initGeneral();
		search();
		cargarReferencias();
		
	}

	private void initProcPostu2() {
		fechaSistema = new Date();
		FROM_DEFAULT_POSTULA = FROM_BASE;				
		WHERE_DEFAULT_PROC_POSTU = WHERE_BASE
		+ "  "
		+ " AND fechasGrupoPuesto.activo is true and fechasGrupoPuesto.concursoPuestoAgr.activo is true "
		+ " AND (concursoPuestoAgr.estado IN(select Referencias.valorNum from Referencias Referencias "
		+ " where"
		+ " Referencias.dominio = 'ESTADOS_GRUPO' and   (Referencias.descAbrev = 'RECIBIR POSTULACION' or Referencias.descAbrev = 'PUBLICADO' or Referencias.descAbrev = 'PUBLICACION'))) "
		+ " AND ((fechasGrupoPuesto.fechaPublicacionDesde <= :fechaSistema and fechasGrupoPuesto.horaPublicacionDesde <= :horaSistema ) "
		+ "				OR (fechasGrupoPuesto.fechaPublicacionDesde < :fechaSistema and fechasGrupoPuesto.horaPublicacionDesde >= :horaSistema))   "
		+ " AND (fechasGrupoPuesto.fechaPublicacionHasta >= :fechaSistema )   ";
	}

	public void initProcPostu() {
		initProcPostu2();
		searchPostula();
		initGeneral();
	}

	public void initEvaluados() {
		fechaSistema = new Date();
		if(paginaActual == null)
			paginaActual = "evaluados";
		else{ 
			if (!paginaActual.equals("evaluados")){
				paginaActual = "evaluados";
				this.setFirstResult(0);
				this.sugestion = "";
				this.sugestionGrupo = "";
			}
		}
			
		WHERE_DEFAULT_EVALUADOS = WHERE_BASE
				+ " AND (fechasGrupoPuesto.fechaRecepcionHasta <= :fechaSistema )   "
				+ " and (concursoPuestoAgr.estado IN(select Referencias.valorNum from Referencias Referencias "
				+ " where Referencias.descAbrev = 'A EVALUAR DOCUMENTOS' OR Referencias.descAbrev = 'EVALUACION DOCUMENTAL' OR Referencias.descAbrev = 'LISTA LARGA' "
				+ " OR  Referencias.descAbrev = 'EVALUACION REFERENCIAL' OR  Referencias.descAbrev = 'ENTREVISTA MAI'"
				+ " OR  Referencias.descAbrev = 'LISTA CORTA' "
				+ " OR  Referencias.descAbrev = 'EVALUACION ADJUDICADOS' "
				+ " OR  Referencias.descAbrev = 'EVALUACION DOCUMENTAL ADJ' "
				+ " OR  Referencias.descAbrev = 'ADJUDICADO'  "
				+ " OR  Referencias.descAbrev = 'CON RESOLUCION ADJUDICACION'"
				+ " OR  Referencias.descAbrev = 'TACHAS_RECLAMOS_MODIF'"
				+ " OR  Referencias.descAbrev = 'PUBLICACION_EVALUACION'"
				+ " OR  Referencias.descAbrev = 'PUBLICACION_LISTA_LARGA'"
				+ " OR  Referencias.descAbrev = 'PUBLICACION_LISTA_CORTA'"
				+ " OR  Referencias.descAbrev = 'PUBLICACION_LISTA_TERNA_FINAL'"
				+ " OR  Referencias.descAbrev = 'PUBLICACION_LISTA_ADJUDICADOS'"
				+ " OR  Referencias.descAbrev = 'PUBLICACION_LISTA_SELECCIONADO')"
				+ ") "
				;
		
//		initGeneral();
		
		searchEvaluados();
	}
	
	public String concatenarTipoConcursoYPcdYAdReferendum(Concurso concurso, String columna){
		String retorno = concurso.getDatosEspecificosTipoConc().getDescripcion();
		if(concurso.getPcd() != null)
			retorno	+=(concurso.getPcd()?" - " + esPcd(concurso.getPcd(),columna):"" );
		if(concurso.getAdReferendum() != null)
			retorno	+=(concurso.getAdReferendum()?" - " + esAdReferendum(concurso.getAdReferendum(),columna):"" );
		return retorno;
	}
	
	
	public String esPcd(Boolean pcd,String columna){
		
		if (pcd  && columna.equals("NUEVO"))
			return " PcD";
		else if (pcd && columna.equals("TIPO_CONCURSO"))
			return "Para Personas con Discapacidad";
		else 
			return "";
		
	}
	
	public String esAdReferendum(Boolean adReferendum,String columna){
		
		if (adReferendum != null && adReferendum  && columna.equals("NUEVO"))
			return " Ad-Referendum";
		else if (adReferendum && columna.equals("TIPO_CONCURSO"))
			return " Ad-Referendum";
		else 
			return "";
		
	}

	public void printAdjudicados72() {
		ElaborarPublicacionSeleccionadosFormController elaborarPublicacionSeleccionadosFC = (ElaborarPublicacionSeleccionadosFormController) Component
				.getInstance(
						ElaborarPublicacionSeleccionadosFormController.class,
						true);
		ConcursoPuestoAgr grupo = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);
		elaborarPublicacionSeleccionadosFC.setConcursoPuestoAgr(grupo);
		elaborarPublicacionSeleccionadosFC.init2();
		elaborarPublicacionSeleccionadosFC.print();

	}

	public void registrarSele(Long idGrupo) {
		idGrupoSele = idGrupo;
	}

	public List<CU422DTO> imprimirHistorial(String cu) {
		if (idGrupoSele != null) {
			Query q = em
					.createQuery("select ConcursoPuestoAgr from ConcursoPuestoAgr  ConcursoPuestoAgr "
							+ "where ConcursoPuestoAgr.idConcursoPuestoAgr =  "
							+ idGrupoSele);
			ConcursoPuestoAgr elGrupo = (ConcursoPuestoAgr) q.getSingleResult();

			StringBuffer SQL = new StringBuffer();

			// TaskInstance
			SQL.append(" SELECT ");
			SQL.append("     proceso.actividad.descripcion_historial, ");
			SQL.append("     jbpm.jbpm_taskinstance.start_, ");
			SQL.append("     proceso.actividad.cod_actividad ");
			SQL.append(" FROM ");
			SQL.append("     jbpm.jbpm_taskinstance ");
			SQL.append(" INNER JOIN proceso.actividad ");
			SQL.append(" ON ");
			SQL.append("     ( ");
			SQL.append("         jbpm.jbpm_taskinstance.name_ = proceso.actividad.cod_actividad ");
			SQL.append("     ) ");
			SQL.append(" WHERE ");
			SQL.append("     jbpm.jbpm_taskinstance.start_ IS NOT NULL ");
			SQL.append("     and jbpm.jbpm_taskinstance.procinst_ =  "
					+ elGrupo.getIdProcessInstance());
			SQL.append(" ORDER BY ");
			SQL.append("     jbpm.jbpm_taskinstance.start_ DESC  ");
			q = em.createNativeQuery(SQL.toString());
			List<Object[]> lista = q.getResultList();
			if (lHistorial == null) {
				lHistorial = new ArrayList<CU422DTO>();
			} else {
				lHistorial.clear();
			}
			for (Object[] o : lista) {
				if (cu.equals("71")
						&& !((String) o[2])
								.equalsIgnoreCase("EVALUACION_DOCUMENTAL")) {
					if (o[0] != null)
						lHistorial.add(new CU422DTO(new Date(((Timestamp) o[1])
								.getTime()), (String) o[0]));
					else
						lHistorial.add(new CU422DTO(new Date(((Timestamp) o[1])
								.getTime()), null));
				} else if ((cu.equals("72") || cu.equals("402"))
						&& !((String) o[2])
								.equalsIgnoreCase("RECIBIR_POSTULACIONES")) {
					if (o[0] != null)
						lHistorial.add(new CU422DTO(new Date(((Timestamp) o[1])
								.getTime()), (String) o[0]));
					else
						lHistorial.add(new CU422DTO(new Date(((Timestamp) o[1])
								.getTime()), null));
				}
			}
		}

		return lHistorial;
	}

	public String ordenarConcurso() {
		search();
		return "OK";
	}

	private void executeQuery(String ejbql) {
		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		parametros.put("fechaSistema", new QueryValue(fechaSistema,
				TemporalType.DATE));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setParams(parametros);
		setCustomEjbql(ejbql);
		setEjbql(ejbql);
		lista = getResultList();
		//System.out.println("***");
	}
	
	private void executeQueryConHora(String ejbql) {
		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		parametros.put("fechaSistema", new QueryValue(fechaSistema,
				TemporalType.DATE));
		parametros.put("horaSistema", new QueryValue(fechaSistema,
				TemporalType.TIME));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setParams(parametros);
		setCustomEjbql(ejbql);
		setEjbql(ejbql);
		lista = getResultList();
		//System.out.println("***");
	}

	public boolean paginador(String tipo) {

		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		parametros.put("fechaSistema", new QueryValue(fechaSistema,
				TemporalType.DATE));
		setParams(parametros);
		if (tipo.equalsIgnoreCase("nextPage")) {
			return this.isNextExists();
		}
		return false;
	}

	private void executeQueryPostula(String ejbql) {
		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		parametros.put("fechaSistema", new QueryValue(fechaSistema,
				TemporalType.DATE));
		parametros.put("horaSistema", new QueryValue(fechaSistema,
				TemporalType.TIME));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setParams(parametros);
		if (getOrderColumn() == null)
			setOrderColumn("fechasGrupoPuesto.concursoPuestoAgr.descripcionGrupo");
		lista = getResultList(ejbql);
		//System.out.print("-- " + ejbql);
	}

	private void executeQueryEvaluados(String ejbql) {
		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		parametros.put("fechaSistema", new QueryValue(fechaSistema,
				TemporalType.DATE));
		
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setParams(parametros);
		try {
			lista = getResultList(ejbql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(ejbql);
	}

	private void executeQueryAdjudicados(String ejbql) {
		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		parametros.put("activo", new QueryValue(true));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setParams(parametros);
		lista = getResultList(ejbql);
		//System.out.print("--");
	}

	public void initAdjudicados() {
		
		fechaSistema = new Date();
		
		if(paginaActual == null)
			paginaActual = "adjudicados";
		else{ 
			if (!paginaActual.equals("adjudicados")){
				paginaActual = "adjudicados";
				this.setFirstResult(0);
				this.sugestion = "";
				this.sugestionGrupo = "";
			}
		}
		
		WHERE_DEFAULT_ADJUDICADOS = "(" + WHERE_BASE
				+ " AND concursoPuestoAgr.fechaAdjudicacion is not null "
				+ " AND  concurso.activo = :activo "
				+ " OR (concursoPuestoAgr.estado IN(select Referencias.valorNum from Referencias Referencias "
				+ " where"
				+ " Referencias.dominio = 'ESTADOS_GRUPO' and   (Referencias.descAbrev = 'GRUPO DESIERTO' "
				+ " OR Referencias.descAbrev = 'DESIERTO_CON_DOC' "
				+ " OR Referencias.descAbrev = 'PUBLICACION_FINALIZADO' "
				+ " OR Referencias.descAbrev = 'GRUPO CANCELADO'"
				+ ")))) ";
		
//		initGeneral();
		searchAdjudicados();
		
	}

	public String genCodigoConcurso(Concurso elConcurso) {
		String retorno = "-";
		if (elConcurso != null) {
			retorno = elConcurso.getNumero() != null ? elConcurso.getNumero()
					.toString() : "-";
			retorno += "/";
			retorno += elConcurso.getAnhio() != null ? elConcurso.getAnhio()
					: "-";
			retorno += " de ";
			retorno += elConcurso.getConfiguracionUoCab().getDescripcionCorta();

		}
		return retorno;

	}

	/**
	 * Este metodo inicializa la cantidad de dias que se deben mostrar el link
	 * nuevo
	 */
	private void cargarReferencias() {
		Session session = jpaResourceBean.getSession();
		Referencias refs = new Referencias();
		refs.setActivo(true);
		refs.setDominio("ETIQUETA_NUEVO");
		List<Referencias> results = session.createCriteria(Referencias.class)
				.add(Example.create(refs)).list();
		if (results.size() == 1) {
			valorNum = results.get(0).getValorNum();
		}
	}

	private Date calcFechaTope(Date fechaIni, Date fechaHasta) {
		Calendar calDesdeMasValorNum = Calendar.getInstance();
		calDesdeMasValorNum.setTime(fechaIni);
		Calendar calDesde = Calendar.getInstance();
		calDesde.setTime(fechaIni);
		Calendar calHasta = Calendar.getInstance();
		calHasta.setTime(fechaHasta);

		if (valorNum != null) {
			calDesdeMasValorNum.add(Calendar.DAY_OF_MONTH, valorNum);
			if ((calDesdeMasValorNum.getTime().getTime() >= calDesde.getTime()
					.getTime())
					&& (calDesdeMasValorNum.getTime().getTime() <= calHasta
							.getTime().getTime())) {
				return calDesdeMasValorNum.getTime();
			}
			return calHasta.getTime();
		}
		return null;

	}

	public Boolean isColumnaNuevo(FechasGrupoPuesto fgp) {
		if (fgp != null) {

			Date fechaTope = calcFechaTope(fgp.getFechaPublicacionDesde(),
					fgp.getFechaPublicacionHasta());
			Date fechaIni = fgp.getFechaPublicacionDesde();
			if (fechaIni != null && fechaTope != null) {
				Date sgteDiaHabil = fechaIni;
				Calendar cal = Calendar.getInstance();
				while ((sgteDiaHabil != null)
						&& (sgteDiaHabil.getTime() < fechaTope.getTime())) {
					sgteDiaHabil = diaHabilController.sgteDiaHabil(fgp
							.getConcursoPuestoAgr().getConcurso()
							.getConfiguracionUoCab().getIdConfiguracionUo(),
							sgteDiaHabil, fechaTope);
					if (sgteDiaHabil != null
							&& calculoFinalIsColumnaNuevo(sgteDiaHabil)) {
						return true;
					}
					if (sgteDiaHabil != null) {
						cal.setTime(sgteDiaHabil);
						cal.add(Calendar.DAY_OF_MONTH, 1);
						sgteDiaHabil = cal.getTime();
					} else {
						break;
					}

				}
			}
		}
		return false;
	}

	private Boolean calculoFinalIsColumnaNuevo(Date sgteDiaHabil) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calAhora = Calendar.getInstance();
		calAhora.set(Calendar.HOUR_OF_DAY, 0);
		Calendar calSgteDia = Calendar.getInstance();
		calSgteDia.setTime(sgteDiaHabil);
		calSgteDia.set(Calendar.HOUR_OF_DAY, 0);
		
		if (calAhora.getTime().getTime() > calSgteDia.getTime().getTime()) {
			return false;
		} else {
			return true;
		}
	}

	public void searchPostula() {
		initProcPostu2();
		initGeneral();
		this.setEntityManager(em);
		executeQueryPostula(aplicarFiltros(FROM_DEFAULT_POSTULA,WHERE_DEFAULT_PROC_POSTU));
		this.setEntityManager(em);
	}

	public void searchAdjudicados() {
		this.setEntityManager(em);
		executeQueryAdjudicados(aplicarFiltros(FROM_BASE,WHERE_DEFAULT_ADJUDICADOS));
		this.setEntityManager(em);

	}

	public void searchEvaluados() {
		this.setEntityManager(em);
		
		String sql = aplicarFiltros(FROM_BASE, WHERE_DEFAULT_EVALUADOS);

		executeQueryEvaluados(sql);
		this.setEntityManager(em);
	}

	public void search() {
		this.setEntityManager(em);
		executeQueryConHora(aplicarFiltros(FROM_BASE, WHERE_DEFAULT_CONCURSOS));
		this.setEntityManager(em);
	}

	private String aplicarFiltros(String elFromBase, String elWhereBase) {
		
		String elWhere = elWhereBase;
		String elFrom = elFromBase;
		String auxSugestion = sugestion;
		String auxSugestionGrupo = sugestionGrupo;
		
		if (auxSugestion != null && !auxSugestion.trim().isEmpty())
			elWhere = " LOWER (TRANSLATE(configuracionUoCab.denominacionUnidad, '�������������������������', 'aeiouAEIOUaeiouAEIOUaeiou')) "
					+ "like TRANSLATE('%"+sugestion.toLowerCase()+"%', '�������������������������', 'aeiouAEIOUaeiouAEIOUaeiou') AND " + elWhere;
		
		
		
	
		//	elWhere += " AND LOWER(configuracionUoCab.denominacionUnidad) like '%"+sugestion.toLowerCase()+"%'";
		if (auxSugestionGrupo != null && !auxSugestionGrupo.trim().isEmpty())
			elWhere = "LOWER (TRANSLATE(concursoPuestoAgr.descripcionGrupo, '�������������������������', 'aeiouAEIOUaeiouAEIOUaeiou')) "
					+ "like TRANSLATE('%"+sugestionGrupo.toLowerCase()+"%', '�������������������������', 'aeiouAEIOUaeiouAEIOUaeiou') AND " + elWhere;
		// elWhere = "LOWER(concursoPuestoAgr.descripcionGrupo)  like '%"+sugestionGrupo.toLowerCase()+"%' AND " + elWhere;
		//	elWhere += " AND LOWER(concursoPuestoAgr.descripcionGrupo)  like '%"+sugestionGrupo.toLowerCase()+"%'";
		
//		Boolean localidadPresente = false;
//		if (sugestion != null && !sugestion.trim().isEmpty()) {
//			try {
//				idConfiguracionUo = mapaSuggsIds.get(sugestion);
//				if (idConfiguracionUo == null) {
//					idConfiguracionUo = new Long("-1");
//				}
//			} catch (Exception ex) {
//				idConfiguracionUo = new Long("-1");
//			}
//		}
		

//		if (idBarrio != null || idCiudad != null || idDpto != null) {
//			localidadPresente = true;
//			elFrom += ",ConcursoPuestoDet concursoPuestoDet,PlantaCargoDet plantaCargoDet, Oficina oficina";
//			elFrom += ",Barrio barrio, Ciudad ciudad, Departamento departamento, Pais pais ";
//			elWhere += " AND concursoPuestoDet.concursoPuestoAgr = concursoPuestoAgr AND "
//					+ "  concursoPuestoDet.plantaCargoDet =  plantaCargoDet AND "
//					+ "  plantaCargoDet.oficina = oficina "
//					+ " AND "
//					+ "  oficina.barrio = barrio"
//					+ " AND"
//					+ "  barrio.ciudad = ciudad"
//					+ " AND"
//					+ "  ciudad.departamento = departamento"
//					+ " AND "
//					+ "  departamento.pais = pais";
//
//			if (idBarrio != null) {
//				elWhere += " AND barrio.idBarrio =" + idBarrio;
//			}
//			if (idCiudad != null) {
//				elWhere += " AND ciudad.idCiudad =" + idCiudad;
//			}
//			if (idDpto != null) {
//				elWhere += " AND departamento.idDepartamento =" + idDpto;
//			}
//
//		}
	/*	if (idEntidad != null) {
			elWhere += " AND entidad.idEntidad = " + idEntidad;
		}*/
		
//		if (idTipoConcurso != null) {
//			elWhere += " AND datosEspecificos.idDatosEspecificos = "
//					+ idTipoConcurso;
//		}

//		if (idTipoPuesto != null) {
//			if (!localidadPresente) {
//				elFrom += " ,ConcursoPuestoDet concursoPuestoDet,PlantaCargoDet plantaCargoDet, Cpt cpt ";
//				elWhere += " AND concursoPuestoAgr = concursoPuestoDet.concursoPuestoAgr AND "
//						+ " concursoPuestoDet.plantaCargoDet = plantaCargoDet AND"
//						+ " plantaCargoDet.cpt = cpt";
//				elWhere += " AND cpt.idCpt = " + idTipoPuesto;
//				localidadPresente = true;
//			} else {
//				elFrom += " ,Cpt cpt ";
//				elWhere += " AND plantaCargoDet.cpt = cpt AND ";
//				elWhere += " cpt.idCpt = " + idTipoPuesto;
//			}
//		}

//		if (modalidadOcupacion != null
//				&& (modalidadOcupacion.getValor() != null)) {
//			if (!localidadPresente) {
//				elFrom += " ,ConcursoPuestoDet concursoPuestoDet,PlantaCargoDet plantaCargoDet";
//				elWhere += " AND concursoPuestoAgr = concursoPuestoDet.concursoPuestoAgr AND "
//						+ " concursoPuestoDet.plantaCargoDet = plantaCargoDet ";
//				if (modalidadOcupacion.getValor().equalsIgnoreCase(
//						ModalidadOcupacion.PERMANENTE.getValor())) {
//					elWhere += " AND plantaCargoDet.permanente = " + true;
//				} else {
//					elWhere += " AND plantaCargoDet.contratado = " + true;
//				}
//
//			} else {
//				if (modalidadOcupacion.getValor().equalsIgnoreCase(
//						ModalidadOcupacion.PERMANENTE.getValor())) {
//					elWhere += " AND plantaCargoDet.permanente = " + true;
//				} else {
//					elWhere += " AND plantaCargoDet.contratado = " + true;
//				}
//			}
//		}
//		
		String elSQL = " SELECT"
				+ CAMPOS_BASE
				+ " FROM "
				+ elFrom
				+ " WHERE "
				+ elWhere
				+ " GROUP BY "
				+ "fechasGrupoPuesto.concurso,fechasGrupoPuesto.fechaPublicacionDesde,"
				+ "fechasGrupoPuesto.horaPublicacionDesde,fechasGrupoPuesto.fechaPublicacionHasta,fechasGrupoPuesto.horaPublicacionHasta,"
				+ "fechasGrupoPuesto.fechaRecepcionDesde,fechasGrupoPuesto.horaRecepcionDesde,fechasGrupoPuesto.fechaRecepcionHasta,"
				+ "fechasGrupoPuesto.horaRecepcionHasta,fechasGrupoPuesto.fechaVigProcDesde,fechasGrupoPuesto.horaVigProcDesde,"
				+ "fechasGrupoPuesto.fechaVigProcHasta,fechasGrupoPuesto.horaVigProcHasta,fechasGrupoPuesto.activo,"
				+ "fechasGrupoPuesto.usuAlta,fechasGrupoPuesto.fechaAlta,fechasGrupoPuesto.usuMod,fechasGrupoPuesto.fechaMod,"
				+ "fechasGrupoPuesto.fechaVigHastaAnt,fechasGrupoPuesto.fechaVigProcDesdeAnt,fechasGrupoPuesto.horaVigProcDesdeAnt,"

				+ "fechasGrupoPuesto.fechaPublicacionDesdeAnt,fechasGrupoPuesto.horaPublicacionDesdeAnt,fechasGrupoPuesto.fechaPublicacionHastaAnt,fechasGrupoPuesto.horaPublicacionHastaAnt,"

				+ "fechasGrupoPuesto.horaVigProcHastaAnt,fechasGrupoPuesto.estado,fechasGrupoPuesto.fechaRecepcionDesdeAnt,"
				+ "fechasGrupoPuesto.horaRecepcionDesdeAnt,fechasGrupoPuesto.fechaRecepcionHastaAnt,fechasGrupoPuesto.horaRecepcionHastaAnt,"
				+ "fechasGrupoPuesto.observacion, fechasGrupoPuesto.concursoPuestoAgr.concurso.configuracionUoCab.entidad.sinEntidad.entNombre"
				+ ",fechasGrupoPuesto.concursoPuestoAgr.concurso.configuracionUoCab.denominacionUnidad,fechasGrupoPuesto.concursoPuestoAgr.concurso.datosEspecificosTipoConc.descripcion"
				+ ",fechasGrupoPuesto.concursoPuestoAgr.concurso.nombre,fechasGrupoPuesto.concursoPuestoAgr.concurso.numero,fechasGrupoPuesto.concursoPuestoAgr.descripcionGrupo"
				+ ",fechasGrupoPuesto.fechaRecepcionDesde,fechasGrupoPuesto.fechaRecepcionHasta , fechasGrupoPuesto.idFechas , fechasGrupoPuesto.concursoPuestoAgr"
				+ " ORDER BY fechasGrupoPuesto.fechaRecepcionDesde desc, fechasGrupoPuesto.horaRecepcionDesde desc";

		
		return elSQL;
	}
	


	private String calQueryAutocomplete(String entNombre) {
		if (entNombre != null) {
			entNombre = entNombre.replaceAll("\\.", "");
		}
//		String SQL = ""
//				+ "SELECT "
//				+ "     planificacion.configuracion_uo_cab.* "
//				+ "FROM "
//				+ "    planificacion.configuracion_uo_cab "
//				+ "    WHERE "
//				+ "  (replace(upper(configuracion_uo_cab.denominacion_unidad),'.','') like upper('%"
//				+ entNombre
//				+ "%') OR (replace(upper(configuracion_uo_cab.descripcion_corta),'.','') like upper('%"
//				+ entNombre
//				+ "%'))) AND configuracion_uo_cab.activo = true order by  configuracion_uo_cab.denominacion_unidad";
		
		String SQL = ""
				+ "SELECT "
				+ "     planificacion.configuracion_uo_cab.* "
				+ "FROM "
				+ "    planificacion.configuracion_uo_cab "
				+ "    WHERE "
				+ "  (replace(lower(translate(configuracion_uo_cab.denominacion_unidad, '�������������������������', 'aeiouAEIOUaeiouAEIOUaeiou')), '.','') "
				+ " like lower (translate('%" + entNombre + "%', '�������������������������', 'aeiouAEIOUaeiouAEIOUaeiou')) "
				+ " OR (replace(lower (translate(configuracion_uo_cab.descripcion_corta, '�������������������������', 'aeiouAEIOUaeiouAEIOUaeiou')) ,'.','') " 
				+ " like lower(translate('%"+ entNombre + "%', '�������������������������', 'aeiouAEIOUaeiouAEIOUaeiou')) )) "
				+ " AND configuracion_uo_cab.activo = true order by  configuracion_uo_cab.denominacion_unidad";

		return SQL;
	}

	private String calQueryAutocompleteGrupo(String nombreGrupo) {
		if (nombreGrupo != null) {
			nombreGrupo = nombreGrupo.replaceAll("\\.", "");
		}
		String SQL = ""
				+ "SELECT "
				+ "     seleccion.concurso_puesto_agr.* "
				+ "FROM "
				+ "    seleccion.concurso_puesto_agr "
				+ "    WHERE "
				+ "  (replace(upper(seleccion.concurso_puesto_agr.descripcion_grupo),'.','') like upper('%"
				+ nombreGrupo
				+ "%')) AND concurso_puesto_agr.activo = true order by  concurso_puesto_agr.descripcion_grupo";

		return SQL;
	}
	
	public List<String> autocompletar(String suggest) throws Exception {
		String pref = (String) suggest;
		if (!seguridadUtilFormController.validarInput(pref,
				TiposDatos.STRING.getValor(), null))
			pref = (new Date()).getTime() + "";
		ArrayList<String> result = new ArrayList<String>();
//		Calendar cal = Calendar.getInstance();
//		Integer anhoActual = cal.get(Calendar.YEAR);
		List<ConfiguracionUoCab> lista = em.createNativeQuery(
				calQueryAutocomplete(pref), ConfiguracionUoCab.class)
				.getResultList();
		if (lista.size() == 0) {
			lista = em.createNativeQuery(
					calQueryAutocomplete(pref), ConfiguracionUoCab.class)
					.getResultList();
		}
		if (mapaSuggsIds != null) {
			mapaSuggsIds.clear();
		} else {
			mapaSuggsIds = new HashMap<String, Long>();
		}
		for (ConfiguracionUoCab o : lista) {
			if (!mapaSuggsIds.keySet().contains(
					o.getDenominacionUnidad())) {
				mapaSuggsIds.put(o.getDenominacionUnidad(),
						o.getIdConfiguracionUo());
				result.add(o.getDenominacionUnidad());
			}
		}
		return result;
	}

	public List<String> autocompletarGrupo(String suggestGrupo) throws Exception {
		String pref = (String) suggestGrupo;
		if (!seguridadUtilFormController.validarInput(pref,
				TiposDatos.STRING.getValor(), null))
			pref = (new Date()).getTime() + "";
		ArrayList<String> result = new ArrayList<String>();
//		Calendar cal = Calendar.getInstance();
//		Integer anhoActual = cal.get(Calendar.YEAR);
/*		List<ConcursoPuestoAgr> lista = em.createNativeQuery(
				calQueryAutocompleteGrupo(pref), ConcursoPuestoAgr.class)
				.getResultList();
		if (lista.size() == 0) {
			lista = em.createNativeQuery(
					calQueryAutocompleteGrupo(pref), ConcursoPuestoAgr.class)
					.getResultList();
		}
		if (mapaSuggsIds != null) {
			mapaSuggsIds.clear();
		} else {
			mapaSuggsIds = new HashMap<String, Long>();
		}
		for (ConcursoPuestoAgr o : lista) {
			if (!mapaSuggsIds.keySet().contains(
					o.getDescripcionGrupo())) {
				mapaSuggsIds.put(o.getDescripcionGrupo(),
						o.getIdConcursoPuestoAgr());
				result.add(o.getDescripcionGrupo());
			}
		}*/
		//result.add(suggestGrupo);
		return result;
	}
	
	/**
	 * Busca el id correspondiente a Paraguay
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Long buscarIdPais() {
		String cad = "select p.* from general.pais p where lower(p.descripcion) = 'paraguay'";
		List<Pais> lista = new ArrayList<Pais>();
		lista = em.createNativeQuery(cad, Pais.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0).getIdPais();
		else
			return null;
	}

	private List<DatosEspecificos> getTipoConcurso() {
		Query q = em
				.createQuery("select dE from DatosEspecificos dE,DatosGenerales dG where dG.activo = true AND dG.descripcion = :descripcion"
						+ " and dE.datosGenerales = dG "
						+ " order by dE.descripcion");
		q.setParameter("descripcion", "TIPOS DE CONCURSO");
		List<DatosEspecificos> laLista = q.getResultList();
		return laLista;
	}

	private List<Cpt> getTipoPuesto() {
		Query q = em.createQuery("select Cpt from Cpt Cpt "
				+ " where Cpt.activo = true and Cpt.nroEspecifico is not null "
				+ " and Cpt.tipoCpt is not null order by Cpt.denominacion  ");

		List<Cpt> laLista = q.getResultList();
		return laLista;
	}

	private List<Departamento> getDepartamentos() {
		Long idPaisDir = buscarIdPais();
		if (idPaisDir != null) {
			Query q = em
					.createQuery("select departamento from Departamento departamento "
							+ " where departamento.pais.descripcion = 'PARAGUAY' and departamento.activo = true "
							+ " order by departamento.descripcion ");

			return q.getResultList();
		}
		return new ArrayList<Departamento>();
	}

	public void updateTipoConcurso() {
		List<DatosEspecificos> laLista = getTipoConcurso();
		tipoConcursoSelecItem = new ArrayList<SelectItem>();
		buildTipoConcursoSelectItem(laLista);
	}

	public void updateTipoPuesto() {
		List<Cpt> laLista = getTipoPuesto();
		tipoPuestoSelecItem = new ArrayList<SelectItem>();
		buildTipoPuestoSelectItem(laLista);
	}

	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentos();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
		idDpto = null;
		idCiudad = null;
		idBarrio = null;
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDpto != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDpto);
			ciudadList.setMaxResults(null);
			ciudadList.setOrderColumn("descripcion");
			return ciudadList.getResultList();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildTipoConcursoSelectItem(List<DatosEspecificos> listSel) {
		if (tipoConcursoSelecItem == null)
			tipoConcursoSelecItem = new ArrayList<SelectItem>();
		else
			tipoConcursoSelecItem.clear();

		tipoConcursoSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : listSel) {
			tipoConcursoSelecItem.add(new SelectItem(o.getIdDatosEspecificos(),
					o.getDescripcion()));
		}
	}

	private void buildTipoPuestoSelectItem(List<Cpt> listSel) {
		if (tipoPuestoSelecItem == null)
			tipoPuestoSelecItem = new ArrayList<SelectItem>();
		else
			tipoPuestoSelecItem.clear();

		tipoPuestoSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Cpt o : listSel) {
			tipoPuestoSelecItem.add(new SelectItem(o.getIdCpt(), o
					.getDenominacion()));
		}
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

	private void buildBarrioSelectItem(List<Barrio> barrioList) {
		if (barrioSelecItem == null)
			barrioSelecItem = new ArrayList<SelectItem>();
		else
			barrioSelecItem.clear();

		barrioSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Barrio bar : barrioList) {
			barrioSelecItem.add(new SelectItem(bar.getIdBarrio(), bar
					.getDescripcion()));
		}
	}

	private List<Barrio> getBarriosByCiudad() {
		if (idCiudad != null) {
			barrioList.setIdCiudad(idCiudad);
			barrioList.setOrder("descripcion");
			barrioList.setMaxResults(null);
			return barrioList.getResultList();
		}
		return new ArrayList<Barrio>();
	}

	public void updateBarrio() {
		List<Barrio> barList = getBarriosByCiudad();
		barrioSelecItem = new ArrayList<SelectItem>();
		buildBarrioSelectItem(barList);
		idBarrio = null;
	}

	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		idCiudad = null;
		idBarrio = null;
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

	public String calcInicioPostulacion(FechasGrupoPuesto elgrupoPuesto) {
		String elResultado = "-";
		if (elgrupoPuesto != null) {
			SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");

			elResultado = sdfFecha.format(elgrupoPuesto
					.getFechaRecepcionDesde())
					+ " "
					+ sdfHora.format(elgrupoPuesto.getHoraRecepcionDesde());
		}
		return elResultado;
	}

	public String calcFinPostulacion(FechasGrupoPuesto elgrupoPuesto) {
		String elResultado = "-";
		if (elgrupoPuesto != null) {
			SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
			elResultado = sdfFecha.format(elgrupoPuesto
					.getFechaRecepcionHasta())
					+ " "
					+ sdfHora.format(elgrupoPuesto.getHoraRecepcionHasta());
		}
		return elResultado;
	}

	public ConcursoListFormController() {
		setEjbql(EJBQL);
		setMaxResults(25);
	}

	public void imprimirPerfilMatriz() {
		
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,idConcursoPuestoAgr);
		
		//1000 es el valor numerico del estado "PUBLICACION" para el Dominio "ESTADOS_GRUPO" 
		//1001 PUBLICACION_EVALUACION
		//1002 PUBLICACION_FINALIZADO
		//1002 PUBLICACION_FINALIZADO
		//1003 LISTA LARGA
		//1004 LISTA CORTA
		//1005 LISTA TERNA FINAL
		//1006 LISTA ADJUDICADOS
		if(concursoPuestoAgr.getEstado() != 1000 
				&& concursoPuestoAgr.getEstado() != 1001
				&& concursoPuestoAgr.getEstado() != 1002
				&& concursoPuestoAgr.getEstado() != 1003
				&& concursoPuestoAgr.getEstado() != 1004
				&& concursoPuestoAgr.getEstado() != 1005
				&& concursoPuestoAgr.getEstado() != 1006
				){
				reportUtilFormController = (ReportUtilFormController) Component
						.getInstance(ReportUtilFormController.class, true);
				reportUtilFormController.init();
				reportUtilFormController
						.setNombreReporte("RPT_CU015_imprimir_perfil_matriz");
				reportUtilFormController.getParametros().put("idConcursoPuestoAgr",
						idConcursoPuestoAgr);
				reportUtilFormController.imprimirReportePdf();
		}else {
			
			//verificar el query ya que las base y condiciones se cargaran por cada grupo.. 
			// posiblemente hara falta agregar un campo en la base de datos en la tabla de documentos (id_concurso_puesto_agr)
			try{
			
			String sql  ="select doc.* from general.documentos doc "
					+ " join seleccion.datos_especificos datos on doc.id_datos_especificos_tipo_documento = datos.id_datos_especificos"
					+ " and  datos.valor_alf = 'BASE_CONDICIONES'"
					+ " where id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ " order by doc.id_documento desc";
					
			
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
		
		
	}

	public void imprimirPerfilMatrizindex(int index) {

		FechasGrupoPuesto aux = lista.get(index);
		reportUtilFormController = (ReportUtilFormController) Component
				.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController
				.setNombreReporte("RPT_CU015_imprimir_perfil_matriz");
		reportUtilFormController.getParametros().put("idConcursoPuestoAgr",
				aux.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		reportUtilFormController.imprimirReportePdf();

	}


	public Concurso getConcurso() {
		return concurso;
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

	public Long getIdBarrio() {
		return idBarrio;
	}

	public void setIdBarrio(Long idBarrio) {
		this.idBarrio = idBarrio;
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

	public List<SelectItem> getBarrioSelecItem() {
		return barrioSelecItem;
	}

	public void setBarrioSelecItem(List<SelectItem> barrioSelecItem) {
		this.barrioSelecItem = barrioSelecItem;
	}

	public List<SelectItem> getTipoPuestoSelecItem() {
		return tipoPuestoSelecItem;
	}

	public void setTipoPuestoSelecItem(List<SelectItem> tipoPuestoSelecItem) {
		this.tipoPuestoSelecItem = tipoPuestoSelecItem;
	}

	public List<SelectItem> getTipoConcursoSelecItem() {
		return tipoConcursoSelecItem;
	}

	public void setTipoConcursoSelecItem(List<SelectItem> tipoConcursoSelecItem) {
		this.tipoConcursoSelecItem = tipoConcursoSelecItem;
	}

	public Long getIdTipoConcurso() {
		return idTipoConcurso;
	}

	public void setIdTipoConcurso(Long idTipoConcurso) {
		this.idTipoConcurso = idTipoConcurso;
	}

	public Long getIdTipoPuesto() {
		return idTipoPuesto;
	}

	public void setIdTipoPuesto(Long idTipoPuesto) {
		this.idTipoPuesto = idTipoPuesto;
	}

	public String getEntidadSugestion() {
		return entidadSugestion;
	}

	public void setEntidadSugestion(String entidadSugestion) {
		this.entidadSugestion = entidadSugestion;
	}

	public String getSugestion() {
		return sugestion;
	}

	public void setSugestion(String sugestion) {
		this.sugestion = sugestion;
	}

	public String getSugestionGrupo() {
		return sugestionGrupo;
	}

	public void setSugestionGrupo(String sugestionGrupo) {
		this.sugestionGrupo = sugestionGrupo;
	}

	public Date getFechaSistema() {
		return fechaSistema;
	}

	public void setFechaSistema(Date fechaSistema) {
		this.fechaSistema = fechaSistema;
	}

	public List<FechasGrupoPuesto> getLista() {
		return lista;
	}

	public void setLista(List<FechasGrupoPuesto> lista) {
		this.lista = lista;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public void setFechasGrupoPuesto(FechasGrupoPuesto fechasGrupoPuesto) {
		this.fechasGrupoPuesto = fechasGrupoPuesto;
	}

	public FechasGrupoPuesto getFechasGrupoPuesto() {
		return fechasGrupoPuesto;
	}

	public ModalidadOcupacion getModalidadOcupacion() {
		return modalidadOcupacion;
	}

	public void setModalidadOcupacion(ModalidadOcupacion modalidadOcupacion) {
		this.modalidadOcupacion = modalidadOcupacion;
	}

	public List<CU422DTO> getlHistorial() {
		return lHistorial;
	}

	public void setlHistorial(List<CU422DTO> lHistorial) {
		this.lHistorial = lHistorial;
	}

	public Long getIdGrupoSele() {
		return idGrupoSele;
	}

	public void setIdGrupoSele(Long idGrupoSele) {
		this.idGrupoSele = idGrupoSele;
	}
	
	
	
	public String getPaginaActual() {
		return paginaActual;
	}

	public void setPaginaActual(String paginaActual) {
		this.paginaActual = paginaActual;
	}

	

	

}
