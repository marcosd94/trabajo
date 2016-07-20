package enums;

public enum TipoValidacion {
	NINGUNO(
			null, "NINGUNO", "SELECCIONE..."), PERSONA(1, "PERSONA", "PERSONA"), PUESTO(2, "PUESTO", "PUESTO");
	private Integer id;
	private String descripcion;
	private String valor;
	
	

	private TipoValidacion(Integer id, String descripcion, String valor) {
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

	public static TipoValidacion getTipoValidacionPorId(Integer id) {
		for (TipoValidacion est : TipoValidacion.values()) {
			if (id == est.getId()) {
				return est;
			}
		}
		return null;
	}

	public static TipoValidacion getTipoValidacionPorValor(String valor) {
		for (TipoValidacion est : TipoValidacion.values()) {
			if (valor.equalsIgnoreCase(est.getValor())) {
				return est;
			}
		}
		return null;
	}

}
