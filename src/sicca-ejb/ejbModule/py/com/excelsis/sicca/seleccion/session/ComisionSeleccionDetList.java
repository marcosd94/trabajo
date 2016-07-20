package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.ComisionSeleccionDet;

import java.util.Arrays;

@Name("comisionSeleccionDetList")
public class ComisionSeleccionDetList extends EntityQuery<ComisionSeleccionDet> {

	private static final String EJBQL = "select comisionSeleccionDet from ComisionSeleccionDet comisionSeleccionDet";

	private static final String[] RESTRICTIONS = {
			"lower(comisionSeleccionDet.titularSuplente) like lower(concat(#{comisionSeleccionDetList.comisionSeleccionDet.titularSuplente},'%'))",
			"lower(comisionSeleccionDet.equipoTecnico) like lower(concat(#{comisionSeleccionDetList.comisionSeleccionDet.equipoTecnico},'%'))", };

	private ComisionSeleccionDet comisionSeleccionDet = new ComisionSeleccionDet();

	public ComisionSeleccionDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ComisionSeleccionDet getComisionSeleccionDet() {
		return comisionSeleccionDet;
	}
}
