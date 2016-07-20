package py.com.excelsis.sicca.dto;

import java.util.List;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.EscalaCondSegur;

public class ValoracionTab06CPT {

	private CondicionSegur condicionSegur;
	private List<EscalaCondSegur> listaEscalaCondSeg;
	
	
	
	public ValoracionTab06CPT() {
		
	}
	public CondicionSegur getCondicionSegur() {
		return condicionSegur;
	}
	public void setCondicionSegur(CondicionSegur condicionSegur) {
		this.condicionSegur = condicionSegur;
	}
	public List<EscalaCondSegur> getListaEscalaCondSeg() {
		return listaEscalaCondSeg;
	}
	public void setListaEscalaCondSeg(List<EscalaCondSegur> listaEscalaCondSeg) {
		this.listaEscalaCondSeg = listaEscalaCondSeg;
	}
	
	
}
