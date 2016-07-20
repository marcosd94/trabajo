package py.com.excelsis.sicca.capacitacion.session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TemporalType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import py.com.excelsis.sicca.entity.BandejaCapacitacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.CONVERSATION)
@Name("capaciatcionesListCustom")
public class CapaciatcionesListCustom extends CustomEntityQuery<Capacitaciones> {
	private static final String EJBQL = "select Capacitaciones from Capacitaciones Capacitaciones ";

	private static final String[] RESTRICTIONS = {};

	private Capacitaciones capacitaciones;

	public CapaciatcionesListCustom() {
		capacitaciones = new Capacitaciones();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<Capacitaciones> listaResultados(String ejbql, Map<String, QueryValue> parametros) {
		if (parametros.size() > 0)
			setParams(parametros);
		else{
			setParams(null);
		}
		//setEjbql(ejbql);
		setCustomEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		List<Capacitaciones> lista = getResultList();
		return lista;
	}

	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}

	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}

}
