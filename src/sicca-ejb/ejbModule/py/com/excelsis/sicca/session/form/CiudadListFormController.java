package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
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

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("ciudadListFormController")
public class CiudadListFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4295873135908284132L;
	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;

	private Ciudad ciudad = new Ciudad();
	private Long codPais = null;
	private Long codDepartamento = null;
	private Estado estado = Estado.ACTIVO;

	private List<SelectItem> departamentosSelecItem;

	public void init() {
		search();
		updateDepartamento();
	}

	public void search() {
		ciudadList.getPais().setIdPais(codPais);
		ciudadList.getDepartamento().setIdDepartamento(codDepartamento);
		ciudadList.getCiudad().setDescripcion(ciudad.getDescripcion());
		ciudadList.getCiudad().setActivo(estado.getValor());
		ciudadList.buscarResultados();
	}

	public void searchAll() {
		codPais = null;
		codDepartamento = null;
		estado = Estado.ACTIVO;
		ciudad = new Ciudad();
		ciudadList.limpiarResultados();
	}

	public void print() {
		Connection conn = null;
		try {
			SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			Map<String, Object> param = new HashMap<String, Object>();

			StringBuffer whereClause = new StringBuffer();
			if (codPais != null) {
				if (!sufc.validarInput(codPais.toString(), TiposDatos.LONG.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "p.id_pais = " + codPais);
			}

			if (codDepartamento != null) {
				if (!sufc.validarInput(codDepartamento.toString(), TiposDatos.LONG.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "d.id_departamento = " + codDepartamento);
			}
			if (ciudad.getDescripcion() != null && !ciudad.getDescripcion().isEmpty()) {
				if (!sufc.validarInput(ciudad.getDescripcion().trim(), TiposDatos.STRING.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "lower(c.descripcion) like '%"
					+ sufc.caracterInvalido(ciudad.getDescripcion().trim()).toLowerCase() + "%' ");
			}
			if (estado.getValor() != null) {
				if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "c.activo = " + estado.getValor());
			}
			whereClause.append(" ORDER BY pais, departamento, ciudad ");

			ServletContext servletContext =
				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("whereClause", whereClause.toString());
			param.put("usuario", usuarioLogueado.getCodigoUsuario());

			conn = JpaResourceBean.getConnection();

			JasperReportUtils.respondPDF("ListarCiudadesUC104", param, false, conn, usuarioLogueado);

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("seam_error_inesperado"));
			e.printStackTrace();
			return;
		} finally {
			if (conn != null)
				JpaResourceBean.closeConnection(conn);
		}
	}

	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		System.out.println(dptoList.size());
		buildDepartamentoSelectItem(dptoList);
	}

	// METODOS PRIVADOS
	private List<Departamento> getDepartamentosByPais() {
		if (codPais != null) {
			departamentoList.getPais().setIdPais(codPais);
			departamentoList.setOrder("descripcion");
			return departamentoList.litarPorPais();
		}
		return new ArrayList<Departamento>();
	}

	private void buildDepartamentoSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(), dep.getDescripcion()));
		}
	}

	// GETTERS Y SETTERS
	public Ciudad getCiudad() {
		return ciudad;
	}

	public void setCiudad(Ciudad ciudad) {
		this.ciudad = ciudad;
	}

	public Long getCodPais() {
		return codPais;
	}

	public void setCodPais(Long codPais) {
		this.codPais = codPais;
	}

	public Long getCodDepartamento() {
		return codDepartamento;
	}

	public void setCodDepartamento(Long codDepartamento) {
		this.codDepartamento = codDepartamento;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

}
