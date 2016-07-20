package enums;


public enum EstadoJuez {
	TODOS(1, "TODOS", null),
	EN_CURSO(2, "EN CURSO", "EC"), 
	CON_RESOLUCION_JUEZ(3, "CON RESOLUCION DEL JUEZ", "RJ"), 
	SOBRESEIMIENTO(4, "SOBRESEIMIENTO", "SO"),
	ARCHIVO(5, "ARCHIVO", "AR"),
	SANCION(6, "SANCION", "SA");

	private Integer id;
	private String descripcion;
	private String valor;

	private EstadoJuez(Integer id, String descripcion, String valor) {
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

	public static EstadoJuez getEstadoPorId(Integer id) {
		for (EstadoJuez est : EstadoJuez.values()) {
			if (id == est.getId()) {
				return est;
			}
		}
		return null;
	}
	
	public static EstadoJuez getEstadoPorValor(String valor) {
		for (EstadoJuez est : EstadoJuez.values()) {
			if (valor == null)
				return EstadoJuez.TODOS;
				
			if (est.getValor() != null && est.getValor().equals(valor)) {
				return est;
			}
		}
		return null;
	}

	public boolean equals(EstadoJuez estadoSumario){
		if (estadoSumario != null){
			return this.getId().equals(estadoSumario.getId());
		}
		
		return false;
	}
}
