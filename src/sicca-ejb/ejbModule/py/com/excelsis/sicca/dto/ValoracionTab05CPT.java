package py.com.excelsis.sicca.dto;

import java.util.List;

import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.EscalaCondTrabEspecif;

public class ValoracionTab05CPT {

	CondicionTrabajoEspecif condicionTrabajoEspecif;
	List<EscalaCondTrabEspecif> listaEscala;
	
	
	
	
	public ValoracionTab05CPT() {
		
	}
	public CondicionTrabajoEspecif getCondicionTrabajoEspecif() {
		return condicionTrabajoEspecif;
	}
	public void setCondicionTrabajoEspecif(
			CondicionTrabajoEspecif condicionTrabajoEspecif) {
		this.condicionTrabajoEspecif = condicionTrabajoEspecif;
	}
	public List<EscalaCondTrabEspecif> getListaEscala() {
		return listaEscala;
	}
	public void setListaEscala(List<EscalaCondTrabEspecif> listaEscala) {
		this.listaEscala = listaEscala;
	}
	
	
}
