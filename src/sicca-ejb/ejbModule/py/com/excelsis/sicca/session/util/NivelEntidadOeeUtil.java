package py.com.excelsis.sicca.session.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.persistence.EntityManagerFactory;
import org.jboss.seam.util.Naming;

import enums.Estado;
import enums.EstadoAprobPendiente;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EntidadOee;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.VwEntidadOee;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.ListarConfiguracionUoDetFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("nivelEntidadOeeUtil")
public class NivelEntidadOeeUtil {
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	StatusMessages statusMessages;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(value = "entityManager", create = true)
	EntityManager em;
	@In(required = false)
	ListarConfiguracionUoDetFormController listarConfiguracionUoDetFormController;
	@In(required = false)
	Usuario usuarioLogueado;
	private Integer ordenUnidOrg;
	private BigDecimal codSinEntidad;
	private String nombreSinEntidad;
	private BigDecimal codNivelEntidad;
	private String nombreNivelEntidad;
	private Entidad entidad = new Entidad();
	private String denominacionUnidad;
	private String varDestinoSinNivel;
	private String varDestinoSinEntidad;
	private String varDestinoConfigUo;
	private String varDestinoUnidadOrg;
	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Long idConfigCab;
	private Long idUnidOrg;
	private Long idUnidadOrganizativa;
	private String codigoUnidOrgDep;
	private String denominacionUnidadOrgaDep;
	private Integer anioOee = null;

	public void init() {

		cargarSinNivelEntidad();
		cargarSinEntidad();
		cargarOEE();
		cargarUnidadOrganizativa();

		if (idSinEntidad == null && idSinNivelEntidad == null && idConfigCab == null) {
			limpiar();
		}
		if (statusMessages == null) {
			statusMessages = StatusMessages.instance();
		}
	}

	/*
	 * Incializa para los encabezados de capacitacion:debe setearse ConfiguracionUoCab y/o ConfiguracionUoDet
	 */
	public void init2() {
		if (em == null) {
			em = (EntityManager) Component.getInstance("entityManager");
		}
		if (idUnidadOrganizativa == null && this.getIdConfigCab() == null)
			cargarUnidad();
		else
			cargarUnidadOrganizativa();
		ConfiguracionUoCab oee = em.find(ConfiguracionUoCab.class, this.getIdConfigCab());
		ordenUnidOrg = oee.getOrden();
		denominacionUnidad = oee.getDenominacionUnidad();
		Entidad entidad = em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		SinEntidad sinEntidad =
			em.find(SinEntidad.class, entidad.getSinEntidad().getIdSinEntidad());
		codSinEntidad = sinEntidad.getEntCodigo();
		nombreSinEntidad = sinEntidad.getEntNombre();
		idSinEntidad = sinEntidad.getIdSinEntidad();
		Long idSinNivelEntidad = getIdSinNivelEntidad(entidad.getSinEntidad().getNenCodigo());
		this.idSinNivelEntidad = idSinNivelEntidad;
		cargarSinNivelEntidad();

		if (statusMessages == null) {
			statusMessages = StatusMessages.instance();
		}
	}

	public void initSinUO() {
		if (em == null) {
			em = (EntityManager) Component.getInstance("entityManager");
		}

		ConfiguracionUoCab oee = em.find(ConfiguracionUoCab.class, this.getIdConfigCab());
		ordenUnidOrg = oee.getOrden();
		denominacionUnidad = oee.getDenominacionUnidad();
		Entidad entidad = em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		SinEntidad sinEntidad =
			em.find(SinEntidad.class, entidad.getSinEntidad().getIdSinEntidad());
		codSinEntidad = sinEntidad.getEntCodigo();
		nombreSinEntidad = sinEntidad.getEntNombre();
		idSinEntidad = sinEntidad.getIdSinEntidad();
		Long idSinNivelEntidad = getIdSinNivelEntidad(entidad.getSinEntidad().getNenCodigo());
		this.idSinNivelEntidad = idSinNivelEntidad;
		cargarSinNivelEntidad();

		if (statusMessages == null) {
			statusMessages = StatusMessages.instance();
		}
	}

