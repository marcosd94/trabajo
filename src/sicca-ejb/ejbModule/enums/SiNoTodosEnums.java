package enums;

public enum SiNoTodosEnums {
	TODOS(1, "Todos", null),
	SI(1, "Si", true), 
	NO(2, "No", false);

	private Integer id;
	private String descripcion;
	private Boolean valor;

	private SiNoTodosEnums(Integer id, String descripcion, Boolean valor) {
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

	public Boolean getValor() {
		return valor;
	}

	public void setValor(Boolean valor) {
		this.valor = valor;
	}

	public static SiNoTodosEnums getSiNoTodosEnumsPorId(Integer id) {
		for (SiNoTodosEnums func : SiNoTodosEnums.values()) {
			if (id == func.getId()) {
				return func;
			}
		}
		return null;
	}
	
	public static SiNoTodosEnums getSiNoTodosEnumsPorValor(Boolean valor) {
		for (SiNoTodosEnums func : SiNoTodosEnums.values()) {
			if (valor == func.getValor()) {
				return func;
			}
		}
		return null;
	}
}
