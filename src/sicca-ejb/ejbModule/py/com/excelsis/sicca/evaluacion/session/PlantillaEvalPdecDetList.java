package py.com.excelsis.sicca.evaluacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.PlantillaEvalPdecDet;

import java.util.Arrays;

@Name("plantillaEvalPdecDetList")
public class PlantillaEvalPdecDetList extends EntityQuery<PlantillaEvalPdecDet> {

	private static final String EJBQL = "select plantillaEvalPdecDet from PlantillaEvalPdecDet plantillaEvalPdecDet";

	private static final String[] RESTRICTIONS = { "lower(plantillaEvalPdecDet.descripcion) like lower(concat(#{plantillaEvalPdecDetList.plantillaEvalPdecDet.descripcion},'%'))", };

	private PlantillaEvalPdecDet plantillaEvalPdecDet = new PlantillaEvalPdecDet();

	public PlantillaEvalPdecDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PlantillaEvalPdecDet getPlantillaEvalPdecDet() {
		return plantillaEvalPdecDet;
	}
}
