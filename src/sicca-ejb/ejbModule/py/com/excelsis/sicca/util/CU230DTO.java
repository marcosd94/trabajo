package py.com.excelsis.sicca.util;

import py.com.excelsis.sicca.entity.DetCondicionTrabajo;

public class CU230DTO {
	private Boolean selected = false;
	private DetCondicionTrabajo det;

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public DetCondicionTrabajo getDet() {
		return det;
	}

	public void setDet(DetCondicionTrabajo det) {
		this.det = det;
	}
}
