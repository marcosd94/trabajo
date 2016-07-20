package py.com.excelsis.sicca.legajo.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.ExperienciaLaboralLegajo;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.legajo.session.ExperienciaLaboralLegajoHome;
import py.com.excelsis.sicca.legajo.session.ExperienciaLaboralLegajoList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import enums.Estado;

@Scope(ScopeType.CONVERSATION)
@Name("experienciaLaboralLegajoFC")
public class ExperienciaLaboralLegajoFC implements Serializable{

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
	ExperienciaLaboralLegajoHome experienciaLaboralLegajoHome;
	@In(create = true)
	ExperienciaLaboralLegajoList experienciaLaboralLegajoList;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;


	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;
	private ExperienciaLaboralLegajo experienciaLaboral;
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
	private String texto;
	
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
	
	
	private List<ExperienciaLaboralLegajo> listDetails = new ArrayList<ExperienciaLaboralLegajo>();

	public void init(){
		try {
			persona=em.find(Persona.class, idPersona);
			experienciaLaboral = new ExperienciaLaboralLegajo();
			experienciaLaboralLegajoList.getPersona().setIdPersona(idPersona);
			experienciaLaboralLegajoList.getExperienciaLaboral().setActivo(Estado.ACTIVO.getValor());
			experienciaLaboralLegajoList.setMaxResults(null);
			listDetails = new ArrayList<ExperienciaLaboralLegajo>(experienciaLaboralLegajoList.getResultList());
			idPais=General.getIdParaguay();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String getTipoVinculacion(ExperienciaLaboralLegajo exp){
		/*System.out.println("exp"+exp);
		System.out.println("DatosEspecificosTipoVinculo"+exp.getDatosEspecificosTipoVinculo());
		System.out.println("descripcion"+exp.getDatosEspecificosTipoVinculo().getDescripcion());*/
		if(exp != null && exp.getDatosEspecificosTipoVinculo()	!= null
				&& exp.getDatosEspecificosTipoVinculo().getDescripcion() != null){
			/*System.out.println("exp2"+exp);
			System.out.println("DatosEspecificosTipoVinculo2"+exp.getDatosEspecificosTipoVinculo());
			System.out.println("descripcion2"+exp.getDatosEspecificosTipoVinculo().getDescripcion());*/
			if( exp.getDatosEspecificosTipoVinculo().getDescripcion().equals("OTROS"))
					return exp.getOtroVincu();
			else 
				return exp.getDatosEspecificosTipoVinculo().getDescripcion();
		}else
			return "";
		
	}
	
	
	public String getSector(ExperienciaLaboralLegajo exp){
		/*System.out.println("exp"+exp);
		System.out.println("DatosEspecificosTipoVinculo"+exp.getDatosEspecificosTipoVinculo());
		System.out.println("descripcion"+exp.getDatosEspecificosTipoVinculo().getDescripcion());*/
		if(exp != null && exp.getDatosEspecificosSector() != null
				&& exp.getDatosEspecificosSector().getDescripcion() != null){
			/*System.out.println("exp2"+exp);
			System.out.println("DatosEspecificosTipoVinculo2"+exp.getDatosEspecificosTipoVinculo());
			System.out.println("descripcion2"+exp.getDatosEspecificosTipoVinculo().getDescripcion());*/
			if( exp.getDatosEspecificosSector().getDescripcion().equals("OTROS"))
					return exp.getOtroSector();
			else 
				return exp.getDatosEspecificosSector().getDescripcion();
		}else
			return "";
		
	}
	
	
	public String save(){
		String result = null;
		try {
			Persona persona = em.find(Persona.class,idPersona );
			experienciaLaboral.setPersona(persona);
			experienciaLaboral.setActivo(Estado.ACTIVO.getValor());
			experienciaLaboralLegajoHome.setInstance(experienciaLaboral);
			result = experienciaLaboralLegajoHome.save();
			experienciaLaboralLegajoHome.clearInstance();

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
		
		if(uploadedFile!=null && idTipoDocumento==null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe Seleccionar El tipo de Documento. Verifique");
			return ;
		}
		if(uploadedFile==null && idTipoDocumento!=null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe Adjuntar un Documento. Verifique");
			return ;
		}
		for(ExperienciaLaboralLegajo ex : listDetails){
			if(ex.getEmpresa().trim().toLowerCase().equals(experienciaLaboral.getEmpresa().trim().toLowerCase())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_en_detalle"));
				return ;
			}
		}
		
		/**
		 * Para el Caso de documento adjuntos
		 * */
		if(uploadedFile!=null){
			idDoc=guardarAdjuntos(fileName, uploadedFile.length, contentType, uploadedFile, idTipoDocumento, null);
			if(idDoc!=null){
				Documentos doc= em.find(Documentos.class,idDoc);
				doc.setPersona(em.find(Persona.class, idPersona));
				em.merge(doc);
				em.flush();
				experienciaLaboral.setDocumentos(doc);
			}else
				return ;
						
		}
		
	
		experienciaLaboral.setPais(em.find(Pais.class, idPais));
		if(idSector != null)
		experienciaLaboral.setDatosEspecificosSector(em.find(DatosEspecificos.class, idSector));
		if(idTipoVinculacion != null)
		experienciaLaboral.setDatosEspecificosTipoVinculo(em.find(DatosEspecificos.class, idTipoVinculacion));
		
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
		experienciaLaboralLegajoHome.clearInstance();
		
		
	}
	
	public void removeRow(int pos){
		statusMessages.clear();
		listDetails.get(pos).setActivo(Estado.INACTIVO.getValor());
		ExperienciaLaboralLegajo el= em.find(ExperienciaLaboralLegajo.class, listDetails.get(pos).getIdExperienciaLab());
		if(el != null && el.getDocumentos() != null){
			Documentos doc= em.find(Documentos.class, el.getDocumentos().getIdDocumento());
			doc.setActivo(false);
			em.merge(doc);
			em.flush();
		}
		experienciaLaboralLegajoHome.setInstance(listDetails.get(pos));
		experienciaLaboralLegajoHome.save();
		displaySuccesfullMessage();
		listDetails.remove(pos);
	}
	
	public void selectToEdit(int pos){
		try {
			experienciaLaboral = listDetails.get(pos);
			experienciaLaboral.setPosicion(pos);
		
			
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
		

		if(!toDetailOk()){
			return;
		}
		for(ExperienciaLaboralLegajo ex : listDetails){
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
			
			
		}
		/**
		 *PARA EL CASO DE DOCUMENTO ADJUNTO 
		 *
		 **/
		Long idDoc=null;
		
		if(uploadedFile!=null){
			Long idDocAnterior=experienciaLaboral.getDocumentos()!=null?experienciaLaboral.getDocumentos().getIdDocumento():null;
			
			idDoc=guardarAdjuntos(fileName, uploadedFile.length, contentType, uploadedFile, idTipoDocumento, idDocAnterior);
			if(idDoc!=null){
				Documentos doc= em.find(Documentos.class,idDoc);
				doc.setPersona(em.find(Persona.class, idPersona));
				em.merge(doc);
				em.flush();
				experienciaLaboral.setDocumentos(doc);
			}else
				return;
				
						
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
		experienciaLaboralLegajoHome.setInstance(experienciaLaboral);
		
		experienciaLaboralLegajoHome.save();
		
		if(experienciaLaboral.getDocumentos()!=null && idDoc!=null){
			Documentos doc= em.find(Documentos.class,idDoc);
			doc.setIdTabla(experienciaLaboral.getIdExperienciaLab());
			doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
			doc.setFechaMod(new Date());
			em.merge(doc);
			em.flush();
		}
		displaySuccesfullMessage();
		clearDataDetail();
		experienciaLaboralLegajoHome.clearInstance();
		
	}
	
	public void abrirDoc(int index){
		
		ExperienciaLaboralLegajo e=listDetails.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumentos().getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}
	
	private Long  guardarAdjuntos(String fileName, int fileSize, String contentType, byte[] file, Long idTipoDoc, Long idDocumento) {
		try {
			
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "ExperienciaLaboralLegajo";
			String nombrePantalla = "ExperienciaLaboralLegajo";
			String sf=File.separator;
			String direccionFisica = sf+"LEGAJO"+sf+persona.getDocumentoIdentidad()+"_"+persona.getIdPersona()+sf+"EXPERIENCIA_LABORAL"+sf;
			
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void clearDataDetail(){
		experienciaLaboral = new ExperienciaLaboralLegajo();
		nombreDoc=null;
		idTipoDocumento=null;
		item=null;
		idPais=General.getIdParaguay();
		idTipoDocumento=null;
		idSector=null;
		esOtroSector=false;
		esOtrovincilo=false;
	
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
		/*if(experienciaLaboral.getFechaDesde() == null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_sin_fecha_desde"));
			return false;
		}*/
		
		
		if(experienciaLaboral.getFechaHasta() != null && experienciaLaboral.getFechaDesde() != null
				&& experienciaLaboral.getFechaHasta().before(experienciaLaboral.getFechaDesde())){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_fecha_hasta_incorrecta"));
			return false;
		}
		if(experienciaLaboral.getFechaDesde() != null && experienciaLaboral.getFechaDesde().after(fechaActual) ){
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
		/**/if(idPais==null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe especificar el Pais. Verifique");
			return false;
		}
		/*if(experienciaLaboral.getPuestoCargo()==null || experienciaLaboral.getPuestoCargo().trim().equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe ingresar un Puesto/Cargo");
			return false;
		}*/

		/*if(idTipoVinculacion==null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe especificar el Tipo de Vinculación. Verifique");
			return false;
		}*/
		if(esOtrovincilo&& (experienciaLaboral.getOtroVincu()==null ||experienciaLaboral.getOtroVincu().trim().equals("")))
		{
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe especificar Otro Tipo de Vinculación. Verifique");
			return false;
		}
		/*if(idSector==null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe especificar el Item Sector. Verifique");
			return false;
		}*/
		if(esOtroSector&& (experienciaLaboral.getOtroSector()==null || experienciaLaboral.getOtroSector().trim().equals(""))){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe especificar Otro Sector. Verifique");
			return false;
		}
		
		
		/*if(experienciaLaboral.getFuncionesRealizadas() == null || experienciaLaboral.getFuncionesRealizadas().trim().isEmpty()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ExperienciaLaboral_msg_sin_funcion"));
			return false;
		}*/
		
		
		/*if(experienciaLaboral.getReferenciasLaborales()==null || experienciaLaboral.getReferenciasLaborales().trim().equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe ingresar las Referencias Laborales ");
			return false;
		}*/
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
	
	
	
	
	private void displaySuccesfullMessage(){
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SICCAAppHelper.getBundleMessage("msg_operacion_exitosa"));
	}
	
	
	
	
//	GETTERS Y SETTERS
	public ExperienciaLaboralLegajo getExperienciaLaboral() {
		return experienciaLaboral;
	}

	public void setExperienciaLaboralLegajo(ExperienciaLaboralLegajo experienciaLaboral) {
		this.experienciaLaboral = experienciaLaboral;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public List<ExperienciaLaboralLegajo> getListDetails() {
		return listDetails;
	}

	public void setListDetails(List<ExperienciaLaboralLegajo> listDetails) {
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

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	
	
}
