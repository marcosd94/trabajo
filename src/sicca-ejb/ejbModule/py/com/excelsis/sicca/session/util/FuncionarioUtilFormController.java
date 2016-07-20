package py.com.excelsis.sicca.session.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;
import enums.ModalidadOcupacionSeleccion;
import enums.TipoGeneralEspecifico;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.FuncionarioOee;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.FuncionarioOeeHome;
import py.com.excelsis.sicca.session.PersonaHome;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.TipoNombramientoList;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("funcionarioUtilFormController")
public class FuncionarioUtilFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8381131410456084595L;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(value = "entityManager")
	EntityManager em;
	@In
	StatusMessages statusMessages;

	@In(create = true)
	FuncionarioOeeHome funcionarioOeeHome;
	@In(create = true)
	PersonaHome personaHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(create = true)
	TipoNombramientoList tipoNombramientoList;

	private SinNivelEntidad sinNivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private FuncionarioOee funcionarioOee;
	private Persona persona;

	private String descNivelEntidad, descEntidad;
	private Long idTipoPlanta, idTipoNombramiento;

	private List<FuncionarioOee> listDetail;
	private List<SelectItem> tipoNombramientoSelectItem;
	public ModalidadOcupacionSeleccion modalidadOcupacionSeleccion;

	private String codigoCpt;
	private Long idTipoCpt;
	private Cpt cpt = new Cpt();
	private Estado activo;
	private String denominacion;
	private TipoGeneralEspecifico tipoEspGeneral;

	private Long idCpt;

	private String from = "";

	public void init() {
		// CARGAMOS LOS OEE ASOCIADOS AL USUARIO
		funcionarioOee = new FuncionarioOee();
		persona = em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
		if (listDetail == null) {
			listDetail = new ArrayList<FuncionarioOee>(persona.getFuncionarioOees());
		}

		initNivelEntidad();
		initEntidad();
		initOee();
		inicializarParametros();

		updateTipoNombramientoSelectItem();
	}

	public void init2(Boolean initParam) {
		// CARGAMOS LOS OEE ASOCIADOS AL USUARIO
		funcionarioOee = new FuncionarioOee();
		persona = em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
		if (listDetail == null) {
			listDetail = new ArrayList<FuncionarioOee>(persona.getFuncionarioOees());
		}

		initNivelEntidad();
		initEntidad();
		initOee();
		if (initParam)
			inicializarParametros();
		else
			cargarCpt();
		updateTipoNombramientoSelectItem();
	}

	public void cargarCpt() {
		if (idCpt == null)
			cpt = new Cpt();
		else {
			try {
				cpt = em.find(Cpt.class, idCpt);
				codigoCpt = traerCodCpt(cpt);
			} catch (Exception e) {
				e.printStackTrace();
				cpt = new Cpt();
			}
		}
	}

	private void inicializarParametros() {
		codigoCpt = null;
		// idTipoCpt = null;
		activo = null;
		setDenominacion(null);
		cargarCpt();
	}

	public void obtenerDescNivelEntidad() {
		descNivelEntidad = null;
		if (sinNivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(sinNivelEntidad.getNenCodigo());
			SinNivelEntidad nEnt = sinNivelEntidadList.nivelEntidadMaxAnho();
			sinNivelEntidad = nEnt != null ? nEnt : new SinNivelEntidad();
			descNivelEntidad = nEnt != null ? nEnt.getNenNombre() : null;
		}
	}

	public void obtenerDescEntidad() {
		descEntidad = null;
		if (sinNivelEntidad.getNenCodigo() != null && sinEntidad.getEntCodigo() != null) {
			sinEntidadList.getSinEntidad().setNenCodigo(sinNivelEntidad.getNenCodigo());
			sinEntidadList.getSinEntidad().setEntCodigo(sinEntidad.getEntCodigo());
			SinEntidad ent = sinEntidadList.entidadMaxAnho();
			sinEntidad = ent != null ? ent : new SinEntidad();
			descEntidad = ent != null ? ent.getEntNombre() : null;
		}
	}

	public String toFindOee() {
		if (sinEntidad.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_entidad"));
			return null;
		}
		return "find";
	}

	public List<SelectItem> updateTipoNombramientoSelectItem() {
		tipoNombramientoSelectItem = new ArrayList<SelectItem>();
		tipoNombramientoSelectItem.add(new SelectItem(null, SICCAAppHelper.getBundleMessage("opt_select_one")));

		if (idTipoPlanta != null) {
			tipoNombramientoList.getTipoPlanta().setIdTipoPlanta(idTipoPlanta);
			tipoNombramientoList.setOrderColumn("tipoNombramiento.descripcion");
			tipoNombramientoList.setMaxResults(null);
			List<TipoNombramiento> list = tipoNombramientoList.getResultList();
			for (TipoNombramiento tn : list) {
				tipoNombramientoSelectItem.add(new SelectItem(tn.getIdTipoNombramiento(), tn.getDescripcion()));
			}
		}
		return tipoNombramientoSelectItem;
	}

	public void addRow() {
		if (!checkToDetail())
			return;

		funcionarioOee.setPersona(persona);
		funcionarioOee.setConfiguracionUoCab(configuracionUoCab);
		funcionarioOee.setTipoNombramiento(em.find(TipoNombramiento.class, idTipoNombramiento));
		funcionarioOee.setModalidadOcupacion(modalidadOcupacionSeleccion.getValor());
		listDetail.add(funcionarioOee);
		clear();
	}

	public String getUrlToPageEntidad() {
		if (sinNivelEntidad.getIdSinNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		sinNivelEntidad = em.find(SinNivelEntidad.class, sinNivelEntidad.getIdSinNivelEntidad());
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=" + from + "&codigoNivel="
			+ sinNivelEntidad.getNenCodigo();
	}

	public String getUrlToPageTipoCpt() {
		if (idTipoCpt == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un Tipo CPT");
			return null;
		}
		return "/planificacion/cpt/CptList.xhtml?from=" + from + "&tipoCpt=" + idTipoCpt;
	}

	// METODOS PRIVADOS
	private void initNivelEntidad() {
		if (sinNivelEntidad.getIdSinNivelEntidad() != null) {
			sinNivelEntidad =
				em.find(SinNivelEntidad.class, sinNivelEntidad.getIdSinNivelEntidad());
		}
	}

	private void initEntidad() {
		if (sinEntidad.getIdSinEntidad() != null) {
			sinEntidad = em.find(SinEntidad.class, sinEntidad.getIdSinEntidad());
			if (sinNivelEntidad.getNenCodigo() == null) {
				sinNivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
				obtenerDescNivelEntidad();
			}
		}
	}

	private void initOee() {
		if (configuracionUoCab.getIdConfiguracionUo() != null) {
			configuracionUoCab =
				em.find(ConfiguracionUoCab.class, configuracionUoCab.getIdConfiguracionUo());
		}
	}

	public void clear() {
		sinNivelEntidad = new SinNivelEntidad();
		sinEntidad = new SinEntidad();
		configuracionUoCab = new ConfiguracionUoCab();
		funcionarioOee = new FuncionarioOee();
		idTipoPlanta = null;
		idTipoNombramiento = null;
		modalidadOcupacionSeleccion = null;
		cpt = new Cpt();
		idCpt = null;
		activo = null;
		idTipoCpt = null;
		tipoEspGeneral = null;
	}

	private boolean checkToDetail() {
		if (persona.getEsFuncionario() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_respuesta"));
			return false;
		}
		if (sinNivelEntidad.getIdSinNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_nivel"));
			return false;
		}
		if (sinEntidad.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_entidad"));
			return false;
		}
		if (configuracionUoCab.getIdConfiguracionUo() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_oee"));
			return false;
		}
		if (modalidadOcupacionSeleccion.getValor() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar el combo Modalidad de Ocupación ");
			return false;
		}
		if (idTipoNombramiento == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_tipo_nombramiento"));
			return false;
		}
		if (funcionarioOee.getFechaIngreso() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_fecha_ingreso"));
			return false;
		}
		if (funcionarioOee.getFechaIngreso().after(new Date())) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_fecha_ingreso_incorrecta"));
			return false;
		}
		for (FuncionarioOee func : listDetail) {
			if (func.getConfiguracionUoCab().getIdConfiguracionUo().equals(configuracionUoCab.getIdConfiguracionUo())
				&& func.getTipoNombramiento().getIdTipoNombramiento().equals(idTipoNombramiento)) {
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_existe_en_grilla"));
				return false;
			}
		}

		return true;
	}
