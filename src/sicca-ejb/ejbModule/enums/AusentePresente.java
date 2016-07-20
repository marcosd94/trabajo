package enums;

public enum AusentePresente {
	 PRESENTE(2, "Presente", true),AUSENTE(1, "Ausente", false);

	private Integer id;
	private String descripcion;
	private Boolean valor;

	private AusentePresente(Integer id, String descripcion, Boolean valor) {
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

	public static AusentePresente getAusentePresentePorId(Integer id) {
		for (AusentePresente est : AusentePresente.values()) {
			if (id == est.getId()) {
				return est;
			}
		}
		return null;
	}
	
	public static AusentePresente getAusentePresentePorId(Boolean valor) {
		for (AusentePresente est : AusentePresente.values()) {
			if (valor == est.getValor()) {
				return est;
			}
		}
		return null;
	}
}
