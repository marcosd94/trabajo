package py.com.excelsis.sicca.capacitacion.session.form;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.print.Doc;
import javax.transaction.SystemException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.persistence.ManagedPersistenceContext;
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;
import enums.TiposEstadosMsjPersona;

import py.com.excelsis.sicca.entity.CapacitacionFinanciacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ComisionCapacEval;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Instructores;
import py.com.excelsis.sicca.entity.ListaCab;
import py.com.excelsis.sicca.entity.ListaDet;
import py.com.excelsis.sicca.entity.ObsCapEval;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.PostulacionCapAdj;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("registrarParticipantesFC")
public class RegistrarParticipantesFC {
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
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	private Capacitaciones capacitaciones= new Capacitaciones();
	private Long idCapacitacion;
	private Long idPaisExp= null;
	private Persona persona= new Persona();
	private Long idPersona=null;
	private String docIdentidad= null;
	private boolean esEdit;
	private String tipoCapac=null;
	private Long idTipoDocumento=null;
	private int indexUP=-1;
	private List<ListaDet> listaDets= new ArrayList<ListaDet>();
	private ListaCab listaCab= new ListaCab();
	
	private String nombre;
	private String apellido;
	private String telefono;
	private String email;
	private Boolean habBtnAddPersons = false;
	private PersonaDTO personaDTO;
	private String from;
	
	/**
	 * DOCUMENTO ADJUNTO
	 **/
	private String nombreDoc=null;
	private String nombrePantalla;
	private Boolean primeraEntrada = true;
	private byte[] uploadedFile=null;
	private String contentType=null;
	private String fileName=null;
	


	



