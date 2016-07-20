package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import enums.Estado;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ExperienciaLaboralHome;
import py.com.excelsis.sicca.session.ExperienciaLaboralList;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("tab3ExperienciaLaboralFormController")
public class Tab3ExperienciaLaboralFormController implements Serializable{

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
	ExperienciaLaboralHome experienciaLaboralHome;
	@In(create = true)
	ExperienciaLaboralList experienciaLaboralList;
	
	@In(create=true,required=false)
	Tab6VistaPreliminarFormController tab6VistaPreliminarFormController;
	@In(create=true,required=false)
	Tab4AdjuntarDocumentosFormController tab4AdjuntarDocumentosFormController;
	
	
	private ExperienciaLaboral experienciaLaboral;
	private Long idTipoDocumento;
	private Persona persona;
	private General general;
	private Long idPais;
	private Long idTipoVinculacion;
	private Long idSector;
	private boolean esOtrovincilo;
	private boolean esOtroSector;
	private Long idPersona;
	private Long idTvin;
	private String experiencia;
	
	
	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item ;
	private String nombreDoc;
	private File inputFile ;
	private String nombrePantalla;
	private Long idDoc;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	
	
	private List<ExperienciaLaboral> listDetails = new ArrayList<ExperienciaLaboral>();

	public void init(){
		try {
			idPersona=usuarioLogueado.getPersona().getIdPersona();
			experienciaLaboral = new ExperienciaLaboral();
			experienciaLaboralList.getPersona().setIdPersona(usuarioLogueado.getPersona().getIdPersona());
			persona= em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
			experienciaLaboralList.getExperienciaLaboral().setActivo(Estado.ACTIVO.getValor());
			experienciaLaboralList.setMaxResults(null);
			listDetails = new ArrayList<ExperienciaLaboral>();
			listDetails =
				em.createQuery(" select e from ExperienciaLaboral e " + " where e.persona.idPersona="
					+ persona.getIdPersona() + " and e.activo = true order by e.fechaDesde DESC, e.fechaHasta DESC").getResultList();
			idPais=idParaguay();
			experiencia = null;
			if(experienciaLaboral.getGeneral() != null && experienciaLaboral.getGeneral())
				experiencia = "G";
			if(experienciaLaboral.getEspecifica() != null && experienciaLaboral.getEspecifica())
				experiencia = "E";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String save(){
		String result = null;
		try {
			Persona persona = em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
			experienciaLaboral.setPersona(persona);
			experienciaLaboral.setActivo(Estado.ACTIVO.getValor());
			if(experiencia != null){
				if(experiencia.equalsIgnoreCase("G")){
					experienciaLaboral.setGeneral(true);
					experienciaLaboral.setEspecifica(false);
				}
				if(experiencia.equalsIgnoreCase("E")){
					experienciaLaboral.setGeneral(false);
					experienciaLaboral.setEspecifica(true);
				}	
			}
			
			experienciaLaboralHome.setInstance(experienciaLaboral);
			
			result = experienciaLaboralHome.save();
			experienciaLaboralHome.clearInstance();

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null){
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SICCAAppHelper.getBundleMessage("msg_operacion_exitosa"));
			}
		}
		return result;
	}
	
