package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;


import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.MemoHomologacion;
import py.com.excelsis.sicca.entity.NotaHomologacion;
import py.com.excelsis.sicca.entity.PlantillaMemoHomologacion;
import py.com.excelsis.sicca.entity.PlantillaNotaHomologacion;
import py.com.excelsis.sicca.entity.Tags;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.MemoHomologacionHome;
import py.com.excelsis.sicca.seleccion.session.NotaHomologacionHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("generarMemoHomologacion")
public class GenerarMemoHomologacion implements Serializable{
	
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
	MemoHomologacionHome memoHomologacionHome;
	
	private MemoHomologacion memoHomologacion;
	private Long idConcurso;
	private Long idConfiguracionUo;//OEE
	private String idConcursoPuestoAgr;
	private Long idMemoHomologacion;
	private List<PlantillaMemoHomologacion> pLMemoHomologacionList;
	private PlantillaMemoHomologacion plantillaMemoHomologacionSelec;
	private String fechaMemo;
	private String anio;
	private Concurso concurso;
	private Long idConcurso411;
	General general;
	private boolean primeraEntrada=true;
	
	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item ;
	private String nombreDoc;
	private File inputFile ;
	private Long idDoc;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}
	
	public void init(){
		if(primeraEntrada){
			primeraEntrada=false;
			concurso=em.find(Concurso.class,idConcurso);
			idConfiguracionUo=concurso.getConfiguracionUoCab().getIdConfiguracionUo();
			validarOee(concurso);
			memoHomologacion = new MemoHomologacion();
			nombreDoc="";
			if(idMemoHomologacion!=null){
				memoHomologacion= em.find(MemoHomologacion.class,idMemoHomologacion);
				if(memoHomologacion.getDocumentos()!=null)
					nombreDoc=memoHomologacion.getDocumentos().getNombreFisicoDoc();
			}else
				memoHomologacion.setFecha(new Date());
				
		}
	
		
		memoHomologacionHome.setInstance(memoHomologacion);
		allPlantilla();
	}
	
	public void findPlantilla(Long idPlantilla){
		plantillaMemoHomologacionSelec=em.find(PlantillaMemoHomologacion.class, idPlantilla);
		if(plantillaMemoHomologacionSelec.getA()!=null)
			memoHomologacion.setA(eliminarTags(plantillaMemoHomologacionSelec.getA()));
		if(plantillaMemoHomologacionSelec.getDe()!=null)
			memoHomologacion.setDe(eliminarTags(plantillaMemoHomologacionSelec.getDe()));
		if(plantillaMemoHomologacionSelec.getRef()!=null)
			memoHomologacion.setRef(eliminarTags(plantillaMemoHomologacionSelec.getRef()));
		if(plantillaMemoHomologacionSelec.getTexto()!=null)
			memoHomologacion.setTexto(eliminarTags(plantillaMemoHomologacionSelec.getTexto()));
		
		if(plantillaMemoHomologacionSelec.getTitulo()!=null)
			memoHomologacion.setTitulo(eliminarTags(plantillaMemoHomologacionSelec.getTitulo()));
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
	            nombreDoc=item.getFileName();
	           

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	@SuppressWarnings("unchecked")
	public String save(){
		
		try {
			if(!chekValores())
				return null;
			if(memoHomologacion.getFecha().after(new Date())){
				statusMessages.add(Severity.ERROR,"La Fecha de Memo no puede ser mayor a la fecha actual, verifique");
				return null;
			}
		if( !general.isFechaValida(memoHomologacion.getFecha())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "La Fecha ingresada no es inválida, verifique");
				return null ;
		}
			
		/**
		 * Para el Caso de documento adjuntos
		 * */
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
		if(item!=null){
			/**
			 * VALIDACION AGREGADA
			 * */
			String nomfinal = "";
			nomfinal = item.getFileName();
			 String extension = nomfinal.substring( nomfinal.lastIndexOf( "." ) );
				List<TipoArchivo> ta = em
						.createQuery(
								"select t from TipoArchivo t "
										+ " where lower(t.extension) like '" 
										+ extension.toLowerCase() + "'").getResultList();
				if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
					if (item.getFileSize() > ta.get(0).getUnidMedidaByte()
							.intValue()) {
						statusMessages
								.add(Severity.ERROR,"El archivo supera el tamaño máximo permitido. Límite "+ta.get(0).getTamanho()+ta.get(0).getUnidMedida()+", verifique");
						return null;
					}
				}
				/**
				 * FIN
				 * */
			
			documento();
			if(idDoc!=null){
				
				if(idDoc.longValue()==-8){
					statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return  null;					
				}
				if(idDoc.longValue()==-7){
					statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return  null;	
				}
				if(idDoc.longValue()==-6){
					statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
					return  null;	
				}
				Documentos doc=em.find(Documentos.class, idDoc);
				doc.setIdTabla(memoHomologacion.getIdMemoHomologacion());
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				memoHomologacion.setDocumentos(em.find(Documentos.class,idDoc));
				doc.setFechaMod(new Date());
				
				
			}else{
				statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
				return  null;
			}
			
		}
		memoHomologacion.setPlantillaMemoHomologacion(plantillaMemoHomologacionSelec);
		memoHomologacion.setFechaAlta(new Date());
		memoHomologacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		em.persist(memoHomologacion);
		em.flush();
		String msg="persisted";
	
		
		String[] idsAgs=idConcursoPuestoAgr.split(",");
		for (int i = 0; i < idsAgs.length; i++) {
			Long id=Long.parseLong(idsAgs[i]);
			if(id!=null){
				ConcursoPuestoAgr agr= em.find(ConcursoPuestoAgr.class, id);
				agr.setMemoHomologacion(memoHomologacion);
			//	agr.setEstado(5);
				agr.setFechaMod(new Date());
				agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(agr);
				em.flush();
			}
		}
		idMemoHomologacion=memoHomologacion.getIdMemoHomologacion();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return msg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean chekValores(){
		
		if(memoHomologacion.getNroMemo().intValue()<=0){
			statusMessages.add(Severity.ERROR,"El valor del Nro. de Memo  debe ser mayor a cero");
			return false;
		}
		
		return true;
		
	}
	@SuppressWarnings("unchecked")
	public String update(){
		try {
			if(!chekValores())
				return null;
			if(memoHomologacion.getFecha().after(new Date())){
				statusMessages.add(Severity.ERROR,"La Fecha de Memo no puede ser mayor a la fecha actual, verifique");
				return null;
			}
			if( !general.isFechaValida(memoHomologacion.getFecha())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "La Fecha ingresada no es inválida, verifique");
				return null ;
		}
		
		/**
		 *PARA EL CASO DE DOCUMENTO ADJUNTO 
		 *
		 **/
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
		Long idDoc=null;
		
		if(item!=null ){
			/**
			 * VALIDACION AGREGADA
			 * */
			String nomfinal = "";
			nomfinal = item.getFileName();
			 String extension = nomfinal.substring( nomfinal.lastIndexOf( "." ) );
				List<TipoArchivo> ta = em
						.createQuery(
								"select t from TipoArchivo t "
										+ " where lower(t.extension) like '" 
										+ extension.toLowerCase() + "'").getResultList();
				if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
					if (item.getFileSize() > ta.get(0).getUnidMedidaByte()
							.intValue()) {
						statusMessages
								.add(Severity.ERROR,"El archivo supera el tamaño máximo permitido. Límite "+ta.get(0).getTamanho()+ta.get(0).getUnidMedida()+", verifique");
						return null;
					}
				}
				/**
				 * FIN
				 * */
		}
		if(memoHomologacion.getDocumentos()!=null && item!=null){//habia un archivo y se modifica
			
			if(memoHomologacion.getDocumentos().getIdDocumento()!=null){
				Documentos docDB=em.find(Documentos.class, memoHomologacion.getDocumentos().getIdDocumento());
				if(!docDB.getNombreFisicoDoc().toLowerCase().equals(nombreDoc.toLowerCase()) || 
						!docDB.getTamanhoDoc().equals(String.valueOf(item.getFileSize()))){//significa que ubo algun cambio se envia los parametros del item
					idDoc=AdmDocAdjuntoFormController.editar(item.getFile(),Integer.valueOf(item.getFileSize()),item.getFileName(),item.getContentType(), docDB.getUbicacionFisica(), "memoHomologacion_edit", docDB.getIdDocumento(), findResolucion(), usuarioLogueado.getIdUsuario(), memoHomologacion.getIdMemoHomologacion(),"MemoHomologacion");
					if(idDoc!=null){
						
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
							return null;	
						}
						
						memoHomologacion.setDocumentos(em.find(Documentos.class,idDoc));
							
					}else{
						statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
						return null;
					}
				}//porque no hubo modificacion! nohace  nada! 
				
			}
		}
		if (inputFile == null && item != null) {//no habia ningun archivo y se carga
			if (memoHomologacion.getDocumentos() == null) {
				String direccionFisica = "";
				SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
				String anio = sdfSoloAnio.format(new Date());
				direccionFisica = "//SICCA//"+anio+"//OEE//"+idConfiguracionUo+"//"+concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos()+"//"+concurso.getIdConcurso();
				idDoc = AdmDocAdjuntoFormController.editar(item.getFile(),
						item.getFileSize(), item.getFileName(),
						item.getContentType(), direccionFisica,
						"memoHomologacion_edit", null,
						findResolucion(), usuarioLogueado.getIdUsuario(),
						memoHomologacion.getIdMemoHomologacion(),
						"MemoHomologacion");
				if(idDoc!=null){
					
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
						return null;	
					}
					
					memoHomologacion.setDocumentos(em.find(Documentos.class,idDoc));
						
				}else{
					statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
					return null;
				}

			}

		}
		

		memoHomologacion.setFechaMod(new Date());
		memoHomologacion.setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
		em.merge(memoHomologacion);
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		String msg="updated";
		return msg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void abrirDoc(){
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(memoHomologacion.getDocumentos().getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}
	 public void print() {
		 try {
			 if(idMemoHomologacion==null){
				 statusMessages.add(Severity.WARN,"Debe guardar para poder imprimir,Verifique");
				 return;
			 }
			 
			    SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
				Connection conn = JpaResourceBean.getConnection();
				  
				HashMap<String, Object> param = new HashMap<String, Object>();
			        param.put("usuario", usuarioLogueado.getCodigoUsuario());
			        param.put("idMemo",idMemoHomologacion);
			        ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			        param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
					param.put("path_logo", servletContext.getRealPath("/img/"));
				    param.put("REPORT_CONNECTION", conn);
				 JasperReportUtils.respondPDF("CU250", param, false, conn, usuarioLogueado);
				
					
				
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	 }
	 public void documento() throws NamingException{
			String nombrePantalla="memoHomologacion_edit";
			String direccionFisica="";
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio=sdfSoloAnio.format(new Date());
			direccionFisica="//SICCA//"+anio+"//OEE//"+idConfiguracionUo+"//"+concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos()+"//"+concurso.getIdConcurso();
			idDoc=AdmDocAdjuntoFormController.guardarDirecto(item, direccionFisica,nombrePantalla, findResolucion(),usuarioLogueado.getIdUsuario(),"MemoHomologacion");
			
			
		}
		
	
//	METODOS PRIVADOS 
	 //retorna el id del dato especifico que tiene RESOLUCION
	 @SuppressWarnings("unchecked")
	private Long findResolucion(){
			List<DatosEspecificos> dEspecificos= em.createQuery("Select d from DatosEspecificos d " +
					" where lower(d.descripcion) like 'resolucion' ").getResultList();
			if(dEspecificos.isEmpty())
				return null;
			
			return dEspecificos.get(0).getIdDatosEspecificos();
		}
	 private void crearUploadItem(String fileName, int fileSize,
				String contentType, byte[] file) {
			item = new UploadItem(fileName, fileSize, contentType,
					AdminReclamoSugPortalFormController.crearFile(fileName, file));
			nombreDoc = item.getFileName();
		}
	@SuppressWarnings("unchecked")
	private void allPlantilla(){
		pLMemoHomologacionList=em.createQuery(" Select p from PlantillaMemoHomologacion p " +
				" order by  p.descripcion").getResultList();
	}
	
	//recibe el valor del campo retorna sin los tags en caso que contenga
	private String eliminarTags(String campo){
		String result="";
		for (int i = 0; i < campo.length(); i++) {
			if(String.valueOf(campo.charAt(i)).equals("<") && String.valueOf(campo.charAt(i+1)).equals("@")){
				String tags="";
				String valorTag;
				for (int j = i; j < campo.length(); j++) {
					tags+=String.valueOf(campo.charAt(j));
					if(String.valueOf(campo.charAt(j)).equals(">")){
						i=j;
						break;
					}
						
				}
				valorTag=valTags(tags);
				result+=" "+valorTag+" ";
			}else
				result+=String.valueOf(campo.charAt(i));
			
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private String valTags(String tags){
		String valorFinal="";
		List<Tags> tList=em.createQuery("Select t from Tags t " +
				" where t.tag like '"+tags+"'").getResultList();
		if(!tList.isEmpty()){
			if(tags.equals("<@nombre_concurso@>")){
				valorFinal=selectTags(idConcurso, tList.get(0).getConsulta());
			}else if(tags.equals("<@tipo_concurso@>")){
				valorFinal=selectTags(idConcurso, tList.get(0).getConsulta());
			}else if(tags.equals("<@oee@>")){
				valorFinal=selectTags(usuarioLogueado.getIdUsuario(), tList.get(0).getConsulta());
			}
		}
		
		return valorFinal;
		
	}
	
	//arma el select con el id correspondiente
	@SuppressWarnings("unchecked")
	private String selectTags(long id,String select){
			String result="";
		
		for (int i = 0; i < select.length(); i++) {
			if(String.valueOf(select.charAt(i)).equals("=") ){
				if((i < select.length()) &&String.valueOf(select.charAt(i+2)).equals("<")){
					
				int ultPos=0;
				for (int j = i+2; j <  select.length(); j++) {
					if(String.valueOf(select.charAt(j)).equals(">")){
						ultPos=j;
						break;
					}
				}
				result+=String.valueOf(select.charAt(i));
				result+=id;
				i=ultPos;
				}else{
					result+=String.valueOf(select.charAt(i));
				}
			}else
				result+=String.valueOf(select.charAt(i));
			
		}
		
		if(!result.equals("")){
			   List<String> query = em.createNativeQuery(result.toString()).getResultList();
			   if(!query.isEmpty())
				   return query.get(0);
			   
				   
		}
		
		return result;
	}

//	GETTERS Y SETTERS


	public Long getIdConcurso() {
		return idConcurso;
	}




	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}





	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}


	public Long getIdConfiguracionUo() {
		return idConfiguracionUo;
	}

	public void setIdConfiguracionUo(Long idConfiguracionUo) {
		this.idConfiguracionUo = idConfiguracionUo;
	}

	

	public String getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(String idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}



	public Long getIdMemoHomologacion() {
		return idMemoHomologacion;
	}

	public void setIdMemoHomologacion(Long idMemoHomologacion) {
		this.idMemoHomologacion = idMemoHomologacion;
	}

	public MemoHomologacion getMemoHomologacion() {
		return memoHomologacion;
	}

	public void setMemoHomologacion(MemoHomologacion memoHomologacion) {
		this.memoHomologacion = memoHomologacion;
	}

	public List<PlantillaMemoHomologacion> getpLMemoHomologacionList() {
		return pLMemoHomologacionList;
	}

	public void setpLMemoHomologacionList(
			List<PlantillaMemoHomologacion> pLMemoHomologacionList) {
		this.pLMemoHomologacionList = pLMemoHomologacionList;
	}

	public PlantillaMemoHomologacion getPlantillaMemoHomologacionSelec() {
		return plantillaMemoHomologacionSelec;
	}

	public void setPlantillaMemoHomologacionSelec(
			PlantillaMemoHomologacion plantillaMemoHomologacionSelec) {
		this.plantillaMemoHomologacionSelec = plantillaMemoHomologacionSelec;
	}

	public String getFechaMemo() {
		return fechaMemo;
	}

	public void setFechaMemo(String fechaMemo) {
		this.fechaMemo = fechaMemo;
	}

	public Long getIdConcurso411() {
		return idConcurso411;
	}

	public void setIdConcurso411(Long idConcurso411) {
		this.idConcurso411 = idConcurso411;
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

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}
	
	
	

	
	
	
}
