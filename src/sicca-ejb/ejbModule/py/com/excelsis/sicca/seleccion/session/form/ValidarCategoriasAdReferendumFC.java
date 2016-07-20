package py.com.excelsis.sicca.seleccion.session.form;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;


@Scope(ScopeType.CONVERSATION)
@Name("validarCategoriasAdReferendumFC")
public class ValidarCategoriasAdReferendumFC{
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;

	private Long idConcursoPuestoAgr;
	private String grupoNombre;
	private String concursoNombre;
	private List<ConcursoPuestoDet> puestoList= new ArrayList<ConcursoPuestoDet>();
	private boolean habDisminuir=true;
	private boolean habAsignar1=true;// habAsignar1=Asignar Objeto de Gasto 111
	private boolean habAsignar2=true;// habAsignar2=Asignar Objeto de Gasto <> 111
	private ConcursoPuestoAgr concursoPuestoAgr= new ConcursoPuestoAgr();
	private Concurso concurso= new Concurso();
	private Long idConcursoPuestoDetSelec;
	private String observacion;
	private Boolean declaroDesierto=false;
	private String urlvolver;
	
	Date fechaSistema=new Date();
	

	public void init() throws Exception {
		
		
		
		observacion=null;
		cargarCabecera();
		listarPuestos();
		verificarGrupos();
		if(declaroDesierto)
			urlvolver="/seleccion/bandejaEntrada/BandejaEntradaList.seam";
		else
			urlvolver="/seleccion/evalReferencialPostulante/ElaboracionListaCorta.seam&concursoPuestoAgrIdConcursoPuestoAgr="+idConcursoPuestoAgr;
				
		
	}
	
	

