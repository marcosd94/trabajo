package py.com.excelsis.sicca.seleccion.session.form;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

import py.com.excelsis.sicca.entity.ComisionGrupo;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.ReglaDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import enums.ActividadEnum;

@Name("admFecRecPosFC423")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class AdmFecRecPosFC423 {

	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(required = false, create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false, create = true)
	private Actor actor;
	@In(required = false, create = true)
	@Logger
	Log log;

	ReponerCategoriasController reponerCategoriasController;
	private String ESTADO_VERIFICADO = "VERIFICADO";
	private String ESTADO_A_VERIFICAR = "A VERIFICAR";
	private String DOMINIO_REFERENCIA = "ESTADOS_GRUPO";

	private String DSC_ABREV_EVALUAR = "A EVALUAR DOCUMENTOS";
	private String DSC_ABREV_COMPLETAR_CARPETAS = "A COMPLETAR CARPETAS";
	private String DSC_ABREV_PRORROGAR = "A PRORROGAR";
	private String DSC_GRUPO_DESIERTO = "GRUPO DESIERTO";
	private String DSC_PUBLICADO = "PUBLICADO";
	private String DSC_RECIBIR_POSTULACION = "RECIBIR POSTULACION";

	private Integer idEstadoRefEvaluar;
	private Integer idEstadoRefCompletarCarpetas;
	private Integer idEstadoRefPublicado;
	private Integer idEstadoRefListaLarga;
	private Integer idEstadoRefASortear;
	private Integer idEstadoRefProrrogar;
	private Integer idEstadoRefRecibirPostulacion;
	private Integer idEstadoRefPublicacion;
	private Integer idEstadoGrupoDesierto;
	private Integer idEstadoDetLibre;

	private Integer idEstadoDetRecibirPostulacion;
	private Integer idEstadoDetListaLarga;
	private Integer idEstadoDetASortear;
	private Integer idEstadoDetProrrogarPuestos;
	private Integer idEstadoDetEvaluarPuestos;
	private Integer idEstadoDetDesierto;
	private Integer idEstadoDetCompletarCarpetas;
	private Long idEstadoCabVacante;
	public static String SYSTEM_USR = "SYSTEM1";
	SeleccionUtilFormController seleccionUtilFormController;

	public void init() {
		seleccionUtilFormController = (SeleccionUtilFormController) Component
				.getInstance(SeleccionUtilFormController.class, true);
		// Las referencias
		if (idEstadoRefCompletarCarpetas == null)
			idEstadoRefCompletarCarpetas = calcIdEstadosReferencia(
					DOMINIO_REFERENCIA, DSC_ABREV_COMPLETAR_CARPETAS);
		if (idEstadoRefEvaluar == null)
			idEstadoRefEvaluar = calcIdEstadosReferencia(DOMINIO_REFERENCIA,
					DSC_ABREV_EVALUAR);
		if (idEstadoRefProrrogar == null)
			idEstadoRefProrrogar = calcIdEstadosReferencia(DOMINIO_REFERENCIA,
					DSC_ABREV_PRORROGAR);
		if (idEstadoGrupoDesierto == null)
			idEstadoGrupoDesierto = calcIdEstadosReferencia(DOMINIO_REFERENCIA,
					DSC_GRUPO_DESIERTO);
		if (idEstadoRefPublicado == null)
			idEstadoRefPublicado = calcIdEstadosReferencia(DOMINIO_REFERENCIA,
					DSC_PUBLICADO);
		if (idEstadoRefListaLarga == null)
			idEstadoRefListaLarga = calcIdEstadosReferencia(DOMINIO_REFERENCIA,
					"LISTA LARGA");
		if (idEstadoRefASortear == null)
			idEstadoRefASortear = calcIdEstadosReferencia(DOMINIO_REFERENCIA,
					"REALIZAR SORTEO");
		if (idEstadoRefRecibirPostulacion == null)
			idEstadoRefRecibirPostulacion = calcIdEstadosReferencia(
					DOMINIO_REFERENCIA, DSC_RECIBIR_POSTULACION);
		
		if (idEstadoRefPublicacion == null) 
			idEstadoRefPublicacion = calcIdEstadosReferencia(
					DOMINIO_REFERENCIA, "PUBLICACION");

		// Los estados_det

		if (idEstadoDetListaLarga == null)
			idEstadoDetListaLarga = calcIdEstadosDet("CONCURSO", "LISTA LARGA");
		if (idEstadoDetASortear == null)
			idEstadoDetASortear = calcIdEstadosDet("CONCURSO",
					"REALIZAR SORTEO");

		if (idEstadoDetLibre == null)
			idEstadoDetLibre = calcIdEstadosDet("CONCURSO", "LIBRE");
		if (idEstadoCabVacante == null)
			idEstadoCabVacante = seleccionUtilFormController.buscarEstadoCab(
					"VACANTE").getIdEstadoCab();
		if (idEstadoDetDesierto == null)
			idEstadoDetDesierto = calcIdEstadosDet("CONCURSO", "DESIERTO");
		if (idEstadoDetRecibirPostulacion == null)
			idEstadoDetRecibirPostulacion = calcIdEstadosDet("CONCURSO",
					"RECIBIR POSTULACION");
		if (idEstadoDetEvaluarPuestos == null)
			idEstadoDetEvaluarPuestos = calcIdEstadosDet("CONCURSO",
					"A EVALUAR DOCUMENTOS");
		if (idEstadoDetProrrogarPuestos == null)
			idEstadoDetProrrogarPuestos = calcIdEstadosDet("CONCURSO",
					"A PRORROGAR");
		if (idEstadoDetCompletarCarpetas == null)
			idEstadoDetCompletarCarpetas = calcIdEstadosDet("CONCURSO",
					"A COMPLETAR CARPETAS");

		// Asignar Actor para el jBPM
		actor.setId(SYSTEM_USR);
	}

	/**
	 * Trae los candidatos para ser iniciados a la actividad 11
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<FechasGrupoPuesto> lActIniciar() {
		Query q = em
				.createQuery("select FechasGrupoPuesto from FechasGrupoPuesto FechasGrupoPuesto "
						+ " where FechasGrupoPuesto.activo is true "
						+ "and FechasGrupoPuesto.estado='"
						+ ESTADO_A_VERIFICAR
						+ "' and FechasGrupoPuesto.concursoPuestoAgr.estado =  "
						+ idEstadoRefPublicado
						+ " and FechasGrupoPuesto.fechaRecepcionDesde <= :fechaSistema  ");
		Date fechaSistema = new Date();
		q.setParameter("fechaSistema", fechaSistema, TemporalType.DATE);

		List<FechasGrupoPuesto> lista = q.getResultList();
		return lista;
	}

	/**
	 * Trae los candidatos para ser finalizados de la actividad 11
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<FechasGrupoPuesto> lActFinalizar() {

		Query q = em
				.createQuery("select FechasGrupoPuesto from FechasGrupoPuesto FechasGrupoPuesto "
						+ " where FechasGrupoPuesto.activo is true "
						+ "and FechasGrupoPuesto.estado='"
						+ ESTADO_A_VERIFICAR
						+ "' and FechasGrupoPuesto.concursoPuestoAgr.estado =  "
						+ idEstadoRefRecibirPostulacion
						+ " AND ((fechasGrupoPuesto.fechaRecepcionHasta = :fechaSistema and fechasGrupoPuesto.horaRecepcionHasta <= :horaSistema ) "
						+ "				OR (fechasGrupoPuesto.fechaRecepcionHasta < :fechaSistema ))   "
						
						
//						+ " and FechasGrupoPuesto.fechaRecepcionHasta <= :fechaSistema "
//						+ "and FechasGrupoPuesto.horaRecepcionHasta < :horaSistema"
						);
		Date fechaSistema = new Date();

		q.setParameter("fechaSistema", fechaSistema, TemporalType.DATE);
		q.setParameter("horaSistema", fechaSistema, TemporalType.TIME);

		List<FechasGrupoPuesto> lista = q.getResultList();
		return lista;
	}
	
	
	/**
	 * Trae los candidatos para ser finalizados de la actividad 11
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<FechasGrupoPuesto> listaConcursoParaPublicacion() {

		Query q = em
				.createQuery("select FechasGrupoPuesto from FechasGrupoPuesto FechasGrupoPuesto "
						+ " where FechasGrupoPuesto.activo is true "
						+ " and FechasGrupoPuesto.concursoPuestoAgr.estado =  "
						+ idEstadoRefPublicacion
						+ " and FechasGrupoPuesto.fechaRecepcionHasta < :fechaSistema ");
		Date fechaSistema = new Date();

		q.setParameter("fechaSistema", fechaSistema, TemporalType.DATE);
		
		
		
		List<FechasGrupoPuesto> lista = q.getResultList();
		//List<FechasGrupoPuesto> lista = em.createNativeQuery(sql,FechasGrupoPuesto.class).getResultList();
		 q = em
				.createQuery("select FechasGrupoPuesto from FechasGrupoPuesto FechasGrupoPuesto "
						+ " where FechasGrupoPuesto.activo is true "
						+ " and FechasGrupoPuesto.concursoPuestoAgr.estado =  "
						+ idEstadoRefPublicacion
						+ " and FechasGrupoPuesto.fechaRecepcionHasta = :fechaSistema "
						+ "and FechasGrupoPuesto.horaRecepcionHasta < :horaSistema ");
	
		 q.setParameter("fechaSistema", fechaSistema, TemporalType.DATE);
		 q.setParameter("horaSistema", fechaSistema, TemporalType.TIME);
		List<FechasGrupoPuesto> lista2  = q.getResultList();
		
		for (FechasGrupoPuesto o : lista2) {
			lista.add(o);
		}
		
		return lista;
	}

	// AGREGADO JD
	private Integer cantPuestos(Long idGrupo) {
		Query q = em
				.createQuery(" select count(concursoPuestoDet) from ConcursoPuestoDet concursoPuestoDet "
						+ "where concursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idGrupo);
		Long count = (Long) q.getResultList().get(0);
		return count.intValue();
	}

	private Integer cantPostulantes(Long idGrupo) {
		Query q = em
				.createQuery("select count(Postulacion) from Postulacion Postulacion "
						+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idGrupo
						+ " and Postulacion.usuCancelacion is null "
						+ " and Postulacion.fechaCancelacion is null and Postulacion.activo is true ");
		Long count = (Long) q.getResultList().get(0);
		return count.intValue();
	}

	private Integer cantPostulantesCarpeta(Long idGrupo) {
		Query q = em
				.createQuery("select count(Postulacion) from Postulacion Postulacion "
						+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idGrupo
						+ " and Postulacion.tipo = 'CARPETA'"
						+ " and Postulacion.usuCancelacion is null "
						+ " and Postulacion.fechaCancelacion is null and Postulacion.activo is true ");
		Long count = (Long) q.getResultList().get(0);
		return count.intValue();
	}

	private List<Postulacion> lPostulantesCarpeta(Long idGrupo) {
		Query q = em
				.createQuery("select Postulacion from Postulacion Postulacion "
						+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idGrupo
						+ " and Postulacion.tipo = 'CARPETA'"
						+ " and Postulacion.usuCancelacion is null "
						+ " and Postulacion.fechaCancelacion is null and Postulacion.activo is true ");

		return q.getResultList();
	}

	private Boolean isPrimeraProrroga(FechasGrupoPuesto entity) {
		if (entity.getFechaRecepcionDesdeAnt() == null
				&& entity.getFechaRecepcionHastaAnt() == null
				&& entity.getHoraRecepcionHastaAnt() == null
				&& entity.getHoraRecepcionDesdeAnt() == null) {
			return true;
		}
		return false;
	}

	public List<ComisionSeleccionDet> traerDestinatarios(Long idGrupo) {
		Query q = em
				.createQuery("select ComisionGrupo from ComisionGrupo ComisionGrupo where ComisionGrupo.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idGrupo);
		ComisionGrupo comisionGrupo = (ComisionGrupo) q.getSingleResult();
		q = em.createQuery("select ComisionSeleccionDet from ComisionSeleccionDet ComisionSeleccionDet where ComisionSeleccionDet.comisionSeleccionCab.idComisionSel = "
				+ comisionGrupo.getComisionSeleccionCab().getIdComisionSel());

		return q.getResultList();
	}

	public List<Postulacion> traerDestinatarios2(Long idGrupo) {
		Query q = em
				.createQuery("select Postulacion from Postulacion Postulacion"
						+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idGrupo + " and Postulacion.activo is true "
						+ " and Postulacion.personaPostulante.activo is true "
						+ " and Postulacion.usuCancelacion is null "
						+ " and Postulacion.fechaCancelacion is null ");

		return q.getResultList();
	}

	private void enviarMail(ConcursoPuestoAgr concursoPuestoAgr) {
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<ComisionSeleccionDet> lDestinatarios = traerDestinatarios(concursoPuestoAgr
				.getIdConcursoPuestoAgr());
		String dirEmail = null;
		for (ComisionSeleccionDet o : lDestinatarios) {
			dirEmail = o.getPersona().getEMail();
			String asunto = "Declarar Desierto el Concurso o hacer Prorroga por Excepción – con Aprobación de la SFP";
			String texto = "";
			try {
				texto = texto
						+ "<p align=\"justify\"> <b> Estimado(a) Integrante de la Comisi&oacute;n de Selecci&oacute;n    "
						+ "<p align=\"justify\">Le comunicamos que el Grupo "
						+ concursoPuestoAgr.getDescripcionGrupo()
						+ " del Concurso "
						+ concursoPuestoAgr.getConcurso().getNombre()
						+ " ha sido declarado Desierto porque tuvo una Pr&oacute;rroga, y no ha reunido la cantidad de Postulantes suficientes."
						+ "</p>"
						+ "<p></p>"
						+ "<p> Atentamente,</p> "
						+ "<b>"
						+ concursoPuestoAgr.getConcurso()
								.getConfiguracionUoCab()
								.getDenominacionUnidad() + "</p></b>";

				usuarioPortalFormController.enviarMails(dirEmail, texto,
						asunto, null);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void enviarMail2(ConcursoPuestoAgr concursoPuestoAgr) {
		List<Postulacion> lDestinatarios = traerDestinatarios2(concursoPuestoAgr
				.getIdConcursoPuestoAgr());
		String dirEmail = null;
		for (Postulacion o : lDestinatarios) {
			dirEmail = o.getPersonaPostulante().getPersona().getEMail();
			//Modificado RVeron, 20140416
			//String asunto = "NOTIF_PUESTO_DESIERTO_"
			String asunto = "Declarar Desierto el Concurso";
					//+ concursoPuestoAgr.getConcurso().getNombre() + "_"
					//+ concursoPuestoAgr.getDescripcionGrupo();
			String texto = "";
			try {
				texto = texto
						+ "<p align=\"justify\"> <b> Estimado(a) "
						+ o.getPersonaPostulante().getNombres()
						+ " "
						+ o.getPersonaPostulante().getApellidos()
						+ ":"
						+ "</b></p>"
						+ "<p align=\"justify\">Le comunicamos que el Puesto de "
						+ concursoPuestoAgr.getDescripcionGrupo()
						+ " del Concurso "
						+ concursoPuestoAgr.getConcurso().getNombre()
						//Modificado RVeron, 20140416
						//+ " ha sido declarado Desierto porque no ha reunido la cantidad de Postulantes suficientes."
						+ " ha sido declarado desierto en raz&oacute;n de que de acuerdo a las Evaluaciones Curriculares y Entrevistas realizadas ning&uacute;n postulante reune el perfil requerido en las Bases y Condiciones."
						+ "</p>"
						+ "<p align=\"justify\">Le agradecemos el inter&eacute;s que ha mostrado para este Concurso, y deseamos poder contar con Usted para nuestros pr&oacute;ximos concursos. </p>"
						+ "<p></p>"
						+ "<p> Atentamente,</p> "
						+ "<b>"
						+ concursoPuestoAgr.getConcurso()
								.getConfiguracionUoCab()
								.getDenominacionUnidad() + "</p></b>";

				usuarioPortalFormController.enviarMails(dirEmail, texto,
						asunto, null);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void pasarElConcursoDeRicardo(ActividadEnum actividadActual,
			ActividadEnum sgteActividad, ConcursoPuestoAgr concursoPuestoAgr,
			String transition) {

		jbpmUtilFormController = (JbpmUtilFormController) Component
				.getInstance(JbpmUtilFormController.class, true);

		jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		jbpmUtilFormController.setActividadActual(actividadActual);
		jbpmUtilFormController.setActividadSiguiente(sgteActividad);
		jbpmUtilFormController.setTransition("next2");

		jbpmUtilFormController.nextTask();
	}

	private void nextTask(ActividadEnum actividadActual,
			ActividadEnum sgteActividad, ConcursoPuestoAgr concursoPuestoAgr,
			String transition) {

		jbpmUtilFormController = (JbpmUtilFormController) Component
				.getInstance(JbpmUtilFormController.class, true);

		jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		jbpmUtilFormController.setActividadActual(actividadActual);
		jbpmUtilFormController.setActividadSiguiente(sgteActividad);
		jbpmUtilFormController.setTransition(transition);

		jbpmUtilFormController.nextTask();
	}

	private Integer calcIdEstadosReferencia(String dominio, String descAbrev) {
		Query q = em
				.createQuery("select Referencias from Referencias Referencias "
						+ "  where Referencias.activo is true and Referencias.dominio ='"
						+ dominio + "' and Referencias.descAbrev ='"
						+ descAbrev + "'");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorNum().intValue();
	}

	public Integer calcIdEstadosDet(String cabecera, String detalle) {
		Query q = em.createQuery("select EstadoDet from EstadoDet EstadoDet "
				+ "  where EstadoDet.estadoCab.descripcion ='" + cabecera
				+ "' and EstadoDet.activo is true and EstadoDet.descripcion ='"
				+ detalle + "'");
		EstadoDet ref = (EstadoDet) q.getSingleResult();
		return ref.getIdEstadoDet().intValue();
	}

	// AGREGADO POR JD
	private Date calcNuevaFechaRecepcion(Concurso concurso)
			throws ParseException {
		Long idCUO = concurso.getConfiguracionUoCab().getIdConfiguracionUo();
		Integer cantDias = cantDiasProrroga(idCUO);

		Date fechaRetorno;
		Date hoy = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c2 = Calendar.getInstance();

		c2.setTime(hoy);
		c2.add(Calendar.DATE, cantDias);

		String aux = sdf.format(c2.getTime());
		fechaRetorno = sdf.parse(aux);
		return fechaRetorno;

	}

	// AGREGADO POR JD
	private Integer cantDiasProrroga(Long idCUO) throws ParseException {
		Integer cantDias = null;
		Calendar c;
		Date fechaAux;
		Date hoy = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Query q = em
				.createNativeQuery("select valor_num from seleccion.referencias "
						+ "where dominio like 'PRORROGA_CONC' and activo=true  "
						+ "and valor_alf like 'CPO' ");
		cantDias = (Integer) q.getResultList().get(0);

		for (int i = 1; i <= cantDias; i++) {
			c = Calendar.getInstance();
			c.setTime(hoy);
			c.add(Calendar.DATE, i);
			String aux = sdf.format(c.getTime());
			fechaAux = sdf.parse(aux);

			if (!fechaTrabajaOee(fechaAux, idCUO)) {
				cantDias += 1;
			}
		}
		return cantDias;
	}

	// AGREGADO POR JD
	private Integer cantProrrogas() {
		Query q = em
				.createNativeQuery("select valor_num from seleccion.referencias "
						+ "where dominio like 'PRORROGA_CONC_NUM' and activo=true;");
		Integer count = (Integer) q.getResultList().get(0);
		return count;
	}

	/**
	 * traido de FechasGrupoFormController Indica si una OEE trabaja o no en una
	 * fecha teniendo en cuenta su calendario y los feriados.
	 * 
	 * @param fecha
	 *            : fecha en que se desea saber si trabaja o no la OEE.
	 * @param idConfiguracionUo
	 *            : id de la OEE en cuestion.
	 * @return
	 */
	public boolean fechaTrabajaOee(Date fecha, Long idConfiguracionUo) {
		String consulta = " select proceso.fecha_trabaja_oee (:fecha, :idConfiguracionUo) ";
		Query q = em.createNativeQuery(consulta);
		q.setParameter("fecha", fecha);
		q.setParameter("idConfiguracionUo", idConfiguracionUo);

		List lista = q.getResultList();
		if (lista != null && lista.size() > 0)
			return (Boolean) lista.get(0);

		return false;
	}

	private FechasGrupoPuesto actualizarPuesto(FechasGrupoPuesto entity,
			Integer idEstado) {

		List<ConcursoPuestoDet> lista = seleccionUtilFormController
				.getConcursoPuestoDet(entity.getConcursoPuestoAgr()
						.getIdConcursoPuestoAgr());
		for (ConcursoPuestoDet o : lista) {
			if (o.isActivo()) {
				o.setEstadoDet(new EstadoDet());
				o.getEstadoDet().setIdEstadoDet(new Long(idEstado));
				o.setActivo(true);
				o.setFechaMod(new Date());
				o.setUsuMod(SYSTEM_USR);
				em.merge(o);
				o.getPlantaCargoDet().setEstadoDet(new EstadoDet());
				o.getPlantaCargoDet().getEstadoDet()
						.setIdEstadoDet(new Long(idEstado));
				o.getPlantaCargoDet().setFechaMod(new Date());
				o.getPlantaCargoDet().setUsuMod(SYSTEM_USR);
				em.merge(o.getPlantaCargoDet());
			}
		}

		return entity;
	}

	private FechasGrupoPuesto actualizarPuestoDesierto(FechasGrupoPuesto entity) {
		Query q = em
				.createQuery("select ConcursoPuestoDet from ConcursoPuestoDet ConcursoPuestoDet "
						+ "where ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ entity.getConcursoPuestoAgr()
								.getIdConcursoPuestoAgr());

		List<ConcursoPuestoDet> lista = q.getResultList();
		for (ConcursoPuestoDet o : lista) {
			o.setEstadoDet(new EstadoDet());
			o.getEstadoDet().setIdEstadoDet(new Long(idEstadoDetDesierto));
			o.setActivo(false);
			o.setFechaMod(new Date());
			o.setUsuMod(SYSTEM_USR);
			em.merge(o);
			o.getPlantaCargoDet().setEstadoDet(new EstadoDet());
			o.getPlantaCargoDet().getEstadoDet()
					.setIdEstadoDet(new Long(idEstadoDetLibre));
			o.getPlantaCargoDet().setEstadoCab(new EstadoCab());
			o.getPlantaCargoDet().getEstadoCab()
					.setIdEstadoCab(new Long(idEstadoCabVacante));
			o.getPlantaCargoDet().setFechaMod(new Date());
			o.getPlantaCargoDet().setUsuMod(SYSTEM_USR);
			em.merge(o.getPlantaCargoDet());
		}
		try {
			em.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return entity;
	}

	public FechasGrupoPuesto actualizarEstado(FechasGrupoPuesto entity,
			String tipo) {

		if (tipo.equalsIgnoreCase("INICIAR_TAREA")) {
			entity.getConcursoPuestoAgr().setEstado(
					idEstadoRefRecibirPostulacion);
			entity.getConcursoPuestoAgr().setFechaMod(new Date());
			entity.getConcursoPuestoAgr().setUsuMod(SYSTEM_USR);
			actualizarPuesto(entity, idEstadoDetRecibirPostulacion);
		} else if (tipo.equalsIgnoreCase("LISTA_LARGA")) {
			entity.getConcursoPuestoAgr().setEstado(idEstadoRefListaLarga);
			entity.getConcursoPuestoAgr().setFechaMod(new Date());
			entity.getConcursoPuestoAgr().setUsuMod(SYSTEM_USR);
			actualizarPuesto(entity, idEstadoDetListaLarga);
			entity.setEstado(ESTADO_VERIFICADO);
		} else if (tipo.equalsIgnoreCase("SORTEO")) {
			entity.getConcursoPuestoAgr().setEstado(idEstadoRefASortear);
			entity.getConcursoPuestoAgr().setFechaMod(new Date());
			entity.getConcursoPuestoAgr().setUsuMod(SYSTEM_USR);
			actualizarPuesto(entity, idEstadoDetASortear);
			entity.setEstado(ESTADO_VERIFICADO);
		} else if (tipo.equalsIgnoreCase("EVALUAR")) {
			entity.getConcursoPuestoAgr().setEstado(idEstadoRefEvaluar);
			entity.getConcursoPuestoAgr().setFechaMod(new Date());
			entity.getConcursoPuestoAgr().setUsuMod(SYSTEM_USR);
			actualizarPuesto(entity, idEstadoDetEvaluarPuestos);
			entity.setEstado(ESTADO_VERIFICADO);
		} else if (tipo.equalsIgnoreCase("COMPLETAR_CARPETAS")) {
			entity.getConcursoPuestoAgr().setEstado(
					idEstadoRefCompletarCarpetas);
			entity.getConcursoPuestoAgr().setFechaMod(new Date());
			entity.getConcursoPuestoAgr().setUsuMod(SYSTEM_USR);
			actualizarPuesto(entity, idEstadoDetCompletarCarpetas);
			entity.setEstado(ESTADO_VERIFICADO);
		} else if (tipo.equalsIgnoreCase("PRORROGAR")) {
			entity.getConcursoPuestoAgr().setEstado(idEstadoRefProrrogar);
			entity.getConcursoPuestoAgr().setFechaMod(new Date());
			entity.getConcursoPuestoAgr().setUsuMod(SYSTEM_USR);
			actualizarPuesto(entity, idEstadoDetProrrogarPuestos);
			entity.setEstado(ESTADO_VERIFICADO);
		} else if (tipo.equalsIgnoreCase("GRUPO_DESIERTO")) {
			entity.getConcursoPuestoAgr().setEstado(idEstadoGrupoDesierto);
			entity.getConcursoPuestoAgr().setFechaMod(new Date());
			entity.getConcursoPuestoAgr().setUsuMod(SYSTEM_USR);
			actualizarPuestoDesierto(entity);
			entity.setEstado(ESTADO_VERIFICADO);
		}
		entity.setUsuMod(SYSTEM_USR);
		entity.setFechaMod(new Date());
		entity = em.merge(entity);
		return entity;
	}

	public void insertarPublicacionPortal(Long idConcursoPuestoAgr,
			Long idConcurso, String texto) {
		PublicacionPortal entity = new PublicacionPortal();
		entity.setFechaAlta(new Date());
		entity.setUsuAlta(SYSTEM_USR);
		entity.setActivo(true);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
				idConcursoPuestoAgr);
		entity.setConcurso(new Concurso());
		entity.getConcurso().setIdConcurso(idConcurso);
		entity.setTexto(texto);
		em.persist(entity);
	}

	public String genTextoPublicacion1(Date fechaProrroga) {
		String texto;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdf.format(new Date()).toString();
		String hr = "<hr>";
		String h1O = "<h1>";
		String h1C = "</h1>";
		String spanO = "<span >";
		String spanC = "</span>";
		String br = "</br>";
		texto = hr + fechaPublicacion + h1O + spanO
				+ "Se prorroga el plazo de Recepción de Postulaciones hasta: "
				+ sdf.format(fechaProrroga) + spanC + h1C + br;
		return texto;
	}

	public String genTextoPublicacion2() {
		StringBuffer texto = new StringBuffer();
		String h1O = "<h1>";
		String hr = "<hr>";
		String h1C = "</h1>";
		String spanO = "<span >";
		String spanC = "</span>";
		String br = "</br>";
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String fechaPublicacion = sdfFecha.format(new Date()).toString();
		texto.append(hr + fechaPublicacion + h1O
				+ spanO
				//Modificado RVeron, 20140416
				+ "Se declara desierto el Proceso en raz&oacute;n de que de acuerdo a las Evaluaciones Curriculares y Entrevistas realizadas ning&uacute;n postulante reune el perfil requerido en las Bases y Condiciones"
				//+ "Se declara desierto el Proceso por no reunir la cantidad suficiente de Postulantes, en fecha "
				+ sdfFecha.format(new Date()) + " Hs." + spanC + h1C);
		return texto.toString();
	}
	
	private void cambiarEstadoConcursoParaPublicacion(){
		List<FechasGrupoPuesto> lFechasGrupoPuesto = listaConcursoParaPublicacion();
		ConcursoPuestoAgr cpa;
		for (FechasGrupoPuesto o : lFechasGrupoPuesto) {
			cpa = em.find(ConcursoPuestoAgr.class, o.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			cpa.setEstado(1001);//Para el estado PUBLICACION_EVALUACION
			em.merge(cpa);
			em.flush();
		}
	}

	private void iniciarActividades() {
		
		List<FechasGrupoPuesto> lFechasGrupoPuesto = lActIniciar();
		for (FechasGrupoPuesto o : lFechasGrupoPuesto) {
			o = actualizarEstado(o, "INICIAR_TAREA");
			DatosEspecificos detc = o.getConcursoPuestoAgr().getConcurso()
					.getDatosEspecificosTipoConc();
			// if (detc.getValorAlf().equalsIgnoreCase("CPO")//MODIFICADO RV
			// || detc.getValorAlf().equalsIgnoreCase("CME"))
			// nextTask(ActividadEnum.APROBAR_PUBLICACION,
			// ActividadEnum.RECIBIR_POSTULACIONES, o.getConcursoPuestoAgr(),
			// "next2");
			// if (detc.getValorAlf().equalsIgnoreCase("CII")
			// || detc.getValorAlf().equalsIgnoreCase("CIR"))
			// nextTask(ActividadEnum.APROBAR_PUBLICACION,
			// ActividadEnum.CIO_RECIBIR_POSTULACIONES,
			// o.getConcursoPuestoAgr(), "next2");
			// if (detc.getValorAlf().equalsIgnoreCase("CSI"))
			// nextTask(ActividadEnum.APROBAR_PUBLICACION,
			// ActividadEnum.CSI_RECIBIR_POSTULACIONES,
			// o.getConcursoPuestoAgr(), "next2");
		}
		em.flush();
	}

	private Integer obtenerNum(Long idFechas) {
		Query q = em
				.createNativeQuery("select prorroga_num from seleccion.concurso_puesto_agr c "
						+ "join seleccion.fechas_grupo_puesto f "
						+ "on f.id_concurso_puesto_agr = c.id_concurso_puesto_agr "
						+ "where f.id_fechas= " + idFechas);
		Integer a = (Integer) q.getResultList().get(0);
		return (a == null) ? 0 : a;
	}

	private Concurso traerConcurso(Long idFechas) {
		FechasGrupoPuesto o = em.find(FechasGrupoPuesto.class, idFechas);
		return o.getConcurso();
	}

	/**
	 * Proceso principal, distribuidor de actividades
	 * 
	 * @throws ParseException
	 */
	private void finalizarActividades() throws ParseException {
		
		List<FechasGrupoPuesto> lFechasGrupoPuesto = lActFinalizar();
		Integer cantPostulantes = null;
		Integer cantPuestos = null;
		ConcursoPuestoAgr concursoPuestoAgr;
		// o.getConcursoPuestoAgr().getProrrogaNum()

		for (FechasGrupoPuesto o : lFechasGrupoPuesto) {
			try {
				cantPostulantes = cantPostulantes(o.getConcursoPuestoAgr()
						.getIdConcursoPuestoAgr());
				System.out.println("a) IMPRIMIENDO idConcursoPuestoAgr: "
						+ o.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
				cantPuestos = cantPuestos(o.getConcursoPuestoAgr()
						.getIdConcursoPuestoAgr());
				concursoPuestoAgr = new ConcursoPuestoAgr();

				// la prórroga se realizará si hay menos o igual cantidad de
				// postulantes que cantidad de puestos en el grupo
				if (cantPostulantes.intValue() <= cantPuestos) {// MODIFICADO JD
					Boolean prorroga1 = isPrimeraProrroga(o);
					if (obtenerNum(o.getIdFechas()) < cantProrrogas()) {
						// Enviar a la Actividad Prorrogar Postulación
						
						// o = actualizarEstado(o, "PRORROGAR");
						// insertarPublicacionPortal(o.getConcursoPuestoAgr()
						// .getIdConcursoPuestoAgr(), o
						// .getConcursoPuestoAgr().getConcurso()
						// .getIdConcurso(), genTextoPublicacion1());
						Long a = (o.getConcursoPuestoAgr()
								.getIdConcursoPuestoAgr() == null) ? 0 : o
								.getConcursoPuestoAgr()
								.getIdConcursoPuestoAgr();
						
						DatosEspecificos detc = o.getConcursoPuestoAgr()
								.getConcurso().getDatosEspecificosTipoConc();
						if (detc.getValorAlf().equalsIgnoreCase("CPO")
								|| detc.getValorAlf().equalsIgnoreCase("CME")) {

							if (prorroga1) {
								// LAS FECHAS DE LA PRIMERA PUBLICACION SE
								// GUARDAN EN fecha_recepcion_%%_ant
								o.setFechaRecepcionDesdeAnt(o
										.getFechaRecepcionDesde());
								o.setFechaRecepcionHastaAnt(o
										.getFechaRecepcionHasta());
								o.setFechaPublicacionDesdeAnt(o
										.getFechaPublicacionDesde());
								o.setFechaPublicacionHastaAnt(o
										.getFechaPublicacionHasta());
								;
								// NUEVAS FECHAS DE LA PRORROGA
								o.setFechaRecepcionDesde(new Date());

								ConcursoPuestoAgr i = em.find(
										ConcursoPuestoAgr.class, o
												.getConcursoPuestoAgr()
												.getIdConcursoPuestoAgr());

								o.setFechaRecepcionHasta(calcNuevaFechaRecepcion(i
										.getConcurso()));
								o.setFechaPublicacionDesde(new Date());
								o.setFechaPublicacionHasta(o
										.getFechaRecepcionHasta());
								em.merge(o);
								// se guarda la cantidad de prorrogas
								concursoPuestoAgr = o.getConcursoPuestoAgr();
								concursoPuestoAgr.setProrrogaNum(1);
								em.merge(concursoPuestoAgr);

								insertarPublicacionPortal(o
										.getConcursoPuestoAgr()
										.getIdConcursoPuestoAgr(), o
										.getConcursoPuestoAgr().getConcurso()
										.getIdConcurso(),
										genTextoPublicacion1(o
												.getFechaRecepcionHasta()));
							} else {
								o.setFechaRecepcionDesde(new Date());
								ConcursoPuestoAgr i = em.find(
										ConcursoPuestoAgr.class, o
												.getConcursoPuestoAgr()
												.getIdConcursoPuestoAgr());
								o.setFechaRecepcionHasta(calcNuevaFechaRecepcion(i
										.getConcurso()));
								em.merge(o);
								// se guarda de prorrogas
								concursoPuestoAgr = o.getConcursoPuestoAgr();
								concursoPuestoAgr
										.setProrrogaNum(concursoPuestoAgr
												.getProrrogaNum() + 1);
								em.merge(concursoPuestoAgr);
							}
							// nextTask(ActividadEnum.RECIBIR_POSTULACIONES,
							// ActividadEnum.PRORROGAR_POSTULACION,
							// o.getConcursoPuestoAgr(), "next");
						}
						if (detc.getValorAlf().equalsIgnoreCase("CII")
								|| detc.getValorAlf().equalsIgnoreCase("CIR")
								|| detc.getValorAlf().equalsIgnoreCase("CIPS")
								){
							if (prorroga1) {
								// LAS FECHAS DE LA PRIMERA PUBLICACION SE
								// GUARDAN EN fecha_recepcion_%%_ant
								o.setFechaRecepcionDesdeAnt(o
										.getFechaRecepcionDesde());
								o.setFechaRecepcionHastaAnt(o
										.getFechaRecepcionHasta());
								o.setFechaPublicacionDesdeAnt(o
										.getFechaPublicacionDesde());
								o.setFechaPublicacionHastaAnt(o
										.getFechaPublicacionHasta());
								;
								// NUEVAS FECHAS DE LA PRORROGA
								o.setFechaRecepcionDesde(new Date());

								ConcursoPuestoAgr i = em.find(
										ConcursoPuestoAgr.class, o
												.getConcursoPuestoAgr()
												.getIdConcursoPuestoAgr());

								o.setFechaRecepcionHasta(calcNuevaFechaRecepcion(i
										.getConcurso()));
								o.setFechaPublicacionDesde(new Date());
								o.setFechaPublicacionHasta(o
										.getFechaRecepcionHasta());
								em.merge(o);
								// se guarda la cantidad de prorrogas
								concursoPuestoAgr = o.getConcursoPuestoAgr();
								concursoPuestoAgr.setProrrogaNum(1);
								em.merge(concursoPuestoAgr);

								insertarPublicacionPortal(o
										.getConcursoPuestoAgr()
										.getIdConcursoPuestoAgr(), o
										.getConcursoPuestoAgr().getConcurso()
										.getIdConcurso(),
										genTextoPublicacion1(o
												.getFechaRecepcionHasta()));
							} else {
								o.setFechaRecepcionDesde(new Date());
								ConcursoPuestoAgr i = em.find(
										ConcursoPuestoAgr.class, o
												.getConcursoPuestoAgr()
												.getIdConcursoPuestoAgr());
								o.setFechaRecepcionHasta(calcNuevaFechaRecepcion(i
										.getConcurso()));
								em.merge(o);
								// se guarda de prorrogas
								concursoPuestoAgr = o.getConcursoPuestoAgr();
								concursoPuestoAgr
										.setProrrogaNum(concursoPuestoAgr
												.getProrrogaNum() + 1);
								em.merge(concursoPuestoAgr);
							}
						}
//							nextTask(ActividadEnum.RECIBIR_POSTULACIONES,
//									ActividadEnum.CIO_PRORROGAR_POSTULACION,
//									o.getConcursoPuestoAgr(), "next");
						if (detc.getValorAlf().equalsIgnoreCase("CSI"))
							nextTask(ActividadEnum.RECIBIR_POSTULACIONES,
									ActividadEnum.CSI_PRORROGAR_POSTULACION,
									o.getConcursoPuestoAgr(), "next");
					} else {
						o = actualizarEstado(o, "GRUPO_DESIERTO");
						insertarPublicacionPortal(o.getConcursoPuestoAgr()
								.getIdConcursoPuestoAgr(), o
								.getConcursoPuestoAgr().getConcurso()
								.getIdConcurso(), genTextoPublicacion2());
						DatosEspecificos detc = o.getConcursoPuestoAgr()
								.getConcurso().getDatosEspecificosTipoConc();
						if (detc.getValorAlf().equalsIgnoreCase("CPO")
								|| detc.getValorAlf().equalsIgnoreCase("CME"))
							nextTask(ActividadEnum.RECIBIR_POSTULACIONES, null,
									o.getConcursoPuestoAgr(), "recPos_TO_end");
						if (detc.getValorAlf().equalsIgnoreCase("CII")
								|| detc.getValorAlf().equalsIgnoreCase("CIR")
								|| detc.getValorAlf().equalsIgnoreCase("CIPS"))
							nextTask(ActividadEnum.CIO_RECIBIR_POSTULACIONES,
									null, o.getConcursoPuestoAgr(),
									"recPos_TO_end");
						if (detc.getValorAlf().equalsIgnoreCase("CSI"))
							nextTask(ActividadEnum.CSI_RECIBIR_POSTULACIONES,
									null, o.getConcursoPuestoAgr(),
									"recPos_TO_end");

						/*
						 * Enviar mails a los integrantes de la Comisión de
						 * Selección
						 */
						enviarMail(o.getConcursoPuestoAgr());
						/* Enviar mail al Postulante (si hubiere) para el Grupo */
						enviarMail2(o.getConcursoPuestoAgr());

						/**
						 * Incidencia 0001736 Llamar al CU604 (le envía como
						 * parámetros el id_concurso_puesto_agr, y las cadenas
						 * ‘GRUPO’, ‘CONCURSO’)
						 */
						if (reponerCategoriasController == null) {
							reponerCategoriasController = (ReponerCategoriasController) Component
									.getInstance(
											ReponerCategoriasController.class,
											true);
						}
						reponerCategoriasController.reponerCategorias(
								o.getConcursoPuestoAgr(), "GRUPO", "CONCURSO");
						/**
						 * fin
						 */

					}
				} else if (cantPostulantes.intValue() > cantPuestos) {

					DatosEspecificos detc = o.getConcursoPuestoAgr()
							.getConcurso().getDatosEspecificosTipoConc();
					if (detc.getValorAlf().equalsIgnoreCase("CII")
							|| detc.getValorAlf().equalsIgnoreCase("CIR")
							|| detc.getValorAlf().equalsIgnoreCase("CIPS")) {
						o = actualizarEstado(o, "EVALUAR");
						nextTask(ActividadEnum.CIO_RECIBIR_POSTULACIONES,
								ActividadEnum.CIO_EVALUACION_DOCUMENTAL,
								o.getConcursoPuestoAgr(), "next2");

					} else if (detc.getValorAlf().equalsIgnoreCase("CPO")
							|| detc.getValorAlf().equalsIgnoreCase("CME")) {
						Integer cantPostulantesCarpeta = cantPostulantesCarpeta(o
								.getConcursoPuestoAgr()
								.getIdConcursoPuestoAgr());
						if (cantPostulantesCarpeta.intValue() == 0) {
							o = actualizarEstado(o, "EVALUAR");
							nextTask(ActividadEnum.RECIBIR_POSTULACIONES,
									ActividadEnum.EVALUACION_DOCUMENTAL,
									o.getConcursoPuestoAgr(), "next2");
						} else {
							if (completarCarpetas(o.getConcursoPuestoAgr())) {
								o = actualizarEstado(o, "COMPLETAR_CARPETAS");
								nextTask(ActividadEnum.RECIBIR_POSTULACIONES,
										ActividadEnum.COMPLETAR_CARPETAS,
										o.getConcursoPuestoAgr(),
										"recPos_TO_ComCar");
							} else {
								o = actualizarEstado(o, "EVALUAR");
								nextTask(ActividadEnum.RECIBIR_POSTULACIONES,
										ActividadEnum.EVALUACION_DOCUMENTAL,
										o.getConcursoPuestoAgr(), "next2");
							}
						}
					} else if (detc.getValorAlf().equalsIgnoreCase("CSI")) {
						Query q = em
								.createQuery("select ReglaDet from ReglaDet ReglaDet where ReglaDet.reglaCab.activo is true "
										+ " and ReglaDet.activo is true "
										+ " and ReglaDet.reglaCab.fechaVigenciaDesde<= :fechaSistema "
										+ " and ReglaDet.reglaCab.fechaVigenciaHasta>=:fechaSistema    ");
						Date fechaSistema = new Date();
						q.setParameter("fechaSistema", fechaSistema,
								TemporalType.DATE);

						List<ReglaDet> lista = q.getResultList();
						if (lista.size() == 0) {
							Integer cantPostulantesCarpeta = cantPostulantesCarpeta(o
									.getConcursoPuestoAgr()
									.getIdConcursoPuestoAgr());
							if (cantPostulantesCarpeta.intValue() == 0) {
								o = actualizarEstado(o, "LISTA_LARGA");
								nextTask(
										ActividadEnum.CSI_RECIBIR_POSTULACIONES,
										ActividadEnum.CSI_REALIZAR_EVALUACIONES,
										o.getConcursoPuestoAgr(),
										"recPos_TO_reaEva");
							} else {
								if (completarCarpetas(o.getConcursoPuestoAgr())) {
									o = actualizarEstado(o,
											"COMPLETAR_CARPETAS");
									nextTask(
											ActividadEnum.CSI_RECIBIR_POSTULACIONES,
											ActividadEnum.COMPLETAR_CARPETAS,
											o.getConcursoPuestoAgr(),
											"recPos_TO_ComCar");
								} else {
									o = actualizarEstado(o, "LISTA_LARGA");
									nextTask(
											ActividadEnum.CSI_RECIBIR_POSTULACIONES,
											ActividadEnum.CSI_REALIZAR_EVALUACIONES,
											o.getConcursoPuestoAgr(),
											"recPos_TO_reaEva");
								}
							}
						} else {
							q = em.createQuery("select count(ConcursoPuestoDet) from ConcursoPuestoDet ConcursoPuestoDet "
									+ " where ConcursoPuestoDet.activo is true "
									+ " and ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
							q.setParameter("idGrupo", o.getConcursoPuestoAgr()
									.getIdConcursoPuestoAgr());
							Long cantVaca = (Long) q.getSingleResult();
							q = em.createQuery("select ReglaDet from ReglaDet ReglaDet where ReglaDet.reglaCab.activo is true "
									+ " and ReglaDet.activo is true "
									+ " and ReglaDet.reglaCab.fechaVigenciaDesde<= :fechaSistema "
									+ " and ReglaDet.reglaCab.fechaVigenciaHasta>=:fechaSistema  "
									+ " and ReglaDet.cantVacancias  = :cantVaca ");
							q.setParameter("cantVaca", cantVaca.intValue());
							q.setParameter("fechaSistema", fechaSistema,
									TemporalType.DATE);
							lista = q.getResultList();

							Integer tope = 0;
							if (lista.size() > 0) {
								tope = lista.get(0).getCantPostulantes();
								if (cantPostulantes.intValue() > tope
										.intValue()) {
									o = actualizarEstado(o, "SORTEO");
									nextTask(
											ActividadEnum.CSI_RECIBIR_POSTULACIONES,
											ActividadEnum.CSI_REALIZAR_SORTEO,
											o.getConcursoPuestoAgr(),
											"recPos_TO_sor");
								} else {
									Integer cantPostulantesCarpeta = cantPostulantesCarpeta(o
											.getConcursoPuestoAgr()
											.getIdConcursoPuestoAgr());
									if (cantPostulantesCarpeta.intValue() == 0) {
										o = actualizarEstado(o, "LISTA_LARGA");
										nextTask(
												ActividadEnum.CSI_RECIBIR_POSTULACIONES,
												ActividadEnum.CSI_REALIZAR_EVALUACIONES,
												o.getConcursoPuestoAgr(),
												"recPos_TO_reaEva");
									} else {
										if (completarCarpetas(o
												.getConcursoPuestoAgr())) {
											o = actualizarEstado(o,
													"COMPLETAR_CARPETAS");
											nextTask(
													ActividadEnum.CSI_RECIBIR_POSTULACIONES,
													ActividadEnum.COMPLETAR_CARPETAS,
													o.getConcursoPuestoAgr(),
													"recPos_TO_ComCar");
										} else {
											o = actualizarEstado(o,
													"LISTA_LARGA");
											nextTask(
													ActividadEnum.CSI_RECIBIR_POSTULACIONES,
													ActividadEnum.CSI_REALIZAR_EVALUACIONES,
													o.getConcursoPuestoAgr(),
													"recPos_TO_reaEva");
										}
									}
								}
							} else {
								Integer cantPostulantesCarpeta = cantPostulantesCarpeta(o
										.getConcursoPuestoAgr()
										.getIdConcursoPuestoAgr());
								if (cantPostulantesCarpeta.intValue() == 0) {
									o = actualizarEstado(o, "LISTA_LARGA");
									nextTask(
											ActividadEnum.CSI_RECIBIR_POSTULACIONES,
											ActividadEnum.CSI_REALIZAR_EVALUACIONES,
											o.getConcursoPuestoAgr(),
											"recPos_TO_reaEva");
								} else {
									if (completarCarpetas(o
											.getConcursoPuestoAgr())) {
										o = actualizarEstado(o,
												"COMPLETAR_CARPETAS");
										nextTask(
												ActividadEnum.CSI_RECIBIR_POSTULACIONES,
												ActividadEnum.COMPLETAR_CARPETAS,
												o.getConcursoPuestoAgr(),
												"recPos_TO_ComCar");
									} else {
										o = actualizarEstado(o, "LISTA_LARGA");
										nextTask(
												ActividadEnum.CSI_RECIBIR_POSTULACIONES,
												ActividadEnum.CSI_REALIZAR_EVALUACIONES,
												o.getConcursoPuestoAgr(),
												"recPos_TO_reaEva");
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		em.flush();
	}

	/**
	 * Indica si se debe completar o no la carpeta de los postulantes
	 * 
	 * @param grupo
	 *            el grupo en cuestion
	 * @return
	 */

	private Boolean completarCarpetas(ConcursoPuestoAgr grupo) {
		List<Postulacion> lista = lPostulantesCarpeta(grupo
				.getIdConcursoPuestoAgr());
		for (Postulacion o : lista) {
			PersonaPostulante pp = o.getPersonaPostulante();
			if (pp == null) {
				return true;
			}
			if (grupo.getConcurso().getPcd()) {
				if (pp.getEstudiosRealizadoses().size() == 0
						|| pp.getExperienciaLaborals().size() == 0
						|| pp.getPersonaDiscapacidads().size() == 0) {
					return true;
				}
			} else {
				// No es concurso PCD
				if (pp.getEstudiosRealizadoses().size() == 0
						|| pp.getExperienciaLaborals().size() == 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Transactional
	@Asynchronous
	public QuartzTriggerHandle procesoPrincipal(@Expiration Date when,
			@IntervalCron String interval) {
		
		init();
		iniciarActividades();
		try {
			finalizarActividades();
			cambiarEstadoConcursoParaPublicacion();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public String getSYSTEM_USR() {
		return SYSTEM_USR;
	}

	public void setSYSTEM_USR(String sYSTEM_USR) {
		SYSTEM_USR = sYSTEM_USR;
	}

}
