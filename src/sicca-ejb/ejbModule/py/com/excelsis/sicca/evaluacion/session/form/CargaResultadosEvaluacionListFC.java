package py.com.excelsis.sicca.evaluacion.session.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.evaluacion.session.GrupoMetodologiaList;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("cargaResultadosEvaluacionListFC")
public class CargaResultadosEvaluacionListFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	GrupoMetodologiaList grupoMetodologiaList;

	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	private Long idEvaluacionDesempeno = null;
	private Integer estadoEnCurso;
	private Integer estadoFinalizada;
	private String from;
	private EvaluacionDesempeno evaluacionDesempeno = new EvaluacionDesempeno();
	private List<GrupoMetodologia> listaGruposMetodologia = new ArrayList<GrupoMetodologia>();
	
//agregado para la fase final; Werner*********************************************
	@In(create = true)
	ReportUtilFormController reportUtilFormController;
	private String estado;
	private List<GruposSujetos> gruposSujetos = new ArrayList<GruposSujetos>();
	private String direccionFisica;
//********************************************************************************
	
	public void init() {
		try {
			SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			if (!sufc.validarInput(idEvaluacionDesempeno.toString(), TiposDatos.LONG.getValor(), null))
				return;
			evaluacionDesempeno = em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
			//Se deshabilitó condición UO; Werner.
//			if (!seguridadUtilFormController.verificarPerteneceUoDet(evaluacionDesempeno.getConfiguracionUoDet().getIdConfiguracionUoDet())) {
//				throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
//			}
			estadoEnCurso =
				referenciasUtilFormController.getReferencia("ESTADOS_EVALUACION_GRUPO", "EVALUACION EN CURSO").getValorNum();

			estadoFinalizada =
				referenciasUtilFormController.getReferencia("ESTADOS_EVALUACION_GRUPO", "EVALUACION FINALIZADA").getValorNum();

//			seguridadUtilFormController.validarEvaluacionEstado(evaluacionDesempeno.getIdEvaluacionDesempeno(), referenciasUtilFormController.getReferencia("ESTADOS_EVALUACION_DESEMPENO", "FINALIZADO CIRCUITO").getValorNum());
//			agregado; Werner.****************************************************************************
			if (estado!=null) {
				if (estado.contentEquals("FINALIZADA"))
					seguridadUtilFormController.validarEvaluacionEstado(evaluacionDesempeno.getIdEvaluacionDesempeno(), referenciasUtilFormController.getReferencia("ESTADOS_EVALUACION_DESEMPENO", "FINALIZADA").getValorNum());
				else
					seguridadUtilFormController.validarEvaluacionEstado(evaluacionDesempeno.getIdEvaluacionDesempeno(), referenciasUtilFormController.getReferencia("ESTADOS_EVALUACION_DESEMPENO", "FINALIZADO CIRCUITO").getValorNum());
			}
//*******************************************************************************************************			
			cargarCabecera();
			searchResultados();
			generandoDireccion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void searchResultados() throws Exception {
		grupoMetodologiaList.setIdEvaluacionDesempeno(idEvaluacionDesempeno);
		grupoMetodologiaList.getGrupoMetodologia().setActivo(true);
		listaGruposMetodologia = grupoMetodologiaList.listaResultadosEvaluacion();
	}

	private void cargarCabecera() {

		ConfiguracionUoCab uo =
			em.find(ConfiguracionUoCab.class, evaluacionDesempeno.getConfiguracionUoCab().getIdConfiguracionUo());

		nivelEntidadOeeUtil.setIdConfigCab(uo.getIdConfiguracionUo());
//		nivelEntidadOeeUtil.setIdUnidadOrganizativa(uo.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
	}

	public String getUrlCargaResultado(GrupoMetodologia grupo) {
		String resultado = "/#.xhtml";
		// llama al CU 572 CARGAR RESULTADOS EVALUACION METODOLOGIA PDEC
		if (grupo.getTipo().equalsIgnoreCase("P")) {
			resultado =
				"/evaluacionDesempenho/CargarResultEvalPDEC/CargarResultEvalPDEC572.seam?idGrupoMetodologia="
					+ grupo.getIdGrupoMetodologia()
					+ "&from=/evaluacionDesempenho/cargaResultadoEvaluacion/GrupoMetodologiaList.xhtml";
			return resultado;
		}

		// llama al CU 608 CARGAR RESULTADOS EVALUACION PERCEPCION
		if (grupo.getTipo().equalsIgnoreCase("A"))
			resultado =
				"/evaluacionDesempenho/CargarResultEvalPerce/CargarResultEvalPerce608.seam?idGrupoMetodologia="
					+ grupo.getIdGrupoMetodologia()
					+ "&from=/evaluacionDesempenho/cargaResultadoEvaluacion/GrupoMetodologiaList.xhtml";
		// llama al CU 571 CARGAR RESULTADOS EVALUACION METODOLOGIA
		if (grupo.getTipo().equalsIgnoreCase("O")) {

			if (grupo.getValor().equalsIgnoreCase("G")) {
				resultado =
					"/evaluacionDesempenho/CargarResultEvalMetodo/CargarResultEvalMetodoGrupal571.seam?idGrupoMetodologia="
						+ grupo.getIdGrupoMetodologia()
						+ "&from=/evaluacionDesempenho/cargaResultadoEvaluacion/GrupoMetodologiaList.xhtml";
			} else if (grupo.getValor().equalsIgnoreCase("I")) {
				resultado =
					"/evaluacionDesempenho/CargarResultEvalMetodo/CargarResultEvalMetodoIndividual571.seam?idGrupoMetodologia="
						+ grupo.getIdGrupoMetodologia()
						+ "&from=/evaluacionDesempenho/cargaResultadoEvaluacion/GrupoMetodologiaList.xhtml";
			}

		}

		return resultado;
	}

	public String getUrlVerResultado(GrupoMetodologia grupo) {
		String resultado = "/#.xhtml";
		// llama al CU 572 CARGAR RESULTADOS EVALUACION METODOLOGIA PDEC
		if (grupo.getTipo().equalsIgnoreCase("P")) {
			resultado =
				"/evaluacionDesempenho/CargarResultEvalPDEC/CargarResultEvalPDEC572Ver.seam?idGrupoMetodologia="
					+ grupo.getIdGrupoMetodologia()
					+ "&from=/evaluacionDesempenho/cargaResultadoEvaluacion/GrupoMetodologiaList.xhtml";

			return resultado;
		}
		// llama al CU 608 CARGAR RESULTADOS EVALUACION PERCEPCION
		if (grupo.getTipo().equalsIgnoreCase("A"))
			resultado =
				"/evaluacionDesempenho/CargarResultEvalPerce/CargarResultEvalPerce608Ver.seam?idGrupoMetodologia="
					+ grupo.getIdGrupoMetodologia()
					+ "&from=/evaluacionDesempenho/cargaResultadoEvaluacion/GrupoMetodologiaList.xhtml";
		// llama al CU 571 CARGAR RESULTADOS EVALUACION METODOLOGIA
		if (grupo.getTipo().equalsIgnoreCase("O")) {

			if (grupo.getValor().equalsIgnoreCase("G")) {
				resultado =
					"/evaluacionDesempenho/CargarResultEvalMetodo/VerCargarResultEvalMetodoGrupal571.seam?idGrupoMetodologia="
						+ grupo.getIdGrupoMetodologia()
						+ "&from=/evaluacionDesempenho/cargaResultadoEvaluacion/GrupoMetodologiaList.xhtml";
			} else if (grupo.getValor().equalsIgnoreCase("I")) {
				resultado =
					"/evaluacionDesempenho/CargarResultEvalMetodo/VerCargarResultEvalMetodoIndividual571.seam?idGrupoMetodologia="
						+ grupo.getIdGrupoMetodologia()
						+ "&from=/evaluacionDesempenho/cargaResultadoEvaluacion/GrupoMetodologiaList.xhtml";
			}

		}
		return resultado;
	}

	public String valorEstado(GrupoMetodologia grupo) {
		if (grupo.getEstado() == null)
			return "";
		else
			return referenciasUtilFormController.getReferenciaPorValorNum("ESTADOS_EVALUACION_GRUPO", grupo.getEstado()).getDescAbrev();
	}

	public String finalizarEvaluacion() {
		String idGrupoEvaluacion="(";
		Boolean primeraEntrada = true;
		for (GrupoMetodologia g : listaGruposMetodologia) {
			if (g.getEstado() == null) {
				statusMessages.add(Severity.ERROR, "Debe finalizar todos los grupos de evaluaciones antes de realizar esta acción. Verifique");
				return null;
			}
			if (g.getEstado().intValue() != estadoFinalizada.intValue()) {
				statusMessages.add(Severity.ERROR, "Debe finalizar todos los grupos de evaluaciones antes de realizar esta acción. Verifique");
				return null;
			}
			//agregado; Werner.****************************
			if (primeraEntrada==true) {
				for (int i = 0; i < listaGruposMetodologia.size(); i++) {
					if (listaGruposMetodologia.size()>1 && i != (listaGruposMetodologia.size()-1)) {
						idGrupoEvaluacion = idGrupoEvaluacion+listaGruposMetodologia.get(i).getGruposEvaluacion().getIdGruposEvaluacion().toString()+",";
					}else{
						idGrupoEvaluacion = idGrupoEvaluacion+listaGruposMetodologia.get(i).getGruposEvaluacion().getIdGruposEvaluacion().toString();
					}
				}
				idGrupoEvaluacion = idGrupoEvaluacion+")";primeraEntrada=false;
			}
			//*********************************************
		}
		Referencias ref =
			referenciasUtilFormController.getReferencia("ESTADOS_EVALUACION_DESEMPENO", "FINALIZADA");
		try {
//agregado; Werner.**********************************************************************************************************			
			String q = "select * from eval_desempeno.grupos_sujetos where id_grupos_evaluacion IN "+idGrupoEvaluacion;
			gruposSujetos = em.createNativeQuery(q, GruposSujetos.class).getResultList();
			
			for (int i = 0; i < gruposSujetos.size(); i++) {
				Float pdec=Float.parseFloat(gruposSujetos.get(i).getPuntajePdec().toString());
				Float percepcion=Float.parseFloat(gruposSujetos.get(i).getPuntajePercepcion().toString());
				gruposSujetos.get(i).setPuntajeFinal(obtenerPuntajeFinal(pdec,percepcion));
				em.merge(gruposSujetos.get(i));
			}
//***************************************************************************************************************************
			
			evaluacionDesempeno.setEstado(ref.getValorNum());
			evaluacionDesempeno.setFechaMod(new Date());
			evaluacionDesempeno.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(evaluacionDesempeno);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, "No se ha podido evaluar, ha ocurrido un error "
				+ e.getMessage());
			return null;
		}
	}
	//agregado***************************************************************************
	public Float obtenerPuntajeFinal(Float pdec, Float percepcion) {
		float puntajeFinal = 0;
		if (pdec!=null && percepcion!=null)
			puntajeFinal = (pdec + percepcion)/2;
		if (pdec==null)
			puntajeFinal = percepcion;
		if (percepcion==null)
			puntajeFinal = pdec;
		puntajeFinal=round(puntajeFinal, 2);
		return puntajeFinal;
	}
	private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
	//agregado; Werner.********************************************
	public void imprimirResultados(){
		reportUtilFormController =
				(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController.init();

			reportUtilFormController.setNombreReporte("RPT_CU644_resultadoEvalFinal");
			ConfiguracionUoCab oeeUsuario =
				em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			reportUtilFormController.getParametros().put("oee_usuario", oeeUsuario.getDenominacionUnidad().toUpperCase());
			reportUtilFormController.getParametros().put("idEvaluacionDesempeno", idEvaluacionDesempeno);
			Map<String, String> diccDesc = nivelEntidadOeeUtil.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel", diccDesc.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad", diccDesc.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oee", diccDesc.get("OEE"));

			reportUtilFormController.imprimirReportePdf();
	}
	//agregado
	public void imprimirAnalisisMatriz(){
		reportUtilFormController =
				(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController.init();

			reportUtilFormController.setNombreReporte("RPT_CU580_AnalisisMatrizDesemp");
			ConfiguracionUoCab oeeUsuario =
				em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			reportUtilFormController.getParametros().put("oee_usuario", oeeUsuario.getDenominacionUnidad().toUpperCase());
			reportUtilFormController.getParametros().put("id", idEvaluacionDesempeno);
			Map<String, String> diccDesc = nivelEntidadOeeUtil.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel", diccDesc.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad", diccDesc.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oee", diccDesc.get("OEE"));

			reportUtilFormController.imprimirReportePdf();
	}
	//*************************************************************
	public void generandoDireccion() {
		//dirección física para el informe final
		Long idOEE=seguridadUtilFormController.getNivelEntidadOeeUtil().getIdConfigCab();
		setDireccionFisica("//SICCA//" + General.anhoActual() + "//OEE//" + idOEE + "EVAL.INFO.FINAL"+idEvaluacionDesempeno);
	}
	public boolean habilitandoFinalizar(){
		String query = "select * from general.documentos where documentos.activo is true and documentos.id_tabla="
					+ idEvaluacionDesempeno + " and nombre_pantalla = 'adjuntarInformeFinal'";
		List<Documentos> documentoDTOList = new ArrayList<Documentos>();
		documentoDTOList = em.createNativeQuery(query, Documentos.class)
			.getResultList();
		return !documentoDTOList.isEmpty();
	}
	//*************************************************************************************	
	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}

	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}

	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}

	public void setEvaluacionDesempeno(EvaluacionDesempeno evaluacionDesempeno) {
		this.evaluacionDesempeno = evaluacionDesempeno;
	}

	public List<GrupoMetodologia> getListaGruposMetodologia() {
		return listaGruposMetodologia;
	}

	public void setListaGruposMetodologia(List<GrupoMetodologia> listaGruposMetodologia) {
		this.listaGruposMetodologia = listaGruposMetodologia;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Integer getEstadoEnCurso() {
		return estadoEnCurso;
	}

	public void setEstadoEnCurso(Integer estadoEnCurso) {
		this.estadoEnCurso = estadoEnCurso;
	}

	public Integer getEstadoFinalizada() {
		return estadoFinalizada;
	}

	public void setEstadoFinalizada(Integer estadoFinalizada) {
		this.estadoFinalizada = estadoFinalizada;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public List<GruposSujetos> getGruposSujetos() {
		return gruposSujetos;
	}

	public void setGruposSujetos(List<GruposSujetos> gruposSujetos) {
		this.gruposSujetos = gruposSujetos;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

}
