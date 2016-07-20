package py.com.excelsis.sicca.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("contenidoFuncionalList")
public class ContenidoFuncionalList extends EntityQuery<ContenidoFuncional> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<ContenidoFuncional> listaContenidoFuncionals;

	private static final String EJBQL =
		"select contenidoFuncional from ContenidoFuncional contenidoFuncional";

	private static final String[] RESTRICTIONS =
		{
			"lower(contenidoFuncional.descripcion) like lower(concat('%',concat(#{contenidoFuncionalList.contenidoFuncional.descripcion},'%')))",
			"lower(contenidoFuncional.usuAlta) like lower(concat(#{contenidoFuncionalList.contenidoFuncional.usuAlta},'%'))",
			"lower(contenidoFuncional.usuMod) like lower(concat(#{contenidoFuncionalList.contenidoFuncional.usuMod},'%'))",
			" contenidoFuncional.activo =#{contenidoFuncionalList.estado.valor} " };

	@In(required = false)
	Usuario usuarioLogueado;

	private ContenidoFuncional contenidoFuncional = new ContenidoFuncional();
	private Estado estado = Estado.ACTIVO;
	private static final String ORDER = "contenidoFuncional.orden asc";

	public ContenidoFuncionalList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(10);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<ContenidoFuncional> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<ContenidoFuncional> limpiar() {
		contenidoFuncional = new ContenidoFuncional();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public void pdf() throws SQLException {
		Connection conn =null; 
		try {
			HashMap<String, Object> mapa = obtenerMapaParametros();
			if (mapa == null) {
				throw new Exception(SeamResourceBundle.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
			} else {
				conn = JpaResourceBean.getConnection();
				JasperReportUtils.respondPDF("RPT_CU146_Condicion_func", mapa, false, conn, new Usuario());
				conn.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		StringBuffer sql = new StringBuffer();

		sql.append("  select  f.id_contenido_funcional as idContenidoFuncional, f.orden as orden,  ");
		sql.append(" f.descripcion as descripcion,   ");
		sql.append(" (SELECT COUNT(*) FROM planificacion.valor_nivel_org WHERE planificacion.valor_nivel_org.id_contenido_funcional = f.id_contenido_funcional) as cuenta");
		sql.append(" from planificacion.contenido_funcional f   ");
		sql.append(" WHERE 1=1"); 
		if(estado.getValor()!=null){
			if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
				return null;
			}
			sql.append(" and f.activo = "+estado.getValor());
		}
			
		if(contenidoFuncional.getDescripcion()!=null && !contenidoFuncional.getDescripcion().trim().equals("")){
			if (!sufc.validarInput(contenidoFuncional.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append(" and lower(f.descripcion ) like '%"+sufc.caracterInvalido(contenidoFuncional.getDescripcion().toLowerCase())+"%'");
		}
			
		
		sql.append("   order by f.orden   asc ");
		param.put("sql", sql.toString());
		param.put("descripcion", "TODOS");
		param.put("estado", "ACTIVOS");
		return param;
	}

	public ContenidoFuncional getContenidoFuncional() {
		return contenidoFuncional;
	}

	public List<ContenidoFuncional> obtenerListaContenidoFuncionals() {
		listaContenidoFuncionals = getResultList();
		return listaContenidoFuncionals;
	}

	public List<ContenidoFuncional> getListaContenidoFuncionals() {
		return listaContenidoFuncionals;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
