package py.com.excelsis.sicca.legajo.session.form;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosAmonestacion;
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
@Name("admAmonestacionesFC")
public class AdmAmonestacionesFC implements Serializable{

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
	private List<DatosAmonestacion> datosAmonestacionLista= new Vector<DatosAmonestacion>();
	private DatosAmonestacion datosAmonestacion= new DatosAmonestacion();
	// private Long idMotivo;
	private Long idTipo;
	

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
			findAmonestaciones();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void findAmonestaciones(){
		datosAmonestacionLista=em.createQuery("Select d from DatosAmonestacion d " +
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
					datosAmonestacion.setDocumento(em.find(Documentos.class, idDoc));
				}else
					return ;
			}
			datosAmonestacion.setFechaAlta(new Date());
			datosAmonestacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			datosAmonestacion.setActivo(true);
			datosAmonestacion.setPersona(persona);
			// datosAmonestacion.setDatosEspMotivoAmonestacion(em.find(DatosEspecificos.class, idMotivo));
			datosAmonestacion.setDatosEspTipoSancion(em.find(DatosEspecificos.class, idTipo));
			em.persist(datosAmonestacion);
			if(datosAmonestacion.getDocumento()!=null && idDoc!=null){
				Documentos doc= em.find(Documentos.class,idDoc);
				doc.setIdTabla(datosAmonestacion.getIdDatosAmonestacion());
				doc.setPersona(persona);
//				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
//				doc.setFechaMod(new Date());
				em.merge(doc);
			}
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			limpiar();
			findAmonestaciones();
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
				Long idDocAnterior=datosAmonestacion.getDocumento()!=null?datosAmonestacion.getDocumento().getIdDocumento():null;
				idDoc=guardarAdjuntos(fileName, uploadedFile.length, contentType, uploadedFile, idTipoDoc, idDocAnterior);
				if(idDoc!=null){
					datosAmonestacion.setDocumento(em.find(Documentos.class, idDoc));
				}else
					return ;
			}
			datosAmonestacion.setFechaMod(new Date());
			datosAmonestacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			datosAmonestacion.setPersona(persona);
			// datosAmonestacion.setDatosEspMotivoAmonestacion(em.find(DatosEspecificos.class, idMotivo));
			datosAmonestacion.setDatosEspTipoSancion(em.find(DatosEspecificos.class, idTipo));
			em.merge(datosAmonestacion);
			if(datosAmonestacion.getDocumento()!=null && idDoc!=null){
				Documentos doc= em.find(Documentos.class,idDoc);
				doc.setIdTabla(datosAmonestacion.getIdDatosAmonestacion());
				doc.setPersona(persona);
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				doc.setFechaMod(new Date());
				em.merge(doc);
			}
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			limpiar();
			findAmonestaciones();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	private Long  guardarAdjuntos(String fileName, int fileSize, String contentType, byte[] file, Long idTipoDoc, Long idDocumento) {
		try {
			
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "DatosAmonestacion";
			String nombrePantalla = "AdmAmonestaciones";
			String sf=File.separator;
			String direccionFisica = sf+"LEGAJO"+sf+persona.getDocumentoIdentidad()+"_"+persona.getIdPersona()+sf+"AMONESTACIONES"+sf;
			
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void editar(Long id){
		datosAmonestacion=em.find(DatosAmonestacion.class, id);
		if(datosAmonestacion.getDocumento()!=null)
		{
			nombreDoc=datosAmonestacion.getDocumento().getNombreFisicoDoc();
			idTipoDoc=datosAmonestacion.getDocumento().getDatosEspecificos().getIdDatosEspecificos();
			idTipo=datosAmonestacion.getDatosEspTipoSancion().getIdDatosEspecificos();
		}
		// idMotivo=datosAmonestacion.getDatosEspMotivoAmonestacion().getIdDatosEspecificos();
	}
	
	private boolean toDetailOk(){
		
		/* if(idMotivo==null){
			statusMessages.add(Severity.ERROR,"No se ingresó el dato correspondiente al campo obligatorio Motivo, verifique");
			return false;
		} */
		
		if(idTipo==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Campo Tipo de Sanción antes de realizar la operacion, verifique");
			return false;
		}
		
		if(datosAmonestacion.getNroActo()!=null && datosAmonestacion.getNroActo().intValue()<=0){
			statusMessages.add(Severity.ERROR,"El campo Acto Administrativo  debe ser mayor a cero, verifique");
			return false;
		}
		if(idTipoDoc==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Campo Tipo de Documento antes de realizar la operacion, verifique");
			return false;
		}
		if(uploadedFile==null && datosAmonestacion.getDocumento()==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Archivo, verifique");
			return false;
		}
		if(datosAmonestacion.getFechaActo()!=null){
			if( !General.isFechaValida(datosAmonestacion.getFechaActo())){
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
		datosAmonestacion= new DatosAmonestacion();
		// idMotivo=null;
		idTipo=null;
	}

	
	public void abrirDoc(int index){
		
		DatosAmonestacion e= datosAmonestacionLista.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumento().getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}
	public void deleteRow(int index){
		DatosAmonestacion amonestacion=datosAmonestacionLista.get(index);
		amonestacion.setFechaMod(new Date());
		amonestacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
		amonestacion.setActivo(false);
		em.merge(amonestacion);
		if(amonestacion.getDocumento()!=null){
			Documentos doc=em.find(Documentos.class,amonestacion.getDocumento().getIdDocumento());
			doc.setFechaMod(new Date());
			doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
			doc.setActivo(false);
			em.merge(doc);
		}
		
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		findAmonestaciones();
	}

	
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getTipoSanciones() {
		try {
			
			 String sql = "Select * from seleccion.datos_especificos dato_e " +
					" join seleccion.datos_generales dato_g on ( dato_e.id_datos_generales = dato_g.id_datos_generales)" +
					" where dato_g.descripcion='TIPO DE SANCION'  and dato_e.activo=true order by dato_e.descripcion";
		
			List<DatosEspecificos> especificos = em.createNativeQuery(sql, DatosEspecificos.class).getResultList();
			

			return especificos;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	public List<SelectItem> getTipoSancionSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos obj : getTipoSanciones())
			si.add(new SelectItem(obj.getIdDatosEspecificos(), ""
					+ obj.getDescripcion()));
		return si;
	}
	
	
	/*
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getMotivoAmonestaciones() {
		try {
			
			 String sql = "Select * from seleccion.datos_especificos dato_e " +
					" join seleccion.datos_generales dato_g on ( dato_e.id_datos_generales = dato_g.id_datos_generales)" +
					" where dato_g.descripcion='MOTIVO DE AMONESTACION'  and dato_e.activo=true order by dato_e.descripcion";
		
			List<DatosEspecificos> especificos = em.createNativeQuery(sql, DatosEspecificos.class).getResultList();
			

			return especificos;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	public List<SelectItem> getMotivoAmonestacionesSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos obj : getMotivoAmonestaciones())
			si.add(new SelectItem(obj.getIdDatosEspecificos(), ""
					+ obj.getDescripcion()));
		return si;
	}
	*/
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getTipoDocumeto() {
		try {
			List<DatosEspecificos> especificos=em.createQuery("Select d from DatosEspecificos d" +
					" where d.datosGenerales.descripcion='TIPOS DE DOCUMENTOS'" +
					" and (d.valorAlf='DOC_V' or d.valorAlf='RES')" +
					" and (d.descripcion='ACTO ADMINISTRATIVO' or d.descripcion='RESOLUCION')" +
					" and d.activo=true order by d.descripcion").getResultList();
			
			return especificos;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	public List<SelectItem> getTipoDocumetoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getTipoDocumeto())
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


	public List<DatosAmonestacion> getDatosAmonestacionLista() {
		return datosAmonestacionLista;
	}


	public void setDatosAmonestacionLista(
			List<DatosAmonestacion> datosAmonestacionLista) {
		this.datosAmonestacionLista = datosAmonestacionLista;
	}


	public DatosAmonestacion getDatosAmonestacion() {
		return datosAmonestacion;
	}


	public void setDatosAmonestacion(DatosAmonestacion datosAmonestacion) {
		this.datosAmonestacion = datosAmonestacion;
	}


	public Long getIdTipo() {
		return idTipo;
	}


	public void setIdTipo(Long idTipo) {
		this.idTipo = idTipo;
	}
	
	
	/*
	public Long getIdMotivo() {
		return idMotivo;
	}


	public void setIdMotivo(Long idMotivo) {
		this.idMotivo = idMotivo;
	}
	*/
	
	
	
}
