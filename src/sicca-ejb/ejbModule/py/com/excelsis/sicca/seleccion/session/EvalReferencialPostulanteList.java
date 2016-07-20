package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import java.util.Arrays;
import java.util.List;

@Name("evalReferencialPostulanteList")
public class EvalReferencialPostulanteList extends
		EntityQuery<EvalReferencialPostulante> {

	private static final String EJBQL = "select evalReferencialPostulante from EvalReferencialPostulante evalReferencialPostulante";

	private static final String[] RESTRICTIONS = {
			"evalReferencialPostulante.activo = #{evalReferencialPostulanteList.evalReferencialPostulante.activo}",
			"lower(evalReferencialPostulante.usuAlta) like lower(concat(#{evalReferencialPostulanteList.evalReferencialPostulante.usuAlta},'%'))",
			"lower(evalReferencialPostulante.usuMod) like lower(concat(#{evalReferencialPostulanteList.evalReferencialPostulante.usuMod},'%'))",
			"lower(evalReferencialPostulante.obsElegible) like lower(concat(#{evalReferencialPostulanteList.evalReferencialPostulante.obsElegible},'%'))",
			"lower(evalReferencialPostulante.obsEmpate) like lower(concat(#{evalReferencialPostulanteList.evalReferencialPostulante.obsEmpate},'%'))", };
	

	private static final String[] RESTRICTIONS_CU148 = {
			"evalReferencialPostulante.activo = #{evalReferencialPostulanteList.evalReferencialPostulante.activo}",
			"evalReferencialPostulante.concursoPuestoAgr.idConcursoPuestoAgr = #{evalReferencialPostulanteList.evalReferencialPostulante.concursoPuestoAgr.idConcursoPuestoAgr}",
			"evalReferencialPostulante.listaCortaReplica = #{evalReferencialPostulanteList.evalReferencialPostulante.listaCortaReplica}", 
			"evalReferencialPostulante.postulacion.activo = #{evalReferencialPostulanteList.evalReferencialPostulante.activo}", };

	private static final String ORDER = "evalReferencialPostulante.puntajeRealizado desc";
	private EvalReferencialPostulante evalReferencialPostulante = new EvalReferencialPostulante();

	public EvalReferencialPostulanteList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
		setOrder(ORDER);
	}

	public EvalReferencialPostulante getEvalReferencialPostulante() {
		return evalReferencialPostulante;
	}
	
	public List<EvalReferencialPostulante> getListaCompleta(){
		setMaxResults(null);
		setOrder(ORDER);
		return getResultList();
	}
	
	public List<EvalReferencialPostulante> getListaCU148(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU148));
		setOrder(ORDER);
		return getListaCompleta();
	}
}
