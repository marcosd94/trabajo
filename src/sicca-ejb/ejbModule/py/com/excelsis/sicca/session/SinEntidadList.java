package py.com.excelsis.sicca.session;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.ScopeType;

import enums.Estado;

import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("sinEntidadList")
public class SinEntidadList extends EntityQuery<SinEntidad> {

	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<SinEntidad> listaSinEntidads;

	private static final String EJBQL = "select sinEntidad from SinEntidad sinEntidad";

	private static final String[] RESTRICTIONS =
		{
			"sinEntidad.entCodigo = #{sinEntidadList.sinEntidad.entCodigo}",
			"sinEntidad.nenCodigo = #{sinEntidadList.sinEntidad.nenCodigo}",
			"sinEntidad.aniAniopre = #{sinEntidadList.sinEntidad.aniAniopre}",
			"lower(sinEntidad.entNombre) like lower(concat('%', concat(#{sinEntidadList.sinEntidad.entNombre},'%')))",
			"lower(sinEntidad.entNomabre) like lower(concat(#{sinEntidadList.sinEntidad.entNomabre},'%'))",
			"lower(sinEntidad.entSigla) like lower(concat(#{sinEntidadList.sinEntidad.entSigla},'%'))",
			"lower(sinEntidad.entRuc) like lower(concat(#{sinEntidadList.sinEntidad.entRuc},'%'))", 
			"sinEntidad.activo = #{sinEntidadList.estado.valor}",};

	private static final String[] RESTRICTIONS_UC137 = {
		"sinEntidad.entCodigo = #{sinEntidadList.sinEntidad.entCodigo}",
		"sinEntidad.nenCodigo = #{sinEntidadList.sinEntidad.nenCodigo}",
		"sinEntidad.aniAniopre = #{sinEntidadList.sinEntidad.aniAniopre}",
		"sinEntidad.activo = #{sinEntidadList.estado.valor}", };

	// private static final String EJBQL_WITH_DET = "SELECT sinEntidad FROM SinEntidad sinEntidad, " +
	// " IN (sinEntidad.entidads) entidad";

	private SinEntidad sinEntidad = new SinEntidad();
	private Estado estado = Estado.ACTIVO;
	private static final String ORDER = "sinEntidad.entNombre";

	public SinEntidadList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public SinEntidad entidadMaxAnho() {
		setEjbql(EJBQL);
		setOrder("sinEntidad.aniAniopre DESC");
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(1);
		List<SinEntidad> listResult = getResultList();
		return listResult.isEmpty() ? null : listResult.get(0);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<SinEntidad> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<SinEntidad> limpiar() {
		sinEntidad = new SinEntidad();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<SinEntidad> listaResultadosUC137() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_UC137));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	// GETTERS Y SETTERS
	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public List<SinEntidad> obtenerListaSinEntidads() {
		listaSinEntidads = getResultList();
		return listaSinEntidads;
	}

	public List<SinEntidad> getListaSinEntidads() {
		return listaSinEntidads;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

}
