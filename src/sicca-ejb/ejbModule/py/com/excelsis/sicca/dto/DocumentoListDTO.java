package py.com.excelsis.sicca.dto;

import java.util.Date;

import py.com.excelsis.sicca.entity.Documentos;

public class DocumentoListDTO {

	private Long idDetalleDoc;
	private Documentos documentos;
	private Long  idCU;
	private Class<?> clazz;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	
	
	
	public DocumentoListDTO() {
		 
	}



	public Long getIdDetalleDoc() {
		return idDetalleDoc;
	}



	public void setIdDetalleDoc(Long idDetalleDoc) {
		this.idDetalleDoc = idDetalleDoc;
	}



	public Documentos getDocumentos() {
		return documentos;
	}



	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}



	public Long getIdCU() {
		return idCU;
	}



	public void setIdCU(Long idCU) {
		this.idCU = idCU;
	}



	public boolean isActivo() {
		return activo;
	}





	public Boolean getActivo() {
		return activo;
	}



	public void setActivo(Boolean activo) {
		this.activo = activo;
	}



	public void setUsuAlta(String usuAlta) {
		this.usuAlta = usuAlta;
	}



	public Date getFechaAlta() {
		return fechaAlta;
	}



	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}



	public String getUsuMod() {
		return usuMod;
	}



	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}



	public Date getFechaMod() {
		return fechaMod;
	}



	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}



	public String getUsuAlta() {
		return usuAlta;
	}



	public Class<?> getClazz() {
		return clazz;
	}



	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}




	
	




	
	
}
