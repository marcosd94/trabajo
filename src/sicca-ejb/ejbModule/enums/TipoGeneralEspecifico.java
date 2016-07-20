package enums;

public enum TipoGeneralEspecifico {
	NINGUNO(null, "Seleccionar...", null), ESPECIFICO(1, "Especifico", "especifico"), GENERAL(2,
			"General", "general");

	private Integer id;
	private String descripcion;
	private String valor;

	private TipoGeneralEspecifico(Integer id, String descripcion, String valor) {

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

	public static TipoGeneralEspecifico getTipoGeneralEspecificoPorId(Integer id) {
		for (TipoGeneralEspecifico mod : TipoGeneralEspecifico.values()) {
			if (id == mod.getId()) {
				return mod;
			}
		}
		return null;
	}
}
