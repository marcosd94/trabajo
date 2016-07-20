package py.com.excelsis.sicca.util;

import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.session.CondicionSegurList;

public class CU232DTO {
	private Boolean selected = false;
	private DetCondicionSegur det;
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	public DetCondicionSegur getDet() {
		return det;
	}
	public void setDet(DetCondicionSegur det) {
		this.det = det;
	}
	
	
	
}
