package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Naming;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PostulacionAdjuntos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.SelFuncionDatosEsp;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Funcion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoHome;
import py.com.excelsis.sicca.session.DocumentosList;
import py.com.excelsis.sicca.session.util.CheckSum;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admDocAdjuntoFormController")
public class AdmDocAdjuntoFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(value = "entityManager")
	EntityManager em;

	@PersistenceUnit
	EntityManagerFactory emf;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(required = false, create = true)
	DocumentosList documentosList;
	
	//*********************************************
	@In(create = true)//agregado; Werner.
	ConcursoHome concursoHome;
	//*********************************************
	
	private List<Documentos> documentoDTOList = new ArrayList<Documentos>();
	private Documentos documentos;
	UploadItem item;
	private Integer tamArchivo;
	private String tipoDoc;
	private String nomfinal;
	private Long idFuncionDatosEsp;

	private String from;
	private String fromFrom;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;

	private Long idConcurso;
	private Long idConcursoPuestoAgr;
	private Concurso concurso;
	private ConcursoPuestoAgr concursoPuestoAgr;
	 
	private List<ConcursoPuestoAgr> listaGruposParaFirmar;
	private List<Boolean> seleccionados;
	private Boolean isEdit;
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
	private String plantaCargoDetFrom;
	private Long uoCab;

	/* Aqui se especifican las distintas clases de pantallas en las que se pueden anexar documentos. */
	public static final String[] VALORES_OCULTAR_GRUPO = 
		{	"concurso_edit",
			"adjuntarResolucion", "cargarResultEvalMetodo571",
			"cargarResultEvalMetodo571Area", "cargarResultEvalPDEC572FC",
			"cargarResultEvalPerce608FC", "adjuntarReglamento",
			"cargarResultEvalMetodo571Gestion", "lista_larga_list",
			"adjuntarObservacionSFP", "evaluar_doc_postulante_list",
			"edit_puestos_trabajo", "adjuntarInformeFinal",
			"enviarHomologacionPerfil_edit", "evaluacion_doc_adjudicados",
			"publicacion_seleccionados", "modificacion_datos_concurso_list", "realizarEntrevistaFinal",
			"comision_edit", "desvinculacion_edit", "admInhabilitadosSFP", "admJubiladosSFP"};
	
	public static final Set<String> OCULTAR_GRUPO = new HashSet<String>(Arrays.asList(VALORES_OCULTAR_GRUPO));
	/****
	 * 
	 * */
	public void init() {
		try {
			//*****************************************************************************************
			if (nombrePantalla.equals("concurso_edit")) {//agregado para hacer la consulta sql; Werner
				concurso = concursoHome.getInstance();
				idConcurso = concurso.getIdConcurso();
			}
			//*****************************************************************************************
			documentos = new Documentos();
			documentoDTOList = new ArrayList<Documentos>();
			nomfinal = "";
			dirFisica = direccionFisica;
			if(idConcursoPuestoAgr != null) {
				concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
				concurso = concursoPuestoAgr.getConcurso();
				idConcurso = concurso.getIdConcurso();
				
			}
			if (!ocultandoPanelGrupo()) {
				ListarConcursoPorEstado();
			}
			//*****************************************************************************************
			
			listarExistentesDocs();
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void ListarConcursoPorEstado() {
		String estado = "";
		if( this.nombrePantalla.equals("FirmaResolAdjudic"))
			estado = "25";
		if( this.nombrePantalla.equals("administrarDecreto_edit"))
			estado = "29";
		if( this.nombrePantalla.equals("FirmaResolHomolog"))
			estado = "5";
		String query = "select * from seleccion.concurso_puesto_agr cpa "
				+ "where cpa.id_concurso = " + idConcurso
				+ " and cpa.activo is true and cpa.estado = "
				+ estado;
		
		listaGruposParaFirmar = new ArrayList<ConcursoPuestoAgr>();
		listaGruposParaFirmar = em.createNativeQuery(query,
				ConcursoPuestoAgr.class).getResultList();

		seleccionados = new ArrayList<Boolean>();
		for (int i = 0; i < listaGruposParaFirmar.size(); i++){
			boolean seleccionar = nombrePantalla.equals("FirmarResolAdjudic");
			listaGruposParaFirmar.get(i).setSeleccionado(seleccionar);
			seleccionados.add(i, seleccionar);
		}
	}

	public void changeSeleccionado(int index) {
		boolean valorAnterior  = seleccionados.get(index);
		this.listaGruposParaFirmar.get(index).setSeleccionado(!valorAnterior);
		seleccionados.set(index, !valorAnterior);
	}

	public boolean haySeleccionado() {
		for( boolean seleccion : seleccionados )
			if(seleccion)
				return true;
		return false;
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

	public List<SelectItem> getDatosEspecificosFuncionSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (SelFuncionDatosEsp o : getDatosEspecificoss())
			si.add(new SelectItem(o.getIdFuncionDatosEsp(), ""
					+ o.getDatosEspecificos().getDescripcion()));
		return si;
	}

	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
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
	
	public boolean porGrupo() {
		if (!nombrePantalla.equals("concurso_edit") && !nombrePantalla.equals("adjuntarResolucion") 
				&& !nombrePantalla.equals("cargarResultEvalMetodo571") && !nombrePantalla.equals("cargarResultEvalMetodo571Area") 
				&& !nombrePantalla.equals("cargarResultEvalPDEC572FC") && !nombrePantalla.equals("cargarResultEvalPerce608FC") 
				&& !nombrePantalla.equals("adjuntarReglamento") && !nombrePantalla.equals("cargarResultEvalMetodo571Gestion")
				&& !nombrePantalla.equals("evaluar_doc_postulante_list") && !nombrePantalla.equals("edit_puestos_trabajo") 
				&& !nombrePantalla.equals("lista_larga_list") && !nombrePantalla.equals("adjuntarObservacionSFP")) {
				return true;
			}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void addDocs() throws IOException {
//		if (!nombrePantalla.equals("FirmaResolHomolog") && !haySeleccionado()){
//			statusMessages.add(Severity.ERROR,
//					"Debe seleccionar al menos un grupo.");
//			return;
//		}
		//*****************************************************************************************
		
		if (!ocultandoPanelGrupo()){
			if(!haySeleccionado()){
				statusMessages.add(Severity.ERROR,
						"Debe seleccionar al menos un grupo.");
				return;}
		}
		//*****************************************************************************************	
		if (uploadedFile != null) {
			if (validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return;
			}
		} else
			item = null;

		if (item == null || idFuncionDatosEsp == null
				|| documentos.getNombreLogDoc() == null
				|| documentos.getNombreLogDoc().isEmpty()) {
			statusMessages
					.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return;
		}
		if (documentos.getNroDocumento() != null
				&& documentos.getNroDocumento().intValue() <= 0) {
			statusMessages.add(Severity.ERROR,
					"El numero de Documento debe ser mayor a cero");
			return;

		}
		if (documentos.getAnhoDocumento() != null) {

			if (!General.validarAnio(Long.parseLong(documentos
					.getAnhoDocumento().toString()))) {
				statusMessages.add(Severity.ERROR, "Año invalido");
				return;
			}

		}
		if ((documentos.getNroDocumento() == null || documentos.getAnhoDocumento() == null) 
				&& (nombrePantalla.contentEquals("adjuntarResolucion") && nombreTabla.contentEquals("EvaluacionDesempeno"))) {//agregado; Werner.
			statusMessages.add(Severity.ERROR,
					"El número y el año del Documento, no deben ser nulos.");
			return;
		}

		if (superaTipoDocs()) {
			statusMessages
					.add(Severity.ERROR,
							"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
			return;
		}

		SelFuncionDatosEsp f = new SelFuncionDatosEsp();
		f = em.find(SelFuncionDatosEsp.class, idFuncionDatosEsp);

		if (documentos.getAnhoDocumento() != null
				&& documentos.getNroDocumento() != null) {
			List<Documentos> docs = em.createQuery(
					"Select d from Documentos d "
							+ " where d.anhoDocumento="
							+ documentos.getAnhoDocumento()
							+ " and d.configuracionUoCab.idConfiguracionUo="
							+ usuarioLogueado.getConfiguracionUoCab()
									.getIdConfiguracionUo()
							+ " and d.nroDocumento="
							+ documentos.getNroDocumento()
							+ " and d.datosEspecificos.idDatosEspecificos ="
							+ f.getDatosEspecificos().getIdDatosEspecificos()
							+ "" + " and d.nombrePantalla='" + nombrePantalla
							+ "' and d.activo=true").getResultList();
			if (!docs.isEmpty() && !nombrePantalla.contentEquals("adjuntarResolucion")) {
				statusMessages
						.add(Severity.ERROR,
								"Ya existe el Nro./Año para el Tipo de Documento seleccionado. Verifique");
				return;
			}
		}

		// String[] t = tipoDoc.split("\\/");
		// obtener la extension
		String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
		List<TipoArchivo> ta = em.createQuery(
				"select t from TipoArchivo t "
						+ " where lower(t.extension) like '"
						+ extension.toLowerCase() + "'").getResultList();
		if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
			if (tamArchivo.intValue() > ta.get(0).getUnidMedidaByte()
					.intValue()) {
				statusMessages.add(Severity.ERROR,
						"El archivo supera el tamaño máximo permitido. Límite "
								+ ta.get(0).getTamanho()
								+ ta.get(0).getUnidMedida() + ", verifique");
				return;
			}
		}
		nomfinal = "";
		nomfinal = item.getFileName();
		nomfinal = fileNameFormatter(nomfinal);

		CheckSum s = new CheckSum();
		s.comprobar();
		String md5Valor = CheckSum.MD5Checksum(item.getFile());

		if (existeDocChksum(md5Valor, nombrePantalla)) {
			statusMessages.add(Severity.ERROR,
					"Ya existe el mismo archivo. Verifique");
			return;
		}
		em.clear();
		documentos.setChecksum(md5Valor);
		documentos.setActivo(true);
		documentos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		documentos.setFechaAlta(new Date());
		documentos.setMimetype(tipoDoc);
		documentos.setTamanhoDoc(tamArchivo.toString());
		documentos.setIdTabla(idCU);
		documentos.setNombreTabla(nombreTabla);
		if (idConcurso != null) {
			// String
			// q1="select * from seleccion.concurso where id_concurso = "+idConcurso;
			String q2 = "select concurso from Concurso concurso where concurso.idConcurso = :idConcurso";
			List<Concurso> concursoList = em.createQuery(q2)
					.setParameter("idConcurso", idConcurso).getResultList();

			documentos.setConcurso(concursoList.get(0));
		}

		direccionFisica = ponerBarra(direccionFisica);
		dirFisica = ponerBarra(dirFisica);

		direccionFisica = PRE + dirFisica;
		documentos.setUbicacionFisica(direccionFisica);
		documentos.setNombrePantalla(nombrePantalla);
		documentos.setNombreFisicoDoc(nomfinal);
		documentos.setDatosEspecificos(em.find(DatosEspecificos.class, f
				.getDatosEspecificos().getIdDatosEspecificos()));
		
		if (nombrePantalla.contentEquals("adjuntarResolucion")) 
			documentos.setConfiguracionUoCab(capturandoOee());
		else 
			documentos.setConfiguracionUoCab(usuarioLogueado.getConfiguracionUoCab());
		
		em.persist(documentos);
		em.flush();

		guardarArchivo(nomfinal, item.getFile(), extension, direccionFisica);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
/*		for( ConcursoPuestoAgr grupo : this.listaGruposParaFirmar ){
			if(grupo.getSeleccionado()!=null && grupo.getSeleccionado()){
				if(this.nombrePantalla.equals("FirmaResolAdjudic")){;
				}
			}
				
		}*/
		em.clear();
		Resolucion resolucion = new Resolucion();
		resolucion.setDocumentos(documentos);
		resolucion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		resolucion.setFechaAlta(new Date());
		//monton de campos NOT NULL
		resolucion.setCargo("");
		resolucion.setTitulo(this.nombreTabla);
		resolucion.setVisto("");
		resolucion.setConsiderando("");
		resolucion.setFirma("");
		resolucion.setInstitucion("");
		resolucion.setPorTanto("");
		resolucion.setResuelve("");
		em.persist(resolucion);
		em.flush();
//		for( ConcursoPuestoAgr cpagr : listaGruposParaFirmar )
//			if(cpagr.getSeleccionado()){
//				if(nombrePantalla.equalsIgnoreCase("FirmaResolAdjudic"))
//					cpagr.setResolucionAdjudicacion(resolucion);
//				if(nombrePantalla.equalsIgnoreCase("administrarDecreto_edit"))
//					cpagr.setDecreto(resolucion);
//				if(nombrePantalla.equalsIgnoreCase("FirmaResolHomolog"))
//					cpagr.setResolucionHomologacion(resolucion);
//				em.clear();
//				em.merge(cpagr);
//				em.flush();
//			}
		//**********************************************************************************
		if (!ocultandoPanelGrupo()) {
			for( ConcursoPuestoAgr cpagr : listaGruposParaFirmar )
				if(cpagr.getSeleccionado()){
					if(nombrePantalla.equalsIgnoreCase("FirmaResolAdjudic"))
						cpagr.setResolucionAdjudicacion(resolucion);
					if(nombrePantalla.equalsIgnoreCase("administrarDecreto_edit"))
						cpagr.setDecreto(resolucion);
					if(nombrePantalla.equalsIgnoreCase("FirmaResolHomolog"))
						cpagr.setResolucionHomologacion(resolucion);
					em.clear();
					em.merge(cpagr);
					em.flush();
				}
		}
		//**********************************************************************************
		em.clear();
//		documentos.setIdTabla(resolucion.getIdResolucion());
		//agregado para el módulo Eval. Desempeño; Werner.
		if (!nombrePantalla.equals("adjuntarResolucion") && !nombrePantalla.equals("cargarResultEvalMetodo571") 
				&& !nombrePantalla.equals("cargarResultEvalMetodo571Area") && !nombrePantalla.equals("cargarResultEvalPDEC572FC") 
				&& !nombrePantalla.equals("cargarResultEvalPerce608FC") && !nombrePantalla.equals("adjuntarReglamento") 
				&& !nombrePantalla.equals("cargarResultEvalMetodo571Gestion") && !nombrePantalla.equals("edit_puestos_trabajo")
				&& !nombrePantalla.equals("adjuntarObservacionSFP") && !nombrePantalla.equals("lista_larga_list")
				&& !nombrePantalla.equals("adjuntarInformeFinal") && !nombrePantalla.equals("enviarHomologacionPerfil_edit")
				&& !nombrePantalla.equals("publicacion_seleccionados") && !nombrePantalla.equals("evaluacion_doc_adjudicados")
				&& !nombrePantalla.equals("comision_edit") && !nombrePantalla.equals("desvinculacion_edit")
				&& !nombrePantalla.equals("admInhabilitadosSFP") && !nombrePantalla.equals("admJubiladosSFP")) {
			documentos.setIdTabla(resolucion.getIdResolucion());
		}
		em.merge(documentos);
		em.flush();
		listarExistentesDocs();
		documentos = new Documentos();
		nomfinal = "";
		idFuncionDatosEsp = null;
		item = null;
		uploadedFile = null;
		contentType = null;

	}

	/**
	 * @param itemCU
	 *            :archivo adjunto,
	 */
	public Long guardarDoc(UploadItem itemCU, String direccionfisicaCU,
			String nPantalla, Long idDatosEspecifico, String nombreTabla,
			Long idDocumento) {
		try {
			Long idDoc = null;
			if (!validarContentType(itemCU.getContentType())) {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return null;
			}
			if (!nPantalla.endsWith("_copia")) {
				if (!checkTamanho(itemCU.getFileName(), itemCU.getFileSize()))
					return null;

			}

			if (idDocumento != null) {
				CheckSum s = new CheckSum();
				s.comprobar();
				String md5Valor = CheckSum.MD5Checksum(itemCU.getFile());
				// si es el mismo archivo adjunto PARA EL MNISMO DOC. NO SE HACE
				// NADA

				if (existeDocChksumEdit(md5Valor, nPantalla, idDocumento))
					return idDocumento;

				Documentos docAnterior = em.find(Documentos.class, idDocumento);
				/**
				 * se inactiva el otro
				 */
				docAnterior.setActivo(false);
				docAnterior.setUsuMod(usuarioLogueado.getCodigoUsuario());
				docAnterior.setFechaMod(new Date());
				em.merge(docAnterior);
				em.flush();
			}
			idDoc = guardarDirecto(itemCU, direccionfisicaCU, nPantalla,
					idDatosEspecifico, usuarioLogueado.getIdUsuario(),
					nombreTabla);
			if (idDoc != null) {

				if (idDoc.longValue() == -9) {
					statusMessages
							.add(Severity.ERROR,
									"Ingrese los datos obligatorios antes de adjuntar el Documento");
					return null;
				}
				if (idDoc.longValue() == -8) {
					statusMessages
							.add(Severity.ERROR,
									"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return null;
				}
				if (idDoc.longValue() == -7) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return null;
				}
				if (idDoc.longValue() == -6) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar ya existe. Seleccione otro");
					return null;
				}

			} else {
				statusMessages.add(Severity.ERROR,
						"Error al adjuntar el registro. Verifique");
				return null;
			}

			return idDoc;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
			return null;

		}
	}

	@SuppressWarnings("unchecked")
	private boolean checkTamanho(String nomfinal, int tamanho) {

		String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
		List<TipoArchivo> tam = em.createQuery(
				"select t from TipoArchivo t "
						+ " where lower(t.extension) like '"
						+ extension.toLowerCase() + "'").getResultList();
		if (!tam.isEmpty() && tam.get(0).getUnidMedidaByte() != null) {
			if (tamanho > tam.get(0).getUnidMedidaByte().intValue()) {
				statusMessages.add(Severity.ERROR,
						"El archivo supera el tamaño máximo permitido. Límite "
								+ tam.get(0).getTamanho()
								+ tam.get(0).getUnidMedida() + ", verifique");
				return false;
			}
		}

		return true;
	}

	/***
	 * Este metodo retorna el IdDocumento y varios cod para mensajes -9 si falta
	 * campos obligatorios -8 si supera la cantidad msg=La cantidad de archivos
	 * permitidos supera lo permitido. Consulte con el administrador del sistema
	 * -7 si supera el tamaño msg=El archivo que desea levantar, supera el
	 * tamaño permitido. Seleccione otro -6 Ya existe el mismo archivo,
	 * Verifique null Error a intentar adjuntar el archivo
	 */
	@SuppressWarnings("unchecked")
	public static Long guardarDirecto(UploadItem itemCU,
			String direccionfisicaCU, String nPantalla, Long idDatosEspecifico,
			Long IdUsuario, String nombreTabla) throws NamingException {

		/**
		 * Se agregar una validacion cuando el nombre de la pantalla finaliza en
		 * _copia la validacion de tamaño no se reaizara
		 */
		Boolean esCopia = false;
		if (nPantalla.endsWith("_copia"))
			esCopia = true;
		/**
		 * fin
		 */

		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
				"java:/siccaEntityManagerFactory");
		emsta = emf.createEntityManager();
		Long idDoc = null;
		Usuario usu = emsta.find(Usuario.class, IdUsuario);
		try {
			if (itemCU == null || idDatosEspecifico == null
					|| direccionfisicaCU == null) {
				// statusMessages.add("Ingrese los datos obligatorios");
				return new Long(-9);// retorna el valor -9 para el mensaje datos
									// Obligatorios
			}
			direccionfisicaCU = ponerBarra(direccionfisicaCU);
			if (!direccionfisicaCU.toUpperCase().startsWith(PRE.toUpperCase()))
				direccionfisicaCU = PRE + direccionfisicaCU;

			Documentos documentos = new Documentos();

			List<Funcion> funcions = emsta.createQuery(
					"Select f from Funcion f" + " where lower(f.url) like '"
							+ nPantalla.toLowerCase().trim() + "'")
					.getResultList();

			String nomfinal = "";
			nomfinal = itemCU.getFileName();

			Integer tamArchivo = itemCU.getFileSize();
			String tipoDoc = itemCU.getContentType();
			// obtener la extension
			String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
			if (!esCopia) {

				List<TipoArchivo> ta = emsta.createQuery(
						"select t from TipoArchivo t "
								+ " where lower(t.extension) like '"
								+ extension.toLowerCase() + "'")
						.getResultList();
				if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
					if (tamArchivo.intValue() > ta.get(0).getUnidMedidaByte()
							.intValue()) {
						// statusMessages.add("El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return new Long(-7);// retorna el valor -7 para el
											// mensaje
											// tamaño
					}
				}
			}

			CheckSum s = new CheckSum();
			s.comprobar();
			String md5Valor = CheckSum.MD5Checksum(itemCU.getFile());

			Long existe = existeDocChksumId(md5Valor, nPantalla);
			if (existe != null && existeDocNombre(nomfinal, nPantalla)) {
				// statusMessages.add("Ya existe el mismo archivo, Verifique");
				//return existe;// retorna el valor -6 para el mensaje
				return new Long(-6);				// duplicado
			}

			nomfinal = fileNameFormatter(nomfinal);
			documentos.setActivo(true);
			documentos.setUsuAlta(usu.getCodigoUsuario());
			documentos.setFechaAlta(new Date());
			documentos.setMimetype(tipoDoc);
			documentos.setTamanhoDoc(tamArchivo.toString());
			documentos.setChecksum(md5Valor);

			documentos.setUbicacionFisica(direccionfisicaCU);
			documentos.setNombreFisicoDoc(nomfinal);
			documentos.setNombreTabla(nombreTabla);
			documentos.setNombrePantalla(nPantalla);
			
			documentos.setDatosEspecificos(emsta.find(DatosEspecificos.class,
					idDatosEspecifico));
			emsta.persist(documentos);
			emsta.flush();
			idDoc = documentos.getIdDocumento();
			guardarArchivo(nomfinal, itemCU.getFile(), extension,
					direccionfisicaCU);

			return idDoc;// retorna el valor del id generado

		} catch (Exception e) {
			e.printStackTrace();
			return null;// error
		}

	}
	/***
	 * Este metodo retorna el IdDocumento y varios cod para mensajes -9 si falta
	 * campos obligatorios -8 si supera la cantidad msg=La cantidad de archivos
	 * permitidos supera lo permitido. Consulte con el administrador del sistema
	 * -7 si supera el tamaño msg=El archivo que desea levantar, supera el
	 * tamaño permitido. Seleccione otro -6 Ya existe el mismo archivo,
	 * Verifique null Error a intentar adjuntar el archivo
	 * Guarda la fecha del documento
	 */
	@SuppressWarnings("unchecked")
	public static Long guardarConFechaDoc(UploadItem itemCU,
			String direccionfisicaCU, String nPantalla, Long idDatosEspecifico,
			Long IdUsuario, String nombreTabla, Date fechaDoc) throws NamingException {

		/**
		 * Se agregar una validacion cuando el nombre de la pantalla finaliza en
		 * _copia la validacion de tamaño no se reaizara
		 */
		Boolean esCopia = false;
		if (nPantalla.endsWith("_copia"))
			esCopia = true;
		/**
		 * fin
		 */

		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
				"java:/siccaEntityManagerFactory");
		emsta = emf.createEntityManager();
		Long idDoc = null;
		Usuario usu = emsta.find(Usuario.class, IdUsuario);
		try {
			if (itemCU == null || idDatosEspecifico == null
					|| direccionfisicaCU == null) {
				return new Long(-9);// retorna el valor -9 para el mensaje datos
									// Obligatorios
			}
			direccionfisicaCU = ponerBarra(direccionfisicaCU);
			if (!direccionfisicaCU.toUpperCase().startsWith(PRE.toUpperCase()))
				direccionfisicaCU = PRE + direccionfisicaCU;

			Documentos documentos = new Documentos();
			String nomfinal = itemCU.getFileName();

			Integer tamArchivo = itemCU.getFileSize();
			String tipoDoc = itemCU.getContentType();
			// obtener la extension
			String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
			if (!esCopia) {

				List<TipoArchivo> ta = emsta.createQuery(
						"select t from TipoArchivo t "
								+ " where lower(t.extension) like '"
								+ extension.toLowerCase() + "'")
						.getResultList();
				if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
					if (tamArchivo.intValue() > ta.get(0).getUnidMedidaByte()
							.intValue()) {
						return new Long(-7);// retorna el valor -7 para el
											// mensaje
											// tamaño
					}
				}
			}

			CheckSum s = new CheckSum();
			s.comprobar();
			String md5Valor = CheckSum.MD5Checksum(itemCU.getFile());

			Long existe = existeDocChksumId(md5Valor, nPantalla);
			if (existe != null && existeDocNombre(nomfinal, nPantalla)) {
				//return existe;// retorna el valor -6 para el mensaje
				return new Long(-6);				// duplicado
			}

			nomfinal = fileNameFormatter(nomfinal);
			documentos.setActivo(true);
			documentos.setUsuAlta(usu.getCodigoUsuario());
			documentos.setFechaAlta(new Date());
			documentos.setFechaDoc(fechaDoc);
			documentos.setMimetype(tipoDoc);
			documentos.setTamanhoDoc(tamArchivo.toString());
			documentos.setChecksum(md5Valor);

			documentos.setUbicacionFisica(direccionfisicaCU);
			documentos.setNombreFisicoDoc(nomfinal);
			documentos.setNombreTabla(nombreTabla);
			documentos.setNombrePantalla(nPantalla);
			
			documentos.setDatosEspecificos(emsta.find(DatosEspecificos.class,
					idDatosEspecifico));
			emsta.persist(documentos);
			emsta.flush();
			idDoc = documentos.getIdDocumento();
			guardarArchivo(nomfinal, itemCU.getFile(), extension,
					direccionfisicaCU);

			return idDoc;// retorna el valor del id generado

		} catch (Exception e) {
			e.printStackTrace();
			return null;// error
		}

	}
	/***
	 * Este metodo retorna el IdDocumento y varios cod para mensajes -9 si falta
	 * campos obligatorios -8 si supera la cantidad msg=La cantidad de archivos
	 * permitidos supera lo permitido. Consulte con el administrador del sistema
	 * -7 si supera el tamaño msg=El archivo que desea levantar, supera el
	 * tamaño permitido. Seleccione otro -6 Ya existe el mismo archivo,
	 * Verifique null Error a intentar adjuntar el archivo
	 */
	@SuppressWarnings("unchecked")
	public static Long guardarDirectoPersona(UploadItem itemCU,
			String direccionfisicaCU, String nPantalla, Long idDatosEspecifico,
			Long IdUsuario, String nombreTabla, Persona persona) throws NamingException {

		/**
		 * Se agregar una validacion cuando el nombre de la pantalla finaliza en
		 * _copia la validacion de tamaño no se reaizara
		 */
		Boolean esCopia = false;
		if (nPantalla.endsWith("_copia"))
			esCopia = true;
		/**
		 * fin
		 */

		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
				"java:/siccaEntityManagerFactory");
		emsta = emf.createEntityManager();
		Long idDoc = null;
		Usuario usu = emsta.find(Usuario.class, IdUsuario);
		try {
			if (itemCU == null || idDatosEspecifico == null
					|| direccionfisicaCU == null) {
				// statusMessages.add("Ingrese los datos obligatorios");
				return new Long(-9);// retorna el valor -9 para el mensaje datos
									// Obligatorios
			}
			direccionfisicaCU = ponerBarra(direccionfisicaCU);
			if (!direccionfisicaCU.toUpperCase().startsWith(PRE.toUpperCase()))
				direccionfisicaCU = PRE + direccionfisicaCU;

			Documentos documentos = new Documentos();

			List<Funcion> funcions = emsta.createQuery(
					"Select f from Funcion f" + " where lower(f.url) like '"
							+ nPantalla.toLowerCase().trim() + "'")
					.getResultList();

			String nomfinal = "";
			nomfinal = itemCU.getFileName();

			Integer tamArchivo = itemCU.getFileSize();
			String tipoDoc = itemCU.getContentType();
			// obtener la extension
			String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
			if (!esCopia) {

				List<TipoArchivo> ta = emsta.createQuery(
						"select t from TipoArchivo t "
								+ " where lower(t.extension) like '"
								+ extension.toLowerCase() + "'")
						.getResultList();
				if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
					if (tamArchivo.intValue() > ta.get(0).getUnidMedidaByte()
							.intValue()) {
						// statusMessages.add("El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return new Long(-7);// retorna el valor -7 para el
											// mensaje
											// tamaño
					}
				}
			}

			CheckSum s = new CheckSum();
			s.comprobar();
			String md5Valor = CheckSum.MD5Checksum(itemCU.getFile());

			Long existe = existeDocChksumId(md5Valor, nPantalla);
			if (existe != null && existeDocNombre(nomfinal, nPantalla)) {
				// statusMessages.add("Ya existe el mismo archivo, Verifique");
				//return existe;// retorna el valor -6 para el mensaje
				return new Long(-6);				// duplicado
			}

			nomfinal = fileNameFormatter(nomfinal);
			documentos.setActivo(true);
			documentos.setUsuAlta(usu.getCodigoUsuario());
			documentos.setFechaAlta(new Date());
			documentos.setMimetype(tipoDoc);
			documentos.setTamanhoDoc(tamArchivo.toString());
			documentos.setChecksum(md5Valor);

			documentos.setUbicacionFisica(direccionfisicaCU);
			documentos.setNombreFisicoDoc(nomfinal);
			documentos.setNombreTabla(nombreTabla);
			documentos.setNombrePantalla(nPantalla);
			documentos.setPersona(persona); 
			documentos.setDatosEspecificos(emsta.find(DatosEspecificos.class,
					idDatosEspecifico));
			emsta.persist(documentos);
			emsta.flush();
			idDoc = documentos.getIdDocumento();
			guardarArchivo(nomfinal, itemCU.getFile(), extension,
					direccionfisicaCU);

			return idDoc;// retorna el valor del id generado

		} catch (Exception e) {
			e.printStackTrace();
			return null;// error
		}

	}
	
	@SuppressWarnings("unchecked")
	private static boolean existeDocNombre(String nombre, String nombrePantalla) {

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
		sql = "Select d from Documentos d where d.nombreFisicoDoc like '" + nombre
				+ "' and lower(d.nombrePantalla) like '"
				+ nombrePantalla.toLowerCase() + "' and  d.activo=true";

		List<Documentos> documentos = emsta.createQuery(sql).getResultList();

		if (!documentos.isEmpty())
			return true;

		return false;
	}

	@SuppressWarnings("unchecked")
	public static Long guardarDirectoCU367(UploadItem itemCU,
			String direccionfisicaCU, String nPantalla, Long idDatosEspecifico,
			String usuarioActual, String nombreTabla) throws NamingException {

		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
				"java:/siccaEntityManagerFactory");
		emsta = emf.createEntityManager();
		Long idDoc = null;
		// Usuario usu=emsta.find(Usuario.class, IdUsuario);
		try {
			if (itemCU == null || idDatosEspecifico == null
					|| direccionfisicaCU == null) {
				// statusMessages.add("Ingrese los datos obligatorios");
				return new Long(-9);// retorna el valor -9 para el mensaje datos
									// Obligatorios
			}
			direccionfisicaCU = ponerBarra(direccionfisicaCU);
			direccionfisicaCU = PRE + direccionfisicaCU;

			Documentos documentos = new Documentos();

			/*
			 * List<Funcion> funcions=
			 * emsta.createQuery("Select f from Funcion f" +
			 * " where lower(f.url) like '"
			 * +nPantalla.toLowerCase().trim()+"'").getResultList();
			 * 
			 * List<SelFuncionDatosEsp> sflist=
			 * emsta.createQuery(" SELECT o FROM " +
			 * SelFuncionDatosEsp.class.getName() + " o " +
			 * "WHERE o.activo = true and o.funcion.idFuncion="
			 * +funcions.get(0).getIdFuncion()+" ").getResultList();
			 */
			List<Documentos> entityInterfaces = emsta.createQuery(
					" select o from  Documentos  o "
							+ "where o.datosEspecificos.idDatosEspecificos="
							+ idDatosEspecifico + " "
							+ " and lower(o.nombreTabla) like '"
							+ nombreTabla.toLowerCase() + "'"
							+ "and o.activo=true").getResultList();
			/*
			 * if(!sflist.isEmpty()){
			 * if(entityInterfaces.size()>sflist.get(0).getCantidad()){
			 * //statusMessages.add(
			 * "La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema"
			 * ); return new Long(-8);//retorna el valor -8 para el mensaje
			 * cantidad } }
			 */

			String nomfinal = "";
			nomfinal = itemCU.getFileName();

			Integer tamArchivo = itemCU.getFileSize();
			String tipoDoc = itemCU.getContentType();

			String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
			List<TipoArchivo> ta = emsta.createQuery(
					"select t from TipoArchivo t "
							+ " where lower(t.extension) like '"
							+ extension.toLowerCase() + "'").getResultList();
			if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
				if (tamArchivo.intValue() > ta.get(0).getUnidMedidaByte()
						.intValue()) {
					// statusMessages.add("El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return new Long(-7);// retorna el valor -7 para el mensaje
										// tamaño
				}
			}

			SelFuncionDatosEsp f = new SelFuncionDatosEsp();
			/*
			 * if( !sflist.isEmpty()) f= emsta.find(SelFuncionDatosEsp.class,
			 * sflist.get(0).getIdFuncionDatosEsp());
			 */

			CheckSum s = new CheckSum();
			s.comprobar();
			String md5Valor = CheckSum.MD5Checksum(itemCU.getFile());

			if (existeDocChksum(md5Valor, nPantalla)) {
				// statusMessages.add("Ya existe el mismo archivo, Verifique");
				return new Long(-6);// retorna el valor -6 para el mensaje
									// duplicado
			}

			nomfinal = fileNameFormatter(nomfinal);
			documentos.setActivo(true);
			documentos.setUsuAlta(usuarioActual);
			documentos.setFechaAlta(new Date());
			documentos.setMimetype(tipoDoc);
			documentos.setTamanhoDoc(tamArchivo.toString());
			documentos.setChecksum(md5Valor);

			documentos.setUbicacionFisica(direccionfisicaCU);
			documentos.setNombreFisicoDoc(nomfinal);
			documentos.setNombreTabla(nombreTabla);
			documentos.setNombrePantalla(nPantalla);
			documentos.setDatosEspecificos(emsta.find(DatosEspecificos.class,
					idDatosEspecifico));
			emsta.persist(documentos);
			emsta.flush();
			idDoc = documentos.getIdDocumento();
			guardarArchivo(nomfinal, itemCU.getFile(), extension,
					direccionfisicaCU);

			return idDoc;// retorna el valor del id generado

		} catch (Exception e) {
			e.printStackTrace();
			return null;// error
		}

	}
	
	
	@SuppressWarnings("unchecked")
	public static Long guardarDocParaPublicacion(UploadItem itemCU,
			String direccionfisicaCU, String nPantalla, Long idDatosEspecifico,
			String usuarioActual, String nombreTabla, Long idConcurso , Long idConcursoPuestoAgr) throws NamingException {

		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
				"java:/siccaEntityManagerFactory");
		emsta = emf.createEntityManager();
		Long idDoc = null;
		// Usuario usu=emsta.find(Usuario.class, IdUsuario);
		try {
			if (itemCU == null || idDatosEspecifico == null
					|| direccionfisicaCU == null) {
				// statusMessages.add("Ingrese los datos obligatorios");
				return new Long(-9);// retorna el valor -9 para el mensaje datos
									// Obligatorios
			}
			direccionfisicaCU = ponerBarra(direccionfisicaCU);
			direccionfisicaCU = PRE + direccionfisicaCU;

			Documentos documentos = new Documentos();

			
			List<Documentos> entityInterfaces = emsta.createQuery(
					" select o from  Documentos  o "
							+ "where o.datosEspecificos.idDatosEspecificos="
							+ idDatosEspecifico + " "
							+ " and lower(o.nombreTabla) like '"
							+ nombreTabla.toLowerCase() + "'"
							+ "and o.activo=true").getResultList();
		
			String nomfinal = "";
			nomfinal = itemCU.getFileName();

			Integer tamArchivo = itemCU.getFileSize();
			String tipoDoc = itemCU.getContentType();

			String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
			List<TipoArchivo> ta = emsta.createQuery(
					"select t from TipoArchivo t "
							+ " where lower(t.extension) like '"
							+ extension.toLowerCase() + "'").getResultList();
			if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
				if (tamArchivo.intValue() > ta.get(0).getUnidMedidaByte()
						.intValue()) {
					// statusMessages.add("El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return new Long(-7);// retorna el valor -7 para el mensaje
										// tamaño
				}
			}

			SelFuncionDatosEsp f = new SelFuncionDatosEsp();
		
			
			CheckSum s = new CheckSum();
			s.comprobar();
			String md5Valor = CheckSum.MD5Checksum(itemCU.getFile());
		

			nomfinal = fileNameFormatter(nomfinal);
			documentos.setActivo(true);
			documentos.setUsuAlta(usuarioActual);
			documentos.setFechaAlta(new Date());
			documentos.setMimetype(tipoDoc);
			documentos.setTamanhoDoc(tamArchivo.toString());
			documentos.setChecksum(md5Valor);
			Concurso concurso = emsta.find(Concurso.class,idConcurso);
			if(concurso != null)
				documentos.setConcurso(concurso);
			
			ConcursoPuestoAgr cpa = emsta.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
			if(cpa != null)
				documentos.setConcursoPuestoAgr(cpa);
			
			documentos.setUbicacionFisica(direccionfisicaCU);
			documentos.setNombreFisicoDoc(nomfinal);
			documentos.setNombreTabla(nombreTabla);
			documentos.setNombrePantalla(nPantalla);
			documentos.setDatosEspecificos(emsta.find(DatosEspecificos.class,
					idDatosEspecifico));
		
			emsta.persist(documentos);
			emsta.flush();
			idDoc = documentos.getIdDocumento();
			guardarArchivo(nomfinal, itemCU.getFile(), extension,
					direccionfisicaCU);

			return idDoc;// retorna el valor del id generado

		} catch (Exception e) {
			e.printStackTrace();
			return null;// error
		}

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

	public static String ponerBarraSimple(String direccion) {
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

	/***
	 * Este metodo retorna el IdDocumento y varios cod para mensajes -9 si falta
	 * campos obligatorios -8 si supera la cantidad msg=La cantidad de archivos
	 * permitidos supera lo permitido. Consulte con el administrador del sistema
	 * -7 si supera el tamaño msg=El archivo que desea levantar, supera el
	 * tamaño permitido. Seleccione otro null Error a intentar adjuntar el
	 * archivo
	 */
	@SuppressWarnings("unchecked")
	public static Long editar(File inputFile, Integer tam, String nombreFisico,
			String tipo, String direccionfisicaCU, String nPantalla,
			Long idDocumento, Long idDatosEspecifico, Long idUsuario,
			Long idCU, String nomTabla) {
		Long idDoc = null;

		try {
			EntityManagerFactory emf = null;
			EntityManager emsta = null;
			emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
					"java:/siccaEntityManagerFactory");
			emsta = emf.createEntityManager();
			if (idDocumento == null) {
				direccionfisicaCU = ponerBarra(direccionfisicaCU);
				direccionfisicaCU = PRE + direccionfisicaCU;
			} else {
				direccionfisicaCU = ponerBarraSimple(direccionfisicaCU);
			}

			nombreFisico = fileNameFormatter(nombreFisico);

			Usuario usu = emsta.find(Usuario.class, idUsuario);
			if (inputFile == null) {
				// statusMessages.add("Ingrese los datos obligatorios");
				return new Long(-9);// retorna el valor -9 para el mensaje datos
									// Obligatorios
			}
			// List<Funcion> funcions =
			// emsta.createQuery("Select f from Funcion f" +
			// " where lower(f.url) like '"
			// + nPantalla.toLowerCase().trim() + "'").getResultList();
			//
			// List<SelFuncionDatosEsp> sflist =
			// emsta.createQuery(" SELECT o FROM " +
			// SelFuncionDatosEsp.class.getName() + " o "
			// + "WHERE o.activo = true and o.funcion.idFuncion="
			// + funcions.get(0).getIdFuncion() + " ").getResultList();
			//
			// List<Documentos> entityInterfaces =
			// emsta.createQuery(" select o from  Documentos  o "
			// + "where o.datosEspecificos.idDatosEspecificos=" +
			// idDatosEspecifico + " "
			// + " and lower(o.nombreTabla) like '" + nomTabla.toLowerCase() +
			// "'"
			// + " and o.activo=true").getResultList();
			// if (!sflist.isEmpty()) {
			// if (entityInterfaces.size() < sflist.get(0).getCantidad()) {
			// //
			// statusMessages.add("La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
			// return new Long(-8);// retorna el valor -8 para el mensaje
			// // cantidad
			// }
			// }

			Integer tamArchivo = tam;
			String tipoDoc = tipo;

			String extension = nombreFisico.substring(nombreFisico
					.lastIndexOf("."));
			// String[] t = tipoDoc.split("\\/");
			List<TipoArchivo> ta = emsta.createQuery(
					"select t from TipoArchivo t "
							+ " where lower(t.extension) like '"
							+ extension.toLowerCase() + "'").getResultList();
			if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
				if (tamArchivo.intValue() > ta.get(0).getUnidMedidaByte()
						.intValue())
					// statusMessages.add("El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return new Long(-7);// retorna el valor -7 para el mensaje
										// tamaño
			}

			if (idDocumento != null) {
				Documentos docEdit = emsta.find(Documentos.class, idDocumento);
				String nomfinal = "";
				nomfinal = docEdit.getNombreFisicoDoc();
				CheckSum s = new CheckSum();
				s.comprobar();
				String md5Valor = CheckSum.MD5Checksum(inputFile);

				if (mismoDocChksumEdit(md5Valor, idDocumento)
						&& docEdit.getDatosEspecificos().getIdDatosEspecificos() == idDatosEspecifico) {
					// si es el
					// mismo archivo y no se cambio su tipo
					// no realiza nada! retorna el mismo idDocumento
					return idDocumento;

				} else {// si no es el mismo doc.
					Documentos doc = new Documentos();// se crea un nuevo
														// documento
					doc.setActivo(true);
					doc.setUsuAlta(usu.getCodigoUsuario());
					doc.setFechaAlta(new Date());
					doc.setChecksum(md5Valor);
					doc.setMimetype(tipoDoc);
					doc.setTamanhoDoc(tamArchivo.toString());
					doc.setIdTabla(idCU);
					doc.setNombreTabla(nomTabla);
					doc.setNombrePantalla(nPantalla);
					doc.setUbicacionFisica(direccionfisicaCU);
					doc.setNombreFisicoDoc(nombreFisico);
					doc.setDatosEspecificos(emsta.find(DatosEspecificos.class,
							idDatosEspecifico));
					emsta.persist(doc);
					emsta.flush();
					idDoc = doc.getIdDocumento();
					/**
					 * se inactiva el otro
					 */
					docEdit.setActivo(false);
					docEdit.setUsuMod(usu.getCodigoUsuario());
					docEdit.setFechaMod(new Date());
					emsta.merge(docEdit);
					emsta.persist(docEdit);
					guardarArchivo(nombreFisico, inputFile, extension,
							direccionfisicaCU);
				}
			} else {// se crea uno nuevo

				CheckSum s = new CheckSum();
				s.comprobar();
				String md5Valor = CheckSum.MD5Checksum(inputFile);
				if (existeDocChksum(md5Valor, nPantalla)) {
					// statusMessages.add("El archivo que desea levantar ya existe");
					return new Long(-6);// retorna el valor -6 para el mensaje
										// tamaño
				}

				Documentos doc = new Documentos();// se crea un nuevo documento
				doc.setActivo(true);
				doc.setChecksum(md5Valor);
				doc.setUsuAlta(usu.getCodigoUsuario());
				doc.setFechaAlta(new Date());
				doc.setMimetype(tipoDoc);
				doc.setTamanhoDoc(tamArchivo.toString());
				doc.setIdTabla(idCU);
				doc.setNombreTabla(nomTabla);
				doc.setNombrePantalla(nPantalla);
				doc.setUbicacionFisica(direccionfisicaCU);
				doc.setNombreFisicoDoc(nombreFisico);
				doc.setDatosEspecificos(emsta.find(DatosEspecificos.class,
						idDatosEspecifico));
				emsta.persist(doc);
				emsta.flush();
				idDoc = doc.getIdDocumento();
				guardarArchivo(nombreFisico, inputFile, extension,
						direccionfisicaCU);

			}

			return idDoc;// retorna el valor del id generado
		} catch (Exception e) {
			e.printStackTrace();
			return null;// error
		}
	}

	public void abrirDocumento(int Index) {

		try {
			if(documentoDTOList.size() > 0){
				
			
				Documentos doc = em.find(Documentos.class,documentoDTOList.get(Index).getIdDocumento());
				doc.setUsuUltDesc(usuarioLogueado.getCodigoUsuario());
				doc.setFechaUltDesc(new Date());
				em.merge(doc);
				em.flush();
	
				String url = ponerBarraSimple(doc.getUbicacionFisica());
	
				String separador = System.getProperty("file.separator");
				if (!url.endsWith(separador))
					url = url + System.getProperty("file.separator");
	
				url += doc.getNombreFisicoDoc();
	
				File inputFile = new File(url);
				enviarArchivoANavegador(doc.getNombreFisicoDoc(), inputFile);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void abrirDocumentoFromCU(Long idDocmuento, String codUsu) {

		try {
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
			Documentos doc = emsta.find(Documentos.class, idDocmuento);

			doc.setUsuUltDesc(codUsu);
			doc.setFechaUltDesc(new Date());
			emsta.merge(doc);
			emsta.flush();

			String url = ponerBarraSimple(doc.getUbicacionFisica());

			String separador = System.getProperty("file.separator");
			if (!url.endsWith(separador))
				url = url + System.getProperty("file.separator");

			url += doc.getNombreFisicoDoc();

			File inputFile = new File(url);
			enviarArchivoANavegador(doc.getNombreFisicoDoc(), inputFile);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String abrirDocumentoFromCU(Long idDocmuento, Long idUsu) {

		try {
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
			Documentos doc = emsta.find(Documentos.class, idDocmuento);
			if (idUsu != null) {
				Usuario u = emsta.find(Usuario.class, idUsu);
				doc.setUsuUltDesc(u.getCodigoUsuario());
			}
			doc.setFechaUltDesc(new Date());
			emsta.merge(doc);
			emsta.flush();

			String url = ponerBarraSimple(doc.getUbicacionFisica());

			String separador = System.getProperty("file.separator");
			if (!url.endsWith(separador))
				url = url + System.getProperty("file.separator");

			url += doc.getNombreFisicoDoc();

			File inputFile = new File(url);
			enviarArchivoANavegador(doc.getNombreFisicoDoc(), inputFile);
			return "OK";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "No se encontró el archivo ";
		} catch (IOException e) {
			e.printStackTrace();
			return "Error de I/O";
		}
	}

	public static void enviarArchivoANavegador(String nombreArchivoSugerido,
		File archivoABajar) throws FileNotFoundException, IOException {
		FileInputStream archivo = new FileInputStream(archivoABajar);

		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();

		response.setContentType("application/binary");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ nombreArchivoSugerido);
		response.setContentLength(archivo.available());

		OutputStream salida = response.getOutputStream();

		byte buffer[] = new byte[1000000];

		long count = 0;
		long resto = 0;

		archivo.toString().getBytes();
		count = (long) archivo.available() / 1000000;

		if (((float) archivo.available() / 1000000) > count) {
			resto = archivo.available() % 1000000;
		}

		for (long bytes = 0; bytes < count; bytes++) {
			archivo.read(buffer);
			salida.write(buffer);
		}

		if (resto != 0) {
			archivo.read(buffer, 0, (int) resto);
			salida.write(buffer, 0, (int) resto);
		} else {
			archivo.read(buffer);
			salida.write(buffer);
		}

		archivo.close();
		salida.flush();
		salida.close();
		FacesContext.getCurrentInstance().responseComplete();
	}

	@SuppressWarnings("unchecked")
	private List<SelFuncionDatosEsp> getDatosEspecificoss() {
		try {
			String sql = "";
			if (nombrePantalla.equals("FirmaResolHomolog")
					|| nombrePantalla.equals("FirmaResolNombram")
					|| nombrePantalla.equals("FirmaResolAdjudic")
					|| nombrePantalla.equals("edit_puestos_trabajo")
					|| nombrePantalla.equals("evaluacion_doc_adjudicados")
					|| nombrePantalla.equals("modificacion_datos_concurso_list")) {
				sql = "Select f from Funcion f where f.url like 'firmarResolucionHomologacion_edit' ";
			} else
				sql = "Select f from Funcion f where lower(f.url) like '"
						+ nombrePantalla.toLowerCase().trim() + "'";
			List<Funcion> funcions = em.createQuery(sql).getResultList();
			if (funcions.isEmpty()) return new Vector<SelFuncionDatosEsp>();
			String sqll = " SELECT o FROM " + SelFuncionDatosEsp.class.getName()
					+ " o "
					+ "WHERE o.activo = true and o.funcion.idFuncion="
					+ funcions.get(0).getIdFuncion() + " ";
			return em.createQuery(
					" SELECT o FROM " + SelFuncionDatosEsp.class.getName()
							+ " o "
							+ "WHERE o.activo = true and o.funcion.idFuncion="
							+ funcions.get(0).getIdFuncion() + " ")
					.getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return new Vector<SelFuncionDatosEsp>();
		}
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
	private void listarExistentesDocs() {
		if (idConcurso == null && documentoDTOList == null) {
			documentosList.getDocumentos().setIdTabla(idCU);
			documentosList.getDocumentos().setNombreTabla(nombreTabla);
			documentosList.getDocumentos().setNombrePantalla(nombrePantalla);
			documentoDTOList = documentosList.getResultList();
		} else {
//			String q = "select * from general.documentos where documentos.activo is true and documentos.id_concurso="
//					+ idConcurso + " and nombre_pantalla = '"+nombrePantalla+ "'";
//			documentoDTOList = em.createNativeQuery(q, Documentos.class)
//					.getResultList();
			//agregado para el módulo Eval. Desempeño; Werner.
			//******************************************************************************************************************************
			String q = new String();
			if (nombrePantalla.equals("cargarResultEvalMetodo571") 
					|| nombrePantalla.equals("cargarResultEvalMetodo571Area") || nombrePantalla.equals("cargarResultEvalPDEC572FC") 
					|| nombrePantalla.equals("cargarResultEvalPerce608FC") || nombrePantalla.equals("adjuntarReglamento") 
					|| nombrePantalla.equals("cargarResultEvalMetodo571Gestion") || nombrePantalla.equals("lista_larga_list")
					|| nombrePantalla.equals("edit_puestos_trabajo") || nombrePantalla.equals("adjuntarObservacionSFP")
					|| nombrePantalla.equals("adjuntarInformeFinal") || nombrePantalla.equals("enviarHomologacionPerfil_edit")
					|| nombrePantalla.equals("evaluacion_doc_adjudicados") || nombrePantalla.equals("publicacion_seleccionados")
					|| nombrePantalla.equals("comision_edit") || nombrePantalla.equals("desvinculacion_edit")
					|| nombrePantalla.equals("admInhabilitadosSFP") || nombrePantalla.equals("admJubiladosSFP")) {
				q = "select * from general.documentos where documentos.activo is true and documentos.id_tabla="
					+ idCU + " and nombre_pantalla = '"+nombrePantalla+ "'";
				documentoDTOList = em.createNativeQuery(q, Documentos.class).getResultList();
				
			}else{
				if (nombrePantalla.equals("adjuntarResolucion")) {//agregado; Werner.
					SimpleDateFormat sdfFechaCompleta = new SimpleDateFormat("yyyy");
					String fechaCompleta = sdfFechaCompleta.format(new Date());
					Integer anho = Integer.parseInt(fechaCompleta);
					
					q = "select * from general.documentos where documentos.activo is true "
							+ "and nombre_pantalla = '"+nombrePantalla+ "' and documentos.anho_documento = "+anho
							+ " and documentos.id_configuracion_uo = "+capturandoOee().getIdConfiguracionUo();
						documentoDTOList = em.createNativeQuery(q, Documentos.class).getResultList();
				}else{
					q = "select * from general.documentos where documentos.activo is true and documentos.id_concurso="
							+ idConcurso + " and nombre_pantalla = '"+nombrePantalla+ "'";
					documentoDTOList = em.createNativeQuery(q, Documentos.class).getResultList();
				}
			}
			//******************************************************************************************************************************
		}
	}

	public void miListenerAdjuntar(UploadEvent event) {
		try {

			item = event.getUploadItem();
			item.getFile();
			FileInputStream fis;
			try {
				fis = new FileInputStream(item.getFile());
			} catch (FileNotFoundException e1) {
				return;
			}

			long length = item.getFileSize();

			if (length > 5242880) {
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
			int pos = item.getFileName().lastIndexOf(".");

			tamArchivo = item.getFileSize();
			tipoDoc = item.getContentType();
			nomfinal = item.getFileName();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void eliminar(int index) {
		try {
			Documentos aux = documentoDTOList.get(index);
			aux.setActivo(false);
			aux.setFechaMod(new Date());
			aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(aux);
			em.flush();
			documentoDTOList.remove(index);
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	@SuppressWarnings("unchecked")
	private static Long existeDocChksumId(String md5, String nombrePantalla) {

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
			return documentos.get(0).getIdDocumento();

		return null;
	}

	@SuppressWarnings("unchecked")
	private static boolean existeDocChksumEdit(String md5,
			String nombrePantalla, Long idDoc) {

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
				+ nombrePantalla.toLowerCase()
				+ "' and  d.activo=true and d.idDocumento=" + idDoc.longValue();

		List<Documentos> documentos = emsta.createQuery(sql).getResultList();

		if (!documentos.isEmpty())
			return true;

		return false;
	}

	@SuppressWarnings("unchecked")
	private static boolean mismoDocChksumEdit(String md5, Long idDoc) {

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
				+ "' and d.idDocumento =" + idDoc + " and  d.activo=true";

		List<Documentos> documentos = emsta.createQuery(sql).getResultList();

		if (!documentos.isEmpty())
			return true;

		return false;
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

	/**
	 * Crea en la Tabla PostulacionAdjuntos un registro con el Documento
	 * generado
	 * 
	 * @return id del documento
	 */
	public Long hacerCopiaDocPostulantes(Long idPostulacion,
			String direccionFisica, byte[] file, String nombrePantalla,
			Documentos documentos) {
		try {
			UploadItem fileItem = seleccionUtilFormController.crearUploadItem(
					documentos.getNombreFisicoDoc(),
					Integer.parseInt(documentos.getTamanhoDoc()),
					documentos.getMimetype(), file);

			/**
			 * 1. COPIAR LOS DOCUMENTOS INSERTADOS EN LA TABLA DOCUMENTOS
			 * ADJUNTOS A LA CARPETA CI_ID_PERSONA
			 **/
			Long idTipoDoc = documentos.getDatosEspecificos()
					.getIdDatosEspecificos();

			Long idDocumentoCopia = guardarDoc(fileItem, direccionFisica,
					nombrePantalla, idTipoDoc, documentos.getNombreTabla(),
					null);

			if (idDocumentoCopia == null)
				return null;
			/**
			 * 2. Obtener los ID_DOCUMENTOS que fueron insertados en el paso
			 * ‘DOCUMENTO ADJUNTO’, e ir insertando en la tabla
			 * SEL_POSTULACION_ADJUNTOS
			 **/
			PostulacionAdjuntos postulacionAdjuntos = new PostulacionAdjuntos();
			postulacionAdjuntos.setPostulacion(em.find(Postulacion.class,
					idPostulacion));
			postulacionAdjuntos.setDocumento(new Documentos());
			postulacionAdjuntos.getDocumento().setIdDocumento(idDocumentoCopia);
			postulacionAdjuntos.setActivo(true);
			postulacionAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			postulacionAdjuntos.setFechaAlta(new Date());
			em.persist(postulacionAdjuntos);

			return idDocumentoCopia;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static File recuperarImagen(Long idDocmuento, Long idUsu) {

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
		Documentos doc = emsta.find(Documentos.class, idDocmuento);
		if (idUsu != null) {
			Usuario u = emsta.find(Usuario.class, idUsu);
			doc.setUsuUltDesc(u.getCodigoUsuario());
		}
		doc.setFechaUltDesc(new Date());
		emsta.merge(doc);
		emsta.flush();

		String url = ponerBarraSimple(doc.getUbicacionFisica());

		String separador = System.getProperty("file.separator");
		if (!url.endsWith(separador))
			url = url + System.getProperty("file.separator");

		url += doc.getNombreFisicoDoc();

		File inputFile = new File(url);

		return inputFile;

	}
	//********************************************************************* Werner
	//agregado para el rendered;
	
	public boolean ocultandoPanelGrupo(){
		return (OCULTAR_GRUPO.contains(nombrePantalla));
	}
	public boolean ocultandoReglamento(){
		if (nombrePantalla.equals("adjuntarReglamento"))
			return true;
		else
			return false;
	}
	public boolean agregandoFrom(){
		if (nombrePantalla.equals("edit_puestos_trabajo") || nombrePantalla.equals("concurso_edit")
				|| nombrePantalla.equals("adjuntarReglamento") || nombrePantalla.equals("adjuntarResolucion") 
				|| nombrePantalla.equals("cargarResultEvalMetodo571") || nombrePantalla.equals("cargarResultEvalMetodo571Area") 
				|| nombrePantalla.equals("cargarResultEvalPerce608FC") || nombrePantalla.equals("cargarResultEvalPDEC572FC")
				|| nombrePantalla.equals("cargarResultEvalMetodo571Gestion") || nombrePantalla.equals("adjuntarObservacionSFP")
				|| nombrePantalla.equals("lista_larga_list") || nombrePantalla.equals("adjuntarInformeFinal")
				|| nombrePantalla.equals("enviarHomologacionPerfil_edit") || nombrePantalla.equals("evaluacion_doc_adjudicados")
				|| nombrePantalla.equals("publicacion_seleccionados") || nombrePantalla.equals("FirmaResolAdjudic")
				|| nombrePantalla.equals("modificacion_datos_concurso_list")) {
			return true;
		}
		else
			return false;
	}
	public String renderizandoSpan(){//para Adjuntar Resolución en Eval. Desempeño; Werner.
		if (nombrePantalla.equals("adjuntarResolucion") && nombreTabla.contentEquals("EvaluacionDesempeno"))
			return "*";
		else
			return "";
	}
	private ConfiguracionUoCab capturandoOee(){
		List<EvaluacionDesempeno> evaluacionDesem = new ArrayList<EvaluacionDesempeno>();
		String qEval = "select * from eval_desempeno.evaluacion_desempeno where evaluacion_desempeno.id_evaluacion_desempeno = "+idCU;
		evaluacionDesem = em.createNativeQuery(qEval, EvaluacionDesempeno.class).getResultList();
		return evaluacionDesem.get(0).getConfiguracionUoCab();
	}
	//*********************************************************************
	// GETTERS Y SETTERS

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public List<Documentos> getDocumentoDTOList() {
		return documentoDTOList;
	}

	public void setDocumentoDTOList(List<Documentos> documentoDTOList) {
		this.documentoDTOList = documentoDTOList;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public boolean isMostrarDoc() {
		return mostrarDoc;
	}

	public void setMostrarDoc(boolean mostrarDoc) {
		this.mostrarDoc = mostrarDoc;
	}

	public String getNombreTabla() {
		return nombreTabla;
	}

	public void setNombreTabla(String nombreTabla) {
		this.nombreTabla = nombreTabla;
	}

	public Long getIdCU() {
		return idCU;
	}

	public void setIdCU(Long idCU) {
		this.idCU = idCU;
	}

	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public Long getIdFuncionDatosEsp() {
		return idFuncionDatosEsp;
	}

	public void setIdFuncionDatosEsp(Long idFuncionDatosEsp) {
		this.idFuncionDatosEsp = idFuncionDatosEsp;
	}

	public Integer getTamArchivo() {
		return tamArchivo;
	}

	public void setTamArchivo(Integer tamArchivo) {
		this.tamArchivo = tamArchivo;
	}

	public String getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	public String getNomfinal() {
		return nomfinal;
	}

	public void setNomfinal(String nomfinal) {
		this.nomfinal = nomfinal;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		if (from == null || "".equals(from))
			return "/home.xhtml";
		if (from.contains(".seam"))
			return "/" + from;
		if (nombrePantalla.equals("FirmaResolHomolog"))
			return "/" + from + ".seam";
		if (nombrePantalla.equals("realizarEntrevistaFinal"))
			return "/" + from + ".seam";
		
		
		//debe recibir en el valor de from una url sin la extencion .seam o .xhtml ejemplo : seleccion/concurso/ConcursoEdit  
//		if(nombrePantalla.equals("edit_puestos_trabajo") || nombrePantalla.equals("concurso_edit") )
		if(agregandoFrom())
			return "/" + from + ".seam";
		if (from.contains("?"))
			return "/" + from.substring(0, from.indexOf("?")) + ".seam"
					+ from.substring(from.indexOf("?"), from.length());
		if (!from.contains(".xhtml"))
			return "/" + from + ".xhtml";
		return from;
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

	public String getDirFisica() {
		return dirFisica;
	}

	public void setDirFisica(String dirFisica) {
		this.dirFisica = dirFisica;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public List<ConcursoPuestoAgr> getListaGruposParaFirmar() {
		return listaGruposParaFirmar;
	}

	public void setListaGruposParaFirmar(
			List<ConcursoPuestoAgr> listaGruposParaFirmar) {
		this.listaGruposParaFirmar = listaGruposParaFirmar;
	}

	public List<Boolean> getSeleccionados() {
		return seleccionados;
	}

	public void setSeleccionados(List<Boolean> seleccionados) {
		this.seleccionados = seleccionados;
	}

	public String getFromFrom() {
		return fromFrom;
	}

	public void setFromFrom(String fromFrom) {
		this.fromFrom = fromFrom;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getPlantaCargoDetFrom() {
		return plantaCargoDetFrom;
	}

	public void setPlantaCargoDetFrom(String plantaCargoDetFrom) {
		this.plantaCargoDetFrom = plantaCargoDetFrom;
	}

}
