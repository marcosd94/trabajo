package enums;

public enum ModalidadOcupacion {
	NINGUNO(null, "Seleccionar...", null),
	CONTRATADO(1, "CONTRATADO", "CONTRATADO"), 
	PERMANENTE(2, "PERMANENTE", "PERMANENTE");
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private ModalidadOcupacion(Integer id, String descripcion, String valor) {
		
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
	
	public static ModalidadOcupacion getModalidadPorId(Integer id) {
		for (ModalidadOcupacion mod : ModalidadOcupacion.values()) {
			if (id == mod.getId()) {
				return mod;
			}
		}
		return null;
	}
	
	
}
