package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PostulacionAdjuntos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.TiposDatos;

@Scope(ScopeType.CONVERSATION)
@Name("depoPostulaFC")
public class DepoPostulaFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	private List<Persona> lPostulantes;
	private List<Concurso> lConcursos;
	private List<Postulacion> lGrupos;
	private String ci;
	private String nombre;
	private String apellido;
	private Long idPaisEpeDoc;
	private Long idPersonaSele;
	private byte[] uFile = null;
	private String cType = null;
	private String fName = null;
	private String observacion;
	private Long idPostulaSelected;
	private Boolean mostrarModal = false;
	private Long idConcursoSel;
	private Boolean deshabilitarTodo = false;

	public void init() throws Exception {
		if (nivelEntidadOeeUtil == null) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		}
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		if (!controlPaso2()) {
			deshabilitarTodo = true;
		}

	}

	public void limpiar() {
		ci = null;
		nombre = null;
		apellido = null;
		idPaisEpeDoc = null;
		idPersonaSele = null;
		idPostulaSelected = null;
		lConcursos = null;
		lGrupos = null;
		lPostulantes = null;
		idConcursoSel = null;
	}

	private Boolean controlPaso2() {
		Query q =
			em.createQuery("select Concurso from Concurso Concurso "
				+ " where Concurso.configuracionUoCab.idConfiguracionUo  = :idUo "
				+ " and Concurso.activo is true");
		q.setParameter("idUo", usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		if (q.getResultList().size() > 0) {
			return true;
		}
		statusMessages.add(Severity.ERROR, "No tiene postulaciones a este OEE");
		return false;
	}

	private Boolean precondCargarPostulantes() throws Exception {
		if ((ci == null || ci.isEmpty()) && (nombre == null || nombre.isEmpty())
			&& (apellido == null || apellido.isEmpty()) && idPaisEpeDoc == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar por lo menos uno de los parámetros del Panel de filtros");
			return false;
		}
		if (!(ci == null || ci.isEmpty())) {
			if (!seguridadUtilFormController.validarInput(ci, TiposDatos.STRING.getValor(), 30)) {
				return false;
			}
		}
		if (!(nombre == null || nombre.isEmpty())) {
			if (!seguridadUtilFormController.validarInput(nombre, TiposDatos.STRING.getValor(), 100)) {
				return false;
			}
		}
		if (!(apellido == null || apellido.isEmpty())) {
			if (!seguridadUtilFormController.validarInput(apellido, TiposDatos.STRING.getValor(), 80)) {
				return false;
			}
		}
		if (!(idPaisEpeDoc == null)) {
			if (!seguridadUtilFormController.validarInput(idPaisEpeDoc.toString(), TiposDatos.LONG.getValor(), null)) {
				return false;
			}
		}
		return true;
	}

	public void cargarPostulantes() throws Exception {
		idPersonaSele = null;
		idPostulaSelected = null;
		mostrarModal = false;
		if (lConcursos != null)
			lConcursos.clear();
		if (lGrupos != null)
			lGrupos.clear();
		if (!precondCargarPostulantes())
			return;
		String sql = "select Persona from Persona Persona ";
		String elWhere = " where Persona.activo is true ";
		if (!(ci == null || ci.isEmpty())) {
			elWhere += " and Persona.documentoIdentidad = :ci ";
		}
		if (!(nombre == null || nombre.isEmpty())) {
			elWhere += " and Persona.nombres = :nombre ";
		}
		if (!(apellido == null || apellido.isEmpty())) {
			elWhere += " and Persona.apellidos= :apellido ";
		}
		if (!(idPaisEpeDoc == null)) {
			elWhere += " and Persona.paisByIdPaisExpedicionDoc.idPais = :idPais ";
		}
		sql += elWhere;
		Query q = em.createQuery(sql);
		if (!(ci == null || ci.isEmpty())) {
			q.setParameter("ci", "%"+ci+"%");
		}
		if (!(nombre == null || nombre.isEmpty())) {
			q.setParameter("nombre","%"+nombre+"%");
		}
		if (!(apellido == null || apellido.isEmpty())) {
			q.setParameter("apellido","%"+apellido+"%");
		}
		if (!(idPaisEpeDoc == null)) {
			q.setParameter("idPais", idPaisEpeDoc);
		}
		lPostulantes = q.getResultList();
	}

	/**
	 * Trae los concursos de una persona cuya configuracion uo sea igual a la del usuario logueado
	 */
	public void cargarConcursos(Long idPersona) {
		mostrarModal = false;
		if (idPersona == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un Postulante para ver sus Concursos");
			return;
		}
		idPersonaSele = idPersona;
		Query q =
			em.createQuery("select DISTINCT (Postulacion.concursoPuestoAgr.concurso) from Postulacion Postulacion "
				+ " where Postulacion.activo is true and"
				+ " Postulacion.personaPostulante.persona.idPersona = :idPersona "
				+ " and Postulacion.concursoPuestoAgr.concurso.configuracionUoCab.idConfiguracionUo = :idUo ");
		q.setParameter("idPersona", idPersonaSele);
		q.setParameter("idUo", usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		lConcursos = q.getResultList();
	}

	public void cargarGrupos(Long idConcurso) {
		mostrarModal = false;
		if (idPersonaSele == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un Postulante para ver sus Concursos");
		}
		idConcursoSel = idConcurso;
		Query q =
			em.createQuery("select DISTINCT Postulacion from Postulacion Postulacion "
				+ " where Postulacion.activo is true and Postulacion.usuCancelacion is null and "
				+ " (Postulacion.persona.idPersona = :idPersona or Postulacion.personaPostulante.persona.idPersona = :idPersona)"
				+ " and Postulacion.concursoPuestoAgr.concurso.idConcurso = :idConcurso "
				+ " and Postulacion.concursoPuestoAgr.concurso.configuracionUoCab.idConfiguracionUo = :idUo ");
		q.setParameter("idConcurso", idConcurso);
		q.setParameter("idPersona", idPersonaSele);
		q.setParameter("idUo", usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		lGrupos = q.getResultList();
	}

	private Boolean precondCancelacionPostula() {
		if (idPostulaSelected == null)
			return false;
		if (uFile == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar un documento adjunto");
			return false;
		}
		if (observacion != null && !observacion.isEmpty()) {
			if (observacion.length() > 1000) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
					+ ": Observación(1000)");
				return false;
			}
		}
		return true;
	}

	public void cancelarPostulacion() {
		if (!precondCancelacionPostula())
			return;
		try {
			Postulacion postulacion = em.find(Postulacion.class, idPostulaSelected);
			DatosEspecificos de =
				seleccionUtilFormController.traerDatosEspecificos("POR PRESENTACION DE DOCUMENTOS");
			postulacion.setDatosEspecificosTipoCanc(de);
			postulacion.setUsuModif(usuarioLogueado.getCodigoUsuario());
			postulacion.setFechaModif(new Date());
			postulacion.setObsCancelacion(observacion);
			postulacion.setUsuCancelacion(usuarioLogueado.getCodigoUsuario());
			postulacion.setFechaCancelacion(new Date());
			postulacion = em.merge(postulacion);

			enviarMail(postulacion);
			// Guardar documentos
			if (!guardarDocumento(postulacion))
				return;
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			cargarGrupos(idConcursoSel);
			limpiar2();
			idConcursoSel = null;
		} catch (Exception e) {
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}

	}

	public String limpiar2() {
		mostrarModal = false;
		idPostulaSelected = null;
		return "";
	}

	private Boolean guardarDocumento(Postulacion postulacion) {
		UploadItem fileItem =
			seleccionUtilFormController.crearUploadItem(fName, uFile.length, cType, uFile);
		Long idDocuGenerado;
		String nombreTabla = "PersonaPostulante";
		String nombrePantalla = "cancelarPostulacion";
		String direccionFisica = "";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String anho = sdf.format(new Date());
		String cParador = File.separator;
		String nroDocId =
			postulacion.getPersonaPostulante().getPersona().getDocumentoIdentidad()
				+ postulacion.getPersonaPostulante().getPersona().getIdPersona();
		direccionFisica =
			cParador
				+ "SICCA"
				+ cParador
				+ anho
				+ cParador
				+ "OEE"
				+ cParador
				+ postulacion.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getIdConfiguracionUo()
				+ cParador
				+ postulacion.getConcursoPuestoAgr().getConcurso().getDatosEspecificosTipoConc().getIdDatosEspecificos()
				+ cParador + postulacion.getConcursoPuestoAgr().getConcurso().getIdConcurso()
				+ cParador + postulacion.getConcursoPuestoAgr().getIdConcursoPuestoAgr() + cParador
				+ "POSTULANTES" + cParador + nroDocId;
		Long idTipoDoc =
			seleccionUtilFormController.traerDatosEspecificos("CANCELACION POR PRESENTACION DE DOCUMENTO").getIdDatosEspecificos();
		idDocuGenerado =
			admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, null);
		if (idDocuGenerado == null)
			return false;
		// En postulacionAdjuntos
		PostulacionAdjuntos postulacionAdjuntos = new PostulacionAdjuntos();
		postulacionAdjuntos.setDocumento(new Documentos());
		postulacionAdjuntos.getDocumento().setIdDocumento(idDocuGenerado);
		postulacionAdjuntos.setPostulacion(postulacion);
		postulacionAdjuntos.setActivo(true);
		postulacionAdjuntos.setFechaAlta(new Date());
		postulacionAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		em.persist(postulacionAdjuntos);
		return true;
	}

	private String cargarCuerpoMail(Postulacion postulacion, Persona persona) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		VelocityEngine ve = new VelocityEngine();
		java.util.Properties p = new java.util.Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);
		VelocityContext context = new VelocityContext();
		context.put("personNomrbre", persona.getNombres());
		context.put("personApellido", persona.getApellidos());
		context.put("usuarioCod", postulacion.getPersonaPostulante().getUsuAlta());
		context.put("postulanteNombre", postulacion.getPersonaPostulante().getPersona().getNombres());
		context.put("postulanteApellido", postulacion.getPersonaPostulante().getPersona().getApellidos());
		context.put("postulanteCI", postulacion.getPersonaPostulante().getPersona().getDocumentoIdentidad());
		context.put("postulantePaisExpe", postulacion.getPersonaPostulante().getPersona().getPaisByIdPaisExpedicionDoc().getDescripcion());
		context.put("concursoNro", postulacion.getConcursoPuestoAgr().getConcurso().getNumero());
		context.put("concursoNombre", postulacion.getConcursoPuestoAgr().getConcurso().getNombre());
		context.put("concursoAnio", postulacion.getConcursoPuestoAgr().getConcurso().getAnhio());
		context.put("uoDescCorta", postulacion.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getDescripcionCorta());
		context.put("descGrupo", postulacion.getConcursoPuestoAgr().getDescripcionGrupo());
		context.put("uoDenominacion", postulacion.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getDenominacionUnidad());
		context.put("fecha", sdf.format(new Date()));
		Template t = ve.getTemplate("/templates/email_539.vm");
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		return writer.toString();
	}

	private void enviarMail(Postulacion postulacion) {
		List<Persona> destinatarios =
			seleccionUtilFormController.getComisionSelecion(postulacion.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		String dirEmail = null;
		String texto = "";
		for (Persona o : destinatarios) {
			dirEmail = o.getEMail();
			String asunto = "Cancelación de Postulación por presentación de documentos";
			texto = cargarCuerpoMail(postulacion, o);
			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto, null);

		}
	}

	public void selectIdPos(Long idSelected) {
		mostrarModal = true;
		idPostulaSelected = idSelected;
	}

	public List<Persona> getlPostulantes() {
		return lPostulantes;
	}

	public void setlPostulantes(List<Persona> lPostulantes) {
		this.lPostulantes = lPostulantes;
	}

	public List<Concurso> getlConcursos() {
		return lConcursos;
	}

	public void setlConcursos(List<Concurso> lConcursos) {
		this.lConcursos = lConcursos;
	}

	public List<Postulacion> getlGrupos() {
		return lGrupos;
	}

	public void setlGrupos(List<Postulacion> lGrupos) {
		this.lGrupos = lGrupos;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
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

	public Long getIdPaisEpeDoc() {
		return idPaisEpeDoc;
	}

	public void setIdPaisEpeDoc(Long idPaisEpeDoc) {
		this.idPaisEpeDoc = idPaisEpeDoc;
	}

	public byte[] getuFile() {
		return uFile;
	}

	public void setuFile(byte[] uFile) {
		this.uFile = uFile;
	}

	public String getcType() {
		return cType;
	}

	public void setcType(String cType) {
		this.cType = cType;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Boolean getMostrarModal() {
		return mostrarModal;
	}

	public void setMostrarModal(Boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}

	public Boolean getDeshabilitarTodo() {
		return deshabilitarTodo;
	}

	public void setDeshabilitarTodo(Boolean deshabilitarTodo) {
		this.deshabilitarTodo = deshabilitarTodo;
	}
}
