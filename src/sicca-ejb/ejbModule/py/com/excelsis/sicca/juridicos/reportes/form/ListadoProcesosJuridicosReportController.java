package py.com.excelsis.sicca.juridicos.reportes.form;

import java.io.Serializable;
import java.math.BigDecimal;
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
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.PAGE)
@Name("listadoProcesosJuridicosReportController")
public class ListadoProcesosJuridicosReportController implements Serializable {

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
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();
	private Persona persona = new Persona();
	private SinNivelEntidad nivelEntidadUsu = new SinNivelEntidad();
	private SinEntidad sinEntidadUsu = new SinEntidad();
	private Entidad entidadUsu = new Entidad();
	private ConfiguracionUoCab configuracionUoCabUsu = new ConfiguracionUoCab();

	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Long idConfigCab;
	private Long idConfiguracionUoDet;
	private Long idPais;
	private Integer anhoActual;

	private String codigoUnidOrgDep;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		Date fechaActual = new Date();
		anhoActual = fechaActual.getYear() + 1900;
		idPais = idParaguay();
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
		if ((nivelEntidad == null || nivelEntidad.getIdSinNivelEntidad() == null)
				&& (sinEntidad == null || sinEntidad.getIdSinEntidad() == null)
				&& (configuracionUoCab == null || configuracionUoCab
						.getIdConfiguracionUo() == null)) {
			buscarDatosAsociadosUsuario();
		}
	}

	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p = em.createQuery(
				" Select p from Pais p"
						+ " where lower(p.descripcion) like 'paraguay'")
				.getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	public void limpiar() {
		persona = new Persona();

		idConfiguracionUoDet = null;
		configuracionUoDet = new ConfiguracionUoDet();
		codigoUnidOrgDep = null;
		buscarDatosAsociadosUsuario();
	}

	private Boolean esAdministrador() {
		String sentencia = "select r.* from seguridad.rol r "
				+ "join seguridad.usuario_rol ur " + "on ur.id_rol = r.id_rol "
				+ "join seguridad.usuario u "
				+ "on u.id_usuario = ur.id_usuario" + " where u.id_usuario = "
				+ usuarioLogueado.getIdUsuario();
		List<Rol> lista = em.createNativeQuery(sentencia, Rol.class)
				.getResultList();
		for (Rol o : lista) {
			if (o.getHomologador())
				return true;
		}
		return false;
	}

	private Boolean validoImpresion() {
		if (!persona.getDocumentoIdentidad().trim().isEmpty())
			return true;
		if (nivelEntidad == null
				|| nivelEntidad.getIdSinNivelEntidad() == null
				|| !nivelEntidad.getIdSinNivelEntidad().equals(
						nivelEntidadUsu.getIdSinNivelEntidad())
				|| sinEntidad == null
				|| sinEntidad.getIdSinEntidad() == null
				|| !sinEntidad.getIdSinEntidad().equals(
						sinEntidadUsu.getIdSinEntidad())
				|| configuracionUoCab == null
				|| configuracionUoCab.getIdConfiguracionUo() == null
				|| !configuracionUoCabUsu.getIdConfiguracionUo().equals(
						configuracionUoCab.getIdConfiguracionUo()))
			return false;
		return true;
	}

	/**
	 * Es llamado desde el boton Imprimir
	 * 
	 * @throws Exception
	 */
	public void print() throws Exception {
		if (!esAdministrador() && !validoImpresion()) {
			buscarDatosAsociadosUsuario();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_WARN,
									"El OEE especificado no corresponde al usuario logueado...",
									"No hay datos..."));
			return;
		}
		if (idSinNivelEntidad != null
				&& (idSinEntidad == null || idConfigCab == null)) {
			statusMessages.add(Severity.ERROR,
					"Debe escoger la Entidad y la OEE");
			return;
		}
		if (idSinNivelEntidad == null
				&& (persona.getNombres() == null || persona.getNombres().trim()
						.isEmpty())
				&& (persona.getApellidos() == null || persona.getApellidos()
						.trim().isEmpty())
				&& (persona.getDocumentoIdentidad() == null || persona
						.getDocumentoIdentidad().trim().isEmpty())) {
			statusMessages.add(Severity.ERROR, "Ingrese datos para filtrar");
			return;
		}

		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (persona != null && persona.getDocumentoIdentidad() != null
				&& !persona.getDocumentoIdentidad().trim().isEmpty()) {
			if (!sufc.validarInput(persona.getDocumentoIdentidad().toString(),
					TiposDatos.STRING.getValor(), null))
				return;
		}

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

		for (Object[] obj : lista) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[0] != null)
				map.put("id_sin_entidad", new Long(obj[0].toString()));
			if (obj[1] != null)
				map.put("id_nivel", new Long(obj[1].toString()));
			if (obj[2] != null)
				map.put("cod_nivel", new Long(obj[2].toString()));
			if (obj[3] != null)
				map.put("nivel", obj[3].toString());
			if (obj[4] != null)
				map.put("cod_entidad", obj[4].toString());
			if (obj[5] != null)
				map.put("entidad", obj[5].toString());
			if (obj[6] != null)
				map.put("id_oee", new Long(obj[6].toString()));
			if (obj[7] != null)
				map.put("cod_oee", obj[7].toString());
			if (obj[8] != null)
				map.put("desc_oee", obj[8].toString());
			if (obj[9] != null)
				map.put("ci", obj[9].toString());
			if (obj[10] != null)
				map.put("pais", obj[10].toString());
			if (obj[11] != null)
				map.put("nombres", obj[11].toString());
			if (obj[12] != null)
				map.put("id_persona", new Long(obj[12].toString()));

			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("RPT_CU460_Listado_Procesos_Juridicos",
				false, listaDatosReporte, param);

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * busca los datos para ser enviados en el reporte
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> consulta() {
		String cad = "SELECT distinct(sin_entidad.id_sin_entidad) as id_sin_entidad, "
				+ "nivel.id_sin_nivel_entidad as id_nivel, nivel.nen_codigo as cod_nivel, "
				+ "nivel.nen_nombre as nivel, nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo as cod_entidad, "
				+ "sin_entidad.ent_nombre as entidad, oee.id_configuracion_uo as id_oee, "
				+ "nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo ||'.'||oee.orden as cod_oee, "
				+ "oee.denominacion_unidad as desc_oee, "
				+ "PERSONA.DOCUMENTO_IDENTIDAD AS CI, "
				+ "PAIS.DESCRIPCION AS PAIS, "
				+ "PERSONA.NOMBRES || ' ' || PERSONA.APELLIDOS AS NOMBRES, PERSONA.ID_PERSONA AS ID_PERSONA "
				+ "FROM sinarh.sin_nivel_entidad nivel, GENERAL.EMPLEADO_PUESTO EMP "
				+ "JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO ON PUESTO.ID_PLANTA_CARGO_DET = EMP.ID_PLANTA_CARGO_DET "
				+ "JOIN GENERAL.PERSONA ON EMP.ID_PERSONA = PERSONA.ID_PERSONA "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_DET UO ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON OEE.ID_CONFIGURACION_UO = UO.ID_CONFIGURACION_UO "
				+ "JOIN GENERAL.PAIS ON PAIS.ID_PAIS = PERSONA.ID_PAIS_EXPEDICION_DOC "
				+ "Join planificacion.entidad entidad "
				+ "on oee.id_entidad = entidad.id_entidad "
				+ "join sinarh.sin_entidad sin_entidad "
				+ "on entidad.id_sin_entidad = sin_entidad.id_sin_entidad "
				+ "where nivel.nen_codigo = sin_entidad.nen_codigo "
				+ "and nivel.ani_aniopre = sin_entidad.ani_aniopre "
				+ "AND EMP.ACTUAL = TRUE ";

		if (persona.getDocumentoIdentidad() != null
				&& !persona.getDocumentoIdentidad().trim().isEmpty())
			cad += " and persona.documento_identidad = '"
					+  seguridadUtilFormController.caracterInvalido(persona
							.getDocumentoIdentidad().toLowerCase()) + "'";
		if (idSinNivelEntidad != null) {
			cad += " and sin_entidad.id_sin_entidad = " + idSinEntidad
					+ " and oee.id_configuracion_uo = "
					+ configuracionUoCab.getIdConfiguracionUo()
					+ " and nivel.id_sin_nivel_entidad = "
					+ nivelEntidad.getIdSinNivelEntidad();
		}
		if (configuracionUoDet != null
				&& configuracionUoDet.getIdConfiguracionUoDet() != null) {
			cad += " and uo.id_configuracion_uo_det = "
					+ configuracionUoDet.getIdConfiguracionUoDet();
		}
		cad += " order by nivel.nen_codigo, cod_entidad, cod_oee, desc_oee";

		try {

			List<Object[]> config = em.createNativeQuery(cad).getResultList();
			if (config == null || config.size() == 0) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * Método que obtiene el codigo del puesto
	 */
	public String obtenerCodigoUnidad(Long id) {
		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = em.find(ConfiguracionUoDet.class, id);
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += "." + codigo;
		}

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

	private void buscarDatosAsociadosUsuario() {
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			configuracionUoCabUsu = configuracionUoCab;
			if (configuracionUoCab.getOrden() != null)

				if (configuracionUoCab.getEntidad() != null) {
					entidad = configuracionUoCab.getEntidad();
					entidadUsu = entidad;
				}
			sinEntidad = entidad.getSinEntidad();
			sinEntidadUsu = sinEntidad;
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
			nivelEntidadUsu = nivelEntidad;
			idConfigCab = configuracionUoCab.getIdConfiguracionUo();
			idSinEntidad = sinEntidad.getIdSinEntidad();
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
				idSinNivelEntidad = null;
				return;
			}
		} else {
			nivelEntidad = new SinNivelEntidad();
			idSinNivelEntidad = null;
		}
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
				entidad = new Entidad();
				sinEntidad = new SinEntidad();
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}

		} else {
			entidad = new Entidad();
			sinEntidad = new SinEntidad();
		}
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
				Integer orden = new Integer(arrayCodigo[0]);
				Integer tam = arrayCodigo.length;

				configuracionUoDet = new ConfiguracionUoDet();

				configuracionUoDet = buscarDetalle(configuracionUoCab, orden);

				ConfiguracionUoDet det = new ConfiguracionUoDet();
				if (tam == 1)
					det = null;
				for (int i = 1; i < arrayCodigo.length; i++) {
					Integer ord = new Integer(arrayCodigo[i]);

					det = buscarDetalle(configuracionUoDet, ord);
					if (det != null)
						configuracionUoDet = det;
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
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=juridicos/reportes/ListadoProcesosJuridicos&codigoNivel="
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
		String retorno = "/planificacion/searchForms/FindDependencias.xhtml?from=juridicos/reportes/ListadoProcesosJuridicos&sinNivelEntidadIdSinNivelEntidad="
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

		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=juridicos/reportes/ListadoProcesosJuridicos&idNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad()
				+ "&sinEntidadIdSinEntidad="
				+ sinEntidad.getIdSinEntidad()
				+ "&idConfiguracionUoCab="
				+ configuracionUoCab.getIdConfiguracionUo();
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

	public Integer getAnhoActual() {
		return anhoActual;
	}

	public void setAnhoActual(Integer anhoActual) {
		this.anhoActual = anhoActual;
	}

	public String getCodigoUnidOrgDep() {
		return codigoUnidOrgDep;
	}

	public void setCodigoUnidOrgDep(String codigoUnidOrgDep) {
		this.codigoUnidOrgDep = codigoUnidOrgDep;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public SinNivelEntidad getNivelEntidadUsu() {
		return nivelEntidadUsu;
	}

	public void setNivelEntidadUsu(SinNivelEntidad nivelEntidadUsu) {
		this.nivelEntidadUsu = nivelEntidadUsu;
	}

	public SinEntidad getSinEntidadUsu() {
		return sinEntidadUsu;
	}

	public void setSinEntidadUsu(SinEntidad sinEntidadUsu) {
		this.sinEntidadUsu = sinEntidadUsu;
	}

	public Entidad getEntidadUsu() {
		return entidadUsu;
	}

	public void setEntidadUsu(Entidad entidadUsu) {
		this.entidadUsu = entidadUsu;
	}

	public ConfiguracionUoCab getConfiguracionUoCabUsu() {
		return configuracionUoCabUsu;
	}

	public void setConfiguracionUoCabUsu(
			ConfiguracionUoCab configuracionUoCabUsu) {
		this.configuracionUoCabUsu = configuracionUoCabUsu;
	}

}
