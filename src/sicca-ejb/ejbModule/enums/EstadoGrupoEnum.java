package enums;

public enum EstadoGrupoEnum {
	ENTREVISTA_MAI(1, "ENTREVISTA MAI", "ENTREVISTA MAI"),
	LISTA_CORTA(2, "LISTA CORTA", "LISTA CORTA");

	private Integer id;
	private String descripcion;
	private String valor;

	private EstadoGrupoEnum(Integer id, String descripcion, String valor) {
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

	public static EstadoGrupoEnum getEstadoPorId(Integer id) {
		for (EstadoGrupoEnum est : EstadoGrupoEnum.values()) {
			if (id == est.getId()) {
				return est;
			}
		}
		return null;
	}
	
	public static EstadoGrupoEnum getEstadoPorValor(String valor) {
		for (EstadoGrupoEnum est : EstadoGrupoEnum.values()) {
			if (valor.equals(est.getValor())) {
				return est;
			}
		}
		return null;
	}

}
