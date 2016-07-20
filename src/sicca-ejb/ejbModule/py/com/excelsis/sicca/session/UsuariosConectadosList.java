package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.UsuariosConectados;

import java.util.Arrays;

@Name("usuariosConectadosList")
public class UsuariosConectadosList extends EntityQuery<UsuariosConectados> {

	private static final String EJBQL = "select usuariosConectados from UsuariosConectados usuariosConectados";

	private static final String[] RESTRICTIONS = {
			"lower(usuariosConectados.navegador) like lower(concat(#{usuariosConectadosList.usuariosConectados.navegador},'%'))",
			"lower(usuariosConectados.direccionIp) like lower(concat(#{usuariosConectadosList.usuariosConectados.direccionIp},'%'))", };

	private UsuariosConectados usuariosConectados = new UsuariosConectados();

	public UsuariosConectadosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public UsuariosConectados getUsuariosConectados() {
		return usuariosConectados;
	}
}
