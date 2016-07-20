package py.com.excelsis.sicca.capacitacion.session.form;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.drools.lang.DRLParser.operator_key_return;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.bpm.Actor;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.ProrrogaCap;
import py.com.excelsis.sicca.entity.RedCapacitacion;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;
import enums.ProcesoEnum;

@Name("procesoCapacitacionFC525")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class ProcesoCapacitacionFC525 {
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
	public static String SYSTEM_USR = "SYSTEM";
	SeleccionUtilFormController seleccionUtilFormController;

	private Integer idEstadoRefReproCancelar = null;
	private Integer idEstadoRefInsLista = null;
	private Integer idEstadoRefAsigComision = null;

	@Transactional
	@Asynchronous
	public QuartzTriggerHandle procesoPrincipal(@Expiration Date when, @IntervalCron String interval) {
		
		init();
		List<Capacitaciones> lCapacitaciones = lCapacitaciones();
		Integer cantInscriptos = 0;
		for (Capacitaciones o : lCapacitaciones) {
			cantInscriptos = cantInscriptosCapa(o.getIdCapacitacion());
			if (o.getTipoSeleccion().equalsIgnoreCase("I")) {
				if (cantInscriptos.intValue() < o.getCupoMinimo().intValue()) {
					o.setEstado(idEstadoRefReproCancelar);
					o.setUsuMod(SYSTEM_USR);
					o.setFechaMod(new Date());
					o = em.merge(o);
					ProrrogaCap prorrogaCap = new ProrrogaCap();
					prorrogaCap.setActivo(true);
					prorrogaCap.setCapacitaciones(new Capacitaciones());
					prorrogaCap.getCapacitaciones().setIdCapacitacion(o.getIdCapacitacion());
					prorrogaCap.setEstado("A PRORROGAR");
					prorrogaCap.setUsuAlta(SYSTEM_USR);
					prorrogaCap.setFechaAlta(new Date());
					em.persist(prorrogaCap);
					nextTask(ActividadEnum.CAPA_RECEPCIONAR_POST_INSC, ActividadEnum.CAPA_REPROGRAMAR_CANCELAR_CAPAC, o, "recPosIns_TO_repCanCap");
				} else {
					o.setEstado(idEstadoRefInsLista);
					o.setUsuMod(SYSTEM_USR);
					o.setFechaMod(new Date());
					o = em.merge(o);
					nextTask(ActividadEnum.CAPA_RECEPCIONAR_POST_INSC, ActividadEnum.CAPA_INSCRIPCION_LISTA, o, "recPosIns_TO_insLis");
				}
			} else if (o.getTipoSeleccion().equalsIgnoreCase("C")) {
				if (cantInscriptos.intValue() < o.getCupoMinimo().intValue()) {
					o.setEstado(idEstadoRefReproCancelar);
					o.setUsuMod(SYSTEM_USR);
					o.setFechaMod(new Date());
					o = em.merge(o);
					ProrrogaCap prorrogaCap = new ProrrogaCap();
					prorrogaCap.setActivo(true);
					prorrogaCap.setCapacitaciones(new Capacitaciones());
					prorrogaCap.getCapacitaciones().setIdCapacitacion(o.getIdCapacitacion());
					prorrogaCap.setEstado("A PRORROGAR");
					prorrogaCap.setUsuAlta(SYSTEM_USR);
					prorrogaCap.setFechaAlta(new Date());
					em.persist(prorrogaCap);
					nextTask(ActividadEnum.CAPA_RECEPCIONAR_POST_INSC, ActividadEnum.CAPA_REPROGRAMAR_CANCELAR_CAPAC, o, "recPosIns_TO_repCanCap");
				} else {
					o.setEstado(idEstadoRefAsigComision);
					o.setUsuMod(SYSTEM_USR);
					o.setFechaMod(new Date());
					o = em.merge(o);
					nextTask(ActividadEnum.CAPA_RECEPCIONAR_POST_INSC, ActividadEnum.CAPA_REGISTRAR_COMISION, o, "recPosIns_TO_regCom");
				}
			}
			o.setEstadoPublic("VERIFICADO");
			o = em.merge(o);
			em.flush();
		}
		/* Incidencia 1384 */
		seleccionUtilFormController =
			(SeleccionUtilFormController) Component.getInstance(SeleccionUtilFormController.class, true);
		List<Capacitaciones> lCapacitaciones2 = lCapacitaciones2();
		Integer idEstadoRefCargar = traerValorNum("ESTADOS_CAPACITACION", "CARGAR PARTICIPANTES");
		for (Capacitaciones o : lCapacitaciones2) {
			try {
				List<RedCapacitacion> lIntegrantes =
					integegrantesRed(o.getConfiguracionUoCab().getIdConfiguracionUo());
				o.setEstado(idEstadoRefCargar);
				o.setEstadoPublic("VERIFICADO");
				o.setUsuMod(SYSTEM_USR);
				o.setFechaMod(new Date());
				em.merge(o);
				em.flush();
				for (RedCapacitacion p : lIntegrantes) {
					seleccionUtilFormController.enviarMails(p.getPersona().getEMail(), generarTextoMail(p.getPersona(), o), "Portal Paraguay Concursa – Registrar Participantes – Curso O.G.290 ");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**/
		return null;
	}

	private String generarTextoMail(Persona persona, Capacitaciones capa) {

		String textoHeader =
			" <p align=\"justify\"> <b> Estimado (a) " + persona.getNombres() + " "
				+ persona.getApellidos() + ": </b></p>";
		String texto =
			"<p>Recordamos que debe cargar los Participantes de la Capacitaci&oacute;n/Beca:</p> <b><p align=\"center\"> "
				+ capa.getDatosEspecificosTipoCap().getDescripcion()
				+ "</p><p align=\"center\"> "
				+ capa.getNombre() + "</p></b>";

		texto +=
			"<p align=\"justify\">Para ello, deber&aacute; ingresar al apartado Capacitaciones &#47; Objeto de Gasto 290 &#47; Gesti&oacute;n de Capacitaciones O.G.290 &#45; Enlace &#8216;Gesti&oacute;n de Participantes&#8217; de la Capacitaci&oacute;n&#47;Beca</p>";
		String textoFooter =
			"<b>Atentamente,<br/> " + capa.getConfiguracionUoCab().getDenominacionUnidad()
				+ "<br/></b>";

		return textoHeader + texto + textoFooter;
	}

	private List<RedCapacitacion> integegrantesRed(Long idUoCab) {
		Query q =
			em.createQuery("select RedCapacitacion from RedCapacitacion RedCapacitacion"
				+ " where  RedCapacitacion.configuracionUoCab.idConfiguracionUo = :idUoCab ");
		q.setParameter("idUoCab", idUoCab);
		return q.getResultList();
	}

	private void init() {
		seleccionUtilFormController =
			(SeleccionUtilFormController) Component.getInstance(SeleccionUtilFormController.class, true);

		if (idEstadoRefReproCancelar == null) {
			idEstadoRefReproCancelar =
				seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_CAPACITACION", "REPROGRAMAR/CANCELAR");
		}
		if (idEstadoRefInsLista == null) {
			idEstadoRefInsLista =
				seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_CAPACITACION", "INSCRIPCION/LISTA");
		}
		if (idEstadoRefAsigComision == null) {
			idEstadoRefAsigComision =
				seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_CAPACITACION", "ASIGNAR COMISION");
		}

		// Asignar Actor para el jBPM
		actor.setId(SYSTEM_USR);
	}

	private Integer cantInscriptosCapa(Long idCapacitacion) {
		Query q =
			em.createQuery("select count(PostulacionCap) from PostulacionCap PostulacionCap "
				+ " where PostulacionCap.capacitacion.idCapacitacion = :idCapacitacion"
				+ " and PostulacionCap.activo is true and PostulacionCap.ficha is false ");
		q.setParameter("idCapacitacion", idCapacitacion);
		Long count = (Long) q.getSingleResult();
		return count.intValue();
	}

	private List<Capacitaciones> lCapacitaciones() {
		Query q =
			em.createQuery("select Capacitaciones from Capacitaciones Capacitaciones "
				+ " where Capacitaciones.fechaRecepHasta < :fechaSistema and Capacitaciones.estadoPublic = 'A VERIFICAR' "
				+ " and Capacitaciones.tipo ='CAP_SFP'");
		q.setParameter("fechaSistema", new Date(), TemporalType.DATE);

		return q.getResultList();
	}

	private List<Capacitaciones> lCapacitaciones2() {
		Query q =
			em.createQuery("select Capacitaciones from Capacitaciones Capacitaciones "
				+ " where ((Capacitaciones.recepcionPostulacion is true and Capacitaciones.fechaPubHasta = :fechaSistemaMenosX and Capacitaciones.estadoPublic is null) "
				+ " OR (Capacitaciones.recepcionPostulacion is false and Capacitaciones.fechaInicio = :fechaSistemaMasY and Capacitaciones.estadoPublic is null )) "
				+ " and Capacitaciones.tipo ='CAP_OG290'");
		q.setParameter("fechaSistemaMenosX", sumarFecha(new Date(), -1*traerValorNum("GESTION_CAPOG", "RECEP_T")), TemporalType.DATE);
		q.setParameter("fechaSistemaMasY", sumarFecha(new Date(), traerValorNum("GESTION_CAPOG", "RECEP_F")), TemporalType.DATE);

		return q.getResultList();
	}

	private Date sumarFecha(Date fecha, Integer cDias) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.add(Calendar.DATE, cDias);
		return cal.getTime();
	}

	private Integer traerValorNum(String dominio, String descAbrev) {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ "where Referencias.activo is true and  Referencias.dominio = :dominio and Referencias.descAbrev = :descAbrev");
		q.setParameter("dominio", dominio);
		q.setParameter("descAbrev", descAbrev);
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorNum();
	}

	private void nextTask(ActividadEnum actividadActual, ActividadEnum sgteActividad, Capacitaciones capa, String transition) {

		jbpmUtilFormController =
			(JbpmUtilFormController) Component.getInstance(JbpmUtilFormController.class, true);
		jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
		jbpmUtilFormController.setCapacitacion(capa);
		jbpmUtilFormController.setActividadActual(actividadActual);
		jbpmUtilFormController.setActividadSiguiente(sgteActividad);
		jbpmUtilFormController.setTransition(transition);
		jbpmUtilFormController.nextTask();
	}
}
