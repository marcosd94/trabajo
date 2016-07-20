package py.com.excelsis.sicca.seleccion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;






import py.com.excelsis.sicca.entity.NivelesDeCargos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.NivelesDeCargosHome;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
@Name("nivelesDeCargosFormController")
@Scope(ScopeType.PAGE)
public class NivelesDeCargosFormController {
	
	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;
	
//	@In(required = false)
	//NivelesDeCargosHome nivelesDeCargosHome;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	private Long idNivelesDeCargo;
	private String tipo;
	private String descripcion;
	private NivelesDeCargos nivelesDeCargos;
	

	private Boolean activo=true;
	private String radioButton = "1";
	private Boolean isEdit=false;
	private Boolean isNew=false;
	private Boolean isView=false;
	private String usuario;
	private Date fecha = new Date();
	private List<NivelesDeCargos> nivelesDeCargosList = new ArrayList<NivelesDeCargos>();
	
	private String mensaje;
	
	public void init() {
		searchAll();
	}
	public void cambiarEdit(){
		isEdit = (isEdit)?false:true;
	}
	public void cambiarNew(){
		isNew = (isNew)?false:true;
	}
	public void cambiarView(){
		isView=(isView)?false:true;
	}
	public void allFalse(){
		isEdit=isView=isNew=false;
	}
	@SuppressWarnings("unchecked")
	public void searchAll() {
		nivelesDeCargosList = new ArrayList<NivelesDeCargos>();
		String q = "select n from NivelesDeCargos n";
		nivelesDeCargosList = em.createQuery(q).getResultList();
		

	}
	@SuppressWarnings("unchecked")
	public void search(){
		nivelesDeCargosList = new ArrayList<NivelesDeCargos>();
		String q = "select n from NivelesDeCargos n where 1=1 ";
		if(!descripcion.trim().isEmpty())
			q+=" and n.descripcion like upper('%"+ descripcion.toUpperCase().trim() + "%') ";
		if(!tipo.trim().isEmpty())
			q+=" and n.tipo like upper('%"+tipo.toUpperCase().trim()+ "%') ";
		
		if(radioButton.equals("1"))
			q+=" and n.activo = true ";
		else if(radioButton.equals("2"))
			q+=" and n.activo = false ";
		else
			q+="";
		q+=" order by n.tipo asc";
		
		nivelesDeCargosList = em.createQuery(q).getResultList();
		
	}
	public void init2(){
		if(isEdit || isView){
			nivelesDeCargos=em.find(NivelesDeCargos.class, idNivelesDeCargo);
		}else{
			nivelesDeCargos=new NivelesDeCargos();
			nivelesDeCargos.setActivo(true);
			nivelesDeCargos.setDescripcion("");
			nivelesDeCargos.setTipo("");
		}
		
	}
	@SuppressWarnings("unchecked")
	public void searchByTipo(String tipoFromSearch) {
		nivelesDeCargosList = new ArrayList<NivelesDeCargos>();
		String q = "select * from planificacion.niveles_de_cargos where tipo like upper('"
				+ tipoFromSearch + "')";
		nivelesDeCargosList = em.createNativeQuery(q).getResultList();
	}

	@SuppressWarnings("unchecked")
	public void searchByDescripcion(String descripcionFromSearch) {
		nivelesDeCargosList = new ArrayList<NivelesDeCargos>();
		String q = "select * from planificacion.niveles_de_cargos where descripcion like upper('%"
				+ descripcionFromSearch + "%')";
		nivelesDeCargosList = em.createNativeQuery(q).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String q="select n from NivelesDeCargos n where ";
		if(operation.equalsIgnoreCase("update")){
			q += " n.idNivelesDeCargo != " + idNivelesDeCargo+" and ";
		}
		
		q+= "(n.tipo=upper('"+nivelesDeCargos.getTipo().trim()+"') or  n.descripcion like upper('"+nivelesDeCargos.getDescripcion().trim()+"'))";
		
		List<NivelesDeCargos> list = em.createQuery(q).getResultList();
		return list.isEmpty();
	}
	
	public String update() {
		try {
			if(!validado())
				return null;
			nivelesDeCargos.setDescripcion(nivelesDeCargos.getDescripcion().toUpperCase().trim());
			nivelesDeCargos.setActivo(nivelesDeCargos.getActivo());
			nivelesDeCargos.setTipo(nivelesDeCargos.getTipo().toUpperCase().trim());
			nivelesDeCargos.setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
			nivelesDeCargos.setFechaMod(new Date());
			em.merge(nivelesDeCargos);
			em.flush();
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		cambiarEdit();
		mensaje = "Registro actualizado con exito";
		statusMessages.add(Severity.INFO, mensaje);
		return "updated";
}
	
	public String save() {
		try {
			if(!validado())
				return null;
			
			nivelesDeCargos.setDescripcion(nivelesDeCargos.getDescripcion().toUpperCase().trim());
			nivelesDeCargos.setActivo(nivelesDeCargos.getActivo());
			nivelesDeCargos.setTipo(nivelesDeCargos.getTipo().toUpperCase().trim());
			nivelesDeCargos.setFechaAlta(new Date());
			nivelesDeCargos.setUsuarioAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
			
			em.persist(nivelesDeCargos);
			em.flush(); 
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
		cambiarNew();
		mensaje = "Registro creado con exito";
		statusMessages.add(Severity.INFO, mensaje);
		return "persisted";
	}
	private Boolean validado(){
		if(isNew && !checkDuplicate("persist")){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		if(isEdit && !checkDuplicate("update")){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		if(nivelesDeCargos.getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if(seguridadUtilFormController.contieneCaracter(nivelesDeCargos.getDescripcion().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(nivelesDeCargos.getTipo().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, "Debe completar el campo Tipo");
			return false;
		}
		if(nivelesDeCargos.getTipo().length() > 1){
			statusMessages.add(Severity.ERROR, "Tipo debe tener un solo caracter");
			return false;
		}
		
			
		return true; 
	}
	
	//GETTERS Y SETTERS
	public List<NivelesDeCargos> getNivelesDeCargosList() {
		return nivelesDeCargosList;
	}
	public void setNivelesDeCargosList(List<NivelesDeCargos> nivelesDeCargosList) {
		this.nivelesDeCargosList = nivelesDeCargosList;
	}
	public Long getIdNivelesDeCargo() {
		return idNivelesDeCargo;
	}
	public void setIdNivelesDeCargo(Long idNivelesDeCargo) {
		this.idNivelesDeCargo = idNivelesDeCargo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public String getRadioButton() {
		return radioButton;
	}

	public void setRadioButton(String radioButton) {
		this.radioButton = radioButton;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public Boolean getIsView() {
		return isView;
	}

	public void setIsView(Boolean isView) {
		this.isView = isView;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public NivelesDeCargos getNivelesDeCargos() {
		return nivelesDeCargos;
	}

	public void setNivelesDeCargos(NivelesDeCargos nivelesDeCargos) {
		this.nivelesDeCargos = nivelesDeCargos;
	}

}
