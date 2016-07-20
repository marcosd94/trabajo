package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.TransicionEstado;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("transicionEstadoList")
public class TransicionEstadoList extends EntityQuery<TransicionEstado> {

	private static final String EJBQL = "select transicionEstado from TransicionEstado transicionEstado";

	private static final String[] RESTRICTIONS = {
			"transicionEstado.estadoCabByIdEstadoCab.idEstadoCab =#{transicionEstadoList.idEstadoCab} ",
			"transicionEstado.estadoCabByPosibleEstado.idEstadoCab =#{transicionEstadoList.idEstadoCabPosible} ",
			};

	private TransicionEstado transicionEstado = new TransicionEstado();
	private static final String ORDER = "transicionEstado.estadoCabByIdEstadoCab.descripcion";
	private Long idEstadoCab;
	private Long idEstadoCabPosible;
	
	public TransicionEstadoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<TransicionEstado> listaResultados() {
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
	public List<TransicionEstado> limpiar() {
		transicionEstado= new TransicionEstado();
		idEstadoCab=null;
		idEstadoCabPosible=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

//	GETTERS Y SETTERS
	public TransicionEstado getTransicionEstado() {
		return transicionEstado;
	}
	public Long getIdEstadoCab() {
		return idEstadoCab;
	}
	public void setIdEstadoCab(Long idEstadoCab) {
		this.idEstadoCab = idEstadoCab;
	}
	public Long getIdEstadoCabPosible() {
		return idEstadoCabPosible;
	}
	public void setIdEstadoCabPosible(Long idEstadoCabPosible) {
		this.idEstadoCabPosible = idEstadoCabPosible;
	}

	
}
