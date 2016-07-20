package py.com.excelsis.sicca.util;

import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.entity.EmpresaPersona;
import py.com.excelsis.sicca.entity.Persona;

public class IntegrantesDTO {

	private Long id;
	private String comision;
	private Persona personaComision;
	private EmprTercerizada emprTercerizada;
	private Persona personaEmpresa;
	private EmpresaPersona empresaPersona;
	private ComisionSeleccionCab comisionSeleccionCab;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getComision() {
		return comision;
	}
	public void setComision(String comision) {
		this.comision = comision;
	}
	public Persona getPersonaComision() {
		return personaComision;
	}
	public void setPersonaComision(Persona personaComision) {
		this.personaComision = personaComision;
	}
	public EmprTercerizada getEmprTercerizada() {
		return emprTercerizada;
	}
	public void setEmprTercerizada(EmprTercerizada emprTercerizada) {
		this.emprTercerizada = emprTercerizada;
	}
	public Persona getPersonaEmpresa() {
		return personaEmpresa;
	}
	public void setPersonaEmpresa(Persona personaEmpresa) {
		this.personaEmpresa = personaEmpresa;
	}
	public EmpresaPersona getEmpresaPersona() {
		return empresaPersona;
	}
	public void setEmpresaPersona(EmpresaPersona empresaPersona) {
		this.empresaPersona = empresaPersona;
	}
	public ComisionSeleccionCab getComisionSeleccionCab() {
		return comisionSeleccionCab;
	}
	public void setComisionSeleccionCab(ComisionSeleccionCab comisionSeleccionCab) {
		this.comisionSeleccionCab = comisionSeleccionCab;
	}
	
	
	
	
}