	public void cargarCabecera(){
		concursoPuestoAgr=em.find(ConcursoPuestoAgr.class,idConcursoPuestoAgr);
		grupoNombre=concursoPuestoAgr.getDescripcionGrupo();
		concurso= em.find(Concurso.class, concursoPuestoAgr.getConcurso().getIdConcurso());
		concursoNombre=concurso.getNombre();
		nivelEntidadOeeUtil.setIdConfigCab(concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		
			
	}
	
	/**
	 * Lista los puestos que tenga objeto de gasto 111 y estado = ‘PENDIENTE’.
	 * */
	@SuppressWarnings("unchecked")
	private void listarPuestos(){
		puestoList=em.createQuery("Select puesto from ConcursoPuestoDet puesto " +
				" join puesto.plantaCargoDet.puestoConceptoPagos as cp " +
				" where cp.objCodigo=111 and cp.estado=:estado and" +
				" puesto.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and puesto.activo=true")
		.setParameter("estado", seguridadUtilFormController.estadoActividades("ESTADOS_CATEGORIA", "PENDIENTE"))
		.setParameter("idGrupo", idConcursoPuestoAgr.longValue()).getResultList();
		
	}
	@SuppressWarnings("unchecked")
	private void verificarGrupos(){
		String query= " Select cp from GrupoConceptoPago cp " +
		" where cp.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and cp.estado=:estado and cp.activo=true ";
		/**
		 * PARA VERIFICAR SI SE HABILITA DISMINUIR PUESTO
		 * **/
		List<GrupoConceptoPago> grupoConceptoPagos= em.createQuery(query)
				.setParameter("idGrupo", idConcursoPuestoAgr).
				setParameter("estado", +seguridadUtilFormController.estadoActividades("ESTADOS_CATEGORIA_GRUPO", "RESERVADO")).getResultList();
		if(!grupoConceptoPagos.isEmpty())
			habDisminuir=false;
		/**
		 * PARA VERIFICAR SI SE HABILITA Asignar Objeto de Gasto 111
		 * */
		String q111=" and cp.objCodigo= 111";
		List<GrupoConceptoPago> conceptoPagos2=em.createQuery(query+q111)
		.setParameter("idGrupo", idConcursoPuestoAgr).
		setParameter("estado", +seguridadUtilFormController.estadoActividades("ESTADOS_CATEGORIA_GRUPO", "RESERVADO")).getResultList();
		if(!conceptoPagos2.isEmpty())
			habAsignar1=false;
		
		/**
		 * 	Para deshabilitar el botón ‘Asignar Objeto de Gasto <> 111’: 
		 * */
		
		String qNot111=" and cp.objCodigo != 111 order by cp.objCodigo ";
		List<GrupoConceptoPago> conceptoPagos3Pendiente=em.createQuery(query+qNot111)
		.setParameter("idGrupo", idConcursoPuestoAgr).
		setParameter("estado", +seguridadUtilFormController.estadoActividades("ESTADOS_CATEGORIA_GRUPO", "PENDIENTE")).getResultList();
		List<GrupoConceptoPago> conceptoPagos3Reservado=em.createQuery(query+qNot111)
		.setParameter("idGrupo", idConcursoPuestoAgr).
		setParameter("estado", +seguridadUtilFormController.estadoActividades("ESTADOS_CATEGORIA_GRUPO", "RESERVADO")).getResultList();
		habAsignar2=false;
		for (int i = 0; i < conceptoPagos3Pendiente.size(); i++) {
			 Integer objCodigo=conceptoPagos3Pendiente.get(i).getObjCodigo();
			 if(objCodigo!=null){
				 boolean existe=false;
				 for (GrupoConceptoPago grupoConceptoPago : conceptoPagos3Reservado) {
					 if(grupoConceptoPago.getObjCodigo()!=null &&  grupoConceptoPago.getObjCodigo().intValue()==objCodigo.intValue())
					 {
						 existe=true;
						 break;
					 }
					
				}
				 
				 /**
				  * SI AL MENOS 1 NO ENCUENTRA SE DEBE HABILITAR EL LINK
				  * */
				 if(!existe)
				 {
					habAsignar2=true;
					break;
				 }
					
			 }
			 
		}
					
		
		
	}
	
	/**
	 * BUSCA LA CATEGORIA DEL OBJETO GASTO 111 y que sea de estado PENDIENTE
	 * */
	@SuppressWarnings("unchecked")
	public String categoriaPuesto(Long idPlanta){
		String cat="";
		List<PuestoConceptoPago>  puestoConceptoPagos=em.createQuery("Select p from PuestoConceptoPago p" +
				"  where p.objCodigo=111  and p.estado=:estado").setParameter("estado", seguridadUtilFormController.estadoActividades("ESTADOS_CATEGORIA", "PENDIENTE")).getResultList();
		for (PuestoConceptoPago puestoConceptoPago : puestoConceptoPagos) {
			if(cat.equals(""))
				cat=puestoConceptoPago.getCategoria();
			else
				cat+="."+puestoConceptoPago.getCategoria();
		}
		return cat;
	}
	
	
	/**
	 * DECLARA DESIERTO AL GRUPO
	 * */
	@SuppressWarnings("unchecked")
	public void declararDesierto(){
	
			/**
			 * Actualizar el estado del  Grupo:
			 * */
			ConcursoPuestoAgr agr= em.find(ConcursoPuestoAgr.class,idConcursoPuestoAgr);
			agr.setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "GRUPO DESIERTO"));
			agr.setUsuMod("SYSTEM");
			agr.setFechaMod(new Date());
			agr.setActivo(false);
			em.merge(agr);
			
			/**
			 * Insertar en la tabla SEL_PUBLICACIÓN_PORTAL:
			 * */
			insertarPublicacionPortal();
			
			em.flush();
			
			/**
			 * Envío de mails a los Postulantes:
			 * usa el tipo 2 de enviar mail,
			 * */
				/**
				 * De la tabla postulación (leer solo registros activos) obtener los
					id_persona_postulante del grupo.
				 * */
			List<Postulacion> postulacions=em.createQuery("Select p from Postulacion p" +
					" where p.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo").setParameter("idGrupo", idConcursoPuestoAgr).getResultList();
			
			for (Postulacion postulacion : postulacions) {
				if(postulacion.getPersonaPostulante()!=null){
					PersonaPostulante perPostulante=em.find(PersonaPostulante.class, postulacion.getPersonaPostulante().getIdPersonaPostulante());
					enviarMail(perPostulante.getEMail(), "NOTIF_PUESTO_DESIERTO_", "2", perPostulante.getNombreCompleto());
				}
				
			}
			
