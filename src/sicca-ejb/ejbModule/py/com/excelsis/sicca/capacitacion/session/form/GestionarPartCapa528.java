package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EvaluacionInscPost;
import py.com.excelsis.sicca.entity.EvaluacionTipo;
import py.com.excelsis.sicca.entity.ListaCab;
import py.com.excelsis.sicca.entity.ListaDet;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.PostulacionCapAdj;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.TiposDatos;
import enums.TiposEstadosMsjPersona;

@Scope(ScopeType.CONVERSATION)
@Name("gestionarPartCapa528")
public class GestionarPartCapa528 {
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
	@In(required = false, create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	private Long idPaisExpe;
	private String docIdentidad;
	private Long idCapa;
	private Capacitaciones capacitacion;
	private String titulo;
	private String tipoCapacitacion;
	private String idMostrar;
	private List<ListaDet> lista;
	private List<PostulacionCapAdj> lpostulacionCapAdj;
	private Boolean mostrarModal = false;
	private String nombreApellido;
	private String telefono;
	private String email;
	private Boolean habBtnAddPersons = false;
	private Long idPersona;

	private List<SelectItem> combo1SI;
	private List<SelectItem> combo2SI;
	private List<SelectItem> combo3SI;
	private Long idCombo1;
	private Long idCombo2;
	private Long idCombo3;
	private byte[] uFile1 = null;
	private String cType1 = null;
	private String fName1 = null;
	private byte[] uFile2 = null;
	private String cType2 = null;
	private String fName2 = null;
	private byte[] uFile3 = null;
	private String cType3 = null;
	private String fName3 = null;
	private String mensaje;
	private Persona persona;
	private Long idPostulacion;
	private static final String F1_CAP = "F1_CAP";
	private static final String F2_CAP = "F2_CAP";
	private static final String F3_CAP = "F3_CAP";
	private PersonaDTO personaDTO;
	private Boolean primeraVez = true;

	public void init() throws Exception {
		mostrarModal = false;
		SeguridadUtilFormController seguridadUtilFormController =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		if (!seguridadUtilFormController.validarInput(idCapa.toString(), TiposDatos.LONG.getValor(), null))
			return;
		capacitacion = em.find(Capacitaciones.class, idCapa);
		if (primeraVez) {
			idMostrar = "P";
			primeraVez = false;
		}

		seguridadUtilFormController.validarPerteneceOEE(capacitacion.getConfiguracionUoCab().getIdConfiguracionUo());
		cargarCabecera();
		primeraVez();
		search();
		if (mensaje != null) {
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			mensaje = null;
		}

	}

	private void cargarPersona(Boolean completo) throws Exception {
		if (idPersona != null) {
			// Persona desde el botón buscar persona.
			if (!seguridadUtilFormController.validarInput(idPersona.toString(), TiposDatos.LONG.getValor(), null)) {
				return;
			}
			persona = em.find(Persona.class, idPersona);
			completarDatosPersona(persona, completo);
			idPostulacion = null;
		} else if (idPostulacion != null) {
			idPersona = null;
			idCombo1 = null;
			idCombo2 = null;
			idCombo3 = null;
			uFile1 = null;
			uFile2 = null;
			uFile3 = null;
			// Persona desde lista de espera
			if (!seguridadUtilFormController.validarInput(idPostulacion.toString(), TiposDatos.LONG.getValor(), null)) {
				return;
			}
			PostulacionCap postulacionCap = em.find(PostulacionCap.class, idPostulacion);
			persona = postulacionCap.getPersona();
			completarDatosPersona(persona, completo);
		}
	}

	public void initEdit() throws Exception {
		limpiar();
		updateTipoDocumento(F1_CAP);
		updateTipoDocumento(F2_CAP);
		updateTipoDocumento(F3_CAP);
		cargarPersona(true);

	}

	public String finalizo(Boolean finalizo) {
		if (finalizo) {
			return "Sí";
		} else
			return "No";
	}

	public void cargarDocumentosAdjuntos(Long idPostulacion) {
		Query q =
			em.createQuery("select PostulacionCapAdj from PostulacionCapAdj PostulacionCapAdj "
				+ "where PostulacionCapAdj.activo is true "
				+ " and PostulacionCapAdj.postulacionCap.idPostulacion  = :idPostulacion ");
		q.setParameter("idPostulacion", idPostulacion);
		lpostulacionCapAdj = q.getResultList();
		mostrarModal = true;
	}

	public String formatearFecha(Date fecha) {
		if (fecha == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(fecha);
	}

	public void imprimir() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU528_gestionar_part_capa");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}

	private void cargarParametros() {
		try {
			String elWhere = " ";
			Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel", diccDscNEO.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad", diccDscNEO.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oee", diccDscNEO.get("OEE"));
			reportUtilFormController.getParametros().put("unidadOrga", diccDscNEO.get("UND_ORG"));
			reportUtilFormController.getParametros().put("nombreCapa", capacitacion.getNombre());
			reportUtilFormController.getParametros().put("tipoCapa", capacitacion.getDatosEspecificosTipoCap().getDescripcion());
			ConfiguracionUoCab uo =
				em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			reportUtilFormController.getParametros().put("oeeUsuarioLogueado", uo.getDenominacionUnidad());

			elWhere = "  AND CAB.ID_CAPACITACION = " + idCapa;
			if (idPaisExpe == null)
				reportUtilFormController.getParametros().put("pais", "TODOS");
			else {
				if (!seguridadUtilFormController.validarInput(idPaisExpe.toString(), TiposDatos.INTEGER.getValor(), null)) {
					elWhere += "  AND P.id_pais_expedicion_doc = " + (new Date()).getTime();
				} else {
					reportUtilFormController.getParametros().put("pais", em.find(Pais.class, idPaisExpe).getDescripcion());
					elWhere += "  AND P.id_pais_expedicion_doc = " + idPaisExpe;
				}
			}

			if (docIdentidad == null || docIdentidad.trim().isEmpty())
				reportUtilFormController.getParametros().put("cedula", "TODOS");
			else {
				if (!seguridadUtilFormController.validarInput(docIdentidad, TiposDatos.STRING.getValor(), null)) {
					reportUtilFormController.getParametros().put("cedula", docIdentidad);
					elWhere += "  AND P.documento_identidad = '" + (new Date()).getTime() + "'";
				} else {
					reportUtilFormController.getParametros().put("cedula", docIdentidad);
					elWhere +=
						"  AND P.documento_identidad = '"
							+ seguridadUtilFormController.caracterInvalido(docIdentidad) + "'";
				}
			}
			if (idMostrar == null)
				reportUtilFormController.getParametros().put("mostrar", "TODOS");
			else {
				if (!seguridadUtilFormController.validarInput(idMostrar, TiposDatos.STRING.getValor(), 1)) {
					elWhere += "  AND  DET.TIPO = '" + (new Date()).getTime() + "'";
				} else {
					elWhere += "  AND  DET.TIPO = '" + idMostrar + "'";
					reportUtilFormController.getParametros().put("mostrar", idMostrar.equals("P")
						? "PARTICIPANTES" : "DESVINCULADOS");
				}

			}

			reportUtilFormController.getParametros().put("usuAlta", usuarioLogueado.getCodigoUsuario());
			reportUtilFormController.getParametros().put("elWhere", elWhere);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deshacerDesvinculacion(ListaDet listaDet) {
		try {
			listaDet = em.find(ListaDet.class, listaDet.getIdListaDet());
			PostulacionCap postulacionCap = listaDet.getPostulacionCap();
			/* a. Actualiza POSTULACION_CAB correspondiente al ID_POSTULACION */
			postulacionCap.setActivo(true);
			postulacionCap.setUsuCancelacion(null);
			postulacionCap.setFechaCancelacion(null);
			postulacionCap.setObsCancelacion(null);
			postulacionCap.setUsuMod(usuarioLogueado.getCodigoUsuario());
			postulacionCap.setFechaMod(new Date());
			postulacionCap = em.merge(postulacionCap);
			/* b. Actualiza la tabla LISTA_DET de la postulación seleccionada correspondiente a la capacitación (LISTA_CAB.ID_CAPACITACION): */
			listaDet.setTipo("P");
			listaDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			listaDet.setFechaMod(new Date());
			listaDet.setDatosEspecificosDesv(null);
			listaDet.setObservacion(null);
			listaDet.setFechaDesv(null);
			listaDet.setUsuDesv(null);
			listaDet.setFechaDesvinculacion(null);

			if (listaDet.getDocumento() != null) {
				listaDet.getDocumento().setActivo(false);
				listaDet.getDocumento().setUsuMod(usuarioLogueado.getCodigoUsuario());
				listaDet.getDocumento().setFechaMod(new Date());
				em.merge(listaDet.getDocumento());
			}
			em.merge(listaDet);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			search();
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}

	}

	public void updateTipoDocumento(String comboNro) {
		List<DatosEspecificos> lista = getTipoDocumento(comboNro);
		if (comboNro.equals(F1_CAP)) {
			combo1SI = buildSelectItem(lista, combo1SI);
		} else if (comboNro.equals(F2_CAP)) {
			combo2SI = buildSelectItem(lista, combo2SI);
		} else if (comboNro.equals(F3_CAP)) {
			combo3SI = buildSelectItem(lista, combo3SI);
		}

	}

	private List<SelectItem> buildSelectItem(List<DatosEspecificos> lista, List<SelectItem> selectItem) {
		if (selectItem == null)
			selectItem = new ArrayList<SelectItem>();
		else
			selectItem.clear();
		selectItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : lista) {
			selectItem.add(new SelectItem(o.getIdDatosEspecificos(), o.getDescripcion()));
		}
		return selectItem;
	}

	@SuppressWarnings("unchecked")
	private List<DatosEspecificos> getTipoDocumento(String tipo) {
		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ " where DatosEspecificos.valorAlf =  '" + tipo + "'"
				+ " and DatosEspecificos.datosGenerales.descripcion = 'TIPOS DE DOCUMENTOS'"
				+ " and DatosEspecificos.activo is true"
				+ " order by DatosEspecificos.descripcion ");

		return q.getResultList();
	}

	private String calIdEvalTipo(String tipoEval ) {
		Query q =
			em.createQuery("select EvaluacionTipo from EvaluacionTipo EvaluacionTipo "
				+ " where EvaluacionTipo.tipo in " + tipoEval +" and EvaluacionTipo.capacitaciones.idCapacitacion = :idCapa ");
		q.setParameter("idCapa", capacitacion.getIdCapacitacion());

		List<EvaluacionTipo> lista = q.getResultList();
		String respuesta = "";
		if (lista.size() > 0) {
			for (EvaluacionTipo o : lista) {
				respuesta += "," + o.getIdEval();
			}
			respuesta = "(" + respuesta.replaceFirst(",", "") + ")";
		}
		return respuesta;
	}

	/**
	 * Inserta los postulante que aun no estan presentes en EvaluacionInscPost
	 */
	private void insertarPostulantes(Long idCab) {
		String idEvalTipo = calIdEvalTipo("('EVAL_INSCRIP','EVAL_POST')");

		Query q =
			em.createQuery("select EvaluacionInscPost from EvaluacionInscPost EvaluacionInscPost"
				+ " where EvaluacionInscPost.resultado = 'S' "
				+ " and  EvaluacionInscPost.evaluacionTipo in " + idEvalTipo);

		List<EvaluacionInscPost> lista = q.getResultList();
		for (EvaluacionInscPost o : lista) {
			// Insertar
			ListaDet listaDet = new ListaDet();
			listaDet.setListaCab(new ListaCab());
			listaDet.getListaCab().setIdLista(idCab);
			listaDet.setActivo(true);
			listaDet.setFechaAlta(new Date());
			listaDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			listaDet.setPostulacionCap(o.getPostulacionCap());
			listaDet.setTipo("P");
			em.persist(listaDet);
		}
	}

	/**
	 * Chequea la configuración inicial de la actividad
	 */
	private void primeraVez() {
		if (capacitacion == null) {
			return;
		}
		Query q =
			em.createQuery("select ListaCab from ListaCab ListaCab "
				+ " where ListaCab.capacitaciones.idCapacitacion = :idCapa  "
				+ " and ListaCab.activo is true ");
		q.setParameter("idCapa", idCapa);
		List<EvaluacionTipo> lista = q.getResultList();
		if (lista.size() == 0) {
			// Primera vez
			ListaCab listaCab = new ListaCab();
			listaCab.setActivo(true);
			listaCab.setCapacitaciones(new Capacitaciones());
			listaCab.getCapacitaciones().setIdCapacitacion(idCapa);
			listaCab.setFechaAlta(new Date());
			listaCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(listaCab);
			if (capacitacion.getRecepcionPostulacion() != null
				&& capacitacion.getRecepcionPostulacion()) {
				// Insertar los postulantes
				insertarPostulantes(listaCab.getIdLista());
			}
			em.flush();
		}
	}

	public String traerResultado(String res) {
		if (res == null) {
			return "";
		} else if (res.equalsIgnoreCase("S"))
			return "Seleccionado";
		else if (res.equalsIgnoreCase("N"))
			return "No Seleccionado";
		else if (res.equalsIgnoreCase("L"))
			return "Lista de Espera";

		return "";
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

	private void cargarCabecera() {

		if (nivelEntidadOeeUtil == null) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		}
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitacion.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdConfigCab(capacitacion.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		if (capacitacion != null) {
			titulo = capacitacion.getNombre();
			tipoCapacitacion = capacitacion.getDatosEspecificosTipoCap().getDescripcion();
		}
	}

	public String descTipo(String elTipo) {
		if (elTipo == null) {
			return "-";
		}
		if (elTipo.equalsIgnoreCase("P")) {
			return "PARTICIPANTE";
		} else {
			return "DESVINCULADO";
		}
	}

	public void search() {
		mostrarModal = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select ListaDet from ListaDet ListaDet ");
		StringBuffer elWhere = new StringBuffer();
		elWhere.append(" where ListaDet.listaCab.capacitaciones.idCapacitacion = :idCapa");
		elWhere.append(" and  ListaDet.activo is true ");
		if (docIdentidad != null && !docIdentidad.trim().isEmpty()) {
			elWhere.append(" and  ListaDet.postulacionCap.persona.documentoIdentidad = :docIdentidad");
		}
		if (idMostrar != null) {
			elWhere.append(" and  ListaDet.tipo = :idTipo");
		}
		if (idPaisExpe != null) {
			elWhere.append(" and  ListaDet.postulacionCap.persona.paisByIdPaisExpedicionDoc.idPais = :idPais");
		}
		sql.append(elWhere.toString());
		sql.append(" order by ListaDet.postulacionCap.persona.nombres asc,ListaDet.postulacionCap.persona.apellidos asc ");
		Query q = em.createQuery(sql.toString());
		q.setParameter("idCapa", idCapa);
		if (docIdentidad != null && !docIdentidad.trim().isEmpty()) {
			q.setParameter("docIdentidad", docIdentidad);
		}
		if (idMostrar != null) {
			q.setParameter("idTipo", idMostrar);
		}
		if (idPaisExpe != null) {
			q.setParameter("idPais", idPaisExpe);
		}
		lista = q.getResultList();

	}

	public void limpiar() {
		idMostrar = null;
		idPaisExpe = null;
		docIdentidad = null;
		nombreApellido = null;
		telefono = null;
		email = null;
		idCombo1 = null;
		idCombo2 = null;
		idCombo3 = null;

	}

	public void limpiarDatosPersona() {
		nombreApellido = null;
		telefono = null;
		email = null;
		docIdentidad = null;
		idPostulacion = null;
	}

	public void limpiarDatosPersona2() {
		nombreApellido = null;
		telefono = null;
		email = null;
	}

	private void completarDatosPersona(Persona persona, Boolean completo) {
		nombreApellido = persona.getNombres() + " " + persona.getApellidos();
		telefono = persona.getTelefonos() == null ? "" : persona.getTelefonos();
		email = persona.getEMail() == null ? "" : persona.getEMail();
		if (completo) {
			idPaisExpe = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad = persona.getDocumentoIdentidad();
		}
	}

	public void buscarPersona() throws Exception {
		limpiarIdPostulacion();
		/* Validaciones */
		if (idPaisExpe == null
			|| !seguridadUtilFormController.validarInput(idPaisExpe.toString(), TiposDatos.LONG.getValor(), null)) {
			return;
		}
		Pais pais = em.find(Pais.class, idPaisExpe);
		if (pais == null)
			return;
		/* fin validaciones */
		personaDTO = seleccionUtilFormController.buscarPersona(docIdentidad, pais.getDescripcion());

		if (personaDTO.getHabilitarBtn() == null) {
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			habBtnAddPersons = false;
			limpiarDatosPersona2();
			persona = null;
		} else if (personaDTO.getHabilitarBtn()) {
			habBtnAddPersons = true;
			persona = null;
			limpiarDatosPersona2();
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {
			habBtnAddPersons = false;
			persona = personaDTO.getPersona();
			completarDatosPersona(personaDTO.getPersona(), false);
		}
	}

	public PostulacionCapAdj actualizarAdjuntos(PostulacionCapAdj postulacionCapAdj) {
		if (postulacionCapAdj.getDocumento().getNombreFisicoDoc() == null) {
			Documentos doc =
				em.find(Documentos.class, postulacionCapAdj.getDocumento().getIdDocumento());
			postulacionCapAdj.setDocumento(doc);
		}
		return postulacionCapAdj;
	}

	public ListaDet actualizarPostulacion(ListaDet listaDet) {
		if (listaDet.getPostulacionCap().getPersona() == null) {
			PostulacionCap postulacionCap =
				em.find(PostulacionCap.class, listaDet.getPostulacionCap().getIdPostulacion());
			Persona persona = em.find(Persona.class, postulacionCap.getPersona().getIdPersona());
			listaDet.setPostulacionCap(postulacionCap);
			listaDet.getPostulacionCap().setPersona(persona);
		}

		return listaDet;
	}

	public String save() {
		if (precondSave()) {
			try {
				// Entró por lista de espera (idPostulacion esta cargado)
				if (idPostulacion != null) {
					Query q =
						em.createQuery("select EvaluacionInscPost from EvaluacionInscPost EvaluacionInscPost"
							+ " where EvaluacionInscPost.evaluacionTipo.tipo in ('EVAL_INSCRIP','EVAL_POST') "
							+ "and EvaluacionInscPost.postulacionCap.capacitacion.idCapacitacion = :idCapa");
					q.setParameter("idCapa", idCapa);
					// Si o si debe haber un valor aca.
					EvaluacionInscPost evaluacionInscPost =
						(py.com.excelsis.sicca.entity.EvaluacionInscPost) q.getSingleResult();
					evaluacionInscPost.setResultado("S");
					em.merge(evaluacionInscPost);
					ListaDet listaDet = new ListaDet();
					listaDet.setActivo(true);
					listaDet.setListaCab(new ListaCab());
					listaDet.getListaCab().setIdLista(calListaCab());
					listaDet.setPostulacionCap(new PostulacionCap());
					listaDet.getPostulacionCap().setIdPostulacion(idPostulacion);
					listaDet.setTipo("P");
					listaDet.setFechaAlta(new Date());
					listaDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(listaDet);
				} else {
					if (persona == null) {
						statusMessages.add(Severity.ERROR, "No se puede determinar la persona");
						return null;
					}
					// Entro por webservice o base de datos local
					persona.setEMail(email);
					persona.setTelefonos(telefono);
					if (persona.getIdPersona() == null) {
						em.persist(persona);
					} else {
						persona = em.merge(persona);
					}
					PostulacionCap postulacionCap = new PostulacionCap();
					postulacionCap.setActivo(true);
					postulacionCap.setPersona(new Persona());
					postulacionCap.getPersona().setIdPersona(persona.getIdPersona());
					postulacionCap.setFicha(false);
					postulacionCap.setTipo("ID");
					postulacionCap.setCapacitacion(new Capacitaciones());
					postulacionCap.getCapacitacion().setIdCapacitacion(idCapa);

					postulacionCap.setUsuFicha("SYSTEM");
					postulacionCap.setFechaFicha(new Date());
					em.persist(postulacionCap);
					// Llama al CU 289 para registrar cada uno de sus adjuntos
					guardarDocAdjuntos(postulacionCap.getIdPostulacion());
					// Registra a la persona como participante
					ListaDet listaDet = new ListaDet();
					listaDet.setActivo(true);
					listaDet.setListaCab(new ListaCab());
					listaDet.getListaCab().setIdLista(calListaCab());
					listaDet.setPostulacionCap(new PostulacionCap());
					listaDet.getPostulacionCap().setIdPostulacion(postulacionCap.getIdPostulacion());
					listaDet.setTipo("P");
					listaDet.setFechaAlta(new Date());
					listaDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(listaDet);
				}
				em.flush();
				limpiar();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				return "OK";

			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				return null;
			}
		}
		return null;
	}

	public void abrirDocumento(Long idPostulacionAdj) {
		try {
			PostulacionCapAdj c = em.find(PostulacionCapAdj.class, idPostulacionAdj);
			AdmDocAdjuntoFormController.abrirDocumentoFromCU(c.getDocumento().getIdDocumento(), usuarioLogueado.getIdUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void guardarDocAdjuntos(Long idPostulacionCap) throws Exception {
		Long idDocumento = null;
		if (uFile1 != null) {
			idDocumento = guardarAdjuntos(fName1, uFile1.length, cType1, uFile1, idCombo1, null);
			if (idDocumento != null) {
				PostulacionCapAdj postulacionCapAdj = new PostulacionCapAdj();
				postulacionCapAdj.setPostulacionCap(new PostulacionCap());
				postulacionCapAdj.getPostulacionCap().setIdPostulacion(idPostulacionCap);
				postulacionCapAdj.setDocumento(new Documentos());
				postulacionCapAdj.getDocumento().setIdDocumento(idDocumento);
				postulacionCapAdj.setActivo(true);
				postulacionCapAdj.setUsuAlta("SYSTEM");
				postulacionCapAdj.setFechaAlta(new Date());
				em.persist(postulacionCapAdj);
			} else {
				throw new Exception("No se pudo guardar el documento 1");
			}
		}
		if (uFile2 != null) {
			idDocumento = guardarAdjuntos(fName2, uFile2.length, cType2, uFile2, idCombo2, null);
			if (idDocumento != null) {
				PostulacionCapAdj postulacionCapAdj = new PostulacionCapAdj();
				postulacionCapAdj.setPostulacionCap(new PostulacionCap());
				postulacionCapAdj.getPostulacionCap().setIdPostulacion(idPostulacionCap);
				postulacionCapAdj.setDocumento(new Documentos());
				postulacionCapAdj.getDocumento().setIdDocumento(idDocumento);
				postulacionCapAdj.setActivo(true);
				postulacionCapAdj.setUsuAlta("SYSTEM");
				postulacionCapAdj.setFechaAlta(new Date());
				em.persist(postulacionCapAdj);
			} else {
				throw new Exception("No se pudo guardar el documento 2");
			}
		}
		if (uFile3 != null) {
			idDocumento = guardarAdjuntos(fName3, uFile3.length, cType3, uFile3, idCombo3, null);
			if (idDocumento != null) {
				PostulacionCapAdj postulacionCapAdj = new PostulacionCapAdj();
				postulacionCapAdj.setPostulacionCap(new PostulacionCap());
				postulacionCapAdj.getPostulacionCap().setIdPostulacion(idPostulacionCap);
				postulacionCapAdj.setDocumento(new Documentos());
				postulacionCapAdj.getDocumento().setIdDocumento(idDocumento);
				postulacionCapAdj.setActivo(true);
				postulacionCapAdj.setUsuAlta("SYSTEM");
				postulacionCapAdj.setFechaAlta(new Date());
				em.persist(postulacionCapAdj);
			} else {
				throw new Exception("No se pudo guardar el documento 3");
			}
		}

	}

	private Long guardarAdjuntos(String fileName, int fileSize, String contentType, byte[] file, Long idTipoDoc, Long idDocumento) {
		try {
			Persona perosonaLogueada =
				em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "PostulacionCap";
			String nombrePantalla = "nuevo_participante_528";
			String direccionFisica = "";

			String usuarioPortal = usuarioLogueado.getCodigoUsuario();
			String nroDocId =
				perosonaLogueada.getDocumentoIdentidad() + perosonaLogueada.getIdPersona();
			direccionFisica =
				File.separator + "SICCA" + File.separator + usuarioPortal + File.separator
					+ nroDocId;
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Long calListaCab() {
		Query q =
			em.createQuery("select ListaCab from ListaCab ListaCab "
				+ " where ListaCab.capacitaciones.idCapacitacion = :idCapa  "
				+ " and ListaCab.activo is true ");
		q.setParameter("idCapa", idCapa);
		ListaCab listaCab = (ListaCab) q.getSingleResult();
		return listaCab.getIdLista();
	}

	private Boolean precondSave() {
		if (idPaisExpe == null || docIdentidad == null || docIdentidad.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
			return false;
		}
		// Solo se aplica cuando RecepcionPostulacion es false y la persona no vino de una lista de espera
		if (!capacitacion.getRecepcionPostulacion() && idPostulacion == null) {
			if (idCombo1 == null || idCombo2 == null || idCombo3 == null || uFile1 == null
				|| uFile2 == null || uFile3 == null) {
				statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
				return false;
			}
		}
		if (uFile1 != null && idCombo1 == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar el Tipo de Docuemento 1");
			return false;
		}
		if (uFile2 != null && idCombo2 == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar el Tipo de Docuemento 2");
			return false;
		}
		if (uFile3 != null && idCombo3 == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar el Tipo de Docuemento 3");
			return false;
		}

		if (telefono != null && telefono.length() > 100) {
			statusMessages.add(Severity.ERROR, "Superada la catidad máxima de caracteres(100) para el campo Teléfono");
			return false;
		}
		if (email != null && email.length() > 100) {
			statusMessages.add(Severity.ERROR, "Superada la catidad máxima de caracteres(100) para el campo E-mail");
			return false;
		}
		if (email != null && !email.isEmpty() && !General.isEmail(email)) {
			statusMessages.add(Severity.ERROR, "E-mail no válido");
			return false;
		}
		return true;
	}

	public String linkAddParticipante() {
		habBtnAddPersons = false;
		idPersona = null;
		idPostulacion = null;
		return "/capacitacion/gesParPosCapa/NuevoParticipante528.seam?from=capacitacion/gesParPosCapa/gesParPosCapa528";
	}

	public String linkAddPersona() {
		habBtnAddPersons = false;
		idPersona = null;
		limpiarIdPostulacion();
		return "/seleccion/persona/PersonaEdit.seam?personaFrom=/capacitacion/gesParPosCapa/NuevoParticipante528";

	}

	public Long getIdPaisExpe() {
		return idPaisExpe;
	}

	public void setIdPaisExpe(Long idPaisExpe) {
		this.idPaisExpe = idPaisExpe;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public Long getidCapa() {
		return idCapa;
	}

	public void setidCapa(Long idCapa) {
		this.idCapa = idCapa;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
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

	public List<ListaDet> getLista() {
		return lista;
	}

	public void setLista(List<ListaDet> lista) {
		this.lista = lista;
	}

	public List<PostulacionCapAdj> getLpostulacionCapAdj() {
		return lpostulacionCapAdj;
	}

	public void setLpostulacionCapAdj(List<PostulacionCapAdj> lpostulacionCapAdj) {
		this.lpostulacionCapAdj = lpostulacionCapAdj;
	}

	public Boolean getMostrarModal() {
		return mostrarModal;
	}

	public void setMostrarModal(Boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}

	public List<SelectItem> getCombo1SI() {
		return combo1SI;
	}

	public void setCombo1SI(List<SelectItem> combo1si) {
		combo1SI = combo1si;
	}

	public List<SelectItem> getCombo2SI() {
		return combo2SI;
	}

	public void setCombo2SI(List<SelectItem> combo2si) {
		combo2SI = combo2si;
	}

	public List<SelectItem> getCombo3SI() {
		return combo3SI;
	}

	public void setCombo3SI(List<SelectItem> combo3si) {
		combo3SI = combo3si;
	}

	public Long getIdCombo1() {
		return idCombo1;
	}

	public void setIdCombo1(Long idCombo1) {
		this.idCombo1 = idCombo1;
	}

	public Long getIdCombo2() {
		return idCombo2;
	}

	public void setIdCombo2(Long idCombo2) {
		this.idCombo2 = idCombo2;
	}

	public Long getIdCombo3() {
		return idCombo3;
	}

	public void setIdCombo3(Long idCombo3) {
		this.idCombo3 = idCombo3;
	}

	public Long getIdCapa() {
		return idCapa;
	}

	public void setIdCapa(Long idCapa) {
		this.idCapa = idCapa;
	}

	public byte[] getuFile1() {
		return uFile1;
	}

	public void setuFile1(byte[] uFile1) {
		this.uFile1 = uFile1;
	}

	public String getcType1() {
		return cType1;
	}

	public void setcType1(String cType1) {
		this.cType1 = cType1;
	}

	public String getfName1() {
		return fName1;
	}

	public void setfName1(String fName1) {
		this.fName1 = fName1;
	}

	public byte[] getuFile2() {
		return uFile2;
	}

	public void setuFile2(byte[] uFile2) {
		this.uFile2 = uFile2;
	}

	public String getcType2() {
		return cType2;
	}

	public void setcType2(String cType2) {
		this.cType2 = cType2;
	}

	public String getfName2() {
		return fName2;
	}

	public void setfName2(String fName2) {
		this.fName2 = fName2;
	}

	public byte[] getuFile3() {
		return uFile3;
	}

	public void setuFile3(byte[] uFile3) {
		this.uFile3 = uFile3;
	}

	public String getcType3() {
		return cType3;
	}

	public void setcType3(String cType3) {
		this.cType3 = cType3;
	}

	public String getfName3() {
		return fName3;
	}

	public void setfName3(String fName3) {
		this.fName3 = fName3;
	}

	public String getNombreApellido() {
		return nombreApellido;
	}

	public void setNombreApellido(String nombreApellido) {
		this.nombreApellido = nombreApellido;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Capacitaciones getCapacitacion() {
		return capacitacion;
	}

	public void setCapacitacion(Capacitaciones capacitacion) {
		this.capacitacion = capacitacion;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Boolean getHabBtnAddPersons() {
		return habBtnAddPersons;
	}

	public void setHabBtnAddPersons(Boolean habBtnAddPersons) {
		this.habBtnAddPersons = habBtnAddPersons;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getIdPostulacion() {
		return idPostulacion;
	}

	public void setIdPostulacion(Long idPostulacion) {
		this.idPostulacion = idPostulacion;
	}

	public void limpiarIdPostulacion() {
		idPostulacion = null;
	}

}
