package py.com.excelsis.sicca.evaluacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.entity.PlantillaEvalConfDet;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("plantillaEvalConfDetList")
public class PlantillaEvalConfDetList extends EntityQuery<PlantillaEvalConfDet> {

	private static final String EJBQL = "select plantillaEvalConfDet from PlantillaEvalConfDet plantillaEvalConfDet";

	private static final String[] RESTRICTIONS = {
			"lower(plantillaEvalConfDet.resultadoEsperado) like concat('%',lower(concat(#{plantillaEvalConfDetList.plantillaEvalConfDet.resultadoEsperado},'%')))",
			"plantillaEvalConfDet.grupoMetodologia.idGrupoMetodologia =#{plantillaEvalConfDetList.idGrupoMetodologia}",
			"plantillaEvalConfDet.datosEspecificoByTipoVar.idDatosEspecificos =#{plantillaEvalConfDetList.idtipo}",
			"plantillaEvalConfDet.activo =#{plantillaEvalConfDetList.plantillaEvalConfDet.activo} ",
			"lower(plantillaEvalConfDet.usuMod) like lower(concat(#{plantillaEvalConfDetList.plantillaEvalConfDet.usuMod},'%'))", };

	private PlantillaEvalConfDet plantillaEvalConfDet = new PlantillaEvalConfDet();
	private static final String ORDER = "plantillaEvalConfDet.orden";
	private Long idtipo=null;
	private Long idGrupoMetodologia=null;

	public PlantillaEvalConfDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	public List<PlantillaEvalConfDet> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public PlantillaEvalConfDet getPlantillaEvalConfDet() {
		return plantillaEvalConfDet;
	}

	public Long getIdtipo() {
		return idtipo;
	}

	public void setIdtipo(Long idtipo) {
		this.idtipo = idtipo;
	}

	public Long getIdGrupoMetodologia() {
		return idGrupoMetodologia;
	}

	public void setIdGrupoMetodologia(Long idGrupoMetodologia) {
		this.idGrupoMetodologia = idGrupoMetodologia;
	}
	
	
}
