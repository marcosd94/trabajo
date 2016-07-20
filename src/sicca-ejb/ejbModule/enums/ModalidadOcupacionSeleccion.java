package enums;

public enum ModalidadOcupacionSeleccion {
	NINGUNO(null, "Seleccionar...", null),
	NOMBRADO(2, "NOMBRADO", "P"),
	CONTRATADO(1, "CONTRATADO", "C");
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private ModalidadOcupacionSeleccion(Integer id, String descripcion, String valor) {
		
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
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public static ModalidadOcupacionSeleccion getModalidadPorId(Integer id) {
		for (ModalidadOcupacionSeleccion mod : ModalidadOcupacionSeleccion.values()) {
			if (id == mod.getId()) {
				return mod;
			}
		}
		return null;
	}
	
	
}
