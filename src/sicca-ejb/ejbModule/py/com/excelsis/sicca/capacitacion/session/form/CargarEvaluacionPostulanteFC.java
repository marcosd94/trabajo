package py.com.excelsis.sicca.capacitacion.session.form;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import enums.SeleccionadoResultado;

import py.com.excelsis.sicca.dto.EvalDocumentalDetDTO;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoPuesto651;
import py.com.excelsis.sicca.entity.EvaluacionInscPost;
import py.com.excelsis.sicca.entity.EvaluacionPart;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PostulacionAdjuntos;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.PostulacionCapAdj;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.entity.VerCursosEvaluacionPostulantes;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("cargarEvaluacionPostulante")
public class CargarEvaluacionPostulanteFC{

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	


	
	private Long idEvalIp;
	private EvaluacionInscPost evaluacionInscPost= new EvaluacionInscPost();
	private PostulacionCap postulacionCap= new PostulacionCap(); 
	private Capacitaciones capacitaciones= new Capacitaciones();
	private Persona  persona= new Persona();
	private String titulo;
	private String tipoCapacitacion;
	private String idMostrar=null;
	private List<PostulacionCapAdj> capAdjLista= new ArrayList<PostulacionCapAdj>();
	private String comentario=null;
	private List<EvaluacionPart> capacEvalParteLista= new ArrayList<EvaluacionPart>();
	private String resultado=null;
	private List<VerCursosEvaluacionPostulantes> capacitacionesEvalPostulantes= new ArrayList<VerCursosEvaluacionPostulantes>(); 
	
	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item;
	private String nombreDoc;
	private File inputFile;
	private SeguridadUtilFormController seguridadUtilFormController;
	private Long idTipoDocumento;
	private String direccionFisica;
	private String nombrePantalla;
	private Long idDoc;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	private Long idAdjunto;
	private boolean primeraEntrada=false;;

	

	public void init() {
		cargarCabecera();
		findDocs();
		FindCapacitaciones();
	}



	


	

	

	
	

	

	private void cargarCabecera() {
		try {
			evaluacionInscPost= em.find(EvaluacionInscPost.class, idEvalIp);
			postulacionCap=em.find(PostulacionCap.class, evaluacionInscPost.getPostulacionCap().getIdPostulacion());
			persona=em.find(Persona.class, postulacionCap.getPersona().getIdPersona());
			if (postulacionCap.getCapacitacion()!= null) {
				capacitaciones = em.find(Capacitaciones.class, postulacionCap.getCapacitacion().getIdCapacitacion());
				titulo = capacitaciones.getNombre();
				tipoCapacitacion = capacitaciones.getTipo();
			}
			if(primeraEntrada=true){
				if(evaluacionInscPost.getResultado()!=null)
					resultado=evaluacionInscPost.getResultado();
				else
					resultado=null;
				comentario=evaluacionInscPost.getObservacion();
				primeraEntrada=false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	@SuppressWarnings("unchecked")
	private void findDocs(){
		
		capAdjLista=em.createQuery("Select adjunto from PostulacionCapAdj adjunto " +
				" where adjunto.postulacionCap.idPostulacion=:idPost").setParameter("idPost",postulacionCap.getIdPostulacion()).getResultList();
	}
	public void back(){
		primeraEntrada=true;
	}

	
	public void abrirDocumento(Long idAdjunto) {

		try {
			PostulacionCapAdj adj= em.find(PostulacionCapAdj.class, idAdjunto);
			AdmDocAdjuntoFormController.abrirDocumentoFromCU(adj.getDocumento().getIdDocumento(), usuarioLogueado
					.getIdUsuario());
			statusMessages.clear();
		} catch (Exception e) {
			statusMessages.clear();
			e.printStackTrace();
		}

	}
	public void seleccionadoParaAdjuntar(int index) {
		item = null;
		nombreDoc = null;
		PostulacionCapAdj aux = capAdjLista.get(index);
		idAdjunto=aux.getIdPostulacionAdj();
		idTipoDocumento=aux.getDocumento().getDatosEspecificos().getIdDatosEspecificos();
		

	}
	@SuppressWarnings("unchecked")
	public void documento() throws NamingException {
		nombrePantalla = "evaluacionPostulante_editar";
		
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return;
			}
			
		}else{
			statusMessages.add(Severity.WARN,"Debe seleccionar el Documento para poder adjuntar");
			return;
		}
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
					return;
				}
			}
			/**
			 * FIN
			 * */
		
		direccionFisica="/SICCA/"+usuarioLogueado.getCodigoUsuario()+"/"+persona.getDocumentoIdentidad()+persona.getIdPersona();
		
