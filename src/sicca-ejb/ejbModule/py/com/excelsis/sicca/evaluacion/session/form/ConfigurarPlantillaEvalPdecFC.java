package py.com.excelsis.sicca.evaluacion.session.form;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.ContenidoFuncionalEnum;
import enums.TiposDatos;
import py.com.excelsis.sicca.dto.FuncionarioCptDTO;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.entity.PlantillaEvalConfDet;
import py.com.excelsis.sicca.entity.PlantillaEvalPdec;
import py.com.excelsis.sicca.entity.PlantillaEvalPdecCab;
import py.com.excelsis.sicca.entity.PlantillaEvalPdecDet;
import py.com.excelsis.sicca.entity.Sujetos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.ContenFuncionalDTO;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;



@Scope(ScopeType.CONVERSATION)
@Name("configurarPlantillaEvalPdecFC")
public class ConfigurarPlantillaEvalPdecFC implements Serializable{

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
	
	
	private List<PlantillaEvalConfDet> plantillaEvalConfDets= new ArrayList<PlantillaEvalConfDet>();
	private List<PlantillaEvalConfDet> eliminarPlantillaEvalConfDets= new ArrayList<PlantillaEvalConfDet>();
	private Long idEvaluacionDesempeno= null;
	private  Long idGrupoEvaluacion=null;
	private Long idMetodologia=null;
	private String evaluacion;
	private String grupo;
	private String plantilla;
	private Long idTipoCpt;
	private Long idCpt;
	private String codigoCpt;
	private String descripcionCpt;
	private List<SelectItem> cptSelecItem = new ArrayList<SelectItem>();
	private List<FuncionarioCptDTO> funcionarioCptDTOs= new ArrayList<FuncionarioCptDTO>();
	private List<FuncionarioCptDTO> inactivarFunCptDTOs= new ArrayList<FuncionarioCptDTO>();
	GrupoMetodologia grupoMetodologia = new GrupoMetodologia();
	private ContenidoFuncionalEnum funcionalEnum;
	private  List<GruposSujetos> gruposSujetosLista= new ArrayList<GruposSujetos>();
	private Long idCptFromList;
	private boolean esCpt;
	/**
	 *  Recibe como parámetro:  ( ID_GRUPO_METODOLOGIA)
	 * */
	private Long idGrupoMetodologia;
	private String from;
	private boolean esAsignar;
	private boolean primeraEntrada=true;
	EvaluacionDesempeno eDesempeno= new EvaluacionDesempeno();
	
