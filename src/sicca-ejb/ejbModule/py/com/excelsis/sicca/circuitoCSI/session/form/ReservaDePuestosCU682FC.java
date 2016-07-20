package py.com.excelsis.sicca.circuitoCSI.session.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.dto.ConcursoPuestoAgrDTO;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.CategoriaCpt;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinCategoria;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.ConcursoHome;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.PlantaCargoDetList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.PlantaCargoDetDTO;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import enums.ModalidadOcupacion;
import enums.TiposDatos;

@Scope(ScopeType.CONVERSATION)
@Name("reservaDePuestosCU682FC")
public class ReservaDePuestosCU682FC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	BarrioList barrioList;
	@In(create = true)
	PlantaCargoDetList plantaCargoDetList;
	@In(create = true)
	ConcursoHome concursoHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true, required = false)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();
	private Concurso concurso;

	private Long idDpto;
	private Long idCiudad;
	private Long idBarrio;
	private Long idCpt;
	private Long idPlantaCargo;
	private Long idAnx;
	private Long idConfiguracionUoDet;
	private Long idAgr;
	private ModalidadOcupacion modalidadOcupacion;
	private String obs;
	private Integer cantReservados;
	private String codigoUnidOrgDep;
	private String fromConcurso;
	private String denominacion;
	private BigDecimal monto;
	private Boolean iniciado = false;
	private Integer nroActividad;

	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private List<SelectItem> barrioSelecItem;
	private List<PlantaCargoDet> listaPlantaCargoDet;
	private List<PlantaCargoDetDTO> listaPlantaCargoDto;
	private List<ConcursoPuestoDet> listaPuestosReservados;
	private List<ConcursoPuestoAgrDTO> listaPuestosAgrupados;
	private SeguridadUtilFormController seguridadUtilFormController;
	private static String CONCURSO_DE_MERITOS = "CONCURSO DE MERITOS";
	private static String CONCURSO_INTERNO_OPOSICION = "CONCURSO INTERNO DE OPOSICION INSTITUCIONAL";
	private static String CONCURSO_PUBLICO_OPOSICION = "CONCURSO PUBLICO DE OPOSICION";
	private static String CONCURSO_SIMPLIFICADO = "CONCURSO SIMPLIFICADO";
	private static String CONCURSO_INTERNO_INTERINSTITUCIONAL = "CONCURSO INTERNO DE OPOSICION INTER INSTITUCIONAL";
	

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		
		if (idConfiguracionUoDet == null) {
			codigoUnidOrgDep = null;
			configuracionUoDet = new ConfiguracionUoDet();
		}

		concurso = new Concurso();
		if (idAgr != null) {
			ConcursoPuestoAgr gr = new ConcursoPuestoAgr();
			gr = em.find(ConcursoPuestoAgr.class, idAgr);
			concurso = gr.getConcurso();
			concursoHome.setInstance(concurso);
		}
		concurso = concursoHome.getInstance();

		
		validarOee();
	
		buscarDatosAsociadosUsuario();
		updateDepartamento();
		updateCiudad();
		
		llenarListado1();
		cargarListadoAgrupados();
		if (idAnx != null && idPlantaCargo != null)
			actualizarDatos();
	}

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		if (!seguridadUtilFormController.verificarPerteneceOee(concurso
				.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}

	}

	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			Entidad entidad = new Entidad();
			if (configuracionUoCab.getEntidad() != null)
				entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	/**
	 * Método que carga todos los concursos agrupados los cuales estan en
	 * seleccion.concurso_puesto_agr
	 */
	@SuppressWarnings("unchecked")
	public void cargarListadoAgrupados() {
		String sql = "select distinct(puesto_agr.*) "
				+ "from seleccion.concurso_puesto_agr puesto_agr  "
				+ "join seleccion.concurso_puesto_det puesto_det  "
				+ "on puesto_agr.id_concurso_puesto_agr = puesto_det.id_concurso_puesto_agr "
				+ " join planificacion.planta_cargo_det cargo "
				+ "on cargo.id_planta_cargo_det = puesto_det.id_planta_cargo_det "
				+ "join seleccion.concurso concurso "
				+ "on concurso.id_concurso = puesto_det.id_concurso "
				+ "where concurso.id_concurso = " + concurso.getIdConcurso();
		
		listaPuestosAgrupados = new ArrayList<ConcursoPuestoAgrDTO>();
		List<ConcursoPuestoAgr> listaAgr = new ArrayList<ConcursoPuestoAgr>();
		listaAgr = em.createNativeQuery(sql, ConcursoPuestoAgr.class)
				.getResultList();
		for (ConcursoPuestoAgr agr : listaAgr) {

			ConcursoPuestoAgrDTO dto = new ConcursoPuestoAgrDTO();

			dto.setConcursoPuestoAgr(agr);
			agr = em.find(ConcursoPuestoAgr.class, agr.getIdConcursoPuestoAgr());
			if (agr.getConcursoPuestoDets() != null
					&& agr.getConcursoPuestoDets().size() > 0) {
				dto.setCantidad(agr.getConcursoPuestoDets().size());
				dto.setPlantaCargoDet(agr.getConcursoPuestoDets().get(0)
						.getPlantaCargoDet());
			} else
				dto.setCantidad(0);
			listaPuestosAgrupados.add(dto);
		}

	}

	/**
	 * Método que realiza el filtrado de la lista de concursos sin agrupar
	 * 
	 * @throws Exception
	 */
	public void search() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (!sufc.validarInput(configuracionUoCab.getIdConfiguracionUo()
				.toString(), TiposDatos.LONG.getValor(), null))
			return;
		if (!sufc.validarInput(concurso.getIdConcurso().toString(),
				TiposDatos.LONG.getValor(), null))
			return;

		String sql1 = Sentencia1();
		String sql2 = Sentencia2();
		String where1 = " where lower(cab.descripcion) = 'concurso'  "
				+ "and (" + "		det.id_estado_det = ("
				+ "				select est.id_estado_det "
				+ "				from planificacion.estado_det est "
				+ "				where lower(est.descripcion)='libre'" + "		) "
				+ "		or det.id_estado_det is null" + "	) "
				+ " and uo_cab.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo();

		String where2 = " where lower(estado_det.descripcion) = 'en reserva' "
				+ "and uo_cab.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " and concurso.id_concurso = " + concurso.getIdConcurso();

		if (idCiudad != null) {
			if (!sufc.validarInput(idCiudad.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			sql1 = sql1
					+ " join planificacion.oficina ofi on ofi.id_oficina = det.id_oficina "
					+ " join general.ciudad ciudad on ciudad.id_ciudad = ofi.id_ciudad";
			where1 = where1 + " and ciudad.id_ciudad = " + idCiudad;
			sql2 = sql2
					+ " join planificacion.oficina ofi on ofi.id_oficina = det.id_oficina "
					+ " join general.ciudad ciudad on ciudad.id_ciudad = ofi.id_ciudad";
			where2 = where2 + " and ciudad.id_ciudad = " + idCiudad;
		}
		if (idConfiguracionUoDet != null) {
			if (!sufc.validarInput(idConfiguracionUoDet.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			where1 = where1 + " and uo_det.id_configuracion_uo_det = "
					+ idConfiguracionUoDet;
			where2 = where2 + " and uo_det.id_configuracion_uo_det = "
					+ idConfiguracionUoDet;
		}
		if (idBarrio != null) {
			if (!sufc.validarInput(idBarrio.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			sql1 = sql1
					+ " join general.barrio barrio on barrio.id_barrio = ofi.id_barrio";
			where1 = where1 + " and barrio.id_barrio = " + idBarrio;
			sql2 = sql2
					+ " join general.barrio barrio on barrio.id_barrio = ofi.id_barrio";
			where2 = where2 + " and barrio.id_barrio = " + idBarrio;
		}
		if (idCpt != null) {
			if (!sufc.validarInput(idCpt.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			if (!concurso.getDatosEspecificosTipoConc().getDescripcion()
					.equalsIgnoreCase(CONCURSO_SIMPLIFICADO))
			sql1 = sql1
					+ " join planificacion.cpt cpt on cpt.id_cpt = det.id_cpt";
			where1 = where1 + " and cpt.id_cpt = " + idCpt;
			sql2 = sql2
					+ " join planificacion.cpt cpt on cpt.id_cpt = det.id_cpt";
			where2 = where2 + " and cpt.id_cpt = " + idCpt;
		}

		if (modalidadOcupacion != null && modalidadOcupacion.getValor() != null) {
			if (modalidadOcupacion.getValor().equals("PERMANENTE")) {
				where1 = where1 + " and det.permanente = true";
				where2 = where2 + " and det.permanente = true";
			}
			if (modalidadOcupacion.getValor().equals("CONTRATADO")) {
				where1 = where1 + " and det.contratado = true";
				where2 = where2 + " and det.contratado = true";
			}
		}
		if (denominacion != null && !denominacion.trim().isEmpty()) {
			if (!sufc.validarInput(denominacion.toString(),
					TiposDatos.STRING.getValor(), null))
				return;
			where1 += " and lower(det.descripcion) like '%"
					+ seguridadUtilFormController.caracterInvalido(denominacion
							.toLowerCase()) + "%'";
			where2 += " and lower(det.descripcion) like '%"
					+ seguridadUtilFormController.caracterInvalido(denominacion
							.toLowerCase()) + "%'";
		}
		where1 += " and det.activo=true";
		where2 += "  and puesto_det.activo=true ";
		cargarLista(sql1 + where1, sql2 + where2);

	}

	/**
	 * Metodo que setea todo el filtrado de los concursos sin agrupar
	 */
	public void searchAll() {
		configuracionUoDet = new ConfiguracionUoDet();
		idConfiguracionUoDet = null;
		idBarrio = null;
		idCiudad = null;
		idCpt = null;
		idDpto = null;
		modalidadOcupacion = null;
		codigoUnidOrgDep = null;
		denominacion = null;
		llenarListado1();
	}

	private String Sentencia1() {
		String sql = "select det.* from planificacion.planta_cargo_det det "
				+ "join planificacion.estado_cab cab on cab.id_estado_cab = det.id_estado_cab "
				+ "join planificacion.configuracion_uo_det uo_det "
				+ "on uo_det.id_configuracion_uo_det = det.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab  uo_cab  "
				+ "on uo_cab.id_configuracion_uo = uo_det.id_configuracion_uo";
		if (concurso.getDatosEspecificosTipoConc().getDescripcion()
				.equalsIgnoreCase(CONCURSO_SIMPLIFICADO))
			sql += " join planificacion.cpt cpt on cpt.id_cpt=det.id_cpt ";

		return sql;
	}

	private String Sentencia2() {
		String sql = "select puesto_det.* "
				+ "from seleccion.concurso_puesto_det puesto_det "
				+ "join planificacion.estado_det estado_det  "
				+ "on estado_det.id_estado_det = puesto_det.id_estado_det "
				+ "join planificacion.planta_cargo_det det "
				+ "on det.id_planta_cargo_det = puesto_det.id_planta_cargo_det "
				+ "join planificacion.configuracion_uo_det uo_det "
				+ "on uo_det.id_configuracion_uo_det = det.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab  uo_cab  "
				+ "on uo_cab.id_configuracion_uo = uo_det.id_configuracion_uo "
				+ "join seleccion.concurso concurso "
				+ "on concurso.id_concurso = puesto_det.id_concurso ";
		return sql;
	}

	/**
	 * Carga la lista de concursos no agrupados que fueron o no reservados
	 * 
	 * @param sql1
	 * @param sql2
	 */
	@SuppressWarnings("unchecked")
	private void cargarLista(String sql1, String sql2) {
		listaPlantaCargoDet = new ArrayList<PlantaCargoDet>();
		listaPuestosReservados = new ArrayList<ConcursoPuestoDet>();
		listaPlantaCargoDto = new ArrayList<PlantaCargoDetDTO>();
		listaPlantaCargoDet = em.createNativeQuery(sql1, PlantaCargoDet.class)
				.getResultList();
		listaPuestosReservados = em.createNativeQuery(sql2,
				ConcursoPuestoDet.class).getResultList();
		if (listaPuestosReservados.size() > 0) {
			cantReservados = listaPuestosReservados.size();
			for (ConcursoPuestoDet r : listaPuestosReservados) {
				PlantaCargoDetDTO dto = new PlantaCargoDetDTO();
				Long id = r.getPlantaCargoDet().getIdPlantaCargoDet();
				dto.setPlantaCargoDet(r.getPlantaCargoDet());
				dto.setReservar(true);
				listaPlantaCargoDto.add(dto);
			}
		} else {
			cantReservados = 0;
		}

		if (listaPlantaCargoDet.size() > 0) {
			for (PlantaCargoDet p : listaPlantaCargoDet) {
				Boolean esta = false;
				for (ConcursoPuestoDet r : listaPuestosReservados) {
					if (r.getPlantaCargoDet().equals(p))
						esta = true;
				}
				if (!esta) {

					PlantaCargoDetDTO dto = new PlantaCargoDetDTO();
					dto.setPlantaCargoDet(p);
					dto.setReservar(false);
					listaPlantaCargoDto.add(dto);

				}
			}
		}
	}

	/**
	 * SE AGREGO LA CONDICION DE ACTIVO DE LA INCIDENCIA 0000967 PARA LAS 2
	 * SENTENCIAS
	 */
	private void llenarListado1() {
		String sql1 = Sentencia1()
				+ " where lower(cab.descripcion) = 'concurso'  " + "and ("
				+ "		det.id_estado_det = (" + "				select est.id_estado_det "
				+ "				from planificacion.estado_det est "
				+ "				where lower(est.descripcion)='libre'" + "		) "
				+ "		or det.id_estado_det is null" + "	) "
				+ " and uo_cab.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " AND det.activo=true";
		/*
		 * Tipo de concurso = CONCURSO SIMPLIFICADO Se despliegan los puestos
		 * PERMANENTES o CONTRATADOS, con estado = LIBRE o NULO Los puestos
		 * deben ser de nivel 1
		 */
		if (concurso.getDatosEspecificosTipoConc().getDescripcion()
				.equalsIgnoreCase(CONCURSO_SIMPLIFICADO)) {
			sql1 += " and   cpt.nivel=1  and cpt.activo=true and (det.permanente is true or det.contratado is true) ";
		}

		
		String sql2 = Sentencia2()
				+ " where lower(estado_det.descripcion) = 'en reserva' "
				+ "and uo_cab.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " and concurso.id_concurso = " + concurso.getIdConcurso()
				+ " and puesto_det.activo=true";

		cargarLista(sql1, sql2);
	}

	/**
	 * Marca todos los concursos como reservados
	 */
	public void marcarTodos() {
		for (int i = 0; i < listaPlantaCargoDto.size(); i++) {
			PlantaCargoDetDTO p = new PlantaCargoDetDTO();
			p = listaPlantaCargoDto.get(i);
			if (!obtenerCategoria(p.getPlantaCargoDet())
					.equals("Sin Categoria"))
				p.setReservar(true);
			listaPlantaCargoDto.set(i, p);
		}
		cantReservados = listaPlantaCargoDto.size();
	}

	/**
	 * Desmarca todos los concursos que estaban reservados
	 */
	public void desmarcarTodos() {
		for (int i = 0; i < listaPlantaCargoDto.size(); i++) {
			PlantaCargoDetDTO p = new PlantaCargoDetDTO();
			p = listaPlantaCargoDto.get(i);
			p.setReservar(false);
			listaPlantaCargoDto.set(i, p);
		}
		cantReservados = 0;
	}

	/**
	 * Método que obtiene el codigo del puesto, concatenado con la unidad
	 * organizativa cabeza unidad organizativa dependiente, entidad, nivel,
	 * orden
	 * 
	 * @param det
	 * @return
	 */
	public String obtenerCodigo(PlantaCargoDet detail) {
		Long id = detail.getIdPlantaCargoDet();
		PlantaCargoDet det = new PlantaCargoDet();
		det = em.find(PlantaCargoDet.class, id);
		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = det.getConfiguracionUoDet();
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";

		}
		if (det.getContratado())
			code = code + "C";
		if (det.getPermanente())
			code = code + "P";
		code = code + det.getOrden();
		return code;
	}

	public String obtenerCodigoDetalle(ConfiguracionUoDet det) {
		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = det.getConfiguracionUoDet();
		String code = "";
		if (uoDet != null) {
			List<Integer> listCodes = obtenerListaCodigosDet(uoDet, null);
			for (Integer codigo : listCodes) {
				code += codigo + ".";
			}
		} else {
			code += code + det.getOrden();
			code = code + ".";
		}

		code = code.substring(0, code.length() - 1);
		return code;
	}

	private List<Integer> obtenerListaCodigosDet(ConfiguracionUoDet uoDet,
			List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);
		else {

			listCodigos.add(0, uoDet.getOrden());
		}
		return listCodigos;
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

	@SuppressWarnings("unchecked")
	public String obtenerCategoria(PlantaCargoDet det) {
		Referencias ref = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "MODELO");
		if (det != null) {
			if (det.getPermanente()) {
				String cad = "select pago.* from planificacion.puesto_concepto_pago pago "
						+ "where pago.obj_codigo = 111 "
						+ "and pago.id_planta_cargo_det = "
						+ det.getIdPlantaCargoDet()
						+ " and pago.activo is true "
						+ " and pago.estado = "
						+ ref.getValorNum() + " order by pago.estado";

				List<PuestoConceptoPago> lista = new ArrayList<PuestoConceptoPago>();
				lista = em.createNativeQuery(cad, PuestoConceptoPago.class)
						.getResultList();
				String resultado = "";
				if (lista.size() > 0) {
					resultado = lista.get(0).getCategoria();
					return resultado;
				}
			}
			if (det.getContratado()) {
				String cad = "select concepto.* "
						+ "from planificacion.puesto_concepto_pago concepto "
						+ "join planificacion.planta_cargo_det det "
						+ "on det.id_planta_cargo_det = concepto.id_planta_cargo_det "
						+ "where det.id_planta_cargo_det = "
						+ det.getIdPlantaCargoDet()
						+ " and concepto.estado = "
						+ ref.getIdReferencias() +" and concepto.activo is true"
						+ " order by concepto.estado";
				List<PuestoConceptoPago> lista = em.createNativeQuery(cad,
						PuestoConceptoPago.class).getResultList();
				if (lista.size() > 0) {
					String resultado = "";
					for (PuestoConceptoPago l : lista)
						resultado = resultado + l.getCategoria();
					return resultado;
				}

			}
		}
		return "Sin Categoria";
	}
	
	/**
	 * Obteniene la categoria correspondiente al puesto actual
	 * @param det
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String obtenerCategoriaDistintoModelo(PlantaCargoDet det) {
		Referencias ref = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "MODELO");
		if (det != null) {
			if (det.getPermanente()) {
				String cad = "select pago.* from planificacion.puesto_concepto_pago pago "
						+ "where pago.obj_codigo = 111 "
						+ "and pago.id_planta_cargo_det = "
						+ det.getIdPlantaCargoDet()
						+ " and pago.activo is true "
						+ " and pago.estado != "
						+ ref.getValorNum() + " order by pago.estado";

				List<PuestoConceptoPago> lista = new ArrayList<PuestoConceptoPago>();
				lista = em.createNativeQuery(cad, PuestoConceptoPago.class)
						.getResultList();
				String resultado = "";
				if (lista.size() > 0) {
					resultado = lista.get(0).getCategoria();
					return resultado;
				}
			}
			if (det.getContratado()) {
				String cad = "select concepto.* "
						+ "from planificacion.puesto_concepto_pago concepto "
						+ "join planificacion.planta_cargo_det det "
						+ "on det.id_planta_cargo_det = concepto.id_planta_cargo_det "
						+ "where det.id_planta_cargo_det = "
						+ det.getIdPlantaCargoDet()
						+ " and concepto.estado != "
						+ ref.getIdReferencias() +" and concepto.activo is true"
						+ " order by concepto.estado";
				List<PuestoConceptoPago> lista = em.createNativeQuery(cad,
						PuestoConceptoPago.class).getResultList();
				if (lista.size() > 0) {
					String resultado = "";
					for (PuestoConceptoPago l : lista)
						resultado = resultado + l.getCategoria();
					return resultado;
				}

			}
		}
		return "Sin Categoria";
	}

	public Boolean habilitarCategoria(PlantaCargoDet det) {
		if (det.getContratado() != null && det.getContratado())
			return false;
		Referencias referencias = new Referencias();
		if (!concurso.getDatosEspecificosTipoConc().getDescripcion()
				.equalsIgnoreCase(CONCURSO_INTERNO_INTERINSTITUCIONAL)) {
			if (concurso.getAdReferendum()) {
				referencias = referenciasUtilFormController.getReferencia(
						"ESTADOS_CATEGORIA", "PENDIENTE");
				String cad = "select pago.* from planificacion.puesto_concepto_pago pago "
						+ "where pago.obj_codigo = 111 "
						+ "and pago.id_planta_cargo_det = "
						+ det.getIdPlantaCargoDet()
						+ " and pago.estado = "
						+ referencias.getValorNum()
							+" and pago.activo is true";
				List<PuestoConceptoPago> lista = em.createNativeQuery(cad,
						PuestoConceptoPago.class).getResultList();
				if (lista.size() == 0)
					return true;
			}
			if (!concurso.getAdReferendum()) {
				referencias = referenciasUtilFormController.getReferencia(
						"ESTADOS_CATEGORIA", "RESERVADO");
				String cad = "select pago.* from planificacion.puesto_concepto_pago pago "
						+ "where pago.obj_codigo = 111 "
						+ "and pago.id_planta_cargo_det = "
						+ det.getIdPlantaCargoDet()
						+ " and pago.estado = "
						+ referencias.getValorNum()
							+" and pago.activo is true";
				List<PuestoConceptoPago> lista = em.createNativeQuery(cad,
						PuestoConceptoPago.class).getResultList();
				if (lista.size() == 0)
					return true;
			}
		}
		return false;

	}

	/**
	 * Combo Departamento
	 */
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentos();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
		idDpto = null;
		idCiudad = null;
		idBarrio = null;
	}

	private List<Departamento> getDepartamentos() {
		Long idPaisDir = buscarIdPais();
		if (idPaisDir != null) {
			departamentoList.getPais().setIdPais(idPaisDir);
			departamentoList.setOrder("descripcion");
			departamentoList.setMaxResults(null);
			return departamentoList.getResultList();
		}
		return new ArrayList<Departamento>();
	}

	/**
	 * Busca el id correspondiente a Paraguay
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Long buscarIdPais() {
		String cad = "select p.* from general.pais p where lower(p.descripcion) = 'paraguay'";
		List<Pais> lista = new ArrayList<Pais>();
		lista = em.createNativeQuery(cad, Pais.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0).getIdPais();
		else
			return null;
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

	/**
	 * Combo ciudad
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		idCiudad = null;
		idBarrio = null;
		updateBarrio();
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDpto != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDpto);
			ciudadList.setMaxResults(null);
			return ciudadList.getResultList();
		}
		return new ArrayList<Ciudad>();
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

	/**
	 * Combo Barrio
	 */
	public void updateBarrio() {
		List<Barrio> barList = getBarriosByCiudad();
		barrioSelecItem = new ArrayList<SelectItem>();
		buildBarrioSelectItem(barList);
		idBarrio = null;
	}

	private List<Barrio> getBarriosByCiudad() {
		if (idCiudad != null || idDpto != null) {
			if (idCiudad != null)
				barrioList.setIdCiudad(idCiudad);
			if (idDpto != null)
				barrioList.setIdDepartamento(idDpto);
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

	public String getUrlToPageSearch() {

		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=circuitoCSI/reservaDePuestos/ConcursoPuestoDetList&sinEntidadIdSinEntidad="
				+ sinEntidad.getIdSinEntidad()
				+ "&idNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad()
				+ "&idConfiguracionUoCab="
				+ configuracionUoCab.getIdConfiguracionUo();
	}

	/**
	 * Actualiza la lista de concursos no reservados
	 * 
	 * @param row
	 */
	@SuppressWarnings("unchecked")
	public void actualizarLista(Integer row) {
		PlantaCargoDetDTO dto = new PlantaCargoDetDTO();
		dto = listaPlantaCargoDto.get(row);
		if (dto.getReservar()) {
			statusMessages.clear();
			dto.setReservar(false);
			cantReservados--;
		} else {
			if (dto.getPlantaCargoDet().getPermanente() && concurso.getPcd()) {
				String cad = "select pago.* from planificacion.puesto_concepto_pago pago "
						+ "where pago.categoria like 'W%' "
						+ "and pago.id_planta_cargo_det = "
						+ dto.getPlantaCargoDet().getIdPlantaCargoDet();
				List<PuestoConceptoPago> listaPag = new ArrayList<PuestoConceptoPago>();
				listaPag = em.createNativeQuery(cad, PuestoConceptoPago.class)
						.getResultList();
				if (listaPag == null || listaPag.size() == 0) {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"El Puesto reservado no tiene la categoría para PcD");
				}
			}
			dto.setReservar(true);
			cantReservados++;
		}
		listaPlantaCargoDto.set(row, dto);
	}

	/**
	 * Método que busca la unidad organizativa dependiente de la unidad
	 * organizativa cabeza, la entidad el nivel y el codigo ingresado
	 */
	public void obtenerUnidadOrganizativaDep() {
		if (codigoUnidOrgDep != null) {
			try {
				String[] arrayCodigo = codigoUnidOrgDep.split("\\.");
				Integer orden = new Integer(arrayCodigo[0]);
				Integer tam = arrayCodigo.length;

				configuracionUoDet = new ConfiguracionUoDet();

				configuracionUoDet = buscarDetalle(configuracionUoCab, orden);

				ConfiguracionUoDet det = new ConfiguracionUoDet();
				if (tam == 1)
					det = null;
				for (int i = 1; i < arrayCodigo.length; i++) {
					Integer ord = new Integer(arrayCodigo[i]);
					det = buscarDetalle(configuracionUoDet, ord);
					configuracionUoDet = det;
				}
				if (configuracionUoDet == null)
					codigoUnidOrgDep = null;

			} catch (Exception e) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Ingrese un código válido");
			}

		}
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

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoDet padre,
			Integer orden) {
		String cad = "select det.* from planificacion.configuracion_uo_det det "
				+ " where det.id_configuracion_uo_det_padre = "
				+ padre.getIdConfiguracionUoDet() + " and det.orden = " + orden;
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

	private void refreshListaPuestoReservados() {
		String sql2 = Sentencia2()
				+ " where lower(estado_det.descripcion) = 'en reserva' "
				+ "and uo_cab.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " and concurso.id_concurso = " + concurso.getIdConcurso()
				+ " and puesto_det.activo=true";
		listaPuestosReservados = em.createNativeQuery(sql2,
				ConcursoPuestoDet.class).getResultList();
	}

	/**
	 * Metodo que guarda los datos en la bd
	 * 
	 * @return
	 */
	public String save() {
		try {

			if (obs != null && !obs.trim().isEmpty()) {
				concurso.setObservacionReserva(obs);
				concursoHome.setInstance(concurso);
				String res = concursoHome.update();
			}

			for (PlantaCargoDetDTO dto : listaPlantaCargoDto) {

				if (dto.getReservar()) {
					EstadoDet est = buscarEstado("en reserva");
					if (!existeEnDetalle(dto.getPlantaCargoDet(), est)) {
						// buscar

						ConcursoPuestoDet p = new ConcursoPuestoDet();
						p.setActivo(true);
						p.setFechaAlta(new Date());
						p.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						p.setNroOrden(2);
						p.setPlantaCargoDet(dto.getPlantaCargoDet());

						if (est != null)
							p.setEstadoDet(est);
						p.setConcurso(concursoHome.getInstance());
						em.persist(p);

						PlantaCargoDet planta = new PlantaCargoDet();
						planta = dto.getPlantaCargoDet();
						planta.setEstadoCab(est.getEstadoCab());
						planta.setEstadoDet(est);
						em.merge(planta);

					}
				}

				/* Incidencia 1079 */
				refreshListaPuestoReservados();
				/**/
				EstadoCab estadoCab = obtenerEstadosCabecera("VACANTE");
				if (esta(dto.getPlantaCargoDet())) {
					ConcursoPuestoDet p = new ConcursoPuestoDet();
					p = recuperarConcurso(dto.getPlantaCargoDet());

					if (!dto.getReservar()) {
						if (p.getConcursoPuestoAgr() != null) {
							statusMessages.clear();
							statusMessages.add(Severity.ERROR,
									"El puesto pertenece a un grupo");
							return null;
						}

						
						PlantaCargoDet planta = new PlantaCargoDet();
						planta = dto.getPlantaCargoDet();
						planta.setEstadoCab(estadoCab);
						planta.setEstadoDet(null);
						em.merge(planta);
						if (!concurso
								.getDatosEspecificosTipoConc()
								.getDescripcion()
								.equalsIgnoreCase(
										CONCURSO_INTERNO_INTERINSTITUCIONAL)
								&& planta.getPermanente())
							reponerCategoriasController.reponerCategorias(p,
									"PUESTO", "CONCURSO");
						em.remove(p);
						// listaPlantaCargoDto.remove(dto);
					}

				}

			}

			obs = null;
			em.flush();
			llenarListado1();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public EstadoCab obtenerEstadosCabecera(String cabDesc) {
		Query q = em
				.createQuery("Select EstadoCab from EstadoCab EstadoCab "
						+ "where EstadoCab.activo is true and EstadoCab.descripcion = '"
						+ cabDesc + "' ");
		List<EstadoCab> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Boolean existeEnDetalle(PlantaCargoDet planta, EstadoDet est) {
		String sql = "select det.* from "
				+ "seleccion.concurso_puesto_det det "
				+ "where det.id_planta_cargo_det = "
				+ planta.getIdPlantaCargoDet() + "and det.id_estado_det = "
				+ est.getIdEstadoDet();

		List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
		lista = em.createNativeQuery(sql, ConcursoPuestoDet.class)
				.getResultList();
		if (lista.size() > 0)
			return true;
		return false;
	}

	/**
	 * Busca el id correspondiente a un estado
	 * 
	 * @param estado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private EstadoDet buscarEstado(String estado) {
		String cad = "select det.* from planificacion.estado_cab cab "
				+ "join planificacion.estado_det det  "
				+ "on det.id_estado_cab = cab.id_estado_cab "
				+ "where lower(cab.descripcion)='concurso' "
				+ "and lower(det.descripcion)='" + estado + "'";
		List<EstadoDet> lista = new ArrayList<EstadoDet>();
		lista = em.createNativeQuery(cad, EstadoDet.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	private Boolean esta(PlantaCargoDet det) {
		for (ConcursoPuestoDet d : listaPuestosReservados) {
			if (d.getPlantaCargoDet().getIdPlantaCargoDet().toString()
					.equals(det.getIdPlantaCargoDet().toString()))
				return true;
		}
		return false;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private void buscarMonto(SinAnx sinAnx) {
		Integer cat_grupo = sinAnx.getCatGrupo();
		PlantaCargoDet plantaCargoDet = new PlantaCargoDet();

		plantaCargoDet = em.find(PlantaCargoDet.class, idPlantaCargo);

		if (plantaCargoDet != null && plantaCargoDet.getContratado()) {
			monto = null;

		} else if (plantaCargoDet != null && plantaCargoDet.getPermanente()) {
			String valor = "" + cat_grupo;
			String sql = "select * from sinarh.sin_categoria cat "
					+ "where cat.ctg_codigo = '" + sinAnx.getCtgCodigo() + "'"
					+ " and cat_grupo = " + cat_grupo
					+ " and cat.vrs_codigo = '" + valor + "'";
			List<SinCategoria> listaMonto = new ArrayList<SinCategoria>();
			listaMonto = em.createNativeQuery(sql, SinCategoria.class)
					.getResultList();
			if (listaMonto.size() > 0) {
				Date fechaActual = new Date();
				Integer mes = fechaActual.getMonth();
				if (mes == 0)
					monto = listaMonto.get(0).getCtgValor1();
				if (mes == 1)
					monto = listaMonto.get(0).getCtgValor2();
				if (mes == 2)
					monto = listaMonto.get(0).getCtgValor3();
				if (mes == 3)
					monto = listaMonto.get(0).getCtgValor4();
				if (mes == 4)
					monto = listaMonto.get(0).getCtgValor5();
				if (mes == 5)
					monto = listaMonto.get(0).getCtgValor6();
				if (mes == 6)
					monto = listaMonto.get(0).getCtgValor7();
				if (mes == 7)
					monto = listaMonto.get(0).getCtgValor8();
				if (mes == 8)
					monto = listaMonto.get(0).getCtgValor9();
				if (mes == 9)
					monto = listaMonto.get(0).getCtgValor10();
				if (mes == 10)
					monto = listaMonto.get(0).getCtgValor11();
				if (mes == 11)
					monto = listaMonto.get(0).getCtgValor12();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void actualizarDatos() {
		SinAnx anx = new SinAnx();
		anx = em.find(SinAnx.class, idAnx);

		Cpt cpt = new Cpt();
		
		PlantaCargoDet plantaCargoDet = new PlantaCargoDet();

		plantaCargoDet = em.find(PlantaCargoDet.class, idPlantaCargo);
		String mensaje = "";
		Long id = concurso.getIdConcurso();
		if (plantaCargoDet.getPermanente() && concurso.getPcd()) {
			String cad = "select pago.* from planificacion.puesto_concepto_pago pago "
					+ "where pago.categoria like 'W%' "
					+ "and pago.id_planta_cargo_det = " + idPlantaCargo;
			List<PuestoConceptoPago> listaPag = new ArrayList<PuestoConceptoPago>();
			listaPag = em.createNativeQuery(cad, PuestoConceptoPago.class)
					.getResultList();
			if (listaPag == null || listaPag.size() == 0)

				mensaje = "La categoría seleccionada no es para PcD";

		}
		Long idC = null;
		if (plantaCargoDet.getCpt() != null)
			idC = plantaCargoDet.getCpt().getIdCpt();

		if (idC == null) {
			if (!mensaje.trim().isEmpty())
				mensaje = mensaje
						+ ", "
						+ SeamResourceBundle.getBundle().getString(
								"CU120_link3_msg");
			else
				mensaje = mensaje
						+ SeamResourceBundle.getBundle().getString(
								"CU120_link3_msg");
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, mensaje);
			mensaje = "";
			return;
		}

		cpt = em.find(Cpt.class, idC);
		String cadCateg = "select * from planificacion.categoria_cpt cat "
				+ "where cat.id_cpt = " + cpt.getIdCpt()
				+ " and cat.categoria = '" + anx.getCtgCodigo() + "'";
		List<CategoriaCpt> listaCatCpt = new ArrayList<CategoriaCpt>();
		listaCatCpt = em.createNativeQuery(cadCateg, CategoriaCpt.class)
				.getResultList();

		buscarMonto(anx);
		try {
			PuestoConceptoPago concepto = new PuestoConceptoPago();

			concepto.setObjCodigo(111);
			concepto.setCategoria(anx.getCtgCodigo());
			Date fecha = new Date();
			Integer anho = fecha.getYear() + 1900;
			concepto.setAnho(anho);
			if (monto != null)
				concepto.setMonto(monto.intValue());
			concepto.setPlantaCargoDet(plantaCargoDet);
			concepto.setFechaAlta(fecha);
			concepto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			Referencias refReservado = referenciasUtilFormController
					.getReferencia("ESTADOS_CATEGORIA", "RESERVADO");
			Referencias refPendiente = referenciasUtilFormController
					.getReferencia("ESTADOS_CATEGORIA", "PENDIENTE");
			if (!concurso.getAdReferendum() && anx.getCantDisponible() > 0) {
				concepto.setDisponible(true);
				concepto.setActivo(true);
				if (refReservado != null)
					concepto.setEstado(refReservado.getValorNum());
				em.persist(concepto);
				anx.setCantDisponible(anx.getCantDisponible() - 1);
				em.merge(anx);
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
			} else if (concurso.getAdReferendum()) {
				concepto.setDisponible(true);
				concepto.setActivo(true);
				if (refPendiente != null)
					concepto.setEstado(refPendiente.getValorNum());
				em.persist(concepto);
				em.flush();
				statusMessages.clear();
				String msg = SeamResourceBundle.getBundle().getString(
						"GENERICO_MSG");

				
				statusMessages.add(Severity.INFO, msg);

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		if (listaCatCpt.size() == 0) {
			String msg = SeamResourceBundle.getBundle().getString(
					"CU120_link3_msg");
			if (!mensaje.trim().isEmpty())
				msg += ", " + mensaje;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, msg);

		}
	}

	/**
	 * Verifica si el concurso ya finalizo o no
	 * 
	 * @return
	 */
	public boolean concursoFinalizado() {

		if (referenciasUtilFormController == null)
			referenciasUtilFormController = (ReferenciasUtilFormController) Component
					.getInstance(ReferenciasUtilFormController.class, true);

		Referencias ref = referenciasUtilFormController.getReferencia(
				"ESTADOS_CONCURSO", "CERRADO GRUPOS");
		if (ref != null && concurso.getEstado() != null) {
			if (concurso.getEstado().intValue() == ref.getValorNum().intValue())
				return true;
		}

		return false;
	}

	private ConcursoPuestoDet recuperarConcurso(PlantaCargoDet det) {
		for (ConcursoPuestoDet d : listaPuestosReservados) {
			if (d.getPlantaCargoDet().equals(det))
				return d;
		}
		return null;
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

	public List<SelectItem> getBarrioSelecItem() {
		return barrioSelecItem;
	}

	public void setBarrioSelecItem(List<SelectItem> barrioSelecItem) {
		this.barrioSelecItem = barrioSelecItem;
	}

	public List<PlantaCargoDet> getListaPlantaCargoDet() {
		return listaPlantaCargoDet;
	}

	public void setListaPlantaCargoDet(List<PlantaCargoDet> listaPlantaCargoDet) {
		this.listaPlantaCargoDet = listaPlantaCargoDet;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public Long getIdDpto() {
		return idDpto;
	}

	public void setIdDpto(Long idDpto) {
		this.idDpto = idDpto;
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

	public Long getIdCpt() {
		return idCpt;
	}

	public void setIdCpt(Long idCpt) {
		this.idCpt = idCpt;
	}

	public ModalidadOcupacion getModalidadOcupacion() {
		return modalidadOcupacion;
	}

	public void setModalidadOcupacion(ModalidadOcupacion modalidadOcupacion) {
		this.modalidadOcupacion = modalidadOcupacion;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public List<PlantaCargoDetDTO> getListaPlantaCargoDto() {
		return listaPlantaCargoDto;
	}

	public void setListaPlantaCargoDto(
			List<PlantaCargoDetDTO> listaPlantaCargoDto) {
		this.listaPlantaCargoDto = listaPlantaCargoDto;
	}

	public List<ConcursoPuestoDet> getListaPuestosReservados() {
		return listaPuestosReservados;
	}

	public void setListaPuestosReservados(
			List<ConcursoPuestoDet> listaPuestosReservados) {
		this.listaPuestosReservados = listaPuestosReservados;
	}

	public Integer getCantReservados() {
		return cantReservados;
	}

	public void setCantReservados(Integer cantReservados) {
		this.cantReservados = cantReservados;
	}

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	public Long getIdConfiguracionUoDet() {
		return idConfiguracionUoDet;
	}

	public void setIdConfiguracionUoDet(Long idConfiguracionUoDet) {
		this.idConfiguracionUoDet = idConfiguracionUoDet;
		if (idConfiguracionUoDet != null) {
			configuracionUoDet = em.find(ConfiguracionUoDet.class,
					idConfiguracionUoDet);
			codigoUnidOrgDep = obtenerCodigoDetalle(configuracionUoDet);
		}
	}

	public String getCodigoUnidOrgDep() {
		return codigoUnidOrgDep;
	}

	public void setCodigoUnidOrgDep(String codigoUnidOrgDep) {
		this.codigoUnidOrgDep = codigoUnidOrgDep;
	}

	public List<ConcursoPuestoAgrDTO> getListaPuestosAgrupados() {
		return listaPuestosAgrupados;
	}

	public void setListaPuestosAgrupados(
			List<ConcursoPuestoAgrDTO> listaPuestosAgrupados) {
		this.listaPuestosAgrupados = listaPuestosAgrupados;
	}

	public BigDecimal getMonto() {
		return monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public Long getIdPlantaCargo() {
		return idPlantaCargo;
	}

	public void setIdPlantaCargo(Long idPlantaCargo) {
		this.idPlantaCargo = idPlantaCargo;
	}

	public Long getIdAnx() {
		return idAnx;
	}

	public void setIdAnx(Long idAnx) {
		this.idAnx = idAnx;
	}

	public Boolean getIniciado() {
		return iniciado;
	}

	public void setIniciado(Boolean iniciado) {
		this.iniciado = iniciado;
	}

	public String getFromConcurso() {
		if (fromConcurso == null || "".equals(fromConcurso))
			return "/home.xhtml";

		if (fromConcurso.contains(".seam")) {
			if (!fromConcurso.startsWith("/"))
				return "/" + fromConcurso;
			else
				return fromConcurso;
		}

		if (fromConcurso.contains("?")) {
			if (!fromConcurso.startsWith("/"))
				return "/"
						+ fromConcurso.substring(0, fromConcurso.indexOf("?"))
						+ ".seam"
						+ fromConcurso.substring(fromConcurso.indexOf("?"),
								fromConcurso.length());
			else
				return fromConcurso.substring(0, fromConcurso.indexOf("?"))
						+ ".seam"
						+ fromConcurso.substring(fromConcurso.indexOf("?"),
								fromConcurso.length());
		}

		if (!fromConcurso.contains(".xhtml")) {
			if (!fromConcurso.startsWith("/"))
				return "/" + fromConcurso + ".xhtml";
			else
				return fromConcurso + ".xhtml";
		}

		return fromConcurso;
	}

	public void setFromConcurso(String fromConcurso) {
		this.fromConcurso = fromConcurso;
	}

	public Long getIdAgr() {
		return idAgr;
	}

	public void setIdAgr(Long idAgr) {
		this.idAgr = idAgr;
	}

	public String getDenominacion() {
		return denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}

	public Integer getNroActividad() {
		return nroActividad;
	}

	public void setNroActividad(Integer nroActividad) {
		this.nroActividad = nroActividad;
	}


}