			/**
			 * Envío de mails a los integrantes de la comisión de selección:
			 * */
			List<ComisionSeleccionCab> cabs=em.createQuery("Select cg.comisionSeleccionCab from ComisionGrupo cg " +
					" where cg.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and  cg.activo=true ")
					.setParameter("idGrupo",idConcursoPuestoAgr).getResultList();
			for (ComisionSeleccionCab comisionSeleccionCab : cabs) {
				for (ComisionSeleccionDet coSeleccionDet : comisionSeleccionCab.getComisionSeleccionDets()) {
					if(coSeleccionDet.getPersona()!=null){
						Persona persona=em.find(Persona.class, coSeleccionDet.getPersona().getIdPersona());
						enviarMail(persona.getEMail(), "NOTIF_GRUPO_DESIERTO_", "1", persona.getNombreCompleto());
					}
					
				}
			}
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("CU580_msg_desierto"));
			
		
	}
	
	public String disminuirPuesto(){
		String url = "/seleccion/bandejaEntrada/BandejaEntradaList.seam";
		try {
			if(observacion==null || observacion.trim().equals("")){
				statusMessages.add("Debe Ingresar una observaci&oacute;n verifique");
				return null;
			}
			urlvolver="/seleccion/bandejaEntrada/BandejaEntradaList.seam";
			ConcursoPuestoDet concursoPuestoDet=em.find(ConcursoPuestoDet.class, idConcursoPuestoDetSelec);
			/**
			 * Guarda la Observacion
			 * */
			concursoPuestoDet.setObservacion(observacion);
			/**
			 * Actualizar el ESTADO del PUESTO:
			 * */
			concursoPuestoDet.setEstadoDet(seleccionUtilFormController.buscarEstadoDet("CONCURSO", "DISMINUCION AD-REFERENDUM"));
			concursoPuestoDet.setActivo(false);
			concursoPuestoDet.setFechaMod(fechaSistema);
			concursoPuestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(concursoPuestoDet);
			
			PlantaCargoDet plantaCargoDet = em.find(PlantaCargoDet.class, concursoPuestoDet.getPlantaCargoDet().getIdPlantaCargoDet());
			plantaCargoDet.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("VACANTE"));
			plantaCargoDet.setEstadoDet(null);
			plantaCargoDet.setFechaMod(fechaSistema);
			plantaCargoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(plantaCargoDet);
			
			/**
			 * Incidencia 0001644
			 * llamar al CU604
			 *  (le envía como parámetros el id_concurso_puesto_det, y las cadenas ‘PUESTO’,
			 *   ‘DISMINUIDO’)
			 * */
			reponerCategoriasController.reponerCategorias(concursoPuestoDet,
					"PUESTO", "DISMINUIDO");
			
			/**
			 * fin
			 * */
			
			/**
			 * Insertar en la tabla SEL_PUBLICACIÓN_PORTAL:
			 * */
			insertarPublicacionPortal();
			em.flush();
			/**
			 * 	Por cada puesto que se disminuye verificar la 
			 * cantidad de puestos activos del listado
			 * */
			int tamPuestoActivo=puestoList.size();
			/**
			 * se resta uno que es el que se esta inactivando
			 * */
			tamPuestoActivo=tamPuestoActivo-1;
			/**
			 * SI la cantidad de puestos activos es 0, el sistema debe declarar DESIERTO el grupo
			 * */
			if(tamPuestoActivo==0){
				declararDesierto();
				setDeclaroDesierto(true);
				//statusMessages.add(Severity.INFO,"Se declara desierto el grupo por no tener puestos activos");
			}
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			if(declaroDesierto)
				return "/seleccion/bandejaEntrada/BandejaEntradaList.seam";
			
			init();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void seleccionarPuesto(Long idPuesto){
		idConcursoPuestoDetSelec=idPuesto;
	}
	
	
	
	/**
	 * Insertar en la tabla SEL_PUBLICACIÓN_PORTAL
	 * */
	private void insertarPublicacionPortal(){
		
		PublicacionPortal publicacionPortal= new PublicacionPortal();
		publicacionPortal.setActivo(true);
		publicacionPortal.setConcurso(concurso);
		publicacionPortal.setConcursoPuestoAgr(concursoPuestoAgr);
		publicacionPortal.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		publicacionPortal.setTexto(getTexto());
		publicacionPortal.setFechaAlta(fechaSistema);
		em.persist(publicacionPortal);
	}
	
	private String getTexto(){
		String texto = "";
		String tag1O = "<h1> <strong>";
		String tag1C = " </strong> </h1>  ";
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		texto=tag1O + " La cantidad de vacancias del concurso Ad-Referendum en fecha " + sdf.format(fechaSistema) +
				" es: "+puestoList.size()+ tag1C;
		return texto;
	}
	
	/**
	 * el paramentro tipo define el template 1 o 2
	 * el template 1 envia mail a los integrantes
	 * el template 2 envia mail a los postulantes
	 * */
	private void enviarMail(String dirEmail,String asunto_desierto,String tipo,String nombreApe) {
		String texto = "";
		String template1="/templates/email_CU580_1.vm";
		String template2="/templates/email_CU580_2.vm";
		
		String asunto = asunto_desierto+grupoNombre.toUpperCase()+"_"+concursoNombre.toUpperCase();
		if(tipo.equals("1"))
			texto = seleccionUtilFormController.cargarCuerpoMail(template1, context1());
		else
			if(tipo.equals("2"))
				texto = seleccionUtilFormController.cargarCuerpoMail(template2, context2(nombreApe));
		
		seleccionUtilFormController.enviarMails(dirEmail, texto, asunto, null);

		
	}
	private VelocityContext context1(){
		VelocityContext context = new VelocityContext();
		context.put("nombreGrupo", grupoNombre.toUpperCase());
		context.put("nombreConcurso", concursoNombre.toUpperCase());
		context.put("oeeDescripcion", nivelEntidadOeeUtil.getDenominacionUnidad().toUpperCase());
		return context;
		
	}
	private VelocityContext context2(String nombreApellido){
		VelocityContext context = context1();
		context.put("nombreApellido", nombreApellido);
		return context;
		
	}
	
	public String back(){
		
		if(declaroDesierto)
			urlvolver="/seleccion/bandejaEntrada/BandejaEntradaList.seam";
		else
			urlvolver="/seleccion/evalReferencialPostulante/ElaboracionListaCorta.seam&concursoPuestoAgrIdConcursoPuestoAgr="+idConcursoPuestoAgr;
			
		return urlvolver;
	}
	

	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getGrupoNombre() {
		return grupoNombre;
	}

	public void setGrupoNombre(String grupoNombre) {
		this.grupoNombre = grupoNombre;
	}

	public String getConcursoNombre() {
		return concursoNombre;
	}

	public void setConcursoNombre(String concursoNombre) {
		this.concursoNombre = concursoNombre;
	}



	public List<ConcursoPuestoDet> getPuestoList() {
		return puestoList;
	}



	public void setPuestoList(List<ConcursoPuestoDet> puestoList) {
		this.puestoList = puestoList;
	}



	public boolean isHabDisminuir() {
		return habDisminuir;
	}

	public void setHabDisminuir(boolean habDisminuir) {
		this.habDisminuir = habDisminuir;
	}

	public boolean isHabAsignar1() {
		return habAsignar1;
	}

	public void setHabAsignar1(boolean habAsignar1) {
		this.habAsignar1 = habAsignar1;
	}

	public boolean isHabAsignar2() {
		return habAsignar2;
	}

	public void setHabAsignar2(boolean habAsignar2) {
		this.habAsignar2 = habAsignar2;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	

	public Long getIdConcursoPuestoDetSelec() {
		return idConcursoPuestoDetSelec;
	}



	public void setIdConcursoPuestoDetSelec(Long idConcursoPuestoDetSelec) {
		this.idConcursoPuestoDetSelec = idConcursoPuestoDetSelec;
	}



	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}



	public Boolean getDeclaroDesierto() {
		return declaroDesierto;
	}



	public void setDeclaroDesierto(Boolean declaroDesierto) {
		this.declaroDesierto = declaroDesierto;
	}



	public String getUrlvolver() {
		return urlvolver;
	}



	public void setUrlvolver(String urlvolver) {
		this.urlvolver = urlvolver;
	}

	
	
}
