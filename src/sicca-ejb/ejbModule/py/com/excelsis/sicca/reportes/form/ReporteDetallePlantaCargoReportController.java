package py.com.excelsis.sicca.reportes.form;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.PlantaCargoDetList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("reporteDetallePlantaCargoReportController")
public class ReporteDetallePlantaCargoReportController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	PlantaCargoDetList plantaCargoDetList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(required = false)
	Usuario usuarioLogueado;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();
	private Persona persona = new Persona();
	private Cpt cpt = new Cpt();

	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Long idConfigCab;
	private Long idConfiguracionUoDet;
	private Long idTipoCpt;
	private Long idCpt;
	private Integer anhoActual;

	private String codigoUnidOrgDep;
	private String codigoCpt;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		Date fechaActual = new Date();
		anhoActual = fechaActual.getYear() + 1900;
		if (idSinNivelEntidad != null)
			nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		if (idSinEntidad != null)
			sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		if (idConfigCab != null)
			configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);
		if (idConfiguracionUoDet != null) {
			configuracionUoDet = em.find(ConfiguracionUoDet.class,
					idConfiguracionUoDet);
			codigoUnidOrgDep = obtenerCodigo(configuracionUoDet);
		}

		if (idCpt != null) {
			cpt = em.find(Cpt.class, idCpt);
			buscarCodigoCpt();
		}

	}

	/**
	 * Método que obtiene el codigo correspondiente a una Unidad Organizativa
	 * 
	 * @param uoDet
	 * @return
	 */
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

		return listCodigos;
	}

	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
			if (nivelEntidad != null)
				idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();
			else {
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
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}

		}
	}

	/**
	 * Método que busca la unidad organizativa correspondiente al codigo
	 * ingresado, a la entidad y al nivel
	 */
	public void findUnidadOrganizativa() {
		if (configuracionUoCab != null && configuracionUoCab.getOrden() != null) {
			/*
			 * configuracionUoCabList.getConfiguracionUoCab().setOrden(
			 * configuracionUoCab.getOrden()); if (entidad != null) { Long id =
			 * entidad.getIdEntidad(); configuracionUoCabList.setIdEntidad(id);
			 * }
			 * 
			 * configuracionUoCab = configuracionUoCabList.searchUnidad();
			 */
			configuracionUoCab = obtenerOeeCorrespondiente();
			if (configuracionUoCab != null)
				idConfigCab = configuracionUoCab.getIdConfiguracionUo();
			else {
				configuracionUoCab = new ConfiguracionUoCab();
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}

		} else
			configuracionUoCab = new ConfiguracionUoCab();
	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoCab obtenerOeeCorrespondiente() {
		String sql = "select uo_cab.*  "
				+ "from planificacion.configuracion_uo_cab uo_cab "
				+ "join planificacion.entidad entidad "
				+ "on entidad.id_entidad = uo_cab.id_entidad "
				+ "join sinarh.sin_entidad sin_ent "
				+ "on sin_ent.id_sin_entidad = entidad.id_sin_entidad "
				+ "where  sin_ent.id_sin_entidad = "
				+ sinEntidad.getIdSinEntidad() + " and uo_cab.orden = "
				+ configuracionUoCab.getOrden() + " and sin_ent.ani_aniopre = "
				+ anhoActual;
		List<ConfiguracionUoCab> lista = new ArrayList<ConfiguracionUoCab>();
		lista = em.createNativeQuery(sql, ConfiguracionUoCab.class)
				.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		else
			return null;
	}

	/**
	 * Método que busca la unidad organizativa dependiente de la unidad
	 * organizativa cabeza, la entidad el nivel y el codigo ingresado
	 */
	public void obtenerUnidadOrganizativaDep() {
		try {
			if (codigoUnidOrgDep != null && !codigoUnidOrgDep.trim().isEmpty()) {
				String[] arrayCodigo = codigoUnidOrgDep.split("\\.");
				if (arrayCodigo.length > 3) {
					Integer orden = new Integer(arrayCodigo[3]);
					Integer tam = arrayCodigo.length;

					configuracionUoDet = new ConfiguracionUoDet();

					configuracionUoDet = buscarDetalle(configuracionUoCab,
							orden);

					ConfiguracionUoDet det = new ConfiguracionUoDet();
					if (tam == 1)
						det = null;
					for (int i = 4; i < arrayCodigo.length; i++) {
						Integer ord = new Integer(arrayCodigo[i]);

						det = buscarDetalle(configuracionUoDet, ord);
						if (det != null)
							configuracionUoDet = det;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("nivel_msg_1"));
			return;
		}
	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoCab padre,
			Integer orden) {
		String cad = "select det.* from planificacion.configuracion_uo_det det "
				+ " where det.id_configuracion_uo = "
				+ padre.getIdConfiguracionUo()
				+ " and det.orden = "
				+ orden
				+ " and det.id_configuracion_uo_det_padre is null";
		List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
		lista = em.createNativeQuery(cad, ConfiguracionUoDet.class)
				.getResultList();
		ConfiguracionUoDet actual = new ConfiguracionUoDet();
		if (lista.size() > 0) {
			actual = lista.get(0);
			return actual;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoDet padre,
			Integer orden) {
		if (padre != null) {
			String cad = "select det.* from planificacion.configuracion_uo_det det "
					+ " where det.id_configuracion_uo_det_padre = "
					+ padre.getIdConfiguracionUoDet()
					+ " and det.orden = "
					+ orden;
			List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
			lista = em.createNativeQuery(cad, ConfiguracionUoDet.class)
					.getResultList();
			ConfiguracionUoDet actual = new ConfiguracionUoDet();
			if (lista.size() > 0) {
				actual = lista.get(0);
				return actual;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void buscarCodigoCpt() {
		String sql = "select cpt.* from planificacion.cpt cpt "
				+ "where cpt.id_cpt = " + cpt.getIdCpt();

		List<Cpt> lista = new ArrayList<Cpt>();
		lista = em.createNativeQuery(sql, Cpt.class).getResultList();
		codigoCpt = cpt.getNivel() + "."
				+ cpt.getGradoSalarialMin().getNumero() + "."
				+ cpt.getGradoSalarialMax().getNumero() + "." + cpt.getNumero()
				+ "." + cpt.getNroEspecifico();
	}

	@SuppressWarnings("unchecked")
	public void findCpt() {
		try {
			if (codigoCpt != null && !codigoCpt.equals("")) {
				Integer nivelCpt = null;
				Integer gradoMin = null;
				Integer gradoMax = null;
				Integer numero = null;
				Integer nroEspecifico = null;
				String[] arrayCodigo = codigoCpt.split("\\.");
				for (int i = 0; i < arrayCodigo.length; i++) {
					if (i == 0)
						nivelCpt = new Integer(arrayCodigo[i]);
					if (i == 1)
						gradoMin = new Integer(arrayCodigo[i]);
					if (i == 2)
						gradoMax = new Integer(arrayCodigo[i]);
					if (i == 3)
						numero = new Integer(arrayCodigo[i]);
					if (i == 4)
						nroEspecifico = new Integer(arrayCodigo[i]);
				}

				String cadena = " select cpt.* from planificacion.cpt cpt "
						+ "join planificacion.grado_salarial max "
						+ "on max.id_grado_salarial = cpt.id_grado_salarial_max "
						+ "join planificacion.grado_salarial min "
						+ "on min.id_grado_salarial = cpt.id_grado_salarial_min "
						+ "where cpt.nivel = " + nivelCpt
						+ " and max.numero = " + gradoMax
						+ " and min.numero = " + gradoMin
						+ " and cpt.numero = " + numero
						+ " and cpt.nro_especifico = " + nroEspecifico;
				if (idTipoCpt != null)
					cadena = cadena + " and id_tipo_cpt = " + idTipoCpt;
				List<Cpt> lista = new ArrayList<Cpt>();
				lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
				if (lista.size() > 0)
					cpt = lista.get(0);
				else
					cpt = new Cpt();
			}

		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("nivel_msg_1"));
		}

	}

	public void limpiarDatosCpt() {
		codigoCpt = null;
		cpt = new Cpt();
		idCpt = null;
	}

	public void limpiar() {
		persona = new Persona();
		codigoUnidOrgDep = null;
		configuracionUoCab = new ConfiguracionUoCab();
		configuracionUoDet = new ConfiguracionUoDet();
		entidad = new Entidad();
		idConfigCab = null;
		idConfiguracionUoDet = null;
		idSinEntidad = null;
		idSinNivelEntidad = null;
		nivelEntidad = new SinNivelEntidad();
		sinEntidad = new SinEntidad();
		codigoCpt = null;
		cpt = new Cpt();
		idCpt = null;
		idTipoCpt = null;
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
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=reports/reportes/ReporteDetallePlantaCargo&codigoNivel="
				+ nivelEntidad.getNenCodigo();
	}

	/**
	 * Método que llama al buscador de OEE's
	 * 
	 * @return
	 */
	public String getUrlToPageOee() {
		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		if (idSinEntidad == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}
		sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		String retorno = "/planificacion/searchForms/FindDependencias.xhtml?from=reports/reportes/ReporteDetallePlantaCargo&sinNivelEntidadIdSinNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad();
		if (idSinEntidad != null)
			retorno = retorno + "&sinEntidadIdSinEntidad="
					+ sinEntidad.getIdSinEntidad();
		retorno += "&anho=" + anhoActual;
		return retorno;
	}

	/**
	 * Método que llama al buscador de Unidades Organizativas Dependientes
	 * 
	 * @return
	 */
	public String getUrlToPageDependencia() {
		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);

		if (idSinEntidad == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}
		sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		if (configuracionUoCab == null
				|| configuracionUoCab.getIdConfiguracionUo() == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Cabecera_msg_oee"));
			return null;
		}
		if (idConfigCab != null)
			configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);

		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=reports/reportes/ReporteDetallePlantaCargo&idNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad()
				+ "&sinEntidadIdSinEntidad="
				+ sinEntidad.getIdSinEntidad()
				+ "&idConfiguracionUoCab="
				+ configuracionUoCab.getIdConfiguracionUo();
	}

	public String getUrlToPageCpt() {
		if (idTipoCpt == null) {
			statusMessages.add(Severity.ERROR, "Seleccione un Tipo CPT");
			return null;
		}
		return "/planificacion/searchForms/CptList.xhtml?from=reports/reportes/ReporteDetallePlantaCargo&tipoCpt="
				+ idTipoCpt;

	}

	/**
	 * Es llamado desde el boton Imprimir
	 * 
	 * @throws Exception
	 */
	public void print() throws Exception {
		List<Object[]> lista = consulta();
		if (lista == null || lista.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
			return;
		}
		Connection conn = JpaResourceBean.getConnection();
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("REPORT_CONNECTION", conn);
		String elDetalle = obtenerSqlDetalle();
		if (elDetalle == null)
			return;
		param.put("sql", elDetalle);

		for (Object[] obj : lista) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[0] != null)
				map.put("id", new Long(obj[0].toString()));
			if (obj[1] != null)
				map.put("entidad", obj[1].toString());
			if (obj[2] != null)
				map.put("cod_entidad", obj[2].toString());
			if (obj[3] != null)
				map.put("desc_oee", obj[3].toString());
			if (obj[4] != null)
				map.put("cod_oee", obj[4].toString());

			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("RPT_CU129_Detalle_Planta_Cargo", false,
				listaDatosReporte, param);

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * busca los datos para ser enviados en el reporte
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> consulta() {
		String sql = "SELECT distinct(oee.id_configuracion_uo) as id ,sin_entidad.ent_nombre as entidad, "
				+ "nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo as cod_entidad, "
				+ "oee.denominacion_unidad as desc_oee, nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo ||'.'||oee.orden as cod_oee "
				+ "FROM sinarh.sin_nivel_entidad nivel, sinarh.sin_entidad sin_entidad "
				+ "inner join planificacion.entidad entidad "
				+ "on entidad.id_sin_entidad = sin_entidad.id_sin_entidad "
				+ "inner join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_entidad = entidad.id_entidad "
				+ "inner join planificacion.configuracion_uo_det uo "
				+ "on uo.id_configuracion_uo = oee.id_configuracion_uo "
				+ "inner join planificacion.planta_cargo_det puesto "
				+ "on puesto.id_configuracion_uo_det = uo.id_configuracion_uo_det "
				+ "inner join planificacion.cpt cpt "
				+ "on cpt.id_cpt = puesto.id_cpt "
				+ "inner join general.empleado_puesto emp "
				+ "on emp.id_planta_cargo_det = puesto.id_planta_cargo_det "
				+ "inner join general.persona persona "
				+ "on emp.id_persona = persona.id_persona "
				+ "inner join planificacion.grado_salarial grado_min "
				+ "on cpt.id_grado_salarial_min = grado_min.id_grado_salarial "
				+ "inner join planificacion.grado_salarial grado_max "
				+ "on cpt.id_grado_salarial_max = grado_max.id_grado_salarial "
				+ "WHERE emp.actual is true "
				+ "and nivel.nen_codigo = sin_entidad.nen_codigo";
		if (nivelEntidad != null && nivelEntidad.getIdSinNivelEntidad() != null)
			sql = sql + " and nivel.id_sin_nivel_entidad = "
					+ nivelEntidad.getIdSinNivelEntidad();
		if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null)
			sql = sql + " and sin_entidad.id_sin_entidad = "
					+ sinEntidad.getIdSinEntidad();
		if (configuracionUoCab != null
				&& configuracionUoCab.getIdConfiguracionUo() != null)
			sql = sql + "  oee.id_configuracion_uo = "
					+ configuracionUoCab.getIdConfiguracionUo();
		if (configuracionUoDet != null
				&& configuracionUoDet.getIdConfiguracionUoDet() != null)
			sql = sql + " and uo.id_configuracion_uo_det = "
					+ configuracionUoDet.getIdConfiguracionUoDet();

		try {

			List<Object[]> config = em.createNativeQuery(sql).getResultList();
			if (config == null || config.size() == 0) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private String obtenerSqlDetalle() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		String sql = "SELECT distinct(cpt.id_cpt) as id, uo.orden as cod_unid_org, "
				+ "cpt.nivel as cpt_nivel, grado_min.numero as grado_min_cpt, "
				+ "grado_max.numero as grado_max_cpt, cpt.numero as nro_cpt, "
				+ "cpt.nro_especifico as nro_espec_cpt, cpt.denominacion as denominacion_cpt, "
				+ "persona.documento_identidad as ci,  persona.apellidos as apellido, "
				+ "persona.nombres as nombre, "
				+ "case when puesto.permanente is true then 'X' else '' end as permanente , "
				+ "case when puesto.contratado is true then 'X' else '' end as contratado "
				+ "FROM sinarh.sin_nivel_entidad nivel, sinarh.sin_entidad sin_entidad "
				+ "inner join planificacion.entidad entidad "
				+ "on entidad.id_sin_entidad = sin_entidad.id_sin_entidad "
				+ "inner join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_entidad = entidad.id_entidad "
				+ "inner join planificacion.configuracion_uo_det uo "
				+ "on uo.id_configuracion_uo = oee.id_configuracion_uo "
				+ "inner join planificacion.planta_cargo_det puesto "
				+ "on puesto.id_configuracion_uo_det = uo.id_configuracion_uo_det "
				+ "inner join planificacion.cpt cpt "
				+ "on cpt.id_cpt = puesto.id_cpt "
				+ "inner join planificacion.tipo_cpt tipo_cpt "
				+ "on tipo_cpt.id_tipo_cpt = cpt.id_tipo_cpt "
				+ "inner join general.empleado_puesto emp "
				+ "on emp.id_planta_cargo_det = puesto.id_planta_cargo_det "
				+ "inner join general.persona persona "
				+ "on emp.id_persona = persona.id_persona "
				+ "inner join planificacion.grado_salarial grado_min "
				+ "on cpt.id_grado_salarial_min = grado_min.id_grado_salarial "
				+ "inner join planificacion.grado_salarial grado_max "
				+ "on cpt.id_grado_salarial_max = grado_max.id_grado_salarial "
				+ "WHERE emp.actual is true ";
		if (idTipoCpt != null) {
			if (!sufc.validarInput(idTipoCpt.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			sql = sql + " and cpt.id_tipo_cpt = " + idTipoCpt;
		}

		if (cpt != null && cpt.getIdCpt() != null) {
			if (!sufc.validarInput(cpt.getIdCpt().toString(),
					TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			sql = sql + " and cpt.id_cpt = " + cpt.getIdCpt();
		}

		if (persona != null && persona.getNombres() != null
				&& !persona.getNombres().trim().isEmpty()) {
			if (!sufc.validarInput(persona.getNombres(),
					TiposDatos.STRING.getValor(), 100)) {
				return null;
			}
			sql = sql + " and lower(persona.nombres) like '%"
					+ sufc.caracterInvalido(persona.getNombres()) + "%'";
		}

		if (persona != null && persona.getApellidos() != null
				&& !persona.getApellidos().trim().isEmpty()) {
			if (!sufc.validarInput(persona.getApellidos(),
					TiposDatos.STRING.getValor(), 80)) {
				return null;
			}
			sql = sql + " and lower(persona.apellidos) like '%"
					+ sufc.caracterInvalido(persona.getApellidos()) + "%'";
		}

		if (persona != null && persona.getDocumentoIdentidad() != null
				&& !persona.getDocumentoIdentidad().trim().isEmpty()) {
			if (!sufc.validarInput(persona.getDocumentoIdentidad(),
					TiposDatos.STRING.getValor(), 30)) {
				return null;
			}
			sql = sql + " and persona.documento_identidad = '"
					+ sufc.caracterInvalido(persona.getDocumentoIdentidad())
					+ "'";
		}

		sql = sql + " and oee.id_configuracion_uo ";
		return sql;
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

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
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

	public Long getIdConfigCab() {
		return idConfigCab;
	}

	public void setIdConfigCab(Long idConfigCab) {
		this.idConfigCab = idConfigCab;
	}

	public Long getIdConfiguracionUoDet() {
		return idConfiguracionUoDet;
	}

	public void setIdConfiguracionUoDet(Long idConfiguracionUoDet) {
		this.idConfiguracionUoDet = idConfiguracionUoDet;
	}

	public String getCodigoUnidOrgDep() {
		return codigoUnidOrgDep;
	}

	public void setCodigoUnidOrgDep(String codigoUnidOrgDep) {
		this.codigoUnidOrgDep = codigoUnidOrgDep;
	}

	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
	}

	public String getCodigoCpt() {
		return codigoCpt;
	}

	public void setCodigoCpt(String codigoCpt) {
		this.codigoCpt = codigoCpt;
	}

	public Cpt getCpt() {
		return cpt;
	}

	public void setCpt(Cpt cpt) {
		this.cpt = cpt;
	}

	public Long getIdCpt() {
		return idCpt;
	}

	public void setIdCpt(Long idCpt) {
		this.idCpt = idCpt;
	}

	public Integer getAnhoActual() {
		return anhoActual;
	}

	public void setAnhoActual(Integer anhoActual) {
		this.anhoActual = anhoActual;
	}

}
