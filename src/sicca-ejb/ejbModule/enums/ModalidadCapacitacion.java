package enums;

public enum ModalidadCapacitacion {
	PRESENCIAL("P", "Presencial"), 
	SEMI_PRESENCIAL("S", "Semi-presencial"),
	VIRTUAL("V", "Virtual");

	private String id;
	private String descripcion;
	
	
	
	private ModalidadCapacitacion(String id, String descripcion) {
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
	
	public static ModalidadCapacitacion getTipoPorId(String id) {
		for (ModalidadCapacitacion tc : ModalidadCapacitacion.values()) {
			if (id.equals(tc.getId())) {
				return tc;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return descripcion;
	}

	
}
