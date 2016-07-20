package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.dto.ReclamoSugerenciaDTO;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PostulacionAdjuntos;
import py.com.excelsis.sicca.entity.ReclamoSugerencia;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("reclamoSugerenciaComiteListFormController")
public class ReclamoSugerenciaComiteListFormController implements Serializable {

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
	

	private ConfiguracionUoCab configuracionUoCab;
	private SinNivelEntidad sinNivelEntidad = new SinNivelEntidad();
	private Persona persona;
	private ReclamoSugerencia reclamo;

	private Long idConcurso;
	private Long idPuesto;
	private String tipoReclamo;
	private Date fechaDesde;
	private Date fechaHasta;
	

	private String direccionFisica;
	private String nombrePantalla;
	private Long idDoc;
	private Long idTipoDoc;
	private Boolean marcado;
	private Boolean marcadoResponder;

	private List<SelectItem> concursoSelecItem;
	private List<SelectItem> puestoSelecItem;
	private List<ReclamoSugerenciaDTO> listaReclamoDto;
	private List<ReclamoSugerencia> listaReclamoAResponder;

	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item;
	private String nombreDoc;
	private File inputFile;

	public void init() {
		obtenerDatosUsuarioLogueado();
		persona = new Persona();
		Long id = usuarioLogueado.getPersona().getIdPersona();
		persona = em.find(Persona.class, id);
		comboConcurso();
		comboPuesto();
		searchAll();
		reclamo = new ReclamoSugerencia();

	}

