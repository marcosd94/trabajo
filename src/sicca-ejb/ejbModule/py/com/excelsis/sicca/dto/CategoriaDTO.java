package py.com.excelsis.sicca.dto;

public class CategoriaDTO {

	private Long id;
	private String categoria;
	private String denominacion;
	
	public CategoriaDTO(Long id, String categoria, String denominacion) {

		this.id = id;
		this.categoria = categoria;
		this.denominacion = denominacion;
	}
	
	
	
	public CategoriaDTO() {
	
	}



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public String getDenominacion() {
		return denominacion;
	}
	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}
	
	
	
	
	
}