	public void addRow(){
		
		
		
		if(!toDetailOk()){
			return;
		}
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return ;
			}
			
		}
		
		if(item!=null && idTipoDocumento==null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe Seleccionar El tipo de Documento. Verifique");
			return ;
		}
		if(item==null && idTipoDocumento!=null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe Adjuntar un Documento. Verifique");
			return ;
		}
		
		
		/*for(ExperienciaLaboral ex : listDetails){
			if(ex.getEmpresa().trim().toLowerCase().equals(experienciaLaboral.getEmpresa().trim().toLowerCase())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_en_detalle"));
				return ;
			}
		}*/
		experienciaLaboral.setPais(em.find(Pais.class, idPais));
		experienciaLaboral.setDatosEspecificosSector(em.find(DatosEspecificos.class, idSector));
		experienciaLaboral.setDatosEspecificosTipoVinculo(em.find(DatosEspecificos.class, idTipoVinculacion));
		/**
		 * Para el Caso de documento adjuntos
		 * */
		if(item!=null){
			try {
				documento();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			if(idDoc!=null){
				
				if(idDoc.longValue()==-8){
					statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return ;					
				}
				if(idDoc.longValue()==-7){
					statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return ;	
				}
				if(idDoc.longValue()==-6){
					statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
					return ;	
				}
				Documentos doc= em.find(Documentos.class,idDoc);
				doc.setPersona(em.find(Persona.class, idPersona));
				doc.setDatosEspecificos(em.find(DatosEspecificos.class, idTipoDocumento));
				em.merge(doc);
				em.flush();
				experienciaLaboral.setDocumentos(doc);
				
				
					
			}else{
				statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
				return ;
			}
			
		}
		save();
		displaySuccesfullMessage();
		listDetails.add(experienciaLaboral);
		if(experienciaLaboral.getDocumentos()!=null && idDoc!=null){
			Documentos doc= em.find(Documentos.class,idDoc);
			doc.setIdTabla(experienciaLaboral.getIdExperienciaLab());
			doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
			doc.setFechaMod(new Date());
			em.merge(doc);
			em.flush();
		}
		
		clearDataDetail();
		experienciaLaboralHome.clearInstance();
		tab6VistaPreliminarFormController.init();
		tab4AdjuntarDocumentosFormController.init();
		
	}
	
	public void removeRow(int pos){
		statusMessages.clear();
		listDetails.get(pos).setActivo(Estado.INACTIVO.getValor());
		ExperienciaLaboral el= em.find(ExperienciaLaboral.class, listDetails.get(pos).getIdExperienciaLab());
		if(el.getDocumentos()!=null){
			Documentos doc= em.find(Documentos.class, el.getDocumentos().getIdDocumento());
			doc.setActivo(false);
			em.merge(doc);
			em.flush();
		}
		experienciaLaboralHome.setInstance(listDetails.get(pos));
		experienciaLaboralHome.save();
		displaySuccesfullMessage();
		listDetails.remove(pos);
	}
	
	public void selectToEdit(int pos){
		try {
			experienciaLaboral = listDetails.get(pos);
			experienciaLaboral.setPosicion(pos);
			experiencia = null;
			if(experienciaLaboral.getEspecifica() != null && experienciaLaboral.getEspecifica())
				experiencia = "E";
			if(experienciaLaboral.getGeneral() != null && experienciaLaboral.getGeneral())
				experiencia = "G";
			if(experienciaLaboral.getPais()!=null)
				idPais=experienciaLaboral.getPais().getIdPais();
			if(experienciaLaboral.getDatosEspecificosTipoVinculo()!=null){
				idTipoVinculacion=experienciaLaboral.getDatosEspecificosTipoVinculo().getIdDatosEspecificos();
				if(experienciaLaboral.getDatosEspecificosTipoVinculo().getDescripcion().toLowerCase().equals("otros")||experienciaLaboral.getDatosEspecificosTipoVinculo().getDescripcion().toLowerCase().equals("otro")){
					 esOtrovincilo=true;
				}else
					esOtrovincilo=false;
			}
				
			if(experienciaLaboral.getDatosEspecificosSector()!=null){
				idSector=experienciaLaboral.getDatosEspecificosSector().getIdDatosEspecificos();
				 if(experienciaLaboral.getDatosEspecificosSector().getDescripcion().toLowerCase().equals("otros")||experienciaLaboral.getDatosEspecificosSector().getDescripcion().toLowerCase().equals("otro")){
					 esOtroSector=true;
				 }
				 else
					 esOtroSector=false;
			}
				
			if(experienciaLaboral.getDocumentos()!=null){
				Documentos doc= em.find(Documentos.class, experienciaLaboral.getDocumentos().getIdDocumento());
				String url=doc.getUbicacionFisica();
				if (url.contains("\\")) {
					url=url.replace("\\",  System.getProperty("file.separator"));
					
				}
				if (url.contains("//")) {
					url=url.replace("//",  System.getProperty("file.separator"));
					
				}
				String separador = System.getProperty("file.separator");
				if (!url.endsWith(separador))
					url = url+ System.getProperty("file.separator");
				
				url+=doc.getNombreFisicoDoc();

				 inputFile = new File(url);
				 idTipoDocumento=doc.getDatosEspecificos().getIdDatosEspecificos();
				 nombreDoc=doc.getNombreFisicoDoc();
				
			}else
				inputFile=null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateOnList(){
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return;
			}
			
			
		}

		if(!toDetailOk()){
			return;
		}
		/*for(ExperienciaLaboral ex : listDetails){
			if(ex.getIdExperienciaLab()!=null && ex.getIdExperienciaLab().intValue()!=experienciaLaboral.getIdExperienciaLab().intValue()){
				if(ex.getEmpresa().trim().toLowerCase().equals(experienciaLaboral.getEmpresa().trim().toLowerCase())){

					statusMessages.clear();
					statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_en_detalle"));
					return ;
				}
			}else if(ex.getIdExperienciaLab()==null){
				if(ex.getEmpresa().trim().toLowerCase().equals(experienciaLaboral.getEmpresa().trim().toLowerCase())){

					statusMessages.clear();
					statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_en_detalle"));
					return ;
				}
			}
			
			
		}*/
		if(experiencia != null){
			if(experiencia.equalsIgnoreCase("G")){
				experienciaLaboral.setGeneral(true);
				experienciaLaboral.setEspecifica(false);
			}
			if(experiencia.equalsIgnoreCase("E")){
				experienciaLaboral.setGeneral(false);
				experienciaLaboral.setEspecifica(true);
			}	
		}
		listDetails.get(experienciaLaboral.getPosicion()).setOtroSector(experienciaLaboral.getOtroSector());
		listDetails.get(experienciaLaboral.getPosicion()).setOtroVincu(experienciaLaboral.getOtroVincu());
		listDetails.get(experienciaLaboral.getPosicion()).setPuestoCargo(experienciaLaboral.getPuestoCargo());
		listDetails.get(experienciaLaboral.getPosicion()).setDatosEspecificosSector(em.find(DatosEspecificos.class, idSector));
		listDetails.get(experienciaLaboral.getPosicion()).setDatosEspecificosTipoVinculo(em.find(DatosEspecificos.class, idTipoVinculacion));
		listDetails.get(experienciaLaboral.getPosicion()).setEmpresa(experienciaLaboral.getEmpresa().trim());
		listDetails.get(experienciaLaboral.getPosicion()).setFechaDesde(experienciaLaboral.getFechaDesde());
		listDetails.get(experienciaLaboral.getPosicion()).setFechaHasta(experienciaLaboral.getFechaHasta());
		listDetails.get(experienciaLaboral.getPosicion()).setFuncionesRealizadas(experienciaLaboral.getFuncionesRealizadas().trim());
		listDetails.get(experienciaLaboral.getPosicion()).setReferenciasLaborales(experienciaLaboral.getReferenciasLaborales().trim());
		listDetails.get(experienciaLaboral.getPosicion()).setEspecifica(experienciaLaboral.getEspecifica());
		listDetails.get(experienciaLaboral.getPosicion()).setGeneral(experienciaLaboral.getGeneral());
		experienciaLaboralHome.setInstance(experienciaLaboral);
		/**
		 *PARA EL CASO DE DOCUMENTO ADJUNTO 
		 *
		 **/
		Long idDoc=null;
		if(inputFile!=null && item!=null){//habia un archivo y se modifica
			
			if(experienciaLaboral.getDocumentos()!=null){
				Documentos docDB=em.find(Documentos.class, experienciaLaboral.getDocumentos().getIdDocumento());
				if(!docDB.getNombreFisicoDoc().toLowerCase().equals(nombreDoc.toLowerCase()) || 
						!docDB.getTamanhoDoc().equals(String.valueOf(item.getFileSize()))){//significa que ubo algun cambio se envia los parametros del item
					idDoc=AdmDocAdjuntoFormController.editar(item.getFile(),Integer.valueOf(item.getFileSize()),item.getFileName(),item.getContentType(), docDB.getUbicacionFisica(), "experienciaLaboral_edit", docDB.getIdDocumento(), idTipoDocumento, usuarioLogueado.getIdUsuario(), experienciaLaboral.getIdExperienciaLab(),"ExperienciaLaboral");
					if(idDoc!=null){
						
						if(idDoc.longValue()==-8){
							statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
							return ;					
						}
						if(idDoc.longValue()==-7){
							statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
							return ;	
						}
						if(idDoc.longValue()==-6){
							statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
							return ;	
						}
						Documentos doc= em.find(Documentos.class,idDoc);
						doc.setPersona(em.find(Persona.class, idPersona));
						doc.setDatosEspecificos(em.find(DatosEspecificos.class, idTipoDocumento));
						em.merge(doc);
						em.flush();
						experienciaLaboral.setDocumentos(doc);
						
							
					}else{
						statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
						return ;
					}
				}//porque no hubo modificacion! nohace  nada! 
				
			}
		}
		if (inputFile == null && item != null) {//no habia ningun archivo y se carga
			if (experienciaLaboral.getDocumentos() == null) {
				if(idTipoDocumento==null){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,"Debe Seleccionar El tipo de Documento. Verifique");
					return ;
				}
				String direccionFisica = "";
				SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
				String anio = sdfSoloAnio.format(new Date());
				direccionFisica = "\\SICCA\\"  + "USUARIO_PORTAL\\"
						+ persona.getDocumentoIdentidad() + "_"
						+ persona.getIdPersona();
				idDoc = AdmDocAdjuntoFormController.editar(item.getFile(),
						item.getFileSize(), item.getFileName(),
						item.getContentType(), direccionFisica,
						"experienciaLaboral_edit", null,
						idTipoDocumento, usuarioLogueado.getIdUsuario(),
						experienciaLaboral.getIdExperienciaLab(),
						"ExperienciaLaboral");
				if(idDoc!=null){
					
					if(idDoc.longValue()==-8){
						statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return ;					
					}
					if(idDoc.longValue()==-7){
						statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return ;	
					}
					if(idDoc.longValue()==-6){
						statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
						return ;	
					}
					Documentos doc= em.find(Documentos.class,idDoc);
					doc.setPersona(em.find(Persona.class, idPersona));
					doc.setDatosEspecificos(em.find(DatosEspecificos.class, idTipoDocumento));

					em.merge(doc);
					em.flush();
					experienciaLaboral.setDocumentos(doc);
						
				}else{
					statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
					return ;
				}

			}

		}

		
		if (inputFile != null && item == null) {//no se modifica el archivo
			if(idTipoDocumento==null){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"Debe Seleccionar El tipo de Documento. Verifique");
				return ;
			}
			Documentos doc= em.find(Documentos.class,experienciaLaboral.getDocumentos().getIdDocumento());
			doc.setDatosEspecificos(em.find(DatosEspecificos.class, idTipoDocumento));
			em.merge(doc);
			em.flush();
			experienciaLaboral.setDocumentos(doc);
			
		}
		experienciaLaboralHome.save();
		displaySuccesfullMessage();
		clearDataDetail();
		experienciaLaboralHome.clearInstance();
		tab4AdjuntarDocumentosFormController.init();
		tab6VistaPreliminarFormController.init();
	}
	
	public void documento() throws NamingException{
		nombrePantalla="experienciaLaboral_edit";
		String direccionFisica="";
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio=sdfSoloAnio.format(new Date());
		direccionFisica="\\SICCA\\"+"USUARIO_PORTAL\\"+persona.getDocumentoIdentidad()+"_"+persona.getIdPersona();
		idDoc=AdmDocAdjuntoFormController.guardarDirecto(item, direccionFisica,nombrePantalla, idTipoDocumento,usuarioLogueado.getIdUsuario(),"ExperienciaLaboral");
		
		
	}
	public void clearDataDetail(){
		experienciaLaboral = new ExperienciaLaboral();
		nombreDoc=null;
		idTipoDocumento=null;
		item=null;
		idPais=idParaguay();
		idTipoDocumento=null;
		idSector=null;
		esOtroSector=false;
		esOtrovincilo=false;
		experiencia = null;
		idTipoVinculacion=null;
		
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
	
	 public void otipoVinculo(){
		 if(idTipoVinculacion!=null){
			 DatosEspecificos tv= em.find(DatosEspecificos.class,idTipoVinculacion);
			 if(tv.getDescripcion().toLowerCase().equals("otros")||tv.getDescripcion().toLowerCase().equals("otro"))
				 esOtrovincilo=true;
			 else{
				 esOtrovincilo=false;
			
			 }
			 
				 
		 }
	 }
		
	 public void oSector(){
		 if(idSector!=null){
			 DatosEspecificos tv= em.find(DatosEspecificos.class,idSector);
			 if(tv.getDescripcion().toLowerCase().equals("otros")||tv.getDescripcion().toLowerCase().equals("otro"))
				 esOtroSector=true;
			 else{
				 esOtroSector=false;
				
			 }
			 
				 
		 }
	 }
	 public void changeNameDoc(){
			nombreDoc=null;
	}
	 
	 
	 
//	METODOS PRIVADOS
		@SuppressWarnings("unchecked")
		private Long idParaguay(){
				List<Pais> p= em.createQuery(" Select p from Pais p" +
						" where lower(p.descripcion) like 'paraguay'").getResultList();
				if(!p.isEmpty())
					return p.get(0).getIdPais();
				
				return null;
		}
	 
	@SuppressWarnings("unchecked")
	private boolean toDetailOk(){
		Date fechaActual = new Date();
		if(esOtroSector && (experienciaLaboral.getOtroSector()==null || experienciaLaboral.getOtroSector().trim().equals("")))
		{	
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe ingresar El campo Otro Sector");
			return false;
		}
		
		if(esOtrovincilo && (experienciaLaboral.getOtroVincu()==null || experienciaLaboral.getOtroVincu().trim().equals("")))
		{
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe ingresar El campo Otro Tipo Vinculo");
			return false;
		}
		if(experienciaLaboral.getEmpresa() == null || experienciaLaboral.getEmpresa().isEmpty()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_sin_empresa"));
			return false;
		}
		if(experienciaLaboral.getFechaDesde() == null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_sin_fecha_desde"));
			return false;
		}
		
		
		if(experienciaLaboral.getFechaHasta() != null
				&& experienciaLaboral.getFechaHasta().before(experienciaLaboral.getFechaDesde())){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_fecha_hasta_incorrecta"));
			return false;
		}
		if(experienciaLaboral.getFechaDesde().after(fechaActual)){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_fecha_desde_incorrecta"));
			return false;
		}
		if(experienciaLaboral.getFechaHasta() != null && experienciaLaboral.getFechaHasta().after(fechaActual)){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_fecha_hasta_incorrecta_dos"));
			return false;
		}
		if(experienciaLaboral.getFechaDesde()!=null && !general.isFechaValida(experienciaLaboral.getFechaDesde())){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha Desde inválida");
			return false;
		}
		if(experienciaLaboral.getFechaHasta() != null && !general.isFechaValida(experienciaLaboral.getFechaHasta())){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha Hasta inválida");
			return false;
		}
		if(idPais==null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe especificar el Pais. Verifique");
			return false;
		}
		if(experienciaLaboral.getPuestoCargo()==null || experienciaLaboral.getPuestoCargo().trim().equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe ingresar un Puesto/Cargo");
			return false;
		}

		if(idTipoVinculacion==null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe especificar el Tipo de Vinculación. Verifique");
			return false;
		}
		if(esOtrovincilo&& (experienciaLaboral.getOtroVincu()==null ||experienciaLaboral.getOtroVincu().trim().equals("")))
		{
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe especificar Otro Tipo de Vinculación. Verifique");
			return false;
		}
		if(idSector==null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe especificar el Item Sector. Verifique");
			return false;
		}
		if(esOtroSector&& (experienciaLaboral.getOtroSector()==null || experienciaLaboral.getOtroSector().trim().equals(""))){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe especificar Otro Sector. Verifique");
			return false;
		}
		
		
		if(experienciaLaboral.getFuncionesRealizadas() == null || experienciaLaboral.getFuncionesRealizadas().trim().isEmpty()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_sin_funcion"));
			return false;
		}
		
		
		if(experienciaLaboral.getReferenciasLaborales()==null || experienciaLaboral.getReferenciasLaborales().trim().equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe ingresar las Referencias Laborales ");
			return false;
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
						return false;
					}
				}
				/**
				 * FIN
				 * */
		}
		
		return true;
	}
	public void abrirDoc(int index){
		
		ExperienciaLaboral e= listDetails.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumentos().getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}

	
	
	
	private void displaySuccesfullMessage(){
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SICCAAppHelper.getBundleMessage("msg_operacion_exitosa"));
	}
	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		
		nombreDoc = item.getFileName();
	}
	
	
	
