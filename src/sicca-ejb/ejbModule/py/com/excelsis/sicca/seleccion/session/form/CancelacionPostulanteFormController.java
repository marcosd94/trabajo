package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;

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

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.entity.ComisionGrupo;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.MatrizDocConfigEnc;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PresentAclaracDoc;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("cancelacionPostulanteFormController")
public class CancelacionPostulanteFormController implements Serializable {

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
	JbpmUtilFormController jbpmUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	private SinNivelEntidad sinNivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private ConcursoPuestoAgr concursoPuestoAgr = new ConcursoPuestoAgr();
	private Long idConcurso;
	private Concurso concurso = new Concurso();
	private Long idPostulacion;
	private Postulacion postulacion = new Postulacion();
	private Long idConcursoPuestoAgr;
	private List<EvalReferencialPostulante> referencialPostulantes =
		new ArrayList<EvalReferencialPostulante>();
	private Persona persona;
	private EvalReferencialPostulante evalReferencialPostulanteObs =
		new EvalReferencialPostulante();
	private SeguridadUtilFormController seguridadUtilFormController;

	public void init() {
		persona = usuarioLogueado.getPersona();

	}

	public void initPage() {

		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		seguridadUtilFormController.verificarPerteneceOee(null, concursoPuestoAgr.getIdConcursoPuestoAgr(), seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "CREADO EN ACTIVIDAD MODIFICAR")
			+ "", ActividadEnum.REVISION_EMPATES.getValor());
		if (concursoPuestoAgr.getConcurso() != null) {
			idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
			concurso = em.find(Concurso.class, idConcurso);
			findEntidades();
		}

		findEmpatados();

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

