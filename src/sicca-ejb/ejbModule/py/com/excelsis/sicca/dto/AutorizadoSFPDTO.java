package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.Referencias;

public class AutorizadoSFPDTO {

	private ConcursoPuestoAgr concursoPuestoAgr;
	private Referencias referencias;
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}
	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}
	public Referencias getReferencias() {
		return referencias;
	}
	public void setReferencias(Referencias referencias) {
		this.referencias = referencias;
	}
	
	
	
}
