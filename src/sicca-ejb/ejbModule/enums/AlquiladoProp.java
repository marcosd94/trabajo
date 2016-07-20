package enums;

public enum AlquiladoProp {
	ALQUILADO(1, "ALQUILADO", true), PROPIO(2, "PROPIO", false);

	private Integer id;
	private String descripcion;
	private Boolean valor;

	private AlquiladoProp(Integer id, String descripcion, Boolean valor) {
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

	public static AlquiladoProp getAlquiladoPropPorId(Integer id) {
		for (AlquiladoProp est : AlquiladoProp.values()) {
			if (id == est.getId()) {
				return est;
			}
		}
		return null;
	}

}
