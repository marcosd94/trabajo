package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EvalDocumentalDet;

public class EvalDocumentalDetDTO {
	private Integer nro;
	private EvalDocumentalDet evalDocumentalDet;
	private DatosEspecificos tipoDocumento;
	private Boolean obligatorio;
	private Boolean presento;
	private Boolean aprobado;
	private Documentos documentos;
	public Integer getNro() {
		return nro;
	}
	public void setNro(Integer nro) {
		this.nro = nro;
	}
	public EvalDocumentalDet getEvalDocumentalDet() {
		return evalDocumentalDet;
	}
	public void setEvalDocumentalDet(EvalDocumentalDet evalDocumentalDet) {
		this.evalDocumentalDet = evalDocumentalDet;
	}
	public DatosEspecificos getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(DatosEspecificos tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public Boolean getObligatorio() {
		return obligatorio;
	}
	public void setObligatorio(Boolean obligatorio) {
		this.obligatorio = obligatorio;
	}
	public Boolean getPresento() {
		return presento;
	}
	public void setPresento(Boolean presento) {
		this.presento = presento;
	}
	public Boolean getAprobado() {
		return aprobado;
	}
	public void setAprobado(Boolean aprobado) {
		this.aprobado = aprobado;
	}
	public Documentos getDocumentos() {
		return documentos;
	}
	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}
	
	
	
}
