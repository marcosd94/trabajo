package py.com.excelsis.sicca.evaluacion.session.form;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.ResolucionEval;
import py.com.excelsis.sicca.entity.RevisionEvaluacion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;



@Scope(ScopeType.CONVERSATION)
@Name("aprobarEvaluacionDesempenhoFC")
public class AprobarEvaluacionDesempenhoFC implements Serializable{

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
	
	
	@In(required = false,create=true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(required = false,create=true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
 	
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	
	
	
	private Long idEvaluacionDesempeno= null;
	private String evaluacion;
	private String grupo;
	private String plantilla;
	private String observacionAnterior;
	private List<RevisionEvaluacion> revisionEvaluacionLista= new ArrayList<RevisionEvaluacion>();
	private List<GrupoMetodologia> grupoMetodologiaLista= new ArrayList<GrupoMetodologia>();
	private List<ResolucionEval> resolucionEvalLista= new ArrayList<ResolucionEval>();
	private EvaluacionDesempeno evaluacionDesempeno= new EvaluacionDesempeno();
	private String observacion=null;
	private String from;
	
//	agregado; Werner.
	private String nombrePantalla ;
	private String entity;
	private String direccionFisica;//para observación SFP
//******
	
	public void init() throws Exception{
	
			seguridadUtilFormController.validarEvaluacion(idEvaluacionDesempeno, referenciasUtilFormController.getReferencia(
					"ESTADOS_EVALUACION_DESEMPENO", "ENVIADO A APROBACION")
					.getValorNum(), ActividadEnum.EVAL_DESEM_APROBAR_EVALUACION.getValor());
			cargarCabecera();
			search();
			cargarObsAnterior();
//			agregado; Werner.
			consultarReglamento();
	}
	public void initVer(){
		try {
			if (!seguridadUtilFormController.validarInput(idEvaluacionDesempeno.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
//			agregado; Werner.**************************************
			if (from.contentEquals("admDocAdjunto"))
				from = "evaluacionDesempenho/historialCircuito/HistorialCircuitoList";
//******************************************************************			
			cargarCabecera();
			search();
			cargarObsAnterior();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
						
		
	}
	
	
	
	
	private void cargarCabecera() throws Exception{
		if (!seguridadUtilFormController.validarInput(idEvaluacionDesempeno.toString(), TiposDatos.LONG.getValor(), null)) {
			return ;
		}
		evaluacionDesempeno=em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
		evaluacion=evaluacionDesempeno.getTitulo();
		nivelEntidadOeeUtil.setIdConfigCab(evaluacionDesempeno.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		
	
	}
	
	@SuppressWarnings("unchecked")
	public void search(){
		/**
		 * -	Grilla “Revisiones Solicitadas /
		 *  Respuestas”: recupera los datos de la tabla “REVISION_EVALUACION”
		 * */
		revisionEvaluacionLista=em.createQuery("Select r from RevisionEvaluacion r " +
				" where r.evaluacionDesempeno.idEvaluacionDesempeno=:idEval")
				.setParameter("idEval", idEvaluacionDesempeno).getResultList();
		
		/**
		 * -	Grilla Grupos: recupera a partir de la tabla GRUPO_METODOLOGIA 
		 * **/
		grupoMetodologiaLista=em.createQuery("Select g from GrupoMetodologia g " +
				" where g.activo=true and g.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno=:idEval")
				.setParameter("idEval", idEvaluacionDesempeno).getResultList();
		/**
		 * PARA EL POPUP
		 * */
		resolucionEvalLista=em.createQuery("Select r from ResolucionEval r " +
				" where r.evaluacionDesempeno.idEvaluacionDesempeno=:idEval ").setParameter("idEval", idEvaluacionDesempeno).getResultList();
	}
	public String verCongPlantilla(Long idGrupoMetodologia){
		String url="";;
		GrupoMetodologia metodologia=em.find(GrupoMetodologia.class,idGrupoMetodologia);
		/**
		 * i.	Si GRUPO_MEOTODOLOGIA.TIPO = ‘P’ llama al CU 559-Configurar Plantilla Evaluación PDEC. 
		 * */
		if(metodologia.getTipo().equals("P"))
		{
			url="/evaluacionDesempenho/configurarPlantillaEvalPdec/VerConfigPlantillaEvalPdec.seam?" +
			"grupoMetodologiaIdGrupoMetodologia="+idGrupoMetodologia+
			"&from=/evaluacionDesempenho/aprobarEvaluacionDesempenho/AprobarEvaluacionDesempenho.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno;
			return url;
		}
		else if(metodologia.getTipo().equals("A"))//llama al CU 607
		{
			url="/evaluacionDesempenho/configurarPlantillaEvalRecepcion/ConfigurarPlantillaEvalRecList.seam?"
				+"grupoMetodologiaIdGrupoMetodologia="+idGrupoMetodologia+"&ver=true"+
				"&from=/evaluacionDesempenho/aprobarEvaluacionDesempenho/AprobarEvaluacionDesempenho.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno;
			return url;
		}
		else {
			//iii.	Caso contrario, llama al 558-Configurar Plantilla Evaluación.
			url="/evaluacionDesempenho/configurarPLantillaEvaluacion/VerConfigPlantillaEvaluacion.seam?" +
			"grupoMetodologiaIdGrupoMetodologia="+idGrupoMetodologia+
			"&from=/evaluacionDesempenho/aprobarEvaluacionDesempenho/AprobarEvaluacionDesempenho.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno;
			return url;
		}
			
	}
	public String verCongPlantillaFromVer(Long idGrupoMetodologia){
		String url="";;
		GrupoMetodologia metodologia=em.find(GrupoMetodologia.class,idGrupoMetodologia);
		/**
		 * i.	Si GRUPO_MEOTODOLOGIA.TIPO = ‘P’ llama al CU 559-Configurar Plantilla Evaluación PDEC. 
		 * */
		if(metodologia.getTipo().equals("P"))
		{
			url="/evaluacionDesempenho/configurarPlantillaEvalPdec/VerConfigPlantillaEvalPdec.seam?" +
			"grupoMetodologiaIdGrupoMetodologia="+idGrupoMetodologia+
			"&from=/evaluacionDesempenho/aprobarEvaluacionDesempenho/VerAprobacionEvaluacionDesempenho.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno;
			return url;
		}
		else if(metodologia.getTipo().equals("A"))//llama al CU 607
		{
			url="/evaluacionDesempenho/configurarPlantillaEvalRecepcion/ConfigurarPlantillaEvalRecList.seam?"
				+"grupoMetodologiaIdGrupoMetodologia="+idGrupoMetodologia+"&ver=true"+
				"&from=/evaluacionDesempenho/aprobarEvaluacionDesempenho/VerAprobacionEvaluacionDesempenho.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno;
			return url;
		}
		else {
			//iii.	Caso contrario, llama al 558-Configurar Plantilla Evaluación.
			url="/evaluacionDesempenho/configurarPLantillaEvaluacion/VerConfigPlantillaEvaluacion.seam?" +
			"grupoMetodologiaIdGrupoMetodologia="+idGrupoMetodologia+
			"&from=/evaluacionDesempenho/aprobarEvaluacionDesempenho/VerAprobacionEvaluacionDesempenho.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno;
			return url;
		}
			
	}
	private void cargarObsAnterior(){
		String sql=" SELECT 	OBS.OBSERVACION "+
					"	FROM EVAL_DESEMPENO.EVALUACION_DESEMPENO EVAL "+
					"	JOIN JBPM.JBPM_TASKINSTANCE TASK "+
					"	ON TASK.PROCINST_ = EVAL.ID_PROCESS_INSTANCE "+
					"	JOIN PROCESO.ACTIVIDAD ACT "+
					"	ON ACT.COD_ACTIVIDAD = TASK.NAME_ "+	
					"	JOIN CAPACITACION.OBS_CAP_EVAL OBS "+
					"	ON OBS.ID_EVALUACION_DESEMPENO = EVAL.ID_EVALUACION_DESEMPENO "+
					"	WHERE ACT.COD_ACTIVIDAD = 'GENERAR_RESOLUCION'  "+
					"	AND OBS.ID_EVALUACION_DESEMPENO =  :idEval";
		try {
			observacionAnterior=(String)em.createNativeQuery(sql).setParameter("idEval", idEvaluacionDesempeno).getSingleResult();
		} catch (NoResultException e) {
			// TODO: handle exception
		}
		
	}
	
	public String aprobar(){
		try {
			
			 /**
			    * MODIFICAR EL ESTADO DE LA EVALUACION
			    * */
			    
			   
			    evaluacionDesempeno.setEstado(referenciasUtilFormController.getReferencia(
						"ESTADOS_EVALUACION_DESEMPENO", "EVALUACION APROBADA")
						.getValorNum());
			    evaluacionDesempeno.setUsuMod(usuarioLogueado.getCodigoUsuario());
			    evaluacionDesempeno.setFechaMod(new Date());
			    em.merge(evaluacionDesempeno);
			
			// Se pasa a la siguiente tarea
			/**
			 *SE PASA A SGT. TAREA
			 * */
			jbpmUtilFormController.setActividadActual(ActividadEnum.EVAL_DESEM_APROBAR_EVALUACION);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EVAL_DESEM_ADJUNTAR_RESOLUCION_FIRMADA);
		    jbpmUtilFormController.setTransition("aprEva_TO_adjResFir");
		    jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EVALUACION);
		    jbpmUtilFormController.setEvaluacionDesempeno(evaluacionDesempeno);

		     
		    if (jbpmUtilFormController.nextTask()){
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
			}
			
			return "next";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error inesperado:"+e.getMessage());
			return null;
		}
	}
	
	public String enviarRevision(){
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Ha ocurriso un error: "+e.getMessage());
			return null;
			
		}
		if(observacion==null || observacion.trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe Ingresar la Observacion");
			return null;
		}
		/**
		 * i.	Guarda la OBSERVACION DE REVISION 
		 * */
		RevisionEvaluacion revisionEvaluacion= new RevisionEvaluacion();
		revisionEvaluacion.setObservacion(observacion);
		revisionEvaluacion.setFechaObs(new Date());
		revisionEvaluacion.setUsuObs(usuarioLogueado.getCodigoUsuario());
		revisionEvaluacion.setEvaluacionDesempeno(evaluacionDesempeno);
		em.persist(revisionEvaluacion);
		
		
		 /**
		    * MODIFICAR EL ESTADO DE LA EVALUACION
		    * */
		    
		   
		    evaluacionDesempeno.setEstado(referenciasUtilFormController.getReferencia(
					"ESTADOS_EVALUACION_DESEMPENO", "REVISAR EVALUACION")
					.getValorNum());
		    evaluacionDesempeno.setUsuMod(usuarioLogueado.getCodigoUsuario());
		    evaluacionDesempeno.setFechaMod(new Date());
		    em.merge(evaluacionDesempeno);
		
		// Se pasa a la siguiente tarea
		/**
		 *SE PASA A SGT. TAREA
		 * */
		jbpmUtilFormController.setActividadActual(ActividadEnum.EVAL_DESEM_APROBAR_EVALUACION);
		jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EVAL_DESEM_REVISAR_EVALUACION);
	    jbpmUtilFormController.setTransition("aprEva_TO_revEva");
	    jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EVALUACION);
	    jbpmUtilFormController.setEvaluacionDesempeno(evaluacionDesempeno);

	     
	    if (jbpmUtilFormController.nextTask()){
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		}
		
		return "next";
	}
	
//	agregado; Werner.
	public void consultarReglamento() {
		nombrePantalla="adjuntarReglamento";
		entity = "EvaluacionDesempeno";
		//dirección física para la observación de la SFP
		Long idOEE=seguridadUtilFormController.getNivelEntidadOeeUtil().getIdConfigCab();
		direccionFisica = "//SICCA//" + General.anhoActual() + "//OEE//" + idOEE + "EVAL.OBS.SFP"+idEvaluacionDesempeno;
	}
//	****************************************************************
	
//	****************************************************************
	public String getNombrePantalla() {
		return nombrePantalla;
	}
	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
//	****************************************************************
	
//		GETTERS Y SETTERS 

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}


	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}

	public String getEvaluacion() {
		return evaluacion;
	}
	public void setEvaluacion(String evaluacion) {
		this.evaluacion = evaluacion;
	}

	
	public String getPlantilla() {
		return plantilla;
	}
	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}

	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public String getObservacionAnterior() {
		return observacionAnterior;
	}
	public void setObservacionAnterior(String observacionAnterior) {
		this.observacionAnterior = observacionAnterior;
	}
	public List<RevisionEvaluacion> getRevisionEvaluacionLista() {
		return revisionEvaluacionLista;
	}
	public void setRevisionEvaluacionLista(
			List<RevisionEvaluacion> revisionEvaluacionLista) {
		this.revisionEvaluacionLista = revisionEvaluacionLista;
	}
	public List<GrupoMetodologia> getGrupoMetodologiaLista() {
		return grupoMetodologiaLista;
	}
	public void setGrupoMetodologiaLista(
			List<GrupoMetodologia> grupoMetodologiaLista) {
		this.grupoMetodologiaLista = grupoMetodologiaLista;
	}
	public List<ResolucionEval> getResolucionEvalLista() {
		return resolucionEvalLista;
	}
	public void setResolucionEvalLista(List<ResolucionEval> resolucionEvalLista) {
		this.resolucionEvalLista = resolucionEvalLista;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}
	public void setEvaluacionDesempeno(EvaluacionDesempeno evaluacionDesempeno) {
		this.evaluacionDesempeno = evaluacionDesempeno;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getDireccionFisica() {
		return direccionFisica;
	}
	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}
}