	//agregado; Werner.
	private float pesoPla, pesoDir, pesoEje, pesoCon;
	private List<Float> pesos= new ArrayList<Float>();
	private Float sum;private boolean primeraRegistro=true;
	private Boolean habilitarGuardar;
	//*********************
	/**
	 * Método que inicia los parametros
	 * 
	 */
	public void init() throws Exception{
		setHabilitarGuardar(false);
		inicializar();
		cargarCabecera();
		
//		if (!seguridadUtilFormController.verificarPerteneceUoDet(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet())) {
//				throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
//		}
		ConfiguracionUoCab uoDetev=em.find(ConfiguracionUoCab.class, eDesempeno.getConfiguracionUoCab().getIdConfiguracionUo());
		if(!seguridadUtilFormController.verificarPerteneceOee(uoDetev.getIdConfiguracionUo()))
		 {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
		if(primeraEntrada){
			search();
			primeraEntrada=false;
			idCptFromList=null;
		}	
		
	}

	public void initVer() {

		try {
			inicializar();
			cargarCabecera();
			search();
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * Inicializa todo los datos Relacionados con el grupo recibido como parametro
	 * */
	private void inicializar(){
		 grupoMetodologia = em.find(GrupoMetodologia.class, idGrupoMetodologia);
		idEvaluacionDesempeno =
			grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno().getIdEvaluacionDesempeno();
		 eDesempeno= em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
			evaluacion = eDesempeno.getTitulo();
			grupo = grupoMetodologia.getGruposEvaluacion().getGrupo();
			plantilla = grupoMetodologia.getDatosEspecifMetod().getDescripcion();
	}
	/**
	 * Redirige a la pantalla de Cpt
	 * */
	public String toUoList() {
		return "/planificacion/searchForms/CptList.xhtml?from=/evaluacionDesempenho/configurarPlantillaEvalPdec/ConfigurarPlantillaEvalPdec="+idTipoCpt;
	}
	
	
	/**
	 * Actualiza el combo del cpt de acuerdo con lo seleccionado
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public void updateCpt() {
		String cadena =
			" select cpt.* from planificacion.cpt cpt " + "where cpt.activo is true "
				+ "and cpt.id_tipo_cpt = " + idTipoCpt;

		List<Cpt> lista = new ArrayList<Cpt>();
		lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
		cptSelecItem = new ArrayList<SelectItem>();
		cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (lista.size() > 0) {

			for (Cpt cpt : lista) {
				cptSelecItem.add(new SelectItem(cpt.getIdCpt(), cpt.getDenominacion()));
			}
		}

		idCpt = null;
		codigoCpt = null;
	}
	
	private void cargarCabecera() throws Exception{
		
		
		nivelEntidadOeeUtil.setIdConfigCab(eDesempeno.getConfiguracionUoCab().getIdConfiguracionUo());
//		nivelEntidadOeeUtil.setIdUnidadOrganizativa(eDesempeno.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		
	}
	/**
	 * Traer las lista de las plantillas en caso que exista.
	 * */
	@SuppressWarnings("unchecked")
	public void search(){
		plantillaEvalConfDets=em.createQuery("Select g from PlantillaEvalConfDet g " +
				" where g.grupoMetodologia.idGrupoMetodologia=:idGrupoMeto " +
				" and g.activo=true  ").setParameter("idGrupoMeto",idGrupoMetodologia).getResultList();
		listarGrupoSujetos();
		cargarDatos();
	}
	
	

	
	/**
	 * Verifica que cumpla con las validaciones antes de que agrege
	 * */
	private boolean preCondicion(){
// PDEC se toma solo por Puesto; Werner.
//		if(funcionalEnum==null|| funcionalEnum.getValor()==null){
//			statusMessages.add(Severity.ERROR,"Debe completar el campo Contenido Funcional antes de realizar esta acci&oacute;n");
//			return false;
//		}
//		if(funcionalEnum.getValor().equals("C") && idCpt==null){
//			statusMessages.add(Severity.ERROR,"Debe completar el campo Cpt Espec&iacute;fico antes de realizar esta acci&oacute;n");
//			return false;
//		}
		if(gruposSujetosLista.isEmpty()){
			statusMessages.add(Severity.ERROR,"No posee Funcionario para asignar, verifique");
			return false;
		}
		return true;
	}
	
	/**
	 * Lista los sujetos de acuerdo a idGeupoEvaluacion
	 * */
	@SuppressWarnings("unchecked")
	private void listarGrupoSujetos(){
		gruposSujetosLista=	 em.createQuery("Select gs from GruposSujetos gs " +
		" where gs.gruposEvaluacion.idGruposEvaluacion=:idGruEval and gs.activo=true ")
		.setParameter("idGruEval", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion()).getResultList();
	}
	
	public void asignar(){
		esAsignar=true;
		try {
			if(!preCondicion())
				return ;
//			String tipo=funcionalEnum.getValor();
			String tipo = "P";//solo del tipo Puesto; Werner.
			
			//agregado*******************************************************************
			pesos.add(pesoPla);pesos.add(pesoDir);pesos.add(pesoEje);pesos.add(pesoCon);
			sum = 0F;
			sum = pesoCon+pesoDir+pesoEje+pesoPla;
			//***************************************************************************
			
			//para obtener los funcionarios del grupo
			for (GruposSujetos gs : gruposSujetosLista) {
				//tomar el todos los funcionarios que se encuentran asignados al Grupo en la tabla GRUPOS_SUJETOS
				Sujetos sujetos= em.find(Sujetos.class, gs.getSujetos().getIdSujetos());
				FuncionarioCptDTO cptDTO= new FuncionarioCptDTO();
				cptDTO.setSujetos(sujetos);
				cptDTO.setMostarEstilo(true);
				funcionarioCptDTOs.add(cptDTO);
				//SI SELECCIONO CPT ESPECIFICO=c SINO SI ES PUESTO= P
				if(tipo.equals("C"))
				{// tomar el CPT asignado en el campo CPT y copiar los datos de su contenido funcional
					addRow(idCpt, sujetos);
					if(!esAsignar)
						break;
				}else{
					EmpleadoPuesto empleadoPuesto= em.find(EmpleadoPuesto.class, sujetos.getEmpleadoPuesto().getIdEmpleadoPuesto());
					//retornar los datos del contenido funcional de cada uno de sus puestos 
					addRow2(empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet(), sujetos);
					if(!esAsignar)
						break;
				}
			}
			if(!esAsignar)
			{
				statusMessages.add(Severity.ERROR,"Debe configurar previamente el contenido funcional, verifique");
				funcionarioCptDTOs= new Vector<FuncionarioCptDTO>();
				return;
			}
			habilitarGuardar = true;
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Agrega en la lista funcionarioCptDTOs de acuerdo con  el CPT 
	 * */
	
	@SuppressWarnings("unchecked")
	private void addRow(Long idCpt,Sujetos sujetos){
		String sql=" SELECT C.DESCRIPCION as desc, DET.PUNTAJE as puntaje ,  " +
				" eval_desempeno.FNC_CONTENIDO_FUNCIONAL(DET.ID_DET_CONTENIDO_FUNCIONAL) as conte," +
				" DET.ID_DET_CONTENIDO_FUNCIONAL as id " +
				" FROM PLANIFICACION.DET_CONTENIDO_FUNCIONAL DET" +
				" JOIN PLANIFICACION.CONTENIDO_FUNCIONAL C" +
				" and C.activo=true" +
				" ON C.ID_CONTENIDO_FUNCIONAL= DET.ID_CONTENIDO_FUNCIONAL" +
				" WHERE DET.ID_CPT = :idCpt and det.activo=true" +
				" ORDER BY C.ORDEN ";
		List<Object> rsl=em.createNativeQuery(sql).setParameter("idCpt", idCpt).getResultList();
		
		if(rsl.isEmpty())
		{
			esAsignar=false;
			return;
		}
		else
			esAsignar=true;
		for (Object obj : (List<Object>) rsl) {
			Object[] record = (Object[]) obj;
			FuncionarioCptDTO dto= new FuncionarioCptDTO();
			if(record[0]!=null)
				dto.setTipo( (String) record[0]);
			if(record[1]!=null)
				dto.setPuntaje(Float.parseFloat(record[1].toString()));
			if(record[2]!=null)
				dto.setActividades(record[2].toString());
			if(record[3]!=null)
			{
				Long id= Long.parseLong(record[3].toString());
				dto.setDetContenidoFuncional(em.find(DetContenidoFuncional.class, id));
			}
			
			funcionarioCptDTOs.add(dto);
		}
		
		
	}
	/***
	 * Agrega en la lista funcionarioCptDTOs de acuerdo con  la PlantaCargoDet 
	 * */
	@SuppressWarnings("unchecked")
	private void addRow2(Long idPlanta,Sujetos sujetos){
		String sql=" SELECT C.DESCRIPCION as desc, DET.PUNTAJE as puntaje ,  " +
				" eval_desempeno.FNC_CONTENIDO_FUNCIONAL(DET.ID_DET_CONTENIDO_FUNCIONAL) as conte," +
				" DET.ID_DET_CONTENIDO_FUNCIONAL as id " +
				" FROM PLANIFICACION.DET_CONTENIDO_FUNCIONAL DET" +
				" JOIN PLANIFICACION.CONTENIDO_FUNCIONAL C" +
				" ON C.ID_CONTENIDO_FUNCIONAL= DET.ID_CONTENIDO_FUNCIONAL" +
				" and C.activo=true" +
				" WHERE DET.id_planta_cargo_det = :idPlanta and det.activo=true" +
				" ORDER BY C.ORDEN ";
		List<Object> rsl=em.createNativeQuery(sql).setParameter("idPlanta", idPlanta).getResultList();
//		if(rsl.isEmpty())
//		{
//			esAsignar=false;
//			return;
//		}
//		else
//			esAsignar=true;
		for (Object obj : (List<Object>) rsl) {
			Object[] record = (Object[]) obj;
			FuncionarioCptDTO dto= new FuncionarioCptDTO();
			if(record[0]!=null)
				dto.setTipo( (String) record[0]);
			if(record[1]!=null)
				dto.setPuntaje(Float.parseFloat(record[1].toString()));
			if(record[2]!=null)
				dto.setActividades(record[2].toString());
			if(record[3]!=null)
			{
				Long id= Long.parseLong(record[3].toString());
				dto.setDetContenidoFuncional(em.find(DetContenidoFuncional.class, id));
			}
			
			funcionarioCptDTOs.add(dto);
		}
		
		
	}
	
	public void desasignar(){
		try {
			esAsignar=false;
			pesos = new ArrayList<Float>();
			sum = 0F;
			boolean fromBd=false;
			if(funcionarioCptDTOs.isEmpty()){
				statusMessages.add(Severity.ERROR,"No posee items para desasignar");
				return;
			}
			if(funcionarioCptDTOs.get(0).getPlantillaEvalPdec()!=null && funcionarioCptDTOs.get(0).getPlantillaEvalPdec().getIdPlantillaEvalPdec()!=null)
				fromBd=true;
			if(fromBd){
				for (int i = 0; i < funcionarioCptDTOs.size(); i++) {
					FuncionarioCptDTO dto=funcionarioCptDTOs.get(i);
					inactivarFunCptDTOs.add(dto);
				}
			}
			
			funcionarioCptDTOs= new ArrayList<FuncionarioCptDTO>();
			habilitarGuardar = true;
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * verifica que se habilite el boton imprimir
	 * */
	public boolean habImprimir(){
		if(!funcionarioCptDTOs.isEmpty()){
			if(funcionarioCptDTOs.get(0).getPlantillaEvalPdec()!=null&& funcionarioCptDTOs.get(0).getPlantillaEvalPdec().getIdPlantillaEvalPdec()!=null)
				return true;
		}
		return false;
	}
	
	/**
	 * Verifica que cumpla con las validaciones antes de que guarde
	 * */
	private boolean preCondicionSave(){
		
//		if(!esAsignar)
//		{
//			statusMessages.add(Severity.ERROR,"Debe configurar previamente el contenido funcional, verifique");
//			return false;
//		}
		if (esAsignar && sum != 100) {
			sum = 0F;
			statusMessages.add(Severity.ERROR,"La suma de los pesos debe ser 100, verifique");
			return false;
		}
		if(funcionarioCptDTOs.isEmpty() && from.contentEquals("/evaluacionDesempenho/revisarEvalDesempeno/revisarEvalDesempeno564.xhtml")){
			statusMessages.add(Severity.ERROR,"Debe Asignar al menos un registro, verifique");
			return false;	
		}
//		if(funcionalEnum.getValor()==null){
//			statusMessages.add(Severity.ERROR,"Ya cuenta con Contenido Funcional, debe desasignarlo antes de realizar esta operación ");
//			return false;
//		}
		return true;
		
	}
	
	public  String  save(){
		try {
			if(!preCondicionSave())
				return null;
			/**
			 * por cada sujeto
			 * i.	Tabla PLANTILLA_EVAL_PDEC 
			 * */
			if (esAsignar) {//agregado
			List<PlantillaEvalPdec> plantillaEvalPdecs= new ArrayList<PlantillaEvalPdec>();
			for (GruposSujetos gruposSujetos : gruposSujetosLista) {
				PlantillaEvalPdec plantillaEvalPdec= new PlantillaEvalPdec();
				plantillaEvalPdec.setActivo(true);
				grupoMetodologia.setCargado(true);//agregado
				plantillaEvalPdec.setGrupoMetodologia(grupoMetodologia);
				//DE ACUERDO CON LO SELECCIONADO SI ES CPT ESPECIFICO=C SINO SI ES PUESTO=P
//				plantillaEvalPdec.setTipo(funcionalEnum.getValor());
//				solo del tipo Puesto; Werner.
				plantillaEvalPdec.setTipo("P");
				plantillaEvalPdec.setSujetos(gruposSujetos.getSujetos());
				plantillaEvalPdec.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				plantillaEvalPdec.setFechaAlta(new Date());
				em.persist(plantillaEvalPdec);
				plantillaEvalPdecs.add(plantillaEvalPdec);
				
			}
			/**
			 * ii.	Tabla PLANTILLA_EVAL_PDEC_CAB
			 * */
			//1.	Si el contenido Funcional es CPT
				
			for (PlantillaEvalPdec pEvalPdec : plantillaEvalPdecs) {
//				if(funcionalEnum.getValor().equals("C")){
//					List<DetContenidoFuncional> detConFuncionals= listarDetContenidoFuncionalPorCpt(idCpt);
//					
//					for (DetContenidoFuncional detFuncional: detConFuncionals) {
//						ContenidoFuncional funcional= em.find(ContenidoFuncional.class, detFuncional.getContenidoFuncional().getIdContenidoFuncional());
//						Long orden=funcional.getOrden()!=null?Long.parseLong(funcional.getOrden().toString()): null;
//						PlantillaEvalPdecCab cab=savePlantillaEvalPdecCab(orden, detFuncional.getPuntaje(),funcional.getDescripcion(),pEvalPdec );
//						/**para cada ID_PLANTILLA_EVAL_PDEC_CAB insertado en el paso anterior
//						 * iii.	Tabla PLANTILLA_EVAL_PDEC_DET
//						 */
//						List<DetDescripcionContFuncional> descConFuc=listarDetDescContenidoFuncional(detFuncional.getIdDetContenidoFuncional());
//						for (DetDescripcionContFuncional detDescFuncional : descConFuc) {
//							savePlantillaEvalPdecDet(detDescFuncional.getDescripcion(),cab);
//						}
//						
//					}
//				}else{Solo por Puesto; Werner.
					List<DetContenidoFuncional> detConFuncionals= listarDetContenidoFuncionalPorPlanta(pEvalPdec.getSujetos().getEmpleadoPuesto().getPlantaCargoDet().getIdPlantaCargoDet());
					//agregado; Werner.
					
					//***********************
					for (DetContenidoFuncional detFuncional: detConFuncionals) {
						ContenidoFuncional funcional= em.find(ContenidoFuncional.class, detFuncional.getContenidoFuncional().getIdContenidoFuncional());
						Long orden=funcional.getOrden()!=null?Long.parseLong(funcional.getOrden().toString()): null;
						
						
						PlantillaEvalPdecCab cab=savePlantillaEvalPdecCab(orden, detFuncional.getPuntaje(),funcional.getDescripcion(),pEvalPdec, pesos );
						/**para cada ID_PLANTILLA_EVAL_PDEC_CAB insertado en el paso anterior
						 * iii.	Tabla PLANTILLA_EVAL_PDEC_DET
						 */
						List<DetDescripcionContFuncional> descConFuc=listarDetDescContenidoFuncional(detFuncional.getIdDetContenidoFuncional());
						for (DetDescripcionContFuncional detDescFuncional : descConFuc) {
							savePlantillaEvalPdecDet(detDescFuncional.getDescripcion(),cab);
						}
					}
//				}
				
			}}else{
				grupoMetodologia.setCargado(false);em.merge(grupoMetodologia);//agregado
			}
			/**
			 * eliminar los registros en caso que se guardaron
			 * */
			eliminarDetalles1();
			for (FuncionarioCptDTO delDto: inactivarFunCptDTOs) {
				PlantillaEvalPdec deldet=delDto.getPlantillaEvalPdec();
				if(deldet!=null && deldet.getIdPlantillaEvalPdec()!=null)
					em.remove(deldet);
			}
			
			em.flush();
			
			setearDatos();
			primeraEntrada=true;habilitarGuardar=false;
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return null;
		}catch (InvalidStateException e) {
		    for (InvalidValue invalidValue : e.getInvalidValues()) {
		    	statusMessages.add("Instance of bean class: " + invalidValue.getBeanClass().getSimpleName() +
		                 " has an invalid property: " + invalidValue.getPropertyName() +
		                 " with message: " + invalidValue.getMessage());
		    }
		    return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Se ha producido un error: "+e.getMessage());
			return null;
		}
		
	}
	/**
	 * elimina los detalles de la tabla PlantillaEvalPdecCab
	 * */
	private void eliminarDetalles1(){
		for (FuncionarioCptDTO dto : inactivarFunCptDTOs) {
			if( dto.getPlantillaEvalPdecCab()!=null){
				PlantillaEvalPdecCab cab= em.find(PlantillaEvalPdecCab.class, dto.getPlantillaEvalPdecCab().getIdPlantillaEvalPdecCab());
				eliminarDetalles2(cab.getIdPlantillaEvalPdecCab());
				em.remove(cab);
			}
			
		}
	}
	/**
	 * elimina los registros de la tabla  PlantillaEvalPdecDet
	 * */
	@SuppressWarnings("unchecked")
	private void eliminarDetalles2(Long idPlantillaEvalPdecCab ){
		List<PlantillaEvalPdecDet> dets=em.createQuery("Select p from PlantillaEvalPdecDet p " +
				" where p.plantillaEvalPdecCab.idPlantillaEvalPdecCab=:idCab")
				.setParameter("idCab",idPlantillaEvalPdecCab).getResultList();
		for (PlantillaEvalPdecDet plantillaEvalPdecDet : dets) {
			em.remove(plantillaEvalPdecDet);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<DetDescripcionContFuncional> listarDetDescContenidoFuncional(Long idContenidoFunc){
		List<DetDescripcionContFuncional> contFuncionals= em.createQuery("Select d from DetDescripcionContFuncional d " +
				" where d.detContenidoFuncional.idDetContenidoFuncional=:idDet and d.activo=true").setParameter("idDet", idContenidoFunc).getResultList();
		return contFuncionals;
	}
	@SuppressWarnings("unchecked")
	private List<DetContenidoFuncional> listarDetContenidoFuncionalPorCpt(Long idCpt){
		try {
		
			List<DetContenidoFuncional> funcionals=  em.createQuery("Select d from DetContenidoFuncional d " +
			" where d.cpt.idCpt=:idCpt and d.activo=true and d.contenidoFuncional.activo=true").setParameter("idCpt", idCpt).getResultList();
			return funcionals;
		} catch (NoResultException e) {
			return new ArrayList<DetContenidoFuncional>();
		}
		
	}
	@SuppressWarnings("unchecked")
	private List<DetContenidoFuncional> listarDetContenidoFuncionalPorPlanta(Long idPlanta){
		try {
		
			List<DetContenidoFuncional> funcionals=  em.createQuery("Select d from DetContenidoFuncional d " +
			" where d.plantaCargoDet.idPlantaCargoDet=:idPlanta and d.activo=true and d.contenidoFuncional.activo=true").setParameter("idPlanta", idPlanta).getResultList();
			return funcionals;
		} catch (NoResultException e) {
			return new ArrayList<DetContenidoFuncional>();
		}
		
	}
	
	/**
	 * iii.	Tabla PLANTILLA_EVAL_PDEC_DET
	 * 
	 * */
	private PlantillaEvalPdecCab savePlantillaEvalPdecCab( Long orden, float puntaje,String descripcion,PlantillaEvalPdec pdec,List<Float> pesos){
		PlantillaEvalPdecCab evalPdecCab= new PlantillaEvalPdecCab();
		evalPdecCab.setDescripcion(descripcion);
		evalPdecCab.setOrden(orden);
		evalPdecCab.setActivo(true);
		evalPdecCab.setPuntaje(puntaje);
		evalPdecCab.setPlantillaEvalPdec(pdec);
		evalPdecCab.setFechaAlta(new Date());
		//agregado; Werner.
		if (esAsignar)
			evalPdecCab.setPesoPdec(pesos.get(orden.intValue()-1));
		else
			evalPdecCab.setPesoPdec(0F);
		//*****************
		evalPdecCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		em.persist(evalPdecCab);
		return evalPdecCab;
	}
	
	/***
	 * iii.	Tabla PLANTILLA_EVAL_PDEC_DET
	 * 
	 * */
	private PlantillaEvalPdecDet savePlantillaEvalPdecDet(String descripcion,PlantillaEvalPdecCab pdecCab){
		PlantillaEvalPdecDet evalPdecDet= new PlantillaEvalPdecDet();
		evalPdecDet.setDescripcion(descripcion);
		evalPdecDet.setPlantillaEvalPdecCab(pdecCab);
		em.persist(evalPdecDet);
		return evalPdecDet;
	}
	
	public String getUrlToPageCpt() {
		if(idTipoCpt==null){
			statusMessages.add(Severity.ERROR,"Debe seleccionar el Tipo CPT ");
			return null;
		}
		return "/planificacion/searchForms/CptList.xhtml?from=evaluacionDesempenho/configurarPlantillaEvalPdec/ConfigurarPlantillaEvalPdec&tipoCpt="
			+ idTipoCpt;
	}

	@SuppressWarnings("unchecked")
	private void cargarDatos(){
		List<PlantillaEvalPdec> pdecs=em.createQuery("Select p from PlantillaEvalPdec p " +
				" where p.grupoMetodologia.idGrupoMetodologia=:idMet and p.activo=true").setParameter("idMet", grupoMetodologia.getIdGrupoMetodologia()).getResultList();
		funcionarioCptDTOs= new ArrayList<FuncionarioCptDTO>();
		primeraRegistro = true;
		for (PlantillaEvalPdec plantillaEvalPdec : pdecs) {
			FuncionarioCptDTO dto= new FuncionarioCptDTO();
			dto.setPlantillaEvalPdec(plantillaEvalPdec);
			dto.setSujetos(plantillaEvalPdec.getSujetos());
			dto.setMostarEstilo(true);
			funcionarioCptDTOs.add(dto);
			cargarDetalle(plantillaEvalPdec.getIdPlantillaEvalPdec());
		}
	}
	@SuppressWarnings("unchecked")
	private void cargarDetalle(Long idPlantillaEvalPdec){
		List<PlantillaEvalPdecCab> dets=em.createQuery("Select p from PlantillaEvalPdecCab p " +
				" where p.activo=true and p.plantillaEvalPdec.idPlantillaEvalPdec=:idPlantillaEvalPdec")
				.setParameter("idPlantillaEvalPdec",idPlantillaEvalPdec).getResultList();
		//agregado
		if (primeraRegistro) {
			sum = 0F;
			if (dets!= null) {
				for (int i = 0; i < dets.size(); i++){
					sum = sum + dets.get(i).getPesoPdec();
					switch (i) {
					case 0:
						pesoPla=dets.get(i).getPesoPdec(); 
						break;
					case 1:
						pesoDir=dets.get(i).getPesoPdec();
						break;
					case 2:
						pesoEje=dets.get(i).getPesoPdec();
						break;
					case 3:
						pesoCon=dets.get(i).getPesoPdec();
						break;
					}
				}
			}
			primeraRegistro = false;
		}
		//*************
		for (PlantillaEvalPdecCab plantillaEvalPdecCab : dets) {
			FuncionarioCptDTO dto= new FuncionarioCptDTO();
			dto.setPuntaje(plantillaEvalPdecCab.getPuntaje());
			dto.setTipo(plantillaEvalPdecCab.getDescripcion());
			dto.setActividades(actividadesDet(plantillaEvalPdecCab.getIdPlantillaEvalPdecCab()));
			dto.setPlantillaEvalPdecCab(plantillaEvalPdecCab);
			funcionarioCptDTOs.add(dto);
		}
	}
	private String actividadesDet(Long idPlantillaEvalPdecCab ) {
		String actividades="";
		try {
			actividades=(String) em.createNativeQuery("select eval_desempeno.fnc_plantilladets(:idCab)")
			.setParameter("idCab",idPlantillaEvalPdecCab).getSingleResult();
		} catch (NoResultException e) {
		}
		
		return actividades;
	}
	public String volver(){
		setearDatos();
		primeraEntrada=true;
		return from;
		
	}
	
	private void setearDatos(){
		funcionarioCptDTOs= new ArrayList<FuncionarioCptDTO>();
		grupoMetodologia= new GrupoMetodologia();
		gruposSujetosLista= new ArrayList<GruposSujetos>();
		idCpt=null;
		idCptFromList=null;
		cptSelecItem= new ArrayList<SelectItem>();
		esCpt=false;
		funcionalEnum= ContenidoFuncionalEnum.Seleccione;
		descripcionCpt= null;
		idTipoCpt=null;
		inactivarFunCptDTOs= new ArrayList<FuncionarioCptDTO>();
		codigoCpt=null;
		pesos = new ArrayList<Float>();
		primeraRegistro = true;
	}
	/**
	 * Método que busca el CPT
	 */
	@SuppressWarnings("unchecked")
	public void findCpt() {
		try {
			if (codigoCpt != null && !codigoCpt.equals("")) {
				Integer nivelCpt = null;
				Integer gradoMin = null;
				Integer gradoMax = null;
				Integer numero = null;
				Integer nroEspecifico = null;
				String[] arrayCodigo = codigoCpt.split("\\.");
				for (int i = 0; i < arrayCodigo.length; i++) {
					if (i == 0)
						nivelCpt = new Integer(arrayCodigo[i]);
					if (i == 1)
						gradoMin = new Integer(arrayCodigo[i]);
					if (i == 2)
						gradoMax = new Integer(arrayCodigo[i]);
					if (i == 3)
						numero = new Integer(arrayCodigo[i]);
					if (i == 4)
						nroEspecifico = new Integer(arrayCodigo[i]);
				}
				String cadena =
					" select cpt.* from planificacion.cpt cpt "
						+ "join planificacion.grado_salarial max "
						+ "on max.id_grado_salarial = cpt.id_grado_salarial_max "
						+ "join planificacion.grado_salarial min "
						+ "on min.id_grado_salarial = cpt.id_grado_salarial_min "
						+ "where cpt.nivel = " + nivelCpt + " and max.numero = " + gradoMax
						+ " and min.numero = " + gradoMin + " and cpt.numero = " + numero
						+ " and cpt.nro_especifico = " + nroEspecifico;
				if (idTipoCpt != null)
					cadena = cadena + " and id_tipo_cpt = " + idTipoCpt;
				
				cadena+=" order by cpt.denominacion ";
				List<Cpt> lista = new ArrayList<Cpt>();
				lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
				cptSelecItem = new ArrayList<SelectItem>();
				cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
				if (lista.size() > 0) {
					for (Cpt cpt : lista) {
						cptSelecItem.add(new SelectItem(cpt.getIdCpt(), cpt.getDenominacion()));
						idCpt = cpt.getIdCpt();
					}
				}

			}
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese un código válido");

		}

	}
	public void obtenerCodigoCpt() {

		Cpt cptActual = new Cpt();
		cptActual = em.find(Cpt.class, idCpt);

		codigoCpt =
			cptActual.getNivel() + "." + cptActual.getGradoSalarialMin().getNumero() + "."
				+ cptActual.getGradoSalarialMax().getNumero() + "." + cptActual.getNumero() + "."
				+ cptActual.getNroEspecifico();

	}
	private void buscarCodigoCpt() {

		Cpt cptActual = new Cpt();
		cptActual = em.find(Cpt.class, idCptFromList);
		descripcionCpt = cptActual.getDenominacion();
		idCpt = idCptFromList;
		codigoCpt =
			cptActual.getNivel() + "." + cptActual.getGradoSalarialMin().getNumero() + "."
				+ cptActual.getGradoSalarialMax().getNumero() + "." + cptActual.getNumero() + "."
				+ cptActual.getNroEspecifico();

	}
	/**
	 * De acuerdo al contenido funcional , estaria habilitando 
	 * */
	public void changeContFunc(ContenidoFuncionalEnum c){
		if(c.getValor()!=null && c.getValor().equals("C"))
			esCpt=true;
		else
		{
			esCpt=false;
			limpiarPanelCpt();
		}
	}
	
	private void limpiarPanelCpt(){
		idCpt=null;
		idCptFromList=null;
		codigoCpt=null;
		descripcionCpt=null;
		cptSelecItem= new ArrayList<SelectItem>();
		idTipoCpt=null;
		
	}
	/**
	 * Metodo que realiza la El reporte 
	 * */
	public void pdf() throws SQLException {
		Connection conn = null;
		try {
			HashMap<String, Object> mapa = obtenerMapaParametros();
			if (mapa == null) {
				return;
			}else
				statusMessages.clear();
			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("RPT_Metodologia_PDEC_CU568", mapa, false, conn, usuarioLogueado);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	private HashMap<String, Object> obtenerMapaParametros() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		EvaluacionDesempeno eval = em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
		param.put("oee", eval.getConfiguracionUoCab().getDenominacionUnidad());
		param.put("id", idGrupoMetodologia);
		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}
	
	//agregado****************************
	public Float suma(){
		Float suma=0F;
		for (int i = 0; i < plantillaEvalConfDets.size(); i++)
			suma = suma + plantillaEvalConfDets.get(i).getPesoPercepcion();
		return suma;
	}
	//************************************
		
	
//		GETTERS Y SETTERS 

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}


	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
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
	public String getEvaluacion() {
		return evaluacion;
	}
	public void setEvaluacion(String evaluacion) {
		this.evaluacion = evaluacion;
	}

	public Long getIdGrupoMetodologia() {
		return idGrupoMetodologia;
	}
	public void setIdGrupoMetodologia(Long idGrupoMetodologia) {
		this.idGrupoMetodologia = idGrupoMetodologia;
	}
	public List<PlantillaEvalConfDet> getPlantillaEvalConfDets() {
		return plantillaEvalConfDets;
	}
	public void setPlantillaEvalConfDets(
			List<PlantillaEvalConfDet> plantillaEvalConfDets) {
		this.plantillaEvalConfDets = plantillaEvalConfDets;
	}
	
	public List<PlantillaEvalConfDet> getEliminarPlantillaEvalConfDets() {
		return eliminarPlantillaEvalConfDets;
	}
	public void setEliminarPlantillaEvalConfDets(
			List<PlantillaEvalConfDet> eliminarPlantillaEvalConfDets) {
		this.eliminarPlantillaEvalConfDets = eliminarPlantillaEvalConfDets;
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
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public Long getIdTipoCpt() {
		return idTipoCpt;
	}
	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
	}
	public Long getIdCpt() {
		return idCpt;
	}
	public void setIdCpt(Long idCpt) {
		this.idCpt = idCpt;
	}
	public String getCodigoCpt() {
		return codigoCpt;
	}
	public void setCodigoCpt(String codigoCpt) {
		this.codigoCpt = codigoCpt;
	}
	public String getDescripcionCpt() {
		return descripcionCpt;
	}
	public void setDescripcionCpt(String descripcionCpt) {
		this.descripcionCpt = descripcionCpt;
	}
	public List<SelectItem> getCptSelecItem() {
		return cptSelecItem;
	}
	public void setCptSelecItem(List<SelectItem> cptSelecItem) {
		this.cptSelecItem = cptSelecItem;
	}
	public List<FuncionarioCptDTO> getFuncionarioCptDTOs() {
		return funcionarioCptDTOs;
	}
	public void setFuncionarioCptDTOs(List<FuncionarioCptDTO> funcionarioCptDTOs) {
		this.funcionarioCptDTOs = funcionarioCptDTOs;
	}
	public ContenidoFuncionalEnum getFuncionalEnum() {
		return funcionalEnum;
	}
	public void setFuncionalEnum(ContenidoFuncionalEnum funcionalEnum) {
		this.funcionalEnum = funcionalEnum;
	}
	public List<FuncionarioCptDTO> getInactivarFunCptDTOs() {
		return inactivarFunCptDTOs;
	}
	public void setInactivarFunCptDTOs(List<FuncionarioCptDTO> inactivarFunCptDTOs) {
		this.inactivarFunCptDTOs = inactivarFunCptDTOs;
	}
	public List<GruposSujetos> getGruposSujetosLista() {
		return gruposSujetosLista;
	}
	public void setGruposSujetosLista(List<GruposSujetos> gruposSujetosLista) {
		this.gruposSujetosLista = gruposSujetosLista;
	}
	public Long getIdCptFromList() {
		return idCptFromList;
	}
	public void setIdCptFromList(Long idCptFromList) {
		this.idCptFromList = idCptFromList;
		if (idCptFromList != null)
			buscarCodigoCpt();
	}
	public boolean isEsCpt() {
		return esCpt;
	}
	public void setEsCpt(boolean esCpt) {
		this.esCpt = esCpt;
	}

	public boolean isEsAsignar() {
		return esAsignar;
	}

	public void setEsAsignar(boolean esAsignar) {
		this.esAsignar = esAsignar;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public float getPesoPla() {
		return pesoPla;
	}

	public void setPesoPla(float pesoPla) {
		this.pesoPla = pesoPla;
	}

	public float getPesoDir() {
		return pesoDir;
	}

	public void setPesoDir(float pesoDir) {
		this.pesoDir = pesoDir;
	}

	public float getPesoEje() {
		return pesoEje;
	}

	public void setPesoEje(float pesoEje) {
		this.pesoEje = pesoEje;
	}

	public float getPesoCon() {
		return pesoCon;
	}

	public void setPesoCon(float pesoCon) {
		this.pesoCon = pesoCon;
	}

	public List<Float> getPesos() {
		return pesos;
	}

	public void setPesos(List<Float> pesos) {
		this.pesos = pesos;
	}

	public Float getSum() {
		return sum;
	}

	public void setSum(Float sum) {
		this.sum = sum;
	}

	public Boolean getHabilitarGuardar() {
		return habilitarGuardar;
	}

	public void setHabilitarGuardar(Boolean habilitarGuardar) {
		this.habilitarGuardar = habilitarGuardar;
	}

	


	

}
