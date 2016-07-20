package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("reporteTipoMovilidad670FC")
public class ReporteTipoMovilidad670FC {
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
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Long idTipoMovilidad;
	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Long idConfigCab;
	private Date fechaDesde;
	private Date fechaHasta;
	private String tipo;
	private List<SelectItem> tipoMovilidadSI;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {

		if (nivelEntidadOeeUtil == null
			|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		tipo = "D";
	}

	public void cargarCabecera() {
		nivelEntidadOeeUtil.limpiar();
		buscarDatosAsociadosUsuario();
		nivelEntidadOeeUtil.setIdSinNivelEntidad(nivelEntidad.getIdSinNivelEntidad());
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab.getIdConfiguracionUo());
		nivelEntidadOeeUtil.init();
	}

	private List<DatosEspecificos> traerTipoMovilidad() {
		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ " where DatosEspecificos.valorAlf ='MOV' and DatosEspecificos.activo is true "
				+ " and DatosEspecificos.datosGenerales.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD' ");
		return q.getResultList();
	}

	public List<SelectItem> buildTipoMovilidadSI() {
		List<DatosEspecificos> lista = traerTipoMovilidad();
		if (tipoMovilidadSI == null)
			tipoMovilidadSI = new ArrayList<SelectItem>();
		else
			tipoMovilidadSI.clear();

		tipoMovilidadSI.add(new SelectItem(null, "Todos"));
		for (DatosEspecificos o : lista) {
			tipoMovilidadSI.add(new SelectItem(o.getIdDatosEspecificos(), o.getDescripcion()));
		}
		return tipoMovilidadSI;
	}

	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo();
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
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
			if (nivelEntidad != null)
				idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();
			else {
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				idSinNivelEntidad = null;
				return;
			}
		} else {
			nivelEntidad = new SinNivelEntidad();
			idSinNivelEntidad = null;
		}
	}

	boolean validar() {
		if (fechaDesde == null || fechaHasta == null 
			|| nivelEntidadOeeUtil == null || nivelEntidadOeeUtil.getIdSinNivelEntidad() == null
			|| nivelEntidadOeeUtil.getIdSinEntidad() == null
			|| nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return false;
		}
		if (fechaDesde.getTime() > fechaHasta.getTime()) {
			statusMessages.add(Severity.ERROR, "La Fecha Desde no puede ser mayor a la Fecha Hasta");
			return false;
		}
		return true;
	}

	public void imprimir() throws Exception {
		if (!validar())
			return;
		Connection conn = null;
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerMapaParametros();
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			param.put("REPORT_CONNECTION", conn);
			if (tipo.equalsIgnoreCase("D"))
				JasperReportUtils.respondPDF("RPT_CU670_TipoMovilidad_Detallado", param, false, conn, usuarioLogueado);

			if (tipo.equalsIgnoreCase("R"))
				JasperReportUtils.respondPDF("RPT_CU670_TipoMovilidad_Resumido", param, false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	private HashMap<String, Object> obtenerMapaParametros() throws Exception {

		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		Long id = usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo();
		param.put("oee", em.find(ConfiguracionUoCab.class, id).getDenominacionUnidad());

		Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
		param.put("nivel", diccDscNEO.get("NIVEL"));
		param.put("entidad", diccDscNEO.get("ENTIDAD"));
		param.put("oeeFil", diccDscNEO.get("OEE"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("sql", obtenerSql());

		return param;
	}

	private String obtenerSql() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String sql = "";
		sql =
			" AND sinarh.sin_nivel_entidad.nen_codigo = "
				+ em.find(SinNivelEntidad.class, nivelEntidadOeeUtil.getIdSinNivelEntidad()).getNenCodigo()
				+ "AND sinarh.sin_entidad.ent_codigo = "
				+ em.find(SinEntidad.class, nivelEntidadOeeUtil.getIdSinEntidad()).getEntCodigo()
				+ "AND planificacion.configuracion_uo_cab. id_configuracion_uo = "
				+ nivelEntidadOeeUtil.getIdConfigCab()
				+ "AND to_date(to_char(general.empleado_puesto.fecha_inicio,'DD/MM/YYYY'),'DD/MM/YYYY') >= to_date('"
				+ sdf.format(fechaDesde)
				+ "','DD/MM/YYYY')"
				+ "AND to_date(to_char(general.empleado_puesto.fecha_inicio,'DD/MM/YYYY'),'DD/MM/YYYY') <= to_date('"
				+ sdf.format(fechaHasta) + "','DD/MM/YYYY')";
		if (idTipoMovilidad != null) {
			sql += " AND seleccion.datos_especificos.id_datos_especificos = " + idTipoMovilidad;
		}

		return sql;
	}

	public void limpiar() {
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		entidad = new Entidad();
		configuracionUoCab = new ConfiguracionUoCab();
		idConfigCab = null;
		idSinEntidad = null;
		idSinNivelEntidad = null;
		idTipoMovilidad = null;
		fechaDesde = null;
		fechaHasta = null;
		tipo = "D";
		cargarCabecera();

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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Long getIdTipoMovilidad() {
		return idTipoMovilidad;
	}

	public void setIdTipoMovilidad(Long idTipoMovilidad) {
		this.idTipoMovilidad = idTipoMovilidad;
	}

}
