package enums;

public enum TitularSuplente {
	TITULAR(1, "TITULAR", "T"), 
	SUPLENTE(2, "SUPLENTE", "S");

	private Integer id;
	private String descripcion;
	private String valor;

	private TitularSuplente(Integer id, String descripcion, String valor) {
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

	public static TitularSuplente getTitularSuplentePorId(Integer id) {
		for (TitularSuplente func : TitularSuplente.values()) {
			if (id == func.getId()) {
				return func;
			}
		}
		return null;
	}
	
	public static TitularSuplente getTitularSuplentePorValor(String valor) {
		for (TitularSuplente func : TitularSuplente.values()) {
			if (valor.equals(func.getValor())) {
				return func;
			}
		}
		return null;
	}
}
