package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.UnidadPlazo;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.TransicionEstado;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EmpleadoPuestoList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("contratosAVencerListFormController")
public class ContratosAVencerListFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	EmpleadoPuestoList empleadoPuestoList;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(required = false, create = true)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private Persona persona = new Persona();
	private PlantaCargoDet cargo;

	private List<SelectItem> estadosTransicionSelecItem;
	private List<EmpleadoPuesto> listaEmpleados;

	private Integer cantDiasVto;
	private Long idEstadoSeleccionado;
	private String observacion;

	public void init() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
	}

	public void cargarCabecera() {
		grupoPuestosController = (GrupoPuestosController) Component
				.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();

		SinNivelEntidad sinNivelEntidad = grupoPuestosController
				.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		ConfiguracionUoCab configuracionUoCab = grupoPuestosController
				.getConfiguracionUoCab();

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad
				.getIdSinNivelEntidad());

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab
				.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

		estadosTransicionSelecItem = new ArrayList<SelectItem>();
		estadosTransicionSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));

		searchAll();

	}

	public void searchAll() {
		listaEmpleados = new ArrayList<EmpleadoPuesto>();
		empleadoPuestoList.setSinEntidad(em.find(SinEntidad.class,
				nivelEntidadOeeUtil.getIdSinEntidad()));
		empleadoPuestoList.setSinNivelEntidad(em.find(SinNivelEntidad.class,
				nivelEntidadOeeUtil.getIdSinNivelEntidad()));
		empleadoPuestoList
				.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class,
						nivelEntidadOeeUtil.getIdConfigCab()));

		listaEmpleados = empleadoPuestoList.listaResultadosCU429();
	}

	public void clean() {
		cantDiasVto = null;
		persona = new Persona();
		nivelEntidadOeeUtil.limpiar();
		cargarCabecera();
		searchAll();
	}

	public void search() {
		listaEmpleados = new ArrayList<EmpleadoPuesto>();
		empleadoPuestoList.setSinEntidad(em.find(SinEntidad.class,
				nivelEntidadOeeUtil.getIdSinEntidad()));
		empleadoPuestoList.setSinNivelEntidad(em.find(SinNivelEntidad.class,
				nivelEntidadOeeUtil.getIdSinNivelEntidad()));
		empleadoPuestoList
				.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class,
						nivelEntidadOeeUtil.getIdConfigCab()));

		if (persona != null
				&& ((persona.getNombres() != null && !persona.getNombres()
						.trim().isEmpty())
						|| (persona.getNombres() != null && !persona
								.getNombres().trim().isEmpty())
						|| (persona.getNombres() != null && !persona
								.getApellidos().trim().isEmpty())
						|| (persona.getApellidos() != null && !persona
								.getApellidos().trim().isEmpty()) || (persona
						.getDocumentoIdentidad() != null && !persona
						.getDocumentoIdentidad().trim().isEmpty())))
			empleadoPuestoList.setPersona(persona);
		if (nivelEntidadOeeUtil != null
				&& nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			empleadoPuestoList.setConfiguracionUoDet(em.find(
					ConfiguracionUoDet.class,
					nivelEntidadOeeUtil.getIdUnidadOrganizativa()));

		if (cantDiasVto != null && cantDiasVto > 0) {
			Calendar hoy = Calendar.getInstance();
			Calendar proximo = Calendar.getInstance();
			proximo.add(Calendar.DAY_OF_MONTH, cantDiasVto);
			empleadoPuestoList.setFechaDesde(hoy.getTime());
			empleadoPuestoList.setFechaHasta(proximo.getTime());
		}
		listaEmpleados = empleadoPuestoList.listaResultadosCU429();
	}

	/**
	 * Método que obtiene el codigo del puesto
	 */
	public String obtenerCodigoPuesto(PlantaCargoDet det) {
		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = det.getConfiguracionUoDet();
		String code = "";
		code += findCodNivelEntidad(uoDet.getConfiguracionUoCab().getEntidad()
				.getSinEntidad().getNenCodigo())
				+ ".";
		code += uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad()
				.getEntCodigo()
				+ ".";
		code += uoDet.getConfiguracionUoCab().getOrden() + ".";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";
		}
		if (det.getContratado())
			code = code + "C";
		if (det.getPermanente())
			code = code + "P";
		code = code + det.getOrden();
		return code;
	}

	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	public String findCodNivelEntidad(BigDecimal nencod) {
		SinNivelEntidad nivel = new SinNivelEntidad();
		if (nencod != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nencod);
			nivel = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			return "";
		return nivel.getNenCodigo() + "";
	}

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet,
			List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);

		return listCodigos;
	}

	public String obtenerCodigo(ConfiguracionUoDet uoDet) {
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";
		}
		code = code.substring(0, code.length() - 1);
		return code;
	}

	@SuppressWarnings("unchecked")
	public void buscarEstadosPosibles(Integer row) {
		EmpleadoPuesto emp = new EmpleadoPuesto();
		emp = listaEmpleados.get(row);
		Long id = emp.getPlantaCargoDet().getIdPlantaCargoDet();
		estadosTransicionSelecItem = new ArrayList<SelectItem>();
		estadosTransicionSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));

		cargo = new PlantaCargoDet();
		cargo = em.find(PlantaCargoDet.class, id);

		String cadena = "select tran.* from planificacion.transicion_estado tran "
				+ "where tran.id_estado_cab = "
				+ cargo.getEstadoCab().getIdEstadoCab();

		List<TransicionEstado> lista = new ArrayList<TransicionEstado>();
		lista = em.createNativeQuery(cadena, TransicionEstado.class)
				.getResultList();
		if (lista.size() > 0) {
			for (TransicionEstado t : lista) {
				estadosTransicionSelecItem.add(new SelectItem(t
						.getEstadoCabByPosibleEstado().getIdEstadoCab(), t
						.getEstadoCabByPosibleEstado().getDescripcion()));
			}

		}
	}

	public void save() {
		EstadoCab cab = new EstadoCab();
		cab = em.find(EstadoCab.class, idEstadoSeleccionado);
		cargo.setUsuMod(usuarioLogueado.getCodigoUsuario());
		cargo.setFechaMod(new Date());
		cargo.setEstadoCab(cab);
		try {
			em.merge(cargo);
			HistoricosEstado historico = new HistoricosEstado();
			historico.setEstadoCab(cab);
			historico.setFechaMod(new Date());
			if (observacion != null && !observacion.trim().isEmpty())
				historico.setObservacion(observacion.trim().toUpperCase());
			historico.setPlantaCargoDet(cargo);
			historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.persist(historico);
			em.flush();
			observacion = null;
			idEstadoSeleccionado = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		clean();

	}

	/**
	 * Getters y Setters
	 * 
	 * @return
	 */

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Integer getCantDiasVto() {
		return cantDiasVto;
	}

	public void setCantDiasVto(Integer cantDiasVto) {
		this.cantDiasVto = cantDiasVto;
	}

	public Long getIdEstadoSeleccionado() {
		return idEstadoSeleccionado;
	}

	public void setIdEstadoSeleccionado(Long idEstadoSeleccionado) {
		this.idEstadoSeleccionado = idEstadoSeleccionado;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public List<SelectItem> getEstadosTransicionSelecItem() {
		return estadosTransicionSelecItem;
	}

	public void setEstadosTransicionSelecItem(
			List<SelectItem> estadosTransicionSelecItem) {
		this.estadosTransicionSelecItem = estadosTransicionSelecItem;
	}

	public List<EmpleadoPuesto> getListaEmpleados() {
		return listaEmpleados;
	}

	public void setListaEmpleados(List<EmpleadoPuesto> listaEmpleados) {
		this.listaEmpleados = listaEmpleados;
	}

	public PlantaCargoDet getCargo() {
		return cargo;
	}

	public void setCargo(PlantaCargoDet cargo) {
		this.cargo = cargo;
	}

}
