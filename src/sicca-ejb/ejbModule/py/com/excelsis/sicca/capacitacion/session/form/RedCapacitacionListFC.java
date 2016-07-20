package py.com.excelsis.sicca.capacitacion.session.form;


import java.io.Serializable;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import enums.Estado;
import py.com.excelsis.sicca.capacitacion.session.RedCapacitacionList;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;



@Scope(ScopeType.CONVERSATION)
@Name("redCapacitacionListFC")
public class RedCapacitacionListFC implements Serializable{

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	RedCapacitacionList redCapacitacionList;
	
	@In(value = "entityManager")
	EntityManager em;
	
	
	
	
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	
	
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
	private Estado estado = Estado.ACTIVO;
	private Persona persona= new Persona();
	private Long idPaisExp= null;
	private boolean primeraEntrada=true;
	
	
	

	public void init(){
		try {
			nivelEntidadOeeUtil.init();
			if(primeraEntrada){
				primeraEntrada=false;
				nivelEntidadOeeUtil.limpiar();
			}
			search();
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
						
		
	}
	
	
	
	public void search(){
		
		redCapacitacionList.setPersona(new Persona());
		if(persona.getNombres()!=null && !persona.getNombres().trim().equals(""))
			redCapacitacionList.getPersona().setNombres(persona.getNombres());
		if(persona.getApellidos()!=null && !persona.getApellidos().trim().equals(""))
			redCapacitacionList.getPersona().setApellidos(persona.getApellidos());
		
		if(persona.getDocumentoIdentidad()!=null && !persona.getDocumentoIdentidad().trim().equals(""))
			redCapacitacionList.getPersona().setDocumentoIdentidad(persona.getDocumentoIdentidad());
		
		
			redCapacitacionList.setEntCodigo(nivelEntidadOeeUtil.getCodSinEntidad() != null
					? nivelEntidadOeeUtil.getCodSinEntidad() : null);
			redCapacitacionList.setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad() != null
				? nivelEntidadOeeUtil.getCodNivelEntidad() : null);
			if (nivelEntidadOeeUtil.getOrdenUnidOrg() != null)
				redCapacitacionList.setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
			
		
		
		redCapacitacionList.setEstado(estado);
		redCapacitacionList.setIdPais(idPaisExp);
		redCapacitacionList.listaResultados();
	

	}
	
	
	
	public void searchAll(){
		
		estado=Estado.ACTIVO;
		persona= new Persona();
		idPaisExp=null;
		nivelEntidadOeeUtil.limpiar();
		redCapacitacionList.limpiarCU489();
	}
	
	
	

//		GETTERS Y SETTERS 


	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Persona getPersona() {
		return persona;
	}



	public void setPersona(Persona persona) {
		this.persona = persona;
	}



	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}



	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}



	public Long getIdPaisExp() {
		return idPaisExp;
	}



	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}



	


}
