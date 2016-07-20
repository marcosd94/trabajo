package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.HistorialActividadesGrupo;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;

@Name("historialActividadesGrupoList")
public class HistorialActividadesGrupoList extends
		EntityQuery<HistorialActividadesGrupo> {

	private static final String EJBQL = "select historialActividadesGrupo from HistorialActividadesGrupo historialActividadesGrupo";

	private static final String[] RESTRICTIONS = {
			"historialActividadesGrupo.concursoPuestoAgr.idConcursoPuestoAgr = #{historialActividadesGrupoList.idConcursoPuestoAgr}"};

	private static final String ORDER = "historialActividadesGrupo.fechaCreacion";
	
	private HistorialActividadesGrupo historialActividadesGrupo;
	private Long idConcursoPuestoAgr;

	public HistorialActividadesGrupoList() {
		historialActividadesGrupo = new HistorialActividadesGrupo();
		setEjbql(EJBQL);
		setOrder(ORDER);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public HistorialActividadesGrupo getHistorialActividadesGrupo() {
		return historialActividadesGrupo;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}
}
