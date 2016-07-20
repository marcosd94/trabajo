package enums;

import py.com.excelsis.sicca.util.SICCAAppHelper;

public enum ContenidoFuncionalEnum {
	Seleccione(1,"Seleccione..", null),
	Puesto(2,"Puesto", "P"),
	CPT(3, SICCAAppHelper.getBundleMessage("CU559_cpt_especifico"), "C") ;
	
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private ContenidoFuncionalEnum(Integer id, String descripcion, String valor) {
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
	
	public static ContenidoFuncionalEnum getContenidoFuncionalEnumPorId(String valor) {
		for (ContenidoFuncionalEnum tr : ContenidoFuncionalEnum.values()) {
			if (valor.equals(tr.getValor())) {
				return tr;
			}
		}
		return null;
	}
	
	
}