	/**
	 * Devuelve un diccionario con las descripciones con codigo del nivel, entidad, oee y unidad organizativa. Prerequisito, que esté cargado el nivel, entidad y oee
	 * 
	 * @return
	 */
	public Map<String, String> descNivelEntidadOee() {
		String codNivel = this.getCodNivelEntidad().toString();
		String codEntidad;
		codEntidad = codNivel + "." + this.getCodSinEntidad().toString();
		String codOee;
		codOee = codEntidad + "." + this.getOrdenUnidOrg().toString();
		String codUndOrg = null;
		if (this.getCodigoUnidOrgDep() != null)
			codUndOrg = this.getCodigoUnidOrgDep().toString();
		Map<String, String> diccDesc = new HashMap<String, String>();
		diccDesc.put("NIVEL", codNivel + " " + this.getNombreNivelEntidad());
		diccDesc.put("ENTIDAD", codEntidad + " " + this.getNombreSinEntidad());
		diccDesc.put("OEE", codOee + " " + this.getDenominacionUnidad());
		diccDesc.put("OEE_COD", codOee);
		if (codUndOrg != null) {
			diccDesc.put("UND_ORG", codUndOrg + " " + this.getDenominacionUnidadOrgaDep());
			diccDesc.put("UND_ORG_COD", codUndOrg);
		}

		return diccDesc;

	}

	private void cargarSinNivelEntidad() {
		if (idSinNivelEntidad != null) {
			if (em == null) {
				em = (EntityManager) Component.getInstance("entityManager");
			}
			SinNivelEntidad nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
			nombreNivelEntidad = nivelEntidad.getNenNombre();
			codNivelEntidad = nivelEntidad.getNenCodigo();
		}
	}

	private void cargarSinEntidad() {
		if (idSinEntidad != null) {
			SinEntidad entity = em.find(SinEntidad.class, idSinEntidad);
			codSinEntidad = entity.getEntCodigo();
			nombreSinEntidad = entity.getEntNombre();
		}
	}

