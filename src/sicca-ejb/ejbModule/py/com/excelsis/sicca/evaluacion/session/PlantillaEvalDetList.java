package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("plantillaEvalDetList")
public class PlantillaEvalDetList extends EntityQuery<PlantillaEvalDet> {

	private static final String EJBQL = "select plantillaEvalDet from PlantillaEvalDet plantillaEvalDet";

	private static final String[] RESTRICTIONS = {
			"lower(plantillaEvalDet.resultadoEsperado) like lower(concat(#{plantillaEvalDetList.plantillaEvalDet.resultadoEsperado},'%'))",
			"lower(plantillaEvalDet.actividades) like lower(concat(#{plantillaEvalDetList.plantillaEvalDet.actividades},'%'))",
			"lower(plantillaEvalDet.indicadoresCump) like lower(concat(#{plantillaEvalDetList.plantillaEvalDet.indicadoresCump},'%'))",
			"lower(plantillaEvalDet.usuAlta) like lower(concat(#{plantillaEvalDetList.plantillaEvalDet.usuAlta},'%'))",
			"lower(plantillaEvalDet.usuMod) like lower(concat(#{plantillaEvalDetList.plantillaEvalDet.usuMod},'%'))", };

	private PlantillaEvalDet plantillaEvalDet = new PlantillaEvalDet();

	public PlantillaEvalDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PlantillaEvalDet getPlantillaEvalDet() {
		return plantillaEvalDet;
	}
}
