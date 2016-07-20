package py.com.excelsis.sicca.seleccion.session.form;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
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

import py.com.excelsis.sicca.entity.ComisionGrupo;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;

@Name("admFecRecPosFC")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class AdmFecRecPosFC {

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

	private String ESTADO_VERIFICADO = "VERIFICADO";
	private String ESTADO_A_VERIFICAR = "A VERIFICAR";
	private String DOMINIO_REFERENCIA = "ESTADOS_GRUPO";

	private String DSC_ABREV_EVALUAR = "A EVALUAR DOCUMENTOS";
	private String DSC_ABREV_PRORROGAR = "A PRORROGAR";
	private String DSC_GRUPO_DESIERTO = "GRUPO DESIERTO";

	private Integer idEstadoRefEvaluar;
	private Integer idEstadoRefEvaluarPuestos;
	private Integer idEstadoRefProrrogar;
	private Integer idEstadoRefProrrogarPuestos;
	private Integer idEstadoGrupoDesierto;
	private Integer idEstadoDetLibre;
	private Integer idEstadoDetDesierto;
	public static String SYSTEM_USR = "SYSTEM1";
	SeleccionUtilFormController seleccionUtilFormController;

	public void init() {
		if (idEstadoRefEvaluar == null)
			idEstadoRefEvaluar = calcIdEstadosReferencia(DOMINIO_REFERENCIA, DSC_ABREV_EVALUAR);
		if (idEstadoRefProrrogar == null)
			idEstadoRefProrrogar = calcIdEstadosReferencia(DOMINIO_REFERENCIA, DSC_ABREV_PRORROGAR);
		if (idEstadoGrupoDesierto == null)
			idEstadoGrupoDesierto = calcIdEstadosReferencia(DOMINIO_REFERENCIA, DSC_GRUPO_DESIERTO);
		if (idEstadoDetLibre == null)
			idEstadoDetLibre = calcIdEstadosDet("CONCURSO", "LIBRE");
		if (idEstadoDetDesierto == null)
			idEstadoDetDesierto = calcIdEstadosDet("CONCURSO", "DESIERTO");
		if (idEstadoRefEvaluarPuestos == null)
			idEstadoRefEvaluarPuestos = calcIdEstadosDet("CONCURSO", "A EVALUAR DOCUMENTOS");
		if (idEstadoRefProrrogarPuestos == null)
			idEstadoRefProrrogarPuestos = calcIdEstadosDet("CONCURSO", "A PRORROGAR");

		seleccionUtilFormController =
			(SeleccionUtilFormController) Component.getInstance(SeleccionUtilFormController.class, true);

		// Asignar Actor para el jBPM
		actor.setId(SYSTEM_USR);

	}

	private List<FechasGrupoPuesto> lFechasGrupoPuesto() {
		Query q =
			em.createQuery("select FechasGrupoPuesto from FechasGrupoPuesto FechasGrupoPuesto "
				+ " where FechasGrupoPuesto.activo is true "
				+ "and FechasGrupoPuesto.estado='"
				+ ESTADO_A_VERIFICAR
				+ "'"
				+ " and (FechasGrupoPuesto.fechaRecepcionHasta < :fechaSistema  "
				+ " or ( :fechaSistema = FechasGrupoPuesto.fechaRecepcionHasta and :horaSistema >= FechasGrupoPuesto.horaRecepcionHasta ) )");
		Date fechaSistema = new Date();
		q.setParameter("fechaSistema", fechaSistema, TemporalType.DATE);
		q.setParameter("horaSistema", fechaSistema, TemporalType.TIME);

		List<FechasGrupoPuesto> lista = q.getResultList();
		return lista;
	}

	private Integer cantPostulantes(Long idGrupo) {

		Query q =
			em.createQuery("select count(Postulacion) from Postulacion Postulacion "
				+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = " + idGrupo
				+ " and Postulacion.usuCancelacion is null "
				+ " and Postulacion.fechaCancelacion is null and Postulacion.activo is true ");
		Long count = (Long) q.getSingleResult();
		return count.intValue();
	}

	private Boolean isPrimeraProrroga(FechasGrupoPuesto entity) {
		if (entity.getFechaRecepcionDesdeAnt() == null
			|| entity.getFechaRecepcionHastaAnt() == null
			|| entity.getHoraRecepcionHastaAnt() == null
			|| entity.getHoraRecepcionDesdeAnt() == null) {
			return true;
		}
		return false;
	}

	public List<ComisionSeleccionDet> traerDestinatarios(Long idGrupo) {
		Query q =
			em.createQuery("select ComisionGrupo from ComisionGrupo ComisionGrupo where ComisionGrupo.concursoPuestoAgr.idConcursoPuestoAgr = "
				+ idGrupo);
		ComisionGrupo comisionGrupo = (ComisionGrupo) q.getSingleResult();
		q =
			em.createQuery("select ComisionSeleccionDet from ComisionSeleccionDet ComisionSeleccionDet where ComisionSeleccionDet.comisionSeleccionCab.idComisionSel = "
				+ comisionGrupo.getComisionSeleccionCab().getIdComisionSel());

		return q.getResultList();
	}

	public List<Postulacion> traerDestinatarios2(Long idGrupo) {
		Query q =
			em.createQuery("select Postulacion from Postulacion Postulacion"
				+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = "
				+ idGrupo
				+ " and Postulacion.activo is true and Postulacion.personaPostulante.activo is true");

		return q.getResultList();
	}

	private void enviarMail(ConcursoPuestoAgr concursoPuestoAgr) {
		List<ComisionSeleccionDet> lDestinatarios =
			traerDestinatarios(concursoPuestoAgr.getIdConcursoPuestoAgr());
		String dirEmail = null;
		for (ComisionSeleccionDet o : lDestinatarios) {
			dirEmail = o.getPersona().getEMail();
			String asunto =
				"Declarar Desierto el Concurso o hacer Prorroga por Excepción – con Aprobación de la SFP";
			String texto = "";
			try {
				texto =
					texto
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
						+ concursoPuestoAgr.getConcurso().getConfiguracionUoCab().getDenominacionUnidad()
						+ "</p></b>";

				usuarioPortalFormController.enviarMails(dirEmail, texto, asunto, null);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void enviarMail2(ConcursoPuestoAgr concursoPuestoAgr) {
		List<Postulacion> lDestinatarios =
			traerDestinatarios2(concursoPuestoAgr.getIdConcursoPuestoAgr());
		String dirEmail = null;
		for (Postulacion o : lDestinatarios) {
			dirEmail = o.getPersonaPostulante().getPersona().getEMail();
			String asunto =
				//modificado RVero, 20140416
				"Declarar desierto el concurso";
				//"NOTIF_PUESTO_DESIERTO_" + concursoPuestoAgr.getConcurso().getNombre() + "_"
				//	+ concursoPuestoAgr.getDescripcionGrupo();
			String texto = "";
			try {
				texto =
					texto
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
						+ " ha sido declarado Desierto"// porque no ha reunido la cantidad de Postulantes suficientes."
						//+ " ha sido declarado desierto en raz&oacute;n de que de acuerdo a las Evaluaciones Curriculares y Entrevistas realizadas ning&uacute;n postulante reune el perfil requerido en las Bases y Condiciones."
						+ "</p>"
						+ "<p align=\"justify\">Le agradecemos el inter&eacute;s que ha mostrado para este Concurso, y deseamos poder contar con Usted para nuestros pr&oacute;ximos concursos. </p>"
						+ "<p></p>"
						+ "<p> Atentamente,</p> "
						+ "<b>"
						+ concursoPuestoAgr.getConcurso().getConfiguracionUoCab().getDenominacionUnidad()
						+ "</p></b>";

				usuarioPortalFormController.enviarMails(dirEmail, texto, asunto, null);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void cambiarActividad(ActividadEnum sgteActividad, ConcursoPuestoAgr concursoPuestoAgr, String transition) {

		jbpmUtilFormController =
			(JbpmUtilFormController) Component.getInstance(JbpmUtilFormController.class, true);

		jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		jbpmUtilFormController.setActividadActual(ActividadEnum.RECIBIR_POSTULACIONES);
		jbpmUtilFormController.setActividadSiguiente(sgteActividad);
		jbpmUtilFormController.setTransition(transition);
		if (jbpmUtilFormController.nextTask()) {

		}
	}

	private Integer calcIdEstadosReferencia(String dominio, String descAbrev) {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ "  where Referencias.activo is true and Referencias.dominio ='" + dominio
				+ "' and Referencias.descAbrev ='" + descAbrev + "'");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorNum().intValue();
	}

	public Integer calcIdEstadosDet(String cabecera, String detalle) {
		Query q =
			em.createQuery("select EstadoDet from EstadoDet EstadoDet "
				+ "  where EstadoDet.estadoCab.descripcion ='" + cabecera
				+ "' and EstadoDet.activo is true and EstadoDet.descripcion ='" + detalle + "'");
		EstadoDet ref = (EstadoDet) q.getSingleResult();
		return ref.getIdEstadoDet().intValue();
	}

	private FechasGrupoPuesto actualizarPuesto(FechasGrupoPuesto entity, Integer idEstado) {

		List<ConcursoPuestoDet> lista =
			seleccionUtilFormController.getConcursoPuestoDet(entity.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		for (ConcursoPuestoDet o : lista) {
			o.setEstadoDet(new EstadoDet());
			o.getEstadoDet().setIdEstadoDet(new Long(idEstado));
			o.setActivo(true);
			o.setFechaMod(new Date());
			o.setUsuMod(SYSTEM_USR);
			em.merge(o);
			o.getPlantaCargoDet().setEstadoDet(new EstadoDet());
			o.getPlantaCargoDet().getEstadoDet().setIdEstadoDet(new Long(idEstado));
			o.getPlantaCargoDet().setFechaMod(new Date());
			o.getPlantaCargoDet().setUsuMod(SYSTEM_USR);
			em.merge(o.getPlantaCargoDet());
		}

		return entity;
	}

	private FechasGrupoPuesto actualizarPuesto(FechasGrupoPuesto entity) {
		Query q =
			em.createQuery("select ConcursoPuestoDet from ConcursoPuestoDet ConcursoPuestoDet "
				+ "where ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = "
				+ entity.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		List<ConcursoPuestoDet> lista = q.getResultList();
		for (ConcursoPuestoDet o : lista) {
			o.setEstadoDet(new EstadoDet());
			o.getEstadoDet().setIdEstadoDet(new Long(idEstadoDetDesierto));
			o.setActivo(false);
			o.setFechaMod(new Date());
			o.setUsuMod(SYSTEM_USR);
			em.merge(o);
			o.getPlantaCargoDet().setEstadoDet(new EstadoDet());
			o.getPlantaCargoDet().getEstadoDet().setIdEstadoDet(new Long(idEstadoDetLibre));
			o.getPlantaCargoDet().setFechaMod(new Date());
			o.getPlantaCargoDet().setUsuMod(SYSTEM_USR);
			em.merge(o.getPlantaCargoDet());
		}
		em.flush();
		return entity;
	}

	public FechasGrupoPuesto actualizarEstado(FechasGrupoPuesto entity, String tipo) {

		if (tipo.equalsIgnoreCase("EVALUAR")) {
			entity.getConcursoPuestoAgr().setEstado(idEstadoRefEvaluar);
			entity.getConcursoPuestoAgr().setFechaMod(new Date());
			entity.getConcursoPuestoAgr().setUsuMod(SYSTEM_USR);
			actualizarPuesto(entity, idEstadoRefEvaluarPuestos);
			entity.setEstado(ESTADO_VERIFICADO);
		} else if (tipo.equalsIgnoreCase("PRORROGAR")) {
			entity.getConcursoPuestoAgr().setEstado(idEstadoRefProrrogar);
			entity.getConcursoPuestoAgr().setFechaMod(new Date());
			entity.getConcursoPuestoAgr().setUsuMod(SYSTEM_USR);
			actualizarPuesto(entity, idEstadoRefProrrogarPuestos);
			entity.setEstado(ESTADO_VERIFICADO);
		} else if (tipo.equalsIgnoreCase("GRUPO_DESIERTO")) {
			entity.getConcursoPuestoAgr().setEstado(idEstadoGrupoDesierto);
			entity.getConcursoPuestoAgr().setFechaMod(new Date());
			entity.getConcursoPuestoAgr().setUsuMod(SYSTEM_USR);
			actualizarPuesto(entity);
			entity.setEstado(ESTADO_VERIFICADO);
		}
		entity.setUsuMod(SYSTEM_USR);
		entity.setFechaMod(new Date());
		entity = em.merge(entity);
		return entity;

	}

	public void insertarPublicacionPortal(Long idConcursoPuestoAgr, Long idConcurso, String texto) {
		PublicacionPortal entity = new PublicacionPortal();
		entity.setFechaAlta(new Date());
		entity.setUsuAlta(SYSTEM_USR);
		entity.setActivo(true);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		entity.setConcurso(new Concurso());
		entity.getConcurso().setIdConcurso(idConcurso);
		entity.setTexto(texto);
		em.persist(entity);

	}

	public String genTextoPublicacion1() {
		StringBuffer texto = new StringBuffer();
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdfFecha.format(new Date()).toString();
		String hr = "<hr>";
		String h1O = "<h1>";
		String h1C = "</h1>";
		String spanO = "<span >";
		String spanC = "</span>";
		String br = "</br>";
		texto.append(hr + fechaPublicacion + h1O + spanO + "Se ha prorrogado el plazo de Recepción de Postulaciones."
			+ spanC + h1C);
		return texto.toString();
	}

	public String genTextoPublicacion2() {
		StringBuffer texto = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdf.format(new Date()).toString();
		String hr = "<hr>";
		String h1O = "<h1>";
		String h1C = "</h1>";
		String spanO = "<span >";
		String spanC = "</span>";
		String br = "</br>";
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		texto.append(hr + fechaPublicacion + h1O
			+ spanO
			//Modificado RVeron, 20140416
			+ " Declarado Desierto"
			//+ "Se declara desierto el Proceso en raz&oacute;n de que de acuerdo a las Evaluaciones Curriculares y Entrevistas realizadas ning&uacute;n postulante reune el perfil requerido en las Bases y Condiciones, en fecha "
			//+ sdfFecha.format(new Date()) + " Hs." 
			+ spanC + h1C);
		return texto.toString();
	}

	@Transactional
	@Asynchronous
	public QuartzTriggerHandle procesoPrincipal(@Expiration Date when, @IntervalCron String interval) {
		
		init();
		List<FechasGrupoPuesto> lFechasGrupoPuesto = lFechasGrupoPuesto();
		Integer cantPostulantes = null;
		for (FechasGrupoPuesto o : lFechasGrupoPuesto) {

			cantPostulantes = cantPostulantes(o.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			if (cantPostulantes.intValue() < 2) {
				Boolean prorroga1 = isPrimeraProrroga(o);
				if (prorroga1) {
					// Enviar a la Actividad Prorrogar Postulación
					cambiarActividad(ActividadEnum.PRORROGAR_POSTULACION, o.getConcursoPuestoAgr(), "next");
					o = actualizarEstado(o, "PRORROGAR");
					insertarPublicacionPortal(o.getConcursoPuestoAgr().getIdConcursoPuestoAgr(), o.getConcursoPuestoAgr().getConcurso().getIdConcurso(), genTextoPublicacion1());
				} else {
					/* Enviar mails a los integrantes de la Comisión de Selección */
					enviarMail(o.getConcursoPuestoAgr());
					/* Enviar mail al Postulante (si hubiere) para el Grupo */
					enviarMail2(o.getConcursoPuestoAgr());
					o = actualizarEstado(o, "GRUPO_DESIERTO");
					insertarPublicacionPortal(o.getConcursoPuestoAgr().getIdConcursoPuestoAgr(), o.getConcursoPuestoAgr().getConcurso().getIdConcurso(), genTextoPublicacion2());

				}
			} else if (cantPostulantes.intValue() > 1) {
				// Enviar a la Actividad Evaluación Documental
				cambiarActividad(ActividadEnum.EVALUACION_DOCUMENTAL, o.getConcursoPuestoAgr(), "next2");
				o = actualizarEstado(o, "EVALUAR");
			}
		}
		em.flush();
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
