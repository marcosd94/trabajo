package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import enums.Estado;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.EmprTercerizadaList;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("empresaTercerizadaListFormController")
public class EmpresaTercerizadaListFormController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7361910966082335018L;

	@In(value="entityManager")
    EntityManager em;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	EmprTercerizadaList emprTercerizadaList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	CiudadList ciudadList;
	
	private EmprTercerizada empresaTercerizada= new EmprTercerizada();
	private Long codPais, codDepartamento, codCiudad;
	
	private List<SelectItem> departamentoSelectItems, ciudadSelectItems;
	private List<EmprTercerizada> resultList = null;
	
	
	public void init(){
		updateDepartamento();
		updateCiudad();
		searchAll();
	}
	
	public void search(){
		emprTercerizadaList.getPais().setIdPais(codPais);
		emprTercerizadaList.getDepartamento().setIdDepartamento(codDepartamento);
		emprTercerizadaList.getCiudad().setIdCiudad(codCiudad);
		emprTercerizadaList.getEmprTercerizada().setActivo(empresaTercerizada.getActivo());
		if(empresaTercerizada.getNombre() != null && !empresaTercerizada.getNombre().isEmpty())
			emprTercerizadaList.getEmprTercerizada().setNombre(empresaTercerizada.getNombre().trim().toLowerCase());
			
		resultList = emprTercerizadaList.getResultList();
	}
	
	public void searchAll(){
		codPais = null;
		codDepartamento = null;
		codCiudad = null;
		empresaTercerizada = new EmprTercerizada();
		empresaTercerizada.setActivo(Estado.ACTIVO.getValor());
		emprTercerizadaList.getEmprTercerizada().setNombre(null);
		search();
	}
	
	public void print(){
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		
		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String,Object>>();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		
		if(resultList == null || resultList.isEmpty()){
			FacesContext.getCurrentInstance()
				.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
						"No existen datos...", "No hay datos..."));
			return;
		}
		for(EmprTercerizada emp : resultList){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("pais", emp.getCiudad().getDepartamento().getPais().getDescripcion());
			map.put("departamento", emp.getCiudad().getDepartamento().getDescripcion());
			map.put("ciudad", emp.getCiudad().getDescripcion());
			map.put("empresa", emp.getNombre());
			map.put("direccion", emp.getDireccion());
			map.put("ruc", emp.getRuc());
			map.put("telefono", emp.getTelefono());
			map.put("mail", emp.getEMail());
			map.put("activo", emp.getActivo() ? "SI" : "NO");
			
			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("ListarEmpresasTercerizadasUC195", false, listaDatosReporte, param);
	}
	
	public void updateDepartamento(){
		departamentoSelectItems = inicializarSelectItems(departamentoSelectItems);
		if(codPais != null){
			departamentoList.getPais().setIdPais(codPais);
			departamentoList.setMaxResults(null);
			List<Departamento> list = departamentoList.getResultList();
			for(Departamento dep : list){
				departamentoSelectItems.add(new SelectItem(dep.getIdDepartamento(), dep.getDescripcion()));
			}
		}
	}
	
	public void updateCiudad(){
		ciudadSelectItems = inicializarSelectItems(ciudadSelectItems);
		if(codDepartamento != null){
			ciudadList.getDepartamento().setIdDepartamento(codDepartamento);
			ciudadList.setMaxResults(null);
			List<Ciudad> list = ciudadList.getResultList();
			for(Ciudad c : list){
				ciudadSelectItems.add(new SelectItem(c.getIdCiudad(), c.getDescripcion()));
			}
		}
	}
	
//	METODOS PRIVADOS
	private List<SelectItem> inicializarSelectItems(List<SelectItem> listToInit){
		if(listToInit == null)
			listToInit = new ArrayList<SelectItem>();
		else
			listToInit.clear();
		
		listToInit.add(new SelectItem(null, SICCAAppHelper.getBundleMessage("opt_select_one")));
		return listToInit;
	}

	
//	GETTERS Y SETTERS
	public EmprTercerizada getEmpresaTercerizada() {
		return empresaTercerizada;
	}
	public void setEmpresaTercerizada(EmprTercerizada empresaTercerizada) {
		this.empresaTercerizada = empresaTercerizada;
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
	public Long getCodCiudad() {
		return codCiudad;
	}
	public void setCodCiudad(Long codCiudad) {
		this.codCiudad = codCiudad;
	}

	public List<SelectItem> getDepartamentoSelectItems() {
		return departamentoSelectItems;
	}

	public void setDepartamentoSelectItems(List<SelectItem> departamentoSelectItems) {
		this.departamentoSelectItems = departamentoSelectItems;
	}
	public List<SelectItem> getCiudadSelectItems() {
		return ciudadSelectItems;
	}
	public void setCiudadSelectItems(List<SelectItem> ciudadSelectItems) {
		this.ciudadSelectItems = ciudadSelectItems;
	}
	public List<EmprTercerizada> getResultList() {
		return resultList;
	}
	public void setResultList(List<EmprTercerizada> resultList) {
		this.resultList = resultList;
	}

}
