package py.com.excelsis.sicca.legajo.session.form;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosPremio;
import py.com.excelsis.sicca.entity.Desvinculacion;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.Inhabilitados;
import py.com.excelsis.sicca.entity.Jubilados;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;

@Scope(ScopeType.CONVERSATION)
@Name("premiosReconocimientosFC")
public class PremiosReconocimientosFC implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -788040635466613316L;

	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(value="entityManager")
    EntityManager em;
	@In
	StatusMessages statusMessages;
	

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;


	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	
	private Long idPersona;
	private String texto;
	private Persona persona= new Persona();
	private List<DatosPremio> datosPremioLista= new Vector<DatosPremio>();
	private DatosPremio datosPremio= new DatosPremio();

	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private String nombreDoc;
	private Long idDoc;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	private Long idTipoDoc;
	
	
	public void init(){
		try {
			persona=em.find(Persona.class, idPersona);
			limpiar();
			findPremios();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void findPremios(){
		datosPremioLista=em.createQuery("Select d from DatosPremio d " +
				" where d.persona.idPersona=:idPersona and d.activo=true order by fechaActo desc ").setParameter("idPersona",idPersona).getResultList();
	}

	public void addRow(){
		try {
			if(!toDetailOk()){
				return;
			}	
			/**
			 * Para el Caso de documento adjuntos
			 * */
			if(uploadedFile!=null){
				idDoc=guardarAdjuntos(fileName, uploadedFile.length, contentType, uploadedFile, idTipoDoc, null);
				if(idDoc!=null){
					datosPremio.setDocumento(em.find(Documentos.class, idDoc));
				}else
					return ;
			}
			datosPremio.setFechaAlta(new Date());
			datosPremio.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			datosPremio.setActivo(true);
			datosPremio.setPersona(persona);
			em.persist(datosPremio);
			if(datosPremio.getDocumento()!=null && idDoc!=null){
				Documentos doc= em.find(Documentos.class,idDoc);
				doc.setIdTabla(datosPremio.getIdDatosPremio());
				doc.setPersona(persona);
//				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
//				doc.setFechaMod(new Date());
				em.merge(doc);
			}
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			limpiar();
			findPremios();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	public void updateRow(){
		try {
			if(!toDetailOk()){
				return;
			}	
			/**
			 * Para el Caso de documento adjuntos
			 * */
			if(uploadedFile!=null){
				Long idDocAnterior=datosPremio.getDocumento()!=null?datosPremio.getDocumento().getIdDocumento():null;
				idDoc=guardarAdjuntos(fileName, uploadedFile.length, contentType, uploadedFile, idTipoDoc, idDocAnterior);
				if(idDoc!=null){
					datosPremio.setDocumento(em.find(Documentos.class, idDoc));
				}else
					return ;
			}
			datosPremio.setFechaMod(new Date());
			datosPremio.setUsuMod(usuarioLogueado.getCodigoUsuario());
			datosPremio.setPersona(persona);
			em.merge(datosPremio);
			if(datosPremio.getDocumento()!=null && idDoc!=null){
				Documentos doc= em.find(Documentos.class,idDoc);
				doc.setIdTabla(datosPremio.getIdDatosPremio());
				doc.setPersona(persona);
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				doc.setFechaMod(new Date());
				em.merge(doc);
			}
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			limpiar();
			findPremios();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	private Long  guardarAdjuntos(String fileName, int fileSize, String contentType, byte[] file, Long idTipoDoc, Long idDocumento) {
		try {
			
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "DatosPremio";
			String nombrePantalla = "PremiosReconocimientos";
			String sf=File.separator;
			String direccionFisica = sf+"LEGAJO"+sf+persona.getDocumentoIdentidad()+"_"+persona.getIdPersona()+sf+"PREMIOS"+sf;
			
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void editar(Long id){
		datosPremio=em.find(DatosPremio.class, id);
		if(datosPremio.getDocumento()!=null)
		{
			nombreDoc=datosPremio.getDocumento().getNombreFisicoDoc();
			idTipoDoc=datosPremio.getDocumento().getDatosEspecificos().getIdDatosEspecificos();
		}
	}
	
	private boolean toDetailOk(){
		if(datosPremio.getObsPremio()==null || datosPremio.getObsPremio().trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo Observacion antes de realizar la operacion, verifique");
			return false;
		}
//		if(datosPremio.getNroActo()!=null && datosPremio.getNroActo().intValue()<=0){
//			statusMessages.add(Severity.ERROR,"El campo Acto Administrativo  debe ser mayor a cero, verifique");
//			return false;
//		}
		if(datosPremio.getNroActo()==null) 
		   datosPremio.setNroActo(0);
		if(idTipoDoc==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Campo Tipo de Documento antes de realizar la operacion, verifique");
			return false;
		}
		if(uploadedFile==null && datosPremio.getDocumento()==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Archivo, verifique");
			return false;
		}
		if(datosPremio.getFechaActo()!=null){
			if( !General.isFechaValida(datosPremio.getFechaActo())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Fecha nválida. Verifique");
				return false;
			}
		}
		return true;
	}
	
	public void limpiar(){
		nombreDoc=null;
		idDoc=null;
		uploadedFile=null;
		contentType=null;
		fileName=null;
		idTipoDoc=null;
		datosPremio= new DatosPremio();
	}

	
	public void abrirDoc(int index){
		
		DatosPremio e= datosPremioLista.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumento().getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}
	public void deleteRow(int index){
		DatosPremio premio=datosPremioLista.get(index);
		premio.setFechaMod(new Date());
		premio.setUsuMod(usuarioLogueado.getCodigoUsuario());
		premio.setActivo(false);
		em.merge(premio);
		if(premio.getDocumento()!=null){
			Documentos doc=em.find(Documentos.class,premio.getDocumento().getIdDocumento());
			doc.setFechaMod(new Date());
			doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
			doc.setActivo(false);
			em.merge(doc);
		}
		
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		findPremios();
	}

	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getTipoDocPremios() {
		try {
			List<DatosEspecificos> especificos=em.createQuery("Select d from DatosEspecificos d" +
					" where d.datosGenerales.descripcion='TIPOS DE DOCUMENTOS'" +
					" and d.valorAlf='DOC_V' and d.descripcion='DOCUMENTO ACTO ADMINISTRATIVO' " +
					" and d.activo=true order by d.descripcion").getResultList();
			

			return especificos;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	public List<SelectItem> getTipoDocPremiosSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getTipoDocPremios())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}
	
//	GETTERS Y SETTERS

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}


	public DatosPremio getDatosPremio() {
		return datosPremio;
	}

	public void setDatosPremio(DatosPremio datosPremio) {
		this.datosPremio = datosPremio;
	}


	public List<DatosPremio> getDatosPremioLista() {
		return datosPremioLista;
	}


	public void setDatosPremioLista(List<DatosPremio> datosPremioLista) {
		this.datosPremioLista = datosPremioLista;
	}


	public String getNombreDoc() {
		return nombreDoc;
	}


	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}


	public Long getIdDoc() {
		return idDoc;
	}


	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
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


	public Long getIdTipoDoc() {
		return idTipoDoc;
	}


	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}
	
	
	
	
}
