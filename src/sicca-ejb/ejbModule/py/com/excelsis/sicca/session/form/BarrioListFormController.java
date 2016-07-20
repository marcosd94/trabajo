package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

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
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("barrioListFormController")
public class BarrioListFormController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	@In(create = true)
	BarrioList barrioList;
	
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	
	private Barrio barrio= new Barrio();
	private Long idCiudad=null;
	private Long codPais = null;
	private Long codDepartamento = null;
	private Estado estado = Estado.ACTIVO;
	
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private String fromLink=null;
	
	public void init(){
		if(fromLink==null){
			barrio = new Barrio();
			codPais = null;
			codDepartamento = null;
			idCiudad=null;
			estado = Estado.ACTIVO;
		}
		updateDepartamento();
		updateCiudad();
		search();
		
		
	}
	
	public void search(){
		barrioList.setIdPais(codPais);
		barrioList.setIdDepartamento(codDepartamento);
		barrioList.setIdCiudad(idCiudad);
		barrioList.getBarrio().setDescripcion(barrio.getDescripcion());
		barrioList.setEstado(estado);
		barrioList.listaResultados();
	}
	
	public void searchAll(){
		barrio = new Barrio();
		codPais = null;
		codDepartamento = null;
		idCiudad=null;
		estado = Estado.ACTIVO;
		barrioList.limpiar();
	}
	
	public void updateDepartamento(){
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
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
	private List<Ciudad> getCiudadByDpto(){
		if(codDepartamento!= null){
			ciudadList.getDepartamento().setIdDepartamento(codDepartamento);
			return ciudadList.listarPorDpto();
		}
		return new ArrayList<Ciudad>();
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

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
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

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public String getFromLink() {
		return fromLink;
	}

	public void setFromLink(String fromLink) {
		this.fromLink = fromLink;
	}
	
	
}
