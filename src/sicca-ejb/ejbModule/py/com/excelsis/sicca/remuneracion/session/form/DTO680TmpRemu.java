package py.com.excelsis.sicca.remuneracion.session.form;

public class DTO680TmpRemu {
	private Integer anhio;
	private Integer mes;
	private String mesDesc;
	private Boolean activo;

	
	public DTO680TmpRemu(Integer anhio, Integer mes, String mesDesc,
			Boolean activo) {
		super();
		this.anhio = anhio;
		this.mes = mes;
		this.mesDesc = mesDesc;
		this.activo = activo;
	}

	public Integer getAnhio() {
		return anhio;
	}

	public void setAnhio(Integer anhio) {
		this.anhio = anhio;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public String getMesDesc() {
		return mesDesc;
	}

	public void setMesDesc(String mesDesc) {
		this.mesDesc = mesDesc;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
}
