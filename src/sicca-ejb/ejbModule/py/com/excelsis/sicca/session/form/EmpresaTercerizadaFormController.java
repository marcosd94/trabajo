package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.entity.EmpresaPersona;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.EmprTercerizadaHome;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.PAGE)
@Name("empresaTercerizadaFormController")
public class EmpresaTercerizadaFormController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1458624856853762950L;
	@In(value="entityManager")
    EntityManager em;
	@In
	StatusMessages statusMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	EmprTercerizadaHome emprTercerizadaHome;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	CiudadList ciudadList;
	
	private EmprTercerizada empresaTercerizada;
	private Long codPais, codDepartamento, codCiudad;
	
	private List<SelectItem> departamentoSelectItems, ciudadSelectItems;
	
	public void init(){
		empresaTercerizada = emprTercerizadaHome.getInstance();
		if(emprTercerizadaHome.isIdDefined()){
			codCiudad = empresaTercerizada.getCiudad().getIdCiudad();
			codDepartamento = empresaTercerizada.getCiudad().getDepartamento().getIdDepartamento();
			codPais = empresaTercerizada.getCiudad().getDepartamento().getPais().getIdPais();
		}
		updateDepartamento();
		updateCiudad();
	}
	
	public String save(){
		String result = null;
		String titleMessage = null;
		try {
			if(!checkData()){
				return null;
			}
			empresaTercerizada.setCiudad(em.find(Ciudad.class, codCiudad));
			emprTercerizadaHome.setInstance(empresaTercerizada);
			
			if(!emprTercerizadaHome.isIdDefined()){
				result = emprTercerizadaHome.persist();
				titleMessage = SeamResourceBundle.getBundle().getString("msg_save_record_title_messages");
			}else{ 
				if(!empresaTercerizada.getActivo())
					inactivar();
				else
					activar();
				result = emprTercerizadaHome.update();
				titleMessage = SeamResourceBundle.getBundle().getString("msg_update_record_title_messages");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null){
				FacesContext.getCurrentInstance()
					.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
							titleMessage, 
							SeamResourceBundle.getBundle().getString("msg_operacion_exitosa")));
			}
		}
		return result;
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
	private boolean checkData(){
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		String operation = emprTercerizadaHome.isIdDefined() ? "update" : "persist";
		if(empresaTercerizada.getNombre()!=null && sufc.contieneCaracter(empresaTercerizada.getNombre().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}if(empresaTercerizada.getDireccion()!=null && sufc.contieneCaracter(empresaTercerizada.getDireccion().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(empresaTercerizada.getTelefono()!=null && sufc.contieneCaracter(empresaTercerizada.getTelefono().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(empresaTercerizada.getRuc()!=null && sufc.contieneCaracter(empresaTercerizada.getRuc().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		
		if(codPais == null){
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("EmprTercerizada_msg_sin_pais"));
			return false;
		}
		if(codDepartamento == null){
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("EmprTercerizada_msg_sin_departamento"));
			return false;
		}
		if(codCiudad == null){
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("EmprTercerizada_msg_sin_ciudad"));
			return false;
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, "El Ruc ingresado ya existe. Verifique");
			return false;
		}
		return true;
	}
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM EmprTercerizada o WHERE  o.ruc = :ruc and o.activo= true" +
				"";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idEmpresaTercerizada != " +emprTercerizadaHome.getEmprTercerizadaIdEmpresaTercerizada() ;
		}
		List<RequisitoMinimoCpt> list = em.createQuery(hql).setParameter("ruc", empresaTercerizada.getRuc()).getResultList();
		return list.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	private void  inactivar(){
		List<EmpresaPersona> empresaPersonas= em.createQuery("Select em from EmpresaPersona em" +
				" where em.emprTercerizada.idEmpresaTercerizada="+empresaTercerizada.getIdEmpresaTercerizada()).getResultList();
		for (int i = 0; i < empresaPersonas.size(); i++) {
			EmpresaPersona ep= em.find(EmpresaPersona.class,empresaPersonas.get(i).getIdSelEmpresaPersona());
			ep.setActivo(false);
			ep.setUsuMod(usuarioLogueado.getCodigoUsuario());
			ep.setFechaMod(new Date());
			em.flush();
		}
	}
	@SuppressWarnings("unchecked")
	private void  activar(){
		List<EmpresaPersona> empresaPersonas= em.createQuery("Select em from EmpresaPersona em" +
				" where em.emprTercerizada.idEmpresaTercerizada="+empresaTercerizada.getIdEmpresaTercerizada()).getResultList();
		for (int i = 0; i < empresaPersonas.size(); i++) {
			EmpresaPersona ep= em.find(EmpresaPersona.class,empresaPersonas.get(i).getIdSelEmpresaPersona());
			ep.setActivo(true);
			ep.setUsuMod(usuarioLogueado.getCodigoUsuario());
			ep.setFechaMod(new Date());
			em.flush();
		}
	}
	
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

	
}
