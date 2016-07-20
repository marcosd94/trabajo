package enums;

import py.com.excelsis.sicca.util.SICCAAppHelper;

public enum TipoPostulacionEnums {
	Oniline(1,"On-line","O"),
	Carpeta(2, "Carpeta", "C") 
	;
	
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private TipoPostulacionEnums(Integer id, String descripcion, String valor) {
		this.id = id;
		this.descripcion = descripcion;
		this.valor = valor;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public static TipoPostulacionEnums getTipoPostulacionEnumsPorId(String valor) {
		for (TipoPostulacionEnums tr : TipoPostulacionEnums.values()) {
			if (valor.equals(tr.getValor())) {
				return tr;
			}
		}
		return null;
	}
	
	
}
