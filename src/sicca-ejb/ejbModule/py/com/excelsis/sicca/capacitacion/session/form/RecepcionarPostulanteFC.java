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
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.capacitacion.session.PostulacionCapList;
import py.com.excelsis.sicca.entity.CapacitacionCerrada;
import py.com.excelsis.sicca.entity.CapacitacionFinanciacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.HistoricoCapacitacionDoc;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RevisionCapacitacion;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("recepcionarPostulanteFC")
public class RecepcionarPostulanteFC{
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
	JbpmUtilFormController jbpmUtilFormController;

	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create=true)
	PostulacionCapList postulacionCapList;
	

	private Capacitaciones capacitaciones= new Capacitaciones();
	private Long idCapacitacion;
	private String tipoCapac=null;
	private Long idPaisExp=null;
	private String nombre=null;
	private String apellido=null;
	private String docIdentidad=null;
	private String from;
	private List<PostulacionCap> postulacionCapLista= new ArrayList<PostulacionCap>();

	private Documentos documentos = new Documentos();
	private UploadItem item = null;
	private String nombreDoc = null;
	private File inputFile = null;
	private byte[] uploadedFile = null;
	private String contentType = null;
	private String fileName = null;
	private Long idTipoDocumento = null;
	private Long idDocumento = null;
	private String fromCU = null;// recibe del CU que le llamo
	private String fName;
	private byte[] uploadedFileCopia = null;
	private String contentTypeCopia = null;
	private String fileNameCopia = null;



	public void init() {
		capacitaciones=em.find(Capacitaciones.class, idCapacitacion);
		seguridadUtilFormController.validarCapacitacion(idCapacitacion, estadoInicioRecepcionarPostulante(), ActividadEnum.CAPA_RECEPCIONAR_POST_INSC.getValor());
		cargarCabecera();
		if(capacitaciones.getDatosEspecificosTipoCap()!=null)
			tipoCapac=capacitaciones.getDatosEspecificosTipoCap().getDescripcion();
		
		if (capacitaciones.getDocumentos() != null) {
			idTipoDocumento = capacitaciones.getDocumentos()
					.getDatosEspecificos().getIdDatosEspecificos();
			nombreDoc = capacitaciones.getDocumentos().getNombreFisicoDoc();
			documentos = em.find(Documentos.class, capacitaciones
					.getDocumentos().getIdDocumento());
		}
		
		findPostulantes();
		
		
	}
	
	public void initEdit() {
		capacitaciones=em.find(Capacitaciones.class, idCapacitacion);
	//	seguridadUtilFormController.validarCapacitacion(idCapacitacion, estadoInicioRecepcionarPostulante(), ActividadEnum.CAPA_RECEPCIONAR_POST_INSC.getValor());
		cargarCabecera();
		if(capacitaciones.getDatosEspecificosTipoCap()!=null)
			tipoCapac=capacitaciones.getDatosEspecificosTipoCap().getDescripcion();
		
		findPostulantes();
		
		
	}
	
	public String reprogramarCancelar(){
		try {
			
			
			
		    /**
			 * SE ACTUALIZA LA TABLA CAPACITACION
			 * **/
			
			capacitaciones.setEstado(estadoReprogramarCancelar());
			capacitaciones.setUsuMod(usuarioLogueado.getCodigoUsuario());
			capacitaciones.setFechaMod(new Date());
			capacitaciones.setEstadoPublic("VERIFICADO");
			capacitaciones.setFechaRecepHasta( new Date());
			em.merge(capacitaciones);
			

			/**
			 *SE PASA A SGT. TAREA
			 * */
			jbpmUtilFormController.setActividadActual(ActividadEnum.CAPA_RECEPCIONAR_POST_INSC);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CAPA_REPROGRAMAR_CANCELAR_CAPAC);
		    jbpmUtilFormController.setTransition("recPosIns_TO_repCanCap");
		    jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
		    jbpmUtilFormController.setCapacitacion(capacitaciones);
		    
		    if (jbpmUtilFormController.nextTask()){
				em.flush();
			}
			
		    statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Se ha producido un error: "+e.getMessage());
			return null;
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	private int estadoReprogramarCancelar(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'REPROGRAMAR/CANCELAR' and r.activo=true").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	@SuppressWarnings("unchecked")
	private int estadoInicioRecepcionarPostulante(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'RECEPCIONAR POSTULANTES' and r.activo=true").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	

	

	public void cargarCabecera(){
		
		//Nivel
		ConfiguracionUoCab oee=em.find(ConfiguracionUoCab.class, capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo());
		Entidad enti= em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		Long idSinNivelEntidad=nivelEntidadOeeUtil.getIdSinNivelEntidad(enti.getSinEntidad().getNenCodigo());
		SinEntidad sinEntidad= em.find(SinEntidad.class, enti.getSinEntidad().getIdSinEntidad());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);
		
		//Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());
		
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(oee.getIdConfiguracionUo());
		
		if(capacitaciones.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
		
		nivelEntidadOeeUtil.init();
		
			
	}

	

	
	@SuppressWarnings("unchecked")
	private void findPostulantes(){
		postulacionCapList.setIdCapacitacion(idCapacitacion);
		postulacionCapLista=postulacionCapList.limpiarResultadosCU479();
	}
	
	
	public void search(){
		postulacionCapList.setIdCapacitacion(idCapacitacion);
		postulacionCapList.setIdPaisExp(idPaisExp);
		if(nombre!=null && !nombre.trim().equals(""))
			postulacionCapList.getPersona().setNombres(nombre);
		if(apellido!=null && !apellido.trim().equals(""))
			postulacionCapList.getPersona().setApellidos(apellido);
		if(docIdentidad!=null && !docIdentidad.trim().equals(""))
			postulacionCapList.getPersona().setDocumentoIdentidad(docIdentidad);
		postulacionCapLista=postulacionCapList.buscarResultadosCU479();
	}
	public void searchAll(){
		nombre=null;
		apellido=null;
		docIdentidad=null;
		idPaisExp=null;
		postulacionCapList.setIdCapacitacion(idCapacitacion);
		postulacionCapLista=postulacionCapList.limpiarResultadosCU479();
	}
	
	
	
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}


	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}


	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}


	public Long getIdCapacitacion() {
		return idCapacitacion;
	}


	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}


	public String getTipoCapac() {
		return tipoCapac;
	}



	public void setTipoCapac(String tipoCapac) {
		this.tipoCapac = tipoCapac;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public List<PostulacionCap> getPostulacionCapLista() {
		return postulacionCapLista;
	}

	public void setPostulacionCapLista(List<PostulacionCap> postulacionCapLista) {
		this.postulacionCapLista = postulacionCapLista;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
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

	public String getFromCU() {
		return fromCU;
	}

	public void setFromCU(String fromCU) {
		this.fromCU = fromCU;
	}

	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}
	
	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}
	
	public void abrirDocumento() {

		try {
			AdmDocAdjuntoFormController.abrirDocumentoFromCU(capacitaciones
					.getDocumentos().getIdDocumento(), usuarioLogueado
					.getIdUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void documento() {
		try {
			String nombrePantalla = "datosCapacitacion_edit";
			String direccionFisica = "";
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio = sdfSoloAnio.format(new Date());
			direccionFisica = "\\SICCA\\"
					+ anio
					+ "\\OEE\\"
					+ capacitaciones.getConfiguracionUoCab()
							.getIdConfiguracionUo()
					+ "\\UO\\"
					+ capacitaciones.getConfiguracionUoDet()
							.getIdConfiguracionUoDet() + "\\C\\"
					+ capacitaciones.getIdCapacitacion();
			idDocumento = AdmDocAdjuntoFormController.guardarDirecto(item,
					direccionFisica, nombrePantalla, idTipoDocumento,
					usuarioLogueado.getIdUsuario(), "Capacitaciones");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public String guardar() {
		try {
			if (!chkDatos())
				return null;
			
			/**
			 * Para el Caso de documento adjuntos
			 * */
			Documentos docAnterior = new Documentos();
			if (capacitaciones.getDocumentos() == null) {
				documento();

			} else if (item != null) {
				docAnterior = em.find(Documentos.class, capacitaciones
						.getDocumentos().getIdDocumento());
				idDocumento = AdmDocAdjuntoFormController.editar(
						item.getFile(), item.getFileSize(), item.getFileName(),
						item.getContentType(),
						docAnterior.getUbicacionFisica(),
						"datosCapacitacion_edit",docAnterior.getIdDocumento(), idTipoDocumento,
						usuarioLogueado.getIdUsuario(),
						capacitaciones.getIdCapacitacion(), "Capacitaciones");

			}
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
						if(capacitaciones.getDocumentos()!=null){
							nombreDoc=capacitaciones.getDocumentos().getNombreFisicoDoc();
							uploadedFileCopia=null;
							contentTypeCopia=null;
							fileNameCopia=null;
						}else
							nombreDoc=null;
						return null;
					}

				} else {
					statusMessages.add(Severity.ERROR,
							"Error al adjuntar el registro. Verifique");
					return null;
				}

				capacitaciones.setDocumentos(em.find(Documentos.class,
						idDocumento));
			}
			/**
			 * fin
			 * */

			em.merge(capacitaciones);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Se ha producido un error" + e.getMessage());
			return null;

		}

	}
	
	@SuppressWarnings("unchecked")
	private boolean chkDatos() {
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

		if (idTipoDocumento == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
			return false;
		}

		if (item == null && capacitaciones.getDocumentos() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar el archivo. Verifique");
			return false;
		}
		
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

		return true;
	}

	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}
	
}