	public void init() {
		try {

			SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
			if (idCapacitacion != null) {
				if (!sufc.validarInput(idCapacitacion.toString(),TiposDatos.LONG.getValor(), null))
					 return;
				capacitaciones = em.find(Capacitaciones.class, idCapacitacion);
				cargarCabecera();
				if (!seguridadUtilFormController.verificarPerteneceUoDet(nivelEntidadOeeUtil.getIdUnidadOrganizativa())) {
					throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
				}
				
				
				if (capacitaciones.getDatosEspecificosTipoCap() != null)
					tipoCapac = capacitaciones.getDatosEspecificosTipoCap()
							.getDescripcion();
				cargarPersonaInit();
				if (primeraEntrada) {
					em.clear();
					idPaisExp = idParaguay();
					primeravez();
					idPersona = null;
					persona = new Persona();
					limpiar();
					primeraEntrada = false;
				}
				findListaDets();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	private void cargarPersonaInit(){
		try {
			SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
			.getInstance(SeguridadUtilFormController.class, true);
			if (idPersona != null) {
				if (!seguridadUtilFormController.validarInput(
						idPersona.toString(), TiposDatos.LONG.getValor(), null)) {
					return;
				}
				persona = em.find(Persona.class, idPersona);
				completarDatosPersona(persona, true);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * o	Si ingresa por primera vez a la pantalla de Gestión de Participantes, registrar en las tablas:listaCab 
	 * en caso que no exista se registra, sino se recupera
	 * */
	private void primeravez(){
		try {
			listaCab=(ListaCab)em.createQuery("Select listaCab from ListaCab listaCab " +
					" where listaCab.capacitaciones.idCapacitacion=:idCapacitacion " +
					" and listaCab.activo=true").setParameter("idCapacitacion", idCapacitacion).getSingleResult();
		} catch (NoResultException e) {
			listaCab= new ListaCab();
			listaCab.setActivo(true);
			listaCab.setCapacitaciones(em.find(Capacitaciones.class, idCapacitacion));
			listaCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			listaCab.setFechaAlta(new Date());
			em.persist(listaCab);
			em.flush();
			
		}
	}
	
	
	
	
	
	
	
	

	public void cargarCabecera(){
		
		nivelEntidadOeeUtil.setIdConfigCab(capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo());
		if(capacitaciones.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
	}
	
	public void limpiarDatosPersona() {
		nombre = null;
		apellido=null;
		telefono = null;
		email = null;
		docIdentidad = null;
	}
	public String linkAddPersona() {
		habBtnAddPersons = false;
		idPersona = null;
		return "/seleccion/persona/PersonaEdit.seam?personaFrom=/capacitacion/registrarParticipantes/RegistrarParticipantes";

	}
	public void buscarPersona() throws Exception {
	
		/* Validaciones */
		if (idPaisExp == null
			|| !seguridadUtilFormController.validarInput(idPaisExp.toString(), TiposDatos.LONG.getValor(), null)) {
			return;
		}
		if (docIdentidad == null || docIdentidad.trim().equals("")
				|| !seguridadUtilFormController.validarInput(docIdentidad.toString(), TiposDatos.STRING.getValor(), null)) {
				return;
			}
		Pais pais = em.find(Pais.class, idPaisExp);
		if (pais == null)
			return;
		/* fin validaciones */
		personaDTO = seleccionUtilFormController.buscarPersona(docIdentidad, pais.getDescripcion());
		if (personaDTO.getHabilitarBtn() == null) {
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			habBtnAddPersons = false;
			limpiarDatosPersona2();
			persona = null;
		} else if (personaDTO.getHabilitarBtn()) {
			habBtnAddPersons = true;
			persona = null;
			limpiarDatosPersona2();
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {
			habBtnAddPersons = false;
			persona = personaDTO.getPersona();
			completarDatosPersona(personaDTO.getPersona(), false);
		}
	}
	public void limpiarDatosPersona2() {
		nombre = null;
		apellido=null;
		telefono = null;
		email = null;
	}
	private void completarDatosPersona(Persona persona, Boolean completo) {
		nombre = persona.getNombres();
		apellido= persona.getApellidos();
		telefono = persona.getTelefonos() == null ? "" : persona.getTelefonos();
		email = persona.getEMail() == null ? "" : persona.getEMail();
		if (completo) {
			idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad = persona.getDocumentoIdentidad();
		}
	}
	

	
	 
		
	

	

	
	
	public void deleteRow(int index){
		try {
			em.clear();
			ListaDet listaDetDelete=em.find(ListaDet.class, listaDets.get(index).getIdListaDet());
			PostulacionCap posCapDelete=em.find(PostulacionCap.class, listaDetDelete.getPostulacionCap().getIdPostulacion());
			PostulacionCapAdj adjDelete=findCapAdj(posCapDelete.getIdPostulacion());
			em.remove(listaDetDelete);
			
			if(adjDelete.getIdPostulacionAdj()!=null){
				
				if(adjDelete.getDocumento()!=null){
					Documentos d= em.find(Documentos.class, adjDelete.getDocumento().getIdDocumento());
					d.setActivo(false);
					d.setFechaMod(new Date());
					d.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(d);
				}
				em.remove(adjDelete);
			}
				
			em.remove(posCapDelete);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			findListaDets();
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"A ocurrido un error: "+e.getMessage());
			return ;
		}
	}
	public void abrirDocumento(Long idPostulacion) {

		try {
			PostulacionCapAdj adj=findCapAdj(idPostulacion);
			if(adj.getIdPostulacionAdj()!=null){
				if(adj.getDocumento()!=null)
					AdmDocAdjuntoFormController.abrirDocumentoFromCU( adj.getDocumento().getIdDocumento(), usuarioLogueado.getIdUsuario());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	public String save(){
		try {
			if(!checkValue())
				return null;
			
			/**
			 * a.	Si el método retornó INSERTAR_SUG = true 
			 * b.	Actualiza los datos en persona (teléfono, e-mail). 
			 * **/
			if (persona == null) {
				statusMessages.add(Severity.ERROR, "No se puede determinar la persona");
				return null;
			}
			if(exite(null, persona.getIdPersona()))
			{
				statusMessages.add(Severity.ERROR,"La persona seleccionada ya existe verifique");
				return null;
			}
			if(idTipoDocumento!=null && uploadedFile==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar un documento. Verifique");
				return null;
			}
			ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
			em = persistenceContext.getEntityManager();
		
			// a.	Actualiza los datos en persona (teléfono, e-mail). 
			persona.setEMail(email);
			persona.setTelefonos(telefono);
			persona.setFechaMod(new Date());
			persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
			persona = em.merge(persona);
			
			
			/**
			 * c.	Registra a la persona como postulante:
			 * **/
			PostulacionCap postulacionCap = new PostulacionCap();
			postulacionCap.setPersona(em.find(Persona.class, persona.getIdPersona()));
			postulacionCap.setFicha(false);
			postulacionCap.setCapacitacion(em.find(Capacitaciones.class, idCapacitacion));
			postulacionCap.setActivo(true);
			postulacionCap.setUsuFicha("SYSTEM");
			postulacionCap.setFechaFicha(new Date());
			postulacionCap.setTelefono("ID");
			postulacionCap.setEmpleadoPuesto(findEmpleadoPuesto());
			em.persist(postulacionCap);
			/**
			 * PARA EL CASO DE POSEER ADJUNTO
			 * paso d,e del ETC
			 * **/
			if(uploadedFile!=null){
				/**
				 * d.	Llama al CU 289 para registrar el adjunto:
				 * */
				Long idDoc=guardarAdjuntos(fileName, uploadedFile.length, contentType, uploadedFile, idTipoDocumento, null, persona);
				if (idDoc != null) {
					/**
					 * e.	El adjunto retorna un ID_DOCUMENTO, este deberá guardarse  en la tabla •	POSTULACION_CAP_ADJ
					 * */
					PostulacionCapAdj capAdj= new PostulacionCapAdj();
					capAdj.setDocumento(new Documentos());
					capAdj.getDocumento().setIdDocumento(idDoc);
					capAdj.setPostulacionCap(new PostulacionCap());
					capAdj.getPostulacionCap().setIdPostulacion(postulacionCap.getIdPostulacion());
					capAdj.setActivo(true);
					capAdj.setUsuAlta("SYSTEM");
					capAdj.setFechaAlta(new Date());
					em.persist(capAdj);
				}else
					return null;
			}
			/**
			 * f.	Registra a la persona como participante:
			 * **/
			
			ListaDet listaDet= new ListaDet();
			listaDet.setListaCab(listaCab);
			listaDet.setPostulacionCap(new PostulacionCap());
			listaDet.getPostulacionCap().setIdPostulacion(postulacionCap.getIdPostulacion());
			listaDet.setTipo("P");
			listaDet.setActivo(true);
			listaDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			listaDet.setFechaAlta(new Date());
			em.persist(listaDet);
			
			
			/**
			 * f.	Si el estado de la capacitación se encuentra en “CARGA”, 
			 * actualizar a “CARGAR PARTICIPANTES” y el campo ESTADO_PUBLIC = ‘VERIFICADO’.
			 * **/
			if(capacitaciones.getEstado().intValue()==estadoCarga()){
				capacitaciones.setEstado(estadoCargarParticipantes());
				capacitaciones.setEstadoPublic("VERIFICADO");
				capacitaciones.setUsuMod(usuarioLogueado.getCodigoUsuario());
				capacitaciones.setFechaMod(new Date());
				em.merge(capacitaciones);
			}
			
			em.flush();
			limpiar();
			findListaDets();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
				
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"Ha ocurrido un error: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private int estadoCarga(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'CARGA'").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	@SuppressWarnings("unchecked")
	private int estadoCargarParticipantes(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'CARGAR PARTICIPANTES'").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	
	
	public String update(){
		try {
			if(!checkValue())
				return null;
			
			ListaDet listaDetEdit=em.find(ListaDet.class, listaDets.get(indexUP).getIdListaDet());
			PostulacionCap capEdit=em.find(PostulacionCap.class, listaDetEdit.getPostulacionCap().getIdPostulacion());
			
			PostulacionCapAdj adj=findCapAdj(capEdit.getIdPostulacion());
			if(exite(capEdit.getIdPostulacion(), persona.getIdPersona()))
			{
				statusMessages.add(Severity.ERROR,"La persona seleccionada ya existe verifique");
				return null;
			}
			if(idTipoDocumento!=null && (uploadedFile==null && adj.getIdPostulacionAdj()!=null && adj.getDocumento()==null)){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar un documento. Verifique");
				return null;
			}
			ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
			em = persistenceContext.getEntityManager();
			/**
			 * i.	Si modificó el Teléfono / E-mail, actualiza los datos en la tabla PERSONAS
			 * */
			persona.setTelefonos(telefono);
			persona.setEMail(email);
			persona.setFechaMod(new Date());
			persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(persona);
			
			capEdit.setPersona(em.find(Persona.class, persona.getIdPersona()));
			capEdit.setFechaMod(new Date());
			capEdit.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(capEdit);
			/**
			 * ii.	Si modificó el adjunto: 
			 * */
			if(uploadedFile!=null){
				/**
				 * d.	Llama al CU 289 para registrar el adjunto:
				 * */
				Long idDocAnterior=null;
				
				if(adj.getDocumento()!=null)
					idDocAnterior=adj.getDocumento().getIdDocumento();
				Long idDoc=guardarAdjuntos(fileName, uploadedFile.length, contentType, uploadedFile, idTipoDocumento, idDocAnterior, persona);
				if (idDoc != null) {
					if(adj.getIdPostulacionAdj()!=null){
						adj.setDocumento(new Documentos());
						adj.getDocumento().setIdDocumento(idDoc);
						adj.setFechaMod(new Date());
						adj.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.merge(adj);
					}else{
						adj.setDocumento(new Documentos());
						adj.getDocumento().setIdDocumento(idDoc);
						adj.setPostulacionCap(new PostulacionCap());
						adj.getPostulacionCap().setIdPostulacion(capEdit.getIdPostulacion());
						adj.setActivo(true);
						adj.setUsuAlta("SYSTEM");
						adj.setFechaAlta(new Date());
						em.persist(adj);
					}
					
						
				}
			}
			em.flush();
			limpiar();
			findListaDets();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"Ha ocurrido un error: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public void editar(int index){
		indexUP=index;
		esEdit=true;
		ListaDet listaDetEdit=em.find(ListaDet.class, listaDets.get(indexUP).getIdListaDet());
		PostulacionCap capEdit=em.find(PostulacionCap.class, listaDetEdit.getPostulacionCap().getIdPostulacion());
		persona=em.find(Persona.class, capEdit.getPersona().getIdPersona());
		telefono=persona.getTelefonos();
		email=persona.getEMail();
		docIdentidad=persona.getDocumentoIdentidad();
		nombre=persona.getNombres();
		apellido=persona.getApellidos();
		idPersona=persona.getIdPersona();
		idPaisExp=persona.getPaisByIdPaisExpedicionDoc().getIdPais();
		PostulacionCapAdj adj=findCapAdj(capEdit.getIdPostulacion());
		if(adj.getIdPostulacionAdj()!=null){
			if(adj.getDocumento()!=null){
				Documentos doc= em.find(Documentos.class,adj.getDocumento().getIdDocumento());
				nombreDoc=doc.getNombreFisicoDoc();
				idTipoDocumento=doc.getDatosEspecificos().getIdDatosEspecificos();
			}
		}else{
			nombreDoc=null;
			idTipoDocumento=null;
		}
		
		
	}
	private boolean exite(Long id,Long idPersona){
		String sql="Select p from PostulacionCap p where p.activo=true  and p.persona.idPersona="+idPersona+"" +
				" and p.capacitacion.idCapacitacion="+idCapacitacion;
		if(id!=null)
			sql+=" and p.idPostulacion!="+id;
			
		List<PostulacionCap> caps=em.createQuery(sql).getResultList();
	
		return !caps.isEmpty();
	}
	
	public void imprimir(){
		try {
			SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			if (!sufc.validarInput(idCapacitacion.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
			reportUtilFormController =
				(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController.init();
			reportUtilFormController.setNombreReporte("RPT_CU499");
			cargarParametros();
			reportUtilFormController.imprimirReportePdf();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	private void cargarParametros() {
		try {
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.setIdConfigCab(capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init2();

			Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel", diccDscNEO.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad", diccDscNEO.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oee", diccDscNEO.get("OEE"));
			reportUtilFormController.getParametros().put("unidadOrga", diccDscNEO.get("UND_ORG"));
			reportUtilFormController.getParametros().put("nombreCapa", capacitaciones.getNombre());
			reportUtilFormController.getParametros().put("tipoCapa", tipoCapac);
			
			ConfiguracionUoCab cab=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			reportUtilFormController.getParametros().put("oeeUsuarioLogueado", cab.getDenominacionUnidad());
			
			reportUtilFormController.getParametros().put("usuAlta", usuarioLogueado.getCodigoUsuario());
			reportUtilFormController.getParametros().put("idCapacitacion",idCapacitacion);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private PostulacionCapAdj findCapAdj(Long idPost){
		try {
			PostulacionCapAdj p=(PostulacionCapAdj)em.createQuery("Select p from PostulacionCapAdj p" +
					" where p.postulacionCap.idPostulacion=:idPostulacion").setParameter("idPostulacion",idPost).getSingleResult();
			return p;
		} catch (NoResultException e) {
			return new PostulacionCapAdj();
		}catch (Exception ex) {
			return new PostulacionCapAdj();
		}
	}
	
	private boolean checkValue(){
		if (idPaisExp == null || docIdentidad == null || docIdentidad.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar la Persona antes de realizar esta acci\u00F3n");
			return false;
		}
		if(telefono==null || telefono.trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Telefonos antes de realizar esta acci\u00F3n");
			return false;
		}
		if(email!=null&& !email.trim().equals("")){
			if(!General.isEmail(email.toLowerCase()))
			{
				statusMessages.add(Severity.ERROR,"Ingrese un mail valido. Verifique");
				return false;
			}
		}
		return true;
	}
	
	private Long guardarAdjuntos(String fileName, int fileSize, String contentType, byte[] file, Long idTipoDoc, Long idDocumento,Persona persona) {
		try {
		
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "PostulacionCap";
			String nombrePantalla = "registrarParticipantes";
			String direccionFisica = "";
			String nro_doc_ID= persona.getDocumentoIdentidad()+persona.getIdPersona();
			direccionFisica =File.separator + "SICCA" + File.separator +"USUARIO_PORTAL" + File.separator+nro_doc_ID;
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	private EmpleadoPuesto findEmpleadoPuesto(){
		List<EmpleadoPuesto> ePuestos=em.createQuery("SELECT empleadoPuesto FROM EmpleadoPuesto empleadoPuesto " +
				" where empleadoPuesto.actual=true").getResultList();
		/**
		 * si retorna varios registros, verificar si uno de ellos pertenece al OEE 
		 * **/
		if(ePuestos.size()>1){
			//SE COMPRUEBA SI UNOS DE ELLOS PERTENECE AL OEE DE LA CAPACITACION
			for (EmpleadoPuesto empleadoPuesto : ePuestos) {
				if(empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab()!=null){
					if(empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo().longValue()==capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo().longValue());
						return empleadoPuesto;
				}
				
			}
			//SI NO HAY,SE ASIGNA CUALQUIERA
			return ePuestos.get(0);
		}
		
		return null;
		
	}
	
	
	public void limpiar(){
		indexUP=-1;
		docIdentidad=null;
		persona=new Persona();
		idPersona=null;
		uploadedFile=null;
		idTipoDocumento=null;
		esEdit=false;
		idPaisExp=idParaguay();
		nombreDoc=null;
		limpiarDatosPersona();
	}
	
	
	
	/**
	 * -	Grilla: “Participantes” recupera los datos de la tabla “LISTA_DET”  
	 * */
	@SuppressWarnings("unchecked")
	private void findListaDets(){
		try {
			em.clear();
			listaDets= em.createQuery("SELECT i FROM ListaDet i" +
					" left join i.postulacionCap.postulacionCapAdjs " +
					" WHERE i.activo=true AND i.tipo='P'" +
					" AND i.listaCab.capacitaciones.idCapacitacion=:idCapacitacion " +
					" AND i.listaCab.idLista=:idCab ORDER BY i.postulacionCap.persona.nombres ").setParameter("idCapacitacion", idCapacitacion).setParameter("idCab", listaCab.getIdLista()).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p =
			em.createQuery(" Select p from Pais p" + " where lower(p.descripcion) like 'paraguay'").getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}
	
	
	
	public String back(){
		primeraEntrada=true;
		return "ok";
	}
	public boolean habLinkEliminar(int index){
		if(index==indexUP)
			return false;
		return true;
	}
	public String documento(int index){
		String nombre="";
		ListaDet listaDetDoc=em.find(ListaDet.class, listaDets.get(index).getIdListaDet());
		PostulacionCap capDoc=em.find(PostulacionCap.class, listaDetDoc.getPostulacionCap().getIdPostulacion());
		PostulacionCapAdj adj=findCapAdj(capDoc.getIdPostulacion());
		if(adj.getIdPostulacionAdj()!=null){
			if(adj.getDocumento()!=null){
				Documentos doc= em.find(Documentos.class,adj.getDocumento().getIdDocumento());
				nombre=doc.getNombreFisicoDoc();
			}
		}
		return nombre;
	}
	public boolean habDescarga(Long idPostulacion){
		PostulacionCapAdj adj=findCapAdj(idPostulacion);
		if(adj.getIdPostulacionAdj()!=null){
			if(adj.getDocumento()!=null)
				return true;
			
		}
		return false;
	}
	
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}

	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public boolean isEsEdit() {
		return esEdit;
	}

	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	
	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}


	public byte[] getUploadedFile() {
		return uploadedFile;
	}



	public void setUploadedFile(byte[] uploadedFile) {
		this.uploadedFile = uploadedFile;
	}


	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public int getIndexUP() {
		return indexUP;
	}

	public void setIndexUP(int indexUP) {
		this.indexUP = indexUP;
	}

	public String getTipoCapac() {
		return tipoCapac;
	}

	public void setTipoCapac(String tipoCapac) {
		this.tipoCapac = tipoCapac;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getHabBtnAddPersons() {
		return habBtnAddPersons;
	}

	public void setHabBtnAddPersons(Boolean habBtnAddPersons) {
		this.habBtnAddPersons = habBtnAddPersons;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public List<ListaDet> getListaDets() {
		return listaDets;
	}
	public void setListaDets(List<ListaDet> listaDets) {
		this.listaDets = listaDets;
	}
	public ListaCab getListaCab() {
		return listaCab;
	}
	public void setListaCab(ListaCab listaCab) {
		this.listaCab = listaCab;
	}

}
