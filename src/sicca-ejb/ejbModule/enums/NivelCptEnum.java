package enums;



public enum NivelCptEnum {
	Todos(0,"Todos", null),
	uno(1, "1", "1") ,
	dos(2, "2", "2"),
	tres(3, "3", "3") ,
	cuatro(4, "4", "4") ,
	cinco(5, "5", "5") ,
	seis(6, "6", "6") ,
	siete(7, "7", "7") 
	;
	
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private NivelCptEnum(Integer id, String descripcion, String valor) {
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
	
	public static NivelCptEnum getNivelCptEnumPorId(String valor) {
		for (NivelCptEnum tr : NivelCptEnum.values()) {
			if (valor.equals(tr.getValor())) {
				return tr;
			}
		}
		return null;
	}
	
	
}
