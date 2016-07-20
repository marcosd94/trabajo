package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.MovUsuariosTarea;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinCategoria;
import py.com.excelsis.sicca.entity.SinCategoriaContratados;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.SinObj;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.SinObjList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("conceptoPagoFormController")
public class ConceptoPagoFormController implements Serializable {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinObjList sinObjList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SinarhUtiles sinarhUtiles;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private SinAnx sinAnx;
	private SinCategoriaContratados sinCategoriaContratados;
	private Integer codigoObj;
	private String valorObj;
	private String codigoCategoria;
	private String categoria;
	private String msgStock;
	private Integer monto;
	private Long idSinAnx;
	private Long idSinCategoria;
	private Boolean habilitar = true;

	private Boolean habilitarCategoria;

	private List<GrupoConceptoPago> listaGrupoPago = new ArrayList<GrupoConceptoPago>();
	private List<GrupoConceptoPago> listaGrupoPagoAux = new ArrayList<GrupoConceptoPago>();
	private SeguridadUtilFormController seguridadUtilFormController;
	private static String CONCURSO_INTERNO_INTERINSTITUCIONAL = "CONCURSO INTERNO DE OPOSICION INTER INSTITUCIONAL";

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

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		concurso = new Concurso();
		msgStock = null;
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = concursoPuestoAgr.getConcurso();
		if (habilitarCategoria == null)
			habilitarCategoria = false;
		/* Incidencia 1014 */
		validarOee();
		/**/

		buscarDatosAsociadosUsuario();
		if (listaGrupoPago.size() == 0 && idSinAnx == null
				&& idSinCategoria == null)
			buscarDatos();
		else {
			idSinAnx = null;
			idSinCategoria = null;
		}

