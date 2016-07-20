package py.com.excelsis.sicca.evaluacion.session.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.SystemException;

import org.hibernate.exception.ConstraintViolationException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.persistence.ManagedPersistenceContext;

import py.com.excelsis.sicca.entity.DatosEspecificos;
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
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;



@Scope(ScopeType.CONVERSATION)
@Name("asignarMetodologiaGrupoListFC")
public class AsignarMetodologiaGrupoListFC implements Serializable{

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
	private boolean primeraEntrada=true;
	private String from;
	
	private String idGrupoMetodologiaCadena;
	private Boolean habilitarSgteTarea;
	
	private List<Long> idGrupoEvaluacion2;

	public void init() throws Exception{
		seguridadUtilFormController.validarEvaluacion(idEvaluacionDesempeno, referenciasUtilFormController.getReferencia(
				"ESTADOS_EVALUACION_DESEMPENO", "INICIADO CIRCUITO")
				.getValorNum(), ActividadEnum.EVAL_DESEM_ASIGNAR_PLANTILLA.getValor());
		cargarCabecera();
		search();
	}
	
	public void initVer(){
		try {
			if (!seguridadUtilFormController.validarInput(idEvaluacionDesempeno.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
			cargarCabecera();
			search();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void cargarCabecera() throws Exception{
		seguridadUtilFormController.cargarCabeceraEvaluacion(idEvaluacionDesempeno);
		if(primeraEntrada){
			grupoMetodologiaLista= new ArrayList<GrupoMetodologia>();
			primeraEntrada=false;
		}
		idGrupoEvaluacion=null;
		idMetodologia=null;
	}
	
	@SuppressWarnings("unchecked")
	public void search(){
		grupoMetodologiaLista=em.createQuery("Select g from GrupoMetodologia g " +
				" where g.gruposEvaluacion.evaluacionDesempeno.configuracionUoCab.idConfiguracionUo=:idUo " +
				" and g.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno=:idEval" +
				" and g.activo=true order by g.gruposEvaluacion.idGruposEvaluacion ").setParameter("idUo", seguridadUtilFormController.getNivelEntidadOeeUtil().getIdConfigCab())
				.setParameter("idEval", idEvaluacionDesempeno).getResultList();
		idGrupoMetodologiaCadena="";
		idGrupoEvaluacion2 = new ArrayList<Long>();
		habilitarSgteTarea=true;
		
		for(int i = 0; i < grupoMetodologiaLista.size(); i++) {//agregado; Werner.
			Long aux=0l;
			if (i==0){
				idGrupoMetodologiaCadena= grupoMetodologiaLista.get(i).getIdGrupoMetodologia()+idGrupoMetodologiaCadena;
				idGrupoEvaluacion2.add(grupoMetodologiaLista.get(i).getGruposEvaluacion().getIdGruposEvaluacion());
				aux=grupoMetodologiaLista.get(i).getGruposEvaluacion().getIdGruposEvaluacion();
			}else
			{
				idGrupoMetodologiaCadena= idGrupoMetodologiaCadena+","+grupoMetodologiaLista.get(i).getIdGrupoMetodologia();
				if (aux != grupoMetodologiaLista.get(i).getGruposEvaluacion().getIdGruposEvaluacion()) {
					idGrupoEvaluacion2.add(grupoMetodologiaLista.get(i).getGruposEvaluacion().getIdGruposEvaluacion());
					aux = grupoMetodologiaLista.get(i).getGruposEvaluacion().getIdGruposEvaluacion();
				}
				else{
					aux = grupoMetodologiaLista.get(i).getGruposEvaluacion().getIdGruposEvaluacion();
				}
			}
			if (grupoMetodologiaLista.size()==1)
				habilitarSgteTarea = false;
			else{
				for (int j = 0; j < idGrupoEvaluacion2.size(); j++) {
					System.out.println(idGrupoEvaluacion2.get(j));
					int cont=0;
					for (int k = 0; k < grupoMetodologiaLista.size(); k++) {
						if (idGrupoEvaluacion2.get(j) == grupoMetodologiaLista.get(k).getGruposEvaluacion().getIdGruposEvaluacion()) {
							cont++;
						}
					}
					if (cont != 2) {
						habilitarSgteTarea = false;
					}
				}
				idGrupoEvaluacion2 = new ArrayList<Long>();
			}
				
		}
	}
	
	

	public List<SelectItem> getGrupoEvalSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (GruposEvaluacion o : getGrupoEval())
			si.add(new SelectItem(o.getIdGruposEvaluacion(), "" + o.getGrupo()));
		return si;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<GruposEvaluacion> getGrupoEval() {
		try {
			List<GruposEvaluacion> evaluacions=em.createQuery("Select e from GruposEvaluacion e" +
					" where e.evaluacionDesempeno.idEvaluacionDesempeno=:idEval and e.activo=true ")
					.setParameter("idEval", idEvaluacionDesempeno).getResultList();

			return evaluacions;
		} catch (Exception ex) {
			return new Vector<GruposEvaluacion>();
		}
	}
	
	
	
	public void desasignar(Long idGrupoMet) {
		try {
			GrupoMetodologia grupoMetodologia= em.find(GrupoMetodologia.class, idGrupoMet);
			/**
			 * 1.	Si corresponde a la metodología PDEC (GRUPO_MEOTODOLOGIA.TIPO = ‘P’), 
			 * */
			if(grupoMetodologia.getTipo().equals("P")){
				//eliminar los registros relacionados 
				//PLANTILLA_EVAL_PDEC, 
				borrarEvalPdec(idGrupoMet,listarPlantillaEvalPdecXGrupo(idGrupoMet));
				//PLANTILLA_EVAL_PDEC_CAB, 
				borrarEvalPdecCab(idGrupoMet,listarPlantillaEvalPdecCabXGrupo(idGrupoMet));
				//PLANTILLA_EVAL_PDEC_DET
				borrarEvalPdecDet(idGrupoMet,listarPlantillaEvalPdecDetXGrupo(idGrupoMet));
				
			}else if(grupoMetodologia.getTipo().equals("O")){
				//PLANTILLA_EVAL_CONF_DET, 
				borrarEvalConfDet(idGrupoMet,listarPlantillaEvalConfDetXGrupo(idGrupoMet));
			}else  if(grupoMetodologia.getTipo().equals("A")){
				//PLANTILLA_EVAL_CONF_DET, 
				borrarEvalConfDet(idGrupoMet,listarPlantillaEvalConfDetXGrupo(idGrupoMet) );
				//PLANTILLA_EVAL_ESCALA 
				borrarPlantillaEvalEsca(idGrupoMet,listarPlantillaEvalEscalaXGrupo(idGrupoMet));
				//ESCALA_EVAL
				borrarEvalEscala(idGrupoMet,listarEscalaEvalXGrupo(idGrupoMet));
				
			}
			//4.	Finalmente eliminar el registro en la tabla GRUPO_METODOLOGIA
			borrarGrupoMetodologia(grupoMetodologia);
			
			search();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"Se ha producido un error: "+e.getMessage());
			e.printStackTrace();
		}
	}
	public String nextTask() {
		try {
			if(!estaEvaluacion())
				return null;
			if(!tinePlastillaConf())
				return null;
			  /**
			    * MODIFICAR EL ESTADO DE LA EVALUACION
			    * */
			    
			    EvaluacionDesempeno evaluacionDesempeno= em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
			    evaluacionDesempeno.setEstado(referenciasUtilFormController.getReferencia(
						"ESTADOS_EVALUACION_DESEMPENO", "RESOLUCION EVALUACION")
						.getValorNum());
			    evaluacionDesempeno.setUsuMod(usuarioLogueado.getCodigoUsuario());
			    evaluacionDesempeno.setFechaMod(new Date());
			    em.merge(evaluacionDesempeno);
			
		
			// Se pasa a la siguiente tarea
			/**
			 *SE PASA A SGT. TAREA
			 * */
			jbpmUtilFormController.setActividadActual(ActividadEnum.EVAL_DESEM_ASIGNAR_PLANTILLA);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EVAL_DESEM_GENERAR_RESOLUCION);
		    jbpmUtilFormController.setTransition("asgPlan_TO_genRes");
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
	/**
	 *  GRUPOS_EVALUACION estén en la tabla GRUPO_METODOLOGIA y que cada uno esté configurado,
	 * @return  SI NO TIENE METODOLOGIA ASIGNADA false
	 * */
	@SuppressWarnings("unchecked")
	private boolean estaEvaluacion(){
		
		List<GruposEvaluacion> evaluacions=em.createQuery("Select ge from GruposEvaluacion ge " +
				" where ge.evaluacionDesempeno.idEvaluacionDesempeno=:idEva and ge.activo=true").setParameter("idEva", idEvaluacionDesempeno).getResultList();
		String grupo=null;
		for (GruposEvaluacion gruposEvaluacion : evaluacions) {
			boolean esta=false;
			for (GrupoMetodologia gMetodologia : grupoMetodologiaLista) {
				if(gMetodologia.getGruposEvaluacion().getIdGruposEvaluacion().longValue()==gruposEvaluacion.getIdGruposEvaluacion().longValue())
				{
					esta=true;
					break;
				}
				
			}
			if(!esta)
			{
				String decripcion=gruposEvaluacion.getGrupo();
				grupo=(grupo==null?decripcion:grupo+" ; "+decripcion);
				
			}
			
		}
		if(grupo!=null){
			statusMessages.add(Severity.ERROR,"Debe asignar alguna Plantilla al/los Grupo :"+grupo);
			return false;
		}
		return true;
		
	}
	
	/**
	 * 
	 * @return SI NO TIENE PLANTILLA CONFIGURADA false
	 * */
	private boolean tinePlastillaConf(){
		String grupo=null;
		
		for (GrupoMetodologia metodologia :grupoMetodologiaLista) {
			boolean tiene=true;
			// que tengan por lo menos un registro por cada tipo
			if(metodologia.getTipo().equals("P")){
				
				//PLANTILLA_EVAL_PDEC
				if(!tienePlantillaEvalPdec(metodologia.getIdGrupoMetodologia()))
					tiene=false;
				//PLANTILLA_EVAL_PDEC_CAB
				if(!tienePlantillaEvalPdecCab(metodologia.getIdGrupoMetodologia()))
					tiene=false;
				//PLANTILLA_EVAL_PDEC_DET
				if(!tienePlantillaEvalPdecDet(metodologia.getIdGrupoMetodologia()))
					tiene=false;
				
			}else if(metodologia.getTipo().equals("O")){
				//PLANTILLA_EVAL_CONF_DET
				if(!tienePlantillaEvalConfDet(metodologia.getIdGrupoMetodologia()))
					tiene=false;
			}else if(metodologia.getTipo().equals("A")){
				//PLANTILLA_EVAL_CONF_DET
				if(!tienePlantillaEvalConfDet(metodologia.getIdGrupoMetodologia()))
					tiene=false;
				//PLANTILLA_EVAL_ESCALA
				if(!tienePlantillaEvalEscala(metodologia.getIdGrupoMetodologia()))
					tiene=false;
				//agregado para Percepción Pesos == 100
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
				//*****************************************
			}
			//PARA CUALQUIERA DE LOS CASOS
			if(!tiene)
			{
				String met=metodologia.getGruposEvaluacion().getGrupo()+"-"+metodologia.getDatosEspecifMetod().getDescripcion();
				grupo=(grupo==null?met:grupo+" ; "+met);
			}
		}
		if(grupo!=null){
			statusMessages.add(Severity.ERROR," Debe configurar la Plantilla para el/los Grupo: "+grupo);
			return false;
		}
		return true;
	}
	/**
	 * verifica que tenga registro de la tabla
	 * PlantillaEvalPdec
	 * */
	private boolean tienePlantillaEvalPdec(Long idGrupoMetodologia){
		return !listarPlantillaEvalPdecXGrupo(idGrupoMetodologia).isEmpty();
	}
	/**
	 * verifica que tenga registro de la tabla
	 * PlantillaEvalPdecCab
	 * */
	private boolean tienePlantillaEvalPdecCab(Long idGrupoMetodologia){
		return !listarPlantillaEvalPdecCabXGrupo(idGrupoMetodologia).isEmpty();
	}
	/**
	 * verifica que tenga registro de la tabla
	 * PlantillaEvalPdecDet
	 * */
	private boolean tienePlantillaEvalPdecDet(Long idGrupoMetodologia){
		return !listarPlantillaEvalPdecDetXGrupo(idGrupoMetodologia).isEmpty();
	}
	/**
	 * verifica que tenga registro de la tabla
	 * PlantillaEvalConfDet
	 * */
	private boolean tienePlantillaEvalConfDet(Long idGrupoMetodologia){
		return !listarPlantillaEvalConfDetXGrupo(idGrupoMetodologia).isEmpty();
	}
	/**
	 * verifica que tenga registro de la tabla
	 * PlantillaEvalEscala
	 * */
	private boolean tienePlantillaEvalEscala(Long idGrupoMetodologia){
		return !listarPlantillaEvalEscalaXGrupo(idGrupoMetodologia).isEmpty();
	}
	/**
	 * listar de la tabla
	 *  PlantillaEvalPdec
	 * del grupo relacionado 
	 * 
	 * */
	@SuppressWarnings("unchecked")
	private List<PlantillaEvalPdec> listarPlantillaEvalPdecXGrupo(Long idGrupoMetodologia){
		List<PlantillaEvalPdec> evalPdecs=em.createQuery("Select p from PlantillaEvalPdec p " +
		" where p.grupoMetodologia.idGrupoMetodologia=:idGrupo and p.activo=true").setParameter("idGrupo", idGrupoMetodologia).getResultList();
		return evalPdecs;
	}
	/**
	 * Listar de la tabla 
	 * PlantillaEvalPdecCab
	 * del grupo relacionado
	 * */
	@SuppressWarnings("unchecked")
	private List<PlantillaEvalPdecCab> listarPlantillaEvalPdecCabXGrupo(Long idGrupoMetodologia){
		List<PlantillaEvalPdecCab> evalPdecsCab=em.createQuery("Select p from PlantillaEvalPdecCab p " +
		" where p.plantillaEvalPdec.grupoMetodologia.idGrupoMetodologia=:idGrupo and p.activo=true").setParameter("idGrupo", idGrupoMetodologia).getResultList();

		return evalPdecsCab;
	}
	/**
	 * Listar de la tabla 
	 * PlantillaEvalPdecDet 
	 * del grupo relacionado
	 * */
	@SuppressWarnings("unchecked")
	private List<PlantillaEvalPdecDet> listarPlantillaEvalPdecDetXGrupo(Long idGrupoMetodologia){
		List<PlantillaEvalPdecDet> evalPdecsDet=em.createQuery("Select p from PlantillaEvalPdecDet p " +
		" where p.plantillaEvalPdecCab.plantillaEvalPdec.grupoMetodologia.idGrupoMetodologia=:idGrupo ").setParameter("idGrupo", idGrupoMetodologia).getResultList();

		return evalPdecsDet;
	}
	/**
	 * Listar de la tabla 
	 * PlantillaEvalConfDet 
	 * del grupo relacionado 
	 * */
	@SuppressWarnings("unchecked")
	private List<PlantillaEvalConfDet> listarPlantillaEvalConfDetXGrupo(Long idGrupoMetodologia){
		List<PlantillaEvalConfDet> confDets=em.createQuery("Select p from PlantillaEvalConfDet p " +
		" where p.grupoMetodologia.idGrupoMetodologia=:idGrupo and p.activo=true").setParameter("idGrupo", idGrupoMetodologia).getResultList();

		return confDets;
	}
	/**
	 * Listar de la tabla 
	 * PlantillaEvalEscala
	 * del grupo relacionado
	 * */
	@SuppressWarnings("unchecked")
	private List<PlantillaEvalEscala> listarPlantillaEvalEscalaXGrupo(Long idGrupoMetodologia){
		List<PlantillaEvalEscala> pEvalEscalas=em.createQuery("Select p from PlantillaEvalEscala p " +
		" where p.plantillaEvalConfDet.grupoMetodologia.idGrupoMetodologia=:idGrupo and p.activo=true").setParameter("idGrupo", idGrupoMetodologia).getResultList();

		return pEvalEscalas;
	}
	/**
	 * Listar de la tabla 
	 * EscalaEval
	 * del grupo relacionado
	 * */
	@SuppressWarnings("unchecked")
	private List<EscalaEval> listarEscalaEvalXGrupo(Long idGrupoMetodologia){
		List<EscalaEval> evals=em.createQuery("Select p from EscalaEval p " +
		" where p.grupoMetodologia.idGrupoMetodologia=:idGrupo and p.activo=true ")
		.setParameter("idGrupo", idGrupoMetodologia).getResultList();
		return evals;
	}
	

	
	
	public void borrarEvalPdec(Long idGrupoMetodologia ,List<PlantillaEvalPdec> evalPdecsDel){
		ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
		EntityManager em;
		try {
			em = persistenceContext.getEntityManager();
			
			for (PlantillaEvalPdec plantillaEvalPdec : evalPdecsDel) {
				em.remove(plantillaEvalPdec);
			}
			em.flush();
		}catch (NamingException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}

	public void borrarEvalPdecCab(Long idGrupoMetodologia,List<PlantillaEvalPdecCab> evalPdecsCabDel){
		ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
		EntityManager em;
		try {
			em = persistenceContext.getEntityManager();
			
			for (PlantillaEvalPdecCab plantillaEvalPdecCab : evalPdecsCabDel) {
				em.remove(plantillaEvalPdecCab);
			}
			em.flush();
		}catch (NamingException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}

	public void borrarEvalPdecDet(Long idGrupoMetodologia,List<PlantillaEvalPdecDet> evalPdecsDetDel){
		ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
		EntityManager em;
		try {
			em = persistenceContext.getEntityManager();
			
			for (PlantillaEvalPdecDet plantillaEvalPdecDet : evalPdecsDetDel) {
				em.remove(plantillaEvalPdecDet);
			}
			em.flush();
		}catch (NamingException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}

	public void borrarEvalConfDet(Long idGrupoMetodologia,List<PlantillaEvalConfDet> confDets){
		ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
		EntityManager em;
		try {
			em = persistenceContext.getEntityManager();
			
			for (PlantillaEvalConfDet confDet : confDets) {
				em.remove(confDet);
			}
			em.flush();
		}catch (NamingException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}catch (ConstraintViolationException e) {
			e.printStackTrace();
		}
	}

	public void borrarPlantillaEvalEsca(Long idGrupoMetodologia,List<PlantillaEvalEscala> pEvalEscalas){
		ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
		EntityManager em;
		try {
			em = persistenceContext.getEntityManager();
//			List<PlantillaEvalEscala> pEvalEscalas=em.createQuery("Select p from PlantillaEvalEscala p " +
//					" where p.plantillaEvalConfDet.grupoMetodologia.idGrupoMetodologia=:idGrupo").setParameter("idGrupo", idGrupoMetodologia).getResultList();
//			
			for (PlantillaEvalEscala evalEscala : pEvalEscalas) {
				em.remove(evalEscala);
			}
			em.flush();
		}catch (NamingException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}

	public void borrarEvalEscala(Long idGrupoMetodologia,List<EscalaEval> evals){
		ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
		EntityManager em;
		try {
			em = persistenceContext.getEntityManager();
//			List<EscalaEval> evals=em.createQuery("Select p from EscalaEval p " +
//					" where p.grupoMetodologia.idGrupoMetodologia=:idGrupo")
//					.setParameter("idGrupo", idGrupoMetodologia).getResultList();
//			
			for (EscalaEval evalEscala : evals) {
				em.remove(evalEscala);
			}
			em.flush();
		}catch (NamingException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}


	public void borrarGrupoMetodologia(GrupoMetodologia metodologia){
		ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
		EntityManager em;
		try {
			em = persistenceContext.getEntityManager();
			em.remove(metodologia);
			em.flush();
		}catch (NamingException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	private boolean checkValue(){
		if(idGrupoEvaluacion==null){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Grupo antes de realizar esta acción");
			return false;
		}
		if(idMetodologia==null){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Metodologia antes de realizar esta acción");
			return false;
		}
		List<GrupoMetodologia> grupoMetodologias=em.createQuery("Select gm from GrupoMetodologia gm" +
				" where gm.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno=:idEval and gm.datosEspecifMetod.idDatosEspecificos=:idPlan" +
				" and gm.gruposEvaluacion.idGruposEvaluacion=:idGrupoEval").setParameter("idPlan", idMetodologia).setParameter("idGrupoEval", idGrupoEvaluacion)
				.setParameter("idEval", idEvaluacionDesempeno).getResultList();
		if(!grupoMetodologias.isEmpty()){
			statusMessages.add(Severity.ERROR,"El Registro ingresado ya existe, verifique");
			return false;
		}
		
		return true;
	}
	
	public void asignar(){
		try {
			if(!checkValue())
				return ;
			
			String tipo=null;
			/**
			 * guarda en la tabla  GRUPO_METODOLOGIA
			 * */
			GrupoMetodologia grupoMetodologia= new GrupoMetodologia();
			grupoMetodologia.setActivo(true);
			grupoMetodologia.setCargado(false);//agregado
			DatosEspecificos plantilla= em.find(DatosEspecificos.class, idMetodologia);
			grupoMetodologia.setDatosEspecifMetod(plantilla);
			grupoMetodologia.setGruposEvaluacion(em.find(GruposEvaluacion.class, idGrupoEvaluacion));
			grupoMetodologia.setActivo(true);
			grupoMetodologia.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			grupoMetodologia.setFechaAlta(new Date());
			/**
			 * iii.	TIPO: guardar el valor que se encuentra en DATOS_ESPECIFICOS.VALOR_ALF
			 * */
			if(plantilla.getValorAlf()==null || plantilla.getValorAlf().trim().equals(""))
				tipo="O";
			else
				tipo=plantilla.getValorAlf();
			grupoMetodologia.setTipo(tipo);
			/**
			 * tipo = ‘O’ y el DATOS_ESPECIFICOS.VALOR_NUM de la metodología es 1
			 * */
			if(tipo!=null && tipo.equals("O"))
			{
				if( plantilla.getValorNum()!=null&& plantilla.getValorNum().intValue()==1){
					grupoMetodologia.setValor("G");
				}else
					grupoMetodologia.setValor("I");
			}
			
				
			em.persist(grupoMetodologia);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			idGrupoEvaluacion=null;
			idMetodologia=null;
			search();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * b.	Enlace Configurar Plantilla: 
	 * Para cualquiera de los casos le pasa como parámetro (CU557, ID_GRUPO_METODOLOGIA)
	 * */
	public String congPlantilla(Long idGrupoMetodologia){
		try {
			GrupoMetodologia grupoMetodologia= em.find(GrupoMetodologia.class, idGrupoMetodologia);
			String url=null;
			/**
			 * 	i.	Si GRUPO_MEOTODOLOGIA.TIPO = ‘P’ 
			 * llama al CU 559-Configurar Plantilla Evaluación PDEC
			 * */
			if(grupoMetodologia.getTipo().equals("P"))
			{
				url="/evaluacionDesempenho/configurarPlantillaEvalPdec/ConfigurarPlantillaEvalPdec.seam?" +
						"grupoMetodologiaIdGrupoMetodologia="+idGrupoMetodologia+
						"&from=/evaluacionDesempenho/metodologiaGrupo/AsignarMetodologiaGrupo.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno+"&conversationPropagation=join";
				return url;
			}
			
			else if(grupoMetodologia.getTipo().equals("A"))
			{
				/**
				 * ii.	Si GRUPO_MEOTODOLOGIA.TIPO = ‘A’ 
				 * Llama al CU 607-Configurar Plantilla Evaluación de Percepción
				 * */
				
				url="/evaluacionDesempenho/configurarPlantillaEvalRecepcion/ConfigurarPlantillaEvalRecList.seam?" +
				"grupoMetodologiaIdGrupoMetodologia="+idGrupoMetodologia+
				"&from=/evaluacionDesempenho/metodologiaGrupo/AsignarMetodologiaGrupo.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno+"&conversationPropagation=join";
				if(tieneEscala())
					return url;
				else
				{
					statusMessages.add(Severity.ERROR,"Debe administrar la escala antes de configurar la Plantilla");
					return null;
				}
			}
			else if(grupoMetodologia.getTipo().equals("O"))
			{
				/**
				 *iii.	Si GRUPO_MEOTODOLOGIA.TIPO = ‘O’,
				 * llama al 558-Configurar Plantilla Evaluación. 
				 * */
				url="/evaluacionDesempenho/configurarPLantillaEvaluacion/ConfigurarPlantillaEvaluacion.seam?" +
						"grupoMetodologiaIdGrupoMetodologia="+idGrupoMetodologia+
						"&from=/evaluacionDesempenho/metodologiaGrupo/AsignarMetodologiaGrupo.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno+"&conversationPropagation=join";
				return url;
				
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 *a.	Enlace Ver Configuración de Plantilla: 
	 * Para cualquiera de los casos le pasa como parámetro (CU557, ID_GRUPO_METODOLOGIA)
	 * */
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
			"&from=/evaluacionDesempenho/metodologiaGrupo/VerAsigMetodologiaGrupo.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno;
			return url;
		}
		else if(metodologia.getTipo().equals("A"))//llama al CU 607
		{
			url="/evaluacionDesempenho/configurarPlantillaEvalRecepcion/ConfigurarPlantillaEvalRecList.seam?" +
			"grupoMetodologiaIdGrupoMetodologia="+idGrupoMetodologia+
			"&from=/evaluacionDesempenho/metodologiaGrupo/VerAsigMetodologiaGrupo.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno+"&ver=true&conversationPropagation=join";
			return url;
		}
		else {
			//iii.	Caso contrario, llama al 558-Configurar Plantilla Evaluación.
			url="/evaluacionDesempenho/configurarPLantillaEvaluacion/VerConfigPlantillaEvaluacion.seam?" +
			"grupoMetodologiaIdGrupoMetodologia="+idGrupoMetodologia+
			"&from=/evaluacionDesempenho/metodologiaGrupo/VerAsigMetodologiaGrupo.seam&evaluacionDesempenoIdEvaluacionDesempeno="+idEvaluacionDesempeno;
			return url;
		}
			
	}
	
	/**
	 * @return 
	 * true -tiene escala
	 * false -no tiene escala
	 * */
	@SuppressWarnings("unchecked")
//	private boolean tieneEscala(){
	public boolean tieneEscala(){
		List<EscalaEval> escalaEvals= em.createQuery("Select e from EscalaEval e" +
				" where e.grupoMetodologia.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno=:idEval " +
				" and e.activo=true")
				.setParameter("idEval",idEvaluacionDesempeno).getResultList();
		return !escalaEvals.isEmpty();
	}
//	agregado; Werner.******************************************************
	public boolean bloqueandoEscala(){
		return !grupoMetodologiaLista.isEmpty();
	}
	public boolean bloqueandoEscala2(){
		boolean aux = false;
		for (int i = 0; i < grupoMetodologiaLista.size(); i++) {
			if (grupoMetodologiaLista.get(i).getCargado()==true)
				aux = true;
		}
		return aux;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean cambiandoMsjLink(Long idGrupoMetodologia){
		GrupoMetodologia grupoMetodologia= em.find(GrupoMetodologia.class, idGrupoMetodologia);
		return grupoMetodologia.getCargado();
	}
//**************************************************************************	
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
	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}
	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getIdGrupoMetodologiaCadena() {
		return idGrupoMetodologiaCadena;
	}
	public void setIdGrupoMetodologiaCadena(String idGrupoMetodologiaCadena) {
		this.idGrupoMetodologiaCadena = idGrupoMetodologiaCadena;
	}

	public Boolean getHabilitarSgteTarea() {
		return habilitarSgteTarea;
	}

	public void setHabilitarSgteTarea(Boolean habilitarSgteTarea) {
		this.habilitarSgteTarea = habilitarSgteTarea;
	}
	

	
}
