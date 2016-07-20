package py.com.excelsis.sicca.reportes.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("consultaOeeUnidadesPuestoReportController")
public class ConsultaOeeUnidadesPuestoReportController implements Serializable {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(required = false)
	Usuario usuarioLogueado;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Entidad entidad = new Entidad();

	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Integer anhoActual;
	private int selectedRow = -1;
	private int selectedRowEdit = -1;

	private List<ConfiguracionUoCab> listaOee = new ArrayList<ConfiguracionUoCab>();
	private List<ConfiguracionUoDet> listaUnidadesOrgs = new ArrayList<ConfiguracionUoDet>();
	private List<EmpleadoPuesto> listaPuestos = new ArrayList<EmpleadoPuesto>();

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		if (listaOee == null || listaOee.size() == 0) {
			selectedRow = -1;
			Date fechaActual = new Date();
			anhoActual = fechaActual.getYear() + 1900;
			if (idSinNivelEntidad != null)
				nivelEntidad = em
						.find(SinNivelEntidad.class, idSinNivelEntidad);
			if (idSinEntidad != null)
				sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		}
	}

	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	public void findNivelEntidad() {
		sinEntidad = new SinEntidad();
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
			if (nivelEntidad != null)
				idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();
			else {
				nivelEntidad = new SinNivelEntidad();
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	/**
	 * Método que busca la entidad correspondiente al codigo ingresado y el
	 * nivel
	 */
	public void findEntidad() {
		try {
			if (nivelEntidad.getNenCodigo() != null
					&& sinEntidad.getEntCodigo() != null) {
				sinEntidadList.getSinEntidad().setEntCodigo(
						sinEntidad.getEntCodigo());
				sinEntidadList.getSinEntidad().setNenCodigo(
						nivelEntidad.getNenCodigo());
				sinEntidad = sinEntidadList.entidadMaxAnho();

				if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null) {
					idSinEntidad = sinEntidad.getIdSinEntidad();
					entidadList.getSinEntidad().setIdSinEntidad(
							sinEntidad.getIdSinEntidad());
					entidad = entidadList.getEntidadBySinEntidad();
				} else {
					sinEntidad = new SinEntidad();
					entidad = new Entidad();
					statusMessages.add(Severity.ERROR,
							SICCAAppHelper.getBundleMessage("nivel_msg_1"));
					return;
				}

			}
		} catch (Exception e) {
			sinEntidad = new SinEntidad();
			entidad = new Entidad();
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("nivel_msg_1"));
			return;
		}

	}

	/**
	 * Metodo que llama al buscador de entidades
	 * 
	 * @return
	 */
	public String getUrlToPageEntidad() {
		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=reports/reportes/ConsultarOEEUnidadesPuestos&codigoNivel="
				+ nivelEntidad.getNenCodigo();
	}

	public void limpiar() {
		idSinEntidad = null;
		idSinNivelEntidad = null;
		nivelEntidad = new SinNivelEntidad();
		sinEntidad = new SinEntidad();
		listaOee = new ArrayList<ConfiguracionUoCab>();
		listaPuestos = new ArrayList<EmpleadoPuesto>();
		listaUnidadesOrgs = new ArrayList<ConfiguracionUoDet>();
		selectedRow = -1;
	}

	@SuppressWarnings("unchecked")
	public void buscarListaOee() {
		selectedRow = -1;
		String sql = "select oee.* "
				+ "from sinarh.sin_nivel_entidad nivel, sinarh.sin_entidad sin_entidad "
				+ "inner join planificacion.entidad entidad  "
				+ "on entidad.id_sin_entidad = sin_entidad.id_sin_entidad "
				+ "inner join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_entidad = entidad.id_entidad "
				+ "where nivel.nen_codigo = sin_entidad.nen_codigo "
				+ "and oee.activo is true ";
		if ((nivelEntidad != null && nivelEntidad.getIdSinNivelEntidad() != null)
				|| (sinEntidad != null && sinEntidad.getIdSinEntidad() != null)) {
			if (nivelEntidad != null
					&& nivelEntidad.getIdSinNivelEntidad() != null)
				sql += " and nivel.id_sin_nivel_entidad = "
						+ nivelEntidad.getIdSinNivelEntidad();
			if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null)
				sql += " and sin_entidad.id_sin_entidad = "
						+ sinEntidad.getIdSinEntidad();
			sql += " and sin_entidad.ani_aniopre = " + anhoActual;
			sql += " order by sin_entidad.ent_codigo, oee.orden ";
			listaOee = new ArrayList<ConfiguracionUoCab>();
			listaOee = em.createNativeQuery(sql, ConfiguracionUoCab.class)
					.getResultList();
		} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Ingrese los datos a filtrar", "No hay datos..."));
			return;
		}
	}

	@SuppressWarnings("unchecked")
	public void buscarListaUnidadOrganizativa(Long id) {

		for (int i = 0; i < listaOee.size(); i++) {
			ConfiguracionUoCab o = new ConfiguracionUoCab();
			o = listaOee.get(i);
			if (o.getIdConfiguracionUo().equals(id)) {
				selectedRow = i;
			}
		}
		String sql = "select uo_det.* "
				+ "from planificacion.configuracion_uo_det uo_det "
				+ "join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_configuracion_uo = uo_det.id_configuracion_uo "
				+ "where uo_det.activo is true "
				+ "and oee.id_configuracion_uo = "
				+ id
				+ " order by uo_det.id_configuracion_uo_det_padre, uo_det.orden ";
		listaUnidadesOrgs = new ArrayList<ConfiguracionUoDet>();
		listaUnidadesOrgs = em.createNativeQuery(sql, ConfiguracionUoDet.class)
				.getResultList();
		listaPuestos = new ArrayList<EmpleadoPuesto>();
	}

	@SuppressWarnings("unchecked")
	public void buscarListaPuestos(Long id) {
		String sql = "select emp.* "
				+ "from general.empleado_puesto emp "
				+ "join planificacion.planta_cargo_det puesto "
				+ "on emp.id_planta_cargo_det = puesto.id_planta_cargo_det "
				+ "join planificacion.configuracion_uo_det uo_det "
				+ "on uo_det.id_configuracion_uo_det = puesto.id_configuracion_uo_det "
				+ "where emp.actual is true " + "and emp.activo is true "
				+ "and uo_det.id_configuracion_uo_det = " + id;
		listaPuestos = new ArrayList<EmpleadoPuesto>();
		listaPuestos = em.createNativeQuery(sql, EmpleadoPuesto.class)
				.getResultList();
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

	public String codigos(Long id) {
		ConfiguracionUoCab c = em.find(ConfiguracionUoCab.class, id);
		String codigo = "";
		codigo = c.getEntidad().getSinEntidad().getNenCodigo() + "."
				+ c.getEntidad().getSinEntidad().getEntCodigo() + "."
				+ c.getOrden();
		return codigo;
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

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet,
			List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);
		else {
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getOrden());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad()
					.getSinEntidad().getEntCodigo().intValue());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad()
					.getSinEntidad().getNenCodigo().intValue());
		}
		return listCodigos;
	}

	public String getRowClass(int currentRow) {
		if (selectedRow == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}

	public String getRowEditClass(int currentRow) {
		if (selectedRowEdit == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
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

	public Long getIdSinNivelEntidad() {
		return idSinNivelEntidad;
	}

	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	public List<ConfiguracionUoCab> getListaOee() {
		return listaOee;
	}

	public void setListaOee(List<ConfiguracionUoCab> listaOee) {
		this.listaOee = listaOee;
	}

	public List<ConfiguracionUoDet> getListaUnidadesOrgs() {
		return listaUnidadesOrgs;
	}

	public void setListaUnidadesOrgs(List<ConfiguracionUoDet> listaUnidadesOrgs) {
		this.listaUnidadesOrgs = listaUnidadesOrgs;
	}

	public List<EmpleadoPuesto> getListaPuestos() {
		return listaPuestos;
	}

	public void setListaPuestos(List<EmpleadoPuesto> listaPuestos) {
		this.listaPuestos = listaPuestos;
	}

	public Integer getAnhoActual() {
		return anhoActual;
	}

	public void setAnhoActual(Integer anhoActual) {
		this.anhoActual = anhoActual;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public int getSelectedRowEdit() {
		return selectedRowEdit;
	}

	public void setSelectedRowEdit(int selectedRowEdit) {
		this.selectedRowEdit = selectedRowEdit;
	}

}
