package py.com.excelsis.sicca.evaluacion.session.form;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.text.SimpleDateFormat;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.SystemException;

import org.hibernate.exception.ConstraintViolationException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.persistence.ManagedPersistenceContext;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EscalaEval;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.GruposEvaluacion;
import py.com.excelsis.sicca.entity.PlantillaEvalConfDet;
import py.com.excelsis.sicca.entity.PlantillaEvalEscala;
import py.com.excelsis.sicca.entity.PlantillaEvalPdec;
import py.com.excelsis.sicca.entity.PlantillaEvalPdecCab;
import py.com.excelsis.sicca.entity.PlantillaEvalPdecDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;



@Scope(ScopeType.CONVERSATION)
@Name("adjuntarResolucionFC")
public class AdjuntarResolucionFC implements Serializable{

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
 	
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	
	
	private List<GrupoMetodologia> grupoMetodologiaLista= new ArrayList<GrupoMetodologia>();
	private Long idEvaluacionDesempeno= null;
	private  Long idGrupoEvaluacion=null;
	private Long idMetodologia=null;
	private String nombrePantalla ;
	private String direccionFisica;
	private String entity;
	private String from;
	
	
	private List<Documentos> documentoDTOList = new ArrayList<Documentos>();

	public void init(){
		try {
			if (!seguridadUtilFormController.validarInput(idEvaluacionDesempeno.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
			seguridadUtilFormController.validarEvaluacion(idEvaluacionDesempeno, referenciasUtilFormController.getReferencia(
					"ESTADOS_EVALUACION_DESEMPENO", "EVALUACION APROBADA")
					.getValorNum(), ActividadEnum.EVAL_DESEM_ADJUNTAR_RESOLUCION_FIRMADA.getValor());
			cargarCabecera();
			anexar();
			search();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void initVer(){
		try {
			if (!seguridadUtilFormController.validarInput(idEvaluacionDesempeno.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
//			agregado; Werner.***************************************
			if (from.contentEquals("admDocAdjunto"))
				from = "evaluacionDesempenho/historialCircuito/HistorialCircuitoList";
//******************************************************************	
			cargarCabecera();
			anexar();
			search();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
						
		
	}
	
	
	private void cargarCabecera() throws Exception{
		seguridadUtilFormController.cargarCabeceraEvaluacion(idEvaluacionDesempeno);
		
	}
	
	@SuppressWarnings("unchecked")
	public void search(){
		grupoMetodologiaLista=em.createQuery("Select g from GrupoMetodologia g " +
				" where g.gruposEvaluacion.evaluacionDesempeno.configuracionUoCab.idConfiguracionUo=:idUo " +
				" and g.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno=:idEval" +
				" and g.activo=true  ").setParameter("idUo", seguridadUtilFormController.getNivelEntidadOeeUtil().getIdConfigCab())
				.setParameter("idEval", idEvaluacionDesempeno).getResultList();
	}
	
	

	
	@SuppressWarnings("unchecked")
	public boolean tieneAdjuntos(){
//		List<Documentos> documentos=em.createQuery("Select d from Documentos d " +//original
//				" where d.nombreTabla=:nombreTabla and d.idTabla=:idTabla and d.nombrePantalla=:nombrePantalla")
//				.setParameter("nombreTabla",entity).setParameter("idTabla", idEvaluacionDesempeno).setParameter("nombrePantalla", nombrePantalla).getResultList();

		//agregado; Werner.		
		SimpleDateFormat sdfFechaCompleta = new SimpleDateFormat("yyyy");
		String fechaCompleta = sdfFechaCompleta.format(new Date());
		Integer anho = Integer.parseInt(fechaCompleta);
		EvaluacionDesempeno evaluacionUoCab= em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
		String q = "select * from general.documentos where documentos.activo is true "
				+ "and nombre_pantalla = '"+nombrePantalla+ "' and documentos.anho_documento = "+anho
				+ " and documentos.id_configuracion_uo = '"+evaluacionUoCab.getConfiguracionUoCab().getIdConfiguracionUo()+"'";
			documentoDTOList = em.createNativeQuery(q, Documentos.class).getResultList();
		return !documentoDTOList.isEmpty();
		//*****************
	}
	

	public String nextTask() {
		try {
			if(!tieneAdjuntos())
			{
				statusMessages.add(Severity.ERROR,"Debe adjuntar una resoluci&oacute;n antes de realizar esta acci&oacute;n");
				return null;
			}
			  /**
			    * MODIFICAR EL ESTADO DE LA EVALUACION
			    * */
			    
			    EvaluacionDesempeno evaluacionDesempeno= em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
			    evaluacionDesempeno.setEstado(referenciasUtilFormController.getReferencia(
						"ESTADOS_EVALUACION_DESEMPENO", "FINALIZADO CIRCUITO")
						.getValorNum());
			    evaluacionDesempeno.setUsuMod(usuarioLogueado.getCodigoUsuario());
			    evaluacionDesempeno.setFechaMod(new Date());
			    em.merge(evaluacionDesempeno);
			
		
			// Se pasa a la siguiente tarea
			/**
			 *SE FINALIZA LA TAREA
			 * */
			jbpmUtilFormController.setActividadActual(ActividadEnum.EVAL_DESEM_ASIGNAR_PLANTILLA);
			 jbpmUtilFormController.setTransition("adjResFir_TO_end");
		    jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EVALUACION);
		    jbpmUtilFormController.setEvaluacionDesempeno(evaluacionDesempeno);

		     
		    if (jbpmUtilFormController.nextTask()){
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
			}
			
			return "next";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"Error inesperado: "+e.getMessage());
			return null;
		}

		
	}
	

	public void anexar() {
		nombrePantalla="adjuntarResolucion";
		entity = "EvaluacionDesempeno";
		Long idOEE=seguridadUtilFormController.getNivelEntidadOeeUtil().getIdConfigCab();
		Long idUO=seguridadUtilFormController.getNivelEntidadOeeUtil().getIdUnidadOrganizativa();
		direccionFisica =
			"//SICCA//" + General.anhoActual() + "//OEE//" + idOEE + "//UO//"+idUO+"E"+idEvaluacionDesempeno;

	}
	


	




	

	
	
	
	
//		GETTERS Y SETTERS 

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}


	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}
	

	

	public List<GrupoMetodologia> getGrupoMetodologiaLista() {
		return grupoMetodologiaLista;
	}

	public void setGrupoMetodologiaLista(
			List<GrupoMetodologia> grupoMetodologiaLista) {
		this.grupoMetodologiaLista = grupoMetodologiaLista;
	}



	public Long getIdGrupoEvaluacion() {
		return idGrupoEvaluacion;
	}

	public void setIdGrupoEvaluacion(Long idGrupoEvaluacion) {
		this.idGrupoEvaluacion = idGrupoEvaluacion;
	}

	public Long getIdMetodologia() {
		return idMetodologia;
	}

	public void setIdMetodologia(Long idMetodologia) {
		this.idMetodologia = idMetodologia;
	}
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
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}

	
}
