package py.com.excelsis.sicca.excseleccion.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.PlantillaResolucion;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.Tags;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("generarResolucionExcFC")
public class GenerarResolucionExcFC implements Serializable{
	
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

	
	private Resolucion resolucionHomologacion= new Resolucion();
	private Long idConcurso;
	private Long idConfiguracionUo;//OEE
	private String idConcursoPuestoAgrExc;
	private Long idResolucionHomologacion;
	private List<PlantillaResolucion>  plResoHomoloList;
	private PlantillaResolucion homologacionSelect;
	private String fechaResolucion;
	private String anio;
	private Concurso concurso;
	private Long idConcurso411;
	private String fromCU;
	
	General general;
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
	private boolean primeraEntrada=true;
	private boolean modoUpdate=false;
	private Long idExcepcion;
	private String texto;
	
	public void init(){
		try {
			if(primeraEntrada){
				concurso= em.find(Concurso.class, idConcurso);
				resolucionHomologacion = new Resolucion();
				idConfiguracionUo=concurso.getConfiguracionUoCab().getIdConfiguracionUo();
				if(idResolucionHomologacion!=null){
					resolucionHomologacion= em.find(Resolucion.class,idResolucionHomologacion);
					if(resolucionHomologacion.getDocumentos()!=null)
						nombreDoc=resolucionHomologacion.getDocumentos().getNombreFisicoDoc();
					modoUpdate=true;
				}else
					resolucionHomologacion.setFecha(new Date());
				
			
			}

			allPlantilla();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void findPlantilla(Long idPlantilla){
		homologacionSelect=em.find(PlantillaResolucion.class, idPlantilla);
		if(homologacionSelect.getTitulo()!=null)
			resolucionHomologacion.setTitulo(eliminarTags(homologacionSelect.getTitulo()));
		if(homologacionSelect.getVisto()!=null)
			resolucionHomologacion.setVisto(eliminarTags(homologacionSelect.getVisto()));
		if(homologacionSelect.getConsiderando()!=null)
			resolucionHomologacion.setConsiderando(eliminarTags(homologacionSelect.getConsiderando()));
		if(homologacionSelect.getPorTanto()!=null)
			resolucionHomologacion.setPorTanto(eliminarTags(homologacionSelect.getPorTanto()));
		if(homologacionSelect.getResuelve()!=null)
			resolucionHomologacion.setResuelve(eliminarTags(homologacionSelect.getResuelve()));
		if(homologacionSelect.getFirma()!=null)
			resolucionHomologacion.setFirma(eliminarTags(homologacionSelect.getFirma()));
		if(homologacionSelect.getCargo()!=null)
			resolucionHomologacion.setCargo(eliminarTags(homologacionSelect.getCargo()));
		if(homologacionSelect.getInstitucion()!=null)
			resolucionHomologacion.setInstitucion((eliminarTags(homologacionSelect.getInstitucion())));
			
		resolucionHomologacion.setPlantillaResolucionHomologacion(homologacionSelect);
		
	}
	public void findFecha(){
		String mes="";
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		
		switch (resolucionHomologacion.getFecha().getMonth()) {
		case 0:
			mes="Enero";	
			break;
		case 1:
			mes="Febrero";	
			break;
			
		case 2:
			mes="Marzo";	
			break;

		case 3:
			mes="Abril";	
			break;

		case 4:
			mes="Mayo";	
			break;

		case 5:
			mes="Junio";	
			break;

		case 6:
			mes="Julio";	
			break;
			
		case 7:
			mes="Agosto";	
			break;
			
		case 8:
			mes="Setiembre";	
			break;
			
		case 9:
			mes="Octubre";	
			break;
			
		case 10:
			mes="Noviembre";	
			break;
			
		case 11:
			mes="Diciembre";	
			break;
		}
		fechaResolucion=resolucionHomologacion.getLugar()+", "+resolucionHomologacion.getFecha().getDate()+" de "+mes+" de "+sdfSoloAnio.format(resolucionHomologacion.getFecha());
		anio="/"+sdfSoloAnio.format(resolucionHomologacion.getFecha());
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
			if(resolucionHomologacion.getFecha().after(new Date())){
				statusMessages.add(Severity.ERROR,"La Fecha de Resolución no puede ser mayor a la fecha actual, verifique");
				return null;
			}
			
			
			if( !general.isFechaValida(resolucionHomologacion.getFecha())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "La Fecha ingresada no es inválida, verifique");
				return null ;
			}
			
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
			
				
	
		
		resolucionHomologacion.setFechaAlta(new Date());
		resolucionHomologacion.setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		em.persist(resolucionHomologacion);
		
		String[] idsAgs=idConcursoPuestoAgrExc.split(",");
		for (int i = 0; i < idsAgs.length; i++) {
			Long id=Long.parseLong(idsAgs[i]);
			if(id!=null){//los cambios de estado de los grupos se cambio en el CU al presionar sgt. tarea para todos los casos
				ConcursoPuestoAgrExc agr= em.find(ConcursoPuestoAgrExc.class, id);
				if(fromCU.equals("595")){
					agr.setResolucionAdjudicacion(resolucionHomologacion);
				}else if(fromCU.equals("417")){
					agr.setResolucionAdjudicacion(resolucionHomologacion);
				}else if(fromCU.equals("597")){
					agr.setDecreto(resolucionHomologacion);
				}else if(fromCU.equals("599")){
					agr.setResolucionNombramiento(resolucionHomologacion);
				}
					
				agr.setFechaMod(new Date());
				agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(agr);
			
			}
		}
		em.flush();
		/**
		 * Para el Caso de documento adjuntos
		 * */
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
					statusMessages.add("La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return  null;					
				}
				if(idDoc.longValue()==-7){
					statusMessages.add("El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return  null;	
				}
				if(idDoc.longValue()==-6){
					statusMessages.add("El archivo que desea levantar ya existe. Seleccione otro");
					return  null;	
				}
				Documentos doc=em.find(Documentos.class, idDoc);
				doc.setIdTabla(resolucionHomologacion.getIdResolucion());
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				doc.setFechaMod(new Date());
				resolucionHomologacion.setDocumentos(em.find(Documentos.class,idDoc));
				em.merge(resolucionHomologacion);
				em.flush();
			
			}else{
				statusMessages.add("Error al adjuntar el registro. Verifique");
				modoUpdate=false;
				return  null;
			}
			
		}
		
		
		idResolucionHomologacion=resolucionHomologacion.getIdResolucion();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		modoUpdate=true;
		return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	private boolean chekValores(){
			
			if(resolucionHomologacion.getNroResolucion().intValue()<=0){
				statusMessages.add(Severity.ERROR,"El valor del Nro. Resolución  debe ser mayor a cero");
				return false;
			}
			
			return true;
			
		}
	



	
	@SuppressWarnings("unchecked")
	public String update(){
		modoUpdate=true;
		try {
			if(!chekValores())
				return null;
			if(resolucionHomologacion.getFecha().after(new Date())){
				statusMessages.add(Severity.ERROR,"La Fecha de Resolución no puede ser mayor a la fecha actual, verifique");
				return null;
			}
			if( !general.isFechaValida(resolucionHomologacion.getFecha())){
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
		}
		Long idDoc=null;
		if(resolucionHomologacion.getDocumentos()!=null && item!=null){
				if(resolucionHomologacion.getDocumentos().getIdDocumento()!=null){
				Documentos docDB=em.find(Documentos.class, resolucionHomologacion.getDocumentos().getIdDocumento());
				if(!docDB.getNombreFisicoDoc().toLowerCase().equals(nombreDoc.toLowerCase()) || 
						!docDB.getTamanhoDoc().equals(String.valueOf(item.getFileSize()))){//significa que ubo algun cambio se envia los parametros del item
					idDoc=AdmDocAdjuntoFormController.editar(item.getFile(),Integer.valueOf(item.getFileSize()),item.getFileName(),item.getContentType(), docDB.getUbicacionFisica(), "GENERAR_RESOLUCION_EXC", docDB.getIdDocumento(), findResolucion(), usuarioLogueado.getIdUsuario(), resolucionHomologacion.getIdResolucion(),"Resolucion");
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
						
						resolucionHomologacion.setDocumentos(em.find(Documentos.class,idDoc));
						em.merge(resolucionHomologacion);
						em.flush();
						
							
					}else{
						statusMessages.add("Error al adjuntar el registro. Verifique");
						return null;
					}
				}//porque no hubo modificacion! nohace  nada! 
				
			}
		}
		if (inputFile == null && item != null) {//no habia ningun archivo y se carga

			
			
			if (resolucionHomologacion.getDocumentos() == null) {
				String direccionFisica = "";
				SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
				String anio = sdfSoloAnio.format(new Date());
				direccionFisica = "//SICCA//"+anio+"//OEE//"+idConfiguracionUo+"//"+concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos()+"//"+concurso.getIdConcurso();
				idDoc = AdmDocAdjuntoFormController.editar(item.getFile(),
						item.getFileSize(), item.getFileName(),
						item.getContentType(), direccionFisica,
						"GENERAR_RESOLUCION_EXC", null,
						findResolucion(), usuarioLogueado.getIdUsuario(),
						resolucionHomologacion.getIdResolucion(),
						"Resolucion");
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
					
					resolucionHomologacion.setDocumentos(em.find(Documentos.class,idDoc));
//					em.merge(resolucionHomologacion);
//					em.flush();
						
				}else{
					statusMessages.add("Error al adjuntar el registro. Verifique");
					return null;
				}

			}

		}
		em.clear();
		resolucionHomologacion.setFechaMod(new Date());
		resolucionHomologacion.setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
		em.merge(resolucionHomologacion);
		em.flush();
		String msg="updated";
			
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return msg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	 public void print() {
		 try {
			 if(idResolucionHomologacion==null){
				 statusMessages.add("Debe guardar para poder imprimir,Verifique");
				 return;
			 }
			 
			 if (resolucionHomologacion == null)
				 resolucionHomologacion= em.find(Resolucion.class,idResolucionHomologacion);
			 
			    SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
				Connection conn = JpaResourceBean.getConnection();
				  
				HashMap<String, Object> param = new HashMap<String, Object>();
			        param.put("usuario", usuarioLogueado.getCodigoUsuario());
			        param.put("idReso", idResolucionHomologacion);
			        ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			        param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
					param.put("path_logo", servletContext.getRealPath("/img/"));
				    param.put("REPORT_CONNECTION", conn);
				    param.put("fecha", fechaResolucion);
				    
			if (resolucionHomologacion != null)
				    param.put("nroAnio","RESOLUCION SFP N"+ resolucionHomologacion.getNroResolucion()+"/"+sdfSoloAnio.format(resolucionHomologacion.getFecha()));
				   
				   
				  
				 JasperReportUtils.respondPDF("CU57", param, false, conn, usuarioLogueado);
				
					
				
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	 }
	
	 public void abrirDoc(){
			AdmDocAdjuntoFormController.abrirDocumentoFromCU( resolucionHomologacion.getDocumentos().getIdDocumento(), usuarioLogueado.getIdUsuario());
			
	}
	
//	METODOS PRIVADOS
	 private void crearUploadItem(String fileName, int fileSize,
				String contentType, byte[] file) {
			item = new UploadItem(fileName, fileSize, contentType,
					AdminReclamoSugPortalFormController.crearFile(fileName, file));
			nombreDoc = item.getFileName();
		}
	public void documento() throws NamingException{
		//se le envia el id 60 en tipo doc resolucion
		String nombrePantalla="GENERAR_RESOLUCION_EXC";
		String direccionFisica="";
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio=sdfSoloAnio.format(new Date());
		direccionFisica="//SICCA//"+anio+"//OEE//"+idConfiguracionUo+"//"+concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos()+"//"+concurso.getIdConcurso();
		idDoc=AdmDocAdjuntoFormController.guardarDirecto(item, direccionFisica,nombrePantalla, findResolucion(),usuarioLogueado.getIdUsuario(),"Resolucion");
		
		
	}
	@SuppressWarnings("unchecked")
	private Long findResolucion(){
		List<DatosEspecificos> dEspecificos= em.createQuery("Select d from DatosEspecificos d " +
				" where lower(d.descripcion) like 'resolucion' ").getResultList();
		if(dEspecificos.isEmpty())
			return null;
		
		return dEspecificos.get(0).getIdDatosEspecificos();
	}
	
	@SuppressWarnings("unchecked")
	private void allPlantilla(){
		try {
			plResoHomoloList=em.createQuery(" Select p from PlantillaResolucion p " +
			" order by  p.descripcion").getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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




	public List<PlantillaResolucion> getPlResoHomoloList() {
		return plResoHomoloList;
	}




	public void setPlResoHomoloList(
			List<PlantillaResolucion> plResoHomoloList) {
		this.plResoHomoloList = plResoHomoloList;
	}




	public Resolucion getResolucionHomologacion() {
		return resolucionHomologacion;
	}




	public void setResolucionHomologacion(
			Resolucion resolucionHomologacion) {
		this.resolucionHomologacion = resolucionHomologacion;
	}

	

	public String getFechaResolucion() {
		return fechaResolucion;
	}

	public void setFechaResolucion(String fechaResolucion) {
		this.fechaResolucion = fechaResolucion;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public Long getIdResolucionHomologacion() {
		return idResolucionHomologacion;
	}

	public void setIdResolucionHomologacion(Long idResolucionHomologacion) {
		this.idResolucionHomologacion = idResolucionHomologacion;
	}

	public Long getIdConfiguracionUo() {
		return idConfiguracionUo;
	}

	public void setIdConfiguracionUo(Long idConfiguracionUo) {
		this.idConfiguracionUo = idConfiguracionUo;
	}



	

	public String getIdConcursoPuestoAgrExc() {
		return idConcursoPuestoAgrExc;
	}

	public void setIdConcursoPuestoAgrExc(String idConcursoPuestoAgrExc) {
		this.idConcursoPuestoAgrExc = idConcursoPuestoAgrExc;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public Long getIdConcurso411() {
		return idConcurso411;
	}

	public void setIdConcurso411(Long idConcurso411) {
		this.idConcurso411 = idConcurso411;
	}

	public String getFromCU() {
		return fromCU;
	}

	public void setFromCU(String fromCU) {
		this.fromCU = fromCU;
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

	public boolean isModoUpdate() {
		return modoUpdate;
	}

	public void setModoUpdate(boolean modoUpdate) {
		this.modoUpdate = modoUpdate;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	
	

	
	
	
}