	private void obtenerDatosUsuarioLogueado() {
		configuracionUoCab = em.find(ConfiguracionUoCab.class, usuarioLogueado
				.getConfiguracionUoCab().getIdConfiguracionUo());
		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
				configuracionUoCab.getEntidad().getSinEntidad().getNenCodigo());
		sinNivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
	}

	public void searchAll() {
		idConcurso = null;
		idPuesto = null;
		fechaDesde = null;
		fechaHasta = null;
		tipoReclamo = "N";
		List<ReclamoSugerencia> listaReclamos = buscarTodosLosReclamos();
		listaReclamoDto = new ArrayList<ReclamoSugerenciaDTO>();
		for (ReclamoSugerencia rec : listaReclamos) {
			ReclamoSugerenciaDTO dto = new ReclamoSugerenciaDTO();
			dto.setId(rec.getIdReclamoSugerencia());
			dto.setReclamoSugerencia(rec);
			listaReclamoDto.add(dto);
		}
	}

	public void search() {
		if (fechaDesde != null || fechaHasta != null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese ambas fechas");
			return;
		}
		if (idConcurso == null && idPuesto == null && fechaDesde == null
				&& fechaHasta == null && tipoReclamo.equals("N"))
			searchAll();
		else {
			List<ReclamoSugerencia> listaReclamos = filtrar();
			listaReclamoDto = new ArrayList<ReclamoSugerenciaDTO>();
			for (ReclamoSugerencia rec : listaReclamos) {
				ReclamoSugerenciaDTO dto = new ReclamoSugerenciaDTO();
				dto.setId(rec.getIdReclamoSugerencia());
				dto.setReclamoSugerencia(rec);
				listaReclamoDto.add(dto);
			}
		}

	}

	@SuppressWarnings("unchecked")
	private List<ReclamoSugerencia> buscarTodosLosReclamos() {
		String sql = "select recl.* "
				+ "from seleccion.reclamo_sugerencia recl "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = recl.id_postulacion "
				+ "join seleccion.persona_postulante pers_post "
				+ "on pers_post.id_persona_postulante = post.id_persona_postulante "
				+ "join general.persona p "
				+ "on p.id_persona = pers_post.id_persona "
				+ "where p.id_persona = " + persona.getIdPersona();
		return em.createNativeQuery(sql, ReclamoSugerencia.class)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<ReclamoSugerencia> filtrar() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");

		String sql = "select recl.* from seleccion.reclamo_sugerencia recl  "
				+ "join seleccion.postulacion post  "
				+ "on post.id_postulacion = recl.id_postulacion  "
				+ "join seleccion.persona_postulante pers_post  "
				+ "on pers_post.id_persona_postulante = post.id_persona_postulante  "
				+ "join general.persona p  "
				+ "on p.id_persona = pers_post.id_persona  "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = post.id_concurso_puesto_agr "
				+ "join seleccion.concurso conc  "
				+ "on conc.id_concurso = agr.id_concurso "
				+ "where p.id_persona = " + persona.getIdPersona();
		if (idPuesto != null)
			sql = sql + " and agr.id_concurso_puesto_agr = " + idPuesto;
		if (idConcurso != null)
			sql = sql + " and conc.id_concurso = " + idConcurso;
		if (!tipoReclamo.equals("N"))
			sql = sql + " and recl.reclamo_sugerencia = '" + tipoReclamo + "'";
		if (fechaDesde != null && fechaHasta != null) {
			sql = sql + " and date(recl.fecha_reclamo_sugerencia) >= to_date('"
					+ formatoDeFecha.format(fechaDesde) + "','DD-MM-YYYY') ";
			sql = sql + " and date(recl.fecha_reclamo_sugerencia) <= to_date('"
					+ formatoDeFecha.format(fechaHasta) + "','DD-MM-YYYY') ";
		}
		return em.createNativeQuery(sql, ReclamoSugerencia.class)
				.getResultList();

	}

	/**
	 * Combo Concurso
	 */
	public void comboConcurso() {
		List<Concurso> concursoList = getConcursoByConfiguracionUo();
		concursoSelecItem = new ArrayList<SelectItem>();
		buildConcursoSelectItem(concursoList);
		idConcurso = null;
		idPuesto = null;
	}

	@SuppressWarnings("unchecked")
	private List<Concurso> getConcursoByConfiguracionUo() {
		String sql = "select conc.* " + "from seleccion.concurso conc "
				+ "where conc.activo is true "
				+ "and conc.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " order by conc.nombre";
		return em.createNativeQuery(sql, Concurso.class).getResultList();

	}

	private void buildConcursoSelectItem(List<Concurso> concursoList) {
		if (concursoSelecItem == null)
			concursoSelecItem = new ArrayList<SelectItem>();
		else
			concursoSelecItem.clear();

		concursoSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Concurso c : concursoList) {
			concursoSelecItem.add(new SelectItem(c.getIdConcurso(), c
					.getNombre()));
		}
	}

	/**
	 * Combo Puesto
	 */
	public void comboPuesto() {
		List<ConcursoPuestoAgr> concursoPuestoList = getConcursoPuestoByConcurso();
		puestoSelecItem = new ArrayList<SelectItem>();
		buildPuestoSelectItem(concursoPuestoList);
		idPuesto = null;
	}

	private void buildPuestoSelectItem(List<ConcursoPuestoAgr> grupoList) {
		if (puestoSelecItem == null)
			puestoSelecItem = new ArrayList<SelectItem>();
		else
			puestoSelecItem.clear();
		puestoSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (ConcursoPuestoAgr agr : grupoList) {
			puestoSelecItem.add(new SelectItem(agr.getIdConcursoPuestoAgr(),
					agr.getDescripcionGrupo()));
		}
	}

	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoAgr> getConcursoPuestoByConcurso() {
		if (idConcurso != null) {
			String sql = "select distinct(agr.*) "
					+ "from seleccion.concurso_puesto_agr agr "
					+ "join seleccion.comision_grupo com_grupo "
					+ "on com_grupo.id_concurso_puesto_agr = agr.id_concurso_puesto_agr "
					+ "join seleccion.comision_seleccion_cab sel_cab "
					+ "on sel_cab.id_comision_sel = com_grupo.id_comision_cab "
					+ "join seleccion.comision_seleccion_det sel_det "
					+ "on sel_det.id_comision_sel = sel_cab.id_comision_sel "
					+ "join seleccion.concurso conc "
					+ "on conc.id_concurso = sel_cab.id_concurso "
					+ "where agr.activo is true " + "and conc.id_concurso = "
					+ idConcurso;
			return em.createNativeQuery(sql, ConcursoPuestoAgr.class)
					.getResultList();

		}
		return new ArrayList<ConcursoPuestoAgr>();
	}

	public String obtenerCodigoConcurso(ReclamoSugerencia reclamo) {
		Concurso concursoActual = new Concurso();
		concursoActual = reclamo.getPostulacion().getConcursoPuestoAgr()
				.getConcurso();
		return concursoActual.getNumero() + "/" + concursoActual.getAnhio()
				+ " de " + configuracionUoCab.getDescripcionCorta();
	}

	@SuppressWarnings("unchecked")
	public String obtenerEstadoConcurso(ReclamoSugerencia reclamo) {
		String sql = "select r.*  "
				+ "from seleccion.referencias r  "
				+ "where dominio = 'ESTADOS_CONCURSO' "
				+ "and r.valor_num = "
				+ reclamo.getPostulacion().getConcursoPuestoAgr().getConcurso()
						.getEstado();
		List<Referencias> listaRef = new ArrayList<Referencias>();
		listaRef = em.createNativeQuery(sql, Referencias.class).getResultList();
		if (listaRef.size() > 0)
			return listaRef.get(0).getDescAbrev();
		return "";
	}



	public void check(Integer row) {
		if (listaReclamoDto.get(row).getVer())
			marcado = true;
		else
			marcado = false;
	}

	public void checkResponder(Integer row) {
		if (listaReclamoDto.get(row).getResponder())
			marcadoResponder = true;
		else
			marcadoResponder = false;
	}

	/**
	 * Prepara la lista para mostrar de todos los reclamos a responder
	 */
	public String prepararListaAResponder() {
		Long idReclamo = null;
		listaReclamoAResponder = new ArrayList<ReclamoSugerencia>();
		for (ReclamoSugerenciaDTO dto : listaReclamoDto) {
			if (dto.getResponder() != null && dto.getResponder()){
				listaReclamoAResponder.add(dto.getReclamoSugerencia());
				idReclamo = dto.getId();
			}
		}
		if (listaReclamoAResponder.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Seleccione el registro a responder");
			return null;
		}
		return getUrlToPageReclamoEdit(idReclamo);
	}
	
	public String prepararListaAVer() {
		Long idReclamo = null;
		List<ReclamoSugerencia> listaVer = new ArrayList<ReclamoSugerencia>();
		for (ReclamoSugerenciaDTO dto : listaReclamoDto) {
			if (dto.getVer() != null && dto.getVer()){
				listaVer.add(dto.getReclamoSugerencia());
				idReclamo = dto.getId();
			}
		}
		if (listaVer.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Seleccione el registro a ver");
			return null;
		}
		return getUrlToPageReclamo(idReclamo);
	}
	
	
	public String getUrlToPageReclamoEdit(Long idReclamo) {
		
		return "/seleccion/reclamoSugerenciaPorComite/ReclamoSugerenciaEdit.xhtml?fromToPage=seleccion/reclamoSugerenciaPorComite/ReclamoSugerenciaList&reclamoSugerenciaIdReclamoSugerencia="
		+ idReclamo;
	}
	
