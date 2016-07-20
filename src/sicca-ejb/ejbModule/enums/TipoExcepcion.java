package enums;

public enum TipoExcepcion {
	SELECCIONE(null, "Seleccionar...", null), 
	         INCLUSION_MIEMBRO_CS(1, "INCLUSION MIEMBRO CS","INCLUSION_MIEMBRO_CS"), 
			 REEMPLAZO_MIEMBRO_CS(2, "REEMPLAZO MIEMBRO CS","REEMPLAZO_MIEMBRO_CS"), 
			 EXCLUSION_MIEMBRO_CS(3, "EXCLUSION MIEMBRO CS","EXCLUSION_MIEMBRO_CS"),
	         /* PRORROGA_RECEP_POSTULACION(4, "PRORROGA RECEP. POSTULACION","PRORROGA_RECEP_POSTULACION"), */
	         PRORROGA_PLAZO_TOTAL_CONCURSO_GRUPO(5, "PRORROGA PLAZO TOTAL CONCURSO/GRUPO","PRORROGA_PLAZO_TOTAL_CONCURSO_GRUPO"), 
	         BLOQUEO_CONCURSO(6, "BLOQUEO POR CONCURSO","BLOQUEO_CONCURSO"), 
	         BLOQUEO_GRUPO(7, "BLOQUEO POR GRUPO DE PUESTOS", "BLOQUEO_GRUPO"), 
	         BLOQUEO_PUESTO(8, "BLOQUEO POR PUESTOS", "BLOQUEO_PUESTOS"), 
	         CANCELACION_CONCURSO(9,"CANCELACION DE CONCURSO POR SFP", "CANCELACION_CONCURSO"), 
	         EXCLUSION_POSTULANTES(10,"EXCLUSION DE POSTULANTES", "EXCLUSION_POSTULANTES"), 
	         CANCELACION_AUTOMATICA(11,"CANCELACION AUTOMATICA", "CANCELACION_AUTOMATICA"), 
	         EXCLUSION_POSTULANTES_POR_OEE(12,"EXCLUSION DE POSTULANTES POR OEE", "EXCLUSION_POSTULANTES_POR_OEE"),
	         
	         DESBLOQUEO_VACANCIAS_PLANIFICACION(13, "DESBLOQUEO DE VACANCIAS - PLANIFICACION", "DESBLOQUEO_VACANCIAS_PLANIFICACION")
	         ,DESBLOQUEO_POR_CONCURSO(14, "DESBLOQUEO POR CONCURSO", "DESBLOQUEO_POR_CONCURSO")
	         ,DESBLOQUEO_POR_GRUPO(15, "DESBLOQUEO POR GRUPO", "DESBLOQUEO_POR_GRUPO")
	         ,DESBLOQUEO_POR_PUESTO(16, "DESBLOQUEO POR PUESTO", "DESBLOQUEO_POR_PUESTO")
	         ,INGRESO_POR_CONCURSO_CPO(17, "INGRESO POR CONCURSO CPO", "INGRESO_POR_CONCURSO_CPO"),
	         INGRESO_POR_CONCURSO_MERITOS(18, "INGRESO POR CONCURSO MERITOS", "INGRESO_POR_CONCURSO_MERITOS"),
             INGRESO_POR_CONCURSO_SIMPLIFICADO(19, "INGRESO POR CONCURSO SIMPLIFICADO", "INGRESO_POR_CONCURSO_SIMPLIFICADO"),
             INGRESO_POR_CONCURSO_INSTITUCIONAL(20, "INGRESO POR CONCURSO INSTITUCIONAL", "INGRESO_POR_CONCURSO_INSTITUCIONAL"),
             INGRESO_POR_CONCURSO_INTERINSTITUCIONAL(21, "INGRESO POR CONCURSO INTERINSTITUCIONAL", "INGRESO_POR_CONCURSO_INTERINSTITUCIONAL");

	private Integer id;
	private String descripcion;
	private String valor;

	private TipoExcepcion(Integer id, String descripcion, String valor) {

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

	public static TipoExcepcion getTipoExcepcionId(Integer id) {
		for (TipoExcepcion mod : TipoExcepcion.values()) {
			if (id == mod.getId()) {
				return mod;
			}
		}
		return null;
	}

	public static TipoExcepcion findTipoExcepcion(String descripcion) {
		for (TipoExcepcion mod : TipoExcepcion.values()) {
			if (descripcion.equalsIgnoreCase(mod.getDescripcion())) {
				return mod;
			}
		}
		return null;
	}

}
