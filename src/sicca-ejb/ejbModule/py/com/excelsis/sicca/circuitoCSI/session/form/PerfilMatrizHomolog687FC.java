package py.com.excelsis.sicca.circuitoCSI.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.ActividadEnum;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.MatrizRefConf;
import py.com.excelsis.sicca.entity.MatrizRefConfDet;
import py.com.excelsis.sicca.entity.MatrizRefConfEnc;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("perfilMatrizHomolog687FC")
public class PerfilMatrizHomolog687FC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	ConcursoPuestoAgrList concursoPuestoAgrList;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Long idConcurso;// enviado por el CU
	private Concurso concurso;
	private String obs=null;
	private String nombrePantalla = "VerificarPerfilYMatrizPorSFPConcursoSimplificado";
	private String direccionFisica;
	private String entity;
	private Long idConcursoPuestoAgr;
	private String estado;
	private boolean eviadoHomo;
	private Observacion observacion = new Observacion();
	private HomologacionPerfilMatriz homologacionPerfilMatriz = new HomologacionPerfilMatriz();
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		seguridadUtilFormController.verificarPerteneceOeeCSI(null, concursoPuestoAgr.getIdConcursoPuestoAgr(), seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "ENVIADO A HOMOLOGACION")
			+ "#"+seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "PENDIENTE REVISION")+"", ActividadEnum.CSI_APROBAR_HOMOLOG_SFP.getValor());
		
	}

	@SuppressWarnings("unchecked")
	public void init() {
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		observacion = new Observacion();
		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = em.find(Concurso.class, idConcurso);
		validarOee();
		eviadoHomo = enviadoHomologacion();
		findEntidades();// Trae las entidades segun el grupo que se envio
		searchAll();
		List<HomologacionPerfilMatriz> h =
			em.createQuery(" select h from  HomologacionPerfilMatriz h "
				+ " where h.concursoPuestoAgr.idConcursoPuestoAgr=:idConcursoPuestoAgr" ).setParameter("idConcursoPuestoAgr",idConcursoPuestoAgr).getResultList();
		homologacionPerfilMatriz = h.get(0);
		anexar();
		findEstado();
		observacion = new Observacion();
		obs=null;

	}

	public void searchAll() {
		concursoPuestoAgrList.setIdConcurso(idConcurso);
		concursoPuestoAgrList.listaResultados();
	}

	@SuppressWarnings("unchecked")
	private void findEntidades() {
		configuracionUoCab =
			em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		if (configuracionUoCab.getEntidad() != null) {
			sinEntidad =
				em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin =
				em.createQuery("Select n from SinNivelEntidad n " + " where n.aniAniopre ="
					+ sinEntidad.getAniAniopre() + " and n.nenCodigo=" + sinEntidad.getNenCodigo()).getResultList();
			if (!sin.isEmpty())
				sinNivelEntidad = sin.get(0);

		}

	}

	@SuppressWarnings("unchecked")
	public String homologar() {
		try {
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio = sdfSoloAnio.format(new Date());

			Concurso concurso = em.find(Concurso.class, idConcurso);
			if (concurso.getNroExpediente() == null && concurso.getFechaExpediente() == null) {
				concurso.setNroExpediente(genNroExpediente());
				concurso.setFechaExpediente(new Date());
			}
			em.merge(concurso);

			Date actual = new Date();
			Calendar calCalendario = Calendar.getInstance();
			calCalendario.setTimeInMillis(actual.getTime());
			// se suma un año a la fecha
			calCalendario.add(Calendar.YEAR, 1);

			List<HomologacionPerfilMatriz> h =
				em.createQuery(" select h from  HomologacionPerfilMatriz h "
					+ " where h.concursoPuestoAgr.idConcursoPuestoAgr=" + idConcursoPuestoAgr).getResultList();
			HomologacionPerfilMatriz matriz = new HomologacionPerfilMatriz();

			if (!h.isEmpty()) {
				matriz = h.get(0);
				matriz.setUsuHomolog(usuarioLogueado.getCodigoUsuario());
				matriz.setFechaHomolog(new Date());
				matriz.setNumero(maxNum());
				matriz.setAnio(Integer.parseInt(anio));
				matriz.setEstado("PENDIENTE DE FIRMA");
				matriz.setFechaVencimiento(calCalendario.getTime());
				matriz.setTipo("CONCURSO");
				em.merge(matriz);
				em.flush();
			}
			concursoPuestoAgr.setEstado(estadoHomologado());// CAMBIA EL ESTADO DEL GRUPO A HOMOLOGADO
			em.merge(concursoPuestoAgr);
			EstadoDet ed = eHomologado();
			eviadoHomo = enviadoHomologacion();
			em.flush();

			/**
			 * Actualiza los datos en la tabla: PLANTA_CARGO_DET y tambien Actializa de la tabla ConcursoPuestoDet para la incidencia 0000883
			 **/
			List<ConcursoPuestoDet> concursoPuestoDets =
				em.createQuery("Select d from ConcursoPuestoDet d"
					+ " where d.concursoPuestoAgr.idConcursoPuestoAgr= " + idConcursoPuestoAgr).getResultList();

			for (int i = 0; i < concursoPuestoDets.size(); i++) {
				if (ed != null) {
					ConcursoPuestoDet puestoDet =
						em.find(ConcursoPuestoDet.class, concursoPuestoDets.get(i).getIdConcursoPuestoDet());
					puestoDet.setEstadoDet(ed);
					puestoDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					puestoDet.setFechaAlta(new Date());
					em.merge(puestoDet);
					if (concursoPuestoDets.get(i).getPlantaCargoDet() != null) {
						PlantaCargoDet aux =
							em.find(PlantaCargoDet.class, concursoPuestoDets.get(i).getPlantaCargoDet().getIdPlantaCargoDet());
						aux.setEstadoCab(ed.getEstadoCab());
						aux.setEstadoDet(ed);
						aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
						aux.setFechaMod(new Date());
						em.merge(aux);
					}

					em.flush();
				}
			}

			/***
			 * 1. SEL_MATRIZ_REF_CONF
			 **/

			List<MatrizRefConf> matrizRefConfs =
				em.createQuery("Select m from MatrizRefConf m "
					+ " where m.concursoPuestoAgr.idConcursoPuestoAgr= " + idConcursoPuestoAgr + ""
					+ " and m.activo= true and m.tipo like 'GRUPO' ").getResultList();
			/**
			 * realizar la copia
			 */
			List<MatrizRefConf> matrizRefConfCopia = new ArrayList<MatrizRefConf>();
			for (int i = 0; i < matrizRefConfs.size(); i++) {

				MatrizRefConf confCopi = new MatrizRefConf();
				confCopi.setActivo(matrizRefConfs.get(i).getActivo());
				confCopi.setConcursoPuestoAgr(matrizRefConfs.get(i).getConcursoPuestoAgr());
				confCopi.setDatosEspecificos(matrizRefConfs.get(i).getDatosEspecificos());
				confCopi.setDescripcion(matrizRefConfs.get(i).getDescripcion());
				confCopi.setFechaAlta(matrizRefConfs.get(i).getFechaAlta());
				confCopi.setFechaMod(matrizRefConfs.get(i).getFechaMod());
				confCopi.setPuntajeMaximo(matrizRefConfs.get(i).getPuntajeMaximo());
				confCopi.setUsuAlta(matrizRefConfs.get(i).getUsuAlta());
				confCopi.setUsuMod(matrizRefConfs.get(i).getUsuMod());
				confCopi.setTipo("HOMOLOGADO");
				confCopi.setHomologacionPerfilMatriz(em.find(HomologacionPerfilMatriz.class, matriz.getIdHomologacion()));
				em.persist(confCopi);
				em.flush();
				matrizRefConfCopia.add(confCopi);
			}

			/***
			 * 1.2 SEL_MATRIZ_REF_ENC que estan referenciados para cada SEL_MATRIZ_REF_CONF
			 **/
			List<MatrizRefConfEnc> matrizRefConfEncs = new ArrayList<MatrizRefConfEnc>();
			List<MatrizRefConfEnc> matrizRefConfEncCopia = new ArrayList<MatrizRefConfEnc>();

			for (int i = 0; i < matrizRefConfs.size(); i++) {
				List<MatrizRefConfEnc> aux =
					em.createQuery(" select me from MatrizRefConfEnc me"
						+ " where me.activo=true  and me.matrizRefConf.idMatrizRefConf="
						+ matrizRefConfs.get(i).getIdMatrizRefConf()).getResultList();

				// todos los detalles de esa cabera realizar la copia correspondiente
				for (int j = 0; j < aux.size(); j++) {
					MatrizRefConfEnc enc =
						em.find(MatrizRefConfEnc.class, aux.get(j).getIdMatrizRefConfEnc());
					MatrizRefConfEnc encCopia = new MatrizRefConfEnc();
					encCopia.setActivo(enc.getActivo());
					encCopia.setDatosEspecificos(enc.getDatosEspecificos());
					encCopia.setDescripcion(enc.getDescripcion());
					encCopia.setFechaAlta(enc.getFechaAlta());
					encCopia.setFechaMod(enc.getFechaMod());
					encCopia.setNroOrden(enc.getNroOrden());
					encCopia.setObligatorioSN(enc.getObligatorioSN());
					encCopia.setPuntajeMaximo(enc.getPuntajeMaximo());
					encCopia.setSumItemsSN(enc.getSumItemsSN());
					encCopia.setUsuAlta(enc.getUsuAlta());
					encCopia.setUsuMod(enc.getUsuMod());
					encCopia.setMatrizRefConf(matrizRefConfCopia.get(i));
					em.persist(encCopia);
					em.flush();
					matrizRefConfEncCopia.add(encCopia);
					matrizRefConfEncs.add(aux.get(j));
				}

			}

			/***
			 * 1.3. SEL_MATRIZ_REF_DET que estan referenciados para cada SEL_MATRIZ_REF_ENC
			 **/

			for (int i = 0; i < matrizRefConfEncs.size(); i++) {
				List<MatrizRefConfDet> aux =
					em.createQuery("Select d from MatrizRefConfDet d "
						+ " where d.activo= true and d.matrizRefConfEnc.idMatrizRefConfEnc="
						+ matrizRefConfEncs.get(i).getIdMatrizRefConfEnc()).getResultList();
				// todos los detalles de esa cabera realizar la copia correspondiente
				for (int j = 0; j < aux.size(); j++) {
					MatrizRefConfDet confDet =
						em.find(MatrizRefConfDet.class, aux.get(j).getIdMatrizRefConfDet());
					MatrizRefConfDet confDetCopia = new MatrizRefConfDet();
					confDetCopia.setActivo(confDet.isActivo());
					confDetCopia.setDescripcion(confDet.getDescripcion());
					confDetCopia.setFechaAlta(confDet.getFechaAlta());
					confDetCopia.setFechaMod(confDet.getFechaMod());
					confDetCopia.setPuntaje(confDet.getPuntaje());
					confDetCopia.setPuntajeMaximo(confDet.getPuntajeMaximo());
					confDetCopia.setPuntajeMinimo(confDet.getPuntajeMinimo());
					confDetCopia.setUsuAlta(confDet.getUsuAlta());
					confDetCopia.setUsuAlta(confDet.getUsuAlta());
					confDetCopia.setMatrizRefConfEnc(matrizRefConfEncCopia.get(i));
					em.persist(confDetCopia);
					em.flush();
				}

			}

			/***
			 * 2. DET_REQ_MIN
			 */
			List<DetReqMin> detReqMins =
				em.createQuery("Select d from DetReqMin d "
					+ " where d.tipo like 'GRUPO' and d.concursoPuestoAgr.idConcursoPuestoAgr="
					+ idConcursoPuestoAgr).getResultList();

			List<DetReqMin> detReqMinCopia = new ArrayList<DetReqMin>();
			// se realiza la copia
			for (int i = 0; i < detReqMins.size(); i++) {
				DetReqMin reqMin = em.find(DetReqMin.class, detReqMins.get(i).getIdDetReqMin());
				DetReqMin reqMinCopia = new DetReqMin();
				reqMinCopia.setConcursoPuestoAgr(reqMin.getConcursoPuestoAgr());
				reqMinCopia.setCpt(reqMin.getCpt());
				reqMinCopia.setPlantaCargoDet(reqMin.getPlantaCargoDet());
				reqMinCopia.setPuntajeReqMin(reqMin.getPuntajeReqMin());
				reqMinCopia.setRequisitoMinimoCpt(reqMin.getRequisitoMinimoCpt());
				reqMinCopia.setHomologacionPerfilMatriz(matriz);
				reqMinCopia.setTipo("HOMOLOGADO");
				reqMinCopia.setActivo(true);
				em.persist(reqMinCopia);
				em.flush();
				detReqMinCopia.add(reqMinCopia);
			}

			/***
			 * 2. a. DET_MINIMOS_REQUERIDOS que estan referenciados para cada DET_REQ_MIN
			 */

			for (int i = 0; i < detReqMins.size(); i++) {
				List<DetMinimosRequeridos> aux =
					em.createQuery("select dr from DetMinimosRequeridos dr"
						+ " where dr.activo=true and dr.detReqMin.idDetReqMin= "
						+ detReqMins.get(i).getIdDetReqMin()).getResultList();
				for (int j = 0; j < aux.size(); j++) {// se realiza las copias
					DetMinimosRequeridos req =
						em.find(DetMinimosRequeridos.class, aux.get(j).getIdMinimosRequeridos());
					DetMinimosRequeridos requeridosCopia = new DetMinimosRequeridos();
					requeridosCopia.setActivo(req.getActivo());
					requeridosCopia.setMinimosRequeridos(req.getMinimosRequeridos());
					requeridosCopia.setDetReqMin(detReqMinCopia.get(i));
					em.persist(requeridosCopia);
					em.flush();

				}
			}
			/***
			 * 2. b. DET_OPCIONES_CONVENIENTES que estan referenciados para cada DET_REQ_MIN
			 */

			for (int i = 0; i < detReqMins.size(); i++) {
				List<DetOpcionesConvenientes> aux =
					em.createQuery("select do from DetOpcionesConvenientes do"
						+ " where do.activo=true and do.detReqMin.idDetReqMin= "
						+ detReqMins.get(i).getIdDetReqMin()).getResultList();
				for (int j = 0; j < aux.size(); j++) {// se realiza las copias
					DetOpcionesConvenientes opc =
						em.find(DetOpcionesConvenientes.class, aux.get(j).getIdDetOpcionesConvenientes());
					DetOpcionesConvenientes opcionesCopia = new DetOpcionesConvenientes();
					opcionesCopia.setActivo(opc.getActivo());
					opcionesCopia.setDetReqMin(detReqMinCopia.get(i));
					opcionesCopia.setOpcConvenientes(opc.getOpcConvenientes());
					em.persist(opcionesCopia);
					em.flush();

				}
			}
			/***
			 * 3. DET_CONTENIDO_FUNCIONAL
			 */

			List<DetContenidoFuncional> contenidoFuncionals =
				em.createQuery("select c from DetContenidoFuncional c "
					+ " where c.tipo like 'GRUPO'  and  c.concursoPuestoAgr.idConcursoPuestoAgr="
					+ idConcursoPuestoAgr).getResultList();

			List<DetContenidoFuncional> contenidoFuncionalCopia =
				new ArrayList<DetContenidoFuncional>();
			for (int i = 0; i < contenidoFuncionals.size(); i++) {
				DetContenidoFuncional cf =
					em.find(DetContenidoFuncional.class, contenidoFuncionals.get(i).getIdDetContenidoFuncional());
				DetContenidoFuncional contenidoFuncional = new DetContenidoFuncional();
				contenidoFuncional.setConcursoPuestoAgr(cf.getConcursoPuestoAgr());
				contenidoFuncional.setContenidoFuncional(cf.getContenidoFuncional());
				contenidoFuncional.setCpt(cf.getCpt());
				contenidoFuncional.setPlantaCargoDet(cf.getPlantaCargoDet());
				contenidoFuncional.setTipo("HOMOLOGADO");
				contenidoFuncional.setHomologacionPerfilMatriz(matriz);
				contenidoFuncional.setActivo(true);
				contenidoFuncional.setPuntaje(cf.getPuntaje());
				em.persist(contenidoFuncional);
				em.flush();
				contenidoFuncionalCopia.add(contenidoFuncional);
			}
			/***
			 * a. DET_DESCRIPCION_CONT_FUNCIONAL
			 */

			for (int i = 0; i < contenidoFuncionals.size(); i++) {
				List<DetDescripcionContFuncional> detContFuncionals =
					em.createQuery("Select d from DetDescripcionContFuncional d "
						+ " where d.activo= true and d.detContenidoFuncional.idDetContenidoFuncional="
						+ contenidoFuncionals.get(i).getIdDetContenidoFuncional()).getResultList();

				for (int j = 0; j < detContFuncionals.size(); j++) {// se realiza la copia
					DetDescripcionContFuncional fun =
						em.find(DetDescripcionContFuncional.class, detContFuncionals.get(j).getIdDetDescripcionContFuncional());
					DetDescripcionContFuncional funcionalCopia = new DetDescripcionContFuncional();
					funcionalCopia.setActivo(fun.getActivo());
					funcionalCopia.setDescripcion(fun.getDescripcion());
					funcionalCopia.setDetContenidoFuncional(contenidoFuncionalCopia.get(i));
					em.persist(funcionalCopia);
					em.flush();
				}

			}

			/**
			 * otras modificacion a HomologacionPerfilMatriz
			 */
			if (matriz != null) {
				matriz.setObjetivo(concursoPuestoAgr.getObjetivo());
				matriz.setDenominacion(concursoPuestoAgr.getDescripcionGrupo());
				matriz.setMision(concursoPuestoAgr.getMision());
				matriz.setContacto(concursoPuestoAgr.getContacto());
				matriz.setLugar(concursoPuestoAgr.getLugar());
				matriz.setPostHabilitados(concursoPuestoAgr.getPostHabilitados());
				matriz.setFechaLimite(concursoPuestoAgr.getFechaLimite());
				matriz.setCondLaborales(concursoPuestoAgr.getCondLaborales());
				matriz.setOtrasInf(concursoPuestoAgr.getOtrasInf());
				matriz.setRemuneracion(concursoPuestoAgr.getRemuneracion());
				matriz.setModalidad(concursoPuestoAgr.getModalidad());
				matriz.setMisionEspecifica(concursoPuestoAgr.getMisionEspecifica());
				matriz.setDocumentos(concursoPuestoAgr.getDocumentos());
				matriz.setHorario(concursoPuestoAgr.getHorario());
				matriz.setConfiguracionUoCab(concursoPuestoAgr.getConcurso().getConfiguracionUoCab());
				matriz.setPostNoHabilitados(concursoPuestoAgr.getPostNoHabilitados());
				matriz.setOtrosBeneficios(concursoPuestoAgr.getOtrosBeneficios());
				matriz.setFuenteFinanciacion(concursoPuestoAgr.getFuenteFinanciacion());
				matriz.setActivo(true);
				em.merge(matriz);
				em.flush();
			}

		
			findEstado();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			statusMessages.add(Severity.WARN, "Debe finalizar la tarea presionando el botón Siguiente Tarea");

			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, "Error inesperado");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private Integer genNroExpediente() {
		List<Integer> c =
			em.createNativeQuery("select max(nro_expediente) from  seleccion.concurso  "
				+ " where extract(year from fecha_expediente)=extract(year from now())").getResultList();
		if (c.isEmpty() || c.get(0) == null)
			return 1;

		return c.get(0) + 1;

	}

	@SuppressWarnings("unchecked")
	private EstadoDet eHomologado() {
		List<EstadoDet> dets =
			em.createQuery("Select d from EstadoDet d "
				+ " where d.descripcion like 'HOMOLOGADO' and "
				+ " d.estadoCab.descripcion like 'CONCURSO' ").getResultList();
		if (dets.isEmpty())
			return null;
		else
			return dets.get(0);
	}

	@SuppressWarnings("unchecked")
	private Integer maxNum() {
		List<Integer> num =
			em.createQuery("Select max(h.numero) from HomologacionPerfilMatriz h ").getResultList();
		if (!num.isEmpty() && num.get(0) != null)
			return num.get(0) + 1;
		else
			return 1;
	}

	@SuppressWarnings("unchecked")
	private Integer estadoHomologado() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'HOMOLOGADO'").getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	public String nextTask() {

		// Si esta en estado enviado a homologacion emite un mensaje
		if (enviadoHomologacion()) {
			statusMessages.add("Debe homologar el Grupo de Puestos antes de pasar a la sgte. tarea");
			return null;
		}

		
		if (observacion != null && observacion.getIdObservacion() != null) {
			observacion.setObservacion(obs);
			observacion.setFechaMod(new Date());
			observacion.setConcurso(em.find(Concurso.class, idConcurso));
			observacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			observacion.setIdTaskInstance(jbpmUtilFormController.getIdTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance()));
			em.merge(observacion);

		} else {
			observacion = new Observacion();
			observacion.setObservacion(obs);
			observacion.setFechaMod(new Date());
			observacion.setConcurso(em.find(Concurso.class, idConcurso));
			observacion.setFechaAlta(new Date());
			observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			observacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			observacion.setIdTaskInstance(jbpmUtilFormController.getIdTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance()));
			em.persist(observacion);

		}
		em.flush();
		// Se pasa a la siguiente tarea
		jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		jbpmUtilFormController.setActividadActual(ActividadEnum.CSI_APROBAR_HOMOLOG_SFP);

		if (esHomologado()) {
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CSI_ELABORAR_DOC_HOMOLOG);
			jbpmUtilFormController.setTransition("next2");
		} else if (esPendienteRevision()) {
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.HOMOLOGACION_OEE);
			jbpmUtilFormController.setTransition("next1");
		} else {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ocurrio un error al inicializar el proceso. Verifique el estado del grupo.");
			return "next";
		}

		if (jbpmUtilFormController.nextTask()) {
			em.flush();
		}

		return "next";
	}

	public Observacion cargarObservacion(long idTaskInstance) {
		try {
			String cad =
				" select o.* " + " from seleccion.observacion o " + " where id_task_instance = "
					+ idTaskInstance + " ";

			List<Observacion> lista = em.createNativeQuery(cad, Observacion.class).getResultList();
			if (lista.size() > 0)
				return lista.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public void imprimirPerfilMatriz() {
		reportUtilFormController.setNombreReporte("RPT_CU015_imprimir_perfil_matriz");
		reportUtilFormController.getParametros().put("idConcursoPuestoAgr", idConcursoPuestoAgr);
		reportUtilFormController.imprimirReportePdf();
	}

	public String back() {
		statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return "back";
	}

	public void anexar() {
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		entity = "HomologacionPerfilMatriz";
		direccionFisica =
			"//SICCA//" + anio + "//OEE//" + configuracionUoCab.getIdConfiguracionUo() + "//"
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
				+ idConcurso;

	}

	@SuppressWarnings("unchecked")
	public boolean enviadoHomologacion() {
		List<Referencias> ref =
			em.createQuery("select r from Referencias r " + " where r.dominio like 'ESTADOS_GRUPO'"
				+ " and r.descAbrev like 'ENVIADO A HOMOLOGACION'" + "  and r.valorNum ="
				+ concursoPuestoAgr.getEstado()).getResultList();

		return !ref.isEmpty();
	}

	@SuppressWarnings("unchecked")
	private void findEstado() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.valorNum ="
				+ concursoPuestoAgr.getEstado()).getResultList();
		if (!ref.isEmpty())
			estado = ref.get(0).getDescAbrev();
	}

	/**
	 * true es pendiente de revision
	 */
	@SuppressWarnings("unchecked")
	public boolean esPendienteRevision() {
		List<Referencias> ref =
			em.createQuery("select r from Referencias r " + " where r.dominio like 'ESTADOS_GRUPO'"
				+ " and r.descAbrev like 'PENDIENTE REVISION'" + "  and r.valorNum ="
				+ concursoPuestoAgr.getEstado()).getResultList();
		return !ref.isEmpty();
	}

	/**
	 * true es homologado
	 */
	@SuppressWarnings("unchecked")
	public boolean esHomologado() {
		List<Referencias> ref =
			em.createQuery("select r from Referencias r " + " where r.dominio like 'ESTADOS_GRUPO'"
				+ " and r.descAbrev like 'HOMOLOGADO'" + "  and r.valorNum ="
				+ concursoPuestoAgr.getEstado()).getResultList();
		return !ref.isEmpty();
	}

	// GETTERS Y SETTERS

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

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public ConcursoPuestoAgrList getConcursoPuestoAgrList() {
		return concursoPuestoAgrList;
	}

	public void setConcursoPuestoAgrList(ConcursoPuestoAgrList concursoPuestoAgrList) {
		this.concursoPuestoAgrList = concursoPuestoAgrList;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public boolean isEviadoHomo() {
		return eviadoHomo;
	}

	public void setEviadoHomo(boolean eviadoHomo) {
		this.eviadoHomo = eviadoHomo;
	}

	public Observacion getObservacion() {
		return observacion;
	}

	public void setObservacion(Observacion observacion) {
		this.observacion = observacion;
	}

	public HomologacionPerfilMatriz getHomologacionPerfilMatriz() {
		return homologacionPerfilMatriz;
	}

	public void setHomologacionPerfilMatriz(HomologacionPerfilMatriz homologacionPerfilMatriz) {
		this.homologacionPerfilMatriz = homologacionPerfilMatriz;
	}

}
