package py.com.excelsis.sicca.session;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.util.SICCASessionParameters;


import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("barrioList")
public class BarrioList extends EntityQuery<Barrio> {

	private static final String EJBQL = "select barrio from Barrio barrio";

	private static final String[] RESTRICTIONS = {
			"lower(barrio.descripcion) like concat('%',lower(concat(#{barrioList.barrio.descripcion},'%')))",
			"lower(barrio.usuAlta) like lower(concat(#{barrioList.barrio.usuAlta},'%'))",
			"lower(barrio.usuMod) like lower(concat(#{barrioList.barrio.usuMod},'%'))",
			"barrio.ciudad.departamento.idDepartamento = #{barrioList.idDepartamento} ",
			"barrio.ciudad.departamento.pais.idPais =#{barrioList.idPais} ",
			"barrio.ciudad.idCiudad = #{barrioList.idCiudad} ",
			"barrio.activo=#{barrioList.estado.valor}",
			};

	private Barrio barrio = new Barrio();
	private Estado estado= Estado.ACTIVO;
	private Long idDepartamento=null;
	private Long idCiudad=null;
	private Long idPais=null;
	private static final String ORDER = "barrio.descripcion";

	

	public BarrioList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public Barrio getBarrio() {
		return barrio;
	}
	
		
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Barrio> listaResultados() {
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
	public List<Barrio> limpiar() {
		barrio = new Barrio();
		estado = Estado.ACTIVO;
		idDepartamento=null;
		idCiudad=null;
		idPais=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public List<Barrio> listaPorCiudad() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(null);
		return getResultList();
	}

	
//	GETTERS Y SETTERS
	
	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdDepartamento() {
		return idDepartamento;
	}

	public void setIdDepartamento(Long idDepartamento) {
		this.idDepartamento = idDepartamento;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}


	
	
}
