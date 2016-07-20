package enums;

public enum SexoEnums {
	TODOS(1, "Todos",null), 
	FEMENINO(2, "Femenino", "F"),
	MASCULINO(2, "Masculino", "M");

	private Integer id;
	private String descripcion;
	private String valor;

	private SexoEnums(Integer id, String descripcion, String valor) {
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

	public static SexoEnums getSexoEnumsPorId(Integer id) {
		for (SexoEnums func : SexoEnums.values()) {
			if (id == func.getId()) {
				return func;
			}
		}
		return null;
	}
	
	public static SexoEnums getSexoEnumsPorValor(String valor) {
		for (SexoEnums func : SexoEnums.values()) {
			if (valor.equals(func.getValor())) {
				return func;
			}
		}
		return null;
	}
}