//	GETTERS Y SETTERS
	public ExperienciaLaboral getExperienciaLaboral() {
		return experienciaLaboral;
	}

	public void setExperienciaLaboral(ExperienciaLaboral experienciaLaboral) {
		this.experienciaLaboral = experienciaLaboral;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public List<ExperienciaLaboral> getListDetails() {
		return listDetails;
	}

	public void setListDetails(List<ExperienciaLaboral> listDetails) {
		this.listDetails = listDetails;
	}

	public UploadItem getItem() {
		return item;
	}

	public void setItem(UploadItem item) {
		this.item = item;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdTipoVinculacion() {
		return idTipoVinculacion;
	}

	public void setIdTipoVinculacion(Long idTipoVinculacion) {
		this.idTipoVinculacion = idTipoVinculacion;
	}

	public Long getIdSector() {
		return idSector;
	}

	public void setIdSector(Long idSector) {
		this.idSector = idSector;
	}

	

	public boolean isEsOtrovincilo() {
		return esOtrovincilo;
	}

	public void setEsOtrovincilo(boolean esOtrovincilo) {
		this.esOtrovincilo = esOtrovincilo;
	}

	public boolean isEsOtroSector() {
		return esOtroSector;
	}

	public void setEsOtroSector(boolean esOtroSector) {
		this.esOtroSector = esOtroSector;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getIdTvin() {
		return idTvin;
	}

	public void setIdTvin(Long idTvin) {
		this.idTvin = idTvin;
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

	public String getExperiencia() {
		return experiencia;
	}

	public void setExperiencia(String experiencia) {
		this.experiencia = experiencia;
	}
	
	
	
}
