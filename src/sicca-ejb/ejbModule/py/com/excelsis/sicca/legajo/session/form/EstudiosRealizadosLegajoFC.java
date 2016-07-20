package py.com.excelsis.sicca.legajo.session.form;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import enums.HorasAnios;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Especialidades;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.EstudiosRealizadosLegajo;
import py.com.excelsis.sicca.entity.IdiomasLegajoPersona;
import py.com.excelsis.sicca.entity.IdiomasPersona;
import py.com.excelsis.sicca.entity.InstitucionEducativa;
import py.com.excelsis.sicca.entity.NivelEstudio;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.entity.TituloAcademico;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.EnumAppHelper;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("estudiosRealizadosLegajoFC")
public class EstudiosRealizadosLegajoFC implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8554952821119561635L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	General general;

	private Long idPersona;
	private String texto;
	private Persona persona;
	private Long idPais;
	private Long idNivelEstudio;
	private Long idProfesion;
	private Long idIstUniversidad;
	private Long idIdioma;
	private Long idHabla;
	private Long idEscribe;
	private Long idLee;
	private Long idTipoDoc;
	private Long idTituloAcademico;
	private int indexUp;
	private EstudiosRealizadosLegajo estudiosRealizados = new EstudiosRealizadosLegajo();
	private List<EstudiosRealizadosLegajo> estudiosRealizadosList = new ArrayList<EstudiosRealizadosLegajo>();
	private List<IdiomasLegajoPersona> idiomasPersonasList = new ArrayList<IdiomasLegajoPersona>();
	private List<SelectItem> tituloSelecItem;
	private List<SelectItem> profesionSelecItem;
	private List<SelectItem> paisSelecItem;
	private List<SelectItem>  tipoDocumentos;
	private boolean esOtro;// para el titulo
	private boolean esOtroNivel;//para el Nivel de Estudios
	private boolean esOtraInst;
	private String nombrePantalla;
	private Long idDoc;
	
	private HorasAnios horasAnios;
	HorasAnios[] horasAniosCombo;
	private boolean habPais;
	private boolean esEdit = false;
	private boolean esNingunaInsNingunTit = false;// en le caso que se
													// seleccione ningun titulo
													// y ninguna institucion
	private boolean tieneTitulo = false;
	private Long idEstadoActual;
	private String estadoActual;
	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item;
	private String nombreDoc;
	private File inputFile;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;

	
	//NUEVOS DOCUMENTOS ADJUNTOS
	
	private Long idEstudiosRealizados;
	private List <Documentos> estudiosRealizadosDocList = new ArrayList();
	private String tituloDocumentoAdjunto;
	private Integer tamArchivo;
	private String from;
	
	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		
		estadoActual = "FINALIZADO";
		idEstadoActual = searchEstadoActual(estadoActual);
		tipoDocumentos = getDatosEspecificossByTipoDocumentoFPFACAndNivelEstudioSelectItems(idNivelEstudio);
		
			
		
		try {
			setear();
			persona = em.find(Persona.class, idPersona);
//			findEstudioPersona();
			paisSelecItem = new ArrayList<SelectItem>();
			idiomasPersonasList = new ArrayList<IdiomasLegajoPersona>();
			if (persona != null) {
				findEstudiosRealizados();
				findIdiomaPersona();
			}
			esEdit = false;
			getTituloAcademicos();
			upEspecialidad();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	//PARA AGREGAR DOCUMENTOS NUEVOS
	public void initAgregarDocumento(){
		
		this.estudiosRealizados = em.find(EstudiosRealizadosLegajo.class, this.idEstudiosRealizados);
		this.persona = em.find(Persona.class, estudiosRealizados.getPersona().getIdPersona());
		this.estudiosRealizadosDocList = obtenerDocumentosXEstudiosRealizados(this.idEstudiosRealizados);
		tipoDocumentos = getDatosEspecificossByTipoDocumentoFPFACAndNivelEstudioSelectItems(estudiosRealizados.getTituloAcademico().getNivelEstudio().getIdNivelEstudio());
		
	}
	
	private List<Documentos> obtenerDocumentosXEstudiosRealizados(Long idEstudiosRealizados){
		String sql = "select * from general.documentos where activo = true and (nombre_pantalla = 'EstudiosRealizadosLegajo' or nombre_pantalla = 'EstudiosRealizadosLegajo_edit') and nombre_tabla = 'EstudiosRealizadosLegajo' "
				+ "and id_tabla =  " + idEstudiosRealizados;
		
		return em.createNativeQuery(sql, Documentos.class).getResultList();
	}
	
	public void btnAgregarDocumento(){
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
		
		/**
		 * Para el Caso de documento adjuntos
		 * */
		if (item != null) {

		
			/**
			 * VALIDACION AGREGADA
			 * */
			String nomfinal = "";
			nomfinal = item.getFileName();
			String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
			List<TipoArchivo> tam = em.	createQuery("select t from TipoArchivo t "+ " where lower(t.extension) like '"	
													+ extension.toLowerCase() + "'").getResultList();
			if (!tam.isEmpty() && tam.get(0).getUnidMedidaByte() != null) {
				if (item.getFileSize() > tam.get(0).getUnidMedidaByte()
						.intValue()) {
					statusMessages.add(Severity.ERROR,"El archivo supera el tamaño máximo permitido. Límite "+ tam.get(0).getTamanho()+ tam.get(0).getUnidMedida()+ ", verifique");
					return;
				}
			}
			/**
			 * FIN
			 * */
			
			try {
				documento();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (idDoc != null) {

				if (idDoc.longValue() == -8) {
					statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return;
				}
				if (idDoc.longValue() == -7) {
					statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return;
				}
				if (idDoc.longValue() == -6) {
					statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
					return;
				}
				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDoc);
				doc.setPersona(persona);
//				doc.setNombreTabla("EstudiosRealizados");
				doc.setIdTabla(this.idEstudiosRealizados);
				em.merge(doc);
				em.flush();
				
				this.estudiosRealizadosDocList = obtenerDocumentosXEstudiosRealizados(this.idEstudiosRealizados);
				statusMessages.clear();
				statusMessages.add(Severity.INFO, "Documento adjuntado correctamente");

			} else {
				statusMessages.add(Severity.ERROR,"Error al adjuntar el Documento. Verifique");
				return;
			}

		}
		
		
	}
	
	public void eliminarDocumento(Long idDocumento){
		Documentos doc =  em.find(Documentos.class,idDocumento);
		doc.setActivo(false);
		em.merge(doc);
		em.flush();
		this.estudiosRealizadosDocList = obtenerDocumentosXEstudiosRealizados(this.idEstudiosRealizados);
		statusMessages.clear();
		statusMessages.add(Severity.INFO, "Documento eliminado correctamente");
	}
	
	public void imprimirDocumento(Long idDocumento){
		String usuario = "Invitado";
		
		if (usuarioLogueado != null )
			usuario = usuarioLogueado.getCodigoUsuario();
		
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDocumento,usuario);
		
		
	}
	
	public String volverEstudiosRealizados(){
		
		return "/legajos/MenuDatosPersonales.xhtml";
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getNivelesEstudioSelectItems(Long idNivel){
		String q ="";
		String descripcion="";
		String aux="or descripcion ilike '%otras%' or descripcion ilike '%ninguna%'";
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		
		if( idNivel != null && idNivel != 0){
			NivelEstudio nivelEstudio= em.find(NivelEstudio.class, idNivel);
			descripcion=nivelEstudio.getDescripcion().toLowerCase();
		}
		if(descripcion.contains("escolar"))
			 q = "select * from seleccion.institucion_educativa where descripcion not ilike '%universidad%' "+aux+"; ";
		else if((descripcion.contains("postgrado") || descripcion.contains("universitaria")) )
			 q= "select * from seleccion.institucion_educativa where descripcion ilike '%universidad%' "+aux+"; ";
		else
			 q= "select * from seleccion.institucion_educativa; ";
		
		List <InstitucionEducativa> resultList = new ArrayList<InstitucionEducativa>();
		resultList = em.createNativeQuery(q,InstitucionEducativa.class).getResultList();
		
		for( InstitucionEducativa ie : resultList){
			si.add(new SelectItem(ie.getIdInstEducativa(), ie.getDescripcion()));			
		}
		return si;
	}
	
	
	@SuppressWarnings("unchecked")
	public String update(){
		Documentos doc = new Documentos();
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
			if(idNivelEstudio==null ){
				statusMessages.add(Severity.ERROR,"Debe seleccionar un Nivel de Estudio. Verifique");
				return null;
			}
			if( idTituloAcademico==null ){
				statusMessages.add(Severity.ERROR,"Debe seleciconar el Titulo Obtenido");
				return null;
			}
			if(idIstUniversidad==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar una Institucion/Univ. Verifique");
				return null;
			}
			
			TituloAcademico ta= em.find(TituloAcademico.class, idTituloAcademico);
			InstitucionEducativa ie= em.find(InstitucionEducativa.class, idIstUniversidad);	
			if(!esNingunaInsNingunTit){
					if(existeEstudio("update")){
						statusMessages.add(Severity.ERROR,"El registro  ingresado ya existe, favor verificar");
						return null;
					}
			}
			
			
			if(habPais)
			{
				if(idPais==null){
					statusMessages.add(Severity.ERROR,"Debe seleccionar un Pais. Verifique");
					return null;
				}
//				if(estudiosRealizados.getFechaInicio()==null){
//					statusMessages.add(Severity.ERROR,"Debe ingresar la fecha de inicio. Verifique");
//					return null;
//				}
			}
			
			if(!ta.getDescripcion().toLowerCase().equals("ninguno")&& !ta.getDescripcion().toLowerCase().equals("ningunos")){
				
				if(ie.getDescripcion().toLowerCase().equals("ninguna")){
					statusMessages.add(Severity.ERROR,"Debe seleccionar la institucion. Verifique");
					return null;
				}
				if(idPais==null ){
					statusMessages.add(Severity.ERROR,"Seleccione el Pais. Verifique");
					return null;
				}
//				if(estudiosRealizados.getFechaInicio()==null ){
//					statusMessages.add(Severity.ERROR,"Ingrese la Fecha de Inicio. Verifique");
//					return null;
//				}
				
				/*if(estudiosRealizados.getFechaFin()==null && !estadoActual.equals("CURSANDO")){
					statusMessages.add(Severity.ERROR,"Ingrese la Fecha Fin. Verifique");
					return null;
				}*/
				
//				if(estudiosRealizados.getFechaFin()==null){
//					statusMessages.add(Severity.ERROR,"Ingrese la Fecha Fin. Verifique");
//					return null;
//				}
//				if(estudiosRealizados.getFechaFin()!=null&&estudiosRealizados.getFechaInicio().after(estudiosRealizados.getFechaFin())){
//					statusMessages.add(Severity.ERROR,"La fecha de Inicio no puede ser mayor a la Fecha Fin. Verifique");
//					return null;
//				}
				
				
				/*if(estudiosRealizados.getDuracionHoras()==null){
					statusMessages.add(Severity.ERROR,"Ingrese la Duración. Verifique");
					return null;
				}
				if(estudiosRealizados.getDuracionHoras()!=null && estudiosRealizados.getDuracionHoras().intValue()<=0){
					statusMessages.add(Severity.ERROR,"La Duración ingresada debe ser mayor a cero. Verifique");
					return null;
				}*/
				if(idTipoDoc==null){
					statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
					return null;
				}	
				
			}
			if(!tieneTitulo){
				estudiosRealizados.setFechaFin(null);
				estudiosRealizados.setDuracionHoras(null);
				horasAnios=HorasAnios.Anios;
			}
			if(esOtraInst && (estudiosRealizados.getOtraInstit()==null || estudiosRealizados.getOtraInstit().trim().equals(""))){
				statusMessages.add(Severity.ERROR,"Debe ingresar en el campo Otra de Institucion antes de agregar . Verifique");
				return null ;
			}
			if(esOtro && (estudiosRealizados.getOtroTituloObt()==null || estudiosRealizados.getOtroTituloObt().trim().equals(""))){
				if(!esOtroNivel){
					statusMessages.add(Severity.ERROR,"Debe ingresar en el campo Otros de Titulo antes de agregar. Verifique");
					return null ;
				}else
					estudiosRealizados.setOtroTituloObt(estudiosRealizados.getOtroNivel());
			}
			
			if(item==null && inputFile==null && idTipoDoc!=null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar el archivo. Verifique");
				return null;
			}
	//			if(estudiosRealizados.getFechaInicio()!=null){
	//				if( !general.isFechaValida(estudiosRealizados.getFechaInicio())){
	//					statusMessages.clear();
	//					statusMessages.add(Severity.ERROR, "Fecha de Inicio inválida");
	//					return null ;
	//				}
	//				if(estudiosRealizados.getFechaInicio().after(new Date())){
	//					statusMessages.clear();
	//					statusMessages.add(Severity.ERROR,"La Fecha de Inicio no puede ser mayor a la fecha actual.  Verifique ");
	//					return null;
	//				}
	//			}
	//			if(estudiosRealizados.getFechaInicio()!=null && estudiosRealizados.getFechaFin()!=null){
	//				if(estudiosRealizados.getFechaInicio().after(estudiosRealizados.getFechaFin())){
	//					statusMessages.add(Severity.ERROR,"Verifique la Fecha Inicio y Fecha Fin");
	//					return null;
	//				}
	//			}
			
			if(estudiosRealizados.getFechaFin()!=null ){
				if( !general.isFechaValida(estudiosRealizados.getFechaFin())){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Fecha Fin invalida");
					return null;
				}
				if(estudiosRealizados.getFechaFin().after(new Date())){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,"La Fecha Fin no puede ser mayor a la fecha actual.  Verifique ");
					return null;
				}
			}
			if(estudiosRealizados.getDuracionHoras()!=null && estudiosRealizados.getDuracionHoras().intValue()<=0){
				statusMessages.add(Severity.ERROR,"La Duracion ingresada debe ser mayor a cero. Verifique");
				return null;
			}
			estudiosRealizados.setActivo(true);
			if(idProfesion!=null)
				estudiosRealizados.setEspecialidades((em.find(Especialidades.class, idProfesion)));
			else
				estudiosRealizados.setEspecialidades(null);
			if(ta.getDescripcion().toLowerCase().equals("ninguno")|| ta.getDescripcion().toLowerCase().equals("ningunos"))
				estudiosRealizados.setFinalizo(false);
			else
				estudiosRealizados.setFinalizo(true);
			if(horasAnios!=null)
				estudiosRealizados.setTipoDuracion(horasAnios.getValor());
			estudiosRealizados.setFechaMod(new Date());
			estudiosRealizados.setUsuMod(usuarioLogueado.getCodigoUsuario());
			estudiosRealizados.setPersona(persona);
			estudiosRealizados.setEstadoActual(em.find(Referencias.class, idEstadoActual).getDescAbrev());
			if(estudiosRealizados.getEstadoActual().equalsIgnoreCase("CURSANDO"))
				estudiosRealizados.setFechaFin(null);
						estudiosRealizados.setInstitucionEducativa(em.find(InstitucionEducativa.class, idIstUniversidad));
			estudiosRealizados.setTituloAcademico(em.find(TituloAcademico.class,idTituloAcademico));
			
			//SI NO TIENE TITULO NI iNSTI. INACTIVA EL DOC. 
			if(esNingunaInsNingunTit){
				if(estudiosRealizados.getDocumentos()!=null){
					Documentos docIna=em.find(Documentos.class, estudiosRealizados.getDocumentos().getIdDocumento());
					docIna.setActivo(false);
					docIna.setFechaMod(new Date());
					docIna.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(docIna);
					em.flush();
				}
				estudiosRealizados.setDocumentos(null);
				
				
			}
			
			/**
			 *PARA EL CASO DE DOCUMENTO ADJUNTO 
			 *
			 **/
			Long idDoc=null;
			if(item!=null){
				/**
				 * VALIDACION AGREGADA
				 * */
				String nomfinal = "";
				nomfinal = item.getFileName();
				 String extension = nomfinal.substring( nomfinal.lastIndexOf( "." ) );
					List<TipoArchivo> tam = em
							.createQuery(
									"select t from TipoArchivo t "
											+ " where lower(t.extension) like '" 
											+ extension.toLowerCase() + "'").getResultList();
					if (!tam.isEmpty() && tam.get(0).getUnidMedidaByte() != null) {
						if (item.getFileSize() > tam.get(0).getUnidMedidaByte()
								.intValue()) {
							statusMessages
									.add(Severity.ERROR,"El archivo supera el tamaño máximo permitido. Límite "+tam.get(0).getTamanho()+tam.get(0).getUnidMedida()+", verifique");
							return null;
						}
					}
					/**
					 * FIN
					 * */
			}
			Documentos docDB=em.find(Documentos.class, estudiosRealizados.getDocumentos().getIdDocumento());
			if(inputFile!=null && item!=null){//habia un archivo y se modifica
				
				if(estudiosRealizados.getDocumentos()!=null){
					if(!docDB.getNombreFisicoDoc().toLowerCase().equals(nombreDoc.toLowerCase()) || 
							!docDB.getTamanhoDoc().equals(String.valueOf(item.getFileSize()))){//significa que ubo algun cambio se envia los parametros del item
						idDoc=AdmDocAdjuntoFormController.editar(item.getFile(),Integer.valueOf(item.getFileSize()),item.getFileName(),item.getContentType(), docDB.getUbicacionFisica(), "EstudiosRealizadosLegajo_edit", docDB.getIdDocumento(), idTipoDoc, usuarioLogueado.getIdUsuario(), estudiosRealizados.getIdEstudiosRealizados(),"EstudiosRealizadosLegajo");
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
							doc = em.find(Documentos.class, idDoc);
							doc.setPersona(persona);
							doc.setIdTabla(estudiosRealizados.getIdEstudiosRealizados());
							doc.setValidoLegajo(true);
							em.merge(doc);
							em.flush();
							estudiosRealizados.setDocumentos(em.find(Documentos.class,idDoc));
								
						}else{
							statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
							return null;
						}
					}//porque no hubo modificacion! nohace  nada! 
					
				}
			}
			
			if(item == null & idTipoDoc != docDB.getDatosEspecificos().getIdDatosEspecificos()) {
				docDB.setDatosEspecificos(em.find(DatosEspecificos.class, idTipoDoc));
				em.merge(docDB);
				em.flush();
			}
			
			if (inputFile == null && item != null) {//no habia ningun archivo y se carga
				if (estudiosRealizados.getDocumentos() == null) {
					String separador = File.separator;
					String direccionFisica = separador
							+ "LEGAJO" + separador
							+ persona.getDocumentoIdentidad() + "_"
							+ persona.getIdPersona()+ separador+"ESTUDIOS_REALIZADOS"+ separador;
					idDoc = AdmDocAdjuntoFormController.editar(item.getFile(),
							item.getFileSize(), item.getFileName(),
							item.getContentType(), direccionFisica,
							"EstudiosRealizadosLegajo_edit", null,
							idTipoDoc, usuarioLogueado.getIdUsuario(),
							estudiosRealizados.getIdEstudiosRealizados(),
							"EstudiosRealizadosLegajo");
					if(idDoc!=null){
						
						if(idDoc.longValue()==-8){
							statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
							return null;					
						}
						if(idDoc.longValue()==-7){
							statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tama�o permitido. Seleccione otro");
							return null;	
						}
						if(idDoc.longValue()==-6){
							statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
							return null;	
						}
						doc = em.find(Documentos.class, idDoc);
						doc.setPersona(persona);
						doc.setIdTabla(estudiosRealizados.getIdEstudiosRealizados());
						doc.setValidoLegajo(true);
						em.merge(doc);
						em.flush();
						estudiosRealizados.setDocumentos(em.find(Documentos.class,idDoc));
							
					}else{
						statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
						return null;
					}

				}

			}
			em.merge(estudiosRealizados);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			
			limpiar();
			/*tab6VistaPreliminarFormController.init();
			tab4AdjuntarDocumentosFormController.init();
			*/
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error al actualizar. Verifique");
		}
		return null;
	}
	
	public void cancelar() {
		estudiosRealizados = new EstudiosRealizadosLegajo();
		idNivelEstudio = null;
		idPais = null;
		idProfesion = null;
		idIstUniversidad = null;
		idTituloAcademico = null;
		idTipoDoc = null;
		nombreDoc = null;
		horasAnios = HorasAnios.Anios;
		habPais = false;
		uploadedFile = null;
		item = null;
		esNingunaInsNingunTit = false;
		esOtraInst = false;
		esOtro = false;
		tieneTitulo = true;
		fileName = null;
		contentType = null;
		esEdit = false;
		estudiosRealizadosList = new ArrayList<EstudiosRealizadosLegajo>();
		em.clear();
		findEstudiosRealizados();
	}


	public void editar(int index) {
		esEdit = true;

		estudiosRealizados = em.find(EstudiosRealizadosLegajo.class,estudiosRealizadosList.get(index).getIdEstudiosRealizados());

		idNivelEstudio = estudiosRealizados.getTituloAcademico().getNivelEstudio().getIdNivelEstudio();
		estadoActual=estudiosRealizados.getEstadoActual();
		idEstadoActual = searchEstadoActual(estadoActual);
//		if(estudiosRealizados.getTituloAcademico().getNivelEstudio().getDescripcion().trim().toLowerCase().equalsIgnoreCase("Cursos, charlas y conferencias") 
//				|| estudiosRealizados.getTituloAcademico().getNivelEstudio().getDescripcion().trim().toLowerCase().equalsIgnoreCase("Jornadas, talleres y seminarios"))
		if(estudiosRealizados.getTituloAcademico().getNivelEstudio().getIdNivelEstudio() == 9)
			esOtroNivel = true;
		else
			esOtroNivel = false;
		idTituloAcademico = estudiosRealizados.getTituloAcademico().getIdTituloAcademico();
		idIstUniversidad = estudiosRealizados.getInstitucionEducativa().getIdInstEducativa();
		tipoDocumentos = getDatosEspecificossByTipoDocumentoFPFACAndNivelEstudioSelectItems(idNivelEstudio);
		getTituloAcademicos();

		
		if (estudiosRealizados.getPais() != null) {
			idPais = estudiosRealizados.getPais().getIdPais();
		}
		TituloAcademico ta = em.find(TituloAcademico.class, idTituloAcademico);
		if (idTituloAcademico != null && idIstUniversidad != null) {
			if (estudiosRealizados.getInstitucionEducativa().getDescripcion().toLowerCase().equals("ninguna")) {
				habPais = false;
				if (ta.getDescripcion().toLowerCase().equals("ninguno")) {
					esNingunInstitucionTituto();
				} else
					esNingunaInsNingunTit = false;
			} else
				habPais = true;
		}
		if (ta.getDescripcion().toLowerCase().equals("ninguno")) {
			noTienTitulo();
		} else
			tieneTitulo = true;
		if (estudiosRealizados.getTituloAcademico().getDescripcion().toLowerCase().equals("otros"))
			esOtro = true;
		else
			esOtro = false;
		if (estudiosRealizados.getInstitucionEducativa().getDescripcion().toLowerCase().equals("otras")	|| estudiosRealizados.getInstitucionEducativa().getDescripcion().toLowerCase().equals("otra"))
			esOtraInst = true;
		else
			esOtraInst = false;
		if (estudiosRealizados.getEspecialidades() != null)
			idProfesion = estudiosRealizados.getEspecialidades().getIdEspecialidades();
		if (estudiosRealizados.getTipoDuracion() != null)
			horasAnios = HorasAnios.getHorasAniosPorId(estudiosRealizados.getTipoDuracion());
		if (idPais != null) {
			estudiosRealizados.setPais(em.find(Pais.class, idPais));
		}

		
		
		idTituloAcademico = estudiosRealizados.getTituloAcademico().getIdTituloAcademico();
		if (idProfesion != null)
			upEspecialidad();
		if (estudiosRealizados.getDocumentos() != null) {
			Documentos doc = em.find(Documentos.class, estudiosRealizados.getDocumentos().getIdDocumento());
			String url = doc.getUbicacionFisica();
			if (url.contains("\\")) {
				url = url.replace("\\", System.getProperty("file.separator"));

			}
			if (url.contains("//")) {
				url = url.replace("//", System.getProperty("file.separator"));

			}
			String separador = System.getProperty("file.separator");
			if (!url.endsWith(separador))
				url = url + System.getProperty("file.separator");

			url += doc.getNombreFisicoDoc();

			inputFile = new File(url);
			idTipoDoc = doc.getDatosEspecificos().getIdDatosEspecificos();
			nombreDoc = doc.getNombreFisicoDoc();

		} else
			inputFile = null;
		if (estudiosRealizados.getPais() != null) {
			idPais = estudiosRealizados.getPais().getIdPais();
			upInstitucionOtro();
		}
		indexUp = index;
		
	}
	
	@SuppressWarnings("unchecked")
	public void addEstudios() {
		try {

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

			if (idNivelEstudio == null) {
				statusMessages.add(Severity.ERROR,"Debe seleccionar un Nivel de Estudio. Verifique");
				return;
			}
			if (idTituloAcademico == null && !esOtroNivel) {
				statusMessages.add(Severity.ERROR,"Debe seleccionar el Titulo Obtenido");
				return;
			}
			if (idIstUniversidad == null) {
				statusMessages.add(Severity.ERROR,"Debe seleccionar una Institucion/Univ. Verifique");
				return;
			}
			if(esOtroNivel && (estudiosRealizados.getOtroTituloObt() == null || estudiosRealizados.getOtroTituloObt().isEmpty())) {
				statusMessages.add(Severity.ERROR,"Debe completar la Descripcion Charla/Conferencia");
				return;
			}
			TituloAcademico ta = null;
			ta = em.find(TituloAcademico.class, idTituloAcademico);

			if (habPais) {
				if (idPais == null) {
					statusMessages.add(Severity.ERROR,"Debe seleccionar un Pais. Verifique");
					return;
				}
		//		if (estudiosRealizados.getFechaInicio() == null) {
		//			statusMessages.add(Severity.ERROR,"Debe ingresar la fecha de inicio. Verifique");
		//			return;
		//		}
		
			}
			InstitucionEducativa ie = em.find(InstitucionEducativa.class,idIstUniversidad);
			if (ta != null && !ta.getDescripcion().toLowerCase().equals("ninguno")&& !ta.getDescripcion().toLowerCase().equals("ningunos")) {// si tiene titulo

				if (ie.getDescripcion().toLowerCase().equals("ninguna")) {
					statusMessages.add(Severity.ERROR,"Debe seleccionar la institucion. Verifique");
					return;
				}

				if (idPais == null) {
					statusMessages.add(Severity.ERROR,"Seleccione el Pais. Verifique");
					return;
				}
//				if (estudiosRealizados.getFechaInicio() == null) {
//					statusMessages.add(Severity.ERROR,"Ingrese la Fecha de Inicio. Verifique");
//					return;
//				}
//				if(estudiosRealizados.getFechaFin()==null && !estadoActual.equals("CURSANDO")){
//					statusMessages.add(Severity.ERROR,"Ingrese la Fecha Fin. Verifique");
//					return;
//				}
//				if (estudiosRealizados.getFechaFin() != null && estudiosRealizados.getFechaInicio().after(estudiosRealizados.getFechaFin())) {
//					statusMessages.add(Severity.ERROR,"La fecha de Inicio no puede ser mayor a la Fecha Fin. Verifique");
//					return;
//				}

//				if (estudiosRealizados.getDuracionHoras() == null) {
//					statusMessages.add(Severity.ERROR,"Ingrese la Duración. Verifique");
//					return;
//				}
				/*if (estudiosRealizados.getDuracionHoras() != null && estudiosRealizados.getDuracionHoras().intValue() <= 0) {
					statusMessages.add(Severity.ERROR,"La Duración ingresada debe ser mayor a cero. Verifique");
					return;
				}*/
				if (idTipoDoc == null) {
					statusMessages
							.add(Severity.ERROR,
									"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
					return;
				}

			}
			if (!tieneTitulo) {
				estudiosRealizados.setFechaFin(null);
				estudiosRealizados.setDuracionHoras(null);
				horasAnios = HorasAnios.Anios;
			}
			if (!esNingunaInsNingunTit) {
				if (existeEstudio("save")) {
					statusMessages
							.add(Severity.ERROR,"El registro  ingresado ya existe, favor verificar");
					return;
				}
			}

			if (esOtraInst	&& (estudiosRealizados.getOtraInstit() == null || estudiosRealizados.getOtraInstit().trim().equals(""))) {
				statusMessages.add(Severity.ERROR,"Debe ingresar en el campo Otra de Institucion  antes de agregar . Verifique");
				return;
			}
			if (esOtro && (estudiosRealizados.getOtroTituloObt() == null || estudiosRealizados.getOtroTituloObt().trim().equals(""))) {
				if(!esOtroNivel){
					statusMessages.add(Severity.ERROR,"Debe ingresar en el campo Otros de Titulo antes de agregar. Verifique");
					return;
				}else
					estudiosRealizados.setOtroTituloObt(estudiosRealizados.getOtroNivel());
			}
			if (item != null && idTipoDoc == null) {
				statusMessages.add(Severity.ERROR,"Debe seleccionar el Tipo de documento. Verifique");
				return;
			}
			if (item == null && idTipoDoc != null) {
				statusMessages.add(Severity.ERROR,"Debe seleccionar el archivo. Verifique");
				return;
			}
			/*if (estudiosRealizados.getFechaInicio() != null) {
				if (!general.isFechaValida(estudiosRealizados.getFechaInicio())) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,"Fecha de Inicio inválida. Verifique");
					return;
				}
				if (estudiosRealizados.getFechaInicio().after(new Date())) {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,"La Fecha de Inicio no puede ser mayor a la fecha actual.  Verifique ");
					return;
				}
			}
			if (estudiosRealizados.getFechaInicio() != null	&& estudiosRealizados.getFechaFin() != null) {
				if (estudiosRealizados.getFechaInicio().after(estudiosRealizados.getFechaFin())) {
					statusMessages.add(Severity.ERROR,"La Fecha de Inicio no puede ser mayor a la fecha Fin. Verifique");
					return;
				}
			}*/

			if (estudiosRealizados.getFechaFin() != null) {
				if (!general.isFechaValida(estudiosRealizados.getFechaFin())) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Fecha Fin invalida");
					return;
				}
				if (estudiosRealizados.getFechaFin().after(new Date())) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,"La Fecha Fin no puede ser mayor a la fecha actual.  Verifique ");
					return;
				}
			}

			if (estudiosRealizados.getDuracionHoras() != null && estudiosRealizados.getDuracionHoras().intValue() <= 0) {
				statusMessages.add(Severity.ERROR,"La Duracion ingresada debe ser mayor a cero. Verifique");
				return;
			}

			
			
			
			
			/**
			 * Para el Caso de documento adjuntos
			 * */
			if (item != null) {

			
				/**
				 * VALIDACION AGREGADA
				 * */
				String nomfinal = "";
				nomfinal = item.getFileName();
				String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
				List<TipoArchivo> tam = em.
						createQuery(	
								"select t from TipoArchivo t "
									+ " where lower(t.extension) like '"
									+ extension.toLowerCase() + "'").getResultList();
				if (!tam.isEmpty() && tam.get(0).getUnidMedidaByte() != null) {
					if (item.getFileSize() > tam.get(0).getUnidMedidaByte()
							.intValue()) {
						statusMessages.add(Severity.ERROR,"El archivo supera el tama�o maximo permitido. Limite "+ tam.get(0).getTamanho()+ tam.get(0).getUnidMedida()+ ", verifique");
						return;
					}
				}
				/**
				 * FIN
				 * */
				
				documento();
				if (idDoc != null) {

					if (idDoc.longValue() == -8) {
						statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return;
					}
					if (idDoc.longValue() == -7) {
						statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tama�o permitido. Seleccione otro");
						return;
					}
					if (idDoc.longValue() == -6) {
						statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
						return;
					}
					Documentos doc = new Documentos();
					doc = em.find(Documentos.class, idDoc);
					doc.setPersona(persona);
//					doc.setNombreTabla("EstudiosRealizados");
					doc.setIdTabla(estudiosRealizados.getIdEstudiosRealizados());
					em.merge(doc);
					em.flush();
					estudiosRealizados.setDocumentos(doc);
					

				} else {
					statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
					return;
				}

			}
			if (ta.getDescripcion().toLowerCase().equals("ninguno")	|| ta.getDescripcion().toLowerCase().equals("ningunos"))
				estudiosRealizados.setFinalizo(false);
			else
				estudiosRealizados.setFinalizo(true);
			if (horasAnios != null)
				estudiosRealizados.setTipoDuracion(horasAnios.getValor());
			estudiosRealizados.setActivo(true);
			if (idProfesion != null)
				estudiosRealizados.setEspecialidades((em.find(Especialidades.class, idProfesion)));
			estudiosRealizados.setEstadoActual(em.find(Referencias.class, idEstadoActual).getDescAbrev());
			estudiosRealizados.setFechaAlta(new Date());
			estudiosRealizados.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			estudiosRealizados.setPersona(persona);
			estudiosRealizados.setInstitucionEducativa(em.find(InstitucionEducativa.class, idIstUniversidad));
			estudiosRealizados.setTituloAcademico(em.find(TituloAcademico.class, idTituloAcademico));
			if (habPais && idPais != null) {
				estudiosRealizados.setPais(em.find(Pais.class, idPais));
			}
			estudiosRealizadosList.add(estudiosRealizados);
			em.persist(estudiosRealizados);
			em.flush();
			if (estudiosRealizados.getDocumentos() != null && idDoc != null) {
				Documentos doc = em.find(Documentos.class, idDoc);
				doc.setIdTabla(estudiosRealizados.getIdEstudiosRealizados());
//				doc.setNombreTabla("EstudiosRealizados");
//				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
//				doc.setFechaMod(new Date());
//				doc.setPersona(persona);
				doc.setValidoLegajo(true);
				em.merge(doc);
				em.flush();
			}

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se puedo agregar el registro, verifique");
		}

		limpiar();
	}
		
	public void limpiar() {
		estudiosRealizados = new EstudiosRealizadosLegajo();
		
		idIstUniversidad = null;
		idNivelEstudio = null;
		idPais = null;
		idTituloAcademico = null;
		idProfesion = null;
		idTipoDoc = null;
		horasAnios = HorasAnios.Anios;
//		tab6VistaPreliminarFormController.init();
//		tab4AdjuntarDocumentosFormController.init();
		habPais = false;
		uploadedFile = null;
		nombreDoc = null;
		item = null;
		esNingunaInsNingunTit = false;
		esOtraInst = false;
		esOtro = false;
		esOtroNivel = false;
		tieneTitulo = true;
		fileName = null;
		contentType = null;
		esEdit = false;
		findEstudiosRealizados();
	}
	
	
	public void documento() throws NamingException {
		nombrePantalla = "EstudiosRealizadosLegajo";
		String separador = File.separator;
		String direccionFisica = separador
				+ "LEGAJO" + separador
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona()+ separador+"ESTUDIOS_REALIZADOS"+ separador;
		idDoc = AdmDocAdjuntoFormController.guardarDirecto(item,direccionFisica, nombrePantalla, idTipoDoc,	usuarioLogueado.getIdUsuario(), "EstudiosRealizadosLegajo");

	}
	
	public void abrirDoc(int index) {

		EstudiosRealizadosLegajo e = estudiosRealizadosList.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumentos().getIdDocumento(), usuarioLogueado.getIdUsuario());

	}
	
	public void delItems(int index) {
		EstudiosRealizadosLegajo e = estudiosRealizadosList.get(index);
		e.setActivo(false);
		e.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		e.setFechaAlta(new Date());
		if (e.getDocumentos() != null) {
			Documentos aux = e.getDocumentos();
			aux.setActivo(false);
			aux.setFechaMod(new Date());
			aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(aux);
			em.flush();
		}

		em.merge(e);
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		estudiosRealizadosList.remove(index);
		/*
		 * tab6VistaPreliminarFormController.init();
		 * tab4AdjuntarDocumentosFormController.init();
		 */
	}
	
	public void addIdiomas(){
		try {
			if(idIdioma==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Item Idioma");
				return;
			}
			if(idHabla==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Item Habla");
				return;
			}
			if(idEscribe==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Item Escribe");
				return;
			}
			
			if(idLee==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Item Lee");
				return;
			}
			if(existeIdiomaCIO()){
				statusMessages.add(Severity.ERROR,"El registro  ingresado ya existe, favor verificar");
				return;
			}
			Referencias escribe=em.find(Referencias.class, idEscribe);
			Referencias habla= em.find(Referencias.class, idHabla);
			Referencias lee=em.find(Referencias.class, idLee);
			IdiomasLegajoPersona idiomasPersona= new IdiomasLegajoPersona();
			idiomasPersona.setDatosEspecificos(em.find(DatosEspecificos.class, idIdioma));
			idiomasPersona.setEscribe(escribe.getDescAbrev());
			idiomasPersona.setHabla(habla.getDescAbrev());
			idiomasPersona.setLee(lee.getDescAbrev());
			idiomasPersona.setPersona(persona);
			idiomasPersona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			idiomasPersona.setFechaAlta(new Date());
			idiomasPersona.setActivo(true);
			idiomasPersonasList.add(idiomasPersona);
			em.persist(idiomasPersona);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			idEscribe=null;
			idHabla=null;
			idIdioma=null;
			idLee=null;
			//tab6VistaPreliminarFormController.init();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private void setear() {
		estudiosRealizadosList = new ArrayList<EstudiosRealizadosLegajo>();
		estudiosRealizados = new EstudiosRealizadosLegajo();
		estudiosRealizados.setFinalizo(false);
		idEscribe = null;
		idHabla = null;
		idIdioma = null;
		idIstUniversidad = null;
		idLee = null;
		idNivelEstudio = null;
		idPais = null;
		idProfesion = null;
		idTituloAcademico = null;
		esOtro = false;

	}
	@SuppressWarnings("unchecked")
	private void findEstudioPersona() {
		estudiosRealizadosList = em.createQuery("select e from EstudiosRealizadosLegajo e where "
						+ " e.persona.idPersona =" + persona.getIdPersona()	+ " and e.activo=true order by e.fechaFin desc").getResultList();
	}
	@SuppressWarnings("unchecked")
	private void findIdiomaPersona() {
		idiomasPersonasList = em.createQuery("select e from IdiomasLegajoPersona e where "
						+ " e.persona.idPersona =" + persona.getIdPersona()	+ " AND e.activo = true").getResultList();
	}
	
	public String descInstitucionEducativa(EstudiosRealizadosLegajo estudio){
		if(estudio.getInstitucionEducativa().getDescripcion().equalsIgnoreCase("OTRAS"))
			return estudio.getOtraInstit();
		else
			return estudio.getInstitucionEducativa().getDescripcion();
	}
	
	public List<TituloAcademico> upTituloAcademico() {
		if(idNivelEstudio != null){
			NivelEstudio ta= em.find(NivelEstudio.class, idNivelEstudio);
			if(ta.getIdNivelEstudio().intValue()==9){ //"otros estudios" o como se llame ahora.
				esOtroNivel=true;
				esOtro=false;
				idTituloAcademico = new Long(117); //"Otros"
			} else {
				esOtroNivel=false;
				idTituloAcademico=null;
			}
			
			tipoDocumentos = getDatosEspecificossByTipoDocumentoFPFACAndNivelEstudioSelectItems(idNivelEstudio);
		}
		return getTituloAcademicos();
	}

	
	@SuppressWarnings("unchecked")
	private void findEstudiosRealizados() {
		estudiosRealizadosList = em.createQuery(
				" select d from EstudiosRealizadosLegajo d where d.persona.idPersona= "
						+ persona.getIdPersona() + ""
						+ " and  d.activo= true order by d.fechaFin desc")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<TituloAcademico> getTituloAcademicos() {
		List<TituloAcademico> ti= new Vector<TituloAcademico>();
		try{
			if(idNivelEstudio!=null){
				setIdNivelEstudio(idNivelEstudio);
				ti= em.createQuery(" select o from " + TituloAcademico.class.getName() + " o where o.activo = true " +
					" and o.nivelEstudio.idNivelEstudio="+idNivelEstudio+" order by o.descripcion").getResultList();
			}
			 getTituloAcademicoSelectItems(ti);
			 habFechaFin();
			return ti;
		}catch(Exception ex){
			ex.printStackTrace();
			return ti;
		}
	}

	
	
	public List<SelectItem> getTituloAcademicoSelectItems(List<TituloAcademico> ti) {
		List<SelectItem> si = new Vector<SelectItem>();
		tituloSelecItem= new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(TituloAcademico o : ti)
			si.add(new SelectItem(o.getIdTituloAcademico(), o.getDescripcion()));
		
		tituloSelecItem=si;
		
		//idTituloAcademico=null;
//		if(idTituloAcademico!=null){
//			TituloAcademico ta= em.find(TituloAcademico.class, idTituloAcademico);
//			if(ta.getDescripcion().toLowerCase().equals("otros"))
//				esOtro=true;
//		}else{
//			esOtro=false;
//			estudiosRealizados.setOtroTituloObt(null);
//			esNingunaInsNingunTit=false;
//		}

		upEspecialidad();
		return si;
	}
	
	
	public List<SelectItem> getDatosEspecificossByTipoDocumentoFPFACAndNivelEstudioSelectItems(Long idNivelEstudio) {
		
		String sql = "select * from seleccion.datos_especificos where id_datos_generales = 2 and valor_alf = 'FPFAC' and activo is true ";
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(0, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		
		if(idNivelEstudio != null && idNivelEstudio != 0){
							
			NivelEstudio nivelEstudio = em.find(NivelEstudio.class, idNivelEstudio);
			if(!nivelEstudio.getDescripcion().toUpperCase().contains("POSTGRADO") 
					&& !nivelEstudio.getDescripcion().toUpperCase().contains("CHARLAS")){
				sql += " and valor_num = " + nivelEstudio.getIdNivelEstudio();
				sql += " or (id_datos_generales = 2  and valor_alf = 'FPFAC'  and valor_num  = 0) ";//Agrega  "COPIA DE CERTIFICADOS O CONSTANCIAS DE ESTUDIOS"
				
			}else if (nivelEstudio.getDescripcion().toUpperCase().contains("POSTGRADO")){
				sql += " and valor_num is null";
				sql += " or (id_datos_generales = 2  and valor_alf = 'FPFAC'  and valor_num  = 0) ";//Agrega  "COPIA DE CERTIFICADOS O CONSTANCIAS DE ESTUDIOS"
			}else if (nivelEstudio.getDescripcion().toUpperCase().contains("CHARLAS")){
				sql += " and valor_num = " + nivelEstudio.getIdNivelEstudio();
			}
			
		}
			
		List<DatosEspecificos> listaDE = em.createNativeQuery(sql, DatosEspecificos.class).getResultList();
		
		
		for (DatosEspecificos o : listaDE)
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		
		
		return si;
	}

	
	@SuppressWarnings("unchecked")
	public List<Especialidades> upEspecialidad() {
		List<Especialidades> es= new ArrayList<Especialidades>();
		if(idTituloAcademico!=null){
			TituloAcademico ta= em.find(TituloAcademico.class, idTituloAcademico);
			if(ta.getDescripcion().toLowerCase().equals("otros"))
				esOtro=true;
			else
				esOtro=false;
		
			if(ta.getDescripcion().toLowerCase().equals("ninguno")){
				noTienTitulo();
			}else
				tieneTitulo=true;
				
			if(idIstUniversidad!=null){
				InstitucionEducativa ie= em.find(InstitucionEducativa.class, idIstUniversidad);
				if(ie.getDescripcion().toLowerCase().equals("ninguna")){
					habPais=false;
					if(ta.getDescripcion().toLowerCase().equals("ninguno")){
						esNingunInstitucionTituto();//selecciono ningun titulo ninguna institucion
						
					}else{
						esNingunaInsNingunTit=false;
					
					}
				}else
					habPais=true;
			}else
				habPais=false;

		}else{
			esNingunaInsNingunTit=false;
			
			noTienTitulo();
			if(idIstUniversidad!=null){
				InstitucionEducativa ie= em.find(InstitucionEducativa.class, idIstUniversidad);
				if(ie.getDescripcion().toLowerCase().equals("ninguna"))
					habPais=false;
				else
					habPais=true;
				
			}else
				habPais=false;
		}
			
		
		
		
		habFechaFin();
		return es;

	}

	private void noTienTitulo() {
		tieneTitulo = false;
		esOtro = false;
		horasAnios = null;
	}

	private void esNingunInstitucionTituto() {
		idPais = null;
		habPais = false;
		idTipoDoc = null;
		estudiosRealizados.setFechaInicio(null);
		estudiosRealizados.setFechaFin(null);
		estudiosRealizados.setDuracionHoras(null);
		esNingunaInsNingunTit = true;
		nombreDoc = null;
		horasAnios = HorasAnios.Anios;
	}

	@SuppressWarnings("unchecked")
	public List<Pais> upInstitucionOtro() {
		List<Pais> pais= new ArrayList<Pais>();
		if(idIstUniversidad!=null){
			InstitucionEducativa ie= em.find(InstitucionEducativa.class, idIstUniversidad);
			List<InstitucionEducativa> ieList= em.createQuery("select t from InstitucionEducativa t " +
					" where lower(t.descripcion) like '"+ie.getDescripcion().toLowerCase()+"'").getResultList();
			habPais=true;
			esNingunaInsNingunTit=false;
			if(ie.getDescripcion().toLowerCase().equals("otras") ||ie.getDescripcion().toLowerCase().equals("otra")){
				esOtraInst=true;
				
			}else
				esOtraInst=false;
			if(ie.getDescripcion().toLowerCase().equals("ninguna"))
				habPais=false;
			else
				habPais=true;
			
			if(idTituloAcademico!=null){
					TituloAcademico ta= em.find(TituloAcademico.class, idTituloAcademico);
					if(ie.getDescripcion().toLowerCase().equals("ninguna") && ta.getDescripcion().toLowerCase().equals("ninguno")){
						esNingunInstitucionTituto();
					}else
						esNingunaInsNingunTit=false;
						
			}else
				habPais=true;
			
				
				
			if(!esOtraInst){
				if(ie.getDescripcion().equalsIgnoreCase("CENTRO LATINOAMERICANO DE ADMINISTRACION PARA EL DESARROLLO (CLAD)")){
					pais= em.createQuery("Select p from Pais p " +
							"where p.activo = true order by p.descripcion").getResultList();
				}else{
					for (int i = 0; i < ieList.size(); i++) {
						if(ieList.get(i).getPais()!=null){
							Pais p= em.find(Pais.class, ieList.get(i).getPais().getIdPais());
							pais.add(p);
						}
					}
				}
			}else{
				pais= em.createQuery("Select p from Pais p " +
						"where p.activo = true order by p.descripcion").getResultList();
			}
			
			
			
		}else
			habPais=false;
		
		getPaisInstSelectItems(pais);
		
		return pais;
		
	}
	
	
	public List<SelectItem> getPaisInstSelectItems(List<Pais> p) {
		List<SelectItem> si = new Vector<SelectItem>();
		paisSelecItem = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Pais e : p)
			si.add(new SelectItem(e.getIdPais(), "" + e.getDescripcion()));
		paisSelecItem = si;
		return si;
	}

	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}

	@SuppressWarnings("unchecked")
	private boolean existeEstudio(String op) {
		try {
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-dd-MM");
			String sql = " select e from EstudiosRealizadosLegajo e "
					+ " where e.tituloAcademico.nivelEstudio.idNivelEstudio="
					+ idNivelEstudio + ""
					+ "  and  e.tituloAcademico.idTituloAcademico="
					+ idTituloAcademico + "" + "  and  e.pais.idPais=" + idPais
					+ "  and e.activo=true"
					+ " and e.persona.idPersona = "+ persona.getIdPersona();
			
			if(estudiosRealizados.getFechaFin() != null){
				sql +=" and  date(e.fechaFin) = to_date('"
						+ formato.format(estudiosRealizados.getFechaFin())
						+ "','YYYY-DD-MM') ";
			}
								

			if (!esOtraInst)
				sql += "  and e.institucionEducativa.idInstEducativa="
						+ idIstUniversidad;
			else if (estudiosRealizados.getOtraInstit() != null)
				sql += "  and lower(and e.otraInstit) like '"
						+ estudiosRealizados.getOtraInstit().toLowerCase()
						+ "'";
			if(esOtroNivel)
				sql+="  and lower(e.otroNivel) like '"+estudiosRealizados.getOtroNivel().toLowerCase().replace("'", "''") +"'";
			
			if(esOtro && !esOtroNivel)
				sql+=" and lower(e.otroTituloObt) like '"+estudiosRealizados.getOtroTituloObt().toLowerCase().replace("'", "''") +"'";
			
			
			if (idProfesion != null)
				sql += "  and e.especialidades.idEspecialidades ="
						+ idProfesion + "";

			if (op.equals("update"))
				sql += " and e.idEstudiosRealizados!="
						+ estudiosRealizados.getIdEstudiosRealizados();
			List<EstudiosRealizados> e = em.createQuery(sql).getResultList();
			return !e.isEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	



	
	
	
	@SuppressWarnings("unchecked")
	public void validarLegajo(int index){
		Documentos aux = em.find(Documentos.class,
				estudiosRealizadosList.get(index).getDocumentos().getIdDocumento());
		aux.setValidoLegajo(true);
		aux.setFechaMod(new Date());
		aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(aux);
		em.flush();
	}
	
	@SuppressWarnings("unchecked")
	public void invalidarLegajo(int index){
		Documentos aux = em.find(Documentos.class,
				estudiosRealizadosList.get(index).getDocumentos().getIdDocumento());
		aux.setValidoLegajo(false);
		aux.setFechaMod(new Date());
		aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(aux);
		em.flush();
	}

	
	

	

	
	
	
	
	@SuppressWarnings("unchecked")
	private boolean existeIdioma(){
		String consulta = "select i from IdiomasPersona i " +
		" where i.datosEspecificos.idDatosEspecificos="+idIdioma +
				" and i.activo = true " + 
				" and i.persona.idPersona = " + persona.getIdPersona();
		List<IdiomasPersona> ip= em.createQuery(consulta).getResultList(); 
		return !ip.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	private boolean existeIdiomaCIO(){
		String consulta = "select i from IdiomasLegajoPersona i " +
		" where i.datosEspecificos.idDatosEspecificos="+idIdioma +
				" and i.activo = true " + 
				" and i.persona.idPersona = " + persona.getIdPersona();
		List<IdiomasLegajoPersona> ip= em.createQuery(consulta).getResultList(); 
		return !ip.isEmpty();
	}
	
	public void delIdioma(int index){
		IdiomasLegajoPersona i= idiomasPersonasList.get(index);
		i.setActivo(false);
		em.merge(i);
		em.flush();
		idiomasPersonasList.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		//tab6VistaPreliminarFormController.init();
	}
	
	@SuppressWarnings("unchecked")
	public Boolean isOriginal(Long idDocumento){
		String sql = "select * from legajo.estudios_realizados where activo = true and id_documento =  " + idDocumento;
		List <Documentos> lista = em.createNativeQuery(sql, Documentos.class).getResultList();
		if(lista != null && lista.size() > 0)
			return true;
		else
			return false;
	}
	
	public void nombre(){
		nombreDoc=null;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public Boolean habFechaFin() {
		return !esNingunaInsNingunTit && tieneTitulo;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdNivelEstudio() {
		return idNivelEstudio;
	}

	public void setIdNivelEstudio(Long idNivelEstudio) {
		this.idNivelEstudio = idNivelEstudio;
	}

	public Long getIdProfesion() {
		return idProfesion;
	}

	public void setIdProfesion(Long idProfesion) {
		this.idProfesion = idProfesion;
	}

	public Long getIdIstUniversidad() {
		return idIstUniversidad;
	}

	public void setIdIstUniversidad(Long idIstUniversidad) {
		this.idIstUniversidad = idIstUniversidad;
	}

	public Long getIdIdioma() {
		return idIdioma;
	}

	public void setIdIdioma(Long idIdioma) {
		this.idIdioma = idIdioma;
	}

	public Long getIdHabla() {
		return idHabla;
	}

	public void setIdHabla(Long idHabla) {
		this.idHabla = idHabla;
	}

	public Long getIdEscribe() {
		return idEscribe;
	}

	public void setIdEscribe(Long idEscribe) {
		this.idEscribe = idEscribe;
	}

	public Long getIdLee() {
		return idLee;
	}

	public void setIdLee(Long idLee) {
		this.idLee = idLee;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		if (idTipoDoc != null && idTipoDoc != 0)
			this.idTipoDoc = idTipoDoc;
	}

	public Long getIdTituloAcademico() {
		return idTituloAcademico;
	}

	public void setIdTituloAcademico(Long idTituloAcademico) {
		this.idTituloAcademico = idTituloAcademico;
	}

	public int getIndexUp() {
		return indexUp;
	}

	public void setIndexUp(int indexUp) {
		this.indexUp = indexUp;
	}

	public List<IdiomasLegajoPersona> getIdiomasPersonasList() {
		return idiomasPersonasList;
	}

	public void setIdiomasPersonasList(List<IdiomasLegajoPersona> idiomasPersonasList) {
		this.idiomasPersonasList = idiomasPersonasList;
	}

	public List<SelectItem> getTituloSelecItem() {
		return tituloSelecItem;
	}

	public void setTituloSelecItem(List<SelectItem> tituloSelecItem) {
		this.tituloSelecItem = tituloSelecItem;
	}

	public List<SelectItem> getProfesionSelecItem() {
		return profesionSelecItem;
	}

	public void setProfesionSelecItem(List<SelectItem> profesionSelecItem) {
		this.profesionSelecItem = profesionSelecItem;
	}

	public List<SelectItem> getPaisSelecItem() {
		return paisSelecItem;
	}

	public void setPaisSelecItem(List<SelectItem> paisSelecItem) {
		this.paisSelecItem = paisSelecItem;
	}

	public boolean isEsOtro() {
		return esOtro;
	}

	public void setEsOtro(boolean esOtro) {
		this.esOtro = esOtro;
	}

	public boolean isEsOtraInst() {
		return esOtraInst;
	}

	public void setEsOtraInst(boolean esOtraInst) {
		this.esOtraInst = esOtraInst;
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

	public HorasAnios getHorasAnios() {
		return horasAnios;
	}

	public void setHorasAnios(HorasAnios horasAnios) {
		this.horasAnios = horasAnios;
	}

	public boolean isHabPais() {
		return habPais;
	}

	public void setHabPais(boolean habPais) {
		this.habPais = habPais;
	}

	public boolean isEsNingunaInsNingunTit() {
		return esNingunaInsNingunTit;
	}

	public void setEsNingunaInsNingunTit(boolean esNingunaInsNingunTit) {
		this.esNingunaInsNingunTit = esNingunaInsNingunTit;
	}

	public boolean isTieneTitulo() {
		return tieneTitulo;
	}

	public void setTieneTitulo(boolean tieneTitulo) {
		this.tieneTitulo = tieneTitulo;
	}

	public EstudiosRealizadosLegajo getEstudiosRealizados() {
		return estudiosRealizados;
	}

	public void setEstudiosRealizados(EstudiosRealizadosLegajo estudiosRealizados) {
		this.estudiosRealizados = estudiosRealizados;
	}

	public List<EstudiosRealizadosLegajo> getEstudiosRealizadosList() {
		return estudiosRealizadosList;
	}

	public void setEstudiosRealizadosList(
			List<EstudiosRealizadosLegajo> estudiosRealizadosList) {
		this.estudiosRealizadosList = estudiosRealizadosList;
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

	public boolean isEsEdit() {
		return esEdit;
	}

	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}

	public boolean isEsOtroNivel() {
		return esOtroNivel;
	}

	public void setEsOtroNivel(boolean esOtroNivel) {
		this.esOtroNivel = esOtroNivel;
	}

	public Long getIdEstadoActual() {
		return idEstadoActual;
	}

	public void setIdEstadoActual(Long idEstadoActual) {
		this.idEstadoActual = idEstadoActual;
	}

	public String getEstadoActual() {
		return estadoActual;
	}

	public void setEstadoActual(String estadoActual) {
		this.estadoActual = estadoActual;
	}
	
	public void actualizarEstadoActual()
	{
		estadoActual = em.find(Referencias.class, idEstadoActual).getDescAbrev();	
	}
	
	@SuppressWarnings("unchecked")
	private Long searchEstadoActual(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista = em
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'ESTADO_ACTUAL' and o.descAbrev = '"
									+ c + "' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}


	public List<SelectItem> getTipoDocumentos() {
		return tipoDocumentos;
	}


	public void setTipoDocumentos(List<SelectItem> tipoDocumentos) {
		this.tipoDocumentos = tipoDocumentos;
	}


	public HorasAnios[] getHorasAniosCombo() {
		return horasAniosCombo;
	}


	public void setHorasAniosCombo(HorasAnios[] horasAniosCombo) {
		this.horasAniosCombo = horasAniosCombo;
	}
	
	
	public HorasAnios[] listarHorasAnioCombo(){
		int i = 0;
		horasAniosCombo = new HorasAnios[6];
		for (HorasAnios tr : HorasAnios.values()) {
			if (!"C".equals(tr.getValor())) {
				horasAniosCombo[i]= tr;
				i++;
			}
		}
		return horasAniosCombo;
	}


	public Long getIdEstudiosRealizados() {
		return idEstudiosRealizados;
	}


	public void setIdEstudiosRealizados(Long idEstudiosRealizados) {
		this.idEstudiosRealizados = idEstudiosRealizados;
	}


	public List<Documentos> getEstudiosRealizadosDocList() {
		return estudiosRealizadosDocList;
	}


	public void setEstudiosRealizadosDocList(
			List<Documentos> estudiosRealizadosDocList) {
		this.estudiosRealizadosDocList = estudiosRealizadosDocList;
	}


	public String getTituloDocumentoAdjunto() {
		return tituloDocumentoAdjunto;
	}


	public void setTituloDocumentoAdjunto(String tituloDocumentoAdjunto) {
		this.tituloDocumentoAdjunto = tituloDocumentoAdjunto;
	}


	public Integer getTamArchivo() {
		return tamArchivo;
	}


	public void setTamArchivo(Integer tamArchivo) {
		this.tamArchivo = tamArchivo;
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getDuracionTipo(EstudiosRealizadosLegajo estudio){
		if(estudio.getTipoDuracion().equalsIgnoreCase("H") && estudio.getDuracionHoras() != null)
			return estudio.getDuracionHoras().toString()+" horas";
		if(estudio.getTipoDuracion().equalsIgnoreCase("C") && estudio.getDuracionHoras() != null)
			return estudio.getDuracionHoras().toString()+" cursos";
		if(estudio.getTipoDuracion().equalsIgnoreCase("S") && estudio.getDuracionHoras() != null)
			return estudio.getDuracionHoras().toString()+" semestres";
		if(estudio.getTipoDuracion().equalsIgnoreCase("A") && estudio.getDuracionHoras() != null)
			return estudio.getDuracionHoras().toString()+" a�os";
		if(estudio.getTipoDuracion().equalsIgnoreCase("M") && estudio.getDuracionHoras() != null)
			return estudio.getDuracionHoras().toString()+" meses";
		if(estudio.getTipoDuracion().equalsIgnoreCase("D") && estudio.getDuracionHoras() != null)
			return estudio.getDuracionHoras().toString()+" d�as";
		if(estudio.getTipoDuracion().equalsIgnoreCase("X") && estudio.getDuracionHoras() != null)
			return estudio.getDuracionHoras().toString()+" m�dulos";
		return "";
	}

}