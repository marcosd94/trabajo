package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.EntidadOeeList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("administrarUoCabListFormController")
public class AdministrarUoCabListFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(value = "entityManager")
	EntityManager em;
	

	@In(required = false)
	Usuario usuarioLogueado;

	private Estado estado = Estado.ACTIVO;
	private ConfiguracionUoCab configuracionUoCab;
	private Date fechaDesde;
	private Date fechaHasta;
	private String codigo;
	private String denominacion;
	private BigDecimal nenCod;
	private Integer orden;
	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	private boolean primeraEntrada=true;


	public void init() {
		if(primeraEntrada){
			nivelEntidadOeeUtil.limpiar();
			primeraEntrada=false;
		}
		nivelEntidadOeeUtil.init();
		search();
	}

	

	public void search() {
		
		
		configuracionUoCabList.getConfiguracionUoCab().setOrden(orden);
		if (denominacion != null && !denominacion.isEmpty())
			configuracionUoCabList.getConfiguracionUoCab().setDenominacionUnidad(denominacion.trim());
		configuracionUoCabList.setVigDesde(fechaDesde);
		configuracionUoCabList.setVigHasta(fechaHasta);
		configuracionUoCabList.setIdSinEntidad(nivelEntidadOeeUtil.getIdSinEntidad());
		configuracionUoCabList.setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad());
		configuracionUoCabList.setEstado(estado);

		configuracionUoCabList.listaResultados();
	}

	public void searchAll() {
		try {
			configuracionUoCab = new ConfiguracionUoCab();
			codigo = null;
			denominacion = null;
			estado = Estado.ACTIVO;
			fechaDesde = null;
			fechaHasta = null;
			orden = null;
			nivelEntidadOeeUtil.limpiar();
			configuracionUoCabList.limpiar();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	public String codigos(Long idOee) {
		Map<String, String> oeeCod=nivelEntidadOeeUtil.codigoDescripcionOee(idOee);
		codigo =oeeCod.get("CODIGO");
		return codigo;
	}

	

	public void pdf() throws Exception {
		Connection conn = null;
		try {
			HashMap<String, Object> mapa = obtenerMapaParametros();
			if (mapa == null) {
				throw new Exception(SeamResourceBundle.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
			} else {
				conn = JpaResourceBean.getConnection();
				JasperReportUtils.respondPDF("RPT_CU47_unidad_organizativa_cabeza", mapa, false, conn, usuarioLogueado);
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	private HashMap<String, Object> obtenerMapaParametros() throws Exception {

		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		StringBuffer sql = new StringBuffer();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		sql.append(" Select sinEn.nen_codigo||' - '||sin_nivel_entidad.nen_nombre as nivel, sinEn.ent_codigo ||' - '||sinEn.ent_nombre as entidad,  ");
		sql.append(" sinEn.nen_codigo||'.'||sinEn.ent_codigo||'.'||cabecera.orden as codigo,  ");
		sql.append(" cabecera.denominacion_unidad as descripcion,   ");
		sql.append(" case when cabecera.activo = 'f'   then 'NO' else 'SI' end as activo,   ");
		sql.append(" cabecera.vigencia_desde as vig_desde  ");
		sql.append("from  planificacion.configuracion_uo_cab cabecera   ");
		sql.append(" join planificacion.entidad entidad on entidad.id_entidad=cabecera.id_entidad  ");
		sql.append(" join sinarh.sin_entidad sinEn on sinEn.id_sin_entidad=entidad.id_sin_entidad ");
		sql.append(" JOIN sinarh.sin_nivel_entidad  sin_nivel_entidad on (sin_nivel_entidad.ani_aniopre=sinEn.ani_aniopre and sin_nivel_entidad.nen_codigo=sinEn.nen_codigo) ");
		sql.append(" where 1=1 ");
		if (denominacion != null && !denominacion.trim().isEmpty()) {
			if (!sufc.validarInput(denominacion, TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append(" and lower(cabecera.denominacion_unidad) like '"
				+ denominacion.toLowerCase() + "%'");
		}
		if (orden != null) {
			if (!sufc.validarInput(orden.toString(), TiposDatos.INTEGER.getValor(), null)) {
				return null;
			}
			sql.append(" and cabecera.orden= " + orden);
		}
		
		if (fechaDesde != null) {
			sql.append("  and cabecera.vigencia_desde >= '" + formato.format(fechaDesde) + "'");
		}
		if (fechaHasta != null) {
			sql.append("  and cabecera.vigencia_desde <= '" + formato.format(fechaHasta) + "'");
		}
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null) {
			if (!sufc.validarInput(nivelEntidadOeeUtil.getCodSinEntidad().toString(), TiposDatos.BIGDECIMAL.getValor(), null)) {
				return null;
			}
			sql.append("  and  sinEn.ent_codigo =" +nivelEntidadOeeUtil.getCodSinEntidad());
		}
		if (nivelEntidadOeeUtil.getCodNivelEntidad() != null) {
			if (!sufc.validarInput(nivelEntidadOeeUtil.getCodNivelEntidad().toString(), TiposDatos.BIGDECIMAL.getValor(), null)) {
				return null;
			}
			sql.append("  and  sinEn.nen_codigo =" + nivelEntidadOeeUtil.getCodNivelEntidad());
		}
		if (estado.getValor() != null) {
			if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
				return null;
			}
			sql.append("  and  cabecera.activo = " + estado.getValor());
		}
		sql.append("  order by  cabecera.denominacion_unidad   ");

		param.put("sql", sql.toString());
		if (denominacion != null)
			param.put("descripcion", !denominacion.equals("") ? denominacion : "TODOS");
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null ){
			
			param.put("nivel",nivelEntidadOeeUtil.getCodNivelEntidad()+" - "+nivelEntidadOeeUtil.getNombreNivelEntidad());
		}else
			param.put("nivel", "TODOS");
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null )
			param.put("entidad", nivelEntidadOeeUtil.getCodNivelEntidad()+"."+nivelEntidadOeeUtil.getCodSinEntidad()+" - "+nivelEntidadOeeUtil.getNombreSinEntidad());
		else
			param.put("entidad", "TODOS");

		if (orden != null)
			param.put("codigo", orden.toString());
		else
			param.put("codigo", "TODOS");

		if (fechaDesde != null && fechaHasta == null) {
			param.put("vigencia", formato.format(fechaDesde));
		} else if (fechaDesde != null && fechaHasta != null) {

			param.put("vigencia", formato.format(fechaDesde) + " y " + formato.format(fechaHasta));
		}

		param.put("estado", estado.getDescripcion());
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	// GETTERS Y SETTERS

	
	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Date getFechaDesde() {
		if (fechaDesde != null) {
			GregorianCalendar gc1 = new GregorianCalendar();
			gc1.setTime(fechaDesde);
			gc1.clear(GregorianCalendar.HOUR);
			gc1.clear(GregorianCalendar.MILLISECOND);
			gc1.clear(GregorianCalendar.MINUTE);
			fechaDesde = gc1.getTime();
		}

		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		if (fechaHasta != null) {
			GregorianCalendar gc1 = new GregorianCalendar();
			gc1.setTime(fechaHasta);
			gc1.clear(GregorianCalendar.HOUR);
			gc1.clear(GregorianCalendar.MILLISECOND);
			gc1.clear(GregorianCalendar.MINUTE);
			fechaHasta = gc1.getTime();
		}

		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDenominacion() {
		return denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}

	public BigDecimal getNenCod() {
		return nenCod;
	}

	public void setNenCod(BigDecimal nenCod) {
		this.nenCod = nenCod;
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

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}



	
	

}
