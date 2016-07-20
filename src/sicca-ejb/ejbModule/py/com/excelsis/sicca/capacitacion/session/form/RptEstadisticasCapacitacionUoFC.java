package py.com.excelsis.sicca.capacitacion.session.form;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
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

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("rptEstadisticasCapacitacionUoFC")
public class RptEstadisticasCapacitacionUoFC {

	
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Date fechaDesde;
	private Date fechaHasta;
	private Long idTipoCapacitacion;
	private Long idCapacitacion;
	private boolean primeraEntrada=true;

	private List<SelectItem> capacitacionSelectItems = new ArrayList<SelectItem>();

	public void init() {

		
		if(primeraEntrada){
			cargarCabecera();
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			fechaDesde = calendar.getTime();
			fechaHasta = new Date();
			primeraEntrada=false;
		}
		nivelEntidadOeeUtil.init();
		cargarSelectItem();

	}

	@SuppressWarnings("unchecked")
	public void cargarSelectItem() {
		List<Capacitaciones> lista = new ArrayList<Capacitaciones>();
		String sql = obtenerSql();
		lista = em.createNativeQuery(sql, Capacitaciones.class).getResultList();
		capacitacionSelectItems.clear();
		capacitacionSelectItems.add(new SelectItem(null, "Todos"));
		for (Capacitaciones c : lista) {
			capacitacionSelectItems.add(new SelectItem(c.getIdCapacitacion(), c
					.getNombre()));
		}
	}

	private String obtenerSql() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sql = "SELECT C.* as resultado FROM CAPACITACION.CAPACITACIONES C "
				+ "JOIN SELECCION.REFERENCIAS REF "
				+ "ON REF.VALOR_NUM = C.ESTADO "
				+ "JOIN SELECCION.DATOS_ESPECIFICOS DE "
				+ "ON DE.ID_DATOS_ESPECIFICOS = C.ID_DATOS_ESPECIFICOS_TIPO_CAP "
				+ "WHERE C.ACTIVO = TRUE "
				+ "AND REF.DOMINIO= 'ESTADOS_CAPACITACION' "
				+ "AND REF.DESC_LARGA = 'FINALIZADA' ";
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " AND C.ID_CONFIGURACION_UO = "
					+ nivelEntidadOeeUtil.getIdConfigCab();
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			sql += " AND C.ID_CONFIGURACION_UO_DET = "
					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		if (idTipoCapacitacion != null)
			sql += " AND DE.DESCRIPCION = '"
					+ em.find(DatosEspecificos.class, idTipoCapacitacion)
							.getDescripcion() + "'";
		if (fechaDesde != null)
			sql += " AND C.FECHA_ALTA >= to_date('" + sdf.format(fechaDesde)
					+ "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			sql += " AND C.FECHA_ALTA <= to_date('" + sdf.format(fechaHasta)
					+ "','DD-MM-YYYY') ";
		return sql;
	}

	public void cargarCabecera() {

		/**
		 *  SE CARGA LA CABECERA OEE,DEL USUARIO LOGUEADO
		 * */
		ConfiguracionUoCab cabUsuario= em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdConfigCab(cabUsuario.getIdConfiguracionUo());
		if(usuarioLogueado.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		/**
		 * **/
	}
	
	public void cambiarOee() {
		nivelEntidadOeeUtil.findUnidadOrganizativa();
		cargarSelectItem();
	}

	public void cambiarUO() {
		nivelEntidadOeeUtil.obtenerUnidadOrganizativaDep();
		cargarSelectItem();
	}
	
	public void imprimir() throws Exception {
			if(nivelEntidadOeeUtil.getIdConfigCab()==null){
				statusMessages.add(Severity.ERROR,"Debe ingresar el campo OEE antes de realizar esta acci\u00F3n");
				return ;
			}
			if(fechaDesde==null){
				statusMessages.add(Severity.ERROR,"Debe ingresar el campo Fecha Desde antes de realizar esta acci\u00F3n");
				return ;
			}
			if(fechaHasta==null){
				statusMessages.add(Severity.ERROR,"Debe ingresar el campo Fecha Hasta antes de realizar esta acci\u00F3n");
				return ;
			}
			
			Connection conn = null;
			try {
	
				HashMap<String, Object> param = new HashMap<String, Object>();
				param=obtenerMapaParametros();
				if(param==null)
					return;
				conn = JpaResourceBean.getConnection();
				param.put("REPORT_CONNECTION", conn);
				JasperReportUtils.respondPDF(
						"RPT_CU502", param, false,
						conn, usuarioLogueado);
				conn.close();
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
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
	
		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		
		Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
		if(nivelEntidadOeeUtil.getIdConfigCab()!=null){
			if (!sufc.validarInput(nivelEntidadOeeUtil.getIdConfigCab().toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			param.put("nivel", diccDscNEO.get("NIVEL"));
			param.put("entidad", diccDscNEO.get("ENTIDAD"));
			param.put("oee", diccDscNEO.get("OEE"));
		
			
		}
			
		if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null ){
			
			if (!sufc.validarInput(nivelEntidadOeeUtil.getIdUnidadOrganizativa().toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			
			param.put("unidadOrga", diccDscNEO.get("UND_ORG"));
		}
		
		if(idTipoCapacitacion!=null ){
			
			if (!sufc.validarInput(idTipoCapacitacion.toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			DatosEspecificos d=em.find(DatosEspecificos.class,idTipoCapacitacion.longValue());
			param.put("idTipoCapacitacion", idTipoCapacitacion);
			param.put("tipoCapa", d.getDescripcion());
		}
		if(idCapacitacion!=null ){
			
			if (!sufc.validarInput(idTipoCapacitacion.toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			Capacitaciones cap=em.find(Capacitaciones.class, idCapacitacion);
			
			param.put("idCapacitacion", idCapacitacion.longValue());
			param.put("nombreCapa", cap.getNombre());
		}
	
		param.put("titulo1","Cantidad de Participantes por Género");
		param.put("titulo2", "Cantidad de Desvinculados");
		param.put("titulo3", "Cantidad de Funcionarios por Institución");
		ConfiguracionUoCab cab=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		param.put("oeeUsuarioLogueado", cab.getDenominacionUnidad());
		
	
		
		return param;
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

	public Long getIdTipoCapacitacion() {
		return idTipoCapacitacion;
	}

	public void setIdTipoCapacitacion(Long idTipoCapacitacion) {
		this.idTipoCapacitacion = idTipoCapacitacion;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public List<SelectItem> getCapacitacionSelectItems() {
		return capacitacionSelectItems;
	}

	public void setCapacitacionSelectItems(List<SelectItem> capacitacionSelectItems) {
		this.capacitacionSelectItems = capacitacionSelectItems;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}
	
	
}
