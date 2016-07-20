package py.com.excelsis.sicca.movilidadLaboral.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("solicitudTrasladoCabList")
public class SolicitudTrasladoCabList extends EntityQuery<SolicitudTrasladoCab> {
	@In(required = false)
	Usuario usuarioLogueado;
	private static final String EJBQL = "select solicitudTrasladoCab from SolicitudTrasladoCab solicitudTrasladoCab";

	private static final String[] RESTRICTIONS = {
			"lower(solicitudTrasladoCab.persona.nombres) like concat('%',lower(concat(#{solicitudTrasladoCabList.persona.nombres},'%')))",
			"solicitudTrasladoCab.datosEspTipoTranslado.idDatosEspecificos=#{solicitudTrasladoCabList.idTipoTraslado}",
			"solicitudTrasladoCab.datosEspEstadoSolicitud.idDatosEspecificos=#{solicitudTrasladoCabList.idEstadoTraslado}", };

	private SolicitudTrasladoCab solicitudTrasladoCab = new SolicitudTrasladoCab();
	private Persona persona = new Persona();
	private Long idTipoTraslado;
	private Long idEstadoTraslado;
	private static final String ORDER = "solicitudTrasladoCab.datosEspTipoTranslado.descripcion";

	public SolicitudTrasladoCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<SolicitudTrasladoCab> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<SolicitudTrasladoCab> limpiar() {
		solicitudTrasladoCab = new SolicitudTrasladoCab();
		idEstadoTraslado = null;
		idTipoTraslado = null;
		persona = new Persona();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public SolicitudTrasladoCab getSolicitudTrasladoCab() {
		return solicitudTrasladoCab;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdTipoTraslado() {
		return idTipoTraslado;
	}

	public void setIdTipoTraslado(Long idTipoTraslado) {
		this.idTipoTraslado = idTipoTraslado;
	}

	public Long getIdEstadoTraslado() {
		return idEstadoTraslado;
	}

	public void setIdEstadoTraslado(Long idEstadoTraslado) {
		this.idEstadoTraslado = idEstadoTraslado;
	}

	
}
