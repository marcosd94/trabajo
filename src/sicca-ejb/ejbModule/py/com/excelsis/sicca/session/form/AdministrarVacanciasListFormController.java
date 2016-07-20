package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;
import enums.ModalidadOcupacion;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.DetTipoNombramiento;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PromocionSalarial;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.TransicionEstado;
import py.com.excelsis.sicca.entity.VwEntidadOee;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.PlantaCargoDetList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("administrarVacanciasListFormController")
public class AdministrarVacanciasListFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7881910914378783316L;
	private static final String nro = "0123456789";
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	PlantaCargoDetList plantaCargoDetList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(create = true)
	BarrioList barrioList;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(required = false)
	Usuario usuarioLogueado;

	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();

	private String codigoPuesto;
	private String denominacionPuesto;

	private String codigoUnidOrgDep;
	private String observacion;
	private Integer ordenUnidOrg;
	private Long idConfigCab;
	private Long idPais;
	private Long idTipoCpt;
	private Long idBarrio;
	private Long idEstadoSeleccionado;
	private Long idCiudad;
	private Long idTipoNombramiento;
	private Long idEstado;
	private Long codDepartamento;
	private Long idConfiguracionUoDet;
	private PlantaCargoDet cargo;
	private ModalidadOcupacion modalidadOcupacion;
	private Estado activo = Estado.ACTIVO;
	private Boolean tieneUsuario;
	private Boolean seleccionarTodo = false;
	private List<SelectItem> departamentosSelecItem = new ArrayList<SelectItem>();;
	private List<SelectItem> ciudadSelecItem = new ArrayList<SelectItem>();;
	private List<SelectItem> barrioSelecItem = new ArrayList<SelectItem>();;
	private List<PlantaCargoDet> puestos;
	private List<PromocionSalarial> ListadoPromicionSalarial;
	private List<SelectItem> estadosTransicionSelecItem;
	private String elFrom = "planificacion/plantaCargoDet/PlantaCargoDetList";
	private String from = "planificacion/promocionSalarial/PromocionSalarialList";
	private Boolean esVacante;
	

	public Long getIdConfigCab() {
		return idConfigCab;
	}

	public void setIdConfigCab(Long idConfigCab) {
		this.idConfigCab = idConfigCab;
	}

	private PlantaCargoDet plantaCargoDet = new PlantaCargoDet();
	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();

	public void init() throws Exception {

		tieneUsuario = false;

		estadosTransicionSelecItem = new ArrayList<SelectItem>();
		estadosTransicionSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));

		if (idConfiguracionUoDet != null) {
			configuracionUoDet = em.find(ConfiguracionUoDet.class,
					idConfiguracionUoDet);
			codigoUnidOrgDep = obtenerCodigo(configuracionUoDet);
		}
		buscarDatosAsociadosUsuario();
		obtenerPuesto();
		search();
		updateBarrio();
		updateCiudad();
		updateDepartamento();
	}
	
	public void initPromocionSalarial() throws Exception {

		tieneUsuario = false;
		if(from == null) from ="planificacion/puestosTrabajo/PlantaCargoDetList";
		estadosTransicionSelecItem = new ArrayList<SelectItem>();
		estadosTransicionSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));

		if (idConfiguracionUoDet != null) {
			configuracionUoDet = em.find(ConfiguracionUoDet.class,
					idConfiguracionUoDet);
			codigoUnidOrgDep = obtenerCodigo(configuracionUoDet);
		}
		buscarDatosAsociadosUsuario();
		//obtenerPuesto();
