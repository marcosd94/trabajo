package enums;

public enum PorInstitucion {
	TODOS(1, "Todas Las Instituciones", false),  POR(2,"Por Institucion", true);

	private Integer id;
	private String descripcion;
	private Boolean valor;

	private PorInstitucion(Integer id, String descripcion, Boolean valor) {
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

	public static PorInstitucion getPorInstitucionPorId(Integer id) {
		for (PorInstitucion est : PorInstitucion.values()) {
			if (id == est.getId()) {
				return est;
			}
		}
		return null;
	}

	public static PorInstitucion getPorInstitucionPorValor(Boolean valor) {
		for (PorInstitucion est : PorInstitucion.values()) {
			if (valor == est.getValor()) {
				return est;
			}
		}
		return null;
	}

}
