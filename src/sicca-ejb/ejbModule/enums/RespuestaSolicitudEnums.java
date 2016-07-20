package enums;



public enum RespuestaSolicitudEnums {
	Todos(1,"Seleccione…", null),
	Alta(2, "APROBADA", "APROBADA") ,
	Operativos(3, "DENEGADA", "DENEGADA")
	;
	
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private RespuestaSolicitudEnums(Integer id, String descripcion, String valor) {
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
	
	public static RespuestaSolicitudEnums getRespuestaSolicitudEnumsPorId(String valor) {
		for (RespuestaSolicitudEnums tr : RespuestaSolicitudEnums.values()) {
			if (valor.equals(tr.getValor())) {
				return tr;
			}
		}
		return null;
	}
	
	
}
