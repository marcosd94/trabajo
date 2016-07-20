package py.com.excelsis.sicca.seleccion.session.form;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

import enums.EvalsReferenciales;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("evaluaRefereFC")
public class EvaluaRefereFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	private List<SelectItem> concursoSelecItem;
	private List<SelectItem> grupoPuestoSelecItem;
	private List<SelectItem> tipoEvalSelecItem;
	private EvalsReferenciales evalsRefsRadio;
	private Integer anhoConcurso;
	private Long idConcurso;
	private Long idConcursoPuestoAgr;
	private Long idTipoEval;
	private Boolean printCedula = true;
	private String lInPersonaConDetalleRep = "";

	public void init() {
		iniciarAnhoConcurso();
		updateConcurso();
		updateGrupoPuesto();
		updateTipoEval();
	}

	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		idConcurso = null;
		idConcursoPuestoAgr = null;
		idTipoEval = null;
		evalsRefsRadio = null;
		iniciarAnhoConcurso();
	}

	private String genWhere() {
		String respuesta = null;

		respuesta = "WHERE postulacion.id_concurso_puesto_agr = " + idConcursoPuestoAgr;
		respuesta += " AND evalRef.activo is true";
		respuesta += " AND grupo.id_concurso = " + idConcurso;
		respuesta +=
			" AND seleccion.concurso.id_configuracion_uo = " + nivelEntidadOeeUtil.getIdConfigCab();
		if (idTipoEval != null) {
			respuesta += " AND evalRefTipoEval.id_datos_especificos_tipo_eval = " + idTipoEval;
		}
		if (lInPersonaConDetalleRep != null && !lInPersonaConDetalleRep.isEmpty()) {
			respuesta +=
				" AND personaPostulante.id_persona_postulante IN " + lInPersonaConDetalleRep;
		}
		return respuesta;
	}

	private void cargarParametros() {
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		reportUtilFormController.getParametros().put("elWhere", genWhere());
		reportUtilFormController.getParametros().put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		reportUtilFormController.getParametros().put("path_logo", servletContext.getRealPath("/img/"));
		reportUtilFormController.getParametros().put("nivel", nivelEntidadOeeUtil.getNombreNivelEntidad());
		reportUtilFormController.getParametros().put("entidad", nivelEntidadOeeUtil.getNombreSinEntidad());
		reportUtilFormController.getParametros().put("oee", nivelEntidadOeeUtil.getDenominacionUnidad());
		reportUtilFormController.getParametros().put("concurso", em.find(Concurso.class, idConcurso).getNombre());
		reportUtilFormController.getParametros().put("grupo", em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr).getDescripcionGrupo());
		reportUtilFormController.getParametros().put("usuario", usuarioLogueado.getCodigoUsuario());
		reportUtilFormController.getParametros().put("printCedula", printCedula);

		Query q =
			em.createQuery("select datosGrupoPuesto from DatosGrupoPuesto datosGrupoPuesto where "
				+ "datosGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr =  "
				+ idConcursoPuestoAgr);
		List<DatosGrupoPuesto> lista = q.getResultList();
		if (lista.size() == 1) {
			DecimalFormat fNumero = new DecimalFormat("#0.00");
			DatosGrupoPuesto datosGrupoPuesto = lista.get(0);
			if (datosGrupoPuesto.getPorMinPorEvaluacion() != null
				&& datosGrupoPuesto.getPorMinPorEvaluacion())
				reportUtilFormController.getParametros().put("minAplicar", SeamResourceBundle.getBundle().getString("CU88_porCadaEvaluacion"));
			else if (datosGrupoPuesto.getPorMinFinEvaluacion() != null
				&& datosGrupoPuesto.getPorMinFinEvaluacion())
				reportUtilFormController.getParametros().put("minAplicar", SeamResourceBundle.getBundle().getString("CU88_alFinalizarEvals"));
			if (datosGrupoPuesto.getPorcMinimo() != null) {
				reportUtilFormController.getParametros().put("porMin", fNumero.format(datosGrupoPuesto.getPorcMinimo().doubleValue()));
			} else {
				reportUtilFormController.getParametros().put("porMin", "-");
			}
		}
	}

	private Boolean precondImprimir() {
		if (idConcurso == null || idConcursoPuestoAgr == null || evalsRefsRadio == null
			|| nivelEntidadOeeUtil.getIdSinEntidad() == null
			|| nivelEntidadOeeUtil.getIdSinNivelEntidad() == null
			&& nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
			return false;
		}
		return true;
	}

	public void imprimir() {
		if (precondImprimir()) {
			if (evalsRefsRadio.getId().intValue() == EvalsReferenciales.SIN_DET.getId().intValue()) {
				reportUtilFormController =
					(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
				reportUtilFormController.setNombreReporte("RPT_CU421_IMPRIMIR_EVALUACIONES_SIN_DET");
			} else if (evalsRefsRadio.getId().intValue() == EvalsReferenciales.CON_DET.getId().intValue()) {
				reportUtilFormController =
					(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
				reportUtilFormController.setNombreReporte("RPT_CU421_IMPRIMIR_EVALUACIONES_CON_DET");
			}
			cargarParametros();
			reportUtilFormController.imprimirReportePdf();
		}
	}

	private void iniciarAnhoConcurso() {
		Calendar cal = Calendar.getInstance();
		anhoConcurso = cal.get(Calendar.YEAR);
	}

	public void updateConcurso() {
		List<Concurso> lista = getConcurso();
		concursoSelecItem = new ArrayList<SelectItem>();
		buildConcursoSelectItem(lista);
		idConcurso = null;
	}

	private List<Concurso> getConcurso() {
		if (anhoConcurso == null) {
			iniciarAnhoConcurso();
		}
		Query q =
			em.createQuery("select Concurso from Concurso Concurso " + "where Concurso.anhio = "
				+ anhoConcurso + " order by Concurso.nombre asc");
		return q.getResultList();
	}

	private void buildConcursoSelectItem(List<Concurso> lista) {
		if (concursoSelecItem == null)
			concursoSelecItem = new ArrayList<SelectItem>();
		else
			concursoSelecItem.clear();
		concursoSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Concurso o : lista) {
			concursoSelecItem.add(new SelectItem(o.getIdConcurso(), o.getNombre()));
		}
	}

	public void updateGrupoPuesto() {
		List<ConcursoPuestoAgr> lista = getGrupoPuesto();
		grupoPuestoSelecItem = new ArrayList<SelectItem>();
		buildGrupoPuestoSelectItem(lista);
		idConcursoPuestoAgr = null;
	}

	private List<ConcursoPuestoAgr> getGrupoPuesto() {
		Query q =
			em.createQuery("select ConcursoPuestoAgr from ConcursoPuestoAgr ConcursoPuestoAgr "
				+ "where ConcursoPuestoAgr.concurso.idConcurso= " + idConcurso
				+ " order by ConcursoPuestoAgr.descripcionGrupo asc");
		return q.getResultList();
	}

	private void buildGrupoPuestoSelectItem(List<ConcursoPuestoAgr> lista) {
		if (grupoPuestoSelecItem == null)
			grupoPuestoSelecItem = new ArrayList<SelectItem>();
		else
			grupoPuestoSelecItem.clear();
		grupoPuestoSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (ConcursoPuestoAgr o : lista) {
			grupoPuestoSelecItem.add(new SelectItem(o.getIdConcursoPuestoAgr(), o.getDescripcionGrupo()));
		}
	}

	public void updateTipoEval() {
		List<DatosEspecificos> lista = getTipoEval();
		tipoEvalSelecItem = new ArrayList<SelectItem>();
		buildTipoEvalSelectItem(lista);
		idTipoEval = null;
	}

	private List<DatosEspecificos> getTipoEval() {
		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ "where DatosEspecificos.datosGenerales.descripcion = 'TIPOS DE EVALUACION'"
				+ " order by DatosEspecificos.descripcion asc");
		return q.getResultList();
	}

	private void buildTipoEvalSelectItem(List<DatosEspecificos> lista) {
		if (tipoEvalSelecItem == null)
			tipoEvalSelecItem = new ArrayList<SelectItem>();
		else
			tipoEvalSelecItem.clear();
		tipoEvalSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : lista) {
			tipoEvalSelecItem.add(new SelectItem(o.getIdDatosEspecificos(), o.getDescripcion()));
		}
	}

	public List<SelectItem> getConcursoSelecItem() {
		return concursoSelecItem;
	}

	public void setConcursoSelecItem(List<SelectItem> concursoSelecItem) {
		this.concursoSelecItem = concursoSelecItem;
	}

	public Integer getAnhoConcurso() {
		return anhoConcurso;
	}

	public void setAnhoConcurso(Integer anhoConcurso) {
		this.anhoConcurso = anhoConcurso;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public List<SelectItem> getGrupoPuestoSelecItem() {
		return grupoPuestoSelecItem;
	}

	public void setGrupoPuestoSelecItem(List<SelectItem> grupoPuestoSelecItem) {
		this.grupoPuestoSelecItem = grupoPuestoSelecItem;
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

	public EvalsReferenciales getEvalsRefsRadio() {
		return evalsRefsRadio;
	}

	public void setEvalsRefsRadio(EvalsReferenciales evalsRefsRadio) {
		this.evalsRefsRadio = evalsRefsRadio;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public Boolean getPrintCedula() {
		return printCedula;
	}

	public void setPrintCedula(Boolean printCedula) {
		this.printCedula = printCedula;
	}

	public String getlInPersonaConDetalleRep() {
		return lInPersonaConDetalleRep;
	}

	public void setlInPersonaConDetalleRep(String lInPersonaConDetalleRep) {
		this.lInPersonaConDetalleRep = lInPersonaConDetalleRep;
	}

}
