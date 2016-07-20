package py.com.excelsis.sicca.reportes.form;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

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
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("cantidadModalidadOcupacionReportController")
public class CantidadModalidadOcupacionReportController implements Serializable {

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
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();

	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Long idConfigCab;
	private Integer contratados_fem;
	private Integer contratados_masc;
	private Integer permanentes_fem;
	private Integer permanentes_masc;
	private String opcionImpresion;


	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		if (idSinNivelEntidad != null)
			nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		if (idSinEntidad != null)
			sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		if (idConfigCab != null)
			configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);
		if ((nivelEntidad == null || nivelEntidad.getIdSinNivelEntidad() == null)
				&& (sinEntidad == null || sinEntidad.getIdSinEntidad() == null)
				&& (configuracionUoCab == null || configuracionUoCab
						.getIdConfiguracionUo() == null)) {
			buscarDatosAsociadosUsuario();
		}
	}

	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			if (configuracionUoCab.getOrden() != null)

				if (configuracionUoCab.getEntidad() != null)
					entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
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
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=reports/reportes/CantidadModalidadOcupacion&codigoNivel="
				+ nivelEntidad.getNenCodigo();
	}

	/**
	 * Método que busca la unidad organizativa correspondiente al codigo
	 * ingresado, a la entidad y al nivel
	 */
	public void findUnidadOrganizativa() {
		if (configuracionUoCab != null && configuracionUoCab.getOrden() != null) {

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
				+ configuracionUoCab.getOrden();
		List<ConfiguracionUoCab> lista = new ArrayList<ConfiguracionUoCab>();
		lista = em.createNativeQuery(sql, ConfiguracionUoCab.class)
				.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		else
			return null;
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
		String retorno = "/planificacion/searchForms/FindDependencias.xhtml?from=reports/reportes/CantidadModalidadOcupacion&sinNivelEntidadIdSinNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad();
		if (idSinEntidad != null)
			retorno = retorno + "&sinEntidadIdSinEntidad="
					+ sinEntidad.getIdSinEntidad();
		return retorno;
	}

	public void print() {
		if (opcionImpresion == null){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Seleccione el Tipo de Salida...", "No hay datos..."));
			return;
		}
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		Connection conn = JpaResourceBean.getConnection();

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("REPORT_CONNECTION", conn);
		List<Object[]> listaContratados = searchContratados();
		List<Object[]> listaPermanentes = searchPermanentes();
		if ((listaContratados == null || listaContratados.size() == 0)
				&& (listaPermanentes == null || listaPermanentes.size() == 0)) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
			return;
		}

		Integer totalFila = 0;

		if (listaContratados.size() > listaPermanentes.size()) {
			Long idNivelAnterior = null;
			Long idEntidadAnterior = null;
			for (Object[] obj : listaContratados) {

				String codNiv = null;
				String codEnt = null;
				HashMap<String, Object> map = new HashMap<String, Object>();
				if (obj[0] != null) {
					Long id = new Long(obj[0].toString());
					if (idNivelAnterior == null || idNivelAnterior != id) {
						idNivelAnterior = id;
						SinNivelEntidad variable = new SinNivelEntidad();
						variable = em.find(SinNivelEntidad.class, id);
						map.put("id_nivel", id);
						codNiv = variable.getNenCodigo() + "";
						map.put("nivel",
								codNiv + " - " + variable.getNenNombre());
					}
				}

				if (obj[1] != null) {
					Long id = new Long(obj[1].toString());
					if (idEntidadAnterior == null || idEntidadAnterior != id) {
						idEntidadAnterior = id;
						SinEntidad variable = new SinEntidad();
						variable = em.find(SinEntidad.class, id);
						map.put("id_sin_entidad", id);
						codEnt = codNiv + "." + variable.getEntCodigo();
						map.put("entidad",
								codEnt + " - " + variable.getEntNombre());
					}
				}
				if (obj[2] != null) {
					Long id = new Long(obj[2].toString());

					ConfiguracionUoCab variable = new ConfiguracionUoCab();
					variable = em.find(ConfiguracionUoCab.class, id);
					map.put("id_oee", id);
					String cod = codEnt + "."
							+ variable.getOrden();
					map.put("oee", cod);
					map.put("denominacion", variable.getDenominacionUnidad());
				}
				if (obj[3] != null) {
					Integer masculino = new Integer(obj[3].toString());
					totalFila = totalFila + masculino;
					map.put("contratado_masc", masculino);
				}
				if (obj[4] != null) {
					Integer femenino = new Integer(obj[4].toString());
					totalFila = totalFila + femenino;
					map.put("contratado_fem", femenino);
				}
				Boolean esta = false;
				for (Object[] obj2 : listaPermanentes) {
					if (obj2[0] != null && obj2[0].equals(obj[0])
							&& obj2[1] != null && obj2[1].equals(obj[1])
							&& obj2[2] != null && obj2[2].equals(obj[2])) {
						esta = true;
						if (obj2[3] != null) {
							Integer masculino = new Integer(obj2[3].toString());
							totalFila = totalFila + masculino;
							map.put("permanente_masc", masculino);
						}
						if (obj2[4] != null) {
							Integer femenino = new Integer(obj2[4].toString());
							totalFila = totalFila + femenino;
							map.put("permanente_fem", femenino);
						}
					}
				}
				if (!esta) {
					map.put("permanente_masc", new Integer(0));
					map.put("permanente_fem", new Integer(0));
				}
				map.put("total", totalFila);
				listaDatosReporte.add(map);
				totalFila = 0;
			}

		} else {
			Long idNivelAnterior = null;
			Long idEntidadAnterior = null;
			for (Object[] obj : listaPermanentes) {

				String codNiv = null;
				String codEnt = null;
				HashMap<String, Object> map = new HashMap<String, Object>();
				if (obj[0] != null) {
					Long id = new Long(obj[0].toString());
					if (idNivelAnterior == null || idNivelAnterior != id) {
						idNivelAnterior = id;
						SinNivelEntidad variable = new SinNivelEntidad();
						variable = em.find(SinNivelEntidad.class, id);
						map.put("id_nivel", id);
						codNiv = variable.getNenCodigo() + "";
						map.put("nivel",
								codNiv + " - " + variable.getNenNombre());
					}
				}

				if (obj[1] != null) {
					Long id = new Long(obj[1].toString());
					if (idEntidadAnterior == null || idEntidadAnterior != id) {
						idEntidadAnterior = id;
						SinEntidad variable = new SinEntidad();
						variable = em.find(SinEntidad.class, id);
						map.put("id_sin_entidad", id);
						codEnt = codNiv + "." + variable.getEntCodigo();
						map.put("entidad",
								codEnt + " - " + variable.getEntNombre());
					}
				}
				if (obj[2] != null) {
					Long id = new Long(obj[2].toString());

					ConfiguracionUoCab variable = new ConfiguracionUoCab();
					variable = em.find(ConfiguracionUoCab.class, id);
					map.put("id_oee", id);
					String cod = codEnt + "."
							+ variable.getOrden();
					map.put("oee", cod);
					map.put("denominacion", variable.getDenominacionUnidad());
				}
				if (obj[3] != null) {
					Integer masculino = new Integer(obj[3].toString());
					totalFila = totalFila + masculino;
					map.put("permanente_masc", masculino);
				}
				if (obj[4] != null) {
					Integer femenino = new Integer(obj[4].toString());
					totalFila = totalFila + femenino;
					map.put("permanente_fem", femenino);
				}
				Boolean esta = false;
				for (Object[] obj2 : listaContratados) {
					if (obj2[0] != null && obj2[0].equals(obj[0])
							&& obj2[1] != null && obj2[1].equals(obj[1])
							&& obj2[2] != null && obj2[2].equals(obj[2])) {
						esta = true;
						if (obj2[3] != null) {
							Integer masculino = new Integer(obj2[3].toString());
							totalFila = totalFila + masculino;
							map.put("contratado_masc", masculino);
						}
						if (obj2[4] != null) {
							Integer femenino = new Integer(obj2[4].toString());
							totalFila = totalFila + femenino;
							map.put("contratado_fem", femenino);
						}
					}
				}
				if (!esta) {
					map.put("contratado_masc", new Integer(0));
					map.put("contratado_fem", new Integer(0));
				}

				map.put("total", totalFila);
				listaDatosReporte.add(map);
				totalFila = 0;
			}

		}
		if (opcionImpresion.equals("pdf"))
			JasperReportUtils.respondPDF("RPT_CU436_Cant_Pers_Mod_Ocupacion",
					false, listaDatosReporte, param);
		if (opcionImpresion.equals("xls"))
			JasperReportUtils.respondXLS(
					"RPT_CU436_Cant_Pers_Mod_Ocupacion_Excel", param,
					listaDatosReporte, usuarioLogueado);
	}

	/**
	 * Es llamado desde el imprimir Grafico
	 */
	public void printGrafico() {

		if (nivelEntidad == null || nivelEntidad.getIdSinNivelEntidad() == null
				|| sinEntidad == null || sinEntidad.getIdSinEntidad() == null
				|| configuracionUoCab == null
				|| configuracionUoCab.getIdConfiguracionUo() == null) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Ingrese los datos para el filtro...",
							"No hay datos..."));
			return;
		}
		searchCantContratados();
		searchCantPermanentes();
		if (contratados_fem.intValue() == 0 && contratados_masc.intValue() == 0
				&& permanentes_fem.intValue() == 0
				&& permanentes_masc.intValue() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No se encontraron datos que mostrar...",
							"No hay datos..."));
			return;
		}

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		Connection conn = JpaResourceBean.getConnection();

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("REPORT_CONNECTION", conn);
		param.put(
				"nivel",
				nivelEntidad.getNenCodigo() + " - "
						+ nivelEntidad.getNenNombre());
		param.put("entidad",
				nivelEntidad.getNenCodigo() + "." + sinEntidad.getEntCodigo()
						+ " - " + sinEntidad.getEntNombre());
		param.put("oee",
				nivelEntidad.getNenCodigo() + "." + sinEntidad.getEntCodigo()
						+ "." + configuracionUoCab.getOrden() + " - "
						+ configuracionUoCab.getDenominacionUnidad());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("contratado_fem", contratados_fem);
		map.put("contratado_masc", contratados_masc);
		map.put("permanente_fem", permanentes_fem);
		map.put("permanente_masc", permanentes_masc);

		listaDatosReporte.add(map);

		JasperReportUtils.respondPDF("RPT_CU436_Grafico_Cant_Mod_Ocupacion",
				false, listaDatosReporte, param);

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> searchPermanentes() {
		String sql = sentenciaMasc();
		sql += " and emp.contratado is false";
		if (nivelEntidad != null && nivelEntidad.getIdSinNivelEntidad() != null)
			sql += " and nivel.id_sin_nivel_entidad = "
					+ nivelEntidad.getIdSinNivelEntidad();
		if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null)
			sql += " and sin_entidad.id_sin_entidad = "
					+ sinEntidad.getIdSinEntidad();
		if (configuracionUoCab != null
				&& configuracionUoCab.getIdConfiguracionUo() != null)
			sql += " and oee.id_configuracion_uo = "
					+ configuracionUoCab.getIdConfiguracionUo();

		sql += " group by nivel.id_sin_nivel_entidad, sin_entidad.id_sin_entidad, "
				+ "oee.id_configuracion_uo ";
		sql += " union (" + sentenciaFem();
		sql += " and emp.contratado is false ";
		sql += " group by nivel.id_sin_nivel_entidad, sin_entidad.id_sin_entidad, "
				+ "oee.id_configuracion_uo ";
		sql += " order by nivel.id_sin_nivel_entidad, sin_entidad.id_sin_entidad )";
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

	@SuppressWarnings("unchecked")
	private List<Object[]> searchContratados() {
		String sql = sentenciaMasc();
		sql += " and emp.contratado is true";
		if (nivelEntidad != null && nivelEntidad.getIdSinNivelEntidad() != null)
			sql += " and nivel.id_sin_nivel_entidad = "
					+ nivelEntidad.getIdSinNivelEntidad();
		if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null)
			sql += " and sin_entidad.id_sin_entidad = "
					+ sinEntidad.getIdSinEntidad();
		if (configuracionUoCab != null
				&& configuracionUoCab.getIdConfiguracionUo() != null)
			sql += " and oee.id_configuracion_uo = "
					+ configuracionUoCab.getIdConfiguracionUo();

		sql += " group by nivel.id_sin_nivel_entidad, sin_entidad.id_sin_entidad, "
				+ "oee.id_configuracion_uo ";
		sql += " union (" + sentenciaFem();
		sql += " and emp.contratado is true ";
		sql += " group by nivel.id_sin_nivel_entidad, sin_entidad.id_sin_entidad, "
				+ "oee.id_configuracion_uo ";
		sql += " order by nivel.id_sin_nivel_entidad, sin_entidad.id_sin_entidad )";
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

	private String sentenciaMasc() {
		String sql = "select distinct(nivel.id_sin_nivel_entidad) as id_nivel, "
				+ "sin_entidad.id_sin_entidad as id_sin_entidad, "
				+ "oee.id_configuracion_uo as id_oee, count(emp.contratado) as permanente_masc , 0 as permanente_fem "
				+ "from sinarh.sin_nivel_entidad nivel, general.empleado_puesto emp  "
				+ "join planificacion.planta_cargo_det puesto  "
				+ "on puesto.id_planta_cargo_det = emp.id_planta_cargo_det  "
				+ "join planificacion.configuracion_uo_det uo_det  "
				+ "on uo_det.id_configuracion_uo_det = puesto.id_configuracion_uo_det  "
				+ "join planificacion.configuracion_uo_cab oee  "
				+ "on oee.id_configuracion_uo = uo_det.id_configuracion_uo  "
				+ "join planificacion.entidad entidad   "
				+ "on oee.id_entidad = entidad.id_entidad   "
				+ "join sinarh.sin_entidad sin_entidad  "
				+ "on sin_entidad.id_sin_entidad = entidad.id_sin_entidad  "
				+ "join general.persona persona  "
				+ "on persona.id_persona = emp.id_persona "
				+ "where nivel.nen_codigo = sin_entidad.nen_codigo "
				+ " and persona.sexo = 'M'";
		return sql;
	}

	private String sentenciaFem() {
		String sql = "select distinct(nivel.id_sin_nivel_entidad) as id_nivel, "
				+ "sin_entidad.id_sin_entidad as id_sin_entidad, "
				+ "oee.id_configuracion_uo as id_oee, 0 as permanente_masc , count(emp.contratado) as permanente_fem "
				+ "from sinarh.sin_nivel_entidad nivel, general.empleado_puesto emp  "
				+ "join planificacion.planta_cargo_det puesto  "
				+ "on puesto.id_planta_cargo_det = emp.id_planta_cargo_det  "
				+ "join planificacion.configuracion_uo_det uo_det  "
				+ "on uo_det.id_configuracion_uo_det = puesto.id_configuracion_uo_det  "
				+ "join planificacion.configuracion_uo_cab oee  "
				+ "on oee.id_configuracion_uo = uo_det.id_configuracion_uo  "
				+ "join planificacion.entidad entidad   "
				+ "on oee.id_entidad = entidad.id_entidad   "
				+ "join sinarh.sin_entidad sin_entidad  "
				+ "on sin_entidad.id_sin_entidad = entidad.id_sin_entidad  "
				+ "join general.persona persona  "
				+ "on persona.id_persona = emp.id_persona "
				+ "where nivel.nen_codigo = sin_entidad.nen_codigo "
				+ " and persona.sexo = 'F' ";
		return sql;
	}

	/**
	 * Método que cuenta la cantidad de contratados tanto femenino como
	 * masculino llamado para generar el grafico
	 */
	private void searchCantContratados() {

		String sql = sentenciaSimple();

		String where = " where emp.contratado is true "
				+ "and nivel.nen_codigo = sin_entidad.nen_codigo ";
		if (nivelEntidad != null && nivelEntidad.getIdSinNivelEntidad() != null)
			where += " and nivel.id_sin_nivel_entidad = "
					+ nivelEntidad.getIdSinNivelEntidad();
		if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null)
			where += " and sin_entidad.id_sin_entidad = "
					+ sinEntidad.getIdSinEntidad();
		if (configuracionUoCab != null
				&& configuracionUoCab.getIdConfiguracionUo() != null)
			where += " and oee.id_configuracion_uo = "
					+ configuracionUoCab.getIdConfiguracionUo();
		String sentencia1 = sql + where + " and persona.sexo = 'F' ";
		String sentencia2 = sql + where + " and persona.sexo = 'M' ";
		Object config = em.createNativeQuery(sentencia1).getSingleResult();
		Object config2 = em.createNativeQuery(sentencia2).getSingleResult();
		if (config == null)
			contratados_fem = 0;
		else
			contratados_fem = new Integer(config.toString());

		if (config2 == null)
			contratados_masc = 0;
		else
			contratados_masc = new Integer(config2.toString());

	}

	/**
	 * Método que busca la cantidad de empleados permanentes tanto femenino como
	 * masculino llamado para generar el grafico
	 */
	private void searchCantPermanentes() {

		String sql = sentenciaSimple();
		String where = " where emp.contratado is false "
				+ "and nivel.nen_codigo = sin_entidad.nen_codigo ";
		if (nivelEntidad != null && nivelEntidad.getIdSinNivelEntidad() != null)
			where += " and nivel.id_sin_nivel_entidad = "
					+ nivelEntidad.getIdSinNivelEntidad();
		if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null)
			where += " and sin_entidad.id_sin_entidad = "
					+ sinEntidad.getIdSinEntidad();
		if (configuracionUoCab != null
				&& configuracionUoCab.getIdConfiguracionUo() != null)
			where += " and oee.id_configuracion_uo = "
					+ configuracionUoCab.getIdConfiguracionUo();
		String sentencia1 = sql + where + " and persona.sexo = 'F' ";
		String sentencia2 = sql + where + " and persona.sexo = 'M' ";
		Object config = em.createNativeQuery(sentencia1).getSingleResult();
		Object config2 = em.createNativeQuery(sentencia2).getSingleResult();
		if (config == null)
			permanentes_fem = 0;
		else
			permanentes_fem = new Integer(config.toString());

		if (config2 == null)
			permanentes_masc = 0;
		else
			permanentes_masc = new Integer(config2.toString());
	}

	private String sentenciaSimple() {
		String sent = "select count(emp.contratado) as femenino "
				+ "from sinarh.sin_nivel_entidad nivel, general.empleado_puesto emp "
				+ "join planificacion.planta_cargo_det puesto "
				+ "on puesto.id_planta_cargo_det = emp.id_planta_cargo_det "
				+ "join planificacion.configuracion_uo_det uo_det "
				+ "on uo_det.id_configuracion_uo_det = puesto.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_configuracion_uo = uo_det.id_configuracion_uo "
				+ "join planificacion.entidad entidad  "
				+ "on oee.id_entidad = entidad.id_entidad  "
				+ "join sinarh.sin_entidad sin_entidad "
				+ "on sin_entidad.id_sin_entidad = entidad.id_sin_entidad "
				+ "join general.persona persona "
				+ "on persona.id_persona = emp.id_persona";
		return sent;
	}

	public void limpiar() {
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		entidad = new Entidad();
		configuracionUoCab = new ConfiguracionUoCab();
		idConfigCab = null;
		idSinEntidad = null;
		idSinNivelEntidad = null;
	}

	public void prepararImpresion() {

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

	public Integer getContratados_fem() {
		return contratados_fem;
	}

	public void setContratados_fem(Integer contratados_fem) {
		this.contratados_fem = contratados_fem;
	}

	public Integer getContratados_masc() {
		return contratados_masc;
	}

	public void setContratados_masc(Integer contratados_masc) {
		this.contratados_masc = contratados_masc;
	}

	public Integer getPermanentes_fem() {
		return permanentes_fem;
	}

	public void setPermanentes_fem(Integer permanentes_fem) {
		this.permanentes_fem = permanentes_fem;
	}

	public Integer getPermanentes_masc() {
		return permanentes_masc;
	}

	public void setPermanentes_masc(Integer permanentes_masc) {
		this.permanentes_masc = permanentes_masc;
	}

	public String getOpcionImpresion() {
		return opcionImpresion;
	}

	public void setOpcionImpresion(String opcionImpresion) {
		this.opcionImpresion = opcionImpresion;
	}

	
	
	

}
