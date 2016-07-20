package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.SelFuncionDatosEsp;
import py.com.excelsis.sicca.seguridad.entity.Funcion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.session.FuncionHome;
import py.com.excelsis.sicca.session.MatrizDocumentalEncHome;
import py.com.excelsis.sicca.session.SelFuncionDatosEspHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("paginasPorTipoDocumentoFormController")
@Scope(ScopeType.PAGE)
public class PaginasPorTipoDocumentoFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3444738528475654380L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	SelFuncionDatosEspHome selFuncionDatosEspHome;
	@In(create = true)
	FuncionHome funcionHome;
	SelFuncionDatosEsp selFuncionDatosEsp = new SelFuncionDatosEsp();
	List<SelFuncionDatosEsp> listaPaginasPorTipoDoc = new ArrayList<SelFuncionDatosEsp>();
	
	
	private Long idPagina;
	private Long idTipoDocumento;
	private Integer cantidad;
	private Boolean obligatorio;
	private String mensaje;

	public void init() {
		selFuncionDatosEsp = selFuncionDatosEspHome.getInstance();
		listaPaginasPorTipoDoc = new ArrayList<SelFuncionDatosEsp>();
		if (selFuncionDatosEspHome.isIdDefined()) {
			listaPaginasPorTipoDoc.add(selFuncionDatosEsp);
		} else {

		}
	}

	public void agregarLista() {
		SelFuncionDatosEsp actual = new SelFuncionDatosEsp();
		if (check()) {
			actual.setActivo(true);
			actual.setCantidad(cantidad);
			actual.setDatosEspecificos(em.find(DatosEspecificos.class,
					idTipoDocumento));
			actual.setFechaAlta(new Date());
			actual.setFuncion(em.find(Funcion.class, idPagina));
			actual.setObligatorioSN(obligatorio);
			actual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			listaPaginasPorTipoDoc.add(actual);
			cantidad = null;
			idPagina = null;
			idTipoDocumento = null;
			obligatorio = null;
			mensaje = null;
			funcionHome.setIdModulo(null);
			List<SelectItem> si = new Vector<SelectItem>();
			si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
					"opt_select_one")));
			funcionHome.setFuncionesPorModuloSelectItems(si);
		}

	}

	private Boolean check() {
		if (cantidad == null || cantidad <= 0 || idPagina == null || idTipoDocumento == null
				|| obligatorio == null) {
			mensaje = SeamResourceBundle.getBundle()
			.getString("CU3401_msg1");
			return false;
		}
		for (SelFuncionDatosEsp f : listaPaginasPorTipoDoc) {
			if (f.getFuncion().getIdFuncion().equals(idPagina)) {

				if (f.getDatosEspecificos().getIdDatosEspecificos()
						.equals(idTipoDocumento)) {
					mensaje = SeamResourceBundle.getBundle()
					.getString("CU3401_msg2");
					return false;
				}
			}
		}
		return true;
	}

	public void actualizarLista(Integer row) {
		SelFuncionDatosEsp actual = new SelFuncionDatosEsp();
		actual = listaPaginasPorTipoDoc.get(row);

		listaPaginasPorTipoDoc.set(row, actual);
	}

	public void eliminarLista(Integer row) {
		SelFuncionDatosEsp actual = new SelFuncionDatosEsp();
		actual = listaPaginasPorTipoDoc.get(row);
		listaPaginasPorTipoDoc.remove(actual);
	}

	public String save() {
		try {
			for (SelFuncionDatosEsp f : listaPaginasPorTipoDoc) {
				if (f.getIdFuncionDatosEsp() == null){
					f.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					f.setFechaAlta(new Date());
					em.persist(f);
					
				}
					
				else {
					f.setUsuMod(usuarioLogueado.getCodigoUsuario());
					f.setFechaMod(new Date());
					em.merge(f);
				}
				em.flush();
			}
			
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	public SelFuncionDatosEsp getSelFuncionDatosEsp() {
		return selFuncionDatosEsp;
	}

	public void setSelFuncionDatosEsp(SelFuncionDatosEsp selFuncionDatosEsp) {
		this.selFuncionDatosEsp = selFuncionDatosEsp;
	}

	public List<SelFuncionDatosEsp> getListaPaginasPorTipoDoc() {
		return listaPaginasPorTipoDoc;
	}

	public void setListaPaginasPorTipoDoc(
			List<SelFuncionDatosEsp> listaPaginasPorTipoDoc) {
		this.listaPaginasPorTipoDoc = listaPaginasPorTipoDoc;
	}

	public Long getIdPagina() {
		return idPagina;
	}

	public void setIdPagina(Long idPagina) {
		this.idPagina = idPagina;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public Boolean getObligatorio() {
		return obligatorio;
	}

	public void setObligatorio(Boolean obligatorio) {
		this.obligatorio = obligatorio;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
