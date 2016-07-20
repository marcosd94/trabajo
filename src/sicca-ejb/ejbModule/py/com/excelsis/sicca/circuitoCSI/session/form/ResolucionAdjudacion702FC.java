package py.com.excelsis.sicca.circuitoCSI.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.velocity.VelocityContext;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import enums.ActividadEnum;

@Scope(ScopeType.CONVERSATION)
@Name("resolucionAdjudacion702FC")
public class ResolucionAdjudacion702FC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	@In(required = false)
	Usuario usuarioLogueado;

	private List<ConcursoPuestoAgr> puestoAgrList = new ArrayList<ConcursoPuestoAgr>();

	private Long idConcurso;// Recibe del cu que le llama
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private Concurso concurso;// enviado por el CU
	private String obs;

	private List<Resolucion> resolucionAdjudicacionList = new ArrayList<Resolucion>();
	private String paramMemo;
	private String paramNota;
	private String paramReso;
	private Long paramIdConcurso;

	private Long idResoEdit = null;
	private Long idResoConsurso;
	private Long idConcursoPuestoAgr;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private String nombrePantalla = "elaborarResolucionAdjudicacion_editConcursoSimplificado";
	private String direccionFisica;
	private String entity;
	private Long idSave = null;
	private boolean habReso;
	private SeguridadUtilFormController seguridadUtilFormController;
	Date fechaSistema=new Date();
	private boolean habBotones=true;
	private boolean habcControles=false;
	private String codActividad;


	public void init() {
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		validarOee();
		codActividad=ActividadEnum.CSI_ELABORAR_RESOLUCION_ADJUDICACION.getValor();
		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = em.find(Concurso.class, idConcurso);
		validaciones1y2();
		cargarGrupos();
		findEntidades();// Trae las entidades segun el grupo que se envio
		cargarListas();
		verificarResolucion();
		anexar();
	}
	@SuppressWarnings("unchecked")
	private void cargarGrupos(){
		puestoAgrList =
			em.createQuery("Select c from ConcursoPuestoAgr c   where c.concurso.idConcurso=:idConcurso"
					+ " and c.estado =:estado  and c.activo=true")
					.setParameter("idConcurso",idConcurso).setParameter("estado", estadoADJUDICADO() ).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	private void validaciones1y2(){
		List<ConcursoPuestoAgr> agrList =
			em.createQuery("Select c from ConcursoPuestoAgr c   where c.concurso.idConcurso=:idConcurso"
				+ " and c.estado =:estado  and c.activo=true")
				.setParameter("idConcurso",idConcurso).setParameter("estado", estadoADJUDICADO() ).getResultList();
		
		for (int i = 0; i < agrList.size(); i++) {
			if(validacion1(agrList.get(i))){
				validacion2(agrList.get(i));
			}
		}
		
	}
	 public String endTask(ConcursoPuestoAgr cAgr){
		 try {
		
			
			//Se finaliza  la tarea
			jbpmUtilFormController.setConcursoPuestoAgr(cAgr);
			jbpmUtilFormController.setActividadPorGrupo(true);
			jbpmUtilFormController.setActividadActual(ActividadEnum.CSI_ELABORAR_RESOLUCION_ADJUDICACION);
			jbpmUtilFormController.setTransition("end");
			if(jbpmUtilFormController.nextTask())
				em.flush();
			else
				new Excepcion();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "end";
			 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	 }

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		seguridadUtilFormController.verificarPerteneceOeeCSI(null, concursoPuestoAgr.getIdConcursoPuestoAgr(), seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "ADJUDICADO")
			+ "", ActividadEnum.CSI_ELABORAR_RESOLUCION_ADJUDICACION.getValor());
	}

	@SuppressWarnings("unchecked")
	private Integer estadoADJUDICADO() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'ADJUDICADO' ").getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}
	
	/**
	 * @return cantidad de  adjudicados
	 * */
	@SuppressWarnings("unchecked")
	private int cntPostulanteXGrupo(Long idGrupo ){
		List<EvalReferencialPostulante> evRefPostulantes=em.createQuery("Select d from EvalReferencialPostulante d " +
				" where d.selAdjudicado=true and d.postulacion.activo=true and d.activo=true" +
				" and d.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo").setParameter("idGrupo", idGrupo).getResultList();
		if(evRefPostulantes.isEmpty())
			return 0;
		
		return evRefPostulantes.size();
	}

	/**
	 * Cantidad de puestos activos: de cada grupo
	 * */
	@SuppressWarnings("unchecked")
	private int cntPuestoActivos(Long idGrupo){
		List<ConcursoPuestoDet> dets= em.createQuery("Select d from ConcursoPuestoDet d " +
				" where d.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and d.activo=true").setParameter("idGrupo", idGrupo).getResultList();
		
		if(dets.isEmpty())
			return 0;
		
		return dets.size();
	}
	
	@SuppressWarnings("unchecked")
	private void findEntidades() {
		configuracionUoCab =
			em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		if (configuracionUoCab.getEntidad() != null) {
			sinEntidad =
				em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin =
				em.createQuery("Select n from SinNivelEntidad n " + " where n.aniAniopre ="
					+ sinEntidad.getAniAniopre() + " and n.nenCodigo=" + sinEntidad.getNenCodigo()).getResultList();
			if (!sin.isEmpty())
				sinNivelEntidad = sin.get(0);

		}

	}
	

	

	@SuppressWarnings("unchecked")
	public String nextTask() {
		
		validaciones1y2();
		cargarGrupos();
		
		if (!tieneResolucion()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar al menos una resolucion");
			return null;
		}
		
		Observacion observacion = new Observacion();
		observacion.setObservacion(obs);
		observacion.setFechaAlta(new Date());
		observacion.setConcurso(em.find(Concurso.class, idConcurso));
		observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		observacion.setIdTaskInstance(jbpmUtilFormController.getIdTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance()));
		em.persist(observacion);

		for (int i = 0; i < puestoAgrList.size(); i++) {
			ConcursoPuestoAgr agr =
				em.find(ConcursoPuestoAgr.class, puestoAgrList.get(i).getIdConcursoPuestoAgr());
			/**
			 * Actualiza el estado del grupo a ‘CON RESOLUCION ADJUDICACION’.
			 *  
			 * **/
			
			agr.setEstado(conResolucionAdjudicacion());
			agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
			agr.setFechaMod(new Date());
			em.merge(agr);
			em.flush();
			
			/**
			 * Al igual que el estado de los puestos en PLANTA_CARGO_DET y 
			 * */
			List<ConcursoPuestoDet> d =
				em.createQuery("Select c from ConcursoPuestoDet c"
					+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo" +
							" and c.estadoDet=:idEstadoDet and c.excepcion.idExcepcion is null ").setParameter("idGrupo", puestoAgrList.get(i).getIdConcursoPuestoAgr())
					.setParameter("idEstadoDet",  seleccionUtilFormController.buscarEstadoDet("CONCURSO","ADJUDICADO")).getResultList();
			for (int j = 0; j < d.size(); j++) {
				ConcursoPuestoDet puestoDet =
					em.find(ConcursoPuestoDet.class, d.get(j).getIdConcursoPuestoDet());
				puestoDet.setEstadoDet(conResolucionAdjudicacionEstadoDet());
				puestoDet.setFechaMod(new Date());
				puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(puestoDet);
				em.flush();
				if (d.get(j).getPlantaCargoDet() != null) {
					PlantaCargoDet pdet =
						em.find(PlantaCargoDet.class, d.get(j).getPlantaCargoDet().getIdPlantaCargoDet());
					pdet.setEstadoDet(conResolucionAdjudicacionEstadoDet());
					pdet.setFechaMod(new Date());
					pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(pdet);
					em.flush();
				}
			}
			
			
		}
		
		
		
		

		// Se pasa a la siguiente tarea
		jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		jbpmUtilFormController.setActividadActual(ActividadEnum.CSI_ELABORAR_RESOLUCION_ADJUDICACION);
		jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CSI_FIRMAR_RESOLUCION_ADJUDICACION);

		if (jbpmUtilFormController.nextTask()) {
			em.flush();
		}

		return "next";

	}

	public void anexar() {
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		entity = "ConcursoPuestoAgr";
		direccionFisica =
			"//SICCA//" + anio + "//OEE//" + configuracionUoCab.getIdConfiguracionUo() + "//"
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
				+ idConcurso+ "//"+idConcursoPuestoAgr;

	}

	private boolean tieneResolucion() {
		for (int i = 0; i < puestoAgrList.size(); i++) {
			if (puestoAgrList.get(i).getResolucionAdjudicacion() != null)
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Integer conResolucionAdjudicacion() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'CON RESOLUCION ADJUDICACION'").getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}
	@SuppressWarnings("unchecked")
	private EstadoDet conResolucionAdjudicacionEstadoDet() {
		List<EstadoDet> eDet =
			em.createQuery(" Select e from EstadoDet e "
				+ " where e.descripcion like 'CON RESOLUCION ADJUDICACION' and e.estadoCab.descripcion like 'CONCURSO'").getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	public void verificarResolucion() {
		habReso = false;
		for (int i = 0; i < puestoAgrList.size(); i++) {
			if (puestoAgrList.get(i).getResolucionAdjudicacion() == null)
				habReso = true;

		}
	}

	@SuppressWarnings("unchecked")
	public String finIdConcursoReso(Long id, String dir) {
		idResoEdit = id;
		Resolucion m = em.find(Resolucion.class, id);
		List<ConcursoPuestoAgr> p =
			em.createQuery("select c from ConcursoPuestoAgr c "
				+ " where c.resolucionHomologacion.idResolucion=" + m.getIdResolucion()).getResultList();
		if (p.isEmpty())
			idResoConsurso = idConcurso;
		else
			idResoConsurso = p.get(0).getConcurso().getIdConcurso();

		return dir;

	}


	private void cargarListas() {
		try {
			resolucionAdjudicacionList = new ArrayList<Resolucion>();
			for (int i = 0; i < puestoAgrList.size(); i++) {
				Resolucion adjudicacion = new Resolucion();
				if (puestoAgrList.get(i).getResolucionAdjudicacion() != null) {
					adjudicacion =
						em.find(Resolucion.class, puestoAgrList.get(i).getResolucionAdjudicacion().getIdResolucion());
					if (!resolucionAdjudicacionList.contains(adjudicacion))
						resolucionAdjudicacionList.add(adjudicacion);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String resolucion() {
		paramReso = "";
		idResoEdit = null;

		
		if (puestoAgrList.isEmpty()) {
			statusMessages.add("No posee grupos para realizar la Resolucion. Verifique");
			return null;
		}
		
		validaciones1y2();
		cargarGrupos();
		
		for (ConcursoPuestoAgr f : puestoAgrList) {
			paramReso += f.getIdConcursoPuestoAgr() + ",";
			paramIdConcurso = f.getConcurso().getIdConcurso();

		}
		return "ir";

	}
	private boolean verficarPostResolucion(){
		habBotones=true;
		for (ConcursoPuestoAgr agr : puestoAgrList) {
			if(cntPostulanteXGrupo(agr.getIdConcursoPuestoAgr())!=cntPuestoActivos(agr.getIdConcursoPuestoAgr())){
				statusMessages.add(Severity.ERROR,"Cantidad de adjudicados no es igual a cantidad de puestos para el grupo"+agr.getDescripcionGrupo()+" Verificar en control Puesto/Postulante");
				habBotones=false;
			}else
				habcControles=true;
		}
		return habBotones;
	}
	
	
	/**
	 * Actualizar el estado del  Grupo: A  GRUPO DESIERTO
	 * */
	private void declararDesiertoGrupo(Long idGrupo){
		/**
		 * SE MODIFICA EL ESTADO DEL GRUPO
		 * */
		ConcursoPuestoAgr agr=em.find(ConcursoPuestoAgr.class, idGrupo);
		agr.setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "GRUPO DESIERTO"));
		agr.setUsuMod("SYSTEM");
		agr.setFechaMod(new Date());
		em.merge(agr);
		/**
		 * Actualizar el ESTADO de los PUESTOS:
		 * */
		modificarEstadoPuesto(idGrupo);
		/**
		 * Envío de mails a los integrantes de la comisión de selección:
		 * */
		enviarMailItegrantes(idGrupo);
		/**
		 * Insertar en la tabla SEL_PUBLICACIÓN_PORTAL:
		 * **/
		insertarPublicacionPortal(idGrupo);
		
		reponerCategoriasController.reponerCategorias(em.find(ConcursoPuestoAgr.class, idGrupo), "GRUPO", "CONCURSO");
	}
	@SuppressWarnings("unchecked")
	private void modificarEstadoPuesto(Long idGrupo){
		List<ConcursoPuestoDet> concPuestodets=em.createQuery("Select c from ConcursoPuestoDet c " +
				" where c.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo").setParameter("idGrupo", idGrupo).getResultList();
		for (ConcursoPuestoDet concursoPuestoDet : concPuestodets) {
			if(cumpleCondicion(concursoPuestoDet)){
				/**
				 * Actualizar el ESTADO de los PUESTOS: A VACANTE
				 * 	PLANIFICACION.PLANTA_CARGO_DET.ID_ESTADO_DET
				 * */
				PlantaCargoDet plantaCargoDet=em.find(PlantaCargoDet.class,concursoPuestoDet.getPlantaCargoDet().getIdPlantaCargoDet());
				plantaCargoDet.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("VACANTE"));
				plantaCargoDet.setFechaMod(new Date());
				plantaCargoDet.setUsuMod("SYSTEM");
				em.merge(plantaCargoDet);
				/**
				 * •	CONCURSO_PUESTO_DET.ID_ESTADO_DET : A DESIERTO’  
				 * 		columna ACTIVO=FALSE
				 * */
				
				concursoPuestoDet.setEstadoDet(seleccionUtilFormController.buscarEstadoDet("CONCURSO", "DESIERTO"));
				concursoPuestoDet.setActivo(false);
				concursoPuestoDet.setUsuMod("SYSTEM");
				concursoPuestoDet.setFechaMod(new Date());
				em.merge(concursoPuestoDet);
			}
			
			
		}
		ConcursoPuestoAgr agr=em.find(ConcursoPuestoAgr.class, idGrupo);
		agr.setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "GRUPO DESIERTO"));
		agr.setUsuMod("SYSTEM");
		agr.setFechaMod(new Date());
		em.merge(agr);
	}
	
	/**
	 * Envío de mails a los integrantes de la comisión de selección:
	 * */
	@SuppressWarnings("unchecked")
	private void enviarMailItegrantes(Long idGrupo){
		
		List<ComisionSeleccionCab> cabs=em.createQuery("Select cg.comisionSeleccionCab from ComisionGrupo cg " +
				" where cg.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and  cg.activo=true ")
				.setParameter("idGrupo",idGrupo).getResultList();
		for (ComisionSeleccionCab comisionSeleccionCab : cabs) {
			for (ComisionSeleccionDet coSeleccionDet : comisionSeleccionCab.getComisionSeleccionDets()) {
				if(coSeleccionDet.getPersona()!=null){
					Persona persona=em.find(Persona.class, coSeleccionDet.getPersona().getIdPersona());
					sentEmail(persona.getEMail(),idGrupo);
				}
				
			}
		}
	}
	/**
	 * Insertar en la tabla SEL_PUBLICACIÓN_PORTAL
	 * */
	private void insertarPublicacionPortal(Long idGrupo){
		ConcursoPuestoAgr agr=em.find(ConcursoPuestoAgr.class, idGrupo); 
		
		PublicacionPortal publicacionPortal= new PublicacionPortal();
		publicacionPortal.setActivo(true);
		publicacionPortal.setConcurso(agr.getConcurso());
		publicacionPortal.setConcursoPuestoAgr(agr);
		publicacionPortal.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		publicacionPortal.setTexto(getTexto());
		publicacionPortal.setFechaAlta(fechaSistema);
		em.persist(publicacionPortal);
	}
	
	private void sentEmail(String dirEmail,Long idGrupo) {
		ConcursoPuestoAgr  agr=em.find(ConcursoPuestoAgr.class, idGrupo);
		String texto = "";
		String template="/templates/email_CU417.vm";
		String asunto = "NOTIF_GRUPO_DESIERTO_"+agr.getConcurso().getNombre().toUpperCase()+"_"+agr.getDescripcionGrupo().toUpperCase();
		texto = seleccionUtilFormController.cargarCuerpoMail(template, contextEmail(idGrupo));
		
		seleccionUtilFormController.enviarMails(dirEmail, texto, asunto, null);

		
	}
	private VelocityContext contextEmail(Long idGrupo){
		ConcursoPuestoAgr agr=em.find(ConcursoPuestoAgr.class, idGrupo);
		Concurso c=em.find(Concurso.class,agr.getConcurso().getIdConcurso());
		VelocityContext context = new VelocityContext();
		context.put("nombreGrupo", agr.getDescripcionGrupo().toUpperCase());
		context.put("nombreConcurso", c.getNombre().toUpperCase());
		context.put("oeeDescripcion",c.getConfiguracionUoCab().getDenominacionUnidad().toUpperCase());
		return context;
		
	}
	
	/**
	 * VERIFICA QUE EL ESTADO DEL PUESTO ESTE ACTIVO Y SEA ADJUDICADO
	 * */
	private boolean cumpleCondicion(ConcursoPuestoDet det){
		if(det.isActivo() && 
				det.getEstadoDet().getIdEstadoDet().longValue()==
					seleccionUtilFormController.buscarEstadoDet("CONCURSO", "ADJUDICADO").getIdEstadoDet().longValue())
			return true;
		
		return false;
			
	}
	private String getTexto(){
		String texto = "";
		String tag1O = "<h1> <strong>";
		String tag1C = " </strong> </h1>  ";
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		texto=tag1O + "Se declara desierto el proceso por no tener adjudicados, en fecha " + sdf.format(fechaSistema)+ tag1C;
		return texto;
	}
	
	private boolean validacion1(ConcursoPuestoAgr grupo){
		
			/***1.Verificar si hay 0 Puestos activos (POR CADA GRUPO QUE ESTÁ EN ESTA ACTIVIDAD)*/
			if(seleccionUtilFormController.cantPuestosActivos(grupo.getIdConcursoPuestoAgr())==0){
				declararDesiertoGrupo(grupo.getIdConcursoPuestoAgr());
				//si el grupo esta desierto
				//Terminar la tarea del JBPM
				endTask(grupo);
				statusMessages.add(Severity.WARN,"El grupo "+grupo.getDescripcionGrupo()+" se declara desierto por no reunir la cantidad suficiente de Adjudicados");
				return false;
			}
			return true;
		
	}
	private boolean validacion2(ConcursoPuestoAgr agr){
		/**
		 * 2.Verificar si hay 0 Postulantes (porque los demás CANCELARON su postulación)
		 * */
		if(seleccionUtilFormController.cntAdjudicados(agr.getIdConcursoPuestoAgr())==0){
			/**
			 * Cantidad de Elegibles
			 * */
			if(seleccionUtilFormController.cntElegible(agr.getIdConcursoPuestoAgr())==0){//no hay eleegible
				declararDesiertoGrupo(agr.getIdConcursoPuestoAgr());
				endTask(agr);
				statusMessages.add(Severity.WARN,"El grupo "+agr.getDescripcionGrupo()+" se declara desierto por no reunir la cantidad suficiente de Adjudicados");
				return false;
			}else{//hay elegibles 
				
				/**
				 * Actualiza el estado del grupo
				 * */
				agr.setActivo(false);
				agr.setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "GRUPO CANCELADO"));
				agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				agr.setFechaMod(new Date());
				em.merge(agr);
				endTask(agr);
				statusMessages.add(Severity.WARN,"Debe gestionar elegibles desde la Bandeja de Excepciones para el Grupo "+agr.getDescripcionGrupo()+" porque este grupo no cuenta con Postulantes suficientes");
			}
			return false;
		}
		
		
		return true;
	}

	
	

	// GETTERS Y SETTERS

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public List<ConcursoPuestoAgr> getPuestoAgrList() {
		return puestoAgrList;
	}

	public void setPuestoAgrList(List<ConcursoPuestoAgr> puestoAgrList) {
		this.puestoAgrList = puestoAgrList;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public List<Resolucion> getResolucionAdjudicacionList() {
		return resolucionAdjudicacionList;
	}

	public void setResolucionAdjudicacionList(List<Resolucion> resolucionAdjudicacionList) {
		this.resolucionAdjudicacionList = resolucionAdjudicacionList;
	}

	public String getParamMemo() {
		return paramMemo;
	}

	public void setParamMemo(String paramMemo) {
		this.paramMemo = paramMemo;
	}

	public String getParamNota() {
		return paramNota;
	}

	public void setParamNota(String paramNota) {
		this.paramNota = paramNota;
	}

	public String getParamReso() {
		return paramReso;
	}

	public void setParamReso(String paramReso) {
		this.paramReso = paramReso;
	}

	public Long getParamIdConcurso() {
		return paramIdConcurso;
	}

	public void setParamIdConcurso(Long paramIdConcurso) {
		this.paramIdConcurso = paramIdConcurso;
	}

	public Long getIdResoEdit() {
		return idResoEdit;
	}

	public void setIdResoEdit(Long idResoEdit) {
		this.idResoEdit = idResoEdit;
	}

	public Long getIdResoConsurso() {
		return idResoConsurso;
	}

	public void setIdResoConsurso(Long idResoConsurso) {
		this.idResoConsurso = idResoConsurso;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
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

	public Long getIdSave() {
		return idSave;
	}

	public void setIdSave(Long idSave) {
		this.idSave = idSave;
	}

	public boolean isHabReso() {
		return habReso;
	}

	public void setHabReso(boolean habReso) {
		this.habReso = habReso;
	}
	public boolean isHabBotones() {
		return habBotones;
	}
	public void setHabBotones(boolean habBotones) {
		this.habBotones = habBotones;
	}
	public String getCodActividad() {
		return codActividad;
	}
	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}
	public boolean isHabcControles() {
		return habcControles;
	}
	public void setHabcControles(boolean habcControles) {
		this.habcControles = habcControles;
	}

}
