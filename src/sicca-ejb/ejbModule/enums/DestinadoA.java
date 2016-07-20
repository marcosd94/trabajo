package enums;

public enum DestinadoA {
	CERRADO("C", "Cerrado"), 
	ABIERTO_PUBLICO("A", "Abiertos a todos los funcionarios públicos"),
	ABIERTO_CIUDADANIA("F", "Abierto a la ciudadanía");

	private String id;
	private String descripcion;
	
	
	
	private DestinadoA(String id, String descripcion) {
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
	
	public static DestinadoA getTipoPorId(String id) {
		for (DestinadoA tc : DestinadoA.values()) {
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
