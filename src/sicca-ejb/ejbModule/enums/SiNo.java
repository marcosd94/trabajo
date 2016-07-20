package enums;

public enum SiNo {
	SI(1, "SI", true), 
	NO(2, "NO", false);

	private Integer id;
	private String descripcion;
	private Boolean valor;

	private SiNo(Integer id, String descripcion, Boolean valor) {
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

	public static SiNo getSiNoPorId(Integer id) {
		for (SiNo func : SiNo.values()) {
			if (id == func.getId()) {
				return func;
			}
		}
		return null;
	}
	
	public static SiNo getSiNoPorValor(Boolean valor) {
		for (SiNo func : SiNo.values()) {
			if (valor == func.getValor()) {
				return func;
			}
		}
		return null;
	}
}
