package enums;

public enum TipoReclamo {

	NINGUNO(null, "Seleccionar...", null),
	/*FELICITACION(1, "FELICITACION", "F"), */
	RECLAMO(2, "RECLAMO", "R"),
	SUGERENCIA(3, "SUGERENCIA", "S");
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private TipoReclamo(Integer id, String descripcion, String valor) {
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
	
	public static TipoReclamo getTipoReclamoPorId(String valor) {
		for (TipoReclamo tr : TipoReclamo.values()) {
			if (valor.equals(tr.getValor())) {
				return tr;
			}
		}
		return null;
	}
	
	
}