//		search();
//		updateBarrio();
//		updateCiudad();
//		updateDepartamento();
	}

	public void init2() {
		if (idConfiguracionUoDet != null) {
			configuracionUoDet = em.find(ConfiguracionUoDet.class,
					idConfiguracionUoDet);
			codigoUnidOrgDep = obtenerCodigo(configuracionUoDet);
		}
	}
	

	public void seleccionarTodos() throws Exception {
		String query = getQuery();
		if (query == null)
			return;
		puestos = plantaCargoDetList.listaResultadosVacancias(query,
				seleccionarTodo);
		System.out.println("-");
	}

	public String obtenerCodigo(ConfiguracionUoDet uoDet) {
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";
		}
		code = code.substring(0, code.length() - 1);
		return code;
	}

	public String getUrlToPageDependencia() {

		if (configuracionUoCab == null
				|| configuracionUoCab.getIdConfiguracionUo() == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Cabecera_msg_oee"));
			return null;
		}
		if (idConfigCab != null)
			configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);

		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from="
				+ elFrom
				+ "&idNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad()
				+ "&sinEntidadIdSinEntidad="
				+ sinEntidad.getIdSinEntidad()
				+ "&idConfiguracionUoCab="
				+ configuracionUoCab.getIdConfiguracionUo();
	}

	public void searchAll() throws Exception {

		updateBarrio();
		updateCiudad();
		updateDepartamento();
		configuracionUoCab = new ConfiguracionUoCab();
		ordenUnidOrg = null;

		sinEntidad = new SinEntidad();
		plantaCargoDet = new PlantaCargoDet();
		nivelEntidad = new SinNivelEntidad();
		tieneUsuario = false;
		buscarDatosAsociadosUsuario();
		puestos = new ArrayList<PlantaCargoDet>();
		plantaCargoDetList.setConfiguracionUoCab(configuracionUoCab);
		plantaCargoDetList.setSinEntidad(sinEntidad);
		plantaCargoDetList.setSinNivelEntidad(nivelEntidad);
		configuracionUoDet = null;
		codigoUnidOrgDep = null;
		plantaCargoDetList.setConfiguracionUoDet(configuracionUoDet);
		codigoPuesto = null;
		idBarrio = null;
		idCiudad = null;
		codDepartamento = null;
		idPais = null;
		idTipoCpt = null;
		idTipoNombramiento = null;
		modalidadOcupacion = null;
		activo = Estado.ACTIVO;
		idEstado = null;
		idConfiguracionUoDet = null;
		denominacionPuesto = null;
		if (!validar()) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar por lo menos un filtro antes de buscar.");
			return;
		}
		// puestos = plantaCargoDetList.searchInit();
		searchVacancia();
	}
	
	
	public void searchAllPromocionSalarial() throws Exception {

		updateBarrio();
		updateCiudad();
		updateDepartamento();
		configuracionUoCab = new ConfiguracionUoCab();
		ordenUnidOrg = null;

		sinEntidad = new SinEntidad();
		plantaCargoDet = new PlantaCargoDet();
		nivelEntidad = new SinNivelEntidad();
		tieneUsuario = false;
		buscarDatosAsociadosUsuario();
		puestos = new ArrayList<PlantaCargoDet>();
		plantaCargoDetList.setConfiguracionUoCab(configuracionUoCab);
		plantaCargoDetList.setSinEntidad(sinEntidad);
		plantaCargoDetList.setSinNivelEntidad(nivelEntidad);
		configuracionUoDet = null;
		codigoUnidOrgDep = null;
		plantaCargoDetList.setConfiguracionUoDet(configuracionUoDet);
		codigoPuesto = null;
		idBarrio = null;
		idCiudad = null;
		codDepartamento = null;
		idPais = null;
		idTipoCpt = null;
		idTipoNombramiento = null;
		modalidadOcupacion = null;
		activo = Estado.ACTIVO;
		idEstado = null;
		idConfiguracionUoDet = null;
		denominacionPuesto = null;
		if (!validar()) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar por lo menos un filtro antes de buscar.");
			return;
		}
		// puestos = plantaCargoDetList.searchInit();
		searchVacancia();
	}

	public void searchVacancia() throws Exception {
		String query = getQuery();
		if (query == null)
			return;
		puestos = plantaCargoDetList.listaResultadosVacancias(query,
				seleccionarTodo);
		System.out.println("-");
	}

	public String getQuery() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		String query = "select distinct plantaCargoDet from PlantaCargoDet plantaCargoDet "
				+ "left join plantaCargoDet.detTipoNombramientos nom "
				+ "left join nom.tipoNombramiento tipo "
				+ "join plantaCargoDet.configuracionUoDet uoDet "
				+ "join uoDet.configuracionUoCab uoCab "
				+ "join uoCab.entidad entidad "
				+ "join entidad.sinEntidad sinEntidad ";
		// JOINS
		if (idTipoCpt != null) {
			query += "join plantaCargoDet.cpt cpt "
					+ "join cpt.tipoCpt tipoCpt ";
		}
		if (idCiudad != null || idPais != null || codDepartamento != null) {
			query += "join plantaCargoDet.oficina oficina "
					+ "join oficina.ciudad ciudad "
					+ "join ciudad.departamento dpto " + "join dpto.pais pais ";
		}
		if (idBarrio != null) {
			query += "join plantaCargoDet.oficina oficina "
					+ "join oficina.barrio barrio ";
		}
		if (idEstado != null)
			query += "join plantaCargoDet.estadoCab estadoCab ";

		if (!sufc.validarInput(configuracionUoCab.getIdConfiguracionUo()
				.toString(), TiposDatos.LONG.getValor(), null)) {
			return null;
		}
		if (!sufc.validarInput(sinEntidad.getIdSinEntidad().toString(),
				TiposDatos.LONG.getValor(), null)) {
			return null;
		}

		// WHERE
		query += "where uoCab.idConfiguracionUo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " and sinEntidad.idSinEntidad = "
				+ sinEntidad.getIdSinEntidad();
		if (configuracionUoDet != null
				&& configuracionUoDet.getIdConfiguracionUoDet() != null) {
			if (!sufc.validarInput(configuracionUoDet.getIdConfiguracionUoDet()
					.toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			query += " and uoDet.idConfiguracionUoDet = "
					+ configuracionUoDet.getIdConfiguracionUoDet();
		}

		if (idTipoCpt != null) {
			if (!sufc.validarInput(idTipoCpt.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			query += " and tipoCpt.idTipoCpt = " + idTipoCpt;
		}

		if (idCiudad != null) {
			if (!sufc.validarInput(idCiudad.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			query += " and ciudad.idCiudad = " + idCiudad;
		}

		if (codDepartamento != null) {
			if (!sufc.validarInput(codDepartamento.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			query += " and dpto.idDepartamento = " + codDepartamento;
		}

		if (idPais != null) {
			if (!sufc.validarInput(idPais.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			query += " and pais.idPais = " + idPais;
		}

		if (idBarrio != null) {
			if (!sufc.validarInput(idBarrio.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			query += " and barrio.idBarrio = " + idBarrio;
		}

		if (idTipoNombramiento != null) {
			if (!sufc.validarInput(idTipoNombramiento.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			query += " and tipo.idTipoNombramiento = " + idTipoNombramiento;
		}
		if (modalidadOcupacion != null && modalidadOcupacion.getId() != null
				&& modalidadOcupacion.getId() == 1)
			query += " and plantaCargoDet.contratado is true ";
		if (modalidadOcupacion != null && modalidadOcupacion.getId() != null
				&& modalidadOcupacion.getId() == 2)
			query += " and plantaCargoDet.permanente is true ";
		if (idEstado != null) {
			if (!sufc.validarInput(idEstado.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			query += " and estadoCab.idEstadoCab = " + idEstado;
		}

		if (activo != null && activo.getValor() != null) {
			if (!sufc.validarInput(activo.getValor().toString(),
					TiposDatos.BOOLEAN.getValor(), null)) {
				return null;
			}
			query += " and plantaCargoDet.activo is " + activo.getValor();
		}

		if (plantaCargoDet != null
				&& plantaCargoDet.getIdPlantaCargoDet() != null) {
			if (!sufc.validarInput(plantaCargoDet.getIdPlantaCargoDet()
					.toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			query += " and plantaCargoDet.idPlantaCargoDet = "
					+ plantaCargoDet.getIdPlantaCargoDet();
		}
		if (configuracionUoDet == null
				&& idBarrio == null
				&& idCiudad == null
				&& idPais == null
				&& idTipoCpt == null
				&& idTipoNombramiento == null
				&& idEstado == null
				&& (modalidadOcupacion == null || modalidadOcupacion.getId() == null)
				&& activo == null) {

			query += " and (plantaCargoDet.estadoCab.descripcion = 'JUDICIALIZADO' or plantaCargoDet.estadoCab.descripcion = 'VACANTE' or plantaCargoDet.activo is false) ";
		}

		if (denominacionPuesto != null && !denominacionPuesto.trim().isEmpty()) {
			if (!sufc.validarInput(denominacionPuesto,
					TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			query += " and lower(plantaCargoDet.descripcion) like lower(concat('%', concat('"
					+ seguridadUtilFormController
							.caracterInvalido(denominacionPuesto.toLowerCase())
					+ "','%')))";
		}
		//PARA QUE NO APAREZCAN LAS PLANTILLAS GENERADAS PARA LA INSERCION MASIVA - ECESPEDES
		query += " and plantaCargoDet.esPlantilla is false";
		return query;
	}

	public void search() throws Exception {
		if (!validar()) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar por lo menos un filtro antes de buscar.");
			return;
		}
		searchVacancia();
		
		estadoVacante();
	}
	
	public void estadoVacante(){	
		if(idEstado == null){
			esVacante= false;	
		}else{
		EstadoCab estadoActual=em.find(EstadoCab.class, idEstado);
		esVacante= (estadoActual.getDescripcion().equalsIgnoreCase("VACANTE"))? true:false;
		}
	}
	
	
	public void searchPromocionSalarial() throws Exception {
		if (!validar()) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar por lo menos un filtro antes de buscar.");
			return;
		}
		searchCategorias();
		
		estadoVacante();
	}
	
	public void searchCategorias() throws Exception {
		
		
		
		String query = "select * from seleccion.promocion_salarial where 1= 1";
				
			if(idEstado != null)	
				query	+= "and  id_estado_cab = " + idEstado;
		
		
		ListadoPromicionSalarial = em.createNativeQuery(query, PromocionSalarial.class).getResultList();
		
	}

	private Boolean validar() {
		try {
			if ((configuracionUoDet == null || configuracionUoDet
					.getIdConfiguracionUoDet() == null)
					&& (plantaCargoDet == null || plantaCargoDet
							.getIdPlantaCargoDet() == null)
					&& idPais == null
					&& idTipoCpt == null
					&& codDepartamento == null
					&& idCiudad == null
					&& idTipoNombramiento == null
					&& idBarrio == null
					&& idEstado == null
					&& (modalidadOcupacion == null || modalidadOcupacion
							.getId() == null) && (denominacionPuesto == null || denominacionPuesto.trim().isEmpty())) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void buscarDatosAsociadosUsuario() {
		nivelEntidad = new SinNivelEntidad();
		if (usuarioLogueado.getConfiguracionUoCab() != null) {
			tieneUsuario = true;
			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			if (configuracionUoCab.getOrden() != null)
				ordenUnidOrg = configuracionUoCab.getOrden();

			sinEntidad = obtenerEntidad();
			nivelEntidad = obtenerNivel();
		}
	}

	private SinEntidad obtenerEntidad() {
		String sql = "Select v.* from planificacion.vw_entidad_oee v "
				+ " where v.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo();

		List<VwEntidadOee> sin = em.createNativeQuery(sql, VwEntidadOee.class)
				.getResultList();
		if (!sin.isEmpty()) {
			SinEntidad sinE = em.find(SinEntidad.class, sin.get(0)
					.getSinEntidad().getIdSinEntidad());
			return sinE;
		}
		return null;
	}

	private SinNivelEntidad obtenerNivel() {
		String sql = "Select v.* from planificacion.vw_entidad_oee v "
				+ " where v.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " and v.id_sin_entidad = " + sinEntidad.getIdSinEntidad();

		List<VwEntidadOee> sin = em.createNativeQuery(sql, VwEntidadOee.class)
				.getResultList();
		if (!sin.isEmpty()) {
			SinNivelEntidad nivel = em.find(SinNivelEntidad.class, sin.get(0)
					.getIdNivelEntidad());
			return nivel;
		}
		return null;
	}

	public void findNivelEntidad() {

		if (nivelEntidad.getNenCodigo() != null)
			nivelEntidad = obtenerNivelEntidad(nivelEntidad.getNenCodigo());
		else
			nivelEntidad = new SinNivelEntidad();

	}

	private SinNivelEntidad obtenerNivelEntidad(BigDecimal codNivelEntidad) {
		String sql = "Select v.* from planificacion.vw_entidad_oee v "
				+ " where v.codigo_nivel = " + codNivelEntidad;
		List<VwEntidadOee> sin = em.createNativeQuery(sql, VwEntidadOee.class)
				.getResultList();
		if (!sin.isEmpty()) {
			SinNivelEntidad sinNivelEntidad = em.find(SinNivelEntidad.class,
					sin.get(0).getIdNivelEntidad());
			return sinNivelEntidad;
		}
		return null;
	}

	public void findEntidad() {
		if (nivelEntidad.getNenCodigo() != null
				&& sinEntidad.getEntCodigo() != null)
			obtenerEntidad(sinEntidad.getEntCodigo());

	}

	private SinEntidad obtenerEntidad(BigDecimal codSinEntidad) {
		String sql = "Select v.* from planificacion.vw_entidad_oee v "
				+ " where v.ent_codigo = " + codSinEntidad
				+ " and v.id_sin_nivel_entidad = "
				+ nivelEntidad.getIdSinNivelEntidad();
		List<VwEntidadOee> sin = em.createNativeQuery(sql, VwEntidadOee.class)
				.getResultList();
		if (!sin.isEmpty()) {
			SinEntidad sinE = em.find(SinEntidad.class, sin.get(0)
					.getSinEntidad().getIdSinEntidad());
			return sinE;
		}
		return null;
	}

	private ConfiguracionUoCab obtenerOee() {
		String sql = "Select v.* from planificacion.vw_entidad_oee v "
				+ "join planificacion.configuracion_uo_cab uo on uo.id_configuracion_uo = v.id_configuracion_uo"
				+ " where v.id_sin_entidad = " + sinEntidad.getIdSinEntidad()
				+ " and v.id_sin_nivel_entidad = "
				+ nivelEntidad.getIdSinNivelEntidad() + " and uo.orden = "
				+ ordenUnidOrg;
		List<VwEntidadOee> sin = em.createNativeQuery(sql, VwEntidadOee.class)
				.getResultList();
		if (!sin.isEmpty()) {
			ConfiguracionUoCab oee = em.find(ConfiguracionUoCab.class,
					sin.get(0).getConfiguracionUo().getIdConfiguracionUo());
			return oee;
		}
		return null;
	}

	public void findUnidadOrganizativa() {
		if (ordenUnidOrg != null) {
			configuracionUoCab = obtenerOee();
			if (configuracionUoCab != null)
				idConfigCab = configuracionUoCab.getIdConfiguracionUo();
		} else
			configuracionUoCab = new ConfiguracionUoCab();
	}

	@SuppressWarnings("unchecked")
	public void obtenerPuesto() {
		if (codigoPuesto != null && !codigoPuesto.trim().isEmpty()) {
			if (codigoUnidOrgDep == null || codigoUnidOrgDep.trim().isEmpty()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Primero escoja una Unidad Organizativa");
				return;
			}
			if (codigoPuesto.length() < 2) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"El caracter ingresado es invalido");
				return;
			}

			String modalidad = codigoPuesto.substring(0, 1).toUpperCase();
			Integer pos = nro.indexOf(modalidad);
			if (pos != -1) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"El caracter ingresado es invalido");
				return;
			}
			String ord = codigoPuesto.substring(1, codigoPuesto.length());
			byte[] separacion = ord.getBytes();

			for (int i = 0; i < separacion.length; i++) {
				Integer p = nro.indexOf((char) separacion[i]);
				if (p == null || p == -1) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"El caracter ingresado es invalido");
					return;
				}

			}

			Integer orden = new Integer(ord);
			if (orden != null) {
				List<PlantaCargoDet> listaPuesto = new ArrayList<PlantaCargoDet>();

				listaPuesto = configuracionUoDet.getPlantaCargoDets();
				for (PlantaCargoDet p : listaPuesto) {
					Integer dOrden = p.getOrden();
					if (modalidad.equals("C")) {
						if (dOrden.equals(orden) && p.getContratado()) {
							plantaCargoDet = new PlantaCargoDet();
							plantaCargoDet = p;
							return;
						}
					}
					if (modalidad.equals("P")) {
						if (dOrden.equals(orden) && p.getPermanente()) {
							plantaCargoDet = new PlantaCargoDet();
							plantaCargoDet = p;
							return;
						}
					}
				}
			}
		}

	}

	/**
	 * Método que busca la unidad organizativa dependiente de la unidad
	 * organizativa cabeza, la entidad el nivel y el codigo ingresado
	 */
	public void obtenerUnidadOrganizativaDep() {
		try {
			if (codigoUnidOrgDep != null && !codigoUnidOrgDep.trim().isEmpty()) {
				String[] arrayCodigo = codigoUnidOrgDep.split("\\.");
				if (arrayCodigo.length > 3) {
					Integer orden = new Integer(arrayCodigo[3]);
					Integer tam = arrayCodigo.length;
					findUnidadOrganizativa();
					configuracionUoDet = new ConfiguracionUoDet();

					configuracionUoDet = buscarDetalle(configuracionUoCab,
							orden);

					ConfiguracionUoDet det = new ConfiguracionUoDet();
					if (tam == 1)
						det = null;
					for (int i = 4; i < arrayCodigo.length; i++) {
						Integer ord = new Integer(arrayCodigo[i]);

						det = buscarDetalle(configuracionUoDet, ord);
						if (det != null)
							configuracionUoDet = det;
					}

				}
			}
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("nivel_msg_1"));
		}

	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoCab padre,
			Integer orden) {
		String cad = "select det.* from planificacion.configuracion_uo_det det "
				+ " where det.id_configuracion_uo = "
				+ padre.getIdConfiguracionUo()
				+ " and det.orden = "
				+ orden
				+ " and det.id_configuracion_uo_det_padre is null";
		List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
		lista = em.createNativeQuery(cad, ConfiguracionUoDet.class)
				.getResultList();
		ConfiguracionUoDet actual = new ConfiguracionUoDet();
		if (lista.size() > 0) {
			actual = lista.get(0);
			return actual;
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoDet padre,
			Integer orden) {
		if (padre != null) {
			String cad = "select det.* from planificacion.configuracion_uo_det det "
					+ " where det.id_configuracion_uo_det_padre = "
					+ padre.getIdConfiguracionUoDet()
					+ " and det.orden = "
					+ orden;
			List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
			lista = em.createNativeQuery(cad, ConfiguracionUoDet.class)
					.getResultList();
			ConfiguracionUoDet actual = new ConfiguracionUoDet();
			if (lista.size() > 0) {
				actual = lista.get(0);
				return actual;
			}
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	private List<DetTipoNombramiento> buscarTipoNombramiento(Long id) {
		String sql = "select det.* "
				+ "from planificacion.det_tipo_nombramiento det "
				+ "where det.id_planta_cargo_det = " + id;

		return em.createNativeQuery(sql, DetTipoNombramiento.class)
				.getResultList();
	}

	public String searchNombramiento(PlantaCargoDet det) {
		List<DetTipoNombramiento> lista = new ArrayList<DetTipoNombramiento>();
		Long idDet = det.getIdPlantaCargoDet();
		lista = buscarTipoNombramiento(idDet);
		String nombramiento = "";
		for (DetTipoNombramiento nom : lista) {
			nombramiento += nom.getTipoNombramiento().getDescripcion() + " - ";

		}
		if (nombramiento.length() > 0)
			nombramiento = nombramiento.substring(0, nombramiento.length() - 2);
		return nombramiento;
	}

	public String obtenerCodigo(PlantaCargoDet det) {
		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = det.getConfiguracionUoDet();
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes)
			code += codigo + ".";

		if (det.getContratado())
			code = code + "C";
		if (det.getPermanente())
			code = code + "P";
		code = code + det.getOrden();
		return code;
	}

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet,
			List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);
		else {
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getOrden());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad()
					.getSinEntidad().getEntCodigo().intValue());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad()
					.getSinEntidad().getNenCodigo().intValue());
		}
		return listCodigos;
	}

	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
	}

	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
	}

	public void updateBarrio() {
		List<Barrio> barList = getBarriosByCiudad();
		barrioSelecItem = new ArrayList<SelectItem>();
		buildBarrioSelectItem(barList);
	}

	private List<Ciudad> getCiudadByDpto() {
		if (codDepartamento != null) {
			ciudadList.getDepartamento().setIdDepartamento(codDepartamento);
			ciudadList.setMaxResults(null);
			return ciudadList.getResultList();
		}
		return new ArrayList<Ciudad>();
	}

	private List<Departamento> getDepartamentosByPais() {
		if (idPais != null) {
			departamentoList.getPais().setIdPais(idPais);
			departamentoList.setOrder("descripcion");
			departamentoList.setMaxResults(null);
			return departamentoList.getResultList();
		}
		return new ArrayList<Departamento>();
	}

	private List<Barrio> getBarriosByCiudad() {
		if (idCiudad != null) {
			barrioList.setIdCiudad(idCiudad);
			barrioList.setOrder("descripcion");
			barrioList.setMaxResults(null);
			return barrioList.getResultList();
		}
		return new ArrayList<Barrio>();
	}

	private void buildBarrioSelectItem(List<Barrio> barrioList) {
		if (barrioSelecItem == null)
			barrioSelecItem = new ArrayList<SelectItem>();
		else
			barrioSelecItem.clear();
		barrioSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Barrio bar : barrioList) {
			barrioSelecItem.add(new SelectItem(bar.getIdBarrio(), bar
					.getDescripcion()));
		}
	}

	private void buildDepartamentoSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();
		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(),
					dep.getDescripcion()));
		}
	}

	private void buildCiudadSelectItem(List<Ciudad> ciudadList) {
		if (ciudadSelecItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecItem.clear();
		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}

	public Boolean checkSeleccionados() {

		Long estadoActual = null;
		if (puestos == null)
			return false;
		for (PlantaCargoDet p : puestos) {
			if (p.getSeleccionado()) {
				if (estadoActual == null)
					estadoActual = p.getEstadoCab().getIdEstadoCab();
				else {
					if (estadoActual.intValue() != p.getEstadoCab()
							.getIdEstadoCab().intValue())
						return false;
				}
			}
		}

		if (estadoActual == null)
			return false;
		buscarEstadosPosibles(estadoActual);
		return true;

	}
	
	public Boolean checkSeleccionadosPromocionSalarial() {

		Long estadoActual = null;
		if (ListadoPromicionSalarial == null)
			return false;
		for (PromocionSalarial p : ListadoPromicionSalarial) {
			if (p.getSeleccionado() != null && p.getSeleccionado()) {
				if (estadoActual == null)
					estadoActual = p.getEstadoCab().getIdEstadoCab();
				else {
					if (estadoActual.intValue() != p.getEstadoCab()
							.getIdEstadoCab().intValue())
						return false;
				}
			}
		}

		if (estadoActual == null)
			return false;
		buscarEstadosPosibles(estadoActual);
		return true;

	}

	@SuppressWarnings("unchecked")
	private void buscarEstadosPosibles(Long idEstadoActual) {

		estadosTransicionSelecItem = new ArrayList<SelectItem>();
		estadosTransicionSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));

		String cadena = "select tran.* from planificacion.transicion_estado tran "
				+ "where tran.id_estado_cab = " + idEstadoActual;

		List<TransicionEstado> lista = new ArrayList<TransicionEstado>();
		lista = em.createNativeQuery(cadena, TransicionEstado.class)
				.getResultList();
		if (lista.size() > 0) {
			for (TransicionEstado t : lista) {
				estadosTransicionSelecItem.add(new SelectItem(t
						.getEstadoCabByPosibleEstado().getIdEstadoCab(), t
						.getEstadoCabByPosibleEstado().getDescripcion()));
			}

		}
	}

	public void save() throws Exception {
		if (idEstadoSeleccionado == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe escoger el estado");
			return;
		}
		EstadoCab cab = new EstadoCab();
		cab = em.find(EstadoCab.class, idEstadoSeleccionado);
		
		try {
			for (PlantaCargoDet p : puestos) {
				if (p.getSeleccionado()) {
					String mensajeValidacion = p.validarCondiciones();
					if(mensajeValidacion.equals("Ok")){
						p.setUsuMod(usuarioLogueado.getCodigoUsuario());
						p.setFechaMod(new Date());
						p.setEstadoCab(cab);
						em.merge(p);
						HistoricosEstado historico = new HistoricosEstado();
						historico.setEstadoCab(cab);
						historico.setFechaMod(new Date());
						if (observacion != null)
							historico.setObservacion(observacion.trim()
									.toUpperCase());
						historico.setPlantaCargoDet(p);
						historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.persist(historico);
						em.flush();
					}
					else {
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, mensajeValidacion);
						return;
					}
				}
			}

			observacion = null;
			idEstadoSeleccionado = null;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_NO_MSG"));
			// TODO: handle exception
		}
		search();

	}
	
	
	public void savePromocionSalarial() throws Exception {
		if (idEstadoSeleccionado == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe escoger el estado");
			return;
		}
		EstadoCab cab = new EstadoCab();
		cab = em.find(EstadoCab.class, idEstadoSeleccionado);
		
		try {
			for (PromocionSalarial p : ListadoPromicionSalarial) {
				if (p.getSeleccionado()) {
					p.setUsuMod(usuarioLogueado.getCodigoUsuario());
					p.setFechaMod(new Date());
					p.setEstadoCab(cab);
					em.merge(p);
					em.flush();
					
//					String mensajeValidacion = p.validarCondiciones();
//					if(mensajeValidacion.equals("Ok")){
//						p.setUsuMod(usuarioLogueado.getCodigoUsuario());
//						p.setFechaMod(new Date());
//						p.setEstadoCab(cab);
//						em.merge(p);
//						HistoricosEstado historico = new HistoricosEstado();
//						historico.setEstadoCab(cab);
//						historico.setFechaMod(new Date());
//						if (observacion != null)
//							historico.setObservacion(observacion.trim()
//									.toUpperCase());
//						historico.setPlantaCargoDet(p);
//						historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
//						em.persist(historico);
//						em.flush();
//					}
//					else {
//						statusMessages.clear();
//						statusMessages.add(Severity.ERROR, mensajeValidacion);
//						return;
//					}
				}
			}

			observacion = null;
			idEstadoSeleccionado = null;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_NO_MSG"));
			// TODO: handle exception
		}
		searchPromocionSalarial();

	}

	/**
	 * En caso de que el puesto esta Activo lo Inactiva y vice versa
	 * 
	 * @param row
	 * @throws Exception
	 */
	public void inactivar(Integer row) throws Exception {

		PlantaCargoDet pl = new PlantaCargoDet();
		pl = puestos.get(row);
		if (pl.getActivo())
			pl.setActivo(false);
		else
			pl.setActivo(true);
		pl.setUsuMod(usuarioLogueado.getCodigoUsuario());
		pl.setFechaMod(new Date());
		try {
			em.merge(pl);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_NO_MSG"));
			e.printStackTrace();
		}
		searchAll();
	}
	
	
	
	public void inactivarPromocionSalarial(Integer row) throws Exception {

		PromocionSalarial pl = new PromocionSalarial();
		pl = this.ListadoPromicionSalarial.get(row);
		if (pl.getActivo())
			pl.setActivo(false);
		else
			pl.setActivo(true);
		pl.setUsuMod(usuarioLogueado.getCodigoUsuario());
		pl.setFechaMod(new Date());
		try {
			em.merge(pl);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_NO_MSG"));
			e.printStackTrace();
		}
		searchAllPromocionSalarial();
	}
	
	/**
	 * Inactiva todos los puestos seleccionados
	 *  AGREGADO POR JUANBER
	 * 
	 * 
	 */
	public void inactivarSeleccionados(Boolean activo) throws Exception {
		
		if (puestos == null)
			return ;
		for (PlantaCargoDet p : puestos) {
			if (p.getSeleccionado()) {
				p.setActivo(activo);
				p.setUsuMod(usuarioLogueado.getCodigoUsuario());
				p.setFechaMod(new Date());
					try {
						em.merge(p);
						em.flush();
						statusMessages.clear();
						statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
								.getString("GENERICO_MSG"));
					} catch (Exception e) {
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("GENERICO_NO_MSG"));
						e.printStackTrace();
					}
			}


		
	}
		
		search();

	}
	

	@SuppressWarnings("unchecked")
	public String eliminar(Integer row) {
		PlantaCargoDet pl = new PlantaCargoDet();
		pl = puestos.get(row);
		Long id = pl.getIdPlantaCargoDet();
		String cad = "select hist.* from planificacion.historicos_estado hist "
				+ "where hist.id_planta_cargo_det = " + id;

		List<HistoricosEstado> lista = new ArrayList<HistoricosEstado>();
		lista = em.createNativeQuery(cad, HistoricosEstado.class)
				.getResultList();

		if (lista.size() == 1) {
			try {
				em.remove(lista.get(0));
				em.remove(pl);
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				searchVacancia();
				return "ok";
			} catch (Exception e) {

			}
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El registro no pudo ser eliminado, esta en uso");

			return null;
		}
		if (lista == null || lista.size() == 0) {
			try {
				em.remove(pl);
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				searchVacancia();
				return "ok";

			} catch (Exception e) {

			}
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El registro no pudo ser eliminado, esta en uso");

			return null;
		}

		statusMessages.clear();
		statusMessages.add(Severity.ERROR,
				"El registro no pudo ser eliminado, ya tuvo movimientos");

		return "ok";
	}

	public String obtenerAccion(PlantaCargoDet planta) {
		if (planta.getActivo() && planta.getEstadoCab() != null
				&& planta.getEstadoCab().getDescripcion().equals("VACANTE"))
			return "Inactivar";
		if (!planta.getActivo())
			return "Activar";
		return "";
	}
	
	
	public String obtenerAccionPromocion(PromocionSalarial planta) {
		if (planta.getActivo() && planta.getEstadoCab() != null
				&& planta.getEstadoCab().getDescripcion().equals("VACANTE"))
			return "Inactivar";
		if (!planta.getActivo())
			return "Activar";
		return "";
	}


	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
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

	public String getCodigoPuesto() {
		return codigoPuesto;
	}

	public void setCodigoPuesto(String codigoPuesto) {
		this.codigoPuesto = codigoPuesto;
	}

	public PlantaCargoDet getPlantaCargoDet() {
		return plantaCargoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public String getCodigoUnidOrgDep() {
		return codigoUnidOrgDep;
	}

	public void setCodigoUnidOrgDep(String codigoUnidOrgDep) {
		this.codigoUnidOrgDep = codigoUnidOrgDep;
	}

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public Long getIdBarrio() {
		return idBarrio;
	}

	public void setIdBarrio(Long idBarrio) {
		this.idBarrio = idBarrio;
	}

	public Long getIdTipoNombramiento() {
		return idTipoNombramiento;
	}

	public void setIdTipoNombramiento(Long idTipoNombramiento) {
		this.idTipoNombramiento = idTipoNombramiento;
	}

	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}

	public ModalidadOcupacion getModalidadOcupacion() {
		return modalidadOcupacion;
	}

	public void setModalidadOcupacion(ModalidadOcupacion modalidadOcupacion) {
		this.modalidadOcupacion = modalidadOcupacion;
	}

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(
			List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public Long getCodDepartamento() {
		return codDepartamento;
	}

	public void setCodDepartamento(Long codDepartamento) {
		this.codDepartamento = codDepartamento;
	}

	public List<SelectItem> getBarrioSelecItem() {
		return barrioSelecItem;
	}

	public void setBarrioSelecItem(List<SelectItem> barrioSelecItem) {
		this.barrioSelecItem = barrioSelecItem;
	}

	public Estado getActivo() {
		return activo;
	}

	public void setActivo(Estado activo) {
		this.activo = activo;
	}

	public Boolean getTieneUsuario() {
		return tieneUsuario;
	}

	public void setTieneUsuario(Boolean tieneUsuario) {
		this.tieneUsuario = tieneUsuario;
	}

	public List<PlantaCargoDet> getPuestos() {
		return puestos;
	}

	public void setPuestos(List<PlantaCargoDet> puestos) {
		this.puestos = puestos;
	}

	public List<SelectItem> getEstadosTransicionSelecItem() {
		return estadosTransicionSelecItem;
	}

	public void setEstadosTransicionSelecItem(
			List<SelectItem> estadosTransicionSelecItem) {
		this.estadosTransicionSelecItem = estadosTransicionSelecItem;
	}

	public Long getIdEstadoSeleccionado() {
		return idEstadoSeleccionado;
	}

	public void setIdEstadoSeleccionado(Long idEstadoSeleccionado) {
		this.idEstadoSeleccionado = idEstadoSeleccionado;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public PlantaCargoDet getCargo() {
		return cargo;
	}

	public void setCargo(PlantaCargoDet cargo) {
		this.cargo = cargo;
	}

	public Long getIdConfiguracionUoDet() {
		return idConfiguracionUoDet;
	}

	public void setIdConfiguracionUoDet(Long idConfiguracionUoDet) {
		this.idConfiguracionUoDet = idConfiguracionUoDet;
	}

	public String getElFrom() {
		return elFrom;
	}

	public void setElFrom(String elFrom) {
		this.elFrom = elFrom;
	}

	public String getDenominacionPuesto() {
		return denominacionPuesto;
	}

	public void setDenominacionPuesto(String denominacionPuesto) {
		this.denominacionPuesto = denominacionPuesto;
	}

	public Boolean getSeleccionarTodo() {
		return seleccionarTodo;
	}

	public void setSeleccionarTodo(Boolean seleccionarTodo) {
		this.seleccionarTodo = seleccionarTodo;
	}

	public Boolean getEsVacante() {
		return esVacante;
	}

	public void setEsVacante(Boolean esVacante) {
		this.esVacante = esVacante;
	}

	public List<PromocionSalarial> getListadoPromicionSalarial() {
		return ListadoPromicionSalarial;
	}

	public void setListadoPromicionSalarial(
			List<PromocionSalarial> listadoPromicionSalarial) {
		ListadoPromicionSalarial = listadoPromicionSalarial;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
