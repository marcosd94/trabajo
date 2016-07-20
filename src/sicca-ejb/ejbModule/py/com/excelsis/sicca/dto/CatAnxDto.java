package py.com.excelsis.sicca.dto;

public class CatAnxDto{

	private Integer objCodigo;
	private String objNombre;
	private String ctgCodigo;
	private Long cntDisponible;
	
	public CatAnxDto(Integer objCodigo, String objNombre, String ctgCodigo,Long cntDisponible) {

		this.objCodigo=objCodigo;
		this.objNombre=objNombre;
		this.ctgCodigo=ctgCodigo;
		this.cntDisponible=cntDisponible;
	}
	
	
	
	public CatAnxDto() {
	
	}



	public Integer getObjCodigo() {
		return objCodigo;
	}



	public void setObjCodigo(Integer objCodigo) {
		this.objCodigo = objCodigo;
	}



	public String getObjNombre() {
		return objNombre;
	}



	public void setObjNombre(String objNombre) {
		this.objNombre = objNombre;
	}



	public String getCtgCodigo() {
		return ctgCodigo;
	}



	public void setCtgCodigo(String ctgCodigo) {
		this.ctgCodigo = ctgCodigo;
	}



	public Long getCntDisponible() {
		return cntDisponible;
	}



	public void setCntDisponible(Long cntDisponible) {
		this.cntDisponible = cntDisponible;
	}




	
	
	
}
