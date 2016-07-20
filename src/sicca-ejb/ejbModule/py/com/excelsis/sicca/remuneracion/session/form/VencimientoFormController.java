package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.Vencimientos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadHome;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("vencimientoFormController")
public class VencimientoFormController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;
	
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	private Vencimientos vencimiento;
	private Integer anho;
	private Integer mes;
	private Boolean activo;
	private Date fechaVencimiento;
	private Date fechaProrroga;
	private Boolean editar;
	private Long vencimientoId;
	
	public void init(){
		/*if(editar){
			System.out.println("EDITAR");
		}
		else{
			System.out.println("NO EDITAR");
		}*/
		if(vencimientoId != null){
			Query q = em.createQuery("select Vencimientos from Vencimientos Vencimientos "
					+ " where Vencimientos.idVencimiento = :vencimientoId and Vencimientos.activo is true ");
			q.setParameter("vencimientoId", vencimientoId);
			Vencimientos venc = (Vencimientos) q.getResultList().get(0);
			anho = venc.getAnho();
			mes = venc.getMes();
			fechaVencimiento = venc.getVencimiento();
			fechaProrroga = venc.getProrroga();
			activo = true;
		}
		else{
			activo = true;
		}
	}
	
	public String save(){
		try {
			if(editar && vencimientoId != null){
				Query q = em.createQuery("select Vencimientos from Vencimientos Vencimientos "
						+ " where Vencimientos.idVencimiento = :vencimientoId and Vencimientos.activo is true ");
				q.setParameter("vencimientoId", vencimientoId);
				Vencimientos venc = (Vencimientos) q.getResultList().get(0);
				venc.setAnho(anho);
				venc.setMes(mes);
				venc.setVencimiento(fechaVencimiento);
				venc.setProrroga(fechaProrroga);
				venc.setActivo(activo);
				venc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				venc.setFechaMod(new Date());
				em.merge(venc);
				em.flush();
				return "updated";
			}
			else{
				Vencimientos venc = new Vencimientos();
				venc.setAnho(anho);
				venc.setMes(mes);
				venc.setVencimiento(fechaVencimiento);
				venc.setProrroga(fechaProrroga);
				venc.setActivo(true);
				venc.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				venc.setFechaAlta(new Date());
				em.persist(venc);
				em.flush();
				return "persisted";
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String eliminar(Long idVencimiento){
		try {
			Query q = em.createQuery("select Vencimientos from Vencimientos Vencimientos "
					+ " where Vencimientos.idVencimiento = :vencimientoId and Vencimientos.activo is true ");
			q.setParameter("vencimientoId", idVencimiento);
			Vencimientos venc = (Vencimientos) q.getResultList().get(0);
			venc.setActivo(false);
			em.merge(venc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	GETTERS Y SETTERS
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
	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	public Date getFechaProrroga() {
		return fechaProrroga;
	}
	public void setFechaProrroga(Date fechaProrroga) {
		this.fechaProrroga = fechaProrroga;
	}
	public Boolean getEditar() {
		return editar;
	}
	public void setEditar(Boolean editar) {
		this.editar = editar;
	}
	public Long getVencimientoId(){
		return vencimientoId;
	}
	public void setVencimientoId(Long vencimientoId){
		this.vencimientoId = vencimientoId;
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
	
	public Boolean mostrarActualizar(){
		if(editar && (vencimientoId != null)){
			return true;
		}
		else{
			return false;
		}
	}
}
