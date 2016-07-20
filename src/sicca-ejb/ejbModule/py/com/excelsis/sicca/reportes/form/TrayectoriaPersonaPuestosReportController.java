package py.com.excelsis.sicca.reportes.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EmpleadoPuestoList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("trayectoriaPersonaPuestosReportController")
public class TrayectoriaPersonaPuestosReportController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	EmpleadoPuestoList empleadoPuestoList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(required = false)
	Usuario usuarioLogueado;
	@In (create=true)
	SeguridadUtilFormController seguridadUtilFormController;
	private Persona persona = new Persona();
	List<Persona> listadoPersonas;
	private Long idPais;

	public void init() {
		idPais = idParaguay();
		listadoPersonas = new ArrayList<Persona>();
	}

	@SuppressWarnings("unchecked")
	public void search() {

		if (persona != null
				&& ((persona.getNombres() != null && !persona.getNombres()
						.trim().isEmpty())
						|| (persona.getApellidos() != null && !persona
								.getApellidos().trim().isEmpty()) || (persona
						.getDocumentoIdentidad() != null && !persona
						.getDocumentoIdentidad().trim().isEmpty()))
				|| idPais != null) {
			listadoPersonas = new ArrayList<Persona>();
			String sql = obtenerSql();
			listadoPersonas = em.createNativeQuery(sql, Persona.class)
					.getResultList();

		} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Ingrese los filtros...", "No hay datos..."));
			return;
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

	public void searchAll() {
		persona = new Persona();
		listadoPersonas = new ArrayList<Persona>();
		idPais = idParaguay();
	}

	private String obtenerSql() {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		String sql = "select distinct(persona.*) "
				+ "from general.empleado_puesto emp "
				+ "join general.persona persona "
				+ "on persona.id_persona = emp.id_persona "
				+ "where emp.activo is true";
		try {
			if (persona.getNombres() != null
					&& !persona.getNombres().trim().isEmpty()) {
				if (!sufc.validarInput(persona.getNombres().toString(),
						TiposDatos.STRING.getValor(), null)) {
					return null;
				}
				sql += " and lower(persona.nombres) like '%"
						+ seguridadUtilFormController.caracterInvalido(persona.getNombres().toLowerCase()) + "%'";
			}
			if (persona.getApellidos() != null
					&& !persona.getApellidos().toLowerCase().trim().isEmpty()) {
				if (!sufc.validarInput(persona.getApellidos().toString(),
						TiposDatos.STRING.getValor(), null)) {
					return null;
				}
				sql += " and lower(persona.apellidos) like '%"
						+ seguridadUtilFormController.caracterInvalido(persona.getApellidos().toLowerCase()) + "%'";
			}
			if (persona.getDocumentoIdentidad() != null
					&& !persona.getDocumentoIdentidad().trim().isEmpty()) {
				if (!sufc.validarInput(persona.getDocumentoIdentidad()
						.toString(), TiposDatos.STRING.getValor(), null)) {
					return null;
				}
				sql += " and persona.documento_identidad = '"
						+ seguridadUtilFormController.caracterInvalido(persona.getDocumentoIdentidad().toLowerCase()) + "'";
			}
			if (idPais != null)
				sql += " and persona.id_pais_expedicion_doc = " + idPais;

		} catch (Exception e) {
			// TODO: handle exception
		}

		return sql;
	}

	public void print(Integer row) {

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		Persona p = new Persona();
		p = listadoPersonas.get(row);

		List<Object[]> lista = consulta(p);
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
		param.put("nombres", p.getNombres() + " " + p.getApellidos());
		param.put("documento", p.getDocumentoIdentidad());
		if (idPais != null)
			param.put("pais", em.find(Pais.class, idPais).getDescripcion());
		else
			param.put("pais", "TODOS");

		SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

		for (Object[] obj : lista) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[0] != null) {
				PlantaCargoDet pl = new PlantaCargoDet();
				Long id = new Long(obj[0].toString());
				pl = em.find(PlantaCargoDet.class, id);

				SinEntidad sinEntidad = new SinEntidad();
				Entidad entidad = new Entidad();
				SinNivelEntidad nivelEntidad = new SinNivelEntidad();
				if (pl.getConfiguracionUoDet().getConfiguracionUoCab() != null) {
					if (pl.getConfiguracionUoDet().getConfiguracionUoCab()
							.getEntidad() != null)
						entidad = pl.getConfiguracionUoDet()
								.getConfiguracionUoCab().getEntidad();
					sinEntidad = entidad.getSinEntidad();
					nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
					if (nivelEntidad.getNenCodigo() != null) {
						sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
								nivelEntidad.getNenCodigo());
						nivelEntidad = sinNivelEntidadList
								.nivelEntidadMaxAnho();
					}
				}
				map.put("nivel", nivelEntidad.getNenCodigo() + " - "
						+ nivelEntidad.getNenNombre());
				String codEntidad = nivelEntidad.getNenCodigo() + "."
						+ sinEntidad.getEntCodigo();
				map.put("entidad",
						codEntidad + " - " + sinEntidad.getEntNomabre());
				String codOee = codEntidad
						+ "."
						+ pl.getConfiguracionUoDet().getConfiguracionUoCab()
								.getOrden();
				map.put("oee", codOee
						+ " - "
						+ pl.getConfiguracionUoDet().getConfiguracionUoCab()
								.getDenominacionUnidad());
				String codUnidOrg = codOee + "."
						+ obtenerCodigo(pl.getConfiguracionUoDet());
				codUnidOrg += " - ";
				codUnidOrg += pl.getConfiguracionUoDet()
						.getDenominacionUnidad();
				map.put("unidad_org", codUnidOrg);
				String cod = obtenerCodigoPuesto(pl);

				map.put("cod_puesto", cod);
			}
			if (obj[1] != null)
				map.put("puesto", obj[1].toString());
			if (obj[2] != null)
				map.put("fecha_inicio", obj[2]);
			if (obj[3] != null) {
				String fecha = sdf.format(obj[3]);
				map.put("fecha_fin", fecha);
			}
			if (obj[3] == null)
				map.put("fecha_fin", "-");
			if (obj[4] != null)
				map.put("ocupa", obj[4].toString());

			listaDatosReporte.add(map);
		}

		JasperReportUtils.respondPDF("RPT_CU433_Trayectoria_Persona_Puesto",
				false, listaDatosReporte, param);
	}

	public void imprimirCU449(Long idPersona) throws Exception {
		Connection conn = JpaResourceBean.getConnection();
		try {

			String where = "";

			where += " where PERSONA.id_persona=" + idPersona;

			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("where", where);
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("oeeUsuario", usuarioLogueado.getConfiguracionUoCab().getDenominacionUnidad().toUpperCase());
			Persona persona = em.find(Persona.class, idPersona);
			param.put("nroDoc", persona.getDocumentoIdentidad());
			param.put("nomApe", persona.getNombreCompleto());
			param.put("idPersona", persona.getIdPersona());
			param.put("pais", persona.getPaisByIdPaisExpedicionDoc()
					.getDescripcion());

			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			//
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));

			JasperReportUtils.respondPDF("RPT_CU449_tray_empleado", param,
					false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}

	}

	@SuppressWarnings("unchecked")
	private List<Object[]> consulta(Persona pers) {
		String sql = "select planta.id_planta_cargo_det as id_puesto,  "
				+ "planta.descripcion as puesto, "
				+ "emp.fecha_inicio as fecha_inicio, "
				+ "emp.fecha_fin as fecha_fin,  "
				+ "case when emp.fecha_fin is null then 'SI' "
				+ "else 'NO' end as ocupa "
				+ "from general.empleado_puesto emp "
				+ "join planificacion.planta_cargo_det planta "
				+ "on planta.id_planta_cargo_det = emp.id_planta_cargo_det "
				+ "join general.persona persona "
				+ "on persona.id_persona = emp.id_persona "
				+ "where emp.activo is true " + "and persona.id_persona = "
				+ pers.getIdPersona();
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

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public List<Persona> getListadoPersonas() {
		return listadoPersonas;
	}

	public void setListadoPersonas(List<Persona> listadoPersonas) {
		this.listadoPersonas = listadoPersonas;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

}
