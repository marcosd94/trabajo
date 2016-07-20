package py.com.excelsis.sicca.evaluacion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.PlantillaEvalConfDet;
import py.com.excelsis.sicca.entity.PlantillaEvalPdecCab;
import py.com.excelsis.sicca.entity.ResolucionEval;
import py.com.excelsis.sicca.entity.RevisionEvaluacion;
import py.com.excelsis.sicca.entity.Sujetos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("revisarEvalDesempeno564FC")
public class RevisarEvalDesempeno564FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private String responderObs;
	private List<GrupoMetodologia> lGrilla1;
	private List<RevisionEvaluacion> lGrilla2;
	private List<ResolucionEval> lGrilla3;
	private Long idEvalDesempenho;
	private EvaluacionDesempeno evaluacionDesempeno;
	private String evaluacionTitle;

	private String elFrom;
//	agregado; Werner.
	private String nombrePantalla ;
	private String direccionFisica;
	private String entity;
//******
	public void init() {
		cargarCabecera();
		cargarGrilla1();
		cargarGrilla2();
		cargarGrilla3();
		responderObs = null;
//		agregado; Werner.*******************************************
		anexarReglamento();
		if (elFrom!=null) {
			if (elFrom.contentEquals("admDocAdjunto"))
				elFrom = "evaluacionDesempenho/historialCircuito/HistorialCircuitoList";	
		}
//******************************************************************	
	}

	private void cargarCabecera() {
		evaluacionDesempeno = em.find(EvaluacionDesempeno.class, idEvalDesempenho);
		evaluacionTitle = evaluacionDesempeno.getTitulo();
//		nivelEntidadOeeUtil.setIdUnidadOrganizativa(evaluacionDesempeno.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdConfigCab(evaluacionDesempeno.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
	}

	private void cargarGrilla1() {
		Query q =
			em.createQuery("select GrupoMetodologia from GrupoMetodologia GrupoMetodologia "
				+ " where GrupoMetodologia.activo is true "
				+ " and GrupoMetodologia.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno = :idEvalDesempeno");
		q.setParameter("idEvalDesempeno", idEvalDesempenho);
		lGrilla1 = q.getResultList();
	}

	private void cargarGrilla2() {
		Query q =
			em.createQuery("select RevisionEvaluacion from RevisionEvaluacion RevisionEvaluacion "
				+ " where   "
				+ "   RevisionEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno= :idEvalDesempeno "
				+ " order by RevisionEvaluacion.idRevisionEval asc");
		q.setParameter("idEvalDesempeno", idEvalDesempenho);

		lGrilla2 = q.getResultList();
	}

	private void cargarGrilla3() {
		Query q =
			em.createQuery("select ResolucionEval from ResolucionEval ResolucionEval "
				+ " where ResolucionEval.evaluacionDesempeno.idEvaluacionDesempeno = :idEvalDese "
				+ "  order by ResolucionEval.nro ");
		q.setParameter("idEvalDese", idEvalDesempenho);
		lGrilla3 = q.getResultList();
	}

	private List<Sujetos> traerSujetos(Long idEvalDesempeno) {
		Query q =
			em.createQuery("select Sujetos from Sujetos Sujetos "
				+ " where Sujetos.activo is true "
				+ " and Sujetos.evaluacionDesempeno.idEvaluacionDesempeno = :idEvalDese");
		q.setParameter("idEvalDese", idEvalDesempeno);
		return q.getResultList();
	}

	private Boolean sujetoPerteneceGrupo(Long idSujeto, Long idEval) {
		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
//				+ "where GruposSujetos.activo is true "//original
				+ "where GruposSujetos.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno = :idEvalDese"
				+ " and GruposSujetos.sujetos.idSujetos = :idSujeto");
		q.setParameter("idEvalDese", idEval);
		q.setParameter("idSujeto", idSujeto);
		List lista = q.getResultList();
		if (lista.size() == 0) {
			statusMessages.add(Severity.ERROR, "Existen sujetos que no forman parte de un Grupo. Debe dar de baja o asignar al Grupo");
			return false;
		}
		return true;
	}

	private Boolean precondEnviarAAprobacion() {
		if (responderObs == null || responderObs.trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
			return false;
		}
		if (lGrilla2.size() == 0) {
			statusMessages.add(Severity.ERROR, "No hay Revisiones Solicitadas/Respuestas");
			return false;
		}
//		agregado, debe tener al menos un reglamento cargado; Werner.
		String q = "select * from general.documentos where documentos.activo is true and documentos.id_tabla="
				+ idEvalDesempenho + " and nombre_pantalla = '"+nombrePantalla+ "'";
			
		List<Documentos> documentoDTOList = new ArrayList<Documentos>();
		documentoDTOList = em.createNativeQuery(q, Documentos.class).getResultList();
			
		if (documentoDTOList.isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU605_msg_reglamento"));
			return false;
		}
//		***************************************************************
		
		
		//agregado para Percepción Pesos == 100
		for (GrupoMetodologia metodologia :lGrilla1) {
			if (metodologia.getTipo().equals("A")){
				List<PlantillaEvalConfDet> confDets=em.createQuery("Select p.pesoPercepcion from PlantillaEvalConfDet p " +
						" where p.grupoMetodologia.idGrupoMetodologia=:idGrupo and p.activo=true").
						setParameter("idGrupo", metodologia.getIdGrupoMetodologia()).getResultList();
				Float sum=0F;Float pesos =0F;String aux;
				for (int i = 0; i < confDets.size(); i++){
					aux=""+confDets.get(i);sum=sum+ Float.parseFloat(aux);//a lo bruto xD...
				}
				if (sum!=100) {
					statusMessages.add(Severity.ERROR," La suma de los Pesos en la Plantilla Percepción, Grupo "
							+ ""+metodologia.getGruposEvaluacion().getGrupo()+" debe ser igual a 100, verifique.");
					return false;
				}
			}
		}
		//*****************************************
		
		
//		List<Sujetos> lSujetos = traerSujetos(idEvalDesempenho);
//		for (Sujetos o : lSujetos) {
//			if (!sujetoPerteneceGrupo(o.getIdSujetos(), idEvalDesempenho))
//				return false;
//		}

		return true;
	}

	public String enviarAAprobacion() {
		if (!precondEnviarAAprobacion()) {
			return "FAIL";
		}
		try {
			RevisionEvaluacion re = lGrilla2.get(lGrilla2.size() - 1);
			re.setRespuesta(responderObs);
			re.setFechaRpta(new Date());
			re.setUsuRpta(usuarioLogueado.getCodigoUsuario());
			re = em.merge(re);
			/**
			 * SE PASA A SGT. TAREA
			 */
			jbpmUtilFormController.setActividadActual(ActividadEnum.EVAL_DESEM_REVISAR_EVALUACION);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EVAL_DESEM_APROBAR_EVALUACION);
			jbpmUtilFormController.setTransition("revEva_TO_aprEva");
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EVALUACION);
			jbpmUtilFormController.setEvaluacionDesempeno(evaluacionDesempeno);
			evaluacionDesempeno.setEstado(seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_EVALUACION_DESEMPENO", "ENVIADO A APROBACION"));
			evaluacionDesempeno.setUsuMod(usuarioLogueado.getCodigoUsuario());
			evaluacionDesempeno.setFechaMod(new Date());
			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

			return "OK";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
//	agregado; Werner.
	public void anexarReglamento() {
		nombrePantalla="adjuntarReglamento";
		entity = "EvaluacionDesempeno";
		Long idOEE=seguridadUtilFormController.getNivelEntidadOeeUtil().getIdConfigCab();
		Long idUO=seguridadUtilFormController.getNivelEntidadOeeUtil().getIdUnidadOrganizativa();
		direccionFisica =
			"//SICCA//" + General.anhoActual() + "//OEE//" + idOEE + "//UO//"+idUO+"REGLAM"+idEvalDesempenho;

	}
//	****************************************************************
	public String getNombrePantalla() {
		return nombrePantalla;
	}
	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}
	public String getDireccionFisica() {
		return direccionFisica;
	}
	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
