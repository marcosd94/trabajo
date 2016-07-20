package enums;

import py.com.excelsis.sicca.util.SICCAAppHelper;

public enum HorasAnios {
	Curso(4,"Curso","C"),
	Semestre(3,"Semestre","S"),//MODIFICADO RV
	Anios(2,SICCAAppHelper.getBundleMessage("SinNivelEntidad_aniAniopre")+"/s", "A"),
	Meses(5,"Meses","M"),
	Dias(6,"Días","D"),	
	Horas(1, "Horas", "H"), 
	Modulo(7, "Módulo", "X")
	;
	
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private HorasAnios(Integer id, String descripcion, String valor) {
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
	
	public static HorasAnios getHorasAniosPorId(String valor) {
		for (HorasAnios tr : HorasAnios.values()) {
			if (valor.equals(tr.getValor())) {
				return tr;
			}
		}
		return null;
	}
	
	
}
