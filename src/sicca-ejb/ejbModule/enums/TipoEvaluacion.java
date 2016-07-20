package enums;

public enum TipoEvaluacion {
	COMISION("comision", "COMISION"), 
	EMPRESA("empresa", "EMPRESA TERCERIZADA");

	private String id;
	private String descripcion;
	
	
	
	private TipoEvaluacion(String id, String descripcion) {
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
	
	public static TipoEvaluacion getTipoPorId(String id) {
		for (TipoEvaluacion tc : TipoEvaluacion.values()) {
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
