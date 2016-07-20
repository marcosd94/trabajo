package py.com.excelsis.sicca.circuitoCIO.session.form;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ComisionGrupo;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.MatrizDocConfigEnc;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PresentAclaracDoc;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;
import enums.ProcesoEnum;

@Scope(ScopeType.CONVERSATION)
@Name("modificarConcursoCU623FC")
public class ModificarConcursoCU623FC {
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
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	ConcursoPuestoAgrList concursoPuestoAgrList;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	@In(required = false, create = true)
	private Actor actor;

	private ConcursoPuestoAgr concursoPuestoAgr;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private ConcursoPuestoAgr grupoSeleccionado;

	private String observacion;
	private String codConcurso;
	private String direccionFisica;
	private Integer estadoCreado;

	private static final String ESTADO_CREADO = "CREADO EN ACTIVIDAD MODIFICAR";
	private List<ConcursoPuestoAgr> listaConcursoAgr;
	private List<PlantaCargoDet> listaCargos;
	private SeguridadUtilFormController seguridadUtilFormController;
	private String fromConcurso;
	private Long idConcursoPuestoAgr;

	/**
	 * Asigna los roles necesarios a la primera tarea
	 */
	private void asignarRolesTarea() {
		roles =
			seleccionUtilFormController.getRolesTarea(ActividadEnum.CIO_CARGA_GRUPO.getValor(), ProcesoEnum.CONCURSO.getValor());
	}

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		String estados =
			seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "FIRMADO HOMOLOGACION")
				+ "";
		estados +=
			"#"
				+ seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "CREADO EN ACTIVIDAD MODIFICAR");
		estados +=
			"#"
				+ seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "FINALIZADO CARGA");
		seguridadUtilFormController.verificarPerteneceOeeCIO(null, concursoPuestoAgr.getIdConcursoPuestoAgr(), estados, ActividadEnum.CIO_MODIFICAR_DATOS_CONCURSO.getValor());

	}

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		asignarRolesTarea();
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		concursoPuestoAgrHome.setInstance(concursoPuestoAgr);
		// concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
		estadoCreado = obtenerEstado();
		if (concurso != null) {
			validarOee();
			codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null) {
				codConcurso = codConcurso + " DE " + configuracionUoCab.getDescripcionCorta();
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			}
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}
		cargarDatos();
		obtenerDireccionFisica();

		fromConcurso =
			"circuitoCIO/modificarDatosConcurso/ModificarDatosConcursoList.seam?concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	private Integer obtenerEstado() {
		Referencias ref3 = new Referencias();
		ref3 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", ESTADO_CREADO);
		return ref3.getValorNum();
	}

	/**
	 * Busca el nivel correspondiente a la entidad
	 * 
	 * @param nenCodigo
	 * @return
	 */
	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {

		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}

	@SuppressWarnings("unchecked")
	private void cargarDatos() {
		String query = getQuery();
		listaConcursoAgr = concursoPuestoAgrList.listaResultadosCU414(query);
	}

	public String getQuery() {
		Referencias ref1 = new Referencias();
		ref1 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "FIRMADO HOMOLOGACION");
		Referencias ref2 = new Referencias();
		ref2 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "FINALIZADO CARGA");
		Referencias ref3 = new Referencias();
		ref3 =
			referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "CREADO EN ACTIVIDAD MODIFICAR");
		String query =
			"select concursoPuestoAgr from ConcursoPuestoAgr concursoPuestoAgr "
				+ "join concursoPuestoAgr.concurso concurso " + "where concurso.idConcurso = "
				+ concurso.getIdConcurso() + " and concursoPuestoAgr.activo is true "
				+ "and (concursoPuestoAgr.estado = " + ref1.getValorNum()
				+ " or (concursoPuestoAgr.estado = " + ref2.getValorNum()
				+ " and concursoPuestoAgr.homologar is false)" + " or concursoPuestoAgr.estado = "
				+ ref3.getValorNum() + ")";
		return query;
	}

	/**
	 * Método que obtiene el codigo del puesto, concatenado con la unidad organizativa cabeza unidad organizativa dependiente, entidad, nivel, orden
	 * 
	 * @param det
	 * @return
	 */
	public String obtenerCodigo(PlantaCargoDet det) {
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

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet, List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);

		return listCodigos;
	}

	public void actualizarGrupo(ConcursoPuestoAgr grupo) {
		grupoSeleccionado = new ConcursoPuestoAgr();
		Long idSel = grupo.getIdConcursoPuestoAgr();
		grupoSeleccionado = em.find(ConcursoPuestoAgr.class, idSel);
	}

	/**
	 * Obtiene la direccion fisica a ser usada en la adjuncion de documentos
	 */
	private void obtenerDireccionFisica() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String separador = File.separator;
		direccionFisica =
			separador + "SICCA" + separador + anho + separador + "OEE" + separador
				+ concurso.getConfiguracionUoCab().getIdConfiguracionUo() + separador
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + separador
				+ concurso.getIdConcurso();
	}

	@SuppressWarnings("unchecked")
	private Boolean verificarPcd(Long id) {
		String sql =
			"select datos.* " + "from seleccion.datos_grupo_puesto datos "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = datos.id_concurso_puesto_agr "
				+ "join seleccion.concurso concurso "
				+ "on concurso.id_concurso = agr.id_concurso "
				+ "where agr.id_concurso_puesto_agr = " + id;
		List<DatosGrupoPuesto> listaPcd = new ArrayList<DatosGrupoPuesto>();
		listaPcd = em.createNativeQuery(sql, DatosGrupoPuesto.class).getResultList();
		if (listaPcd == null || listaPcd.size() == 0)
			return false;
		else {
			for (DatosGrupoPuesto pcd : listaPcd) {
				if (pcd.getPreferenciaPersDiscapacidad() == null)
					return false;
			}
			return true;
		}
	}
	
	private Boolean cuentaConMatriz(Long id){
		String sql = "SELECT M.* " +
				"FROM SELECCION.MATRIZ_DOC_CONFIG_ENC M " +
				"WHERE M.ACTIVO IS TRUE " +
				"AND M.ID_CONCURSO_PUESTO_AGR = "+id;
		List<MatrizDocConfigEnc> listDoc = new ArrayList<MatrizDocConfigEnc>();
		listDoc = em.createNativeQuery(sql, MatrizDocConfigEnc.class).getResultList();
		if(listDoc.isEmpty())
			return false;
		return true;
	}

	public String nextTask() {

		if (!estaConcursoEnFecha()) {

			boolean presentacionConcurso = tienePresentacionConcurso(concurso.getIdConcurso());

			for (ConcursoPuestoAgr sel : listaConcursoAgr) {
				if (sel.getEstado().intValue() != estadoCreado.intValue()) {
					if (!cumpleFechaConcurso(sel.getIdConcursoPuestoAgr())) {
						statusMessages.clear();
						String mensaje =
							"Debe cargar fechas al grupo: " + sel.getDescripcionGrupo();
						statusMessages.add(Severity.ERROR, mensaje);

						return null;
					}
				}

//				if (!presentacionConcurso && !tienePresentacionGrupo(sel.getIdConcursoPuestoAgr())) {
//					statusMessages.clear();
//					String mensaje =
//						"Debe cargar Lugar de Presentación al grupo: " + sel.getDescripcionGrupo();
//					statusMessages.add(Severity.ERROR, mensaje);
//
//					return null;
//				}

				if (sel.getEstado().intValue() == estadoCreado.intValue()) {
					if (!cumpleComisionConcurso(sel.getIdConcursoPuestoAgr())
						|| !cumpleDatosPuesto(sel.getIdConcursoPuestoAgr())
						|| !cumpleFechaConcurso(sel.getIdConcursoPuestoAgr())) {
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU414_msg"));

						return null;
					}
					if(!cuentaConMatriz(sel.getIdConcursoPuestoAgr())){
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Debe configurar una matriz documental para el grupo");

						return null;
					}
					
					if (sel.getConcurso().getPcd() != null && !sel.getConcurso().getPcd()) {
						if (!verificarPcd(sel.getIdConcursoPuestoAgr())) {
							statusMessages.clear();
							String mensaje = "Debe cargar Preferencia de Personas con Discapacidad";
							statusMessages.add(Severity.ERROR, mensaje);

							return null;
						}
					}
					
					

				}
			}
		}

		try {

			Referencias ref = new Referencias();
			ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "MODIFICADO DATOS");
			
			if (ref != null) {
				int aux;
				EstadoDet estadoDet = seleccionUtilFormController
						.buscarEstadoDet("CONCURSO", "MODIFICADO DATOS");
				for (ConcursoPuestoAgr sel : listaConcursoAgr) {
					buscarCargos(sel.getIdConcursoPuestoAgr());
					aux = (ref.getValorNum() == 9) ? 15 : ref.getValorNum();
					sel.setEstado(aux);
					sel.setFechaEvalDocHasta(new Date());
					em.merge(sel);

					// Actualizar el estado de los puestos
					if (sel.getConcursoPuestoDets() != null) {
						for (ConcursoPuestoDet concursoPuestoDet : sel
								.getConcursoPuestoDets()) {
							concursoPuestoDet.setEstadoDet(estadoDet);
							em.merge(concursoPuestoDet);
						}
					}

					// Actualizar el estado de las plantas
					buscarCargos(sel.getIdConcursoPuestoAgr());
					for (PlantaCargoDet cargo : listaCargos) {
						cargo.setEstadoCab(estadoDet.getEstadoCab());
						cargo.setEstadoDet(estadoDet);
						cargo.setUsuMod(usuarioLogueado.getCodigoUsuario());
						cargo.setFechaMod(new Date());
						em.merge(cargo);
					}
					// em.flush();
				}
			}
			for (ConcursoPuestoAgr sel : listaConcursoAgr) {
				Concurso conc = new Concurso();
				conc = sel.getConcurso();
				if (conc.getFechaExpediente() == null && conc.getNroExpediente() == null) {
					conc.setFechaExpediente(new Date());
					conc.setNroExpediente(obtenerNroExpediente());
					conc.setUsuMod(usuarioLogueado.getCodigoUsuario());
					conc.setFechaMod(new Date());
					em.merge(conc);
					// em.flush();

				}

				// Se guardan las observaciones para cada grupo - INCIDENCIA 1002
				if (observacion != null && !observacion.trim().isEmpty()) {
					Observacion tablaObservacion = new Observacion();
					tablaObservacion.setConcurso(concurso);
					tablaObservacion.setObservacion(observacion);

					tablaObservacion.setFechaAlta(new Date());
					tablaObservacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					tablaObservacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
					tablaObservacion.setIdTaskInstance(jbpmUtilFormController.getIdTaskInstanceActual(sel.getIdProcessInstance()));
					em.persist(tablaObservacion);
				}

			}
			

			/* Incidencia 801 */
			/***** GENERA EL NRO_EXPEDIENTE */
			cargarNroExpediente();
			/***** Finalizar el CONCURSO */
			Referencias estadoCerrado =
				referenciasUtilFormController.getReferencia("ESTADOS_CONCURSO", "CERRADO GRUPOS");
		//	if (finalizarConcurso(concurso.getIdConcurso())) {
				concurso.setEstado(estadoCerrado.getValorNum());
				concurso.setFechaFinalizacion(new Date());
				concurso.setUsuFinalizacion(usuarioLogueado.getCodigoUsuario());
				concurso = em.merge(concurso);
		//	}
			/****** Publicar */
			if (concurso.getEstado().intValue() == estadoCerrado.getValorNum().intValue()) {
				insertarPublicacionPortal(concursoPuestoAgr.getIdConcursoPuestoAgr(), concurso.getIdConcurso(), genTextoPublicacion());
			}
			/****** Fin Incidencia 801 ********** */
			// Se pasa a la siguiente tarea
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
			jbpmUtilFormController.setActividadActual(ActividadEnum.CIO_MODIFICAR_DATOS_CONCURSO);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CIO_RECIBIR_POSTULACIONES);
			jbpmUtilFormController.setTransition("next");

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "next";
	}

	private boolean tienePresentacionConcurso(Long idConcurso) {
		String sql =
			"select * from seleccion.present_aclarac_doc " + "where id_concurso = " + idConcurso;
		List<PresentAclaracDoc> lista =
			em.createNativeQuery(sql, PresentAclaracDoc.class).getResultList();
		if (lista != null && lista.size() > 0)
			return true;

		return false;
	}

	private boolean tienePresentacionGrupo(Long idGrupo) {
		String sql =
			"select * from seleccion.present_aclarac_doc " + "where id_concurso_puesto_agr = "
				+ idGrupo;
		List<PresentAclaracDoc> lista =
			em.createNativeQuery(sql, PresentAclaracDoc.class).getResultList();
		if (lista != null && lista.size() > 0)
			return true;

		return false;
	}

	private Integer genNroExpediente() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String query =
			"select max(concurso.nro_expediente)" + " from seleccion.concurso concurso "
				+ " where " + "   concurso.anio = " + sdf.format(new Date())
				+ "   and concurso.activo is true";
		List lista = em.createNativeQuery(query).getResultList();

		return (Integer) lista.get(0);
	}

	private void cargarNroExpediente() {
		if (concurso.getNroExpediente() == null && concurso.getFechaExpediente() == null) {
			concurso.setNroExpediente(genNroExpediente() + 1);
			concurso.setFechaExpediente(new Date());
			concurso = em.merge(concurso);
		}
	}

	public void insertarPublicacionPortal(Long idConcursoPuestoAgr, Long idConcurso, String texto) {
		PublicacionPortal entity = new PublicacionPortal();
		entity.setFechaAlta(new Date());
		entity.setUsuAlta(AdmFecRecPosFC.SYSTEM_USR);
		entity.setActivo(true);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		entity.setConcurso(new Concurso());
		entity.getConcurso().setIdConcurso(idConcurso);
		entity.setTexto(texto);
		em.persist(entity);
	}

	public String genTextoPublicacion() {
		String texto;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaString = sdf.format(new Date()).toString();
		FechasGrupoPuesto fecha = (FechasGrupoPuesto) em
				.createNativeQuery(
						"select * from seleccion.fechas_grupo_puesto where "
								+ "seleccion.fechas_grupo_puesto.id_concurso_puesto_agr = "
								+ listaConcursoAgr.get(0)
										.getIdConcursoPuestoAgr(),
						FechasGrupoPuesto.class).getSingleResult();
		String hr = "<hr>";
		String h1O = "<h1>";
		String h1C = "</h1>";
		String br = "</br>";
		texto = hr + fechaString + h1O + "Período de Postulación:  "
				+ sdf.format(fecha.getFechaRecepcionDesde()) + " - "
				+ sdf.format(fecha.getFechaRecepcionHasta()) + h1C + br;
		return texto;
	}

	public Boolean finalizarConcurso(Long idConcurso) {
		String query =
			"SELECT GRUPO.DESCRIPCION_GRUPO FROM SELECCION.CONCURSO_PUESTO_AGR GRUPO"
				+ " WHERE GRUPO.ACTIVO = TRUE AND GRUPO.ESTADO IN   ("
				+ "   SELECT REF.VALOR_NUM FROM SELECCION.REFERENCIAS REF"
				+ "        WHERE REF.DOMINIO = 'ESTADOS_GRUPO' AND DESC_LARGA IN ('INICIADO CIRCUITO', "
				+ "                            'FINALIZADO CARGA', 'ENVIADO A HOMOLOGACION', 'PENDIENTE REVISION', "
				+ "                            'HOMOLOGADO', 'CON DOCUMENTOS DE HOMOLOGACION', 'FIRMADO HOMOLOGACION'))"
				+ "  AND GRUPO.ID_CONCURSO = " + idConcurso;
		List lista = em.createNativeQuery(query).getResultList();
		if (lista.size() == 0) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Integer obtenerNroExpediente() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String sql =
			"select max(nro_expediente) as nro " + "from seleccion.concurso concurso "
				+ "where extract(year from concurso.fecha_expediente) = " + anho;

		List<Object[]> config = em.createNativeQuery(sql).getResultList();
		if (config == null) {
			return 1;
		}
		Object obj1 = config.get(0);
		if (obj1 == null)
			return 1;
		String v = obj1.toString();
		Integer valor = new Integer(v);
		valor++;
		return valor;
	}

	@SuppressWarnings("unchecked")
	private void buscarCargos(Long id) {
		String sql =
			"select cargo.* " + "from planificacion.planta_cargo_det cargo "
				+ "join seleccion.concurso_puesto_det puesto_det "
				+ "on puesto_det.id_planta_cargo_det = cargo.id_planta_cargo_det "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = puesto_det.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = " + id + " and cargo.activo is true";
		try {
			listaCargos = new ArrayList<PlantaCargoDet>();
			listaCargos = em.createNativeQuery(sql, PlantaCargoDet.class).getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	@SuppressWarnings("unchecked")
	private Boolean estaConcursoEnFecha() {
		String sql =
			"select fech_agr.*  " + "from seleccion.fechas_grupo_puesto fech_agr  "
				+ "join seleccion.concurso conc  " + "on conc.id_concurso = fech_agr.id_concurso "
				+ "where conc.id_concurso = " + concurso.getIdConcurso();
		List<FechasGrupoPuesto> listaFec = new ArrayList<FechasGrupoPuesto>();
		listaFec = em.createNativeQuery(sql, FechasGrupoPuesto.class).getResultList();
		if (listaFec != null && listaFec.size() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	private Boolean cumpleFechaConcurso(Long id) {
		String sql =
			"select fech_agr.* " + "from seleccion.fechas_grupo_puesto fech_agr "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = fech_agr.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = " + id;
		try {
			List<FechasGrupoPuesto> listaFec = new ArrayList<FechasGrupoPuesto>();
			listaFec = em.createNativeQuery(sql, FechasGrupoPuesto.class).getResultList();
			if (listaFec.size() > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Boolean cumpleComisionConcurso(Long id) {
		String sql =
			"select com_grupo.* " + "from seleccion.comision_grupo com_grupo "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = com_grupo.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = " + id;
		try {
			List<ComisionGrupo> listaComision = new ArrayList<ComisionGrupo>();
			listaComision = em.createNativeQuery(sql, ComisionGrupo.class).getResultList();
			if (listaComision.size() > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Boolean cumpleDatosPuesto(Long id) {
		String sql =
			"select dato.* " + "from seleccion.datos_grupo_puesto dato "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = dato.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = " + id;
		try {
			List<DatosGrupoPuesto> listaDatos = new ArrayList<DatosGrupoPuesto>();
			listaDatos = em.createNativeQuery(sql, DatosGrupoPuesto.class).getResultList();
			if (listaDatos.size() > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return false;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
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

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getCodConcurso() {
		return codConcurso;
	}

	public void setCodConcurso(String codConcurso) {
		this.codConcurso = codConcurso;
	}

	public List<ConcursoPuestoAgr> getListaConcursoAgr() {
		return listaConcursoAgr;
	}

	public void setListaConcursoAgr(List<ConcursoPuestoAgr> listaConcursoAgr) {
		this.listaConcursoAgr = listaConcursoAgr;
	}

	public ConcursoPuestoAgr getGrupoSeleccionado() {
		return grupoSeleccionado;
	}

	public void setGrupoSeleccionado(ConcursoPuestoAgr grupoSeleccionado) {
		this.grupoSeleccionado = grupoSeleccionado;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public List<PlantaCargoDet> getListaCargos() {
		return listaCargos;
	}

	public void setListaCargos(List<PlantaCargoDet> listaCargos) {
		this.listaCargos = listaCargos;
	}

	public Integer getEstadoCreado() {
		return estadoCreado;
	}

	public void setEstadoCreado(Integer estadoCreado) {
		this.estadoCreado = estadoCreado;
	}

	public String getFromConcurso() {
		return fromConcurso;
	}

	public void setFromConcurso(String fromConcurso) {
		this.fromConcurso = fromConcurso;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

}