		idDoc = AdmDocAdjuntoFormController.guardarDirecto(item,
				direccionFisica, nombrePantalla, idTipoDocumento,
				usuarioLogueado.getIdUsuario(), "PersonaPostulante");
		if (idDoc != null) {
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
			try {
				
				PostulacionCapAdj postulacionCapAdj= em.find(PostulacionCapAdj.class, idAdjunto);
				//inactivamos el documento
				Documentos docInactivar= em.find(Documentos.class, postulacionCapAdj.getDocumento().getIdDocumento());
				docInactivar.setActivo(false);
				docInactivar.setUsuMod(usuarioLogueado.getCodigoUsuario());
				docInactivar.setFechaMod( new Date());
				em.merge(docInactivar);
				//guardamos en nuevo documento
				postulacionCapAdj.setDocumento(em.find(Documentos.class, idDoc));
				postulacionCapAdj.setFechaMod(new Date());
				postulacionCapAdj.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.persist(postulacionCapAdj);
				
				em.flush();
				findDocs();
			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR,"Se ha producido un error :"+e.getMessage());
			}
		}
	}
	
	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}
	
	private void FindCapacitaciones(){
		String sql=" Select * from  capacitacion.ver_cursos_evaluacion_postulantes where id_persona=:idPersona";
		capacitacionesEvalPostulantes= new ArrayList<VerCursosEvaluacionPostulantes>();
		List<Object> result = em.createNativeQuery(sql).setParameter("idPersona",  persona.getIdPersona()).getResultList();
		if (!result.isEmpty()) {
			for (Object obj : (List<Object>) result) {
				Object[] record = (Object[]) obj;
				VerCursosEvaluacionPostulantes aux= new VerCursosEvaluacionPostulantes();
				if(record[0]!=null)
					aux.setNombre((String)record[0]);
				
				if(record[1]!=null)
					aux.setDescripcion((String)record[1]);

				if(record[2]!=null)
					aux.setModalidad((String)record[2]);
				if(record[3]!=null)
					aux.setFecha((String)record[3]);
				if(record[4]!=null)
					aux.setEstado((String)record[4]);
				if(record[5]!=null)
					aux.setIdPersona(Long.parseLong(record[5].toString()));
				if(record[6]!=null)
					aux.setCargaHoraria(Long.parseLong(record[6].toString()));
				if(record[7]!=null)
					aux.setCreditos(Long.parseLong(record[7].toString()));
									
				capacitacionesEvalPostulantes.add(aux);
			}

		}

	}

		
	public String modalidad(String mod) {
		if (mod == null) {
			return "";
		}
		if (mod.equalsIgnoreCase("S")) {
			return "SEMIPRESENCIAL";
		} else if (mod.equalsIgnoreCase("V")) {
			return "VIRTUAL";
		} else if (mod.equalsIgnoreCase("P")) {
			return "PRESENCIAL";
		}
		return "";
	}
	public String fechaInicioFin(Date fecha1, Date fecha2) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String respuesta = "";
		if (fecha1 != null)
			respuesta = sdf.format(fecha1);
		else {
			return "";
		}
		if (fecha2 != null)
			respuesta = respuesta + " - " + sdf.format(fecha2);

		return respuesta;
	}
	public void limpiar(){
		resultado=null;
		comentario=null;
		primeraEntrada=false;
	}
	
	public String guardar(){
		try {
			SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			if(resultado==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar un resultado");
				return null;
			}
			if(comentario==null || comentario.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Dede Ingresar un Comentario");
				return null;
			}
			if(sufc.contieneCaracter(comentario.trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return null;
			}
			evaluacionInscPost.setResultado(resultado);
			evaluacionInscPost.setObservacion(comentario);
			evaluacionInscPost.setUsuEval(usuarioLogueado.getCodigoUsuario());
			evaluacionInscPost.setFechaEval(new Date());
			em.merge(evaluacionInscPost);
			em.flush();
			limpiar();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error inesperado: "+e.getMessage());
			return null ;
		}
	}
	
	
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTipoCapacitacion() {
		return tipoCapacitacion;
	}

	public void setTipoCapacitacion(String tipoCapacitacion) {
		this.tipoCapacitacion = tipoCapacitacion;
	}

	public String getIdMostrar() {
		return idMostrar;
	}

	public void setIdMostrar(String idMostrar) {
		this.idMostrar = idMostrar;
	}

	public Long getIdEvalIp() {
		return idEvalIp;
	}

	public void setIdEvalIp(Long idEvalIp) {
		this.idEvalIp = idEvalIp;
	}

	public EvaluacionInscPost getEvaluacionInscPost() {
		return evaluacionInscPost;
	}

	public void setEvaluacionInscPost(EvaluacionInscPost evaluacionInscPost) {
		this.evaluacionInscPost = evaluacionInscPost;
	}

	public PostulacionCap getPostulacionCap() {
		return postulacionCap;
	}

	public void setPostulacionCap(PostulacionCap postulacionCap) {
		this.postulacionCap = postulacionCap;
	}

	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}

	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public List<PostulacionCapAdj> getCapAdjLista() {
		return capAdjLista;
	}

	public void setCapAdjLista(List<PostulacionCapAdj> capAdjLista) {
		this.capAdjLista = capAdjLista;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
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

	public SeguridadUtilFormController getSeguridadUtilFormController() {
		return seguridadUtilFormController;
	}

	public void setSeguridadUtilFormController(
			SeguridadUtilFormController seguridadUtilFormController) {
		this.seguridadUtilFormController = seguridadUtilFormController;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public List<EvaluacionPart> getCapacEvalParteLista() {
		return capacEvalParteLista;
	}

	public void setCapacEvalParteLista(List<EvaluacionPart> capacEvalParteLista) {
		this.capacEvalParteLista = capacEvalParteLista;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
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

	public Long getIdAdjunto() {
		return idAdjunto;
	}

	public void setIdAdjunto(Long idAdjunto) {
		this.idAdjunto = idAdjunto;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public List<VerCursosEvaluacionPostulantes> getCapacitacionesEvalPostulantes() {
		return capacitacionesEvalPostulantes;
	}

	public void setCapacitacionesEvalPostulantes(
			List<VerCursosEvaluacionPostulantes> capacitacionesEvalPostulantes) {
		this.capacitacionesEvalPostulantes = capacitacionesEvalPostulantes;
	}

}