public String traerCodCpt(Cpt cpt){
	
	Integer nivelCpt = cpt.getNivel();
	Integer gradoMin = cpt.getGradoSalarialMin().getNumero();
	Integer gradoMax = cpt.getGradoSalarialMax().getNumero();
	Integer numero = cpt.getNumero();
	Integer nroEspecifico = cpt.getNroEspecifico();
	String cSeparador=".";
	return nivelCpt+cSeparador+gradoMin+cSeparador+gradoMax+cSeparador+numero+cSeparador+nroEspecifico;
}
	
	@SuppressWarnings("unchecked")
	public void findCpt() {
		if (codigoCpt != null && !"".equals(codigoCpt)) {
			try {
				Integer nivelCpt = null;
				Integer gradoMin = null;
				Integer gradoMax = null;
				Integer numero = null;
				Integer nroEspecifico = null;
				String[] arrayCodigo = codigoCpt.split("\\.");
				for (int i = 0; i < arrayCodigo.length; i++) {
					if (i == 0)
						nivelCpt = new Integer(arrayCodigo[i]);
					if (i == 1)
						gradoMin = new Integer(arrayCodigo[i]);
					if (i == 2)
						gradoMax = new Integer(arrayCodigo[i]);
					if (i == 3)
						numero = new Integer(arrayCodigo[i]);
					if (i == 4)
						nroEspecifico = new Integer(arrayCodigo[i]);
				}

				String cadena =
					" " + "select cpt.* from planificacion.cpt cpt "
						+ "join planificacion.grado_salarial max "
						+ "on max.id_grado_salarial = cpt.id_grado_salarial_max "
						+ "join planificacion.grado_salarial min "
						+ "on min.id_grado_salarial = cpt.id_grado_salarial_min "
						+ "where cpt.nivel = " + nivelCpt + " and max.numero = " + gradoMax
						+ " and min.numero = " + gradoMin + " and cpt.numero = " + numero
						+ " and cpt.nro_especifico = " + nroEspecifico;
				if (idTipoCpt != null)
					cadena = cadena + " and id_tipo_cpt = " + idTipoCpt;
				List<Cpt> lista = new ArrayList<Cpt>();
				lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
				if (lista.size() > 0)
					cpt = lista.get(0);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	public boolean vacio(String s) {
		if (s == null || "".equals(s))
			return true;

		return false;
	}

	// GETTERS Y SETTERS
	public FuncionarioOee getFuncionarioOee() {
		return funcionarioOee;
	}

	public void setFuncionarioOee(FuncionarioOee funcionarioOee) {
		this.funcionarioOee = funcionarioOee;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public String getDescNivelEntidad() {
		return descNivelEntidad;
	}

	public void setDescNivelEntidad(String descNivelEntidad) {
		this.descNivelEntidad = descNivelEntidad;
	}

	public String getDescEntidad() {
		return descEntidad;
	}

	public void setDescEntidad(String descEntidad) {
		this.descEntidad = descEntidad;
	}

	public List<FuncionarioOee> getListDetail() {
		return listDetail;
	}

	public void setListDetail(List<FuncionarioOee> listDetail) {
		this.listDetail = listDetail;
	}

	public Long getIdTipoPlanta() {
		return idTipoPlanta;
	}

	public void setIdTipoPlanta(Long idTipoPlanta) {
		this.idTipoPlanta = idTipoPlanta;
	}

	public Long getIdTipoNombramiento() {
		return idTipoNombramiento;
	}

	public void setIdTipoNombramiento(Long idTipoNombramiento) {
		this.idTipoNombramiento = idTipoNombramiento;
	}

	public List<SelectItem> getTipoNombramientoSelectItem() {
		return tipoNombramientoSelectItem;
	}

	public void setTipoNombramientoSelectItem(List<SelectItem> tipoNombramientoSelectItem) {
		this.tipoNombramientoSelectItem = tipoNombramientoSelectItem;
	}

	public ModalidadOcupacionSeleccion getModalidadOcupacionSeleccion() {
		return modalidadOcupacionSeleccion;
	}

	public void setModalidadOcupacionSeleccion(ModalidadOcupacionSeleccion modalidadOcupacionSeleccion) {
		this.modalidadOcupacionSeleccion = modalidadOcupacionSeleccion;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getCodigoCpt() {
		return codigoCpt;
	}

	public void setCodigoCpt(String codigoCpt) {
		this.codigoCpt = codigoCpt;
	}

	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
	}

	public Cpt getCpt() {
		return cpt;
	}

	public void setCpt(Cpt cpt) {
		this.cpt = cpt;
	}

	public Estado getActivo() {
		return activo;
	}

	public void setActivo(Estado activo) {
		this.activo = activo;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}

	public String getDenominacion() {
		return denominacion;
	}

	public void setIdCpt(Long idCpt) {
		this.idCpt = idCpt;
	}

	public Long getIdCpt() {
		return idCpt;
	}

	public TipoGeneralEspecifico getTipoEspGeneral() {
		return tipoEspGeneral;
	}

	public void setTipoEspGeneral(TipoGeneralEspecifico tipoEspGeneral) {
		this.tipoEspGeneral = tipoEspGeneral;
	}

}
