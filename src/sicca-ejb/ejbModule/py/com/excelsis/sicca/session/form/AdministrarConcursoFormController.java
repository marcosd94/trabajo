package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.util.Naming;
import org.richfaces.model.UploadItem;

import bsh.util.Util;
import enums.HorasAnios;
import excepciones.OeeException;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SelFuncionDatosEsp;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Funcion;
import py.com.excelsis.sicca.seguridad.entity.RolFuncion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.UsuariosConectadosList;
import py.com.excelsis.sicca.session.util.CheckSum;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("administrarConcursoFormController")
public class AdministrarConcursoFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true, required = false)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ConcursoHome concursoHome;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	private Entidad entidad = new Entidad();
	private Concurso concurso;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Integer ordenUnidOrg;
	private Long idTipoConcurso;
	private String radioButton;
	private String operacion = null;
	private String ubicacionFisica;
	private String radioPcd;
	private String radioDesprecarizado;
	private SeguridadUtilFormController seguridadUtilFormController;
	private DatosEspecificos datEspe;
	private Long idConcurso;
	private Long idConcursoPuestoAgr;
	private Boolean mostrarAdReferendum = true;
	private Boolean mostrarDesprecarizado = false;
	//Grupos para los concursos para publicacion
	private List <ConcursoPuestoAgr> concursoPuestoAgrList ;
	private ConcursoPuestoAgr concursoPuestoAgrNuevo;
	private FechasGrupoPuesto fechasGrupoPuesto;
	private Date fechaPublicacionDesde,fechaPublicacionHasta,fechaRecepcionDesde,fechaRecepcionHasta;
	private String horaPublicacionDesde, horaPublicacionHasta,horaRecepcionDesde,horaRecepcionHasta;
	private SimpleDateFormat sdfhora = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat sdffecha = new SimpleDateFormat("dd/MM/yyyy");
	
	//Atributo para almacenar si un concurso es por merito o terna, el puntaje minimo a cumplir.
	public static final String CODIGO_TERNA = "T";
	public static final String CODIGO_PUNTAJE = "P";
	private String seleccionAdjudicados;
	private Integer minimoEvaluacion;
	
	//Documentos Adjuntos
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	private String actualFileName;
	private Documentos documentos;
	
	private UploadItem item;
	private Integer tamArchivo;
	private String tipoDoc;
	private Long idFuncionDatosEsp;
	private String nomfinal;
	
	//Para la Publicacion de los Concursos para Publicacion
	private String publicTitulo,publicDetalle,publicObservacion,publicDetalleSinNegrita,publicObservacionSinNegrita;
	private Date fechaConvocatoria; 
	private String tituloConvocatoria,horaDesdeConvocatoria,horaHastaConvocatoria,lugarConvocatoria,departamentoConvocatoria, cuidadConvocatoria,direccionConvocatoria,observacionConvocatoria;
	private List <PublicacionPortal> publicacionPortalList;
	private PublicacionPortal publicacionPortalNuevo;
	private Documentos documentosAdjuntoPublicacion;
	private String tituloDocumentoAdjunto;
	private Long idTabla;
	
	

	

	/**
	 * PARAMENTROS QUE DEBE RECIBIR NECESARIAMENTE PARA EL FUNCIONAMIENTO
	 **/
	private boolean mostrarDoc;// true muestra el campo Doc false no
	private String direccionFisica;// direccion del archivo adjunto
	private String nombreTabla;// Nombre de la Entidad
	private Long idCU;// id Del CU
	private String nombrePantalla;// nombre de la pantalla del CU llamador
	private static final String PRE = locate();
	static List<String> lContetTypeProhibidos = Arrays
			.asList("application/octet-stream");
	private String dirFisica;

	

	

	/**
	 * Método que valida si la OEE del concurso es igual a la OEE del usuario
	 * logueado
	 */
	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso
				.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	/**
	 * Metodo que inicializa los valores
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		mostrarAdReferendum = true;
		concurso = concursoHome.getInstance();
		idConcurso = concurso.getIdConcurso();
		seleccionAdjudicados = "";
		minimoEvaluacion = 0;
		actualFileName = "";
		if (idConcurso != null)
			operacion = "updated";
		else
			operacion = "persisted";
		datEspe = seleccionUtilFormController
				.traerTipoConcursoConcurso("CONCURSO INTERNO DE OPOSICION");
		buscarDatosAsociadosUsuario();
		if (concursoHome.isIdDefined()) {

			validarOee();

			idTipoConcurso = concurso.getDatosEspecificosTipoConc()
					.getIdDatosEspecificos();
			esMeritoOinterInstitucional();
			if (mostrarAdReferendum) {
				if (concurso.getAdReferendum())
					radioButton = "SI";
				else
					radioButton = "NO";
			}
			if (concurso.getPcd())
				radioPcd = "SI";
			else
				radioPcd = "NO";
			if (mostrarDesprecarizado) {
				if (concurso.getDesprecarizacion())
					radioDesprecarizado = "SI";
				else
					radioDesprecarizado = "NO";
			}
			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;
			String separador = File.separator;
			ubicacionFisica = separador + "SICCA" + separador + anho
					+ separador + "OEE" + separador
					+ configuracionUoCab.getIdConfiguracionUo() + separador
					+ idTipoConcurso + separador
					+ concursoHome.getInstance().getIdConcurso();

		}
	}
	
	/**
	 * Metodo que inicializa los valores para la pantalla para agregar grupo a los concurso para Publicación
	 * 
	 * @throws Exception
	 */
	public void initAgregarPublicacion() throws Exception {
		
		concursoPuestoAgrNuevo = concursoPuestoAgrHome.getInstance();
		idConcursoPuestoAgr = concursoPuestoAgrNuevo.getIdConcursoPuestoAgr();
		idConcurso = concursoPuestoAgrNuevo.getConcurso().getIdConcurso();
		
		publicacionPortalList = ObtenerPublicacionListXidConcursoPuestoAgr(idConcursoPuestoAgr);
		
		publicacionPortalNuevo = new PublicacionPortal();
		
		
		
		
		
		
	}
	
	/**
	 * Metodo que inicializa los valores para la pantalla para agregar grupo a los concurso para Publicación
	 * 
	 * @throws Exception
	 */
	public void initConcursoPuestoAgrEdit() throws Exception {
		
		concursoPuestoAgrNuevo = concursoPuestoAgrHome.getInstance();
		idConcursoPuestoAgr = concursoPuestoAgrNuevo.getIdConcursoPuestoAgr();
		idConcurso = concursoPuestoAgrNuevo.getConcurso().getIdConcurso();
		
		//Se recupera el porcentaje minimo de evaluacion
		this.minimoEvaluacion = obtenerPorcentajeMinimoGrupo(idConcursoPuestoAgr);
		//Se recupera el tipo de evaluacion (MERITO o TERNA)
		if(obtenerTipoEvaluacionGrupo(idConcursoPuestoAgr).equals("MÉRITO")){
			this.seleccionAdjudicados = CODIGO_PUNTAJE;
		}
		if(obtenerTipoEvaluacionGrupo(idConcursoPuestoAgr).equals("TERNA")){
			this.seleccionAdjudicados = CODIGO_TERNA;
		}
		//Se recupera el archivo de Bases y Condiciones
		String sql = "select * from general.documentos where id_datos_especificos_tipo_documento = 1491 and id_concurso_puesto_agr = "+idConcursoPuestoAgr;
		List<Documentos> lista = em.createNativeQuery(sql,Documentos.class).getResultList();
		this.actualFileName = lista.get(lista.size()-1).getNombreFisicoDoc();
		
		//Se recuperan las fechas
		fechasGrupoPuesto = ObtenerFechasGrupoPuestoXIdConcursoPuestoAgr(idConcursoPuestoAgr);

		this.fechaPublicacionDesde = fechasGrupoPuesto.getFechaPublicacionDesde();  
		this.horaPublicacionDesde = sdfhora.format(fechasGrupoPuesto.getHoraPublicacionDesde());
		this.fechaPublicacionHasta =  fechasGrupoPuesto.getFechaPublicacionHasta();
		this.horaPublicacionHasta = sdfhora.format(fechasGrupoPuesto.getHoraPublicacionHasta());
		this.fechaRecepcionDesde = fechasGrupoPuesto.getFechaRecepcionDesde();
		this.horaRecepcionDesde = sdfhora.format(fechasGrupoPuesto.getHoraRecepcionDesde());
		this.fechaRecepcionHasta  = fechasGrupoPuesto.getFechaRecepcionHasta();
		this.horaRecepcionHasta = sdfhora.format(fechasGrupoPuesto.getHoraRecepcionHasta());
				
		this.volverListaGrupos();
		
	}
	
	
	/**
	 * Metodo que inicializa los valores para la pantalla para agregar grupo a los concurso para Publicación
	 * 
	 * @throws Exception
	 */
	public void initRealizarProrroga() throws Exception {
		
		concursoPuestoAgrNuevo = concursoPuestoAgrHome.getInstance();
		idConcursoPuestoAgr = concursoPuestoAgrNuevo.getIdConcursoPuestoAgr();
		idConcurso = concursoPuestoAgrNuevo.getConcurso().getIdConcurso();
		
		fechasGrupoPuesto = ObtenerFechasGrupoPuestoXIdConcursoPuestoAgr(idConcursoPuestoAgr);

		this.fechaPublicacionDesde = fechasGrupoPuesto.getFechaPublicacionDesde();  
		this.horaPublicacionDesde = sdfhora.format(fechasGrupoPuesto.getHoraPublicacionDesde());
		this.fechaPublicacionHasta =  fechasGrupoPuesto.getFechaPublicacionHasta();
		this.horaPublicacionHasta = sdfhora.format(fechasGrupoPuesto.getHoraPublicacionHasta());
		this.fechaRecepcionDesde = fechasGrupoPuesto.getFechaRecepcionDesde();
		this.horaRecepcionDesde = sdfhora.format(fechasGrupoPuesto.getHoraRecepcionDesde());
		this.fechaRecepcionHasta  = fechasGrupoPuesto.getFechaRecepcionHasta();
		this.horaRecepcionHasta = sdfhora.format(fechasGrupoPuesto.getHoraRecepcionHasta());
				
		this.volverListaGrupos();
		
	}
	
	private List <PublicacionPortal> ObtenerPublicacionListXidConcursoPuestoAgr (Long idConcursoPuestoAgr){
		
		String sql = "select * from seleccion.publicacion_portal where activo = true and id_concurso_puesto_agr = " + idConcursoPuestoAgr + " order by fecha_alta asc ";
		
		return em.createNativeQuery(sql,PublicacionPortal.class).getResultList();
	}
	
	
	
	/**
	 * Método para agregar los concursoPuestoAgr al concurso para publicacion
	 * @throws Exception 
	 */
	public void btnAgregarPublicacion() throws Exception{
		
						
			try {
				
				Long id= null;
				
				if(fileName != null && !fileName.equals("")){
					if(tituloDocumentoAdjunto.equals("")){
						tituloDocumentoAdjunto = "Documento Adjunto";
						
					}
					
					id = this.guardarAdjuntoPublicacion(fileName, tamArchivo, contentType, uploadedFile);
				
					 documentosAdjuntoPublicacion = em.find(Documentos.class,id);
					 
					 
				}
				
				PublicacionPortal pp = new PublicacionPortal();
				
				String texto = this.genTextoPublicacion();
				
				
				
					 pp = insertarPublicacionPortal(idConcursoPuestoAgr, idConcurso,texto );
				
								
				if(id != null ){
										
					Documentos doc = em.find(Documentos.class, id);
					
					doc.setIdTabla(pp.getIdPublicacionPortal());
					em.merge(doc);
					em.flush();
					
					
					 texto = pp.getTexto(); 
					
									
					texto = texto + "<dd><h3><a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=ImprimirDocAdjunto&#38;idPublicacionPortal="
							+pp.getIdPublicacionPortal()
							+ "'>"
							+ tituloDocumentoAdjunto
							+ "</a></h3></dd>";
					
					pp.setTexto(texto);
					em.merge(pp);
					em.flush();
					
					
				}
				
							
				
				
					publicTitulo = "";
					publicDetalle = "";
					publicObservacion = "";
					tituloDocumentoAdjunto = "";
					publicDetalleSinNegrita = "";
					publicObservacionSinNegrita = "";
					tituloConvocatoria = "";
					fechaConvocatoria = null;
					horaDesdeConvocatoria = "";
					horaHastaConvocatoria = "";
					lugarConvocatoria = "";
					departamentoConvocatoria = "";
					cuidadConvocatoria = "";
					direccionConvocatoria = "";
					
				
					
					publicacionPortalList = this.ObtenerPublicacionListXidConcursoPuestoAgr(idConcursoPuestoAgr);
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			
		
	}
	
	private String genTextoPublicacion() {
		String texto = new String();
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdfFecha.format(new Date()).toString();
		String hr = "<hr>";
		String h1O = "<h1>";
		String h1C = "</h1>";
		String h5O = "<h5>";
		String h5C = "</h5>";
		String h3O = "<h3>";
		String h3C = "</h3>";
		String h4O = "<h4>";
		String h4C = "</h4>";
		String spanO = "<span>";
		String spanC = "</span>";
		String boldO = "<b>";
		String boldC = "</b>";
		String tabO = "<dd>";
		String tabC = "</dd>";
		String fontO = "<font size = 3>";
		String fontC = "</font>";
		String br = "</br>";
		
			if(publicTitulo != null && !publicTitulo.equals(""))
				texto += h1O + publicTitulo+ h1C;
			
			if(publicDetalle != null && !publicDetalle.equals(""))
				texto += tabO + h3O + spanO + publicDetalle+ spanC+ h3C + tabC;
			
			if(publicDetalleSinNegrita != null && !publicDetalleSinNegrita.equals(""))
				texto +=   tabO +fontO + publicDetalleSinNegrita +fontC + tabC +br ;
			
			if(publicObservacion != null && !publicObservacion.equals(""))
				texto +=   tabO +spanO + boldO + publicObservacion  + boldC + spanC ;
			
			if (publicObservacionSinNegrita != null && !publicObservacionSinNegrita.equals(""))
				texto +=  fontO + " " + publicObservacionSinNegrita +fontC  + tabC;
			
			
			if(tituloConvocatoria != null && !tituloConvocatoria.equals(""))
				texto += h3O + spanO + tituloConvocatoria+ spanC+ h3C;
			
			if(fechaConvocatoria != null && !sdffecha.format(fechaConvocatoria).equals("")){
				texto +=   tabO +spanO + boldO + "Fecha: "  + boldC + spanC +  fontO  + sdffecha.format(fechaConvocatoria);
				
				if(horaDesdeConvocatoria != null && !horaDesdeConvocatoria.equals("")){
					texto +=    " - "  + horaDesdeConvocatoria ;
					if(horaHastaConvocatoria != null && !horaHastaConvocatoria.equals(""))
						texto +=   " A  "  +  horaHastaConvocatoria;
				}
				
				texto +=  fontC + tabC;
				
			}
				
			
			if(lugarConvocatoria != null && !lugarConvocatoria.equals(""))
				texto +=   tabO +spanO + boldO + "Lugar: "  + boldC + spanC +  fontO  + lugarConvocatoria  +fontC  + tabC;
			
			if(departamentoConvocatoria != null && !departamentoConvocatoria.equals(""))
				texto +=   tabO +spanO + boldO + "Departamento: "  + boldC + spanC +  fontO  + departamentoConvocatoria  +fontC  + tabC;
			
			if(cuidadConvocatoria != null && !cuidadConvocatoria.equals(""))
				texto +=   tabO +spanO + boldO + "Cuidad: "  + boldC + spanC +  fontO  + cuidadConvocatoria  +fontC  + tabC;
			
						
			if(direccionConvocatoria != null && !direccionConvocatoria.equals(""))
				texto +=   tabO +spanO + boldO + "Dirección: "  + boldC + spanC +  fontO  + direccionConvocatoria  +fontC  + tabC;
				
			
			
		return hr + fechaPublicacion + texto;
	}
	
	
private void genPublicacionProrroga(ConcursoPuestoAgr concursoPuestoAgr, FechasGrupoPuesto fechasGrupoPuesto) {
		
		
		
		
		String texto = new String();
		String hr = "<hr>";
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdfFecha.format(new Date()).toString();
		String h1O = "<h1>";
		String h1C = "</h1>";
		String h3O = "<h3>";
		String h3C = "</h3>";
		String spanO = "<span>";
		String spanC = "</span>";
		String br = "</br>";
		String tabO = "<dd>";
		String tabC = "</dd>";
			texto = texto
					+ hr + fechaPublicacion
					+ h3O
					+ "Se prorroga el plazo de Recepción de Postulaciones hasta: "
					+ this.sdffecha.format(fechaRecepcionHasta)+" - "+ sdfhora.format(fechasGrupoPuesto.getHoraRecepcionHasta())
					+ h3C
					
					;
		
			insertarPublicacionPortal(concursoPuestoAgr.getIdConcursoPuestoAgr(),concursoPuestoAgr.getConcurso().getIdConcurso(),texto);
		
		
	}
	
	private void genPrimeraPublicacion(ConcursoPuestoAgr concursoPuestoAgr, FechasGrupoPuesto fechasGrupoPuesto) {
		
		
		
		
		String texto = new String();
		String hr = "<hr>";
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdfFecha.format(new Date()).toString();
		String h1O = "<h1>";
		String h1C = "</h1>";
		String h3O = "<h3>";
		String h3C = "</h3>";
		String spanO = "<span>";
		String spanC = "</span>";
		String br = "</br>";
		String tabO = "<dd>";
		String tabC = "</dd>";
			texto = texto
					+ hr + fechaPublicacion
					+ h1O
					+ concursoPuestoAgr.getConcurso().getConfiguracionUoCab().getDenominacionUnidad()
					+ h1C
					+ tabO
					+ h3O
					+concursoPuestoAgr.getConcurso().getNombre()
					+ h3C
					+ tabC
					+ tabO
					+ h3O
					+concursoPuestoAgr.getConcurso().getDatosEspecificosTipoConc().getDescripcion()
					+ h3C
					+ tabC
					+ tabO
					+ h3O
					+concursoPuestoAgr.getDescripcionGrupo()
					+ h3C
					+ tabC
					+ tabO
					+ h3O
					+ "Publicado en el Portal de Paraguay Concursa desde " + this.sdffecha.format(fechaPublicacionDesde)+" - "+ sdfhora.format(fechasGrupoPuesto.getHoraPublicacionDesde())
					+ " hasta " + this.sdffecha.format(fechaPublicacionHasta)+" - "+ sdfhora.format(fechasGrupoPuesto.getHoraPublicacionHasta())
					+ h3C
					+ tabC
					+ tabO
					+ h3O
					+ "Periodo de Postulación : " + this.sdffecha.format(fechaRecepcionDesde)+" - "+ sdfhora.format(fechasGrupoPuesto.getHoraRecepcionDesde())
					+ " A " + this.sdffecha.format(fechaRecepcionHasta)+" - "+ sdfhora.format(fechasGrupoPuesto.getHoraRecepcionHasta())
					+ h3C
					+ tabC
					;
		
			insertarPublicacionPortal(concursoPuestoAgr.getIdConcursoPuestoAgr(),concursoPuestoAgr.getConcurso().getIdConcurso(),texto);
		
		
	}
	
	public PublicacionPortal insertarPublicacionPortal(Long idConcursoPuestoAgr, Long idConcurso, String texto) {
		PublicacionPortal entity = new PublicacionPortal();
		entity.setFechaAlta(new Date());
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		entity.setActivo(true);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		entity.setConcurso(new Concurso());
		entity.getConcurso().setIdConcurso(idConcurso);
		entity.setTexto(texto);
		em.persist(entity);
		em.flush();
		
		return entity;

	}
	
	public void eliminarPublicacion(Long idPublicacionPortal){
		try {
			PublicacionPortal pp = em.find(PublicacionPortal.class, idPublicacionPortal);
			pp.setActivo(false);
			em.merge(pp);
			em.flush();
			publicacionPortalList = this.ObtenerPublicacionListXidConcursoPuestoAgr(concursoPuestoAgrNuevo.getIdConcursoPuestoAgr());
				
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public String interpretarHtml(String texto) throws IOException, BadLocationException{
		 
       
		EditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        try {
            Reader reader = new StringReader(texto);
            kit.read(reader, doc, 0);
            
            return doc.getText(0, doc.getLength());
        } catch (Exception e) {
          
            return "";
        }
        
    }
	
	/**
	 * Metodo que inicializa los valores para la pantalla para agregar grupo a los concurso para Publicación
	 * 
	 * @throws Exception
	 */
	public void initAgregarGrupo() throws Exception {
		if(idConcurso == null){
			concurso = concursoHome.getInstance();
			idConcurso = concurso.getIdConcurso();
		}
		
		concursoPuestoAgrList = ObtenerGruposXidConcurso(idConcurso);
		
		concursoPuestoAgrNuevo = new ConcursoPuestoAgr();
		//Valores que según la base de datos son Not Null
		concursoPuestoAgrNuevo.setCodGrupo("1");
		concursoPuestoAgrNuevo.setNroOrden(1);
		concursoPuestoAgrNuevo.setConcurso(concurso);
		concursoPuestoAgrNuevo.setActivo(true);
		concursoPuestoAgrNuevo.setEstado(1000);//ESTADO PUBLICACION
		
		fechasGrupoPuesto = new FechasGrupoPuesto();
		fechaPublicacionDesde = null;
		fechaPublicacionHasta = null;
		fechaRecepcionDesde = null;
		fechaRecepcionHasta = null;
		horaPublicacionDesde = "";
		horaPublicacionHasta = "";
		horaRecepcionDesde = "";
		horaRecepcionHasta = "";
		seleccionAdjudicados = "";
		minimoEvaluacion = 0;
		
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String separador = File.separator;
		ubicacionFisica = separador + "SICCA" + separador + anho
				+ separador + "OEE" + separador
				+ configuracionUoCab.getIdConfiguracionUo() + separador
				+ idTipoConcurso + separador
				+ concursoHome.getInstance().getIdConcurso();
		
	}
	/**
	 * Método que busca los grupos ya asociados a un concurso para los concursos para publicacion
	 */
	private List<ConcursoPuestoAgr> ObtenerGruposXidConcurso (Long idConcurso){
		String sql = "select * from seleccion.concurso_puesto_agr cpa "
				+ " join seleccion.fechas_grupo_puesto fechas on fechas.id_concurso_puesto_agr = cpa.id_concurso_puesto_agr "
				+ " where cpa.activo = true and cpa.id_concurso = " + idConcurso +" order by fechas.fecha_publicacion_desde desc, fechas.hora_publicacion_desde desc ";
		
		List <ConcursoPuestoAgr> retorno = em.createNativeQuery(sql, ConcursoPuestoAgr.class).getResultList(); 
		
		return retorno;
	}
	
	/**
	 * Método para agregar los concursoPuestoAgr al concurso para publicacion
	 * @throws Exception 
	 */
	public void btnAgregarGrupo() throws Exception{
		
					
			if(datosRequeridosGrupoOk())
			{
				ConcursoPuestoAgr cpa = this.concursoPuestoAgrNuevo;
				cpa.setFechaAlta(new Date());
				cpa.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				
				em.persist(cpa);
				em.flush();
				
				
				fechasGrupoPuesto.setConcursoPuestoAgr(cpa);
				try {
					fechasGrupoPuesto.setFechaPublicacionDesde((this.fechaPublicacionDesde));
					fechasGrupoPuesto.setHoraPublicacionDesde(sdfhora.parse(this.horaPublicacionDesde));
					fechasGrupoPuesto.setFechaPublicacionHasta(this.fechaPublicacionHasta);
					fechasGrupoPuesto.setHoraPublicacionHasta(sdfhora.parse(this.horaPublicacionHasta));
					fechasGrupoPuesto.setFechaRecepcionDesde(this.fechaRecepcionDesde);
					fechasGrupoPuesto.setHoraRecepcionDesde(sdfhora.parse(this.horaRecepcionDesde));
					fechasGrupoPuesto.setFechaRecepcionHasta(this.fechaRecepcionHasta);
					fechasGrupoPuesto.setHoraRecepcionHasta(sdfhora.parse(this.horaRecepcionHasta));
					
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fechasGrupoPuesto.setFechaVigProcDesde(fechasGrupoPuesto.getFechaRecepcionHasta());
				fechasGrupoPuesto.setFechaVigProcHasta(sugerenciaDateVigencia(fechasGrupoPuesto.getFechaVigProcDesde()));
				fechasGrupoPuesto.setFechaAlta(new Date());
				fechasGrupoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				fechasGrupoPuesto.setHoraVigProcDesde(fechasGrupoPuesto.getHoraPublicacionDesde());
				fechasGrupoPuesto.setHoraVigProcHasta(fechasGrupoPuesto.getHoraPublicacionHasta());
				fechasGrupoPuesto.setActivo(true);
				fechasGrupoPuesto.setEstado("VERIFICADO");
				
				em.persist(fechasGrupoPuesto);
				em.flush();
				
				cpa.getFechasGrupoPuestos().add(fechasGrupoPuesto);
				em.merge(cpa);
				em.flush();
				
				
				this.genPrimeraPublicacion(cpa,fechasGrupoPuesto);
				
				concursoPuestoAgrList = this.ObtenerGruposXidConcurso(idConcurso);
					
				Long id = this.guardarBasesYCondiciones(fileName, tamArchivo, contentType, uploadedFile);
				
				DatosGrupoPuesto datosGrupoPuesto = new DatosGrupoPuesto();
				// Cargar datos para el objeto DatosGrupoPuesto antes de almacenarlo en la base de datos
				if (CODIGO_TERNA.equals(seleccionAdjudicados)) {
					datosGrupoPuesto.setTerna(true);
					datosGrupoPuesto.setMerito(false);
				} else {
					datosGrupoPuesto.setTerna(false);
					datosGrupoPuesto.setMerito(true);
				}
				datosGrupoPuesto.setPorcMinimo(minimoEvaluacion);
				datosGrupoPuesto.setConcursoPuestoAgr(cpa);
				datosGrupoPuesto.setActivo(true);
				datosGrupoPuesto.setFechaAlta(new Date());
				datosGrupoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				datosGrupoPuesto.setPreferenciaPersDiscapacidad(false);
				em.persist(datosGrupoPuesto);
				em.flush();
				seleccionAdjudicados = "";
				minimoEvaluacion = 0;
				
				if(id == null ){
					throw new Exception("No se pudo guardar Documento de Bases y Condiciones...");
								
				}
				
				this.initAgregarGrupo();
			}
		
	}
	private boolean datosRequeridosGrupoOk(){
		boolean retorno = true;
		String sqlPublicacion = "select * from seleccion.referencias where dominio like 'PLAZO' and desc_abrev like 'PUBLICACION_CONCURSO'";
		Referencias diasMinimosPublicacion = (Referencias)  em.createNativeQuery(sqlPublicacion, Referencias.class).getSingleResult();
		
		String sqlRecepcion = "select * from seleccion.referencias where dominio like 'PLAZO' and desc_abrev like 'RECEPCION_POST_LINEA'";
		Referencias diasMinimosRecepcion = (Referencias)  em.createNativeQuery(sqlRecepcion, Referencias.class).getSingleResult();
		
		if(concursoPuestoAgrNuevo.getDescripcionGrupo().equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe asignar un Nombre al Grupo");
			retorno = false;
			return retorno;
			
		}else if(fileName == null || fileName.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Seleccionar un Documento de Bases y Condiciones");
			retorno = false;
			return retorno;
		}else if(fechaPublicacionDesde == null || fechaPublicacionDesde.equals("") 
				||this.horaPublicacionDesde == null || horaPublicacionDesde.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Publicacion 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaPublicacionHasta == null || fechaPublicacionHasta.equals("")
				||this.horaPublicacionHasta == null || horaPublicacionHasta.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Publicacion 'Hasta'");
			retorno = false;
			return retorno;
		}else if(fechaRecepcionDesde == null || fechaRecepcionDesde.equals("")
				||this.horaRecepcionDesde == null || horaRecepcionDesde.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Recepción 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaRecepcionHasta == null || fechaRecepcionHasta.equals("")
				||this.horaRecepcionHasta == null || horaRecepcionHasta.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Recepción 'Hasta'");
			retorno = false;
			return retorno;
		}else if(fechaPublicacionHasta.equals(fechaPublicacionDesde) || fechaPublicacionHasta.before(fechaPublicacionDesde) ){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Fecha Publicación 'Hasta' debe ser posterior a Fecha Publicación 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaPublicacionHasta.after(fechaPublicacionDesde) ){
			  
						  
			Calendar cal = new GregorianCalendar();
		    cal.setTimeInMillis(fechaPublicacionDesde.getTime());
		    cal.add(Calendar.DATE, diasMinimosPublicacion.getValorNum().intValue());
			
			Date fechaMinima = new Date(cal.getTimeInMillis());
			if(fechaPublicacionHasta.before(fechaMinima)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
				"Fecha Publicación 'Hasta' debe ser como mínimo " +diasMinimosPublicacion.getValorNum().intValue() +" días posterior a la Fecha Publicación 'Desde'");
				retorno = false;
				return retorno;
			}
		}
		else if(fechaRecepcionHasta.equals(fechaRecepcionDesde) || fechaRecepcionHasta.before(fechaRecepcionDesde) ){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Fecha Recepción 'Hasta' debe ser posterior a Fecha Recepción 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaRecepcionHasta.after(fechaRecepcionDesde) ){
			  
						  
			Calendar cal = new GregorianCalendar();
		    cal.setTimeInMillis(fechaRecepcionDesde.getTime());
		    cal.add(Calendar.DATE, diasMinimosRecepcion.getValorNum().intValue());
			
			Date fechaMinima = new Date(cal.getTimeInMillis());
			if(fechaRecepcionHasta.before(fechaMinima)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
				"Fecha Recepción 'Hasta' debe ser como mínimo " +diasMinimosRecepcion.getValorNum().intValue() +" días posterior a la Fecha Recepción 'Desde'");
				retorno = false;
				return retorno;
			}
		}
				
				
		return retorno;
		
	}
	
	private boolean datosRequeridosEditGrupoOk(){
		boolean retorno = true;
		String sqlPublicacion = "select * from seleccion.referencias where dominio like 'PLAZO' and desc_abrev like 'PUBLICACION_CONCURSO'";
		Referencias diasMinimosPublicacion = (Referencias)  em.createNativeQuery(sqlPublicacion, Referencias.class).getSingleResult();
		
		String sqlRecepcion = "select * from seleccion.referencias where dominio like 'PLAZO' and desc_abrev like 'RECEPCION_POST_LINEA'";
		Referencias diasMinimosRecepcion = (Referencias)  em.createNativeQuery(sqlRecepcion, Referencias.class).getSingleResult();
		
		if(concursoPuestoAgrNuevo.getDescripcionGrupo().equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe asignar un Nombre al Grupo");
			retorno = false;
			return retorno;
			
		}else if(fechaPublicacionDesde == null || fechaPublicacionDesde.equals("") 
				||this.horaPublicacionDesde == null || horaPublicacionDesde.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Publicacion 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaPublicacionHasta == null || fechaPublicacionHasta.equals("")
				||this.horaPublicacionHasta == null || horaPublicacionHasta.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Publicacion 'Hasta'");
			retorno = false;
			return retorno;
		}else if(fechaRecepcionDesde == null || fechaRecepcionDesde.equals("")
				||this.horaRecepcionDesde == null || horaRecepcionDesde.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Recepción 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaRecepcionHasta == null || fechaRecepcionHasta.equals("")
				||this.horaRecepcionHasta == null || horaRecepcionHasta.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Recepción 'Hasta'");
			retorno = false;
			return retorno;
		}else if(fechaPublicacionHasta.equals(fechaPublicacionDesde) || fechaPublicacionHasta.before(fechaPublicacionDesde) ){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Fecha Publicación 'Hasta' debe ser posterior a Fecha Publicación 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaPublicacionHasta.after(fechaPublicacionDesde) ){
			  
						  
			Calendar cal = new GregorianCalendar();
		    cal.setTimeInMillis(fechaPublicacionDesde.getTime());
		    cal.add(Calendar.DATE, diasMinimosPublicacion.getValorNum().intValue());
			
			Date fechaMinima = new Date(cal.getTimeInMillis());
			if(fechaPublicacionHasta.before(fechaMinima)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
				"Fecha Publicación 'Hasta' debe ser como mínimo " +diasMinimosPublicacion.getValorNum().intValue() +" días posterior a la Fecha Publicación 'Desde'");
				retorno = false;
				return retorno;
			}
		}
		else if(fechaRecepcionHasta.equals(fechaRecepcionDesde) || fechaRecepcionHasta.before(fechaRecepcionDesde) ){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Fecha Recepción 'Hasta' debe ser posterior a Fecha Recepción 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaRecepcionHasta.after(fechaRecepcionDesde) ){
			  
						  
			Calendar cal = new GregorianCalendar();
		    cal.setTimeInMillis(fechaRecepcionDesde.getTime());
		    cal.add(Calendar.DATE, diasMinimosRecepcion.getValorNum().intValue());
			
			Date fechaMinima = new Date(cal.getTimeInMillis());
			if(fechaRecepcionHasta.before(fechaMinima)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
				"Fecha Recepción 'Hasta' debe ser como mínimo " +diasMinimosRecepcion.getValorNum().intValue() +" días posterior a la Fecha Recepción 'Desde'");
				retorno = false;
				return retorno;
			}
		}
				
				
		return retorno;
		
	}
	
	private boolean datosRequeridosProrrogaOk(){
		boolean retorno = true;
		
		String sqlPublicacion = "select * from seleccion.referencias where dominio like 'PLAZO' and desc_abrev like 'PUBLICACION_CONCURSO'";
		Referencias diasMinimosPublicacion = (Referencias)  em.createNativeQuery(sqlPublicacion, Referencias.class).getSingleResult();
		
		String sqlRecepcion = "select * from seleccion.referencias where dominio like 'PLAZO' and desc_abrev like 'RECEPCION_POST_LINEA'";
		Referencias diasMinimosRecepcion = (Referencias)  em.createNativeQuery(sqlRecepcion, Referencias.class).getSingleResult();
		
		if(fechaPublicacionDesde == null || fechaPublicacionDesde.equals("") 
				||this.horaPublicacionDesde == null || horaPublicacionDesde.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Publicacion 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaPublicacionHasta == null || fechaPublicacionHasta.equals("")
				||this.horaPublicacionHasta == null || horaPublicacionHasta.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Publicacion 'Hasta'");
			retorno = false;
			return retorno;
		}else if(fechaRecepcionDesde == null || fechaRecepcionDesde.equals("")
				||this.horaRecepcionDesde == null || horaRecepcionDesde.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Recepción 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaRecepcionHasta == null || fechaRecepcionHasta.equals("")
				||this.horaRecepcionHasta == null || horaRecepcionHasta.equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Debe Asignar las Fechas Y horas de Recepción 'Hasta'");
			retorno = false;
			return retorno;
		}else if(fechaPublicacionHasta.equals(fechaPublicacionDesde) || fechaPublicacionHasta.before(fechaPublicacionDesde) ){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Fecha Publicación 'Hasta' debe ser posterior a Fecha Publicación 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaPublicacionHasta.after(fechaPublicacionDesde) ){
			  
						  
			Calendar cal = new GregorianCalendar();
		    cal.setTimeInMillis(fechaPublicacionDesde.getTime());
		    cal.add(Calendar.DATE, diasMinimosPublicacion.getValorNum().intValue());
			
			Date fechaMinima = new Date(cal.getTimeInMillis());
			if(fechaPublicacionHasta.before(fechaMinima)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
				"Fecha Publicación 'Hasta' debe ser como mínimo " +diasMinimosPublicacion.getValorNum().intValue() +" días posterior a la Fecha Publicación 'Desde'");
				retorno = false;
				return retorno;
			}
		}
		else if(fechaRecepcionHasta.equals(fechaRecepcionDesde) || fechaRecepcionHasta.before(fechaRecepcionDesde) ){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
			"Fecha Recepción 'Hasta' debe ser posterior a Fecha Recepción 'Desde'");
			retorno = false;
			return retorno;
		}else if(fechaRecepcionHasta.after(fechaRecepcionDesde) ){
			  
						  
			Calendar cal = new GregorianCalendar();
		    cal.setTimeInMillis(fechaRecepcionDesde.getTime());
		    cal.add(Calendar.DATE, diasMinimosRecepcion.getValorNum().intValue());
			
			Date fechaMinima = new Date(cal.getTimeInMillis());
			if(fechaRecepcionHasta.before(fechaMinima)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
				"Fecha Recepción 'Hasta' debe ser como mínimo " +diasMinimosRecepcion.getValorNum().intValue() +" días posterior a la Fecha Recepción 'Desde'");
				retorno = false;
				return retorno;
			}
		}
				
				
		return retorno;
		
	}
	
	/**
	 * Método para agregar los concursoPuestoAgr al concurso para publicacion
	 * @throws Exception 
	 */
	public String btnGuardarProrroga() throws Exception{
		if(datosRequeridosProrrogaOk()){
			try {
				fechasGrupoPuesto.setFechaPublicacionDesde((this.fechaPublicacionDesde));
				fechasGrupoPuesto.setHoraPublicacionDesde(sdfhora.parse(this.horaPublicacionDesde));
				fechasGrupoPuesto.setFechaPublicacionHasta(this.fechaPublicacionHasta);
				fechasGrupoPuesto.setHoraPublicacionHasta(sdfhora.parse(this.horaPublicacionHasta));
				fechasGrupoPuesto.setFechaRecepcionDesde(this.fechaRecepcionDesde);
				fechasGrupoPuesto.setHoraRecepcionDesde(sdfhora.parse(this.horaRecepcionDesde));
				fechasGrupoPuesto.setFechaRecepcionHasta(this.fechaRecepcionHasta);
				fechasGrupoPuesto.setHoraRecepcionHasta(sdfhora.parse(this.horaRecepcionHasta));
				
				
				
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			em.merge(fechasGrupoPuesto);
			ConcursoPuestoAgr cpa = em.find(ConcursoPuestoAgr.class, fechasGrupoPuesto.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			cpa.setEstado(1000);//estado Publicación
			em.merge(cpa);
			em.flush();
			
			this.genPublicacionProrroga(fechasGrupoPuesto.getConcursoPuestoAgr(), fechasGrupoPuesto);
			
			fechaPublicacionDesde = null;
			fechaPublicacionHasta = null;
			fechaRecepcionDesde = null;
			fechaRecepcionHasta = null;
			horaPublicacionDesde = "";
			horaPublicacionHasta = "";
			horaRecepcionDesde = "";
			horaRecepcionHasta = "";
			return this.volverListaGrupos();
		}else{
			return this.errorGuardarProrroga();
		}
				
		
	}
	
	/**
	 * Método para agregar los concursoPuestoAgr al concurso para publicacion
	 * @throws Exception 
	 */
	public String btnActualizarConcursoPuestoAgr() throws Exception{
		if(datosRequeridosEditGrupoOk()){
			try {
				fechasGrupoPuesto.setFechaPublicacionDesde((this.fechaPublicacionDesde));
				fechasGrupoPuesto.setHoraPublicacionDesde(sdfhora.parse(this.horaPublicacionDesde));
				fechasGrupoPuesto.setFechaPublicacionHasta(this.fechaPublicacionHasta);
				fechasGrupoPuesto.setHoraPublicacionHasta(sdfhora.parse(this.horaPublicacionHasta));
				fechasGrupoPuesto.setFechaRecepcionDesde(this.fechaRecepcionDesde);
				fechasGrupoPuesto.setHoraRecepcionDesde(sdfhora.parse(this.horaRecepcionDesde));
				fechasGrupoPuesto.setFechaRecepcionHasta(this.fechaRecepcionHasta);
				fechasGrupoPuesto.setHoraRecepcionHasta(sdfhora.parse(this.horaRecepcionHasta));	
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			em.merge(fechasGrupoPuesto);
			em.flush();
				
			String sql = "select * from seleccion.datos_grupo_puesto where id_concurso_puesto_agr = "+this.idConcursoPuestoAgr;
			List<DatosGrupoPuesto> lista = em.createNativeQuery(sql,DatosGrupoPuesto.class).getResultList();
			
			if(lista.size() != 0){
				lista.get(0).setPorcMinimo(minimoEvaluacion);
				if (CODIGO_TERNA.equals(seleccionAdjudicados)) {
					lista.get(0).setTerna(true);
					lista.get(0).setMerito(false);
				}else {
					lista.get(0).setTerna(false);
					lista.get(0).setMerito(true);
				}
				em.merge(lista.get(0));
				em.flush();
			}
			else{
				DatosGrupoPuesto datosGrupoPuesto = new DatosGrupoPuesto();
				// Cargar datos para el objeto DatosGrupoPuesto antes de almacenarlo en la base de datos
				if (CODIGO_TERNA.equals(seleccionAdjudicados)) {
					datosGrupoPuesto.setTerna(true);
					datosGrupoPuesto.setMerito(false);
				} else {
					datosGrupoPuesto.setTerna(false);
					datosGrupoPuesto.setMerito(true);
				}
				datosGrupoPuesto.setPorcMinimo(minimoEvaluacion);
				datosGrupoPuesto.setConcursoPuestoAgr(this.concursoPuestoAgrNuevo);
				datosGrupoPuesto.setActivo(true);
				datosGrupoPuesto.setFechaAlta(new Date());
				datosGrupoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				datosGrupoPuesto.setPreferenciaPersDiscapacidad(false);
				em.persist(datosGrupoPuesto);
				em.flush();
			}
			minimoEvaluacion = 0;
			seleccionAdjudicados = "";
			
			fechaPublicacionDesde = null;
			fechaPublicacionHasta = null;
			fechaRecepcionDesde = null;
			fechaRecepcionHasta = null;
			horaPublicacionDesde = "";
			horaPublicacionHasta = "";
			horaRecepcionDesde = "";
			horaRecepcionHasta = "";
			
			em.merge(concursoPuestoAgrNuevo);
			em.flush();
			
			if(!fileName.equals("")){
				Long id = this.guardarBasesYCondiciones(fileName, tamArchivo, contentType, uploadedFile);
			
				if(id == null ){
					throw new Exception("No se pudo guardar Documento de Bases y Condiciones...");				
				}
			}
			return this.volverListaGrupos();
		}else{
			return this.errorActualizarConcursoPuestoAgr();
		}
				
		
	}
	
	public void eliminarConcursoPuestoAgr(Long concursoPuestoAgr){
		try {
			ConcursoPuestoAgr cpa = em.find(ConcursoPuestoAgr.class, concursoPuestoAgr);
			cpa.setActivo(false);
			em.merge(cpa);
			em.flush();
			concursoPuestoAgrList = this.ObtenerGruposXidConcurso(idConcurso);
			
			FechasGrupoPuesto fgp = this.ObtenerFechasGrupoPuestoXIdConcursoPuestoAgr(cpa.getIdConcursoPuestoAgr());
			fgp.setActivo(false);
			em.merge(fgp);
			em.flush();
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void finalizarConcursoPuestoAgr(Long concursoPuestoAgr){
		try {
			ConcursoPuestoAgr cpa = em.find(ConcursoPuestoAgr.class, concursoPuestoAgr);
			cpa.setEstado(1002);//ESTADO PUBLICACION_FINALIZADO
			em.merge(cpa);
			em.flush();
			concursoPuestoAgrList = this.ObtenerGruposXidConcurso(idConcurso);
						
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public boolean isEstadoFinalizado(Integer idEstado){
		if(idEstado == 1002)//ESTADO PUBLICACION_FINALIZADO
			return true;
		else if(idEstado == 35)// CONCURSO SICCA DESIERTO
			return true;
		else if (idEstado == 38)// CONCURSO SICCA DESIERTO CON DOCUMENTO
			return true;
		
		return false;
	}
	
	public boolean isEstadoEvaluacion(Integer idEstado){
		return idEstado == 1001;//ESTADO PUBLICACION_EVALUACION
	}
	
	public boolean isEstadoListaLarga(Integer idEstado){
		return idEstado == 1003;//ESTADO PUBLICACION_LISTA_LARGA
	}
	public boolean isEstadoListaCorta(Integer idEstado){
		return idEstado == 1004;//ESTADO PUBLICACION_LISTA_CORTA
	}
	
	public boolean isEstadoListaTernaFinal(Integer idEstado){
		return idEstado == 1005;//ESTADO PUBLICACION_LISTA_TERNA_FINAL
	}
	public boolean isEstadoListaAdjudicados(Integer idEstado){
		return idEstado == 1006;//ESTADO PUBLICACION_LISTA_ADJUDICADOS
	}
	
	private FechasGrupoPuesto ObtenerFechasGrupoPuestoXIdConcursoPuestoAgr(Long idConcursoPuestoAgr){
		
		String sql = "select  * from seleccion.fechas_grupo_puesto where activo = true and id_concurso_puesto_agr = " + idConcursoPuestoAgr;
						
		List <FechasGrupoPuesto> list = em.createNativeQuery(sql,FechasGrupoPuesto.class).getResultList(); 
				
		return list.get(0);		
	}
	
	public Date sugerenciaDateVigencia(Date fechaDesde) {
		if (fechaDesde == null) {
			return null;
		}
		
		String abrev = "";
		
		StringBuffer SQL = new StringBuffer();
		SQL.append(" SELECT ");
		SQL.append("     REF");
		SQL.append(" FROM ");
		SQL.append("     Referencias REF ");
		SQL.append(" WHERE ");
		SQL.append("     REF.dominio = 'PLAZO' ");
		SQL.append(" AND REF.descAbrev = 'VIGENCIA_PROCESO_SELECCION'");
		SQL.append(" AND REF.activo = TRUE ");
		Query q = em.createQuery(SQL.toString());
		List<Referencias> ref = q.getResultList();
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaDesde);
		if (ref.size() == 1 && ref.get(0).getValorNum() != null) {
			cal.add(Calendar.DAY_OF_MONTH, ref.get(0).getValorNum().intValue());
				
			
		} 
		
		return cal.getTime();

	}
	
	public String volverListaGrupos(){
		concurso = em.find(Concurso.class, idConcurso);
		return "/seleccion/concursoPublicacion/AccionAgregarGrupo.seam?concursoIdConcurso="+idConcurso;
	}
	public String errorGuardarProrroga(){
		concurso = em.find(Concurso.class, idConcurso);
		return "/seleccion/concursoPublicacion/AccionRealizarProrroga.seam?idConcursoPuestoAgr="+idConcursoPuestoAgr;
	}
	
	public String errorActualizarConcursoPuestoAgr(){
		concurso = em.find(Concurso.class, idConcurso);
		return "/seleccion/concursoPublicacion/ConcursoPuestoAgrEdit.seam?idConcursoPuestoAgr="+idConcursoPuestoAgr;
	}
	
	
	public String formatFecha(Date fecha){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(fecha);
	}
	
	public String formatHora(Date hora){
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(hora);
	}
	
	private void crearUploadItem(String fileName, int fileSize,String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		tamArchivo = item.getFileSize();
		tipoDoc = item.getContentType();
		nomfinal = item.getFileName();
	}

	public static Boolean validarContentType(String contentType) {
		if (lContetTypeProhibidos.contains(contentType)) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private boolean superaTipoDocs() {
		try {

			// AGREGADO JD para adjuntar resolucion en firmar resolucion//
			if (idConcurso != null) {
				/*
				 * String q2 =
				 * "select concurso from Concurso concurso where concurso.idConcurso = :idConcurso"
				 * ; List<Concurso> concursoList = em.createQuery(q2)
				 * .setParameter("idConcurso", idConcurso).getResultList();
				 * concurso = concursoList.get(0);
				 * 
				 * SelFuncionDatosEsp sfe = em.find(SelFuncionDatosEsp.class,
				 * idFuncionDatosEsp); List<Documentos> cantResoluciones = em
				 * .createQuery( " select o from  Documentos  o " +
				 * "where o.datosEspecificos.idDatosEspecificos=" +
				 * sfe.getDatosEspecificos() .getIdDatosEspecificos() + " " +
				 * " and o.concurso=" + concurso +
				 * " and lower(o.nombreTabla) like '" +
				 * nombreTabla.toLowerCase() +
				 * "' and lower(o.nombrePantalla) like '" +
				 * nombrePantalla.toLowerCase() + "' " +
				 * " and o.activo=true").getResultList(); if
				 * (cantResoluciones.size() < sfe.getCantidad()) return false;
				 */
				return false;
				// //////////////FIN AGREGADO/////////////

			} else {

				SelFuncionDatosEsp sf = em.find(SelFuncionDatosEsp.class,
						idFuncionDatosEsp);
				List<Documentos> entityInterfaces = em
						.createQuery(
								" select o from  Documentos  o "
										+ "where o.datosEspecificos.idDatosEspecificos="
										+ sf.getDatosEspecificos()
												.getIdDatosEspecificos()
										+ " "
										+ " and o.idTabla="
										+ idCU
										+ " and lower(o.nombreTabla) like '"
										+ nombreTabla.toLowerCase()
										+ "' and lower(o.nombrePantalla) like '"
										+ nombrePantalla.toLowerCase() + "' "
										+ " and o.activo=true").getResultList();
				if (entityInterfaces.size() < sf.getCantidad())
					return false;
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	private static String locate() {
		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		String dir = "";
		try {
			emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
					"java:/siccaEntityManagerFactory");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		emsta = emf.createEntityManager();
		List<Referencias> referencias = emsta
				.createQuery(
						"Select r from Referencias r "
								+ " where r.descAbrev like 'ADJUNTOS' and r.dominio like 'ADJUNTOS'")
				.getResultList();
		if (!referencias.isEmpty()) {
			dir = referencias.get(0).getDescLarga();
			if (dir.contains("\\")) {
				dir = dir.replace("\\", System.getProperty("file.separator"));

			} else if (dir.contains("//")) {
				dir = dir.replace("//", System.getProperty("file.separator"));

			} else if (dir.contains("/")) {
				dir = dir.replace("/", System.getProperty("file.separator"));

			}

		}
		return dir;

	}
	public static String fileNameFormatter(String nombreArchivo) {
		String[] archivo = nombreArchivo.split("\\.");

		int ultimoPunto = nombreArchivo.lastIndexOf(".");

		String nombreOriginal = "";
		String nombreNuevo = "";
		String extension = "";

		if (ultimoPunto > -1) {
			nombreOriginal = nombreArchivo.substring(0, ultimoPunto);
			extension = nombreArchivo.substring(ultimoPunto);
		} else {
			nombreOriginal = nombreArchivo;
			extension = ".pdf";
		}
		String fechaNueva = "";

		for (int i = 0; i < nombreOriginal.length(); i++) {
			if (Pattern.matches("[a-zA-Z0-9_]",
					Character.toString(nombreOriginal.charAt(i))))
				nombreNuevo += Character.toString(nombreOriginal.charAt(i))
						.toLowerCase();
			else
				nombreNuevo += "_";
		}
		if (!nombreNuevo.endsWith("_"))
			nombreNuevo += "_";

		SimpleDateFormat sdfSoloHoraMin = new SimpleDateFormat("hh_mm");
		SimpleDateFormat sdfFechaCompleta = new SimpleDateFormat("dd_MM_yyyy");
		String fechaCompleta = sdfFechaCompleta.format(new Date());
		String horaSeg = sdfSoloHoraMin.format(new Date());
		String ret = nombreNuevo + "" + fechaCompleta + "_" + horaSeg
				+ extension;
		return ret;
	}
	@SuppressWarnings("unchecked")
	private static boolean existeDocChksum(String md5, String nombrePantalla) {

		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		try {
			emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
					"java:/siccaEntityManagerFactory");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		emsta = emf.createEntityManager();

		String sql;
		sql = "Select d from Documentos d where d.checksum like '" + md5
				+ "' and lower(d.nombrePantalla) like '"
				+ nombrePantalla.toLowerCase() + "' and  d.activo=true";

		List<Documentos> documentos = emsta.createQuery(sql).getResultList();

		if (!documentos.isEmpty())
			return true;

		return false;
	}
	
	private static String ponerBarra(String direccion) {
		if (direccion.contains("\\")) {
			direccion = direccion.replace("\\",
					System.getProperty("file.separator"));

		} else if (direccion.contains("//")) {
			direccion = direccion.replace("//",
					System.getProperty("file.separator"));

		} else if (direccion.contains("/")) {
			direccion = direccion.replace("/",
					System.getProperty("file.separator"));

		}
		return direccion;
	}
	
	public static String guardarArchivo(String name, File file, String tipoDoc,
			String urlFinal) throws IOException {

		String url = "";
		urlFinal = ponerBarraSimple(urlFinal);
		// urlFinal=System.getProperty("file.separator")+"vol01"+System.getProperty("file.separator")+"sicca"+urlFinal;

		String separador = System.getProperty("file.separator");

		if (!urlFinal.endsWith(separador))
			urlFinal = urlFinal + System.getProperty("file.separator");

		String nuevoFileName = urlFinal + name;

		File directorio = new File(urlFinal);// donde va ir
		try {
			if (!directorio.exists())
				if (!directorio.mkdirs())
					throw new IOException("No se pudo crear el directorio: "
							+ directorio.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();

		}

		File destino = new File(nuevoFileName);// donde va ir mas documento tipo

		url = copyFile(file, destino, true);

		return url;

	}
	private static String ponerBarraSimple(String direccion) {
		if (direccion.contains("\\")) {
			direccion = direccion.replace("\\",
					System.getProperty("file.separator"));

		}
		if (direccion.contains("//")) {
			direccion = direccion.replace("//",
					System.getProperty("file.separator"));

		}
		if (direccion.contains("/")) {
			direccion = direccion.replace("/",
					System.getProperty("file.separator"));

		}
		return direccion;
	}
	
	public static String copyFile(File source, File dest, boolean sobreEscribir)
			throws IOException {
		if (!source.exists())
			throw new IOException("No existe el archivo para copiar: "
					+ source.getAbsolutePath());

		if (!dest.exists()) {
			if (!dest.createNewFile())
				throw new IOException(
						"No se puede crear el destino de la copia: "
								+ dest.getAbsolutePath());
		} else {

			/*
			 * if (sobreEscribir && !delete(dest)) throw newIOException(
			 * "El destino de la copia ya existia, pero no se pudo borrar: " +
			 * dest.getAbsolutePath()); else if (!dest.createNewFile()) throw
			 * new IOException("No se puede crear el destino de la copia: " +
			 * dest.getAbsolutePath());
			 */

		}

		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			in.close();
			out.close();
		}
		// System.out.println("Se copio exitosamente: " + source + " -> " +
		// dest);
		return dest.toString();
	}
	
	@SuppressWarnings("unchecked")
	private Long guardarBasesYCondiciones(String fileName, int tamArchivo,String contentType, byte[] uploadedFile) {
		try {
			
			String sql = "select * from seleccion.datos_especificos where valor_alf = 'BASE_CONDICIONES'";
			
			 List <DatosEspecificos> list = em.createNativeQuery(sql,DatosEspecificos.class).getResultList();
			 
			 Long idTipoDoc = list.get(0).getIdDatosEspecificos();
			
			UploadItem fileItem = seleccionUtilFormController.crearUploadItem(fileName, tamArchivo, contentType, uploadedFile);
			Long idDocuGenerado;
			
			String nombreTabla = "CONCURSO_PUBLICACION";
			
			String direccionFisica = File.separator
					+ "CONCURSO_PUBLICACION" + File.separator +idConcurso + File.separator + concursoPuestoAgrNuevo.getIdConcursoPuestoAgr();
			idDocuGenerado = admDocAdjuntoFormController.guardarDocParaPublicacion(fileItem,direccionFisica, nombrePantalla, idTipoDoc,usuarioLogueado.getCodigoUsuario() ,nombreTabla,idConcurso,concursoPuestoAgrNuevo.getIdConcursoPuestoAgr());
			
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Long guardarAdjuntoPublicacion(String fileName, int tamArchivo,String contentType, byte[] uploadedFile) {
		try {
			
			String sql = "select * from seleccion.datos_especificos where valor_alf = 'DOC_PUBLICACION'";
			
			 List <DatosEspecificos> list = em.createNativeQuery(sql,DatosEspecificos.class).getResultList();
			 
			 Long idTipoDoc = list.get(0).getIdDatosEspecificos();
			
			UploadItem fileItem = seleccionUtilFormController.crearUploadItem(fileName, tamArchivo, contentType, uploadedFile);
			Long idDocuGenerado;
			
			String nombreTabla = "CONCURSO_PUBLICACION";
			
			String direccionFisica = File.separator
					+ "CONCURSO_PUBLICACION" + File.separator +idConcurso + File.separator + concursoPuestoAgrNuevo.getIdConcursoPuestoAgr();
			idDocuGenerado = admDocAdjuntoFormController.guardarDocParaPublicacion(fileItem,direccionFisica, nombrePantalla, idTipoDoc,usuarioLogueado.getCodigoUsuario() ,nombreTabla,idConcurso,concursoPuestoAgrNuevo.getIdConcursoPuestoAgr());
			
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void imprimirBasesCondiciones(Long idConcursoPuestoAgr) {
		
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,idConcursoPuestoAgr);
		
		
				
			
			//verificar el query ya que las base y condiciones se cargaran por cada grupo.. 
			// posiblemente hara falta agregar un campo en la base de datos en la tabla de documentos (id_concurso_puesto_agr)
			try{
			
			String sql  ="select doc.* from general.documentos doc "
					+ " join seleccion.datos_especificos datos on doc.id_datos_especificos_tipo_documento = datos.id_datos_especificos"
					+ " and  datos.valor_alf = 'BASE_CONDICIONES'"
					+ " where id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ " order by doc.id_documento desc";
					
			
			List <Documentos> docs = em.createNativeQuery(sql,Documentos.class).getResultList();
			
			Long id = docs.get(0).getIdDocumento();
			
			String usuario = "Invitado";
			
			if (usuarioLogueado != null )
				usuario = usuarioLogueado.getCodigoUsuario();
			
			AdmDocAdjuntoFormController.abrirDocumentoFromCU(id,usuario);
			
			}catch(Exception e){
				e.printStackTrace();
				
			}
						
		
		
	}
	
public void imprimirDocumentoPublicacion(Long idPublicacionPortal) {
		
		
			//verificar el query ya que las base y condiciones se cargaran por cada grupo.. 
			// posiblemente hara falta agregar un campo en la base de datos en la tabla de documentos (id_concurso_puesto_agr)
			try{
			
			String sql  ="select doc.* from general.documentos doc "
					+ " join seleccion.datos_especificos datos on doc.id_datos_especificos_tipo_documento = datos.id_datos_especificos"
					+ " and  datos.valor_alf = 'DOC_PUBLICACION'"
					+ " where id_tabla = " + idPublicacionPortal;
			
			List <Documentos> docs = em.createNativeQuery(sql,Documentos.class).getResultList();
			
			Long id = docs.get(0).getIdDocumento();
			
			String usuario = "Invitado";
			
			if (usuarioLogueado != null )
				usuario = usuarioLogueado.getCodigoUsuario();
			
			AdmDocAdjuntoFormController.abrirDocumentoFromCU(id,usuario);
			
			}catch(Exception e){
				e.printStackTrace();
				
			}
						
		}

public boolean tieneDocAdjunto(Long idPublicacionPortal){
	
	String sql  ="select doc.* from general.documentos doc "
			+ " join seleccion.datos_especificos datos on doc.id_datos_especificos_tipo_documento = datos.id_datos_especificos"
			+ " and  datos.valor_alf = 'DOC_PUBLICACION'"
			+ " where id_tabla = " + idPublicacionPortal;
	
	List <Documentos> docs = em.createNativeQuery(sql,Documentos.class).getResultList();
	
	return docs.isEmpty();
	
}
		
		
	public String obtenerCantidadPostulantes(Long idConcursoPuestoAgr){
		
		String sql = "select * from seleccion.postulacion where id_concurso_puesto_agr = "+idConcursoPuestoAgr;
		
		List<Postulacion> lista = em.createNativeQuery(sql,Postulacion.class).getResultList();
				
		return ""+lista.size();
	}
	
	public String obtenerTipoEvaluacionGrupo(Long idConcursoPuestoAgr){
		
		String sql = "select * from seleccion.datos_grupo_puesto where id_concurso_puesto_agr = "+idConcursoPuestoAgr;
		
		List<DatosGrupoPuesto> lista = em.createNativeQuery(sql,DatosGrupoPuesto.class).getResultList();
		
		if((lista.size() != 0) && lista.get(0).getMerito() && !lista.get(0).getTerna()){
			return "MÉRITO";
		}
		else if((lista.size() != 0) && !lista.get(0).getMerito() && lista.get(0).getTerna()){
			return "TERNA";
		}
		else{
			return "SIN DEFINIR";
		}
	}
	
	public int obtenerPorcentajeMinimoGrupo(Long idConcursoPuestoAgr){
		
		String sql = "select * from seleccion.datos_grupo_puesto where id_concurso_puesto_agr = "+idConcursoPuestoAgr;
		
		List<DatosGrupoPuesto> lista = em.createNativeQuery(sql,DatosGrupoPuesto.class).getResultList();
		
		if(lista.size() != 0){
			return lista.get(0).getPorcMinimo();
		}
		else{
			return 0;
		}
	}
	
	public String obtenerEstadoConcurso(Integer idEstado){
		
		String sql = "select * from seleccion.referencias where valor_num = "+idEstado+"and dominio = 'ESTADOS_GRUPO'";
		List<Referencias> lista = em.createNativeQuery(sql,Referencias.class).getResultList();
		
		return lista.get(0).getDescLarga();
	}
	
	
	
	/**
	 * Método que busca datos asociados al usuario logueado
	 */
	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {
			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			if (configuracionUoCab.getOrden() != null)
				ordenUnidOrg = configuracionUoCab.getOrden();
			if (configuracionUoCab.getEntidad() != null)
				entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	/**
	 * Método que verifica el tipo de concurso de tal forma a habilitar o
	 * deshabilitar ciertos radio buttons
	 */
	public void esMeritoOinterInstitucional() {
		mostrarDesprecarizado = false;
		DatosEspecificos datMerito = seleccionUtilFormController
				.traerTipoConcursoConcurso("CONCURSO DE MERITOS");
		DatosEspecificos datInter = seleccionUtilFormController
				.traerTipoConcursoConcurso("CONCURSO INTERNO DE OPOSICION INTER INSTITUCIONAL");
		DatosEspecificos datInterno = seleccionUtilFormController
				.traerTipoConcursoConcurso("CONCURSO INTERNO DE OPOSICION INSTITUCIONAL");
		DatosEspecificos internoInstitucionalDespre = seleccionUtilFormController
				.traerTipoConcursoConcurso("CONCURSO INTERNO INSTITUCIONAL PARA LA DESPRECARIZACION LABORAL");
		
		if (idTipoConcurso.equals(datMerito.getIdDatosEspecificos()))
			mostrarAdReferendum = false;
		else if (idTipoConcurso.equals(datInter.getIdDatosEspecificos()))
			mostrarAdReferendum = false;
		else if (idTipoConcurso.equals(datInterno.getIdDatosEspecificos()))
			mostrarDesprecarizado = false;//no se utilizara el campo
		else if (idTipoConcurso.equals(internoInstitucionalDespre.getIdDatosEspecificos())){
			mostrarDesprecarizado = false;//no se utilizara el campo
			mostrarAdReferendum = true;
		}
		else
			mostrarAdReferendum = true;
	}

	/**
	 * Método que setea todos los datos necesarios para luego guardarlos.
	 * 
	 * @return
	 */
	public String save() {
		try {
			if (!check()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"LLene los campos requeridos");
				return null;
			}

			concurso.setDatosEspecificosTipoConc(em.find(
					DatosEspecificos.class, idTipoConcurso));
			concurso.setConfiguracionUoCab(configuracionUoCab);
			concurso.setEstado(1);
			concurso.setFechaCreacion(new Date());
			concurso.setActivo(true);
			if (mostrarAdReferendum) {
				if (radioButton != null) {
					if (radioButton.equals("SI"))
						concurso.setAdReferendum(true);
					else
						concurso.setAdReferendum(false);
				}
			}
			if (radioPcd.equals("SI"))
				concurso.setPcd(true);
			else
				concurso.setPcd(false);
			if (mostrarDesprecarizado) {
				if (radioDesprecarizado != null) {
					if (radioDesprecarizado.equals("SI"))
						concurso.setDesprecarizacion(true);
					else
						concurso.setDesprecarizacion(false);
				}
			}
			concurso.setPromocion(false);
			concurso.setFechaAlta(new Date());
			concurso.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(concurso);
			em.flush();
			concursoHome.setInstance(concurso);
			idConcurso = concursoHome.getInstance().getIdConcurso();

			operacion = "persisted";
			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;
			String separador = File.separator;
			ubicacionFisica = separador+"SICCA"+separador + anho + separador + "OEE"+separador
					+ configuracionUoCab.getIdConfiguracionUo() + separador
					+ idTipoConcurso + separador
					+ concursoHome.getInstance().getIdConcurso();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
		return null;
	}
	
	
	

	/**
	 * Método que setea todos los datos necesarios para luego guardarlos.
	 * 
	 * @return
	 */
	public String savePublicacion() {
		try {
			if (!checkPublicacion()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"LLene los campos requeridos");
				return null;
			}

			concurso.setDatosEspecificosTipoConc(em.find(DatosEspecificos.class, idTipoConcurso));
			
			NivelEntidadOeeUtil nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component
					.getInstance(NivelEntidadOeeUtil.class, true);
			
			Long id = nivelEntidadOeeUtil.getIdConfigCab();
			
			concurso.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, id));
			concurso.setEstado(1000); // Para el Estado Publicacion
			concurso.setFechaCreacion(new Date());
			concurso.setActivo(true);
			concurso.setAdReferendum(false);
			if (mostrarAdReferendum) {
				if (radioButton != null) {
					if (radioButton.equals("SI"))
						concurso.setAdReferendum(true);
					else
						concurso.setAdReferendum(false);
				}
			}	
			if (radioPcd.equals("SI"))
				concurso.setPcd(true);
			else
				concurso.setPcd(false);
			
			
			if (mostrarDesprecarizado) {
				if (radioDesprecarizado.equals("SI"))
					concurso.setDesprecarizacion(true);
				else
					concurso.setDesprecarizacion(false);
			}
			

			concurso.setFechaAlta(new Date());
			concurso.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(concurso);
			em.flush();
			concursoHome.setInstance(concurso);
			idConcurso = concursoHome.getInstance().getIdConcurso();
			

			
			operacion = "persisted";
			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;
			String separador = File.separator;
			ubicacionFisica = separador+"SICCA"+separador + anho + separador + "OEE"+separador
					+ configuracionUoCab.getIdConfiguracionUo() + separador
					+ idTipoConcurso + separador
					+ concursoHome.getInstance().getIdConcurso();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
			
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
		return null;
	}
	/**
	 * Método que setea todos los datos necesarios para luego actualizarlos.
	 * 
	 * @return
	 */
	public String update() {
		try {
			if (!check()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"LLene los campos requeridos");
				return null;
			}
			
			AdminConcursosListFormController adminConcursosListFormController = (AdminConcursosListFormController) Component
					.getInstance(AdminConcursosListFormController.class, true);
			adminConcursosListFormController.calcEstado();
			if (concurso.getEstado() != null
					&& concurso.getEstado().intValue() == adminConcursosListFormController
							.getValorNumEstadoConcurso().intValue()) {
				return null;
			}
		
			concurso.setDatosEspecificosTipoConc(em.find(
					DatosEspecificos.class, idTipoConcurso));
			concurso.setConfiguracionUoCab(configuracionUoCab);
			concurso.setActivo(true);
			if (mostrarAdReferendum) {
				if (radioButton != null) {
					if (radioButton.equals("SI"))
						concurso.setAdReferendum(true);
					else
						concurso.setAdReferendum(false);
				}
			}
			if (radioPcd.equals("SI"))
				concurso.setPcd(true);
			else
				concurso.setPcd(false);
			if (mostrarDesprecarizado) {
				if (radioDesprecarizado != null) {
					if (radioDesprecarizado.equals("SI"))
						concurso.setDesprecarizacion(true);
					else
						concurso.setDesprecarizacion(false);
				}
			}
			concurso.setFechaMod(new Date());
			concurso.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(concurso);

			concursoHome.setInstance(concurso);
			String result = "updated";
			operacion = result;
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		}

		return null;
	}
	
	/**
	 * Método que setea todos los datos necesarios para luego actualizarlos.
	 * 
	 * @return
	 */
	public String updatePublicacion() {
		try {
			if (!checkPublicacion()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"LLene los campos requeridos");
				return null;
			}
			
			
		
			concurso.setDatosEspecificosTipoConc(em.find(
					DatosEspecificos.class, idTipoConcurso));
			NivelEntidadOeeUtil nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component
					.getInstance(NivelEntidadOeeUtil.class, true);
			
			Long id = nivelEntidadOeeUtil.getIdConfigCab();
			
			concurso.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, id));
			concurso.setActivo(true);
			if (mostrarAdReferendum) {
				if (radioButton != null) {
					if (radioButton.equals("SI"))
						concurso.setAdReferendum(true);
					else
						concurso.setAdReferendum(false);
				}
			}
			
			if (radioPcd.equals("SI"))
				concurso.setPcd(true);
			else
				concurso.setPcd(false);
			
			if (radioDesprecarizado.equals("SI"))
				concurso.setDesprecarizacion(true);
			else
				concurso.setDesprecarizacion(false);
			
			concurso.setFechaMod(new Date());
			concurso.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(concurso);

			concursoHome.setInstance(concurso);
			String result = "updated";
			operacion = result;
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		}

		return null;
	}

	/**
	 * Método que elimina el concurso actual.
	 * 
	 * @return
	 */
	public String delete() {
		try {
			concurso.setActivo(false);
			concursoHome.setInstance(concurso);
			String result = concursoHome.update();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return result;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}

		return null;
	}

	/**
	 * Método que chequea que todos los datos obligatorios hayan sido cargados
	 * @return
	 */
	private Boolean check() {
		if (idTipoConcurso == null || radioPcd == null)
			return false;
		if (mostrarAdReferendum && radioButton == null)
			return false;
		if (seleccionUtilFormController != null) {
			seleccionUtilFormController = (SeleccionUtilFormController) Component
					.getInstance(SeleccionUtilFormController.class, true);

		}

		if (concurso.getNombre().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_descripcion_invalida"));
			return false;
		}

		return true;
	}
	/**
	 * Método que chequea que todos los datos obligatorios hayan sido cargados en el insert de un nuevo concurso para publicacion
	 * @return
	 */
	private Boolean checkPublicacion() {
		if (idTipoConcurso == null)
			return false;
		
		if (seleccionUtilFormController != null) {
			seleccionUtilFormController = (SeleccionUtilFormController) Component
					.getInstance(SeleccionUtilFormController.class, true);

		}

		if (concurso.getNombre().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_descripcion_invalida"));
			return false;
		}

		return true;
	}

	public Boolean habilidarCamposPorTipo() {
		if (idTipoConcurso != null)
			if (idTipoConcurso.intValue() == datEspe.getIdDatosEspecificos()
					.intValue()) {
				return true;
			}
		return false;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public Long getIdTipoConcurso() {
		return idTipoConcurso;
	}

	public void setIdTipoConcurso(Long idTipoConcurso) {
		this.idTipoConcurso = idTipoConcurso;
	}

	public String getRadioButton() {
		return radioButton;
	}

	public void setRadioButton(String radioButton) {
		this.radioButton = radioButton;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public String getRadioPcd() {
		return radioPcd;
	}

	public void setRadioPcd(String radioPcd) {
		this.radioPcd = radioPcd;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Boolean getMostrarAdReferendum() {
		return mostrarAdReferendum;
	}

	public void setMostrarAdReferendum(Boolean mostrarAdReferendum) {
		this.mostrarAdReferendum = mostrarAdReferendum;
	}

	public Boolean getMostrarDesprecarizado() {
		return mostrarDesprecarizado;
	}

	public void setMostrarDesprecarizado(Boolean mostrarDesprecarizado) {
		this.mostrarDesprecarizado = mostrarDesprecarizado;
	}

	public String getRadioDesprecarizado() {
		return radioDesprecarizado;
	}

	public void setRadioDesprecarizado(String radioDesprecarizado) {
		this.radioDesprecarizado = radioDesprecarizado;
	}
	
	public List<ConcursoPuestoAgr> getConcursoPuestoAgrList() {
		return concursoPuestoAgrList;
	}

	public void setConcursoPuestoAgrList(
			List<ConcursoPuestoAgr> concursoPuestoAgrList) {
		this.concursoPuestoAgrList = concursoPuestoAgrList;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgrNuevo() {
		return concursoPuestoAgrNuevo;
	}

	public void setConcursoPuestoAgrNuevo(ConcursoPuestoAgr concursoPuestoAgrNuevo) {
		this.concursoPuestoAgrNuevo = concursoPuestoAgrNuevo;
	}
	
	public FechasGrupoPuesto getFechasGrupoPuesto() {
		return fechasGrupoPuesto;
	}

	public void setFechasGrupoPuesto(FechasGrupoPuesto fechasGrupoPuesto) {
		this.fechasGrupoPuesto = fechasGrupoPuesto;
	}
	
	public Date getFechaPublicacionDesde() {
		return fechaPublicacionDesde;
	}

	public void setFechaPublicacionDesde(Date fechaPublicacionDesde) {
		this.fechaPublicacionDesde = fechaPublicacionDesde;
	}

	public Date getFechaPublicacionHasta() {
		return fechaPublicacionHasta;
	}

	public void setFechaPublicacionHasta(Date fechaPublicacionHasta) {
		this.fechaPublicacionHasta = fechaPublicacionHasta;
	}

	public Date getFechaRecepcionDesde() {
		return fechaRecepcionDesde;
	}

	public void setFechaRecepcionDesde(Date fechaRecepcionDesde) {
		this.fechaRecepcionDesde = fechaRecepcionDesde;
	}

	public Date getFechaRecepcionHasta() {
		return fechaRecepcionHasta;
	}

	public void setFechaRecepcionHasta(Date fechaRecepcionHasta) {
		this.fechaRecepcionHasta = fechaRecepcionHasta;
	}

	public String getHoraPublicacionDesde() {
		return horaPublicacionDesde;
	}

	public void setHoraPublicacionDesde(String horaPublicacionDesde) {
		this.horaPublicacionDesde = horaPublicacionDesde;
	}

	public String getHoraPublicacionHasta() {
		return horaPublicacionHasta;
	}

	public void setHoraPublicacionHasta(String horaPublicacionHasta) {
		this.horaPublicacionHasta = horaPublicacionHasta;
	}

	public String getHoraRecepcionDesde() {
		return horaRecepcionDesde;
	}

	public void setHoraRecepcionDesde(String horaRecepcionDesde) {
		this.horaRecepcionDesde = horaRecepcionDesde;
	}

	public String getHoraRecepcionHasta() {
		return horaRecepcionHasta;
	}

	public void setHoraRecepcionHasta(String horaRecepcionHasta) {
		this.horaRecepcionHasta = horaRecepcionHasta;
	}
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public byte[] getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(byte[] uploadedFile) {
		this.uploadedFile = uploadedFile;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getActualFileName() {
		return actualFileName;
	}

	public void setActualFileName(String actualFileName) {
		this.actualFileName = actualFileName;
	}

	public UploadItem getItem() {
		return item;
	}

	public void setItem(UploadItem item) {
		this.item = item;
	}
	
	public Integer getTamArchivo() {
		return tamArchivo;
	}

	public void setTamArchivo(Integer tamArchivo) {
		this.tamArchivo = tamArchivo;
	}
	public String getPublicTitulo() {
		return publicTitulo;
	}

	public void setPublicTitulo(String publicTitulo) {
		this.publicTitulo = publicTitulo;
	}

	public String getPublicDetalle() {
		return publicDetalle;
	}

	public void setPublicDetalle(String publicDetalle) {
		this.publicDetalle = publicDetalle;
	}

	public String getPublicObservacion() {
		return publicObservacion;
	}

	public void setPublicObservacion(String publicObservacion) {
		this.publicObservacion = publicObservacion;
	}
	
	public List<PublicacionPortal> getPublicacionPortalList() {
		return publicacionPortalList;
	}

	public void setPublicacionPortalList(
			List<PublicacionPortal> publicacionPortalList) {
		this.publicacionPortalList = publicacionPortalList;
	}
	public PublicacionPortal getPublicacionPortalNuevo() {
		return publicacionPortalNuevo;
	}

	public void setPublicacionPortalNuevo(PublicacionPortal publicacionPortalNuevo) {
		this.publicacionPortalNuevo = publicacionPortalNuevo;
	}
	public String getTituloDocumentoAdjunto() {
		return tituloDocumentoAdjunto;
	}

	public void setTituloDocumentoAdjunto(String tituloDocumentoAdjunto) {
		this.tituloDocumentoAdjunto = tituloDocumentoAdjunto;
	}
	
	public Long getIdTabla() {
		return idTabla;
	}

	public void setIdTabla(Long idTabla) {
		this.idTabla = idTabla;
	}
	public String getPublicDetalleSinNegrita() {
		return publicDetalleSinNegrita;
	}

	public void setPublicDetalleSinNegrita(String publicDetalleSinNegrita) {
		this.publicDetalleSinNegrita = publicDetalleSinNegrita;
	}
	public String getPublicObservacionSinNegrita() {
		return publicObservacionSinNegrita;
	}

	public void setPublicObservacionSinNegrita(String publicObservacionSinNegrita) {
		this.publicObservacionSinNegrita = publicObservacionSinNegrita;
	}
	
	public Date getFechaConvocatoria() {
		return fechaConvocatoria;
	}

	public void setFechaConvocatoria(Date fechaConvocatoria) {
		this.fechaConvocatoria = fechaConvocatoria;
	}

	public String getHoraDesdeConvocatoria() {
		return horaDesdeConvocatoria;
	}

	public void setHoraDesdeConvocatoria(String horaDesdeConvocatoria) {
		this.horaDesdeConvocatoria = horaDesdeConvocatoria;
	}

	public String getHoraHastaConvocatoria() {
		return horaHastaConvocatoria;
	}

	public void setHoraHastaConvocatoria(String horaHastaConvocatoria) {
		this.horaHastaConvocatoria = horaHastaConvocatoria;
	}

	public String getLugarConvocatoria() {
		return lugarConvocatoria;
	}

	public void setLugarConvocatoria(String lugarConvocatoria) {
		this.lugarConvocatoria = lugarConvocatoria;
	}

	public String getDireccionConvocatoria() {
		return direccionConvocatoria;
	}

	public void setDireccionConvocatoria(String direccionConvocatoria) {
		this.direccionConvocatoria = direccionConvocatoria;
	}

	public String getObservacionConvocatoria() {
		return observacionConvocatoria;
	}

	public void setObservacionConvocatoria(String observacionConvocatoria) {
		this.observacionConvocatoria = observacionConvocatoria;
	}
	
	public String getDepartamentoConvocatoria() {
		return departamentoConvocatoria;
	}

	public void setDepartamentoConvocatoria(String departamentoConvocatoria) {
		this.departamentoConvocatoria = departamentoConvocatoria;
	}

	public String getCuidadConvocatoria() {
		return cuidadConvocatoria;
	}

	public void setCuidadConvocatoria(String cuidadConvocatoria) {
		this.cuidadConvocatoria = cuidadConvocatoria;
	}
	
	public String getTituloConvocatoria() {
		return tituloConvocatoria;
	}

	public void setTituloConvocatoria(String tituloConvocatoria) {
		this.tituloConvocatoria = tituloConvocatoria;
	}
	public Integer getMinimoEvaluacion() {
		return minimoEvaluacion;
	}

	public void setMinimoEvaluacion(Integer minimoEvaluacion) {
		this.minimoEvaluacion = minimoEvaluacion;
	}

	public String getSeleccionAdjudicados() {
		return seleccionAdjudicados;
	}

	public void setSeleccionAdjudicados(String seleccionAdjudicados) {
		this.seleccionAdjudicados = seleccionAdjudicados;
	}
	public List<SelectItem> seleccionAdjudicadosValueItems() {
		List<SelectItem> listaItem = new ArrayList<SelectItem>();
		listaItem.add(new SelectItem(CODIGO_TERNA, "Por Terna o Dupla"));
		listaItem.add(new SelectItem(CODIGO_PUNTAJE, "Por Puntaje"));
		return listaItem;
	}




}
