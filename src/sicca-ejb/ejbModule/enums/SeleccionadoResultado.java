package enums;

public enum SeleccionadoResultado {
	SELECCIONADO("S", "Seleccionado"), 
	NO_SELECCIONADO("N", "No Seleccionado"),
	LISTA_ESPERA("L", "Lista de Espera");

	private String id;
	private String descripcion;
	
	
	
	private SeleccionadoResultado(String id, String descripcion) {
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
	
	public static SeleccionadoResultado getSeleccionadoResultadoId(String id) {
		for (SeleccionadoResultado tc : SeleccionadoResultado.values()) {
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
