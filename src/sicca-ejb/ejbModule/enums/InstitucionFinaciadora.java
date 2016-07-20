package enums;

public enum InstitucionFinaciadora {
	OEE("oee", "Oee"), 
	OTRAS_INTITUCIONES("otras", "Otras Instituciones");

	private String id;
	private String descripcion;
	
	
	
	private InstitucionFinaciadora(String id, String descripcion) {
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
	
	public static InstitucionFinaciadora getTipoPorId(String id) {
		for (InstitucionFinaciadora tc : InstitucionFinaciadora.values()) {
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
