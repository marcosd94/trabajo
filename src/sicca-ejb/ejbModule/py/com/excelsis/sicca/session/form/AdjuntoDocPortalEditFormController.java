package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.print.Doc;
import javax.servlet.http.HttpServletResponse;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.util.Naming;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import enums.EsFuncionario;
import enums.Estado;

import py.com.excelsis.sicca.entity.AdjuntoDocPortal;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.SelFuncionDatosEsp;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Funcion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.AdjuntoDocPortalHome;
import py.com.excelsis.sicca.util.JpaResourceBean;



@Scope(ScopeType.CONVERSATION)
@Name("adjuntoDocPortalEditFormController")
public class AdjuntoDocPortalEditFormController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;
	
	
	
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	@In(value = "entityManager")
	EntityManager em;
	
	@PersistenceUnit
	EntityManagerFactory emf;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(required = false)
	AdjuntoDocPortalHome adjuntoDocPortalHome;
	
	
	
	private AdjuntoDocPortal  adjuntoDocPortal;

	private Long idAdjuntoDocPortal;

	private Long idTipoDoc;
	private String nomLog;
	private String descripcion;
	private EsFuncionario publicado;//sino
	private Estado estado;
	private Integer nroOrden;
	private Integer maxOrden;
	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item ;
	private String nombreDoc;
	private String nombrePantalla;
	private Long idDoc;
	private Integer tamArchivo;
	private String tipoDoc;
	private File inputFile ;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	
	
	
	/****
	 * 
	 * */
	public void init(){
		try {
			 adjuntoDocPortal= new AdjuntoDocPortal();
			 nombreDoc=null;
			 adjuntoDocPortal.setPortal(false);
			 adjuntoDocPortal.setCuentaUsuario(false);
			 adjuntoDocPortal.setPublicado(true);
			 descripcion=null;
			 nomLog=null;
			 idTipoDoc= null;
			 estado=Estado.ACTIVO;
			 if(idAdjuntoDocPortal!=null){
				 adjuntoDocPortal= em.find(AdjuntoDocPortal.class, idAdjuntoDocPortal);
				 nombreDoc=adjuntoDocPortal.getDocumentos().getNombreFisicoDoc();
				 descripcion=adjuntoDocPortal.getDocumentos().getDescripcion();
				 nomLog=adjuntoDocPortal.getDocumentos().getNombreLogDoc();
				 idTipoDoc= adjuntoDocPortal.getDocumentos().getDatosEspecificos().getIdDatosEspecificos();
				 estado.setValor(adjuntoDocPortal.getActivo());
				 nroOrden=adjuntoDocPortal.getNroOrden();
				 maxOrden=maxNroOrden();
				 if(adjuntoDocPortal.getDocumentos()!=null)
					 inputFile= new File(adjuntoDocPortal.getDocumentos().getUbicacionFisica());
				 if(nroOrden==null){
					 nroOrden=maxNroOrden();
					 maxOrden=nroOrden;
				 }
				 
			 }else{
				 nroOrden=maxNroOrden();
				 maxOrden=nroOrden;
			 }
			
			adjuntoDocPortalHome.setInstance(adjuntoDocPortal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String save(){
		try {
			if (uploadedFile != null) {
				if (AdmDocAdjuntoFormController.validarContentType(contentType))
					crearUploadItem(fileName, uploadedFile.length, contentType,
							uploadedFile);
				else {
					statusMessages.add(Severity.ERROR,
							"No se permite la carga de ese tipo de archivos.");
					return null;
				}
				
			}
			
			if(nroOrden!=null){
	    		if(nroOrden.intValue()>maxOrden.intValue()){
	    			statusMessages.add(Severity.ERROR,"El Nro. Orden no puede ser mayor a "+maxOrden);
	    			return null;
	    		}
	    		if(nroOrden.intValue()<=0) {
	    			statusMessages.clear();
	    			statusMessages.add(Severity.ERROR,"EL numero de orden debe ser mayo a 0. Verifique");
	    			return null;
	    		}
	    	}
	    	
	    	if(item==null){
	    		statusMessages.add(Severity.ERROR,"Debe seleccionar el archivo");
	    		return null;
	    	}
			
			if(!checkData())
				return null;
				try {
					documento();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				
				if(idDoc.longValue()==-8){
					statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return null;					
				}
				if(idDoc.longValue()==-7){
					statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return null;	
				}
				if(idDoc.longValue()==-6){
					statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
					return null ;	
				}
				
				Documentos doc= new Documentos();
				doc= em.find(Documentos.class, idDoc);
				doc.setNombreLogDoc(nomLog);
				doc.setDescripcion(descripcion);
				em.merge(doc);
				em.flush();
				adjuntoDocPortal.setNroOrden(nroOrden);
				adjuntoDocPortal.setActivo(true);
				adjuntoDocPortal.setDocumentos(doc);
				adjuntoDocPortal.setFechaAlta(new Date());
				adjuntoDocPortal.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				
				if(nroOrden<maxOrden){
					recalcularOrden();
				}
				em.persist(adjuntoDocPortal);
				em.flush();
				
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				idTipoDoc=null;
				idDoc=null;
				item=null;
				nombreDoc=null;
				
				return "persisted";
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@SuppressWarnings("unchecked")
	public String update(){
		try {
			
			
			if (uploadedFile != null) {
				if (AdmDocAdjuntoFormController.validarContentType(contentType))
					crearUploadItem(fileName, uploadedFile.length, contentType,
							uploadedFile);
				else {
					statusMessages.add(Severity.ERROR,
							"No se permite la carga de ese tipo de archivos.");
					return null;
				}
				
			}
			
			
			if(nroOrden!=null){
	    		if(nroOrden.intValue()>maxOrden.intValue()){
	    			statusMessages.add(Severity.ERROR,"El Nro. Orden no puede ser mayor a "+maxOrden);
	    			return null;
	    		}
	    		if(nroOrden.intValue()<=0) {
	    			statusMessages.clear();
	    			statusMessages.add(Severity.ERROR,"EL numero de orden debe ser mayo a 0. Verifique");
	    			return null;
	    		}
	    		
	    	}
			
			if(item==null && inputFile==null){
	    		statusMessages.add(Severity.ERROR,"Debe seleccionar el archivo");
	    		return null;
	    	}
			
			
			if(!checkData())
				return null;
			
			if(adjuntoDocPortal.getCuentaUsuario()==null && adjuntoDocPortal.getPortal()==null){
	    		statusMessages.add(Severity.ERROR,"Debe seleccionar  Mostrar documento ");
	    		return null;
	    	}
			/**
			 *PARA EL CASO DE DOCUMENTO ADJUNTO 
			 * 
			 **/
			Long idDoc=null;
			if( item!=null){//habia un archivo y se modifica
				
				if(adjuntoDocPortal.getDocumentos()!=null){
					Documentos docDB=em.find(Documentos.class, adjuntoDocPortal.getDocumentos().getIdDocumento());
					if(!docDB.getNombreFisicoDoc().toLowerCase().equals(nombreDoc.toLowerCase()) || 
							!docDB.getTamanhoDoc().equals(String.valueOf(item.getFileSize()))){//significa que ubo algun cambio se envia los parametros del item
						idDoc=AdmDocAdjuntoFormController.editar(item.getFile(),Integer.valueOf(item.getFileSize()),item.getFileName(),item.getContentType(), docDB.getUbicacionFisica(), "adjuntoDocPortal_edit", docDB.getIdDocumento(), idTipoDoc, usuarioLogueado.getIdUsuario(), adjuntoDocPortal.getIdAdjuntoDocPortal(),"AdjuntoDocPortal");
						if(idDoc!=null){
							
							if(idDoc.longValue()==-8){
								statusMessages.add("La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
								return null;					
							}
							if(idDoc.longValue()==-7){
								statusMessages.add("El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
								return null;	
							}
							if(idDoc.longValue()==-6){
								statusMessages.add("El archivo que desea levantar ya existe. Seleccione otro");
								return null;	
							}
							Documentos doc= em.find(Documentos.class,idDoc);
							doc.setNombreLogDoc(nomLog);
							doc.setDescripcion(descripcion);
							em.merge(doc);
							em.flush();
							
							adjuntoDocPortal.setDocumentos(doc);
								
						}else{
							statusMessages.add("Error al adjuntar el registro. Verifique");
							return null;
						}
					}else{
						Documentos doc= em.find(Documentos.class,adjuntoDocPortal.getDocumentos().getIdDocumento());
						doc.setNombreLogDoc(nomLog);
						doc.setDescripcion(descripcion);
						em.merge(doc);
						em.flush();
					}
					
				}
			}else{
				Documentos doc= em.find(Documentos.class,adjuntoDocPortal.getDocumentos().getIdDocumento());
				doc.setNombreLogDoc(nomLog);
				doc.setDescripcion(descripcion);
				em.merge(doc);
				em.flush();
			}
			
			
			if(nroOrden<maxOrden){
				recalcularOrden();
			}
			
			adjuntoDocPortal.setNroOrden(nroOrden);
			//adjuntoDocPortal.setActivo(estado.getValor()); 
			adjuntoDocPortal.setFechaMod(new Date());
			adjuntoDocPortal.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(adjuntoDocPortal);
			em.flush();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
		
	}
	
	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}
	
	
	
	public void documento() throws NamingException{
		nombrePantalla="adjuntoDocPortal_edit";
		String direccionFisica="";
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio=sdfSoloAnio.format(new Date());
		direccionFisica="\\SICCA\\"+anio+"\\SFP\\PORTAL\\";
		idDoc=AdmDocAdjuntoFormController.guardarDirecto(item, direccionFisica,nombrePantalla, idTipoDoc,usuarioLogueado.getIdUsuario(),"AdjuntoDocPortal");
		
		
	}
	
	

    public void miListenerAdjuntar(UploadEvent event) {
        try {
            
        	item=event.getUploadItem();
            item.getFile();
            FileInputStream fis;
            try {
                fis = new FileInputStream(item.getFile());
            } catch (FileNotFoundException e1) {
                return;
            }

            long length = item.getFileSize();

            if (length >5242880) {
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
            int pos=item.getFileName().lastIndexOf("."); 
          
            tamArchivo=item.getFileSize();
            tipoDoc=item.getContentType();
            nombreDoc=item.getFileName();
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<SelectItem> getTipoDocPORTALSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoPORTAL())
			si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
		return si;
	}
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentoPORTAL() {
		try {
			List<DatosEspecificos> datosEspecificosLists= em.createQuery("Select d from DatosEspecificos d " +
					" where d.datosGenerales.descripcion like 'TIPOS DE DOCUMENTOS' and d.valorAlf like 'PORTAL' and d.activo=true order by d.descripcion").getResultList();
		
			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
    
    
    @SuppressWarnings("unchecked")
	private boolean checkData(){
    	if(idTipoDoc==null){
    		statusMessages.add(Severity.ERROR,"Debe seleccionar Tipo de Documento");
    		return false;
    	}
    	
    	if(nomLog.trim().equals("")){
    		statusMessages.add(Severity.ERROR,"Debe ingresar el nombre del documento");
    		return false;
    	}
    	if((adjuntoDocPortal.getCuentaUsuario()==null|| !adjuntoDocPortal.getCuentaUsuario()) && 
    			(adjuntoDocPortal.getPortal()==null || !adjuntoDocPortal.getPortal() )){
    		statusMessages.add(Severity.ERROR,"Debe seleccionar  Mostrar documento ");
    		return false;
    	}
    	if(adjuntoDocPortal.getPortal()==null && adjuntoDocPortal.getCuentaUsuario()==null){
    		statusMessages.add(Severity.ERROR,"Debe seleccionar donde se debe mostrar el Documento ");
    		return false;
    	}
    	/**
		 * VALIDACION AGREGADA
		 * */
    	if(item!=null){
    		String nomfinal = "";
    		nomfinal = item.getFileName();
    		 String extension = nomfinal.substring( nomfinal.lastIndexOf( "." ) );
    			List<TipoArchivo> ta = em
    					.createQuery(
    							"select t from TipoArchivo t "
    									+ " where lower(t.extension) like '" 
    									+ extension.toLowerCase() + "'").getResultList();
    			if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
    				if (item.getFileSize() > ta.get(0).getUnidMedidaByte().intValue()) {
    					statusMessages
    							.add(Severity.ERROR,"El archivo supera el tamaño máximo permitido. Límite "+ta.get(0).getTamanho()+ta.get(0).getUnidMedida()+", verifique");
    					return false;
    				}
    			}
    	}
			/**
			 * FIN
			 * */
    	
    	
    	return true;
    }
    
    public void chkOrden(){
    	
    	if(nroOrden!=null){
    		if(nroOrden.intValue()<=0) {
    			statusMessages.clear();
    			statusMessages.add(Severity.ERROR,"EL numero de orden debe ser mayo a 0. Verifique");
    			return;
    		}
    		if(nroOrden.intValue()>maxOrden.intValue()){
    			//maxOrden=maxNroOrden();
    			nroOrden=maxOrden;
    			statusMessages.add(Severity.ERROR,"El Nro. Orden no puede ser mayor a "+maxOrden);
    			return;
    		}
    		
    	}else{
    		statusMessages.add(Severity.ERROR,"Ingrese el Nro. Orden");
			return;
    	}
    	
    	
   
    }
    
    private void recalcularOrden(){
    	List<AdjuntoDocPortal> docPortals= em.createQuery("Select d from AdjuntoDocPortal d order by d.nroOrden").getResultList();
    	for (int i = 0; i < docPortals.size(); i++) {
    		if(docPortals.get(i).getNroOrden()!=null &&
    				(docPortals.get(i).getNroOrden().intValue()==nroOrden||docPortals.get(i).getNroOrden().intValue()>nroOrden) ){
    			AdjuntoDocPortal aux= em.find(AdjuntoDocPortal.class,docPortals.get(i).getIdAdjuntoDocPortal());
    			aux.setNroOrden(aux.getNroOrden()+1);
    			em.merge(aux);
    			em.flush();
    		}
    		
    		
		}
    	
    }
    
    
    
	@SuppressWarnings("unchecked")
	private Integer maxNroOrden(){
		List<Integer> max= em.createQuery("Select max(s.nroOrden) from AdjuntoDocPortal s").getResultList();
		if(max.isEmpty()|| max.get(0)==null)
			return 1;
		
		return max.get(0)+1;
	}
	
	
	
	
    //	GETTERS Y SETTERS
	public AdjuntoDocPortal getAdjuntoDocPortal() {
		return adjuntoDocPortal;
	}

	public void setAdjuntoDocPortal(AdjuntoDocPortal adjuntoDocPortal) {
		this.adjuntoDocPortal = adjuntoDocPortal;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}





	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public String getNomLog() {
		return nomLog;
	}

	public void setNomLog(String nomLog) {
		this.nomLog = nomLog;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	
	public EsFuncionario getPublicado() {
		return publicado;
	}

	public void setPublicado(EsFuncionario publicado) {
		this.publicado = publicado;
	}

	public Long getIdAdjuntoDocPortal() {
		return idAdjuntoDocPortal;
	}

	public void setIdAdjuntoDocPortal(Long idAdjuntoDocPortal) {
		this.idAdjuntoDocPortal = idAdjuntoDocPortal;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Integer getNroOrden() {
		return nroOrden;
	}

	public void setNroOrden(Integer nroOrden) {
		this.nroOrden = nroOrden;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
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

	

	


	
	
}
