package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.List;

@Name("calendarioOeeCabList")
public class CalendarioOeeCabList extends EntityQuery<CalendarioOeeCab> {

	private static final String EJBQL = "select calendarioOeeCab from CalendarioOeeCab calendarioOeeCab";

	private static final String[] RESTRICTIONS = {
		"calendarioOeeCab.anho=#{calendarioOeeCabList.anho}",
		"calendarioOeeCab.configuracionUoCab.idConfiguracionUo=#{calendarioOeeCabList.id}",
	};

	@In(required = false)
	Usuario usuarioLogueado;
	private CalendarioOeeCab calendarioOeeCab = new CalendarioOeeCab();
	private static final String ORDER = "calendarioOeeCab.anho";
	private Integer anho;
	private Long id;

	public CalendarioOeeCabList() {
		
		
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CalendarioOeeCab> listaResultados() {
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
	public List<CalendarioOeeCab> limpiar() {
		calendarioOeeCab = new CalendarioOeeCab();
		anho = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}


	public CalendarioOeeCab getCalendarioOeeCab() {
		return calendarioOeeCab;
	}

	public Integer getAnho() {
		return anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	
	
}
