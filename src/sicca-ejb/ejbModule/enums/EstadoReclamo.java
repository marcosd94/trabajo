package enums;

public enum EstadoReclamo {

	TODOS(null, "TODOS", null),
	APROBADO(1, "APROBADO", "APROBADO"), 
	PENDIENTE(2, "PENDIENTE APROBACION", "PENDIENTE APROBACION");
	
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private EstadoReclamo(Integer id, String descripcion, String valor) {
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
	
	public static EstadoReclamo getEstadoReclamoPorId(String valor) {
		for (EstadoReclamo tr : EstadoReclamo.values()) {
			if (valor.equals(tr.getValor())) {
				return tr;
			}
		}
		return null;
	}
	
	
}
