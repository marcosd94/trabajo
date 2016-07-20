package py.com.excelsis.sicca.legajo.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.EvaluacionesFuncionario;
import py.com.excelsis.sicca.entity.EvaluacionesFuncionarioEvalLega;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.entity.GrupoMetodologia;

@Scope(ScopeType.CONVERSATION)
@Name("evaluacionesLegajoFC")
public class EvaluacionesLegajoFC implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4001069365694124820L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	ReportUtilFormController reportUtilFormController;

	private Long idPersona;
	private String texto;
	private Persona persona;
	private List<EvaluacionesFuncionarioEvalLega> listaEvalFuncionarios = new ArrayList<EvaluacionesFuncionarioEvalLega>();
	private List<GrupoMetodologia> listaMetodologia = new ArrayList<GrupoMetodologia>();

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		persona = em.find(Persona.class, idPersona);
		String query = "SELECT EF.* "
				+ "FROM EVAL_DESEMPENO.EVALUACIONES_FUNCIONARIO3 EF "
				+ "WHERE EF.ID_PERSONA = " + idPersona;
		listaEvalFuncionarios = new ArrayList<EvaluacionesFuncionarioEvalLega>();
		listaEvalFuncionarios = em.createNativeQuery(query,
				EvaluacionesFuncionarioEvalLega.class).getResultList();
		
		nivelEntidadOeeUtil.setIdConfigCab(buscarConfiguracionUoCab());
	}
	
	private Long buscarConfiguracionUoCab(){
		if (usuarioLogueado.getConfiguracionUoCab() != null && usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo() != null)
			return usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo();
		else {
			String sql = "select * from general.empleado_puesto where id_persona = "
					+ usuarioLogueado.getPersona().getIdPersona()
					+ " order by fecha_alta desc";
			List <EmpleadoPuesto> lista = em.createNativeQuery(sql,EmpleadoPuesto.class).getResultList();
			if (lista.size() > 0){
				EmpleadoPuesto empleadoPuesto = lista.get(0);
				return empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo();				
			}else
				return null;
				
		}
	}

	public String formateandoFecha(Date date){
		SimpleDateFormat sdfFechaCompleta = new SimpleDateFormat("dd/MM/yyyy");
		String fecha = sdfFechaCompleta.format(date);
		return fecha;
	}
	public void abrirDoc(Long idDoc) {

		Documentos documentos = new Documentos();
		documentos = em.find(Documentos.class, idDoc);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(
				documentos.getIdDocumento(), usuarioLogueado.getIdUsuario());

	}
	
	public void generandoPercepcion(Long idSujetos, Long idGruposEvaluacion) {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU642_resultadoEvalPercepcionIndivi");
		ConfiguracionUoCab oeeUsuario =
			em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Long idGrupoMetodologia = devolviendoMetodologia(idGruposEvaluacion, false);
		reportUtilFormController.getParametros().put("oee_usuario", oeeUsuario.getDenominacionUnidad().toUpperCase());
		reportUtilFormController.getParametros().put("idGrupoMetodologia", idGrupoMetodologia);
		Map<String, String> diccDesc = nivelEntidadOeeUtil.descNivelEntidadOee();
		reportUtilFormController.getParametros().put("nivel", diccDesc.get("NIVEL"));
		reportUtilFormController.getParametros().put("entidad", diccDesc.get("ENTIDAD"));
		reportUtilFormController.getParametros().put("oee", diccDesc.get("OEE"));
		reportUtilFormController.getParametros().put("idSujeto", idSujetos);
		reportUtilFormController.imprimirReportePdf();
	}
	
	public void generandoPdec(Long idSujetos, Long idGruposEvaluacion) throws SQLException {
		Connection conn = null;
		try {
			HashMap<String, Object> mapa = obtenerMapaParametros(idSujetos, idGruposEvaluacion);
			if (mapa == null) {
				return;
			} else
				statusMessages.clear();
			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("RPT_Resultado_Evaluacion_PDECindivi_CU574", mapa, false, conn, usuarioLogueado);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}
	
	private Long devolviendoMetodologia(Long idGruposEvaluacion, Boolean pdec){
		Query q = em.createQuery("select GrupoMetodologia from GrupoMetodologia grupoMetodologia where grupoMetodologia.gruposEvaluacion.idGruposEvaluacion =:idGruposEvaluacion ");
		q.setParameter("idGruposEvaluacion", idGruposEvaluacion);
		listaMetodologia = q.getResultList();Long idGrupoMetodologia = null;
		for (int i = 0; i < listaMetodologia.size(); i++) {
			if(listaMetodologia.get(i).getTipo().contentEquals("P") && pdec == true)
				idGrupoMetodologia = listaMetodologia.get(i).getIdGrupoMetodologia();
			if (listaMetodologia.get(i).getTipo().contentEquals("A") && pdec == false) 
				idGrupoMetodologia = listaMetodologia.get(i).getIdGrupoMetodologia();
		}
		return idGrupoMetodologia;
	}
	
	private HashMap<String, Object> obtenerMapaParametros(Long idSujetos, Long idGruposEvaluacion) throws Exception {
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		param.put("oee", nivelEntidadOeeUtil.getDenominacionUnidad());
		if (nivelEntidadOeeUtil.getCodNivelEntidad() != null)
			param.put("nivel", nivelEntidadOeeUtil.getNombreNivelEntidad());
		if (nivelEntidadOeeUtil.getCodSinEntidad() != null)
			param.put("entidad", nivelEntidadOeeUtil.getNombreSinEntidad());
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			param.put("uo", nivelEntidadOeeUtil.getDenominacionUnidad());
		
		Long idGrupoMetodologia = devolviendoMetodologia(idGruposEvaluacion, true);
		GrupoMetodologia metodologia = em.find(GrupoMetodologia.class, idGrupoMetodologia);
		
		param.put("evaluacion",metodologia.getGruposEvaluacion().getEvaluacionDesempeno().getTitulo());
		param.put("grupo",metodologia.getGruposEvaluacion().getGrupo());
		param.put("fecha_desde",metodologia.getGruposEvaluacion().getEvaluacionDesempeno().getFechaEvalDesde());
		param.put("fecha_hasta",metodologia.getGruposEvaluacion().getEvaluacionDesempeno().getFechaEvalHasta());
		param.put("oee_actual", metodologia.getGruposEvaluacion().getEvaluacionDesempeno().getConfiguracionUoCab().getDenominacionUnidad());
		param.put("id", idGrupoMetodologia);
		param.put("idSujeto", idSujetos );
		param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));//está
		param.put("path_logo", servletContext.getRealPath("/img/"));//está
		return param;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public List<EvaluacionesFuncionarioEvalLega> getListaEvalFuncionarios() {
		return listaEvalFuncionarios;
	}

	public void setListaEvalFuncionarios(
			List<EvaluacionesFuncionarioEvalLega> listaEvalFuncionarios) {
		this.listaEvalFuncionarios = listaEvalFuncionarios;
	}

}
