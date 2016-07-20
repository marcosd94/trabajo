package py.com.excelsis.sicca.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("condicionTrabajoList")
public class CondicionTrabajoList extends EntityQuery<CondicionTrabajo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -447373110686692711L;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	StatusMessages statusMessages;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<CondicionTrabajo> listaCondicionTrabajos;

	private static final String EJBQL =
		"select condicionTrabajo from CondicionTrabajo condicionTrabajo";

	private static final String[] RESTRICTIONS =
		{
			"lower(condicionTrabajo.descripcion) like lower(concat('%', concat(#{condicionTrabajoList.condicionTrabajo.descripcion},'%')))",
			"lower(condicionTrabajo.usuAlta) like lower(concat(#{condicionTrabajoList.condicionTrabajo.usuAlta},'%'))",
			"lower(condicionTrabajo.usuMod) like lower(concat(#{condicionTrabajoList.condicionTrabajo.usuMod},'%'))",
			"condicionTrabajo.activo=#{condicionTrabajoList.estado.valor}", };
	private static final String ORDER = "condicionTrabajo.orden";
	private CondicionTrabajo condicionTrabajo = new CondicionTrabajo();

	Estado estado = Estado.ACTIVO;

	public CondicionTrabajoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CondicionTrabajo> listaResultados() {
		seguridadUtilFormController.caracterInvalido(condicionTrabajo.getDescripcion());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<CondicionTrabajo> limpiar() {
		condicionTrabajo = new CondicionTrabajo();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void generarReporte() throws SQLException {
		Connection conn = null;
		try {
			SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			ServletContext servletContext =
				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			  conn = JpaResourceBean.getConnection();
			HashMap param = new HashMap();
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			StringBuffer where = new StringBuffer("WHERE 1 = 1");
			if (estado != null && estado.getValor() != null) {
				if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
					return;
				}
				where.append(" and condicion.activo = " + estado.getValor());
			}

			if (estado != null) {
				param.put("estado", estado.getDescripcion());
			} else
				param.put("estado", "TODOS");

			if (condicionTrabajo.getDescripcion() != null
				&& !condicionTrabajo.getDescripcion().isEmpty()) {
				if (!sufc.validarInput(condicionTrabajo.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
					return;
				}
				where.append(" and lower(condicion.descripcion) like '%"
					+ sufc.caracterInvalido(condicionTrabajo.getDescripcion()).toLowerCase() + "%'");
				param.put("descripcion", condicionTrabajo.getDescripcion().toUpperCase());
			}
			where.append(" order by condicion.orden");
			param.put("where", where.toString());
			JasperReportUtils.respondPDF("RPT_CU043_Condiciones_de_Trabajo", param, false, conn, null);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}

	public CondicionTrabajo getCondicionTrabajo() {
		return condicionTrabajo;
	}

	public List<CondicionTrabajo> obtenerListaCondicionTrabajos() {
		listaCondicionTrabajos = getResultList();
		return listaCondicionTrabajos;
	}

	public List<CondicionTrabajo> getListaCondicionTrabajos() {
		return listaCondicionTrabajos;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
