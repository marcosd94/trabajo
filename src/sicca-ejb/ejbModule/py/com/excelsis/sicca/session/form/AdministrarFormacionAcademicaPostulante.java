package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.text.StyledEditorKit.BoldAction;







import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import enums.HorasAnios;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Especialidad;
import py.com.excelsis.sicca.entity.Especialidades;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.EstudiosRealizadosLegajo;
import py.com.excelsis.sicca.entity.IdiomasPersona;
import py.com.excelsis.sicca.entity.InstitucionEducativa;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.NivelEstudio;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidad;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.entity.TituloAcademico;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EstudiosRealizadosHome;
import py.com.excelsis.sicca.session.PersonaDiscapacidadHome;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("administrarFormacionAcademicaPostulante")
public class AdministrarFormacionAcademicaPostulante implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(required = false)
	Usuario usuarioLogueado;
	 
	
	@In(create =  true)
	EstudiosRealizadosHome estudiosRealizadosHome; 


	@In(value = "entityManager")
	EntityManager em;
	
	
	@In(create=true,required=false)
	Tab6VistaPreliminarFormController tab6VistaPreliminarFormController;
	@In(create=true,required=false)
	Tab4AdjuntarDocumentosFormController tab4AdjuntarDocumentosFormController;
	
	public static final String CONTEXT_VAR_NAME = "referencias";
	
	General general;
	
	
	private EstudiosRealizados estudiosRealizados= new EstudiosRealizados();
	private EstudiosRealizadosLegajo estudiosRealizadosLegajo= new EstudiosRealizadosLegajo();
	private List<EstudiosRealizados> estudiosRealizadosList= new ArrayList<EstudiosRealizados>();
	private Persona persona = new Persona();
	private Long idPais;
	private Long idNivelEstudio = new Long(0);
	//private Long idtituloObtenido;
	private Long idProfesion;
	private Long idIstUniversidad;
	private Long idIdioma;
	private Long idHabla;
	private Long idEscribe;
	private Long idLee;
	private Long idTipoDoc;
	private boolean esEdit=false;
	private Long idTituloAcademico;
	private int indexUp;
	private List<IdiomasPersona> idiomasPersonasList= new ArrayList<IdiomasPersona>();
	private List<SelectItem> tituloSelecItem;
	private List<SelectItem> profesionSelecItem;
	private List<SelectItem> paisSelecItem;
	private boolean esOtro;//para el titulo
	private boolean esOtraInst;
	//private String otraInstitucion;
	private String nombrePantalla;
	private Long idDoc;
	private HorasAnios horasAnios;
	private boolean habPais;
	private boolean esNingunaInsNingunTit=false;//en le caso que se seleccione ningun titulo y ninguna institucion
	private boolean tieneTitulo=false;
	private boolean esOtroNivel;//para el Nivel de Estudios
	
	private String estadoActual;
	private Long idEstadoActual;
	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item ;
	private String nombreDoc;
	private File inputFile ;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	
	public void init(){
		estadoActual = "FINALIZADO";
		idEstadoActual = searchEstadoActual(estadoActual);
		getTituloAcademicos();
		upEspecialidad();
		try {
			setear();
			persona= em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
			findEstudioPersona();
			paisSelecItem= new ArrayList<SelectItem>();
			idiomasPersonasList= new ArrayList<IdiomasPersona>();
			if(persona!=null){
				
				findEstudiosRealizados();
				findIdiomaPersona();
			}
			esEdit=false;
			getTituloAcademicos();
			upEspecialidad();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
	
	
	
	public String save(){
		try {
			
			
			
			for (int i = 0; i < estudiosRealizadosList.size(); i++) {
				if(estudiosRealizadosList.get(i).getIdEstudiosRealizados()!=null){
					em.merge(estudiosRealizadosList.get(i));
					em.flush();
				}else{
					em.persist(estudiosRealizadosList.get(i));
					em.flush();
				}
				
				
			}
			
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
				statusMessages.add(Severity.ERROR,"Debe seleccionar una Instituciï¿½n/Univ. Verifique");
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
				/*if(estudiosRealizados.getFechaInicio()==null){
					statusMessages.add(Severity.ERROR,"Debe ingresar la fecha de inicio. Verifique");
					return null;
				}*/
			}
			
			if(!ta.getDescripcion().toLowerCase().equals("ninguno")&& !ta.getDescripcion().toLowerCase().equals("ningunos")){
				
				if(ie.getDescripcion().toLowerCase().equals("ninguna")){
					statusMessages.add(Severity.ERROR,"Debe seleccionar la instituciï¿½n. Verifique");
					return null;
				}
				if(idPais==null ){
					statusMessages.add(Severity.ERROR,"Seleccione el Paï¿½s. Verifique");
					return null;
				}
				/*if(estudiosRealizados.getFechaInicio()==null ){
					statusMessages.add(Severity.ERROR,"Ingrese la Fecha de Inicio. Verifique");
					return null;
				}*/
				if(estudiosRealizados.getFechaFin()==null && !estadoActual.equals("CURSANDO")){
					statusMessages.add(Severity.ERROR,"Ingrese la Fecha Fin. Verifique");
					return null;
				}
				/*if(estudiosRealizados.getFechaFin()!=null&&estudiosRealizados.getFechaInicio().after(estudiosRealizados.getFechaFin())){
					statusMessages.add(Severity.ERROR,"La fecha de Inicio no puede ser mayor a la Fecha Fin. Verifique");
					return null;
				}*/
				
				
				if(estudiosRealizados.getDuracionHoras()==null){
					statusMessages.add(Severity.ERROR,"Ingrese la Duraciï¿½n. Verifique");
					return null;
				}
				if(estudiosRealizados.getDuracionHoras()!=null && estudiosRealizados.getDuracionHoras().intValue()<=0){
					statusMessages.add(Severity.ERROR,"La Duraciï¿½n ingresada debe ser mayor a cero. Verifique");
					return null;
				}
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
				statusMessages.add(Severity.ERROR,"Debe ingresar en el campo Otra de Instituciï¿½n antes de agregar . Verifique");
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
			/*if(estudiosRealizados.getFechaInicio()!=null){
				if( !general.isFechaValida(estudiosRealizados.getFechaInicio())){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Fecha de Inicio invï¿½lida");
					return null ;
				}
				if(estudiosRealizados.getFechaInicio().after(new Date())){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,"La Fecha de Inicio no puede ser mayor a la fecha actual.  Verifique ");
					return null;
				}
			}*/
			/*if(estudiosRealizados.getFechaInicio()!=null && estudiosRealizados.getFechaFin()!=null){
				if(estudiosRealizados.getFechaInicio().after(estudiosRealizados.getFechaFin())){
					statusMessages.add(Severity.ERROR,"Verifique la Fecha Inicio y Fecha Fin");
					return null;
				}
			}*/
			
			if(estudiosRealizados.getFechaFin()!=null ){
				if( !general.isFechaValida(estudiosRealizados.getFechaFin())){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Fecha Fin invï¿½lida");
					return null;
				}
				if(estudiosRealizados.getFechaFin().after(new Date())){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,"La Fecha Fin no puede ser mayor a la fecha actual.  Verifique ");
					return null;
				}
			}
			if(estudiosRealizados.getDuracionHoras()!=null && estudiosRealizados.getDuracionHoras().intValue()<=0){
				statusMessages.add(Severity.ERROR,"La Duraciï¿½n ingresada debe ser mayor a cero. Verifique");
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
									.add(Severity.ERROR,"El archivo supera el tamaï¿½o mï¿½ximo permitido. Lï¿½mite "+tam.get(0).getTamanho()+tam.get(0).getUnidMedida()+", verifique");
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
						idDoc=AdmDocAdjuntoFormController.editar(item.getFile(),Integer.valueOf(item.getFileSize()),item.getFileName(),item.getContentType(), docDB.getUbicacionFisica(), "tab2FormacionAcademicaPostulante_edit", docDB.getIdDocumento(), idTipoDoc, usuarioLogueado.getIdUsuario(), estudiosRealizados.getIdEstudiosRealizados(),"EstudiosRealizados");
						if(idDoc!=null){
							
							if(idDoc.longValue()==-8){
								statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
								return null;					
							}
							if(idDoc.longValue()==-7){
								statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaï¿½o permitido. Seleccione otro");
								return null;	
							}
							if(idDoc.longValue()==-6){
								statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
								return null;	
							}
							doc = em.find(Documentos.class, idDoc);
							doc.setPersona(persona);
							doc.setIdTabla(estudiosRealizados.getIdEstudiosRealizados());
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
					String direccionFisica = "";
					SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
					String anio = sdfSoloAnio.format(new Date());
					direccionFisica = "\\SICCA\\" +"USUARIO_PORTAL\\"
							+ persona.getDocumentoIdentidad() + "_"
							+ persona.getIdPersona();
					idDoc = AdmDocAdjuntoFormController.editar(item.getFile(),
							item.getFileSize(), item.getFileName(),
							item.getContentType(), direccionFisica,
							"tab2FormacionAcademicaPostulante_edit", null,
							idTipoDoc, usuarioLogueado.getIdUsuario(),
							estudiosRealizados.getIdEstudiosRealizados(),
							"EstudiosRealizados");
					if(idDoc!=null){
						
						if(idDoc.longValue()==-8){
							statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
							return null;					
						}
						if(idDoc.longValue()==-7){
							statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaï¿½o permitido. Seleccione otro");
							return null;	
						}
						if(idDoc.longValue()==-6){
							statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
							return null;	
						}
						doc = em.find(Documentos.class, idDoc);
						doc.setPersona(persona);
						doc.setIdTabla(estudiosRealizados.getIdEstudiosRealizados());
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
			tab6VistaPreliminarFormController.init();
			tab4AdjuntarDocumentosFormController.init();
			
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error al actualizar. Verifique");
		}
		return null;
	}
	public void cancelar(){
		estudiosRealizados= new EstudiosRealizados();
		idNivelEstudio=null;
		idPais=null;
		idProfesion=null;
		idIstUniversidad=null;
		idTituloAcademico=null;
		idTipoDoc=null;
		nombreDoc=null;
		horasAnios=HorasAnios.Anios;
		habPais=false;
		uploadedFile=null;
		item=null;
		esNingunaInsNingunTit=false;
		esOtraInst=false;
		esOtro=false;
		tieneTitulo=true;
		fileName=null;
		contentType=null;
		esEdit=false;
		estudiosRealizadosList= new ArrayList<EstudiosRealizados>();
		em.clear();
		findEstudiosRealizados();
	
	}
	public void editar(int index){
		esEdit=true;
		
		estudiosRealizados=em.find(EstudiosRealizados.class, estudiosRealizadosList.get(index).getIdEstudiosRealizados());
		
		idNivelEstudio=estudiosRealizados.getTituloAcademico().getNivelEstudio().getIdNivelEstudio();
		estadoActual=estudiosRealizados.getEstadoActual();
		idEstadoActual = searchEstadoActual(estadoActual);
		if(estudiosRealizados.getTituloAcademico().getNivelEstudio().getDescripcion().trim().toLowerCase().equalsIgnoreCase("Cursos, charlas y conferencias"))
			esOtroNivel = true;
		else
			esOtroNivel = false;
		idTituloAcademico=estudiosRealizados.getTituloAcademico().getIdTituloAcademico();
		idIstUniversidad=estudiosRealizados.getInstitucionEducativa().getIdInstEducativa();
		getTituloAcademicos();	
		
		
		if(estudiosRealizados.getPais()!=null){
			idPais=estudiosRealizados.getPais().getIdPais();
		}
		TituloAcademico ta= em.find(TituloAcademico.class, idTituloAcademico);
		if(idTituloAcademico!=null&& idIstUniversidad!=null){
			if(estudiosRealizados.getInstitucionEducativa().getDescripcion().toLowerCase().equals("ninguna")){
				habPais=false;
				if( ta.getDescripcion().toLowerCase().equals("ninguno")){
					esNingunInstitucionTituto();
				}else
					esNingunaInsNingunTit=false;
			}else
				habPais=true;
		}
		if(ta.getDescripcion().toLowerCase().equals("ninguno")){
			noTienTitulo();
		}else
			tieneTitulo=true;
		if(estudiosRealizados.getTituloAcademico().getDescripcion().toLowerCase().equals("otros"))
			esOtro=true;
		else
			esOtro=false;
		if(estudiosRealizados.getInstitucionEducativa().getDescripcion().toLowerCase().equals("otras") || estudiosRealizados.getInstitucionEducativa().getDescripcion().toLowerCase().equals("otra"))
			esOtraInst=true;
		else
			esOtraInst=false;
		if(estudiosRealizados.getEspecialidades()!=null)
			idProfesion=estudiosRealizados.getEspecialidades().getIdEspecialidades();
		if(estudiosRealizados.getTipoDuracion()!=null)
			horasAnios=HorasAnios.getHorasAniosPorId(estudiosRealizados.getTipoDuracion());
		if(idPais!=null){
			estudiosRealizados.setPais(em.find(Pais.class, idPais));
		}
//		if(estudiosRealizados.getInstitucionEducativa().getPais()!=null)
//			idPais=estudiosRealizados.getInstitucionEducativa().getPais().getIdPais();
		//idPais=estudiosRealizados.get
		
		idTituloAcademico=estudiosRealizados.getTituloAcademico().getIdTituloAcademico();
		if(idProfesion!=null)
			upEspecialidad();
		if(estudiosRealizados.getDocumentos()!=null){
			Documentos doc= em.find(Documentos.class, estudiosRealizados.getDocumentos().getIdDocumento());
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
			 idTipoDoc=doc.getDatosEspecificos().getIdDatosEspecificos();
			 nombreDoc=doc.getNombreFisicoDoc();
			
		}else
			inputFile=null;
		if(estudiosRealizados.getPais()!=null){
			idPais=estudiosRealizados.getPais().getIdPais();
			upInstitucionOtro();
		}
		indexUp=index;
		
	}
	
	@SuppressWarnings("unchecked")
	public void addEstudios(){
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

			if(idNivelEstudio==null ){
				statusMessages.add(Severity.ERROR,"Debe seleccionar un Nivel de Estudio. Verifique");
				return;
			}
			if( idTituloAcademico==null ){
				statusMessages.add(Severity.ERROR,"Debe seleccionar el Titulo Obtenido");
				return;
			}
			if(idIstUniversidad==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar una InstituciÃ³n/Univ. Verifique");
				return;
			}
			if(esOtroNivel && (estudiosRealizados.getOtroNivel() == null || estudiosRealizados.getOtroNivel().isEmpty())) {
				statusMessages.add(Severity.ERROR,"Debe completar la Descripcion Charla/Conferencia");
				return;
			}
			TituloAcademico ta= null;
			
			ta= em.find(TituloAcademico.class, idTituloAcademico);
			
			if(habPais)
			{
				if(idPais==null){
					statusMessages.add(Severity.ERROR,"Debe seleccionar un Pais. Verifique");
					return;
				}
				/*if(estudiosRealizados.getFechaInicio()==null){
					statusMessages.add(Severity.ERROR,"Debe ingresar la fecha de inicio. Verifique");
					return;
				}*/
			}
			InstitucionEducativa ie= em.find(InstitucionEducativa.class, idIstUniversidad);
			if(!ta.getDescripcion().toLowerCase().equals("ninguno")&& !ta.getDescripcion().toLowerCase().equals("ningunos")){//si tiene titulo
				
				if(ie.getDescripcion().toLowerCase().equals("ninguna")){
					statusMessages.add(Severity.ERROR,"Debe seleccionar la institucion. Verifique");
					return;
				}
				
				if( idPais==null ){
					statusMessages.add(Severity.ERROR,"Seleccione el Pais. Verifique");
					return;
				}
				/*if(estudiosRealizados.getFechaInicio()==null ){
					statusMessages.add(Severity.ERROR,"Ingrese la Fecha de Inicio. Verifique");
					return;
				}*/
//				if(estudiosRealizados.getFechaFin()==null && !estadoActual.equals("CURSANDO")){ MP - 09/11/2015
				if(estudiosRealizados.getFechaFin()==null && estadoActual.equals("FINALIZADO")){
					statusMessages.add(Severity.ERROR,"Ingrese la Fecha Fin. Verifique");
					return;
				}
				/*if(estudiosRealizados.getFechaFin()!=null&&estudiosRealizados.getFechaInicio().after(estudiosRealizados.getFechaFin())){
					statusMessages.add(Severity.ERROR,"La fecha de Inicio no puede ser mayor a la Fecha Fin. Verifique");
					return;
				}*/
				
				if(estudiosRealizados.getDuracionHoras()==null){
					statusMessages.add(Severity.ERROR,"Ingrese la Duracion. Verifique");
					return;
				}
				if(estudiosRealizados.getDuracionHoras()!=null && estudiosRealizados.getDuracionHoras().intValue()<=0){
					statusMessages.add(Severity.ERROR,"La Duracion ingresada debe ser mayor a cero. Verifique");
					return;
				}
				if(idTipoDoc==null && nombreDoc != null){
					statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
					return;
				}
				if(idTipoDoc!=null && nombreDoc == null){
					statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
					return;
				}	
				if(idTipoDoc==null && nombreDoc == null){
					statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
					return;
				}
				
			}
			if(!tieneTitulo){
				estudiosRealizados.setFechaFin(null);
				estudiosRealizados.setDuracionHoras(null);
				horasAnios=HorasAnios.Anios;
			}
			if(!esNingunaInsNingunTit){
				if(existeEstudio("save")){
					statusMessages.add(Severity.ERROR,"El registro ingresado ya existe, favor verificar");
					return;
				}
			}
			
			if(esOtraInst && (estudiosRealizados.getOtraInstit()==null || estudiosRealizados.getOtraInstit().trim().equals(""))){
				statusMessages.add(Severity.ERROR,"Debe ingresar en el campo Otra de Institucion  antes de agregar . Verifique");
				return  ;
			}
			if(esOtro && (estudiosRealizados.getOtroTituloObt()==null || estudiosRealizados.getOtroTituloObt().trim().equals(""))){
				if(!esOtroNivel){
					statusMessages.add(Severity.ERROR,"Debe ingresar en el campo Otros de Titulo antes de agregar. Verifique");
					return;
				}else
					estudiosRealizados.setOtroTituloObt(estudiosRealizados.getOtroNivel());
			}
			if(item!=null && idTipoDoc==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar el Tipo de documento. Verifique");
				return;
			}
			if(item==null && idTipoDoc!=null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar el archivo. Verifique");
				return;
			}
			if(item==null && idTipoDoc==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar el Tipo de documento y el archivo. Verifique");
				return;
			}
			/*if(estudiosRealizados.getFechaInicio()!=null){
				if( !general.isFechaValida(estudiosRealizados.getFechaInicio())){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Fecha de Inicio invï¿½lida. Verifique");
					return ;
				}
				if(estudiosRealizados.getFechaInicio().after(new Date())){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,"La Fecha de Inicio no puede ser mayor a la fecha actual.  Verifique ");
					return;
				}
			}*/
			/*if(estudiosRealizados.getFechaInicio()!=null && estudiosRealizados.getFechaFin()!=null){
				if(estudiosRealizados.getFechaInicio().after(estudiosRealizados.getFechaFin())){
					statusMessages.add(Severity.ERROR,"La Fecha de Inicio no puede ser mayor a la fecha Fin. Verifique");
					return;
				}
			}*/
			
			if(estudiosRealizados.getFechaFin()!=null ){
				if( !general.isFechaValida(estudiosRealizados.getFechaFin())){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Fecha Fin invalida");
					return ;
				}
				if(estudiosRealizados.getFechaFin().after(new Date())){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,"La Fecha Fin no puede ser mayor a la fecha actual.  Verifique ");
					return;
				}
			}
			
			if(estudiosRealizados.getDuracionHoras()!=null && estudiosRealizados.getDuracionHoras().intValue()<=0){
				statusMessages.add(Severity.ERROR,"La Duracion ingresada debe ser mayor a cero. Verifique");
				return;
			}
			
					
			
			
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
					List<TipoArchivo> tam = em
							.createQuery(
									"select t from TipoArchivo t "
											+ " where lower(t.extension) like '" 
											+ extension.toLowerCase() + "'").getResultList();
					if (!tam.isEmpty() && tam.get(0).getUnidMedidaByte() != null) {
						if (item.getFileSize() > tam.get(0).getUnidMedidaByte()
								.intValue()) {
							statusMessages
									.add(Severity.ERROR,"El archivo supera el tamaño maximo permitido. Limite "+tam.get(0).getTamanho()+tam.get(0).getUnidMedida()+", verifique");
							return;
						}
					}
					/**
					 * FIN
					 * */
				documento();
				if(idDoc!=null && persona!=null){
					
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
					Documentos doc = new Documentos();
					doc = em.find(Documentos.class, idDoc);
					doc.setPersona(persona);
					em.merge(doc);
					em.flush();
					estudiosRealizados.setDocumentos(doc);
					
					
				}else{
					statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
					return ;
				}
				
			}
			if(ta.getDescripcion().toLowerCase().equals("ninguno")|| ta.getDescripcion().toLowerCase().equals("ningunos"))
				estudiosRealizados.setFinalizo(false);
			else
				estudiosRealizados.setFinalizo(true);
			if(horasAnios!=null)
				estudiosRealizados.setTipoDuracion(horasAnios.getValor());
			estudiosRealizados.setActivo(true);
			if(idProfesion!=null)
				estudiosRealizados.setEspecialidades((em.find(Especialidades.class, idProfesion)));
			estudiosRealizados.setEstadoActual(em.find(Referencias.class, idEstadoActual).getDescAbrev());
			estudiosRealizados.setFechaAlta(new Date());
			estudiosRealizados.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			estudiosRealizados.setPersona(persona);
			estudiosRealizados.setInstitucionEducativa(em.find(InstitucionEducativa.class, idIstUniversidad));
			estudiosRealizados.setTituloAcademico(em.find(TituloAcademico.class,idTituloAcademico));
			if(habPais && idPais!=null){
				estudiosRealizados.setPais(em.find(Pais.class, idPais));
			}
			estudiosRealizadosList.add(estudiosRealizados);
			em.persist(estudiosRealizados);
			em.flush();
			if(estudiosRealizados.getDocumentos()!=null && idDoc!=null){
				Documentos doc= em.find(Documentos.class,idDoc);
				doc.setIdTabla(estudiosRealizados.getIdEstudiosRealizados());
//				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
//				doc.setFechaMod(new Date());
//				doc.setPersona(persona);
				em.merge(doc);
				em.flush();
			}
					
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error no se puedo agregar el registro, verifique");
		}
		if(this.cuentaConLegajo())
			addEstudiosLegajo();
		
		limpiar();
	}
	
	public Boolean cuentaConLegajo() {
		Query q = em.createQuery("select l from Legajos l"
				+ " where  l.persona.idPersona = :id");
		q.setParameter("id", usuarioLogueado.getPersona().getIdPersona());
		List<Legajos> lista = q.getResultList();
		
		
		Query q1 = em.createQuery("select e from EmpleadoPuesto e"
							+ " where e.actual is true and e.activo is true and  e.persona.idPersona = :id");
			q1.setParameter("id", usuarioLogueado.getPersona().getIdPersona());
			List<Legajos> lista2 = q1.getResultList();
			
				
		if (!lista.isEmpty() && !lista2.isEmpty())
			return true;
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void addEstudiosLegajo(){
		try {
			
			if (uploadedFile != null) {
				if (AdmDocAdjuntoFormController.validarContentType(contentType))
					crearUploadItem("Legajo_"+fileName, uploadedFile.length, contentType,
							uploadedFile);
				else {
					statusMessages.add(Severity.ERROR,
							"No se permite la carga de ese tipo de archivos.");
					return;
				}
				
			} 
			
			replicarEstudiosRealizados();
				
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
					List<TipoArchivo> tam = em
							.createQuery(
									"select t from TipoArchivo t "
											+ " where lower(t.extension) like '" 
											+ extension.toLowerCase() + "'").getResultList();
					if (!tam.isEmpty() && tam.get(0).getUnidMedidaByte() != null) {
						if (item.getFileSize() > tam.get(0).getUnidMedidaByte()
								.intValue()) {
							statusMessages
									.add(Severity.ERROR,"El archivo supera el tamaï¿½o mï¿½ximo permitido. Lï¿½mite "+tam.get(0).getTamanho()+tam.get(0).getUnidMedida()+", verifique");
							return;
						}
					}
					/**
					 * FIN
					 * */
				documentoLegajo();
				if(idDoc!=null){
					
					if(idDoc.longValue()==-8){
						statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return ;					
					}
					if(idDoc.longValue()==-7){
						statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaï¿½o permitido. Seleccione otro");
						return ;	
					}
					if(idDoc.longValue()==-6){
						statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
						return ;	
					}
					Documentos doc = new Documentos();
					doc = em.find(Documentos.class, idDoc);
					doc.setPersona(persona);
					em.merge(doc);
					em.flush();
					estudiosRealizadosLegajo.setDocumentos(doc);
					
				}else{
					statusMessages.add(Severity.INFO,"Verificar Legajo");
					return ;
				}
				
			}
			
			em.persist(estudiosRealizadosLegajo);
			em.flush();
			
			if(estudiosRealizadosLegajo.getDocumentos()!=null && idDoc!=null){
				Documentos doc= em.find(Documentos.class,idDoc);
				doc.setIdTabla(estudiosRealizadosLegajo.getIdEstudiosRealizados());
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				doc.setFechaMod(new Date());
				doc.setPersona(persona);
				em.merge(doc);
				em.flush();
			}
			
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	
	}
	
	private void replicarEstudiosRealizados(){
		estudiosRealizadosLegajo.setDuracionHoras(estudiosRealizados.getDuracionHoras());
		estudiosRealizadosLegajo.setEspecialidades(estudiosRealizados.getEspecialidades());
		estudiosRealizadosLegajo.setFechaFin(estudiosRealizados.getFechaFin());
		estudiosRealizadosLegajo.setFechaInicio(estudiosRealizados.getFechaInicio());
		estudiosRealizadosLegajo.setFinalizo(estudiosRealizados.getFinalizo());
		estudiosRealizadosLegajo.setInstitucionEducativa(estudiosRealizados.getInstitucionEducativa());
		estudiosRealizadosLegajo.setOtraInstit(estudiosRealizados.getOtraInstit());
		estudiosRealizadosLegajo.setOtroNivel(estudiosRealizados.getOtroNivel());
		estudiosRealizadosLegajo.setOtroTituloObt(estudiosRealizados.getOtroTituloObt());
		estudiosRealizadosLegajo.setPais(estudiosRealizados.getPais());
		estudiosRealizadosLegajo.setPersona(estudiosRealizados.getPersona());
		estudiosRealizadosLegajo.setPersonaPostulante(estudiosRealizados.getPersonaPostulante());
		estudiosRealizadosLegajo.setTipo(estudiosRealizados.getTipo());
		estudiosRealizadosLegajo.setTipoDuracion(estudiosRealizados.getTipoDuracion());
		estudiosRealizadosLegajo.setTituloAcademico(estudiosRealizados.getTituloAcademico());
		estudiosRealizadosLegajo.setEstadoActual(estudiosRealizados.getEstadoActual());
		estudiosRealizadosLegajo.setActivo(true);
		estudiosRealizadosLegajo.setFechaAlta(estudiosRealizados.getFechaAlta());
		estudiosRealizadosLegajo.setFechaAltaOee(estudiosRealizados.getFechaAltaOee());
		estudiosRealizadosLegajo.setUsuAlta(estudiosRealizados.getUsuAlta());
		estudiosRealizadosLegajo.setUsuAltaOee(estudiosRealizados.getUsuAltaOee());
	}
	
	public void limpiar(){
		estudiosRealizados= new EstudiosRealizados();
		estudiosRealizadosLegajo= new EstudiosRealizadosLegajo();
		idIstUniversidad=null;
		idNivelEstudio=null;
		idPais=null;
		idTituloAcademico=null;
		idProfesion=null;
		idTipoDoc=null;
		horasAnios=HorasAnios.Anios;
		tab6VistaPreliminarFormController.init();
		tab4AdjuntarDocumentosFormController.init();
		habPais=false;
		uploadedFile=null;
		nombreDoc=null;
		item=null;
		esNingunaInsNingunTit=false;
		esOtraInst=false;
		esOtro=false;
		esOtroNivel = false;
		tieneTitulo=true;
		fileName=null;
		contentType=null;
		esEdit=false;
		findEstudiosRealizados();
	}
	
	
	public void documento() throws NamingException{
		nombrePantalla="tab2FormacionAcademicaPostulante_edit";
		String direccionFisica="";
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio=sdfSoloAnio.format(new Date());
		direccionFisica="\\SICCA\\"+"USUARIO_PORTAL\\"+persona.getDocumentoIdentidad()+"_"+persona.getIdPersona();
		idDoc=AdmDocAdjuntoFormController.guardarDirecto(item, direccionFisica,nombrePantalla, idTipoDoc,usuarioLogueado.getIdUsuario(),"EstudiosRealizados");
		
		
	}
	
	public void documentoLegajo() throws NamingException{
		nombrePantalla="EstudiosRealizadosLegajo_copia";
		String separador = File.separator;
		String direccionFisica = separador
				+ "LEGAJO" + separador
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona()+ separador+"ESTUDIOS_REALIZADOS"+ separador;
		idDoc=AdmDocAdjuntoFormController.guardarDirecto(item, direccionFisica,nombrePantalla, idTipoDoc,usuarioLogueado.getIdUsuario(),"EstudiosRealizadosLegajo");
		
		
	}
	public void abrirDoc(int index){
		
		EstudiosRealizados e= estudiosRealizadosList.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumentos().getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}
	public void delItems(int index){
		EstudiosRealizados e= estudiosRealizadosList.get(index);
		e.setActivo(false);
		e.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		e.setFechaAlta(new Date());
		if(e.getDocumentos()!=null){
			Documentos aux= e.getDocumentos();
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
		tab6VistaPreliminarFormController.init();
		tab4AdjuntarDocumentosFormController.init();
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
			if(existeIdioma()){
				statusMessages.add(Severity.ERROR,"El registro  ingresado ya existe, favor verificar");
				return;
			}
			Referencias escribe=em.find(Referencias.class, idEscribe);
			Referencias habla= em.find(Referencias.class, idHabla);
			Referencias lee=em.find(Referencias.class, idLee);
			IdiomasPersona idiomasPersona= new IdiomasPersona();
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
			tab6VistaPreliminarFormController.init();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void nombre(){
		nombreDoc=null;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getNacionalidadSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		
		List<Object[]> n=em.createNativeQuery("Select  de.id_datos_especificos, de.descripcion From seleccion.referencias  r," +
				"seleccion.datos_especificos de Where r.dominio like 'NACIONALIDADES' And  r.valor_num = de.id_datos_generales" +
				" Order by de.descripcion ").getResultList();
		Iterator<Object[]> it = n.iterator();
		
		 while(it.hasNext()){
			 Object[] fila = it.next();
			 if(fila[0]!=null)
				 si.add(new SelectItem(fila[0],fila[1]!=null?fila[1].toString():""));
		 }
			
		return si;
	}
	public void delIdioma(int index){
		IdiomasPersona i= idiomasPersonasList.get(index);
		i.setActivo(false);
		em.merge(i);
		em.flush();
		idiomasPersonasList.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		tab6VistaPreliminarFormController.init();
	}
	
	//METODOS PRIVADO
	private void esNingunInstitucionTituto(){
		idPais=null;
		habPais=false;
		idTipoDoc=null;
		estudiosRealizados.setFechaInicio(null);
		estudiosRealizados.setFechaFin(null);
		estudiosRealizados.setDuracionHoras(null);
		esNingunaInsNingunTit=true;
		nombreDoc=null;
		horasAnios=HorasAnios.Anios;
	}
	
	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}
	@SuppressWarnings("unchecked")
	private void findEstudiosRealizados(){
		estudiosRealizadosList=em.createQuery(" select d from EstudiosRealizados d where d.persona.idPersona= "+persona.getIdPersona() +"" +
				" and  d.activo= true order by d.fechaInicio desc, d.fechaFin desc").getResultList(); 
	}
	@SuppressWarnings("unchecked")
	private boolean existeEstudio(String op){
		try {
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-dd-MM");
			
			String sql=" select e from EstudiosRealizados e " +
					" where e.tituloAcademico.nivelEstudio.idNivelEstudio="+idNivelEstudio +"" +
					"  and  e.tituloAcademico.idTituloAcademico="+idTituloAcademico +"" +
					"  and  e.pais.idPais="+idPais +"  and e.activo=true" +
					"  and  e.persona.idPersona="+usuarioLogueado.getPersona().getIdPersona() ;//+
					//" and  date(e.fechaInicio) = to_date('"+ formato.format(estudiosRealizados.getFechaInicio())+"','YYYY-DD-MM') " ;
					
					if(!esOtraInst)
						sql+="  and e.institucionEducativa.idInstEducativa="+idIstUniversidad ;
					else if(estudiosRealizados.getOtraInstit()!=null)
						sql+="  and lower(e.otraInstit) like '"+estudiosRealizados.getOtraInstit().toLowerCase().replace("'", "''") +"'" ;
					
					if(esOtroNivel)
						sql+="  and lower(e.otroNivel) like '"+estudiosRealizados.getOtroNivel().toLowerCase().replace("'", "''") +"'";
					
					if(esOtro && !esOtroNivel)
						sql+=" and lower(e.otroTituloObt) like '"+estudiosRealizados.getOtroTituloObt().toLowerCase().replace("'", "''") +"'";
					
			if(idProfesion!=null)
				sql+="  and e.especialidades.idEspecialidades ="+idProfesion+"" ;
			
			if(op.equals("update"))
				sql+=" and e.idEstudiosRealizados!="+estudiosRealizados.getIdEstudiosRealizados();
			List<EstudiosRealizados> e= em.createQuery(sql).getResultList();
			return !e.isEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	
	}
	@SuppressWarnings("unchecked")
	private boolean existeIdioma(){
		String consulta = "select i from IdiomasPersona i " +
		" where i.datosEspecificos.idDatosEspecificos="+idIdioma +
				" and i.activo = true " + 
				" and i.persona.idPersona = " + usuarioLogueado.getPersona().getIdPersona();
		List<IdiomasPersona> ip= em.createQuery(consulta).getResultList(); 
		return !ip.isEmpty();
	}
	private void setear(){
		estudiosRealizadosList= new ArrayList<EstudiosRealizados>();
		estudiosRealizados= new EstudiosRealizados();
		estudiosRealizados.setFinalizo(false);
		idEscribe=null;
		idHabla=null;
		idIdioma=null;
		idIstUniversidad=null;
		idLee=null;
		idNivelEstudio=null;
		idPais=null;
		idProfesion=null;
		idTituloAcademico=null;
		esOtro=false;
		
	}
	@SuppressWarnings("unchecked")
	private void findEstudioPersona(){
		estudiosRealizadosList= em.createQuery("select e from EstudiosRealizados e where " +
				" e.persona.idPersona ="+persona.getIdPersona() + " and e.activo=true order by e.fechaAlta desc").getResultList();
	}
	@SuppressWarnings("unchecked")
	private void findIdiomaPersona(){
		idiomasPersonasList= em.createQuery("select e from IdiomasPersona e where " +
				" e.persona.idPersona ="+persona.getIdPersona()+" AND e.activo = true").getResultList();
	}
	
	
	public List<TituloAcademico> upTituloAcademico(){
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
		}
		return getTituloAcademicos();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TituloAcademico> getTituloAcademicos(){
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
	
	

	public List<SelectItem> getTituloAcademicoSelectItems(List<TituloAcademico> ti){
		List<SelectItem> si = new Vector<SelectItem>();
		tituloSelecItem= new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(TituloAcademico o : ti)
			si.add(new SelectItem(o.getIdTituloAcademico(), o.getDescripcion()));
		
		tituloSelecItem=si;
	//	idTituloAcademico=null;
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
	
	@SuppressWarnings("unchecked")
	public List<Especialidades> upEspecialidad(){
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
			esOtro=false;
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
			
		
		
		//getEspecialidadSelecSelectItems(es);
		habFechaFin();
		return es;
		
	}
	public Boolean habFechaFin(){
		return !esNingunaInsNingunTit & tieneTitulo;
	}
	
	@SuppressWarnings("unchecked")
	public List<Pais> upInstitucionOtro(){
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
				for (int i = 0; i < ieList.size(); i++) {
					if(ieList.get(i).getPais()!=null){
						Pais p= em.find(Pais.class, ieList.get(i).getPais().getIdPais());
						pais.add(p);
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
	
	public List<SelectItem> getPaisInstSelectItems( List<Pais> p){
		List<SelectItem> si = new Vector<SelectItem>();
		paisSelecItem= new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(Pais e : p)
			si.add(new SelectItem(e.getIdPais(),"" + e.getDescripcion()));
		paisSelecItem= si;
		return si;
	}
	
	public List<SelectItem> getEspecialidadSelecSelectItems( List<Especialidades> es){
		List<SelectItem> si = new Vector<SelectItem>();
		profesionSelecItem= new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(Especialidades e : es)
			si.add(new SelectItem(e.getIdEspecialidades(),"" + e.getDescripcion()));
		profesionSelecItem= si;
		return si;
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
	
	
	  
	private void noTienTitulo(){
		tieneTitulo=false;
		esOtro = false;
		horasAnios=null;
	}
	public void setearFecha()
	{
		System.out.println(estudiosRealizados.getFechaFin());
	}
//	GETTERS Y SETTERS
	public EstudiosRealizados getEstudiosRealizados() {
		return estudiosRealizados;
	}


	public void setEstudiosRealizados(EstudiosRealizados estudiosRealizados) {
		this.estudiosRealizados = estudiosRealizados;
	}


	public List<EstudiosRealizados> getEstudiosRealizadosList() {
		return estudiosRealizadosList;
	}


	public void setEstudiosRealizadosList(
			List<EstudiosRealizados> estudiosRealizadosList) {
		this.estudiosRealizadosList = estudiosRealizadosList;
	}


	public Persona getPersona() {
		return persona;
	}


	public void setPersona(Persona persona) {
		this.persona = persona;
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
		this.idTipoDoc = idTipoDoc;
	}


	public boolean isEsEdit() {
		return esEdit;
	}


	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}


	public int getIndexUp() {
		return indexUp;
	}


	public void setIndexUp(int indexUp) {
		this.indexUp = indexUp;
	}


	public List<IdiomasPersona> getIdiomasPersonasList() {
		return idiomasPersonasList;
	}


	public void setIdiomasPersonasList(List<IdiomasPersona> idiomasPersonasList) {
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


	public Long getIdTituloAcademico() {
		return idTituloAcademico;
	}


	public void setIdTituloAcademico(Long idTituloAcademico) {
			this.idTituloAcademico = idTituloAcademico;
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


	


	public List<SelectItem> getPaisSelecItem() {
		return paisSelecItem;
	}


	public void setPaisSelecItem(List<SelectItem> paisSelecItem) {
		this.paisSelecItem = paisSelecItem;
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


	public File getInputFile() {
		return inputFile;
	}


	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
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


	public byte[] getUploadedFile() {
		return uploadedFile;
	}


	public void setUploadedFile(byte[] uploadedFile) {
		this.uploadedFile = uploadedFile;
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


	public boolean isEsOtroNivel() {
		return esOtroNivel;
	}


	public void setEsOtroNivel(boolean esOtroNivel) {
		this.esOtroNivel = esOtroNivel;
	}

	public Long getIdEstadoActual(){
		return idEstadoActual;
	}
	public void setIdEstadoActual(Long idEstadoActual){
		
		this.idEstadoActual = idEstadoActual;
	}
	
	public String getEstadoActual(){
		return this.estadoActual;
	}

	public void setEstadoActual(String estadoActual){
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
	
		
	public List<SelectItem> getDatosEspecificossByTipoDocumentoFPFACAndNivelEstudioSelectItems(Long idNivelEstudio) {
		
		String sql = "select * from seleccion.datos_especificos where id_datos_generales = 2 and valor_alf = 'FPFAC'";
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
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
	
	
}
