package py.com.excelsis.sicca.legajo.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DiscapacidadApoyos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidadLegajo;
import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidadLegajo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.PersonaDiscapacidadHome;
import py.com.excelsis.sicca.session.PersonaHome;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("discapacidadLegajoFC")
public class DiscapacidadLegajoFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	PersonaDiscapacidadHome personaDiscapacidadHome;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	PersonaList personaList;
	@In(create = true)
	PersonaHome personaHome;
	
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	
	
	@In (create=true)
	SeguridadUtilFormController seguridadUtilFormController;

	private PersonaDiscapacidadLegajo personaDiscapacidad;
	private General general;
	private List<PersonaDiscapacidadLegajo> discapacidadsList = new ArrayList<PersonaDiscapacidadLegajo>();
	private Long idPais;
	private Long idNacionalidad;
	private Long idDiscapacidad;
	private Long idFuncionalidad;
	private Long idTipoDoc;
	private ReprPersonaDiscapacidadLegajo reprPersonaDiscapacidad;
	private String nomRep;
	private String apeRep;
	private String dirRep;
	private String telRep;
	private String docRep;
	private String mail;
	private Persona persona;
	private boolean esEdit = false;;
	private boolean guardarRepre = false;
	Persona personaRepre = new Persona();
	private boolean habPaisExp;
	private DiscapacidadApoyos discapacidadApoyos= new DiscapacidadApoyos();
	private int indexUp;
	private Long idPersona;
	private Long idCausa;
	private String texto;

	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item;
	private String nombreDoc;
	private Long idDoc;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;

	public void init() {
		try {
			setear();
			guardarRepre = false;
			persona = em.find(Persona.class,idPersona);
			
			if (persona != null)
				findDiscapacidad();
			
			cargarPanel1();
			esEdit = false;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void cargarPanel1(){
		try {
			Query qDisc = em.createQuery("Select d from DiscapacidadApoyos d " +
					" where d.persona.idPersona=:idPersona and d.activo=true").setParameter("idPersona", idPersona.longValue());
			List<DiscapacidadApoyos> listaDisc = qDisc.getResultList();
			
			if(listaDisc.size() > 0){
				discapacidadApoyos = (DiscapacidadApoyos) listaDisc.get(0);
			}
		} catch (NoResultException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean verifDatosRepre(){
		if ( idPais == null) {
			statusMessages.add(Severity.ERROR,"Debe seleccionar el País. Verifique");
			return false;
		}
		if ( docRep == null || docRep.trim().equals("") ) {
			statusMessages.add(Severity.ERROR,"Debe Ingresar el Doc. de Identidad. Verifique");
			return false;
		}
		if (idNacionalidad == null) {
			statusMessages.add(Severity.ERROR,"Debe seleccionar la Nacionalidad. Verifique");
			return false;
		}
		if (nomRep == null || nomRep.trim().equals("") ) {
			statusMessages.add(Severity.ERROR,"Debe Ingresar el Nombre del Representante. Verifique");
			return false;
		}
		if (apeRep== null || apeRep.trim().equals("") ) {
			statusMessages.add(Severity.ERROR,"Debe Ingresar el Apellido del Representante. Verifique");
			return false;
		}
		if (dirRep== null || dirRep.trim().equals("") ) {
			statusMessages.add(Severity.ERROR,"Debe Ingresar la Dirección del Representante. Verifique");
			return false;
		}
		if (telRep== null || telRep.trim().equals("") ) {
			statusMessages.add(Severity.ERROR,"Debe Ingresar el Teléfono del Representante. Verifique");
			return false;
		}
		if (mail== null || mail.trim().equals("") ) {
			statusMessages.add(Severity.ERROR,"Debe Ingresar el Correo Electrónico del Representante. Verifique");
			return false;
		}
		
		
		
		if (!representante() && guardarRepre) {
			statusMessages
					.add(Severity.WARN," Debe verificar los campos obligatorios del Representante");
			return false;
		}
		if(mail!=null && !mail.trim().equals("")){
			if(!General.isEmail((mail.toLowerCase())))
			{
				statusMessages.add(Severity.WARN,"Ingrese un mail valido. Verifique");
				return false;
			}
			mail=mail.toLowerCase();
				
		}
		
		if(personaRepre.getIdPersona()==null){
			List<Persona> p=em.createQuery("Select p from Persona p " +
					" where p.documentoIdentidad='"+docRep+"' and p.paisByIdPaisExpedicionDoc.idPais="+idPais).getResultList();
			if(!p.isEmpty()){
				statusMessages.clear();
				statusMessages.add("La persona ingresada ya existe. Verifique");
				
			}
				
		}
		return true;
	}

	public String save() {
		try {
			
			if(!verifDatosRepre())
				return null;
			
			if (guardarRepre) {
				if (personaRepre.getIdPersona()!=null) {
					personaRepre.setEMail(mail);
					personaRepre.setNombres(nomRep);
					personaRepre.setApellidos(apeRep);
					personaRepre.setDireccionLaboral(dirRep);
					personaRepre.setTelContacto(telRep);
					personaRepre.setPaisByIdPaisExpedicionDoc(em.find(
							Pais.class, idPais));
					personaRepre.setDatosEspecificos(em.find(
							DatosEspecificos.class, idNacionalidad));
					personaRepre.setUsuMod(usuarioLogueado.getCodigoUsuario());
					personaRepre.setFechaMod(new Date());

					em.merge(personaRepre);
					em.flush();
					personaHome.setInstance(personaRepre);
					
				} else {
					personaRepre.setEMail(mail);
					personaRepre.setNombres(nomRep);
					personaRepre.setApellidos(apeRep);
					personaRepre.setDireccionLaboral(dirRep);
					personaRepre.setTelContacto(telRep);
					personaRepre.setPaisByIdPaisExpedicionDoc(em.find(
							Pais.class, idPais));
					personaRepre.setDatosEspecificos(em.find(
							DatosEspecificos.class, idNacionalidad));
					personaRepre.setDocumentoIdentidad(docRep);
					personaRepre.setFechaAlta(new Date());
					personaRepre.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(personaRepre);
					em.flush();
					personaHome.setInstance(personaRepre);
					
				}
				reprPersonaDiscapacidad.setPersona(persona);
				reprPersonaDiscapacidad.setActivo(true);
				reprPersonaDiscapacidad.setPersonaRep(personaHome.getInstance());
				if(reprPersonaDiscapacidad.getIdReprPersonaDiscapacidad()!=null){
					reprPersonaDiscapacidad.setFechaMod(new Date());
					reprPersonaDiscapacidad.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(reprPersonaDiscapacidad);
				}else{
					reprPersonaDiscapacidad.setFechaAlta(new Date());
					reprPersonaDiscapacidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(reprPersonaDiscapacidad);
				}
				
					
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				

			}
			
			
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	public void eliminar(){
		reprPersonaDiscapacidad.setActivo(false);
		reprPersonaDiscapacidad.setFechaMod(new Date());
		reprPersonaDiscapacidad.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(reprPersonaDiscapacidad);
		em.flush();
		reprPersonaDiscapacidad= new ReprPersonaDiscapacidadLegajo();
		habPaisExp=true;
		personaRepre= new Persona();
		nomRep=null;
		apeRep=null;
		dirRep=null;
		telRep=null;
		idPais=null;
		idNacionalidad=null;
		docRep=null;
		mail= null;
		guardarRepre=false;
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		
	}
	


	public String update() {
		try {
		if(!cumpleValorAdd())
			return null;
			
			
			
			esEdit = false;
			/**
			 * PARA EL CASO DE DOCUMENTO ADJUNTO
			 * 
			 **/
			if(uploadedFile!=null){
				Long idDocAnterior=personaDiscapacidad.getDocumentos()!=null?personaDiscapacidad.getDocumentos().getIdDocumento():null;
				idDoc = guardarAdjuntos(fileName, uploadedFile.length, contentType, uploadedFile, idTipoDoc, idDocAnterior);
				if (idDoc == null) 
					return null;
				else
					personaDiscapacidad.setDocumentos(em.find(Documentos.class, idDoc));
			}
				
			/***/
			
			
			personaDiscapacidad.setActivo(true);
			personaDiscapacidad
					.setDatosEspecificosByIdDatosEspecificosDiscapac(em.find(
							DatosEspecificos.class, idDiscapacidad));
			if(idFuncionalidad!=null)
				personaDiscapacidad.setDatosEspecificosByIdDatosEspecificosGradoAutonom(em.find(DatosEspecificos.class, idFuncionalidad));
			else
				personaDiscapacidad.setDatosEspecificosByIdDatosEspecificosGradoAutonom(null);
			if(idCausa!=null)
				personaDiscapacidad.setDatosEspecificoByIdDatosEspecificosCausa(em.find(DatosEspecificos.class,idCausa));
			personaDiscapacidad.setPersona(persona);
			personaDiscapacidad.setFechaMod(new Date());
			personaDiscapacidad.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(personaDiscapacidad);
			em.flush();
			listarDiscapacidades();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			if (personaDiscapacidad.getDocumentos() != null && idDoc != null) {
				Documentos doc = em.find(Documentos.class, idDoc);
				doc.setIdTabla(personaDiscapacidad.getIdPersonaDiscapacidad());
				doc.setPersona(persona);
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				doc.setFechaMod(new Date());
				em.merge(doc);
				em.flush();
			}
			
			cancelar();
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error al intentar actualizar el registro");
			esEdit=true;
			idDiscapacidad=personaDiscapacidad.getIdPersonaDiscapacidad();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	private void listarDiscapacidades(){
		discapacidadsList = em.createQuery(
				" select d from PersonaDiscapacidadLegajo d where  d.activo = true and d.persona.idPersona= "
						+ persona.getIdPersona()).getResultList();
	}

	/**
	 * GUARDA LOS DATOS DEL PANEL
	 * Apoyos recibidos relativos a la discapacidad seg\u00FAn fuente(Si requiere)
	 * */
	public void savePanel1(){
		try {
			if(discapacidadApoyos.getIdDiscapacidadApoyos()!=null)
			{
				discapacidadApoyos.setUsuMod(usuarioLogueado.getCodigoUsuario());
				discapacidadApoyos.setFechaMod(new Date());
				em.merge(discapacidadApoyos);
			}else {
				discapacidadApoyos.setPersona(persona);
				discapacidadApoyos.setFechaAlta(new Date());
				discapacidadApoyos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				discapacidadApoyos.setActivo(true);
				em.persist(discapacidadApoyos);
			}
			em.flush();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Se ha producido un error inesperado: "+e.getMessage());
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void seleccionoExDoc(){
		try {
			if(idPais==null){
				statusMessages.add(Severity.ERROR,"Debe completar previamente el país de expedición de documento");
				return ;
			}else{
				if(docRep!=null && !docRep.trim().equals(""))
					habPaisExp=false;
				else
					habPaisExp=true;
				
				List<Persona> p=em.createQuery("Select p from Persona p " +
						" where p.documentoIdentidad='"+seguridadUtilFormController.caracterInvalido(docRep)+"' and p.paisByIdPaisExpedicionDoc.idPais="+idPais).getResultList();
				if(!p.isEmpty()){
					personaRepre=em.find(Persona.class, p.get(0).getIdPersona());
					nomRep=personaRepre.getNombres();
					apeRep=personaRepre.getApellidos();
					dirRep=personaRepre.getDireccionLaboral();
					telRep=personaRepre.getTelContacto();
					if(personaRepre.getDatosEspecificos()!=null)
						idNacionalidad=personaRepre.getDatosEspecificos().getIdDatosEspecificos();
					else
						idNacionalidad=null;
					mail=personaRepre.getEMail();
				}else
				{
					personaRepre= new Persona();
					nomRep=null;
					apeRep=null;
					dirRep=null;
					telRep=null;
					idNacionalidad=null;
					mail=null;
				}
					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void cambioPais(){
		personaRepre= new Persona();
		nomRep=null;
		apeRep=null;
		dirRep=null;
		telRep=null;
		idNacionalidad=null;
		mail=null;
		docRep=null;
	}
	public void cancelar() {
		idDiscapacidad = null;
		personaDiscapacidad = new PersonaDiscapacidadLegajo();
		idFuncionalidad = null;
		esEdit = false;
		idTipoDoc = null;
		personaDiscapacidad = new PersonaDiscapacidadLegajo();
		nombreDoc = null;
		item=null;
		idCausa=null;

	}
	

	public void editar(int index) {
		esEdit = true;
		personaDiscapacidad = discapacidadsList.get(index);
		idDiscapacidad = personaDiscapacidad
				.getDatosEspecificosByIdDatosEspecificosDiscapac()
				.getIdDatosEspecificos();
		item=null;
		if(personaDiscapacidad.getDatosEspecificoByIdDatosEspecificosCausa()!=null &&personaDiscapacidad.getDatosEspecificoByIdDatosEspecificosCausa().getIdDatosEspecificos()!=null)
			idCausa=personaDiscapacidad.getDatosEspecificoByIdDatosEspecificosCausa().getIdDatosEspecificos();
		if(personaDiscapacidad.getDatosEspecificosByIdDatosEspecificosGradoAutonom()!=null)
			idFuncionalidad= personaDiscapacidad
				.getDatosEspecificosByIdDatosEspecificosGradoAutonom()
				.getIdDatosEspecificos();
		if (personaDiscapacidad.getDocumentos() != null) {
			Documentos doc = em.find(Documentos.class, personaDiscapacidad
					.getDocumentos().getIdDocumento());
			String url = doc.getUbicacionFisica();
			if (url.contains("\\")) {
				url = url.replace("\\", System.getProperty("file.separator"));

			}
			if (url.contains("//")) {
				url = url.replace("//", System.getProperty("file.separator"));

			}
			String separador = System.getProperty("file.separator");
			if (!url.endsWith(separador))
				url = url + System.getProperty("file.separator");

			url += doc.getNombreFisicoDoc();
			
			idTipoDoc = doc.getDatosEspecificos().getIdDatosEspecificos();
			nombreDoc = doc.getNombreFisicoDoc();

		} else {
			idTipoDoc = null;
			nombreDoc = null;
		}

		indexUp = index;
	}

	   
	  public void limpiar(){
		idDiscapacidad = null;
		idFuncionalidad = null;
		personaDiscapacidad = new PersonaDiscapacidadLegajo();
		idTipoDoc = null;
		nombreDoc = null;
		item = null;
		nombreDoc = null;
		idCausa=null;
		
	}
		
	  
	@SuppressWarnings("unchecked")
	private boolean cumpleValorAdd(){

		if (idDiscapacidad == null ) {
			statusMessages
					.add(Severity.ERROR,"Debe seleccionar el Item Discapacidad antes de agregar. Verifique");
			return false;
		}
		if (idDiscapacidad == null || personaDiscapacidad.getGrado() == null) {
			statusMessages
					.add(Severity.ERROR,"Debe ingresar el Grado antes de agregar. Verifique");
			return false;
		}
		if (personaDiscapacidad.getGrado().intValue() <= 0) {
			statusMessages
					.add(Severity.ERROR,"El valor del Grado debe ser mayor a cero. Verifique");
			return false;
		}
		if (personaDiscapacidad.getGrado() != null
				&&personaDiscapacidad.getGrado().intValue() > 100) {
			statusMessages
					.add(Severity.ERROR,"El valor del Grado no debe ser mayor a 100%. Verifique");
			return false;
		}
		
		
		if (personaDiscapacidad.getNroCertificado() != null
				&& personaDiscapacidad.getNroCertificado().intValue() <= 0) {
			statusMessages
					.add(Severity.ERROR,"El valor del Nro. de Certif debe ser mayor a cero. Verifique");
			return false;
		}

		
		if(idFuncionalidad==null){
			statusMessages.add(Severity.ERROR,"Debe seleccionar el campo funcionalidad. Verifique");
			return false;
		}
		
		if (idTipoDoc == null) {
			statusMessages.add(Severity.ERROR,"Debe Seleccionar El tipo de Documento. Verifique");
			return false;
		}
		if(esEdit){
			
			if (uploadedFile == null && nombreDoc==null ) {
				statusMessages.add(Severity.ERROR,"Debe seleccionar el archivo. Verifique");
				return false;
			}
		}else{
			if (uploadedFile == null) {
				statusMessages.add(Severity.ERROR,"Debe seleccionar el archivo. Verifique");
				return false;
			}
			
		}
		
		
		
		
		if(personaDiscapacidad.getFechaEmision()!=null && personaDiscapacidad.getFechaEmision().after(new Date()))
		{
			statusMessages.add(Severity.ERROR,"La Fecha de Emisión no puede ser mayor a la fecha actual verifique ");
			return false;
		}
		if(personaDiscapacidad.getFechaEmision()!=null && !general.isFechaValida(personaDiscapacidad.getFechaEmision())){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Emisi&oacute;n inv&aacute;lida");
			return false;
		}
		
		String sql="Select d from PersonaDiscapacidadLegajo d" +
				" where d.datosEspecificosByIdDatosEspecificosDiscapac.idDatosEspecificos=:idDiscapacidad " +
				" and d.grado=:grado and d.activo=true " +
				" and d.datosEspecificosByIdDatosEspecificosGradoAutonom.idDatosEspecificos=:idFuncionalidad"+
				" and d.persona.idPersona = :idPersona";
		
		List<PersonaDiscapacidadLegajo> exitePerDiscapacidads=em.createQuery(sql).setParameter("idDiscapacidad", idDiscapacidad)
		.setParameter("idFuncionalidad", idFuncionalidad).setParameter("grado", personaDiscapacidad.getGrado().intValue())
		.setParameter("idPersona", idPersona).getResultList();
		if(!exitePerDiscapacidads.isEmpty() && !esEdit){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"El registro  ingresado ya existe, favor verificar");
			return false;
		}else if(exitePerDiscapacidads.size()>1 && esEdit){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"El registro  ingresado ya existe, favor verificar discapacidad, porcentaje o funcionalidad");
			return false;
		}
		return true;
	}
		

	public void addDiscapacidad() {
		try {
			
			if(!cumpleValorAdd())
				return ;
			
			/**
			 * Para el Caso de documento adjuntos
			 * */
			if (uploadedFile != null) {
				idDoc = guardarAdjuntos(fileName, uploadedFile.length, contentType, uploadedFile, idTipoDoc, null);
				if (idDoc == null) 
					return;
				else
					personaDiscapacidad.setDocumentos(em.find(Documentos.class, idDoc));
			}
			/**
			 * fin
			 * */
			personaDiscapacidad.setActivo(true);
			personaDiscapacidad.setDatosEspecificosByIdDatosEspecificosDiscapac(em
					.find(DatosEspecificos.class, idDiscapacidad));
			if (idFuncionalidad != null)
				personaDiscapacidad
						.setDatosEspecificosByIdDatosEspecificosGradoAutonom(em
								.find(DatosEspecificos.class, idFuncionalidad));
			if(idCausa!=null){
				personaDiscapacidad.setDatosEspecificoByIdDatosEspecificosCausa(em.find(DatosEspecificos.class,idCausa));
				personaDiscapacidad.setCausa(em.find(DatosEspecificos.class,idCausa).getDescripcion());
			}
			personaDiscapacidad.setPersona(persona);
			personaDiscapacidad.setFechaAlta(new Date());
			personaDiscapacidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			
			em.persist(personaDiscapacidad);
			em.flush();
			listarDiscapacidades();
			if (personaDiscapacidad.getDocumentos() != null && idDoc != null) {
				Documentos doc = em.find(Documentos.class, idDoc);
				doc.setIdTabla(personaDiscapacidad.getIdPersonaDiscapacidad());
				doc.setPersona(persona);
//				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
//				doc.setFechaMod(new Date());
				em.merge(doc);
				em.flush();
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			limpiar();
			
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error al agregar el registro");
		}
		

	}

	
	
	private Long  guardarAdjuntos(String fileName, int fileSize, String contentType, byte[] file, Long idTipoDoc, Long idDocumento) {
		try {
			
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "PersonaDiscapacidadLegajo";
			String nombrePantalla = "DiscapacidadLegajo";
			String sf=File.separator;
			String direccionFisica = sf+"LEGAJO"+sf+persona.getDocumentoIdentidad()+"_"+persona.getIdPersona()+sf+"PCD"+sf;
			
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void abrirDoc(int index){
		
		PersonaDiscapacidadLegajo e= discapacidadsList.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumentos().getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}

	public void delItems(int index) {
		PersonaDiscapacidadLegajo pd = em.find(PersonaDiscapacidadLegajo.class,
				discapacidadsList.get(index).getIdPersonaDiscapacidad());
		pd.setActivo(false);
		pd.setUsuMod(usuarioLogueado.getCodigoUsuario());
		pd.setFechaMod(new Date());
		em.merge(pd);
		if(pd.getDocumentos()!=null){
			Documentos documentos= em.find(Documentos.class, pd.getDocumentos().getIdDocumento());
			documentos.setActivo(false);
			documentos.setUsuMod(usuarioLogueado.getCodigoUsuario());
			documentos.setFechaMod(new Date());
			em.merge(documentos);
		}
		
		em.flush();
		discapacidadsList.remove(index);
	
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
	}
	
	
	

	@SuppressWarnings("unchecked")
	public List<SelectItem> getNacionalidadSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));

		List<Object[]> n = em
				.createNativeQuery(
						"Select  de.id_datos_especificos, de.descripcion From seleccion.referencias  r,"
								+ "seleccion.datos_especificos de Where r.dominio like 'NACIONALIDADES' And  r.valor_num = de.id_datos_generales"
								+ " Order by de.descripcion ").getResultList();
		Iterator<Object[]> it = n.iterator();

		while (it.hasNext()) {
			Object[] fila = it.next();
			if (fila[0] != null)
				si.add(new SelectItem(fila[0], fila[1] != null ? fila[1]
						.toString() : ""));
		}

		return si;
	}

	public void miListenerAdjuntar(UploadEvent event) {
		try {

			item = event.getUploadItem();
			item.getFile();
			FileInputStream fis;
			try {
				fis = new FileInputStream(item.getFile());
			} catch (FileNotFoundException e1) {
				return;
			}

			long length = item.getFileSize();

			if (length > 5242880) {
				return;
			}

			byte[] bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			try {
				while (offset < bytes.length
						&& (numRead = fis.read(bytes, offset, bytes.length
								- offset)) >= 0) {
					offset += numRead;
				}
			} catch (IOException e1) {
				return;
			}

			if (offset < bytes.length) {
				try {
					throw new IOException(
							"No se pudo leer el archivo por completo "
									+ item.getFileName());
				} catch (IOException e) {
					return;
				}
			}

			try {
				fis.close();
			} catch (IOException e) {
				return;
			}

			item.getFileSize();
			nombreDoc = item.getFileName();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeNameDoc(){
		nombreDoc=null;
	}
	
	// METODOS PRIVADO
	
	
	

	@SuppressWarnings("unchecked")
	private void findDiscapacidad() {
		discapacidadsList = em.createQuery(
				" select d from PersonaDiscapacidadLegajo d where  d.activo = true and d.persona.idPersona= "
						+ persona.getIdPersona()).getResultList();
		// representante
		List<ReprPersonaDiscapacidadLegajo> rp = em.createQuery(
				"select pr from ReprPersonaDiscapacidadLegajo pr "
						+ " where pr.persona.idPersona="
						+ persona.getIdPersona()+" and pr.activo=true").getResultList();
		if (!rp.isEmpty()) {
			reprPersonaDiscapacidad = rp.get(0);
			nomRep = reprPersonaDiscapacidad.getPersonaRep().getNombres();
			apeRep = reprPersonaDiscapacidad.getPersonaRep().getApellidos();
			dirRep = reprPersonaDiscapacidad.getPersonaRep().getDireccionLaboral();
			telRep = reprPersonaDiscapacidad.getPersonaRep().getTelContacto();
			idPais = reprPersonaDiscapacidad.getPersonaRep()
					.getPaisByIdPaisExpedicionDoc().getIdPais();
			mail = reprPersonaDiscapacidad.getPersonaRep().getEMail();
			idNacionalidad = reprPersonaDiscapacidad.getPersonaRep()
					.getDatosEspecificos().getIdDatosEspecificos();
			docRep = reprPersonaDiscapacidad.getPersonaRep()
					.getDocumentoIdentidad();
			personaRepre=reprPersonaDiscapacidad.getPersonaRep();
			habPaisExp=false;
			guardarRepre = true;
			
		}else{
			idPais=General.getIdParaguay();
			habPaisExp=true;
		}

	}

	private void setear() {
		discapacidadsList = new ArrayList<PersonaDiscapacidadLegajo>();
		nomRep = null;
		apeRep = null;
		dirRep = null;
		telRep = null;
		docRep = null;
		idDiscapacidad = null;
		idFuncionalidad = null;
		idNacionalidad = null;
		idPais = null;
		idTipoDoc = null;
		mail = null;
		personaDiscapacidad = new PersonaDiscapacidadLegajo();
		reprPersonaDiscapacidad = new ReprPersonaDiscapacidadLegajo();
		habPaisExp=true;
		discapacidadApoyos= new DiscapacidadApoyos();

	}

	private boolean representante() {
		if (nomRep != null && !nomRep.trim().equals("")) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
		}
		if (apeRep != null && !apeRep.trim().equals("")) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
		}
		if (dirRep != null && !dirRep.trim().equals("")) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
		}
		if (idNacionalidad != null) {

			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
		}
		if (docRep != null && !docRep.trim().equals("")) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
		}
		if (idPais != null) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
		}
		if (mail != null && !mail.trim().equals("")) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
			
			
			
		}
		if (reprPersonaDiscapacidad.getObservacion() != null
				&& !reprPersonaDiscapacidad.getObservacion().trim().equals("")) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}

		}
		guardarRepre = false;
		return true;
	}

	private boolean verifObligatorios() {
		if (nomRep == null || nomRep.trim().equals("") || apeRep == null
				|| apeRep.trim().equals("") || idNacionalidad == null
				|| dirRep.trim().equals("") || dirRep == null
				|| docRep == null || docRep.trim().equals("") || idPais == null) {
			return false;
		}
		return true;

	}

	
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getGradoAutonomia() {
		try {
			List<DatosEspecificos> especificosGradoAutonomia=em.createQuery(" Select d from DatosEspecificos d" +
					" where d.datosGenerales.descripcion='GRADOS DE AUTONOMIA' and d.activo=true order by d.descripcion ").getResultList();
			return especificosGradoAutonomia;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	public List<SelectItem> getGradoAutonomiaSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getGradoAutonomia())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}
	public List<SelectItem> getDiscapacidadSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDiscapacidades())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}
	
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getDiscapacidades() {
		try {
			List<DatosEspecificos> especificosDiscapacidad=em.createQuery(" Select d from DatosEspecificos d" +
					" where d.datosGenerales.descripcion='DISCAPACIDADES' and d.activo=true order by d.descripcion ").getResultList();
			return especificosDiscapacidad;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
	public List<SelectItem> getCausaDiscSelectItems() {
		List<SelectItem> ca = new Vector<SelectItem>();
		ca.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getCausaDisc())
			ca.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return ca;
	}
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getCausaDisc() {
		try {
			List<DatosEspecificos> especificosCausaDisc=em.createQuery(" Select d from DatosEspecificos d" +
					" where d.datosGenerales.descripcion='DISCAPACIDAD CAUSA' and d.activo=true order by d.descripcion ").getResultList();
			return especificosCausaDisc;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
	
	// GETTERS Y SETTERS 	

	public PersonaDiscapacidadLegajo getPersonaDiscapacidad() {
		return personaDiscapacidad;
	}

	public void setPersonaDiscapacidad(PersonaDiscapacidadLegajo personaDiscapacidad) {
		this.personaDiscapacidad = personaDiscapacidad;
	}

	public List<PersonaDiscapacidadLegajo> getDiscapacidadsList() {
		return discapacidadsList;
	}

	public void setDiscapacidadsList(List<PersonaDiscapacidadLegajo> discapacidadsList) {
		this.discapacidadsList = discapacidadsList;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdNacionalidad() {
		return idNacionalidad;
	}

	public void setIdNacionalidad(Long idNacionalidad) {
		this.idNacionalidad = idNacionalidad;
	}

	public Long getIdDiscapacidad() {
		return idDiscapacidad;
	}

	public void setIdDiscapacidad(Long idDiscapacidad) {
		this.idDiscapacidad = idDiscapacidad;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}


	public Long getIdFuncionalidad() {
		return idFuncionalidad;
	}

	public void setIdFuncionalidad(Long idFuncionalidad) {
		this.idFuncionalidad = idFuncionalidad;
	}

	public ReprPersonaDiscapacidadLegajo getReprPersonaDiscapacidad() {
		return reprPersonaDiscapacidad;
	}

	public void setReprPersonaDiscapacidad(
			ReprPersonaDiscapacidadLegajo reprPersonaDiscapacidad) {
		this.reprPersonaDiscapacidad = reprPersonaDiscapacidad;
	}

	public String getNomRep() {
		return nomRep;
	}

	public void setNomRep(String nomRep) {
		this.nomRep = nomRep;
	}

	public String getApeRep() {
		return apeRep;
	}

	public void setApeRep(String apeRep) {
		this.apeRep = apeRep;
	}
	
	public String getDirRep() {
		return dirRep;
	}

	public void setDirRep(String dirRep) {
		this.dirRep = dirRep;
	}
	
	public String getTelRep() {
		return telRep;
	}

	public void setTelRep(String telRep) {
		this.telRep = telRep;
	}

	public String getDocRep() {
		return docRep;
	}

	public void setDocRep(String docRep) {
		this.docRep = docRep;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public boolean isEsEdit() {
		return esEdit;
	}

	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}

	public boolean isGuardarRepre() {
		return guardarRepre;
	}

	public void setGuardarRepre(boolean guardarRepre) {
		this.guardarRepre = guardarRepre;
	}

	public int getIndexUp() {
		return indexUp;
	}

	public void setIndexUp(int indexUp) {
		this.indexUp = indexUp;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public Persona getPersonaRepre() {
		return personaRepre;
	}

	public void setPersonaRepre(Persona personaRepre) {
		this.personaRepre = personaRepre;
	}

	public boolean isHabPaisExp() {
		return habPaisExp;
	}

	public void setHabPaisExp(boolean habPaisExp) {
		this.habPaisExp = habPaisExp;
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

	public DiscapacidadApoyos getDiscapacidadApoyos() {
		return discapacidadApoyos;
	}

	public void setDiscapacidadApoyos(DiscapacidadApoyos discapacidadApoyos) {
		this.discapacidadApoyos = discapacidadApoyos;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getIdCausa() {
		return idCausa;
	}

	public void setIdCausa(Long idCausa) {
		this.idCausa = idCausa;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Long getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}

	
	
}
