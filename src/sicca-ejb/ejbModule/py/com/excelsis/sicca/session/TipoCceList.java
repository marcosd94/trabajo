package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.TipoCce;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("tipoCceList")
public class TipoCceList extends EntityQuery<TipoCce> {

	private static final String EJBQL = "select tipoCce from TipoCce tipoCce";

	private static final String[] RESTRICTIONS = {
		"lower(tipoCce.descripcion) like lower(concat('%', concat(#{tipoCceList.tipoCce.descripcion},'%')))",
			"tipoCce.activo = #{tipoCceList.estado.valor}",

	};
	private static final String ORDER = "tipoCce.descripcion";
	
	private Estado estado = Estado.ACTIVO;
	
	private TipoCce tipoCce = new TipoCce();

	public TipoCceList() {
		
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
	}
	
	public List<TipoCce> buscarResultados(){
	
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}

	public List<TipoCce> limpiarResultados(){
		tipoCce = new TipoCce();
		estado = Estado.ACTIVO;
		tipoCce.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}

	public TipoCce getTipoCce() {
		return tipoCce;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	
}
