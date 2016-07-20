package enums;



public enum Modulo {
	NINGUNO(null, "NINGUNO", null),
	PLANIFICACION(1, "PLANIFICACION", 1), 
	SELECCION(2, "SELECCION", 2), 
	MOVILIDAD(3, "MOVILIDAD", 3),
	EVALUACION(4, "EVALUACION", 4),
	CAPACITACION(5, "CAPACITACION", 5),
	LEGAJO(6, "LEGAJO", 6),
	REMUNERACION(7, "REMUNERACION", 7),
	PROCESOS(8, "PROCESOS JURIDICOS", 8),
	DESVINCULACION(9, "DESVINCULACION", 9),
	SEGURIDAD(10, "SEGURIDAD", 10),
	PORTAL(11, "PORTAL", 11),
	IGP(12, "IGP", 12)
	;
	
	private Integer id;
	private String descripcion;
	private Integer valor;
	
	
	private Modulo(Integer id, String descripcion, Integer valor) {
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
	public Integer getValor() {
		return valor;
	}
	public void setValor(Integer valor) {
		this.valor = valor;
	}
	
	public static Modulo getModuloPorId(Integer id) {
		for (Modulo mod : Modulo.values()) {
			if (id == mod.getId()) {
				return mod;
			}
		}
		return null;
	}
	
	
}
