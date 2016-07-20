package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinAnxList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

/**
 * @author lvergara
 */
@Scope(ScopeType.CONVERSATION)
@Name("listarCategoriasFormController")
public class ListarCategoriasFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2991843744538615618L;

	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	@In
	StatusMessages statusMessages;

	@In(create = true)
	SinAnxList sinAnxList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;

	private SinAnx sinAnx = new SinAnx();
	private SinNivelEntidad sinNivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Integer anhoActual = null;

	private String descNivelEntidad, descEntidad;

	public void init() {
		cargarAnhoActual();
		sinAnx.setAniAniopre(anhoActual);
		search();
		if (sinNivelEntidad.getIdSinNivelEntidad() != null
			&& sinNivelEntidad.getNenNombre() == null) {
			sinNivelEntidad =
				em.find(SinNivelEntidad.class, sinNivelEntidad.getIdSinNivelEntidad());
		}
		if (sinEntidad.getIdSinEntidad() != null) {
			sinEntidad = em.find(SinEntidad.class, sinEntidad.getIdSinEntidad());
			if (sinNivelEntidad.getNenCodigo() == null) {
				sinNivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
				obtenerDescNivelEntidad();
			}
		}
	}

	public void search() {
		sinAnxList.getSinAnx().setAniAniopre(sinAnx.getAniAniopre());
		sinAnxList.getSinAnx().setNenCodigo(sinNivelEntidad.getNenCodigo() != null
			? sinNivelEntidad.getNenCodigo().intValue() : null);
		sinAnxList.getSinAnx().setEntCodigo(sinEntidad.getEntCodigo() != null
			? sinEntidad.getEntCodigo().intValue() : null);
		sinAnxList.getSinAnx().setAnxDescrip(sinAnx.getAnxDescrip() != null
			? sinAnx.getAnxDescrip().trim() : null);
		sinAnxList.buscarResultados();
	}

	public void searchAll() {
		sinAnx = new SinAnx();
		cargarAnhoActual();
		sinAnx.setAniAniopre(anhoActual);
		sinNivelEntidad = new SinNivelEntidad();
		sinEntidad = new SinEntidad();
		sinAnxList.limpiarResultados();
	}

	public void obtenerDescNivelEntidad() {
		descNivelEntidad = null;
		if (sinNivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(sinNivelEntidad.getNenCodigo());
			SinNivelEntidad nEnt = sinNivelEntidadList.nivelEntidadMaxAnho();
			sinNivelEntidad = nEnt != null ? nEnt : null;
			descNivelEntidad = nEnt != null ? nEnt.getNenNombre() : null;
		}
	}

	public void obtenerDescEntidad() {
		descEntidad = null;
		if (sinNivelEntidad.getNenCodigo() != null && sinEntidad.getEntCodigo() != null) {
			sinEntidadList.getSinEntidad().setNenCodigo(sinNivelEntidad.getNenCodigo());
			sinEntidadList.getSinEntidad().setEntCodigo(sinEntidad.getEntCodigo());
			SinEntidad ent = sinEntidadList.entidadMaxAnho();
			sinEntidad = ent != null ? ent : null;
			descEntidad = ent != null ? ent.getEntNombre() : null;
		}
	}

	public String getUrlToPageEntidad() {
		if (sinNivelEntidad.getIdSinNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		sinNivelEntidad = em.find(SinNivelEntidad.class, sinNivelEntidad.getIdSinNivelEntidad());
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=planificacion/reportes/ListarCategorias&codigoNivel="
			+ sinNivelEntidad.getNenCodigo();
	}

	public void pdf() throws SQLException {
		Connection conn = null;
		try {
			if (sinAnx.getAniAniopre() == null || sinNivelEntidad.getNenCodigo() == null) {
				statusMessages.add("Debe ingresar el año y seleccionar el Nivel. Verifique");
				return;
			}
			HashMap<String, Object> mapa = obtenerMapaParametros();
			if (mapa == null)
				return;
			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("RPT_CU35_listar_categorias", mapa, false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}

	}

	// METODOS PRIVADOS

	private HashMap<String, Object> obtenerMapaParametros() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		StringBuffer sql = new StringBuffer();

		sql.append(" where 1=1 ");
		if (sinAnx.getAniAniopre() != null) {
			if (!sufc.validarInput(sinAnx.getAniAniopre().toString(), TiposDatos.INTEGER.getValor(), null)) {
				return null;
			}
			sql.append("  and  sn.ani_aniopre =" + sinAnx.getAniAniopre());
			param.put("anio", String.valueOf(sinAnx.getAniAniopre()));
		} else
			param.put("anio", "TODOS");
		if (sinNivelEntidad.getNenCodigo() != null) {
			if (!sufc.validarInput(sinNivelEntidad.getNenCodigo().toString(), TiposDatos.BIGDECIMAL.getValor(), null)) {
				return null;
			}
			sql.append("  and  sn.nen_codigo = " + sinNivelEntidad.getNenCodigo());
		}
		if (sinEntidad.getEntCodigo() != null) {
			if (!sufc.validarInput(sinEntidad.getEntCodigo().toString(), TiposDatos.BIGDECIMAL.getValor(), null)) {
				return null;
			}
			sql.append("  and  sn.ent_codigo = " + sinEntidad.getEntCodigo());
		}

		sql.append("   order by sn.ani_aniopre,sn.nen_codigo,sn.ent_codigo   ");
		param.put("sql", sql.toString());

		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	private void cargarAnhoActual() {
		Calendar cal = Calendar.getInstance();
		anhoActual = cal.get(Calendar.YEAR);
	}

	// GETTERS Y SETTERS
	public SinAnx getSinAnx() {
		return sinAnx;
	}

	public void setSinAnx(SinAnx sinAnx) {
		this.sinAnx = sinAnx;
	}

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public Integer getAnhoActual() {
		return anhoActual;
	}

	public void setAnhoActual(Integer anhoActual) {
		this.anhoActual = anhoActual;
	}

	public String getDescNivelEntidad() {
		return descNivelEntidad;
	}

	public void setDescNivelEntidad(String descNivelEntidad) {
		this.descNivelEntidad = descNivelEntidad;
	}

	public String getDescEntidad() {
		return descEntidad;
	}

	public void setDescEntidad(String descEntidad) {
		this.descEntidad = descEntidad;
	}

}
