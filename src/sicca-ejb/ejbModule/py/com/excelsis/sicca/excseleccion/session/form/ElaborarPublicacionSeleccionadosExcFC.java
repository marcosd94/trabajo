package py.com.excelsis.sicca.excseleccion.session.form;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.Lista;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC;
import py.com.excelsis.sicca.seleccion.session.form.EvaluarDocPostulantesFormController;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;

@Scope(ScopeType.CONVERSATION)
@Name("elaborarPublicacionSeleccionadosExcFC")
public class ElaborarPublicacionSeleccionadosExcFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private EvaluarDocPostulantesFormController evaluarDocPostulantesFormController;
	public AdmFecRecPosFC admFecRecPosFC;

	private ConcursoPuestoAgr concursoPuestoAgr;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private Excepcion excepcion;
	private ConcursoPuestoAgrExc concursoPuestoAgrExc;

	private String codConcurso;
	private String observacion;
	private String direccionFisica;
	private String codActividad;
	private Integer vacancia;
	private Integer estadoGrupoDesierto;
	private Boolean bloquearTodo;
	private Boolean bloquearControlPuesto;
	private Long idConcursoPuestoAgr;
	private Long idExcepcion;
	private String text;

	private List<EvalReferencialPostulante> listaPostulantesSeleccionados;
	private List<ConcursoPuestoAgrExc> listaAgrExcs;
	private List<PlantaCargoDet> listaCargos;
	private List<ConcursoPuestoDet> listaPuestos;


	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		bloquearTodo = false;
		bloquearControlPuesto = true;
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);
		excepcion = em.find(Excepcion.class, idExcepcion);
		concursoPuestoAgrExc = obtenerAgrExc();
		
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
		if (concurso != null) {
			codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null) {
				codConcurso = codConcurso + " DE "
						+ configuracionUoCab.getDescripcionCorta();
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			}
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}
		buscarSeleccionados();
		if (listaPostulantesSeleccionados.isEmpty()) {
			bloquearTodo = true;
			bloquearControlPuesto = false;
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU594_msg_sin_postulantes"));
			return;
		}

		vacancia = calcularVacancias();
		obtenerDireccionFisica();
	}

	private Integer calcularVacancias() {
		String sql = "SELECT COUNT(*) " + "FROM seleccion.concurso_puesto_det "
				+ "WHERE id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " AND id_excepcion = " + excepcion.getIdExcepcion()
				+ " AND ACTIVO = TRUE";
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null)
			return 0;
		return new Integer(config.toString());
	}
	
	private ConcursoPuestoAgrExc obtenerAgrExc(){
		List<ConcursoPuestoAgrExc> lista = obtenerListaAgrExc();
		if(lista.isEmpty())
			return null;
		else
			return lista.get(0);
	}
	
	private List<ConcursoPuestoAgrExc> obtenerListaAgrExc(){
		Query q = em.createQuery("select exc from ConcursoPuestoAgrExc exc "
				+ " where exc.activo is true "
				+ "and exc.concursoPuestoAgr.idConcursoPuestoAgr = :id "
				+ "and exc.excepcion.idExcepcion = :idExc");
		q.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		q.setParameter("idExc", excepcion.getIdExcepcion());
		return q.getResultList();
	}
	/**
	 * Obtiene la direccion fisica a ser usada en la adjuncion de documentos
	 */
	private void obtenerDireccionFisica() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String separador = File.separator;
		direccionFisica = separador
				+ "SICCA"
				+ separador
				+ anho
				+ separador
				+ "OEE"
				+ separador
				+ concurso.getConfiguracionUoCab().getIdConfiguracionUo()
				+ separador
				+ concurso.getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + separador
				+ concurso.getIdConcurso() + separador
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}


	@SuppressWarnings("unchecked")
	public void buscarSeleccionados() {
		String sql = "select eval.* "
				+ "from seleccion.eval_referencial_postulante eval "
				+ "join seleccion.postulacion post  "
				+ "on post.id_postulacion = eval.id_postulacion "
				+ "join seleccion.persona_postulante persona_post "
				+ "on post.id_persona_postulante = persona_post.id_persona_postulante "
				+ "where eval.sel_adjudicado is true "
				+ "and eval.activo is true  " + "and post.activo is true "
				+ "and eval.excluido is false " + "and eval.incluido is true "
				+ "and eval.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and eval.id_excepcion = " + excepcion.getIdExcepcion()
				+ " order by persona_post.nombres, persona_post.apellidos";
		try {
			listaPostulantesSeleccionados = new ArrayList<EvalReferencialPostulante>();
			listaPostulantesSeleccionados = em.createNativeQuery(sql,
					EvalReferencialPostulante.class).getResultList();

		} catch (Exception e) {
			// TODO: handle exception
		}

	}


	/**
	 * Busca el nivel correspondiente a la entidad
	 * 
	 * @param nenCodigo
	 * @return
	 */
	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {

		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}
	
	private Boolean verificacion() {
		Integer cantAdj = listaPostulantesSeleccionados.size();
		if (cantAdj.intValue() == vacancia.intValue())
			return true;
		else
			return false;
	}
	
	private Boolean verificarPublicacion() {
		Referencias ref = new Referencias();
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"ADJUDICADO");
		if (ref != null) {
			if (concursoPuestoAgr.getEstado().equals(ref.getValorNum()))
				return true;
		}
		return false;
	}

	public String publicar() {
		if (!verificacion()) {
			bloquearControlPuesto = false;
			bloquearTodo = true;
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Cantidad de adjudicados no es igual a cantidad de puestos. Verificar en control Puesto/Postulante.");
			return null;
		}
		if (verificarPublicacion()) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"La Lista de Postulantes Seleccionados ya ha sido publicada anteriormente");
			return null;
		}
		buscarCargos();
		buscarPuestos();
		listaAgrExcs = obtenerListaAgrExc();
		try {
			Referencias ref = new Referencias();
			ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
					"ADJUDICADO");
			if (ref != null) {
				concursoPuestoAgr.setEstado(ref.getValorNum());
				concursoPuestoAgr.setFechaEvalDocHasta(new Date());
				concursoPuestoAgr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				concursoPuestoAgr.setFechaMod(new Date());
				em.merge(concursoPuestoAgr);
				EstadoDet estadoDet = new EstadoDet();
				estadoDet = seleccionUtilFormController.buscarEstadoDet(
						"CONCURSO", "ADJUDICADO");
				for (PlantaCargoDet cargo : listaCargos) {
					cargo.setEstadoCab(estadoDet.getEstadoCab());
					cargo.setEstadoDet(estadoDet);
					cargo.setUsuMod(usuarioLogueado.getCodigoUsuario());
					cargo.setFechaMod(new Date());
					em.merge(cargo);
				}

				for (ConcursoPuestoDet puesto : listaPuestos) {

					puesto.setEstadoDet(estadoDet);
					puesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
					puesto.setFechaMod(new Date());
					puesto.setExcepcion(excepcion);
					em.merge(puesto);
				}
				
			}
			for (ConcursoPuestoAgrExc exc : listaAgrExcs) {
				exc.setEstado("ADJUDICADO");
				exc.setFechaMod(new Date());
				exc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(exc);
				
			}
			
			HistorialExcepcion historialExcepcion = new HistorialExcepcion();
			historialExcepcion.setConcursoGrupoAgr(concursoPuestoAgr);
			historialExcepcion.setEstado("ADJUDICADO");
			historialExcepcion.setExcepcion(excepcion);
			historialExcepcion.setFechaAlta(new Date());
			historialExcepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			
			if (observacion != null && observacion.trim().isEmpty())
				historialExcepcion.setObservacion(observacion);
			em.persist(historialExcepcion);
			excepcion.setEstado("ADJUDICADO");
			excepcion.setFechaMod(new Date());
			excepcion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(excepcion);
			
			em.flush();

			for (EvalReferencialPostulante seleccionado : listaPostulantesSeleccionados) {
				enviarEmails(seleccionado);
			}
			
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "next";
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public void enviarEmails(EvalReferencialPostulante referencial) {
		PersonaPostulante persona = new PersonaPostulante();
		persona = referencial.getPostulacion().getPersonaPostulante();
		String dirEmail = persona.getEMail();

		String asunto = null;
		asunto = " SICCA - Lista de Seleccionados";
		String texto = "";

		try {
			texto = texto
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ persona.getNombres()
					+ " "
					+ persona.getApellidos()
					+ "</p> "
					+ "</b> <p align=\"justify\"> Le comunicamos que ha sido seleccionado/a para cubrir el Puesto de: <b>"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "</b>, del Concurso: </p> "
					+ "<p> <b>"
					+ concurso.getNumero()
					+ "/"
					+ concurso.getAnhio()
					+ " de "
					+ configuracionUoCab.getDescripcionCorta()
					+ " - "
					+ concurso.getNombre()
					+ "</b> </p> "
					+ "<p align=\"justify\">Para mas informaci&oacute;n, consultar la web del Portal haciendo click <a href="
					+ "http://www.paraguayconcursa.gov.py/"
					+ "> aqui </a></p> " + "<p> Atentamente,</p> "
					+ "<p> SICCA</p> ";

			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,
					null);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@SuppressWarnings("unchecked")
	private void buscarCargos() {
		String sql = "select cargo.* "
				+ "from planificacion.planta_cargo_det cargo "
				+ "join seleccion.concurso_puesto_det puesto_det "
				+ "on puesto_det.id_planta_cargo_det = cargo.id_planta_cargo_det "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = puesto_det.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+" and puesto_det.id_excepcion = "+excepcion.getIdExcepcion()
				+ " and cargo.activo is true "
				+ "and puesto_det.activo is true";
		try {
			listaCargos = new ArrayList<PlantaCargoDet>();
			listaCargos = em.createNativeQuery(sql, PlantaCargoDet.class)
					.getResultList();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@SuppressWarnings("unchecked")
	private void buscarPuestos() {
		String sql = "select det.* "
				+ "from seleccion.concurso_puesto_det det "
				+ "where det.activo is true  "
				+ "and id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+" and det.id_excepcion = "+excepcion.getIdExcepcion();
		try {
			listaPuestos = new ArrayList<ConcursoPuestoDet>();
			listaPuestos = em.createNativeQuery(sql, ConcursoPuestoDet.class)
					.getResultList();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * Es llamado desde el boton Imprimir
	 */
	public void print() {
		Connection conn = null;
		try {
			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();

		
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("usuario", usuarioLogueado == null ? "PORTAL"
					: usuarioLogueado.getCodigoUsuario());
			param.put("concurso", codConcurso + " - " + concurso.getNombre());
			param.put("nivel", nivelEntidad.getNenCodigo() + " - "
					+ nivelEntidad.getNenNombre());
			param.put(
					"entidad",
					sinEntidad.getEntCodigo() + " - "
							+ sinEntidad.getEntNomabre());
			param.put("oee", configuracionUoCab.getOrden() + " - "
					+ configuracionUoCab.getDenominacionUnidad());
			param.put("grupo", concursoPuestoAgr.getDescripcionGrupo());
			param.put("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
			param.put("id_exc", excepcion.getIdExcepcion());

			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF(
					"RPT_CU594_Lista_Postulantes_Seleccionados_Exc", param, false,
					conn, usuarioLogueado);
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("seam_error_inesperado"));
			e.printStackTrace();
			return;
		} finally {
			if (conn != null)
				JpaResourceBean.closeConnection(conn);
		}

	}
	public EvaluarDocPostulantesFormController getEvaluarDocPostulantesFormController() {
		return evaluarDocPostulantesFormController;
	}

	public void setEvaluarDocPostulantesFormController(
			EvaluarDocPostulantesFormController evaluarDocPostulantesFormController) {
		this.evaluarDocPostulantesFormController = evaluarDocPostulantesFormController;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
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

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public Excepcion getExcepcion() {
		return excepcion;
	}

	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}

	public ConcursoPuestoAgrExc getConcursoPuestoAgrExc() {
		return concursoPuestoAgrExc;
	}

	public void setConcursoPuestoAgrExc(
			ConcursoPuestoAgrExc concursoPuestoAgrExc) {
		this.concursoPuestoAgrExc = concursoPuestoAgrExc;
	}

	public String getCodConcurso() {
		return codConcurso;
	}

	public void setCodConcurso(String codConcurso) {
		this.codConcurso = codConcurso;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getCodActividad() {
		return codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	public Integer getVacancia() {
		return vacancia;
	}

	public void setVacancia(Integer vacancia) {
		this.vacancia = vacancia;
	}

	public Integer getEstadoGrupoDesierto() {
		return estadoGrupoDesierto;
	}

	public void setEstadoGrupoDesierto(Integer estadoGrupoDesierto) {
		this.estadoGrupoDesierto = estadoGrupoDesierto;
	}

	public Boolean getBloquearTodo() {
		return bloquearTodo;
	}

	public void setBloquearTodo(Boolean bloquearTodo) {
		this.bloquearTodo = bloquearTodo;
	}

	public Boolean getBloquearControlPuesto() {
		return bloquearControlPuesto;
	}

	public void setBloquearControlPuesto(Boolean bloquearControlPuesto) {
		this.bloquearControlPuesto = bloquearControlPuesto;
	}

	public List<EvalReferencialPostulante> getListaPostulantesSeleccionados() {
		return listaPostulantesSeleccionados;
	}

	public void setListaPostulantesSeleccionados(
			List<EvalReferencialPostulante> listaPostulantesSeleccionados) {
		this.listaPostulantesSeleccionados = listaPostulantesSeleccionados;
	}

	public List<PlantaCargoDet> getListaCargos() {
		return listaCargos;
	}

	public void setListaCargos(List<PlantaCargoDet> listaCargos) {
		this.listaCargos = listaCargos;
	}

	public List<ConcursoPuestoDet> getListaPuestos() {
		return listaPuestos;
	}

	public void setListaPuestos(List<ConcursoPuestoDet> listaPuestos) {
		this.listaPuestos = listaPuestos;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public List<ConcursoPuestoAgrExc> getListaAgrExcs() {
		return listaAgrExcs;
	}

	public void setListaAgrExcs(List<ConcursoPuestoAgrExc> listaAgrExcs) {
		this.listaAgrExcs = listaAgrExcs;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	

}