public String getUrlToPageReclamo(Long idReclamo) {
		
		return "/seleccion/reclamoSugerenciaPorComite/ReclamoSugerencia.xhtml?fromToPage=seleccion/reclamoSugerenciaPorComite/ReclamoSugerenciaList&reclamoSugerenciaIdReclamoSugerencia="
		+ idReclamo;
	}


	public void verReclamoAResponder(ReclamoSugerencia rec) {
		reclamo = new ReclamoSugerencia();
		Long id = rec.getIdReclamoSugerencia();
		reclamo = em.find(ReclamoSugerencia.class, id);

	}

	
	

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public List<SelectItem> getConcursoSelecItem() {
		return concursoSelecItem;
	}

	public void setConcursoSelecItem(List<SelectItem> concursoSelecItem) {
		this.concursoSelecItem = concursoSelecItem;
	}

	public String getTipoReclamo() {
		return tipoReclamo;
	}

	public void setTipoReclamo(String tipoReclamo) {
		this.tipoReclamo = tipoReclamo;
	}

	public Long getIdPuesto() {
		return idPuesto;
	}

	public void setIdPuesto(Long idPuesto) {
		this.idPuesto = idPuesto;
	}

	public List<SelectItem> getPuestoSelecItem() {
		return puestoSelecItem;
	}

	public void setPuestoSelecItem(List<SelectItem> puestoSelecItem) {
		this.puestoSelecItem = puestoSelecItem;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public List<ReclamoSugerenciaDTO> getListaReclamoDto() {
		return listaReclamoDto;
	}

	public void setListaReclamoDto(List<ReclamoSugerenciaDTO> listaReclamoDto) {
		this.listaReclamoDto = listaReclamoDto;
	}

	public List<ReclamoSugerencia> getListaReclamoAResponder() {
		return listaReclamoAResponder;
	}

	public void setListaReclamoAResponder(
			List<ReclamoSugerencia> listaReclamoAResponder) {
		this.listaReclamoAResponder = listaReclamoAResponder;
	}

	public ReclamoSugerencia getReclamo() {
		return reclamo;
	}

	public void setReclamo(ReclamoSugerencia reclamo) {
		this.reclamo = reclamo;
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

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public Boolean getMarcado() {
		return marcado;
	}

	public void setMarcado(Boolean marcado) {
		this.marcado = marcado;
	}

	public Boolean getMarcadoResponder() {
		return marcadoResponder;
	}

	public void setMarcadoResponder(Boolean marcadoResponder) {
		this.marcadoResponder = marcadoResponder;
	}

}
