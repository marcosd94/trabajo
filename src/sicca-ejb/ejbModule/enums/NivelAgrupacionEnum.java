package enums;



public enum NivelAgrupacionEnum {
	Todos(2,"Todos", "T"),
	Alta(1, "Alta Gerencia", "J") ,
	Operativos(1, "Operativo", "O")
	;
	
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private NivelAgrupacionEnum(Integer id, String descripcion, String valor) {
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
	
	public static NivelAgrupacionEnum getNivelAgrupacionEnumPorId(String valor) {
		for (NivelAgrupacionEnum tr : NivelAgrupacionEnum.values()) {
			if (valor.equals(tr.getValor())) {
				return tr;
			}
		}
		return null;
	}
	
	
}
