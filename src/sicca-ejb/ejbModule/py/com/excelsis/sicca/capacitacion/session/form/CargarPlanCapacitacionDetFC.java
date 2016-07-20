package py.com.excelsis.sicca.capacitacion.session.form;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.PlanCapacitacion;
import py.com.excelsis.sicca.entity.PlanCapacitacionDet;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;

import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("cargarPlanCapacitacionDetFC")
public class CargarPlanCapacitacionDetFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
		
	private PlanCapacitacion planCapacitacion= new PlanCapacitacion();
	private Long idPlan;

	private String descripcion=null;
	private List<PlanCapacitacionDet> planCapacitacionDetLista= new ArrayList<PlanCapacitacionDet>();
	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item = null;
	private String nombreDoc = null;
	private File inputFile = null;
	private byte[] uploadedFile = null;
	private String contentType = null;
	private String fileName = null;
	private Long idTipoDocumento = null;
	private Long idDocumento = null;
	private String fName;
	private byte[] uploadedFileCopia = null;
	private String contentTypeCopia = null;
	private String fileNameCopia = null;

	public void init() {
		
			
		nivelEntidadOeeUtil.limpiar();
				
		if(idPlan!=null){
			planCapacitacion= em.find(PlanCapacitacion.class, idPlan);
			if(planCapacitacion.getConfiguracionUoDet()!=null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(planCapacitacion.getConfiguracionUoDet().getIdConfiguracionUoDet());
				
			}else{
				planCapacitacion= new PlanCapacitacion();
			}
			cargarCabecera();	
			fidDet();
			seguridadUtilFormController.validarPerteneceOEE(nivelEntidadOeeUtil.getIdConfigCab());
		}
	
	@SuppressWarnings("unchecked")
	private void fidDet(){
		planCapacitacionDetLista= em.createQuery("Select det from PlanCapacitacionDet det " +
				" where det.planCapacitacion.idPlan=:idPlan and det.activo=true order by det.fechaAlta desc").setParameter("idPlan",idPlan).getResultList();
	}
	
	
	
	

	public void cargarCabecera(){
	nivelEntidadOeeUtil.setIdConfigCab(planCapacitacion.getConfiguracionUoCab().getIdConfiguracionUo());
		if(planCapacitacion.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(planCapacitacion.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
	}
	
	
	
	public String save() {
		try {
			
			if(!validarCampos())
				return null;
			
			
			PlanCapacitacionDet planCapacitacionDet = new PlanCapacitacionDet();
			/**
			 * Para el Caso de documento adjuntos
			 * i.	Guardar el documento adjunto en DOCUMENTOS, llamar al CU 289 y pasarle los parámetros 
			 * */
			documento();
			if (item != null) {
				if (idDocumento != null) {

					if (idDocumento.longValue() == -8) {
						statusMessages
								.add(Severity.ERROR,
										"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return null;
					}
					if (idDocumento.longValue() == -7) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return null;
					}
					if (idDocumento.longValue() == -6) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar ya existe. Seleccione otro");
					
							nombreDoc=null;
							uploadedFileCopia=null;
							contentTypeCopia=null;
							fileNameCopia=null;
							item=null;
						return null;
					}

				} else {
					statusMessages.add(Severity.ERROR,
							"Error al adjuntar el registro. Verifique");
					return null;
				}
				/**
				 * iii.	Actualizar el ID_DOCUMENTO generado en el paso anterior, en la tabla PLAN_CAPACITACION_DET
				 * **/
				planCapacitacionDet.setDocumento(em.find(Documentos.class,
						idDocumento));
			}
			/**
			 * fin
			 * */
			
			/**
			 * ii.	Generar un registro en la tabla PLAN_CAPACITACION_DET, guardar los campos:
			 * */
			planCapacitacionDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			planCapacitacionDet.setFechaAlta(new Date());
			planCapacitacionDet.setActivo(true);
			planCapacitacionDet.setDescripcion(descripcion);
			planCapacitacionDet.setPlanCapacitacion(planCapacitacion);
			em.persist(planCapacitacionDet);
			/**
			 * iv.	Si el estado del PLAN_CAPACITACION relacionado se encuentra en “C” actualizar la columna de la misma tabla:
			 * */
			if(planCapacitacion.getEstado().equals("C")){
				planCapacitacion.setEstado("E");
				planCapacitacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
				planCapacitacion.setFechaMod(new Date());
				em.merge(planCapacitacion);
				
			}
			
			
			em.flush();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
				setearVar();
				fidDet();
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
	
	
	}
	
	public void descargar(int index){
		try {
			PlanCapacitacionDet det= planCapacitacionDetLista.get(index);
		
			if(det.getDocumento()!=null){
				AdmDocAdjuntoFormController.abrirDocumentoFromCU( det.getDocumento().getIdDocumento(),usuarioLogueado.getIdUsuario());
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void eliminar(int index){
		/**i.	Link Eliminar, inactiva el registro de la grilla.
		 * 
		 * **/
		PlanCapacitacionDet det= planCapacitacionDetLista.get(index);
		det.setActivo(false);
		det.setFechaMod(new Date());
		det.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(det);
		//Inactiva el documento
		Documentos doc = em.find(Documentos.class,det.getDocumento().getIdDocumento());
		doc.setActivo(false);
		doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
		doc.setFechaMod(new Date());
		em.merge(doc);
		
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		fidDet();
		
	}
	
	private void setearVar(){
		item=null;
		uploadedFile=null;
		uploadedFileCopia=null;
		idTipoDocumento=null;
		idDocumento=null;
		nombreDoc=null;
		descripcion=null;
		
	}
	
	
	public void documento() {
		try {
			String nombrePantalla = "cargarDatosCapacitacion";
			String direccionFisica = "";
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio = sdfSoloAnio.format(new Date());
			direccionFisica = "\\SICCA\\"
					+ anio
					+ "\\OEE\\"
					+ nivelEntidadOeeUtil.getIdConfigCab()
					+ "\\P\\"
					+ planCapacitacion.getIdPlan();
			idDocumento = AdmDocAdjuntoFormController.guardarDirecto(item,
					direccionFisica, nombrePantalla, idTipoDocumento,
					usuarioLogueado.getIdUsuario(), "PlanCapacitacion");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	@SuppressWarnings("unchecked")
	private boolean validarCampos(){
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return false;
			}
			uploadedFileCopia=uploadedFile;
			contentTypeCopia=contentType;
			fileNameCopia=fileName;

		} else {
			if(uploadedFileCopia!=null){
				uploadedFile=uploadedFileCopia;
				contentType=contentTypeCopia;
				fileName=fileNameCopia;
			}else{
				item = null;
				uploadedFileCopia=null;
			}
			
		}
		

	
		
		if(idTipoDocumento==null)
		{
			statusMessages.add(Severity.ERROR,"Debe seleccionar el Tipo de Documento. Verifique");
			return false;
		}
		if(item==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el documento");
			return false;
		}
		if(descripcion==null || descripcion.trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe Ingresar una descripción");
			return false;
		}
		
		if(item!=null){
			if (item != null) {
				String nomfinal = "";
				nomfinal = item.getFileName();
				String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
				List<TipoArchivo> tam = em.createQuery(
						"select t from TipoArchivo t "
								+ " where lower(t.extension) like '"
								+ extension.toLowerCase() + "'").getResultList();
				if (!tam.isEmpty() && tam.get(0).getUnidMedidaByte() != null) {
					if (item.getFileSize() > tam.get(0).getUnidMedidaByte()
							.intValue()) {
						statusMessages.add(Severity.ERROR,
								"El archivo supera el tamaño máximo permitido. Límite "
										+ tam.get(0).getTamanho()
										+ tam.get(0).getUnidMedida()
										+ ", verifique");
						return false;
					}
				}
			}

		}
		
		
		return true;
	}
	
	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}

	
		
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	

	public PlanCapacitacion getPlanCapacitacion() {
		return planCapacitacion;
	}

	public void setPlanCapacitacion(PlanCapacitacion planCapacitacion) {
		this.planCapacitacion = planCapacitacion;
	}

	public Long getIdPlan() {
		return idPlan;
	}

	public void setIdPlan(Long idPlan) {
		this.idPlan = idPlan;
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

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public Long getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(Long idDocumento) {
		this.idDocumento = idDocumento;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public byte[] getUploadedFileCopia() {
		return uploadedFileCopia;
	}

	public void setUploadedFileCopia(byte[] uploadedFileCopia) {
		this.uploadedFileCopia = uploadedFileCopia;
	}

	public String getContentTypeCopia() {
		return contentTypeCopia;
	}

	public void setContentTypeCopia(String contentTypeCopia) {
		this.contentTypeCopia = contentTypeCopia;
	}

	public String getFileNameCopia() {
		return fileNameCopia;
	}

	public void setFileNameCopia(String fileNameCopia) {
		this.fileNameCopia = fileNameCopia;
	}

	public List<PlanCapacitacionDet> getPlanCapacitacionDetLista() {
		return planCapacitacionDetLista;
	}

	public void setPlanCapacitacionDetLista(
			List<PlanCapacitacionDet> planCapacitacionDetLista) {
		this.planCapacitacionDetLista = planCapacitacionDetLista;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
