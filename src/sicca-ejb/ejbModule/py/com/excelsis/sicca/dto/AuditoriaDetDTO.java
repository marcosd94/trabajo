package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.AuditLegajoDet;

public class AuditoriaDetDTO {

	private AuditLegajoDet auditLegajoDet;
	private Boolean seleccionado;
	
	
	public AuditoriaDetDTO() {
		
	}


	public AuditLegajoDet getAuditLegajoDet() {
		return auditLegajoDet;
	}


	public void setAuditLegajoDet(AuditLegajoDet auditLegajoDet) {
		this.auditLegajoDet = auditLegajoDet;
	}


	public Boolean getSeleccionado() {
		return seleccionado;
	}


	public void setSeleccionado(Boolean seleccionado) {
		this.seleccionado = seleccionado;
	}
	
	
	
}
