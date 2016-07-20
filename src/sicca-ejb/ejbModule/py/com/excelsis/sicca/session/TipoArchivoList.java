package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("tipoArchivoList")
public class TipoArchivoList extends EntityQuery<TipoArchivo> {

	private static final String EJBQL = "select tipoArchivo from TipoArchivo tipoArchivo";

	private static final String[] RESTRICTIONS = {
			"lower(tipoArchivo.descripcion) like concat('%',lower(concat(#{tipoArchivoList.tipoArchivo.descripcion},'%')))",
			"lower(tipoArchivo.extension) like lower(concat(#{tipoArchivoList.tipoArchivo.extension},'%'))",
			"lower(tipoArchivo.unidMedida) like lower(concat(#{tipoArchivoList.tipoArchivo.unidMedida},'%'))",
			"lower(tipoArchivo.mimetype) like lower(concat(#{tipoArchivoList.tipoArchivo.mimetype},'%'))",
			"lower(tipoArchivo.usuAlta) like lower(concat(#{tipoArchivoList.tipoArchivo.usuAlta},'%'))",
			"lower(tipoArchivo.usuMod) like lower(concat(#{tipoArchivoList.tipoArchivo.usuMod},'%'))",
			"tipoArchivo.activo=#{tipoArchivoList.estado.valor}",
	};
	

	private TipoArchivo tipoArchivo = new TipoArchivo();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "tipoArchivo.descripcion";
	public TipoArchivoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}


	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<TipoArchivo> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<TipoArchivo> limpiar() {
		tipoArchivo= new TipoArchivo();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public TipoArchivo getTipoArchivo() {
		return tipoArchivo;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
}
