package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.session.BarrioHome;
import py.com.excelsis.sicca.session.CiudadHome;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("barrioFormController")
public class BarrioFormController implements Serializable{

	/**
	 * 
	 */
//	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	@In(create = true)
	DepartamentoList departamentoList;
	
	@In(create =  true)
	BarrioHome barrioHome;
	
	@In(value = "entityManager")
	EntityManager em;
	
	@In(create = true)
	CiudadList ciudadList;
	
	
	private Barrio barrio;
	private Long idBarrio;
	private Long codPais;
	private Long codDepartamento;
	private Long idCiudad;
	
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	public void init(){
		try {
			barrio= new Barrio();
			if(idBarrio!=null){
				barrio= em.find(Barrio.class, idBarrio);
				codDepartamento = barrio.getCiudad().getDepartamento().getIdDepartamento();
				codPais = barrio.getCiudad().getDepartamento().getPais().getIdPais();
				idCiudad=barrio.getCiudad().getIdCiudad();
				updateDepartamento();
				updateCiudad();
			}else
				barrio.setActivo(Estado.ACTIVO.getValor());
			
			barrioHome.setInstance(barrio);
			updateDepartamento();
			updateCiudad();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String save(){
		try {
			barrioHome.setInstance(barrio);
			barrioHome.setIdCiudad(idCiudad);
			return barrioHome.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public String update(){
		try {
			barrioHome.setInstance(barrio);
			barrioHome.setIdCiudad(idCiudad);
			return barrioHome.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void updateDepartamento(){
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
		updateCiudad();
	}
	public void updateDepartamentoChange(){
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		codDepartamento=null;
		buildDepartamentoSelectItem(dptoList);
		updateCiudad();
	}
	public void updateCiudad(){
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
	}
	
//	METODOS PRIVADOS
	private List<Departamento> getDepartamentosByPais(){
		if(codPais != null){
			departamentoList.getPais().setIdPais(codPais);
			departamentoList.setOrder("descripcion");
			return departamentoList.litarPorPais();
		}
		return new ArrayList<Departamento>();
	}
	private List<Ciudad> getCiudadByDpto(){
		if(codDepartamento!= null){
			ciudadList.getDepartamento().setIdDepartamento(codDepartamento);
			return ciudadList.listarPorDpto();
		}
		return new ArrayList<Ciudad>();
	}
	
	private void buildDepartamentoSelectItem(List<Departamento> dptoList){
		if(departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();
		
		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(Departamento dep : dptoList){
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(), dep.getDescripcion()));
		}
	}
	private void buildCiudadSelectItem(List<Ciudad> ciudadList){
		if(ciudadSelecItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecItem.clear();
		
		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(Ciudad dep : ciudadList){
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep.getDescripcion()));
		}
	}

//	GETTERS Y SETTERS
	
	public Long getCodPais() {
		return codPais;
	}
	public Barrio getBarrio() {
		return barrio;
	}

	public void setBarrio(Barrio barrio) {
		this.barrio = barrio;
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
	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}
	public void setDepartamentosSelecItem(List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public Long getIdBarrio() {
		return idBarrio;
	}

	public void setIdBarrio(Long idBarrio) {
		this.idBarrio = idBarrio;
	}
	
	
	
}