	private void cargarOEE() {
		if (idConfigCab != null) {
			if (em == null) {
				em = (EntityManager) Component.getInstance("entityManager");
			}
			ConfiguracionUoCab configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);
			ordenUnidOrg = configuracionUoCab.getOrden();
			denominacionUnidad = configuracionUoCab.getDenominacionUnidad();
		}
	}

	public String traerCodUndOrg() {
		String codUndOrg = "-";
		String cSeperador = ".";
		if (this.getCodNivelEntidad() != null && this.getCodSinEntidad() != null
			&& this.getOrdenUnidOrg() != null) {
			codUndOrg =
				this.getCodNivelEntidad() + cSeperador + this.getCodSinEntidad() + cSeperador
					+ this.getOrdenUnidOrg();
		}
		return codUndOrg;

	}

	public void cargarUnidadOrganizativa() {
		if (idUnidadOrganizativa != null) {
			if (em == null) {
				em = (EntityManager) Component.getInstance("entityManager");
			}
			ConfiguracionUoDet configuracionUoDet =
				em.find(ConfiguracionUoDet.class, idUnidadOrganizativa);
			listarConfiguracionUoDetFormController =
				(ListarConfiguracionUoDetFormController) Component.getInstance(ListarConfiguracionUoDetFormController.class, true);
			codigoUnidOrgDep =
				listarConfiguracionUoDetFormController.obtenerCodigoMod(configuracionUoDet);
			denominacionUnidadOrgaDep = configuracionUoDet.getDenominacionUnidad();
		}
	}

	public void limpiar() {
		entidad = new Entidad();
		denominacionUnidad = null;
		nombreNivelEntidad = null;
		nombreSinEntidad = null;
		codNivelEntidad = null;
		codSinEntidad = null;
		ordenUnidOrg = null;
		idConfigCab = null;
		idSinEntidad = null;
		idSinNivelEntidad = null;
		idUnidadOrganizativa = null;
		denominacionUnidadOrgaDep = null;
		codigoUnidOrgDep = null;
	}

	private void cargarUnidad() {
		idUnidadOrganizativa = usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet();
		if (em == null) {
			em = (EntityManager) Component.getInstance("entityManager");
		}
		ConfiguracionUoDet configuracionUoDet =
			em.find(ConfiguracionUoDet.class, idUnidadOrganizativa);
		listarConfiguracionUoDetFormController =
			(ListarConfiguracionUoDetFormController) Component.getInstance(ListarConfiguracionUoDetFormController.class, true);
		codigoUnidOrgDep =
			listarConfiguracionUoDetFormController.obtenerCodigoMod(configuracionUoDet);
		denominacionUnidadOrgaDep = configuracionUoDet.getDenominacionUnidad();
		this.idConfigCab = configuracionUoDet.getConfiguracionUoCab().getIdConfiguracionUo();
	}

	private SinNivelEntidad nivelEntidadMaxAnho(BigDecimal codNivelEntidad) {
		if (em == null) {
			em = (EntityManager) Component.getInstance("entityManager");
		}
		Query q =
			em.createQuery("select sinNivelEntidad from SinNivelEntidad sinNivelEntidad "
				+ "WHERE sinNivelEntidad.nenCodigo =" + codNivelEntidad
				+ " and sinNivelEntidad.activo is true "
				+ "order by sinNivelEntidad.aniAniopre DESC");
		List<SinNivelEntidad> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	public void findNivelEntidad() {
		BigDecimal cod = codNivelEntidad;
		limpiar();
		codNivelEntidad = cod;
		if (codNivelEntidad != null) {
			SinNivelEntidad nivelEntidad = nivelEntidadMaxAnho(cod);
			if (nivelEntidad != null) {
				idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();

				nombreNivelEntidad = nivelEntidad.getNenNombre();
			} else {
				if (statusMessages == null) {
					statusMessages = (StatusMessages.instance());
				}
				nombreNivelEntidad = null;
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}
		} else {
			nombreNivelEntidad = null;
			nombreSinEntidad = null;
			codSinEntidad = null;
			idSinEntidad = null;
			idSinNivelEntidad = null;
			denominacionUnidad = null;
			ordenUnidOrg = null;
		}
	}

	/**
	 * Método que busca la entidad correspondiente al codigo ingresado y el nivel
	 */
	public void findEntidad() {
		idConfigCab = null;
		idUnidadOrganizativa = null;
		cargarSinNivelEntidad();

		if (codNivelEntidad != null && codSinEntidad != null) {
			if (sinEntidadList == null) {
				sinEntidadList = (SinEntidadList) Component.getInstance(SinEntidadList.class, true);
			}
			sinEntidadList.getSinEntidad().setEntCodigo(codSinEntidad);
			sinEntidadList.getSinEntidad().setNenCodigo(codNivelEntidad);
			SinEntidad sinEntidad = sinEntidadList.entidadMaxAnho();
			if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null) {
				idSinEntidad = sinEntidad.getIdSinEntidad();
				nombreSinEntidad = sinEntidad.getEntNombre();
				if (entidadList == null) {
					entidadList = (EntidadList) Component.getInstance(EntidadList.class, true);
				}
				entidadList.getSinEntidad().setIdSinEntidad(sinEntidad.getIdSinEntidad());
				entidad = entidadList.getEntidadBySinEntidad();
			} else {
				if (statusMessages == null) {
					statusMessages = (StatusMessages.instance());
				}
				nombreSinEntidad = null;
				denominacionUnidad = null;
				ordenUnidOrg = null;
				idSinEntidad = null;
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}

		} else {
			nombreSinEntidad = null;
			denominacionUnidad = null;
			ordenUnidOrg = null;
			idSinEntidad = null;
		}
	}

	private ConfiguracionUoCab obtenerOee() {
		String sql =
			"Select v.* from planificacion.vw_entidad_oee v "
				+ "join planificacion.configuracion_uo_cab uo on uo.id_configuracion_uo = v.id_configuracion_uo"
				+ " where v.id_sin_entidad = " + idSinEntidad + " and v.id_sin_nivel_entidad = "
				+ idSinNivelEntidad + " and uo.orden = " + ordenUnidOrg;
		List<VwEntidadOee> sin = em.createNativeQuery(sql, VwEntidadOee.class).getResultList();
		if (!sin.isEmpty()) {
			ConfiguracionUoCab oee =
				em.find(ConfiguracionUoCab.class, sin.get(0).getConfiguracionUo().getIdConfiguracionUo());
			return oee;
		}
		return null;
	}

	/**
	 * Método que busca la unidad organizativa correspondiente al codigo ingresado, a la entidad y al nivel
	 */
	public void findUnidadOrganizativa() {
		idUnidadOrganizativa = null;
		cargarSinNivelEntidad();
		cargarSinEntidad();
		if (ordenUnidOrg != null) {

			ConfiguracionUoCab configuracionUoCab = obtenerOee();
			if (configuracionUoCab != null) {
				idConfigCab = configuracionUoCab.getIdConfiguracionUo();
				denominacionUnidad = configuracionUoCab.getDenominacionUnidad();
			} else {
				if (statusMessages == null) {
					statusMessages = (StatusMessages.instance());
				}
				denominacionUnidad = null;
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}
		} else {
			denominacionUnidad = null;
			idConfigCab = null;
		}
	}

	/**
	 * Método que busca la unidad organizativa dependiente de la unidad organizativa cabeza, la entidad el nivel y el codigo ingresado
	 */
	public void obtenerUnidadOrganizativaDep2() {
		try {
			if (codigoUnidOrgDep != null && !codigoUnidOrgDep.trim().isEmpty()) {
				String cod = this.codigoUnidOrgDep;
				cargarSinNivelEntidad();
				cargarSinEntidad();
				cargarOEE();
				codigoUnidOrgDep = cod;

				String[] arrayCodigo = codigoUnidOrgDep.split("\\.");

				if (em == null) {
					em = (EntityManager) Component.getInstance("entityManager");
				}

				ConfiguracionUoCab configuracionUoCab =
					em.find(ConfiguracionUoCab.class, idConfigCab);
				ConfiguracionUoDet configuracionUoDet =
					buscarDetalle(configuracionUoCab, arrayCodigo, 3);

				if (configuracionUoDet == null
					|| configuracionUoDet.getIdConfiguracionUoDet() == null) {
					idUnidadOrganizativa = null;
					denominacionUnidadOrgaDep = null;
					if (statusMessages == null) {
						statusMessages = (StatusMessages.instance());
					}
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				} else {
					idUnidadOrganizativa = configuracionUoDet.getIdConfiguracionUoDet();
					denominacionUnidadOrgaDep = configuracionUoDet.getDenominacionUnidad();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (statusMessages == null) {
				statusMessages = (StatusMessages.instance());
			}
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
			return;
		}
	}

	/**
	 * Método que busca la unidad organizativa dependiente de la unidad organizativa cabeza, la entidad el nivel y el codigo ingresado
	 */
	public void obtenerUnidadOrganizativaDep() {
		try {
			if (codigoUnidOrgDep != null && !codigoUnidOrgDep.trim().isEmpty()) {
				String cod = this.codigoUnidOrgDep;
				cargarSinNivelEntidad();
				cargarSinEntidad();
				cargarOEE();
				codigoUnidOrgDep = cod;

				String[] arrayCodigo = codigoUnidOrgDep.split("\\.");

				if (em == null) {
					em = (EntityManager) Component.getInstance("entityManager");
				}

				ConfiguracionUoCab configuracionUoCab =
					em.find(ConfiguracionUoCab.class, idConfigCab);
				ConfiguracionUoDet configuracionUoDet =
					buscarDetalle(configuracionUoCab, arrayCodigo, 3);

				if (configuracionUoDet == null
					|| configuracionUoDet.getIdConfiguracionUoDet() == null) {
					idUnidadOrganizativa = null;
					denominacionUnidadOrgaDep = null;
					if (statusMessages == null) {
						statusMessages = (StatusMessages.instance());
					}
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				} else {
					idUnidadOrganizativa = configuracionUoDet.getIdConfiguracionUoDet();
					denominacionUnidadOrgaDep = configuracionUoDet.getDenominacionUnidad();
				}
			} else {
				idUnidadOrganizativa = null;
				denominacionUnidadOrgaDep = null;

			}
		} catch (Exception e) {
			e.printStackTrace();
			if (statusMessages == null) {
				statusMessages = (StatusMessages.instance());
			}
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
			return;
		}
	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoCab padre, String[] arrayCodigo, int pos) {
		ConfiguracionUoDet actual = new ConfiguracionUoDet();
		if (pos < arrayCodigo.length) {
			String cad =
				"select det.* from planificacion.configuracion_uo_det det "
					+ " where det.id_configuracion_uo = " + padre.getIdConfiguracionUo()
					+ " and activo is true and id_configuracion_uo_det_padre is null"
					+ " and det.orden = " + arrayCodigo[pos];
			List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
			if (em == null) {
				em = (EntityManager) Component.getInstance("entityManager");
			}
			lista = em.createNativeQuery(cad, ConfiguracionUoDet.class).getResultList();
			if (lista.size() > 0) {

				actual = lista.get(0);
				Integer val = pos;
				val++;
				for (int i = val; i < arrayCodigo.length; i++) {
					ConfiguracionUoDet configuracionUoDet =
						privBuscarDetalle(actual, arrayCodigo, i);

					if (i == arrayCodigo.length - 1)
						return configuracionUoDet;
					else {

						actual = configuracionUoDet;
					}
				}
			}
		}

		return actual;
	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet privBuscarDetalle(ConfiguracionUoDet padre, String[] arrayCodigo, int pos) {
		if (pos < arrayCodigo.length) {
			String cad =
				"select det.* from planificacion.configuracion_uo_det det "
					+ " where det.id_configuracion_uo_det_padre = "
					+ padre.getIdConfiguracionUoDet() + " and activo is true and det.orden = "
					+ arrayCodigo[pos];

			List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
			if (em == null) {
				em = (EntityManager) Component.getInstance("entityManager");
			}

			lista = em.createNativeQuery(cad, ConfiguracionUoDet.class).getResultList();
			if (lista.size() > 0)

				return lista.get(0);

		}

		return null;
	}

	public String getUrlToPageNivel(String elFrom) {
		limpiar();
		if(sinNivelEntidadList != null)
			sinNivelEntidadList.limpiar();
		
		if (sinNivelEntidadList != null && sinNivelEntidadList.getSinNivelEntidad() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(null);
		}
		return "/planificacion/sinNivelEntidad/SinNivelEntidadList.xhtml?from=" + elFrom
			+ (varDestinoSinNivel != null ? "&varDestino=" + varDestinoSinNivel : "");
	}

	public String getUrlToPageEntidad(String elFrom) {
		/* Limpieza de Variables */
		idSinEntidad = null;
		codSinEntidad = null;
		nombreSinEntidad = null;
		idConfigCab = null;
		ordenUnidOrg = null;
		denominacionUnidad = null;
		idUnidadOrganizativa = null;
		codigoUnidOrgDep = null;
		denominacionUnidadOrgaDep = null;
		/**/
		statusMessages = StatusMessages.instance();
		if (idSinNivelEntidad == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}

		if (codNivelEntidad == null) {
			if (em == null) {
				em = (EntityManager) Component.getInstance("entityManager");
			}
			SinNivelEntidad nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
			nombreNivelEntidad = nivelEntidad.getNenNombre();
			codNivelEntidad = nivelEntidad.getNenCodigo();
		}

		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=" + elFrom + "&codigoNivel="
			+ codNivelEntidad
			+ (varDestinoSinEntidad != null ? "&varDestino=" + varDestinoSinEntidad : "");
	}

	public String getUrlToPageOee(String elFrom) {
		/* Limpieza de variables */
		idUnidadOrganizativa = null;
		codigoUnidOrgDep = null;
		denominacionUnidadOrgaDep = null;
		/**/
		statusMessages = StatusMessages.instance();
		if (idSinNivelEntidad == null) {
			if (statusMessages == null) {
				statusMessages = StatusMessages.instance();
			}
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		if (idSinEntidad == null) {
			if (statusMessages == null) {
				statusMessages = StatusMessages.instance();
			}
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}

		String retorno =
			"/planificacion/searchForms/FindDependencias.xhtml?from=" + elFrom
				+ "&sinNivelEntidadIdSinNivelEntidad=" + idSinNivelEntidad
				+ (varDestinoConfigUo != null ? "&varDestino=" + varDestinoConfigUo : "");
		if (configuracionUoCabList == null) {
			configuracionUoCabList =
				(ConfiguracionUoCabList) Component.getInstance(ConfiguracionUoCabList.class, true);
		}
		configuracionUoCabList.getConfiguracionUoCab().setOrden(null);
		if (idSinEntidad != null)
			retorno = retorno + "&sinEntidadIdSinEntidad=" + idSinEntidad;
		return retorno;
	}

	public String getUrlToPageUnidadOrgDep(String elForm) {
		statusMessages = StatusMessages.instance();
		if (idSinNivelEntidad == null) {
			if (statusMessages == null) {
				statusMessages = (StatusMessages.instance());
			}
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		if (idSinEntidad == null) {

			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}
		if (idConfigCab == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("Cabecera_msg_oee"));
			return null;
		}

		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=" + elForm
			+ "&sinEntidadIdSinEntidad=" + idSinEntidad + "&idNivelEntidad=" + idSinNivelEntidad
			+ "&idConfiguracionUoCab=" + idConfigCab
			+ (varDestinoUnidadOrg != null ? "&varDestino=" + varDestinoUnidadOrg : "");
	}

	public Long getIdSinNivelEntidad(BigDecimal codNivelEntidad) {
		if (em == null) {
			em = (EntityManager) Component.getInstance("entityManager");
		}
		Query q =
			em.createQuery("select sinNivelEntidad from SinNivelEntidad sinNivelEntidad "
				+ "WHERE sinNivelEntidad.nenCodigo =" + codNivelEntidad
				+ " and sinNivelEntidad.activo is true "
				+ "order by sinNivelEntidad.aniAniopre DESC");
		List<SinNivelEntidad> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getIdSinNivelEntidad();
		}
		return null;
	}

	/**
	 * @return nombre de la entidad apartir del IdSinEntidad
	 */

	@SuppressWarnings("unchecked")
	public String nombreNivel(Long idSinEntidad) {
		String desc = "";
		SinEntidad entidad = em.find(SinEntidad.class, idSinEntidad);
		if (entidad != null) {
			List<SinNivelEntidad> nivelEntidads =
				em.createQuery("Select n from SinNivelEntidad n " + " where n.nenCodigo=" + entidad.getNenCodigo()).getResultList();
			if (!nivelEntidads.isEmpty())
				desc = nivelEntidads.get(0).getNenNombre();
		}

		return desc;
	}

	/**
	 * @return SinNivelEntidad apartir del año y del codigo
	 */
	public SinNivelEntidad nivelEntidad(BigDecimal aniAniopre, BigDecimal nenCodigo) {
		try {
			SinNivelEntidad nivelEntidads =
				(SinNivelEntidad) em.createQuery("Select n from SinNivelEntidad n "
					+ " where n.aniAniopre=:anio and n.nenCodigo=:nenCodigo").setParameter("anio", aniAniopre).setParameter("nenCodigo", nenCodigo).getSingleResult();

			return nivelEntidads;
		} catch (NoResultException ex) {
			// TODO: handle exception
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

	/**
	 * @return la entidadOee del Oee perteneciente al año en caso que no reciba año toma el año actual
	 */
	public EntidadOee entidadOeePorAnio(Long idOee, Integer anio) {
		try {
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anioActual = sdfSoloAnio.format(new Date());
			if (anio == null)
				anio = Integer.parseInt(anioActual);
			EntidadOee entidadOee =
				(EntidadOee) em.createQuery(" Select e from EntidadOee e "
					+ " where e.configuracionUoCab.idConfiguracionUo=:idOee and e.anho=:anio").setParameter("idOee", idOee).setParameter("anio", anio).getSingleResult();

			return entidadOee;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return el codigo del oee y la descripcion del oee apartir de su idOee
	 */
	public Map<String, String> codigoDescripcionOee(Long idOee) {
		Map<String, String> oeeCodNivel = new HashMap<String, String>();
		ConfiguracionUoCab oee = em.find(ConfiguracionUoCab.class, idOee);

		if (oee.getEntidad() != null) {
			Entidad entOee = em.find(Entidad.class, oee.getEntidad().getIdEntidad());
			oeeCodNivel.put("CODIGO", entOee.getSinEntidad().getNenCodigo() + "."
				+ entOee.getSinEntidad().getEntCodigo() + "." + oee.getOrden());
			oeeCodNivel.put("DESCRIPCION", oee.getDenominacionUnidad());
		} else {
			oeeCodNivel.put("CODIGO", "");
			oeeCodNivel.put("DESCRIPCION", "");
		}

		return oeeCodNivel;

	}

	public Long getIdSinNivelEntidad() {
		return idSinNivelEntidad;
	}

	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	public Long getIdConfigCab() {
		return idConfigCab;
	}

	public void setIdConfigCab(Long idConfigCab) {
		this.idConfigCab = idConfigCab;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public BigDecimal getCodSinEntidad() {
		return codSinEntidad;
	}

	public void setCodSinEntidad(BigDecimal codSinEntidad) {
		this.codSinEntidad = codSinEntidad;
	}

	public String getNombreSinEntidad() {
		return nombreSinEntidad;
	}

	public void setNombreSinEntidad(String nombreSinEntidad) {
		this.nombreSinEntidad = nombreSinEntidad;
	}

	public BigDecimal getCodNivelEntidad() {
		return codNivelEntidad;
	}

	public void setCodNivelEntidad(BigDecimal codNivelEntidad) {
		this.codNivelEntidad = codNivelEntidad;
	}

	public String getNombreNivelEntidad() {
		return nombreNivelEntidad;
	}

	public void setNombreNivelEntidad(String nombreNivelEntidad) {
		this.nombreNivelEntidad = nombreNivelEntidad;
	}

	public String getDenominacionUnidad() {
		return denominacionUnidad;
	}

	public void setDenominacionUnidad(String denominacionUnidad) {
		this.denominacionUnidad = denominacionUnidad;
	}

	public Long getIdUnidadOrganizativa() {
		return idUnidadOrganizativa;
	}

	public void setIdUnidadOrganizativa(Long idUnidadOrganizativa) {
		this.idUnidadOrganizativa = idUnidadOrganizativa;
	}

	public String getCodigoUnidOrgDep() {
		return codigoUnidOrgDep;
	}

	public void setCodigoUnidOrgDep(String codigoUnidOrgDep) {
		this.codigoUnidOrgDep = codigoUnidOrgDep;
	}

	public String getDenominacionUnidadOrgaDep() {
		return denominacionUnidadOrgaDep;
	}

	public void setDenominacionUnidadOrgaDep(String denominacionUnidadOrgaDep) {
		this.denominacionUnidadOrgaDep = denominacionUnidadOrgaDep;
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public String getVarDestinoSinNivel() {
		return varDestinoSinNivel;
	}

	public void setVarDestinoSinNivel(String varDestinoSinNivel) {
		this.varDestinoSinNivel = varDestinoSinNivel;
	}

	public String getVarDestinoSinEntidad() {
		return varDestinoSinEntidad;
	}

	public void setVarDestinoSinEntidad(String varDestinoSinEntidad) {
		this.varDestinoSinEntidad = varDestinoSinEntidad;
	}

	public String getVarDestinoConfigUo() {
		return varDestinoConfigUo;
	}

	public void setVarDestinoConfigUo(String varDestinoConfigUo) {
		this.varDestinoConfigUo = varDestinoConfigUo;
	}

	public String getVarDestinoUnidadOrg() {
		return varDestinoUnidadOrg;
	}

	public void setVarDestinoUnidadOrg(String varDestinoUnidadOrg) {
		this.varDestinoUnidadOrg = varDestinoUnidadOrg;
	}

	public Long getIdUnidOrg() {
		return idUnidOrg;
	}

	public void setIdUnidOrg(Long idUnidOrg) {
		this.idUnidOrg = idUnidOrg;
	}

	public Integer getAnioOee() {
		return anioOee;
	}

	public void setAnioOee(Integer anioOee) {
		this.anioOee = anioOee;
	}

}
