package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.MatrizRefConf;
import py.com.excelsis.sicca.entity.MatrizRefConfDet;
import py.com.excelsis.sicca.entity.MatrizRefConfEnc;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.HomologacionPerfilMatrizList;
import py.com.excelsis.sicca.session.util.FuncionarioUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("elegirPerfilHomologadoFormController")
public class ElegirPerfilHomologadoFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5128635150844306697L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	HomologacionPerfilMatrizList homologacionPerfilMatrizList;

	@In(create = true)
	FuncionarioUtilFormController funcionarioUtilFormController;

	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	private HomologacionPerfilMatriz homologacionPerfilMatriz;
	private Long idHomologacion;

	private float totalGraduacion = 0F;
	private float totalPuntajeMaximo = 0F;
	private String oficinas;

	private Long idConcursoPuestoAgr;

	private Boolean habilitar = true;

	private List<DetContenidoFuncional> listaDetContenidoFuncional;
	private List<DetReqMin> listaRequisitosMinimosPuesto;
	private List<DetReqMin> listaRequisitosMinimosPuestoA1;
	private List<MatrizRefConfEnc> listaFactoresEvaluacion;
	private String elFrom;
	private String fromConcurso;
	private String fromX;
	private Long idAgr;
	private Boolean primeraVez = false;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		if (primeraVez) {
			funcionarioUtilFormController.init2(true);
			primeraVez = false;
		} else
			funcionarioUtilFormController.init2(false);
		habilitarPantalla();
		search();
	}

	private void habilitarPantalla() {
		habilitar = false;
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);

		Referencias refIniciado = new Referencias();
		refIniciado = referenciasUtilFormController.getReferencia(
				"ESTADOS_GRUPO", "INICIADO CIRCUITO");
		Referencias refCreado = new Referencias();
		refCreado = referenciasUtilFormController.getReferencia(
				"ESTADOS_GRUPO", "CREADO EN ACTIVIDAD MODIFICAR");

		if (concursoPuestoAgr.getEstado().intValue() == refIniciado
				.getValorNum().intValue()
				|| concursoPuestoAgr.getEstado().intValue() == refCreado
						.getValorNum().intValue())
			habilitar = true;
		else
			habilitar = false;

	}

	public void searchAll() {
		funcionarioUtilFormController.clear();
		funcionarioUtilFormController.init();
		homologacionPerfilMatrizList.limpiarCU255Activos();
	}

	public String getUrlToPageTipoCpt() {
		if (funcionarioUtilFormController.getTipoEspGeneral() == null
				|| (funcionarioUtilFormController.getTipoEspGeneral().getId() == null)) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar si es General o Específico.");
			return "";
		}
		if (funcionarioUtilFormController.getIdTipoCpt() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un Tipo CPT");
			return null;
		}
		return "/planificacion/cpt/CptList.xhtml?from="
				+ funcionarioUtilFormController.getFrom() + "&tipoCpt="
				+ funcionarioUtilFormController.getIdTipoCpt() + "&tipo="
				+ funcionarioUtilFormController.getTipoEspGeneral().getValor();

	}

	public void search() {
		homologacionPerfilMatrizList.setIdTipoCpt(funcionarioUtilFormController
				.getIdTipoCpt());
		homologacionPerfilMatrizList.setIdCpt(funcionarioUtilFormController
				.getCpt().getId());
		homologacionPerfilMatrizList
				.setDenominacion(funcionarioUtilFormController
						.getDenominacion());
		homologacionPerfilMatrizList.setNenCodigo(funcionarioUtilFormController
				.getSinNivelEntidad().getNenCodigo());
		homologacionPerfilMatrizList.setEntCodigo(funcionarioUtilFormController
				.getSinEntidad().getEntCodigo());
		homologacionPerfilMatrizList
				.setIdConfiguracionUo(funcionarioUtilFormController
						.getConfiguracionUoCab().getIdConfiguracionUo());
		homologacionPerfilMatrizList
				.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		homologacionPerfilMatrizList.setActivo(true);

		homologacionPerfilMatrizList.listaResultadosCU255();

	}

	public void verPerfilHomologado() {
		if (idHomologacion == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Perfil no válido.");
		} else {
			homologacionPerfilMatriz = em.find(HomologacionPerfilMatriz.class,
					idHomologacion);

			// Cargar Oficinas
			cargarOficinas(homologacionPerfilMatriz);
		}

	}

	private void cargarOficinas(
			HomologacionPerfilMatriz homologacionPerfilMatriz) {
		if (homologacionPerfilMatriz != null) {
			String consulta = " "
					+ " SELECT seleccion.get_oficinas_puesto_grupo(id_concurso_puesto_agr) as oficinas "
					+ " FROM seleccion.concurso_puesto_agr agr "
					+ " where agr.activo = true "
					+ " and id_concurso_puesto_agr = " + idConcursoPuestoAgr;

			Query q = em.createNativeQuery(consulta);
			List<Object> lista = q.getResultList();
			if (lista != null && lista.size() > 0) {
				oficinas = (String) lista.get(0);
			}
		}
	}

	/**
	 * Verifica si el usuario pertence a la misma OEE que la
	 * homologacionPerfilMatriz
	 * 
	 * @return
	 */
	public boolean mismaOee() {
		if (homologacionPerfilMatriz == null
				|| homologacionPerfilMatriz.getConfiguracionUoCab() == null)
			return false;
		else {
			Long idOeeUsuario = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			if (homologacionPerfilMatriz.getConfiguracionUoCab()
					.getIdConfiguracionUo().longValue() == idOeeUsuario
					.longValue())
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<DetContenidoFuncional> getDetContenidoFuncionals() {
		listaDetContenidoFuncional = new ArrayList<DetContenidoFuncional>();

		try {
			if (homologacionPerfilMatriz != null) {
				String consulta = ""
						+ " select distinct detContenidoFuncional from DetContenidoFuncional detContenidoFuncional "
						+ " join detContenidoFuncional.contenidoFuncional contenidoFuncional "
						+ " join detContenidoFuncional.detDescripcionContFuncionals detDescripcionContFuncional "
						+ " join detContenidoFuncional.homologacionPerfilMatriz homologacionPerfilMatriz "
						+ " where contenidoFuncional.activo = :activo "
						+ " 	and detDescripcionContFuncional.activo = :activo "
						+ " 	and detContenidoFuncional.tipo = 'HOMOLOGADO' "
						+ " 	and homologacionPerfilMatriz.idHomologacion = :idHomologacion "
						+ "  ";

				Query q = em.createQuery(consulta);
				q.setParameter("activo", new Boolean(true));
				q.setParameter("idHomologacion",
						homologacionPerfilMatriz.getIdHomologacion());

				listaDetContenidoFuncional = q.getResultList();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaDetContenidoFuncional;
	}

	@SuppressWarnings("unchecked")
	public List<DetReqMin> getRequisitosMinimosPuesto() {
		listaRequisitosMinimosPuesto = new ArrayList<DetReqMin>();

		try {
			if (homologacionPerfilMatriz != null) {
				String consulta = ""
						+ " select distinct detReqMin from DetReqMin detReqMin "
						+ " join detReqMin.requisitoMinimoCpt requisitoMinimoCpt "
						+ " join detReqMin.detMinimosRequeridoses detMinimosRequeridos "
						+ " join detReqMin.homologacionPerfilMatriz homologacionPerfilMatriz "
						+ " where requisitoMinimoCpt.activo = :activo "
						+ " 	and detMinimosRequeridos.activo = :activo "
						+ " 	and detReqMin.tipo = 'HOMOLOGADO' "
						+ " 	and homologacionPerfilMatriz.idHomologacion = :idHomologacion "
						+ "  ";

				Query q = em.createQuery(consulta);
				q.setParameter("activo", new Boolean(true));
				q.setParameter("idHomologacion",
						homologacionPerfilMatriz.getIdHomologacion());

				listaRequisitosMinimosPuesto = q.getResultList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaRequisitosMinimosPuesto;
	}

	@SuppressWarnings("unchecked")
	public List<DetReqMin> getRequisitosMinimosPuestoA1() {
		listaRequisitosMinimosPuestoA1 = new ArrayList<DetReqMin>();

		try {
			if (homologacionPerfilMatriz != null) {
				String consulta = ""
						+ " select distinct detReqMin from DetReqMin detReqMin "
						+ " join detReqMin.requisitoMinimoCpt requisitoMinimoCpt "
						+ " join detReqMin.detOpcionesConvenienteses detOpcionesConvenientes "
						+ " join detReqMin.homologacionPerfilMatriz homologacionPerfilMatriz "
						+ " where requisitoMinimoCpt.activo = :activo "
						+ " 	and detOpcionesConvenientes.activo = :activo "
						+ " 	and detReqMin.tipo = 'HOMOLOGADO' "
						+ " 	and homologacionPerfilMatriz.idHomologacion = :idHomologacion "
						+ "  ";

				Query q = em.createQuery(consulta);
				q.setParameter("activo", new Boolean(true));
				q.setParameter("idHomologacion",
						homologacionPerfilMatriz.getIdHomologacion());

				listaRequisitosMinimosPuestoA1 = q.getResultList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaRequisitosMinimosPuestoA1;
	}

	@SuppressWarnings("unchecked")
	public List<MatrizRefConfEnc> getFactoresEvaluacion() {
		listaFactoresEvaluacion = new ArrayList<MatrizRefConfEnc>();

		try {
			if (homologacionPerfilMatriz != null) {
				String consulta = ""
						+ " select distinct matrizRefConfEnc from MatrizRefConfEnc matrizRefConfEnc "
						+ " join matrizRefConfEnc.matrizRefConf matrizRefConf "
						+ " join matrizRefConfEnc.matrizRefConfDets matrizRefConfDets "
						+ " join matrizRefConf.homologacionPerfilMatriz homologacionPerfilMatriz "
						+ " where matrizRefConf.activo = :activo "
						+ " 	and matrizRefConfEnc.activo = :activo "
						+ " 	and homologacionPerfilMatriz.idHomologacion = :idHomologacion "
						+ "  ";

				Query q = em.createQuery(consulta);
				q.setParameter("activo", new Boolean(true));
				q.setParameter("idHomologacion",
						homologacionPerfilMatriz.getIdHomologacion());

				listaFactoresEvaluacion = q.getResultList();
				if (listaFactoresEvaluacion != null
						&& listaFactoresEvaluacion.size() > 0) {
					Collections.sort(listaFactoresEvaluacion);

					totalGraduacion = 0F;
					totalPuntajeMaximo = 0F;
					for (MatrizRefConfEnc matrizRefConfEnc : listaFactoresEvaluacion) {
						totalGraduacion += matrizRefConfEnc.getPuntajeMaximo();
						for (MatrizRefConfDet matrizRefConfDet : matrizRefConfEnc
								.getMatrizRefConfDets()) {
							totalPuntajeMaximo += matrizRefConfDet
									.getPuntajeMaximo();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaFactoresEvaluacion;
	}

	public List<MatrizRefConf> getListaFactoresEvaluacion() {
		List<MatrizRefConf> lista = new ArrayList<MatrizRefConf>();

		try {
			if (homologacionPerfilMatriz != null) {
				String consulta = ""
						+ " select distinct matrizRefConf from MatrizRefConf matrizRefConf "
						+ " join matrizRefConf.homologacionPerfilMatriz homologacionPerfilMatriz "
						+ " where matrizRefConf.activo = :activo "
						+ " 	and homologacionPerfilMatriz.idHomologacion = :idHomologacion "
						+ "  ";

				Query q = em.createQuery(consulta);
				q.setParameter("activo", new Boolean(true));
				q.setParameter("idHomologacion",
						homologacionPerfilMatriz.getIdHomologacion());

				lista = q.getResultList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

	/**
	 * Asgina un perfil a un grupo
	 */
	public String seleccionar() {
		if (idHomologacion == null || idConcursoPuestoAgr == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Perfil o Grupo no válido.");
			return null;
		} else {
			homologacionPerfilMatriz = em.find(HomologacionPerfilMatriz.class,
					idHomologacion);
			ConcursoPuestoAgr concursoPuestoAgr = em.find(
					ConcursoPuestoAgr.class, idConcursoPuestoAgr);
			/* Incio Incidencia 583 */
			concursoPuestoAgr
					.setHomologacionPerfilMatriz(new HomologacionPerfilMatriz());
			concursoPuestoAgr.getHomologacionPerfilMatriz().setIdHomologacion(
					idHomologacion);
			/* End Incidencia 583 */
			concursoPuestoAgr.setObjetivo(homologacionPerfilMatriz
					.getObjetivo());
			concursoPuestoAgr.setMision(homologacionPerfilMatriz.getMision());
			// concursoPuestoAgr.setHomologacionPerfilMatriz(homologacionPerfilMatriz);

			// Copiar contenidos funcionales
			// if (listaDetContenidoFuncional != null) {
			for (DetContenidoFuncional detContenidoFuncional : getDetContenidoFuncionals()) {
				DetContenidoFuncional detContenidoFuncional2 = new DetContenidoFuncional();
				detContenidoFuncional2.setConcursoPuestoAgr(concursoPuestoAgr);
				detContenidoFuncional2
						.setContenidoFuncional(detContenidoFuncional
								.getContenidoFuncional());
				detContenidoFuncional2.setCpt(detContenidoFuncional.getCpt());
				detContenidoFuncional2.setPlantaCargoDet(detContenidoFuncional
						.getPlantaCargoDet());
				detContenidoFuncional2.setPuntaje(detContenidoFuncional
						.getPuntaje());
				detContenidoFuncional2.setActivo(detContenidoFuncional
						.getActivo());
				detContenidoFuncional2.setTipo("GRUPO");

				em.persist(detContenidoFuncional2);

				List<DetDescripcionContFuncional> listaDetDescripcionContFuncional = detContenidoFuncional
						.getDetDescripcionContFuncionals();
				for (DetDescripcionContFuncional detDescripcionContFuncional : listaDetDescripcionContFuncional) {
					DetDescripcionContFuncional detDescripcionContFuncional2 = new DetDescripcionContFuncional();
					detDescripcionContFuncional2
							.setActivo(detDescripcionContFuncional.getActivo());
					detDescripcionContFuncional2
							.setDescripcion(detDescripcionContFuncional
									.getDescripcion());
					detDescripcionContFuncional2
							.setDetContenidoFuncional(new DetContenidoFuncional());
					detDescripcionContFuncional2.getDetContenidoFuncional()
							.setIdDetContenidoFuncional(
									detContenidoFuncional2
											.getIdDetContenidoFuncional());
					em.persist(detDescripcionContFuncional2);
				}
				// }
			}

			// Copiar requerimientos minimos
			// if (listaRequisitosMinimosPuesto != null) {
			for (DetReqMin detReqMin : getRequisitosMinimosPuesto()) {
				DetReqMin detReqMin2 = new DetReqMin();
				detReqMin2.setConcursoPuestoAgr(concursoPuestoAgr);
				detReqMin2.setCpt(detReqMin.getCpt());
				detReqMin2.setPlantaCargoDet(detReqMin.getPlantaCargoDet());
				detReqMin2.setPuntajeReqMin(detReqMin.getPuntajeReqMin());
				detReqMin2.setRequisitoMinimoCpt(detReqMin
						.getRequisitoMinimoCpt());
				detReqMin2.setTipo("GRUPO");
				detReqMin2.setActivo(detReqMin.getActivo());
				em.persist(detReqMin2);

				// Detalle de minimos requeridos
				List<DetMinimosRequeridos> listaDetMinimosRequeridos = detReqMin2
						.getDetMinimosRequeridoses();
				for (DetMinimosRequeridos detMinimosRequeridos : listaDetMinimosRequeridos) {
					DetMinimosRequeridos detMinimosRequeridos2 = new DetMinimosRequeridos();
					detMinimosRequeridos2.setActivo(detMinimosRequeridos
							.getActivo());
					detMinimosRequeridos2
							.setMinimosRequeridos(detMinimosRequeridos
									.getMinimosRequeridos());
					detMinimosRequeridos2.setDetReqMin(new DetReqMin());
					detMinimosRequeridos2.getDetReqMin().setIdDetReqMin(
							detReqMin2.getIdDetReqMin());
					em.persist(detMinimosRequeridos2);
				}

				// Detalle de opciones convenientes
				List<DetOpcionesConvenientes> listaDetOpcionesConvenientes = detReqMin2
						.getDetOpcionesConvenienteses();
				for (DetOpcionesConvenientes detOpcionesConvenientes : listaDetOpcionesConvenientes) {
					DetOpcionesConvenientes detOpcionesConvenientes2 = new DetOpcionesConvenientes();
					detOpcionesConvenientes2.setActivo(detOpcionesConvenientes
							.getActivo());
					detOpcionesConvenientes2
							.setOpcConvenientes(detOpcionesConvenientes
									.getOpcConvenientes());
					detOpcionesConvenientes2.setDetReqMin(new DetReqMin());
					detOpcionesConvenientes2.getDetReqMin().setIdDetReqMin(
							detReqMin2.getIdDetReqMin());
					em.persist(detOpcionesConvenientes2);
				}
				// }
			}

			// Copiar Factores de Evaluacion
			Date fechaAlta = new Date();
			List<MatrizRefConf> listaFactoresEvaluacion = getListaFactoresEvaluacion();
			for (MatrizRefConf matrizRefConf : listaFactoresEvaluacion) {
				MatrizRefConf matrizRefConf2 = new MatrizRefConf();
				matrizRefConf2.setActivo(matrizRefConf.getActivo());
				matrizRefConf2.setConcursoPuestoAgr(concursoPuestoAgr);
				matrizRefConf2.setDatosEspecificos(matrizRefConf
						.getDatosEspecificos());
				matrizRefConf2.setDescripcion(matrizRefConf.getDescripcion());
				matrizRefConf2.setFechaAlta(fechaAlta);
				matrizRefConf2.setPuntajeMaximo(matrizRefConf
						.getPuntajeMaximo());
				matrizRefConf2.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				matrizRefConf2.setTipo("GRUPO");
				em.persist(matrizRefConf2);

				// Copiar detalles
				for (MatrizRefConfEnc matrizRefConfEnc : matrizRefConf
						.getMatrizRefConfEncs()) {
					matrizRefConfEnc = em.find(MatrizRefConfEnc.class,
							matrizRefConfEnc.getIdMatrizRefConfEnc());
					MatrizRefConfEnc matrizRefConfEnc2 = new MatrizRefConfEnc();
					matrizRefConfEnc2.setActivo(matrizRefConfEnc.getActivo());
					matrizRefConfEnc2.setDatosEspecificos(matrizRefConfEnc
							.getDatosEspecificos());
					matrizRefConfEnc2.setDescripcion(matrizRefConfEnc
							.getDescripcion());
					matrizRefConfEnc2.setFechaAlta(fechaAlta);
					matrizRefConfEnc2.setNroOrden(matrizRefConfEnc
							.getNroOrden());
					matrizRefConfEnc2.setObligatorioSN(matrizRefConfEnc
							.getObligatorioSN());
					matrizRefConfEnc2.setPuntajeMaximo(matrizRefConfEnc
							.getPuntajeMaximo());
					matrizRefConfEnc2.setSumItemsSN(matrizRefConfEnc
							.getSumItemsSN());
					matrizRefConfEnc2.setUsuAlta(usuarioLogueado
							.getCodigoUsuario());
					matrizRefConfEnc2.setMatrizRefConf(new MatrizRefConf());
					matrizRefConfEnc2.getMatrizRefConf().setIdMatrizRefConf(
							matrizRefConf2.getIdMatrizRefConf());
					em.persist(matrizRefConfEnc2);

					// Copiar sub detalles
					List<MatrizRefConfDet> listaMatrizRefConfDet = matrizRefConfEnc
							.getMatrizRefConfDets();
					for (MatrizRefConfDet matrizRefConfDet : listaMatrizRefConfDet) {
						MatrizRefConfDet matrizRefConfDet2 = new MatrizRefConfDet();
						matrizRefConfDet2
								.setActivo(matrizRefConfDet.isActivo());
						matrizRefConfDet2.setDescripcion(matrizRefConfDet
								.getDescripcion());
						matrizRefConfDet2.setFechaAlta(fechaAlta);
						matrizRefConfDet2.setPuntajeMaximo(matrizRefConfDet
								.getPuntajeMaximo());
						matrizRefConfDet2.setPuntajeMinimo(matrizRefConfDet
								.getPuntajeMinimo());
						matrizRefConfDet2.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
						matrizRefConfDet2
								.setMatrizRefConfEnc(new MatrizRefConfEnc());
						matrizRefConfDet2.getMatrizRefConfEnc()
								.setIdMatrizRefConfEnc(
										matrizRefConfEnc2
												.getIdMatrizRefConfEnc());
						em.persist(matrizRefConfDet2);
					}
				}
			}

			concursoPuestoAgr.setModalidad(homologacionPerfilMatriz
					.getModalidad());
			concursoPuestoAgr.setFuenteFinanciacion(homologacionPerfilMatriz
					.getFuenteFinanciacion());
			concursoPuestoAgr.setRemuneracion(homologacionPerfilMatriz
					.getRemuneracion());
			concursoPuestoAgr.setOtrosBeneficios(homologacionPerfilMatriz
					.getOtrosBeneficios());
			concursoPuestoAgr.setHorario(homologacionPerfilMatriz.getHorario());
			concursoPuestoAgr.setCondLaborales(homologacionPerfilMatriz
					.getCondLaborales());
			concursoPuestoAgr.setOtrasInf(homologacionPerfilMatriz
					.getOtrasInf());
			concursoPuestoAgr.setPostHabilitados(homologacionPerfilMatriz
					.getPostHabilitados());
			concursoPuestoAgr.setPostNoHabilitados(homologacionPerfilMatriz
					.getPostNoHabilitados());
			concursoPuestoAgr.setDocumentos(homologacionPerfilMatriz
					.getDocumentos());
			concursoPuestoAgr.setFechaLimite(homologacionPerfilMatriz
					.getFechaLimite());
			concursoPuestoAgr.setLugar(homologacionPerfilMatriz.getLugar());
			concursoPuestoAgr.setContacto(homologacionPerfilMatriz
					.getContacto());
			em.merge(concursoPuestoAgr);

			em.flush();

			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Operación realizada con éxito.");
			if (fromConcurso == null) {
				if (fromX == null)
					return "/seleccion/admCargaGrupo/ConcursoPuestoDetList.seam?concursoPuestoAgrIdConcursoPuestoAg="
							+ concursoPuestoAgr.getIdConcursoPuestoAgr();
				else
					return "/" + fromX
							+ ".seam?concursoPuestoAgrIdConcursoPuestoAg="
							+ concursoPuestoAgr.getIdConcursoPuestoAgr();
			} else
				return "/" + fromConcurso
						+ ".seam?concursoPuestoAgrIdConcursoPuestoAgr=" + idAgr;
		}

	}

	public void setIdHomologacion(Long idHomologacion) {
		this.idHomologacion = idHomologacion;
	}

	public Long getIdHomologacion() {
		return idHomologacion;
	}

	public void setHomologacionPerfilMatriz(
			HomologacionPerfilMatriz homologacionPerfilMatriz) {
		this.homologacionPerfilMatriz = homologacionPerfilMatriz;
	}

	public HomologacionPerfilMatriz getHomologacionPerfilMatriz() {
		return homologacionPerfilMatriz;
	}

	public float getTotalGraduacion() {
		return totalGraduacion;
	}

	public void setTotalGraduacion(float totalGraduacion) {
		this.totalGraduacion = totalGraduacion;
	}

	public float getTotalPuntajeMaximo() {
		return totalPuntajeMaximo;
	}

	public void setTotalPuntajeMaximo(float totalPuntajeMaximo) {
		this.totalPuntajeMaximo = totalPuntajeMaximo;
	}

	public void setOficinas(String oficinas) {
		this.oficinas = oficinas;
	}

	public String getOficinas() {
		return oficinas;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setListaDetContenidoFuncional(
			List<DetContenidoFuncional> listaDetContenidoFuncional) {
		this.listaDetContenidoFuncional = listaDetContenidoFuncional;
	}

	public List<DetContenidoFuncional> getListaDetContenidoFuncional() {
		return listaDetContenidoFuncional;
	}

	public String getElFrom() {
		return elFrom;
	}

	public void setElFrom(String elFrom) {
		this.elFrom = elFrom;
	}

	public String getFromConcurso() {
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

	public String getFromX() {
		return fromX;
	}

	public void setFromX(String fromX) {
		this.fromX = fromX;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}

}
