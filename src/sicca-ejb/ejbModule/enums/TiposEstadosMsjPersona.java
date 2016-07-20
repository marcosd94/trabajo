package enums;

public enum TiposEstadosMsjPersona {
	NUEVO("NUEVO", "NUEVO"), 
	NO_EXISTE("NO EXISTE", "NO EXISTE"), 
	EXISTE("EXISTE", "EXISTE"), 
	SERVIDOR_OCUPADO("SERVIDOR OCUPADO", "SERVIDOR OCUPADO");

	private String id;
	private String descripcion;

	private TiposEstadosMsjPersona(String id, String descripcion) {
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

	public static TiposEstadosMsjPersona getEstadoPorId(String id) {
		for (TiposEstadosMsjPersona tc : TiposEstadosMsjPersona.values()) {
			if (id == tc.getId()) {
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
