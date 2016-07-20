package enums;


public enum ProcesoEnum {

	CONCURSO(1, "CONCURSO", "CONCURSO"),	
	EXCEPCION(2, "EXCEPCION", "EXCEPCION"),
	CAPACITACION(3, "CAPACITACION", "CAPACITACION"),
	EVALUACION(4, "EVALUACION", "EVALUACION_DESEMPENO"),
	MOVILIDAD(5, "TRASLADO SICCA", "TRASLADO SICCA");
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private ProcesoEnum(Integer id, String descripcion, String valor) {
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
	
	public static ProcesoEnum getById(String id) {
		for (ProcesoEnum o : ProcesoEnum.values()) {
			if (id.equals("" + o.getId())) {
				return o;
			}
		}
		return null;
	}
	
	public static ProcesoEnum getPorValor(String valor) {
		for (ProcesoEnum o : ProcesoEnum.values()) {
			if (valor.equals("" + o.getValor())) {
				return o;
			}
		}
		return null;
	}
}
