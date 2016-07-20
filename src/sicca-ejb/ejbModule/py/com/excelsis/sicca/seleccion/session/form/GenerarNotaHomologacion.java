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
import py.com.excelsis.sicca.entity.NotaHomologacion;
import py.com.excelsis.sicca.entity.PlantillaNotaHomologacion;
import py.com.excelsis.sicca.entity.PlantillaResolucion;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.Tags;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.NotaHomologacionHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("generarNotaHomologacion")
public class GenerarNotaHomologacion implements Serializable{
	
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
	NotaHomologacionHome notaHomologacionHome;
	
	private NotaHomologacion notaHomologacion;
	private Long idConcurso;
	private Long idConfiguracionUo;//OEE
	private String idConcursoPuestoAgr;
	private Long idNotaHomologacion;
	private List<PlantillaNotaHomologacion>  notaHomologacionList;
	private PlantillaNotaHomologacion plantillaNotaHomoSelected;
	private String fechaNota;
	private String anio;
	private Concurso concurso;
	private Long idConcurso411;
	
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
	private SeguridadUtilFormController seguridadUtilFormController;
	private boolean primeraEntrada=true;

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
			concurso=em.find(Concurso.class, idConcurso);
			idConfiguracionUo=concurso.getConfiguracionUoCab().getIdConfiguracionUo();
			notaHomologacion = new NotaHomologacion();
			validarOee(concurso);
			nombreDoc="";
			if(idNotaHomologacion!=null){
				notaHomologacion= em.find(NotaHomologacion.class,idNotaHomologacion);
				if(notaHomologacion.getDocumentos()!=null)
					nombreDoc=notaHomologacion.getDocumentos().getNombreFisicoDoc();
			}else
				notaHomologacion.setFecha(new Date());
				
		}
		
		
		notaHomologacionHome.setInstance(notaHomologacion);
		allPlantilla();
	}
	
	public void findPlantilla(Long idPlantilla){
		plantillaNotaHomoSelected=em.find(PlantillaNotaHomologacion.class, idPlantilla);
		if(plantillaNotaHomoSelected.getReferencia()!=null)
			notaHomologacion.setReferencia(eliminarTags(plantillaNotaHomoSelected.getReferencia()));
		if(plantillaNotaHomoSelected.getTitulo1()!=null)
			notaHomologacion.setTitulo1(eliminarTags(plantillaNotaHomoSelected.getTitulo1()));
		if(plantillaNotaHomoSelected.getTitulo2()!=null)
			notaHomologacion.setTitulo2(eliminarTags(plantillaNotaHomoSelected.getTitulo2()));
		if(plantillaNotaHomoSelected.getCuerpo()!=null)
			notaHomologacion.setCuerpo(eliminarTags(plantillaNotaHomoSelected.getCuerpo()));
		if(plantillaNotaHomoSelected.getFirma()!=null)
			notaHomologacion.setFirma(eliminarTags(plantillaNotaHomoSelected.getFirma()));
		if(plantillaNotaHomoSelected.getCargo()!=null)
			notaHomologacion.setCargo(eliminarTags(plantillaNotaHomoSelected.getCargo()));
		if(plantillaNotaHomoSelected.getInstitucionFirma()!=null)
			notaHomologacion.setInstitucionFirma(eliminarTags(plantillaNotaHomoSelected.getInstitucionFirma()));
		if(plantillaNotaHomoSelected.getA()!=null)
			notaHomologacion.setA(eliminarTags(plantillaNotaHomoSelected.getA()));
		if(plantillaNotaHomoSelected.getDon()!=null)
			notaHomologacion.setDon(eliminarTags(plantillaNotaHomoSelected.getDon()));
		if(plantillaNotaHomoSelected.getInstitucion()!=null)
			notaHomologacion.setInstitucion(eliminarTags(plantillaNotaHomoSelected.getInstitucion()));
		if(plantillaNotaHomoSelected.getItemFinal()!=null)
			notaHomologacion.setItemFinal(eliminarTags(plantillaNotaHomoSelected.getItemFinal()));
		
		notaHomologacion.setPlantillaNotaHomologacion(plantillaNotaHomoSelected);
		
	}
	public void findFecha(){
		String mes="";
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		
		switch (notaHomologacion.getFecha().getMonth()) {
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
			
		default:
			mes="<No definido>";
			break;
		}
		fechaNota=notaHomologacion.getLugar()+", "+notaHomologacion.getFecha().getDate()+" de "+mes+" de "+sdfSoloAnio.format(notaHomologacion.getFecha());
		anio="/"+sdfSoloAnio.format(notaHomologacion.getFecha());
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
			if(notaHomologacion.getFecha().after(new Date())){
				statusMessages.add(Severity.ERROR,"La Fecha de Nota no puede ser mayor a la fecha actual, verifique");
				return null;
			}
			if( !general.isFechaValida(notaHomologacion.getFecha())){
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
				doc.setIdTabla(notaHomologacion.getIdNotaHomologacion());
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				doc.setFechaMod(new Date());
				notaHomologacion.setDocumentos(em.find(Documentos.class,idDoc));
//				em.merge(notaHomologacion);
//				em.flush();
			}else{
				statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
				return  null;
			}
			
		}
		notaHomologacion.setFechaAlta(new Date());
		notaHomologacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		//notaHomologacionHome.setInstance(notaHomologacion);
		em.persist(notaHomologacion);
		em.flush();
		String msg="persisted";
		if(idConcursoPuestoAgr!=null){
			String[] idsAgs=idConcursoPuestoAgr.split(",");
			for (int i = 0; i < idsAgs.length; i++) {
				Long id=Long.parseLong(idsAgs[i]);
				if(id!=null){
					ConcursoPuestoAgr agr= em.find(ConcursoPuestoAgr.class, id);
					agr.setNotaHomologacion(notaHomologacion);
				//	agr.setEstado(5);
					agr.setFechaMod(new Date());
					agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(agr);
					em.flush();
				}
			}
		}
		
		idNotaHomologacion=notaHomologacion.getIdNotaHomologacion();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return msg;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add("Se ha producido un error al intentar guardar el resgistro");
			return null;
		}
	}
	private boolean chekValores(){
		
		if(notaHomologacion.getNroNota().intValue()<=0){
			statusMessages.add(Severity.ERROR,"El valor del Nro. de Nota  debe ser mayor a cero");
			return false;
		}
		
		return true;
		
	}
	@SuppressWarnings("unchecked")
	public String update(){
		try {
			if(!chekValores())
				return null;
			if(notaHomologacion.getFecha().after(new Date())){
				statusMessages.add(Severity.ERROR,"La Fecha de Nota no puede ser mayor a la fecha actual, verifique");
				return null;
			}
		if( !general.isFechaValida(notaHomologacion.getFecha())){
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
		if(notaHomologacion.getDocumentos()!=null && item!=null){//habia un archivo y se modifica
			
			if(notaHomologacion.getDocumentos().getIdDocumento()!=null){
				Documentos docDB=em.find(Documentos.class, notaHomologacion.getDocumentos().getIdDocumento());
				if(!docDB.getNombreFisicoDoc().toLowerCase().equals(nombreDoc.toLowerCase()) || 
						!docDB.getTamanhoDoc().equals(String.valueOf(item.getFileSize()))){//significa que ubo algun cambio se envia los parametros del item
					idDoc=AdmDocAdjuntoFormController.editar(item.getFile(),Integer.valueOf(item.getFileSize()),item.getFileName(),item.getContentType(), docDB.getUbicacionFisica(), "notaHomologacion_edit", docDB.getIdDocumento(), findResolucion(), usuarioLogueado.getIdUsuario(), notaHomologacion.getIdNotaHomologacion(),"NotaHomologacion");
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
						
						notaHomologacion.setDocumentos(em.find(Documentos.class,idDoc));
							
					}else{
						statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
						return null;
					}
				}//porque no hubo modificacion! nohace  nada! 
				
			}
		}
		if (inputFile == null && item != null) {//no habia ningun archivo y se carga
			if (notaHomologacion.getDocumentos() == null) {
				String direccionFisica = "";
				SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
				String anio = sdfSoloAnio.format(new Date());
				direccionFisica = "//SICCA//"+anio+"//OEE//"+idConfiguracionUo+"//"+concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos()+"//"+concurso.getIdConcurso();
				idDoc = AdmDocAdjuntoFormController.editar(item.getFile(),
						item.getFileSize(), item.getFileName(),
						item.getContentType(), direccionFisica,
						"notaHomologacion_edit", null,
						findResolucion(), usuarioLogueado.getIdUsuario(),
						notaHomologacion.getIdNotaHomologacion(),
						"NotaHomologacion");
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
					
					notaHomologacion.setDocumentos(em.find(Documentos.class,idDoc));
						
				}else{
					statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
					return null;
				}

			}

		}
		notaHomologacion.setFechaMod(new Date());
		notaHomologacion.setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
		em.merge(notaHomologacion);
		em.flush();
		String msg="updated";
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return msg;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add("Se ha producido un error al intentar actualizar el resgistro");
			return null;
		}
	}
	 public void print() {
		 try {
			 if(idNotaHomologacion==null)
			 {
				 statusMessages.add("Debe guardar para poder imprimir,Verifique");
				 return;
			 }
			 
			 if (notaHomologacion == null)
			 	notaHomologacion= em.find(NotaHomologacion.class,idNotaHomologacion);
			 
			    SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
				Connection conn = JpaResourceBean.getConnection();
				  
				HashMap<String, Object> param = new HashMap<String, Object>();
			        param.put("usuario", usuarioLogueado.getCodigoUsuario());
			        param.put("idNota", idNotaHomologacion);
			        ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			        param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
					param.put("path_logo", servletContext.getRealPath("/img/"));
				    param.put("REPORT_CONNECTION", conn);
				    param.put("fecha", fechaNota);
				    
				if (notaHomologacion != null)
				    param.put("nroAnio", notaHomologacion.getNroNota()+"/"+sdfSoloAnio.format(notaHomologacion.getFecha()));
				   
				   
				  
				 JasperReportUtils.respondPDF("CU56", param, false, conn, usuarioLogueado);
				
					
				
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	 }
		
	
	public void abrirDoc(){
		AdmDocAdjuntoFormController.abrirDocumentoFromCU( notaHomologacion.getDocumentos().getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}
	
	
	public void documento() throws NamingException{
		//se le envia el id 60 en tipo doc resolucion
		String nombrePantalla="notaHomologacion_edit";
		String direccionFisica="";
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio=sdfSoloAnio.format(new Date());
		direccionFisica="//SICCA//"+anio+"//OEE//"+idConfiguracionUo+"//"+concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos()+"//"+concurso.getIdConcurso();
		idDoc=AdmDocAdjuntoFormController.guardarDirecto(item, direccionFisica,nombrePantalla,findResolucion(),usuarioLogueado.getIdUsuario(),"NotaHomologacion");
		}
	
	
//	METODOS PRIVADOS 
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
		notaHomologacionList=em.createQuery(" Select p from PlantillaNotaHomologacion p " +
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

	public NotaHomologacion getNotaHomologacion() {
		return notaHomologacion;
	}

	public void setNotaHomologacion(NotaHomologacion notaHomologacion) {
		this.notaHomologacion = notaHomologacion;
	}

	public Long getIdNotaHomologacion() {
		return idNotaHomologacion;
	}

	public void setIdNotaHomologacion(Long idNotaHomologacion) {
		this.idNotaHomologacion = idNotaHomologacion;
	}

	public List<PlantillaNotaHomologacion> getNotaHomologacionList() {
		return notaHomologacionList;
	}

	public void setNotaHomologacionList(
			List<PlantillaNotaHomologacion> notaHomologacionList) {
		this.notaHomologacionList = notaHomologacionList;
	}

	public String getFechaNota() {
		return fechaNota;
	}

	public void setFechaNota(String fechaNota) {
		this.fechaNota = fechaNota;
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
