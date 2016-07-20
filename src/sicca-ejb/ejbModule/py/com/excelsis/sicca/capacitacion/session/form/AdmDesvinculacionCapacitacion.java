package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
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

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EvaluacionPart;
import py.com.excelsis.sicca.entity.ListaDet;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admDesvinculacionCapacitacion")
public class AdmDesvinculacionCapacitacion {

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
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Long idListaDet;
	private Long idMotivo;
	private Long idTipoDoc;
	private Long idDoc;
	private String nombreApellido;
	private String cu;
	private String obs;
	private Date fechaDesvinculacion;
	private ListaDet listaDet = new ListaDet();
	private Capacitaciones capacitaciones = new Capacitaciones();
	private Persona persona = new Persona();
	private Documentos documentos = new Documentos();

	private String fileName;
	private byte[] uploadedFile;
	private UploadItem item;
	private String contentType;
	private String nombreDoc;

	private List<SelectItem> motivoSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> tipoDocSelecItem = new ArrayList<SelectItem>();

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idListaDet != null) {
			if (!sufc.validarInput(idListaDet.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			listaDet = em.find(ListaDet.class, idListaDet);
			Long id = listaDet.getPostulacionCap().getCapacitacion()
					.getIdCapacitacion();
			capacitaciones = em.find(Capacitaciones.class, id);
			Long idPer = listaDet.getPostulacionCap().getPersona()
					.getIdPersona();
			persona = em.find(Persona.class, idPer);
			nombreApellido = persona.getNombres() + " "
					+ persona.getApellidos();
			seguridadUtilFormController.validarCapacitacionPerteneceUO(id);
			cargarDatosNivel();
			comboMotivoDesvinculacion();
			comboTipoDoc();
			limpiarCampos();
		}
	}
	
	private void limpiarCampos(){
		idMotivo = null;
		fechaDesvinculacion = null;
		idTipoDoc = null;
		fileName = null;
		uploadedFile = null;
		item = null;
		contentType = null;
		nombreDoc = null;
		obs = null;
	}

	public void initEdit() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idListaDet != null) {
			if (!sufc.validarInput(idListaDet.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			listaDet = em.find(ListaDet.class, idListaDet);
			Long id = listaDet.getPostulacionCap().getCapacitacion()
					.getIdCapacitacion();
			capacitaciones = em.find(Capacitaciones.class, id);
			Long idPer = listaDet.getPostulacionCap().getPersona()
					.getIdPersona();
			persona = em.find(Persona.class, idPer);
			nombreApellido = persona.getNombres() + " "
					+ persona.getApellidos();

			cargarDatosNivel();
			comboMotivoDesvinculacion();
			comboTipoDoc();
			if (listaDet.getDatosEspecificosDesv() != null)
				idMotivo = listaDet.getDatosEspecificosDesv()
						.getIdDatosEspecificos();
			fechaDesvinculacion = listaDet.getFechaDesvinculacion();
			documentos = new Documentos();
			documentos = listaDet.getDocumento();
			if (documentos != null) {
				idDoc = documentos.getIdDocumento();
				idTipoDoc = documentos.getDatosEspecificos()
						.getIdDatosEspecificos();
			}
		}
	}

	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			if (capacitaciones.getConfiguracionUoDet() != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones
						.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
	}

	public void cargarCabecera() {

		Long idOee = capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo();
		ConfiguracionUoCab uoCab = new ConfiguracionUoCab();
		uoCab = em.find(ConfiguracionUoCab.class, idOee);
		Long idEnt = uoCab.getEntidad().getIdEntidad();
		Entidad ent = new Entidad();
		ent = em.find(Entidad.class, idEnt);
		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil
				.getIdSinNivelEntidad(ent.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(ent.getSinEntidad()
				.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(uoCab.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

	private void comboMotivoDesvinculacion() {
		String sql = "SELECT DE.* "
				+ "FROM SELECCION.DATOS_ESPECIFICOS DE "
				+ "JOIN SELECCION.DATOS_GENERALES DG ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
				+ "WHERE DG.DESCRIPCION = 'DESVINCULACION CAPACITACION' "
				+ "AND DE.ACTIVO = TRUE ORDER BY DE.DESCRIPCION";
		List<DatosEspecificos> listaDatos = em.createNativeQuery(sql,
				DatosEspecificos.class).getResultList();
		motivoSelecItem = new ArrayList<SelectItem>();

		motivoSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (DatosEspecificos d : listaDatos)
			motivoSelecItem.add(new SelectItem(d.getIdDatosEspecificos(), d
					.getDescripcion()));

	}

	private void comboTipoDoc() {
		String sql = "SELECT DATOS_ESPECIFICOS.* "
				+ "FROM SELECCION.DATOS_ESPECIFICOS DATOS_ESPECIFICOS "
				+ "JOIN SELECCION.DATOS_GENERALES DATOS_GENERALES ON "
				+ "DATOS_GENERALES.ID_DATOS_GENERALES = DATOS_ESPECIFICOS.ID_DATOS_GENERALES "
				+ "WHERE DATOS_GENERALES.DESCRIPCION = 'TIPOS DE DOCUMENTOS' "
				+ "AND DATOS_ESPECIFICOS.VALOR_ALF = 'D_CAP' "
				+ "AND DATOS_ESPECIFICOS.ACTIVO = TRUE "
				+ "ORDER BY  DATOS_ESPECIFICOS.DESCRIPCION";
		List<DatosEspecificos> listaDatos = em.createNativeQuery(sql,
				DatosEspecificos.class).getResultList();
		tipoDocSelecItem = new ArrayList<SelectItem>();

		tipoDocSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (DatosEspecificos d : listaDatos)
			tipoDocSelecItem.add(new SelectItem(d.getIdDatosEspecificos(), d
					.getDescripcion()));

	}

	public void changeNameDoc() {
		nombreDoc = null;
	}

	private Boolean checkObligatorios() {

		if (persona.getPaisByIdPaisExpedicionDoc() == null) {
			statusMessages
					.add(Severity.ERROR,
							"El campo País Exp. Doc. no puede ser nulo para realizar esta acción");
			return false;
		}
		if (persona.getDocumentoIdentidad() == null) {
			statusMessages.add(Severity.ERROR,
					"El campo C.I no puede ser nulo para realizar esta acción");
			return false;
		}
		if (idMotivo == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe ingresar el Motivo de Desvinculación para realizar esta acción");
			return false;
		}
		if (fechaDesvinculacion == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe ingresar la Fecha de Desvinculación para realizar esta acción");
			return false;
		}
		return true;
	}

	public String guardar() {
		if (!checkObligatorios())
			return null;
		if (!validacionDocumentos())
			return null;
		try {
			documento();
			listaDet.setTipo("D");
			listaDet.setDatosEspecificosDesv(em.find(DatosEspecificos.class,
					idMotivo));
			listaDet.setFechaDesvinculacion(fechaDesvinculacion);
			if (obs != null && !obs.trim().isEmpty())
				listaDet.setObservacion(obs);
			listaDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			listaDet.setFechaMod(new Date());
			listaDet.setFechaDesv(new Date());
			listaDet.setUsuDesv(usuarioLogueado.getCodigoUsuario());
			if (idDoc != null)
				listaDet.setDocumento(em.find(Documentos.class, idDoc));
			em.merge(listaDet);
			PostulacionCap postulacionCap = new PostulacionCap();
			postulacionCap = listaDet.getPostulacionCap();
			postulacionCap.setActivo(false);
			postulacionCap
					.setUsuCancelacion(usuarioLogueado.getCodigoUsuario());
			postulacionCap.setFechaCancelacion(new Date());
			if (obs != null && !obs.trim().isEmpty())
				postulacionCap.setObsCancelacion(obs);
			em.merge(postulacionCap);
			if (cu.equals("CU490")) {
				EvaluacionPart evaluacionPart = new EvaluacionPart();
				evaluacionPart = buscarEvaluacionPart(postulacionCap
						.getIdPostulacion());
				if (evaluacionPart != null) {
					evaluacionPart.setActivo(false);
					evaluacionPart
							.setUsuMod(usuarioLogueado.getCodigoUsuario());
					evaluacionPart.setFechaMod(new Date());
					em.merge(evaluacionPart);
				}
			}
			em.flush();

		} catch (Exception e) {
			return null;
		}
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return "ok";
	}

	private EvaluacionPart buscarEvaluacionPart(Long id) {
		String sql = "SELECT P.* FROM CAPACITACION.EVALUACION_PART P "
				+ "WHERE P.ID_POSTULACION = " + id;
		List<EvaluacionPart> listaPart = em.createNativeQuery(sql,
				EvaluacionPart.class).getResultList();
		if (listaPart != null && listaPart.size() > 0)
			return listaPart.get(0);
		return null;
	}

	private Boolean validacionDocumentos() {
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType)) {
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
				String nomfinal1 = "";
				nomfinal1 = item.getFileName();
				String extension = nomfinal1.substring(nomfinal1
						.lastIndexOf("."));
				List<TipoArchivo> tam = em.createQuery(
						"select t from TipoArchivo t "
								+ " where lower(t.extension) like '"
								+ extension.toLowerCase() + "'")
						.getResultList();
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
			} else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return false;
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

	private void documento() throws NamingException {
		String nombrePantalla = "admDesvinculacionCapacitacion";
		String direccionFisica = "";
		String separador = File.separator;

		direccionFisica = separador + "SICCA" + separador + "USUARIO_PORTAL"
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona();

		idDoc = AdmDocAdjuntoFormController.guardarDirecto(item,
				direccionFisica, nombrePantalla, idTipoDoc,
				usuarioLogueado.getIdUsuario(), "PostulacionCap");

	}

	public void abrirDoc() {

		AdmDocAdjuntoFormController.abrirDocumentoFromCU(
				documentos.getIdDocumento(), usuarioLogueado.getIdUsuario());
	}

	public Long getIdListaDet() {
		return idListaDet;
	}

	public void setIdListaDet(Long idListaDet) {
		this.idListaDet = idListaDet;
	}

	public ListaDet getListaDet() {
		return listaDet;
	}

	public void setListaDet(ListaDet listaDet) {
		this.listaDet = listaDet;
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

	public List<SelectItem> getMotivoSelecItem() {
		return motivoSelecItem;
	}

	public void setMotivoSelecItem(List<SelectItem> motivoSelecItem) {
		this.motivoSelecItem = motivoSelecItem;
	}

	public String getNombreApellido() {
		return nombreApellido;
	}

	public void setNombreApellido(String nombreApellido) {
		this.nombreApellido = nombreApellido;
	}

	public Long getIdMotivo() {
		return idMotivo;
	}

	public void setIdMotivo(Long idMotivo) {
		this.idMotivo = idMotivo;
	}

	public Date getFechaDesvinculacion() {
		return fechaDesvinculacion;
	}

	public void setFechaDesvinculacion(Date fechaDesvinculacion) {
		this.fechaDesvinculacion = fechaDesvinculacion;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public List<SelectItem> getTipoDocSelecItem() {
		return tipoDocSelecItem;
	}

	public void setTipoDocSelecItem(List<SelectItem> tipoDocSelecItem) {
		this.tipoDocSelecItem = tipoDocSelecItem;
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

	public UploadItem getItem() {
		return item;
	}

	public void setItem(UploadItem item) {
		this.item = item;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public String getCu() {
		return cu;
	}

	public void setCu(String cu) {
		this.cu = cu;
	}

	public Long getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}

}
