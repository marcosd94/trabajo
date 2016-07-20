package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.PresentAclaracDoc;

import java.util.Arrays;

@Name("presentAclaracDocList")
public class PresentAclaracDocList extends EntityQuery<PresentAclaracDoc> {

	private static final String EJBQL = "select presentAclaracDoc from PresentAclaracDoc presentAclaracDoc";

	private static final String[] RESTRICTIONS = {
			"lower(presentAclaracDoc.lugar) like lower(concat(#{presentAclaracDocList.presentAclaracDoc.lugar},'%'))",
			"lower(presentAclaracDoc.descripcion) like lower(concat(#{presentAclaracDocList.presentAclaracDoc.descripcion},'%'))",
			"lower(presentAclaracDoc.direccion) like lower(concat(#{presentAclaracDocList.presentAclaracDoc.direccion},'%'))",
			"lower(presentAclaracDoc.telefono) like lower(concat(#{presentAclaracDocList.presentAclaracDoc.telefono},'%'))",
			"lower(presentAclaracDoc.email) like lower(concat(#{presentAclaracDocList.presentAclaracDoc.email},'%'))",
			"lower(presentAclaracDoc.tipoPA) like lower(concat(#{presentAclaracDocList.presentAclaracDoc.tipoPA},'%'))",
			"lower(presentAclaracDoc.usuAlta) like lower(concat(#{presentAclaracDocList.presentAclaracDoc.usuAlta},'%'))",
			"lower(presentAclaracDoc.usuMod) like lower(concat(#{presentAclaracDocList.presentAclaracDoc.usuMod},'%'))", };

	private PresentAclaracDoc presentAclaracDoc = new PresentAclaracDoc();

	public PresentAclaracDocList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PresentAclaracDoc getPresentAclaracDoc() {
		return presentAclaracDoc;
	}
}
