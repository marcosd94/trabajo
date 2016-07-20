package enums;

public enum EvalsReferenciales {
	CON_DET(1, "Con Detalle de Evaluación"), SIN_DET(2, "Sin Detalle de Evaluación"),
	CON_MATRIZ(3, "En forma de matriz");

	private Integer id;
	private String descripcion;

	private EvalsReferenciales(Integer id, String descripcion) {
		this.id = id;
		this.descripcion = descripcion;		 
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
	public static EvalsReferenciales getEvalsReferencialesPorId(Integer id) {
		for (EvalsReferenciales est : EvalsReferenciales.values()) {
			if (id == est.getId()) {
				return est;
			}
		}
		return null;
	}

}
