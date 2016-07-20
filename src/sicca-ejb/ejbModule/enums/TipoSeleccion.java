package enums;

public enum TipoSeleccion{
	CONCURSO("C", "Concurso"), 
	INSCRIPCION_DIRECTA("I", "Inscripción Directa");

	private String id;
	private String descripcion;
	
	
	
	private TipoSeleccion(String id, String descripcion) {
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
	
	public static TipoSeleccion getTipoPorId(String id) {
		for (TipoSeleccion tc : TipoSeleccion.values()) {
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
