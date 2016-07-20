package py.com.excelsis.sicca.circuitoCSI.session.form;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

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
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ComisionGrupo;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;

@Scope(ScopeType.CONVERSATION)
@Name("admSorteo511")
public class AdmSorteo511 implements Serializable {

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

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	ConcursoPuestoAgrList concursoPuestoAgrList;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReportUtilFormController reportUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;


	private ConcursoPuestoAgr concursoPuestoAgr;
	private Long idConcurso;// enviado por el CU
	private Concurso concurso;
	private String obs;
	private String nombrePantalla;
	private String direccionFisica;
	private String entity;
	private Long idConcursoPuestoAgr;
	private SeguridadUtilFormController seguridadUtilFormController;
	private boolean realizoSorteo=false;
	private String msg1="";
	private String msg2="";
	private List<Postulacion> postulacionLista= new Vector<Postulacion>();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private String nombreConcurso;

	public void init() {
		if(seguridadUtilFormController==null){
			seguridadUtilFormController = (SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class,true);
		}
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);

		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = em.find(Concurso.class, idConcurso);
		cargarDatos();
		validar();
	}
	
	private void validar(){
		if (!seguridadUtilFormController.validaEstado(concursoPuestoAgr.getIdConcursoPuestoAgr(), "REALIZAR SORTEO")) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}
	public void init2() {
		if(seguridadUtilFormController==null){
			seguridadUtilFormController = (SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class,true);
		}
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);

		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = em.find(Concurso.class, idConcurso);
		validar();
	}
	
	private void cargarDatos(){
		realizoSorteo=verificarSorteo()>0?true:false;
		nombreConcurso=concurso.getNombre();
		cargarCabecera();
		cargarPostulantes();
		/**
		 * cargar mensajes
		 * */
		msg1=" El grupo tuvo "+cntPostulantesInscriptos()+" Postulantes inscriptos, para "+cntVacanciaXGrupo()+" vacancias. ";
		msg2=" Al realizar el sorteo, "+cntPostulanteASacar()+" Postulantes quedarán fuera de carrera.";
		anexar();
	}


	private void cargarCabecera(){
		nivelEntidadOeeUtil.setIdConfigCab(concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		
	}
	@SuppressWarnings("unchecked")
	private void cargarPostulantes(){
		postulacionLista=em.createQuery("Select p from Postulacion p " +
				" where p.activo=true and p.concursoPuestoAgr.idConcursoPuestoAgr=:idConcursoPuestoAgr" +
				" order by 	p.seleccionado,p.usuPostulacion").setParameter("idConcursoPuestoAgr",idConcursoPuestoAgr).getResultList();
	}
	
	
	public void iniciarSorteo(){
		/**
		 *	Ejecutar algoritmo randómico en el cual, 
		*	sobre la lista de Postulantes del grupo, 
		*	el algoritmo vaya marcando cada Postulante seleccionado
		 * */
		long cntASeleccionar=topePostulante();
		int totalPostulante=Integer.parseInt(cntPostulantesInscriptos()+"");
		Date fechaSorteo= new Date();
		 Random randomGenerator = new Random();
		 
		 for (int i = 0; i < postulacionLista.size(); i++) {
			 Postulacion postulacion= new Postulacion();
			 postulacion=postulacionLista.get(i);
			 postulacion.setSeleccionado(false);
			 postulacion.setFechaSorteo(fechaSorteo);
			 postulacion.setUsuModif(usuarioLogueado.getCodigoUsuario());
			 postulacion.setFechaModif(new Date());
			 postulacion.setFechaAltaSorteo(new Date());
			 postulacion.setUsuAltaSorteo(usuarioLogueado.getCodigoUsuario());
			 em.merge(postulacion);
		}
		 
		 List<Integer> seleccionados= new ArrayList<Integer>();
		for (int i = 0; i < cntASeleccionar; i++) {
			 int randomInt = randomGenerator.nextInt(totalPostulante);
			 /**
			  * Por cada registro seleccionado actualiza los dos campos :
			  * seleccionado = true y fecha_sorteo con la fecha y hora del sistema.
			  * */
			 while (seleccionados.contains(new Integer(randomInt))) {
				 randomInt = randomGenerator.nextInt(totalPostulante);
			}
			seleccionados.add(randomInt);
			 
			 
			 Postulacion postulacion= new Postulacion();
			 postulacion=postulacionLista.get(randomInt);
			 postulacion.setSeleccionado(true);
			 postulacion.setFechaSorteo(fechaSorteo);
			 postulacion.setUsuModif(usuarioLogueado.getCodigoUsuario());
			 postulacion.setFechaModif(new Date());
			 postulacion.setFechaAltaSorteo(new Date());
			 postulacion.setUsuAltaSorteo(usuarioLogueado.getCodigoUsuario());
			 postulacion.setFechaAltaSorteo(new Date());
			 postulacion.setUsuAltaSorteo(usuarioLogueado.getCodigoUsuario());
			 em.merge(postulacion);
			 
			 
		}
		
		/**
		 * actualizar la publicación
		 * */
		actualizarPublicacion(fechaSorteo);
		
		
		em.flush();
		/**
		 * actualizar grilla de ‘Postulantes Seleccionados y no Seleccionados
		 * */
		cargarDatos();
		
		/**
		 * 	SI la cantidad de seleccionados es igual al tope
		 * */
		
		/**
		 * Enviar mails a los Postulantes y a los Integrantes de la Comisión de Selección
		 * */
		enviarMailPostcomi(fechaSorteo);
		enviarMailPostulantes(fechaSorteo);
		
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
	
		
	}
	
	private void  actualizarPublicacion(Date fecSorteo){
		//Insertar en la tabla SEL_PUBLICACION_PORTAL:
		PublicacionPortal publicacionPortal= new PublicacionPortal();
		publicacionPortal.setConcurso(concurso);
		publicacionPortal.setConcursoPuestoAgr(concursoPuestoAgr);
		publicacionPortal.setActivo(true);
		publicacionPortal.setFechaAlta(new Date());
		publicacionPortal.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		publicacionPortal.setTexto(getTexto(fecSorteo));
		em.persist(publicacionPortal);
	}
	
	private String getTexto(Date fec){
		String hr = "<hr>";
		String fechaPublicacion = sdf.format(new Date()).toString();
		String texto = "";
		String tag1O = "<h1> <strong>";
		String tag1C = " </strong> </h1>  ";
		String spanO = "<span>";
		String br = "</br>";
		String spanC = "</span>";
	
		texto= hr + fechaPublicacion + tag1O + " Debido a que la cantidad de Postulantes inscriptos excede el Tope de Postulantes, " +
				"se procedió a realizar un sorteo en fecha: " + sdf.format(fec)
				+ tag1C+ br+
		spanO
		+ "Descargar aquí la  "
		+ spanC
		+ br
		+ spanO
		+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=CU_511&#38;idConcursoPuestoAgr="
		+ concursoPuestoAgr.getIdConcursoPuestoAgr()
		+ "'>Lista de Seleccionados del Sorteo</a>"+spanC + br;
		return texto;
	}
	private void enviarMailPostulantes(Date fec){
		String asunto="SICCA – LISTA SORTEO";
	
		for (Postulacion  postulacion : postulacionLista) {
			String email="";
			String nombreCompleto="";
			if(postulacion.getPersona()==null){//por CARPETA 
				email=postulacion.getPersonaPostulante().getEMail();
				nombreCompleto=postulacion.getPersonaPostulante().getNombreCompleto();
			}else{//PORTAL
				email=postulacion.getPersona().getEMail();
				nombreCompleto=postulacion.getPersona().getNombreCompleto();
			}
			if(postulacion.getSeleccionado()){
				enviarMail(email,asunto ,"2",nombreCompleto,fec );
			}else{
				enviarMail(email,asunto ,"3",nombreCompleto,fec );
			}
		}
	}
	
	
	/**
	 * enviar mails a los Postulantes y a los Integrantes de la Comisión de Selección
	 * */
	@SuppressWarnings("unchecked")
	private void enviarMailPostcomi(Date fechaSorteo){
		String asunto="SICCA – LISTA SORTEO";
		
		//1.	Obtener Integrantes de Comisión de Selección
		List<ComisionGrupo> comisionGrupos=em.createQuery("Select d from ComisionGrupo d " +
				" where d.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and  d.activo=true ").setParameter("idGrupo", idConcursoPuestoAgr).getResultList();
		for (ComisionGrupo comisionGrupo : comisionGrupos) {
			List<ComisionSeleccionDet> comisionSeleccionDets=em.createQuery("Select d from ComisionSeleccionDet d " +
					" where d.comisionSeleccionCab.idComisionSel=:idComisionSel and d.activo=true ")
					.setParameter("idComisionSel", comisionGrupo.getComisionSeleccionCab().getIdComisionSel()).getResultList();
			//De cada registro leer el id_persona, enviara el e_mail
			for (ComisionSeleccionDet comiSeleccionDet : comisionSeleccionDets) {
				Persona persona=comiSeleccionDet.getPersona();
				enviarMail(persona.getEMail(), asunto,"1", persona.getNombreCompleto(), fechaSorteo);
			}
		}
		
	}
	/**
	 * PARA TODOS LOS DE COMISION/POSTULANTE
	 * */
	private VelocityContext context1(Date fecSorteo,String nombre){
		VelocityContext context = new VelocityContext();
		context.put("titulo", concurso.getNumero()+"/"+concurso.getAnhio()+" de "+concurso.getConfiguracionUoCab().getDescripcionCorta()+concurso.getNombre());
		context.put("nombreGrupo", concursoPuestoAgr.getDescripcionGrupo().toUpperCase());
		context.put("fechaSorteo",sdf.format(fecSorteo ));
		context.put("oeeDescripcion",nivelEntidadOeeUtil.getDenominacionUnidad().toUpperCase());
		context.put("nombreApellido", nombre);
		return context;
		
	}
	/**
	 * SELECCIONADO Y NO SELECCIONADO
	 * */
	private VelocityContext context2(String nombres){
		VelocityContext context = new VelocityContext();
		context.put("titulo", concurso.getNumero()+"/"+concurso.getAnhio()+" de "+concurso.getConfiguracionUoCab().getDescripcionCorta()+concurso.getNombre());
		context.put("nombreGrupo", concursoPuestoAgr.getDescripcionGrupo().toUpperCase());
		context.put("nombreApellido",nombres );
		context.put("oeeDescripcion",nivelEntidadOeeUtil.getDenominacionUnidad().toUpperCase());
		return context;
		
	}
	/**
	 * tipo=1;comisionPostulante
	 * tipo=2;seleccionados
	 * tipo=3;no seleccionados
	 * */
	private void enviarMail(String dirEmail,String asunto,String tipo,String nombreApe,Date fecSorteo) {
		String texto = "";
		String template1="/templates/email_CU511_comision_post.vm";
		String template2="/templates/email_CU511_seleccionado.vm";
		String template3="/templates/email_CU511_no_seleccionado.vm";
		
		if(tipo.equals("1"))
			texto = seleccionUtilFormController.cargarCuerpoMail(template1, context1(fecSorteo, nombreApe));
		else if(tipo.equals("2"))
				texto = seleccionUtilFormController.cargarCuerpoMail(template2, context2(nombreApe));
		else
			texto = seleccionUtilFormController.cargarCuerpoMail(template3, context2(nombreApe));
		
		seleccionUtilFormController.enviarMails(dirEmail, texto, asunto, null);

		
	}


	@SuppressWarnings("unchecked")
	public String nextTask() {

		statusMessages.clear();
		
		Observacion observacion = new Observacion();
		observacion.setObservacion(obs);
		observacion.setFechaAlta(new Date());
		observacion.setConcurso(em.find(Concurso.class, idConcurso));
		observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		observacion.setIdTaskInstance(jbpmUtilFormController.getIdTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance()));
		em.persist(observacion);
		jbpmUtilFormController.setActividadActual(ActividadEnum.CSI_REALIZAR_SORTEO);
		
	
		
		/**
		 * Hay que determinar si 
		 * el Grupo irá a la actividad ‘Completar Carpetas’ o a la actividad ‘Realizar Evaluaciones’
		 * */
		long cantidadPostulante=cntPostulanteXCarpeta();
		/**
		 * SI la cantidad obtenida es 0 
		 * */
		if(cantidadPostulante==0){///*no hay postulaciones por CARPETA */
			//Enviar a la Actividad 15 ‘Realizar Evaluaciones’
			//ACTUALIZA A LISTA LARGA
			/**
			 * 	Actualizar el estado del Grupo: 
			 * */
			concursoPuestoAgr.setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "LISTA LARGA"));
			/**
			 * Actualizar el estado del Puesto en Selección:
			 * */
			List<ConcursoPuestoDet> detsPuesto=
				em.createQuery("Select c from ConcursoPuestoDet c"
					+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo ")
					.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr()).getResultList();
			for (int i = 0; i < detsPuesto.size(); i++) {
				ConcursoPuestoDet puestoDet =
					em.find(ConcursoPuestoDet.class, detsPuesto.get(i).getIdConcursoPuestoDet());	
				PlantaCargoDet pdet=null;
				if (puestoDet.getPlantaCargoDet() != null) 
					pdet =em.find(PlantaCargoDet.class, puestoDet.getPlantaCargoDet().getIdPlantaCargoDet());
				
				puestoDet.setEstadoDet(new EstadoDet());
				puestoDet.getEstadoDet().setIdEstadoDet(conListaLarga());
				puestoDet.setFechaMod(new Date());
				puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(puestoDet);
				if(pdet!=null){
					pdet.setEstadoDet(new EstadoDet());
					pdet.getEstadoDet().setIdEstadoDet(conListaLarga());
					pdet.setFechaMod(new Date());
					pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(pdet);
				}
			}
			//           Enviar a la Actividad 15 ‘Realizar Evaluaciones’
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CSI_REALIZAR_EVALUACIONES);
			jbpmUtilFormController.setTransition("sor_TO_reaEva");
		}else{
			
			for (Postulacion postulacion : postulacionLista) {
				if(postulacion.getPersonaPostulante()!=null){
					Long idPersonaPostulante=postulacion.getPersonaPostulante().getIdPersonaPostulante();
					/**
					 * Estudios_realizados**/
					List<EstudiosRealizados> estudiosRealizados=em.createQuery("Select r from EstudiosRealizados r " +
							" where r.personaPostulante.idPersonaPostulante=:idPersonaPostulante and r.activo=true").setParameter("idPersonaPostulante", idPersonaPostulante).getResultList();
					List<ExperienciaLaboral> experienciaLaborals=em.createQuery("Select el from ExperienciaLaboral el " +
							" where el.personaPostulante.idPersonaPostulante=:idPersonaPostulante").setParameter("idPersonaPostulante",idPersonaPostulante).getResultList();
					List<PersonaDiscapacidad> personaDiscapacidads=em.createQuery("Select pd from PersonaDiscapacidad pd " +
							" where pd.activo=true and pd.personaPostulante.idPersonaPostulante=:idPersonaPostulante").setParameter("idPersonaPostulante",idPersonaPostulante).getResultList();
					boolean registrosCargados=false;//si tienen todos los registros cargados en las 3 tablas de acuerdo al tipo concurso
					if(concurso.getPcd())
					{
						if(!estudiosRealizados.isEmpty()&& !experienciaLaborals.isEmpty() && !personaDiscapacidads.isEmpty()){
							registrosCargados=true;
						}
					}else{
						if(!estudiosRealizados.isEmpty()&& !experienciaLaborals.isEmpty() ){
							registrosCargados=true;
						}
					}
					/**
					 * SI existe algún id_persona_postulante que 
					 * no se encuentre en alguna de estas tablas (ver si el concurso es o no pcd)
					 * si registroCargado=false
					 * */
					if(!registrosCargados){
						//ACTUALIZA A :A COMPLETAR CARPETAS
						/**
						 * 	Actualizar el estado del Grupo: 
						 * */
						concursoPuestoAgr.setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "A COMPLETAR CARPETAS"));
						/**
						 * Actualizar el estado del Puesto en Selección:
						 * */
						List<ConcursoPuestoDet> detsPuesto=
							em.createQuery("Select c from ConcursoPuestoDet c"
								+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo "
								+"").setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr()).getResultList();
						for (int i = 0; i < detsPuesto.size(); i++) {
							ConcursoPuestoDet puestoDet =
								em.find(ConcursoPuestoDet.class, detsPuesto.get(i).getIdConcursoPuestoDet());	
							PlantaCargoDet pdet=null;
							if (puestoDet.getPlantaCargoDet() != null) 
								pdet =em.find(PlantaCargoDet.class, puestoDet.getPlantaCargoDet().getIdPlantaCargoDet());
							
							puestoDet.setEstadoDet(seleccionUtilFormController.buscarEstadoDet("CONCURSO", "A COMPLETAR CARPETAS"));
							puestoDet.setFechaMod(new Date());
							puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
							em.merge(puestoDet);
							if(pdet!=null){
								pdet.setEstadoDet(seleccionUtilFormController.buscarEstadoDet("CONCURSO", "A COMPLETAR CARPETAS"));
								pdet.setFechaMod(new Date());
								pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
								em.merge(pdet);
							}
							
								
							
						}
							//	Enviar a la Actividad 14 ‘Completar Carpetas’
						jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CSI_COMPLETAR_CARPETAS);
						jbpmUtilFormController.setTransition("sor_TO_comCar");
					}else{
						//ACTUALIZA A LISTA LARGA
						/**
						 * 	Actualizar el estado del Grupo: 
						 * */
						concursoPuestoAgr.setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "LISTA LARGA"));
						/**
						 * Actualizar el estado del Puesto en Selección:
						 * */
						List<ConcursoPuestoDet> detsPuesto=
							em.createQuery("Select c from ConcursoPuestoDet c"
								+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo ")
								.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr()).getResultList();
						for (int i = 0; i < detsPuesto.size(); i++) {
							ConcursoPuestoDet puestoDet =
								em.find(ConcursoPuestoDet.class, detsPuesto.get(i).getIdConcursoPuestoDet());	
							PlantaCargoDet pdet=null;
							if (puestoDet.getPlantaCargoDet() != null) 
								pdet =em.find(PlantaCargoDet.class, puestoDet.getPlantaCargoDet().getIdPlantaCargoDet());
							
							puestoDet.setEstadoDet(new EstadoDet());
							puestoDet.getEstadoDet().setIdEstadoDet(conListaLarga());
							puestoDet.setFechaMod(new Date());
							puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
							em.merge(puestoDet);
							if(pdet!=null){
								pdet.setEstadoDet(new EstadoDet());
								pdet.getEstadoDet().setIdEstadoDet(conListaLarga());
								pdet.setFechaMod(new Date());
								pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
								em.merge(pdet);
							}
							
								
							
						}
					//Enviar a la Actividad 15 ‘Realizar Evaluaciones’
					jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CSI_REALIZAR_EVALUACIONES);
					}
						
						
				}
			}
		}

		
		
			
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
			if (jbpmUtilFormController.nextTask()) {
				em.flush();
		
			}

		
	
		return "next";

	}
	public void imprimir() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		
		reportUtilFormController.setNombreReporte("RPT_CU509");
		reportUtilFormController.getParametros().put("oeeUsuario", usuarioLogueado.getConfiguracionUoCab().getDenominacionUnidad().toUpperCase());
		reportUtilFormController.getParametros().put("idGrupo", idConcursoPuestoAgr);
		Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
		reportUtilFormController.getParametros().put("nivel", diccDscNEO.get("NIVEL"));
		reportUtilFormController.getParametros().put("entidad", diccDscNEO.get("ENTIDAD"));
		reportUtilFormController.getParametros().put("oee", diccDscNEO.get("OEE"));
		reportUtilFormController.getParametros().put("titulo",concurso.getNumero()+"-"+concurso.getNombre().toUpperCase());
		reportUtilFormController.getParametros().put("grupoPuesto",concursoPuestoAgr.getDescripcionGrupo() );
		reportUtilFormController.imprimirReportePdf();

	}
	
	@SuppressWarnings("unchecked")
	private Long conListaLarga() {
		List<EstadoDet> eDet =
			em.createQuery(" Select e from EstadoDet e "
				+ " where e.descripcion like 'LISTA LARGA' and e.estadoCab.descripcion like 'CONCURSO'").getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0).getIdEstadoDet();
	}
	
	
	
	/**
	 * @return cantidad de personas sorteadas
	 * */
	@SuppressWarnings("unchecked")
	private int verificarSorteo(){
		List<Postulacion> postulacions=em.createQuery("Select c from Postulacion c " +
				" where c.concursoPuestoAgr.idConcursoPuestoAgr=:idConcursoPuestoAgr and c.seleccionado is true").setParameter("idConcursoPuestoAgr",idConcursoPuestoAgr).getResultList();
		return postulacions.size();
	}
	/**
	 * @return cantidad de postulantes inscriptos
	 * **/
	private long cntPostulantesInscriptos(){
		try {
			Long cnt= new Long(0);
			BigInteger c =(BigInteger) em.createNativeQuery(" SELECT COUNT(*) AS cantidad_postulantes_inscriptos " +
					" FROM seleccion.postulacion WHERE activo= true  and   id_concurso_puesto_agr =:idConcursoPuestoAgr ").setParameter("idConcursoPuestoAgr",idConcursoPuestoAgr).getSingleResult();
			if(c==null)
				return 0;
			
			cnt=c.longValue();
			
			return cnt;
		} catch (NoResultException e) {
			return 0;
		}
		
	}
	/**
	 * @return cantidad Vacancia por grupo
	 * */
	private long cntVacanciaXGrupo(){
		try {
			Long cnt= new Long(0);
			BigInteger c=(BigInteger) em.createNativeQuery(" SELECT COUNT(*) AS vacancias_grupo " +
					" FROM seleccion.concurso_puesto_det WHERE activo= true  and   id_concurso_puesto_agr =:idConcursoPuestoAgr ").setParameter("idConcursoPuestoAgr",idConcursoPuestoAgr).getSingleResult();
			if(c==null)
				return 0;
			
			cnt=c.longValue();
			return cnt;
		} catch (NoResultException e) {
			return 0;
		}
		
	}
	/**
	 * @return tope de postulantes
	 * */
	private long topePostulante(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String fechaActual=sdf.format(new Date());
		try {
			Integer cnt=(Integer) em.createNativeQuery(" SELECT DISTINCT det.cant_postulantes AS tope " +
					" FROM seleccion.regla_cab  cab " +
					" JOIN    seleccion.regla_det  det ON   cab.id_regla_cab  =  det.id_regla_cab " +
					" WHERE cab.activo =  true AND det.activo=  true " +
					" AND        to_date('" + fechaActual
					+ "','DD-MM-YYYY') >= date(cab.fecha_vigencia_desde) "+
					" AND      to_date('" + fechaActual
							+ "','DD-MM-YYYY') <=  date(cab.fecha_vigencia_hasta) AND    det.cant_vacancias=  :vacancias_grupo  ").setParameter("vacancias_grupo",new BigInteger(cntVacanciaXGrupo()+"")).getSingleResult();
			if(cnt==null)
				return 0;
			
			return cnt.longValue();
		} catch (NoResultException e) {
			return 0;
		}
		
	}

	private long cntPostulanteASacar(){
		return cntPostulantesInscriptos()-topePostulante();
	}
	private long cntPostulanteXCarpeta(){
		try {
			BigInteger cnt=(BigInteger) em.createNativeQuery(" SELECT COUNT(*) AS cantidad_postulantes_inscriptos " +
					" FROM seleccion.postulacion WHERE activo= true and tipo='CARPETA' and   " +
					" id_concurso_puesto_agr =:idConcursoPuestoAgr and usu_cancelacion is null and fecha_cancelacion is null ").setParameter("idConcursoPuestoAgr",idConcursoPuestoAgr).getSingleResult();
			if(cnt==null)
				return 0;
			
			return cnt.longValue();
		} catch (NoResultException e) {
			return 0;
		}
	}

	public void anexar() {
		nombrePantalla="realizarSorteo";
		entity="Postulacion";
		direccionFisica =
			"//SICCA//" + General.anhoActual() + "//OEE//" + nivelEntidadOeeUtil.getIdConfigCab() + "//"
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
				+ idConcurso+"//"+idConcursoPuestoAgr;
		
	}

	
	
	
	

	// GETTERS Y SETTERS

	

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
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

	public ConcursoPuestoAgrList getConcursoPuestoAgrList() {
		return concursoPuestoAgrList;
	}

	public void setConcursoPuestoAgrList(ConcursoPuestoAgrList concursoPuestoAgrList) {
		this.concursoPuestoAgrList = concursoPuestoAgrList;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

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

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public boolean isRealizoSorteo() {
		return realizoSorteo;
	}

	public void setRealizoSorteo(boolean realizoSorteo) {
		this.realizoSorteo = realizoSorteo;
	}

	public String getMsg1() {
		return msg1;
	}

	public void setMsg1(String msg1) {
		this.msg1 = msg1;
	}

	public String getMsg2() {
		return msg2;
	}

	public void setMsg2(String msg2) {
		this.msg2 = msg2;
	}

	public List<Postulacion> getPostulacionLista() {
		return postulacionLista;
	}

	public void setPostulacionLista(List<Postulacion> postulacionLista) {
		this.postulacionLista = postulacionLista;
	}
	public String getNombreConcurso() {
		return nombreConcurso;
	}
	public void setNombreConcurso(String nombreConcurso) {
		this.nombreConcurso = nombreConcurso;
	}



	

	

}