		if (esContratadoOpermanente(concursoPuestoAgr.getIdConcursoPuestoAgr())
				.equalsIgnoreCase("PERMANENTE")
				&& !concurso.getDatosEspecificosTipoConc().getDescripcion()
						.equalsIgnoreCase(CONCURSO_INTERNO_INTERINSTITUCIONAL)) {
			if (esPrimeraEntrada()) {
				// /copia
				PlantaCargoDet planta = new PlantaCargoDet();
				planta = obtenerPlantaCargoDet(concursoPuestoAgr
						.getIdConcursoPuestoAgr());
				if (planta != null) {
					PuestoConceptoPago pago = new PuestoConceptoPago();
					pago = obtenerPuestoConceptoPago(planta);
					if (pago != null)
						insertarPrimeraEntrada(pago);
				}
			}
		}
		habilitarPantalla();
	}

	private void habilitarPantalla() {
		habilitar = false;
		if (concursoPuestoAgr.getHomologar() != null
				&& !concursoPuestoAgr.getHomologar()
				&& concursoPuestoAgr.getHomologacionPerfilMatriz() != null)
			habilitar = false;
		else {
			Referencias refIniciado = new Referencias();
			refIniciado = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "INICIADO CIRCUITO");
			Referencias refPendiente = new Referencias();
			refPendiente = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "PENDIENTE REVISION");
			Referencias refAjuste = new Referencias();
			refAjuste = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "AJUSTE PUBLICACION");
			if (concursoPuestoAgr.getEstado().intValue() == refIniciado
					.getValorNum().intValue()
					|| concursoPuestoAgr.getEstado().intValue() == refPendiente
							.getValorNum().intValue()
					|| concursoPuestoAgr.getEstado().intValue() == refAjuste
							.getValorNum().intValue())
				habilitar = true;
			else
				habilitar = false;
		}

	}

	/**
	 * En caso de que el puesto sea permanente y sea la primera entrada a esta
	 * pantalla se ejecuta este metodo
	 * 
	 * @param pago
	 */
	private void insertarPrimeraEntrada(PuestoConceptoPago pago) {
		listaGrupoPago = new ArrayList<GrupoConceptoPago>();
		listaGrupoPagoAux = new ArrayList<GrupoConceptoPago>();
		Referencias referencias = new Referencias();
		if (!concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "RESERVADO");
		if (concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "PENDIENTE");
		try {
			GrupoConceptoPago grupoConceptoPago = new GrupoConceptoPago();
			grupoConceptoPago.setAnho(pago.getAnho());
			grupoConceptoPago.setCategoria(pago.getCategoria());
			grupoConceptoPago.setConcursoPuestoAgr(concursoPuestoAgr);
			grupoConceptoPago.setEstado(referencias.getValorNum());
			grupoConceptoPago.setFechaAlta(new Date());
			grupoConceptoPago.setMonto(pago.getMonto());
			grupoConceptoPago.setObjCodigo(pago.getObjCodigo());
			grupoConceptoPago.setTipo("GRUPO");
			grupoConceptoPago.setActivo(true);
			grupoConceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());

			em.persist(grupoConceptoPago);
			em.flush();
			listaGrupoPago.add(grupoConceptoPago);
			listaGrupoPagoAux.add(grupoConceptoPago);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obtiene la PlantaCargoDet correspondiente al grupo
	 * 
	 * @return
	 */
	public PlantaCargoDet obtenerPlantaCargoDet(Long id) {
		Query q = em
				.createQuery("select det from ConcursoPuestoDet det "
						+ " where det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo and det.activo is true");
		q.setParameter("idGrupo", id);
		List<ConcursoPuestoDet> listaDet = q.getResultList();
		if (listaDet.isEmpty())
			return null;
		return listaDet.get(0).getPlantaCargoDet();
	}

	/**
	 * Obtiene el PuestoConceptoPago correspondiente al puesto
	 * 
	 * @param planta
	 * @return
	 */
	private PuestoConceptoPago obtenerPuestoConceptoPago(PlantaCargoDet planta) {

		Referencias referencias = new Referencias();
		if (!concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA", "RESERVADO");
		if (concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA", "PENDIENTE");
		Query q = em
				.createQuery("select concepto from PuestoConceptoPago concepto "
						+ " where concepto.plantaCargoDet.idPlantaCargoDet = :idPuesto and concepto.estado = :estado");
		q.setParameter("idPuesto", planta.getIdPlantaCargoDet());
		q.setParameter("estado", referencias.getValorNum());
		List<PuestoConceptoPago> lista = q.getResultList();
		if (lista.isEmpty())
			return null;
		return lista.get(0);
	}

	/**
	 * Verifica si es la primera entrada a la pagina
	 * 
	 * @return
	 */
	private boolean esPrimeraEntrada() {
		Referencias referencias = new Referencias();
		if (!concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "RESERVADO");
		if (concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "PENDIENTE");
		Query q = em
				.createQuery("select pago from GrupoConceptoPago pago "
						+ " where pago.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo and pago.estado = :estado");
		q.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr());
		q.setParameter("estado", referencias.getValorNum());
		List<GrupoConceptoPago> lista = q.getResultList();
		if (lista.isEmpty())
			return true;
		else
			return false;

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

	@SuppressWarnings("unchecked")
	public void findObj() {
		habilitarCategoria = false;
		if (codigoObj != null) {

			List<Referencias> ref = em
					.createNativeQuery(
							"select * from seleccion.referencias "
									+ "where dominio like 'OBJETOS_GASTO_CONT' and valor_num= "
									+ codigoObj, Referencias.class)
					.getResultList();

			List<SinAnx> sinanx = em.createNativeQuery(
					"select * from sinarh.sin_anx " + "where obj_codigo= "
							+ codigoObj, SinAnx.class).getResultList();

			if (esContratadoOpermanente(
					concursoPuestoAgr.getIdConcursoPuestoAgr())
					.equalsIgnoreCase("CONTRATADO")) {

				if (ref != null && ref.size() > 0) {
					sinObjList.setCod(codigoObj);
					List<SinObj> lista = new ArrayList<SinObj>();
					lista = sinObjList.listaResultados();
					if (lista.size() > 0)
						valorObj = lista.get(0).getObjNombre();
					else
						valorObj = null;
					codigoCategoria = null;
					categoria = null;
					monto = null;
				} else {// MODIFICADO RV
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"El codigo de obj debe estar entre 140 y 149");
					return;
				}
			} else {
				if (sinanx != null && sinanx.size() > 0) {
					habilitarCategoria = true;
				}

				if (ref != null && ref.size() == 0 && codigoObj != 111) {
					sinObjList.setCod(codigoObj);
					List<SinObj> lista = new ArrayList<SinObj>();
					lista = sinObjList.listaResultados();
					if (lista.size() > 0)
						valorObj = lista.get(0).getObjNombre();
					else
						valorObj = null;
					codigoCategoria = null;
					categoria = null;
					monto = null;
				} else {// MODIFICADO RV
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"El codigo de obj no debe estar entre 140 y 149; y no debe ser 111");
					return;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void findCategoria() {
		if (codigoObj == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese el Código OBJ.");
			return;
		}
		String tipoPuesto = esContratadoOpermanente(concursoPuestoAgr
				.getIdConcursoPuestoAgr());
		if (obtenerObjeto(tipoPuesto, codigoObj).isEmpty()) {
			if (tipoPuesto.equalsIgnoreCase("CONTRATADO")) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"El objeto de gasto ingresado no es un objeto de gasto definido para puestos contratados");
				return;
			}

			if (tipoPuesto.equalsIgnoreCase("PERMANENTE")) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"El objeto de gasto ingresado no es un objeto de gasto definido para puestos permanentes");
				return;
			}
		}
		if (codigoCategoria != null) {
			if (esContratadoOpermanente(
					concursoPuestoAgr.getIdConcursoPuestoAgr())
					.equalsIgnoreCase("PERMANENTE")) {

				try {

					List<SinAnx> lista = sinarhUtiles.obtenerListaSinAnx(null,
							new Integer(50), codigoObj, codigoCategoria, null);
					if (lista.isEmpty()) {
						statusMessages.clear();
						statusMessages.add(Severity.ERROR,
								"No existe el Objeto de Gasto en el SINARH");
						return;
					}
					if (lista.get(0).getCantDisponible().intValue() < cantidadPuestosActivos()) {
						statusMessages.clear();
						statusMessages
								.add(Severity.ERROR,
										"No existe stock suficiente en SINARH. Verifique");
						return;
					}
					categoria = lista.get(0).getAnxDescrip();

					buscarMonto(lista.get(0).getCatGrupo());

				} catch (Exception e) {
					e.printStackTrace();
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"Código de sinarh no válido. Verifique.");
					return;
				}
			} else {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Debe asignar el código de sinarh al OEE antes de realizar esta acción. Verifique.");
				return;
			}
		}
		if (esContratadoOpermanente(concursoPuestoAgr.getIdConcursoPuestoAgr())
				.equalsIgnoreCase("CONTRATADO")) {
			SinCategoriaContratados sinCategoriaContratados = new SinCategoriaContratados();
			sinCategoriaContratados = existeCategoriaContratados(codigoObj);
			if (sinCategoriaContratados == null) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"No existe el Objeto de Gasto en el SINARH. Verifique.");
				return;
			}
			categoria = sinCategoriaContratados.getCtgDescrip();
		}

	}

	/**
	 * En caso de que el puesto sea contratado se obtiene la categoria en este
	 * metodo
	 * 
	 * @return
	 */
	public SinCategoriaContratados existeCategoriaContratados(Integer cod) {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		BigDecimal an = new BigDecimal(anho.intValue());
		BigDecimal codActual = new BigDecimal(cod.intValue());
		Query q = em
				.createQuery("select sinCat from SinCategoriaContratados sinCat "
						+ " where sinCat.aniAniopre = :anio and sinCat.objCodigo = :cod ");
		q.setParameter("anio", an);
		q.setParameter("cod", codActual);
		List<SinCategoriaContratados> lista = q.getResultList();
		if (lista.isEmpty())
			return null;
		return lista.get(0);
	}

	/**
	 * Cuenta los puestos activos para el grupo actual
	 * 
	 * @return
	 */
	private Integer cantidadPuestosActivos() {
		Query q = em
				.createQuery("select det from ConcursoPuestoDet det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<ConcursoPuestoDet> lista = q.getResultList();
		if (lista.isEmpty())
			return new Integer(0);
		else
			return lista.size();

	}

	private List<ConcursoPuestoDet> puestosActivos() {
		Query q = em
				.createQuery("select det from ConcursoPuestoDet det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr());
		return q.getResultList();

	}

	/**
	 * Busca el monto para la categoria de grupo actual
	 * 
	 * @param cat_grupo
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	private void buscarMonto(Integer cat_grupo) {
		String sql = "select cargo_det.* from "
				+ "planificacion.planta_cargo_det cargo_det "
				+ "join seleccion.concurso_puesto_det concurso_det "
				+ "on concurso_det.id_planta_cargo_det = cargo_det.id_planta_cargo_det "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = concurso_det.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		List<PlantaCargoDet> listaCargoDet = new ArrayList<PlantaCargoDet>();
		listaCargoDet = em.createNativeQuery(sql, PlantaCargoDet.class)
				.getResultList();
		if (listaCargoDet.size() > 0) {
			PlantaCargoDet planta = new PlantaCargoDet();
			planta = listaCargoDet.get(0);
			if (planta.getContratado()) {
				monto = null;

			} else if (planta.getPermanente()) {
				String valor = "" + cat_grupo;
				String sql2 = "select * from sinarh.sin_categoria cat "
						+ "where cat.ctg_codigo = '" + codigoCategoria + "'"
						+ " and cat_grupo = " + cat_grupo
						+ " and cat.vrs_codigo = '" + valor + "'";
				List<SinCategoria> listaMonto = new ArrayList<SinCategoria>();
				listaMonto = em.createNativeQuery(sql2, SinCategoria.class)
						.getResultList();
				if (listaMonto.size() > 0) {
					Date fechaActual = new Date();
					Integer mes = fechaActual.getMonth();
					if (mes == 0)
						monto = listaMonto.get(0).getCtgValor1().intValue();
					if (mes == 1)
						monto = listaMonto.get(0).getCtgValor2().intValue();
					if (mes == 2)
						monto = listaMonto.get(0).getCtgValor3().intValue();
					if (mes == 3)
						monto = listaMonto.get(0).getCtgValor4().intValue();
					if (mes == 4)
						monto = listaMonto.get(0).getCtgValor5().intValue();
					if (mes == 5)
						monto = listaMonto.get(0).getCtgValor6().intValue();
					if (mes == 6)
						monto = listaMonto.get(0).getCtgValor7().intValue();
					if (mes == 7)
						monto = listaMonto.get(0).getCtgValor8().intValue();
					if (mes == 8)
						monto = listaMonto.get(0).getCtgValor9().intValue();
					if (mes == 9)
						monto = listaMonto.get(0).getCtgValor10().intValue();
					if (mes == 10)
						monto = listaMonto.get(0).getCtgValor11().intValue();
					if (mes == 11)
						monto = listaMonto.get(0).getCtgValor12().intValue();
				}
			}
		}
	}

	public String getUrlToPageCategoria() {
		if (codigoObj == null) {
			statusMessages.add(Severity.ERROR,
					"El campo Código OBJ es requerido");
			return null;
		}
		String url = "/seleccion/asignarCategoria/categoriaPorGrupo/AsignarCategoriaGrupo.xhtml?from=seleccion/basesCondiciones/conceptosPago/ConceptosPagoEdit&idConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ "&objeto="
				+ codigoObj
				+ "&cadena="
				+ esContratadoOpermanente(concursoPuestoAgr
						.getIdConcursoPuestoAgr());

		return url;
	}

	/**
	 * Verifica si el puesto es contratado o permanente
	 * 
	 * @return
	 */
	public String esContratadoOpermanente(Long id) {
		Query q = em
				.createQuery("select det from ConcursoPuestoDet det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", id);
		List<ConcursoPuestoDet> lista = q.getResultList();
		if (!lista.isEmpty()) {
			if (lista.get(0).getPlantaCargoDet().getContratado())
				return "CONTRATADO";
			if (lista.get(0).getPlantaCargoDet().getPermanente())
				return "PERMANENTE";
		}
		return null;
	}

	private Boolean existeRegistro() {
		for (GrupoConceptoPago p : listaGrupoPago) {
			if (p.getObjCodigo() == codigoObj)
				return true;
		}
		return false;
	}

	/**
	 * Valida si el registro puede agregarse a la lista y lo agrega si es
	 * posible
	 */
	@SuppressWarnings("deprecation")
	public void agregarLista() {

		if (codigoObj == null || monto == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese los campos requeridos");
			return;
		}
		if (codigoObj == 111) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"No se puede agregar objetos de gasto 111");
			return;
		}
		if (existeRegistro()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Ya existe el Objeto de Gasto que desea agregar");
			return;
		}
		String tipoPuesto = esContratadoOpermanente(concursoPuestoAgr
				.getIdConcursoPuestoAgr());
		if (!obtenerObjeto(tipoPuesto, codigoObj).isEmpty()) {
			if (tipoPuesto.equalsIgnoreCase("PERMANENTE")) {
				String ms = validarParaAgregar(tipoPuesto);
				if (ms != null) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, ms);
					return;
				}

			}
			if (tipoPuesto.equalsIgnoreCase("CONTRATADO")) {
				SinCategoriaContratados sinCategoriaContratados = new SinCategoriaContratados();
				sinCategoriaContratados = existeCategoriaContratados(codigoObj);
				if (sinCategoriaContratados == null) {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"No existe el Objeto de Gasto en el SINARH. Verifique.");
					return;
				}
			}
			GrupoConceptoPago concepto = new GrupoConceptoPago();
			if (codigoObj != null)
				concepto.setObjCodigo(codigoObj);
			if (codigoCategoria != null && !codigoCategoria.trim().isEmpty())
				concepto.setCategoria(codigoCategoria);
			Date fecha = new Date();
			Integer anho = fecha.getYear() + 1900;
			concepto.setAnho(anho);
			if (monto != null)
				concepto.setMonto(monto);
			concepto.setConcursoPuestoAgr(concursoPuestoAgr);
			concepto.setFechaAlta(fecha);
			concepto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			concepto.setTipo("GRUPO");
			concepto.setActivo(true);
			listaGrupoPago.add(concepto);
		} else {
			if (tipoPuesto.equalsIgnoreCase("CONTRATADO")) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"El objeto de gasto ingresado no es un objeto de gasto definido para puestos contratados");
				return;
			}

			if (tipoPuesto.equalsIgnoreCase("PERMANENTE")) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"El objeto de gasto ingresado no es un objeto de gasto definido para puestos permanentes");
				return;
			}
		}
		habilitarCategoria = false;
		codigoCategoria = null;
		codigoObj = null;
		valorObj = null;
		categoria = null;
		monto = null;
	}

	public List<Referencias> obtenerObjeto(String tipo, Integer obj) {
		String query = "select referencia from Referencias referencia ";
		if (tipo.equalsIgnoreCase("PERMANENTE"))
			query += "where referencia.dominio = 'OBJETOS_GASTO_PERM'";
		if (tipo.equalsIgnoreCase("CONTRATADO"))
			query += "where referencia.dominio = 'OBJETOS_GASTO_CONT'";
		query += " and referencia.valorNum = " + obj;
		Query q = em.createQuery(query);
		return q.getResultList();
	}

	private String validarParaAgregar(String tipo) {
		if (tipo.equalsIgnoreCase("PERMANENTE") && categoria != null
				&& !categoria.trim().isEmpty()) {

			List<SinAnx> listaAnx = sinarhUtiles.obtenerListaSinAnx(null,
					new Integer(50), codigoObj, categoria, null);
			if (!listaAnx.isEmpty()) {
				Integer cantPuestosAct = cantidadPuestosActivos();
				Integer total = new Integer(0);
				for (SinAnx a : listaAnx)
					total = total + a.getCantDisponible();
				if (total.intValue() < cantPuestosAct)
					return "No existe stock suficiente en SINARH";
			} else
				return "La categoria ingresada no existe en SINARH";
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void buscarDatos() {
		String sql = "select pag.* from seleccion.grupo_concepto_pago pag "
				+ "where pag.activo is true and pag.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		listaGrupoPago = new ArrayList<GrupoConceptoPago>();
		listaGrupoPagoAux = new ArrayList<GrupoConceptoPago>();
		listaGrupoPago = em.createNativeQuery(sql, GrupoConceptoPago.class)
				.getResultList();
		if (listaGrupoPago.size() > 0)
			listaGrupoPagoAux.addAll(listaGrupoPago);
		codigoObj = null;
		valorObj = null;
		codigoCategoria = null;
		categoria = null;
		monto = null;
	}

	public void eliminarLista(Integer row) {
		GrupoConceptoPago concepto = new GrupoConceptoPago();
		concepto = listaGrupoPago.get(row);
		listaGrupoPago.remove(concepto);
	}

	/**
	 * Guarda el registro en caso de que el puesto sea permanente y sin
	 * categoria
	 * 
	 * @param pago
	 */
	public void guardarPermanenteSinCategoria(GrupoConceptoPago pago) {
		Referencias referenciaGrupo = new Referencias();
		referenciaGrupo = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA_GRUPO", "RESERVADO");
		Referencias referenciaPuesto = new Referencias();
		referenciaPuesto = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "RESERVADO");
		pago.setEstado(referenciaGrupo.getValorNum());
		pago.setTipo("GRUPO");
		em.persist(pago);

		PuestoConceptoPago puestoPago = new PuestoConceptoPago();
		puestoPago.setActivo(true);
		puestoPago.setAnho(pago.getAnho());
		puestoPago.setEstado(referenciaPuesto.getValorNum());
		puestoPago.setFechaAlta(new Date());
		puestoPago.setMonto(pago.getMonto());
		puestoPago.setObjCodigo(pago.getObjCodigo());
		puestoPago.setCategoria(pago.getCategoria());
		puestoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		PlantaCargoDet planta = new PlantaCargoDet();
		planta = obtenerPlantaCargoDet(concursoPuestoAgr
				.getIdConcursoPuestoAgr());
		puestoPago.setPlantaCargoDet(planta);
		em.persist(puestoPago);

	}

	/**
	 * Guarda el registro en caso de que el puesto sea permanente y cuente con
	 * categoria
	 * 
	 * @param pago
	 */
	public void guardarPermanenteConCategoria(GrupoConceptoPago pago) {
		if (concurso.getAdReferendum() != null && !concurso.getAdReferendum()) {
			Referencias referenciaGrupo = new Referencias();
			referenciaGrupo = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "RESERVADO");
			Referencias referenciaPuesto = new Referencias();
			referenciaPuesto = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA", "RESERVADO");

			msgStock = "";

			List<SinAnx> listaAnx = sinarhUtiles.obtenerListaSinAnx(null,
					new Integer(50), codigoObj, pago.getCategoria(), null);

			if (!listaAnx.isEmpty()) {
				Integer cantADescontar = cantidadPuestosActivos();
				Integer total = new Integer(0);
				for (SinAnx a : listaAnx)
					total = total + a.getCantDisponible();
				if (total.intValue() < cantADescontar) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"No existe stock suficiente en SINARH");
					return;

				} else {
					for (SinAnx a : listaAnx) {
						if (cantADescontar.intValue() > 0) {
							if (a.getCantDisponible().intValue() > cantADescontar
									.intValue()) {
								Integer cantidad = a.getCantDisponible()
										.intValue() - cantADescontar.intValue();
								a.setCantDisponible(cantidad);
								em.merge(a);
								cantADescontar = cantADescontar - cantidad;
							} else {
								Integer cantidad = cantADescontar;
								a.setCantDisponible(cantidad);
								em.merge(a);
								cantADescontar = cantADescontar - cantidad;
							}
						}
					}
					pago.setEstado(referenciaGrupo.getValorNum());
					pago.setTipo("GRUPO");

					em.persist(pago);

					for (ConcursoPuestoDet act : puestosActivos()) {
						PuestoConceptoPago puestoPago = new PuestoConceptoPago();
						puestoPago.setActivo(true);
						puestoPago.setAnho(pago.getAnho());
						puestoPago.setEstado(referenciaPuesto.getValorNum());
						puestoPago.setFechaAlta(new Date());
						puestoPago.setMonto(pago.getMonto());
						puestoPago.setObjCodigo(pago.getObjCodigo());
						puestoPago.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
						puestoPago.setPlantaCargoDet(act.getPlantaCargoDet());
						puestoPago.setCategoria(pago.getCategoria());
						em.persist(puestoPago);
					}

				}
			}

		} else {
			Referencias referenciaGrupo = new Referencias();
			referenciaGrupo = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "PENDIENTE");
			Referencias referenciaPuesto = new Referencias();
			referenciaPuesto = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA", "PENDIENTE");
			pago.setEstado(referenciaGrupo.getValorNum());
			pago.setTipo("GRUPO");
			em.persist(pago);
			for (ConcursoPuestoDet act : puestosActivos()) {
				PuestoConceptoPago puestoPago = new PuestoConceptoPago();
				puestoPago.setActivo(true);
				puestoPago.setAnho(pago.getAnho());
				puestoPago.setEstado(referenciaPuesto.getValorNum());
				puestoPago.setFechaAlta(new Date());
				puestoPago.setMonto(pago.getMonto());
				puestoPago.setObjCodigo(pago.getObjCodigo());
				puestoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				puestoPago.setPlantaCargoDet(act.getPlantaCargoDet());
				puestoPago.setCategoria(pago.getCategoria());
				em.persist(puestoPago);
			}
		}
	}

	/**
	 * Guarda el registro en caso de que el puesto sea de contratado
	 * 
	 * @param p
	 */
	private void guardarContratado(GrupoConceptoPago p) {
		Referencias referenciaGrupo = new Referencias();
		referenciaGrupo = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA_GRUPO", "RESERVADO");
		Referencias referenciaPuesto = new Referencias();
		referenciaPuesto = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "RESERVADO");
		p.setEstado(referenciaGrupo.getValorNum());
		p.setTipo("GRUPO");
		em.persist(p);

		for (ConcursoPuestoDet activo : puestosActivos()) {
			PuestoConceptoPago puestoPago = new PuestoConceptoPago();
			puestoPago.setActivo(true);
			puestoPago.setAnho(p.getAnho());
			puestoPago.setEstado(referenciaPuesto.getValorNum());
			puestoPago.setFechaAlta(new Date());
			puestoPago.setMonto(p.getMonto());
			puestoPago.setObjCodigo(p.getObjCodigo());
			puestoPago.setCategoria(p.getCategoria());
			puestoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			puestoPago.setPlantaCargoDet(activo.getPlantaCargoDet());
			em.persist(puestoPago);
		}
	}

	/**
	 * Metodo llamando desde el boton guardar de la pantalla
	 * 
	 * @return
	 */
	public String guardar() {
		try {
			if (esContratadoOpermanente(
					concursoPuestoAgr.getIdConcursoPuestoAgr())
					.equalsIgnoreCase("PERMANENTE")) {
				for (GrupoConceptoPago p : listaGrupoPago) {
					if (p.getCategoria() == null)
						guardarPermanenteSinCategoria(p);
					else
						guardarPermanenteConCategoria(p);

				}
			}
			if (esContratadoOpermanente(
					concursoPuestoAgr.getIdConcursoPuestoAgr())
					.equalsIgnoreCase("CONTRATADO")) {
				for (GrupoConceptoPago p : listaGrupoPago)
					guardarContratado(p);
			}

			em.flush();

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			if (msgStock != null && !msgStock.trim().isEmpty()) {
				statusMessages
						.add(Severity.WARN,
								"Los Objetos de Gastos: "
										+ msgStock
										+ " no fueron guardados por no contar con suficiente stock");
				msgStock = null;
			}
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	/**
	 * Metodo llamado desde el metodo actualizar en caso de que el registro
	 * actual sea uno nuevo
	 * 
	 * @param p
	 */
	public void guardar(GrupoConceptoPago p) {
		if (esContratadoOpermanente(concursoPuestoAgr.getIdConcursoPuestoAgr())
				.equalsIgnoreCase("PERMANENTE")) {
			if (p.getCategoria() == null)
				guardarPermanenteSinCategoria(p);
			else
				guardarPermanenteConCategoria(p);
		}
		if (esContratadoOpermanente(concursoPuestoAgr.getIdConcursoPuestoAgr())
				.equalsIgnoreCase("CONTRATADO")) {
			guardarContratado(p);
		}
	}

	/**
	 * Metodo llamado desde el boton actualizar desde la pantalla
	 * 
	 * @return
	 */
	public String updated() {
		try {
			for (GrupoConceptoPago p : listaGrupoPago) {
				if (p.getIdGrupoConceptoPago() == null) {
					guardar(p);
				}
			}

			for (GrupoConceptoPago paux : listaGrupoPagoAux) {
				Boolean esta = false;
				for (GrupoConceptoPago p : listaGrupoPago) {
					if (p.getIdGrupoConceptoPago().equals(
							paux.getIdGrupoConceptoPago()))
						esta = true;
				}
				if (!esta) {
					inactivarGrupoConceptoPago(paux);

				}
			}
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	/**
	 * En caso de que uno mas registros son eliminados de la grilla estos son
	 * inactivados de la bd
	 * 
	 * @param gr
	 */
	private void inactivarGrupoConceptoPago(GrupoConceptoPago gr) {
		gr.setActivo(false);
		gr.setFechaMod(new Date());
		gr.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(gr);
		Query q = em
				.createQuery("select pago from PuestoConceptoPago pago "
						+ " where pago.activo is true and pago.objCodigo = :cod and pago.monto = :monto ");
		q.setParameter("cod", gr.getObjCodigo());
		q.setParameter("monto", gr.getMonto());
		List<PuestoConceptoPago> listaPuestoPago = q.getResultList();
		for (PuestoConceptoPago pago : listaPuestoPago) {
			if (pago.getCategoria() == null
					|| pago.getCategoria().trim().isEmpty()) {
				pago.setActivo(false);
				pago.setUsuMod(usuarioLogueado.getCodigoUsuario());
				pago.setFechaMod(new Date());
				em.merge(pago);
			}

		}

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

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public Integer getCodigoObj() {
		return codigoObj;
	}

	public void setCodigoObj(Integer codigoObj) {
		this.codigoObj = codigoObj;
	}

	public String getValorObj() {
		return valorObj;
	}

	public void setValorObj(String valorObj) {
		this.valorObj = valorObj;
	}

	public String getCodigoCategoria() {
		return codigoCategoria;
	}

	public void setCodigoCategoria(String codigoCategoria) {
		this.codigoCategoria = codigoCategoria;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public Integer getMonto() {
		return monto;
	}

	public void setMonto(Integer monto) {
		this.monto = monto;
	}

	public List<GrupoConceptoPago> getListaGrupoPago() {
		return listaGrupoPago;
	}

	public void setListaGrupoPago(List<GrupoConceptoPago> listaGrupoPago) {
		this.listaGrupoPago = listaGrupoPago;
	}

	public List<GrupoConceptoPago> getListaGrupoPagoAux() {
		return listaGrupoPagoAux;
	}

	public void setListaGrupoPagoAux(List<GrupoConceptoPago> listaGrupoPagoAux) {
		this.listaGrupoPagoAux = listaGrupoPagoAux;
	}

	public Long getIdSinAnx() {
		return idSinAnx;
	}

	public void setIdSinAnx(Long idSinAnx) {
		this.idSinAnx = idSinAnx;

		if (idSinAnx != null) {
			sinAnx = new SinAnx();
			sinAnx = em.find(SinAnx.class, idSinAnx);
			categoria = sinAnx.getCtgCodigo();
			codigoCategoria = sinAnx.getCtgCodigo();
			if (esContratadoOpermanente(
					concursoPuestoAgr.getIdConcursoPuestoAgr())
					.equalsIgnoreCase("CONTRATADO"))
				monto = null;
			if (esContratadoOpermanente(
					concursoPuestoAgr.getIdConcursoPuestoAgr())
					.equalsIgnoreCase("PERMANENTE"))
				buscarMonto(sinAnx.getCatGrupo());
		}
		idSinAnx = null;
	}

	public SinAnx getSinAnx() {
		return sinAnx;
	}

	public void setSinAnx(SinAnx sinAnx) {
		this.sinAnx = sinAnx;
	}

	public String getMsgStock() {
		return msgStock;
	}

	public void setMsgStock(String msgStock) {
		this.msgStock = msgStock;
	}

	public Long getIdSinCategoria() {
		return idSinCategoria;
	}

	public void setIdSinCategoria(Long idSinCategoria) {
		this.idSinCategoria = idSinCategoria;
		if (idSinCategoria != null) {
			sinCategoriaContratados = new SinCategoriaContratados();
			sinCategoriaContratados = em.find(SinCategoriaContratados.class,
					idSinCategoria);
			categoria = sinCategoriaContratados.getConCtg();
			codigoCategoria = sinCategoriaContratados.getConCtg();
			if (esContratadoOpermanente(
					concursoPuestoAgr.getIdConcursoPuestoAgr())
					.equalsIgnoreCase("CONTRATADO"))
				monto = null;
		}
		idSinCategoria = null;
	}

	public SinCategoriaContratados getSinCategoriaContratados() {
		return sinCategoriaContratados;
	}

	public void setSinCategoriaContratados(
			SinCategoriaContratados sinCategoriaContratados) {
		this.sinCategoriaContratados = sinCategoriaContratados;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}

	public Boolean getHabilitarCategoria() {
		return habilitarCategoria;
	}

	public void setHabilitarCategoria(Boolean habilitarCategoria) {
		this.habilitarCategoria = habilitarCategoria;
	}

}
