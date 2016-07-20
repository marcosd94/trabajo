package enums;

public enum UnidadPlazo {

	NINGUNO(null, "Seleccionar...", null),
	DIA(1, "DIA", "D"), 
	SEMANA(2, "SEMANA", "S"),
	MES(3, "MES", "M");
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private UnidadPlazo(Integer id, String descripcion, String valor) {
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
	
	public static UnidadPlazo getById(String id) {
		for (UnidadPlazo o : UnidadPlazo.values()) {
			if (id.equals("" + o.getId())) {
				return o;
			}
		}
		return null;
	}
	
	public static UnidadPlazo getPorValor(String valor) {
		for (UnidadPlazo o : UnidadPlazo.values()) {
			if (valor.equals("" + o.getValor())) {
				return o;
			}
		}
		return null;
	}
}
