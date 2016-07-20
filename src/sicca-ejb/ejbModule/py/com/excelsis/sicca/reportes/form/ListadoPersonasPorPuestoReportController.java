package py.com.excelsis.sicca.reportes.form;

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
import py.com.excelsis.sicca.entity.Lista;
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
@Name("listadoPersonasPorPuestoReportController")
public class ListadoPersonasPorPuestoReportController implements Serializable {

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

	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Long idConfigCab;
	private Long idConfiguracionUoDet;
	private Integer anhoActual;

	private String codigoUnidOrgDep;

	private List<EmpleadoPuesto> listaPersonasPorPuesto;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		Date fechaActual = new Date();
		if (anhoActual == null)
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
		listaPersonasPorPuesto = new ArrayList<EmpleadoPuesto>();

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
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=reports/reportes/PlantaCargoDetList&codigoNivel="
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
		String retorno = "/planificacion/searchForms/FindDependencias.xhtml?from=reports/reportes/PlantaCargoDetList&sinNivelEntidadIdSinNivelEntidad="
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

		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=reports/reportes/PlantaCargoDetList&idNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad()
				+ "&sinEntidadIdSinEntidad="
				+ sinEntidad.getIdSinEntidad()
				+ "&idConfiguracionUoCab="
				+ configuracionUoCab.getIdConfiguracionUo();
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

	private String sentenciaSelect() {
		String cad = "select emp_puesto.*  "
				+ "from general.empleado_puesto emp_puesto  "
				+ "join planificacion.planta_cargo_det puesto  "
				+ "on puesto.id_planta_cargo_det = emp_puesto.id_planta_cargo_det  "
				+ "join general.persona pers  "
				+ "on pers.id_persona = emp_puesto.id_persona "
				+ " join planificacion.configuracion_uo_det uo_det "
				+ "on uo_det.id_configuracion_uo_det = puesto.id_configuracion_uo_det "
				+ " join planificacion.configuracion_uo_cab uo_cab "
				+ "on uo_cab.id_configuracion_uo = uo_det.id_configuracion_uo "
				+ " join planificacion.entidad entidad "
				+ "on entidad.id_entidad = uo_cab.id_entidad "
				+ "join sinarh.sin_entidad sin_entidad "
				+ "on sin_entidad.id_sin_entidad = entidad.id_sin_entidad "
				+ " join sinarh.sin_nivel_entidad nivel "
				+ "on nivel.nen_codigo = sin_entidad.nen_codigo ";
		return cad;
	}

