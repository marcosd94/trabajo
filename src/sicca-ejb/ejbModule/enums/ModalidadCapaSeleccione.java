package enums;

public enum ModalidadCapaSeleccione {
	NINGUNO(null, "Todos", null),
	PRESENCIAL(1, "PRESENCIAL","P"), 
	SEMI_PRESENCIAL(2, "SEMIPRESENCIAL","S"),
	VIRTUAL(3, "VIRTUAL","V");
	
	
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private ModalidadCapaSeleccione(Integer id, String descripcion, String valor) {
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
	
	public static ModalidadCapaSeleccione getModalidadCapaSeleccioneId(String valor) {
		for (ModalidadCapaSeleccione tr : ModalidadCapaSeleccione.values()) {
			if (valor.equals(tr.getValor())) {
				return tr;
			}
		}
		return null;
	}
	
	
}
