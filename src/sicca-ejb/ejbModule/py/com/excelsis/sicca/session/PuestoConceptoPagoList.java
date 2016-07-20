package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.PuestoConceptoPago;

@Name("puestoConceptoPagoList")
public class PuestoConceptoPagoList extends EntityQuery<PuestoConceptoPago> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<PuestoConceptoPago> listaPuestoConceptoPagos;
	
	private static final String EJBQL = "select puestoConceptoPago from PuestoConceptoPago puestoConceptoPago";

	private static final String[] RESTRICTIONS = {
			"lower(puestoConceptoPago.usuAlta) like lower(concat(#{puestoConceptoPagoList.puestoConceptoPago.usuAlta},'%'))",
			"lower(puestoConceptoPago.usuMod) like lower(concat(#{puestoConceptoPagoList.puestoConceptoPago.usuMod},'%'))",
};

	private PuestoConceptoPago puestoConceptoPago = new PuestoConceptoPago();

	public PuestoConceptoPagoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public PuestoConceptoPago getPuestoConceptoPago() {
		return puestoConceptoPago;
	}
	
	public List<PuestoConceptoPago> obtenerListaPuestoConceptoPagos(){
    	listaPuestoConceptoPagos = getResultList(); 
    	return listaPuestoConceptoPagos;
    }
	
    public List<PuestoConceptoPago> getListaPuestoConceptoPagos() {
		return listaPuestoConceptoPagos;
	}
}
