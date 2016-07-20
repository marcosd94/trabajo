package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.Vencimientos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.VencimientoList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("vencimientoListFormController")
public class VencimientoListFormController implements Serializable {

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
	VencimientoList vencimientoList;
	
	@In(value = "entityManager")
	EntityManager em;

	private Vencimientos vencimiento = new Vencimientos();
	private Integer anho = null;
	private Integer mes = null;
	private Estado estado = Estado.ACTIVO;

	private List<SelectItem> departamentosSelecItem;

	public void init() {
		search();
	}

	public void search() {
		vencimientoList.setAnho(anho);
		vencimientoList.setMes(mes);
		vencimientoList.setActivo(estado.getValor());
		vencimientoList.buscarResultados();
	}

	public void searchAll() {
		anho = null;
		mes = null;
		estado = Estado.ACTIVO;
		vencimiento = new Vencimientos();
		vencimientoList.limpiarResultados();
	}

	// GETTERS Y SETTERS
	public Vencimientos getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Vencimientos vencimiento) {
		this.vencimiento = vencimiento;
	}

	public Integer getAnho() {
		return anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
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
	
	public List<SelectItem> mesAnhoSI(String dominio) {
		Query q = em.createQuery("select Referencias from Referencias Referencias "
						+ " where Referencias.dominio = :dominio and Referencias.activo is true order by valorNum asc ");
		q.setParameter("dominio", dominio);
		List<Referencias> lista = q.getResultList();

		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (dominio.equalsIgnoreCase("MESES")) {
			for (Referencias o : lista)
				si.add(new SelectItem(o.getValorNum(), "" + o.getDescLarga()));
			return si;
		} else {
			for (Referencias o : lista)
				si.add(new SelectItem(o.getValorNum(), "" + o.getValorNum()));
			return si;
		}
	}
	
	public String fechaFormateada(Date fecha){
		 SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yy");
		 //System.out.println(format2.format(fecha));
		 return format2.format(fecha);
	}
	
	public String mesNombre(Integer mes){
		Query q = em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.dominio = :dominio and Referencias.valorNum = :numero and Referencias.activo is true order by valorNum asc ");
		q.setParameter("dominio", "MESES");
		q.setParameter("numero", mes);
		List<Referencias> lista = q.getResultList();
		return lista.get(0).getDescAbrev();
	}

}
