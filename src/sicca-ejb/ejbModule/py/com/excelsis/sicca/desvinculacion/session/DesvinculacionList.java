package py.com.excelsis.sicca.desvinculacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Name("desvinculacionList")
public class DesvinculacionList extends EntityQuery<Desvinculacion> {

	private static final String EJBQL = "select desvinculacion from Desvinculacion desvinculacion";

	private static final String[] RESTRICTIONS = {
			"lower(desvinculacion.observacion) like lower(concat('%', concat(#{desvinculacionList.desvinculacion.observacion},'%')))",
			"lower(desvinculacion.concepto) like lower(concat('%', concat(#{desvinculacionList.desvinculacion.concepto},'%')))",
			"lower(desvinculacion.usuAlta) like lower(concat('%', concat(#{desvinculacionList.desvinculacion.usuAlta},'%')))", };

	private static final String[] RESTRICTIONSCU440 = {
		"desvinculacion.empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{desvinculacionList.oee.idConfiguracionUo}",
		"lower(desvinculacion.empleadoPuesto.persona.nombres) like lower(concat('%', concat(#{desvinculacionList.persona.nombres},'%')))",
		"lower(desvinculacion.empleadoPuesto.persona.apellidos) like lower(concat('%', concat(#{desvinculacionList.persona.apellidos},'%')))",
		"desvinculacion.empleadoPuesto.persona.documentoIdentidad=#{desvinculacionList.persona.documentoIdentidad}",
		"desvinculacion.empleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais=#{desvinculacionList.idPais}",
		"desvinculacion.datosEspecifMotivo.idDatosEspecificos=#{desvinculacionList.idMotivo}",
		"date(desvinculacion.fechaAlta) BETWEEN #{desvinculacionList.fechaDesde}",
		" #{desvinculacionList.fechaHasta}",
		};

	private static final String ORDER = "desvinculacion.empleadoPuesto.persona.documentoIdentidad";
	private Desvinculacion desvinculacion = new Desvinculacion();
	private Persona persona = new Persona();
	private ConfiguracionUoCab oee = new ConfiguracionUoCab();
	private Long idMotivo;
	private Long idPais;
	private Date fechaDesde=null;
	private Date fechaHasta=null ;

	public DesvinculacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Desvinculacion> listaResultadosCU440() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU440));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}


	public Desvinculacion getDesvinculacion() {
		return desvinculacion;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public ConfiguracionUoCab getOee() {
		return oee;
	}

	public void setOee(ConfiguracionUoCab oee) {
		this.oee = oee;
	}

	public Long getIdMotivo() {
		return idMotivo;
	}

	public void setIdMotivo(Long idMotivo) {
		this.idMotivo = idMotivo;
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

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}
	
	
	
}
