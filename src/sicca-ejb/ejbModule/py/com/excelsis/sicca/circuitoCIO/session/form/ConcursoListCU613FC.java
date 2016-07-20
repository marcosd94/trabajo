package py.com.excelsis.sicca.circuitoCIO.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jbpm.graph.exe.ProcessInstance;

import enums.ActividadEnum;
import enums.EstadoEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.entity.ComisionGrupo;
import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PromocionConcursoAgr;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.form.EvaluarDocPostulantesFormController;
import py.com.excelsis.sicca.session.ConcursoList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;

@Scope(ScopeType.CONVERSATION)
@Name("concursoListCU613FC")
public class ConcursoListCU613FC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4847572863311278068L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	ConcursoList concursoList;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	private EvaluarDocPostulantesFormController evaluarDocPostulantesFormController;
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(required = false)
	ProcessInstance processInstance;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	Integer valorNumEstadoConcurso = null;
	Integer valorNumEstadoConcursoAbierto = null;

	private Entidad entidad = new Entidad();

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Integer ordenUnidOrg;

	private String concursoDesc;
	private Long idTipoConcurso;

	private List<PlantaCargoDet> listaCargos;
	private List<Concurso> listaConcursos;

	public void init() throws Exception {
		evaluarDocPostulantesFormController = (EvaluarDocPostulantesFormController) Component
				.getInstance(EvaluarDocPostulantesFormController.class, true);
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		searchAll();
		asignarRolesTarea();
		calcEstado();

	}

	public void cargarCabecera() {
		grupoPuestosController = (GrupoPuestosController) Component
				.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();

		SinNivelEntidad sinNivelEntidad = grupoPuestosController
				.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		ConfiguracionUoCab configuracionUoCab = grupoPuestosController
				.getConfiguracionUoCab();

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad
				.getIdSinNivelEntidad());

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab
				.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();
	}

	public void calcEstado() {
		Query q = em
				.createQuery("select Referencias from Referencias Referencias "
						+ "where Referencias.dominio = 'ESTADOS_CONCURSO' and Referencias.descAbrev = 'CERRADO GRUPOS'");

		List<Referencias> lista = q.getResultList();

		if (lista.size() == 1) {
			valorNumEstadoConcurso = lista.get(0).getValorNum();
		} else
			valorNumEstadoConcurso = null;
	}

	public void searchAll() throws Exception {
		idTipoConcurso = null;
		concursoDesc = null;

		listaConcursos = new ArrayList<Concurso>();
		searchConcurso();
	}

	public void searchConcurso() throws Exception {
		String query = getQuery();
		if (query == null)
			return;
		listaConcursos = concursoList.listaConcursos(query);
		System.out.println("-");
	}

	public String getQuery() {
		String query = "select distinct concurso from Concurso concurso "
				+ "left join concurso.configuracionUoCab configuracionUoCab "
				+ "left join concurso.datosEspecificosTipoConc tipoConcurso "
				+ "left join configuracionUoCab.entidad entidad "
				+ "left join entidad.sinEntidad sinEntidad "
				+ " where concurso.activo is true ";
		if (nivelEntidadOeeUtil != null
				&& nivelEntidadOeeUtil.getIdConfigCab() != null) {
			configuracionUoCab = em.find(ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab());
			query += " and configuracionUoCab.idConfiguracionUo = "
					+ configuracionUoCab.getIdConfiguracionUo();
		}
		if (idTipoConcurso != null)
			query += " and tipoConcurso.idDatosEspecificos = " + idTipoConcurso;
		else {
			DatosEspecificos institucional = buscarTipoConcurso("CONCURSO INTERNO DE OPOSICION INSTITUCIONAL");
			DatosEspecificos interInstitucional = buscarTipoConcurso("CONCURSO INTERNO DE OPOSICION INTER INSTITUCIONAL");
			DatosEspecificos promocion = buscarTipoConcurso("CONCURSO INTERNO DE PROMOCION SALARIAL");//NUEVO TIPO DE CONCURSO DE PROMOCION SALARIAL - EC
			query += " and (tipoConcurso.idDatosEspecificos = "
					+ institucional.getIdDatosEspecificos()
					+ " or tipoConcurso.idDatosEspecificos = "
					+ interInstitucional.getIdDatosEspecificos() 
					+ " or tipoConcurso.idDatosEspecificos = "
					+ promocion.getIdDatosEspecificos()
					+ ")";
		}
		
		
		if (concursoDesc != null && !concursoDesc.trim().isEmpty())
			query += " and lower(concurso.nombre) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(concursoDesc
							.toLowerCase()) + "','%')))";
		//se agrego la siguiente condicion para que no se incluyan los concursos que se cargaron solo para publicacion
		query += " and concurso.estado  != (select Referencias.valorNum from Referencias Referencias "
				+ " where Referencias.dominio = 'ESTADOS_CONCURSO' and Referencias.descAbrev = 'PUBLICACION') "
				+ " ";

		return query;
	}

	public Boolean desHabilitarLink(Concurso concurso) {
		if (concurso.getEstado() != null
				&& concurso.getEstado().intValue() == valorNumEstadoConcurso
						.intValue()) {
			return true;
		}
		return false;
	}

	public Boolean desHabilitarLinkIniciar(Concurso concurso) {
		if (concurso.getEstado() != null
				&& concurso.getEstado().intValue() == valorNumEstadoConcurso
						.intValue()) {
			return true;
		}
		
		return false;
	}

	public void search() throws Exception {

		listaConcursos = new ArrayList<Concurso>();
		searchConcurso();
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	public String eliminar(Integer row) throws Exception {
		try {

			List<ConcursoPuestoAgr> puestosAgr = new ArrayList<ConcursoPuestoAgr>();
			List<ConcursoPuestoAgr> puestosAg = new ArrayList<ConcursoPuestoAgr>();
			Concurso concur = new Concurso();
			concur = listaConcursos.get(row);

			puestosAgr = buscarGrupos(concur.getIdConcurso());
			/* Incidencia 810 */
			if (concur.getEstado() != null
					&& concur.getEstado().intValue() == valorNumEstadoConcurso
							.intValue()) {
				return null;
			}
			/******/

			for (ConcursoPuestoAgr agr : puestosAgr) {
				Long idProcess = agr.getIdProcessInstance();
				if (idProcess != null) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"El concurso no puede ser eliminado, está en uso.");
					searchAll();
					return null;
				}
			}

			puestosAg = buscarGruposAgrupados(concur.getIdConcurso());

			for (ConcursoPuestoAgr agr : puestosAg) {
				Long idProcess = agr.getIdProcessInstance();
				if (idProcess != null) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"El concurso no puede ser eliminado, está en uso.");
					searchAll();
					return null;
				}
			}

			Concurso concurso = new Concurso();
			concurso = em.find(Concurso.class, concur.getIdConcurso());
			List<Documentos> documentosAdj = new ArrayList<Documentos>();
			documentosAdj = buscarDocumentos("Concurso", concur.getIdConcurso());
			for (Documentos docAdj : documentosAdj)
				em.remove(docAdj);

			for (ConcursoPuestoAgr agr : puestosAgr) {
				List<Documentos> documentosAgr = new ArrayList<Documentos>();
				documentosAgr = buscarDocumentos("ConcursoPuestoAgr",
						agr.getIdConcursoPuestoAgr());
				for (Documentos doc : documentosAgr) {
					em.remove(doc);

				}

				List<ComisionGrupo> lcomGrup = new ArrayList<ComisionGrupo>();
				lcomGrup = buscarComisionGrupo(agr.getIdConcursoPuestoAgr());
				for (ComisionGrupo g : lcomGrup) {
					em.remove(g);

				}
				em.remove(agr);

			}

			puestosAg = buscarGruposAgrupados(concur.getIdConcurso());
			for (ConcursoPuestoAgr agr : puestosAg) {
				List<Documentos> documentosAgr = new ArrayList<Documentos>();
				documentosAgr = buscarDocumentos("ConcursoPuestoAgr",
						agr.getIdConcursoPuestoAgr());
				for (Documentos doc : documentosAgr) {
					em.remove(doc);

				}
				List<ConcursoPuestoDet> puestosDet = new ArrayList<ConcursoPuestoDet>();
				puestosDet = agr.getConcursoPuestoDets();
				for (ConcursoPuestoDet det : puestosDet) {
					em.remove(det);

				}
				List<ComisionGrupo> lcomGrup = new ArrayList<ComisionGrupo>();
				lcomGrup = buscarComisionGrupo(agr.getIdConcursoPuestoAgr());
				for (ComisionGrupo g : lcomGrup) {
					em.remove(g);

				}
				em.remove(agr);

			}
			List<ComisionSeleccionCab> listaComisionCab = searchComisionSeleccionCab(concurso
					.getIdConcurso());
			if (listaComisionCab != null && listaComisionCab.size() > 0) {
				for (ComisionSeleccionCab cab : listaComisionCab) {
					List<ComisionSeleccionDet> listaDet = searchComisionSeleccionDet(cab
							.getIdComisionSel());
					if (listaDet != null && listaDet.size() > 0) {
						for (ComisionSeleccionDet d : listaDet) {
							em.remove(d);
						}

					}
					List<ComisionGrupo> listaGr = searchComisionGrupo(cab
							.getIdComisionSel());
					if (listaGr != null && listaGr.size() > 0) {
						for (ComisionGrupo g : listaGr) {
							em.remove(g);
						}

					}
					em.remove(cab);
					// /em.flush();
				}

			}

			List<ConcursoPuestoDet> listaEliminar = listaConcursoPuestoDet(concurso
					.getIdConcurso());
			EstadoCab estadoCab = obtenerEstadosCabecera("VACANTE");
			for (ConcursoPuestoDet c : listaEliminar) {
				c.getPlantaCargoDet().setEstadoCab(estadoCab);
				c.getPlantaCargoDet().setEstadoDet(null);
				em.merge(c.getPlantaCargoDet());
				if (c.getPlantaCargoDet().getPermanente()) {
					if (!concur
							.getDatosEspecificosTipoConc()
							.getDescripcion()
							.equals(buscarTipoConcurso(
									"CONCURSO INTERNO DE OPOSICION INTER INSTITUCIONAL")
									.getDescripcion())) {
						if (!buscarPuestoConceptoPago(concur,
								c.getPlantaCargoDet().getIdPlantaCargoDet())
								.isEmpty())
							reponerCategoriasController.reponerCategorias(c,
									"PUESTO", "CONCURSO");
					}
				}
				em.remove(c);
			}
			em.remove(concurso);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			searchAll();
			return "ok";
		} catch (javax.persistence.PersistenceException ex) {
			if (ex.getCause() != null
					&& ex.getMessage()
							.contains(
									"org.hibernate.exception.ConstraintViolationException")) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"El concurso no puede ser eliminado, está en uso.");
			}
			return null;
		} catch (Exception e) {
			if (e instanceof org.hibernate.exception.ConstraintViolationException) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"El concurso no puede ser eliminado, está en uso.");
			}
			searchAll();
			return null;
		}
	}

	private DatosEspecificos buscarTipoConcurso(String valor) {
		return seleccionUtilFormController.traerTipoConcursoConcurso(valor);
	}

	private List<PuestoConceptoPago> buscarPuestoConceptoPago(Concurso c,
			Long id) {
		Referencias refNoAdReferendum = referenciasUtilFormController
				.getReferencia("ESTADOS_CATEGORIA", "RESERVADO");
		Referencias refAdReferendum = referenciasUtilFormController
				.getReferencia("ESTADOS_CATEGORIA", "PENDIENTE");
		String query = "select pago from PuestoConceptoPago pago "
				+ " where pago.activo is true "
				+ " and pago.plantaCargoDet.idPlantaCargoDet = :id  "
				+ " and pago.estado = :estado ";
		Query q = em.createQuery(query);

		q.setParameter("id", id);
		if (c.getAdReferendum())
			q.setParameter("estado", refAdReferendum.getValorNum());
		else
			q.setParameter("estado", refNoAdReferendum.getValorNum());

		return q.getResultList();
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

	private List<ConcursoPuestoDet> listaConcursoPuestoDet(Long id) {
		String sql = "select det.* "
				+ "from seleccion.concurso_puesto_det det  "
				+ "where det.id_concurso = " + id;
		return em.createNativeQuery(sql, ConcursoPuestoDet.class)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<ComisionSeleccionCab> searchComisionSeleccionCab(Long id) {
		String sql = "select cab.* from seleccion.comision_seleccion_cab cab "
				+ "where cab.id_concurso = " + id;
		return em.createNativeQuery(sql, ComisionSeleccionCab.class)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<ComisionGrupo> searchComisionGrupo(Long id) {
		String sql = "select grup.* from seleccion.comision_grupo grup  "
				+ "where grup.id_comision_cab = " + id;
		return em.createNativeQuery(sql, ComisionGrupo.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<ComisionSeleccionDet> searchComisionSeleccionDet(Long id) {
		String sql = "select det.* from seleccion.comision_seleccion_det det "
				+ "where det.id_comision_sel = " + id;
		return em.createNativeQuery(sql, ComisionSeleccionDet.class)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<ComisionGrupo> buscarComisionGrupo(Long id) {
		String sql = "select com_gr.* "
				+ "from seleccion.comision_grupo com_gr "
				+ "where com_gr.id_concurso_puesto_agr = " + id;

		return em.createNativeQuery(sql, ComisionGrupo.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	private void actualizarDatosConcursoPuestoDet(Long idConc) {
		String sql = "select distinct(puesto_det.*)  "
				+ " from seleccion.concurso_puesto_det puesto_det  "
				+ "join planificacion.estado_det estado_det   "
				+ "on estado_det.id_estado_det = puesto_det.id_estado_det  "
				+ "join planificacion.planta_cargo_det det  "
				+ "on det.id_planta_cargo_det = puesto_det.id_planta_cargo_det  "
				+ "join planificacion.configuracion_uo_det uo_det  "
				+ "on uo_det.id_configuracion_uo_det = det.id_configuracion_uo_det  "
				+ "join planificacion.configuracion_uo_cab  uo_cab   "
				+ "on uo_cab.id_configuracion_uo = uo_det.id_configuracion_uo "
				+ "where lower(estado_det.descripcion) = 'en reserva'  "
				+ "and uo_cab.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " and puesto_det.id_concurso = " + idConc;

		String sql2 = "select distinct(puesto_det.*)  "
				+ " from seleccion.concurso_puesto_det puesto_det  "
				+ "join planificacion.estado_det estado_det   "
				+ "on estado_det.id_estado_det = puesto_det.id_estado_det  "
				+ "join planificacion.planta_cargo_det det  "
				+ "on det.id_planta_cargo_det = puesto_det.id_planta_cargo_det  "
				+ "join planificacion.configuracion_uo_det uo_det  "
				+ "on uo_det.id_configuracion_uo_det = det.id_configuracion_uo_det  "
				+ "join planificacion.configuracion_uo_cab  uo_cab   "
				+ "on uo_cab.id_configuracion_uo = uo_det.id_configuracion_uo "
				+ "where lower(estado_det.descripcion) = 'agrupado'  "
				+ "and uo_cab.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " and puesto_det.id_concurso = " + idConc;
		List<ConcursoPuestoDet> listaPuestosReservados = new ArrayList<ConcursoPuestoDet>();
		List<ConcursoPuestoDet> listaPuestosAgr = new ArrayList<ConcursoPuestoDet>();
		EstadoCab estadoCab = obtenerEstadosCabecera("VACANTE");
		try {
			listaPuestosReservados = em.createNativeQuery(sql,
					ConcursoPuestoDet.class).getResultList();
			listaPuestosAgr = em.createNativeQuery(sql2,
					ConcursoPuestoDet.class).getResultList();

			for (ConcursoPuestoDet det : listaPuestosReservados) {
				PlantaCargoDet planta = new PlantaCargoDet();
				planta = det.getPlantaCargoDet();
				planta.setEstadoCab(estadoCab);
				planta.setEstadoDet(null);
				em.merge(planta);
				em.remove(det);
				em.flush();
			}

			for (ConcursoPuestoDet det : listaPuestosAgr) {
				PlantaCargoDet planta = new PlantaCargoDet();
				planta = det.getPlantaCargoDet();
				planta.setEstadoCab(estadoCab);
				planta.setEstadoDet(null);
				em.merge(planta);
				em.remove(det);
				em.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

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

	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoDet> buscarDetalles(Long id) {
		String sql = "select puesto_det.* "
				+ "from seleccion.concurso_puesto_det puesto_det "
				+ "where puesto_det.id_concurso = " + id;
		return em.createNativeQuery(sql, ConcursoPuestoDet.class)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	private List<PromocionConcursoAgr> buscarDetallesPromocionSalarial(Long id) {
		String sql = "select puesto_det.* "
				+ " from seleccion.promocion_concurso_agr puesto_det "
				+ " join seleccion.concurso_puesto_agr agr on puesto_det.id_concurso_puesto_agr = agr.id_concurso_puesto_agr"
				+ " where agr.id_concurso = " + id;
		return em.createNativeQuery(sql, PromocionConcursoAgr.class)
				.getResultList();
	}

	/**
	 * Inicializa el proceso del workflow para un concurso.
	 * 
	 * @param id
	 *            : identificador del concurso para el cual se inicializara el
	 *            proceso.
	 * @return
	 * @throws Exception
	 */
	public String iniciarProceso(Integer row) throws Exception {
		try {
			statusMessages.clear();
			Concurso concurso = new Concurso();
			concurso = listaConcursos.get(row);
			/* Incidencia 810 */
			if (concurso.getEstado() != null
					&& concurso.getEstado().intValue() == valorNumEstadoConcurso
							.intValue()) {
				return null;
			}
			/******/
			Long id = concurso.getIdConcurso();
			// Si existen puestos marcados 'En Reserva' y que no pertenecen a
			// uno de los
			// Grupos creados, el mismo debe volver a estar disponible
			List<ConcursoPuestoDet> puestosReservados = buscarPuestosReservados();
			List<ConcursoPuestoDet> puestosDet = buscarDetalles(id);

			if (puestosDet == null || puestosDet.size() == 0) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "El Proceso de Concurso "
						+ concurso.getNombre()
						+ " no será iniciado por no tener grupos");
				searchAll();

				return "ok";

			}
			// validar que tenga comision grupo

			List<ConcursoPuestoAgr> listaAverificarComision = buscarGrupos(id);
			List<ConcursoPuestoAgr> listaConComision = new ArrayList<ConcursoPuestoAgr>();
			String msgComision = "";
			String auxMsgComision = "";
			if (listaAverificarComision != null
					&& listaAverificarComision.size() > 0) {
				Boolean cuentaConComision = false;
				String gruposComision = "";
				for (ConcursoPuestoAgr ag : listaAverificarComision) {
					msgComision = buscarEnComisionGrupo(ag
							.getIdConcursoPuestoAgr());

					if (!msgComision.trim().isEmpty())
						gruposComision += msgComision;

					else {
						cuentaConComision = true;
						listaConComision.add(ag);
					}
				}
				if (!msgComision.trim().isEmpty()) {
					String msgTotalComision = "El proceso de concurso para el/los grupo/s: "
							+ msgComision
							+ " no será iniciado por no tener comisión de selección";
					statusMessages.add(Severity.ERROR, msgTotalComision);
					searchAll();

					return "ok";

				}

			}
			Referencias refer = new Referencias();
			refer = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "INICIADO CIRCUITO");
			// Buscar grupos e inicializar procesos
			// List<ConcursoPuestoAgr> puestosAgr = buscarGrupos(id);
			EstadoDet estadoDet = new EstadoDet();
			estadoDet = seleccionUtilFormController.buscarEstadoDet("CONCURSO",
					"INICIADO CIRCUITO");
			if (listaConComision.size() > 0) {
				// si existen grupos que no iniciaron el proceso
				for (ConcursoPuestoAgr agr : listaConComision) {

					if (agr.getIdProcessInstance() == null) {
						BusinessProcess bp = BusinessProcess.instance();
						bp.createProcess("CIO");

						if (bp != null && bp.getProcessId() != null) {
							agr.setIdProcessInstance(bp.getProcessId());
						} else {
							em.getTransaction().rollback();
							statusMessages.clear();
							statusMessages
									.add(Severity.ERROR,
											"Ocurrio un error al inicializar el proceso. Consulte con el administrador del sistema.");
							throw new Exception();
						}

						if (refer != null) {
							agr.setEstado(refer.getValorNum());

						}
						if (estadoDet != null) {
							/* Incidencia 742 */
							for (ConcursoPuestoDet cpd : agr
									.getConcursoPuestoDets()) {
								cpd.setEstadoDet(estadoDet);
								cpd.setUsuMod(usuarioLogueado
										.getCodigoUsuario());
								cpd.getPlantaCargoDet().setEstadoDet(estadoDet);
								cpd = em.merge(cpd);
								/* Incidencia 880 */
								cpd.getPlantaCargoDet().setUsuMod(
										usuarioLogueado.getCodigoUsuario());
								cpd.setFechaMod(new Date());
								cpd.getPlantaCargoDet().setFechaMod(new Date());
								/**/
								em.merge(cpd.getPlantaCargoDet());

							}
							/* Fin Incidencia 742 */
						}

						em.merge(agr);
						statusMessages
								.add(Severity.INFO,
										"El grupo: "
												+ agr.getDescripcionGrupo()
												+ " ha sido derivado a la siguiente Actividad");
					}
				}
				Referencias ref = new Referencias();
				
				
				/*************************<MODIFICACION>*********************************/
				/*************************MODIFICADO: 21/04/2014*************************/
				/*************************AUTOR: ENRIQUE CESPEDES***********************/
				/*Se Ajusto el código para replicar lo realizado en el proceso de CPO
				 * cambiando la referencia del estado de ABIERTO a CERRADO GRUPOS, el cual es una condicion 
				 * para que pueda habilitarse el boton de homologacion y también deshabilitar los links de Eliminar y Iniciar Proceso de la pantalla ConcursoList
				 * 
				ref = referenciasUtilFormController.getReferencia(
						"ESTADOS_CONCURSO", "ABIERTO");*/
				
				ref = referenciasUtilFormController.getReferencia(
						"ESTADOS_CONCURSO", "CERRADO GRUPOS");
				/*************************</MODIFICACION>********************************/
				

				if (ref != null)
					concurso.setEstado(ref.getValorNum());
				em.merge(concurso);
				em.flush();

				em.flush();
			}

			searchAll();

			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			searchAll();

			return null;
		}

	}
	
	
	
	public String iniciarProcesoPromocionSalarial(Integer row) throws Exception {
		try {
			statusMessages.clear();
			Concurso concurso = new Concurso();
			concurso = listaConcursos.get(row);
			/* Incidencia 810 */
			if (concurso.getEstado() != null
					&& concurso.getEstado().intValue() == valorNumEstadoConcurso
							.intValue()) {
				return null;
			}
			/******/
			Long id = concurso.getIdConcurso();
			// Si existen puestos marcados 'En Reserva' y que no pertenecen a
			// uno de los
			// Grupos creados, el mismo debe volver a estar disponible
			List<PromocionConcursoAgr> puestosReservados = buscarPromocionSalarialReservados();
			List<PromocionConcursoAgr> puestosDet = buscarDetallesPromocionSalarial(id);

			if (puestosDet == null || puestosDet.size() == 0) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "El Proceso de Concurso "
						+ concurso.getNombre()
						+ " no será iniciado por no tener grupos");
				searchAll();

				return "ok";

			}
			// validar que tenga comision grupo

			List<ConcursoPuestoAgr> listaAverificarComision = buscarGrupos(id);
			List<ConcursoPuestoAgr> listaConComision = new ArrayList<ConcursoPuestoAgr>();
			String msgComision = "";
			String auxMsgComision = "";
			if (listaAverificarComision != null
					&& listaAverificarComision.size() > 0) {
				Boolean cuentaConComision = false;
				String gruposComision = "";
				for (ConcursoPuestoAgr ag : listaAverificarComision) {
					msgComision = buscarEnComisionGrupo(ag
							.getIdConcursoPuestoAgr());

					if (!msgComision.trim().isEmpty())
						gruposComision += msgComision;

					else {
						cuentaConComision = true;
						listaConComision.add(ag);
					}
				}
				if (!msgComision.trim().isEmpty()) {
					String msgTotalComision = "El proceso de concurso para el/los grupo/s: "
							+ msgComision
							+ " no será iniciado por no tener comisión de selección";
					statusMessages.add(Severity.ERROR, msgTotalComision);
					searchAll();

					return "ok";

				}

			}
			Referencias refer = new Referencias();
			refer = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "INICIADO CIRCUITO");
			// Buscar grupos e inicializar procesos
			// List<ConcursoPuestoAgr> puestosAgr = buscarGrupos(id);
			EstadoDet estadoDet = new EstadoDet();
			estadoDet = seleccionUtilFormController.buscarEstadoDet("CONCURSO",
					"INICIADO CIRCUITO");
			if (listaConComision.size() > 0) {
				// si existen grupos que no iniciaron el proceso
				for (ConcursoPuestoAgr agr : listaConComision) {

					if (agr.getIdProcessInstance() == null) {
						BusinessProcess bp = BusinessProcess.instance();
						bp.createProcess("PromocionCIO");

						if (bp != null && bp.getProcessId() != null) {
							agr.setIdProcessInstance(bp.getProcessId());
						} else {
							em.getTransaction().rollback();
							statusMessages.clear();
							statusMessages
									.add(Severity.ERROR,
											"Ocurrio un error al inicializar el proceso. Consulte con el administrador del sistema.");
							throw new Exception();
						}

						if (refer != null) {
							agr.setEstado(refer.getValorNum());

						}
						if (estadoDet != null) {
							/* Incidencia 742 */
							for (PromocionConcursoAgr cpd : agr.getPromocionConcursoAgrs()) {
								cpd.setEstadoDet(estadoDet);
								cpd.setUsuMod(usuarioLogueado
										.getCodigoUsuario());
//								cpd.getPlantaCargoDet().setEstadoDet(estadoDet);
//								cpd = em.merge(cpd);
//								/* Incidencia 880 */
//								cpd.getPlantaCargoDet().setUsuMod(
//										usuarioLogueado.getCodigoUsuario());
//								cpd.setFechaMod(new Date());
//								cpd.getPlantaCargoDet().setFechaMod(new Date());
//								/**/
//								em.merge(cpd.getPlantaCargoDet());

							}
							/* Fin Incidencia 742 */
						}

						em.merge(agr);
						statusMessages
								.add(Severity.INFO,
										"El grupo: "
												+ agr.getDescripcionGrupo()
												+ " ha sido derivado a la siguiente Actividad");
					}
				}
				Referencias ref = new Referencias();
				
				
				/*************************<MODIFICACION>*********************************/
				/*************************MODIFICADO: 21/04/2014*************************/
				/*************************AUTOR: ENRIQUE CESPEDES***********************/
				/*Se Ajusto el código para replicar lo realizado en el proceso de CPO
				 * cambiando la referencia del estado de ABIERTO a CERRADO GRUPOS, el cual es una condicion 
				 * para que pueda habilitarse el boton de homologacion y también deshabilitar los links de Eliminar y Iniciar Proceso de la pantalla ConcursoList
				 * 
				ref = referenciasUtilFormController.getReferencia(
						"ESTADOS_CONCURSO", "ABIERTO");*/
				
				ref = referenciasUtilFormController.getReferencia(
						"ESTADOS_CONCURSO", "CERRADO GRUPOS");
				/*************************</MODIFICACION>********************************/
				

				if (ref != null)
					concurso.setEstado(ref.getValorNum());
				em.merge(concurso);
				em.flush();

				em.flush();
			}

			searchAll();

			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			searchAll();

			return null;
		}

	}
	
	
	
	

	@SuppressWarnings("unchecked")
	private String buscarEnComisionGrupo(Long idAg) {
		String sentencia = "select com.*  "
				+ "from seleccion.comision_grupo com "
				+ "where com.id_concurso_puesto_agr = " + idAg;
		List<ComisionGrupo> listaCom = new ArrayList<ComisionGrupo>();
		listaCom = em.createNativeQuery(sentencia, ComisionGrupo.class)
				.getResultList();
		if (listaCom == null || listaCom.size() == 0) {
			ConcursoPuestoAgr agrActual = new ConcursoPuestoAgr();
			agrActual = em.find(ConcursoPuestoAgr.class, idAg);
			return " - " + agrActual.getDescripcionGrupo();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	private List<Documentos> buscarDocumentos(String nombre, Long id) {
		String sql = "select doc.* " + "from general.documentos doc "
				+ "where nombre_tabla = '" + nombre + "' " + "and id_tabla = "
				+ id;
		List<Documentos> lista = new ArrayList<Documentos>();
		lista = em.createNativeQuery(sql, Documentos.class).getResultList();
		return lista;
	}

	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoAgr> buscarGrupos(Long id) {
		String sql = "select agr.* "
				+ "from seleccion.concurso_puesto_agr agr "
				+ "where agr.id_concurso = " + id;
		List<ConcursoPuestoAgr> lista = new ArrayList<ConcursoPuestoAgr>();
		lista = em.createNativeQuery(sql, ConcursoPuestoAgr.class)
				.getResultList();
		return lista;
	}

	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoAgr> buscarGruposAgrupados(Long id) {
		String sql = "select distinct(agr.*) from seleccion.concurso_puesto_agr agr "
				+ "join seleccion.concurso_puesto_det det "
				+ "on agr.id_concurso_puesto_agr = det.id_concurso_puesto_agr "
				+ "join planificacion.estado_det estado_det "
				+ "on estado_det.id_estado_det = det.id_estado_det "
				+ "where lower(estado_det.descripcion) = 'agrupado' "
				+ "and det.id_concurso = " + id;

		List<ConcursoPuestoAgr> lista = new ArrayList<ConcursoPuestoAgr>();
		lista = em.createNativeQuery(sql, ConcursoPuestoAgr.class)
				.getResultList();

		return lista;
	}

	@SuppressWarnings({ "unchecked" })
	private List<ConcursoPuestoDet> buscarPuestosReservados() {
		try {
			String cad = " select puesto_det.* "
					+ "from seleccion.concurso_puesto_det puesto_det "
					+ "join planificacion.estado_det estado_det "
					+ "on estado_det.id_estado_det = puesto_det.id_estado_det  "
					+ "join planificacion.estado_cab  "
					+ "on estado_cab.id_estado_cab = estado_det.id_estado_cab "
					+ "where puesto_det.id_concurso_puesto_agr is null "
					+ "and lower(estado_det.descripcion) = '"
					+ EstadoEnum.RESERVA.getValor().toLowerCase() + "' "
					+ "and lower(estado_cab.descripcion) = '"
					+ EstadoEnum.CONCURSO.getValor().toLowerCase() + "' ";

			List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
			lista = em.createNativeQuery(cad, ConcursoPuestoDet.class)
					.getResultList();
			return lista;
		} catch (Exception ex) {
			return new Vector<ConcursoPuestoDet>();
		}
	}
	
	
	
	
	private List<PromocionConcursoAgr> buscarPromocionSalarialReservados() {
		try {
			String cad = " select puesto_det.* "
					+ "from seleccion.promocion_concurso_agr puesto_det "
					+ "join planificacion.estado_det estado_det "
					+ "on estado_det.id_estado_det = puesto_det.id_estado_det  "
					+ "join planificacion.estado_cab  "
					+ "on estado_cab.id_estado_cab = estado_det.id_estado_cab "
					+ "where puesto_det.id_concurso_puesto_agr is null "
					+ "and lower(estado_det.descripcion) = '"
					+ EstadoEnum.RESERVA.getValor().toLowerCase() + "' "
					+ "and lower(estado_cab.descripcion) = '"
					+ EstadoEnum.CONCURSO.getValor().toLowerCase() + "' ";

			List<PromocionConcursoAgr> lista = new ArrayList<PromocionConcursoAgr>();
			lista = em.createNativeQuery(cad, PromocionConcursoAgr.class)
					.getResultList();
			return lista;
		} catch (Exception ex) {
			return new Vector<PromocionConcursoAgr>();
		}
	}

	/**
	 * Asigna los roles necesarios a la primera tarea
	 */
	private void asignarRolesTarea() {
		roles = seleccionUtilFormController.getRolesTarea(
				ActividadEnum.CIO_CARGA_GRUPO.getValor(),
				ProcesoEnum.CONCURSO.getValor());
	}

	@SuppressWarnings("unchecked")
	public Boolean habilitarLink(Long id) {
		String sql = "select cab.*  "
				+ "from seleccion.comision_seleccion_cab cab "
				+ "join seleccion.concurso conc "
				+ "on conc.id_concurso = cab.id_concurso "
				+ "where conc.id_concurso = " + id;
		List<ComisionSeleccionCab> listaCab = new ArrayList<ComisionSeleccionCab>();
		listaCab = em.createNativeQuery(sql, ComisionSeleccionCab.class)
				.getResultList();
		if (listaCab.size() > 0)
			return true;
		return false;
	}

	private boolean tieneGrupo(Concurso actual) {
		String query = "select agr from ConcursoPuestoAgr agr "
				+ " where agr.activo is true "
				+ " and agr.concurso.idConcurso = :id  ";
		Query q = em.createQuery(query);
		q.setParameter("id", actual.getIdConcurso());
		List<Concurso> lista = q.getResultList();
		if (lista.isEmpty())
			return false;

		return true;
	}

	public Boolean habilitarLinkEditar(Concurso actual) {
		if (!tieneGrupo(actual) && !descontoStock(actual))
			return true;
		return false;
	}

	private boolean descontoStock(Concurso actual) {
		Referencias referencia = new Referencias();
		if (actual.getAdReferendum() == null)
			return false;
		if (actual.getAdReferendum())
			referencia = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA", "PENDIENTE");
		else
			referencia = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA", "RESERVADO");

		String query = "select det from ConcursoPuestoDet det "
				+ " where det.activo is true "
				+ " and det.plantaCargoDet.activo is true "
				+ " and det.plantaCargoDet.permanente is true "
				+ " and det.concurso.idConcurso = :id  ";
		Query q = em.createQuery(query);
		q.setParameter("id", actual.getIdConcurso());
		List<ConcursoPuestoDet> lista = q.getResultList();

		for (ConcursoPuestoDet d : lista) {
			String que = "select pago from PuestoConceptoPago pago "
					+ " where pago.activo is true "
					+ " and pago.plantaCargoDet.idPlantaCargoDet = :id  "
					+ " and pago.estado = :estado ";
			Query qr = em.createQuery(que);
			qr.setParameter("id", d.getPlantaCargoDet().getIdPlantaCargoDet());
			qr.setParameter("estado", referencia.getValorNum());
			if (!qr.getResultList().isEmpty())
				return true;
		}
		return false;
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

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public String getConcursoDesc() {
		return concursoDesc;
	}

	public void setConcursoDesc(String concursoDesc) {
		this.concursoDesc = concursoDesc;
	}

	public Long getIdTipoConcurso() {
		return idTipoConcurso;
	}

	public void setIdTipoConcurso(Long idTipoConcurso) {
		this.idTipoConcurso = idTipoConcurso;
	}

	public List<PlantaCargoDet> getListaCargos() {
		return listaCargos;
	}

	public void setListaCargos(List<PlantaCargoDet> listaCargos) {
		this.listaCargos = listaCargos;
	}

	public List<Concurso> getListaConcursos() {
		return listaConcursos;
	}

	public void setListaConcursos(List<Concurso> listaConcursos) {
		this.listaConcursos = listaConcursos;
	}

	public Integer getValorNumEstadoConcurso() {
		return valorNumEstadoConcurso;
	}

	public void setValorNumEstadoConcurso(Integer valorNumEstadoConcurso) {
		this.valorNumEstadoConcurso = valorNumEstadoConcurso;
	}

	public Integer getValorNumEstadoConcursoAbierto() {
		return valorNumEstadoConcursoAbierto;
	}

	public void setValorNumEstadoConcursoAbierto(
			Integer valorNumEstadoConcursoAbierto) {
		this.valorNumEstadoConcursoAbierto = valorNumEstadoConcursoAbierto;
	}

}
