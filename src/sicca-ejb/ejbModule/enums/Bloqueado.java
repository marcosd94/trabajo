package enums;


public enum Bloqueado {
	TODOS(null, "TODOS", null),
	SI(1, "SI", "SI"), 
	NO(2, "NO", "NO");
	
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private Bloqueado(Integer id, String descripcion, String valor) {
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
	
	public static Bloqueado getBloqueadoPorId(String valor) {
		for (Bloqueado tr : Bloqueado.values()) {
			if (valor.equals(tr.getValor())) {
				return tr;
			}
		}
		return null;
	}
}