	@SuppressWarnings("unchecked")
	public void search() throws Exception {

		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		String select = sentenciaSelect();
		String where = " where emp_puesto.activo is true ";

		if (((configuracionUoDet != null && configuracionUoDet
				.getIdConfiguracionUoDet() != null)
				|| (configuracionUoCab != null && configuracionUoCab
						.getIdConfiguracionUo() != null)
				|| (sinEntidad != null && sinEntidad.getIdSinEntidad() != null)
				|| (persona != null && persona.getNombres() != null && !persona
						.getNombres().trim().isEmpty())
				|| (persona != null && persona.getApellidos() != null && !persona
						.getApellidos().trim().isEmpty()) || (persona != null
				&& persona.getDocumentoIdentidad() != null && !persona
				.getDocumentoIdentidad().trim().isEmpty()))) {

			if (configuracionUoDet != null
					&& configuracionUoDet.getIdConfiguracionUoDet() != null) {

				if (!sufc.validarInput(configuracionUoDet
						.getIdConfiguracionUoDet().toString(), TiposDatos.LONG
						.getValor(), null)) {
					return;
				}
				where = where + " and uo_det.id_configuracion_uo_det = "
						+ configuracionUoDet.getIdConfiguracionUoDet();
			}

			if (configuracionUoCab != null
					&& configuracionUoCab.getIdConfiguracionUo() != null) {
				if (!sufc.validarInput(configuracionUoCab
						.getIdConfiguracionUo().toString(), TiposDatos.LONG
						.getValor(), null)) {
					return;
				}
				where = where + " and uo_cab.id_configuracion_uo = "
						+ configuracionUoCab.getIdConfiguracionUo();
			}

			if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null) {
				if (!sufc.validarInput(sinEntidad.getIdSinEntidad().toString(),
						TiposDatos.LONG.getValor(), null)) {
					return;
				}
				where = where + " and sin_entidad.id_sin_entidad = "
						+ sinEntidad.getIdSinEntidad();
			}

			if (persona != null && persona.getNombres() != null
					&& !persona.getNombres().trim().isEmpty()) {
				if (!sufc.validarInput(persona.getNombres().trim().toString(),
						TiposDatos.STRING.getValor(), null)) {
					return;
				}
				where = where
						+ " and lower(pers.nombres) like '%"
						+ sufc.caracterInvalido(persona.getNombres().trim())
								.toLowerCase() + "%'";
			}

			if (persona != null && persona.getApellidos() != null
					&& !persona.getApellidos().trim().isEmpty()) {
				if (!sufc.validarInput(persona.getApellidos().trim(),
						TiposDatos.STRING.getValor(), null)) {
					return;
				}
				where = where
						+ " and lower(pers.apellidos) like '%"
						+ sufc.caracterInvalido(persona.getApellidos().trim())
								.toLowerCase() + "%'";
			}

			if (persona != null && persona.getDocumentoIdentidad() != null
					&& !persona.getDocumentoIdentidad().trim().isEmpty()) {
				if (!sufc.validarInput(persona.getDocumentoIdentidad().trim(),
						TiposDatos.STRING.getValor(), null)) {
					return;
				}
				where = where
						+ " and pers.documento_identidad = '"
						+ sufc.caracterInvalido(persona.getDocumentoIdentidad())
						+ "'";
			}

			where = where + " order by sin_entidad.id_sin_entidad";
			listaPersonasPorPuesto = new ArrayList<EmpleadoPuesto>();
			listaPersonasPorPuesto = em.createNativeQuery(select + where,
					EmpleadoPuesto.class).getResultList();
			return;
		}
		if (nivelEntidad != null && nivelEntidad.getIdSinNivelEntidad() != null) {

			where += " and nivel.nen_codigo = " + nivelEntidad.getNenCodigo()
					+ " order by sin_entidad.id_sin_entidad ";
			listaPersonasPorPuesto = new ArrayList<EmpleadoPuesto>();
			listaPersonasPorPuesto = em.createNativeQuery(select + where,
					EmpleadoPuesto.class).getResultList();
			return;
		} else {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Ingrese los parámetros de búsqueda");
			listaPersonasPorPuesto = new ArrayList<EmpleadoPuesto>();
		}
	}

	public void searchAll() {
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
		listaPersonasPorPuesto = new ArrayList<EmpleadoPuesto>();

	}

	/**
	 * Es llamado desde el boton Imprimir
	 */
	public void printLink(Integer row) {
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
		empleadoPuesto = listaPersonasPorPuesto.get(row);
		List<Object[]> lista = consultaReporteSimple(empleadoPuesto
				.getPlantaCargoDet());
		if (lista == null || lista.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
			return;
		}

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("cod_entidad", empleadoPuesto.getPlantaCargoDet()
				.getConfiguracionUoDet().getConfiguracionUoCab().getEntidad()
				.getSinEntidad().getEntCodigo()
				+ "");
		param.put("entidad", empleadoPuesto.getPlantaCargoDet()
				.getConfiguracionUoDet().getConfiguracionUoCab().getEntidad()
				.getSinEntidad().getEntNombre());
		String codOee = empleadoPuesto.getPlantaCargoDet()
				.getConfiguracionUoDet().getConfiguracionUoCab().getEntidad()
				.getSinEntidad().getEntCodigo()
				+ "."
				+ empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet()
						.getConfiguracionUoCab().getOrden();
		param.put("oee", empleadoPuesto.getPlantaCargoDet()
				.getConfiguracionUoDet().getConfiguracionUoCab()
				.getDenominacionUnidad());
		param.put("cod_oee", codOee);
		String codUnid = codOee
				+ "."
				+ obtenerCodigo(empleadoPuesto.getPlantaCargoDet()
						.getConfiguracionUoDet());
		param.put("cod_unid_org", codUnid);
		param.put("unidad_org", empleadoPuesto.getPlantaCargoDet()
				.getConfiguracionUoDet().getDenominacionUnidad());
		param.put("puesto", empleadoPuesto.getPlantaCargoDet().getDescripcion());
		param.put("nombres_apellidos", empleadoPuesto.getPersona().getNombres()
				+ " " + empleadoPuesto.getPersona().getApellidos());
		param.put("ci", empleadoPuesto.getPersona().getDocumentoIdentidad());

		for (Object[] obj : lista) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[1] != null)
				map.put("contenido", obj[1].toString());
			if (obj[2] != null)
				map.put("puntaje", obj[2].toString());
			if (obj[3] != null)
				map.put("descripcion", obj[3].toString());

			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("RPT_CU021_Personas_Puesto_Simple", false,
				listaDatosReporte, param);
	}

	public void printLinkValorEconomico(Integer row) {

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
		empleadoPuesto = listaPersonasPorPuesto.get(row);

		List<Object[]> lista = consultaReporteValorEconomico(empleadoPuesto
				.getPlantaCargoDet());
		if (lista == null || lista.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
			return;
		}
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("cod_entidad", empleadoPuesto.getPlantaCargoDet()
				.getConfiguracionUoDet().getConfiguracionUoCab().getEntidad()
				.getSinEntidad().getEntCodigo()
				+ "");
		param.put("entidad", empleadoPuesto.getPlantaCargoDet()
				.getConfiguracionUoDet().getConfiguracionUoCab().getEntidad()
				.getSinEntidad().getEntNombre());
		String codOee = empleadoPuesto.getPlantaCargoDet()
				.getConfiguracionUoDet().getConfiguracionUoCab().getEntidad()
				.getSinEntidad().getEntCodigo()
				+ "."
				+ empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet()
						.getConfiguracionUoCab().getOrden();
		param.put("oee", empleadoPuesto.getPlantaCargoDet()
				.getConfiguracionUoDet().getConfiguracionUoCab()
				.getDenominacionUnidad());
		param.put("cod_oee", codOee);
		String codUnid = codOee
				+ "."
				+ obtenerCodigo(empleadoPuesto.getPlantaCargoDet()
						.getConfiguracionUoDet());
		param.put("cod_unid_org", codUnid);
		param.put("unidad_org", empleadoPuesto.getPlantaCargoDet()
				.getConfiguracionUoDet().getDenominacionUnidad());
		param.put("puesto", empleadoPuesto.getPlantaCargoDet().getDescripcion());
		param.put("nombres_apellidos", empleadoPuesto.getPersona().getNombres()
				+ " " + empleadoPuesto.getPersona().getApellidos());
		param.put("ci", empleadoPuesto.getPersona().getDocumentoIdentidad());

		for (Object[] obj : lista) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[0] != null)
				map.put("obj_codigo", new Integer(obj[0].toString()));
			if (obj[1] != null)
				map.put("categoria", obj[1].toString());
			if (obj[2] != null)
				map.put("monto", new Integer(obj[2].toString()));

			listaDatosReporte.add(map);
		}

		JasperReportUtils.respondPDF("RPT_CU021_Personas_Puesto_Valor_Econ",
				false, listaDatosReporte, param);
	}

	/**
	 * busca los datos para ser enviados en el reporte
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> consultaReporteSimple(PlantaCargoDet planta) {
		String sql = "SELECT id_det_contenido_funcional as id, "
				+ "cf.descripcion as contenido, "
				+ "puntaje as puntaje, "
				+ "ddcf.descripcion as descripcion "
				+ "FROM planificacion.det_contenido_funcional dcf "
				+ "join planificacion.contenido_funcional cf "
				+ "on(dcf.id_contenido_funcional = cf.id_contenido_funcional) "
				+ "join planificacion.det_descripcion_cont_funcional ddcf "
				+ "on(dcf.id_det_contenido_funcional = ddcf.id_contenido_funcional) "
				+ "where cf.activo is true " + "and ddcf.activo is true "
				+ "and id_planta_cargo_det = " + planta.getIdPlantaCargoDet()
				+ " order by cf.orden";
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
	private List<Object[]> consultaReporteValorEconomico(PlantaCargoDet planta) {
		String sql = "select concepto.obj_codigo as obj_codigo, "
				+ "concepto.categoria as categoria, concepto.monto as monto "
				+ "from planificacion.puesto_concepto_pago concepto "
				+ "join planificacion.planta_cargo_det puesto "
				+ "on puesto.id_planta_cargo_det = concepto.id_planta_cargo_det "
				+ "where concepto.activo is true "
				+ "and puesto.id_planta_cargo_det = "
				+ planta.getIdPlantaCargoDet()
				+ " order by concepto.obj_codigo";
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

	/**
	 * Es llamado desde el boton Imprimir
	 */
	public void print() {

		if (listaPersonasPorPuesto == null
				|| listaPersonasPorPuesto.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
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

		List<PlantaCargoDet> listaPuestos = new ArrayList<PlantaCargoDet>();
		listaPuestos = buscarPuestosExistentes();
		for (PlantaCargoDet ob : listaPuestos) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("cod_entidad", ob.getConfiguracionUoDet()
					.getConfiguracionUoCab().getEntidad().getSinEntidad()
					.getEntCodigo()
					+ "");

			map.put("entidad", ob.getConfiguracionUoDet()
					.getConfiguracionUoCab().getEntidad().getSinEntidad()
					.getEntNombre());

			String codOee = ob.getConfiguracionUoDet().getConfiguracionUoCab()
					.getEntidad().getSinEntidad().getEntCodigo()
					+ "."
					+ ob.getConfiguracionUoDet().getConfiguracionUoCab()
							.getOrden();
			map.put("cod_oee", codOee);
			map.put("oee", ob.getConfiguracionUoDet().getConfiguracionUoCab()
					.getDenominacionUnidad());
			String codUnid = codOee + "."
					+ obtenerCodigo(ob.getConfiguracionUoDet());
			map.put("cod_unid_org", codUnid);
			map.put("unidad_org", ob.getConfiguracionUoDet()
					.getDenominacionUnidad());
			map.put("id_puesto", ob.getIdPlantaCargoDet());

			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("RPT_CU021_Personas_Puesto_Complejo",
				false, listaDatosReporte, param);
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<PlantaCargoDet> buscarPuestosExistentes() {
		List<PlantaCargoDet> listaPlanta = new ArrayList<PlantaCargoDet>();
		for (EmpleadoPuesto e : listaPersonasPorPuesto) {
			Boolean existe = false;
			for (PlantaCargoDet l : listaPlanta) {
				if (l.getIdPlantaCargoDet().equals(
						e.getPlantaCargoDet().getIdPlantaCargoDet()))
					existe = true;
			}
			if (!existe)
				listaPlanta.add(e.getPlantaCargoDet());
		}

		return listaPlanta;
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

	public Long getIdSinNivelEntidad() {
		return idSinNivelEntidad;
	}

	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Long getIdConfigCab() {
		return idConfigCab;
	}

	public void setIdConfigCab(Long idConfigCab) {
		this.idConfigCab = idConfigCab;
	}

	public String getCodigoUnidOrgDep() {
		return codigoUnidOrgDep;
	}

	public void setCodigoUnidOrgDep(String codigoUnidOrgDep) {
		this.codigoUnidOrgDep = codigoUnidOrgDep;
	}

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	public Long getIdConfiguracionUoDet() {
		return idConfiguracionUoDet;
	}

	public void setIdConfiguracionUoDet(Long idConfiguracionUoDet) {
		this.idConfiguracionUoDet = idConfiguracionUoDet;
	}

	public List<EmpleadoPuesto> getListaPersonasPorPuesto() {
		return listaPersonasPorPuesto;
	}

	public void setListaPersonasPorPuesto(
			List<EmpleadoPuesto> listaPersonasPorPuesto) {
		this.listaPersonasPorPuesto = listaPersonasPorPuesto;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Integer getAnhoActual() {
		return anhoActual;
	}

	public void setAnhoActual(Integer anhoActual) {
		this.anhoActual = anhoActual;
	}

}
