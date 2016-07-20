package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;

import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EmpleadoPuestoList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("buscarFuncionarioFormController")
public class BuscarFuncionarioFormController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6843048221583658786L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	EmpleadoPuestoList empleadoPuestoList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	private Persona persona = new Persona();

	private String cargoActual;
	private Integer orden;
	private String codUnidOrgDep;
	private String from;
	BigDecimal nenCodigo;

	/**
	 * Inicializa los valores
	 */
	public void init() {
		try {

			cargarCampos();// EN EL CASO QUE RECIBA PARAMETRO

			if (nivelEntidadOeeUtil == null
					|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
							.getNombreNivelEntidad() == null)) {
				nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component
						.getInstance(NivelEntidadOeeUtil.class, true);
				cargarDatosNivel();
			}
			search();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Instancia la clase NivelEntidadOeeUtil y setea la Unidad Organizativo del usuario logueado
	 */
	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null || (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
			
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			
			if (usuarioLogueado.getConfiguracionUoDet() != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
	}

	/**
	 * Setea todos los datos del usuario logueado
	 */
	public void cargarCabecera() {

		Long idOee = usuarioLogueado.getConfiguracionUoCab()
				.getIdConfiguracionUo();
		ConfiguracionUoCab uoCab = new ConfiguracionUoCab();
		uoCab = em.find(ConfiguracionUoCab.class, idOee);
		Long idEnt = uoCab.getEntidad().getIdEntidad();
		Entidad ent = new Entidad();
		ent = em.find(Entidad.class, idEnt);
		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil.getIdSinNivelEntidad(ent
				.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(ent.getSinEntidad()
				.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(uoCab.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

	private void cargarCampos() {
		cargoActual = "SI";

	}

	public void search() {
		Long idSinEnt = nivelEntidadOeeUtil.getIdSinEntidad();
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null) {
			SinEntidad ent = em.find(SinEntidad.class, nivelEntidadOeeUtil.getIdSinEntidad());
			empleadoPuestoList.setSinEntidad(ent);
			empleadoPuestoList.setNenCodigo(ent.getNenCodigo());
		}
		Long idSinNivel = nivelEntidadOeeUtil.getIdSinNivelEntidad();
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null) {
			SinNivelEntidad nive = em.find(SinNivelEntidad.class, nivelEntidadOeeUtil.getIdSinNivelEntidad());
			empleadoPuestoList.setSinNivelEntidad(nive);
			empleadoPuestoList.setNenCodigo(nive.getNenCodigo());
		}
		Long idO = nivelEntidadOeeUtil.getIdConfigCab();
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			empleadoPuestoList.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));

		if (persona != null &&
			((persona.getNombres() != null && !persona.getNombres().trim().isEmpty()) ||
			(persona.getNombres() != null && !persona.getNombres().trim().isEmpty()) ||
			(persona.getDocumentoIdentidad() != null && !persona.getDocumentoIdentidad().trim().isEmpty())))
			
			empleadoPuestoList.setPersona(persona);
		
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			empleadoPuestoList.setConfiguracionUoDet(em.find(ConfiguracionUoDet.class, nivelEntidadOeeUtil.getIdUnidadOrganizativa()));
		if (cargoActual != null && cargoActual.equals("SI"))
			empleadoPuestoList.setValor(true);
		if (cargoActual != null && cargoActual.equals("NO"))
			empleadoPuestoList.setValor(false);
		empleadoPuestoList.listaResultadosCU159();
		//empleadoPuestoList.listaResultadosReasignacionInsercionMasiva();
	}

	public void clean() {

		orden = null;
		persona = new Persona();
		cargoActual = "SI";
		nivelEntidadOeeUtil = null;
		cargarDatosNivel();
		search();
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getCargoActual() {
		return cargoActual;
	}

	public void setCargoActual(String cargoActual) {
		this.cargoActual = cargoActual;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public String getCodUnidOrgDep() {
		return codUnidOrgDep;
	}

	public void setCodUnidOrgDep(String codUnidOrgDep) {
		this.codUnidOrgDep = codUnidOrgDep;
	}

	public BigDecimal getNenCodigo() {
		return nenCodigo;
	}

	public void setNenCodigo(BigDecimal nenCodigo) {
		this.nenCodigo = nenCodigo;
	}

}
