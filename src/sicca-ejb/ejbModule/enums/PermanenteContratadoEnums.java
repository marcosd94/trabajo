package enums;

public enum PermanenteContratadoEnums {
	TODOS(1, "Todos",null), 
	PERMANENTE(2, "Permanente", false),
	CONTRATADO(2, "Contratado", true);

	private Integer id;
	private String descripcion;
	private Boolean valor;

	private PermanenteContratadoEnums(Integer id, String descripcion, Boolean valor) {
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

	public Boolean getValor() {
		return valor;
	}

	public void setValor(Boolean valor) {
		this.valor = valor;
	}

	public static PermanenteContratadoEnums getPermanenteContratadoEnumsPorId(Integer id) {
		for (PermanenteContratadoEnums func : PermanenteContratadoEnums.values()) {
			if (id == func.getId()) {
				return func;
			}
		}
		return null;
	}
	
	public static PermanenteContratadoEnums getPermanenteContratadoEnumsPorValor(Boolean valor) {
		for (PermanenteContratadoEnums func : PermanenteContratadoEnums.values()) {
			if (valor == func.getValor()) {
				return func;
			}
		}
		return null;
	}
}
