package enums;

public enum EstadoEnum {
	AGRUPADO(1, "AGRUPADO", "AGRUPADO"), 
	CONCURSO(2, "CONCURSO", "CONCURSO"), 
	LIBRE(2, "LIBRE", "LIBRE"), 
	RESERVA(3,"EN RESERVA", "EN RESERVA");

	private Integer id;
	private String descripcion;
	private String valor;

	private EstadoEnum(Integer id, String descripcion, String valor) {
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

	public static EstadoEnum getEstadoPorId(Integer id) {
		for (EstadoEnum est : EstadoEnum.values()) {
			if (id == est.getId()) {
				return est;
			}
		}
		return null;
	}
	
	public static EstadoEnum getEstadoPorValor(String valor) {
		for (EstadoEnum est : EstadoEnum.values()) {
			if (valor.equals(est.getValor())) {
				return est;
			}
		}
		return null;
	}

}
