package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Vencimientos;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("vencimientoList")
public class VencimientoList extends EntityQuery<Vencimientos> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Vencimientos> listaVencimientos;
	
	private static final String EJBQL = "select vencimiento from Vencimientos vencimiento";

	private static final String[] RESTRICTIONS = {
			"vencimiento.anho = #{vencimientoList.anho}",
			"vencimiento.mes = #{vencimientoList.mes}",
			"vencimiento.activo = #{vencimientoList.activo}",
	};
	private static final String ORDER = "vencimiento.anho, vencimiento.mes";

	private Integer anho = null;
	private Integer mes = null;
	private Boolean activo = null;

	public VencimientoList() {
		activo = Estado.ACTIVO.getValor();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(12);
	}

	public List<Vencimientos> buscarResultados(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}

	public List<Vencimientos> limpiarResultados(){
		activo = Estado.ACTIVO.getValor();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}
	public List<Vencimientos> listarPorAnho(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(null);
		return getResultList(); 
	}
	
//	GETTERS Y SETTERS
	public Boolean getActivo() {
		return activo;
	}
	
	public void setActivo(Boolean activo){
		this.activo = activo;
	}
	
	public Integer getAnho() {
		return anho;
	}
	
	public void setAnho(Integer anho) {
		this.anho = anho;
	}
	
	public Integer getMes() {
		return mes;
	}
	
	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public List<Vencimientos> obtenerListaVencimientos(){
    	listaVencimientos = getResultList(); 
    	return listaVencimientos;
    }
	
    public List<Vencimientos> getListaVenciminetos() {
		return listaVencimientos;
	}
}
