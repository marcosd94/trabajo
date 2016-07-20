package py.com.excelsis.sicca.juridicos.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Name("accionInconstCabList")
public class AccionInconstCabList extends EntityQuery<AccionInconstCab> {

	private static final String EJBQL = "select accionInconstCab from AccionInconstCab accionInconstCab";

	private static final String[] RESTRICTIONS = {
			
			"lower(accionInconstCab.estado) like lower(concat(#{accionInconstCabList.accionInconstCab.estado},'%'))",
			"lower(accionInconstCab.observacion) like lower(concat(#{accionInconstCabList.accionInconstCab.observacion},'%'))",
			"lower(accionInconstCab.resultado) like lower(concat(#{accionInconstCabList.accionInconstCab.resultado},'%'))",
			"lower(accionInconstCab.usuAlta) like lower(concat(#{accionInconstCabList.accionInconstCab.usuAlta},'%'))",
			"lower(accionInconstCab.usuMod) like lower(concat(#{accionInconstCabList.accionInconstCab.usuMod},'%'))", };

	private static final String[] RESTRICTIONSCU453 = {
		"lower(accionInconstCab.persona.nombres) like lower(concat('%', concat(#{accionInconstCabList.persona.nombres},'%')))",
		"lower(accionInconstCab.persona.apellidos) like lower(concat('%', concat(#{accionInconstCabList.persona.apellidos},'%')))",
		"accionInconstCab.persona.documentoIdentidad=#{accionInconstCabList.persona.documentoIdentidad}",
		"accionInconstCab.persona.paisByIdPaisExpedicionDoc.idPais=#{accionInconstCabList.idPais}",
		"accionInconstCab.estado=#{accionInconstCabList.estado}",
		"date(accionInconstCab.fechaAlta) BETWEEN #{accionInconstCabList.fechaDesde}",
		" #{accionInconstCabList.fechaHasta}",
	 };

	private static final String ORDER = "accionInconstCab.persona.documentoIdentidad";
	private AccionInconstCab accionInconstCab = new AccionInconstCab();
	private Persona persona = new Persona();
	private Long idPais = null;
	private Date fechaDesde=null;
	private Date fechaHasta=null ;
	private String estado = null;

	public AccionInconstCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<AccionInconstCab> listaResultadosCU453() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU453));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	
	public AccionInconstCab getAccionInconstCab() {
		return accionInconstCab;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}



	public Long getIdPais() {
		return idPais;
	}



	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}
	
	
	
}
