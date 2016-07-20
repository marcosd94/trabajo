package enums;

public enum TiposDatos {
	BOOLEAN(1, "BOOLEAN", "BOOLEAN"), INTEGER(2, "INTEGER", "INTEGER"), STRING(3, "STRING",
			"STRING"), LONG(4, "LONG", "LONG"), DOUBLE(5, "DOUBLE", "DOUBLE"), FLOAT(6, "FLOAT",
			"FLOAT"), BIGDECIMAL(7, "BIGDECIMAL", "BIGDECIMAL");

	private Integer id;
	private String descripcion;
	private String valor;

	private TiposDatos(Integer id, String descripcion, String valor) {
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

	public static TiposDatos getTiposDatosPorId(Integer id) {
		for (TiposDatos est : TiposDatos.values()) {
			if (id == est.getId()) {
				return est;
			}
		}
		return null;
	}

	public static TiposDatos getTiposDatosPorValor(String valor) {
		for (TiposDatos est : TiposDatos.values()) {
			if (valor.equalsIgnoreCase(est.getValor())) {
				return est;
			}
		}
		return null;
	}
}
