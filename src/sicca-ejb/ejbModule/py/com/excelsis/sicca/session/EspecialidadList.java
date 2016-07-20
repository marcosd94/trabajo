package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.Especialidad;

import java.util.Arrays;

@Name("especialidadList")
public class EspecialidadList extends EntityQuery<Especialidad> {

	private static final String EJBQL = "select especialidad from Especialidad especialidad";

	private static final String[] RESTRICTIONS = {
			"lower(especialidad.descripcion) like lower(concat(#{especialidadList.especialidad.descripcion},'%'))",
			"lower(especialidad.usuAlta) like lower(concat(#{especialidadList.especialidad.usuAlta},'%'))",
			"lower(especialidad.usuMod) like lower(concat(#{especialidadList.especialidad.usuMod},'%'))", };

	private Especialidad especialidad = new Especialidad();

	public EspecialidadList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Especialidad getEspecialidad() {
		return especialidad;
	}
}
