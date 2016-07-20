package enums;

public enum OrdenListadoObj {
	ANHO("ani_aniopre", "A\u00F1o"), OBJETO("obj_codigo", "Objeto");

	private String id;
	private String descripcion;

	private OrdenListadoObj(String id, String descripcion) {
		this.id = id;
		this.descripcion = descripcion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public static Estado getEstadoPorId(Integer id) {
		for (Estado est : Estado.values()) {
			if (id == est.getId()) {
				return est;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return descripcion;
	}
	
}