	/**
	 * para el caso que reciba un solo idPostulacion
	 **/
	@SuppressWarnings("unchecked")
	public String cancelacionPostulacion(Long idPostulacion) {

		postulacion = em.find(Postulacion.class, idPostulacion);
		concursoPuestoAgr =
			em.find(ConcursoPuestoAgr.class, postulacion.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		List<EvalReferencialPostulante> referencialPostulantes =
			em.createQuery("Select e from EvalReferencialPostulante e "
				+ " where e.postulacion.idPostulacion=" + idPostulacion).getResultList();
		if (!referencialPostulantes.isEmpty()) {// se cancela
			EvalReferencialPostulante evalPostulante = referencialPostulantes.get(0);
			//evalPostulante.setActivo(false);
			evalPostulante.setVar(false);
			evalPostulante.setSeleccionado(false);
			em.merge(evalPostulante);
			em.flush();
		}

		List<EvalReferencialPostulante> evalReferencialPost =
			em.createQuery(" Select e from  EvalReferencialPostulante e"
				+ " where e.activo=true and  e.listaCorta=true and e.seleccionado=false order by e.puntajeRealizado desc").getResultList();
		// Si no existen registros en evalReferencialPost, declara desierto el puesto
		if (evalReferencialPost.isEmpty()) {
			List<ConcursoPuestoDet> puestoDets =
				em.createQuery(" Select d from ConcursoPuestoDet d "
					+ " where d.concursoPuestoAgr.idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr() + "" + " and d.activo=true").getResultList();

			ConcursoPuestoDet concursoPuestoDet = puestoDets.get(0);// se toma cualquiera, en este caso el primero

			PlantaCargoDet cargoDet = concursoPuestoDet.getPlantaCargoDet();
			cargoDet.setEstadoCab(eVacante());
			cargoDet.setEstadoDet(null);
			em.merge(cargoDet);

			concursoPuestoDet.setEstadoDet(eDesierto());
			concursoPuestoDet.setActivo(false);
			concursoPuestoDet.setUsuMod("SYSTEM");
			concursoPuestoDet.setFechaMod(new Date());
			em.merge(concursoPuestoDet);

			HistoricosEstado historicosEstado = new HistoricosEstado();
			historicosEstado.setPlantaCargoDet(cargoDet);
			historicosEstado.setUsuMod("SYSTEM");
			historicosEstado.setFechaMod(new Date());
			historicosEstado.setEstadoCab(eVacante());
			em.persist(historicosEstado);

			em.flush();

			return null;

		} else {
			EvalReferencialPostulante evalReferencialPostulante =
				em.find(EvalReferencialPostulante.class, evalReferencialPost.get(0).getIdEvalReferencialPostulante());

			if (evalReferencialPost.size() >= 2) {
				// se verifica si no hay empate almenos con el siguiente
				if (evalReferencialPost.get(1).getPuntajeRealizado().intValue() != evalReferencialPostulante.getPuntajeRealizado().intValue()) {
					evalReferencialPostulante.setSeleccionado(true);
					evalReferencialPostulante.setVar(true);
					em.merge(evalReferencialPostulante);
					em.flush();
					setearVar();
					return null;
				} else {
					try {
						// se registra empate, se inicia la tarea
						Long idProcess = iniciarTareajBPM(concursoPuestoAgr);
						if(idProcess  != null){
							concursoPuestoAgr.setIdProcessInstance(idProcess);
							concursoPuestoAgr.setUsuMod(usuarioLogueado.getCodigoUsuario());
							concursoPuestoAgr.setFechaMod(new Date());
							em.merge(concursoPuestoAgr);
						}
						em.flush();

						return "ir";// se debe redireccionar a bandeja de entrada
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}

				}
			} else {// hay uno solo y es el de mayor puntaje
				try {
					evalReferencialPostulante.setSeleccionado(true);
					evalReferencialPostulante.setVar(true);
					em.merge(evalReferencialPostulante);
					em.flush();
					setearVar();
					return null;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

			}

		}

	}

	@SuppressWarnings("unchecked")
	public String cancelarPostulaciones(List<Long> ids) {
		try {
			int ultimo;
			boolean empate = false;;
			if (ids.size() == 1)
				ultimo = 0;
			else
				ultimo = ids.size() - 1;
			for (int i = 0; i < ids.size(); i++) {// por cada postulante.
				postulacion = em.find(Postulacion.class, ids.get(i));
							
				concursoPuestoAgr =
					em.find(ConcursoPuestoAgr.class, postulacion.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
				List<EvalReferencialPostulante> referencialPostulantes =
					em.createQuery("Select e from EvalReferencialPostulante e "
						+ " where e.postulacion.idPostulacion=" + ids.get(i)).getResultList();
				if (!referencialPostulantes.isEmpty()) {// se cancela
					EvalReferencialPostulante evalPostulante = referencialPostulantes.get(0);
					//evalPostulante.setActivo(false);
					evalPostulante.setVar(false);
					evalPostulante.setSeleccionado(false);
					em.merge(evalPostulante);
					em.flush();
				}

				List<EvalReferencialPostulante> evalReferencialPost =
					em.createQuery(" Select e from  EvalReferencialPostulante e"
						+ " where e.activo=true and e.listaCorta=true and e.seleccionado=false order by e.puntajeRealizado desc").getResultList();
				// Si no existen registros en evalReferencialPost, declara desierto el puesto
				if (evalReferencialPost.isEmpty()) {
					List<ConcursoPuestoDet> puestoDets =
						em.createQuery(" Select d from ConcursoPuestoDet d "
							+ " where d.concursoPuestoAgr.idConcursoPuestoAgr="
							+ concursoPuestoAgr.getIdConcursoPuestoAgr() + ""
							+ " and d.activo=true").getResultList();

					ConcursoPuestoDet concursoPuestoDet = puestoDets.get(0);// se toma cualquiera, en este caso el primero
					if (puestoDets.isEmpty()) {
						PlantaCargoDet cargoDet = concursoPuestoDet.getPlantaCargoDet();
						cargoDet.setEstadoCab(eVacante());
						cargoDet.setEstadoDet(null);
						em.merge(cargoDet);

						concursoPuestoDet.setEstadoDet(eDesierto());
						concursoPuestoDet.setActivo(false);
						concursoPuestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
						concursoPuestoDet.setFechaMod(new Date());
						em.merge(concursoPuestoDet);

						HistoricosEstado historicosEstado = new HistoricosEstado();
						historicosEstado.setPlantaCargoDet(cargoDet);
						historicosEstado.setUsuMod(usuarioLogueado.getCodigoUsuario());
						historicosEstado.setFechaMod(new Date());
						historicosEstado.setEstadoCab(eVacante());
						em.persist(historicosEstado);
						em.flush();
					}

				} else {
					EvalReferencialPostulante evalReferencialPostulante =
						em.find(EvalReferencialPostulante.class, evalReferencialPost.get(0).getIdEvalReferencialPostulante());

					if (i != ultimo) {// si es el ultimo se verifica si no hay empate almenos con el siguiente
						evalReferencialPostulante.setSeleccionado(true);
						evalReferencialPostulante.setVar(true);
						em.merge(evalReferencialPostulante);
						em.flush();
						setearVar();

					} else {
						try {
							if (evalReferencialPost.size() >= 2) {
								if (evalReferencialPostulante.getPuntajeRealizado().equals(evalReferencialPost.get(1).getPuntajeRealizado())) {
									// se registra empate, se inicia la tarea
									Long idProcess = iniciarTareajBPM(concursoPuestoAgr);
									if(idProcess != null){
										concursoPuestoAgr.setIdProcessInstance(idProcess);
										concursoPuestoAgr.setUsuMod(usuarioLogueado.getCodigoUsuario());
										concursoPuestoAgr.setFechaMod(new Date());
										em.merge(concursoPuestoAgr);
										em.flush();
										empate = true;
									}
									
								}
							} else {
								evalReferencialPostulante.setSeleccionado(true);
								evalReferencialPostulante.setVar(true);
								em.merge(evalReferencialPostulante);
								em.flush();
								setearVar();

							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				}

			}
			if (empate)
				return "ir";// debera ser redireccionado a bandeja de entrada

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Inicia la tarea del jBPM para el empate
	 */
	private Long iniciarTareajBPM(ConcursoPuestoAgr grupo) {
		try {
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.REVISION_EMPATES);
			jbpmUtilFormController.setTransition("next3");
			roles = jbpmUtilFormController.asignarRolesTarea();
			Long idProcess = jbpmUtilFormController.initProcess("seleccion");
			return idProcess;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * controles, luego de finalizar el proceso
	 */
	@SuppressWarnings("unchecked")
	private void controles() {
		ConcursoPuestoAgr agrCopia = new ConcursoPuestoAgr();
		if (concursoPuestoAgr.getEstado() == listaCorta()) {
			endTask(concursoPuestoAgr.getIdConcursoPuestoAgr());
		} else {

			agrCopia.setCodGrupo(concursoPuestoAgr.getCodGrupo());
			agrCopia.setDescripcionGrupo(concursoPuestoAgr.getDescripcionGrupo() + " COPIA");
			agrCopia.setNroOrden(concursoPuestoAgr.getNroOrden());
			agrCopia.setObservacion(concursoPuestoAgr.getObservacion());
			agrCopia.setConcurso(concursoPuestoAgr.getConcurso());
			agrCopia.setActivo(concursoPuestoAgr.isActivo());
			agrCopia.setUsuAlta("SYSTEM");
			agrCopia.setFechaAlta(new Date());
			agrCopia.setEstado(listaCorta());
			agrCopia.setMision(concursoPuestoAgr.getMision());
			agrCopia.setObjetivo(concursoPuestoAgr.getObjetivo());
			agrCopia.setModalidad(concursoPuestoAgr.getModalidad());
			agrCopia.setFuenteFinanciacion(concursoPuestoAgr.getFuenteFinanciacion());
			agrCopia.setRemuneracion(concursoPuestoAgr.getRemuneracion());
			agrCopia.setCondLaborales(concursoPuestoAgr.getCondLaborales());
			agrCopia.setOtrasInf(concursoPuestoAgr.getOtrasInf());
			agrCopia.setPostHabilitados(concursoPuestoAgr.getPostHabilitados());
			agrCopia.setDocumentos(concursoPuestoAgr.getDocumentos());
			agrCopia.setFechaLimite(concursoPuestoAgr.getFechaLimite());
			agrCopia.setLugar(concursoPuestoAgr.getLugar());
			agrCopia.setMisionEspecifica(concursoPuestoAgr.getMisionEspecifica());
			agrCopia.setContacto(concursoPuestoAgr.getContacto());
			agrCopia.setOtrosBeneficios(concursoPuestoAgr.getOtrosBeneficios());
			agrCopia.setHorario(concursoPuestoAgr.getHorario());
			agrCopia.setConcursoPuestoAgrOrigen(concursoPuestoAgr);
			em.persist(agrCopia);
			em.flush();

			// se crea detalle
			List<ConcursoPuestoDet> puestoDetsLis = new ArrayList<ConcursoPuestoDet>();
			puestoDetsLis =
				em.createQuery("Select d from  ConcursoPuestoDet d "
					+ " where d.concursoPuestoAgr.idConcursoPuestoAgr="
					+ agrCopia.getConcursoPuestoAgrOrigen().getIdConcursoPuestoAgr()).getResultList();

			List<EvalReferencialPostulante> cancelados =
				em.createQuery("Select c from EvalReferencialPostulante c "
					+ " where c.concursoPuestoAgr.idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr() + ""
					+ " and c.activo=false and   c.seleccionado=false and c.var=false").getResultList();

			// se toma la cantidad de registros según la cantidad de postulantes cancelados
			int tam = 0;
			if (puestoDetsLis.size() < cancelados.size())
				tam = puestoDetsLis.size();
			else
				tam = cancelados.size();
			for (int i = 0; i < tam; i++) {
				ConcursoPuestoDet det =
					em.find(ConcursoPuestoDet.class, puestoDetsLis.get(i).getIdConcursoPuestoDet());
				det.setActivo(false);
				det.setFechaMod(new Date());
				det.setUsuMod("SYSTEM");
				em.merge(det);

				// se crea una copia
				ConcursoPuestoDet detcopia = new ConcursoPuestoDet();
				detcopia.setNroOrden(det.getNroOrden());
				detcopia.setPlantaCargoDet(det.getPlantaCargoDet());
				detcopia.setConcursoPuestoAgr(agrCopia);
				detcopia.setActivo(true);
				detcopia.setEstadoDet(det.getEstadoDet());
				detcopia.setUsuAlta("SYSTEM");
				detcopia.setFechaAlta(new Date());
				detcopia.setConcurso(det.getConcurso());
				em.persist(detcopia);
				em.flush();
			}

			List<EvalReferencialPostulante> postulantesNuevos =
				new ArrayList<EvalReferencialPostulante>();
			postulantesNuevos =
				em.createQuery("Select c from EvalReferencialPostulante c "
					+ " where c.concursoPuestoAgr.idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr() + ""
					+ " and c.seleccionado=true and c.var=true").getResultList();
			// modificar el ID_CONCURSO_PUESTO_AGR con el nuevo generado(copia)
			for (int i = 0; i < postulantesNuevos.size(); i++) {
				EvalReferencialPostulante post =
					em.find(EvalReferencialPostulante.class, postulantesNuevos.get(i).getIdEvalReferencialPostulante());
				post.setConcursoPuestoAgr(agrCopia);
				post.setUsuAlta("SYSTEM");
				post.setFechaAlta(new Date());
				em.merge(post);

				Postulacion postulacion = new Postulacion();
				if (post.getPostulacion() != null) {
					postulacion =
						em.find(Postulacion.class, post.getPostulacion().getIdPostulacion());
					postulacion.setConcursoPuestoAgr(agrCopia);
					em.merge(postulacion);
				}

				em.flush();
			}
			List<MatrizDocConfigEnc> docConfigEncs =
				em.createQuery("select c from MatrizDocConfigEnc c "
					+ " where c.concursoPuestoAgr=" + concursoPuestoAgr.getIdConcursoPuestoAgr()).getResultList();
			for (int i = 0; i < docConfigEncs.size(); i++) {
				MatrizDocConfigEnc docConfigEncCopia = new MatrizDocConfigEnc();
				docConfigEncCopia.setDatosEspecificos(docConfigEncs.get(i).getDatosEspecificos());
				docConfigEncCopia.setConcursoPuestoAgr(agrCopia);
				docConfigEncCopia.setActivo(true);
				docConfigEncCopia.setUsuAlta("SYSTEM");
				docConfigEncCopia.setFechaAlta(new Date());
				em.persist(docConfigEncCopia);
				em.flush();
				// listar sus detalles para la copia
				List<MatrizDocConfigDet> matrizdets =
					em.createQuery("Select c from MatrizDocConfigDet c "
						+ " where c.matrizDocConfigEnc.idMatrizDocConfigEnc= "
						+ docConfigEncs.get(i).getIdMatrizDocConfigEnc() + "" + "").getResultList();

				for (int j = 0; j < matrizdets.size(); j++) {
					MatrizDocConfigDet matrizdetCopia = new MatrizDocConfigDet();
					matrizdetCopia.setNroOrden(matrizdets.get(j).getNroOrden());
					matrizdetCopia.setMatrizDocConfigEnc(docConfigEncCopia);
					matrizdetCopia.setDatosEspecificos(matrizdets.get(j).getDatosEspecificos());
					matrizdetCopia.setActivo(true);
					matrizdetCopia.setUsuAlta("SYSTEM");
					matrizdetCopia.setFechaAlta(new Date());
					matrizdetCopia.setEvaluacionDoc(matrizdets.get(j).getEvaluacionDoc());
					matrizdetCopia.setAdjudicacion(matrizdets.get(j).getAdjudicacion());
					matrizdetCopia.setObligatorio(matrizdets.get(j).getObligatorio());

					em.persist(matrizdetCopia);
					em.flush();
				}

			}

			// COMISION_GRUPO
			List<ComisionGrupo> comisionGrupos =
				em.createQuery("Select cg from ComisionGrupo cg "
					+ " where cg.activo= true and cg.concursoPuestoAgr.idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()).getResultList();

			for (int i = 0; i < comisionGrupos.size(); i++) {
				ComisionGrupo comisionGrupoCopia = new ComisionGrupo();
				comisionGrupoCopia.setActivo(true);
				comisionGrupoCopia.setComisionSeleccionCab(comisionGrupos.get(i).getComisionSeleccionCab());
				comisionGrupoCopia.setUsuAlta("SYSTEM");
				comisionGrupoCopia.setConcursoPuestoAgr(agrCopia);
				comisionGrupoCopia.setFechaAlta(new Date());
				em.persist(comisionGrupoCopia);
				em.flush();

			}

			sentMail();
			iniciarTareajBPMValidar(agrCopia);
			setearVar();// se seteas las variables para los prox controles
		}

	}

	/**
	 * Inicia la tarea del jBPM VALIDAR MATRIZ DOCUMENT
	 */
	private Long iniciarTareajBPMValidar(ConcursoPuestoAgr grupo) {
		jbpmUtilFormController.setActividadSiguiente(ActividadEnum.VALIDAR_MATRIZ_DOCUMENTAL_ADJ);
		jbpmUtilFormController.setTransition("next3");
		roles = jbpmUtilFormController.asignarRolesTarea();
		Long idProcess = jbpmUtilFormController.initProcess("seleccion");
		return idProcess;
	}

	@SuppressWarnings("unchecked")
	private void sentMail() {
		List<EvalReferencialPostulante> postulantes =
			em.createQuery("select eval from EvalReferencialPostulante eval "
				+ " where eval.seleccionado=true and eval.var=true ").getResultList();
		for (int i = 0; i < postulantes.size(); i++) {
			String error = "";
			PersonaPostulante personaPostulante =
				postulantes.get(i).getPostulacion().getPersonaPostulante();
			ConcursoPuestoAgr grupo = new ConcursoPuestoAgr();
			if (postulantes.get(i).getConcursoPuestoAgr() != null)
				grupo =
					em.find(ConcursoPuestoAgr.class, postulantes.get(i).getConcursoPuestoAgr().getIdConcursoPuestoAgr());

			// para sacar la lista de los documentos
			List<MatrizDocConfigDet> configDets =
				em.createQuery("Select m  from MatrizDocConfigDet  m "
					+ " where m.matrizDocConfigEnc.concursoPuestoAgr.idConcursoPuestoAgr="
					+ grupo.getIdConcursoPuestoAgr() + ""
					+ " and m.adjudicacion=true and m.activo=true").getResultList();
			String documentos = "";

			for (int j = 0; j < configDets.size(); j++) {
				documentos +=
					"<p>	 -" + configDets.get(i).getDatosEspecificos().getDescripcion() + "</p>";
			}
			// para la lista de mails
			String mails = "<p>";
			List<PresentAclaracDoc> aclaracDocs = new ArrayList<PresentAclaracDoc>();
			aclaracDocs =
				em.createQuery("Select pa from PresentAclaracDoc pa "
					+ " where pa.concursoPuestoAgr.idConcursoPuestoAgr="
					+ grupo.getConcursoPuestoAgrOrigen().getIdConcursoPuestoAgr()).getResultList();
			if (aclaracDocs.isEmpty()) {
				aclaracDocs =
					em.createQuery("Select pa from PresentAclaracDoc pa "
						+ " where pa.concursoPuestoAgr.concurso.idConcurso="
						+ grupo.getConcurso().getIdConcurso()).getResultList();
			}
			for (int j = 0; j < aclaracDocs.size(); j++) {
				mails += " " + aclaracDocs.get(j).getEmail() + ",";
			}
			mails += "</p>";

			String email = personaPostulante.getEMail();
			String asunto = null;
			asunto =
				grupo.getConcurso().getConfiguracionUoCab().getDescripcionCorta()
					+ "– LISTA CORTA ";
			String mensaje =
				"<p> <b> Estimado/a    "
					+ personaPostulante.getNombreCompleto()
					+ "</b> </p> "
					+ " <p> </p> "
					+ "<p align=\"justify\">&nbsp; </p>"
					+ " <p>Le comunicamos que ha sido seleccionado/a en la etapa de la <b>Lista Corta</b> para el Concurso:  "
					+ " </p> "
					+ " <p  align=\"center\">&nbsp;<b>  "
					+ grupo.getConcurso().getNumero()
					+ " /"
					+ grupo.getConcurso().getAnhio()
					+ " /"
					+ grupo.getConcurso().getConfiguracionUoCab().getDescripcionCorta()
					+ " "
					+ grupo.getDescripcionGrupo()
					+ "</p> "
					// + "<p align=\"center\">&nbsp; </p>"
					// + " <p> "++ " </p>"
					+ "<p align=\"center\">&nbsp; <b> " + grupo.getDescripcionGrupo()
					+ " </b> </p>"
					+ "<p> <b>Recordamos además que deberá acercar los siguientes documentos:</b> "
					+ documentos + "<p>Atentamente, </p> " + "<p> <strong>"
					+ grupo.getConcurso().getConfiguracionUoCab().getDenominacionUnidad()
					+ " </strong> <br />  </p>"
					+ "<p>Para mayor información comunicarse con: </p> " + mails + " ";

			try {

				enviarMails(email, mensaje, asunto);

			} catch (Exception e) {
				System.out.println(e.getMessage());
				error = e.getMessage();
				e.printStackTrace();
			}

		}

	}

	@SuppressWarnings("unchecked")
	public Boolean enviarMails(String destinatario, String contenido, String asunto) {

		Properties props = new Properties();

		List<Referencias> ref =
			em.createQuery(" select r from Referencias r"
				+ " where r.dominio like 'CONFIG_EMAIL' and r.activo= true").getResultList();
		if (ref.isEmpty()) {
			return false;
		}

		String protocol = "", host = "", user = "", pass = "", port = "", usuarioSend = "";
		int i = 0;
		for (Referencias r : ref) {
			if (r.getDescAbrev().equals("HOST"))
				host = r.getDescLarga();

			if (r.getDescAbrev().equals("USER"))
				user = r.getDescLarga();

			if (r.getDescAbrev().equals("PORT"))
				port = r.getDescLarga();

			if (r.getDescAbrev().equals("PASS"))
				pass = r.getDescLarga();

			if (r.getDescAbrev().equals("PROTOCOLO"))
				protocol = r.getDescLarga();
			if (r.getDescAbrev().equals("USUARIO"))
				usuarioSend = r.getDescLarga();

		}

		/*
		 * Ejemplo props.setProperty("mail.transport.protocol", "smtp"); props.setProperty("mail.host", "mail.copaco.com.py"); props.setProperty("mail.user", "menfran@click.com.py");
		 * props.setProperty("mail.password", "12345678");
		 */

		props.setProperty("mail.transport.protocol", protocol);
		props.setProperty("mail.host", host);
		props.setProperty("mail.user", user);
		props.setProperty("mail.password", pass);
		 props.setProperty("mail.smtp.auth", "true");
			//javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(props, null);
			javax.mail.Transport transport;
			
			//Autenticador
			Authenticator aut = new Authenticator() {
				
	            
	            protected PasswordAuthentication getPasswordAuthentication(){
	            	List<Referencias> ref = em.createQuery(" select r from Referencias r"
							+ " where r.dominio like 'CONFIG_EMAIL' and r.activo= true").getResultList();

					if (ref.isEmpty()) {
						return null;
					}
					
					String protocol = "", host = "", user = "", pass = "", port = "", usuarioSend = "";
					int i = 0;
					for (Referencias r : ref) {
						if (r.getDescAbrev().equals("HOST"))
							host = r.getDescLarga();

						if (r.getDescAbrev().equals("USER"))
							user = r.getDescLarga();

						if (r.getDescAbrev().equals("PORT"))
							port = r.getDescLarga();

						if (r.getDescAbrev().equals("PASS"))
							pass = r.getDescLarga();

						if (r.getDescAbrev().equals("PROTOCOLO"))
							protocol = r.getDescLarga();

						if (r.getDescAbrev().equals("USUARIO"))
							usuarioSend = r.getDescLarga();

					}
					         	
	                PasswordAuthentication passwordAuthentication=new PasswordAuthentication(user,pass);
	                return passwordAuthentication;
	              }};
			
			javax.mail.Session mailSession = javax.mail.Session.getInstance(props, aut);
			
			
		try {
			transport = mailSession.getTransport();
			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(asunto);
			message.setContent(contenido, "text/html");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
			message.setFrom(new InternetAddress(user, usuarioSend));
			message.setSentDate(new Date());
			message.setHeader("-", "-");
			transport.connect();
			transport.sendMessage(message, message.getAllRecipients());

			transport.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public void evalSelecc(Long idEval) {
		evalReferencialPostulanteObs = em.find(EvalReferencialPostulante.class, idEval);
	}

	public void saveObs() {
		evalReferencialPostulanteObs.setFechaMod(new Date());
		evalReferencialPostulanteObs.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(evalReferencialPostulanteObs);
		em.flush();
	}

	public String saveEmpates() {
		try {
			if (!chkTieneObs()) {
				statusMessages.add(Severity.ERROR, "Todos los registros deben tener una observación registrada, verifique");
				return null;
			}
			if (!chkSelecciono())
				return null;
			EvalReferencialPostulante postulante = new EvalReferencialPostulante();
			for (int i = 0; i < referencialPostulantes.size(); i++) {
				if (referencialPostulantes.get(i).getSeleccionado()) {
					postulante =
						em.find(EvalReferencialPostulante.class, referencialPostulantes.get(i).getIdEvalReferencialPostulante());
				}
			}
			postulante.setSeleccionado(true);
			postulante.setVar(true);
			postulante.setFechaMod(new Date());
			postulante.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.merge(postulante);
			em.flush();
			// finaliza la tarea del JBPM
			endTask(postulante.getConcursoPuestoAgr().getIdConcursoPuestoAgr());

			// se realiza los controles
			controles();

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String endTask(Long id) {
		try {

			// Se finaliza la tarea
			jbpmUtilFormController.setConcursoPuestoAgr(em.find(ConcursoPuestoAgr.class, id));
			jbpmUtilFormController.setActividadActual(ActividadEnum.REVISION_EMPATES);
			jbpmUtilFormController.setTransition("next");
			jbpmUtilFormController.nextTask();

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "end";

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean chkTieneObs() {
		for (int i = 0; i < referencialPostulantes.size(); i++) {
			if (referencialPostulantes.get(i).getObsEmpate() == null
				|| referencialPostulantes.get(i).getObsEmpate().trim().equals("")) {
				return false;
			}
		}
		return true;
	}

	private boolean chkSelecciono() {
		int selec = 0;
		for (int i = 0; i < referencialPostulantes.size(); i++) {
			if (referencialPostulantes.get(i).getSeleccionado()) {
				selec++;
			}
		}
		if (selec != 1) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un registro para desempate");
			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private Integer listaCorta() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'HOMOLOGADO'").getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private EstadoCab eVacante() {
		try {
			List<EstadoCab> dets =
				em.createQuery("Select d from EstadoCab d "
					+ " where d.descripcion like 'VACANTE' ").getResultList();
			if (dets.isEmpty())
				return null;
			else
				return dets.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	private EstadoDet eDesierto() {
		List<EstadoDet> dets =
			em.createQuery("Select d from EstadoDet d "
				+ " where d.descripcion.descripcion like 'DESIERTO' ").getResultList();
		if (dets.isEmpty())
			return null;
		else
			return dets.get(0);
	}

	@SuppressWarnings("unchecked")
	private void findEmpatados() {
		List<EvalReferencialPostulante> evRef =
			em.createQuery(" Select c from EvalReferencialPostulante c "
				+ " where  c.activo=true and c.listaCorta=true and c.seleccionado=false order by  c.puntajeRealizado desc").getResultList();
		int puntaje = evRef.get(0).getPuntajeRealizado().intValue();
		referencialPostulantes = new ArrayList<EvalReferencialPostulante>();
		for (int i = 0; i < evRef.size(); i++) {
			if (puntaje == evRef.get(i).getPuntajeRealizado().intValue()) {// se agregan los puntajes iguales
				referencialPostulantes.add(evRef.get(i));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void setearVar() {
		List<EvalReferencialPostulante> referencialPostulantes =
			em.createQuery("Select e from EvalReferencialPostulante e "
				+ " where e.var=true or e.var=false").getResultList();
		for (int i = 0; i < referencialPostulantes.size(); i++) {
			EvalReferencialPostulante evalReferencialPostulante = referencialPostulantes.get(i);
			evalReferencialPostulante.setVar(null);
			em.merge(evalReferencialPostulante);
			em.flush();
		}
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

	public Long getIdPostulacion() {
		return idPostulacion;
	}

	public void setIdPostulacion(Long idPostulacion) {
		this.idPostulacion = idPostulacion;
	}

	public Postulacion getPostulacion() {
		return postulacion;
	}

	public void setPostulacion(Postulacion postulacion) {
		this.postulacion = postulacion;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public List<EvalReferencialPostulante> getReferencialPostulantes() {
		return referencialPostulantes;
	}

	public void setReferencialPostulantes(List<EvalReferencialPostulante> referencialPostulantes) {
		this.referencialPostulantes = referencialPostulantes;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public EvalReferencialPostulante getEvalReferencialPostulanteObs() {
		return evalReferencialPostulanteObs;
	}

	public void setEvalReferencialPostulanteObs(EvalReferencialPostulante evalReferencialPostulanteObs) {
		this.evalReferencialPostulanteObs = evalReferencialPostulanteObs;
	}

}
