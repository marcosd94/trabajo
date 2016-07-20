package py.com.excelsis.sicca.capacitacion.session.form;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.entity.AdjuntosCap;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.PlanCapacitacion;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;

import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("planCapacitacionEditFC")
public class PlanCapacitacionEditFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	private PlanCapacitacion planCapacitacion= new PlanCapacitacion();
	private Boolean modoUpdate=false;
	private Long idPlan;
	private Boolean primeraEntrada=true;
	private String monto;

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
		
		
		seguridadUtilFormController.validarPerteneceOEE(nivelEntidadOeeUtil.getIdConfigCab());
		
			if(primeraEntrada)
			{
				nivelEntidadOeeUtil.limpiar();
				primeraEntrada=false;
				if(idPlan!=null){
					
					planCapacitacion= em.find(PlanCapacitacion.class, idPlan);
					modoUpdate=true;
					monto =General.getNumber(planCapacitacion.getMonto().toString());
					monto=monto.replace(",",".");
					Documentos doc= em.find(Documentos.class, planCapacitacion.getDocumento().getIdDocumento());
					nombreDoc=doc.getNombreFisicoDoc();
					idTipoDocumento=doc.getDatosEspecificos().getIdDatosEspecificos();
					if(planCapacitacion.getConfiguracionUoDet()!=null)
						nivelEntidadOeeUtil.setIdUnidadOrganizativa(planCapacitacion.getConfiguracionUoDet().getIdConfiguracionUoDet());
				
				}else{
					planCapacitacion= new PlanCapacitacion();
					modoUpdate=false;
					idTipoDocumento=null;
					item=null;
					uploadedFile=null;
					uploadedFileCopia=null;
					
				}
		}
		cargarCabecera();	
			
	}
	
	
	
	
	

	public void cargarCabecera(){
		
		//Nivel
		ConfiguracionUoCab oee=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Entidad enti= em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		SinEntidad sinEntidad=em.find(SinEntidad.class, enti.getSinEntidad().getIdSinEntidad());
		Long idSinNivelEntidad=nivelEntidadOeeUtil.getIdSinNivelEntidad(enti.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);
		
		//Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());
		
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(oee.getIdConfiguracionUo());
		
		nivelEntidadOeeUtil.init();
		
		
		
		
		
	}
	

	public void descargar(){
		
		AdmDocAdjuntoFormController.abrirDocumentoFromCU( planCapacitacion.getDocumento().getIdDocumento(),usuarioLogueado.getIdUsuario());
		
	}
	
	public String save() {
		try {
			
			if(!validarCampos())
				return null;
			
			/**
			 *  i.	Generar un registro en la tabla PLAN_CAPACITACION, guardar los campos:
			 * */
			planCapacitacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			planCapacitacion.setFechaAlta(new Date());
			planCapacitacion.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));
			if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null)
				planCapacitacion.setConfiguracionUoDet(em.find(ConfiguracionUoDet.class,nivelEntidadOeeUtil.getIdUnidadOrganizativa()));
			planCapacitacion.setEstado("C");
			planCapacitacion.setActivo(true);
			em.persist(planCapacitacion);
			
			

			/**
			 * Para el Caso de documento adjuntos
			 * ii.	Guardar el documento adjunto en DOCUMENTOS, llamar al CU 289 y pasarle los parámetros 
			 *
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
				 * iii.	Actualizar el ID_DOCUMENTO generado en el paso anterior, en la tabla PLAN_CAPACITACION
				 * */
				planCapacitacion.setDocumento(em.find(Documentos.class,
						idDocumento));
			}
			/**
			 * fin
			 * */
			
		//	em.merge(planCapacitacion);
			em.flush();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
				
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
	
	
	}
	
	public void documento() {
		try {
			String nombrePantalla = "planCapacitacion_edit";
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
	
	public String update() {
		try {
			if(!validarCampos())
				return null;
			
			planCapacitacion.setFechaMod(new Date());
			planCapacitacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			
			/***
			 * PARA EL CASO DE DOCUMENTO ADJUNTO
			 * **/
			if (item != null) {
				Long idDocAnterior=null;
				if(planCapacitacion.getDocumento()!=null)
					idDocAnterior=planCapacitacion.getDocumento().getIdDocumento();
				idDocumento = AdmDocAdjuntoFormController.editar(
						item.getFile(), item.getFileSize(), item.getFileName(),
						item.getContentType(),
						planCapacitacion.getDocumento().getUbicacionFisica(),
						"planCapacitacion_edit", idDocAnterior, idTipoDocumento,
						usuarioLogueado.getIdUsuario(),
						planCapacitacion.getIdPlan(), "PlanCapacitacion");

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
							
							nombreDoc=planCapacitacion.getDocumento().getNombreFisicoDoc();
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

				planCapacitacion.setDocumento(em.find(Documentos.class,
						idDocumento));
			}
			
			/**
			 * FIN
			 * **/
			em.merge(planCapacitacion);
			em.flush();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";	
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		
		
	}
	
	
	/**
	 * controla previamente que los campos obligatorios estén completos al momento de guardar/editar el registro
	 * @return true en el caso ue todo este bien
	 * */
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
		

		if(planCapacitacion.getNro()==null){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el Nº de Mesa de Entrada. Verifique");
			return false;
		}
		if(planCapacitacion.getNro().longValue()<0){
			statusMessages.add(Severity.ERROR,"El Nº de Mesa de Entrada debe ser mayor a cero. Verifique");
			return false;
		}
		if(planCapacitacion.getAnio()==null){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el Año de Mesa de Entrada. Verifique");
			return false;
		}
		
		if(!General.validarAnio(planCapacitacion.getAnio()))
		{
			statusMessages.add(Severity.ERROR,"El año ingresado no es válido. Verifique");
			return false;
		}
		
		if(monto==null){
			statusMessages.add(Severity.ERROR,"Debe ingresar el monto. Verifique");
			return false;
		}
		try {
			planCapacitacion.setMonto((Double.parseDouble(monto.replace(".", ""))));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"El campo monto debe ser numerico");
			return false;
		}
		
		if(planCapacitacion.getMonto().longValue()<0){
			statusMessages.add(Severity.ERROR,"El Monto ingresado no puede ser mayor a cero . Verifique");
			return false;
		}
		if(idTipoDocumento==null)
		{
			statusMessages.add(Severity.ERROR,"Debe seleccionar el Tipo de Documento. Verifique");
			return false;
		}
		if(!modoUpdate)
		{
			if(item==null)
			{
				statusMessages.add(Severity.ERROR,"Debe seleccionar el Documento. Verifique");
				return false;
			}
			
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
		
		if(existeRegistro()){
			statusMessages.add(Severity.ERROR,"El registro ya existe. Verifique");
			return false;
		}
		
		
		return true;
	}
	@SuppressWarnings("unchecked")
	private boolean existeRegistro(){
		String sql= "Select c from PlanCapacitacion c where c.activo=true " +
				" and c.configuracionUoCab.idConfiguracionUo= :idOee and c.anio= :anio ";
		if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null)
			sql+=" and c.configuracionUoDet.idConfiguracionUoDet = "+nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		else
			sql+=" and c.configuracionUoDet.idConfiguracionUoDet is null ";
		if(modoUpdate)
			sql+=" and c.idPlan!="+idPlan.longValue();
		List<PlanCapacitacion>planCapacitacions= em.createQuery(sql).setParameter("idOee", nivelEntidadOeeUtil.getIdConfigCab()).
		setParameter("anio", planCapacitacion.getAnio()).getResultList();
		
		return !planCapacitacions.isEmpty();
			
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

	public Boolean getModoUpdate() {
		return modoUpdate;
	}

	public void setModoUpdate(Boolean modoUpdate) {
		this.modoUpdate = modoUpdate;
	}

	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
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

	public String getMonto() {
		return monto;
	}

	public void setMonto(String monto) {
		this.monto = monto;
	}

}
