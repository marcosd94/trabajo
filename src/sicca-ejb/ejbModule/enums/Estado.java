package enums;

public enum Estado {
	ACTIVO(1, "ACTIVO", true), INACTIVO(2, "INACTIVO", false), TODOS(null,
			"TODOS", null);

	private Integer id;
	private String descripcion;
	private Boolean valor;

	private Estado(Integer id, String descripcion, Boolean valor) {
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

	public static Estado getEstadoPorId(Integer id) {
		for (Estado est : Estado.values()) {
			if (id == est.getId()) {
				return est;
			}
		}
		return null;
	}
	
	public static Estado getEstadoPorValor(Boolean valor) {
		for (Estado est : Estado.values()) {
			if (valor == est.getValor()) {
				return est;
			}
		}
		return null;
	}

}
