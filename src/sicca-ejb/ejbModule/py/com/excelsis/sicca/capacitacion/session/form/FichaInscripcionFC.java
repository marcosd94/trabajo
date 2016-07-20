package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.hibernate.JDBCException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import enums.TiposDatos;
import py.com.excelsis.sicca.capacitacion.session.ConsultasCapacitacionHome;
import py.com.excelsis.sicca.capacitacion.session.PostulacionCapHome;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.IdiomasPersona;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.PostulacionCapAdj;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("fichaInscripcionFC")
public class FichaInscripcionFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	PostulacionCapHome postulacionCapHome;

	private Persona persona = new Persona();
	private PostulacionCap postulacionCap = new PostulacionCap();
	private Capacitaciones capacitaciones = new Capacitaciones();
	private EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
	private PlantaCargoDet plantaCargoDet = new PlantaCargoDet();
	private ConfiguracionUoDet confUoDet = new ConfiguracionUoDet();
	private ConfiguracionUoCab confUoCab = new ConfiguracionUoCab();

	private List<EstudiosRealizados> listaEstudios;
	private List<IdiomasPersona> listaIdiomas;
	private List<PersonaDiscapacidad> discapacidadsList;
	private List<ExperienciaLaboral> listaExperiencia;
	private List<SelectItem> doc1SelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> doc2SelecItem = new ArrayList<SelectItem>();

	private String paso;
	private String nombrePantalla;
	private byte[] uploadedFile;
	private UploadItem item1;
	private String contentType;
	private String fileName;
	private byte[] uploadedFile2;
	private UploadItem item2;
	private String contentType2;
	private String fileName2;
	private String nombreDoc1;
	private String nombreDoc2;
	private Long idTipoDoc1;
	private Long idTipoDoc2;
	private Long idDoc1;
	private Long idDoc2;
	private Boolean fichaCopiada;
	private Long idRecibido;
	private String cu;
	private Long idCapacitacion;
	private Boolean puedeEditarOEEyUO;
	private String oee;
	private String uo;

	private Boolean esPersona;

	public void init() throws Exception {
		validarDatosEntrada();
		fichaCopiada = false;
		persona = em.find(Persona.class, usuarioLogueado.getPersona()
				.getIdPersona());
		paso = "paso1";
		esPersona = true;
		obtenerEstudiosRealizados();
		obtenerIdiomas();
		obtenerDiscapacidades();
		obtenerExperiencia();
		buildDoc1SelectItem();
		buildDoc2SelectItem();
		buscarPostulado();
		deshabilitarEdicionOEEyUO();
	}

	private void validarDatosEntrada() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idRecibido != null) {
			if (!sufc.validarInput(idRecibido.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
		}
		if (cu != null && !cu.trim().isEmpty()) {
			if (!sufc.validarInput(cu.toString(), TiposDatos.STRING.getValor(),
					null))
				return;
		}
	}

	public void initVer() {
		verificarEsPersona();
		paso = "paso1";
		if (esPersona)
			persona = em.find(Persona.class, idRecibido);

		else {
			PersonaPostulante personaPostulante = new PersonaPostulante();
			personaPostulante = em.find(PersonaPostulante.class, idRecibido);
			persona = em.find(Persona.class, personaPostulante.getPersona()
					.getIdPersona());
			buscarCapacitacion();
		}
		obtenerEstudiosRealizados();
		obtenerIdiomas();
		obtenerDiscapacidades();
		obtenerExperiencia();
		buscarPostulado();
		buildDoc1SelectItem();
		buildDoc2SelectItem();
		buscarOEEyUO();
	}

	private void verificarEsPersona() {

		if (cu.equalsIgnoreCase("CU482"))
			esPersona = true;
		else
			esPersona = false;

	}

	/****
	 * Obtiene la edad de la persona logueada
	 * 
	 * @return
	 */
	public Integer obtenerEdad() {
		Integer anhoActual = (new Date()).getYear() + 1900;
		if (persona.getFechaNacimiento() != null) {
			Integer anhoNac = persona.getFechaNacimiento().getYear() + 1900;
			return anhoActual - anhoNac;
		}
		return null;
	}

	/**
	 * Obtiene los Estudios Realizados por la persona logueada
	 */
	private void obtenerEstudiosRealizados() {
		listaEstudios = new ArrayList<EstudiosRealizados>();
		String sql = "SELECT E.* " + "FROM SELECCION.ESTUDIOS_REALIZADOS E "
				+ "WHERE E.ACTIVO IS TRUE AND E.ID_PERSONA = "
				+ persona.getIdPersona();
		listaEstudios = em.createNativeQuery(sql, EstudiosRealizados.class)
				.getResultList();
	}

	/**
	 * Obtiene los idiomas de la persona logueada
	 */
	private void obtenerIdiomas() {
		listaIdiomas = new ArrayList<IdiomasPersona>();
		String sql = "SELECT I.* " + "FROM SELECCION.IDIOMAS_PERSONA I "
				+ "WHERE I.ACTIVO IS TRUE AND I.ID_PERSONA = "
				+ persona.getIdPersona();
		listaIdiomas = em.createNativeQuery(sql, IdiomasPersona.class)
				.getResultList();
	}
	
	/**
	 * Obtiene las discapacidades de la persona logueada
	 */
	private void obtenerDiscapacidades() {
		discapacidadsList = new ArrayList<PersonaDiscapacidad>();
		discapacidadsList = em.createQuery(
				" select d from PersonaDiscapacidad d where  d.activo = true and d.persona.idPersona= "
						+ persona.getIdPersona()).getResultList();
	}
	

	/**
	 * Obtiene la experiencia laboral de la persona logueada
	 */
	private void obtenerExperiencia() {
		listaExperiencia = new ArrayList<ExperienciaLaboral>();
		String sql = "SELECT L.* " + "FROM SELECCION.EXPERIENCIA_LABORAL L "
				+ "WHERE L.ACTIVO IS TRUE AND L.ID_PERSONA = "
				+ persona.getIdPersona();
		listaExperiencia = em.createNativeQuery(sql, ExperienciaLaboral.class)
				.getResultList();
	}

	/**
	 * Descarga los documentos adjuntados en Estudios realizados
	 * 
	 * @param index
	 */
	public void abrirDocEstudios(int index) {

		EstudiosRealizados e = listaEstudios.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumentos()
				.getIdDocumento(), usuarioLogueado.getIdUsuario());

	}

	public void abrirDocumento1() {
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDoc1,
				usuarioLogueado.getIdUsuario());

	}

	public void abrirDocumento2() {
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDoc2,
				usuarioLogueado.getIdUsuario());

	}

	/**
	 * Descarga los documentos adjuntados en Experiencia Laboral
	 * 
	 * @param index
	 */
	public void abrirDocExperiencia(int index) {

		ExperienciaLaboral e = listaExperiencia.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumentos()
				.getIdDocumento(), usuarioLogueado.getIdUsuario());

	}

	/**
	 * Carga el combo de Tipo de documento 1
	 */
	private void buildDoc1SelectItem() {
		doc1SelecItem = new ArrayList<SelectItem>();
		String sql1 = "SELECT DATOS_ESPECIFICOS.* "
				+ "FROM SELECCION.DATOS_ESPECIFICOS DATOS_ESPECIFICOS "
				+ "JOIN SELECCION.DATOS_GENERALES DATOS_GENERALES "
				+ "ON DATOS_GENERALES.ID_DATOS_GENERALES = DATOS_ESPECIFICOS.ID_DATOS_GENERALES "
				+ "WHERE  DATOS_GENERALES.DESCRIPCION = 'TIPOS DE DOCUMENTOS' "
				+ "AND DATOS_ESPECIFICOS.VALOR_ALF = 'F1_CAP'  "
				+ "AND DATOS_ESPECIFICOS.ACTIVO = TRUE "
				+ "ORDER BY DATOS_ESPECIFICOS.DESCRIPCION";
		List<DatosEspecificos> lista = new ArrayList<DatosEspecificos>();
		lista = em.createNativeQuery(sql1, DatosEspecificos.class)
				.getResultList();
		

		doc1SelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (DatosEspecificos d : lista) {
			doc1SelecItem.add(new SelectItem(d.getIdDatosEspecificos(), d
					.getDescripcion()));
			idTipoDoc1 = d.getIdDatosEspecificos();
		}
	}

	/**
	 * Carga el combo de Tipo de documento 2
	 */
	private void buildDoc2SelectItem() {
		doc2SelecItem = new ArrayList<SelectItem>();
		String sql1 = "SELECT DATOS_ESPECIFICOS.* "
				+ "FROM SELECCION.DATOS_ESPECIFICOS DATOS_ESPECIFICOS "
				+ "JOIN SELECCION.DATOS_GENERALES DATOS_GENERALES "
				+ "ON DATOS_GENERALES.ID_DATOS_GENERALES = DATOS_ESPECIFICOS.ID_DATOS_GENERALES "
				+ "WHERE DATOS_GENERALES.DESCRIPCION = 'TIPOS DE DOCUMENTOS' "
				+ "AND DATOS_ESPECIFICOS.VALOR_ALF = 'F2_CAP' AND "
				+ "DATOS_ESPECIFICOS.ACTIVO = TRUE "
				+ "ORDER BY  DATOS_ESPECIFICOS.DESCRIPCION";
		List<DatosEspecificos> lista = new ArrayList<DatosEspecificos>();
		lista = em.createNativeQuery(sql1, DatosEspecificos.class)
				.getResultList();

		doc2SelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (DatosEspecificos d : lista) {
			doc2SelecItem.add(new SelectItem(d.getIdDatosEspecificos(), d
					.getDescripcion()));
			idTipoDoc2 = d.getIdDatosEspecificos();
		}
	}
	
	private void buscarCapacitacion(){
		String sql = "SELECT *  " +
				"FROM CAPACITACION.CAPACITACIONES CAP " +
				"JOIN CAPACITACION.POSTULACION_CAP POST_CAB " +
				"ON POST_CAB.ID_CAPACITACION = CAP.ID_CAPACITACION " +
				"JOIN SELECCION.PERSONA_POSTULANTE PPOST " +
				"ON PPOST.ID_PERSONA_POSTULANTE = POST_CAB.ID_PERSONA_POSTULANTE " +
				"WHERE PPOST.ID_PERSONA_POSTULANTE = "+idRecibido;
		List<Capacitaciones> cap = new ArrayList<Capacitaciones>();
		cap = em.createNativeQuery(sql, Capacitaciones.class).getResultList();
		if (cap != null && cap.size() > 0)
			capacitaciones = cap.get(0);
	}

	/**
	 * En caso de que la persona logueada ya tenga una postulación, recupera sus
	 * datos
	 */
	private void buscarPostulado() {
		String sql = "";
		if (esPersona)
			sql = "SELECT * FROM " + "CAPACITACION.POSTULACION_CAP P "
					+ "JOIN SEGURIDAD.USUARIO U "
					+ "ON U.ID_PERSONA = P.ID_PERSONA "
					+ "WHERE P.FICHA = TRUE AND P.ACTIVO =  TRUE "
					+ "AND U.ID_PERSONA = " + persona.getIdPersona();
		else
			sql = "SELECT * FROM " + "CAPACITACION.POSTULACION_CAP P "
			+ "JOIN SEGURIDAD.USUARIO U "
			+ "ON U.ID_PERSONA = P.ID_PERSONA "
			+ "WHERE P.FICHA = FALSE AND P.ACTIVO =  TRUE "
			+ "AND U.ID_PERSONA = " + persona.getIdPersona()
			+" AND P.ID_CAPACITACION = "+capacitaciones.getIdCapacitacion();
		List<PostulacionCap> cap = new ArrayList<PostulacionCap>();
		cap = em.createNativeQuery(sql, PostulacionCap.class).getResultList();
		if (cap != null && cap.size() > 0)
			postulacionCap = cap.get(0);
		if (postulacionCap.getIdPostulacion() != null)
			buscarDocumentosAdjuntos();
	}

	/**
	 * Busca los documentos adjuntados al postularse
	 */
	private void buscarDocumentosAdjuntos() {
		String sql = "SELECT adj.*  "
				+ "FROM capacitacion.postulacion_cap_adj adj "
				+ "where adj.activo is true " + "and adj.id_postulacion = "
				+ postulacionCap.getIdPostulacion();
		List<PostulacionCapAdj> listaAdj = new ArrayList<PostulacionCapAdj>();
		listaAdj = em.createNativeQuery(sql, PostulacionCapAdj.class)
				.getResultList();
		//if (listaAdj != null && listaAdj.size() > 1) { removido a partir de la no obligatoriedad del Certificado de Trabajo pedido por gente del INAPP
		if (listaAdj != null) {
			idDoc1 = listaAdj.get(0).getDocumento().getIdDocumento();
			idTipoDoc1 = listaAdj.get(0).getDocumento().getDatosEspecificos()
					.getIdDatosEspecificos();
			nombreDoc1 = listaAdj.get(0).getDocumento().getNombreFisicoDoc();
			
			if(listaAdj.size() > 1){
				idDoc2 = listaAdj.get(1).getDocumento().getIdDocumento();
				idTipoDoc2 = listaAdj.get(1).getDocumento().getDatosEspecificos()
						.getIdDatosEspecificos();
				nombreDoc2 = listaAdj.get(1).getDocumento().getNombreFisicoDoc();
			}

		}
	}

	public void abrirDoc1() {

		AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDoc1, usuarioLogueado
				.getIdUsuario());
	}
	
	public void abrirDoc2() {

		AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDoc2, usuarioLogueado
				.getIdUsuario());
	}
	/**
	 * Recupera en pantalla última postulación cargada por la persona logueada
	 * **/
	public void copiarFicha() {
		String sql = "SELECT DISTINCT(P.*) FROM "
				+ "CAPACITACION.POSTULACION_CAP P "
				+ "JOIN SEGURIDAD.USUARIO U "
				+ "ON U.ID_PERSONA = P.ID_PERSONA " + "WHERE P.ACTIVO =  TRUE "
				+ "AND U.ID_PERSONA = " + persona.getIdPersona()
				+ " ORDER BY P.FECHA_FICHA DESC";
		List<PostulacionCap> cap = new ArrayList<PostulacionCap>();
		cap = em.createNativeQuery(sql, PostulacionCap.class).getResultList();
		if (cap != null && cap.size() > 0) {
			postulacionCap = cap.get(0);
			fichaCopiada = true;
		}
	}
	
	public void deshabilitarEdicionOEEyUO() {
		String sql = "SELECT * FROM GENERAL.EMPLEADO_PUESTO WHERE EMPLEADO_PUESTO.ID_PERSONA = "
				+ persona.getIdPersona() + " AND ACTIVO = TRUE AND ACTUAL = TRUE";
		List<EmpleadoPuesto> emp = new ArrayList<EmpleadoPuesto>();
		emp = em.createNativeQuery(sql, EmpleadoPuesto.class).getResultList();
		puedeEditarOEEyUO = true;
		if (emp != null && emp.size() > 0) {
			empleadoPuesto = emp.get(0);
			plantaCargoDet = empleadoPuesto.getPlantaCargoDet();
			confUoDet = plantaCargoDet.getConfiguracionUoDet();
			confUoCab = confUoDet.getConfiguracionUoCab();
			uo = confUoDet.getDenominacionUnidad();
			oee = confUoCab.getDenominacionUnidad();
			puedeEditarOEEyUO = false;
		}
	}
	
	public void buscarOEEyUO() {
		String sql = "";
		if (esPersona)
			sql = "SELECT * FROM " + "CAPACITACION.POSTULACION_CAP P "
					+ "JOIN SEGURIDAD.USUARIO U "
					+ "ON U.ID_PERSONA = P.ID_PERSONA "
					+ "WHERE P.FICHA = TRUE AND P.ACTIVO =  TRUE "
					+ "AND U.ID_PERSONA = " + persona.getIdPersona();
		else
			sql = "SELECT * FROM " + "CAPACITACION.POSTULACION_CAP P "
			+ "JOIN SEGURIDAD.USUARIO U "
			+ "ON U.ID_PERSONA = P.ID_PERSONA "
			+ "WHERE P.FICHA = FALSE AND P.ACTIVO =  TRUE "
			+ "AND U.ID_PERSONA = " + persona.getIdPersona()
			+" AND P.ID_CAPACITACION = "+capacitaciones.getIdCapacitacion();
		List<PostulacionCap> cap = new ArrayList<PostulacionCap>();
		cap = em.createNativeQuery(sql, PostulacionCap.class).getResultList();
		if (cap != null && cap.size() > 0)
			postulacionCap = cap.get(0);
		if (postulacionCap.getIdPostulacion() != null){
			oee = postulacionCap.getOee();
			uo = postulacionCap.getUnidadOrganizativa();
		}
	}

	/**
	 * Verifica que todos los campos obligatorios hayan sido cargados
	 * 
	 * @param op
	 * @return
	 */
	private Boolean checkDatosObligatorios(String op) {
		if (op.equalsIgnoreCase("save")) {
			/*if (uploadedFile == null || uploadedFile2 == null) {
				statusMessages.add(Severity.WARN,
						"Debe adjuntar los documentos obligatorios del paso 1");
				return false;
			}
			if (contentType == null || contentType2 == null || fileName == null
					|| fileName2 == null || uploadedFile == null
					|| uploadedFile2 == null) {
				statusMessages.add(Severity.WARN,
						"Debe adjuntar los documentos obligatorios del paso 1");
				return false;
			}*/
			if (uploadedFile == null) {
				statusMessages.add(Severity.WARN,
						"Debe adjuntar el Aval Institucional en el paso 1");
				return false;
			}
			if (contentType == null || fileName == null || uploadedFile == null) {
				statusMessages.add(Severity.WARN,
						"Debe adjuntar el Aval Institucional en el paso 1");
				return false;
			}
		}
		if (postulacionCap.getNombreAp() == null
				|| postulacionCap.getNombreAp().trim().isEmpty()) {
			statusMessages.add(Severity.WARN,
					"Debe completar el campo Nombres y Apellidos del paso 2");
			return false;
		}
		if (postulacionCap.getCargo() == null
				|| postulacionCap.getCargo().trim().isEmpty()) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Cargo en la Institución del paso 2");
			return false;
		}
		if (postulacionCap.getDireccion() == null
				|| postulacionCap.getDireccion().trim().isEmpty()) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Dirección en la Institución del paso 2");
			return false;
		}
		if (postulacionCap.getEMail() == null
				|| postulacionCap.getEMail().trim().isEmpty()) {
			statusMessages.add(Severity.WARN,
					"Debe completar el campo Correo Electrónico del paso 2");
			return false;
		}
		if (postulacionCap.getTelefono() == null
				|| postulacionCap.getTelefono().trim().isEmpty()) {
			statusMessages.add(Severity.WARN,
					"Debe completar el campo Teléfono del paso 2");
			return false;
		}
		if (postulacionCap.getFuncionGral() == null
				|| postulacionCap.getFuncionGral().trim().isEmpty()) {
			statusMessages.add(Severity.WARN,
					"Debe completar el campo Funciones Generales del paso 2");
			return false;
		}
		if (postulacionCap.getFuncionEspecif() == null
				|| postulacionCap.getFuncionEspecif().trim().isEmpty()) {
			statusMessages.add(Severity.WARN,
					"Debe completar el campo Funciones Específicas del paso 2");
			return false;
		}
		if (postulacionCap.getPotencialBenef() == null
				|| postulacionCap.getPotencialBenef().trim().isEmpty()) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Potenciales Beneficios del paso 2");
			return false;
		}

		return true;
	}

	/**
	 * Realiza la operacion de guardar/actualizar de acuerdo a la situación
	 * 
	 * @return
	 */
	public String guardar() throws SQLException {
		String operacion = "";
		if (postulacionCap.getIdPostulacion() != null && !fichaCopiada)
			operacion = "update";
		if (postulacionCap.getIdPostulacion() == null
				|| (postulacionCap.getIdPostulacion() != null && fichaCopiada)) {

			operacion = "save";
		}
		if (!checkDatosObligatorios(operacion))
			return null;
		if (!validacionDocumentos())
			return null;

		try {
			if ((postulacionCap.getIdPostulacion() != null && fichaCopiada)) {
				PostulacionCap cap = new PostulacionCap();
				// cap = postulacionCap;
				// postulacionCap = new PostulacionCap();
				cap.setCargo(postulacionCap.getCargo());
				cap.setCursoRealiza(postulacionCap.getCursoRealiza());
				cap.setDiasAsisAct(postulacionCap.getDiasAsisAct());
				cap.setDiasAsisCur(postulacionCap.getDiasAsisCur());
				cap.setDireccion(postulacionCap.getDireccion());
				cap.setEMail(postulacionCap.getEMail());
				cap.setFax(postulacionCap.getFax());
				cap.setFuncionEspecif(postulacionCap.getFuncionEspecif());
				cap.setFuncionGral(postulacionCap.getFuncionGral());
				cap.setHorarioAct(postulacionCap.getHorarioAct());
				cap.setHorarioCur(postulacionCap.getHorarioCur());
				cap.setNombreAp(postulacionCap.getNombreAp());
				cap.setOtraAct(postulacionCap.getOtraAct());
				cap.setPotencialBenef(postulacionCap.getPotencialBenef());
				cap.setProyectoActi(postulacionCap.getProyectoActi());
				cap.setProyectoCord(postulacionCap.getProyectoCord());
				cap.setProyectoPart(postulacionCap.getProyectoPart());
				cap.setTelefono(postulacionCap.getTelefono());
				cap.setOee(oee);
				cap.setUnidadOrganizativa(uo);
				postulacionCap = new PostulacionCap();
				postulacionCap = cap;
			}
			postulacionCap.setFicha(true);
			postulacionCap.setActivo(true);
			postulacionCap.setPersona(persona);
			postulacionCap.setOee(oee);
			postulacionCap.setUnidadOrganizativa(uo);
			postulacionCapHome.clearInstance();
			if (operacion.equalsIgnoreCase("save")) {

				postulacionCapHome.setInstance(postulacionCap);
				postulacionCapHome.persist();
				documento("todos");
				guardarAdjunto("todos");
			}
			if (operacion.equalsIgnoreCase("update")) {

				postulacionCapHome.setInstance(postulacionCap);
				postulacionCapHome.update();
				verificaNuevoDoc();

			}
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		}catch (JDBCException jdbce) {
			jdbce.getSQLException().getNextException().printStackTrace();
			return null;
		}catch (Exception e) {
			return null;
		}
		

	}

	/**
	 * Guarda los datos en la tabla postulacion_cap_adj
	 * 
	 * @param actual
	 */
	private void guardarAdjunto(String actual) {
		if (actual.equalsIgnoreCase("doc1") || actual.equalsIgnoreCase("todos")) {
			PostulacionCapAdj adj1 = new PostulacionCapAdj();
			adj1.setActivo(true);
			adj1.setDocumento(em.find(Documentos.class, idDoc1));
			adj1.setFechaAlta(new Date());
			adj1.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			adj1.setPostulacionCap(postulacionCapHome.getInstance());
			em.persist(adj1);
		}
		if (uploadedFile2 != null) {
			if (actual.equalsIgnoreCase("doc2") || actual.equalsIgnoreCase("todos")) {
				PostulacionCapAdj adj2 = new PostulacionCapAdj();
				adj2.setActivo(true);
				adj2.setDocumento(em.find(Documentos.class, idDoc2));
				adj2.setFechaAlta(new Date());
				adj2.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				adj2.setPostulacionCap(postulacionCapHome.getInstance());
				em.persist(adj2);
			}
		}
	}

	/**
	 * Verifica si el documento es nuevo o no
	 * 
	 * @throws NamingException
	 */
	private void verificaNuevoDoc() throws NamingException {
		if (uploadedFile != null) {
			inactivarDocAdj(idDoc1);
			documento("doc1");
			guardarAdjunto("doc1");
		}
		if (uploadedFile2 != null) {
			inactivarDocAdj(idDoc2);
			documento("doc2");
			guardarAdjunto("doc2");
		}
	}

	/**
	 * Inactiva los registros en las tablas postulacion_cap_adj y documentos
	 * 
	 * @param id
	 */
	private void inactivarDocAdj(Long id) {
		String sql = "SELECT adj.*  "
				+ "FROM capacitacion.postulacion_cap_adj adj "
				+ "where adj.activo is true " + "and adj.id_postulacion = "
				+ postulacionCap.getIdPostulacion()
				+ " and adj.id_documento = " + id;
		try {
			PostulacionCapAdj adj = new PostulacionCapAdj();
			adj = (PostulacionCapAdj) em.createNativeQuery(sql,
					PostulacionCapAdj.class).getSingleResult();
			if (adj != null) {
				adj.setActivo(false);
				em.merge(adj);
				Documentos docActual = adj.getDocumento();
				docActual.setActivo(false);
				em.merge(docActual);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void crearUploadItem1(String fileName, int fileSize,
			String contentType, byte[] file) {
		item1 = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc1 = item1.getFileName();
	}

	private void crearUploadItem2(String fileName, int fileSize,
			String contentType, byte[] file) {
		item2 = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc2 = item2.getFileName();
	}

	/**
	 * Valida el documento a ser adjuntado
	 * 
	 * @return
	 */
	private Boolean validacionDocumentos() {
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType)) {
				crearUploadItem1(fileName, uploadedFile.length, contentType,
						uploadedFile);
				String nomfinal1 = "";
				nomfinal1 = item1.getFileName();
				String extension = nomfinal1.substring(nomfinal1
						.lastIndexOf("."));
				List<TipoArchivo> tam = em.createQuery(
						"select t from TipoArchivo t "
								+ " where lower(t.extension) like '"
								+ extension.toLowerCase() + "'")
						.getResultList();
				if (!tam.isEmpty() && tam.get(0).getUnidMedidaByte() != null) {
					if (item1.getFileSize() > tam.get(0).getUnidMedidaByte()
							.intValue()) {
						statusMessages.add(Severity.ERROR,
								"El archivo supera el tamaño máximo permitido. Límite "
										+ tam.get(0).getTamanho()
										+ tam.get(0).getUnidMedida()
										+ ", verifique");
						return false;
					}
				}
			} else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return false;
			}

		}
		if (uploadedFile2 != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType2)) {
				crearUploadItem2(fileName2, uploadedFile2.length, contentType2,
						uploadedFile2);
				String nomfinal2 = "";

				nomfinal2 = item2.getFileName();
				String extension2 = nomfinal2.substring(nomfinal2
						.lastIndexOf("."));
				List<TipoArchivo> tama = em.createQuery(
						"select t from TipoArchivo t "
								+ " where lower(t.extension) like '"
								+ extension2.toLowerCase() + "'")
						.getResultList();
				if (!tama.isEmpty() && tama.get(0).getUnidMedidaByte() != null) {
					if (item2.getFileSize() > tama.get(0).getUnidMedidaByte()
							.intValue()) {
						statusMessages.add(Severity.ERROR,
								"El archivo supera el tamaño máximo permitido. Límite "
										+ tama.get(0).getTamanho()
										+ tama.get(0).getUnidMedida()
										+ ", verifique");
						return false;
					}
				}
			} else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return false;
			}

		}

		return true;
		/**
		 * FIN
		 * */

	}

	/**
	 * Guarda el documento adjuntado en la tabla documentos
	 * 
	 * @param actual
	 * @throws NamingException
	 */
	private void documento(String actual) throws NamingException {
		nombrePantalla = "fichaInscripcion";
		String direccionFisica = "";
		String separador = File.separator;

		direccionFisica = separador + "SICCA" + separador + "USUARIO_PORTAL"
				+ separador + persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona();
		if (actual.equals("doc1") || actual.equals("todos"))
			idDoc1 = AdmDocAdjuntoFormController.guardarDirecto(item1,
					direccionFisica, nombrePantalla, idTipoDoc1,
					usuarioLogueado.getIdUsuario(), "PostulacionCap");
		if (uploadedFile2 != null) {
			if (actual.equals("doc2") || actual.equals("todos"))
				idDoc2 = AdmDocAdjuntoFormController.guardarDirecto(item2,direccionFisica, nombrePantalla, idTipoDoc2, usuarioLogueado.getIdUsuario(), "PostulacionCap");
		}

	}

	public void cambiarPaos(String val) {
		paso = val;
	}

	public void changeNameDoc1() {
		nombreDoc1 = null;
	}

	public void changeNameDoc2() {
		nombreDoc2 = null;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getPaso() {
		return paso;
	}

	public void setPaso(String paso) {
		this.paso = paso;
	}

	public List<EstudiosRealizados> getListaEstudios() {
		return listaEstudios;
	}

	public void setListaEstudios(List<EstudiosRealizados> listaEstudios) {
		this.listaEstudios = listaEstudios;
	}

	public List<IdiomasPersona> getListaIdiomas() {
		return listaIdiomas;
	}

	public void setDiscapacidadsList(List<PersonaDiscapacidad> discapacidadsList) {
		this.discapacidadsList = discapacidadsList;
	}
	
	public List<PersonaDiscapacidad> getDiscapacidadsList() {
		return discapacidadsList;
	}

	public void setListaIdiomas(List<IdiomasPersona> listaIdiomas) {
		this.listaIdiomas = listaIdiomas;
	}

	public List<ExperienciaLaboral> getListaExperiencia() {
		return listaExperiencia;
	}

	public void setListaExperiencia(List<ExperienciaLaboral> listaExperiencia) {
		this.listaExperiencia = listaExperiencia;
	}

	public List<SelectItem> getDoc1SelecItem() {
		return doc1SelecItem;
	}

	public void setDoc1SelecItem(List<SelectItem> doc1SelecItem) {
		this.doc1SelecItem = doc1SelecItem;
	}

	public List<SelectItem> getDoc2SelecItem() {
		return doc2SelecItem;
	}

	public void setDoc2SelecItem(List<SelectItem> doc2SelecItem) {
		this.doc2SelecItem = doc2SelecItem;
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

	public Long getIdTipoDoc1() {
		return idTipoDoc1;
	}

	public void setIdTipoDoc1(Long idTipoDoc1) {
		this.idTipoDoc1 = idTipoDoc1;
	}

	public Long getIdTipoDoc2() {
		return idTipoDoc2;
	}

	public void setIdTipoDoc2(Long idTipoDoc2) {
		this.idTipoDoc2 = idTipoDoc2;
	}

	public String getNombreDoc1() {
		return nombreDoc1;
	}

	public void setNombreDoc1(String nombreDoc1) {
		this.nombreDoc1 = nombreDoc1;
	}

	public String getNombreDoc2() {
		return nombreDoc2;
	}

	public void setNombreDoc2(String nombreDoc2) {
		this.nombreDoc2 = nombreDoc2;
	}

	public byte[] getUploadedFile2() {
		return uploadedFile2;
	}

	public void setUploadedFile2(byte[] uploadedFile2) {
		this.uploadedFile2 = uploadedFile2;
	}

	public String getContentType2() {
		return contentType2;
	}

	public void setContentType2(String contentType2) {
		this.contentType2 = contentType2;
	}

	public String getFileName2() {
		return fileName2;
	}

	public void setFileName2(String fileName2) {
		this.fileName2 = fileName2;
	}

	public PostulacionCap getPostulacionCap() {
		return postulacionCap;
	}

	public void setPostulacionCap(PostulacionCap postulacionCap) {
		this.postulacionCap = postulacionCap;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public Long getIdDoc1() {
		return idDoc1;
	}

	public void setIdDoc1(Long idDoc1) {
		this.idDoc1 = idDoc1;
	}

	public Long getIdDoc2() {
		return idDoc2;
	}

	public void setIdDoc2(Long idDoc2) {
		this.idDoc2 = idDoc2;
	}

	public UploadItem getItem1() {
		return item1;
	}

	public void setItem1(UploadItem item1) {
		this.item1 = item1;
	}

	public UploadItem getItem2() {
		return item2;
	}

	public void setItem2(UploadItem item2) {
		this.item2 = item2;
	}

	public Boolean getFichaCopiada() {
		return fichaCopiada;
	}

	public void setFichaCopiada(Boolean fichaCopiada) {
		this.fichaCopiada = fichaCopiada;
	}

	public Long getIdRecibido() {
		return idRecibido;
	}

	public void setIdRecibido(Long idRecibido) {
		this.idRecibido = idRecibido;
	}

	public String getCu() {
		return cu;
	}

	public void setCu(String cu) {
		this.cu = cu;
	}

	public Boolean getEsPersona() {
		return esPersona;
	}

	public void setEsPersona(Boolean esPersona) {
		this.esPersona = esPersona;
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
	
	public Boolean getPuedeEditarOEEyUO() {
		return puedeEditarOEEyUO;
	}

	public void setPuedeEditarOEEyUO(Boolean puedeEditarOEEyUO) {
		this.puedeEditarOEEyUO = puedeEditarOEEyUO;
	}
	
	public String getOee() {
		return oee;
	}

	public void setOee(String oee) {
		this.oee = oee;
	}
	
	public String getUo() {
		return uo;
	}

	public void setUo(String uo) {
		this.uo = uo;
	}
	
	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}
	
}
