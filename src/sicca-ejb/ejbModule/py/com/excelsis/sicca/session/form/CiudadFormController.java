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
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.session.CiudadHome;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("ciudadFormController")
public class CiudadFormController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;
	
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	@In(create = true)
	DepartamentoList departamentoList;
	
	@In(create =  true)
	CiudadHome ciudadHome;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	private Ciudad ciudad;
	private Long codPais;
	private Long codDepartamento;
	
	private List<SelectItem> departamentosSelecItem;
	
	public void init(){
		ciudad = ciudadHome.getInstance();
		if(ciudadHome.isIdDefined()){
			codDepartamento = ciudad.getDepartamento().getIdDepartamento();
			codPais = ciudad.getDepartamento().getPais().getIdPais();
		} else
			ciudad.setActivo(Estado.ACTIVO.getValor());
		
		updateDepartamento();
		
	}
	
	public String save(){
		try {
			if(ciudad.getDescripcion().trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe Ingresar una descripción. Verifique");
				return null;
			}
			if(seguridadUtilFormController.contieneCaracter(ciudad.getDescripcion().trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return null;
			}
				
			ciudadHome.setInstance(ciudad);
			ciudadHome.setIdDepartamento(codDepartamento);
			return ciudadHome.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void updateDepartamento(){
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
	}
	
//	METODOS PRIVADOS
	private List<Departamento> getDepartamentosByPais(){
		if(codPais != null){
			departamentoList.getPais().setIdPais(codPais);
			departamentoList.setMaxResults(null);
			departamentoList.setOrder("descripcion");
			return departamentoList.getResultList();
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

//	GETTERS Y SETTERS
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
	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}
	public void setDepartamentosSelecItem(List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}
}
