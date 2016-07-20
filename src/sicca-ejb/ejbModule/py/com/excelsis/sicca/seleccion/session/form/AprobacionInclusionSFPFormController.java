package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;
import enums.ProcesoEnum;

import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.ExcepcionDetHome;
import py.com.excelsis.sicca.seleccion.session.ExcepcionHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("aprobacionInclusionSFPFormController")
public class AprobacionInclusionSFPFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	ExcepcionHome excepcionHome;
	@In(required = false)
	ExcepcionDetHome excepcionDetHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	private Concurso concurso;
	private Excepcion excepcion;
	private ComisionSeleccionCab comisionSeleccionCab;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Entidad entidad;
	private ConfiguracionUoCab uoCab;
	ExcepcionDet excepcionDet = new ExcepcionDet();
	private String tituloPopUp;
	private String observacion;
	private String direccionFisica;
	private Date fechaObs;
	private Boolean isNew;

	private List<ComisionSeleccionDet> listaNuevosIntegrantesComision;
	private List<ExcepcionDet> listaAprobacionSfp;

	public void init() {
		concurso = new Concurso();
		excepcion = new Excepcion();
		comisionSeleccionCab = new ComisionSeleccionCab();

		excepcion = excepcionHome.getInstance();
		concurso = excepcion.getConcurso();
		comisionSeleccionCab = excepcion.getComisionSeleccionCab();
		excepcionDet = new ExcepcionDet();
		if (excepcionDetHome != null) {
			excepcionDet = excepcionDetHome.getInstance();
			if (excepcionDetHome.isIdDefined())
				fechaObs = excepcionDet.getFechaObs();
			else
				fechaObs = new Date();
		} else
			fechaObs = new Date();
		buscarDatos();
		buscarNuevosIntegrantesComision();
		buscarAprobacionSfp();
		obtenerDireccionFisica();

	}

	private void obtenerDireccionFisica() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		direccionFisica = "//SICCA//"
				+ anho
				+ "//OEE//"
				+ concurso.getConfiguracionUoCab().getIdConfiguracionUo()
				+ "//"
				+ concurso.getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + "//"
				+ concurso.getIdConcurso() + "//CS";
	}

	private void buscarDatos() {
		uoCab = new ConfiguracionUoCab();
		uoCab = concurso.getConfiguracionUoCab();
		if (uoCab != null) {
			entidad = new Entidad();
			entidad = uoCab.getEntidad();
			if (entidad != null) {
				sinEntidad = entidad.getSinEntidad();
				if (sinEntidad != null) {
					nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
					findNivelEntidad();
				}
			}
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

	@SuppressWarnings("unchecked")
	private void buscarNuevosIntegrantesComision() {
		listaNuevosIntegrantesComision = new ArrayList<ComisionSeleccionDet>();
		String sql = "select sel_det.*  "
				+ "from seleccion.comision_seleccion_det sel_det "
				+ "where sel_det.activo is true "
				+ "and sel_det.id_excepcion = " + excepcion.getIdExcepcion();

		listaNuevosIntegrantesComision = em.createNativeQuery(sql,
				ComisionSeleccionDet.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	private void buscarAprobacionSfp() {
		listaAprobacionSfp = new ArrayList<ExcepcionDet>();
		String sql = "select exc_det.* "
				+ "from seleccion.excepcion_det exc_det "
				+ "where exc_det.activo is true "
				+ "and exc_det.id_excepcion = " + excepcion.getIdExcepcion();
		listaAprobacionSfp = em.createNativeQuery(sql, ExcepcionDet.class)
				.getResultList();
	}

	public String guardarObservacion() {
		// excepcionDet = new ExcepcionDet();
		excepcionDet.setActivo(true);
		excepcionDet.setAjustadoOee(false);
		excepcionDet.setEnviadoSfp(false);
		excepcionDet.setFechaObs(fechaObs);
		excepcionDet.setUsuObs(usuarioLogueado.getCodigoUsuario());
		excepcionDet.setExcepcion(excepcion);
		try {
			em.persist(excepcionDet);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return getUrlToPageListado();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

	public String actualizarObservacion() {

		try {
			em.merge(excepcionDet);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return getUrlToPageListado();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	public String aprobar() {
		String sql = "select com_det.* "
				+ "from seleccion.comision_seleccion_det com_det "
				+ "join seleccion.excepcion exc  "
				+ "on exc.id_excepcion = com_det.id_excepcion "
				+ "where com_det.tipo = 'EXC. INCLUSION' "
				+ "and exc.id_excepcion = " + excepcion.getIdExcepcion();
		List<ComisionSeleccionDet> listaAprobar = new ArrayList<ComisionSeleccionDet>();
		listaAprobar = em.createNativeQuery(sql, ComisionSeleccionDet.class)
				.getResultList();
		for (ComisionSeleccionDet det : listaAprobar) {
			ComisionSeleccionDet comision = new ComisionSeleccionDet();
			comision.setActivo(true);
			comision.setComisionSeleccionCab(det.getComisionSeleccionCab());
			comision.setConfiguracionUo(det.getConfiguracionUo());
			comision.setDocumentos(det.getDocumentos());
			comision.setEquipoTecnico(det.getEquipoTecnico());
			comision.setFechaAlta(new Date());
			comision.setPersona(det.getPersona());
			comision.setPuesto(det.getPuesto());
			comision.setRol(det.getRol());
			comision.setSeleccionado(det.getSeleccionado());
			comision.setTipo("CONCURSO");
			comision.setTitularSuplente(det.getTitularSuplente());
			comision.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(comision);
			em.flush();
		}
		excepcion.setEstado("APROBADO");
		excepcion.setUsuAprobacion(usuarioLogueado.getCodigoUsuario());
		excepcion.setFechaAprobacion(new Date());
		try {
			em.merge(excepcion);
			em.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finalizarTarea();
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return "ok";
	}
	
	private void finalizarTarea(){
		// termina
		jbpmUtilFormController.setExcepcion(excepcion);
		jbpmUtilFormController
				.setActividadActual(ActividadEnum.EXCEP_APROBAR_SFP);
		// jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EXCEP_APROBAR_SFP);
		jbpmUtilFormController.setTransition("aprSfp_TO_end");

		if (jbpmUtilFormController.nextTask()) {
			em.flush();
		}
	}

	@SuppressWarnings("unchecked")
	public String rechazar() {
		String sql = "select exc_det.*  "
				+ "from seleccion.excepcion_det exc_det "
				+ "where exc_det.enviado_sfp is false "
				+ "and exc_det.id_excepcion = " + excepcion.getIdExcepcion()
				+ " order by exc_det.id_excepcion_det desc ";
		List<ExcepcionDet> listaExcDet = new ArrayList<ExcepcionDet>();
		listaExcDet = em.createNativeQuery(sql, ExcepcionDet.class)
				.getResultList();
		if (listaExcDet.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe registrar una Observación previamente.");
			return null;
		}
		ExcepcionDet excDet = new ExcepcionDet();
		excDet = listaExcDet.get(0);
		Excepcion exc = new Excepcion();
		exc = excDet.getExcepcion();
		exc.setEstado("PENDIENTE REVISION");
		exc.setUsuMod(usuarioLogueado.getCodigoUsuario());
		exc.setFechaMod(new Date());
		em.merge(exc);
		em.flush();
		siguienteTarea();
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return "ok";
	}
	
	private void siguienteTarea(){
		// pasa a la siguiente tarea
		jbpmUtilFormController.setExcepcion(excepcion);
		jbpmUtilFormController
				.setActividadActual(ActividadEnum.EXCEP_APROBAR_SFP);
		jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EXCEP_REVISAR_OEE);
		jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EXCEPCION);
		jbpmUtilFormController.setTransition("aprSfp_TO_revOee");

		if (jbpmUtilFormController.nextTask()) {
			em.flush();
		}
	}


	public String getUrlToPageListado() {

		return "/seleccion/aprobacionInclusionSFP/ExcepcionList.xhtml?from=seleccion/aprobacionInclusionSFP/ExcepcionDetEdit&excepcionIdExcepcion="
				+ excepcion.getIdExcepcion();

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

	public ComisionSeleccionCab getComisionSeleccionCab() {
		return comisionSeleccionCab;
	}

	public void setComisionSeleccionCab(
			ComisionSeleccionCab comisionSeleccionCab) {
		this.comisionSeleccionCab = comisionSeleccionCab;
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

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public ConfiguracionUoCab getUoCab() {
		return uoCab;
	}

	public void setUoCab(ConfiguracionUoCab uoCab) {
		this.uoCab = uoCab;
	}

	public List<ComisionSeleccionDet> getListaNuevosIntegrantesComision() {
		return listaNuevosIntegrantesComision;
	}

	public void setListaNuevosIntegrantesComision(
			List<ComisionSeleccionDet> listaNuevosIntegrantesComision) {
		this.listaNuevosIntegrantesComision = listaNuevosIntegrantesComision;
	}

	public List<ExcepcionDet> getListaAprobacionSfp() {
		return listaAprobacionSfp;
	}

	public void setListaAprobacionSfp(List<ExcepcionDet> listaAprobacionSfp) {
		this.listaAprobacionSfp = listaAprobacionSfp;
	}

	public String getTituloPopUp() {
		return tituloPopUp;
	}

	public void setTituloPopUp(String tituloPopUp) {
		this.tituloPopUp = tituloPopUp;
	}

	public Date getFechaObs() {
		return fechaObs;
	}

	public void setFechaObs(Date fechaObs) {
		this.fechaObs = fechaObs;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public ExcepcionDet getExcepcionDet() {
		return excepcionDet;
	}

	public void setExcepcionDet(ExcepcionDet excepcionDet) {
		this.excepcionDet = excepcionDet;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

}