//	****************************************************************

	public List<GrupoMetodologia> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<GrupoMetodologia> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

	public List<RevisionEvaluacion> getlGrilla2() {
		return lGrilla2;
	}

	public void setlGrilla2(List<RevisionEvaluacion> lGrilla2) {
		this.lGrilla2 = lGrilla2;
	}

	public String getResponderObs() {
		return responderObs;
	}

	public void setResponderObs(String responderObs) {
		this.responderObs = responderObs;
	}

	public String getEvaluacionTitle() {
		return evaluacionTitle;
	}

	public void setEvaluacionTitle(String evaluacionTitle) {
		this.evaluacionTitle = evaluacionTitle;
	}

	public List<ResolucionEval> getlGrilla3() {
		return lGrilla3;
	}

	public void setlGrilla3(List<ResolucionEval> lGrilla3) {
		this.lGrilla3 = lGrilla3;
	}

	public Long getIdEvalDesempenho() {
		return idEvalDesempenho;
	}

	public void setIdEvalDesempenho(Long idEvalDesempenho) {
		this.idEvalDesempenho = idEvalDesempenho;
	}

	public String getElFrom() {
		return elFrom;
	}

	public void setElFrom(String elFrom) {
		this.elFrom = elFrom;
	}

	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}

	public void setEvaluacionDesempeno(EvaluacionDesempeno evaluacionDesempeno) {
		this.evaluacionDesempeno = evaluacionDesempeno;
	}

}
