package enums;

public enum TipoCriterio {
	TIPO_MATRIZ("tipoMatriz", "TIPO MATRIZ"), 
	FACTOR_EVALUACION("factorEvaluacion", "FACTOR EVALUACION");

	private String id;
	private String descripcion;

	private TipoCriterio(String id, String descripcion) {
		this.id = id;
		this.descripcion = descripcion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public static TipoCriterio getEstadoPorId(String id) {
		for (TipoCriterio tc : TipoCriterio.values()) {
			if (id == tc.getId()) {
				return tc;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return descripcion;
	}

	
}
