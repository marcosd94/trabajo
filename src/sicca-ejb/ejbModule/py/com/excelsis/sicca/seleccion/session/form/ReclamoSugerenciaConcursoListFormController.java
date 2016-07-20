package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
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
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;

import com.sun.org.apache.regexp.internal.RE;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.ReclamoSugerencia;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ReclamoSugerenciaList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("reclamoSugerenciaConcursoListFormController")
public class ReclamoSugerenciaConcursoListFormController implements
		Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ReclamoSugerenciaList reclamoSugerenciaList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	private Persona persona;
	private Long idGrupo;
	private Long idConcurso;
	private Boolean mostrarBtnNuevo;

	private List<SelectItem> gruposSelecItem;
	private List<SelectItem> concursoSelecItem;
	private List<Postulacion> listaPostulaciones;
	private List<ReclamoSugerencia> listaReclamosSugerencias;
	private ConfiguracionUoCab oee;
	private Concurso concurso;
	private SinEntidad entidad;

	public void init() {
		persona = new Persona();
		Long id = usuarioLogueado.getPersona().getIdPersona();
		persona = em.find(Persona.class, id);
		cargarComboGrupo();
		updateConcurso();
		searchAll();
		habilitarBtnNuevo();
	}

	public void searchAll() {
		idConcurso = null;
		idGrupo = null;
		oee = new ConfiguracionUoCab();
		entidad = new SinEntidad();

		buscarListaPostulaciones();
		listaReclamosSugerencias = new ArrayList<ReclamoSugerencia>();
		for (Postulacion p : listaPostulaciones) {
			buscarListaReclamos(p.getIdPostulacion());
		}
	}

	public void search() {
		if (idGrupo != null || idConcurso != null)
			filtrar();
		else
			searchAll();
	}

	@SuppressWarnings("unchecked")
	private void filtrar() {
		listaReclamosSugerencias = new ArrayList<ReclamoSugerencia>();
		String sql = "select reclamo.* "
				+ "from seleccion.reclamo_sugerencia reclamo "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = reclamo.id_postulacion "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = post.id_concurso_puesto_agr "
				+ "join seleccion.concurso concurso "
				+ "on concurso.id_concurso = agr.id_concurso "
				+ "where post.activo is true  "
				+ "and (post.usu_cancelacion is null or post.usu_cancelacion = '')  "
				+ "and post.fecha_cancelacion is null ";

		if (idGrupo != null)
			sql = sql + " and agr.id_concurso_puesto_agr = " + idGrupo;
		if (idConcurso != null)
			sql = sql + " and concurso.id_concurso = " + idConcurso;
		listaReclamosSugerencias = em.createNativeQuery(sql,
				ReclamoSugerencia.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	private void buscarListaPostulaciones() {
		String sql = "select post.* "
				+ "from seleccion.postulacion post "
				+ "join seleccion.persona_postulante pers_post "
				+ "on pers_post.id_persona_postulante = post.id_persona_postulante "
				+ "join general.persona pers on pers.id_persona = pers_post.id_persona "
				+ "where post.activo is true "
				+ "and (post.usu_cancelacion is null or post.usu_cancelacion = '')  "
				+ "and post.fecha_cancelacion is null "
				+ "and pers.id_persona = " + persona.getIdPersona();
		listaPostulaciones = new ArrayList<Postulacion>();
		listaPostulaciones = em.createNativeQuery(sql, Postulacion.class)
				.getResultList();

	}

	@SuppressWarnings("unchecked")
	private void buscarListaReclamos(Long id) {
		String sql = "select reclamo.* "
				+ "from seleccion.reclamo_sugerencia reclamo "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = reclamo.id_postulacion "
				+ "where post.id_postulacion = " + id;
		List<ReclamoSugerencia> lista = new ArrayList<ReclamoSugerencia>();
		lista = em.createNativeQuery(sql, ReclamoSugerencia.class)
				.getResultList();
		for (ReclamoSugerencia rec : lista) {
			listaReclamosSugerencias.add(rec);
		}
	}

	/**
	 * metodo que carga el combo correspondiente a los puestos disponibles para
	 * el usuario logueado
	 */
	@SuppressWarnings("unchecked")
	private void cargarComboGrupo() {
		String cad = "select distinct(agr.*) "
				+ "from seleccion.persona_postulante per_post "
				+ "join general.persona pers  "
				+ "on pers.id_persona = per_post.id_persona "
				+ "join seleccion.postulacion post "
				+ "on post.id_persona_postulante = per_post.id_persona_postulante "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = post.id_concurso_puesto_agr "
				+ "where (post.usu_cancelacion is null or post.usu_cancelacion = '') "
				+ "and post.fecha_cancelacion is null "
				+ "and pers.id_persona = " + persona.getIdPersona();

		List<ConcursoPuestoAgr> lista = new ArrayList<ConcursoPuestoAgr>();
		lista = em.createNativeQuery(cad, ConcursoPuestoAgr.class)
				.getResultList();
		gruposSelecItem = new ArrayList<SelectItem>();
		buildGruposSelectItem(lista);
	}

	private void buildGruposSelectItem(List<ConcursoPuestoAgr> grupoList) {
		if (gruposSelecItem == null)
			gruposSelecItem = new ArrayList<SelectItem>();
		else
			gruposSelecItem.clear();
		gruposSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (ConcursoPuestoAgr agr : grupoList) {
			gruposSelecItem.add(new SelectItem(agr.getIdConcursoPuestoAgr(),
					agr.getDescripcionGrupo()));
		}
	}

	/**
	 * Combo Concurso
	 */
	public void updateConcurso() {
		List<Concurso> concursoList = getConcursoByGrupo();
		concursoSelecItem = new ArrayList<SelectItem>();
		buildConcursoSelectItem(concursoList);
		idConcurso = null;

	}

	@SuppressWarnings("unchecked")
	private List<Concurso> getConcursoByGrupo() {
		if (idGrupo != null) {
			String sql = "select distinct(conc.*) "
					+ "from seleccion.concurso conc "
					+ "join seleccion.concurso_puesto_agr agr "
					+ "on agr.id_concurso = conc.id_concurso "
					+ "where conc.activo is true "
					+ "and agr.id_concurso_puesto_agr = " + idGrupo;
			return em.createNativeQuery(sql, Concurso.class).getResultList();

		}
		return new ArrayList<Concurso>();
	}

	private void buildConcursoSelectItem(List<Concurso> concursoList) {
		if (concursoSelecItem == null)
			concursoSelecItem = new ArrayList<SelectItem>();
		else
			concursoSelecItem.clear();

		concursoSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Concurso c : concursoList) {
			concursoSelecItem.add(new SelectItem(c.getIdConcurso(), c
					.getNombre()));
		}
	}

	public void buscarOee() {
		oee = new ConfiguracionUoCab();
		entidad = new SinEntidad();
		if (idConcurso != null) {
			concurso = em.find(Concurso.class, idConcurso);
			oee = concurso.getConfiguracionUoCab();
			if (oee != null)
				entidad = oee.getEntidad().getSinEntidad();
		}
	}

	private void habilitarBtnNuevo() {
		if (cuentaConPostulaciones() && cumpleValidacionConcurso())
			mostrarBtnNuevo = true;
		else
			mostrarBtnNuevo = false;
	}

	@SuppressWarnings("unchecked")
	private Boolean cuentaConPostulaciones() {
		String sql = "select post.*  "
				+ "from seleccion.postulacion post "
				+ "join seleccion.persona_postulante pers_post "
				+ "on pers_post.id_persona_postulante = post.id_persona_postulante "
				+ "join general.persona pers  "
				+ "on pers.id_persona = pers_post.id_persona "
				+ "where (post.usu_cancelacion is null or post.usu_cancelacion = '')  "
				+ "and post.fecha_cancelacion is null "
				+ " and pers.id_persona = " + persona.getIdPersona();

		List<Postulacion> lista = new ArrayList<Postulacion>();
		lista = em.createNativeQuery(sql, Postulacion.class).getResultList();
		if (lista.size() > 0)
			return true;
		else
			return false;
	}

	private Boolean cumpleValidacionConcurso() {
		Referencias ref = new Referencias();
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"FINALIZADO");
		if (ref != null) {
			for (ReclamoSugerencia rec : listaReclamosSugerencias) {
				Concurso conc = new Concurso();
				conc = rec.getPostulacion().getConcursoPuestoAgr()
						.getConcurso();
				if (conc.getEstado() == ref.getValorNum()) {
					return verificarFecha(conc);
				} else
					return true;
			}
		}
		return true;
	}

	private Boolean verificarFecha(Concurso c) {
		String sql = "SELECT valor_num " + "FROM seleccion.referencias "
				+ "WHERE dominio = 'PLAZO_RECLAMO_POSTULANTE'";
		Integer cantDias;
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null) {
			cantDias = 0;
		}
		cantDias = new Integer(config.toString());
		Date fechaFinalizacion = c.getFechaFinalizacion();
		if (fechaFinalizacion == null)
			return true;
		Calendar fechaFin = Calendar.getInstance();
		fechaFin.setTime(fechaFinalizacion);
		fechaFin.add(Calendar.DATE, cantDias);
		Calendar hoy = Calendar.getInstance();
		if (fechaFin.after(hoy)) {
			return false;
		}
		return true;
	}

	/**
	 * Es llamado desde el boton Imprimir
	 */
	public void print(Long id) {
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		List<Object[]> lista = consulta(id);
		if (lista == null) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
			return;
		}

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");

		for (Object[] obj : lista) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[0] != null)
				map.put("reclamo_sugerencia", obj[0].toString());
			if (obj[1] != null)
				map.put("concurso", obj[1].toString());
			if (obj[2] != null)
				map.put("grupo_puesto", obj[2].toString());
			if (obj[3] != null)
				map.put("cod_postulante", obj[3].toString());
			if (obj[4] != null)
				map.put("nombre_apellido", obj[4].toString());
			if (obj[5] != null)
				map.put("doc_id", obj[5].toString());
			if (obj[6] != null)
				map.put("nro", new Long(obj[6].toString()));
			if (obj[7] != null) {
				String fecha = formatoDeFecha.format(obj[7]);
				map.put("fecha", fecha);
			}
			if (obj[8] != null)
				map.put("asunto", obj[8].toString());
			if (obj[9] != null)
				map.put("descripcion", obj[9].toString());
			if (obj[10] != null)
				map.put("oee", obj[10].toString());

			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("RPT_CU108_Reclamos_Por_Concurso", false,
				listaDatosReporte, param);
	}

	/**
	 * busca los datos para ser enviados en el reporte
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> consulta(Long id) {
		String sql = "SELECT "
				+ "CASE WHEN RECLAMO_SUGERENCIA.RECLAMO_SUGERENCIA = 'S' "
				+ "THEN 'SUGERENCIA' WHEN RECLAMO_SUGERENCIA.RECLAMO_SUGERENCIA = 'R' "
				+ "THEN 'RECLAMO' END AS RECLAMO_SUGERENCIA, "
				+ "CONCURSO.NUMERO || '/' || CONCURSO.ANIO || ' de ' || "
				+ "CONFIGURACION_UO.DESCRIPCION_CORTA ||' - ' || CONCURSO.NOMBRE AS CONCURSO, "
				+ "CONCURSO_PUESTO_AGR.DESCRIPCION_GRUPO AS GRUPO_PUESTO, "
				+ "PERSONA_POSTULANTE.USU_ALTA AS COD_POSTULANTE, "
				+ "PERSONA_POSTULANTE.NOMBRES || ' ' || PERSONA_POSTULANTE.APELLIDOS AS NOMBRE_APELLIDO, "
				+ "PERSONA_POSTULANTE.DOCUMENTO_IDENTIDAD AS DOC_ID, "
				+ "RECLAMO_SUGERENCIA.NRO_RECLAMO AS NRO, "
				+ "RECLAMO_SUGERENCIA.FECHA_RECLAMO_SUGERENCIA AS FECHA, "
				+ "RECLAMO_SUGERENCIA.ASUNTO AS ASUNTO, "
				+ "RECLAMO_SUGERENCIA.DESCRIPCION AS DESCRIPCION, CONFIGURACION_UO.DENOMINACION_UNIDAD AS OEE "
				+ "FROM SELECCION.RECLAMO_SUGERENCIA	RECLAMO_SUGERENCIA "
				+ "JOIN SELECCION.POSTULACION POSTULACION "
				+ "ON POSTULACION.ID_POSTULACION = RECLAMO_SUGERENCIA.ID_POSTULACION "
				+ "JOIN SELECCION.PERSONA_POSTULANTE	PERSONA_POSTULANTE "
				+ "ON PERSONA_POSTULANTE.ID_PERSONA_POSTULANTE = POSTULACION.ID_PERSONA_POSTULANTE "
				+ "JOIN SELECCION.CONCURSO_PUESTO_AGR	CONCURSO_PUESTO_AGR "
				+ "ON CONCURSO_PUESTO_AGR.ID_CONCURSO_PUESTO_AGR = POSTULACION.ID_CONCURSO_PUESTO_AGR "
				+ "JOIN SELECCION.CONCURSO	CONCURSO "
				+ "ON CONCURSO.ID_CONCURSO = CONCURSO_PUESTO_AGR.ID_CONCURSO "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB	CONFIGURACION_UO "
				+ "ON CONFIGURACION_UO.ID_CONFIGURACION_UO = CONCURSO.ID_CONFIGURACION_UO "
				+ "WHERE RECLAMO_SUGERENCIA.ID_RECLAMO_SUGERENCIA = " + id;
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

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public List<SelectItem> getGruposSelecItem() {
		return gruposSelecItem;
	}

	public void setGruposSelecItem(List<SelectItem> gruposSelecItem) {
		this.gruposSelecItem = gruposSelecItem;
	}

	public List<SelectItem> getConcursoSelecItem() {
		return concursoSelecItem;
	}

	public void setConcursoSelecItem(List<SelectItem> concursoSelecItem) {
		this.concursoSelecItem = concursoSelecItem;
	}

	public List<Postulacion> getListaPostulaciones() {
		return listaPostulaciones;
	}

	public void setListaPostulaciones(List<Postulacion> listaPostulaciones) {
		this.listaPostulaciones = listaPostulaciones;
	}

	public List<ReclamoSugerencia> getListaReclamosSugerencias() {
		return listaReclamosSugerencias;
	}

	public void setListaReclamosSugerencias(
			List<ReclamoSugerencia> listaReclamosSugerencias) {
		this.listaReclamosSugerencias = listaReclamosSugerencias;
	}

	public ConfiguracionUoCab getOee() {
		return oee;
	}

	public void setOee(ConfiguracionUoCab oee) {
		this.oee = oee;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public SinEntidad getEntidad() {
		return entidad;
	}

	public void setEntidad(SinEntidad entidad) {
		this.entidad = entidad;
	}

	public Boolean getMostrarBtnNuevo() {
		return mostrarBtnNuevo;
	}

	public void setMostrarBtnNuevo(Boolean mostrarBtnNuevo) {
		this.mostrarBtnNuevo = mostrarBtnNuevo;
	}

}
