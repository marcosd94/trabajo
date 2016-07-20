package py.com.excelsis.sicca.seleccion.session.form;

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
import org.jboss.seam.international.StatusMessage.Severity;

import bsh.util.Util;
import enums.Estado;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("admPersonaListFormController")
public class AdmPersonaListFormController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	@In(create = true)
	PersonaList personaList;
	@In(value = "entityManager")
	EntityManager em;
	
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	
	

	private Long idCiudad=null;
	private Long codPais = null;
	private Long codDepartamento = null;
	private Estado estado = Estado.ACTIVO;
	private Persona persona= new Persona();
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private Long idPaisExp;
	private String nroDoc;//PARA EL RETORNO DESDE LEGAJO A LA PANTALLA FAMILIARES
	private String texto;
	
	public void init(){
		search();
		updateCiudad();
		updateDepartamento();
		
	}
	
	public void search(){
		if (!validar()){
			statusMessages.add(Severity.ERROR, "Debe seleccionar por lo menos un filtro antes de buscar.");
			return;
		}
			
		
		personaList.setIdPais(codPais);
		personaList.setIdDepartamento(codDepartamento);
		personaList.setIdCiudad(idCiudad);
		if(persona.getDocumentoIdentidad()!=null)
			personaList.getPersona().setDocumentoIdentidad(persona.getDocumentoIdentidad());
		else
			personaList.getPersona().setDocumentoIdentidad(null);
		if(persona.getNombres()!=null)
			personaList.getPersona().setNombres(persona.getNombres().trim());
		else
			personaList.setPersistenceContext(null);
		if(persona.getApellidos()!=null){
			personaList.getPersona().setApellidos(persona.getApellidos().trim());
		}else
			personaList.getPersona().setApellidos(null);
		personaList.setIdPaisExp(idPaisExp);
		personaList.setEstado(estado);
		personaList.setIdPaisExp(idPaisExp);
		personaList.listaResultados();
	}
	
	public void searchAll(){
		persona = new Persona();
		codPais = null;
		codDepartamento = null;
		idCiudad=null;
		estado = Estado.ACTIVO;
		idPaisExp=null;
		idPaisExp=null;
		personaList.clean();
		updateCiudad();
		updateDepartamento();
	}
	
	
	private Boolean validar() {
		try{
			if (Utiles.vacio(persona.getDocumentoIdentidad()) && Utiles.vacio(persona.getNombres()) && Utiles.vacio(persona.getApellidos())
					&& codPais == null && codDepartamento == null && idCiudad == null&& idPaisExp==null) {
				return false;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	
	public Boolean mostrarLista(){
		boolean mostrar = validar();
		return mostrar;
	}

	
	private List<Ciudad> getCiudadByDpto(){
		if(codDepartamento!= null){
			ciudadList.getDepartamento().setIdDepartamento(codDepartamento);
			return ciudadList.listarPorDpto();
		}
		return new ArrayList<Ciudad>();
	}
	
	
//	METODOS PRIVADOS 
	@SuppressWarnings("unchecked")
	private Long idParaguay(){
				List<Pais> p= em.createQuery(" Select p from Pais p" +
						" where lower(p.descripcion) like 'paraguay'").getResultList();
				if(!p.isEmpty())
					return p.get(0).getIdPais();
				
				return null;
	}
	private List<Departamento> getDepartamentosByPais(){
		if(codPais != null){
			departamentoList.getPais().setIdPais(codPais);
			departamentoList.setOrder("descripcion");
			
			return departamentoList.litarPorPais();
		}
		return new ArrayList<Departamento>();
	}
	private void buildDepartamentoDirSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep
					.getIdDepartamento(), dep.getDescripcion()));
		}
		updateCiudad();
	}
	private void buildCiudadSelectItem(List<Ciudad> ciudadList) {
		if (ciudadSelecItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecItem.clear();

		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}
	
	
	
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoDirSelectItem(dptoList);
		//codDepartamento = null;
		//idCiudad = null;

	}

	/**
	 * Combo ciudad 
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		
	}

//	GETTERS Y SETTERS
	
	public Long getCodPais() {
		return codPais;
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

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}
	
	public PersonaList getPersonaList(){
		return this.personaList;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getNroDoc() {
		return nroDoc;
	}

	public void setNroDoc(String nroDoc) {
		this.nroDoc = nroDoc;
	}
	
	
}
