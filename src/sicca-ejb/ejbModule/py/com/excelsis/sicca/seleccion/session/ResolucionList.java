package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("resolucionHomologacionList")
public class ResolucionList extends
		EntityQuery<Resolucion> {

	private static final String EJBQL = "select resolucionHomologacion from Resolucion resolucionHomologacion";

	private static final String[] RESTRICTIONS = {
			"lower(resolucionHomologacion.titulo) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.titulo},'%'))",
			"lower(resolucionHomologacion.lugar) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.lugar},'%'))",
			"lower(resolucionHomologacion.fecha) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.fecha},'%'))",
			"lower(resolucionHomologacion.visto) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.visto},'%'))",
			"lower(resolucionHomologacion.considerando) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.considerando},'%'))",
			"lower(resolucionHomologacion.porTanto) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.porTanto},'%'))",
			"lower(resolucionHomologacion.resuelve) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.resuelve},'%'))",
			"lower(resolucionHomologacion.firma) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.firma},'%'))",
			"lower(resolucionHomologacion.cargo) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.cargo},'%'))",
			"lower(resolucionHomologacion.institucion) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.institucion},'%'))",
			"lower(resolucionHomologacion.usuAlta) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.usuAlta},'%'))",
			"lower(resolucionHomologacion.usuMod) like lower(concat(#{resolucionHomologacionList.resolucionHomologacion.usuMod},'%'))", };

	private Resolucion resolucionHomologacion = new Resolucion();
	private Estado estado= Estado.ACTIVO;

	public ResolucionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public Resolucion getResolucionHomologacion() {
		return resolucionHomologacion;
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Resolucion> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
	
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<Resolucion> limpiar() {
		resolucionHomologacion= new Resolucion();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
	
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	
	
}
